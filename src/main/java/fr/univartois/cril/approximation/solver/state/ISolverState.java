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
 * Represents the state of a solver during the solving process.
 * <p>
 * The {@code ISolverState} interface defines methods to manage different
 * stages of the solver, such as transitioning between states, resetting constraints,
 * handling solutions, and managing solver configurations.
 * </p>
 * <p>
 * Implementations of this interface allow for the tracking and control of
 * solver execution flow, including timeout handling and restoration.
 * </p>
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public interface ISolverState {

    /**
     * Solves the current state of the solver.
     *
     * @return The result of the solving process as a {@link UniverseSolverResult}.
     */
    UniverseSolverResult solve();

    /**
     * Solves using the initial state setup.
     *
     * @return The result of the solving process as a {@link UniverseSolverResult}.
     */
    UniverseSolverResult solveStarter();

    /**
     * Moves to the next state of the solver.
     *
     * @return The next solver state.
     */
    ISolverState nextState();

    /**
     * Moves to the previous state of the solver.
     *
     * @return The previous solver state.
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
     * Retrieves the number of removed constraints.
     *
     * @return The number of constraints removed during solving.
     */
    int getNbRemoved();

    /**
     * Checks if the solver has reached a timeout condition.
     *
     * @return {@code true} if the solver has timed out, {@code false} otherwise.
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
     * Checks if the solver state is considered safe.
     *
     * @return {@code true} if the state is safe, {@code false} otherwise.
     */
    boolean isSafe();

    /**
     * Sets a solver listener to track solver events.
     *
     * @param listener The solver listener to attach.
     */
    void setSolverListener(ISolverListener listener);

}
