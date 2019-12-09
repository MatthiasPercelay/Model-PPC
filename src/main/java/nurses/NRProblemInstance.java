package nurses;
import ilog.opl.IloCustomOplDataSource;
import ilog.opl.IloOplDataHandler;
import ilog.opl.IloOplFactory;
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


	public NRProblemInstance(String instanceFile) {
		nbCycles = 2;
		timetable = null;
		workDays = new int[] {10, 13, 10, 11, 17, 17};
		breaksPerCycle= new int[]{4, 4, 6, 6, 4, 4 };
		demands= new int[][]{
			{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
			{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
			{1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 0, 1, 1, 0, 0 }
		};

		breakPreferences= new int[][]{
			{ 1, 0, 0, 0, 0, 1, 1 },
			{ 0, 0, 0, 0, 0, 1, 1 },
			{ 0, 0, 1, 0, 0, 1, 1 },
			{ 0, 0, 1, 0, 0, 0, 1 },
			{ 2, 0, 0, 0, 1, 0, 0 },
			{ 0, 0, 1, 0, 0, 1, 1 }	
		};

		shiftPreferences= new int[][][]{
			{
				{ 1, 0, 0, 0, 0, 1, 1 },
				{ 0, 0, 0, 0, 0, 1, 1 },
				{ 0, 0, 1, 0, 0, 1, 1 },
				{ 0, 0, 1, 0, 0, 0, 1 },
				{ 2, 0, 0, 0, 1, 0, 0 },
				{ 0, 0, 1, 0, 0, 1, 1 }
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

		private final boolean isWorkdayAssignment;

		public NRPOplDataSource(IloOplFactory oplEnv, boolean isWorkdayAssignment) {
			super(oplEnv);
			this.isWorkdayAssignment = isWorkdayAssignment;
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
			handler.startElement("timetable");
			handler.startArray();
			for (int i=1;i<=getNbAgents();i++) {
				handler.startArray();
				for (int j=1;j<=getNbDays();j++)
					handler.addStringItem(timetable.getShift(i, j).toString());
				handler.endArray();
			}
			handler.endArray();
			handler.endElement();

			///////////////////////////
			handler.startElement("demands");
			handler.startArray();
			for (int i=1;i<=demands.length;i++) {
				handler.startArray();
				for (int j=1;j<=demands[i].length;j++)
					handler.addIntItem(demands[i][j]);
				handler.endArray();
			}
			handler.endArray();
			handler.endElement();
			if(isWorkdayAssignment) {
				///////////////////////////
				handler.startElement("workDays");
				handler.startArray();
				for (int i=1;i<=workDays.length;i++) {
					handler.addIntItem(workDays[i]);
				}
				handler.endArray();
				handler.endElement();

				///////////////////////////
				handler.startElement("breaksPerCycle");
				handler.startArray();
				for (int i=1;i<=breaksPerCycle.length;i++) {
					handler.addIntItem(breaksPerCycle[i]);
				}
				handler.endArray();
				handler.endElement();

				///////////////////////////
				handler.startElement("breakPrefs");
				handler.startArray();
				for (int i=1;i<=breakPreferences.length;i++) {
					handler.startArray();
					for (int j=1;j<=breakPreferences[i].length;j++)
						handler.addIntItem(demands[i][j]);
					handler.endArray();
				}
				handler.endArray();
				handler.endElement();
			} else {
				///////////////////////////
				handler.startElement("shiftPrefs");
				handler.startArray();
				for (int i=1;i<=shiftPreferences.length;i++) {
					handler.startArray();
					for (int j=1;j<=shiftPreferences[i].length;j++)
						handler.addIntItem(demands[i][j]);
					handler.endArray();
				}
				handler.endArray();
				handler.endElement();
			}
		}
	}

	@Override
	public IloCustomOplDataSource toWorkdayDataSource(IloOplFactory oplF) {
		return new NRPOplDataSource(oplF, true);
	}

	@Override
	public IloCustomOplDataSource toShiftDataSource(IloOplFactory oplF) {
		return new NRPOplDataSource(oplF, false);
	}

}
