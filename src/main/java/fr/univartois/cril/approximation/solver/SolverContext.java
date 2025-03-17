/**
 * approximation
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

package fr.univartois.cril.approximation.solver;

/**
 * The {@code SolverContext} class stores shared configuration settings for different
 * solver states. It provides access to both the normal and relaxation solver
 * configurations, allowing state transitions to retrieve the appropriate settings
 * without creating direct dependencies between states.
 *
 * <p>
 * This class is primarily used to facilitate smooth transitions between
 * {@code NormalStateSolver} and {@code SubApproximationStateSolver} by providing
 * the necessary configurations dynamically.
 * </p>
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public class SolverContext {

    /** The normal configuration. */
    private SolverConfiguration normalConfiguration;

    /** The sub approximation configuration. */
    private SolverConfiguration subApproximationConfiguration;

    /**
     * Creates a new SolverContext.
     *
     * @param normalConfiguration the normal configuration
     * @param subApproximationConfiguration the sub approximation configuration
     */
    public SolverContext(SolverConfiguration normalConfiguration,
            SolverConfiguration subApproximationConfiguration) {
        this.normalConfiguration = normalConfiguration;
        this.subApproximationConfiguration = subApproximationConfiguration;
    }

    /**
     * Gives the normalConfiguration of this SolverContext.
     *
     * @return This SolverContext's normalConfiguration.
     */
    public SolverConfiguration getNormalConfiguration() {
        return normalConfiguration;
    }

    /**
     * Sets this SolverContext's normalConfiguration.
     *
     * @param normalConfiguration The normalConfiguration to set.
     */
    public void setNormalConfiguration(SolverConfiguration normalConfiguration) {
        this.normalConfiguration = normalConfiguration;
    }

    public void updateNormalConfiguration() {
        this.normalConfiguration = normalConfiguration.update();
    }

    /**
     * Gives the subApproximationConfiguration of this SolverContext.
     *
     * @return This SolverContext's subApproximationConfiguration.
     */
    public SolverConfiguration getSubApproximationConfiguration() {
        return subApproximationConfiguration;
    }

    /**
     * Sets this SolverContext's subApproximationConfiguration.
     *
     * @param subApproximationConfiguration The subApproximationConfiguration to set.
     */
    public void setSubApproximationConfiguration(
            SolverConfiguration subApproximationConfiguration) {
        this.subApproximationConfiguration = subApproximationConfiguration;
    }

    public void updateSubApproximationConfiguration() {
        this.subApproximationConfiguration = subApproximationConfiguration.update();
    }
}
