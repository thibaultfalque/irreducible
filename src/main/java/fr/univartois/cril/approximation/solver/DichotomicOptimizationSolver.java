/**
 * 
 */
package fr.univartois.cril.approximation.solver;

import org.chocosolver.parser.xcsp.XCSP;
import org.chocosolver.solver.ResolutionPolicy;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.objective.AbstractIntObjManager;
import org.chocosolver.solver.objective.IObjectiveManager;
import org.chocosolver.solver.variables.IntVar;

import fr.univartois.cril.approximation.solver.sequence.ISequence;

/**
 * 
 */
public class DichotomicOptimizationSolver implements IApproximationSolver {

	private int lb;

	private int ub;

	private int middle;

	private Solution solution;

	private ApproximationSolverDecorator solver;

	private AbstractIntObjManager om;

	private int nbLb;

	private int nbUb;

	private ISequence sequence;
	
	private long limitStep;

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

		if (om.getPolicy() == ResolutionPolicy.MINIMIZE) {
			om.updateBestUB(middle);
		} else {
			om.updateBestLB(middle);
		}
		while (lb < ub) {
			var result = solver.solve();
			if (result == UniverseSolverResult.SATISFIABLE) {
				this.solution = solver.getSolution();
				solver.log().printf(java.util.Locale.US, "o %d %.1f\n",
						solver.getObjectiveManager().getBestSolutionValue().intValue(), solver.getTimeCount());
				if (om.getPolicy() == ResolutionPolicy.MINIMIZE) {
					nbUb += om.getBestSolutionValue().intValue() != ub ? 1 : 0;
					ub = om.getBestSolutionValue().intValue();
				} else {
					nbLb += om.getBestSolutionValue().intValue() != lb ? 1 : 0;
					lb = om.getBestSolutionValue().intValue();
				}
				middle = (ub + lb) / 2;
			} else if (result == UniverseSolverResult.UNSATISFIABLE) {
				if (om.getPolicy() == ResolutionPolicy.MINIMIZE) {
					lb = middle + 1;
					nbLb++;
				} else {
					ub = middle - 1;
					nbUb++;
				}
				middle = (ub + lb) / 2;
			} else {
				if (om.getPolicy() == ResolutionPolicy.MINIMIZE) {
					middle = (middle + ub) / 2;
				} else {
					middle = (middle + lb) / 2;
				}
			}
			
			solver.limitSteps(solver.getCurrentStep()*sequence.nextGap());

			System.out.println("New bound " + lb + " " + middle + " " + ub);
			System.out.println("New approximation phase with new bounds and "+ (solver.getCurrentStep()*sequence.nextGap())+" steps");
			solver.reset();
			if (om.getPolicy() == ResolutionPolicy.MINIMIZE) {
				om.updateBestLB(lb);
				om.updateBestUB(middle);
			} else if (om.getPolicy() == ResolutionPolicy.MAXIMIZE) {
				om.updateBestLB(middle);
				om.updateBestUB(ub);
			}

		}
		return solution != null ? UniverseSolverResult.OPTIMUM_FOUND : UniverseSolverResult.UNSATISFIABLE;
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
