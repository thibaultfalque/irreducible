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
        generalGroup.addArgument("-i","--instance").type(FileInputStream.class)
        .setDefault(System.in).help("The name of the instance to solve");
        
        var approximationGroup = parser.addArgumentGroup("Approximation");
        approximationGroup.description("This parameters controls the approximation. ");
        approximationGroup.addArgument("-r","--n-runs").help("The number of runs to solve the approximate problem").type(Integer.class);
        approximationGroup.addArgument("-f","--factor-runs").help("The increasing factor for updating the number of runs.").type(Double.class);
        approximationGroup.addArgument("--measure").help("The name of the measure considered to remove constraints.").setDefault("WdegFiltering").type(String.class);
        approximationGroup.addArgument("--constraint-remover").help("The type of strategy for removes constraints using the specify measure").type(String.class);
        
       
        return parser;
    }

}

