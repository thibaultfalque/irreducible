/**
 * 
 */
package fr.univartois.cril.approximation.solver.criteria;

import java.util.concurrent.atomic.AtomicBoolean;

import org.chocosolver.util.criteria.Criterion;

/**
 * 
 */
public class BooleanCriteria implements Criterion {
	
	private AtomicBoolean stop = new AtomicBoolean(false);
	
	public void setStop(boolean value) {
		stop.set(value);
	}
		
	@Override
	public boolean isMet() {
		return stop.get();
	}

}
