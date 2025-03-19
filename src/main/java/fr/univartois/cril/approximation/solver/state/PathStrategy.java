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

import java.util.Set;

import org.chocosolver.solver.constraints.Constraint;

import fr.univartois.cril.approximation.core.IConstraintsRemover;

/**
 * Defines strategies for navigating and restoring solver states in an approximation
 * solver.
 * <p>
 * The {@code PathStrategy} enum provides two strategies when a solution is found:
 * <ul>
 * <li>{@link #APPROX_NORMAL} - Moves to the original solver state.</li>
 * <li>{@link #APPROX_APPROX} - Moves to the previous state.</li>
 * </ul>
 * Both strategies support restoring removed constraints using an
 * {@link IConstraintsRemover}.
 * </p>
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public enum PathStrategy {

    /** Strategy that moves to the original state when a solution is found. */
    APPROX_NORMAL {

        @Override
        public ISolverState previous(ISolverState state, ISolverState current) {
            return state.previousState();
        }

        @Override
        public void restore(IConstraintsRemover remover, Set<Constraint> constraints) {
            if (!constraints.isEmpty()) {
                remover.restoreConstraints(constraints);
            }
        }

    },

    /** Strategy that moves to the previous state when a solution is found. */
    APPROX_APPROX {

        @Override
        public ISolverState previous(ISolverState state, ISolverState current) {
            return state;
        }

        @Override
        public void restore(IConstraintsRemover remover, Set<Constraint> constraints) {
            if (!constraints.isEmpty()) {
                remover.restoreConstraints(constraints);
            }
        }

    };

    /**
     * Determines the previous solver state based on the strategy.
     *
     * @param state The reference solver state.
     * @param current The current solver state.
     *
     * @return The solver state to transition to.
     */
    public abstract ISolverState previous(ISolverState state, ISolverState current);

    /**
     * Restores constraints using the given constraint remover.
     *
     * @param remover The constraint remover responsible for restoring constraints.
     * @param constraints The set of constraints to restore.
     */
    public abstract void restore(IConstraintsRemover remover, Set<Constraint> constraints);

}
