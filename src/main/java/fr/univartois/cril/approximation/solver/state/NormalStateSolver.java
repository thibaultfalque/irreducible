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
import fr.univartois.cril.approximation.solver.SolverContext;
import fr.univartois.cril.approximation.solver.UniverseSolverResult;

/**
 * The {@code NormalStateSolver} represents the state of the solver when performing
 * a standard search on the original problem without any modifications or relaxations.
 * This mode ensures that the solver explores the search space as defined by the
 * initial problem constraints.
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public class NormalStateSolver extends AbstractState {

    /**
     * Default solution observer that resets stop criteria and removes hints.
     * If the solver finds a solution during a normal search state, it continues
     * using this classical search approach. Hints are also removed as they become
     * irrelevant once a solution has been found.
     */
    private IMonitorSolution observerSolution = () -> {
        solver.removeAllStopCriteria();
        solver.removeHints();
    };

    /**
     * A reference to the next state of the solver in the relaxation process.
     */
    private ISolverState next;

    /** A reference to the objective manager. */
    public IObjectiveManager<Variable> om;

    /**
     * Instantiates a new normal state solver.
     *
     * @param solver the solver
     * @param context the context
     * @param decorator the decorator
     */
    public NormalStateSolver(Solver solver, SolverContext context,
            ApproximationSolverDecorator decorator) {
        super(context, solver, decorator, context.getNormalConfiguration().getPathStrategy());
        this.om = solver.getObjectiveManager();
    }

    @Override
    public void resetLimitSolver() {
        super.resetLimitSolver();
        context.updateNormalConfiguration();
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.solver.state.ISolverState#solve()
     */
    @Override
    public UniverseSolverResult solve() {
        System.out.println("we solve with " + this);
        solver.setObjectiveManager(om);
        solver.plugMonitor(observerSolution);
        var r = internalSolve();
        System.out.println(this + " " + r);
        solver.unplugMonitor(observerSolution);
        return r;
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.solver.state.ISolverState#nextState()
     */
    @Override
    public ISolverState nextState() {
        next = new SubApproximationStateSolver(context, solver, this, decorator);
        return next;
    }

    // /**
    // * Inits the instance.
    // *
    // * @param solver the solver
    // * @param configuration the configuration
    // * @param decorator the decorator
    // * @param strat the strat
    // */
    // public static void initInstance(Solver solver, SolverConfiguration configuration,
    // ApproximationSolverDecorator decorator, PathStrategy strat) {
    // INSTANCE = new NormalStateSolver(solver, configuration, decorator, strat);
    // }

    // /**
    // * Gets the single instance of NormalStateSolver.
    // *
    // * @return single instance of NormalStateSolver
    // */
    // public static NormalStateSolver getInstance() {
    // return INSTANCE;
    // }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.solver.state.ISolverState#solveStarter()
     */
    @Override
    public UniverseSolverResult solveStarter() {
        System.out.println("we solve with starter " + this);
        solver.setObjectiveManager(om);
        solver.plugMonitor(observerSolution);
        solver.limitSolution(Integer.MAX_VALUE);

        var r = internalSolve();
        System.out.println(this + " " + r);
        solver.unplugMonitor(observerSolution);
        return r;
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.solver.state.ISolverState#previousState()
     */
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

    /*
     * (non-Javadoc)
     *
     * @see
     * fr.univartois.cril.approximation.solver.state.ISolverState#displaySolution(XCSP)
     */
    @Override
    public void displaySolution(XCSP xcsp) {
        decorator.displaySolution(xcsp);
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.solver.state.ISolverState#resetNoGoods(
     * KeepNoGoodStrategy, Solver)
     */
    @Override
    public void resetNoGoods(KeepNoGoodStrategy ngStrategy, Solver ace) {
        ngStrategy.resetNoGoods(this, ace);
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.solver.state.ISolverState#getNbRemoved()
     */
    @Override
    public int getNbRemoved() {
        return 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.solver.state.ISolverState#isRestored()
     */
    @Override
    public boolean isRestored() {
        return true;
    }

    /**
     * Gives the observer of solution of this NormalStateSolver.
     *
     * @return This NormalStateSolver's observerSolution.
     */
    public IMonitorSolution getObserverSolution() {
        return observerSolution;
    }

    /**
     * Sets the observer of solution.
     *
     * @param observerSolution The observer of solution to set.
     */
    public void setObserverSolution(IMonitorSolution observerSolution) {
        this.observerSolution = observerSolution;
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.solver.state.ISolverState#isSafe()
     */
    @Override
    public boolean isSafe() {
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.solver.state.ISolverState#getConfig()
     */
    @Override
    public SolverConfiguration getConfig() {
        return context.getNormalConfiguration();
    }
}
