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


/**
 * The SolverConfiguration
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public class SolverConfiguration {
    private int nbRun;
    private int initRun;
    private double factor;
    private long limitSolution;
    /**
     * Creates a new SolverConfiguration.
     * @param nbRun
     * @param factor
     */
    public SolverConfiguration(int nbRun, double factor,long limitSolution) {
        super();
        this.nbRun = nbRun;
        this.initRun=nbRun;
        this.factor = factor;
        this.limitSolution=limitSolution;
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
    
    public void update() {
        this.nbRun=(int) (this.nbRun*this.factor);
    }
}

