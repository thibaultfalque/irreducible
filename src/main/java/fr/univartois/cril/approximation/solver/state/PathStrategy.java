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
 * The PathStrategy.
 *
 * @author Thibault Falque
 * @author Romain Wallon
 * @version 0.1.0
 */
public enum PathStrategy {

    /** The approx normal. */
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
            System.out.println(this + " we restore " + constraints.size());
        }

    },
    
    /** The approx approx. */
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
            System.out.println(this + " we restore " + constraints.size());
        }

    };

    /**
     * Previous.
     *
     * @param state the state
     * @param current the current
     * @return the i solver state
     */
    public abstract ISolverState previous(ISolverState state, ISolverState current);

    /**
     * Restore.
     *
     * @param remover the remover
     * @param constraints the constraints
     */
    public abstract void restore(IConstraintsRemover remover, Set<Constraint> constraints);

}
