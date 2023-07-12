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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import constraints.Constraint;
import fr.univartois.cril.aceurancetourix.AceHead;
import fr.univartois.cril.aceurancetourix.JUniverseAceProblemAdapter;
import fr.univartois.cril.aceurancetourix.reader.XCSP3Reader;
import fr.univartois.cril.approximation.core.GroupConstraint;
import fr.univartois.cril.approximation.core.IConstraintGroupSolver;
import fr.univartois.cril.approximation.core.KeepFalsifiedConstraintStrategy;
import fr.univartois.cril.approximation.core.KeepNoGoodStrategy;
import fr.univartois.cril.approximation.solver.state.ISolverState;
import fr.univartois.cril.approximation.solver.state.NormalStateSolver;
import fr.univartois.cril.juniverse.core.UniverseAssumption;
import fr.univartois.cril.juniverse.core.UniverseContradictionException;
import fr.univartois.cril.juniverse.core.UniverseSolverResult;
import fr.univartois.cril.juniverse.core.problem.IUniverseConstraint;
import fr.univartois.cril.juniverse.core.problem.IUniverseVariable;
import problem.Problem;
import solver.Solver;
import solver.Solver.WarmStarter;

/**
 * The ApproximationSolverDecorator
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public class ApproximationSolverDecorator
        implements IConstraintGroupSolver {

    private JUniverseAceProblemAdapter solver;

    private List<GroupConstraint> groupConstraints;

    private ISolverState state;

    private KeepNoGoodStrategy keepNogood = KeepNoGoodStrategy.ALWAYS;

    private KeepFalsifiedConstraintStrategy keepFalsified = KeepFalsifiedConstraintStrategy.NEVER;

    /**
     * Creates a new ApproximationSolverDecorator.
     */
    public ApproximationSolverDecorator(JUniverseAceProblemAdapter solver) {
        this.solver = solver;
        this.groupConstraints = new ArrayList<>();
    }

    @Override
    public void reset() {
        solver.reset();
        solver.getHead().solver.lastConflict.lastAssigned = null;
        Solver ace = solver.getHead().getSolver();
        ace.propagation.clear();
        ace.propagation.nTuplesRemoved = 0;
        ace.restarter.reset();
        ace.resetNoSolutions();
        keepNogood.resetNoGoods(state, ace);
        ace.heuristic.setPriorityVars(ace.problem.priorityVars, 0);
        ace.stats.reset();
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
        int nb = 0;
        for (Constraint c : getAceConstraints()) {
            if (!c.ignored) {
                nb++;
            }
        }
        return nb;
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
        this.state = NormalStateSolver.getInstance();
        var result = state.solve();
        while (result == UniverseSolverResult.UNKNOWN && !this.state.isTimeout()) {
            state = state.nextState();
            System.out.println("Start new state: " + this.state);
            result = state.solve();
            while (result == UniverseSolverResult.SATISFIABLE
                    && this.state.getNbRemoved() != 0 && !this.state.isTimeout()) {
                var solution = solver.solution();
                String stringSolution = solution.stream().map(BigInteger::toString).collect(
                        Collectors.joining(" "));
                WarmStarter starter = new WarmStarter(stringSolution, solver.getHead().solver);
                keepFalsified.checkConstraints(starter, solver.getHead().solver);
                state = state.previousState();
                System.out.println("change to previous state: " + this.state);
                result = state.solve(starter);
                System.out.println(result + " after state.solve()");
            }
            System.out.println(result + " after while");
        }
        System.out.println(result + " before end");
        if(this.state.isTimeout()) {
            result=UniverseSolverResult.UNKNOWN;
        }
        return result;
    }

    @Override
    public UniverseSolverResult solve(String filename) {
        try {
            return solve(new FileInputStream(filename));
        } catch (UniverseContradictionException e) {
            e.printStackTrace();
            return UniverseSolverResult.UNSATISFIABLE;
        } catch (IOException e) {
            e.printStackTrace();
            return UniverseSolverResult.UNKNOWN;
        }
    }

    public UniverseSolverResult solve(FileInputStream stream)
            throws UniverseContradictionException, IOException {
        XCSP3Reader reader = new XCSP3Reader(solver);
        reader.parseInstance(stream);
        return solve();
    }

    @Override
    public UniverseSolverResult solve(List<UniverseAssumption<BigInteger>> assumptions) {
        return solver.solve(assumptions);
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
    public List<Constraint> getAceConstraints() {
        return List.of(getProblem().constraints);
    }

    @Override
    public Constraint getConstraint(int index) {
        return getProblem().constraints[index];
    }

    @Override
    public List<GroupConstraint> getGroups() {
        if (this.groupConstraints.isEmpty()) {

            int nbGroups = JUniverseAceProblemAdapter.currentGroup + 1;
            this.groupConstraints = new ArrayList<>(Collections.nCopies(nbGroups, null));
            for (int i = 0; i < getProblem().constraints.length; i++) {
                Constraint c = getProblem().constraints[i];
                int group = c.group;
                if (this.groupConstraints.get(group) == null) {
                    this.groupConstraints.set(group, new GroupConstraint(group));
                }
                this.groupConstraints.get(group).add(c);
            }
        }
        return this.groupConstraints;
    }

    @Override
    public GroupConstraint getGroup(int index) {
        return this.getGroups().get(index);
    }

    @Override
    public int nGroups() {
        return this.getGroups().size();
    }

    @Override
    public Map<String, BigInteger> mapSolution(boolean excludeAux) {
        // TODO Auto-generated method stub
        return null;
    }

    public void displaySolution() {
        if (state != null) {
            state.displaySolution();
        } else {
            System.out.println("s UNKNOWN");
        }

    }

    /**
     * @return
     * @see fr.univartois.cril.aceurancetourix.JUniverseAceProblemAdapter#getHead()
     */
    public AceHead getHead() {
        return solver.getHead();
    }

    /**
     * @return
     */
    private Problem getProblem() {
        return solver.getHead().getSolver().problem;
    }

    @Override
    public void loadInstance(String filename) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isOptimization() {
        // TODO Auto-generated method stub
        return false;
    }

    public void setKeepNogood(KeepNoGoodStrategy keepNogood) {
        this.keepNogood = keepNogood;
    }


    /**
     * Sets this ApproximationSolverDecorator's keepFalsified.
     *
     * @param keepFalsified The keepFalsified to set.
     */
    public void setKeepFalsified(KeepFalsifiedConstraintStrategy keepFalsified) {
        this.keepFalsified = keepFalsified;
    }
    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.juniverse.core.IUniverseSolver#decisionVariables(java.util.List)
     */
    @Override
    public void decisionVariables(List<String> variables) {
        // TODO Auto-generated method stub.

    }

    @Override
    public List<String> getAuxiliaryVariables() {
        // TODO Auto-generated method stub.
        return null;
    }

    @Override
    public void valueHeuristicStatic(List<String> variables, List<? extends Number> orderedValues) {
        // TODO Auto-generated method stub.
        
    }

    @Override
    public void setLogStream(OutputStream stream) {
        // TODO Auto-generated method stub.
        
    }

    @Override
    public boolean checkSolution() {
        // TODO Auto-generated method stub.
        return false;
    }

    @Override
    public boolean checkSolution(Map<String, BigInteger> assignment) {
        // TODO Auto-generated method stub.
        return false;
    }

    @Override
    public List<IUniverseConstraint> getConstraints() {
        return solver.getConstraints();
    }


}
