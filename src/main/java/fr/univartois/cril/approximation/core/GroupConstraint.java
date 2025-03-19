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
 * Represents a group of constraints in an approximation solver.
 * <p>
 * A {@code GroupConstraint} consists of a collection of constraints that are
 * associated with a specific group number. Each group can be assigned a score,
 * which may be used for evaluating the relaxation or approximation of constraints.
 * </p>
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public class GroupConstraint {

    /**
     * The list of constraints associated with this group.
     */
    private List<Constraint> constraints;

    /**
     * The group number identifying this constraint group.
     */
    private int nGroup;

    /**
     * The score associated with this group, used for evaluation purposes.
     */
    private double score;

    /**
     * Creates a new {@code GroupConstraint} with the specified group number.
     *
     * @param nGroup The group number to assign to this constraint group.
     */
    public GroupConstraint(int nGroup) {
        this.nGroup = nGroup;
        this.constraints = new ArrayList<>();
    }

    /**
     * Adds a constraint to this group.
     *
     * @param c The constraint to add.
     */
    public void add(Constraint c) {
        this.constraints.add(c);
    }

    /**
     * Retrieves the list of constraints in this group.
     *
     * @return The list of constraints associated with this group.
     */
    public List<Constraint> getConstraints() {
        return constraints;
    }

    /**
     * Retrieves the group number of this constraint group.
     *
     * @return The group number assigned to this constraint group.
     */
    public int getGroupNumber() {
        return nGroup;
    }

    /**
     * Retrieves the score associated with this constraint group.
     *
     * @return The score assigned to this group.
     */
    public double getScore() {
        return score;
    }

    /**
     * Sets the score for this constraint group.
     *
     * @param score The score value to assign to this group.
     */
    public void setScore(double score) {
        this.score = score;
    }

}
