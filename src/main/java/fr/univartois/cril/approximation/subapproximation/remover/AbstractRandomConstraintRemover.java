/**
 * approximation
 * Copyright (c) 2025 - Romain Wallon.
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

import java.util.List;
import java.util.Random;

import org.chocosolver.solver.constraints.Constraint;

import fr.univartois.cril.approximation.core.IConstraintGroupSolver;
import fr.univartois.cril.approximation.core.IConstraintMeasure;
import fr.univartois.cril.approximation.core.IConstraintsRemover;

/**
 * The RandomConstraintRemover.
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 *
 * @param <T> the generic type
 */
public abstract class AbstractRandomConstraintRemover<T> implements IConstraintsRemover {

    /** The constraints. */
    protected List<T> constraints;

    /** The Constant RANDOM. */
    protected static final Random RANDOM = new Random(123456789);

    /**
     * Instantiates a new abstract random constraint remover.
     *
     * @param groupSolver the group solver
     */
    protected AbstractRandomConstraintRemover(IConstraintGroupSolver groupSolver) {
        this.constraints = createConstraints(groupSolver);
    }

    /**
     * Creates the constraints.
     *
     * @param groupSolver the group solver
     *
     * @return the list
     */
    protected abstract List<T> createConstraints(IConstraintGroupSolver groupSolver);

    /*
     * (non-Javadoc)
     *
     * @see
     * org.chocosolver.solver.search.loop.monitors.IMonitorApprox#whenBacktrackingChange(
     * org.chocosolver.solver.constraints.Constraint, int, int)
     */
    @Override
    public void whenBacktrackingChange(Constraint arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub.
    }

    /*
     * (non-Javadoc)
     *
     * @see org.chocosolver.solver.search.loop.monitors.IMonitorApprox#
     * whenEffectiveFilteringChange(org.chocosolver.solver.constraints.Constraint, int,
     * int)
     */
    @Override
    public void whenEffectiveFilteringChange(Constraint arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub.
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.chocosolver.solver.search.loop.monitors.IMonitorApprox#whenWDEGWeightChange(org
     * .chocosolver.solver.constraints.Constraint, double, double)
     */
    @Override
    public void whenWDEGWeightChange(Constraint arg0, double arg1, double arg2) {
        // TODO Auto-generated method stub.
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * fr.univartois.cril.approximation.core.IConstraintsRemover#setConstraintMeasure(fr.
     * univartois.cril.approximation.core.IConstraintMeasure)
     */
    @Override
    public void setConstraintMeasure(IConstraintMeasure measure) {
        // TODO Auto-generated method stub.
    }

}
