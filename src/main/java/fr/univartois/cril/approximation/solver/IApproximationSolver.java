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
 * The Interface IApproximationSolver.
 */
public interface IApproximationSolver extends MyISolver {

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.solver.MyISolver#solve()
     */
    @Override
    UniverseSolverResult solve();

    /**
     * Display solution.
     *
     * @param xcsp the xcsp
     */
    void displaySolution(XCSP xcsp);

    /**
     * Restore solution.
     */
    void restoreSolution();

}
