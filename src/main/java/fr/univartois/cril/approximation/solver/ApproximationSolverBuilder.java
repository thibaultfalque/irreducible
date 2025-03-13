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
import fr.univartois.cril.approximation.solver.state.PathStrategy;
import fr.univartois.cril.approximation.subapproximation.measure.ConstraintMeasureFactory;
import fr.univartois.cril.approximation.subapproximation.measure.MeanFilteringConstraintMeasure;
import fr.univartois.cril.approximation.subapproximation.remover.ConstraintRemoverFactory;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * A builder for creating the approximation solver from the command line interface.
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public class ApproximationSolverBuilder {

    /**
     * The solver that will be decorated by the approximation solver.
     */
    private Solver solver;

    /**
     * The decorator that will be used to decorate the solver.
     */
    private ApproximationSolverDecorator decorator;

    /**
     * The supplier of the constraint remover that will be used to creates the remover of
     * the constraints.
     *
     * @see IConstraintsRemover
     */
    private Supplier<IConstraintsRemover> sRemover;

    /**
     * The measure that will be used to select the constraint that we remove.
     *
     * @see fr.univartois.cril.approximation.core.IConstraintMeasure
     */
    private IConstraintMeasure measure;

    /** The number of steps. */
    private long nbSteps;

    /**
     * Instantiates a new approximation solver builder.
     *
     * @param solver the solver
     */
    public ApproximationSolverBuilder(Solver solver) {
        this.solver = solver;
        decorator = new ApproximationSolverDecorator(solver.getModel());
    }

    /**
     * Sets the keep nogood.
     *
     * @param value the value
     *
     * @return the approximation solver builder
     */
    public ApproximationSolverBuilder setKeepNogood(KeepNoGoodStrategy value) {
        decorator.setKeepNogood(value);
        return this;
    }

    /**
     * Sets the keep falsified.
     *
     * @param keep the keep
     *
     * @return the approximation solver builder
     */
    public ApproximationSolverBuilder setKeepFalsified(KeepFalsifiedConstraintStrategy keep) {
        decorator.setKeepFalsified(keep);
        return this;
    }

    /**
     * Sets the verbosity of the decoree solver.
     *
     * @param v the level of verbosity
     *
     * @return the approximation solver builder with the verbosity set
     */
    public ApproximationSolverBuilder setVerbosity(int v) {
        decorator.setVerbosity(v);
        return this;
    }

    /**
     * Sets the timeout using a string representation.
     * For example, "10s" for 10 seconds or "100ms" for 100 milliseconds.
     *
     * @param timeout the timeout
     *
     * @return the approximation solver builder
     */
    public ApproximationSolverBuilder setTimeout(String timeout) {
        if (timeout != null) {
            if (timeout.contains("ms")) {
                decorator.setTimeoutMs(Long.parseLong(timeout.replace("ms", "")));
            } else if (timeout.contains("s")) {
                decorator.setTimeout(Long.parseLong(timeout.replace("s", "")));
            } else {
                throw new IllegalArgumentException(
                        timeout + " is not a correct format for set the timeout");
            }
        }
        return this;
    }

    /**
     * Adds the kind of remover that will be used to remove the constraints.
     *
     * @param rm the name of the constraint remover
     *
     * @return the approximation solver builder
     */
    public ApproximationSolverBuilder withSpecificConstraintRemover(String rm) {
        sRemover = () -> {
            var r = ConstraintRemoverFactory.instance().createConstraintRemoverByName(rm,
                    decorator);
            r.setConstraintMeasure(measure);
            return r;
        };
        return this;
    }

    /**
     * The maximum number of steps (a step is a normal state or a relaxation state) that
     * the approximation solver will perform.
     *
     * @param nbSteps the number of steps
     *
     * @return the approximation solver builder with the number of steps added
     */
    public ApproximationSolverBuilder withNbStep(long nbSteps) {
        this.nbSteps = nbSteps;
        return this;
    }

    /**
     * Adds a measure that will be used to select the constraint that we remove.
     *
     * @param m the name of the measure
     *
     * @return the current builder with the measure added
     */
    public ApproximationSolverBuilder withSpecificConstraintMeasure(String m) {
        measure = ConstraintMeasureFactory.instance().createConstraintMeasurerByName(m, decorator);
        return this;
    }

    /**
     * With mean computation.
     *
     * @param mean the mean
     *
     * @return the approximation solver builder
     */
    public ApproximationSolverBuilder withMeanComputation(boolean mean) {
        if (mean) {
            measure = new MeanFilteringConstraintMeasure(measure);
            measure.setSolver(decorator);
        }
        return this;
    }

    /**
     * Initializes the internal states of the relaxation solver.
     *
     * @param arguments the arguments
     *
     * @return the approximation solver builder
     */
    public ApproximationSolverBuilder initState(Namespace arguments) {
        if (sRemover == null) {
            throw new IllegalStateException("Constraint remover must be initialized !");
        }
        PathStrategy pathStrategy = arguments.get("path_strategy");
        var remover = sRemover.get();
        var subApproximationConfiguration = new SolverConfiguration(
                arguments.getInt("n_runs_approx"),
                arguments.getDouble("factor_runs_approx"),
                arguments.getInt("n_sol_limit"), arguments.getDouble("ratio_assigned_approx"));
        subApproximationConfiguration.setPathStrategy(pathStrategy);
        subApproximationConfiguration.setRemover(remover);

        var normalConfiguration = new SolverConfiguration(arguments.getInt("n_runs_normal"),
                arguments.getDouble("factor_runs_normal"),
                Long.MAX_VALUE, arguments.getDouble("ratio_assigned_normal"));
        normalConfiguration.setPathStrategy(pathStrategy);
        normalConfiguration.setRemover(remover);

        var solverContext = new SolverContext(normalConfiguration, subApproximationConfiguration);
        decorator.setContext(solverContext);
        return this;
    }

    /**
     * Builds the approximation solver.
     *
     * @return the approximation solver that has been built
     */
    public IApproximationSolver build() {
        return decorator;
    }

}
