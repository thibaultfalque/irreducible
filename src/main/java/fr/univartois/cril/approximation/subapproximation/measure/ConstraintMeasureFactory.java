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

import fr.univartois.cril.approximation.core.IConstraintGroupSolver;
import fr.univartois.cril.approximation.core.IConstraintMeasure;
import fr.univartois.cril.approximation.util.AbstractFactory;

/**
 * The ConstraintMeasureFactory.
 *
 * @author Thibault Falque
 * @author Romain Wallon
 * @version 0.1.0
 */
public class ConstraintMeasureFactory extends AbstractFactory<IConstraintMeasure> {

    /** The Constant PACKAGE. */
    private static final String PACKAGE = "fr.univartois.cril.approximation.subapproximation.measure.";

    /** The Constant CLASS_NAME_SUFFIX. */
    private static final String CLASS_NAME_SUFFIX = "ConstraintMeasure";

    /**
     * The single instance of this class.
     */
    private static final ConstraintMeasureFactory INSTANCE = new ConstraintMeasureFactory();

    /**
     * Instantiates a new constraint measure factory.
     */
    private ConstraintMeasureFactory() {
    }

    /**
     * Creates a new ConstraintMeasure object.
     *
     * @param name the name
     * @param adapter the adapter
     * @return the i constraint measure
     */
    public IConstraintMeasure createConstraintMeasurerByName(String name,
            IConstraintGroupSolver adapter) {
        try {
            IConstraintMeasure m;
            if (name.contains(".")) {
                m = createByName(name).newInstance();

            } else {
                m = createByName(PACKAGE + name + CLASS_NAME_SUFFIX).newInstance();
            }
            return m;
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                 | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Instance.
     *
     * @return the constraint measure factory
     */
    public static ConstraintMeasureFactory instance() {
        return INSTANCE;
    }

}
