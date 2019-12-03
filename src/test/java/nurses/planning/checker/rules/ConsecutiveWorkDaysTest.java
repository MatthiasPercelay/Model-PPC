package nurses.planning.checker.rules;

import nurses.Shift;
import org.junit.Test;

import static nurses.Shift.*;
import static org.junit.Assert.*;

public class ConsecutiveWorkDaysTest {

    @Test
    public void check() {
        // this is supposed to be valid and not produce a warning
        Shift[] s1 = {W, W, B, B, W, W, W, W};
        ConsecutiveWorkDays passing1 = new ConsecutiveWorkDays();
        assertTrue(passing1.check(s1));

        // this is also supposed to be valid but produce a warning
        Shift[] s2 = {W, W, W, W, W, W, B, B, W, W};
        ConsecutiveWorkDays passing2 = new ConsecutiveWorkDays();
        assertTrue(passing2.check(s2));

        // this is supposed to be invalid with no warning
        Shift[] s3 = {B, B, W, W, W, W, W, W, W, B, B};
        ConsecutiveWorkDays notPassing = new ConsecutiveWorkDays();
        assertFalse(notPassing.check(s3));
    }
}