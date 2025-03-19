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

package fr.univartois.cril.approximation.core;

import java.util.Collection;
import java.util.List;

import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.search.loop.monitors.IMonitorApprox;

/**
 * Interface for removing constraints in an approximation solver.
 * <p>
 * The {@code IConstraintsRemover} interface extends {@link IMonitorApprox} and provides
 * functionality to dynamically remove and restore constraints during the solving process.
 * Implementations of this interface determine which constraints should be removed based
 * on
 * a given constraint measure and other heuristic criteria.
 * </p>
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public interface IConstraintsRemover extends IMonitorApprox {

    /**
     * Sets the constraint measure used to evaluate constraints.
     *
     * @param measure The constraint measure to set.
     */
    void setConstraintMeasure(IConstraintMeasure measure);

    /**
     * Computes and returns the next set of constraints to be removed.
     *
     * @return A list of constraints selected for removal.
     */
    List<Constraint> computeNextConstraintsToRemove();

    /**
     * Restores previously removed constraints back into the solver.
     *
     * @param constraints The collection of constraints to restore.
     */
    void restoreConstraints(Collection<Constraint> constraints);

}
