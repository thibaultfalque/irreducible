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

package fr.univartois.cril.approximation.subapproximation.remover;

import java.lang.reflect.InvocationTargetException;

import fr.univartois.cril.approximation.core.IConstraintGroupSolver;
import fr.univartois.cril.approximation.core.IConstraintsRemover;
import fr.univartois.cril.approximation.util.AbstractFactory;

/**
 * The ConstraintRemoverFactory
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public class ConstraintRemoverFactory extends AbstractFactory<IConstraintsRemover> {

    private static final String CLASNAME_SUFFIX = "ConstraintRemover";

    private static final String PACKAGE = "fr.univartois.cril.approximation.subapproximation.remover.";

    /**
     * The single instance of this class.
     */
    private static final ConstraintRemoverFactory INSTANCE = new ConstraintRemoverFactory();

    private ConstraintRemoverFactory() {

    }

    public IConstraintsRemover createConstraintRemoverByName(String name,
            IConstraintGroupSolver groupSolver) {
        try {
            if (name.contains(".")) {
                return createByName(name,IConstraintGroupSolver.class).newInstance(groupSolver);
            }
            return createByName(PACKAGE + name + CLASNAME_SUFFIX,IConstraintGroupSolver.class).newInstance(groupSolver);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ConstraintRemoverFactory instance() {
        return INSTANCE;
    }
}
