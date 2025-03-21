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

import fr.univartois.cril.approximation.core.IConstraintMeasure;
import fr.univartois.cril.approximation.util.collections.heaps.Heap;

/**
 * The NEffectiveFilteringConstraintMeasureSelector.
 *
 * @author Thibault Falque
 * @author Romain Wallon
 * @version 0.1.0
 */
public class NEffectiveFilteringConstraintMeasure implements IConstraintMeasure {

    /**
     * Compute score.
     *
     * @param c the c
     * @return the double
     */
    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.IConstraintMeasureSelector#computeScore(
     * constraints.Constraint)
     */
    @Override
    public double computeScore(Constraint c) {
        return c.getnEffectiveFiltering();
    }

    /**
     * Update measure N effective filtering.
     *
     * @param <T> the generic type
     * @param heap the heap
     * @param c the c
     * @param oldValue the old value
     * @param newValue the new value
     */
    @Override
    public <T> void updateMeasureNEffectiveFiltering(Heap<T> heap, T c, double oldValue,
            double newValue) {
        if (oldValue < newValue) {
            heap.increase(c);
        } else {
            heap.decrease(c);
        }
    }

    /**
     * Update measure WDEG weight.
     *
     * @param <T> the generic type
     * @param heap the heap
     * @param c the c
     * @param oldValue the old value
     * @param newValue the new value
     */
    @Override
    public <T> void updateMeasureWDEGWeight(Heap<T> heap, T c, double oldValue, double newValue) {
    }

    /**
     * Update measure N effective backtracking.
     *
     * @param <T> the generic type
     * @param heap the heap
     * @param c the c
     * @param oldValue the old value
     * @param newValue the new value
     */
    @Override
    public <T> void updateMeasureNEffectiveBacktracking(Heap<T> heap, T c, double oldValue,
            double newValue) {
        // TODO Auto-generated method stub
    }

}
