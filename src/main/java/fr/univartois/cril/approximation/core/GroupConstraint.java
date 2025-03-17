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

import java.util.ArrayList;
import java.util.List;

import org.chocosolver.solver.constraints.Constraint;

/**
 * The GroupConstraint.
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public class GroupConstraint {

    /** The constraints. */
    private List<Constraint> constraints;

    /** The n group. */
    private int nGroup;

    /** The score. */
    private double score;

    /**
     * Creates a new GroupConstraint.
     *
     * @param nGroup the n group
     */
    public GroupConstraint(int nGroup) {
        this.nGroup = nGroup;
        this.constraints = new ArrayList<>();
    }

    /**
     * Adds the.
     *
     * @param c the c
     */
    public void add(Constraint c) {
        this.constraints.add(c);
    }

    /**
     * Gives the constraints of this GroupConstraint.
     *
     * @return This GroupConstraint's constraints.
     */
    public List<Constraint> getConstraints() {
        return constraints;
    }

    /**
     * Gives the group number of this GroupConstraint.
     *
     * @return This GroupConstraint's nGroup.
     */
    public int getGroupNumber() {
        return nGroup;
    }

    /**
     * Gives the score of this GroupConstraint.
     *
     * @return This GroupConstraint's score.
     */
    public double getScore() {
        return score;
    }

    /**
     * Sets this GroupConstraint's score.
     *
     * @param score The score to set.
     */
    public void setScore(double score) {
        this.score = score;
    }

}
