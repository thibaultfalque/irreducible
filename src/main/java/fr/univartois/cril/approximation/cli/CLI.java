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

package fr.univartois.cril.approximation.cli;

import java.io.FileInputStream;

import fr.univartois.cril.approximation.core.KeepFalsifiedConstraintStrategy;
import fr.univartois.cril.approximation.core.KeepNoGoodStrategy;
import fr.univartois.cril.approximation.solver.state.PathStrategy;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;

/**
 * The CLI
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public class CLI {

    private static final String PROGRAM_NAME = "Approximation";
    private static final String DESCRIPTION = "Resolve hard combinatorial problem using approximation";
    private static final String VERSION = "0.1.0";

    public static ArgumentParser createCLIParser() {
        ArgumentParser parser = ArgumentParsers.newFor(PROGRAM_NAME).build()
                .defaultHelp(true)
                .description(DESCRIPTION).version(VERSION);


        var generalGroup = parser.addArgumentGroup("General");
        generalGroup.addArgument("-i","--instance").type(String.class);
        generalGroup.addArgument("--global-timeout").type(String.class);
        generalGroup.addArgument("--no-print-color").type(Boolean.class).setDefault(true);
        generalGroup.addArgument("--ace-verbosity").type(Integer.class).setDefault(0);
        generalGroup.addArgument("--keep-nogood").type(KeepNoGoodStrategy.class).setDefault(KeepNoGoodStrategy.ALWAYS);
        generalGroup.addArgument("--keep-falsified").type(KeepFalsifiedConstraintStrategy.class).setDefault(KeepFalsifiedConstraintStrategy.NEVER);
        generalGroup.addArgument("--valh").type(String.class).setDefault("WarmStarterScore");

        var normalGroup = parser.addArgumentGroup("Normal resolution");
        normalGroup.addArgument("--n-runs-normal").help("The number of runs to solve the approximate problem").setDefault(50).type(Integer.class);
        normalGroup.addArgument("--factor-runs-normal").help("The increasing factor for updating the number of runs.").type(Double.class).setDefault(1.1);
        normalGroup.addArgument("--ratio-assigned-normal").help("The ratio above which the solver is kept running in normal state.").type(Double.class).setDefault(11.);
        normalGroup.addArgument("--ratio-assigned-approx").help("The ratio above which the solver is kept running in approx state.").type(Double.class).setDefault(11.);

        var approximationGroup = parser.addArgumentGroup("Approximation resolution");
        approximationGroup.description("This parameters controls the approximation. ");
        approximationGroup.addArgument("--n-runs-approx").help("The number of runs to solve the approximate problem").setDefault(50).type(Integer.class);
        approximationGroup.addArgument("--n-sol-limit").help("The max number of solution for approximate problem.").setDefault(1).type(Integer.class);
        approximationGroup.addArgument("--factor-runs-approx").help("The increasing factor for updating the number of runs.").setDefault(1.1).type(Double.class);
        approximationGroup.addArgument("--percentage").help("The percentage of constraints to remove per approximation.").setDefault(0.).type(Double.class);
        approximationGroup.addArgument("--measure").help("The name of the measure considered to remove constraints.").setDefault("NEffectiveFiltering").type(String.class);
        approximationGroup.addArgument("--mean").help("Use the mean of the measure for a group instead of the sum").setDefault(false).type(Boolean.class);
        approximationGroup.addArgument("--constraint-remover").help("The type of strategy for removes constraints using the specify measure").setDefault("Group").type(String.class);
        approximationGroup.addArgument("--path-strategy").type((p,a,v)->PathStrategy.valueOf(v)).setDefault(PathStrategy.APPROX_NORMAL);

        return parser;
    }

}

