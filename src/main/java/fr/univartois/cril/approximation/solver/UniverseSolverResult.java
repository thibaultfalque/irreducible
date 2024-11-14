package fr.univartois.cril.approximation.solver;

/**
 * The UniverseSolverResult represents the result returned by the solver at the end of its
 * execution.
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.2.0
 */
public enum UniverseSolverResult {

    /**
     * The result returned by the solver when it has proven the problem to be
     * inconsistent.
     */
    UNSATISFIABLE,

    /**
     * The result returned by the solver when it has found a solution.
     */
    SATISFIABLE,

    /**
     * The result returned by the solver when it has found an optimal solution.
     */
    OPTIMUM_FOUND,

    /**
     * The result returned by the solver when it has not been able to decide the
     * consistency of the problem.
     */
    UNKNOWN,

    /**
     * The result returned by the solver when the input problem contains unsupported
     * features.
     */
    UNSUPPORTED

}
