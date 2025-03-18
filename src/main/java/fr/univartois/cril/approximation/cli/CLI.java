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

import java.util.Map;

import fr.univartois.cril.approximation.core.KeepFalsifiedConstraintStrategy;
import fr.univartois.cril.approximation.solver.state.PathStrategy;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentAction;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;

/**
 * The {@code CLI} class is responsible for handling command-line arguments
 * for the Approximation solver. It provides a parser that allows users
 * to configure the solver’s behavior, including normal and relaxation-based
 * solving strategies.
 *
 * <p>
 * This class uses {@link ArgumentParsers} to define various options,
 * such as verbosity, constraint removal strategies, and time limits,
 * allowing flexible solver configuration.
 * </p>
 *
 * <h2>Supported Arguments:</h2>
 * <ul>
 * <li><b>General settings:</b> Instance file, verbosity, timeout, and output
 * formatting.</li>
 * <li><b>Normal resolution:</b> Parameters related to solving the original problem.</li>
 * <li><b>Relaxation resolution:</b> Parameters controlling constraint relaxation and
 * approximation mechanisms.</li>
 * <li><b>Additional solver parameters:</b> Arguments passed directly to the Choco
 * solver.</li>
 * </ul>
 *
 * <p>
 * This class is a utility class and should not be instantiated.
 * </p>
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public class CLI {

    /**
     * The {@code TimeoutAction} class is a custom argument action used to parse and
     * handle timeout values
     * provided via command-line arguments. This class implements the
     * {@link ArgumentAction} interface
     * from the argparse4j library.
     *
     * <p>
     * The timeout value can be specified in milliseconds ("ms") or seconds ("s"). The
     * parsed value is
     * then converted into milliseconds and stored in the argument attributes map.
     * </p>
     *
     * <p>
     * Example usage:
     *
     * <pre>
     *     --global-timeout 500ms   // Sets timeout to 500 milliseconds
     *     --global-timeout 10s     // Sets timeout to 10,000 milliseconds (10 seconds)
     * </pre>
     * </p>
     */
    private static class TimeoutAction implements ArgumentAction {

        /**
         * Parses the timeout argument and converts it into milliseconds.
         *
         * @param parser The {@link ArgumentParser} instance managing arguments.
         * @param arg The {@link Argument} associated with this action.
         * @param attrs The attributes map where the parsed value will be stored.
         * @param flag The flag that triggered this action.
         * @param value The raw timeout value provided as a string.
         *
         * @throws ArgumentParserException If an error occurs during parsing.
         * @throws IllegalArgumentException If the provided timeout format is invalid.
         */
        @Override
        public void run(ArgumentParser parser, Argument arg,
                Map<String, Object> attrs, String flag, Object value)
                throws ArgumentParserException {
            var timeout = (String) value;
            long t = 0;
            if (timeout != null) {
                if (timeout.contains("ms")) {
                    t = Long.parseLong(timeout.replace("ms", ""));
                } else if (timeout.contains("s")) {
                    t = Long.parseLong(timeout.replace("s", "")) * 1000;
                } else {
                    throw new IllegalArgumentException(
                            timeout + " is not a correct format for setting the timeout");
                }
            }

            attrs.put(arg.getDest(), t);
        }

        /**
         * Called when the argument is attached to the parser. This method can be used to
         * modify the argument before parsing, but it is left empty in this
         * implementation.
         *
         * @param arg The argument being attached.
         */
        @Override
        public void onAttach(Argument arg) {
            // No action required
        }

        /**
         * Specifies whether the argument requires an additional value.
         *
         * @return {@code true}, indicating that this action consumes an argument value.
         */
        @Override
        public boolean consumeArgument() {
            return true;
        }

    }

    /** The Constant PROGRAM_NAME. */
    private static final String PROGRAM_NAME = "Approximation";

    /** The Constant DESCRIPTION. */
    private static final String DESCRIPTION = "Resolve hard combinatorial problem using approximation";

    /** The Constant VERSION. */
    private static final String VERSION = "0.1.0";

    /**
     * The Enum SolverKind.
     */
    public enum SolverKind {

        /** The default. */
        DEFAULT,

        /** The approx. */
        APPROX,

        /** The portfolio. */
        PORTFOLIO
    }

    /**
     * Creates and configures the command-line argument parser for the Approximation
     * solver.
     * This parser allows users to define various settings, including general solver
     * configurations, normal resolution parameters, and relaxation-based resolution
     * options.
     *
     * <p>
     * The parser is built using {@link ArgumentParsers} and supports a wide range of
     * arguments to fine-tune the solver's behavior.
     * </p>
     *
     * <h2>Argument Groups:</h2>
     * <ul>
     * <li><b>General:</b> Includes instance file, verbosity, timeout, and output
     * formatting.</li>
     * <li><b>Normal resolution:</b> Defines parameters for solving the original
     * problem.</li>
     * <li><b>Relaxation resolution:</b> Controls how constraints are relaxed and
     * reintroduced.</li>
     * <li><b>Additional solver parameters:</b> Passes extra arguments directly to the
     * Choco solver.</li>
     * </ul>
     *
     * @param withInstance the with instance
     *
     * @return an {@link ArgumentParser} configured for the Approximation solver
     */
    public static ArgumentParser createCLIParser(boolean withInstance) {
        ArgumentParser parser = ArgumentParsers.newFor(PROGRAM_NAME).build().defaultHelp(true)
                .description(DESCRIPTION)
                .version(VERSION);

        var timeoutAction = new TimeoutAction();

        var approxGroup = parser.addMutuallyExclusiveGroup("kind").required(true);
        approxGroup.addArgument("--approx").help("Configures an approximate solver.")
                .action(Arguments.storeTrue());

        approxGroup.addArgument("--default").help("Configures a standard Choco solver.")
                .action(Arguments.storeTrue());

        approxGroup.addArgument("--portfolio").help("Configures a portfolio Choco solver.")
                .action(Arguments.storeTrue());

        var generalGroup = parser.addArgumentGroup("General");
        generalGroup.addArgument("-i", "--instance").type(String.class).required(withInstance);
        generalGroup.addArgument("--global-timeout").action(timeoutAction);
        generalGroup.addArgument("--no-print-color").type(Boolean.class).setDefault(Boolean.TRUE);
        generalGroup.addArgument("--verbosity").type(Integer.class).setDefault(0);
        generalGroup.addArgument("--keep-falsified").type(KeepFalsifiedConstraintStrategy.class)
                .setDefault(KeepFalsifiedConstraintStrategy.NEVER);
        generalGroup.addArgument("--portfolio-configuration").type(String.class);

        var normalGroup = parser.addArgumentGroup("Normal resolution");
        normalGroup.addArgument("--n-runs-normal")
                .help("The number of runs to solve the full problem").setDefault(50)
                .type(Integer.class);
        normalGroup.addArgument("--factor-runs-normal")
                .help("The increasing factor for updating the number of runs.")
                .type(Double.class).setDefault(1.1);
        normalGroup.addArgument("--ratio-assigned-normal")
                .help("The ratio above which the solver is kept running in normal state.")
                .type(Double.class)
                .setDefault(11.);
        normalGroup.addArgument("--ratio-assigned-approx")
                .help("The ratio above which the solver is kept running in approx state.")
                .type(Double.class)
                .setDefault(11.);

        var approximationGroup = parser.addArgumentGroup("Approximation resolution");
        approximationGroup.description("This parameters controls the approximation. ");
        approximationGroup.addArgument("--n-runs-approx")
                .help("The number of runs to solve the approximate problem")
                .setDefault(10).type(Integer.class);

        approximationGroup.addArgument("--n-sol-limit")
                .help("The max number of solution for approximate problem.")
                .setDefault(1).type(Integer.class);
        approximationGroup.addArgument("--factor-runs-approx")
                .help("The increasing factor for updating the number of runs.").setDefault(1.1)
                .type(Double.class);
        approximationGroup.addArgument("--measure")
                .help("The name of the measure considered to remove constraints.")
                .setDefault("NEffectiveFiltering").type(String.class);
        approximationGroup.addArgument("--constraint-remover")
                .help("The type of strategy for removes constraints using the specify measure")
                .setDefault("Group")
                .type(String.class);
        approximationGroup.addArgument("--path-strategy").type((p, a, v) -> PathStrategy.valueOf(v))
                .setDefault(PathStrategy.APPROX_NORMAL);

        parser.addArgument("--").dest("remaining").nargs("*")
                .help("Arguments to pass to the subcommand");

        return parser;
    }

}
