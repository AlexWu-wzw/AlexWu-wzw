package main.control;
/**
 * @(#) Controller.java
 */

import main.model.*;
import main.util.ConformanceChecker;
import main.util.EntityManager;

import java.util.List;

/**
 * @(#) Controller.java
 */

public class Controller {
	private EntityManager entityManager;

	private Log eventLog;
	private PetriNet petriNet;
	
	public Controller() {
		entityManager = new EntityManager();
	}
	
	public void execute( String filePathLog, String filePathPetriNet) {
		// 事件日志文件转为Log 与 petri网
		eventLog = entityManager.getLog(filePathLog);
		petriNet = entityManager.getPetriNet(filePathPetriNet);
		List<Trace> traces = eventLog.getTraces();
		//petriNet.replayTrace(traces);
		ConformanceChecker conformanceChecker = new ConformanceChecker();
		conformanceChecker.replay(traces,petriNet);
	}

	private double calculateFitness( List<Trace> traces ) {
		double missing = 0, consumed = 0, remaining = 0, produced = 0;
		for( int i = 0; i < traces.size(); ++i){
			System.out.println( traces.get(i).getNumber() + "  缺失:" + traces.get(i).getMissing() + "  消耗:" + traces.get(i).getConsumed() + "  遗留:" + Math.abs(traces.get(i).getRemaining()) + "  产生:" + traces.get(i).getProduced() );
			missing += ( traces.get(i).getNumber() * Math.abs(traces.get(i).getMissing()) );
			consumed += ( traces.get(i).getNumber() * Math.abs(traces.get(i).getConsumed()) );
			remaining += ( traces.get(i).getNumber() * Math.abs(traces.get(i).getRemaining() ));
			produced += ( traces.get(i).getNumber() * Math.abs(traces.get(i).getProduced()) );
		}
		
		return (1.0 - (missing/consumed + remaining/produced)/2.0);
	}
	
	public double computeFitness() {
		List<Trace> traces = eventLog.getTraces();
		return ( this.calculateFitness(traces) );
	}
	
	private double calculateBehavioralAppropriateness( List<Trace> traces, int transitionLength ) {
		List<Transition> transitions = petriNet.getTransitions();
		double sumNumberMultyiplyByDisabledTransitions = 0, number = 0, numberTransitions = transitions.size();
		for( int i = 0; i < traces.size(); ++i)
		{	
			sumNumberMultyiplyByDisabledTransitions += (traces.get(i).getNumber() * (numberTransitions - traces.get(i).getEnabled()/(double)(traces.get(i).getEvents().size()) ));
			number += traces.get(i).getNumber();
		}
		return sumNumberMultyiplyByDisabledTransitions/((numberTransitions-1) * number) ;
	}
	
	public double computeBehavioralAppropriateness() {
		List<Trace> traces = eventLog.getTraces();
		List<Transition> transitions = petriNet.getTransitions();
		return (this.calculateBehavioralAppropriateness(traces, transitions.size() ));
	}
	
	private double calculateStructuralAppropriateness( int transitionLength, int placesLength ) {
		double label = (double)transitionLength + 2.0;
		double node = (double)transitionLength + (double)placesLength;
		return label/node;
	}
	
	public double computeStructuralAppropriateness() {
		List<Transition> transitions = petriNet.getTransitions();
		List<Place> places = petriNet.getPlaces();
		return (this.calculateStructuralAppropriateness( transitions.size(), places.size() ));
	}
	
	
}
