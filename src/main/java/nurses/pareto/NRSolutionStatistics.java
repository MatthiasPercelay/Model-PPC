package nurses.pareto;

import nurses.Shift;
import nurses.planning.TimeTable;
import nurses.specs.IProblemInstance;
import nurses.specs.ITimetable;

public class NRSolutionStatistics {

    private ITimetable timetable;

    public NRSolutionStatistics(ITimetable timetable) {
        this.timetable = timetable;
    }

    public static MOSolution makeMOSolution(IProblemInstance instance,  Shift[][] shifts) {
        NRSolutionStatistics sol = new NRSolutionStatistics(new TimeTable(shifts));
        return new MOSolution(new TimeTable(shifts), sol.getObjectiveArray());
    }

    public double[] getObjectiveArray() {
        double[] res = new double[4];
        res[0] = -(double)getTotalWork();
        res[1] = (double)getTotalWeekends();
        res[2] = -(double)getTotalFiveDays();
        res[3] = -(double)getTotalSixDays();
        return res;
    }

    public int getTotalWork(int agent) {
        int worked = 0;
        for (int day = 1; day <= timetable.getNbDays(); day++) {
            if (timetable.getShift(agent, day).isWork()) {
                worked++;
            }
        }
        return worked;
    }

    public int getTotalWeekends(int agent) {
        int weekend = 0;

        for (int day = 1; day <= timetable.getNbDays(); day++) {

            // we have a break on a sunday and the previous saturday is a break too
            if (day % 7 == 0 && timetable.getShift(agent, day).isBreak() && timetable.getShift(agent, day - 1).isBreak()) {
                weekend++;
            }
        }
        return weekend;
    }

    private int getTotalNDays(int agent, int days) {
        int nDays = 0;
        int daysInARow = 0;
        for (int day = 1; day <= timetable.getNbDays(); day++) {
            if (timetable.getShift(agent, day).isWork()) {
                daysInARow++;
            }
            if (timetable.getShift(agent, day).isBreak()) {
                daysInARow = 0;
            }
            if (daysInARow >= days) {
                nDays++;
            }
        }

        return nDays;
    }

    public int getTotalFiveDays(int agent) {
        return getTotalNDays(agent, 5);
    }

    public int getTotalSixDays(int agent) {
        return getTotalNDays(agent, 6);
    }

    public int getTotalWork() {
        int total = 0;
        for (int i = 1; i <= timetable.getNbAgents(); i++) {
            total += getTotalWork(i);
        }
        return total;
    }

    public int getTotalWeekends() {
        int total = 0;
        for (int i = 1; i <= timetable.getNbAgents(); i++) {
            total += getTotalWeekends(i);
        }
        return total;
    }

    public int getTotalFiveDays() {
        int total = 0;
        for (int i = 1; i <= timetable.getNbAgents(); i++) {
            total += getTotalFiveDays(i);
        }
        return total;
    }

    public int getTotalSixDays() {
        int total = 0;
        for (int i = 1; i <= timetable.getNbAgents(); i++) {
            total += getTotalSixDays(i);
        }
        return total;
    }
}
