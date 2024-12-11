/**
 * 
 */
package fr.univartois.cril.approximation.solver.sequence;

/**
 * 
 */
public abstract class AbstractSequence implements ISequence {
	protected long gap;
	
	protected abstract long internalNextGap();

	@Override
	public long nextGap() {
		gap = internalNextGap();
		return gap;
	}	
}
