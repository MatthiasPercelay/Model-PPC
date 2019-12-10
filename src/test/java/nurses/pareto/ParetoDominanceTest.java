/**
 * This file is part of nurse-rostering-solver, https://github.com/MatthiasPercelay/Model-PPC
 *
 * Copyright (c) 2019, Université Nice Sophia Antipolis. All rights reserved.
 *
 * Licensed under the BSD 3-clause license.
 * See LICENSE file in the project root for full license information.
 */
package nurses.pareto;

import nurses.specs.IDominanceComparator;
import nurses.pareto.ParetoDominance;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ParetoDominanceTest {
    private IDominanceComparator dom;

    @Before
    public void setup() {
        dom = new ParetoDominance();
    }

    @Test
    public void firstDominatesSecond() {
        double[] objList1 = new double[]{0, 1, 0, 0};
        double[] objList2 = new double[]{1, 2, 1, 1};

        assertEquals(-1, dom.compare(objList1, objList2));
    }

    @Test
    public void secondDominatesFirst() {
        double[] objList1 = new double[]{4, 3, 2, 2};
        double[] objList2 = new double[]{1, 2, 1, 1};

        assertEquals(1, dom.compare(objList1, objList2));
    }

    @Test
    public void nonDominantObjectives() {
        double[] objList1 = new double[]{1, 3, 1, 3};
        double[] objList2 = new double[]{2, 2, 2, 2};

        assertEquals(0, dom.compare(objList1, objList2));
    }

    @Test
    public void firstDomSecondsEq() {
        double[] objList1 = new double[]{0, 0, 1, 0};
        double[] objList2 = new double[]{1, 1, 1, 1};

        assertEquals(-1, dom.compare(objList1, objList2));
    }

    @Test
    public void secondDomFirstEq() {
        double[] objList1 = new double[]{1, 1, 1, 1};
        double[] objList2 = new double[]{0, 0, 1, 0};

        assertEquals(1, dom.compare(objList1, objList2));
    }

    @Test
    public void noDomEq() {
        double[] objList1 = new double[]{1, 1, 1, 1};
        double[] objList2 = new double[]{1, 1, 1, 1};

        assertEquals(0, dom.compare(objList1, objList2));
    }
}
