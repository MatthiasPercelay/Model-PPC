package nurses.planning;

import nurses.Shift;

public class Planning {
    private Shift[][] days;
    int cycles;
    int agents;

    public Planning(int cycles, int agents) {
        this.days = new Shift[agents][14 * cycles];
    }

    public int getCycles() {
        return cycles;
    }

    public int getAgents() {
        return agents;
    }
}