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

import fr.univartois.cril.juniverse.core.IUniverseSolver;
import fr.univartois.cril.juniverse.core.UniverseSolverResult;

/**
 * The NormalStateSolver
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public class NormalStateSolver implements ISolverState {
	
	private static NormalStateSolver INSTANCE;
	
	private IUniverseSolver solver;
	
	
	private NormalStateSolver(IUniverseSolver solver) {
		this.solver = solver;
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see fr.univartois.cril.approximation.solver.state.ISolverState#solve()
	 */
	@Override
	public UniverseSolverResult solve() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see fr.univartois.cril.approximation.solver.state.ISolverState#nextState()
	 */
	@Override
	public ISolverState nextState() {
		return SubApproximationStateSolver.getInstance();
	}
	
	public static void initInstance(IUniverseSolver solver) {
		INSTANCE = new NormalStateSolver(solver);
	}
	
	public static NormalStateSolver getInstance() {
		return INSTANCE;
	}
	
	
}

