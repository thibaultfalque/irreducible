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

import java.util.Collection;
import java.util.List;

import constraints.Constraint;
import fr.univartois.cril.approximation.core.GroupConstraint;
import fr.univartois.cril.approximation.core.IConstraintGroupSolver;
import fr.univartois.cril.approximation.core.IConstraintMeasure;
import fr.univartois.cril.approximation.util.CollectionFactory;
import fr.univartois.cril.approximation.util.collections.heaps.HeapFactory;

/**
 * The GroupConstraintRemover
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public class GroupConstraintRemover extends AbstractConstraintRemover<GroupConstraint> {

    public GroupConstraintRemover(IConstraintGroupSolver groupSolver) {
        super(groupSolver);
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.core.remover.IConstraintsRemover#
     * computeNextConstraintsToRemove()
     */
    @Override
    public List<Constraint> computeNextConstraintsToRemove() {
        var g = heapConstraint.poll();
        ignoredConstraint.addAll(g.getConstraints());
        return g.getConstraints();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * fr.univartois.cril.approximation.subapproximation.remover.AbstractConstraintRemover
     * #setConstraintMeasure(fr.univartois.cril.approximation.core.measure.
     * IConstraintMeasure)
     */
    @Override
    public void setConstraintMeasure(IConstraintMeasure measure) {
        super.setConstraintMeasure(measure);
        this.heapConstraint = HeapFactory.newMaximumHeap(this.groupSolver.nGroups(),
                () -> CollectionFactory.newMapInt(GroupConstraint.class, k -> k.getGroupNumber(),
                        i -> this.groupSolver.getGroup(i), this.groupSolver.nGroups()),
                (a, b) -> Double.compare(measure.computeScore(a), measure.computeScore(b)));
        for (GroupConstraint c : groupSolver.getGroups()) {
            heapConstraint.add(c);
        }
    }

    @Override
    public void whenEffectiveFilteringChange(Constraint c, int oldValue, int newValue) {
        GroupConstraint g = this.groupSolver.getGroup(c.group);
        measure.updateMeasureNEffectiveFiltering(heapConstraint, g, oldValue, newValue);
    }

    @Override
    public void whenWDEGWeightChange(Constraint c, double oldValue, double newValue) {
        GroupConstraint g = this.groupSolver.getGroup(c.group);
        measure.updateMeasureWDEGWeight(heapConstraint, g, oldValue, newValue);

    }

    @Override
    public void restoreConstraints(Collection<Constraint> constraints) {
        int group = -1;
        for (Constraint c : constraints) {
            c.ignored = false;
            group = c.group;
        }

        heapConstraint.add(groupSolver.getGroup(group));

    }
}
