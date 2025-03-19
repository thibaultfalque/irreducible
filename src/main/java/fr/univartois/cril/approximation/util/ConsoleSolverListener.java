/**
 * approximation
 * Copyright (c) 2025 - Romain Wallon.
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

import java.io.PrintStream;

import fr.univartois.cril.approximation.solver.UniverseSolverResult;
import fr.univartois.cril.approximation.solver.state.ISolverState;

/**
 * The ConsoleSolverListener.
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public class ConsoleSolverListener implements ISolverListener {

    /** The out. */
    private PrintStream out;

    /**
     * Instantiates a new console solver listener.
     *
     * @param out the out
     */
    public ConsoleSolverListener(PrintStream out) {
        this.out = out;
    }

    /**
     * Instantiates a new console solver listener.
     */
    public ConsoleSolverListener() {
        this(System.out);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * fr.univartois.cril.approximation.util.ISolverListener#onStartState(fr.univartois.
     * cril.approximation.solver.state.ISolverState)
     */
    @Override
    public void onStartState(ISolverState state) {
        out.println("Start new state: " + state);
    }

    /**
     * On solve with starter.
     *
     * @param state the state
     */
    /*
     * (non-Javadoc)
     *
     * @see
     * fr.univartois.cril.approximation.util.ISolverListener#onSolveWithStarter(java.lang.
     * Object)
     */
    @Override
    public void onSolveWithStarter(ISolverState state) {
        out.println("Solve with starter " + state);
    }

    /**
     * On solve.
     *
     * @param state the state
     */
    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.util.ISolverListener#onSolve()
     */
    @Override
    public void onSolve(ISolverState state) {
        out.println("Solve " + state);
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.util.ISolverListener#onStateSolved(java.lang.
     * String)
     */
    @Override
    public void onStateSolved(ISolverState state) {
        out.println("State solved: " + state);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * fr.univartois.cril.approximation.util.ISolverListener#onEndState(fr.univartois.cril
     * .approximation.solver.state.ISolverState)
     */
    @Override
    public void onEndState(ISolverState state) {
        out.println("End state: " + state);
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.util.ISolverListener#onResetSolver()
     */
    @Override
    public void onResetSolver() {
        out.println("Reset solver");
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * fr.univartois.cril.approximation.util.ISolverListener#onSolution(java.lang.String)
     */
    @Override
    public void onSolution(String solution) {
        out.println("Solution found: " + solution);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * fr.univartois.cril.approximation.util.ISolverListener#onResult(fr.univartois.cril.
     * approximation.solver.UniverseSolverResult)
     */
    @Override
    public void onResult(ISolverState state, UniverseSolverResult result) {
        out.println("Result for state" + state + ": " + result);
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.util.ISolverListener#onNewBoundFound(int,
     * int)
     */
    @Override
    public void onNewBoundFound(int bestBound, int bestIndex) {
        // TODO Auto-generated method stub.
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * fr.univartois.cril.approximation.util.ISolverListener#onRestoreConstraints(org.
     * chocosolver.solver.ISolver, int)
     */
    @Override
    public void onRestoreConstraints(ISolverState state, int constraintCount) {
        out.println(state + " we restore " + constraintCount);
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.util.ISolverListener#onRemoveConstraints(org.
     * chocosolver.solver.ISolver, int)
     */
    @Override
    public void onRemoveConstraints(ISolverState state, int constraintCount) {
        out.println(state + " we remoe " + constraintCount);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * fr.univartois.cril.approximation.util.ISolverListener#onDebugMessage(java.lang.
     * String)
     */
    @Override
    public void onDebugMessage(String message) {
        // TODO Auto-generated method stub.
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.util.ISolverListener#onCompleteRestore()
     */
    @Override
    public void onCompleteRestore() {
        out.println("We reset the solver and restore all constraints.");
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.util.ISolverListener#onFinishResolution(fr.
     * univartois.cril.approximation.solver.state.ISolverState,
     * fr.univartois.cril.approximation.solver.UniverseSolverResult)
     */
    @Override
    public void onFinishResolution(ISolverState lastState, UniverseSolverResult result) {
        out.println(
                "Finish resolution with last state: " + lastState + " and result: " + result);
    }

}
