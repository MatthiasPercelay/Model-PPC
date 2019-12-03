package nurses.planning.checker;

import nurses.planning.TimeTable;
import nurses.planning.checker.rules.IRule;

import java.util.List;

public class PlanningChecker {
    private TimeTable planning;
    private List<IRule> rules;

    public PlanningChecker(TimeTable timeTable, List<IRule> rules) {
        this.planning = timeTable;
        this.rules = rules;
    }


    /**
     *
     * @return true if all the rules are satisfied for this planning
     */
    public boolean check() {
        for (IRule r : this.rules) {
            for (int i = 0; i < this.planning.getNbAgents(); i++) {
                if (!r.check(this.planning.getAgentsSchedule(i))) {
                    return false;
                }
            }
        }
        return true;
    }
}
