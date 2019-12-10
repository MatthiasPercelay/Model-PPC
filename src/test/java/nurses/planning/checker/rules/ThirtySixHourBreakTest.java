package nurses.planning.checker.rules;

import nurses.Shift;
import org.junit.Test;

import static nurses.Shift.*;
import static org.junit.Assert.*;

public class ThirtySixHourBreakTest {

    @Test
    public void check() {
        ThirtySixHourBreak checker = new ThirtySixHourBreak();

        Shift[] s1 = {S, J, S, J, B, S, M,
                      S, J, S, J, B, S, M};
        assertTrue(checker.check(s1));

        Shift[] s2 = {S, B, M, S, B, J, B,
                      S, J, S, J, B, S, M};
        assertFalse(checker.check(s2));

        Shift[] s3 =  {S, B, M, S, S, S, B,
                       M, J, S, J, B, S, M};
        assertFalse(checker.check(s3));

        Shift[] s4 = {S, J, S, J, B, S, M,
                      S, S, S, S, B, M, M};
        assertFalse(checker.check(s4));

        Shift[] s5 =  {S, B, M, S, S, S, B,
                       S, J, S, S, S, S, B};
        assertTrue(checker.check(s5));
        }
}