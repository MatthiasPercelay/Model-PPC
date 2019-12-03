package nurses.planning.checker;

import nurses.planning.Planning;
import nurses.planning.checker.rules.IRule;

import java.util.List;

public class PlanningChecker {
    private Planning planning;
    private List<IRule> rules;

    public PlanningChecker(Planning planning, List<IRule> rules) {
        this.planning = planning;
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
