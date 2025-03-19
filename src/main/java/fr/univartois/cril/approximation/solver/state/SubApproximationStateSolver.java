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
import java.util.Set;

import org.chocosolver.parser.xcsp.XCSP;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.objective.ObjectiveFactory;

import fr.univartois.cril.approximation.core.IConstraintsRemover;
import fr.univartois.cril.approximation.core.KeepNoGoodStrategy;
import fr.univartois.cril.approximation.solver.ApproximationSolverDecorator;
import fr.univartois.cril.approximation.solver.SolverConfiguration;
import fr.univartois.cril.approximation.solver.SolverContext;
import fr.univartois.cril.approximation.solver.UniverseSolverResult;

/**
 * The {@code SubApproximationStateSolver} represents the state of the solver when
 * performing a search on a relaxed problem.
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public class SubApproximationStateSolver extends AbstractState {

    /** The remover. */
    private IConstraintsRemover remover;

    /** The previous. */
    private ISolverState previous;

    /** The next. */
    private ISolverState next;

    /** The last. */
    private UniverseSolverResult last = UniverseSolverResult.UNKNOWN;

    /** The removed constraints. */
    private Set<Constraint> removedConstraints;

    /** The restored. */
    private boolean restored;

    /**
     * Instantiates a new sub approximation state solver.
     *
     * @param context the context
     * @param solver the solver
     * @param previous the previous
     * @param decorator the decorator
     */
    public SubApproximationStateSolver(SolverContext context, Solver solver,
            ISolverState previous,
            ApproximationSolverDecorator decorator) {
        super(context, solver, decorator,
                context.getSubApproximationConfiguration().getPathStrategy());
        this.previous = previous;
        remover = getConfig().getRemover();
        pathStrategy = getConfig().getPathStrategy();
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.solver.state.AbstractState#resetLimitSolver()
     */
    @Override
    public void resetLimitSolver() {
        super.resetLimitSolver();
        context.updateSubApproximationConfiguration();
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.state.ISolverState#solve()
     */
    @Override
    public UniverseSolverResult solve() {
        listener.onSolve(this);
        if (removedConstraints == null) {
            removedConstraints = new HashSet<>(remover.computeNextConstraintsToRemove());
            nbRemoved += removedConstraints.size();
        } else {
            restored = true;
        }
        listener.onRemoveConstraints(this, removedConstraints.size());

        if (!removedConstraints.isEmpty()) {
            for (Constraint c : removedConstraints) {
                if (c.isIgnorable()) {
                    c.setEnabled(!c.isEnabled());
                }
            }
        } else {
            getConfig().setNbFailed(Integer.MAX_VALUE);
        }

        solver.setObjectiveManager(ObjectiveFactory.SAT());
        last = internalSolve();
        listener.onResult(this, last);
        listener.onStateSolved(this);
        return last;
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.solver.state.ISolverState#nextState()
     */
    @Override
    public ISolverState nextState() {
        next = new SubApproximationStateSolver(context, solver, this, decorator);
        next.setSolverListener(listener);
        return next;
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.solver.state.ISolverState#solveStarter()
     */
    @Override
    public UniverseSolverResult solveStarter() {
        listener.onSolveWithStarter(this);
        solver.setObjectiveManager(ObjectiveFactory.SAT());
        decorator.reset();
        resetLimitSolver();
        last = internalSolve();
        listener.onResult(this, last);
        listener.onStateSolved(this);
        return last;
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.solver.state.ISolverState#previousState()
     */
    @Override
    public ISolverState previousState() {
        pathStrategy.restore(remover, removedConstraints);
        listener.onRestoreConstraints(this, removedConstraints.size());
        return pathStrategy.previous(previous, this);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "SubApproximationStateSolver";
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
        // TODO add all nbRemoved of the chain
        return nbRemoved;
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.solver.state.ISolverState#isRestored()
     */
    @Override
    public boolean isRestored() {
        return restored;
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.solver.state.ISolverState#isSafe()
     */
    @Override
    public boolean isSafe() {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.solver.state.ISolverState#getConfig()
     */
    @Override
    public final SolverConfiguration getConfig() {
        return context.getSubApproximationConfiguration();
    }

}
