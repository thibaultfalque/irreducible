/**
 * approximation, an approximation solver.
 * Copyright (c) 2023 - Univ Artois, CNRS & Exakis Nelite.
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

package fr.univartois.cril.approximation.solver.state;

import org.chocosolver.solver.Solver;
import org.chocosolver.util.ESat;

import fr.univartois.cril.approximation.core.RestartObserver;
import fr.univartois.cril.approximation.solver.ApproximationSolverDecorator;
import fr.univartois.cril.approximation.solver.SolverConfiguration;
import fr.univartois.cril.approximation.solver.SolverContext;
import fr.univartois.cril.approximation.solver.UniverseSolverResult;

/**
 * The {@code AbstractState} class serves as a base class for different solver states
 * in the approximation framework. It provides common functionalities for managing
 * solver configurations, executing solving processes, and handling state transitions.
 *
 * <p>
 * This class integrates with {@link ApproximationSolverDecorator} to control
 * solver behavior and relaxation strategies, ensuring smooth transitions between
 * different solving phases.
 * </p>
 *
 * <h2>Key Responsibilities:</h2>
 * <ul>
 * <li>Maintains references to the solver, its configuration, and its context.</li>
 * <li>Defines the mechanism for resetting solver limits based on updated
 * configurations.</li>
 * <li>Implements an internal solving mechanism that supports optimization
 * objectives.</li>
 * <li>Handles timeout detection</li>
 * </ul>
 *
 * <h2>State Transition:</h2>
 * <p>
 * Each solver state extends this class and overrides specific behaviors to dictate
 * how the solver progresses from one state to another.
 * </p>
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public abstract class AbstractState implements ISolverState {

    /**
     * The configuration settings for the solver in the current state.
     * This includes parameters such as failure thresholds, scaling factors, and
     * solution limits that influence solver behavior.
     */
    protected SolverConfiguration config;

    /**
     * The shared context containing global solver configurations.
     * This provides access to different solver configurations and helps manage
     * transitions between solver states.
     */
    protected SolverContext context;

    /** The solver instance. */
    protected Solver solver;

    /**
     * The decorator that enhances the solver with additional approximation mechanisms.
     */
    protected ApproximationSolverDecorator decorator;

    /**
     * The number of constraints removed during the relaxation process.
     */
    protected int nbRemoved;

    /**
     * A reference to the strategy for restoring constraints during the relaxation
     * process.
     * This strategy determines how and when constraints are reintroduced after being
     * relaxed.
     */
    protected PathStrategy pathStrategy;

    /**
     * Constructs a new {@code AbstractState} instance, initializing the solver state
     * with the provided context, solver, decorator, and path strategy.
     *
     * @param context the shared solver context containing global configurations
     * @param solver the solver instance
     * @param decorator the decorator managing solver state transitions and approximations
     * @param pathStrategy the strategy defining how constraints are reintroduced
     *        during the relaxation process
     */
    protected AbstractState(SolverContext context, Solver solver,
            ApproximationSolverDecorator decorator, PathStrategy pathStrategy) {
        this.context = context;
        this.solver = solver;
        this.decorator = decorator;
        this.pathStrategy = pathStrategy;
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.solver.state.ISolverState#resetLimitSolver()
     */
    @Override
    public void resetLimitSolver() {
        solver.limitSolution(config.getLimitSolution());
        solver.limitFail(config.getNbFailed());
        this.config = config.update();
    }

    /**
     * Internal solve.
     *
     * @return the universe solver result
     */
    protected UniverseSolverResult internalSolve() {
        var observer = new RestartObserver(decorator, config.getRatio(), config.getNbFailed(),
                config.getFactor());
        solver.plugMonitor(observer);
        var f = false;
        if (solver.getObjectiveManager().isOptimization()) {
            f = solver.solve();
            if (f) {
                solver.log().printf(java.util.Locale.US, "o %d %.1f\n",
                        solver.getObjectiveManager().getBestSolutionValue().intValue(),
                        solver.getTimeCount());
            }
            while (f) {
                f = solver.solve();
                if (f) {
                    solver.log().printf(java.util.Locale.US, "o %d %.1f\n",
                            solver.getObjectiveManager().getBestSolutionValue().intValue(),
                            solver.getTimeCount());
                }
            }
        } else {
            f = solver.solve();
        }
        solver.log().white().printf("%s %n", solver.getMeasures().toOneLineString());
        solver.unplugMonitor(observer);
        decorator.setUserInterruption(false);
        
        var feasible = solver.isFeasible();
        if (feasible == ESat.TRUE) {
        	return UniverseSolverResult.SATISFIABLE;
        }
        if (feasible == ESat.FALSE) {
        	return UniverseSolverResult.UNSATISFIABLE;
        }
        return UniverseSolverResult.UNKNOWN;
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.solver.state.ISolverState#isTimeout()
     */
    @Override
    public boolean isTimeout() {
        return decorator.isUserinterruption();
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.solver.state.ISolverState#getConfig()
     */
    @Override
    public SolverConfiguration getConfig() {
        return config;
    }

}
