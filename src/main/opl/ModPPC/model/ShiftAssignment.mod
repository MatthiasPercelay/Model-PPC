/*********************************************
 * OPL 12.9.0.0 Model
 * Author: JUNG
 * Creation Date: 25 nov. 2019 at 13:45:45
 *********************************************/
include "nursesCommon.mod";

string workday[AGENTS][DAYS] = ...;

// Constant used for lisibility (could hard code those)
int MORNING = 1;
int EVENING = 2;
int DAY = 3;
int SHIFT[{"M", "J", "S"}] = [MORNING, DAY, EVENING];

// DAY OF WORK AND FIXED SHIFT
// can be simplified as j % DAYS_PER_CYCLE?
int is_preference[i in AGENTS][j in DAYS][s in SHIFTS] = shiftPrefs[i][((j-1)%DAYS_PER_CYCLE) + 1][s] > 0;
int is_forbidden[i in AGENTS][j in DAYS][s in SHIFTS] = shiftPrefs[i][((j-1)%DAYS_PER_CYCLE) + 1][s] < 0;
// abundant conditions rhs?
int fixedWork_[i in AGENTS][j in DAYS] = (fixedWork[i][j] == 1 || workday[i][j] == "W");

// VARIABLES
// Assign a shift to an agent
dvar int work[AGENTS][DAYS] in 0..3;

//////////////////////////////
// OBJECTIVE FUNCTION UTILS //
//////////////////////////////

// Number of DAY where an agent does EVENING and MORNING the next DAY.
dexpr int SM[i in AGENTS] = sum(j in 1..d-1) (work[i][j] == EVENING && work[i][j+1] == MORNING);
// Number of consecutive DAYs where we do the same shift
int CONSECUTIVE_DAYS = 4;
dexpr int sameShift[i in AGENTS][c in 2..CONSECUTIVE_DAYS] = sum(j in 1..d-c+1, s in SHIFTS) (sum(k in 0..c-1) (work[i][j+k] == s) == c);
// Number of time the agent switch shift from one DAY to the next one.
// shift switch times
dexpr int shiftSwitch[i in AGENTS] = sum(j in 1..d-1, s in SHIFTS) ((work[i][j] == s && work[i][j+1] != s) * fixedWork_[i][j+1]); 
// Number of preferences respected for each agent
dexpr int preferences[i in AGENTS] = sum(j in DAYS, s in SHIFTS) (work[i][j] == s && is_preference[i][j][s] == 1) ;
// Number of forbidden not respected for each agent
dexpr int interdictions[i in AGENTS] = sum(j in DAYS, s in SHIFTS) (work[i][j] == s && is_forbidden[i][j][s] == 1) ;


// Differents objectives
// DEFAULT OBJECTIVE
dexpr int objective_0[i in AGENTS] = 2*SM[i] + sameShift[i][4] + 2*shiftSwitch[i];

// LESS SPECIFIC OBJECTIVES
dexpr int objective_1[i in AGENTS] = 2*SM[i] + shiftSwitch[i];
dexpr int objective_2[i in AGENTS] = -sameShift[i][2];
dexpr int objective_3[i in AGENTS] = 0;

dexpr int objectives[i in AGENTS] = OBJECTIVE_SHIFT == 0 ? objective_0[i] : 
									OBJECTIVE_SHIFT == 1 ? objective_1[i] : 
									OBJECTIVE_SHIFT == 2 ? objective_2[i] : objective_3[i];

// How much do we respect the preferences of each agent
// More weight to respect the interdictions than the actual preferences
dexpr int preferences_respect[i in AGENTS] = 4*interdictions[i] - preferences[i];

// Evaluation for each agent
// overall objective + respect for each agent
dexpr int objectiveValuePerAgent[i in AGENTS] = objectives[i] + preferences_respect[i];

// Global evaluation of the solution
dexpr int objectiveValue = sum(i in AGENTS) objectiveValuePerAgent[i];
// Difference of value to the average for each agent
dexpr float differenceToAveragePerAgent[i in AGENTS] = abs(objectiveValue - objectiveValuePerAgent[i]*n) / n;
// Global difference to average
dexpr float differenceToAverage = sum(i in AGENTS) differenceToAveragePerAgent[i];

// Evening + Break + Morning/Day
dexpr int SBMJ[i in AGENTS][j in 2..d-1] = 
		(fixedWork_[i][j] == 0 && fixedWork_[i][j-1] == 1 && fixedWork_[i][j+1] == 1 && (fixedShift[i][j-1] == 0 || fixedShift[i][j+1] == 0))
		* ((work[i][j-1] == EVENING) + (work[i][j+1] == MORNING) + (work[i][j+1] == DAY));

/*	OBJECTIVE FUNCTION
	GOALS :
		- Minimize the shift sequence EVENING + MORNING
		- Maximize the sequence of length 2 of the same shift
		- Maximize the preference satisfaction
		- Maximize the interdictions satisfaction
		- Minimize the difference to the average for each agent 
			(one does not get the timetable of his life and the other wants to kill himself)
*/


minimize objectiveValue + differenceToAverage * OBJECTIVE_SHIFT_USE_AVERAGE; // OBJECTIVE_SHIFT_USE_AVERAGE=0

subject to{

	///////////////////////
	// BASIC CONSTRAINTS //
	///////////////////////
	// If there is a demand, there is an agent
	forall(s in SHIFTS, j in DAYS){
		demands[s][j] <= sum(i in AGENTS) (work[i][j] == s);
		if(demands[s][j] == 0) sum(i in AGENTS) (work[i][j] == s) == 0;
	}		

	forall(j in DAYS, i in AGENTS){
		// If the shift is already shift, then we must respect it	
		if(fixedShift[i][j] == 1) work[i][j] == SHIFT[workday[i][j]];
		// If an agent must work a certain DAY, then he must have a shift
		if(fixedWork_[i][j] == 1 && workday[i][j] != "FO") work[i][j] != 0;
		if(fixedWork_[i][j] == 0 || workday[i][j] == "FO") work[i][j] == 0;
	}
	
	// If an agent has only one DAY for hebdomary break, then he must have more than 36 hours of break (so no S-B-M or S-B-J sequence)
 	forall(j in 2..d-1, i in AGENTS)
 		SBMJ[i][j] <= 1;
 	
 	/////////////////////////////
 	// ADDITIONNAL CONSTRAINTS //
 	/////////////////////////////
 	
 	// TODO ... ?
 	 
}

execute PREPROCESS {
	cplex.mipdisplay = 5
}

execute {
	writeln("SM: ", SM);
	writeln("sameShift: ", sameShift);
	writeln("shiftSwitch: ", shiftSwitch);
	writeln("preferences: ", preferences);
	writeln("interdictions: ", interdictions);	
}

// PRINT THE RESULT
execute POSTPROCESS{
	var no_work = true;
	for(var j in DAYS) write(j + "\t");
	writeln(); writeln();
	for(var i in AGENTS) {
		for(var j in DAYS){
			if(work[i][j] > 0) {
				if(work[i][j] == 2) write("S");
				else if(work[i][j] == 1) write("M");
				else if(work[i][j] == 3) write("J");
				else write("?");			
			}
			else write("-")
            write("\t");
		}                                                 
        writeln();
	}       
}

