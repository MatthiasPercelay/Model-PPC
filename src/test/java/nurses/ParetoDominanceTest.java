package nurses;

import nurses.specs.IDominanceComparator;
import nurses.specs.ParetoDominance;
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
    public void firdtDominatesSecond() {
        double[] objList1 = new double[]{1, 2, 1, 1};
        double[] objList2 = new double[]{0, 1, 0, 0};

        assertEquals(1, dom.compare(objList1, objList2));
    }

    @Test
    public void secondDominatesFirst() {
        double[] objList1 = new double[]{1, 2, 1, 1};
        double[] objList2 = new double[]{4, 3, 2, 2};

        assertEquals(-1, dom.compare(objList1, objList2));
    }

    @Test
    public void nonDominantObjectives() {
        double[] objList1 = new double[]{1, 3, 1, 3};
        double[] objList2 = new double[]{2, 2, 2, 2};

        assertEquals(0, dom.compare(objList1, objList2));
    }
}
