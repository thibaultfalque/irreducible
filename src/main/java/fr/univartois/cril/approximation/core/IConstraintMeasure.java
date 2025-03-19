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
 * Interface defining a measure for evaluating constraints in an approximation solver.
 * <p>
 * The {@code IConstraintMeasure} interface provides methods to compute scores for
 * individual constraints and constraint groups, as well as update measurement values
 * in various heuristic techniques, such as weighted degree (WDEG) and effective
 * filtering.
 * </p>
 * <p>
 * Implementations of this interface should define how constraint scores are computed
 * and updated dynamically during solving.
 * </p>
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public interface IConstraintMeasure {

    /**
     * Computes the score of a given constraint.
     *
     * @param c The constraint to evaluate.
     *
     * @return The computed score for the constraint.
     */
    double computeScore(Constraint c);

    /**
     * Computes the score for a group of constraints.
     *
     * @param g The constraint group to evaluate.
     * @param count the number of times this group has already been removed..
     *
     * @return The computed score for the constraint group.
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
     * Updates the measure for effective filtering in constraint selection.
     *
     * @param <T> The type of elements in the heap.
     * @param heap The heap structure used for constraint ordering.
     * @param c The constraint to update.
     * @param oldValue The previous score value.
     * @param newValue The updated score value.
     */
    <T> void updateMeasureNEffectiveFiltering(Heap<T> heap, T c, double oldValue, double newValue);

    /**
     * Updates the measure for weighted degree (WDEG) heuristic.
     *
     * @param <T> The type of elements in the heap.
     * @param heap The heap structure used for constraint ordering.
     * @param c The constraint to update.
     * @param oldValue The previous weight value.
     * @param newValue The updated weight value.
     */
    <T> void updateMeasureWDEGWeight(Heap<T> heap, T c, double oldValue, double newValue);

    /**
     * Updates the measure for backtracking efficiency in constraint selection.
     *
     * @param <T> The type of elements in the heap.
     * @param heap The heap structure used for constraint ordering.
     * @param c The constraint to update.
     * @param oldValue The previous score value.
     * @param newValue The updated score value.
     */
    <T> void updateMeasureNEffectiveBacktracking(Heap<T> heap, T c, double oldValue,
            double newValue);

}
