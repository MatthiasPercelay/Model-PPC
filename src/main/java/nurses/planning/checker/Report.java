/**
 * This file is part of nurse-rostering-solver, https://github.com/MatthiasPercelay/Model-PPC
 *
 * Copyright (c) 2019, Université Nice Sophia Antipolis. All rights reserved.
 *
 * Licensed under the BSD 3-clause license.
 * See LICENSE file in the project root for full license information.
 */
package nurses.planning.checker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Report {
    private Map<String, Integer> invalidRules;

    public Report() {
        this.invalidRules = new HashMap<>();
    }

    public void addFailedRule(String ruleName, int agentNb) {
        this.invalidRules.put(ruleName, agentNb);
    }

    public boolean getValidity() {
        return invalidRules.isEmpty();
    }
}
