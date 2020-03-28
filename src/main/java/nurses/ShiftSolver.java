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

	protected Shift getShiftValue(double value) {
		if (value == 1.0) return Shift.M;
		else if (value == 2.0) return Shift.S;
		else if (value == 3.0) return Shift.J;
		else return Shift.B;
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

		// for(int ii = 0 ; ii < s.length ; ii++){
		// 	for(int jj =0 ; jj < s[ii].length ; jj++){
		// 		System.out.print(s[ii][jj]);
		// 		System.out.print(" ");
		// 	}
		// 	System.out.print("\n");
		// }


		
		Shift[][] hardcoded = new Shift[][]{
			{Shift.S,Shift.RH,Shift.M,Shift.M,Shift.M,Shift.RH,Shift.RH,Shift.J,Shift.J,Shift.RH,Shift.J,Shift.S,Shift.S,Shift.S,Shift.ND,Shift.ND,Shift.ND,Shift.ND,Shift.ND,Shift.ND,Shift.ND,Shift.ND,Shift.ND,Shift.ND,Shift.ND,Shift.ND,Shift.ND,Shift.ND},
			{Shift.CA,Shift.CA,Shift.CA,Shift.CA,Shift.CA,Shift.RH,Shift.RH,Shift.RH,Shift.W,Shift.W,Shift.W,Shift.W,Shift.B,Shift.W,Shift.W,Shift.B,Shift.W,Shift.W,Shift.W,Shift.W,Shift.B,Shift.B,Shift.B,Shift.JF,Shift.W,Shift.W,Shift.W,Shift.W},
			{Shift.W,Shift.B,Shift.B,Shift.B,Shift.RA,Shift.B,Shift.B,Shift.W,Shift.B,Shift.RA,Shift.W,Shift.W,Shift.RH,Shift.RH,Shift.RA,Shift.RTT,Shift.RTT,Shift.RTT,Shift.RTT,Shift.RH,Shift.RH,Shift.RA,Shift.W,Shift.W,Shift.W,Shift.B,Shift.W,Shift.W},
			{Shift.W,Shift.W,Shift.W,Shift.W,Shift.W,Shift.RH,Shift.RH,Shift.RTT,Shift.RTT,Shift.RTT,Shift.RA,Shift.RTT,Shift.RH,Shift.RH,Shift.RTT,Shift.W,Shift.RA,Shift.W,Shift.B,Shift.RH,Shift.RH,Shift.W,Shift.B,Shift.W,Shift.B,Shift.W,Shift.B,Shift.B},
			{Shift.W,Shift.W,Shift.W,Shift.W,Shift.W,Shift.W,Shift.W,Shift.B,Shift.B,Shift.W,Shift.W,Shift.B,Shift.W,Shift.B,Shift.W,Shift.W,Shift.W,Shift.B,Shift.W,Shift.W,Shift.W,Shift.W,Shift.W,Shift.JF,Shift.W,Shift.W,Shift.W,Shift.B},
			{Shift.W,Shift.W,Shift.W,Shift.W,Shift.B,Shift.W,Shift.W,Shift.W,Shift.W,Shift.W,Shift.W,Shift.B,Shift.B,Shift.B,Shift.W,Shift.W,Shift.W,Shift.W,Shift.W,Shift.B,Shift.W,Shift.W,Shift.W,Shift.JF,Shift.CA,Shift.CA,Shift.RH,Shift.RH}
		};

		NRProblemInstance problem = new NRProblemInstance(instance,sol.getSolution());
		// problem.workday = sol.getSolution().getshifts();
		problem.workday = hardcoded;
		IloOplModel opl=oplF.createOplModel(def,cplex);
		//opl.addDataSource(instance.toShiftDataSource(oplF));
		opl.addDataSource(problem.toShiftDataSource(oplF));
		opl.generate();

		try {

			if(cplex.solve()) {		
				System.out.println("Solving .......................");	
				final int n = cplex.getSolnPoolNsolns();
				for (int i = 0; i < n; i++) {
					storeSolution(instance, opl, archive, i);
				}
                System.out.println("Number of solutions : " + archive.size());
				opl.postProcess();
				//opl.printSolution(System.out);
			}
		} catch (IloException e) {
			System.out.println("An exception happened");
			e.printStackTrace();
			return;
		}

		cplex.end();
		opl.end();
		tearDown();

	}

}
