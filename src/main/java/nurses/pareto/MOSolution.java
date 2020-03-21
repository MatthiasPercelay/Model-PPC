/**
 * This file is part of nurse-rostering-solver, https://github.com/MatthiasPercelay/Model-PPC
 *
 * Copyright (c) 2020, Université Nice Sophia Antipolis. All rights reserved.
 *
 * Licensed under the BSD 3-clause license.
 * See LICENSE file in the project root for full license information.
 */
package nurses.pareto;

import nurses.Shift;
import nurses.planning.TimeTable;
import nurses.specs.ITimetable;

import java.util.Arrays;

public final class MOSolution {

	public final TimeTable solution;

	public final double[] objective;

	public MOSolution(TimeTable solution, double[] objective) {
		super();
		this.solution = solution;
		this.objective = objective;
	}

	public MOSolution(Shift[][] shifts, double[] objective){
		super();
		this.solution = new TimeTable(shifts);
		this.objective = objective;
	}

	public final TimeTable getSolution() {
		return solution;
	}

	public final double[] getObjective() {
		return objective;
	}

	public String toCSV() {
		StringBuilder b = new StringBuilder();
		if(objective.length > 0) {
			b.append(objective[0]);
			for (int i = 1; i < objective.length; i++) {
				b.append(',').append(objective[i]);
			}
		}
		return b.toString();
	}

	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		if(solution != null) {
			return b.append("MOSolution [\nobjective=").
					append(Arrays.toString(objective)).
					append(",\nsolution=").
					append(solution).
					append("\n]").toString();
		} else {
			return "objective=" +Arrays.toString(objective);
		}
	}

}

