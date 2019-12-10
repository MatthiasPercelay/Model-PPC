/**
 * This file is part of nurse-rostering-solver, https://github.com/MatthiasPercelay/Model-PPC
 *
 * Copyright (c) 2019, Université Nice Sophia Antipolis. All rights reserved.
 *
 * Licensed under the BSD 3-clause license.
 * See LICENSE file in the project root for full license information.
 */
package nurses.pareto;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ParetoArchiveLTest {
    private MOSolution[] sols;
    private ParetoArchiveL pareto;

    @Before
    public void setup() {
        sols = new MOSolution[]{
                new MOSolution(new Object(), new double[]{1, 2, 1, 1}),
                new MOSolution(new Object(), new double[]{5, 4, 3, 3}),
                new MOSolution(new Object(), new double[]{1, 3, 1, 3}),
                new MOSolution(new Object(), new double[]{0, 1, 0, 0}),// non dom
                new MOSolution(new Object(), new double[]{2, 2, 2, 2}),
                new MOSolution(new Object(), new double[]{0, 0, 1, 0}) // non dom
        };

        pareto = new ParetoArchiveL();
        for (MOSolution sol : sols) {
            pareto.add(sol);
        }


        // tester qu'on obéit bien aux propriétés d'une archive de pareto
    }

    @Test
    public void notAddingDominatedSolutions() {
        assertFalse("should have removed or never added solution 0", pareto.getSolutions().contains(sols[0]));
        assertFalse("should have removed or never added solution 1", pareto.getSolutions().contains(sols[1]));
        assertFalse("should have removed or never added solution 2", pareto.getSolutions().contains(sols[2]));
        assertFalse("should have removed or never added solution 4", pareto.getSolutions().contains(sols[4]));
    }

    @Test
    public void addingNonDominatedSolutions() {
        assertTrue("should have kept solution 3", pareto.getSolutions().contains(sols[3]));
        assertTrue("should have kept solution 5", pareto.getSolutions().contains(sols[5]));
    }


    @Test
    public void testStepByStepAddingSolutions() {
        ParetoArchiveL paretoWatch = new ParetoArchiveL();
        paretoWatch.add(sols[0]);
        assertEquals("should add the solution 0", 1, paretoWatch.size());

        // sol[1] is dominated by a solution already in the archive
        paretoWatch.add(sols[1]);
        assertEquals("should not add dominated solution", 1, paretoWatch.size());
        assertFalse("should not add the solution 1 because it is dominated by the one already there", pareto.getSolutions().contains(sols[1]));

        // sol[2] is dominated by a solution already in the archive
        paretoWatch.add(sols[2]);
        assertEquals("should not add dominated solution", 1, paretoWatch.size());
        assertFalse("should not add the solution 2 because it is dominated by the one already there", pareto.getSolutions().contains(sols[2]));

        // sol[3] dominates a solution already in the list
        paretoWatch.add(sols[2]);
        assertEquals("should add the solution 3 but remove solution 0", 1, paretoWatch.size());
        assertFalse("should have removed the solution 0 ", pareto.getSolutions().contains(sols[0]));
        assertTrue("should have added the solution 3 ", pareto.getSolutions().contains(sols[3]));

        // sol[4] is dominated by a solution already in the archive
        paretoWatch.add(sols[4]);
        assertEquals("should not add dominated solution", 1, paretoWatch.size());
        assertFalse("should not add the solution 4 because it is dominated by the one already there", pareto.getSolutions().contains(sols[4]));


        // sol[5] neither dominates nor is dominated by a solution that is already there
        paretoWatch.add(sols[5]);
        assertEquals("should add non dominated solution", 2, paretoWatch.size());
        assertTrue("should have added the solution 5", pareto.getSolutions().contains(sols[5]));
        assertTrue("should not have removed the solution 3", pareto.getSolutions().contains(sols[3]));
    }

    @Test
    public void sortByLexico(){
        LexicoDominance d = new LexicoDominance();

        pareto.getSolutions().sort(d);
        assertTrue("lexically minimal solution should come first in sorted solution", pareto.getSolutions().indexOf(sols[5]) < pareto.getSolutions().indexOf(sols[3]));
    }

}
