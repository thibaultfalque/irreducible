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
import java.util.List;
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

    private SubApproximationStateSolver next;

    private UniverseSolverResult last = UniverseSolverResult.UNKNOWN;

    private Set<Constraint> removedConstraints;

    private boolean restored;

    /**
     * Creates a new SubApproximationStateSolver.
     */
    public SubApproximationStateSolver(IUniverseSolver solver, ISolverState previous,
            ApproximationSolverDecorator decorator) {
        super(solverConfiguration, solver, decorator);
        this.previous = previous;
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
        if (removedConstraints == null || pathStrategy != PathStrategy.APPROX_ORDER) {
            removedConstraints = new HashSet<>(remover.computeNextConstraintsToRemove());
            nbRemoved += removedConstraints.size();
        } else {
            restored = true;
        }
        System.out.println(this + " we removed " + removedConstraints.size() + " constraints");
        if (!removedConstraints.isEmpty()) {
            for (Constraint c : removedConstraints) {
                if (c.ignorable) {
                    c.ignored = !c.ignored;
                }
            }
        } else {
            solverConfiguration.setNbRun(Integer.MAX_VALUE);
        }
        for (Variable v : ((JUniverseAceProblemAdapter) solver).getHead().solver.problem.variables) {
            ((AbstractSolutionScore) v.heuristic).setEnabled(true);
        }
        ((JUniverseAceProblemAdapter) solver).getHead().problem.framework = TypeFramework.CSP;
        ((JUniverseAceProblemAdapter) solver).getHead().problem.optimizer = null;
        resetLimitSolver();
        decorator.reset();
        last = internalSolve();
        System.out.println(this + " answer: " + last);
        return last;
    }

    @Override
    public ISolverState nextState() {
        if (next == null || pathStrategy != PathStrategy.APPROX_ORDER) {
            next = new SubApproximationStateSolver(solver, this, decorator);
        }
        if (last == UniverseSolverResult.UNKNOWN) {
            SubApproximationStateSolver tmp;
            for (tmp = next; tmp.next != null; tmp = next.next) {
            }
            tmp.next = new SubApproximationStateSolver(solver, tmp, decorator);
            return tmp.next;
        }
        return next;
    }

    public static void initInstance(IUniverseSolver solver, Supplier<IConstraintsRemover> r,
            SolverConfiguration config, PathStrategy ps) {
        sRemover = r;
        solverConfiguration = config;
        pathStrategy = ps;
    }

    @Override
    public UniverseSolverResult solve(WarmStarter starter) {
        if (pathStrategy == PathStrategy.APPROX_ORDER) {
            for (Constraint c : removedConstraints) {
                if (c.ignorable) {
                    c.ignored = false;
                }
            }
            restored = true;

        }
        System.out.println("we solve with starter " + this);
        // ((JUniverseAceProblemAdapter) solver).getHead().solver.warmStarter = starter;
        for (Variable v : ((JUniverseAceProblemAdapter) solver).getHead().solver.problem.variables) {
            int valueIndexOf = starter.valueIndexOf(v);
            if (valueIndexOf >= 0) {
                ((AbstractSolutionScore) v.heuristic).updateValue(valueIndexOf);
                ((AbstractSolutionScore) v.heuristic).setEnabled(true);
            }
        }
        ((JUniverseAceProblemAdapter) solver).getHead().problem.framework = TypeFramework.CSP;
        ((JUniverseAceProblemAdapter) solver).getHead().problem.optimizer = null;
        resetLimitSolver();
        decorator.reset();
        last = internalSolve();
        return last;
    }

    @Override
    public ISolverState previousState() {
        pathStrategy.restore(remover, removedConstraints);
        return pathStrategy.previous(previous, this);
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
        head.solver.solutions.displayFinalResults();
    }

    @Override
    public void resetNoGoods(KeepNoGoodStrategy ngStrategy, Solver ace) {
        ngStrategy.resetNoGoods(this, ace);
    }

    @Override
    public int getNbRemoved() {
        return previous.getNbRemoved()
                + (removedConstraints.stream().findAny().filter(
                        c -> c.ignored).isPresent() ? nbRemoved : 0)
                + (next != null ? next.getNbRemoved() : 0);
    }

    @Override
    public boolean isRestored() {
        return restored;
    }

}
