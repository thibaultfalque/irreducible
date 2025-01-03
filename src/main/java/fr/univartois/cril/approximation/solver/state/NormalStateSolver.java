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

package fr.univartois.cril.approximation.solver.state;

import org.chocosolver.parser.xcsp.XCSP;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.objective.IObjectiveManager;
import org.chocosolver.solver.search.loop.monitors.IMonitorSolution;
import org.chocosolver.solver.variables.Variable;

import fr.univartois.cril.approximation.core.KeepNoGoodStrategy;
import fr.univartois.cril.approximation.solver.ApproximationSolverDecorator;
import fr.univartois.cril.approximation.solver.SolverConfiguration;
import fr.univartois.cril.approximation.solver.UniverseSolverResult;


/**
 * The NormalStateSolver
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public class NormalStateSolver extends AbstractState {

    private static NormalStateSolver INSTANCE;

    private boolean first = true;

    private IMonitorSolution observer = () -> {
    	solver.removeAllStopCriteria();
    	solver.removeHints();
    };

    private ISolverState next;

    private PathStrategy strat;
    
    private IObjectiveManager<Variable> om;

    private NormalStateSolver(Solver solver, SolverConfiguration config,
            ApproximationSolverDecorator decorator, PathStrategy strat) {
        super(config, solver, decorator);
        this.strat = strat;
        this.om = solver.getObjectiveManager();
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.solver.state.ISolverState#solve()
     */
    @Override
    public UniverseSolverResult solve() {
        System.out.println("we solve with " + this);

        first = false;
        solver.setObjectiveManager(om);
        solver.plugMonitor(observer);
        var r = internalSolve();
        System.out.println(this + " " + r);
        solver.unplugMonitor(observer);
        return r;
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.solver.state.ISolverState#nextState()
     */
    @Override
    public ISolverState nextState() {
        if (next == null || strat != PathStrategy.APPROX_ORDER) {
            next = new SubApproximationStateSolver(solver, this, decorator);
        }
        return next;
    }

    public static void initInstance(Solver solver, SolverConfiguration configuration,
            ApproximationSolverDecorator decorator, PathStrategy strat) {
        INSTANCE = new NormalStateSolver(solver, configuration, decorator, strat);
    }

    public static NormalStateSolver getInstance() {
        return INSTANCE;
    }

    @Override
    public UniverseSolverResult solveStarter() {
        System.out.println("we solve with starter " + this);
        solver.setObjectiveManager(om);
        solver.plugMonitor(observer);
        solver.limitSolution(Integer.MAX_VALUE);
        var r = internalSolve();
        System.out.println(this + " " + r);
        solver.unplugMonitor(observer);
        return r;
    }

    @Override
    public ISolverState previousState() {
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "NormalStateSolver []";
    }

    @Override
    public void displaySolution(XCSP xcsp) {
        decorator.displaySolution(xcsp);
    }

    @Override
    public void resetNoGoods(KeepNoGoodStrategy ngStrategy, Solver ace) {
        ngStrategy.resetNoGoods(this, ace);
    }

    @Override
    public int getNbRemoved() {
        return 0;
    }

    @Override
    public boolean isRestored() {
        return true;
    }

}
