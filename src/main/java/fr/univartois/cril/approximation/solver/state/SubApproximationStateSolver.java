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

import fr.univartois.cril.approximation.core.remover.IConstraintsRemover;
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

    private IConstraintsRemover remover;

    private int nbSubApproximation;

    private static AbstractState INSTANCE;

    /**
     * Creates a new SubApproximationStateSolver.
     */
    public SubApproximationStateSolver(IUniverseSolver solver, IConstraintsRemover remover,
            SolverConfiguration config) {
        super(config, solver);
        this.remover = remover;
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.state.ISolverState#solve()
     */
    @Override
    public UniverseSolverResult solve() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ISolverState nextState() {
        return NormalStateSolver.getInstance();
    }

    public static void initInstance(IUniverseSolver solver, IConstraintsRemover remover,
            SolverConfiguration config) {
        INSTANCE = new SubApproximationStateSolver(solver, remover, config);
    }

    public static AbstractState getInstance() {
        return INSTANCE;
    }

    @Override
    public UniverseSolverResult solve(WarmStarter starter) {
        // TODO Auto-generated method stub
        return null;
    }

}
