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

package fr.univartois.cril.approximation.subapproximation.measure;

import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.constraints.Propagator;

import fr.univartois.cril.approximation.core.IConstraintMeasure;
import fr.univartois.cril.approximation.util.collections.heaps.Heap;

/**
 * The WdegFilteringConstraintMeasureSelector.
 *
 * @author Thibault Falque
 * @author Romain Wallon
 * @version 0.1.0
 */
public class WdegFilteringConstraintMeasure implements IConstraintMeasure {

    /*
     * (non-Javadoc)
     *
     * @see
     * fr.univartois.cril.approximation.IConstraintMeasureSelector#computeScore(
     * constraints.Constraint)
     */
    @Override
    public double computeScore(Constraint c) {
        Propagator[] ps = c.getPropagators();
        double weight = 0;
        for (var p : ps) {
            weight += p.getWeight();
        }
        return weight / ps.length;
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.core.IConstraintMeasure#updateMeasureNEffectiveFiltering(fr.univartois.cril.approximation.util.collections.heaps.Heap, java.lang.Object, double, double)
     */
    @Override
    public <T> void updateMeasureNEffectiveFiltering(Heap<T> heap, T c, double oldValue,
            double newValue) {
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.core.IConstraintMeasure#updateMeasureWDEGWeight(fr.univartois.cril.approximation.util.collections.heaps.Heap, java.lang.Object, double, double)
     */
    @Override
    public <T> void updateMeasureWDEGWeight(Heap<T> heap, T c, double oldValue, double newValue) {
        if (oldValue < newValue) {
            heap.increase(c);
        } else {
            heap.decrease(c);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.core.IConstraintMeasure#updateMeasureNEffectiveBacktracking(fr.univartois.cril.approximation.util.collections.heaps.Heap, java.lang.Object, double, double)
     */
    @Override
    public <T> void updateMeasureNEffectiveBacktracking(Heap<T> heap, T c, double oldValue,
            double newValue) {
        // TODO Auto-generated method stub
    }

}
