package model;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import model.Event.EventType;
import model.Patient.ColorCode;

public class Simulator {
	
	// coda eventi
	private PriorityQueue<Event> queue;
	
	// modello del mondo
	private List<Patient> patients;
	private PriorityQueue<Patient> waitingRoom; // CONTIENE SOLO PAZIENTI GIA' BIANCHI GIALLI ROSSI
	private int freeStudios; // numero studio liberi
	
	private Patient.ColorCode ultimoColore;
	
	// parametri input COSTANTI -> servono SETTER
	private int totStudios = 3;
	private int numPatients = 120;
	
	private Duration T_ARRIVAL = Duration.ofMinutes(5);
	
	private Duration DURATION_TRIAGE = Duration.ofMinutes(5);
	private Duration DURATION_WHITE = Duration.ofMinutes(10);
	private Duration DURATION_YELLOW = Duration.ofMinutes(15);
	private Duration DURATION_RED = Duration.ofMinutes(30);
	
	private Duration TIMEOUT_WHITE = Duration.ofMinutes(60);
	private Duration TIMEOUT_YELLOW = Duration.ofMinutes(30);
	private Duration TIMEOUT_RED = Duration.ofMinutes(30);
	
	private LocalTime startTime = LocalTime.of(8, 00);
	private LocalTime endTime = LocalTime.of(20, 00);
	
	// parametri output -> servono GETTER
	private int patientsTreated;
	private int patientsAbandoned;
	private int patientsDead;
	
	// INIZIALIZZA SIMULATORE e crea aventi iniziali
	public void init() {
		
		// inizializza coda eventi
		this.queue = new PriorityQueue<>();
		
		// inizializza modello del mondo
		this.patients = new ArrayList<>();
		this.freeStudios = this.totStudios;
		this.waitingRoom = new PriorityQueue<>();
		
		this.ultimoColore = ColorCode.RED;
		
		// inizializza parametri output
		this.patientsTreated =0;
		this.patientsAbandoned=0;
		this.patientsDead=0;
		
		// inserisce eventi input (ARRIVAL)
		LocalTime ora = this.startTime;
		int inseriti = 0;
		
		this.queue.add(new Event(ora, EventType.TICK, null));
		
		while(ora.isBefore(this.endTime) && inseriti<this.numPatients) {
			Patient p = new Patient(inseriti, ora, ColorCode.NEW); // perche i colori arrivano dopo il triage
			Event e = new Event(ora, EventType.ARRIVAL, p);
			this.queue.add(e);
			this.patients.add(p);
			
			inseriti++;
			ora = ora.plus(T_ARRIVAL);
		
		}
	}
	
	
	// metodo per assegnare a ciclo i colori, inizializzato a rosso
	private Patient.ColorCode prossimoColore() {
		if(ultimoColore.equals(ColorCode.WHITE))
			ultimoColore = ColorCode.YELLOW;
		else if(ultimoColore.equals(ColorCode.YELLOW))
			ultimoColore = ColorCode.RED;
		else 
			ultimoColore = ColorCode.WHITE;
		return this.ultimoColore;
		
	}
	
	// ESEGUE SIMULAZIONE
	public void run() {
		while(!this.queue.isEmpty()) {
			Event e = this.queue.poll();
			System.out.println(e);
			this.processEvent(e);
		}
	}
	
	private void processEvent(Event e) {
		
		Patient p = e.getPatient();
		LocalTime ora = e.getTime();
		
		 switch(e.getType()) {
		 
		 case ARRIVAL:
			 this.queue.add(new Event(ora.plus(DURATION_TRIAGE), EventType.TRIAGE, p));
			 break;
			 
		 case TRIAGE:
			 p.setColor(this.prossimoColore());
			 
			 if(p.getColor().equals(Patient.ColorCode.WHITE)) {
				 this.queue.add(new Event(ora.plus(TIMEOUT_WHITE), EventType.TIMEOUT, p));
				 this.waitingRoom.add(p);
			 }else if(p.getColor().equals(Patient.ColorCode.YELLOW)) {
				 this.queue.add(new Event(ora.plus(TIMEOUT_YELLOW), EventType.TIMEOUT, p));
			 	this.waitingRoom.add(p);
			 }else if(p.getColor().equals(Patient.ColorCode.RED)) {
				this.queue.add(new Event(ora.plus(TIMEOUT_RED), EventType.TIMEOUT, p));
				this.waitingRoom.add(p);
			 }
			 break;
			 
		 case FREE_STUDIO:
			 if(this.freeStudios==0)
				 return;
			 
			 // quale paziente ha diritto di entrare???
			 Patient primo = this.waitingRoom.poll();
			
			 if(primo!= null) {
				 
				 // ammetti paziente nello studio
				 if(primo.getColor().equals(ColorCode.WHITE))
					 this.queue.add(new Event(ora.plus(DURATION_WHITE), EventType.TREATED, primo));
				 if(primo.getColor().equals(ColorCode.YELLOW))
					 this.queue.add(new Event(ora.plus(DURATION_YELLOW), EventType.TREATED, primo));
				 if(primo.getColor().equals(ColorCode.RED))
					 this.queue.add(new Event(ora.plus(DURATION_RED), EventType.TREATED, primo));
				 
				 primo.setColor(ColorCode.TREATING);
				 this.freeStudios--;
			 }
			 break;
			 
		 case TIMEOUT:
			 Patient.ColorCode colore = p.getColor();
				
			 switch(colore) { // annido un nuovo switch dentro un case dello switch di partenza
			 
			 // IMPORTANTE: devo cambiare il colore ad un elemento dentro una coda !!!!!
			 // ma il colore mi determina la priorità della coda!!!
			 // devo PRIMA TOGLIERE il paziente dalla coda
			 // POI AGGIUNGO di nuovo il paziente con nuovo colore
			 
			 		case WHITE:
			 			this.waitingRoom.remove(p);
			 			p.setColor(ColorCode.OUT);
					 	this.patientsAbandoned++;
					 	break;
						 
					case YELLOW:
						 this.waitingRoom.remove(p); // tolgo
						 p.setColor(ColorCode.RED);  // cambio colore
						 this.queue.add(new Event(ora.plus(TIMEOUT_RED), EventType.TIMEOUT,p));
						 this.waitingRoom.add(p);    // aggiungo
						 break;
						
					case RED:
						 this.waitingRoom.remove(p);
						 p.setColor(ColorCode.BLACK);
						 this.patientsDead++;
						 break;
						 
						 
					default:
						 //System.out.println("ERRORE: TIMEOUT CON COLORE "+ colore);
						// scatta il timeout ma in realta il paziente è gia stato trattato quindi lo ignoriamo
						// perche dentro a 'colore' c'è anche l'opzione OUT o TREATING
					 
					 }
			 break;
					 
		 case TREATED:
			 this.patientsTreated++;
			 p.setColor(ColorCode.OUT);
			 this.freeStudios ++;
			 this.queue.add(new Event(ora, EventType.FREE_STUDIO, null));
			 break;
			 
		 case TICK:
			 if(this.freeStudios>0 && !this.waitingRoom.isEmpty())
				this.queue.add(new Event(ora, EventType.FREE_STUDIO, null));
			 if(ora.isBefore(this.endTime)) // OGNI 5 MIN scatta nuovo tick e controllo if di prima
				 this.queue.add(new Event(ora.plus(Duration.ofMinutes(5)), EventType.TICK, null));
			 break;
		 }
		
	}

	public void setTotStudios(int totStudios) {
		this.totStudios = totStudios;
	}
	public void setNumPatients(int numPatients) {
		this.numPatients = numPatients;
	}
	public void setT_ARRIVAL(Duration t_ARRIVAL) {
		T_ARRIVAL = t_ARRIVAL;
	}
	public void setDURATION_WHITE(Duration dURATION_WHITE) {
		DURATION_WHITE = dURATION_WHITE;
	}
	public void setDURATION_YELLOW(Duration dURATION_YELLOW) {
		DURATION_YELLOW = dURATION_YELLOW;
	}
	public void setDURATION_RED(Duration dURATION_RED) {
		DURATION_RED = dURATION_RED;
	}
	public void setTIMEOUT_WHITE(Duration tIMEOUT_WHITE) {
		TIMEOUT_WHITE = tIMEOUT_WHITE;
	}
	public void setTIMEOUT_YELLOW(Duration tIMEOUT_YELLOW) {
		TIMEOUT_YELLOW = tIMEOUT_YELLOW;
	}
	public void setTIMEOUT_RED(Duration tIMEOUT_RED) {
		TIMEOUT_RED = tIMEOUT_RED;
	}
	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}
	public void setEndTime(LocalTime endTime) {
		this.endTime = endTime;
	}
	public int getPatientsTreated() {
		return patientsTreated;
	}
	public int getPatientsAbandoned() {
		return patientsAbandoned;
	}
	public int getPatientsDead() {
		return patientsDead;
	}
	
	
	

}