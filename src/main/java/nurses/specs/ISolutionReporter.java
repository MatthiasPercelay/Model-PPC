package nurses.specs;

import java.util.List;

public interface ISolutionReporter {

    List<ITimetable> getBestSolutions(int k);
}
