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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
import org.chocosolver.parser.xcsp.XCSPParser;
import org.chocosolver.solver.ICause;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.ParallelPortfolio;
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
import org.chocosolver.solver.search.strategy.BlackBoxConfigurator;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.search.strategy.SearchParams;
import org.chocosolver.solver.search.strategy.decision.Decision;
import org.chocosolver.solver.search.strategy.decision.DecisionPath;
import org.chocosolver.solver.search.strategy.strategy.AbstractStrategy;
import org.chocosolver.solver.trace.IMessage;
import org.chocosolver.solver.trace.VerboseSolving;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.RealVar;
import org.chocosolver.solver.variables.Variable;
import org.chocosolver.util.ESat;
import org.chocosolver.util.criteria.Criterion;
import org.chocosolver.util.criteria.LongCriterion;
import org.chocosolver.util.logger.Logger;
import org.chocosolver.util.tools.VariableUtils;

import fr.univartois.cril.approximation.core.GroupConstraint;
import fr.univartois.cril.approximation.core.IConstraintGroupSolver;
import fr.univartois.cril.approximation.core.KeepFalsifiedConstraintStrategy;
import fr.univartois.cril.approximation.core.KeepNoGoodStrategy;
import fr.univartois.cril.approximation.solver.state.ISolverState;
import fr.univartois.cril.approximation.solver.state.NormalStateSolver;
import gnu.trove.set.hash.THashSet;

/**
 * The ApproximationSolverDecorator
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public class ApproximationSolverDecorator implements IConstraintGroupSolver, IMonitorSolution {

	private Solver solver;

	private Model model;

	private List<GroupConstraint> groupConstraints;

	/**
	 * Needed to print the last solution found
	 */
	private final StringBuilder output = new StringBuilder();

	protected Level level = Level.COMPET;

	private ISolverState state;

	private KeepNoGoodStrategy keepNogood = KeepNoGoodStrategy.ALWAYS;

	private KeepFalsifiedConstraintStrategy keepFalsified = KeepFalsifiedConstraintStrategy.NEVER;

	private Solution solution;

	/**
	 * Creates a new ApproximationSolverDecorator.
	 */
	public ApproximationSolverDecorator(Model model) {
		this.solver = model.getSolver();
		this.model = model;
		this.groupConstraints = new ArrayList<>();
		solution = new Solution(model);
		solver.plugMonitor((IMonitorSolution) solution::record);
	}

	/**
	 * Indicates that the resolution stops on user instruction
	 */
	protected boolean userinterruption = true;

	public void setNoLearning() {
		solver.setNoLearning();
	}

	public void setNoGoodRecordingFromSolutions(IntVar... vars) {
		solver.setNoGoodRecordingFromSolutions(vars);
	}

	public void setDFS() {
		solver.setDFS();
	}

	public void setLearningSignedClauses() {
		solver.setLearningSignedClauses();
	}

	public void setLDS(int discrepancy) {
		solver.setLDS(discrepancy);
	}

	public float getTimeToBestSolution() {
		return solver.getTimeToBestSolution();
	}

	public void printVersion() {
		solver.printVersion();
	}

	public void setNoGoodRecordingFromRestarts() {
		solver.setNoGoodRecordingFromRestarts();
	}

	public void printFeatures() {
		solver.printFeatures();
	}

	public void setDDS(int discrepancy) {
		solver.setDDS(discrepancy);
	}

	public void limitSearch(Criterion aStopCriterion) {
		solver.limitSearch(aStopCriterion);
	}

	public void setHBFS(double a, double b, long N) {
		solver.setHBFS(a, b, N);
	}

	public Solution findSolution(Criterion... stop) {
		return solver.findSolution(stop);
	}

	public void limitNode(long limit) {
		solver.limitNode(limit);
	}

	public void printShortFeatures() {
		solver.printShortFeatures();
	}

	public int hashCode() {
		return solver.hashCode();
	}

	public void limitFail(long limit) {
		solver.limitFail(limit);
	}

	public void setRestarts(LongCriterion restartCriterion, ICutoff restartStrategy, int restartsLimit) {
		solver.setRestarts(restartCriterion, restartStrategy, restartsLimit);
	}

	public void limitBacktrack(long limit) {
		solver.limitBacktrack(limit);
	}

	public void printStatistics() {
		solver.printStatistics();
	}

	public void limitSolution(long limit) {
		solver.limitSolution(limit);
	}

	public String toOneLineString() {
		return solver.toOneLineString();
	}

	public void printShortStatistics() {
		solver.printShortStatistics();
	}

	public void setRestarts(LongCriterion restartCriterion, ICutoff restartStrategy, int restartsLimit,
			boolean resetCutoffOnSolution) {
		solver.setRestarts(restartCriterion, restartStrategy, restartsLimit, resetCutoffOnSolution);
	}

	public void limitRestart(long limit) {
		solver.limitRestart(limit);
	}

	public void printCSVStatistics() {
		solver.printCSVStatistics();
	}

	public void limitTime(long limit) {
		solver.limitTime(limit);
	}

	public List<Solution> findAllSolutions(Criterion... stop) {
		return solver.findAllSolutions(stop);
	}

	public void showStatistics() {
		solver.showStatistics();
	}

	public boolean equals(Object obj) {
		return solver.equals(obj);
	}

	public void limitTime(String duration) {
		solver.limitTime(duration);
	}

	public String toDimacsString() {
		return solver.toDimacsString();
	}

	public void setLubyRestart(long scaleFactor, ICounter restartStrategyLimit, int restartLimit) {
		solver.setLubyRestart(scaleFactor, restartStrategyLimit, restartLimit);
	}

	public void showShortStatistics() {
		solver.showShortStatistics();
	}

	public void attach(Solution solution) {
		solver.attach(solution);
	}

	public String toMultiLineString() {
		return solver.toMultiLineString();
	}

	public void setGeometricalRestart(long base, double geometricalFactor, ICounter restartStrategyLimit,
			int restartLimit) {
		solver.setGeometricalRestart(base, geometricalFactor, restartStrategyLimit, restartLimit);
	}

	public void showShortStatisticsOnShutdown() {
		solver.showShortStatisticsOnShutdown();
	}

	public void showSolutions(IMessage message) {
		solver.showSolutions(message);
	}

	public Stream<Solution> streamSolutions(Criterion... stop) {
		return solver.streamSolutions(stop);
	}

	public void showSolutions() {
		solver.showSolutions();
	}

	public void setLinearRestart(long scaleFactor, ICounter restartStrategyLimit, int restartLimit) {
		solver.setLinearRestart(scaleFactor, restartStrategyLimit, restartLimit);
	}

	public void showSolutions(Variable... variables) {
		solver.showSolutions(variables);
	}

	public void showDecisions(IMessage message) {
		solver.showDecisions(message);
	}

	public void setConstantRestart(long scaleFactor, ICounter restartStrategyLimit, int restartLimit) {
		solver.setConstantRestart(scaleFactor, restartStrategyLimit, restartLimit);
	}

	public void showDecisions() {
		solver.showDecisions();
	}

	public Number[] toArray() {
		return solver.toArray();
	}

	public void showDecisions(int nChars) {
		solver.showDecisions(nChars);
	}

	public void setRestartOnSolutions() {
		solver.setRestartOnSolutions();
	}

	public Solution findOptimalSolution(IntVar objective, boolean maximize, Criterion... stop) {
		return solver.findOptimalSolution(objective, maximize, stop);
	}

	public String toCSV() {
		return solver.toCSV();
	}

	public void showRestarts(IMessage message) {
		solver.showRestarts(message);
	}

	public void setLNS(INeighbor neighbor, ICounter restartCounter, Solution bootstrap) {
		solver.setLNS(neighbor, restartCounter, bootstrap);
	}

	public void showRestarts() {
		solver.showRestarts();
	}

	public void showContradiction() {
		solver.showContradiction();
	}

	public void throwsException(ICause c, Variable v, String s) throws ContradictionException {
		solver.throwsException(c, v, s);
	}

	public ContradictionException getContradictionException() {
		return solver.getContradictionException();
	}

	public void showStatisticsDuringResolution(long f) {
		solver.showStatisticsDuringResolution(f);
	}

	public void verboseSolving(long frequencyInMilliseconds) {
		solver.verboseSolving(frequencyInMilliseconds);
	}

	public void setLNS(INeighbor neighbor, Solution bootstrap) {
		solver.setLNS(neighbor, bootstrap);
	}

	public void showDashboard() {
		solver.showDashboard();
	}

	public void setLNS(INeighbor neighbor, ICounter restartCounter) {
		solver.setLNS(neighbor, restartCounter);
	}

	public List<Solution> findAllOptimalSolutions(IntVar objective, boolean maximize, Criterion... stop) {
		return solver.findAllOptimalSolutions(objective, maximize, stop);
	}

	public void showDashboard(long refresh) {
		solver.showDashboard(refresh);
	}

	public String toString() {
		return solver.toString();
	}

	public void observePropagation(PropagationObserver po) {
		solver.observePropagation(po);
	}

	public boolean searchLoop() {
		return solver.searchLoop();
	}

	public PropagationProfiler profilePropagation() {
		return solver.profilePropagation();
	}

	public void setLNS(INeighbor neighbor) {
		solver.setLNS(neighbor);
	}

	public SolvingStatisticsFlow observeSolving() {
		return solver.observeSolving();
	}

	public Closeable outputSearchTreeToGraphviz(String gvFilename) {
		return solver.outputSearchTreeToGraphviz(gvFilename);
	}

	public Closeable outputSearchTreeToGephi(String gexfFilename) {
		return solver.outputSearchTreeToGephi(gexfFilename);
	}

	public Stream<Solution> streamOptimalSolutions(IntVar objective, boolean maximize, Criterion... stop) {
		return solver.streamOptimalSolutions(objective, maximize, stop);
	}

	public Closeable outputSearchTreeToCPProfiler(boolean domain) {
		return solver.outputSearchTreeToCPProfiler(domain);
	}

	public void constraintNetworkToGephi(String gexfFilename) {
		solver.constraintNetworkToGephi(gexfFilename);
	}

	public double[][] buildDistanceMatrix(List<Solution> solutions, int p) {
		return solver.buildDistanceMatrix(solutions, p);
	}

	public double[][] buildDifferenceMatrix(List<Solution> solutions) {
		return solver.buildDifferenceMatrix(solutions);
	}

	public void preprocessing(long timeLimitInMS) {
		solver.preprocessing(timeLimitInMS);
	}

	public List<Solution> findParetoFront(IntVar[] objectives, boolean maximize, Criterion... stop) {
		return solver.findParetoFront(objectives, maximize, stop);
	}

	public Solution findLexOptimalSolution(IntVar[] objectives, boolean maximize, Criterion... stop) {
		return solver.findLexOptimalSolution(objectives, maximize, stop);
	}

	public boolean findOptimalSolutionWithBounds(IntVar bounded, Supplier<int[]> bounder,
			BiFunction<int[], int[], int[]> boundsRelaxer, Criterion limitPerAttempt, IntPredicate stopCriterion,
			Runnable onSolution) {
		return solver.findOptimalSolutionWithBounds(bounded, bounder, boundsRelaxer, limitPerAttempt, stopCriterion,
				onSolution);
	}

	public void hardReset() {
		solver.hardReset();
	}

	public void propagate() throws ContradictionException {
		solver.propagate();
	}

	public List<Constraint> findMinimumConflictingSet(List<Constraint> conflictingSet) {
		return solver.findMinimumConflictingSet(conflictingSet);
	}

	public void restart() {
		solver.restart();
	}

	public void eachSolutionWithMeasure(BiConsumer<Solution, IMeasures> cons, Criterion... stop) {
		solver.eachSolutionWithMeasure(cons, stop);
	}

	public boolean moveForward(Decision<?> decision) {
		return solver.moveForward(decision);
	}

	public Stream<Solution> tableSampling(int pivot, int nbVariablesInTable, double probaTuple, Random random,
			Criterion... criterion) {
		return solver.tableSampling(pivot, nbVariablesInTable, probaTuple, random, criterion);
	}

	public boolean moveBackward() {
		return solver.moveBackward();
	}

	public boolean isSolving() {
		return solver.isSolving();
	}

	public Model getModel() {
		return solver.getModel();
	}

	public Learn getLearner() {
		return solver.getLearner();
	}

	public Move getMove() {
		return solver.getMove();
	}

	public Propagate getPropagate() {
		return solver.getPropagate();
	}

	public IEnvironment getEnvironment() {
		return solver.getEnvironment();
	}

	public DecisionPath getDecisionPath() {
		return solver.getDecisionPath();
	}

	public <V extends Variable> AbstractStrategy<V> getSearch() {
		return solver.getSearch();
	}

	public <V extends Variable> IObjectiveManager<V> getObjectiveManager() {
		return solver.getObjectiveManager();
	}

	public boolean isDefaultSearchUsed() {
		return solver.isDefaultSearchUsed();
	}

	public boolean isSearchCompleted() {
		return solver.isSearchCompleted();
	}

	public boolean hasEndedUnexpectedly() {
		return solver.hasEndedUnexpectedly();
	}

	public boolean isStopCriterionMet() {
		return solver.isStopCriterionMet();
	}

	public int getSearchWorldIndex() {
		return solver.getSearchWorldIndex();
	}

	public MeasuresRecorder getMeasures() {
		return solver.getMeasures();
	}

	public AbstractEventObserver getEventObserver() {
		return solver.getEventObserver();
	}

	public PropagationEngine getEngine() {
		return solver.getEngine();
	}

	public ESat isFeasible() {
		return solver.isFeasible();
	}

	public ESat isSatisfied() {
		return solver.isSatisfied();
	}

	public int getJumpTo() {
		return solver.getJumpTo();
	}

	public boolean isLearnOff() {
		return solver.isLearnOff();
	}

	public void setLearner(Learn l) {
		solver.setLearner(l);
	}

	public void setMove(Move... m) {
		solver.setMove(m);
	}

	public void setPropagate(Propagate p) {
		solver.setPropagate(p);
	}

	public void addRestarter(AbstractRestart restarter) {
		solver.addRestarter(restarter);
	}

	public AbstractRestart getRestarter() {
		return solver.getRestarter();
	}

	public void clearRestarter() {
		solver.clearRestarter();
	}

	public void setObjectiveManager(IObjectiveManager<?> om) {
		solver.setObjectiveManager(om);
	}

	public void setSearch(AbstractStrategy... strategies) {
		solver.setSearch(strategies);
	}

	public void setEventObserver(AbstractEventObserver explainer) {
		solver.setEventObserver(explainer);
	}

	public void setEngine(PropagationEngine propagationEngine) {
		solver.setEngine(propagationEngine);
	}

	public void makeCompleteStrategy(boolean isComplete) {
		solver.makeCompleteStrategy(isComplete);
	}

	public void addHint(IntVar var, int val) {
		solver.addHint(var, val);
	}

	public void removeHints() {
		solver.removeHints();
	}

	public void addStopCriterion(Criterion... criterion) {
		solver.addStopCriterion(criterion);
	}

	public void removeStopCriterion(Criterion... criterion) {
		solver.removeStopCriterion(criterion);
	}

	public void removeAllStopCriteria() {
		solver.removeAllStopCriteria();
	}

	public SearchMonitorList getSearchMonitors() {
		return solver.getSearchMonitors();
	}

	public void plugMonitor(ISearchMonitor sm) {
		solver.plugMonitor(sm);
	}

	public void unplugMonitor(ISearchMonitor sm) {
		solver.unplugMonitor(sm);
	}

	public void onSolution(Runnable r) {
		solver.onSolution(r);
	}

	public void unplugAllSearchMonitors() {
		solver.unplugAllSearchMonitors();
	}

	public void setJumpTo(int jto) {
		solver.setJumpTo(jto);
	}

	public Solution defaultSolution() {
		return solver.defaultSolution();
	}

	public boolean defaultSolutionExists() {
		return solver.defaultSolutionExists();
	}

	public Solver ref() {
		return solver.ref();
	}

	public String getModelName() {
		return solver.getModelName();
	}

	public long getTimestamp() {
		return solver.getTimestamp();
	}

	public float getTimeCount() {
		return solver.getTimeCount();
	}

	public long getTimeCountInNanoSeconds() {
		return solver.getTimeCountInNanoSeconds();
	}

	public long getTimeToBestSolutionInNanoSeconds() {
		return solver.getTimeToBestSolutionInNanoSeconds();
	}

	public long getReadingTimeCountInNanoSeconds() {
		return solver.getReadingTimeCountInNanoSeconds();
	}

	public float getReadingTimeCount() {
		return solver.getReadingTimeCount();
	}

	public long getNodeCount() {
		return solver.getNodeCount();
	}

	public long getBackTrackCount() {
		return solver.getBackTrackCount();
	}

	public long getBackjumpCount() {
		return solver.getBackjumpCount();
	}

	public long getFailCount() {
		return solver.getFailCount();
	}

	public long getFixpointCount() {
		return solver.getFixpointCount();
	}

	public long getRestartCount() {
		return solver.getRestartCount();
	}

	public long getSolutionCount() {
		return solver.getSolutionCount();
	}

	public long getDecisionCount() {
		return solver.getDecisionCount();
	}

	public long getMaxDepth() {
		return solver.getMaxDepth();
	}

	public long getCurrentDepth() {
		return solver.getCurrentDepth();
	}

	public boolean hasObjective() {
		return solver.hasObjective();
	}

	public boolean isObjectiveOptimal() {
		return solver.isObjectiveOptimal();
	}

	public Number getBestSolutionValue() {
		return solver.getBestSolutionValue();
	}

	public SearchState getSearchState() {
		return solver.getSearchState();
	}

	public IBoundsManager getBoundsManager() {
		return solver.getBoundsManager();
	}

	public Logger log() {
		return solver.log();
	}

	public void logWithANSI(boolean ansi) {
		solver.logWithANSI(ansi);
	}

	public void reset() {
		solver.removeHints();
		solver.reset();
		userinterruption=true;
	}

	public int nVariables() {
		return model.getNbVars();
	}

	public int nConstraints() {
		int nb = 0;
		for (Constraint c : model.getCstrs()) {
			if (c.isEnabled()) {
				nb++;
			}
		}
		return nb;
	}

	public void setTimeout(long seconds) {
		solver.limitTime(seconds * 1000);
	}

	public void setTimeoutMs(long mseconds) {
		solver.limitTime(mseconds);
	}

	public void setVerbosity(int level) {
		if (level > 0) {
			solver.plugMonitor(new VerboseSolving(solver, 1000 / level));
		}
	}

	public UniverseSolverResult solve() {
		this.state = NormalStateSolver.getInstance();
		state.resetLimitSolver();
		var result = state.solve();
		while (result == UniverseSolverResult.UNKNOWN && !this.state.isTimeout()) {
			state = state.nextState();
			System.out.println("Start new state: " + this.state);
			reset();
			state.resetLimitSolver();
			result = state.solve();
			while (result == UniverseSolverResult.SATISFIABLE && this.state.getNbRemoved() != 0
					&& !this.state.isTimeout()) {
				reset();
				for (IntVar var : solution.retrieveIntVars(true)) {
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
		if (this.state.isTimeout()) {
			result = UniverseSolverResult.UNKNOWN;
		}
		return result;
	}

	public UniverseSolverResult solve(String filename) {
		try {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return UniverseSolverResult.UNKNOWN;
		}
	}

	@Override
	public Constraint getConstraint(int index) {
		return model.getCstrs()[index];
	}

	@Override
	public List<GroupConstraint> getGroups() {
		if (this.groupConstraints.isEmpty()) {

			int nbGroups = Constraint.currentGroup + 1;
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
		return this.groupConstraints;
	}

	private void finalOutPut(Solver solver) {
		Logger log = solver.log().bold();
		if (solver.getSolutionCount() > 0) {
			log = log.green();
			if (solver.getObjectiveManager().isOptimization()) {
				output.insert(0, "s OPTIMUM FOUND\n");
			} else {
				output.insert(0, "s SATISFIABLE\n");
			}
		} else if (!userinterruption) {
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
					solver.getObjectiveManager().isOptimization()
							? solver.getObjectiveManager().getBestSolutionValue().intValue()
							: solver.getSolutionCount(),
					solver.getNodeCount(), solver.getFailCount(), solver.getRestartCount(), solver.getSearchState());
		}
		if (level.is(Level.IRACE)) {
			solver.log().printf(Locale.US, "%d %d",
					solver.getObjectiveManager().isOptimization()
							? (solver.getObjectiveManager().getPolicy().equals(ResolutionPolicy.MAXIMIZE) ? -1 : 1)
									* solver.getObjectiveManager().getBestSolutionValue().intValue()
							: -solver.getSolutionCount(),
					!userinterruption ? (int) Math.ceil(solver.getTimeCount()) : Integer.MAX_VALUE);
		}
		if (level.isLoggable(Level.INFO)) {
			solver.log().bold().white().printf("%s \n", solver.getMeasures().toOneLineString());
		}
//        if (csv) {
//            solver.printCSVStatistics();
//        }
//        if (cs) {
//            try {
//                new SolutionChecker(true, instance, new ByteArrayInputStream(output.toString().getBytes()));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
	}

	@Override
	public GroupConstraint getGroup(int index) {
		return this.getGroups().get(index);
	}

	@Override
	public int nGroups() {
		return this.getGroups().size();
	}

	public void displaySolution() {
		if (state != null) {
			finalOutPut(solver);
		} else {
			System.out.println("s UNKNOWN");
		}

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

	@Override
	public void onSolution() {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Constraint> getConstraints() {
		return List.of(model.getCstrs());
	}

	public boolean isUserinterruption() {
		return userinterruption;
	}

	public void setUserInterruption(boolean b) {
		userinterruption = b;
	}

}
