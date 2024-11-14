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

import fr.univartois.cril.approximation.core.RestartObserver;
import fr.univartois.cril.approximation.solver.ApproximationSolverDecorator;
import fr.univartois.cril.approximation.solver.SolverConfiguration;
import fr.univartois.cril.approximation.solver.UniverseSolverResult;


/**
 * The AbstractState
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public abstract class AbstractState implements ISolverState {

    protected SolverConfiguration config;
    protected Solver solver;
    protected ApproximationSolverDecorator decorator;
    protected int nbRemoved;

    /**
     * Creates a new AbstractState.
     */
    public AbstractState(SolverConfiguration config,Solver solver,ApproximationSolverDecorator decorator) {
        this.config = config;
        this.solver=solver;
        this.decorator=decorator;
    }

    protected void resetLimitSolver() {
        //solver.limitSolution(config.getLimitSolution());
        //solver.limitRestart(config.getNbRun());
        //solver.limitFail(25);

        this.config=config.update();
    }

    protected UniverseSolverResult internalSolve() {
        var observer = new RestartObserver(decorator, config.getRatio(), config.getNbRun(), config.getFactor());
        solver.plugMonitor(observer);
        solver.solve();
        var f = solver.isFeasible();
        solver.unplugMonitor(observer);
        return switch(f) {
        case TRUE -> UniverseSolverResult.SATISFIABLE;
        case FALSE -> UniverseSolverResult.UNSATISFIABLE;
        default -> UniverseSolverResult.UNKNOWN;
        };
    }
    @Override
    public boolean isTimeout() {
        return decorator.isUserinterruption();
    }
}
