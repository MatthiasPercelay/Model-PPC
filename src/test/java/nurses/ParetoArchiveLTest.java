package nurses;

import nurses.pareto.MOSolution;
import nurses.specs.ParetoArchiveL;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ParetoArchiveLTest {
    private MOSolution[] sols;
    private ParetoArchiveL pareto;

    @Before
    public void setup() {
        sols = new MOSolution[]{
                new MOSolution(new Object(), new double[]{1, 2, 1, 1}),
                new MOSolution(new Object(), new double[]{0, 1, 0, 0}),// non dom
                new MOSolution(new Object(), new double[]{5, 4, 3, 3}),
                new MOSolution(new Object(), new double[]{1, 3, 1, 3}),
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
    public void notAddingDominatedSolutions(){
        assertFalse(pareto.getSolutions().contains(sols[0]));
        assertFalse(pareto.getSolutions().contains(sols[2]));
        assertFalse(pareto.getSolutions().contains(sols[3]));
        assertFalse(pareto.getSolutions().contains(sols[4]));
    }

    @Test
    public void addingNonDominatedSolutions(){
        assertTrue(pareto.getSolutions().contains(sols[1]));
        assertTrue(pareto.getSolutions().contains(sols[5]));
    }

}
