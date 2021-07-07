package model;

import java.time.LocalTime;

public class Patient implements Comparable<Patient> {
	
	public enum ColorCode {
		
		NEW,                          // triage
		WHITE, YELLOW, RED, BLACK,    // in sala d'attesa
		TREATING,                     // dentro studio medico
		OUT                           // a casa (abbandonato o guarito)
	};
	
	private int num;                  // numero di ordine di arrivo
	private LocalTime arrivalTime;
	private ColorCode color;
	
	public Patient(int num, LocalTime arrivalTime, ColorCode color) {
		super();
		this.num = num;
		this.arrivalTime = arrivalTime;
		this.color = color;
	}

	public LocalTime getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(LocalTime arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public ColorCode getColor() {
		return color;
	}

	public void setColor(ColorCode color) {
		this.color = color;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	@Override
	public String toString() {
		return "Patient [num= " + num + ", arrivalTime=" + arrivalTime + ", color=" + color + "]";
	}

	// SERVONO PER IMPLEMENTARE CORRETTAMENTE IL REMOVE!!!!!
	@Override 
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + num;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Patient other = (Patient) obj;
		if (num != other.num)
			return false;
		return true;
	}
	
	// QUA DENTRO IMPLEMENTO TUTTA LA LOGICA CON CUI VENGONO VISITATI I PAZIENTIIIII !!!!
	@Override // negativo = this       positivo = other
	public int compareTo(Patient o) {
		
		// se hanno lo stesso colore, guardo ordine arrivo
		if(this.color.equals(o.color)) 
			return this.arrivalTime.compareTo(o.arrivalTime);
		
		//  DA QUI I COLORI SONO DIVERSI
		
		else if(this.color.equals(Patient.ColorCode.RED))
			return -1; // se io sono rosso, con NUMERO NEGATIVO passo io
		else if(o.color.equals(Patient.ColorCode.RED))
			return +1; // se altro è rosso, con NUMERO POSTIVO passa altro
		
		// arrivati qui, uno è giallo, l'altro bianco per forza
		else if(this.color.equals(Patient.ColorCode.YELLOW))
			return -1; // se sono io giallo
		else return +1;
			
	}
	
	

}
