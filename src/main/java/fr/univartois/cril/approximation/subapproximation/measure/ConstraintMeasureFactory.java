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

package fr.univartois.cril.approximation.subapproximation.measure;

import java.lang.reflect.InvocationTargetException;

import fr.univartois.cril.aceurancetourix.JUniverseAceProblemAdapter;
import fr.univartois.cril.approximation.core.IConstraintGroupSolver;
import fr.univartois.cril.approximation.core.measure.IConstraintMeasure;
import fr.univartois.cril.approximation.solver.ApproximationSolverDecorator;
import fr.univartois.cril.approximation.util.AbstractFactory;

/**
 * The ConstraintMeasureFactory
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public class ConstraintMeasureFactory extends AbstractFactory<IConstraintMeasure> {

    private static final String PACKAGE = "fr.univartois.cril.approximation.subapproximation.measure.";

    private static final String CLASS_NAME_SUFFIX = "ConstraintMeasure";

    /**
     * The single instance of this class.
     */
    private static final ConstraintMeasureFactory INSTANCE = new ConstraintMeasureFactory();

    private ConstraintMeasureFactory() {

    }

    public IConstraintMeasure createConstraintMeasurerByName(String name,IConstraintGroupSolver adapter) {
        try {
            if (name.contains(".")) {
                return createByName(name).newInstance(adapter);
            }
            return createByName(PACKAGE + name + CLASS_NAME_SUFFIX).newInstance(adapter);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ConstraintMeasureFactory instance() {
        return INSTANCE;
    }
}
