/**
 * This file is part of nurse-rostering-solver, https://github.com/MatthiasPercelay/Model-PPC
 *
 * Copyright (c) 2020, Université Nice Sophia Antipolis. All rights reserved.
 *
 * Licensed under the BSD 3-clause license.
 * See LICENSE file in the project root for full license information.
 */
package nurses.planning.checker.rules;

import nurses.Shift;
import org.junit.Test;

import static nurses.Shift.*;
import static org.junit.Assert.*;

public class ConsecutiveWorkDaysTest {

    @Test
    public void check() {
        ConsecutiveWorkDays checker = new ConsecutiveWorkDays();
        // this is supposed to be valid and not produce a warning
        Shift[] s1 = {W, W, B, B, W, W, W, W};
        assertTrue(checker.check(s1));

        // this is also supposed to be valid but produce a warning
        Shift[] s2 = {W, W, W, W, W, W, B, B, W, W};
        assertTrue(checker.check(s2));

        // this is supposed to be invalid with no warning
        Shift[] s3 = {B, B, W, W, W, W, W, W, W, B, B};
        assertFalse(checker.check(s3));

        // this is also not supposed to pass
        Shift[] s4 = {B, W, B, W, B, B, W, W, W, W, W, W, W};
        assertFalse(checker.check(s4));
    }
}