package model;

import java.time.LocalTime;

public class Event implements Comparable<Event>{
	
	public enum EventType {
		ARRIVAL,            // arriva nuovo paziente, entra SUBITO in triage
		TRIAGE,             // finito triage, entra in sala attesa
		TIMEOUT,            // passa un certo tempo di attesa
		FREE_STUDIO,        // si è liberato uno studio, chiamo qualcuno
		TREATED,            // paziente CURATO
		TICK                // timer per controllare se ci sono studi liberi ogni tot
	};
	
	private LocalTime time;
	private EventType type;
	private Patient patient; // sta volta serve aggiungere anche il paziente perche ha info sul COLORE
	public LocalTime getTime() {
		return time;
	}
	public void setTime(LocalTime time) {
		this.time = time;
	}
	public EventType getType() {
		return type;
	}
	public void setType(EventType type) {
		this.type = type;
	}
	public Patient getPatient() {
		return patient;
	}
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	public Event(LocalTime time, EventType type, Patient patient) {
		super();
		this.time = time;
		this.type = type;
		this.patient = patient;
	}
	@Override
	public String toString() {
		return "Event [time=" + time + ", type=" + type + ", patient=" + patient + "]";
	}
	@Override
	public int compareTo(Event o) {
		return this.time.compareTo(o.getTime());
	}
	
	
}
