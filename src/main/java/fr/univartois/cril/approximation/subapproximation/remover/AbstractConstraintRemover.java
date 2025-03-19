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

import fr.univartois.cril.approximation.core.IConstraintGroupSolver;
import fr.univartois.cril.approximation.core.IConstraintMeasure;
import fr.univartois.cril.approximation.core.IConstraintsRemover;
import fr.univartois.cril.approximation.util.collections.heaps.Heap;

/**
 * The AbstractConstraintRemover.
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 *
 * @param <T> the generic type
 */
public abstract class AbstractConstraintRemover<T> implements IConstraintsRemover {

    /** The measure. */
    protected IConstraintMeasure measure;

    /** The group solver. */
    protected IConstraintGroupSolver groupSolver;

    /** The heap constraint. */
    protected Heap<T> heapConstraint;

    /**
     * Instantiates a new abstract constraint remover.
     *
     * @param groupSolver the group solver
     */
    protected AbstractConstraintRemover(IConstraintGroupSolver groupSolver) {
        this.groupSolver = groupSolver;
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.core.remover.IConstraintsRemover#
     * setConstraintMeasure(fr.univartois.cril.approximation.core.measure.
     * IConstraintMeasure)
     */
    @Override
    public void setConstraintMeasure(IConstraintMeasure measure) {
        this.measure = measure;
    }

}
