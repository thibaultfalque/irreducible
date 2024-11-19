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

package fr.univartois.cril.approximation.solver;

import java.util.function.Supplier;

import org.chocosolver.solver.Solver;

import fr.univartois.cril.approximation.core.IConstraintMeasure;
import fr.univartois.cril.approximation.core.IConstraintsRemover;
import fr.univartois.cril.approximation.core.KeepFalsifiedConstraintStrategy;
import fr.univartois.cril.approximation.core.KeepNoGoodStrategy;
import fr.univartois.cril.approximation.solver.state.NormalStateSolver;
import fr.univartois.cril.approximation.solver.state.SubApproximationStateSolver;
import fr.univartois.cril.approximation.subapproximation.measure.ConstraintMeasureFactory;
import fr.univartois.cril.approximation.subapproximation.measure.MeanFilteringConstraintMeasure;
import fr.univartois.cril.approximation.subapproximation.remover.ConstraintRemoverFactory;
import fr.univartois.cril.approximation.subapproximation.remover.PercentageConstraintRemover;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * The ApproximationSolverBuilder
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public class ApproximationSolverBuilder {

    private Solver solver;

    private ApproximationSolverDecorator decorator;

    private Supplier<IConstraintsRemover> remover;

    private IConstraintMeasure measure;

    private double percentage;

    public ApproximationSolverBuilder(Solver solver) {
    	this.solver = solver;
        decorator = new ApproximationSolverDecorator(solver.getModel());

    }

    public ApproximationSolverBuilder setKeepNogood(KeepNoGoodStrategy value) {
        decorator.setKeepNogood(value);
        return this;
    }

    /**
     * @param object
     * @return
     */
    public ApproximationSolverBuilder setKeepFalsified(KeepFalsifiedConstraintStrategy keep) {
        decorator.setKeepFalsified(keep);
        return this;
    }

    public ApproximationSolverBuilder setVerbosity(int v) {
        decorator.setVerbosity(v);
        return this;
    }

    public ApproximationSolverBuilder setTimeout(String timeout) {
        if (timeout != null) {
            if (timeout.contains("ms")) {
                decorator.setTimeoutMs(
                        Long.parseLong(timeout.replace("ms", "")));
            } else if (timeout.contains("s")) {
                decorator.setTimeout(
                        Long.parseLong(timeout.replace("s", "")));
            } else {
                throw new IllegalArgumentException(
                        timeout + " is not a correct format for set the timeout");
            }
        }
        return this;
    }

    public ApproximationSolverBuilder withPercentage(double percentage) {
        this.percentage = percentage;
        return this;
    }

    public ApproximationSolverBuilder withSpecificConstraintRemover(String rm) {
        remover = () -> {
            var r = ConstraintRemoverFactory.instance().createConstraintRemoverByName(rm,
                    decorator);
            r.setConstraintMeasure(measure);
            if (r instanceof PercentageConstraintRemover) {
                ((PercentageConstraintRemover)r).setPercentage(percentage);
            }
            return r;
        };
        return this;
    }

    public ApproximationSolverBuilder withSpecificConstraintMeasure(String m) {
        measure = ConstraintMeasureFactory.instance().createConstraintMeasurerByName(m, decorator);
        return this;
    }

    public ApproximationSolverBuilder withMeanComputation(boolean mean) {
        if (mean) {
            measure = new MeanFilteringConstraintMeasure(measure);
            measure.setSolver(decorator);
        }
        return this;
    }

    public ApproximationSolverBuilder initState(Namespace arguments) {
        if (remover == null) {
            throw new IllegalStateException("Constraint remover must be initialized !");
        }
        SubApproximationStateSolver.initInstance(solver, remover,
                new SolverConfiguration(arguments.getInt("n_runs_approx"),
                        arguments.getDouble("factor_runs_approx"),
                        arguments.getInt("n_sol_limit"),
                        arguments.getDouble("ratio_assigned_approx")),
                arguments.get("path_strategy"));
        NormalStateSolver.initInstance(solver, new SolverConfiguration(
                arguments.getInt("n_runs_normal"), arguments.getDouble("factor_runs_normal"),
                Long.MAX_VALUE, arguments.getDouble("ratio_assigned_normal")),decorator, arguments.get("path_strategy"));
        return this;
    }

    public ApproximationSolverDecorator build() {
        return decorator;
    }
}
