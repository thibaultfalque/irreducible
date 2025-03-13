/**
 * JUniverse, a solver interface.
 * Copyright (c) 2022 - Univ Artois, CNRS & Exakis Nelite.
 * All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
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

package fr.univartois.cril.approximation.core;

import org.chocosolver.solver.Solver;

import fr.univartois.cril.approximation.solver.state.AbstractState;
import fr.univartois.cril.approximation.solver.state.ISolverState;
import fr.univartois.cril.approximation.solver.state.NormalStateSolver;

/**
 * The KeepNoGoodStrategy
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public enum KeepNoGoodStrategy {
    ALWAYS {

        @Override
        public void resetNoGoods(AbstractState state, Solver ace) {
            
        }

        @Override
        public void resetNoGoods(NormalStateSolver state, Solver ace) {
            
        }
    },NEVER {

        @Override
        public void resetNoGoods(AbstractState state, Solver ace) {
            //todo
        }

        @Override
        public void resetNoGoods(NormalStateSolver state, Solver ace) {
        	//todo
        }
    },NORMAL_ONLY {

        @Override
        public void resetNoGoods(AbstractState state, Solver ace) {
           
        }

        @Override
        public void resetNoGoods(NormalStateSolver state, Solver ace) {
        }
    };

    public void resetNoGoods(ISolverState state, Solver ace) {
        state.resetNoGoods(this, ace);
    }
    public abstract void resetNoGoods(AbstractState state, Solver ace);
    public abstract void resetNoGoods(NormalStateSolver state, Solver ace);
}

