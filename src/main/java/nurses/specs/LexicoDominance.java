package nurses.specs;

public class LexicoDominance implements IDominanceComparator {

    @Override
    public int compare(double[] objective1, double[] objective2) {
        if(objective1.length != objective2.length) { return 0; }
        for(int i = 0;i<objective1.length;i++){
            if(objective1[i] < objective2[i]) { return -1; }
            if(objective1[i] > objective2[i]) { return 1; }
        }
        return 0;
    }
}
