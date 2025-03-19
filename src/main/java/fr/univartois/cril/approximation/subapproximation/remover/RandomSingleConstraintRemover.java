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

package fr.univartois.cril.approximation.subapproximation.remover;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.chocosolver.solver.constraints.Constraint;

import fr.univartois.cril.approximation.core.IConstraintGroupSolver;

/**
 * The RandomSingleConstraintRemover.
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public class RandomSingleConstraintRemover extends AbstractRandomConstraintRemover<Constraint> {

    /**
     * Creates a new RandomSingleConstraintRemover.
     *
     * @param groupSolver the group solver
     */
    protected RandomSingleConstraintRemover(IConstraintGroupSolver groupSolver) {
        super(groupSolver);
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.core.IConstraintsRemover#
     * computeNextConstraintsToRemove()
     */
    @Override
    public List<Constraint> computeNextConstraintsToRemove() {
        int index = RANDOM.nextInt(constraints.size());
        Collections.swap(constraints, index, constraints.size() - 1);
        return List.of(constraints.remove(constraints.size() - 1));
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * fr.univartois.cril.approximation.core.IConstraintsRemover#restoreConstraints(java.
     * util.Collection)
     */
    @Override
    public void restoreConstraints(Collection<Constraint> constraints) {
        for (Constraint c : constraints) {
            c.setEnabled(true);
            this.constraints.add(c);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.subapproximation.remover.
     * AbstractRandomConstraintRemover#createConstraints(fr.univartois.cril.approximation.
     * core.IConstraintGroupSolver)
     */
    @Override
    protected List<Constraint> createConstraints(IConstraintGroupSolver groupSolver) {
        return new ArrayList<>(groupSolver.getConstraints());
    }

}
