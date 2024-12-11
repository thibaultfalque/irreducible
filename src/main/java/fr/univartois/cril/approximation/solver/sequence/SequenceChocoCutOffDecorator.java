/**
 * 
 */
package fr.univartois.cril.approximation.solver.sequence;

import org.chocosolver.solver.search.restart.ICutoff;

/**
 * 
 */
public class SequenceChocoCutOffDecorator extends AbstractSequence {
	private ICutoff cutoff;

	public SequenceChocoCutOffDecorator(ICutoff cutoff) {
		this.cutoff = cutoff;
	}

	@Override
	public void reset() {
		cutoff.reset();
	}

	@Override
	protected long internalNextGap() {
		return cutoff.getNextCutoff();
	}

}
