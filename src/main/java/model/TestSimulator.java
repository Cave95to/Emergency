package model;

public class TestSimulator {

	public static void main(String[] args) {
		Simulator sim = new Simulator();
		
		// impostare parametri
		
		// eseguire simulazione
		sim.init();
		sim.run();
		
		// leggere output
		System.out.println("abbandonati: "+sim.getPatientsAbandoned()+ ", morti: "+ sim.getPatientsDead() 
				+ ", curati: "+sim.getPatientsTreated());

	}

}