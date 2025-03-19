/**
 * approximation, a constraint programming solver based on Choco, utilizing relaxation
 * techniques.
 * Copyright (c) 2025 - Univ Artois, CNRS & Luxembourg University.
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.chocosolver.solver.ResolutionPolicy;
import org.chocosolver.solver.search.loop.monitors.IMonitorSolution;

import fr.univartois.cril.approximation.solver.criteria.BooleanCriteria;

/**
 * A class that manages a portfolio of solvers running in parallel.
 * <p>
 * The {@code Portfolio} class allows multiple solvers to be executed concurrently,
 * selecting the best solution found among them. It uses a fixed thread pool to run
 * solvers within a specified timeout.
 * </p>
 *
 * <p>
 * The portfolio keeps track of the best bound found and updates all solvers accordingly.
 * If an optimization problem is being solved, the best solution value is propagated to
 * all solvers to improve efficiency.
 * </p>
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public class Portfolio {

    /**
     * List of solvers included in the portfolio.
     */
    private List<MyISolver> solvers = new ArrayList<>();

    /**
     * Index of the solver that found the best solution.
     */
    private Integer bestIndex;

    /**
     * Best bound found among all solvers.
     */
    private Integer bestBound;

    /**
     * Result of the solving process.
     */
    private UniverseSolverResult result = UniverseSolverResult.UNKNOWN;

    /**
     * Stop criterion for terminating solvers when needed.
     */
    private BooleanCriteria stopSolver;

    /**
     * Timeout for the execution of solvers.
     */
    private long timeout;

    /**
     * Creates a new {@code Portfolio} with the specified timeout.
     *
     * @param timeout The maximum time allowed for the solvers to run (in milliseconds).
     */
    public Portfolio(long timeout) {
        stopSolver = new BooleanCriteria();
        this.timeout = timeout;
    }

    /**
     * Adds a solver to the portfolio.
     *
     * @param solver The solver to add.
     */
    public void addSolver(MyISolver solver) {
        solvers.add(solver);
    }

    /**
     * Executes all solvers in the portfolio concurrently.
     *
     * @return The result of the solving process as a {@link UniverseSolverResult}.
     */
    public UniverseSolverResult solve() {
        var service = Executors.newFixedThreadPool(solvers.size());
        for (int i = 0; i < solvers.size(); i++) {
            var solver = solvers.get(i);
            solver.plugMonitor(createMonitor(i));
            solver.addStopCriterion(stopSolver);
            service.submit(() -> solve(solver));
        }
        try {
            service.shutdown();
            service.awaitTermination(timeout, TimeUnit.MILLISECONDS);
            stopSolver.setStop(true);
            service.shutdownNow();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return result;
    }

    /**
     * Runs the solving process for a given solver.
     *
     * @param solver The solver to execute.
     */
    private void solve(MyISolver solver) {
        var tmp = solver.solve();
        synchronized (this) {
            if (tmp != UniverseSolverResult.UNKNOWN) {
                result = tmp;
            }
        }
    }

    /**
     * Creates a monitor for tracking solutions found by solvers.
     *
     * @param solverIndex The index of the solver being monitored.
     *
     * @return A monitor that triggers when a solution is found.
     */
    private IMonitorSolution createMonitor(int solverIndex) {
        return () -> onSolution(solverIndex);
    }

    /**
     * Handles the event of a solution being found by a solver.
     *
     * @param solverIndex The index of the solver that found the solution.
     */
    private synchronized void onSolution(int solverIndex) {
        var om = solvers.get(solverIndex).getObjectiveManager();
        if (om.isOptimization()) {
            int bound = (int) om.getBestSolutionValue();
            boolean updated = false;
            boolean bmax = om.getPolicy() == ResolutionPolicy.MAXIMIZE
                           && (bestBound == null || bound > bestBound);
            boolean bmin = om.getPolicy() == ResolutionPolicy.MINIMIZE
                           && (bestBound == null || bound < bestBound);
            if (bmax || bmin) {
                bestBound = bound;
                bestIndex = solverIndex;
                updated = true;
            }
            System.out.println("New bound " + bestBound + " found by the solver " + bestIndex);
            if (updated) {
                for (MyISolver solver : solvers) {
                    solver.getObjectiveManager().updateBestSolution(bestBound);

                }
            }
        }
    }

    /**
     * Retrieves the solver that found the best solution.
     *
     * @return The solver that obtained the best solution.
     */
    public synchronized MyISolver getBestSolver() {
        return solvers.get(bestIndex);
    }

    /**
     * Stops all solvers in the portfolio.
     */
    public void stop() {
        stopSolver.setStop(true);
    }

}
