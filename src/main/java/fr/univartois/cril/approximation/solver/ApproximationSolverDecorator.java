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

package fr.univartois.cril.approximation.solver;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import constraints.Constraint;
import fr.univartois.cril.aceurancetourix.JUniverseAceProblemAdapter;
import fr.univartois.cril.approximation.core.IConstraintGroupSolver;
import fr.univartois.cril.approximation.core.IRemovableConstraintSolver;
import fr.univartois.cril.approximation.solver.state.ISolverState;
import fr.univartois.cril.approximation.solver.state.NormalStateSolver;
import fr.univartois.cril.juniverse.core.IUniverseSolver;
import fr.univartois.cril.juniverse.core.UniverseAssumption;
import fr.univartois.cril.juniverse.core.UniverseSolverResult;
import fr.univartois.cril.juniverse.core.problem.IUniverseVariable;


/**
 * The ApproximationSolverDecorator
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public class ApproximationSolverDecorator implements IUniverseSolver,IConstraintGroupSolver,IRemovableConstraintSolver {
    
    private JUniverseAceProblemAdapter solver;
    
    private ISolverState state;
    
    /**
     * Creates a new ApproximationSolverDecorator.
     */
    public ApproximationSolverDecorator(JUniverseAceProblemAdapter solver) {
        this.solver=solver;
        this.state = NormalStateSolver.getInstance();
    }

    @Override
    public void reset() {
        solver.reset();
    }

    @Override
    public int nVariables() {
        return solver.nVariables();
    }

    @Override
    public Map<String, IUniverseVariable> getVariablesMapping() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int nConstraints() {
        return solver.nConstraints();
    }

    @Override
    public void setTimeout(long seconds) {
        solver.setTimeout(seconds);
    }

    @Override
    public void setTimeoutMs(long mseconds) {
        solver.setTimeoutMs(mseconds);
    }

    @Override
    public void setVerbosity(int level) {
        solver.setVerbosity(level);
        
    }

    @Override
    public void setLogFile(String filename) {
        solver.setLogFile(filename);
    }

    @Override
    public UniverseSolverResult solve() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public UniverseSolverResult solve(String filename) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public UniverseSolverResult solve(List<UniverseAssumption<BigInteger>> assumptions) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void interrupt() {
        solver.interrupt();
    }

    @Override
    public List<BigInteger> solution() {
        return solver.solution();
    }

    @Override
    public Map<String, BigInteger> mapSolution() {
        return solver.mapSolution();
    }

	@Override
	public List<Constraint> getConstraints() {
		return List.of(solver.getHead().problem.constraints);
	}

	@Override
	public List<List<Constraint>> getGroup() {
		int nbGroups = solver.getHead().problem.features.nGroups;
		List<List<Constraint>> lists = new ArrayList<>(nbGroups);
		for(int i=0;i<solver.getHead().problem.constraints.length;i++) {
			Constraint c = solver.getHead().problem.constraints[i];
			int group =c.group;
			if(lists.get(group)==null) {
				lists.set(group,new ArrayList<>());
			}
			lists.get(group).add(c);
		}
		return lists;
	}

	@Override
	public void removeConstraints(List<Constraint> constraints) {
		for(var c: constraints) {
			solver.getHead().problem.constraints[c.num].ignored=true;
		}
	}

	@Override
	public void restoreConstraints(List<Constraint> constraints) {
		for(var c: constraints) {
			solver.getHead().problem.constraints[c.num].ignored=false;
		}
	}

}

