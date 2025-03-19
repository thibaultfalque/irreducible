/**
 * approximation, a constraint programming solver based on Choco, utilizing relaxation
 * techniques.
 * Copyright (c) 2025 - Univ Artois, CNRS & Luxembourg University.
 * All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 * If not, see {@link http://www.gnu.org/licenses}.
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
 * The RestartObserver.
 *
 * @author Romain Wallon
 * @version 0.1.0
 */
public class RestartObserver implements IVariableMonitor, IMonitorRestart {

    /** The assigned vars. */
    private Set<String> assignedVars = new HashSet<>();

    /** The solver. */
    private final ApproximationSolverDecorator solver;

    /** The nb runs. */
    private int nbRuns;

    /** The ratio limit. */
    private final double ratioLimit;

    /** The restart limit. */
    private int restartLimit;

    /** The restart factor. */
    private final double restartFactor;

    /**
     * Creates a new RestartObserver.
     *
     * @param solver the solver
     * @param ratioLimit the ratio limit
     * @param restartLimit the restart limit
     * @param restartFactor the restart factor
     */
    public RestartObserver(ApproximationSolverDecorator solver, double ratioLimit, int restartLimit,
            double restartFactor) {
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
                solver.limitFail(restartLimit);
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

    /*
     * (non-Javadoc)
     *
     * @see org.chocosolver.solver.variables.IVariableMonitor#onUpdate(org.chocosolver.solver.variables.Variable, org.chocosolver.solver.variables.events.IEventType)
     */
    @Override
    public void onUpdate(Variable variable, IEventType event) {
        if (variable.getDomainSize() == 1 && !assignedVars.contains(variable.getName())) {
            assignedVars.add(variable.getName());
        } else if (variable.getDomainSize() != 1 && assignedVars.contains(variable.getName())) {
            assignedVars.remove(variable.getName());
        }
    }

}
