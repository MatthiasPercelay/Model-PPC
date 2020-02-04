package nurses;

import ilog.concert.IloException;
import ilog.concert.IloIntVarMap;
import ilog.opl.IloCplex;
import ilog.opl.IloOplModel;
import nurses.pareto.MOSolution;
import nurses.pareto.NRSolutionStatistics;
import nurses.specs.IParetoArchive;
import nurses.specs.IProblemInstance;
import nurses.specs.IShiftSolver;

import java.util.function.Consumer;

public class ShiftSolver extends NRSolver implements IShiftSolver {

	public final String MODEL_FILE = "src/main/opl/ModPPC/model/WorkDayAssignment.mod";
	private int useRelaxation = 0;

	public ShiftSolver() {
		// TODO Auto-generated constructor stub
	}

	Shift getShiftValue(Shift shift, double value) {
		if (shift == Shift.NA) return Shift.NA;
		if (value == 1) return Shift.M;
		else if (value == 2) return Shift.S;
		else return Shift.J;
	}

	void storeSolution(IProblemInstance instance, IloOplModel opl, IParetoArchive archive, int soln) {
		final IloIntVarMap work = opl.getElement("work").asIntVarMap();
		final int n = instance.getNbAgents();
		final int d = instance.getNbDays();
		final Shift[][] solution = new Shift[n][d];
		IloCplex cplex = opl.getCplex();
		try {
			for (int i = 1; i <= n; i++) {
				IloIntVarMap worki = work.getSub(i);
				for (int j = 1; j <= d; j++) {
					solution[i - 1][j - 1] = getShiftValue(
							instance.getTimeTable().getShift(i, j),
							cplex.getValue(worki.get(j), soln)
					);
				}
			}
			MOSolution msol = NRSolutionStatistics.makeMOSolution(instance, solution);
			archive.add(msol);
		} catch (IloException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void solve(IProblemInstance instance, IParetoArchive workdayArchive, IParetoArchive archive) {
		useRelaxation = 0;

		Consumer<MOSolution> consumer = sol -> {
			setUp(MODEL_FILE);
			IloCplex cplex;
			try {
				cplex = oplF.createCplex();
			} catch (IloException e) {
				e.printStackTrace();
				return;
			}

			IloOplModel opl = oplF.createOplModel(def, cplex);
			NRProblemInstance shiftInstance = new NRProblemInstance(instance, sol.solution);
			opl.addDataSource(shiftInstance.toWorkdayDataSource(oplF, useRelaxation, 0));
			opl.generate();
			try {
				if (cplex.solve()) {
					final int n = cplex.getSolnPoolNsolns();
					for (int i = 0; i < n; i++) {
						storeSolution(instance, opl, archive, i);
					}
					System.out.println("Number of solutions : " + archive.size());
					opl.postProcess();
				}
			} catch (IloException e) {
				e.printStackTrace();
				return;
			}
			cplex.end();
			opl.end();
			tearDown();
		};

		workdayArchive.forEachSolution(consumer);
		if (archive.size() == 0) {
			useRelaxation = 1;
			workdayArchive.forEachSolution(consumer);
		}
	}

}
