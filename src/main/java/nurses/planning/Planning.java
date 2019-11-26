package nurses.planning;

import nurses.Shift;

public class Planning {
    private Shift[][] days;
    private int cycles;
    private int agents;

    public Planning(int cycles, int agents) {
        this.days = new Shift[agents][14 * cycles];
    }

    public int getCycles() {
        return cycles;
    }

    public int getAgents() {
        return agents;
    }

    /**
     *
     * @param k an agent
     * @return agent k's schedule on this planning
     */
    public Shift[] getAgentsSchedule(int k) {
        return days[k];
    }
}