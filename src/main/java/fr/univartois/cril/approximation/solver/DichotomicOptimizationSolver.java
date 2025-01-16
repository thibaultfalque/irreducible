/**
 * 
 */
package fr.univartois.cril.approximation.solver;

import org.chocosolver.parser.xcsp.XCSP;
import org.chocosolver.solver.ResolutionPolicy;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.objective.AbstractIntObjManager;
import org.chocosolver.solver.objective.IObjectiveManager;
import org.chocosolver.solver.search.loop.monitors.IMonitorRestart;
import org.chocosolver.solver.search.strategy.assignments.DecisionOperator;
import org.chocosolver.solver.search.strategy.assignments.DecisionOperatorFactory;
import org.chocosolver.solver.search.strategy.decision.Decision;
import org.chocosolver.solver.search.strategy.selectors.values.IntValueSelector;
import org.chocosolver.solver.search.strategy.selectors.variables.VariableSelector;
import org.chocosolver.solver.search.strategy.strategy.AbstractStrategy;
import org.chocosolver.solver.search.strategy.strategy.IntStrategy;
import org.chocosolver.solver.variables.IntVar;

import fr.univartois.cril.approximation.solver.sequence.ISequence;

/**
 * 
 */
public class DichotomicOptimizationSolver implements IApproximationSolver {

	public static class DichotomicObjectiveVariableSearchStrategy extends IntStrategy implements IMonitorRestart {
		public boolean b;
		private boolean enabled;

		public DichotomicObjectiveVariableSearchStrategy(IntVar[] scope, VariableSelector<IntVar> varSelector,
				IntValueSelector valSelector, DecisionOperator<IntVar> decOperator) {
			super(scope, varSelector, valSelector, decOperator);
			enabled = true;
		}

		@Override
		public Decision<IntVar> computeDecision(IntVar variable) {
			if (b || !enabled) {
				return null;
			}
			b = true;
			var decision = super.computeDecision(variable);
			decision.setRefutable(false);
			return decision;
		}

		public void setEnabled(boolean e) {
			enabled = e;
		}

		@Override
		public void afterRestart() {
			b = false;
		}

	}

	private static class IntValSelector implements IntValueSelector {
		private int value;

		public IntValSelector(int value) {
			this.value = value;
		}

		@Override
		public int selectValue(IntVar var) {
			return value;
		}
	}

	private int lb;

	private int ub;

	private int middle;

	private Solution solution;

	private static ApproximationSolverDecorator solver;

	private AbstractIntObjManager om;

	private ISequence sequence;

	private long limitStep;

	private static DichotomicObjectiveVariableSearchStrategy s1;
	private static DichotomicObjectiveVariableSearchStrategy s2;

	/**
	 * @param solver
	 */
	public DichotomicOptimizationSolver(ApproximationSolverDecorator solver, ISequence sequence, long limitStep) {
		super();
		this.solver = solver;
		this.sequence = sequence;
		this.limitStep = limitStep;
		IObjectiveManager<IntVar> localOm = solver.getObjectiveManager();
		om = (AbstractIntObjManager) localOm;
		solver.setObjectiveManager(new ObjectiveManagerDecorator<>(om));

		lb = om.getBestLB().intValue() + 1;
		ub = om.getBestUB().intValue() - 1;
		middle = (ub + lb) / 2;

		System.out.println("Start bound " + lb + " " + middle + " " + ub);
	}

	public UniverseSolverResult solve() {

		solver.limitSolution(1);
		solver.limitSteps(limitStep);
		AbstractStrategy<IntVar> searchStrategies = solver.getSearch();
		var scope = new IntVar[] { (IntVar) solver.getObjectiveManager().getObjective() };

		VariableSelector<IntVar> variableSelector = new VariableSelector<IntVar>() {
			@Override
			public IntVar getVariable(IntVar[] variables) {
				return variables[0];
			}
		};
		while (lb < ub) {

			if (om.getPolicy() == ResolutionPolicy.MINIMIZE) {
				s1 = new DichotomicObjectiveVariableSearchStrategy(scope, variableSelector, new IntValSelector(middle),
						DecisionOperatorFactory.makeIntSplit());
				s2 = new DichotomicObjectiveVariableSearchStrategy(scope, variableSelector, new IntValSelector(lb),
						DecisionOperatorFactory.makeIntReverseSplit());
				System.out.println("bound " + lb + " " + middle);
				solver.setSearch(s1, s2, searchStrategies);
			} else {
				s1 = new DichotomicObjectiveVariableSearchStrategy(scope, variableSelector, new IntValSelector(ub),
						DecisionOperatorFactory.makeIntSplit());
				s2 = new DichotomicObjectiveVariableSearchStrategy(scope, variableSelector, new IntValSelector(middle),
						DecisionOperatorFactory.makeIntReverseSplit());
				solver.setSearch(s1, s2, searchStrategies);
			}
			solver.plugMonitor(s1);
			solver.plugMonitor(s2);
			var result = solver.solve();
			if (result == UniverseSolverResult.SATISFIABLE) {
				this.solution = solver.getSolution();
				solver.log().printf(java.util.Locale.US, "o %d %.1f\n",
						solver.getObjectiveManager().getBestSolutionValue().intValue(), solver.getTimeCount());
				if (om.getPolicy() == ResolutionPolicy.MINIMIZE) {

					ub = om.getBestSolutionValue().intValue();
				} else {

					lb = om.getBestSolutionValue().intValue();
				}
				middle = (ub + lb) / 2;
			} else if (result == UniverseSolverResult.UNSATISFIABLE) {
				if (om.getPolicy() == ResolutionPolicy.MINIMIZE) {
					lb = middle + 1;
				} else {
					ub = middle - 1;
				}
				middle = (ub + lb) / 2;
			} else {
				if (om.getPolicy() == ResolutionPolicy.MINIMIZE) {
					middle = (middle + ub) / 2;
				} else {
					middle = (middle + lb) / 2;
				}
			}

			long nextGap = sequence.nextGap();
			solver.limitSteps(solver.getCurrentStep() * nextGap);

			solver.reset();
			System.out.println("New bound " + lb + " " + middle + " " + ub);

		}
		return solution != null ? UniverseSolverResult.OPTIMUM_FOUND : UniverseSolverResult.UNSATISFIABLE;
	}

	public static void reset() {
		s1.b = false;
		s2.b = false;
	}

	@Override
	public void displaySolution(XCSP xcsp) {
		solver.displaySolution(xcsp);
	}

	@Override
	public void restoreSolution() {
		solver.restoreSolution();
	}

}
