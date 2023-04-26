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

import org.xcsp.common.Types.TypeFramework;

import fr.univartois.cril.aceurancetourix.AceHead;
import fr.univartois.cril.aceurancetourix.JUniverseAceProblemAdapter;
import fr.univartois.cril.approximation.core.KeepNoGoodStrategy;
import fr.univartois.cril.approximation.solver.ApproximationSolverDecorator;
import fr.univartois.cril.approximation.solver.SolverConfiguration;
import fr.univartois.cril.juniverse.core.IUniverseSolver;
import fr.univartois.cril.juniverse.core.UniverseSolverResult;
import heuristics.HeuristicValuesDynamic.AbstractSolutionScore;
import interfaces.Observers.ObserverOnSolution;
import optimization.Optimizer;
import solver.Solver;
import solver.Solver.WarmStarter;
import variables.Variable;

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

    private TypeFramework type = null;

    private Optimizer optimizer = null;

    private ObserverOnSolution observer = () ->
    ((JUniverseAceProblemAdapter)solver).getBuilder().getOptionsRestartsBuilder().setnRuns(Integer.MAX_VALUE);

    private NormalStateSolver(IUniverseSolver solver, SolverConfiguration config,ApproximationSolverDecorator decorator) {
        super(config, solver,decorator);
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.solver.state.ISolverState#solve()
     */
    @Override
    public UniverseSolverResult solve() {
        AceHead head = ((JUniverseAceProblemAdapter)solver).getHead();
        System.out.println("we solve with "+this);
        resetLimitSolver();
        if (!first) {
            decorator.reset();
        }
        first = false;
        if(type==null) {
            optimizer = ((JUniverseAceProblemAdapter)solver).getHead().problem.optimizer;
            type=((JUniverseAceProblemAdapter)solver).getHead().problem.framework;
        }
        for(Variable v:((JUniverseAceProblemAdapter) solver).getHead().solver.problem.variables) {
            ((AbstractSolutionScore)v.heuristic).setEnabled(false);
        }
        ((JUniverseAceProblemAdapter)solver).getHead().problem.framework=type;
        ((JUniverseAceProblemAdapter)solver).getHead().problem.optimizer=optimizer;
        ((JUniverseAceProblemAdapter)solver).getHead().getSolver().addObserverOnSolution(observer);
        var r= internalSolve();
        System.out.println(this +" "+r);
        ((JUniverseAceProblemAdapter)solver).getHead().getSolver().removeObserverOnSolution(observer);
        return r;
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.solver.state.ISolverState#nextState()
     */
    @Override
    public ISolverState nextState() {
        return new SubApproximationStateSolver(solver, this,decorator);
    }

    public static void initInstance(IUniverseSolver solver, SolverConfiguration configuration,ApproximationSolverDecorator decorator) {
        INSTANCE = new NormalStateSolver(solver, configuration,decorator);
    }

    public static NormalStateSolver getInstance() {
        return INSTANCE;
    }

    @Override
    public UniverseSolverResult solve(WarmStarter starter) {
        System.out.println("we solve with starter "+this);
        if(optimizer!=null) {
            System.out.println(optimizer.stringBounds());
        }

        AceHead head = ((JUniverseAceProblemAdapter)solver).getHead();
        for(Variable v:head.solver.problem.variables) {
            ((AbstractSolutionScore)v.heuristic).updateValue(starter.valueIndexOf(v));
            ((AbstractSolutionScore)v.heuristic).setEnabled(true);
        }
        ((JUniverseAceProblemAdapter)solver).getHead().problem.framework=type;
        ((JUniverseAceProblemAdapter)solver).getHead().problem.optimizer=optimizer;
        resetLimitSolver();
        decorator.reset();
        ((JUniverseAceProblemAdapter)solver).getHead().getSolver().addObserverOnSolution(observer);
        var r = internalSolve();
        System.out.println(this +" "+r);
        ((JUniverseAceProblemAdapter)solver).getHead().getSolver().removeObserverOnSolution(observer);
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
    public void displaySolution() {
        AceHead head = ((JUniverseAceProblemAdapter)solver).getHead();
        head.solver.solutions.displayFinalResults();
    }

    @Override
    public void resetNoGoods(KeepNoGoodStrategy ngStrategy, Solver ace) {
        ngStrategy.resetNoGoods(this, ace);
    }

}
