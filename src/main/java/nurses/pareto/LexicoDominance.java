package nurses.pareto;


import nurses.pareto.MOSolution;
import nurses.specs.IDominanceComparator;

import java.util.Comparator;

public class LexicoDominance implements IDominanceComparator, Comparator<MOSolution> {

    @Override
    public int compare(double[] objective1, double[] objective2) {
        if(objective1.length != objective2.length) { return 0; }
        for(int i = 0;i<objective1.length;i++){
            if(objective1[i] < objective2[i]) { return -1; }
            if(objective1[i] > objective2[i]) { return 1; }
        }
        return 0;
    }
    @Override
    public int compare(MOSolution s1, MOSolution s2) {
        return compare(s1.objective, s2.objective);
    }
}
