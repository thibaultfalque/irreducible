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

package fr.univartois.cril.approximation.core.measure;

import constraints.Constraint;
import fr.univartois.cril.approximation.core.constraint.GroupConstraint;
import fr.univartois.cril.approximation.util.collections.heaps.Heap;

/**
 * The IConstraintMeasureSelector
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public interface IConstraintMeasure {

    double computeScore(Constraint c);

    default double computeScore(GroupConstraint g) {
        double result = 0.0;
        for (Constraint c : g.getConstraints()) {
            result += computeScore(c);
        }
        return result;

    }
    
    <T> void  updateMeasureNEffectiveFiltering(Heap<T> heap, T c,double oldValue, double newValue);
    <T> void  updateMeasureWDEGWeight(Heap<T> heap, T c,double oldValue, double newValue);
}
