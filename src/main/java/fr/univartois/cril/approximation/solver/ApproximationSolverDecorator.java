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

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.IntPredicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.chocosolver.memory.IEnvironment;
import org.chocosolver.parser.Level;
import org.chocosolver.parser.xcsp.XCSP;
import org.chocosolver.solver.ICause;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.ResolutionPolicy;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.learn.AbstractEventObserver;
import org.chocosolver.solver.objective.IBoundsManager;
import org.chocosolver.solver.objective.IObjectiveManager;
import org.chocosolver.solver.propagation.PropagationEngine;
import org.chocosolver.solver.propagation.PropagationObserver;
import org.chocosolver.solver.propagation.PropagationProfiler;
import org.chocosolver.solver.search.SearchState;
import org.chocosolver.solver.search.limits.ICounter;
import org.chocosolver.solver.search.loop.learn.Learn;
import org.chocosolver.solver.search.loop.lns.neighbors.INeighbor;
import org.chocosolver.solver.search.loop.monitors.IMonitorSolution;
import org.chocosolver.solver.search.loop.monitors.ISearchMonitor;
import org.chocosolver.solver.search.loop.monitors.SearchMonitorList;
import org.chocosolver.solver.search.loop.monitors.SolvingStatisticsFlow;
import org.chocosolver.solver.search.loop.move.Move;
import org.chocosolver.solver.search.loop.propagate.Propagate;
import org.chocosolver.solver.search.measure.IMeasures;
import org.chocosolver.solver.search.measure.MeasuresRecorder;
import org.chocosolver.solver.search.restart.AbstractRestart;
import org.chocosolver.solver.search.restart.ICutoff;
import org.chocosolver.solver.search.strategy.decision.Decision;
import org.chocosolver.solver.search.strategy.decision.DecisionPath;
import org.chocosolver.solver.search.strategy.strategy.AbstractStrategy;
import org.chocosolver.solver.trace.IMessage;
import org.chocosolver.solver.trace.VerboseSolving;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.Variable;
import org.chocosolver.util.ESat;
import org.chocosolver.util.criteria.Criterion;
import org.chocosolver.util.criteria.LongCriterion;
import org.chocosolver.util.logger.Logger;
import org.xcsp.parser.entries.XVariables.XVar;

import fr.univartois.cril.approximation.XCSPParserExtension;
import fr.univartois.cril.approximation.core.GroupConstraint;
import fr.univartois.cril.approximation.core.IConstraintGroupSolver;
import fr.univartois.cril.approximation.core.KeepFalsifiedConstraintStrategy;
import fr.univartois.cril.approximation.solver.state.ISolverState;
import fr.univartois.cril.approximation.solver.state.NormalStateSolver;

/**
 * The ApproximationSolverDecorator is a decorator for the Choco solver. It integrates the
 * relaxation mechanism.
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public class ApproximationSolverDecorator

        implements MyISolver, IConstraintGroupSolver, IMonitorSolution, IApproximationSolver {

    /** The context. */
    private SolverContext context;

    /** The solver. */
    private Solver solver;

    /** The model. */
    private Model model;

    /** The group constraints. */
    private List<GroupConstraint> groupConstraints;

    /** Needed to print the last solution found. */
    private final StringBuilder output = new StringBuilder();

    /** The level. */
    protected Level level = Level.COMPET;

    /** The state. */
    private ISolverState state;

    /** The keep falsified. */
    private KeepFalsifiedConstraintStrategy keepFalsified = KeepFalsifiedConstraintStrategy.NEVER;

    /** The solution. */
    private Solution solution;

    /** The cnt steps. */
    private int cntSteps;

    /** The limit steps. */
    private long limitSteps = Long.MAX_VALUE;

    /** The result. */
    private UniverseSolverResult result;

    /** The nb constraints. */
    private int nbConstraints = -1;

    /** The Constant S_INST_IN. */
    private static final String S_INST_IN = "v <instantiation id='sol%s' type='solution' ";

    /** The Constant S_INST_OUT. */
    private static final String S_INST_OUT = "</instantiation>\n";

    /** The Constant S_LIST_IN. */
    private static final String S_LIST_IN = "<list>";

    /** The Constant S_LIST_OUT. */
    private static final String S_LIST_OUT = "</list>";

    /** The Constant S_VALU_IN. */
    private static final String S_VALU_IN = "<values>";

    /** The Constant S_VALU_OUT. */
    private static final String S_VALU_OUT = "</values>";

    /**
     * Creates a new ApproximationSolverDecorator.
     *
     * @param model the model
     */
    public ApproximationSolverDecorator(Model model) {
        this.solver = model.getSolver();
        this.model = model;
        this.groupConstraints = new ArrayList<>();
        solution = new Solution(model);
        solver.plugMonitor((IMonitorSolution) solution::record);
    }

    /** Indicates that the resolution stops on user instruction. */
    protected boolean userinterruption = true;

    /** The normal state. */
    private NormalStateSolver normalState;

    /*
     * (non-Javadoc)
     *
     * @see org.chocosolver.solver.search.loop.learn.ILearnFactory#setNoLearning()
     */
    @Override
    public void setNoLearning() {
        solver.setNoLearning();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.chocosolver.solver.search.loop.monitors.ISearchMonitorFactory#
     * setNoGoodRecordingFromSolutions(org.chocosolver.solver.variables.IntVar[])
     */
    @Override
    public void setNoGoodRecordingFromSolutions(IntVar... vars) {
        solver.setNoGoodRecordingFromSolutions(vars);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.chocosolver.solver.search.loop.move.IMoveFactory#setDFS()
     */
    @Override
    public void setDFS() {
        solver.setDFS();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.chocosolver.solver.search.loop.learn.ILearnFactory#setLearningSignedClauses()
     */
    @Override
    public void setLearningSignedClauses() {
        solver.setLearningSignedClauses();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.chocosolver.solver.search.loop.move.IMoveFactory#setLDS(int)
     */
    @Override
    public void setLDS(int discrepancy) {
        solver.setLDS(discrepancy);
    }

    /**
     * Gets the time to best solution.
     *
     * @return the time to best solution
     */
    public float getTimeToBestSolution() {
        return solver.getTimeToBestSolution();
    }

    /**
     * Prints the version.
     */
    public void printVersion() {
        solver.printVersion();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.chocosolver.solver.search.loop.monitors.ISearchMonitorFactory#
     * setNoGoodRecordingFromRestarts()
     */
    @Override
    public void setNoGoodRecordingFromRestarts() {
        solver.setNoGoodRecordingFromRestarts();
    }

    /**
     * Prints the features.
     */
    public void printFeatures() {
        solver.printFeatures();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.chocosolver.solver.search.loop.move.IMoveFactory#setDDS(int)
     */
    @Override
    public void setDDS(int discrepancy) {
        solver.setDDS(discrepancy);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.chocosolver.solver.search.loop.monitors.ISearchMonitorFactory#limitSearch(org.
     * chocosolver.util.criteria.Criterion)
     */
    @Override
    public void limitSearch(Criterion aStopCriterion) {
        solver.limitSearch(aStopCriterion);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.chocosolver.solver.search.loop.move.IMoveFactory#setHBFS(double, double,
     * long)
     */
    @Override
    public void setHBFS(double a, double b, long N) {
        solver.setHBFS(a, b, N);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.chocosolver.solver.search.IResolutionHelper#findSolution(org.chocosolver.util.
     * criteria.Criterion[])
     */
    @Override
    public Solution findSolution(Criterion... stop) {
        return solver.findSolution(stop);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.chocosolver.solver.search.loop.monitors.ISearchMonitorFactory#limitNode(long)
     */
    @Override
    public void limitNode(long limit) {
        solver.limitNode(limit);
    }

    /**
     * Prints the short features.
     */
    public void printShortFeatures() {
        solver.printShortFeatures();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.chocosolver.solver.search.loop.monitors.ISearchMonitorFactory#limitFail(long)
     */
    @Override
    public void limitFail(long limit) {
        solver.limitFail(limit);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.chocosolver.solver.search.loop.move.IMoveFactory#setRestarts(org.chocosolver.
     * util.criteria.LongCriterion, org.chocosolver.solver.search.restart.ICutoff, int)
     */
    @Override
    public void setRestarts(LongCriterion restartCriterion, ICutoff restartStrategy,
            int restartsLimit) {
        solver.setRestarts(restartCriterion, restartStrategy, restartsLimit);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.chocosolver.solver.search.loop.monitors.ISearchMonitorFactory#limitBacktrack(
     * long)
     */
    @Override
    public void limitBacktrack(long limit) {
        solver.limitBacktrack(limit);
    }

    /**
     * Prints the statistics.
     */
    public void printStatistics() {
        solver.printStatistics();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.chocosolver.solver.search.loop.monitors.ISearchMonitorFactory#limitSolution(
     * long)
     */
    @Override
    public void limitSolution(long limit) {
        solver.limitSolution(limit);
    }

    /**
     * To one line string.
     *
     * @return the string
     */
    public String toOneLineString() {
        return solver.toOneLineString();
    }

    /**
     * Prints the short statistics.
     */
    public void printShortStatistics() {
        solver.printShortStatistics();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.chocosolver.solver.search.loop.move.IMoveFactory#setRestarts(org.chocosolver.
     * util.criteria.LongCriterion, org.chocosolver.solver.search.restart.ICutoff, int,
     * boolean)
     */
    @Override
    public void setRestarts(LongCriterion restartCriterion, ICutoff restartStrategy,
            int restartsLimit,
            boolean resetCutoffOnSolution) {
        solver.setRestarts(restartCriterion, restartStrategy, restartsLimit, resetCutoffOnSolution);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.chocosolver.solver.search.loop.monitors.ISearchMonitorFactory#limitRestart(
     * long)
     */
    @Override
    public void limitRestart(long limit) {
        solver.limitRestart(limit);
    }

    /**
     * Prints the CSV statistics.
     */
    public void printCSVStatistics() {
        solver.printCSVStatistics();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.chocosolver.solver.search.loop.monitors.ISearchMonitorFactory#limitTime(long)
     */
    @Override
    public void limitTime(long limit) {
        solver.limitTime(limit);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.chocosolver.solver.search.IResolutionHelper#findAllSolutions(org.chocosolver.
     * util.criteria.Criterion[])
     */
    @Override
    public List<Solution> findAllSolutions(Criterion... stop) {
        return solver.findAllSolutions(stop);
    }

    /**
     * Show statistics.
     */
    public void showStatistics() {
        solver.showStatistics();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.chocosolver.solver.search.loop.monitors.ISearchMonitorFactory#limitTime(java.
     * lang.String)
     */
    @Override
    public void limitTime(String duration) {
        solver.limitTime(duration);
    }

    /**
     * To dimacs string.
     *
     * @return the string
     */
    public String toDimacsString() {
        return solver.toDimacsString();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.chocosolver.solver.search.loop.move.IMoveFactory#setLubyRestart(long,
     * org.chocosolver.solver.search.limits.ICounter, int)
     */
    @Override
    public void setLubyRestart(long scaleFactor, ICounter restartStrategyLimit, int restartLimit) {
        solver.setLubyRestart(scaleFactor, restartStrategyLimit, restartLimit);
    }

    /**
     * Show short statistics.
     */
    public void showShortStatistics() {
        solver.showShortStatistics();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.chocosolver.solver.search.loop.monitors.ISearchMonitorFactory#attach(org.
     * chocosolver.solver.Solution)
     */
    @Override
    public void attach(Solution solution) {
        solver.attach(solution);
    }

    /**
     * To multi line string.
     *
     * @return the string
     */
    public String toMultiLineString() {
        return solver.toMultiLineString();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.chocosolver.solver.search.loop.move.IMoveFactory#setGeometricalRestart(long,
     * double, org.chocosolver.solver.search.limits.ICounter, int)
     */
    @Override
    public void setGeometricalRestart(long base, double geometricalFactor,
            ICounter restartStrategyLimit,
            int restartLimit) {
        solver.setGeometricalRestart(base, geometricalFactor, restartStrategyLimit, restartLimit);
    }

    /**
     * Show short statistics on shutdown.
     */
    public void showShortStatisticsOnShutdown() {
        solver.showShortStatisticsOnShutdown();
    }

    /**
     * Show solutions.
     *
     * @param message the message
     */
    public void showSolutions(IMessage message) {
        solver.showSolutions(message);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.chocosolver.solver.search.IResolutionHelper#streamSolutions(org.chocosolver.
     * util.criteria.Criterion[])
     */
    @Override
    public Stream<Solution> streamSolutions(Criterion... stop) {
        return solver.streamSolutions(stop);
    }

    /**
     * Show solutions.
     */
    public void showSolutions() {
        solver.showSolutions();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.chocosolver.solver.search.loop.move.IMoveFactory#setLinearRestart(long,
     * org.chocosolver.solver.search.limits.ICounter, int)
     */
    @Override
    public void setLinearRestart(long scaleFactor, ICounter restartStrategyLimit,
            int restartLimit) {
        solver.setLinearRestart(scaleFactor, restartStrategyLimit, restartLimit);
    }

    /**
     * Show solutions.
     *
     * @param variables the variables
     */
    public void showSolutions(Variable... variables) {
        solver.showSolutions(variables);
    }

    /**
     * Show decisions.
     *
     * @param message the message
     */
    public void showDecisions(IMessage message) {
        solver.showDecisions(message);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.chocosolver.solver.search.loop.move.IMoveFactory#setConstantRestart(long,
     * org.chocosolver.solver.search.limits.ICounter, int)
     */
    @Override
    public void setConstantRestart(long scaleFactor, ICounter restartStrategyLimit,
            int restartLimit) {
        solver.setConstantRestart(scaleFactor, restartStrategyLimit, restartLimit);
    }

    /**
     * Show decisions.
     */
    public void showDecisions() {
        solver.showDecisions();
    }

    /**
     * To array.
     *
     * @return the number[]
     */
    public Number[] toArray() {
        return solver.toArray();
    }

    /**
     * Show decisions.
     *
     * @param nChars the n chars
     */
    public void showDecisions(int nChars) {
        solver.showDecisions(nChars);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.chocosolver.solver.search.loop.move.IMoveFactory#setRestartOnSolutions()
     */
    @Override
    public void setRestartOnSolutions() {
        solver.setRestartOnSolutions();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.chocosolver.solver.search.IResolutionHelper#findOptimalSolution(org.chocosolver
     * .solver.variables.IntVar, boolean, org.chocosolver.util.criteria.Criterion[])
     */
    @Override
    public Solution findOptimalSolution(IntVar objective, boolean maximize, Criterion... stop) {
        return solver.findOptimalSolution(objective, maximize, stop);
    }

    /**
     * To CSV.
     *
     * @return the string
     */
    public String toCSV() {
        return solver.toCSV();
    }

    /**
     * Show restarts.
     *
     * @param message the message
     */
    public void showRestarts(IMessage message) {
        solver.showRestarts(message);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.chocosolver.solver.search.loop.move.IMoveFactory#setLNS(org.chocosolver.solver.
     * search.loop.lns.neighbors.INeighbor, org.chocosolver.solver.search.limits.ICounter,
     * org.chocosolver.solver.Solution)
     */
    @Override
    public void setLNS(INeighbor neighbor, ICounter restartCounter, Solution bootstrap) {
        solver.setLNS(neighbor, restartCounter, bootstrap);
    }

    /**
     * Show restarts.
     */
    public void showRestarts() {
        solver.showRestarts();
    }

    /**
     * Show contradiction.
     */
    public void showContradiction() {
        solver.showContradiction();
    }

    /**
     * Throws exception.
     *
     * @param c the c
     * @param v the v
     * @param s the s
     *
     * @throws ContradictionException the contradiction exception
     */
    public void throwsException(ICause c, Variable v, String s) throws ContradictionException {
        solver.throwsException(c, v, s);
    }

    /**
     * Gets the contradiction exception.
     *
     * @return the contradiction exception
     */
    public ContradictionException getContradictionException() {
        return solver.getContradictionException();
    }

    /**
     * Show statistics during resolution.
     *
     * @param f the f
     */
    public void showStatisticsDuringResolution(long f) {
        solver.showStatisticsDuringResolution(f);
    }

    /**
     * Verbose solving.
     *
     * @param frequencyInMilliseconds the frequency in milliseconds
     */
    public void verboseSolving(long frequencyInMilliseconds) {
        solver.verboseSolving(frequencyInMilliseconds);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.chocosolver.solver.search.loop.move.IMoveFactory#setLNS(org.chocosolver.solver.
     * search.loop.lns.neighbors.INeighbor, org.chocosolver.solver.Solution)
     */
    @Override
    public void setLNS(INeighbor neighbor, Solution bootstrap) {
        solver.setLNS(neighbor, bootstrap);
    }

    /**
     * Show dashboard.
     */
    public void showDashboard() {
        solver.showDashboard();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.chocosolver.solver.search.loop.move.IMoveFactory#setLNS(org.chocosolver.solver.
     * search.loop.lns.neighbors.INeighbor, org.chocosolver.solver.search.limits.ICounter)
     */
    @Override
    public void setLNS(INeighbor neighbor, ICounter restartCounter) {
        solver.setLNS(neighbor, restartCounter);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.chocosolver.solver.search.IResolutionHelper#findAllOptimalSolutions(org.
     * chocosolver.solver.variables.IntVar, boolean,
     * org.chocosolver.util.criteria.Criterion[])
     */
    @Override
    public List<Solution> findAllOptimalSolutions(IntVar objective, boolean maximize,
            Criterion... stop) {
        return solver.findAllOptimalSolutions(objective, maximize, stop);
    }

    /**
     * Show dashboard.
     *
     * @param refresh the refresh
     */
    public void showDashboard(long refresh) {
        solver.showDashboard(refresh);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return solver.toString();
    }

    /**
     * Observe propagation.
     *
     * @param po the po
     */
    public void observePropagation(PropagationObserver po) {
        solver.observePropagation(po);
    }

    /**
     * Search loop.
     *
     * @return true, if successful
     */
    public boolean searchLoop() {
        return solver.searchLoop();
    }

    /**
     * Profile propagation.
     *
     * @return the propagation profiler
     */
    public PropagationProfiler profilePropagation() {
        return solver.profilePropagation();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.chocosolver.solver.search.loop.move.IMoveFactory#setLNS(org.chocosolver.solver.
     * search.loop.lns.neighbors.INeighbor)
     */
    @Override
    public void setLNS(INeighbor neighbor) {
        solver.setLNS(neighbor);
    }

    /**
     * Observe solving.
     *
     * @return the solving statistics flow
     */
    public SolvingStatisticsFlow observeSolving() {
        return solver.observeSolving();
    }

    /**
     * Output search tree to graphviz.
     *
     * @param gvFilename the gv filename
     *
     * @return the closeable
     */
    public Closeable outputSearchTreeToGraphviz(String gvFilename) {
        return solver.outputSearchTreeToGraphviz(gvFilename);
    }

    /**
     * Output search tree to gephi.
     *
     * @param gexfFilename the gexf filename
     *
     * @return the closeable
     */
    public Closeable outputSearchTreeToGephi(String gexfFilename) {
        return solver.outputSearchTreeToGephi(gexfFilename);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.chocosolver.solver.search.IResolutionHelper#streamOptimalSolutions(org.
     * chocosolver.solver.variables.IntVar, boolean,
     * org.chocosolver.util.criteria.Criterion[])
     */
    @Override
    public Stream<Solution> streamOptimalSolutions(IntVar objective, boolean maximize,
            Criterion... stop) {
        return solver.streamOptimalSolutions(objective, maximize, stop);
    }

    /**
     * Output search tree to CP profiler.
     *
     * @param domain the domain
     *
     * @return the closeable
     */
    public Closeable outputSearchTreeToCPProfiler(boolean domain) {
        return solver.outputSearchTreeToCPProfiler(domain);
    }

    /**
     * Constraint network to gephi.
     *
     * @param gexfFilename the gexf filename
     */
    public void constraintNetworkToGephi(String gexfFilename) {
        solver.constraintNetworkToGephi(gexfFilename);
    }

    /**
     * Builds the distance matrix.
     *
     * @param solutions the solutions
     * @param p the p
     *
     * @return the double[][]
     */
    public double[][] buildDistanceMatrix(List<Solution> solutions, int p) {
        return solver.buildDistanceMatrix(solutions, p);
    }

    /**
     * Builds the difference matrix.
     *
     * @param solutions the solutions
     *
     * @return the double[][]
     */
    public double[][] buildDifferenceMatrix(List<Solution> solutions) {
        return solver.buildDifferenceMatrix(solutions);
    }

    /**
     * Preprocessing.
     *
     * @param timeLimitInMS the time limit in MS
     */
    public void preprocessing(long timeLimitInMS) {
        solver.preprocessing(timeLimitInMS);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.chocosolver.solver.search.IResolutionHelper#findParetoFront(org.chocosolver.
     * solver.variables.IntVar[], boolean, org.chocosolver.util.criteria.Criterion[])
     */
    @Override
    public List<Solution> findParetoFront(IntVar[] objectives, boolean maximize,
            Criterion... stop) {
        return solver.findParetoFront(objectives, maximize, stop);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.chocosolver.solver.search.IResolutionHelper#findLexOptimalSolution(org.
     * chocosolver.solver.variables.IntVar[], boolean,
     * org.chocosolver.util.criteria.Criterion[])
     */
    @Override
    public Solution findLexOptimalSolution(IntVar[] objectives, boolean maximize,
            Criterion... stop) {
        return solver.findLexOptimalSolution(objectives, maximize, stop);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.chocosolver.solver.search.IResolutionHelper#findOptimalSolutionWithBounds(org.
     * chocosolver.solver.variables.IntVar, java.util.function.Supplier,
     * java.util.function.BiFunction, org.chocosolver.util.criteria.Criterion,
     * java.util.function.IntPredicate, java.lang.Runnable)
     */
    @Override
    public boolean findOptimalSolutionWithBounds(IntVar bounded, Supplier<int[]> bounder,
            BiFunction<int[], int[], int[]> boundsRelaxer, Criterion limitPerAttempt,
            IntPredicate stopCriterion,
            Runnable onSolution) {
        return solver.findOptimalSolutionWithBounds(bounded, bounder, boundsRelaxer,
                limitPerAttempt, stopCriterion,
                onSolution);
    }

    /**
     * Hard reset.
     */
    public void hardReset() {
        solver.hardReset();
    }

    /**
     * Propagate.
     *
     * @throws ContradictionException the contradiction exception
     */
    public void propagate() throws ContradictionException {
        solver.propagate();
    }

    /**
     * Find minimum conflicting set.
     *
     * @param conflictingSet the conflicting set
     *
     * @return the list
     */
    public List<Constraint> findMinimumConflictingSet(List<Constraint> conflictingSet) {
        return solver.findMinimumConflictingSet(conflictingSet);
    }

    /**
     * Restart.
     */
    public void restart() {
        solver.restart();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.chocosolver.solver.search.IResolutionHelper#eachSolutionWithMeasure(java.util.
     * function.BiConsumer, org.chocosolver.util.criteria.Criterion[])
     */
    @Override
    public void eachSolutionWithMeasure(BiConsumer<Solution, IMeasures> cons, Criterion... stop) {
        solver.eachSolutionWithMeasure(cons, stop);
    }

    /**
     * Move forward.
     *
     * @param decision the decision
     *
     * @return true, if successful
     */
    public boolean moveForward(Decision<?> decision) {
        return solver.moveForward(decision);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.chocosolver.solver.search.IResolutionHelper#tableSampling(int, int,
     * double, java.util.Random, org.chocosolver.util.criteria.Criterion[])
     */
    @Override
    public Stream<Solution> tableSampling(int pivot, int nbVariablesInTable, double probaTuple,
            Random random,
            Criterion... criterion) {
        return solver.tableSampling(pivot, nbVariablesInTable, probaTuple, random, criterion);
    }

    /**
     * Move backward.
     *
     * @return true, if successful
     */
    public boolean moveBackward() {
        return solver.moveBackward();
    }

    /**
     * Checks if is solving.
     *
     * @return true, if is solving
     */
    public boolean isSolving() {
        return solver.isSolving();
    }

    /**
     * Gets the model.
     *
     * @return the model
     */
    public Model getModel() {
        return solver.getModel();
    }

    /**
     * Gets the learner.
     *
     * @return the learner
     */
    public Learn getLearner() {
        return solver.getLearner();
    }

    /**
     * Gets the move.
     *
     * @return the move
     */
    public Move getMove() {
        return solver.getMove();
    }

    /**
     * Gets the propagate.
     *
     * @return the propagate
     */
    public Propagate getPropagate() {
        return solver.getPropagate();
    }

    /**
     * Gets the environment.
     *
     * @return the environment
     */
    public IEnvironment getEnvironment() {
        return solver.getEnvironment();
    }

    /**
     * Gets the decision path.
     *
     * @return the decision path
     */
    public DecisionPath getDecisionPath() {
        return solver.getDecisionPath();
    }

    /**
     * Gets the search.
     *
     * @param <V> the value type
     *
     * @return the search
     */
    public <V extends Variable> AbstractStrategy<V> getSearch() {
        return solver.getSearch();
    }

    /**
     * Gets the objective manager.
     *
     * @param <V> the value type
     *
     * @return the objective manager
     */
    @Override
    public <V extends Variable> IObjectiveManager<V> getObjectiveManager() {
        return solver.getObjectiveManager();
    }

    /**
     * Checks if is default search used.
     *
     * @return true, if is default search used
     */
    public boolean isDefaultSearchUsed() {
        return solver.isDefaultSearchUsed();
    }

    /**
     * Checks if is search completed.
     *
     * @return true, if is search completed
     */
    public boolean isSearchCompleted() {
        return solver.isSearchCompleted();
    }

    /**
     * Checks for ended unexpectedly.
     *
     * @return true, if successful
     */
    public boolean hasEndedUnexpectedly() {
        return solver.hasEndedUnexpectedly();
    }

    /**
     * Checks if is stop criterion met.
     *
     * @return true, if is stop criterion met
     */
    public boolean isStopCriterionMet() {
        return solver.isStopCriterionMet();
    }

    /**
     * Gets the search world index.
     *
     * @return the search world index
     */
    public int getSearchWorldIndex() {
        return solver.getSearchWorldIndex();
    }

    /**
     * Gets the measures.
     *
     * @return the measures
     */
    public MeasuresRecorder getMeasures() {
        return solver.getMeasures();
    }

    /**
     * Gets the event observer.
     *
     * @return the event observer
     */
    public AbstractEventObserver getEventObserver() {
        return solver.getEventObserver();
    }

    /**
     * Gets the engine.
     *
     * @return the engine
     */
    public PropagationEngine getEngine() {
        return solver.getEngine();
    }

    /**
     * Checks if is feasible.
     *
     * @return the e sat
     */
    public ESat isFeasible() {
        return solver.isFeasible();
    }

    /**
     * Checks if is satisfied.
     *
     * @return the e sat
     */
    public ESat isSatisfied() {
        return solver.isSatisfied();
    }

    /**
     * Gets the jump to.
     *
     * @return the jump to
     */
    public int getJumpTo() {
        return solver.getJumpTo();
    }

    /**
     * Checks if is learn off.
     *
     * @return true, if is learn off
     */
    public boolean isLearnOff() {
        return solver.isLearnOff();
    }

    /**
     * Sets the learner.
     *
     * @param l the new learner
     */
    public void setLearner(Learn l) {
        solver.setLearner(l);
    }

    /**
     * Sets the move.
     *
     * @param m the new move
     */
    public void setMove(Move... m) {
        solver.setMove(m);
    }

    /**
     * Sets the propagate.
     *
     * @param p the new propagate
     */
    public void setPropagate(Propagate p) {
        solver.setPropagate(p);
    }

    /**
     * Adds the restarter.
     *
     * @param restarter the restarter
     */
    public void addRestarter(AbstractRestart restarter) {
        solver.addRestarter(restarter);
    }

    /**
     * Gets the restarter.
     *
     * @return the restarter
     */
    public AbstractRestart getRestarter() {
        return solver.getRestarter();
    }

    /**
     * Clear restarter.
     */
    public void clearRestarter() {
        solver.clearRestarter();
    }

    /**
     * Sets the objective manager.
     *
     * @param om the new objective manager
     */
    public void setObjectiveManager(IObjectiveManager<?> om) {
        solver.setObjectiveManager(om);
    }

    /**
     * Sets the search.
     *
     * @param strategies the new search
     */
    public void setSearch(AbstractStrategy... strategies) {
        solver.setSearch(strategies);
    }

    /**
     * Sets the event observer.
     *
     * @param explainer the new event observer
     */
    public void setEventObserver(AbstractEventObserver explainer) {
        solver.setEventObserver(explainer);
    }

    /**
     * Sets the engine.
     *
     * @param propagationEngine the new engine
     */
    public void setEngine(PropagationEngine propagationEngine) {
        solver.setEngine(propagationEngine);
    }

    /**
     * Make complete strategy.
     *
     * @param isComplete the is complete
     */
    public void makeCompleteStrategy(boolean isComplete) {
        solver.makeCompleteStrategy(isComplete);
    }

    /**
     * Adds the hint.
     *
     * @param var the var
     * @param val the val
     */
    public void addHint(IntVar var, int val) {
        solver.addHint(var, val);
    }

    /**
     * Removes the hints.
     */
    public void removeHints() {
        solver.removeHints();
    }

    /**
     * Adds the stop criterion.
     *
     * @param criterion the criterion
     */
    @Override
    public void addStopCriterion(Criterion... criterion) {
        solver.addStopCriterion(criterion);
    }

    /**
     * Removes the stop criterion.
     *
     * @param criterion the criterion
     */
    public void removeStopCriterion(Criterion... criterion) {
        solver.removeStopCriterion(criterion);
    }

    /**
     * Removes the all stop criteria.
     */
    public void removeAllStopCriteria() {
        solver.removeAllStopCriteria();
    }

    /**
     * Gets the search monitors.
     *
     * @return the search monitors
     */
    public SearchMonitorList getSearchMonitors() {
        return solver.getSearchMonitors();
    }

    /**
     * Plug monitor.
     *
     * @param sm the sm
     */
    @Override
    public void plugMonitor(ISearchMonitor sm) {
        solver.plugMonitor(sm);
    }

    /**
     * Unplug monitor.
     *
     * @param sm the sm
     */
    public void unplugMonitor(ISearchMonitor sm) {
        solver.unplugMonitor(sm);
    }

    /**
     * On solution.
     *
     * @param r the r
     */
    public void onSolution(Runnable r) {
        solver.onSolution(r);
    }

    /**
     * Unplug all search monitors.
     */
    public void unplugAllSearchMonitors() {
        solver.unplugAllSearchMonitors();
    }

    /**
     * Sets the jump to.
     *
     * @param jto the new jump to
     */
    public void setJumpTo(int jto) {
        solver.setJumpTo(jto);
    }

    /**
     * Default solution.
     *
     * @return the solution
     */
    public Solution defaultSolution() {
        return solver.defaultSolution();
    }

    /**
     * Default solution exists.
     *
     * @return true, if successful
     */
    public boolean defaultSolutionExists() {
        return solver.defaultSolutionExists();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.chocosolver.solver.ISelf#ref()
     */
    @Override
    public Solver ref() {
        return solver.ref();
    }

    /**
     * Gets the model name.
     *
     * @return the model name
     */
    public String getModelName() {
        return solver.getModelName();
    }

    /**
     * Gets the timestamp.
     *
     * @return the timestamp
     */
    public long getTimestamp() {
        return solver.getTimestamp();
    }

    /**
     * Gets the time count.
     *
     * @return the time count
     */
    public float getTimeCount() {
        return solver.getTimeCount();
    }

    /**
     * Gets the time count in nano seconds.
     *
     * @return the time count in nano seconds
     */
    public long getTimeCountInNanoSeconds() {
        return solver.getTimeCountInNanoSeconds();
    }

    /**
     * Gets the time to best solution in nano seconds.
     *
     * @return the time to best solution in nano seconds
     */
    public long getTimeToBestSolutionInNanoSeconds() {
        return solver.getTimeToBestSolutionInNanoSeconds();
    }

    /**
     * Gets the reading time count in nano seconds.
     *
     * @return the reading time count in nano seconds
     */
    public long getReadingTimeCountInNanoSeconds() {
        return solver.getReadingTimeCountInNanoSeconds();
    }

    /**
     * Gets the reading time count.
     *
     * @return the reading time count
     */
    public float getReadingTimeCount() {
        return solver.getReadingTimeCount();
    }

    /**
     * Gets the node count.
     *
     * @return the node count
     */
    public long getNodeCount() {
        return solver.getNodeCount();
    }

    /**
     * Gets the back track count.
     *
     * @return the back track count
     */
    public long getBackTrackCount() {
        return solver.getBackTrackCount();
    }

    /**
     * Gets the backjump count.
     *
     * @return the backjump count
     */
    public long getBackjumpCount() {
        return solver.getBackjumpCount();
    }

    /**
     * Gets the fail count.
     *
     * @return the fail count
     */
    public long getFailCount() {
        return solver.getFailCount();
    }

    /**
     * Gets the fixpoint count.
     *
     * @return the fixpoint count
     */
    public long getFixpointCount() {
        return solver.getFixpointCount();
    }

    /**
     * Gets the restart count.
     *
     * @return the restart count
     */
    public long getRestartCount() {
        return solver.getRestartCount();
    }

    /**
     * Gets the solution count.
     *
     * @return the solution count
     */
    public long getSolutionCount() {
        return solver.getSolutionCount();
    }

    /**
     * Gets the decision count.
     *
     * @return the decision count
     */
    public long getDecisionCount() {
        return solver.getDecisionCount();
    }

    /**
     * Gets the max depth.
     *
     * @return the max depth
     */
    public long getMaxDepth() {
        return solver.getMaxDepth();
    }

    /**
     * Gets the current depth.
     *
     * @return the current depth
     */
    public long getCurrentDepth() {
        return solver.getCurrentDepth();
    }

    /**
     * Checks for objective.
     *
     * @return true, if successful
     */
    public boolean hasObjective() {
        return solver.hasObjective();
    }

    /**
     * Checks if is objective optimal.
     *
     * @return true, if is objective optimal
     */
    public boolean isObjectiveOptimal() {
        return solver.isObjectiveOptimal();
    }

    /**
     * Gets the best solution value.
     *
     * @return the best solution value
     */
    public Number getBestSolutionValue() {
        return solver.getBestSolutionValue();
    }

    /**
     * Gets the search state.
     *
     * @return the search state
     */
    public SearchState getSearchState() {
        return solver.getSearchState();
    }

    /**
     * Gets the bounds manager.
     *
     * @return the bounds manager
     */
    public IBoundsManager getBoundsManager() {
        return solver.getBoundsManager();
    }

    /**
     * Get the current logger.
     *
     * @return the logger
     */
    public Logger log() {
        return solver.log();
    }

    /**
     * Log with ANSI.
     *
     * @param ansi the ansi
     */
    public void logWithANSI(boolean ansi) {
        solver.logWithANSI(ansi);
    }

    /**
     * Reset the solver.
     */
    public void reset() {
        solver.removeHints();
        solver.reset();
        userinterruption = true;
    }

    /**
     * Returns the number of variables.
     *
     * @return the number of variables
     */
    public int nVariables() {
        return model.getNbVars();
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.core.IConstraintGroupSolver#nConstraints()
     */
    @Override
    public int nConstraints() {
        if (nbConstraints == -1) {
            nbConstraints = 0;
            for (Constraint c : model.getCstrs()) {
                if (c.isEnabled()) {
                    nbConstraints++;
                }
            }
        }
        return nbConstraints;
    }

    /**
     * Sets the timeout.
     *
     * @param seconds the new timeout
     */
    public void setTimeout(long seconds) {
        solver.limitTime(seconds * 1000);
    }

    /**
     * Sets the timeout ms.
     *
     * @param mseconds the new timeout ms
     */
    public void setTimeoutMs(long mseconds) {
        solver.limitTime(mseconds);
    }

    /**
     * Sets the verbosity level by attaching a {@code VerboseSolving} monitor to the
     * solver.
     * The refresh rate is dynamically adjusted based on the specified verbosity level,
     * with an update interval of {@code 1000ms / level}.
     *
     * @param level the verbosity level (must be greater than 0 to enable logging)
     */
    public void setVerbosity(int level) {
        if (level > 0) {
            solver.plugMonitor(new VerboseSolving(solver, 1000 / level));
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.solver.IApproximationSolver#solve()
     */
    @Override
    public UniverseSolverResult solve() {
        cntSteps = 0;
        this.state = getInitialState();
        state.resetLimitSolver();
        result = state.solve();
        while (result == UniverseSolverResult.UNKNOWN && !this.state.isTimeout()
               && cntSteps < limitSteps) {
            cntSteps++;
            state = state.nextState();
            System.out.println("Start new state: " + this.state);
            reset();
            state.resetLimitSolver();
            result = state.solve();
            while (result == UniverseSolverResult.SATISFIABLE
                   && !this.state.isSafe()
                   && !this.state.isTimeout()) {
                reset();
                for (IntVar var : solution.retrieveIntVars(true)) {
                    if (var == normalState.om.getObjective()) {
                        continue;
                    }

                    solver.addHint(var, solution.getIntVal(var));
                }
                keepFalsified.checkConstraints(solver.getModel());
                state = state.previousState();
                state.resetLimitSolver();
                System.out.println("change to previous state: " + this.state);
                result = state.solveStarter();
                System.out.println(result + " after state.solve()");
            }
            System.out.println(result + " after while");
        }
        System.out.println(result + " before end");
        if (this.state.isTimeout() || cntSteps >= limitSteps) {
            System.out.println("We reset the solver and restore all constraints.");
            reset();
            var old = this.state;
            this.state = this.state.previousState();
            while (old != this.state) {
                old = this.state;
                this.state = this.state.previousState();
                result = UniverseSolverResult.UNKNOWN;
            }
        }
        return result;
    }

    /**
     * Gets the initial state.
     *
     * @return the initial state
     */
    private ISolverState getInitialState() {
        normalState = new NormalStateSolver(solver, context, this);
        return normalState;
    }

    /**
     * Solve.
     *
     * @param filename the filename
     *
     * @return the universe solver result
     */
    public UniverseSolverResult solve(String filename) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * fr.univartois.cril.approximation.core.IConstraintGroupSolver#getConstraint(int)
     */
    @Override
    public Constraint getConstraint(int index) {
        return model.getCstrs()[index];
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.core.IConstraintGroupSolver#getGroups()
     */
    @Override
    public List<GroupConstraint> getGroups() {
        if (this.groupConstraints.isEmpty()) {
            int nbGroups = Constraint.currentGroup;
            this.groupConstraints = new ArrayList<>(Collections.nCopies(nbGroups, null));
            for (int i = 0; i < nConstraints(); i++) {
                Constraint c = model.getCstrs()[i];
                int group = c.getGroupId();
                if (this.groupConstraints.get(group) == null) {
                    this.groupConstraints.set(group, new GroupConstraint(group));
                }
                this.groupConstraints.get(group).add(c);
            }
        }
        return groupConstraints;
    }

    /**
     * Final out put.
     *
     * @param solver the solver
     */
    private void finalOutPut(Solver solver) {
        Logger log = solver.log().bold();
        if (solver.getSolutionCount() > 0 && state instanceof NormalStateSolver) {
            log = log.green();
            if (solver.getObjectiveManager().isOptimization() && !userinterruption) {
                output.insert(0, "s OPTIMUM FOUND\n");
            } else {
                output.insert(0, "s SATISFIABLE\n");
            }
        } else if (!userinterruption && result == UniverseSolverResult.UNSATISFIABLE
                   && state instanceof NormalStateSolver) {
            output.insert(0, "s UNSATISFIABLE\n");
            log = log.red();
        } else {
            output.insert(0, "s UNKNOWN\n");
            log = log.black();
        }
        if (level.isLoggable(Level.COMPET)) {
            output.append("d FOUND SOLUTIONS ").append(solver.getSolutionCount()).append("\n");
            log.println(output.toString());
        }
        log.reset();
        if (level.is(Level.RESANA)) {
            solver.log().printf(java.util.Locale.US, "s %s %.1f\n", !userinterruption ? "T" : "S",
                    solver.getTimeCount());
        }
        if (level.is(Level.JSON)) {
            solver.log().printf(Locale.US,
                    "\n\t],\n\t\"exit\":{\"time\":%.1f, "
                                           + "\"bound\":%d, \"nodes\":%d, \"failures\":%d, \"restarts\":%d, \"status\":\"%s\"}\n}",
                    solver.getTimeCount(),
                    solver.getObjectiveManager().isOptimization() ? solver.getObjectiveManager()
                            .getBestSolutionValue().intValue() : solver.getSolutionCount(),
                    solver.getNodeCount(), solver.getFailCount(), solver.getRestartCount(),
                    solver.getSearchState());
        }
        if (level.is(Level.IRACE)) {
            solver.log().printf(Locale.US, "%d %d",
                    solver.getObjectiveManager()
                            .isOptimization() ? (ResolutionPolicy.MAXIMIZE
                                    .equals(solver.getObjectiveManager().getPolicy()) ? -1 : 1)
                                                * solver.getObjectiveManager()
                                                        .getBestSolutionValue()
                                                        .intValue() : -solver.getSolutionCount(),
                    !userinterruption ? (int) Math.ceil(solver.getTimeCount()) : Integer.MAX_VALUE);
        }
        if (level.isLoggable(Level.INFO)) {
            solver.log().bold().white().printf("%s \n", solver.getMeasures().toOneLineString());
        }
        // if (csv) {
        // solver.printCSVStatistics();
        // }
        // if (cs) {
        // try {
        // new SolutionChecker(true, instance, new
        // ByteArrayInputStream(output.toString().getBytes()));
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // }
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.core.IConstraintGroupSolver#getGroup(int)
     */
    @Override
    public GroupConstraint getGroup(int index) {
        return this.getGroups().get(index);
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.core.IConstraintGroupSolver#nGroups()
     */
    @Override
    public int nGroups() {
        return this.getGroups().size();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * fr.univartois.cril.approximation.solver.IApproximationSolver#displaySolution(org.
     * chocosolver.parser.xcsp.XCSP)
     */
    @Override
    public void displaySolution(XCSP xcsp) {
        if (state != null) {
            finalOutPut(solver);
            if (solution.exists()) {
                var map = (new XCSPParserExtension(xcsp.parsers[0])).getVarsOfProblem();
                System.out.println(printSolution(false, map));
            }
        } else {
            System.out.println("s UNKNOWN");
        }
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
     * @see org.chocosolver.solver.search.loop.monitors.IMonitorSolution#onSolution()
     */
    @Override
    public void onSolution() {
        // TODO Auto-generated method stub
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.core.IConstraintGroupSolver#getConstraints()
     */
    @Override
    public List<Constraint> getConstraints() {
        return List.of(model.getCstrs());
    }

    /**
     * Checks if is userinterruption.
     *
     * @return true, if is userinterruption
     */
    public boolean isUserinterruption() {
        return userinterruption;
    }

    /**
     * Sets the user interruption.
     *
     * @param b the new user interruption
     */
    public void setUserInterruption(boolean b) {
        userinterruption = b;
    }

    /**
     * Gets the solution.
     *
     * @return the solution
     */
    public Solution getSolution() {
        return solution;
    }

    /**
     * Gets the limit steps.
     *
     * @return the limitSteps
     */
    public long getLimitSteps() {
        return limitSteps;
    }

    /**
     * Limit steps.
     *
     * @param limitSteps the limitSteps to set
     */
    public void limitSteps(long limitSteps) {
        this.limitSteps = limitSteps;
    }

    /**
     * Gets the current step.
     *
     * @return the current step
     */
    public int getCurrentStep() {
        return Math.max(cntSteps, 1);
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.solver.IApproximationSolver#restoreSolution()
     */
    @Override
    public void restoreSolution() {
        try {
            if (solution.exists()) {
                solution.restore();
            }
        } catch (ContradictionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Prints the solution.
     *
     * @param format the format
     * @param map the map
     *
     * @return the string
     */
    public String printSolution(boolean format, HashMap<XVar, IntVar> map) {
        StringBuilder buffer = new StringBuilder();
        var ovars = new ArrayList<>(map.values());
        ovars.sort(IntVar::compareTo);

        buffer.append(String.format(S_INST_IN, model.getSolver().getSolutionCount()));
        if (model.getSolver().hasObjective()) {
            buffer.append("cost='").append(solver.getObjectiveManager().getBestSolutionValue())
                    .append("' ");
        }
        buffer.append(">");
        buffer.append(format ? "\nv \t" : "").append(S_LIST_IN);
        // list variables
        ovars.forEach(ovar -> buffer.append(ovar.getName()).append(' '));
        buffer.append(S_LIST_OUT).append(format ? "\nv \t" : "").append(S_VALU_IN);
        ovars.forEach(ovar -> {
            buffer.append(solution.getIntVal(ovar)).append(' ');
        });
        buffer.append(S_VALU_OUT).append(format ? "\nv " : "").append(S_INST_OUT);
        return buffer.toString();
    }

    /**
     * Gives the context of this ApproximationSolverDecorator.
     *
     * @return This ApproximationSolverDecorator's context.
     */
    public SolverContext getContext() {
        return context;
    }

    /**
     * Sets this ApproximationSolverDecorator's context.
     *
     * @param context The context to set.
     */
    public void setContext(SolverContext context) {
        this.context = context;
    }

}
