/*********************************************
 * OPL 12.8.0.0 Model
 * Author: Thomas
 * Creation Date: 25 nov. 2019 at 14:11:57
 *********************************************/ 

include "nursesCommon.mod";
 
dvar boolean work[AGENTS][DAYS];	

minimize sum(i in AGENTS, j in DAYS) work[i][j];

subject to {
 forall(j in DAYS) {
 	sum(i in AGENTS) work[i][j] >= demand[j]; 
 }
}
execute POSTPROCESS{
	write(n, " ", c, " ", useRelaxation);
	writeln(timetable);
	writeln(demands);
	writeln(workDays);
	writeln(breaksPerCycle);
	writeln(breakPrefs);
	writeln(shiftPrefs);
}
