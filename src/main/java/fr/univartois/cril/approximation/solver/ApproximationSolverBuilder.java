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

import fr.univartois.cril.aceurancetourix.JUniverseAceProblemAdapter;
import fr.univartois.cril.approximation.core.IConstraintMeasure;
import fr.univartois.cril.approximation.core.IConstraintsRemover;
import fr.univartois.cril.approximation.solver.state.NormalStateSolver;
import fr.univartois.cril.approximation.solver.state.SubApproximationStateSolver;
import fr.univartois.cril.approximation.subapproximation.measure.ConstraintMeasureFactory;
import fr.univartois.cril.approximation.subapproximation.remover.ConstraintRemoverFactory;
import net.sourceforge.argparse4j.inf.Namespace;
import solver.AceBuilder;

/**
 * The ApproximationSolverBuilder
 *
 * @author Thibault Falque
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public class ApproximationSolverBuilder {

    private JUniverseAceProblemAdapter aceProblemAdapter;

    private ApproximationSolverDecorator decorator;

    private Supplier<IConstraintsRemover> remover;
    
    private IConstraintMeasure measure;

    private AceBuilder builder;

    public ApproximationSolverBuilder() {
        aceProblemAdapter = new JUniverseAceProblemAdapter();
        decorator = new ApproximationSolverDecorator(aceProblemAdapter);
        builder = aceProblemAdapter.getBuilder();

    }

    public ApproximationSolverBuilder setNoPrintColor(boolean value) {
        builder.getOptionsGeneralBuilder().setNoPrintColors(value);
        return this;
    }

    public ApproximationSolverBuilder setAceVerbosity(int v) {
        builder.getOptionsGeneralBuilder().setVerbose(v);
        return this;
    }

    public ApproximationSolverBuilder setTimeout(String timeout) {
        if (timeout.contains("ms")) {
            builder.getOptionsGeneralBuilder().setTimeout(
                    Long.parseLong(timeout.replace("ms", "")));
        } else if (timeout.contains("s")) {
            builder.getOptionsGeneralBuilder().setTimeout(
                    Long.parseLong(timeout.replace("s", "")) * 1000);
        } else {
            throw new IllegalArgumentException(
                    timeout + " is not a correct format for set the timeout");
        }
        return this;
    }

    public ApproximationSolverBuilder withSpecificConstraintRemover(String rm) {
        remover = ()->{
            var r= ConstraintRemoverFactory.instance().createConstraintRemoverByName(rm, decorator);
            r.setConstraintMeasure(measure);
            return r;
        };
        return this;
    }

    public ApproximationSolverBuilder withSpecificConstraintMeasure(String m) {
        measure = ConstraintMeasureFactory.instance().createConstraintMeasurerByName(m, decorator);
        return this;
    }

    public ApproximationSolverBuilder initState(Namespace arguments) {
        if (remover == null) {
            throw new IllegalStateException("Constraint remover must be initialized !");
        }
        SubApproximationStateSolver.initInstance(aceProblemAdapter, remover,
                new SolverConfiguration(arguments.getInt("n_runs_approx"),
                        arguments.getDouble("factor_runs_approx"),
                        arguments.getInt("n_sol_limit")),arguments.get("path_strategy"));
        NormalStateSolver.initInstance(aceProblemAdapter, new SolverConfiguration(
                arguments.getInt("n_runs_normal"), arguments.getDouble("factor_runs_normal"),Long.MAX_VALUE));
        return this;
    }

    public ApproximationSolverDecorator build() {
        return decorator;
    }
}
