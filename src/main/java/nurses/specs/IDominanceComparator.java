/**
 * This file is part of nurse-rostering-solver, https://github.com/MatthiasPercelay/Model-PPC
 *
 * Copyright (c) 2020, Université Nice Sophia Antipolis. All rights reserved.
 *
 * Licensed under the BSD 3-clause license.
 * See LICENSE file in the project root for full license information.
 */
package nurses.specs;
/* Copyright 2009-2018 David Hadka
*
* This file is part of the MOEA Framework.
*/
import java.util.Comparator;

import nurses.pareto.MOSolution;

/**
 * Interface for comparing two solutions using a dominance relation.  A
 * dominance relation may impose a partial or total ordering on a set of 
 * solutions.
 * <p>
 * Implementations which also implement {@link Comparator} impose a 
 * total ordering on the set of solutions.  However, it is typically the case
 * that {@code (compare(x, y)==0) == (x.equals(y))} does not hold, and the
 * comparator may impose orderings that are inconsistent with equals.
 */
public interface IDominanceComparator {

	/**
	 * Compares the two solutions using a dominance relation, returning
	 * {@code -1} if {@code solution1} dominates {@code solution2}, {@code 1} if
	 * {@code solution2} dominates {@code solution1}, and {@code 0} if the
	 * solutions are non-dominated.
	 * 
	 * @param solution1 the first solution
	 * @param solution2 the second solution
	 * @return {@code -1} if {@code solution1} dominates {@code solution2},
	 *         {@code 1} if {@code solution2} dominates {@code solution1}, and
	 *         {@code 0} if the solutions are non-dominated
	 */
	default int compare(MOSolution solution1, MOSolution solution2) {
		return compare(solution1.objective, solution2.objective);
	}
	
	public int compare(double[] objective1, double[] objective2);
	
}
