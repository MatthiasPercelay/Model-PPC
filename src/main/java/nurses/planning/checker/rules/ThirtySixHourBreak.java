package nurses.planning.checker.rules;

import nurses.Shift;

public class ThirtySixHourBreak implements IRule {
    @Override
    public String getName() {
        return "Weekly 36-hour break";
    }

    @Override
    public boolean check(Shift[] agentSchedule) {
        return false;
    }
}
