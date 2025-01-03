package fr.univartois.cril.approximation.solver;

import org.chocosolver.parser.xcsp.XCSP;

public interface IApproximationSolver {

	UniverseSolverResult solve();

	void displaySolution(XCSP xcsp);
	
	//String printSolution(boolean format);
	
	void restoreSolution();

}