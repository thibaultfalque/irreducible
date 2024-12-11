/**
 * 
 */
package fr.univartois.cril.approximation.solver.sequence;

/**
 * 
 */
public class ExponentialSequence extends AbstractSequence {

	private double factor;

	public ExponentialSequence(double factor) {
		this.factor = factor;
	}

	@Override
	public void reset() {
		this.gap = 1;
	}

	@Override
	protected long internalNextGap() {
		gap = (long) (gap * factor) + 1;
        return gap;
	}

}
