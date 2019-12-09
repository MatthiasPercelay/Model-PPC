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

import nurses.pareto.MOSolution;

public interface IParetoArchive {

	default void add(double[] objective) {
		add(new MOSolution(null, objective));
	}

	default void add(Object solution, double[] objective) {
		add(new MOSolution(null, objective));
	}

	void add(MOSolution mosol);
	
	boolean isDominated(double[] objective);
	
	default boolean isDominated(MOSolution mosol) {
		return isDominated(mosol.objective);
	}
	void forEach(Consumer<MOSolution> consumer);
	
	default <E> void forEachBi(final BiConsumer<E, double[]> consumer) {
		// TODO handle type safety ?
		forEach( (mosol) -> consumer.accept((E) mosol.solution, mosol.objective));
	}
	
	default <E> void forEachSolution(final Consumer<E> consumer) {
		// TODO handle type safety ?
		forEach( (mosol) -> consumer.accept((E) mosol.solution));
	}
	
	default void forEachObjective(final Consumer<double[]> consumer) {
		forEach( (mosol) -> consumer.accept(mosol.objective));
	}
	
	int size();
	
	
}
