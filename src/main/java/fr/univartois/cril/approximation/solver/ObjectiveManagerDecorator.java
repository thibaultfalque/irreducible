/**
 * 
 */
package fr.univartois.cril.approximation.solver;

import java.util.function.Consumer;
import java.util.function.Function;

import org.chocosolver.solver.ResolutionPolicy;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.learn.ExplanationForSignedClause;
import org.chocosolver.solver.objective.IObjectiveManager;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.Variable;

/**
 * 
 */
public class ObjectiveManagerDecorator<V extends Variable> implements IObjectiveManager<V> {
	
	private IObjectiveManager<V> decoree;
	
	public ObjectiveManagerDecorator(IObjectiveManager<V> d){
		this.decoree=d;
	}

	/**
	 * @param pivot
	 * @param explanation
	 * @see org.chocosolver.solver.ICause#explain(int, org.chocosolver.solver.learn.ExplanationForSignedClause)
	 */
	public void explain(int pivot, ExplanationForSignedClause explanation) {
		decoree.explain(pivot, explanation);
	}

	/**
	 * @param action
	 * @see org.chocosolver.solver.ICause#forEachIntVar(java.util.function.Consumer)
	 */
	public void forEachIntVar(Consumer<IntVar> action) {
		decoree.forEachIntVar(action);
	}

	/**
	 * @return
	 * @see org.chocosolver.solver.objective.IBoundsManager#getBestLB()
	 */
	public Number getBestLB() {
		return decoree.getBestLB();
	}

	/**
	 * @return
	 * @see org.chocosolver.solver.objective.IBoundsManager#getBestSolutionValue()
	 */
	public Number getBestSolutionValue() {
		return decoree.getBestSolutionValue();
	}

	/**
	 * @return
	 * @see org.chocosolver.solver.objective.IBoundsManager#getBestUB()
	 */
	public Number getBestUB() {
		return decoree.getBestUB();
	}

	/**
	 * @return
	 * @see org.chocosolver.solver.objective.IObjectiveManager#getObjective()
	 */
	public V getObjective() {
		return decoree.getObjective();
	}

	/**
	 * @return
	 * @see org.chocosolver.solver.objective.IBoundsManager#getPolicy()
	 */
	public ResolutionPolicy getPolicy() {
		return decoree.getPolicy();
	}

	/**
	 * @return
	 * @see org.chocosolver.solver.objective.IBoundsManager#isOptimization()
	 */
	public boolean isOptimization() {
		return decoree.isOptimization();
	}

	/**
	 * @throws ContradictionException
	 * @see org.chocosolver.solver.objective.IObjectiveManager#postDynamicCut()
	 */
	public void postDynamicCut() throws ContradictionException {
		//decoree.postDynamicCut();
	}

	/**
	 * @param arg0
	 * @see org.chocosolver.solver.objective.IObjectiveManager#setCutComputer(java.util.function.Function)
	 */
	public void setCutComputer(Function<Number, Number> arg0) {
		decoree.setCutComputer(arg0);
	}

	/**
	 * 
	 * @see org.chocosolver.solver.objective.IObjectiveManager#setStrictDynamicCut()
	 */
	public void setStrictDynamicCut() {
		decoree.setStrictDynamicCut();
	}

	/**
	 * 
	 * @see org.chocosolver.solver.objective.IObjectiveManager#setWalkingDynamicCut()
	 */
	public void setWalkingDynamicCut() {
		decoree.setWalkingDynamicCut();
	}

	/**
	 * @return
	 * @see org.chocosolver.solver.objective.IObjectiveManager#updateBestSolution()
	 */
	public boolean updateBestSolution() {
		return decoree.updateBestSolution();
	}

	/**
	 * @param arg0
	 * @return
	 * @see org.chocosolver.solver.objective.IObjectiveManager#updateBestSolution(java.lang.Number)
	 */
	public boolean updateBestSolution(Number arg0) {
		return decoree.updateBestSolution(arg0);
	}

	@Override
	public void resetBestBounds() {
		decoree.resetBestBounds();
	}
	
}
