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

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import org.xcsp.common.Types.TypeFramework;

import constraints.Constraint;
import fr.univartois.cril.aceurancetourix.AceHead;
import fr.univartois.cril.aceurancetourix.JUniverseAceProblemAdapter;
import fr.univartois.cril.approximation.core.IConstraintsRemover;
import fr.univartois.cril.approximation.core.KeepNoGoodStrategy;
import fr.univartois.cril.approximation.solver.ApproximationSolverDecorator;
import fr.univartois.cril.approximation.solver.SolverConfiguration;
import fr.univartois.cril.juniverse.core.IUniverseSolver;
import fr.univartois.cril.juniverse.core.UniverseSolverResult;
import heuristics.HeuristicValuesDynamic.AbstractSolutionScore;
import solver.Solver;
import solver.Solver.WarmStarter;
import utility.Kit;
import variables.Variable;

/**
 * The SubApproximationStateSolver
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public class SubApproximationStateSolver extends AbstractState {

    private static Supplier<IConstraintsRemover> sRemover;

    private static IConstraintsRemover remover;

    private static SolverConfiguration solverConfiguration;

    private static PathStrategy pathStrategy;

    private static int subApproximationCounter = 0;

    private int nb;

    private ISolverState previous;

    private UniverseSolverResult last = UniverseSolverResult.UNKNOWN;

    private Set<Constraint> removedConstraints;

    /**
     * Creates a new SubApproximationStateSolver.
     */
    public SubApproximationStateSolver(IUniverseSolver solver, ISolverState previous,ApproximationSolverDecorator decorator) {
        super(solverConfiguration, solver,decorator);
        this.previous = previous;
        this.removedConstraints = new HashSet<>();
        this.nb = subApproximationCounter;
        subApproximationCounter++;
        if (remover == null) {
            remover = Objects.requireNonNull(sRemover.get());
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.state.ISolverState#solve()
     */
    @Override
    public UniverseSolverResult solve() {
        System.out.println("we solve with " + this);
        var list = remover.computeNextConstraintsToRemove();
        System.out.println(this + " we removed " + list.size() + " constraints");
        if (!list.isEmpty()) {
            for (Constraint c : list) {
                c.ignored = true;
                removedConstraints.add(c);
            }
        } else {
            solverConfiguration.setNbRun(Integer.MAX_VALUE);
        }
        for(Variable v:((JUniverseAceProblemAdapter) solver).getHead().solver.problem.variables) {
            ((AbstractSolutionScore)v.heuristic).setEnabled(true);
        }
        ((JUniverseAceProblemAdapter) solver).getHead().problem.framework = TypeFramework.CSP;
        ((JUniverseAceProblemAdapter) solver).getHead().problem.optimizer = null;
        resetLimitSolver();
        decorator.reset();
        last = solver.solve();
        System.out.println(this + " answer: " + last);
        return last;
    }

    @Override
    public ISolverState nextState() {
        return new SubApproximationStateSolver(solver, this,decorator);
    }

    public static void initInstance(IUniverseSolver solver, Supplier<IConstraintsRemover> r,
            SolverConfiguration config, PathStrategy ps) {
        sRemover = r;
        solverConfiguration = config;
        pathStrategy = ps;
    }

    @Override
    public UniverseSolverResult solve(WarmStarter starter) {
        System.out.println("we solve with starter " + this);
//        ((JUniverseAceProblemAdapter) solver).getHead().solver.warmStarter = starter;
        for(Variable v:((JUniverseAceProblemAdapter) solver).getHead().solver.problem.variables) {
            ((AbstractSolutionScore)v.heuristic).updateValue(starter.valueIndexOf(v));
            ((AbstractSolutionScore)v.heuristic).setEnabled(true);
        }
        ((JUniverseAceProblemAdapter) solver).getHead().problem.framework = TypeFramework.CSP;
        ((JUniverseAceProblemAdapter) solver).getHead().problem.optimizer = null;
        resetLimitSolver();
        decorator.reset();
        last = solver.solve();
        return last;
    }

    @Override
    public ISolverState previousState() {
        if (!removedConstraints.isEmpty()) {
            remover.restoreConstraints(removedConstraints);
        }
        System.out.println(this + " we restore " + removedConstraints.size());
        return pathStrategy.previous(previous);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "SubApproximationStateSolver [nb=" + nb + "]";
    }

    @Override
    public void displaySolution() {
        AceHead head = ((JUniverseAceProblemAdapter) solver).getHead();
        System.out.println("s UNKNOWN");
        System.out.println("d INCOMPLETE EXPLORATION");
        Kit.log.config("\nc real time : " + head.stopwatch.cpuTimeInSeconds());
        System.out.flush();
    }

    @Override
    public void resetNoGoods(KeepNoGoodStrategy ngStrategy, Solver ace) {
        ngStrategy.resetNoGoods(this, ace);
    }

}
