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

public class SundayBreak implements IRule {
    @Override
    public String getName() {
        return "Sunday Break";
    }

    @Override
    public boolean check(Shift[] agentSchedule) {
        for (int i = 0; i * 14 < agentSchedule.length; i++) {
            if (agentSchedule[6 + 14 * i].isWork() && agentSchedule[13 + 14 * i].isWork()) return false;
        }
        return true;
    }
}
