/**
 * This file is part of nurse-rostering-solver, https://github.com/MatthiasPercelay/Model-PPC
 *
 * Copyright (c) 2020, Université Nice Sophia Antipolis. All rights reserved.
 *
 * Licensed under the BSD 3-clause license.
 * See LICENSE file in the project root for full license information.
 */
package nurses.pareto;

import nurses.specs.IDominanceComparator;

public class ParetoDominance implements IDominanceComparator {

    /**
     * Hypothesis : we want to minimize : the comparator will
     *
     * @param objective1 the first objective list
     * @param objective2 the second objective list
     * @return {@code -1} if {@code solution1} is minimal compared to {@code solution2},
     * {@code 1} if {@code solution2} is minimal compared to {@code solution1}, and
     * {@code 0} if the solutions are non-dominated
     */
    @Override
    public int compare(double[] objective1, double[] objective2) {
        if (objective1.length != objective2.length) {
            return 0;
        }
        int comp = 0;

        for (int i = 0; i < objective1.length; i++) {
            if(objective1[i] == objective2[i]) { continue; }
            if(objective1[i] < objective2[i] && comp == 1) { return 0; }
            if(objective1[i] > objective2[i] && comp == -1) { return 0; }
            if(objective1[i] < objective2[i]) { comp = -1; }
            else if(objective1[i] > objective2[i]) { comp = 1; }
        }

        return comp;
    }
}
