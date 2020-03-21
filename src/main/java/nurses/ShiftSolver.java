/**
 * This file is part of nurse-rostering-solver, https://github.com/MatthiasPercelay/Model-PPC
 *
 * Copyright (c) 2020, Université Nice Sophia Antipolis. All rights reserved.
 *
 * Licensed under the BSD 3-clause license.
 * See LICENSE file in the project root for full license information.
 */
package nurses;

import java.util.Arrays;
import java.util.List;
import ilog.concert.IloException;
import ilog.concert.IloIntVarMap;
import ilog.opl.IloCplex;
import ilog.opl.IloOplModel;
import nurses.pareto.MOSolution;
import nurses.pareto.NRSolutionStatistics;
import nurses.specs.IParetoArchive;
import nurses.specs.IProblemInstance;
import nurses.specs.IShiftSolver;
import nurses.pareto.ParetoArchiveL;
public class ShiftSolver extends NRSolver implements IShiftSolver {

	public final String MODEL_FILE = "src/main/opl/ModPPC/model/ShiftAssignment.mod";


	public ShiftSolver() {}

	protected Shift getShiftValue(Shift shift, double value) {
		return shift == Shift.NA ? 
				(value == 0 ? Shift.B : Shift.W) : shift; 
	}


	protected void storeSolution(IProblemInstance instance, IloOplModel opl, ParetoArchiveL archive, int soln) {
		final IloIntVarMap work = opl.getElement("work").asIntVarMap();
		final int n = instance.getNbAgents();
		final int d = instance.getNbDays();
		final Shift[][] solution = new Shift[n][d];
		IloCplex cplex = opl.getCplex();
		//System.out.println("SOLUTION");
		try {
			for (int i = 1; i <= n; i++) {
				IloIntVarMap worki = work.getSub(i);
				for (int j = 1; j <= d; j++) {
					solution[i-1][j-1] = getShiftValue(
							instance.getTimeTable().getShift(i, j),
							cplex.getValue(worki.get(j), soln)
							);
				}
				System.out.println(Arrays.toString(solution[i-1]));
			}
			MOSolution msol = NRSolutionStatistics.makeMOSolution(instance, solution);
			archive.add(msol);
			//archive.add(new MOSolution(solution, new double[] {0, 0}));
		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	@Override
	public void solve(IProblemInstance instance, ParetoArchiveL workdayArchive, ParetoArchiveL archive) {
		setUp(this.MODEL_FILE);
		IloCplex cplex;
		try {
			cplex = oplF.createCplex();
		} catch (IloException e) {
			e.printStackTrace();
			return;
		}
		List<MOSolution> data = workdayArchive.getSolutions();
		MOSolution sol = data.get(0);
		System.out.println("sol--------------------------------------------------------------------------------------");
		Shift[][] s = sol.getSolution().getshifts();
		System.out.println(s);


		NRProblemInstance problem = new NRProblemInstance(instance,sol.getSolution());
		problem.workday = sol.getSolution().getshifts();
		IloOplModel opl=oplF.createOplModel(def,cplex);
		//opl.addDataSource(instance.toShiftDataSource(oplF));
		opl.addDataSource(problem.toShiftDataSource(oplF));
		opl.generate();

		try {
			if(cplex.solve()) {			
				final int n = cplex.getSolnPoolNsolns();
				for (int i = 0; i < n; i++) {
					storeSolution(instance, opl, archive, i);
				}
                System.out.println("Number of solutions : " + archive.size());
				opl.postProcess();
				//opl.printSolution(System.out);
			}
		} catch (IloException e) {
			e.printStackTrace();
			return;
		}

		cplex.end();
		opl.end();
		tearDown();

	}

}
