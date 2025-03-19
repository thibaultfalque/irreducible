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

import org.chocosolver.parser.xcsp.XCSP;
import org.chocosolver.solver.Solver;

import fr.univartois.cril.approximation.core.KeepNoGoodStrategy;
import fr.univartois.cril.approximation.solver.SolverConfiguration;
import fr.univartois.cril.approximation.solver.UniverseSolverResult;
import fr.univartois.cril.approximation.util.ISolverListener;

/**
 * The ISolverState.
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public interface ISolverState {

    /**
     * Solve.
     *
     * @return the universe solver result
     */
    UniverseSolverResult solve();

    /**
     * Solve starter.
     *
     * @return the universe solver result
     */
    UniverseSolverResult solveStarter();

    /**
     * Next state.
     *
     * @return the i solver state
     */
    ISolverState nextState();

    /**
     * Previous state.
     *
     * @return the i solver state
     */
    ISolverState previousState();

    /**
     * Reset no goods.
     *
     * @param ngStrategy the ng strategy
     * @param ace the ace
     */
    void resetNoGoods(KeepNoGoodStrategy ngStrategy, Solver ace);

    /**
     * Display solution.
     *
     * @param xcsp the xcsp
     */
    void displaySolution(XCSP xcsp);

    /**
     * Gets the nb removed.
     *
     * @return the nb removed
     */
    int getNbRemoved();

    /**
     * Checks if is timeout.
     *
     * @return true, if is timeout
     */
    boolean isTimeout();

    /**
     * Checks if is restored.
     *
     * @return true, if is restored
     */
    boolean isRestored();

    /**
     * Reset limit solver.
     */
    void resetLimitSolver();

    /**
     * Gets the config.
     *
     * @return the config
     */
    SolverConfiguration getConfig();

    /**
     * Checks if is safe.
     *
     * @return true, if is safe
     */
    boolean isSafe();

    /**
     * Sets the solver listener.
     *
     * @param listener the new solver listener
     */
    void setSolverListener(ISolverListener listener);

}
