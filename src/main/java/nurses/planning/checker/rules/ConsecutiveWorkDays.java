package nurses.planning.checker.rules;

import nurses.NRCmd;
import nurses.Shift;

import java.util.ArrayList;
import java.util.List;

public class ConsecutiveWorkDays implements IRule {

    public ConsecutiveWorkDays() {
    }

    @Override
    public String getName() {
        return "Number of consecutive work days";
    }

    @Override
    public boolean check(Shift[] agentSchedule) {
        List<String> warnings = new ArrayList<>();
        int consecutiveDays = 0;
        for (int i = 0; i < agentSchedule.length-7; i++) {
            for (int j = 0; j < 7; j++) {
                if (consecutiveDays == 7) {
                    if (warnings.size() > 0) {
                        warnings.remove(warnings.size() - 1);   // the 6-day warning is no longer needed
                    }
                    return false;
                }
                if (consecutiveDays == 6) {
                    warnings.add("Warning: an agent is working 6 days in a row (from day " + i + ")");
                }

                if (agentSchedule[i+j].isWork()) {
                    consecutiveDays += 1;
                } else {
                    consecutiveDays = 0;
                }
            }
        }

        for (String s : warnings) {
            NRCmd.LOGGER.info(s);
        }
        return true;
    }
}
