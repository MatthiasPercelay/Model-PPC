package nurses.planning.checker.rules;

import nurses.Shift;

public interface IRule {
    public String getName();    // a rule has to have a name
    public boolean check(Shift[] agentSchedule);   // checks whether the planning of some agent obeys the rule
}
