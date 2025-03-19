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

package fr.univartois.cril.approximation;

import org.chocosolver.parser.Level;
import org.chocosolver.parser.xcsp.XCSP;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.strategy.BlackBoxConfigurator;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.search.strategy.SearchParams;

/**
 * The Class XCSPExtension.
 */
class XCSPExtension extends XCSP {

    /**
     * Removes the shutdown hook.
     */
    public void removeShutdownHook() {
        Runtime.getRuntime().removeShutdownHook(statOnKill);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.chocosolver.parser.xcsp.XCSP#freesearch(org.chocosolver.solver.Solver)
     */
    @Override
    public void freesearch(Solver solver) {
        BlackBoxConfigurator bb = BlackBoxConfigurator.init();
        boolean opt = solver.getObjectiveManager().isOptimization();
        final SearchParams.ValSelConf defaultValSel;
        final SearchParams.VarSelConf defaultVarSel;
        final SearchParams.ResConf defaultResConf;
        if (free) {
            defaultValSel = valsel;
            defaultVarSel = varsel;
            defaultResConf = restarts;
            bb.setNogoodOnRestart(false)
                    .setRestartOnSolution(true)
                    .setExcludeObjective(true)
                    .setExcludeViews(false)
                    .setMetaStrategy(m -> {
                        if (lc > 0) {
                            return Search.lastConflict(m, 1);
                        } else if (cos) {
                            return Search.conflictOrderingSearch(m);
                        } else {
                            return m;
                        }
                    });
        } else {
            // variable selection
            defaultValSel = new SearchParams.ValSelConf(
                    SearchParams.ValueSelection.MIN, opt, 1, opt);
            defaultVarSel = new SearchParams.VarSelConf(
                    SearchParams.VariableSelection.DOMWDEG, Integer.MAX_VALUE);
            // restart policy
            defaultResConf = new SearchParams.ResConf(
                    SearchParams.Restart.LUBY, 500, 50_000, true);
            // other parameters
            bb.setNogoodOnRestart(false)
                    .setRestartOnSolution(true)
                    .setExcludeObjective(true)
                    .setExcludeViews(false)
                    .setMetaStrategy(m -> Search.lastConflict(m, 1));
        }
        bb.setIntVarStrategy(vars -> defaultVarSel.make().apply(vars,
                defaultValSel.make().apply(vars[0].getModel())));
        bb.setRestartPolicy(defaultResConf.make());

        if (level.isLoggable(Level.INFO)) {
            solver.log().println(bb.toString());
        }
        bb.complete(solver.getModel(), solver.getSearch());
    }

}
