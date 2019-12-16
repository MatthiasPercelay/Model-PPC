/**
 * This file is part of nurse-rostering-solver, https://github.com/MatthiasPercelay/Model-PPC
 *
 * Copyright (c) 2019, Université Nice Sophia Antipolis. All rights reserved.
 *
 * Licensed under the BSD 3-clause license.
 * See LICENSE file in the project root for full license information.
 */
package nurses.specs;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import nurses.Shift;
import nurses.pareto.MOSolution;

public interface IParetoArchive {

	default void add(Shift[][] solution, double[] objective) {
		add(new MOSolution(solution, objective));
	}

	default void add(ITimetable solution, double[] objective) {
		add(new MOSolution(solution, objective));
	}

	void add(MOSolution mosol);

	boolean isDominated(double[] objective);

	default boolean isDominated(MOSolution mosol) {
		return isDominated(mosol.objective);
	}
	void forEach(Consumer<MOSolution> consumer);

	default void forEachBi(final BiConsumer<ITimetable, double[]> consumer) {
		// TODO handle type safety ?
		forEach( (mosol) -> consumer.accept(mosol.solution, mosol.objective));
	}

	default void forEachSolution(final Consumer<MOSolution> consumer) {
		// TODO handle type safety ?
		forEach( (mosol) -> consumer.accept(mosol));
	}

	default void forEachObjective(final Consumer<double[]> consumer) {
		forEach( (mosol) -> consumer.accept(mosol.objective));
	}

	int size();


}
