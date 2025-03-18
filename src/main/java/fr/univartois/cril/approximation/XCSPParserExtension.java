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

import java.util.HashMap;
import java.util.Map;

import org.chocosolver.parser.xcsp.XCSPParser;
import org.chocosolver.solver.variables.IntVar;
import org.xcsp.parser.entries.XVariables.XVar;

/**
 * The Class XCSPParserExtension.
 */
public class XCSPParserExtension extends XCSPParser {

    /** The decoree. */
    private XCSPParser decoree;

    /**
     * Instantiates a new XCSP parser extension.
     *
     * @param p the p
     */
    public XCSPParserExtension(XCSPParser p) {
        decoree = p;
    }

    /**
     * Gets the vars of problem.
     *
     * @return the vars of problem
     */
    public Map<XVar, IntVar> getVarsOfProblem() {
        try {
            var f = XCSPParser.class.getDeclaredField("mvars");
            f.setAccessible(true);
            return (HashMap<XVar, IntVar>) f.get(decoree);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

}
