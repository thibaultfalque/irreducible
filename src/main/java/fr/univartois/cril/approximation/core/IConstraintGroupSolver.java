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

import constraints.Constraint;
import fr.univartois.cril.approximation.core.constraint.GroupConstraint;
import fr.univartois.cril.juniverse.core.IUniverseSolver;

/**
 * The IConstraintGroupSolver
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public interface IConstraintGroupSolver extends IUniverseSolver{
	List<Constraint> getConstraints();
	Constraint getConstraint(int index);
	GroupConstraint getGroup(int index);
	List<GroupConstraint> getGroups();
	int nGroups();
}

