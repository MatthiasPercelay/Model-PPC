/**
 * This file is part of nurse-rostering-solver, https://github.com/MatthiasPercelay/Model-PPC
 *
 * Copyright (c) 2020, Université Nice Sophia Antipolis. All rights reserved.
 *
 * Licensed under the BSD 3-clause license.
 * See LICENSE file in the project root for full license information.
 */
package nurses.specs;

import ilog.opl.IloCustomOplDataSource;
import ilog.opl.IloOplFactory;
import nurses.NRConstants;
import nurses.NRExtargs;

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

	void setExtArgs(NRExtargs args);

	void relaxWday();

	void relaxShift();

	ITimetable getTimeTable();
	
	int[][] getDemands();
	
	int[] getWorkdays();
	
	int[] getBreaksPerCycle();
	
	int[][] getBreakPreferences();
	
	int[][][] getShiftPreferences();
	
	IloCustomOplDataSource toWorkdayDataSource(IloOplFactory oplF);
	
	IloCustomOplDataSource toShiftDataSource(IloOplFactory oplF);
	
}
