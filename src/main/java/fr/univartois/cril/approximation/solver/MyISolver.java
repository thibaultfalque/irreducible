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

import org.chocosolver.solver.ISolver;
import org.chocosolver.solver.objective.IObjectiveManager;
import org.chocosolver.solver.search.loop.monitors.ISearchMonitor;
import org.chocosolver.solver.variables.Variable;
import org.chocosolver.util.criteria.Criterion;

/**
 * The Interface MyISolver.
 */
public interface MyISolver extends ISolver {

    /**
     * Solve.
     *
     * @return the universe solver result
     */
    UniverseSolverResult solve();

    /**
     * Plug monitor.
     *
     * @param monitor the monitor
     */
    void plugMonitor(ISearchMonitor monitor);

    /**
     * Gets the objective manager.
     *
     * @param <V> the value type
     * @return the objective manager
     */
    <V extends Variable> IObjectiveManager<V> getObjectiveManager();

    /**
     * Adds the stop criterion.
     *
     * @param criterion the criterion
     */
    void addStopCriterion(Criterion... criterion);

}
