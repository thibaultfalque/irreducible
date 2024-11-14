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

import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.strategy.strategy.WarmStart;

import fr.univartois.cril.approximation.core.KeepNoGoodStrategy;
import fr.univartois.cril.approximation.solver.UniverseSolverResult;

/**
 * The ISolverState
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public interface ISolverState {
    UniverseSolverResult solve();
    UniverseSolverResult solveStarter();
    ISolverState nextState();
    ISolverState previousState();
    
    void resetNoGoods(KeepNoGoodStrategy ngStrategy, Solver ace);
    void displaySolution();
    int getNbRemoved();
    boolean isTimeout();
    boolean isRestored();
}

