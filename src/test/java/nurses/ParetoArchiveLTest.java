package nurses;

import nurses.pareto.MOSolution;
import nurses.specs.ParetoArchiveL;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class ParetoArchiveLTest {
    private MOSolution[] sols;
    private ParetoArchiveL pareto;

    @Before
    public void setup() {
        sols = new MOSolution[]{
                new MOSolution(new Object(), new double[]{1, 2, 1, 1}),
                new MOSolution(new Object(), new double[]{0, 1, 0, 0}),
                new MOSolution(new Object(), new double[]{5, 4, 3, 3}),
                new MOSolution(new Object(), new double[]{1, 3, 1, 3}),
                new MOSolution(new Object(), new double[]{2, 2, 2, 2}),
                new MOSolution(new Object(), new double[]{0, 0, 1, 0})
        };

        pareto = new ParetoArchiveL();
        for (MOSolution sol : sols) {
            pareto.add(sol);
        }
    }

    @Test
    public void dominatedAreSpottedAsSuch() {
        assertTrue("Solution 0 is supposed to be dominated", pareto.isDominated(sols[0]));
        assertTrue("Solution 2 is supposed to be dominated", pareto.isDominated(sols[2]));
        assertTrue("Solution 3 is supposed to be dominated", pareto.isDominated(sols[3]));
        assertTrue("Solution 4 is supposed to be dominated", pareto.isDominated(sols[4]));
    }

    @Test
    public void nonDominatedAreSpottedAsSuch() {
        assertFalse("Solution 1 is not supposed to be dominated", pareto.isDominated(sols[1]));
        assertFalse("Solution 5 is not supposed to be dominated", pareto.isDominated(sols[5]));
    }


}
