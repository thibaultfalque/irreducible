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
 * The Class Portfolio.
 */
public class Portfolio {

    /** The solvers. */
    private List<MyISolver> solvers = new ArrayList<>();

    /** The best index. */
    private Integer bestIndex;

    /** The best bound. */
    private Integer bestBound;

    /** The result. */
    private UniverseSolverResult result = UniverseSolverResult.UNKNOWN;

    /** The stop solver. */
    private BooleanCriteria stopSolver;

    /** The timeout. */
    private long timeout;

    /**
     * Instantiates a new portfolio.
     *
     * @param timeout the timeout
     */
    public Portfolio(long timeout) {
        stopSolver = new BooleanCriteria();
        this.timeout = timeout;
    }

    /**
     * Adds the solver.
     *
     * @param solver the solver
     */
    public void addSolver(MyISolver solver) {
        solvers.add(solver);
    }

    /**
     * Solve.
     *
     * @return the universe solver result
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
     * Solve.
     *
     * @param solver the solver
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
     * Creates the monitor.
     *
     * @param solverIndex the solver index
     *
     * @return the i monitor solution
     */
    private IMonitorSolution createMonitor(int solverIndex) {
        return () -> onSolution(solverIndex);
    }

    /**
     * On solution.
     *
     * @param solverIndex the solver index
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
     * Gets the best solver.
     *
     * @return the best solver
     */
    public synchronized MyISolver getBestSolver() {
        return solvers.get(bestIndex);
    }

    /**
     * Stop.
     */
    public void stop() {
        stopSolver.setStop(true);
    }

}
