/**
 * This file is part of nurse-rostering-solver, https://github.com/MatthiasPercelay/Model-PPC
 *
 * Copyright (c) 2019, Université Nice Sophia Antipolis. All rights reserved.
 *
 * Licensed under the BSD 3-clause license.
 * See LICENSE file in the project root for full license information.
 */
package nurses.planning.checker.rules;

import nurses.Shift;

public class ConsecutiveBreakDays implements IRule {
    @Override
    public String getName() {
        return "Consecutive break days";
    }

    @Override
    public boolean check(Shift[] agentSchedule) {
        for (int i = 0; i < agentSchedule.length; i+=14) {
            if (!isGoodCycle(agentSchedule, i)) {
                return false;
            }
        }
        return true;
    }

    public boolean isGoodCycle(Shift[] agentSchedule, int c) {
        boolean twoConsecutive = false;
        int consecutiveBreaks = 0;
        int nbBreaks = 0;
        for (int i = c; i < c + 14; i++) {
            if (agentSchedule[i].isBreak()) {
                consecutiveBreaks++;
                nbBreaks++;

                if (consecutiveBreaks >= 2) {
                    twoConsecutive = true;
                }
            } else {
                consecutiveBreaks = 0;
            }
        }

        return twoConsecutive && nbBreaks >= 4;
    }
}
