/**
 * This file is part of nurse-rostering-solver, https://github.com/MatthiasPercelay/Model-PPC
 *
 * Copyright (c) 2019, Université Nice Sophia Antipolis. All rights reserved.
 *
 * Licensed under the BSD 3-clause license.
 * See LICENSE file in the project root for full license information.
 */
package nurses.planning.checker;

import nurses.NRCmd;
import nurses.planning.TimeTable;
import nurses.planning.checker.rules.IRule;
import nurses.specs.ITimetable;

import java.util.ArrayList;
import java.util.List;

public class PlanningChecker {
    private ITimetable planning;
    private List<IRule> rules;

    public PlanningChecker(ITimetable timeTable, List<IRule> rules) {
        this.planning = timeTable;
        this.rules = rules;
    }

    public PlanningChecker(ITimetable timeTable) {
        this.planning = timeTable;
        this.rules = new ArrayList<>();
    }
    /**
     *
     * @return true if all the rules are satisfied for this planning
     */
    public Report check() {
        Report report = new Report();
        for (int i = 0; i < this.planning.getNbAgents(); i++) {
            NRCmd.LOGGER.info("Processing agent " + i + ":");
            for (IRule r : rules) {
                if (!r.check(this.planning.getAgentsSchedule(i))) {
                    NRCmd.LOGGER.info("\t Rule " + r.getName() + " failed");
                    report.addFailedRule(r.getName(), i);
                }
            }
            NRCmd.LOGGER.info("\n");
        }
        NRCmd.LOGGER.info("----- Done");
        return report;
    }

    public void addRule(IRule r) {
        this.rules.add(r);
    }
}
