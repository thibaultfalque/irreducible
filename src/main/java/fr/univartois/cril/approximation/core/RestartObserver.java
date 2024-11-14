/**
 * This file is a part of the {@code fr.univartois.cril.approximation.core} package.
 *
 * It contains the type RestartObserver.
 *
 * (c) 2023 Romain Wallon - approximation.
 * All rights reserved.
 */

package fr.univartois.cril.approximation.core;

import java.util.HashSet;
import java.util.Set;

import org.chocosolver.solver.search.loop.monitors.IMonitorRestart;
import org.chocosolver.solver.variables.IVariableMonitor;
import org.chocosolver.solver.variables.Variable;
import org.chocosolver.solver.variables.events.IEventType;

import fr.univartois.cril.approximation.solver.ApproximationSolverDecorator;


/**
 * The RestartObserver
 *
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public class RestartObserver implements IVariableMonitor, IMonitorRestart {
	
	private Set<String> assignedVars = new HashSet<>();

    private final ApproximationSolverDecorator solver;


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
    public RestartObserver(ApproximationSolverDecorator solver, double ratioLimit, int restartLimit, double restartFactor) {
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
    public void afterRestart() {
        nbRuns++;
        if (nbRuns >= restartLimit - 1) {
            double ratio = (double) assignedVars.size() / (double) solver.nVariables();
            if (ratio >= ratioLimit) {
                restartLimit *= restartFactor;
                solver.limitRestart(restartLimit);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see interfaces.Observers.ObserverOnRuns#beforeRun()
     */
    @Override
    public void beforeRestart() {
        assignedVars.clear();
    }


	@Override
	public void onUpdate(Variable arg0, IEventType arg1) {
		if (arg0.getDomainSize() == 1 && !assignedVars.contains(arg0.getName())) {
			assignedVars.add(arg0.getName());
		} else if (arg0.getDomainSize() != 1 && assignedVars.contains(arg0.getName())) {
			assignedVars.remove(arg0.getName());
		} 
	}

}

