/**
 * This file is a part of the {@code fr.univartois.cril.approximation.core} package.
 *
 * It contains the type KeepFalsifiedConstraintStrategy.
 *
 * (c) 2023 Romain Wallon - approximation.
 * All rights reserved.
 */

package fr.univartois.cril.approximation.core;

import constraints.Constraint;
import solver.Solver;
import solver.Solver.WarmStarter;
import variables.Variable;

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
        public void checkConstraints(WarmStarter starter, Solver solver) {
            // Do nothing here.
        }

    },

    ALWAYS {

        @Override
        public void checkConstraints(WarmStarter starter, Solver solver) {
            int[] t = new int[solver.problem.variables.length];
            for (Variable variable : solver.problem.variables) {
                int valueIndex = starter.valueIndexOf(variable);
                int value = variable.dom.toVal(valueIndex);
                t[variable.num] = value;
            }

            for (Constraint constr : solver.problem.constraints) {
                if (!constr.isSatisfiedBy(t)) {
                    constr.ignorable = false;
                }
            }
        }

    };

    public abstract void checkConstraints(WarmStarter starter, Solver solver);

}

