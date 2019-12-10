/*********************************************
 * OPL 12.9.0.0 Model
 * Author: JUNG
 * Creation Date: 25 nov. 2019 at 13:45:45
 *********************************************/
include "nursesCommon.mod";

string hebdomary_break = ...;

int DAYS_PER_CYCLE = DAYS_PER_WEEK * WEEKS_PER_CYCLE;

// MAIN DATA
int shiftPrefs[AGENTS][CYCLEDAYS][SHIFTS] = ...;    // what each agent wants or doesn't want

// DAY OF WORK AND FIXED SHIFT
int is_preference[i in AGENTS][j in DAYS][s in SHIFTS] = shiftPrefs[i][((j-1)%DAYS_PER_CYCLE) + 1][s] > 0;
int is_forbidden[i in AGENTS][j in DAYS][s in SHIFTS] = shiftPrefs[i][((j-1)%DAYS_PER_CYCLE) + 1][s] < 0;

// VARIABLES
// Assign a shift to an agent
dvar int shift_assign[AGENTS][DAYS] in 0..3; 

//////////////////////////////
// OBJECTIVE FUNCTION UTILS //
//////////////////////////////

// Number of DAY where an agent does EVENING and MORNING the next DAY.
dexpr int SM[i in AGENTS] = sum(j in 1..d-1) (shift_assign[i][j] == EVENING && shift_assign[i][j+1] == MORNING);
// Number of consecutive DAYs where we do the same shift
int CONSECUTIVE_DAYS = 3;
dexpr int sameShift[i in AGENTS][c in 2..CONSECUTIVE_DAYS] = sum(j in 1..d-c+1, s in SHIFTS) (sum(k in 0..c-1) (shift_assign[i][j+k] == s) == c);
// Number of time the agent switch shift from one DAY to the next one.
dexpr int shiftSwitch[i in AGENTS] = sum(j in 1..d-1, s in SHIFTS) ((shift_assign[i][j] == s && shift_assign[i][j+1] != s) * fixedWork[i][j+1]); 

// Number of preferences respected for each agent
dexpr int preferences[i in AGENTS] = sum(j in DAYS, s in SHIFTS) (shift_assign[i][j] == s && is_preference[i][j][s] == 1) ;
// Number of forbidden not respected for each agent
dexpr int interdictions[i in AGENTS] = sum(j in DAYS, s in SHIFTS) (shift_assign[i][j] == s && is_forbidden[i][j][s] == 1) ;

// Evaluation for each agent
dexpr int objectiveValuePerAgent[i in AGENTS] = 2*SM[i] + sameShift[i][2] + 4*interdictions[i] - preferences[i] + 2*shiftSwitch[i];
// Global evaluation of the solution
dexpr int objectiveValue = sum(i in AGENTS) objectiveValuePerAgent[i];
// Difference of value to the average for each agent
dexpr float differenceToAveragePerAgent[i in AGENTS] = abs(objectiveValue - objectiveValuePerAgent[i]*n) / n;
// Global difference to average
dexpr float differenceToAverage = sum(i in AGENTS) differenceToAveragePerAgent[i];

// Evening + Break + Morning/Day
dexpr int SBMJ[i in AGENTS][j in 2..d-1] = 
		(fixedWork[i][j] == 0 && fixedWork[i][j-1] == 1 && fixedWork[i][j+1] == 1 && (fixedShift[i][j-1] == 0 || fixedShift[i][j+1] == 0))
		* ((shift_assign[i][j-1] == EVENING) + (shift_assign[i][j+1] == MORNING) + (shift_assign[i][j+1] == DAY));

/*	OBJECTIVE FUNCTION
	GOALS :
		- Minimize the shift sequence EVENING + MORNING
		- Maximize the sequence of length 2 of the same shift
		- Maximize the preference satisfaction
		- Maximize the interdictions satisfaction
		- Minimize the difference to the average for each agent 
			(one does not get the timetable of his life and the other wants to kill himself)
*/
minimize objectiveValue + differenceToAverage;

subject to{

	///////////////////////
	// BASIC CONSTRAINTS //
	///////////////////////
	// If there is a demand, there is an agent
	forall(s in SHIFTS, j in DAYS)
		demands[s][j] == sum(i in AGENTS) (shift_assign[i][j] == s);

	forall(j in DAYS, i in AGENTS){
		// If the shift is already shift, then we must respect it	
		if(fixedShift[i][j] == 1) shift_assign[i][j] == SHIFT[timetable[i][j]];
		// If an agent must work a certain DAY, then he must have a shift
		if(fixedWork[i][j] == 1 && timetable[i][j] != "FO") shift_assign[i][j] != 0;
		if(fixedWork[i][j] == 0 || timetable[i][j] == "FO") shift_assign[i][j] == 0;
	}
	
	// If an agent has only one DAY for hebdomary break, then he must have more than 36 hours of break
 	forall(j in 2..d-1, i in AGENTS){
 		SBMJ[i][j] <= 1;
	}
 	
 	/////////////////////////////
 	// ADDITIONNAL CONSTRAINTS //
 	/////////////////////////////
 	
 	// TODO ... ?
 	 
}

// PRINT THE RESULT
execute POSTPROCESS{
	var no_work = true;
	for(var j in DAYS) write(j + "\t");
	writeln(); writeln();
	for(var i in AGENTS) {
		for(var j in DAYS){
			if(shift_assign[i][j] > 0) write(shift_assign[i][j]);
			else write("-")
            write("\t");
		}                                                 
        writeln();
	}       
}

