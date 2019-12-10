package nurses;

import java.util.Arrays;

import ilog.concert.IloException;
import ilog.concert.IloIntVarMap;
import ilog.opl.IloCplex;
import ilog.opl.IloOplModel;
import nurses.pareto.MOSolution;
import nurses.specs.IParetoArchive;
import nurses.specs.IProblemInstance;
import nurses.specs.IWorkdaySolver;

public class WorkdaySolver extends NRSolver implements IWorkdaySolver {

	public final String MODEL_FILE = "src/main/opl/ModPPC/model/testCustomData.mod";
	
	public WorkdaySolver() {}

	protected Shift getShiftValue(Shift shift, double value) {
		return shift == Shift.NA ? 
				(value == 0 ? Shift.B : Shift.W) : shift; 
	}
	
	protected void storeSolution(IProblemInstance instance, IloOplModel opl, IParetoArchive archive, int soln) {
		final IloIntVarMap work = opl.getElement("work").asIntVarMap();
		final int n = instance.getNbAgents();
		final int d = instance.getNbDays();
		final Shift[][] solution = new Shift[n][d];
		IloCplex cplex = opl.getCplex();
		System.out.println("SOLUTION");
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
			archive.add(new MOSolution(solution, new double[] {0, 0}));
		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void solve(IProblemInstance instance, IParetoArchive archive) {
		setUp(MODEL_FILE);
		IloCplex cplex;
		try {
			cplex = oplF.createCplex();
		} catch (IloException e) {
			e.printStackTrace();
			return;
		}

		IloOplModel opl=oplF.createOplModel(def,cplex);
		opl.addDataSource(instance.toWorkdayDataSource(oplF));
		opl.generate();

		try {
			if(cplex.solve()) {			
				final int n = cplex.getSolnPoolNsolns();
				for (int i = 0; i < n; i++) {
					storeSolution(instance, opl, archive, i);
				}
				opl.postProcess();
				opl.printSolution(System.out);
			}
		} catch (IloException e) {
			e.printStackTrace();
			return;
		}
		// Do not change the instruction order !
		cplex.end();
		opl.end();
		tearDown();
	}

}


