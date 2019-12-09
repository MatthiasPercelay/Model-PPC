package nurses;

import ilog.concert.IloException;
import ilog.opl.IloCplex;
import ilog.opl.IloOplErrorHandler;
import ilog.opl.IloOplFactory;
import ilog.opl.IloOplModel;
import ilog.opl.IloOplModelDefinition;
import ilog.opl.IloOplModelSource;
import ilog.opl.IloOplSettings;
import nurses.specs.IParetoArchive;
import nurses.specs.IProblemInstance;
import nurses.specs.IWorkdaySolver;

public class WorkdaySolver implements IWorkdaySolver {

	public WorkdaySolver() {}

	@Override
	public void solve(IProblemInstance instance, IParetoArchive archive) {
		IloOplFactory oplF = new IloOplFactory();
		IloOplErrorHandler errHandler = oplF.createOplErrorHandler(System.out);
		IloOplModelSource modelSource = oplF.createOplModelSource("src/main/opl/ModPPC/model/testCustomData.mod");
		IloOplSettings settings = oplF.createOplSettings(errHandler);
		IloOplModelDefinition def=oplF.createOplModelDefinition(modelSource,settings);
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
			//while(cplex.get)
			if(cplex.solve()) {
				// cplex.getValue(opl.getElement("x").asIntVar(), 1);
				System.out.println(cplex.getSolnPoolNsolns());
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
		modelSource.end();
		settings.end();
		def.end();
		errHandler.end();
		oplF.end();

	}

}
