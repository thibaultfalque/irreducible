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
 * The SolverConfiguration.
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public class SolverConfiguration {

    /** The nb run. */
    private int nbRun;

    /** The init run. */
    private int initRun;

    /** The factor. */
    private double factor;

    /** The limit solution. */
    private long limitSolution;

    /** The dichotomic bound. */
    private boolean dichotomicBound;

    /** The ratio. */
    private double ratio;

    /** The remover. */
    private IConstraintsRemover remover;

    /** The path strategy. */
    private PathStrategy pathStrategy;

    /**
     * Creates a new SolverConfiguration.
     *
     * @param nbRun the nb run
     * @param factor the factor
     * @param limitSolution the limit solution
     * @param ratio the ratio
     * @param dichotomic the dichotomic
     */
    public SolverConfiguration(int nbRun, double factor, long limitSolution, double ratio,
            boolean dichotomic) {
        this.nbRun = nbRun;
        this.initRun = nbRun;
        this.factor = factor;
        this.limitSolution = limitSolution;
        this.ratio = ratio;
        this.dichotomicBound = dichotomic;
    }

    /**
     * Gives the nbRun of this SolverConfiguration.
     *
     * @return This SolverConfiguration's nbRun.
     */
    public int getNbRun() {
        return nbRun;
    }

    /**
     * Sets this SolverConfiguration's nbRun.
     *
     * @param nbRun The new nbRun for this SolverConfiguration.
     */
    public void setNbRun(int nbRun) {
        this.nbRun = nbRun;
    }

    /**
     * Gives the factor of this SolverConfiguration.
     *
     * @return This SolverConfiguration's factor.
     */
    public double getFactor() {
        return factor;
    }

    /**
     * Sets this SolverConfiguration's factor.
     *
     * @param factor The new factor for this SolverConfiguration.
     */
    public void setFactor(double factor) {
        this.factor = factor;
    }

    /**
     * Gives the limitSolution of this SolverConfiguration.
     *
     * @return This SolverConfiguration's limitSolution.
     */
    public long getLimitSolution() {
        return limitSolution;
    }

    /**
     * Sets this SolverConfiguration's limitSolution.
     *
     * @param limitSolution The new limitSolution for this SolverConfiguration.
     */
    public void setLimitSolution(long limitSolution) {
        this.limitSolution = limitSolution;
    }

    /**
     * Gives the ratio of this SolverConfiguration.
     *
     * @return This SolverConfiguration's ratio.
     */
    public double getRatio() {
        return ratio;
    }

    /**
     * Checks if is dichotomic bound.
     *
     * @return the dichotomicBound
     */
    public boolean isDichotomicBound() {
        return dichotomicBound;
    }

    /**
     * Sets the dichotomic bound.
     *
     * @param dichotomicBound the dichotomicBound to set
     */
    public void setDichotomicBound(boolean dichotomicBound) {
        this.dichotomicBound = dichotomicBound;
    }

    /**
     * Update.
     *
     * @return the solver configuration
     */
    public SolverConfiguration update() {
        return new SolverConfiguration((int) (this.nbRun * this.factor), factor, limitSolution,
                ratio, dichotomicBound);
    }

    /**
     * Gets the remover.
     *
     * @return the remover
     */
    public IConstraintsRemover getRemover() {
        return remover;
    }

    /**
     * Sets the remover.
     *
     * @param remover the new remover
     */
    public void setRemover(IConstraintsRemover remover) {
        this.remover = remover;
    }

    /**
     * Gets the path strategy.
     *
     * @return the path strategy
     */
    public PathStrategy getPathStrategy() {
        return pathStrategy;
    }

    /**
     * Sets the path strategy.
     *
     * @param pathStrategy the new path strategy
     */

    public void setPathStrategy(PathStrategy pathStrategy) {
        this.pathStrategy = pathStrategy;
    }

}
