/**
 * 
 */
package fr.univartois.cril.approximation.solver.criteria;

import org.chocosolver.solver.ICause;
import org.chocosolver.solver.constraints.Propagator;
import org.chocosolver.solver.propagation.PropagationObserver;
import org.chocosolver.solver.variables.Variable;
import org.chocosolver.solver.variables.events.IEventType;

/**
 * 
 */
public class PropagationMeasureObserver implements PropagationObserver {

	@Override
	public void onCoarseEvent(Propagator<?> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFailure(ICause arg0, Propagator<?> arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFiltering(ICause arg0, Propagator<?> arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFineEvent(Propagator<?> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onVariableModification(Variable arg0, IEventType arg1, ICause arg2) {
		// TODO Auto-generated method stub

	}

}
