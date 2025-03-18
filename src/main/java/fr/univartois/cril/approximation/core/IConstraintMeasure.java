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

import org.chocosolver.solver.constraints.Constraint;

import fr.univartois.cril.approximation.util.collections.heaps.Heap;

/**
 * The IConstraintMeasureSelector.
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public interface IConstraintMeasure {

    /**
     * Compute score.
     *
     * @param c the c
     *
     * @return the double
     */
    double computeScore(Constraint c);

    /**
     * Compute score.
     *
     * @param g the g
     * @param count the count
     *
     * @return the double
     */
    default double computeScore(GroupConstraint g, int count) {
        double result = 0.0;
        for (Constraint c : g.getConstraints()) {
            result += computeScore(c);
        }
        result = count > 0 ? result / (count * g.getConstraints().size()) : result;
        g.setScore(result);
        return result;
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
    <T> void updateMeasureNEffectiveFiltering(Heap<T> heap, T c, double oldValue, double newValue);

    /**
     * Update measure WDEG weight.
     *
     * @param <T> the generic type
     * @param heap the heap
     * @param c the c
     * @param oldValue the old value
     * @param newValue the new value
     */
    <T> void updateMeasureWDEGWeight(Heap<T> heap, T c, double oldValue, double newValue);

    /**
     * Update measure N effective backtracking.
     *
     * @param <T> the generic type
     * @param heap the heap
     * @param c the c
     * @param oldValue the old value
     * @param newValue the new value
     */
    <T> void updateMeasureNEffectiveBacktracking(Heap<T> heap, T c, double oldValue,
            double newValue);

}
