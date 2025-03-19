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

import java.util.ArrayList;
import java.util.List;

import org.chocosolver.parser.SetUpException;

import fr.univartois.cril.approximation.cli.CLI;
import fr.univartois.cril.approximation.solver.ApproximationSolverBuilder;
import net.sourceforge.argparse4j.inf.ArgumentParserException;

/**
 * The {@code Main} class is the entry point for the Approximation solver.
 * <p>
 * This class parses command-line arguments, initializes the solver,
 * and executes the solving process. It supports portfolio-based
 * solving, relaxation solving and standard constraint solving using the Choco solver.
 * </p>
 * <p>
 * The solver incorporates relaxation techniques and can be configured
 * using various parameters, such as constraint removal strategies,
 * constraint measurement, verbosity level, and timeout settings.
 * </p>
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public class Main {

    /**
     * The main method, which serves as the entry point for the application.
     * <p>
     * This method initializes the command-line parser, extracts arguments,
     * and either runs a portfolio solver, relaxation solver or a standard constraint
     * solver using Choco.
     * Shutdown hooks are registered to ensure proper cleanup.
     * </p>
     *
     * @param args The command-line arguments provided by the user.
     */
    public static void main(String[] args) {
        var parser = CLI.createCLIParser(true);
        try {
            var arguments = parser.parseArgs(args);
            if (Boolean.TRUE.equals(arguments.getBoolean("portfolio"))) {
                var portfolio = PortfolioFactory.newDefaultPortfolio(arguments);
                Runtime.getRuntime().addShutdownHook(new Thread(portfolio::stop));
                portfolio.solve();
            } else {
                List<String> chocoArgs = new ArrayList<>();
                chocoArgs.add(arguments.<String>get("instance"));
                chocoArgs.addAll(arguments.getList("remaining"));

                var xcsp = new XCSPExtension();
                if (xcsp.setUp(chocoArgs.toArray(new String[chocoArgs.size()]))) {
                    xcsp.createSolver();
                    xcsp.buildModel();
                    xcsp.configureSearch();
                }
                xcsp.removeShutdownHook();
                var model = xcsp.getModel();

                model.getSolver().logWithANSI(!arguments.getBoolean("no_print_color"));
                var builder = new ApproximationSolverBuilder(model.getSolver())
                        .withSpecificConstraintRemover(arguments.getString("constraint_remover"))
                        .withSpecificConstraintMeasure(arguments.getString("measure"))
                        .setKeepFalsified(arguments.get("keep_falsified"))
                        .setVerbosity(arguments.getInt("verbosity"))
                        .setTimeout(arguments.getLong("global_timeout"));

                var solver = builder.initState(arguments).build();

                Runtime.getRuntime()
                        .addShutdownHook(new Thread(() -> solver.displaySolution(xcsp)));
                solver.solve();
            }

        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        } catch (SetUpException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}
