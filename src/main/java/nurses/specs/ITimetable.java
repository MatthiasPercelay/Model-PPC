package nurses.specs;

import nurses.Shift;

public interface ITimetable extends ITTDimension {

	Shift getShift(int i, int j);

	boolean isWorkdayAssignment();
	
	boolean isShiftAssignment();
}
