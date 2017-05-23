package it.polito.tdp.emergency.model;

import java.util.PriorityQueue;

import it.polito.tdp.emergency.model.Event.EventType;
import it.polito.tdp.emergency.model.Patient.PatientStatus;

public class Simulator {

	// Simulation parameters

	private int NS; // number of studios

	private int DURATION_TRIAGE = 5 * 60;
	private int DURATION_WHITE = 10 * 60;
	private int DURATION_YELLOW = 15 * 60;
	private int DURATION_RED = 30 * 60;

	private int WHITE_TIMEOUT = 30 * 60;
	private int YELLOW_TIMEOUT = 30 * 60;
	private int RED_TIMEOUT = 60 * 60;

	// World model
	private PriorityQueue<Patient> waitingRoom;
	private int occupiedStudios = 0;

	// Measures of Interest
	private int patientsTreated = 0;
	private int patientsDead = 0;
	private int patientsAbandoned = 0;

	// Event queue
	private PriorityQueue<Event> queue;

	public Simulator(int NS) {
		this.NS = NS;

		this.queue = new PriorityQueue<>();
		this.waitingRoom = new PriorityQueue<>(new PatientComparator());
	}

	public void addPatient(Patient patient, int time) {


	
	}

	public void run() {
		while (!queue.isEmpty()) {
			Event e = queue.poll();
			System.out.println(e);

			switch (e.getType()) {
			case TRIAGE:
				processTriageEvent(e);
				break;
			case TIMEOUT:
				processTimeoutEvent(e);
				break;
			case FREE_STUDIO:
				processFreeStudioEvent(e);
				break;
			}
		}
	}

	/**
	 * A patient finished treatment. The studio is freed, and a new patient is
	 * called in.
	 * 
	 * @param e
	 */
	private void processFreeStudioEvent(Event e) {

 
		
		
		
	}

	private void processTimeoutEvent(Event e) {


		
		
	}

	/**
	 * Patient goes out of triage. A severity code is assigned. If a studio is
	 * free, then it is immediately assigned. Otherwise, he is put in the waiting
	 * list.
	 * 
	 * @param e
	 */
	private void processTriageEvent(Event e) {


	
	}

	public int getPatientsTreated() {
		return patientsTreated;
	}

	public int getPatientsDead() {
		return patientsDead;
	}

	public int getPatientsAbandoned() {
		return patientsAbandoned;
	}
}
