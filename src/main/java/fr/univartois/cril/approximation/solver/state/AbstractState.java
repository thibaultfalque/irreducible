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

import fr.univartois.cril.aceurancetourix.JUniverseAceProblemAdapter;
import fr.univartois.cril.approximation.core.RestartObserver;
import fr.univartois.cril.approximation.solver.ApproximationSolverDecorator;
import fr.univartois.cril.approximation.solver.SolverConfiguration;
import fr.univartois.cril.juniverse.core.IUniverseSolver;
import fr.univartois.cril.juniverse.core.UniverseSolverResult;

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
    protected IUniverseSolver solver;
    protected ApproximationSolverDecorator decorator;

    /**
     * Creates a new AbstractState.
     */
    public AbstractState(SolverConfiguration config,IUniverseSolver solver,ApproximationSolverDecorator decorator) {
        this.config = config;
        this.solver=solver;
        this.decorator=decorator;
    }

    protected void resetLimitSolver() {
        ((JUniverseAceProblemAdapter)solver).getHead().getSolver().solutions.limit=config.getLimitSolution();
        ((JUniverseAceProblemAdapter)solver).getBuilder().getOptionsRestartsBuilder().setnRuns(config.getNbRun());
        ((JUniverseAceProblemAdapter)solver).getHead().solver.restarter.reset();

        ((JUniverseAceProblemAdapter)solver).getHead().solver.nRecursiveRuns=0;

        this.config=config.update();
    }

    protected UniverseSolverResult internalSolve() {
        var observer = new RestartObserver(((JUniverseAceProblemAdapter)solver).getHead().getSolver(), config.getRatio(), config.getNbRun(), config.getFactor());
        ((JUniverseAceProblemAdapter)solver).getHead().getSolver().observersOnRuns.add(observer);
        ((JUniverseAceProblemAdapter)solver).getHead().getSolver().observersOnAssignments.add(observer);
        var r = solver.solve();
        ((JUniverseAceProblemAdapter)solver).getHead().getSolver().observersOnAssignments.remove(observer);
        ((JUniverseAceProblemAdapter)solver).getHead().getSolver().observersOnRuns.remove(observer);
        return r;
    }

}
