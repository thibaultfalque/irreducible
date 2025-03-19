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
 * If not, see {@link "http://www.gnu.org/licenses"}.
 */

package fr.univartois.cril.approximation.core;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.util.ESat;

/**
 * The KeepFalsifiedConstraintStrategy.
 *
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public enum KeepFalsifiedConstraintStrategy {

    /** The never. */
    NEVER {

        @Override
        public void checkConstraints(Model m) {
            // Do nothing here.
        }

    },

    /** The always. */
    ALWAYS {

        @Override
        public void checkConstraints(Model m) {
            for (Constraint constr : m.getCstrs()) {
                if (constr.isIgnorable() && constr.isSatisfied() != ESat.TRUE) {
                    constr.setIgnorable(false);
                }
            }
        }

    };

    /**
     * Check constraints.
     *
     * @param m the m
     */
    public abstract void checkConstraints(Model m);

}
