/**
 * This file is part of nurse-rostering-solver, https://github.com/MatthiasPercelay/Model-PPC
 *
 * Copyright (c) 2020, Université Nice Sophia Antipolis. All rights reserved.
 *
 * Licensed under the BSD 3-clause license.
 * See LICENSE file in the project root for full license information.
 */
package nurses.pareto;

import nurses.specs.IParetoArchive;


import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ParetoArchiveL implements IParetoArchive {
    private List<MOSolution> solutions;
    final private ParetoDominance comp;

    public ParetoArchiveL() {
        solutions = new ArrayList<>();
        comp = new ParetoDominance();
    }

    @Override
    public void add(MOSolution mosol) {
        if (!isDominated(mosol)) {
            removeNowDominated();
            solutions.add(mosol);
        }
    }

    @Override
    public boolean isDominated(double[] objective) {
        for (MOSolution s : solutions) {
            if (comp.compare(s.getObjective(), objective) < 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void forEach(Consumer<MOSolution> consumer) {
        for (MOSolution s : solutions) {
            consumer.accept(s);
        }
    }

    @Override
    public int size() {
        return solutions.size();
    }

    public List<MOSolution> getSolutions() {
        return solutions;
    }

    private void removeNowDominated() {
        for (MOSolution s : solutions) {
            if (isDominated(s)) {
                solutions.remove(s);
                return;
            }
        }
    }
}
