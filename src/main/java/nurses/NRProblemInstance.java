/**
 * This file is part of nurse-rostering-solver, https://github.com/MatthiasPercelay/Model-PPC
 *
 * Copyright (c) 2019, Université Nice Sophia Antipolis. All rights reserved.
 *
 * Licensed under the BSD 3-clause license.
 * See LICENSE file in the project root for full license information.
 */
package nurses;
import java.io.File;

import ilog.opl.IloCustomOplDataSource;
import ilog.opl.IloOplDataHandler;
import ilog.opl.IloOplFactory;
import nurses.planning.TimeTable;
import nurses.specs.IProblemInstance;
import nurses.specs.ITimetable;

public class NRProblemInstance implements IProblemInstance {

	private final int nbCycles;
	private final ITimetable timetable;
	private final int[] workDays;
	private final int[] breaksPerCycle;
	private final int[][] demands;
	private final int[][] breakPreferences;
	private final int[][][] shiftPreferences;


	public NRProblemInstance(File instanceFile) {
		nbCycles = 2;
		timetable = new TimeTable(instanceFile, 2, 6);
		workDays = new int[] {10, 13, 10, 11, 17, 17};
		breaksPerCycle= new int[]{4, 4, 6, 6, 4, 4 };
		demands= new int[][]{
			{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
			{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
			{ 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 0, 1, 1, 0, 0 }
		};

		breakPreferences= new int[][]{
			{ 1, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 1, 1 },
			{ 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 1 },
			{ 0, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 1, 1 },
			{ 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1,},
			{ 2, 0, 0, 0, 1, 0, 0, 2, 0, 0, 0, 1, 0, 0 },
			{ 0, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 1, 1 },
		};

		shiftPreferences= new int[][][]{
			{
				{ 1, 0, 0 },    // L
				{ 0, 0, 0 },    // M
				{ 0, 0, 1 },    // M
				{ 0, 0, 1 },    // J
				{ 0, 0, 0 },    // V
				{ 0, 0, 0 },    // S
				{ 0, 0, 1 },    // D
				{ 1, 0, 0 },    // L
				{ 0, 0, 0 },    // M
				{ 0, 0, 1 },    // M
				{ 0, 0, 1 },    // J
				{ 0, 0, 0 },    // V
				{ 0, 0, 0 },    // S
				{ 0, 0, 1 }     // D
			}, 
			{
				{ 1, 0, 0 },    // L
				{ 0, 0, 0 },    // M
				{ 0, 0, 1 },    // M
				{ 0, 0, 1 },    // J
				{ 0, 0, 0 },    // V
				{ 0, 0, 0 },    // S
				{ 0, 0, 1 },    // D
				{ 1, 0, 0 },    // L
				{ 0, 0, 0 },    // M
				{ 0, 0, 1 },    // M
				{ 0, 0, 1 },    // J
				{ 0, 0, 0 },    // V
				{ 0, 0, 0 },    // S
				{ 0, 0, 1 }     // D
			},
			{
				{ 1, 0, 0 },    // L
				{ 0, 0, 0 },    // M
				{ 0, 0, 1 },    // M
				{ 0, 0, 1 },    // J
				{ 0, 0, 0 },    // V
				{ 0, 0, 0 },    // S
				{ 0, 0, 1 },    // D
				{ 1, 0, 0 },    // L
				{ 0, 0, 0 },    // M
				{ 0, 0, 1 },    // M
				{ 0, 0, 1 },    // J
				{ 0, 0, 0 },    // V
				{ 0, 0, 0 },    // S
				{ 0, 0, 1 }     // D
			},
			{
				{ 1, 0, 0 },    // L
				{ 0, 0, 0 },    // M
				{ 0, 0, 1 },    // M
				{ 0, 0, 1 },    // J
				{ 0, 0, 0 },    // V
				{ 0, 0, 0 },    // S
				{ 0, 0, 1 },    // D
				{ 1, 0, 0 },    // L
				{ 0, 0, 0 },    // M
				{ 0, 0, 1 },    // M
				{ 0, 0, 1 },    // J
				{ 0, 0, 0 },    // V
				{ 0, 0, 0 },    // S
				{ 0, 0, 1 }     // D
			},
			{
				{ 1, 0, 0 },    // L
				{ 0, 0, 0 },    // M
				{ 0, 0, 1 },    // M
				{ 0, 0, 1 },    // J
				{ 0, 0, 0 },    // V
				{ 0, 0, 0 },    // S
				{ 0, 0, 1 },    // D
				{ 1, 0, 0 },    // L
				{ 0, 0, 0 },    // M
				{ 0, 0, 1 },    // M
				{ 0, 0, 1 },    // J
				{ 0, 0, 0 },    // V
				{ 0, 0, 0 },    // S
				{ 0, 0, 1 }     // D
			},
			{
				{ 1, 0, 0 },    // L
				{ 0, 0, 0 },    // M
				{ 0, 0, 1 },    // M
				{ 0, 0, 1 },    // J
				{ 0, 0, 0 },    // V
				{ 0, 0, 0 },    // S
				{ 0, 0, 1 },    // D
				{ 1, 0, 0 },    // L
				{ 0, 0, 0 },    // M
				{ 0, 0, 1 },    // M
				{ 0, 0, 1 },    // J
				{ 0, 0, 0 },    // V
				{ 0, 0, 0 },    // S
				{ 0, 0, 1 }     // D
			}
		};

	}

	public NRProblemInstance(IProblemInstance instance, ITimetable timetable) {
		super();
		this.nbCycles = instance.getNbCycles();
		this.timetable = timetable;
		this.demands = instance.getDemands();
		this.workDays = instance.getWorkdays();
		this.breaksPerCycle = instance.getBreaksPerCycle();
		this.breakPreferences = instance.getBreakPreferences();
		this.shiftPreferences = instance.getShiftPreferences();
	}



	@Override
	public int getNbCycles() {
		return nbCycles;
	}

	@Override
	public ITimetable getTimeTable() {
		return timetable;
	}

	@Override
	public int[][] getDemands() {
		return demands;
	}

	@Override
	public int[] getWorkdays() {
		return workDays;
	}

	@Override
	public int[] getBreaksPerCycle() {
		return breaksPerCycle;
	}

	@Override
	public int[][] getBreakPreferences() {
		return breakPreferences;
	}

	@Override
	public int[][][] getShiftPreferences() {
		return shiftPreferences;
	}

	private class NRPOplDataSource extends IloCustomOplDataSource {

		public NRPOplDataSource(IloOplFactory oplEnv) {
			super(oplEnv);
		}

		@Override
		public void customRead() {
			final IloOplDataHandler handler = getDataHandler();
			///////////////////////////
			handler.startElement("n");
			handler.addIntItem(getNbAgents());
			handler.endElement();

			///////////////////////////
			handler.startElement("c");
			handler.addIntItem(getNbCycles());
			handler.endElement();

			///////////////////////////
			handler.startElement("useRelaxation");
			handler.addIntItem(0);
			handler.endElement();

			///////////////////////////
			handler.startElement("timetable");
			handler.startArray();
			for (int i=1;i<=getNbAgents();i++) {
				handler.startArray();
				for (int j=1;j<=getNbDays();j++) {
					handler.addStringItem(timetable.getShift(i-1, j-1).toString());
				}
				handler.endArray();
			}
			handler.endArray();
			handler.endElement();

			///////////////////////////
			handler.startElement("demands");
			handler.startArray();
			for (int i=1;i<=demands.length;i++) {
				handler.startArray();
				for (int j=1;j<=demands[i-1].length;j++)
					handler.addIntItem(demands[i-1][j-1]);
				handler.endArray();
			}
			handler.endArray();
			handler.endElement();

			///////////////////////////
			handler.startElement("workDays");
			handler.startArray();
			for (int i=1;i<=workDays.length;i++) {
				handler.addIntItem(workDays[i-1]);
			}
			handler.endArray();
			handler.endElement();

			///////////////////////////
			handler.startElement("breaksPerCycle");
			handler.startArray();
			for (int i=1;i<=breaksPerCycle.length;i++) {
				handler.addIntItem(breaksPerCycle[i-1]);
			}
			handler.endArray();
			handler.endElement();

			///////////////////////////
			handler.startElement("breakPrefs");
			handler.startArray();
			for (int i=1;i<=breakPreferences.length;i++) {
				handler.startArray();
				for (int j=1;j<=breakPreferences[i-1].length;j++)
					handler.addIntItem(breakPreferences[i-1][j-1]);
				handler.endArray();
			}
			handler.endArray();
			handler.endElement();
			///////////////////////////
			handler.startElement("shiftPrefs");
			
			
			handler.startArray();
			for (int i=1;i<=shiftPreferences.length;i++) {
				handler.startArray();
				for (int j=1;j<=shiftPreferences[i-1].length;j++) {
					handler.startArray();
					for (int k=1;k<=shiftPreferences[i-1][j-1].length;k++) {
						handler.addIntItem(shiftPreferences[i-1][j-1][k-1]);
					}
					
					handler.endArray();
				}
				handler.endArray();
			}
			handler.endArray();
			handler.endElement();
		}
	}

	@Override
	public IloCustomOplDataSource toWorkdayDataSource(IloOplFactory oplF) {
		return new NRPOplDataSource(oplF);
	}

	@Override
	public IloCustomOplDataSource toShiftDataSource(IloOplFactory oplF) {
		return new NRPOplDataSource(oplF);
	}

}
