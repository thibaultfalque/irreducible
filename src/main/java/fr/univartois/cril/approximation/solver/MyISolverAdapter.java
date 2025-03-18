
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

/**
 * The Class MyISolverAdapter.
 */
public class MyISolverAdapter implements MyISolver {

    /** The adaptee. */
    private Solver adaptee;

    /**
     * Instantiates a new my I solver adapter.
     *
     * @param adaptee the adaptee
     */
    public MyISolverAdapter(Solver adaptee) {
        this.adaptee = adaptee;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.chocosolver.solver.ISelf#ref()
     */
    @Override
    public Solver ref() {
        return adaptee;
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.solver.MyISolver#solve()
     */
    @Override
    public UniverseSolverResult solve() {
        var f = adaptee.solve();
        while (f) {
            f = adaptee.solve();
        }
        var feasible = adaptee.isFeasible();
        if (feasible == ESat.TRUE) {
            return UniverseSolverResult.SATISFIABLE;
        } else if (feasible == ESat.FALSE) {
            return UniverseSolverResult.UNSATISFIABLE;
        } else {
            return UniverseSolverResult.UNKNOWN;
        }
    }

    /**
     * Adds the hint.
     *
     * @param variable the variable
     * @param val the val
     */
    public void addHint(IntVar variable, int val) {
        adaptee.addHint(variable, val);
    }

    /**
     * Adds the restarter.
     *
     * @param restarter the restarter
     */
    public void addRestarter(AbstractRestart restarter) {
        adaptee.addRestarter(restarter);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * fr.univartois.cril.approximation.solver.MyISolver#addStopCriterion(org.chocosolver.
     * util.criteria.Criterion[])
     */
    @Override
    public void addStopCriterion(Criterion... criterion) {
        adaptee.addStopCriterion(criterion);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.chocosolver.solver.search.loop.monitors.ISearchMonitorFactory#attach(org.
     * chocosolver.solver.Solution)
     */
    @Override
    public void attach(Solution solution) {
        adaptee.attach(solution);
    }

    /**
     * Builds the difference matrix.
     *
     * @param solutions the solutions
     *
     * @return the double[][]
     */
    public double[][] buildDifferenceMatrix(List<Solution> solutions) {
        return adaptee.buildDifferenceMatrix(solutions);
    }

    /**
     * Builds the distance matrix.
     *
     * @param solutions the solutions
     * @param d the d
     *
     * @return the double[][]
     */
    public double[][] buildDistanceMatrix(List<Solution> solutions, int d) {
        return adaptee.buildDistanceMatrix(solutions, d);
    }

    /**
     * Clear restarter.
     */
    public void clearRestarter() {
        adaptee.clearRestarter();
    }

    /**
     * Constraint network to gephi.
     *
     * @param gexfFilename the gexf filename
     */
    public void constraintNetworkToGephi(String gexfFilename) {
        adaptee.constraintNetworkToGephi(gexfFilename);
    }

    /**
     * Default solution.
     *
     * @return the solution
     */
    public Solution defaultSolution() {
        return adaptee.defaultSolution();
    }

    /**
     * Default solution exists.
     *
     * @return true, if successful
     */
    public boolean defaultSolutionExists() {
        return adaptee.defaultSolutionExists();
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
        adaptee.eachSolutionWithMeasure(cons, stop);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.chocosolver.solver.search.IResolutionHelper#findAllOptimalSolutions(org.
     * chocosolver.solver.variables.IntVar, boolean,
     * org.chocosolver.util.criteria.Criterion[])
     */
    @Override
    public List<Solution> findAllOptimalSolutions(IntVar variable, boolean b, Criterion... stop) {
        return adaptee.findAllOptimalSolutions(variable, b, stop);
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
        return adaptee.findAllSolutions(stop);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.chocosolver.solver.search.IResolutionHelper#findLexOptimalSolution(org.
     * chocosolver.solver.variables.IntVar[], boolean,
     * org.chocosolver.util.criteria.Criterion[])
     */
    @Override
    public Solution findLexOptimalSolution(IntVar[] arg0, boolean arg1, Criterion... arg2) {
        return adaptee.findLexOptimalSolution(arg0, arg1, arg2);
    }

    /**
     * Find minimum conflicting set.
     *
     * @param conflicting the conflicting set
     *
     * @return the list
     */
    public List<Constraint> findMinimumConflictingSet(List<Constraint> conflicting) {
        return adaptee.findMinimumConflictingSet(conflicting);
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
        return adaptee.findOptimalSolution(objective, maximize, stop);
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
        return adaptee.findOptimalSolutionWithBounds(bounded, bounder, boundsRelaxer,
                limitPerAttempt, stopCriterion,
                onSolution);
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
        return adaptee.findParetoFront(objectives, maximize, stop);
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
        return adaptee.findSolution(stop);
    }

    /**
     * Gets the back track count.
     *
     * @return the back track count
     */
    public long getBackTrackCount() {
        return adaptee.getBackTrackCount();
    }

    /**
     * Gets the backjump count.
     *
     * @return the backjump count
     */
    public long getBackjumpCount() {
        return adaptee.getBackjumpCount();
    }

    /**
     * Gets the best solution value.
     *
     * @return the best solution value
     */
    public Number getBestSolutionValue() {
        return adaptee.getBestSolutionValue();
    }

    /**
     * Gets the bounds manager.
     *
     * @return the bounds manager
     */
    public IBoundsManager getBoundsManager() {
        return adaptee.getBoundsManager();
    }

    /**
     * Gets the contradiction exception.
     *
     * @return the contradiction exception
     */
    public ContradictionException getContradictionException() {
        return adaptee.getContradictionException();
    }

    /**
     * Gets the current depth.
     *
     * @return the current depth
     */
    public long getCurrentDepth() {
        return adaptee.getCurrentDepth();
    }

    /**
     * Gets the decision count.
     *
     * @return the decision count
     */
    public long getDecisionCount() {
        return adaptee.getDecisionCount();
    }

    /**
     * Gets the decision path.
     *
     * @return the decision path
     */
    public DecisionPath getDecisionPath() {
        return adaptee.getDecisionPath();
    }

    /**
     * Gets the engine.
     *
     * @return the engine
     */
    public PropagationEngine getEngine() {
        return adaptee.getEngine();
    }

    /**
     * Gets the environment.
     *
     * @return the environment
     */
    public IEnvironment getEnvironment() {
        return adaptee.getEnvironment();
    }

    /**
     * Gets the event observer.
     *
     * @return the event observer
     */
    public AbstractEventObserver getEventObserver() {
        return adaptee.getEventObserver();
    }

    /**
     * Gets the fail count.
     *
     * @return the fail count
     */
    public long getFailCount() {
        return adaptee.getFailCount();
    }

    /**
     * Gets the fixpoint count.
     *
     * @return the fixpoint count
     */
    public long getFixpointCount() {
        return adaptee.getFixpointCount();
    }

    /**
     * Gets the jump to.
     *
     * @return the jump to
     */
    public int getJumpTo() {
        return adaptee.getJumpTo();
    }

    /**
     * Gets the learner.
     *
     * @return the learner
     */
    public Learn getLearner() {
        return adaptee.getLearner();
    }

    /**
     * Gets the max depth.
     *
     * @return the max depth
     */
    public long getMaxDepth() {
        return adaptee.getMaxDepth();
    }

    /**
     * Gets the measures.
     *
     * @return the measures
     */
    public MeasuresRecorder getMeasures() {
        return adaptee.getMeasures();
    }

    /**
     * Gets the model.
     *
     * @return the model
     */
    public Model getModel() {
        return adaptee.getModel();
    }

    /**
     * Gets the model name.
     *
     * @return the model name
     */
    public String getModelName() {
        return adaptee.getModelName();
    }

    /**
     * Gets the move.
     *
     * @return the move
     */
    public Move getMove() {
        return adaptee.getMove();
    }

    /**
     * Gets the node count.
     *
     * @return the node count
     */
    public long getNodeCount() {
        return adaptee.getNodeCount();
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.solver.MyISolver#getObjectiveManager()
     */
    @Override
    public <V extends Variable> IObjectiveManager<V> getObjectiveManager() {
        return adaptee.getObjectiveManager();
    }

    /**
     * Gets the propagate.
     *
     * @return the propagate
     */
    public Propagate getPropagate() {
        return adaptee.getPropagate();
    }

    /**
     * Gets the reading time count.
     *
     * @return the reading time count
     */
    public float getReadingTimeCount() {
        return adaptee.getReadingTimeCount();
    }

    /**
     * Gets the reading time count in nano seconds.
     *
     * @return the reading time count in nano seconds
     */
    public long getReadingTimeCountInNanoSeconds() {
        return adaptee.getReadingTimeCountInNanoSeconds();
    }

    /**
     * Gets the restart count.
     *
     * @return the restart count
     */
    public long getRestartCount() {
        return adaptee.getRestartCount();
    }

    /**
     * Gets the restarter.
     *
     * @return the restarter
     */
    public AbstractRestart getRestarter() {
        return adaptee.getRestarter();
    }

    /**
     * Gets the search.
     *
     * @param <V> the value type
     *
     * @return the search
     */
    public <V extends Variable> AbstractStrategy<V> getSearch() {
        return adaptee.getSearch();
    }

    /**
     * Gets the search monitors.
     *
     * @return the search monitors
     */
    public SearchMonitorList getSearchMonitors() {
        return adaptee.getSearchMonitors();
    }

    /**
     * Gets the search state.
     *
     * @return the search state
     */
    public SearchState getSearchState() {
        return adaptee.getSearchState();
    }

    /**
     * Gets the search world index.
     *
     * @return the search world index
     */
    public int getSearchWorldIndex() {
        return adaptee.getSearchWorldIndex();
    }

    /**
     * Gets the solution count.
     *
     * @return the solution count
     */
    public long getSolutionCount() {
        return adaptee.getSolutionCount();
    }

    /**
     * Gets the time count.
     *
     * @return the time count
     */
    public float getTimeCount() {
        return adaptee.getTimeCount();
    }

    /**
     * Gets the time count in nano seconds.
     *
     * @return the time count in nano seconds
     */
    public long getTimeCountInNanoSeconds() {
        return adaptee.getTimeCountInNanoSeconds();
    }

    /**
     * Gets the time to best solution.
     *
     * @return the time to best solution
     */
    public float getTimeToBestSolution() {
        return adaptee.getTimeToBestSolution();
    }

    /**
     * Gets the time to best solution in nano seconds.
     *
     * @return the time to best solution in nano seconds
     */
    public long getTimeToBestSolutionInNanoSeconds() {
        return adaptee.getTimeToBestSolutionInNanoSeconds();
    }

    /**
     * Gets the timestamp.
     *
     * @return the timestamp
     */
    public long getTimestamp() {
        return adaptee.getTimestamp();
    }

    /**
     * Hard reset.
     */
    public void hardReset() {
        adaptee.hardReset();
    }

    /**
     * Checks for ended unexpectedly.
     *
     * @return true, if successful
     */
    public boolean hasEndedUnexpectedly() {
        return adaptee.hasEndedUnexpectedly();
    }

    /**
     * Checks for objective.
     *
     * @return true, if successful
     */
    public boolean hasObjective() {
        return adaptee.hasObjective();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return adaptee.hashCode();
    }

    /**
     * Checks if is default search used.
     *
     * @return true, if is default search used
     */
    public boolean isDefaultSearchUsed() {
        return adaptee.isDefaultSearchUsed();
    }

    /**
     * Checks if is feasible.
     *
     * @return the e sat
     */
    public ESat isFeasible() {
        return adaptee.isFeasible();
    }

    /**
     * Checks if is learn off.
     *
     * @return true, if is learn off
     */
    public boolean isLearnOff() {
        return adaptee.isLearnOff();
    }

    /**
     * Checks if is objective optimal.
     *
     * @return true, if is objective optimal
     */
    public boolean isObjectiveOptimal() {
        return adaptee.isObjectiveOptimal();
    }

    /**
     * Checks if is satisfied.
     *
     * @return the e sat
     */
    public ESat isSatisfied() {
        return adaptee.isSatisfied();
    }

    /**
     * Checks if is search completed.
     *
     * @return true, if is search completed
     */
    public boolean isSearchCompleted() {
        return adaptee.isSearchCompleted();
    }

    /**
     * Checks if is solving.
     *
     * @return true, if is solving
     */
    public boolean isSolving() {
        return adaptee.isSolving();
    }

    /**
     * Checks if is stop criterion met.
     *
     * @return true, if is stop criterion met
     */
    public boolean isStopCriterionMet() {
        return adaptee.isStopCriterionMet();
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
        adaptee.limitBacktrack(limit);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.chocosolver.solver.search.loop.monitors.ISearchMonitorFactory#limitFail(long)
     */
    @Override
    public void limitFail(long limit) {
        adaptee.limitFail(limit);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.chocosolver.solver.search.loop.monitors.ISearchMonitorFactory#limitNode(long)
     */
    @Override
    public void limitNode(long limit) {
        adaptee.limitNode(limit);
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
        adaptee.limitRestart(limit);
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
        adaptee.limitSearch(aStopCriterion);
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
        adaptee.limitSolution(limit);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.chocosolver.solver.search.loop.monitors.ISearchMonitorFactory#limitTime(long)
     */
    @Override
    public void limitTime(long limit) {
        adaptee.limitTime(limit);
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
        adaptee.limitTime(duration);
    }

    /**
     * Log.
     *
     * @return the logger
     */
    public Logger log() {
        return adaptee.log();
    }

    /**
     * Log with ANSI.
     *
     * @param ansi the ansi
     */
    public void logWithANSI(boolean ansi) {
        adaptee.logWithANSI(ansi);
    }

    /**
     * Make complete strategy.
     *
     * @param isComplete the is complete
     */
    public void makeCompleteStrategy(boolean isComplete) {
        adaptee.makeCompleteStrategy(isComplete);
    }

    /**
     * Move backward.
     *
     * @return true, if successful
     */
    public boolean moveBackward() {
        return adaptee.moveBackward();
    }

    /**
     * Move forward.
     *
     * @param arg0 the arg 0
     *
     * @return true, if successful
     */
    public boolean moveForward(Decision<?> arg0) {
        return adaptee.moveForward(arg0);
    }

    /**
     * Observe propagation.
     *
     * @param po the po
     */
    public void observePropagation(PropagationObserver po) {
        adaptee.observePropagation(po);
    }

    /**
     * Observe solving.
     *
     * @return the solving statistics flow
     */
    public SolvingStatisticsFlow observeSolving() {
        return adaptee.observeSolving();
    }

    /**
     * On solution.
     *
     * @param r the r
     */
    public void onSolution(Runnable r) {
        adaptee.onSolution(r);
    }

    /**
     * Output search tree to CP profiler.
     *
     * @param domain the domain
     *
     * @return the closeable
     */
    public Closeable outputSearchTreeToCPProfiler(boolean domain) {
        return adaptee.outputSearchTreeToCPProfiler(domain);
    }

    /**
     * Output search tree to gephi.
     *
     * @param gexfFilename the gexf filename
     *
     * @return the closeable
     */
    public Closeable outputSearchTreeToGephi(String gexfFilename) {
        return adaptee.outputSearchTreeToGephi(gexfFilename);
    }

    /**
     * Output search tree to graphviz.
     *
     * @param gvFilename the gv filename
     *
     * @return the closeable
     */
    public Closeable outputSearchTreeToGraphviz(String gvFilename) {
        return adaptee.outputSearchTreeToGraphviz(gvFilename);
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.cril.approximation.solver.MyISolver#plugMonitor(org.chocosolver.
     * solver.search.loop.monitors.ISearchMonitor)
     */
    @Override
    public void plugMonitor(ISearchMonitor sm) {
        adaptee.plugMonitor(sm);
    }

    /**
     * Preprocessing.
     *
     * @param arg0 the arg 0
     */
    public void preprocessing(long arg0) {
        adaptee.preprocessing(arg0);
    }

    /**
     * Prints the CSV statistics.
     */
    public void printCSVStatistics() {
        adaptee.printCSVStatistics();
    }

    /**
     * Prints the features.
     */
    public void printFeatures() {
        adaptee.printFeatures();
    }

    /**
     * Prints the short features.
     */
    public void printShortFeatures() {
        adaptee.printShortFeatures();
    }

    /**
     * Prints the short statistics.
     */
    public void printShortStatistics() {
        adaptee.printShortStatistics();
    }

    /**
     * Prints the statistics.
     */
    public void printStatistics() {
        adaptee.printStatistics();
    }

    /**
     * Prints the version.
     */
    public void printVersion() {
        adaptee.printVersion();
    }

    /**
     * Profile propagation.
     *
     * @return the propagation profiler
     */
    public PropagationProfiler profilePropagation() {
        return adaptee.profilePropagation();
    }

    /**
     * Propagate.
     *
     * @throws ContradictionException the contradiction exception
     */
    public void propagate() throws ContradictionException {
        adaptee.propagate();
    }

    /**
     * Removes the all stop criteria.
     */
    public void removeAllStopCriteria() {
        adaptee.removeAllStopCriteria();
    }

    /**
     * Removes the hints.
     */
    public void removeHints() {
        adaptee.removeHints();
    }

    /**
     * Removes the stop criterion.
     *
     * @param arg0 the arg 0
     */
    public void removeStopCriterion(Criterion... arg0) {
        adaptee.removeStopCriterion(arg0);
    }

    /**
     * Reset.
     */
    public void reset() {
        adaptee.reset();
    }

    /**
     * Restart.
     */
    public void restart() {
        adaptee.restart();
    }

    /**
     * Search loop.
     *
     * @return true, if successful
     */
    public boolean searchLoop() {
        return adaptee.searchLoop();
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
        adaptee.setConstantRestart(scaleFactor, restartStrategyLimit, restartLimit);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.chocosolver.solver.search.loop.move.IMoveFactory#setDDS(int)
     */
    @Override
    public void setDDS(int discrepancy) {
        adaptee.setDDS(discrepancy);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.chocosolver.solver.search.loop.move.IMoveFactory#setDFS()
     */
    @Override
    public void setDFS() {
        adaptee.setDFS();
    }

    /**
     * Sets the engine.
     *
     * @param propagationEngine the new engine
     */
    public void setEngine(PropagationEngine propagationEngine) {
        adaptee.setEngine(propagationEngine);
    }

    /**
     * Sets the event observer.
     *
     * @param explainer the new event observer
     */
    public void setEventObserver(AbstractEventObserver explainer) {
        adaptee.setEventObserver(explainer);
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
        adaptee.setGeometricalRestart(base, geometricalFactor, restartStrategyLimit, restartLimit);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.chocosolver.solver.search.loop.move.IMoveFactory#setHBFS(double, double,
     * long)
     */
    @Override
    public void setHBFS(double a, double b, long N) {
        adaptee.setHBFS(a, b, N);
    }

    /**
     * Sets the jump to.
     *
     * @param jto the new jump to
     */
    public void setJumpTo(int jto) {
        adaptee.setJumpTo(jto);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.chocosolver.solver.search.loop.move.IMoveFactory#setLDS(int)
     */
    @Override
    public void setLDS(int discrepancy) {
        adaptee.setLDS(discrepancy);
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
        adaptee.setLNS(neighbor, restartCounter, bootstrap);
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
        adaptee.setLNS(neighbor, restartCounter);
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
        adaptee.setLNS(neighbor, bootstrap);
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
        adaptee.setLNS(neighbor);
    }

    /**
     * Sets the learner.
     *
     * @param l the new learner
     */
    public void setLearner(Learn l) {
        adaptee.setLearner(l);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.chocosolver.solver.search.loop.learn.ILearnFactory#setLearningSignedClauses()
     */
    @Override
    public void setLearningSignedClauses() {
        adaptee.setLearningSignedClauses();
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
        adaptee.setLinearRestart(scaleFactor, restartStrategyLimit, restartLimit);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.chocosolver.solver.search.loop.move.IMoveFactory#setLubyRestart(long,
     * org.chocosolver.solver.search.limits.ICounter, int)
     */
    @Override
    public void setLubyRestart(long scaleFactor, ICounter restartStrategyLimit, int restartLimit) {
        adaptee.setLubyRestart(scaleFactor, restartStrategyLimit, restartLimit);
    }

    /**
     * Sets the move.
     *
     * @param m the new move
     */
    public void setMove(Move... m) {
        adaptee.setMove(m);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.chocosolver.solver.search.loop.monitors.ISearchMonitorFactory#
     * setNoGoodRecordingFromRestarts()
     */
    @Override
    public void setNoGoodRecordingFromRestarts() {
        adaptee.setNoGoodRecordingFromRestarts();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.chocosolver.solver.search.loop.monitors.ISearchMonitorFactory#
     * setNoGoodRecordingFromSolutions(org.chocosolver.solver.variables.IntVar[])
     */
    @Override
    public void setNoGoodRecordingFromSolutions(IntVar... vars) {
        adaptee.setNoGoodRecordingFromSolutions(vars);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.chocosolver.solver.search.loop.learn.ILearnFactory#setNoLearning()
     */
    @Override
    public void setNoLearning() {
        adaptee.setNoLearning();
    }

    /**
     * Sets the objective manager.
     *
     * @param om the new objective manager
     */
    public void setObjectiveManager(IObjectiveManager<?> om) {
        adaptee.setObjectiveManager(om);
    }

    /**
     * Sets the propagate.
     *
     * @param p the new propagate
     */
    public void setPropagate(Propagate p) {
        adaptee.setPropagate(p);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.chocosolver.solver.search.loop.move.IMoveFactory#setRestartOnSolutions()
     */
    @Override
    public void setRestartOnSolutions() {
        adaptee.setRestartOnSolutions();
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
        adaptee.setRestarts(restartCriterion, restartStrategy, restartsLimit,
                resetCutoffOnSolution);
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
        adaptee.setRestarts(restartCriterion, restartStrategy, restartsLimit);
    }

    /**
     * Sets the search.
     *
     * @param strategies the new search
     */
    public void setSearch(AbstractStrategy... strategies) {
        adaptee.setSearch(strategies);
    }

    /**
     * Show contradiction.
     */
    public void showContradiction() {
        adaptee.showContradiction();
    }

    /**
     * Show dashboard.
     */
    public void showDashboard() {
        adaptee.showDashboard();
    }

    /**
     * Show dashboard.
     *
     * @param refresh the refresh
     */
    public void showDashboard(long refresh) {
        adaptee.showDashboard(refresh);
    }

    /**
     * Show decisions.
     */
    public void showDecisions() {
        adaptee.showDecisions();
    }

    /**
     * Show decisions.
     *
     * @param message the message
     */
    public void showDecisions(IMessage message) {
        adaptee.showDecisions(message);
    }

    /**
     * Show decisions.
     *
     * @param nChars the n chars
     */
    public void showDecisions(int nChars) {
        adaptee.showDecisions(nChars);
    }

    /**
     * Show restarts.
     */
    public void showRestarts() {
        adaptee.showRestarts();
    }

    /**
     * Show restarts.
     *
     * @param message the message
     */
    public void showRestarts(IMessage message) {
        adaptee.showRestarts(message);
    }

    /**
     * Show short statistics.
     */
    public void showShortStatistics() {
        adaptee.showShortStatistics();
    }

    /**
     * Show short statistics on shutdown.
     */
    public void showShortStatisticsOnShutdown() {
        adaptee.showShortStatisticsOnShutdown();
    }

    /**
     * Show solutions.
     */
    public void showSolutions() {
        adaptee.showSolutions();
    }

    /**
     * Show solutions.
     *
     * @param message the message
     */
    public void showSolutions(IMessage message) {
        adaptee.showSolutions(message);
    }

    /**
     * Show solutions.
     *
     * @param variables the variables
     */
    public void showSolutions(Variable... variables) {
        adaptee.showSolutions(variables);
    }

    /**
     * Show statistics.
     */
    public void showStatistics() {
        adaptee.showStatistics();
    }

    /**
     * Show statistics during resolution.
     *
     * @param f the f
     */
    public void showStatisticsDuringResolution(long f) {
        adaptee.showStatisticsDuringResolution(f);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.chocosolver.solver.search.IResolutionHelper#streamOptimalSolutions(org.
     * chocosolver.solver.variables.IntVar, boolean,
     * org.chocosolver.util.criteria.Criterion[])
     */
    @Override
    public Stream<Solution> streamOptimalSolutions(IntVar arg0, boolean arg1, Criterion... arg2) {
        return adaptee.streamOptimalSolutions(arg0, arg1, arg2);
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
        return adaptee.streamSolutions(stop);
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
        return adaptee.tableSampling(pivot, nbVariablesInTable, probaTuple, random, criterion);
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
        adaptee.throwsException(c, v, s);
    }

    /**
     * To array.
     *
     * @return the number[]
     */
    public Number[] toArray() {
        return adaptee.toArray();
    }

    /**
     * To CSV.
     *
     * @return the string
     */
    public String toCSV() {
        return adaptee.toCSV();
    }

    /**
     * To dimacs string.
     *
     * @return the string
     */
    public String toDimacsString() {
        return adaptee.toDimacsString();
    }

    /**
     * To multi line string.
     *
     * @return the string
     */
    public String toMultiLineString() {
        return adaptee.toMultiLineString();
    }

    /**
     * To one line string.
     *
     * @return the string
     */
    public String toOneLineString() {
        return adaptee.toOneLineString();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return adaptee.toString();
    }

    /**
     * Unplug all search monitors.
     */
    public void unplugAllSearchMonitors() {
        adaptee.unplugAllSearchMonitors();
    }

    /**
     * Unplug monitor.
     *
     * @param sm the sm
     */
    public void unplugMonitor(ISearchMonitor sm) {
        adaptee.unplugMonitor(sm);
    }

    /**
     * Verbose solving.
     *
     * @param frequencyInMilliseconds the frequency in milliseconds
     */
    public void verboseSolving(long frequencyInMilliseconds) {
        adaptee.verboseSolving(frequencyInMilliseconds);
    }

}
