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
 * The Main.
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public class Main {

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        var parser = CLI.createCLIParser(true);
        try {
            var arguments = parser.parseArgs(args);
            if (arguments.getBoolean("portfolio")) {
                var portfolio = PortfolioFactory.newDefaultPortfolio(arguments);
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    portfolio.stop();
                }));
                portfolio.solve();
            } else {
                System.out.println("c " + arguments);
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
                        .setKeepNogood(arguments.get("keep_nogood"))
                        .setKeepFalsified(arguments.get("keep_falsified"))
                        .setVerbosity(arguments.getInt("verbosity"))
                        .setTimeout(arguments.getString("global_timeout"));

                var solver = builder.initState(arguments).build();

                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    solver.displaySolution(xcsp);
                }));
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
