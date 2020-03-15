/**
 * This file is part of nurse-rostering-solver, https://github.com/MatthiasPercelay/Model-PPC
 *
 * Copyright (c) 2020, Université Nice Sophia Antipolis. All rights reserved.
 *
 * Licensed under the BSD 3-clause license.
 * See LICENSE file in the project root for full license information.
 */
package nurses.planning.checker.rules;

import nurses.NRCmd;
import nurses.Shift;

import java.util.HashSet;

import static java.lang.Math.min;

public class ThirtySixHourBreak implements IRule {
    @Override
    public String getName() {
        return "Weekly 36-hour break";
    }

    /**
     * @param agentSchedule schedule of the agent
     * @return true if the agent has at least one 36-hour break during each week
     */
    @Override
    public boolean check(Shift[] agentSchedule) {
        for (int i = 0; i < agentSchedule.length; i+=7) {
            if (!goodInWeek(agentSchedule, i)) {
                return false;
            }
        }
        return true;
    }

    private boolean goodInWeek(Shift[] agentSchedule, int w) {
        int end = min(w+8, agentSchedule.length);
        boolean hasGoodBreak = false;   // is there a 36-hour break this week ?

        for (int i = w; i < end; i++) {
            if (agentSchedule[i].isBreak()) {
                if (!isBadBreak(agentSchedule, i)) {    // if the break is 36 hours long then we're good
                    hasGoodBreak = true;
                    break;
                }
            }
        }

        if (agentSchedule[w] == Shift.B && !hasGoodBreak) {
            NRCmd.LOGGER.info("\t Warning: could know if the break on the first day of the month was 36h");
            return true;
        }

        return hasGoodBreak;
    }

    private boolean isBadBreak(Shift[] agentSchedule, int d) {
        if (d == 0) {
            if (agentSchedule[d+1] == Shift.M || agentSchedule[d+1] == Shift.J) {
                return true;
            } else {
                return false;
            }
        } else {
            if (d == agentSchedule.length - 1 && agentSchedule[d-1] == Shift.S) {
                NRCmd.LOGGER.info("\t Warning: Agent can't work a morning or day shift the first monday of next month");
                return false;
            } else {
                if (agentSchedule[d-1] == Shift.S && (agentSchedule[d+1] == Shift.M || agentSchedule[d+1] == Shift.J)) {
                    return true;
                } else {
                    return false;
                }
            }
        }
    }
}
