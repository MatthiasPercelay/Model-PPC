package nurses.specs;

public interface IProblemInstance extends ITTDimension {

	int getNbCycles();
	
	int getNbWeeks();
			
	default int getNbAgents() {
		return getTimeTable().getNbAgents();
	}

	default int getNbDays() {
		return getTimeTable().getNbDays();
	}

	ITimetable getTimeTable();
	
}
