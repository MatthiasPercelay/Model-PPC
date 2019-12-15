/**
 * This file is part of nurse-rostering-solver, https://github.com/MatthiasPercelay/Model-PPC
 *
 * Copyright (c) 2019, Université Nice Sophia Antipolis. All rights reserved.
 *
 * Licensed under the BSD 3-clause license.
 * See LICENSE file in the project root for full license information.
 */
package nurses.planning.checker.rules;

import nurses.NRCmd;
import nurses.Shift;

public class ConsecutiveWorkDays implements IRule {

    public ConsecutiveWorkDays() {
    }

    @Override
    public String getName() {
        return "Number of consecutive work days";
    }

    @Override
    public boolean check(Shift[] agentSchedule) {
        int consecutive = 0;
        boolean warning = false;

        for (int i = 0; i < agentSchedule.length; i++) {
            if (agentSchedule[i].isWork()) {
                consecutive++;
            } else {
                consecutive = 0;
            }

            if (consecutive == 6) {
                warning = true;
            }

            if (consecutive == 7) {
                return false;
            }
        }

        if (warning) {
            NRCmd.LOGGER.info("Warning: Agent is working 6 days in a row");
        }

        return true;
    }
}
