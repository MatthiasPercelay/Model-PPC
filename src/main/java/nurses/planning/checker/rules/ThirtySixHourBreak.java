package nurses.planning.checker.rules;

import nurses.NRCmd;
import nurses.Shift;

import java.util.logging.Logger;

public class ThirtySixHourBreak implements IRule {
    @Override
    public String getName() {
        return "Weekly 36-hour break";
    }

    /**
     * @param agentSchedule schedule of the agent
     * @return true if the agent has at least one 36-hour break during each week
     */
    @Override
    public boolean check(Shift[] agentSchedule) {
        int nbNiceBreaks;
        for (int w = 0; w < agentSchedule.length - 6; w += 7) {    // for each week in the schedule
            nbNiceBreaks = 0;
            for (int i = 0; i < 6; i++) {   // for each day in the week
                if (agentSchedule[w + i].isBreak() && i > 0) {    // a "nice" break is a break that is not surrounded by S-M of S-J
                    if (agentSchedule[w + i - 1] == Shift.S) {
                        if (agentSchedule[w + i + 1] != Shift.M &&
                                agentSchedule[w + i + 1] != Shift.J) {
                            nbNiceBreaks++;
                        }
                    } else {
                        nbNiceBreaks++;
                    }
                }
            }
            if (nbNiceBreaks == 0) {
                if (agentSchedule[w + 5] == Shift.S && agentSchedule[w + 6] == Shift.B) {   // if we don't have a break until sunday
                    if (w != agentSchedule.length - 7) {    // if it's not the last sunday we check the next monday
                        if (agentSchedule[w+7] == Shift.M || agentSchedule[w+7] == Shift.J) {
                            return false;
                        } else {
                            continue;
                        }
                    } else {    // if it is the last sunday we give a warning
                        NRCmd.LOGGER.info("Warning : can't work on the first monday morning of next month");
                        continue;
                    }
                } else {
                    return false;
                }
            }
        }
        return true;
    }
}
