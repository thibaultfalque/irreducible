/**
 * This file is a part of the {@code fr.univartois.cril.approximation.core} package.
 *
 * It contains the type KeepFalsifiedConstraintStrategy.
 *
 * (c) 2023 Romain Wallon - approximation.
 * All rights reserved.
 */

package fr.univartois.cril.approximation.core;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.util.ESat;

/**
 * The KeepFalsifiedConstraintStrategy
 *
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public enum KeepFalsifiedConstraintStrategy {

	NEVER {

		@Override
		public void checkConstraints(Model m) {
			// Do nothing here.
		}

	},

	ALWAYS {

		@Override
		public void checkConstraints(Model m) {
			for (Constraint constr : m.getCstrs()) {
				if (constr.isIgnorable() && constr.isSatisfied() != ESat.TRUE) {
					constr.setIgnorable(false);
				}
			}
		}

	};

	public abstract void checkConstraints(Model m);

}
