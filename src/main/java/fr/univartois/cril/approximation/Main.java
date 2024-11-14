/**
 * JUniverse, a solver interface.
 * Copyright (c) 2022 - Univ Artois, CNRS & Exakis Nelite.
 * All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
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

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Settings;
import org.chocosolver.solver.search.strategy.BlackBoxConfigurator;

import fr.univartois.cril.approximation.cli.CLI;
import fr.univartois.cril.approximation.solver.ApproximationSolverBuilder;
import net.sourceforge.argparse4j.inf.ArgumentParserException;

/**
 * The Main
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public class Main {

	/**
	 * Creates a new Main.
	 */
	public Main() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		var parser = CLI.createCLIParser();
		try {
			var arguments = parser.parseArgs(args);
			var model = new Model(Settings.dev());
			var solver = new ApproximationSolverBuilder(model.getSolver())
					.withPercentage(arguments.getDouble("percentage"))
					.withSpecificConstraintRemover(arguments.getString("constraint_remover"))
					.withSpecificConstraintMeasure(arguments.getString("measure"))
					.withMeanComputation(arguments.getBoolean("mean"))
                    .setKeepNogood(arguments.get("keep_nogood"))
                    .setKeepFalsified(arguments.get("keep_falsified"))
					.setVerbosity(arguments.getInt("ace_verbosity"))
					.setTimeout(arguments.getString("global_timeout"))
					.initState(arguments).build();
			Runtime.getRuntime().addShutdownHook(new Thread(() -> solver.displaySolution()));
			System.out.println(solver.solve(arguments.<String>get("instance")));
			solver.displaySolution();

		} catch (ArgumentParserException e) {
			parser.handleError(e);

			System.exit(1);
		}

	}

}
