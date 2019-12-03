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
