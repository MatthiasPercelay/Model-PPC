/**
 * This file is part of nurse-rostering-solver, https://github.com/MatthiasPercelay/Model-PPC
 *
 * Copyright (c) 2019, Université Nice Sophia Antipolis. All rights reserved.
 *
 * Licensed under the BSD 3-clause license.
 * See LICENSE file in the project root for full license information.
 */
package nurses.planning.checker;

import nurses.Shift;
import nurses.planning.TimeTable;
import nurses.planning.checker.rules.ConsecutiveBreakDays;
import nurses.planning.checker.rules.ConsecutiveWorkDays;
import nurses.planning.checker.rules.SundayBreak;
import nurses.planning.checker.rules.ThirtySixHourBreak;
import org.junit.Test;

import static org.junit.Assert.*;
import static nurses.Shift.*;

public class PlanningCheckerTest {

    @Test
    public void check() {
        TimeTable planning = new TimeTable(2,3);
        Shift[][] shifts = {{RH, S,	RA, J, J, M, M, J, J, RA, S, J, RH, RH, S, S, RH, J, RA, RH, RH, RTT, J, RA, S, J, RH, RH},
                            {S, RH, S, S, S, RH, RH, M, M, M, M, RTT, RH, RH, CA, CA, CA, CA, CA, RH, RH, CA, CA, CA, RTT, RTT, RH, RH},
                            {RH, M, M, M, M, RH, RH, S, S, S, RH, S, S, S, RH, RTT, M, M, M, RH, RH, J, S, S, RH, RH, M, M}};
        planning.setDays(shifts);
        PlanningChecker legalityChecker = new PlanningChecker(planning);

        legalityChecker.addRule(new ConsecutiveWorkDays());
        legalityChecker.addRule(new ConsecutiveBreakDays());
        legalityChecker.addRule(new SundayBreak());
        legalityChecker.addRule(new ThirtySixHourBreak());

        assertTrue(legalityChecker.check().getValidity());

    }
}