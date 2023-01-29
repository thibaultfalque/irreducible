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

import fr.univartois.cril.aceurancetourix.JUniverseAceProblemAdapter;
import fr.univartois.cril.approximation.core.remover.IConstraintsRemover;
import fr.univartois.cril.approximation.solver.state.NormalStateSolver;
import fr.univartois.cril.approximation.solver.state.SubApproximationStateSolver;
import fr.univartois.cril.approximation.subapproximation.measure.ConstraintMeasureFactory;
import fr.univartois.cril.approximation.subapproximation.remover.ConstraintRemoverFactory;
import fr.univartois.cril.juniverse.core.IUniverseSolver;
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
	private IConstraintsRemover remover;
	private AceBuilder builder;

	public ApproximationSolverBuilder() {
		aceProblemAdapter = new JUniverseAceProblemAdapter();
		builder = aceProblemAdapter.getBuilder();
		builder.getOptionsGeneralBuilder().setNoPrintColors(true);
		builder.getOptionsGeneralBuilder().setVerbose(1);
	}

	public ApproximationSolverBuilder withSpecificConstraintRemover(String rm) {
		remover = ConstraintRemoverFactory.instance().createConstraintRemoverByName(rm);
		return this;
	}

	public ApproximationSolverBuilder withSpecificConstraintMeasure(String m) {
		remover.setConstraintMeasure(ConstraintMeasureFactory.instance().createConstraintMeasurerByName(m));
		return this;
	}

	public ApproximationSolverBuilder initState() {
		if (remover == null) {
			throw new IllegalStateException("Constraint remover must be initialized !");
		}
		SubApproximationStateSolver.initInstance(aceProblemAdapter, remover);
		NormalStateSolver.initInstance(aceProblemAdapter);
		return this;
	}

	public IUniverseSolver build() {
		return new ApproximationSolverDecorator(aceProblemAdapter);
	}
}
