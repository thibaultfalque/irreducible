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

import constraints.Constraint;
import fr.univartois.cril.aceurancetourix.JUniverseAceProblemAdapter;
import fr.univartois.cril.approximation.core.IConstraintsRemover;
import fr.univartois.cril.approximation.solver.SolverConfiguration;
import fr.univartois.cril.juniverse.core.IUniverseSolver;
import fr.univartois.cril.juniverse.core.UniverseSolverResult;
import solver.Solver.WarmStarter;

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
    private ISolverState previous;
    private UniverseSolverResult last=UniverseSolverResult.UNKNOWN;
    private Set<Constraint> removedConstraints;

    /**
     * Creates a new SubApproximationStateSolver.
     */
    public SubApproximationStateSolver(IUniverseSolver solver,ISolverState previous) {
        super(solverConfiguration, solver);
        this.previous=previous;
        this.removedConstraints=new HashSet<>();
        if(remover==null) {
            remover=Objects.requireNonNull(sRemover.get());
        }
    }


    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.state.ISolverState#solve()
     */
    @Override
    public UniverseSolverResult solve() {
        var list = remover.computeNextConstraintsToRemove();       
        
        for(Constraint c:list) {
            c.ignored=true;
            removedConstraints.add(c);
        }
        resetLimitSolver();
        solver.reset();
        last = solver.solve();
        System.out.println("last: "+last);
        return last;
    }

    @Override
    public ISolverState nextState() {
        return new SubApproximationStateSolver(solver, this);
    }

    public static void initInstance(IUniverseSolver solver, Supplier<IConstraintsRemover> r,
            SolverConfiguration config,PathStrategy ps) {
        sRemover = r;
        solverConfiguration=config;
        pathStrategy=ps;
    }

    @Override
    public UniverseSolverResult solve(WarmStarter starter) {
        ((JUniverseAceProblemAdapter)solver).getHead().solver.warmStarter = starter;
        resetLimitSolver();
        last= solver.solve();
        return last;
    }

    @Override
    public ISolverState previousState() {
        remover.restoreConstraints(removedConstraints);
        return pathStrategy.previous(previous);
    }
    

}
