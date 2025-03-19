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

import java.util.List;

import org.chocosolver.solver.ISolver;
import org.chocosolver.solver.constraints.Constraint;

/**
 * Interface defining a solver that handles constraint groups.
 * <p>
 * The {@code IConstraintGroupSolver} extends {@link ISolver} and provides
 * additional functionality to manage constraints organized into groups.
 * It allows retrieval of constraints and constraint groups, as well as
 * querying the number of groups and constraints.
 * </p>
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public interface IConstraintGroupSolver extends ISolver {

    /**
     * Gives the list of constraints in the solver.
     *
     * @return the list of constraints
     */
    List<Constraint> getConstraints();

    /**
     * Gives the constraint with the specified index.
     *
     * @param index the index of the constraint
     * @return The constraint with the specified index
     */
    Constraint getConstraint(int index);

    /**
     * Gets the groups.
     *
     * @return the groups
     */
    List<GroupConstraint> getGroups();

    /**
     * Gets the group.
     *
     * @param index the index
     * @return the group
     */
    GroupConstraint getGroup(int index);

    /**
     * N groups.
     *
     * @return the int
     */
    int nGroups();

    /**
     * N constraints.
     *
     * @return the int
     */
    int nConstraints();

}
