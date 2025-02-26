/**
 * 
 */
package fr.univartois.cril.approximation.solver.criteria;

import org.chocosolver.util.criteria.Criterion;

/**
 * 
 */
public class PropCriteria implements Criterion {
	private long cpt;
	private long limit;

	public PropCriteria(long l) {
		this.limit = l;
	}

	public void incNbCalledProp() {
		cpt++;
	}

	public void reset() {
		this.cpt = 0;
	}

	@Override
	public boolean isMet() {
		return cpt >= limit;
	}

}
