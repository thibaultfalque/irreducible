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
 * The IConstraintsSelector.
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public interface IConstraintsRemover extends IMonitorApprox {

    /**
     * Sets the constraint measure.
     *
     * @param measure the new constraint measure
     */
    void setConstraintMeasure(IConstraintMeasure measure);

    /**
     * Compute next constraints to remove.
     *
     * @return the list
     */
    List<Constraint> computeNextConstraintsToRemove();

    /**
     * Restore constraints.
     *
     * @param constraints the constraints
     */
    void restoreConstraints(Collection<Constraint> constraints);

}
