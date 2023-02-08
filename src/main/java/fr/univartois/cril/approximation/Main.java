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

import java.io.FileInputStream;
import java.io.IOException;

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

			var solver = new ApproximationSolverBuilder()
					.withSpecificConstraintRemover(arguments.getString("constraint_remover"))
					.withSpecificConstraintMeasure(arguments.getString("measure"))
					.setNoPrintColor(arguments.getBoolean("no_print_color"))
					.setAceVerbosity(arguments.getInt("ace_verbosity"))
					.setTimeout(arguments.getString("global_timeout"))
					.initState(arguments).build();
			System.out.println(solver.solve(arguments.<FileInputStream>get("instance")));

		} catch (ArgumentParserException e) {
			parser.handleError(e);
			
			System.exit(1);
		}catch (IOException e) {
            e.printStackTrace();
        }

	}

}
