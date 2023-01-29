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

import constraints.Constraint;
import fr.univartois.cril.aceurancetourix.JUniverseAceProblemAdapter;
import fr.univartois.cril.approximation.core.IConstraintGroupSolver;
import fr.univartois.cril.approximation.core.measure.IConstraintMeasure;
import fr.univartois.cril.approximation.util.collections.heaps.Heap;


/**
 * The MeanFilteringConstraintMeasure
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public class MeanFilteringConstraintMeasure extends AbstractMeasure {

    private IConstraintMeasure decoree;
    private int nb;
    /**
     * Creates a new MeanFilteringConstraintMeasure.
     */
    public MeanFilteringConstraintMeasure(IConstraintGroupSolver adapter,IConstraintMeasure decoree) {
        super(adapter);
        this.decoree=decoree;
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.core.measure.IConstraintMeasure#computeScore(constraints.Constraint)
     */
    @Override
    public double computeScore(Constraint c) {
        return decoree.computeScore(c)/groupSolver.nGroups();
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.core.measure.IConstraintMeasure#updateMeasureNEffectiveFiltering(fr.univartois.cril.approximation.util.collections.heaps.Heap, java.lang.Object, double, double)
     */
    @Override
    public <T> void updateMeasureNEffectiveFiltering(Heap<T> heap, T c, double oldValue,
            double newValue) {
        decoree.updateMeasureNEffectiveFiltering(heap, c, oldValue, newValue);

    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.core.measure.IConstraintMeasure#updateMeasureWDEGWeight(fr.univartois.cril.approximation.util.collections.heaps.Heap, java.lang.Object, double, double)
     */
    @Override
    public <T> void updateMeasureWDEGWeight(Heap<T> heap, T c, double oldValue, double newValue) {
        decoree.updateMeasureWDEGWeight(heap, c, oldValue, newValue);

    }

}

