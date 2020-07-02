/**
 * This file is part of nurse-rostering-solver, https://github.com/MatthiasPercelay/Model-PPC
 *
 * Copyright (c) 2020, Université Nice Sophia Antipolis. All rights reserved.
 *
 * Licensed under the BSD 3-clause license.
 * See LICENSE file in the project root for full license information.
 */
package nurses.specs;

import nurses.Shift;

public interface ITimetable extends ITTDimension {

	Shift getShift(int i, int j);

	boolean isWorkdayAssignment();
	
	boolean isShiftAssignment();

	Shift[] getAgentsSchedule(int agent);
}
