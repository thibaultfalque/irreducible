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

package fr.univartois.cril.approximation.util;

import java.lang.reflect.Constructor;

/**
 * The AbstractFactory.
 *
 * @author Thibault Falque
 * @author Romain Wallon
 * @version 0.1.0
 * @param <T> the generic type
 */
public abstract class AbstractFactory<T> {

    /**
     * Creates a new Abstract object.
     *
     * @param className the class name
     * @param args the args
     * @return the constructor< t>
     */
    @SuppressWarnings("unchecked")
    protected Constructor<T> createByName(String className, Class<?>... args) {
        try {
            var clz = Class.forName(className);
            return (Constructor<T>) clz.getDeclaredConstructor(args);
        } catch (SecurityException e) {
            System.err.println(e.getLocalizedMessage());
        } catch (IllegalArgumentException e) {
            System.err.println(e.getLocalizedMessage());
        } catch (NoSuchMethodException e) {
            System.err.println(e.getLocalizedMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
