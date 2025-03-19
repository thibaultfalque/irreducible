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

package fr.univartois.cril.approximation.solver;

import org.chocosolver.parser.xcsp.XCSP;

/**
 * Interface defining an approximation solver.
 * <p>
 * The {@code IApproximationSolver} extends {@link MyISolver} and provides additional
 * functionality to handle approximate solving methods. It supports solving, displaying,
 * and restoring solutions.
 * </p>
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public interface IApproximationSolver extends MyISolver {

    /**
     * Solves the problem using an approximation strategy.
     *
     * @return The result of the solving process as a {@link UniverseSolverResult}.
     */
    @Override
    UniverseSolverResult solve();

    /**
     * Displays the solution using the provided XCSP parser.
     *
     * @param xcsp The XCSP parser instance used for displaying the solution.
     */
    void displaySolution(XCSP xcsp);

}
