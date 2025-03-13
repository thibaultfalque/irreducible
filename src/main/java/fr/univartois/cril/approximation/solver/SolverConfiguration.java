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

package fr.univartois.cril.approximation.solver;

import fr.univartois.cril.approximation.core.IConstraintsRemover;
import fr.univartois.cril.approximation.solver.state.PathStrategy;

/**
 * /**
 * The {@code SolverConfiguration} class defines the configuration settings
 * for an approximation-based solver. It encapsulates various parameters
 * controlling the solver’s execution, such as the number of failed,
 * solution limits, relaxation strategies, and path selection strategies.
 *
 * <p>
 * This class provides getter and setter methods to update
 * configuration parameters dynamically, along with an {@code update()} method
 * that generates a new configuration based on scaling factors.
 * </p>
 *
 * <h2>Configuration Parameters:</h2>
 * <ul>
 * <li><b>nbFailed:</b> The number of failed allowing during the resolution.</li>
 * <li><b>factor:</b> A scaling factor applied to the number of failed.</li>
 * <li><b>limitSolution:</b> The maximum number of solutions allowed.</li>
 * <li><b>ratio:</b> A threshold ratio affecting solver transitions.</li>
 * <li><b>remover:</b> A strategy defining how constraints are removed
 * during approximation.</li>
 * <li><b>pathStrategy:</b> The path strategy that defines how constraints are
 * reintroduced during state transitions.</li>
 *
 * in state transitions.</li>
 * </ul>
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public class SolverConfiguration {

    /**
     * The number of failed decisions before switching between states.
     * This value is updated dynamically during the solving process.
     * Once this limit is reached, the solver stops the search.
     */
    private int nbFailed;

    /**
     * The initial number of allowed failed before adjustments are made.
     * This value is set at the beginning and remains unchanged to track baseline
     * behavior.
     */
    private int initFailed;

    /**
     * A scaling factor applied to the number of failed when updating solver
     * parameters.
     */
    private double factor;

    /**
     * The maximum number of solutions the solver is allowed to find.
     * Once this limit is reached, the solver stops the search.
     */
    private long limitSolution;

    /**
     * The threshold ratio that influences state transitions within the solver.
     * This ratio helps determine when to switch between different solving strategies.
     */
    private double ratio;

    /**
     * The strategy used to remove constraints during the approximation process.
     * This component defines which constraints are relaxed to facilitate solving.
     */
    private IConstraintsRemover remover;

    /**
     * The strategy that defines how constraints are reintroduced during state
     * transitions.
     */
    private PathStrategy pathStrategy;

    /**
     * Creates a new {@code SolverConfiguration} instance with the specified parameters.
     * This configuration determines how the solver behaves when handling failed attempts,
     * adjusting solving parameters dynamically based on predefined scaling factors.
     *
     * @param nbFailed the initial number of allowed failed attempts before adjustments
     * @param factor the scaling factor applied when updating the solver's behavior
     * @param limitSolution the maximum number of solutions the solver is allowed to find
     * @param ratio the threshold ratio that influences state transitions in the solver
     */
    public SolverConfiguration(int nbFailed, double factor, long limitSolution, double ratio) {
        this.nbFailed = nbFailed;
        this.initFailed = nbFailed;
        this.factor = factor;
        this.limitSolution = limitSolution;
        this.ratio = ratio;
    }

    /**
     * Retrieves the current number of failed attempts before a solver adjustment is
     * triggered.
     *
     * @return the number of failed attempts before state adaptation
     */
    public int getNbFailed() {
        return nbFailed;
    }

    /**
     * Updates the number of failed attempts before a solver adjustment is triggered.
     *
     * @param nbFailed the new number of failed attempts before adaptation
     */
    public void setNbFailed(int nbFailed) {
        this.nbFailed = nbFailed;
    }

    /**
     * Retrieves the scaling factor applied when updating solver behavior.
     *
     * @return the scaling factor affecting solver adjustments
     */
    public double getFactor() {
        return factor;
    }

    /**
     * Sets the scaling factor that controls solver adjustments.
     *
     * @param factor the new scaling factor
     */
    public void setFactor(double factor) {
        this.factor = factor;
    }

    /**
     * Retrieves the maximum number of solutions the solver is allowed to find.
     *
     * @return the solution limit imposed on the solver
     */
    public long getLimitSolution() {
        return limitSolution;
    }

    /**
     * Updates the maximum number of solutions the solver is allowed to find.
     *
     * @param limitSolution the new solution limit
     */
    public void setLimitSolution(long limitSolution) {
        this.limitSolution = limitSolution;
    }

    /**
     * Retrieves the threshold ratio that influences solver state transitions.
     *
     * @return the threshold ratio affecting the solver's decision-making process
     */
    public double getRatio() {
        return ratio;
    }

    /**
     * Generates an updated solver configuration by applying the scaling factor
     * to the number of failed attempts. The new configuration retains the same
     * factor, solution limit, and ratio while inheriting the remover and path strategy.
     *
     * @return a new {@code SolverConfiguration} instance with updated parameters
     */
    public SolverConfiguration update() {
        var s = new SolverConfiguration((int) (this.nbFailed * this.factor), factor, limitSolution,
                ratio);
        s.setRemover(remover);
        s.setPathStrategy(pathStrategy);
        return s;
    }

    /**
     * Retrieves the constraint removal strategy associated with this configuration.
     *
     * @return the constraint remover strategy
     */
    public IConstraintsRemover getRemover() {
        return remover;
    }

    /**
     * Sets the constraint removal strategy used by the solver.
     *
     * @param remover the new constraint remover strategy
     */
    public void setRemover(IConstraintsRemover remover) {
        this.remover = remover;
    }

    /**
     * Retrieves the path strategy defining how constraints are reintroduced
     * during state transitions.
     *
     * @return the current path strategy used by the solver
     */
    public PathStrategy getPathStrategy() {
        return pathStrategy;
    }

    /**
     * Updates the path strategy that determines how constraints are reintroduced
     * during solver state transitions.
     *
     * @param pathStrategy the new path strategy
     */
    public void setPathStrategy(PathStrategy pathStrategy) {
        this.pathStrategy = pathStrategy;
    }

}
