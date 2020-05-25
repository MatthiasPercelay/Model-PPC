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
import nurses.pareto.LexicoDominance;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LexicoDominanceTest {
    private IDominanceComparator dom;

    @Before
    public void setup() {
        dom = new LexicoDominance();
    }

    // @Test
    // public void firstDominatesSecond() {
    //     double[] objList1 = new double[]{0, 0, 1, 20, 20};
    //     double[] objList2 = new double[]{0, 0, 5, 0, 0};

    //     assertEquals(-1, dom.compare(objList1, objList2));
    // }

    // @Test
    // public void secondDominatesFirst() {
    //     double[] objList1 = new double[]{0, 0, 30, 0, 0};
    //     double[] objList2 = new double[]{0, 0, 5, 200, 200};

    //     assertEquals(1, dom.compare(objList1, objList2));
    // }

    // @Test
    // public void noDominance() {
    //     double[] objList1 = new double[]{0, 0, 30, 0, 0};
    //     double[] objList2 = new double[]{0, 0, 30, 0, 0};

    //     assertEquals(0, dom.compare(objList1, objList2));
    // }
}
