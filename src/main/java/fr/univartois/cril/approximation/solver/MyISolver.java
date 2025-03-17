package fr.univartois.cril.approximation.solver;

import org.chocosolver.solver.ISolver;
import org.chocosolver.solver.objective.IObjectiveManager;
import org.chocosolver.solver.search.loop.monitors.ISearchMonitor;
import org.chocosolver.solver.variables.Variable;
import org.chocosolver.util.criteria.Criterion;

public interface MyISolver extends ISolver {

	UniverseSolverResult solve();
	
	void plugMonitor(ISearchMonitor monitor);
	
	<V extends Variable> IObjectiveManager<V> getObjectiveManager();
	
	void addStopCriterion(Criterion... criterion);
}
