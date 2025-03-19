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

package fr.univartois.cril.approximation.util;

import fr.univartois.cril.approximation.solver.UniverseSolverResult;
import fr.univartois.cril.approximation.solver.state.ISolverState;

/**
 * The {@code ISolverListener} interface defines a set of callback methods
 * that can be implemented to track the state and progress of a solver.
 * <p>
 * It provides hooks for various solver events, such as starting and ending
 * a state, solving with a starter, obtaining results, and handling constraint
 * changes.
 * </p>
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public interface ISolverListener {

    /**
     * Called when the solver starts processing a state.
     *
     * @param state The current state of the solver.
     */
    void onStartState(ISolverState state);

    /**
     * Called when the solver starts solving using a specific starter.
     *
     * @param state the state
     */
    void onSolveWithStarter(ISolverState state);

    /**
     * Called when the solver starts a solving process.
     *
     * @param state the state
     */
    void onSolve(ISolverState state);

    /**
     * Called when a state has been solved and a result is available.
     *
     * @param state the state
     */
    void onStateSolved(ISolverState state);

    /**
     * Called when the solver ends processing a state.
     *
     * @param state The final state after processing.
     */
    void onEndState(ISolverState state);

    /**
     * Called when the solver is reset.
     */
    void onResetSolver();

    /**
     * Called when a solution is found.
     *
     * @param solution The string representation of the solution.
     */
    void onSolution(String solution);

    /**
     * Called when a result is obtained.
     *
     * @param state the state
     * @param result The result obtained from the solver.
     */
    void onResult(ISolverState state, UniverseSolverResult result);

    /**
     * Called when a new bound is found.
     *
     * @param bestBound The best bound found so far.
     * @param bestIndex The index of the solver that found the solution.
     */
    void onNewBoundFound(int bestBound, int bestIndex);

    /**
     * Called when constraints are restored to the solver.
     *
     * @param state the state
     * @param constraintCount The number of constraints restored.
     */
    void onRestoreConstraints(ISolverState state, int constraintCount);

    /**
     * Called when constraints are removed from the solver.
     *
     * @param state the state
     * @param constraintCount The number of constraints removed.
     */
    void onRemoveConstraints(ISolverState state, int constraintCount);

    /**
     * Called when a debug message is generated.
     *
     * @param message The debug message.
     */
    void onDebugMessage(String message);

    /**
     * On complete restore.
     */
    void onCompleteRestore();

    /**
     * On finish resolution.
     *
     * @param lastState the last state
     * @param result the result
     */
    void onFinishResolution(ISolverState lastState, UniverseSolverResult result);

}
