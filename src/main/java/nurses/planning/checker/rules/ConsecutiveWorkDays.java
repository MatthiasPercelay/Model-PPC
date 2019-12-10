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

import java.util.ArrayList;
import java.util.List;

public class ConsecutiveWorkDays implements IRule {

    public ConsecutiveWorkDays() {
    }

    @Override
    public String getName() {
        return "Number of consecutive work days";
    }

    @Override
    public boolean check(Shift[] agentSchedule) {
        List<String> warnings = new ArrayList<>();
        int consecutiveDays = 0;
        for (int i = 0; i < agentSchedule.length-6; i++) {
            for (int j = 0; j < 7; j++) {
                if (agentSchedule[i+j].isWork()) {  // increment each day an agent is working
                    consecutiveDays += 1;
                } else {
                    consecutiveDays = 0;
                }
            }

            if (consecutiveDays == 7) {
                if (warnings.size() > 0) {
                    warnings.remove(warnings.size() - 1);   // the 6-day warning is no longer needed
                }
                return false;
            }
            if (consecutiveDays == 6) {
                warnings.add("\t Warning: agent is working 6 days in a row");
            }
        }

        for (String s : warnings) {
            NRCmd.LOGGER.info(s);
        }
        return true;
    }
}
