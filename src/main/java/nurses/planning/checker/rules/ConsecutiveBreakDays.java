package nurses.planning.checker.rules;

import nurses.Shift;

public class ConsecutiveBreakDays implements IRule {
    @Override
    public String getName() {
        return "Consecutive break days";
    }

    @Override
    public boolean check(Shift[] agentSchedule) {
        int cycle = 0;
        int breaks = 0;
        int maxBreaks = 0;
        for (int i = 0; i < agentSchedule.length; i++) {
            cycle++;
            Shift shift = agentSchedule[i];

            if (shift.isBreak()) {
                breaks++;
                if (breaks > maxBreaks) maxBreaks = breaks;
            } else breaks = 0;

            if (cycle == 13) {
                cycle = 0;
                if (maxBreaks < 2) return false;
            }
        }
        return true;
    }
}
