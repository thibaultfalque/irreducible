/**
 * approximation
 * Copyright (c) 2025 - Romain Wallon.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.chocosolver.solver.constraints.Constraint;

import fr.univartois.cril.approximation.core.GroupConstraint;
import fr.univartois.cril.approximation.core.IConstraintGroupSolver;

/**
 * The RandomGroupConstraintRemover.
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public class RandomGroupConstraintRemover extends AbstractRandomConstraintRemover<GroupConstraint> {

    /** The group solver. */
    private IConstraintGroupSolver groupSolver;

    /**
     * Creates a new RandomGroupConstraintRemover.
     *
     * @param groupSolver the group solver
     */
    protected RandomGroupConstraintRemover(IConstraintGroupSolver groupSolver) {
        super(groupSolver);
        this.groupSolver = groupSolver;
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.core.IConstraintsRemover#
     * computeNextConstraintsToRemove()
     */
    @Override
    public List<Constraint> computeNextConstraintsToRemove() {
        int index = RANDOM.nextInt(constraints.size());
        Collections.swap(constraints, index, constraints.size() - 1);
        return constraints.remove(constraints.size() - 1).getConstraints();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * fr.univartois.cril.approximation.core.IConstraintsRemover#restoreConstraints(java.
     * util.Collection)
     */
    @Override
    public void restoreConstraints(Collection<Constraint> constraints) {
        int group = -1;
        for (Constraint c : constraints) {
            c.setEnabled(true);
            group = c.getGroupId();
        }

        this.constraints.add(groupSolver.getGroup(group));
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.subapproximation.remover.
     * AbstractRandomConstraintRemover#createConstraints(fr.univartois.cril.approximation.
     * core.IConstraintGroupSolver)
     */
    @Override
    protected List<GroupConstraint> createConstraints(IConstraintGroupSolver groupSolver) {
        return new ArrayList<>(groupSolver.getGroups());
    }

}
