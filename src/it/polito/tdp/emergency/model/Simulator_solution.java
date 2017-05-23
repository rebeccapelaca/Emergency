package it.polito.tdp.emergency.model;

import java.util.PriorityQueue;

import it.polito.tdp.emergency.model.Event.EventType;
import it.polito.tdp.emergency.model.Patient.PatientStatus;

public class Simulator_solution {

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

	public Simulator_solution(int NS) {
		this.NS = NS;

		this.queue = new PriorityQueue<>();
		this.waitingRoom = new PriorityQueue<>(new PatientComparator());
	}

	public void addPatient(Patient patient, int time) {
		Event e = new Event(patient, time + DURATION_TRIAGE, Event.EventType.TRIAGE);
		patient.setStatus(Patient.PatientStatus.NEW);
		queue.add(e) ;
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

		// dismiss current patient
		Patient cured = e.getPatient();
		cured.setStatus(PatientStatus.OUT);
		this.patientsTreated++;
		this.occupiedStudios--;
		System.out.println("\tCured "+cured);


		// now call next patient
		Patient next = waitingRoom.poll();

		if (next != null) {
			this.occupiedStudios++;

			int duration = 0; // should not happen
			if (next.getStatus() == PatientStatus.WHITE)
				duration = DURATION_WHITE;
			else if (next.getStatus() == PatientStatus.YELLOW)
				duration = DURATION_YELLOW;
			else if (next.getStatus() == PatientStatus.RED)
				duration = DURATION_RED;

			next.setStatus(PatientStatus.TREATING);

			// TODO: [1] should remove any additional "TIMEOUT" events for this patient!!
			
			Event exitEvent = new Event(next, e.getTime() + duration, EventType.FREE_STUDIO);
			queue.add(exitEvent);
			System.out.println("\tNext in: "+next);


		} else {
			System.out.println("\tNobody there");

		}
		

	}

	private void processTimeoutEvent(Event e) {
		Patient p = e.getPatient();

		switch (p.getStatus()) {
		case WHITE:
			// patient abandons
			this.patientsAbandoned++;
			waitingRoom.remove(p);
			p.setStatus(PatientStatus.OUT);
			System.out.println("\tAbandons "+p);

			break;

		case YELLOW:
			// becomes red
			waitingRoom.remove(p);
			p.setStatus(PatientStatus.RED);
			waitingRoom.add(p);

			Event timeoutEvent = new Event(p, e.getTime() + RED_TIMEOUT, Event.EventType.TIMEOUT);
			queue.add(timeoutEvent);
			System.out.println("\tWorsens "+p);

			break;
			
		case RED:
			// bye-bye...
			waitingRoom.remove(p) ;
			p.setStatus(PatientStatus.BLACK);
			this.patientsDead++ ;
			System.out.println("\tDies "+p);
			
		case OUT:
		case TREATING: 
			// patient was in treatment or already dismissed ... don't do anything, the timeout event was dangling
			// NOTE: Better to implement TODO [1]
			break ;
			
		default:
			throw new InternalError("Wrong status at timeout for event "+e.toString()) ;

		}

	}

	/**
	 * Patient goes out of triage. A severity code is assigned. If a studio is
	 * free, then it is immediately assigned. Otherwise, he is put in the waiting
	 * list.
	 * 
	 * @param e
	 */
	private void processTriageEvent(Event e) {
		Patient p = e.getPatient();

		// assign severity status
		int rand = (int) (1 + Math.random() * 3);
		if (rand == 1)
			p.setStatus(Patient.PatientStatus.WHITE);
		else if (rand == 2)
			p.setStatus(Patient.PatientStatus.YELLOW);
		else if (rand == 3)
			p.setStatus(Patient.PatientStatus.RED);
		System.out.println("\tAssigned status "+p);

		// check if a studio is free
		if (occupiedStudios < NS) {

			// immediately treat!
			occupiedStudios++;

			int duration = 0; // should not happen
			if (p.getStatus() == Patient.PatientStatus.WHITE)
				duration = DURATION_WHITE;
			else if (p.getStatus() == Patient.PatientStatus.YELLOW)
				duration = DURATION_YELLOW;
			else if (p.getStatus() == Patient.PatientStatus.RED)
				duration = DURATION_RED;

			p.setStatus(PatientStatus.TREATING);

			Event exitEvent = new Event(p, e.getTime() + duration, Event.EventType.FREE_STUDIO);
			queue.add(exitEvent);
			
			System.out.println("\tFree studio: Treating "+p);


		} else {
			// waiting list

			waitingRoom.add(p);

			int timeout = 0; // should not happen
			if (p.getStatus() == Patient.PatientStatus.WHITE)
				timeout = WHITE_TIMEOUT;
			else if (p.getStatus() == Patient.PatientStatus.YELLOW)
				timeout = YELLOW_TIMEOUT;
			else if (p.getStatus() == Patient.PatientStatus.RED)
				timeout = RED_TIMEOUT;

			Event timeoutEvent = new Event(p, e.getTime() + timeout, Event.EventType.TIMEOUT);
			queue.add(timeoutEvent);
			
			System.out.println("\tIn Waiting List "+p);

		}
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
