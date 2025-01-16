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

import java.util.ArrayList;
import java.util.List;

import org.chocosolver.parser.SetUpException;
import org.chocosolver.parser.xcsp.XCSP;

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

			var builder = new ApproximationSolverBuilder(model.getSolver())
					.withPercentage(arguments.getDouble("percentage"))
					.withSpecificConstraintRemover(arguments.getString("constraint_remover"))
					.withSpecificConstraintMeasure(arguments.getString("measure"))
					.withMeanComputation(arguments.getBoolean("mean")).setKeepNogood(arguments.get("keep_nogood"))
					.setKeepFalsified(arguments.get("keep_falsified")).setVerbosity(arguments.getInt("verbosity"))
					.setTimeout(arguments.getString("global_timeout"));
			
			if(arguments.getBoolean("dichotomic_bound").booleanValue()) {
				builder.withNbStep(arguments.get("n_steps")).withSequenceApproximation(arguments.get("sequence"), arguments).setDichotomic(true);
			}
			
			var solver = builder.initState(arguments).build()		;

			model.getSolver().logWithANSI(!arguments.getBoolean("no_print_color"));
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				//solver.restoreSolution();
				solver.displaySolution(xcsp);
			}));
			solver.solve();


		} catch (ArgumentParserException e) {
			parser.handleError(e);
			System.exit(1);
		} catch (SetUpException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}
