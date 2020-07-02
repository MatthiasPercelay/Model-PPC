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

import java.util.Comparator;

public class LexicoDominance implements IDominanceComparator, Comparator<MOSolution> {

    @Override
    public int compare(double[] objective1, double[] objective2) {
        if(objective1.length != objective2.length) { return 0; }
        for(int i = 0;i<objective1.length;i++){
            if(objective1[i] < objective2[i]) { return -1; }
            if(objective1[i] > objective2[i]) { return 1; }
        }
        return 0;
    }
    @Override
    public int compare(MOSolution s1, MOSolution s2) {
        return compare(s1.objective, s2.objective);
    }
}
