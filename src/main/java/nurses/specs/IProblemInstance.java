package nurses.specs;

import ilog.opl.IloCustomOplDataSource;
import ilog.opl.IloOplFactory;
import nurses.NRConstants;

public interface IProblemInstance extends ITTDimension {

	int getNbCycles();
	
	default int getNbWeeks() {
		return NRConstants.WEEKS_PER_CYCLE * getNbCycles();
	}
			
	default int getNbAgents() {
		return getTimeTable().getNbAgents();
	}

	default int getNbDays() {
		return getTimeTable().getNbDays();
	}

	ITimetable getTimeTable();
	
	int[][] getDemands();
	
	int[] getWorkdays();
	
	int[] getBreaksPerCycle();
	
	int[][] getBreakPreferences();
	
	int[][][] getShiftPreferences();
	
	IloCustomOplDataSource toWorkdayDataSource(IloOplFactory oplF);
	
	IloCustomOplDataSource toShiftDataSource(IloOplFactory oplF);
	
}
