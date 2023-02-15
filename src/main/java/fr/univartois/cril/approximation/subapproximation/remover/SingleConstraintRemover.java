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
 * The SingleConstraintRemover
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public class SingleConstraintRemover extends AbstractConstraintRemover<Constraint> {

    public SingleConstraintRemover(IConstraintGroupSolver groupSolver) {
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
        if (heapConstraint.size() == 1) {
            return List.of();
        }
        var c = heapConstraint.poll();
        ignoredConstraint.add(c);
        return List.of(c);
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
        this.heapConstraint = HeapFactory.newMaximumHeap(this.groupSolver.nConstraints(),
                () -> CollectionFactory.newMapInt(Constraint.class, k -> k.num,
                        i -> this.groupSolver.getConstraint(i), this.groupSolver.nConstraints()),
                (a, b) -> Double.compare(measure.computeScore(a), measure.computeScore(b)));
        for (Constraint c : groupSolver.getConstraints()) {
            heapConstraint.add(c);
        }
    }

    @Override
    public void whenEffectiveFilteringChange(Constraint c, int old, int newValue) {
        measure.updateMeasureNEffectiveFiltering(heapConstraint, c, old, newValue);
    }

    @Override
    public void whenWDEGWeightChange(Constraint c, double old, double newValue) {
        measure.updateMeasureWDEGWeight(heapConstraint, c, old, newValue);
    }

    @Override
    public void restoreConstraints(Collection<Constraint> constraints) {
        for (Constraint c : constraints) {
            c.ignored = false;
            heapConstraint.add(c);
        }

    }

    @Override
    public void whenBacktrackingChange(Constraint c, int old, int newValue) {
        measure.updateMeasureNEffectiveBacktracking(heapConstraint, c, old, newValue);

    }

}
