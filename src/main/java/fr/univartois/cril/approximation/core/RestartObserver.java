/**
 * This file is a part of the {@code fr.univartois.cril.approximation.core} package.
 *
 * It contains the type RestartObserver.
 *
 * (c) 2023 Romain Wallon - approximation.
 * All rights reserved.
 */

package fr.univartois.cril.approximation.core;

import interfaces.Observers.ObserverOnAssignments;
import interfaces.Observers.ObserverOnRuns;
import solver.Solver;
import variables.Variable;


/**
 * The RestartObserver
 *
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public class RestartObserver implements ObserverOnRuns, ObserverOnAssignments {

    private final Solver solver;

    private int nbAssigned;

    private int nbRuns;

    private final double ratioLimit;

    private int restartLimit;

    private final double restartFactor;

    /**
     * Creates a new RestartObserver.
     * @param solver
     * @param ratioLimit
     * @param restartLimit
     */
    public RestartObserver(Solver solver, double ratioLimit, int restartLimit, double restartFactor) {
        this.solver = solver;
        this.ratioLimit = ratioLimit;
        this.restartLimit = (int) (restartLimit / restartFactor);
        this.restartFactor = restartFactor;
    }

    /*
     * (non-Javadoc)
     *
     * @see interfaces.Observers.ObserverOnRuns#afterRun()
     */
    @Override
    public void afterRun() {
        nbRuns++;
        if (nbRuns >= restartLimit - 1) {
            double ratio = (double) nbAssigned / (double) solver.problem.variables.length;
            if (ratio >= ratioLimit) {
                restartLimit *= restartFactor;
                solver.head.getBuilder().getOptionsRestartsBuilder().setnRuns(restartLimit);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see interfaces.Observers.ObserverOnRuns#beforeRun()
     */
    @Override
    public void beforeRun() {
        nbAssigned = 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see interfaces.Observers.ObserverOnAssignments#afterAssignment(variables.Variable, int)
     */
    @Override
    public void afterAssignment(Variable arg0, int arg1) {
        nbAssigned++;
    }

    /*
     * (non-Javadoc)
     *
     * @see interfaces.Observers.ObserverOnAssignments#afterFailedAssignment(variables.Variable, int)
     */
    @Override
    public void afterFailedAssignment(Variable arg0, int arg1) {
        // Nothing to do.
    }

    /*
     * (non-Javadoc)
     *
     * @see interfaces.Observers.ObserverOnAssignments#afterUnassignment(variables.Variable)
     */
    @Override
    public void afterUnassignment(Variable arg0) {
        // Nothing to do.
    }

}

