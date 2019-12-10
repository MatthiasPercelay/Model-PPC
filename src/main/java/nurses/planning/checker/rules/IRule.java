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

public interface IRule {
    public String getName();    // a rule has to have a name
    public boolean check(Shift[] agentSchedule);   // checks whether the planning of some agent obeys the rule
}
