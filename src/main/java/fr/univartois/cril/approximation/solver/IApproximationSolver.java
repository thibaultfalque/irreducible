package fr.univartois.cril.approximation.solver;

public interface IApproximationSolver {

	UniverseSolverResult solve();

	void displaySolution();
	
	//String printSolution(boolean format);
	
	void restoreSolution();

}