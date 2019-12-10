package nurses.planning.checker.rules;

import nurses.Shift;
import org.junit.Test;

import static nurses.Shift.*;
import static org.junit.Assert.*;

public class ConsecutiveBreakDaysTest {

    @Test
    public void check() {
        ConsecutiveBreakDays checker = new ConsecutiveBreakDays();

        Shift[] s1 = {S, M, S, M, B, S, B,
                B, J, B, J, B, S, M};
        assertTrue(checker.check(s1));

        Shift[] s2 = {S, B, M, S, B, J, J,
                S, J, S, J, B, S, B};
        assertFalse(checker.check(s2));

        Shift[] s3 =  {S, B, M, S, B, J, J,
                S, J, S, J, S, RH, B};
        assertTrue(checker.check(s3));

        Shift[] s4 = {S, J, S, J, J, S, B,
                S, S, S, S, B, M, M};
        assertFalse(checker.check(s4));

        Shift[] s5 =  {S, B, B, S, S, S, J,
                S, RH, S, B, S, S, M};
        assertTrue(checker.check(s5));  // doesn't respect the rule on sundays but respects the one on consecutive days
    }
}