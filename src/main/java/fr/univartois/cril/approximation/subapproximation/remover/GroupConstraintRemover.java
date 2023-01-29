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

package fr.univartois.cril.approximation.subapproximation.remover;

import java.util.List;
import java.util.Set;

import constraints.Constraint;
import fr.univartois.cril.approximation.core.IConstraintGroupSolver;

/**
 * The GroupConstraintRemover
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public class GroupConstraintRemover extends AbstractConstraintRemover {
	public GroupConstraintRemover(IConstraintGroupSolver groupSolver) {
		super(groupSolver);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see fr.univartois.cril.approximation.core.remover.IConstraintsRemover#computeNextConstraintsToRemove()
	 */
	@Override
	public List<Constraint> computeNextConstraintsToRemove() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see fr.univartois.cril.approximation.core.remover.IConstraintsRemover#getIgnoredConstraints()
	 */
	@Override
	public Set<Constraint> getIgnoredConstraints() {
		// TODO Auto-generated method stub
		return null;
	}

}

