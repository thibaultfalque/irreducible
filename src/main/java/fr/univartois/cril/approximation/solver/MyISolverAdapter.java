package fr.univartois.cril.approximation.solver;

import java.io.Closeable;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.IntPredicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.chocosolver.memory.IEnvironment;
import org.chocosolver.solver.ICause;
import org.chocosolver.solver.Model;
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
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.Variable;
import org.chocosolver.util.ESat;
import org.chocosolver.util.criteria.Criterion;
import org.chocosolver.util.criteria.LongCriterion;
import org.chocosolver.util.logger.Logger;

public class MyISolverAdapter implements MyISolver {
	
	private Solver adaptee;
	
	public MyISolverAdapter(Solver adaptee) {
		this.adaptee = adaptee;
	}

	@Override
	public Solver ref() {
		return adaptee;
	}

	@Override
	public UniverseSolverResult solve() {
		adaptee.solve();
		var feasible = adaptee.isFeasible();
		if (feasible == ESat.TRUE) {
			return UniverseSolverResult.SATISFIABLE;
		} else if (feasible == ESat.FALSE) {
			return UniverseSolverResult.UNSATISFIABLE;
		} else {
			return UniverseSolverResult.UNKNOWN;
		}
	}

	public void addHint(IntVar var, int val) {
		adaptee.addHint(var, val);
	}

	public void addRestarter(AbstractRestart restarter) {
		adaptee.addRestarter(restarter);
	}

	public void addStopCriterion(Criterion... criterion) {
		adaptee.addStopCriterion(criterion);
	}

	@Override
	public void attach(Solution solution) {
		adaptee.attach(solution);
	}

	public double[][] buildDifferenceMatrix(List<Solution> arg0) {
		return adaptee.buildDifferenceMatrix(arg0);
	}

	public double[][] buildDistanceMatrix(List<Solution> arg0, int arg1) {
		return adaptee.buildDistanceMatrix(arg0, arg1);
	}

	public void clearRestarter() {
		adaptee.clearRestarter();
	}

	public void constraintNetworkToGephi(String gexfFilename) {
		adaptee.constraintNetworkToGephi(gexfFilename);
	}

	public Solution defaultSolution() {
		return adaptee.defaultSolution();
	}

	public boolean defaultSolutionExists() {
		return adaptee.defaultSolutionExists();
	}

	@Override
	public void eachSolutionWithMeasure(BiConsumer<Solution, IMeasures> cons, Criterion... stop) {
		adaptee.eachSolutionWithMeasure(cons, stop);
	}

	@Override
	public boolean equals(Object obj) {
		return adaptee.equals(obj);
	}

	@Override
	public List<Solution> findAllOptimalSolutions(IntVar arg0, boolean arg1, Criterion... arg2) {
		return adaptee.findAllOptimalSolutions(arg0, arg1, arg2);
	}

	@Override
	public List<Solution> findAllSolutions(Criterion... stop) {
		return adaptee.findAllSolutions(stop);
	}

	@Override
	public Solution findLexOptimalSolution(IntVar[] arg0, boolean arg1, Criterion... arg2) {
		return adaptee.findLexOptimalSolution(arg0, arg1, arg2);
	}

	public List<Constraint> findMinimumConflictingSet(List<Constraint> conflictingSet) {
		return adaptee.findMinimumConflictingSet(conflictingSet);
	}

	@Override
	public Solution findOptimalSolution(IntVar objective, boolean maximize, Criterion... stop) {
		return adaptee.findOptimalSolution(objective, maximize, stop);
	}

	@Override
	public boolean findOptimalSolutionWithBounds(IntVar bounded, Supplier<int[]> bounder,
			BiFunction<int[], int[], int[]> boundsRelaxer, Criterion limitPerAttempt, IntPredicate stopCriterion,
			Runnable onSolution) {
		return adaptee.findOptimalSolutionWithBounds(bounded, bounder, boundsRelaxer, limitPerAttempt, stopCriterion,
				onSolution);
	}

	@Override
	public List<Solution> findParetoFront(IntVar[] objectives, boolean maximize, Criterion... stop) {
		return adaptee.findParetoFront(objectives, maximize, stop);
	}

	@Override
	public Solution findSolution(Criterion... stop) {
		return adaptee.findSolution(stop);
	}

	public long getBackTrackCount() {
		return adaptee.getBackTrackCount();
	}

	public long getBackjumpCount() {
		return adaptee.getBackjumpCount();
	}

	public Number getBestSolutionValue() {
		return adaptee.getBestSolutionValue();
	}

	public IBoundsManager getBoundsManager() {
		return adaptee.getBoundsManager();
	}

	public ContradictionException getContradictionException() {
		return adaptee.getContradictionException();
	}

	public long getCurrentDepth() {
		return adaptee.getCurrentDepth();
	}

	public long getDecisionCount() {
		return adaptee.getDecisionCount();
	}

	public DecisionPath getDecisionPath() {
		return adaptee.getDecisionPath();
	}

	public PropagationEngine getEngine() {
		return adaptee.getEngine();
	}

	public IEnvironment getEnvironment() {
		return adaptee.getEnvironment();
	}

	public AbstractEventObserver getEventObserver() {
		return adaptee.getEventObserver();
	}

	public long getFailCount() {
		return adaptee.getFailCount();
	}

	public long getFixpointCount() {
		return adaptee.getFixpointCount();
	}

	public int getJumpTo() {
		return adaptee.getJumpTo();
	}

	public Learn getLearner() {
		return adaptee.getLearner();
	}

	public long getMaxDepth() {
		return adaptee.getMaxDepth();
	}

	public MeasuresRecorder getMeasures() {
		return adaptee.getMeasures();
	}

	public Model getModel() {
		return adaptee.getModel();
	}

	public String getModelName() {
		return adaptee.getModelName();
	}

	public Move getMove() {
		return adaptee.getMove();
	}

	public long getNodeCount() {
		return adaptee.getNodeCount();
	}

	@Override
	public <V extends Variable> IObjectiveManager<V> getObjectiveManager() {
		return adaptee.getObjectiveManager();
	}

	public Propagate getPropagate() {
		return adaptee.getPropagate();
	}

	public float getReadingTimeCount() {
		return adaptee.getReadingTimeCount();
	}

	public long getReadingTimeCountInNanoSeconds() {
		return adaptee.getReadingTimeCountInNanoSeconds();
	}

	public long getRestartCount() {
		return adaptee.getRestartCount();
	}

	public AbstractRestart getRestarter() {
		return adaptee.getRestarter();
	}

	public <V extends Variable> AbstractStrategy<V> getSearch() {
		return adaptee.getSearch();
	}

	public SearchMonitorList getSearchMonitors() {
		return adaptee.getSearchMonitors();
	}

	public SearchState getSearchState() {
		return adaptee.getSearchState();
	}

	public int getSearchWorldIndex() {
		return adaptee.getSearchWorldIndex();
	}

	public long getSolutionCount() {
		return adaptee.getSolutionCount();
	}

	public float getTimeCount() {
		return adaptee.getTimeCount();
	}

	public long getTimeCountInNanoSeconds() {
		return adaptee.getTimeCountInNanoSeconds();
	}

	public float getTimeToBestSolution() {
		return adaptee.getTimeToBestSolution();
	}

	public long getTimeToBestSolutionInNanoSeconds() {
		return adaptee.getTimeToBestSolutionInNanoSeconds();
	}

	public long getTimestamp() {
		return adaptee.getTimestamp();
	}

	public void hardReset() {
		adaptee.hardReset();
	}

	public boolean hasEndedUnexpectedly() {
		return adaptee.hasEndedUnexpectedly();
	}

	public boolean hasObjective() {
		return adaptee.hasObjective();
	}

	@Override
	public int hashCode() {
		return adaptee.hashCode();
	}

	public boolean isDefaultSearchUsed() {
		return adaptee.isDefaultSearchUsed();
	}

	public ESat isFeasible() {
		return adaptee.isFeasible();
	}

	public boolean isLearnOff() {
		return adaptee.isLearnOff();
	}

	public boolean isObjectiveOptimal() {
		return adaptee.isObjectiveOptimal();
	}

	public ESat isSatisfied() {
		return adaptee.isSatisfied();
	}

	public boolean isSearchCompleted() {
		return adaptee.isSearchCompleted();
	}

	public boolean isSolving() {
		return adaptee.isSolving();
	}

	public boolean isStopCriterionMet() {
		return adaptee.isStopCriterionMet();
	}

	@Override
	public void limitBacktrack(long limit) {
		adaptee.limitBacktrack(limit);
	}

	@Override
	public void limitFail(long limit) {
		adaptee.limitFail(limit);
	}

	@Override
	public void limitNode(long limit) {
		adaptee.limitNode(limit);
	}

	@Override
	public void limitRestart(long limit) {
		adaptee.limitRestart(limit);
	}

	@Override
	public void limitSearch(Criterion aStopCriterion) {
		adaptee.limitSearch(aStopCriterion);
	}

	@Override
	public void limitSolution(long limit) {
		adaptee.limitSolution(limit);
	}

	@Override
	public void limitTime(long limit) {
		adaptee.limitTime(limit);
	}

	@Override
	public void limitTime(String duration) {
		adaptee.limitTime(duration);
	}

	public Logger log() {
		return adaptee.log();
	}

	public void logWithANSI(boolean ansi) {
		adaptee.logWithANSI(ansi);
	}

	public void makeCompleteStrategy(boolean isComplete) {
		adaptee.makeCompleteStrategy(isComplete);
	}

	public boolean moveBackward() {
		return adaptee.moveBackward();
	}

	public boolean moveForward(Decision<?> arg0) {
		return adaptee.moveForward(arg0);
	}

	public void observePropagation(PropagationObserver po) {
		adaptee.observePropagation(po);
	}

	public SolvingStatisticsFlow observeSolving() {
		return adaptee.observeSolving();
	}

	public void onSolution(Runnable r) {
		adaptee.onSolution(r);
	}

	public Closeable outputSearchTreeToCPProfiler(boolean domain) {
		return adaptee.outputSearchTreeToCPProfiler(domain);
	}

	public Closeable outputSearchTreeToGephi(String gexfFilename) {
		return adaptee.outputSearchTreeToGephi(gexfFilename);
	}

	public Closeable outputSearchTreeToGraphviz(String gvFilename) {
		return adaptee.outputSearchTreeToGraphviz(gvFilename);
	}

	@Override
	public void plugMonitor(ISearchMonitor sm) {
		adaptee.plugMonitor(sm);
	}

	public void preprocessing(long arg0) {
		adaptee.preprocessing(arg0);
	}

	public void printCSVStatistics() {
		adaptee.printCSVStatistics();
	}

	public void printFeatures() {
		adaptee.printFeatures();
	}

	public void printShortFeatures() {
		adaptee.printShortFeatures();
	}

	public void printShortStatistics() {
		adaptee.printShortStatistics();
	}

	public void printStatistics() {
		adaptee.printStatistics();
	}

	public void printVersion() {
		adaptee.printVersion();
	}

	public PropagationProfiler profilePropagation() {
		return adaptee.profilePropagation();
	}

	public void propagate() throws ContradictionException {
		adaptee.propagate();
	}

	public void removeAllStopCriteria() {
		adaptee.removeAllStopCriteria();
	}

	public void removeHints() {
		adaptee.removeHints();
	}

	public void removeStopCriterion(Criterion... arg0) {
		adaptee.removeStopCriterion(arg0);
	}

	public void reset() {
		adaptee.reset();
	}

	public void restart() {
		adaptee.restart();
	}

	public boolean searchLoop() {
		return adaptee.searchLoop();
	}

	@Override
	public void setConstantRestart(long scaleFactor, ICounter restartStrategyLimit, int restartLimit) {
		adaptee.setConstantRestart(scaleFactor, restartStrategyLimit, restartLimit);
	}

	@Override
	public void setDDS(int discrepancy) {
		adaptee.setDDS(discrepancy);
	}

	@Override
	public void setDFS() {
		adaptee.setDFS();
	}

	public void setEngine(PropagationEngine propagationEngine) {
		adaptee.setEngine(propagationEngine);
	}

	public void setEventObserver(AbstractEventObserver explainer) {
		adaptee.setEventObserver(explainer);
	}

	@Override
	public void setGeometricalRestart(long base, double geometricalFactor, ICounter restartStrategyLimit,
			int restartLimit) {
		adaptee.setGeometricalRestart(base, geometricalFactor, restartStrategyLimit, restartLimit);
	}

	@Override
	public void setHBFS(double a, double b, long N) {
		adaptee.setHBFS(a, b, N);
	}

	public void setJumpTo(int jto) {
		adaptee.setJumpTo(jto);
	}

	@Override
	public void setLDS(int discrepancy) {
		adaptee.setLDS(discrepancy);
	}

	@Override
	public void setLNS(INeighbor neighbor, ICounter restartCounter, Solution bootstrap) {
		adaptee.setLNS(neighbor, restartCounter, bootstrap);
	}

	@Override
	public void setLNS(INeighbor neighbor, ICounter restartCounter) {
		adaptee.setLNS(neighbor, restartCounter);
	}

	@Override
	public void setLNS(INeighbor neighbor, Solution bootstrap) {
		adaptee.setLNS(neighbor, bootstrap);
	}

	@Override
	public void setLNS(INeighbor neighbor) {
		adaptee.setLNS(neighbor);
	}

	public void setLearner(Learn l) {
		adaptee.setLearner(l);
	}

	@Override
	public void setLearningSignedClauses() {
		adaptee.setLearningSignedClauses();
	}

	@Override
	public void setLinearRestart(long scaleFactor, ICounter restartStrategyLimit, int restartLimit) {
		adaptee.setLinearRestart(scaleFactor, restartStrategyLimit, restartLimit);
	}

	@Override
	public void setLubyRestart(long scaleFactor, ICounter restartStrategyLimit, int restartLimit) {
		adaptee.setLubyRestart(scaleFactor, restartStrategyLimit, restartLimit);
	}

	public void setMove(Move... m) {
		adaptee.setMove(m);
	}

	@Override
	public void setNoGoodRecordingFromRestarts() {
		adaptee.setNoGoodRecordingFromRestarts();
	}

	@Override
	public void setNoGoodRecordingFromSolutions(IntVar... vars) {
		adaptee.setNoGoodRecordingFromSolutions(vars);
	}

	@Override
	public void setNoLearning() {
		adaptee.setNoLearning();
	}

	public void setObjectiveManager(IObjectiveManager<?> om) {
		adaptee.setObjectiveManager(om);
	}

	public void setPropagate(Propagate p) {
		adaptee.setPropagate(p);
	}

	@Override
	public void setRestartOnSolutions() {
		adaptee.setRestartOnSolutions();
	}

	@Override
	public void setRestarts(LongCriterion restartCriterion, ICutoff restartStrategy, int restartsLimit,
			boolean resetCutoffOnSolution) {
		adaptee.setRestarts(restartCriterion, restartStrategy, restartsLimit, resetCutoffOnSolution);
	}

	@Override
	public void setRestarts(LongCriterion restartCriterion, ICutoff restartStrategy, int restartsLimit) {
		adaptee.setRestarts(restartCriterion, restartStrategy, restartsLimit);
	}

	public void setSearch(AbstractStrategy... strategies) {
		adaptee.setSearch(strategies);
	}

	public void showContradiction() {
		adaptee.showContradiction();
	}

	public void showDashboard() {
		adaptee.showDashboard();
	}

	public void showDashboard(long refresh) {
		adaptee.showDashboard(refresh);
	}

	public void showDecisions() {
		adaptee.showDecisions();
	}

	public void showDecisions(IMessage message) {
		adaptee.showDecisions(message);
	}

	public void showDecisions(int nChars) {
		adaptee.showDecisions(nChars);
	}

	public void showRestarts() {
		adaptee.showRestarts();
	}

	public void showRestarts(IMessage message) {
		adaptee.showRestarts(message);
	}

	public void showShortStatistics() {
		adaptee.showShortStatistics();
	}

	public void showShortStatisticsOnShutdown() {
		adaptee.showShortStatisticsOnShutdown();
	}

	public void showSolutions() {
		adaptee.showSolutions();
	}

	public void showSolutions(IMessage message) {
		adaptee.showSolutions(message);
	}

	public void showSolutions(Variable... variables) {
		adaptee.showSolutions(variables);
	}

	public void showStatistics() {
		adaptee.showStatistics();
	}

	public void showStatisticsDuringResolution(long f) {
		adaptee.showStatisticsDuringResolution(f);
	}

	@Override
	public Stream<Solution> streamOptimalSolutions(IntVar arg0, boolean arg1, Criterion... arg2) {
		return adaptee.streamOptimalSolutions(arg0, arg1, arg2);
	}

	@Override
	public Stream<Solution> streamSolutions(Criterion... stop) {
		return adaptee.streamSolutions(stop);
	}

	@Override
	public Stream<Solution> tableSampling(int pivot, int nbVariablesInTable, double probaTuple, Random random,
			Criterion... criterion) {
		return adaptee.tableSampling(pivot, nbVariablesInTable, probaTuple, random, criterion);
	}

	public void throwsException(ICause c, Variable v, String s) throws ContradictionException {
		adaptee.throwsException(c, v, s);
	}

	public Number[] toArray() {
		return adaptee.toArray();
	}

	public String toCSV() {
		return adaptee.toCSV();
	}

	public String toDimacsString() {
		return adaptee.toDimacsString();
	}

	public String toMultiLineString() {
		return adaptee.toMultiLineString();
	}

	public String toOneLineString() {
		return adaptee.toOneLineString();
	}

	@Override
	public String toString() {
		return adaptee.toString();
	}

	public void unplugAllSearchMonitors() {
		adaptee.unplugAllSearchMonitors();
	}

	public void unplugMonitor(ISearchMonitor sm) {
		adaptee.unplugMonitor(sm);
	}

	public void verboseSolving(long frequencyInMilliseconds) {
		adaptee.verboseSolving(frequencyInMilliseconds);
	}
}
