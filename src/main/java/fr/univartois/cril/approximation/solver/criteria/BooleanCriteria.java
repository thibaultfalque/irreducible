/**
 * approximation, a constraint programming solver based on Choco, utilizing relaxation
 * techniques.
 * Copyright (c) 2025 - Univ Artois, CNRS & Luxembourg University.
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

package fr.univartois.cril.approximation.solver.criteria;

import java.util.concurrent.atomic.AtomicBoolean;

import org.chocosolver.util.criteria.Criterion;

/**
 * A criterion that allows stopping a solving process based on a boolean flag.
 * <p>
 * The {@code BooleanCriteria} class implements {@link Criterion} and provides a simple
 * mechanism to signal when a solver should stop by setting an atomic boolean value.
 * </p>
 *
 * <p>
 * This criterion is useful for managing termination conditions across multiple solvers
 * running in parallel.
 * </p>
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public class BooleanCriteria implements Criterion {

    /**
     * The atomic boolean flag indicating whether the stop condition is met.
     */
    private AtomicBoolean stop = new AtomicBoolean(false);

    /**
     * Sets the stop condition.
     *
     * @param value {@code true} to indicate stopping, {@code false} otherwise.
     */
    public void setStop(boolean value) {
        stop.set(value);
    }

    /**
     * Checks whether the stop condition is met.
     *
     * @return {@code true} if stopping is required, otherwise {@code false}.
     */
    @Override
    public boolean isMet() {
        return stop.get();
    }

}
