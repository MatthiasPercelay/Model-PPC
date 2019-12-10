package nurses.planning.checker.rules;

import nurses.Shift;
import org.junit.Test;

import static nurses.Shift.*;
import static nurses.Shift.M;
import static org.junit.Assert.*;

public class SundayBreakTest {

    @Test
    public void check() {
        SundayBreak checker = new SundayBreak();

        Shift[] s1 = {S, M, S, M, B, S, RA,
                B, J, B, J, B, S, M};
        assertTrue(checker.check(s1));

        Shift[] s2 = {S, B, M, S, B, J, J,
                S, J, S, J, B, S, B};
        assertTrue(checker.check(s2));

        Shift[] s3 =  {S, B, M, S, B, J, RH,
                S, J, S, J, S, RH, RH};
        assertTrue(checker.check(s3));

        Shift[] s4 = {S, J, S, J, J, S, S,
                S, S, S, S, B, M, M};
        assertFalse(checker.check(s4));
    }
}