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

import java.util.stream.Collectors;

import org.xcsp.common.Types.TypeFramework;

import fr.univartois.cril.aceurancetourix.AceHead;
import fr.univartois.cril.aceurancetourix.JUniverseAceProblemAdapter;
import fr.univartois.cril.approximation.solver.SolverConfiguration;
import fr.univartois.cril.juniverse.core.IUniverseSolver;
import fr.univartois.cril.juniverse.core.UniverseSolverResult;
import solver.Solver.WarmStarter;

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

    private NormalStateSolver(IUniverseSolver solver, SolverConfiguration config) {
        super(config, solver);
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
            solver.reset();
        }
        first = false;
        if(type==null) {
            type=((JUniverseAceProblemAdapter)solver).getHead().problem.framework;
        }
        ((JUniverseAceProblemAdapter)solver).getHead().problem.framework=type;
        var r= solver.solve();
        System.out.println(this +" "+r);
        if(r!=UniverseSolverResult.SATISFIABLE) {
            return r;
        }
        if(head.problem.framework==TypeFramework.COP) {
            System.out.println(this +" continue COP ");
            var solution = solver.solution();
            String stringSolution = solution.stream().map(i -> i.toString()).collect(
                    Collectors.joining(" "));
            var starter = new WarmStarter(stringSolution, head.solver);
            config.setNbRun(Integer.MAX_VALUE);
            resetLimitSolver();
            solver.reset();
            head.solver.warmStarter=starter;
            return solver.solve();
        }
        return r;
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.solver.state.ISolverState#nextState()
     */
    @Override
    public ISolverState nextState() {
        return new SubApproximationStateSolver(solver, this);
    }

    public static void initInstance(IUniverseSolver solver, SolverConfiguration configuration) {
        INSTANCE = new NormalStateSolver(solver, configuration);
    }

    public static NormalStateSolver getInstance() {
        return INSTANCE;
    }

    @Override
    public UniverseSolverResult solve(WarmStarter starter) {
        System.out.println("we solve with starter "+this);
        AceHead head = ((JUniverseAceProblemAdapter)solver).getHead();
        head.solver.warmStarter = starter;
        head.problem.framework=type;
        resetLimitSolver();
        solver.reset();
        var r = solver.solve();
        System.out.println(this +" "+r);
        if(r!=UniverseSolverResult.SATISFIABLE) {
            return r;
        }
        if(head.problem.framework==TypeFramework.COP) {
            System.out.println(this +" continue COP ");
            var solution = solver.solution();
            String stringSolution = solution.stream().map(i -> i.toString()).collect(
                    Collectors.joining(" "));
            starter = new WarmStarter(stringSolution, head.solver);
            config.setNbRun(Integer.MAX_VALUE);
            resetLimitSolver();
            solver.reset();
            head.solver.warmStarter=starter;
            return solver.solve();
        }
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
    
}
