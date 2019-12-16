package nurses;

import nurses.pareto.LexicoDominance;
import nurses.pareto.MOSolution;
import nurses.specs.IParetoArchive;
import nurses.specs.ISolutionReporter;
import nurses.specs.ITimetable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class NRReporter implements ISolutionReporter {
    private final IParetoArchive archive;

    public NRReporter(IParetoArchive archive) {
        this.archive = archive;
    }

    @Override
    public List<ITimetable> getBestSolutions(int k) {
        // sort solutions by lexico order
        LexicoDominance dom = new LexicoDominance();
        List<MOSolution> sols = new ArrayList<>();

        archive.forEachSolution((Consumer<MOSolution>) moSolution -> sols.add(moSolution));

        sols.sort(dom);

        List<ITimetable> tables = new ArrayList<>();
        for (int i = 0; i < sols.size() && i < k; i++) {
            MOSolution sol = sols.get(i);
            tables.add(sol.getSolution());
        }
        return tables;
    }
}
