/*********************************************
 * OPL 12.8.0.0 Model
 * Author: Thomas
 * Creation Date: 25 nov. 2019 at 14:11:57
 *********************************************/ 

include "nursesCommon.mod";
 
 
int useRelaxation = ...; 

int workDays[AGENTS] = ...;
 
int breaksPerCycle[AGENTS] = ...;
  
int breakPrefs[AGENTS][CYCLEDAYS] = ...;

range SHIFTS2 = 1..3;
int shiftPrefs[AGENTS][CYCLEDAYS][SHIFTS2] = ...;

execute POSTPROCESS{
	write(n, " ", c, " ", useRelaxation);
	writeln(timetable);
	writeln(demands);
	writeln(workDays);
	writeln(breaksPerCycle);
	writeln(breakPrefs);
	writeln(shiftPrefs);
}
