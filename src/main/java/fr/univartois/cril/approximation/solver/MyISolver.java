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
 * Interface defining a solver with additional functionalities.
 * <p>
 * The {@code MyISolver} interface extends {@link ISolver} and provides
 * methods for solving, managing search monitors, handling objectives,
 * and defining stop criteria.
 * </p>
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public interface MyISolver extends ISolver {

    /**
     * Solves the problem using the configured solver.
     *
     * @return The result of the solving process as a {@link UniverseSolverResult}.
     */
    UniverseSolverResult solve();

    /**
     * Attaches a search monitor to the solver.
     *
     * @param monitor The search monitor to be plugged in.
     */
    void plugMonitor(ISearchMonitor monitor);

    /**
     * Retrieves the objective manager handling optimization objectives.
     *
     * @param <V> The type of the objective variable.
     *
     * @return The objective manager associated with this solver.
     */
    <V extends Variable> IObjectiveManager<V> getObjectiveManager();

    /**
     * Adds one or more stop criteria to the solver.
     *
     * @param criterion The stop criteria to be added.
     */
    void addStopCriterion(Criterion... criterion);

}
