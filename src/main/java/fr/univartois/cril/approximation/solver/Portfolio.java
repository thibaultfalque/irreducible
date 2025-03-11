package fr.univartois.cril.approximation.solver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.chocosolver.solver.ResolutionPolicy;
import org.chocosolver.solver.search.loop.monitors.IMonitorSolution;

import fr.univartois.cril.approximation.solver.criteria.BooleanCriteria;

public class Portfolio {

	private List<MyISolver> solvers = new ArrayList<>();

	private ExecutorService service;

	private Integer bestIndex;

	private Integer bestBound;

	private UniverseSolverResult result = UniverseSolverResult.UNKNOWN;

	private BooleanCriteria stopSolver;

	public Portfolio() {
		stopSolver = new BooleanCriteria();
	}

	public void addSolver(MyISolver solver) {
		solvers.add(solver);
	}

	public UniverseSolverResult solve() {
		service = Executors.newFixedThreadPool(solvers.size());
		for (int i = 0; i < solvers.size(); i++) {
			var solver = solvers.get(i);
			solver.plugMonitor(createMonitor(i));
			solver.addStopCriterion(stopSolver);
			service.submit(() -> solve(solver));
		}
		try {
			service.awaitTermination(600, TimeUnit.SECONDS);
			stopSolver.setStop(true);
			service.shutdownNow();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		return result;
	}

	private void solve(MyISolver solver) {
		var tmp = solver.solve();
		synchronized (result) {
			if (tmp != UniverseSolverResult.UNKNOWN) {
				result = tmp;
			}
		}

	}

	private IMonitorSolution createMonitor(int solverIndex) {
		return () -> onSolution(solverIndex);
	}

	private synchronized void onSolution(int solverIndex) {
		var om = solvers.get(solverIndex).getObjectiveManager();
		if (om.isOptimization()) {
			int bound = (int) om.getBestSolutionValue();
			boolean updated = false;
			if (om.getPolicy() == ResolutionPolicy.MAXIMIZE && (bestBound == null || bound > bestBound)) {
				bestBound = bound;
				bestIndex = solverIndex;
				updated = true;
			} else if (om.getPolicy() == ResolutionPolicy.MINIMIZE && (bestBound == null || bound < bestBound)) {
				bestBound = bound;
				bestIndex = solverIndex;
				updated = true;
			}
			System.out.println("New bound " + bestBound + " found by the solver " + bestIndex);
			if (updated) {
				for (MyISolver solver : solvers) {
					solver.getObjectiveManager().updateBestSolution(bestBound);

				}
			}
		}
	}

	public MyISolver getBestSolver() {
		return solvers.get(bestIndex);
	}

	public void stop() {
		stopSolver.setStop(true);
	}
}
