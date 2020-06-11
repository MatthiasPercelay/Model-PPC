/*********************************************
 * OPL 12.9.0.0 Model
 * Author: JUNG
 * Creation Date: 25 nov. 2019 at 13:45:45
 *********************************************/
include "nursesCommon.mod";

//-------------------------------- Definition of variables ------------------------------

string workday[AGENTS][DAYS] = ...;

// Constant used for lisibility (could hard code those)
int MORNING = 1;
int EVENING = 2;
int DAY = 3;
int SHIFT[{"M", "J", "S"}] = [MORNING, DAY, EVENING];

// DAY OF WORK AND FIXED SHIFT
int is_preference[i in AGENTS][j in DAYS][s in SHIFTS] = shiftPrefs[i][((j-1)%DAYS_PER_CYCLE) + 1][s] > 0;
int is_forbidden[i in AGENTS][j in DAYS][s in SHIFTS] = shiftPrefs[i][((j-1)%DAYS_PER_CYCLE) + 1][s] < 0;
int fixedWork_[i in AGENTS][j in DAYS] = (fixedWork[i][j] == 1 || workday[i][j] == "W");

// solution
dvar int work[AGENTS][DAYS] in 0..3;


//-------------------------------- Definition of objectives ------------------------------

// indicator of if there is a 36h break on each day, taking every 3 days when judge
// the former one SBMJ below is forbidding every possible existence of S-B-M/J
//dexpr int SBMJ[i in AGENTS][j in 2..d-1] = 
//		(fixedWork_[i][j] == 0 && fixedWork_[i][j-1] == 1 && fixedWork_[i][j+1] == 1 && (fixedShift[i][j-1] == 0 || fixedShift[i][j+1] == 0))
//		* ((work[i][j-1] == EVENING) + (work[i][j+1] == MORNING) + (work[i][j+1] == DAY));
dexpr int cons36hBreak[i in AGENTS][j in 1..d] = (j==1) ? (1 - maxl(fixedWork_[i][1], fixedWork_[i][2])) :  // on day1, if day1 and day2 are all break then true
												 (j==d) ? (1 - maxl(fixedWork_[i][d-1], fixedWork_[i][d])) :  // on day 28, if day27 and day28 are all break then true
		((fixedWork_[i][j-1] + fixedWork_[i][j] == 0) || (fixedWork_[i][j] + fixedWork_[i][j+1] == 0) ||
		(fixedWork_[i][j] == 0 && work[i][j-1] != EVENING) || (fixedWork_[i][j] == 0 && work[i][j+1] == EVENING));

////// obj for relaxation, fixedShift can be violated and assigned to additional agents
// this is the first priority, viloate the least fixed shifts when using relaxation
dexpr int violatedFixedShift[i in AGENTS][j in 1..d] = fixedShift[i][j] == 1 ? (SHIFT[workday[i][j]] != work[i][j]) : 0;


////// obj for customized requirements
// Number of DAY where an agent does EVENING and MORNING the next DAY.
dexpr int SM[i in AGENTS] = sum(j in 1..d-1) (work[i][j] == EVENING && work[i][j+1] == MORNING);

/*   customized objectives, not required in the description
// not required
// Number of consecutive DAYs where we do the same shift
int CONSECUTIVE_DAYS = 4;
dexpr int sameShift[i in AGENTS][c in 2..CONSECUTIVE_DAYS] = sum(j in 1..d-c+1, s in SHIFTS) (sum(k in 0..c-1) (work[i][j+k] == s) == c);
// Number of time the agent switch shift from one DAY to the next one.
// shift switch times
dexpr int shiftSwitch[i in AGENTS] = sum(j in 1..d-1, s in SHIFTS) ((work[i][j] == s && work[i][j+1] != s) * fixedWork_[i][j+1]); 
*/

dexpr int customRequire[i in AGENTS] = OBJECTIVE_SHIFT == 0? 0 : SM[i]; 

////// obj for preferences
// Number of preferences respected for each agent
dexpr int preferences[i in AGENTS] = sum(j in DAYS, s in SHIFTS) ((work[i][j] == s && is_preference[i][j][s] == 1) ||
																  (work[i][j] == s && fixedShift[i][j] == 1)) ;
// Number of interdictions for each agent
dexpr int interdictions[i in AGENTS] = sum(j in DAYS, s in SHIFTS) (work[i][j] == s && is_forbidden[i][j][s] == 1) ;

/* all are customized objectives which are not required in the problem description
// Differents objectives
// DEFAULT OBJECTIVE
dexpr int objective_0[i in AGENTS] = 2*SM[i] + sameShift[i][4] + 2*shiftSwitch[i];

// LESS SPECIFIC OBJECTIVES
dexpr int objective_1[i in AGENTS] = 2*SM[i] + shiftSwitch[i];
dexpr int objective_2[i in AGENTS] = -sameShift[i][2];
dexpr int objective_3[i in AGENTS] = 0;

dexpr int objectives[i in AGENTS] = OBJECTIVE_SHIFT == 0 ? objective_0[i] : 
									OBJECTIVE_SHIFT == 1 ? objective_1[i] : 
									OBJECTIVE_SHIFT == 2 ? objective_2[i] : objective_3[i]; */

// How much do we respect the preferences of each agent
// More weight to respect the interdictions than the actual preferences
//dexpr int preferences_respect[i in AGENTS] = 4*interdictions[i] - preferences[i];
dexpr int preferences_respect[i in AGENTS] = interdictions[i] - preferences[i];


////// obj for balance
// overall objective + respect for each agent
dexpr int objectiveValuePerAgent[i in AGENTS] = customRequire[i] + preferences_respect[i];
// Global evaluation of the solution
dexpr int objectiveValue = sum(i in AGENTS) preferences_respect[i];
// Difference of value to the average for each agent
dexpr float differenceToAveragePerAgent[i in AGENTS] = abs(objectiveValue - objectiveValuePerAgent[i]*n) / n;


//-------------------------------- Definition of final objective functions ------------------------------

dexpr int objRelax = sum(i in AGENTS)(sum(j in 1..d) (violatedFixedShift[i][j]));
dexpr int objCustom = sum(i in AGENTS) customRequire[i];
dexpr int objPref = sum(i in AGENTS)preferences_respect[i];
dexpr float objBalance = max(i in AGENTS) differenceToAveragePerAgent[i] - min(i in AGENTS) differenceToAveragePerAgent[i];
//dexpr float objBalance = sum(i in AGENTS) differenceToAveragePerAgent[i];



/*	OBJECTIVE FUNCTION
	GOALS :
		- Minimize the shift sequence EVENING + MORNING
		- Maximize the sequence of length 2 of the same shift
		- Maximize the preference satisfaction
		- Maximize the interdictions satisfaction
		- Minimize the difference to the average for each agent 
			(one does not get the timetable of his life and the other wants to kill himself)
*/

minimize staticLex(objRelax*useRelaxation2, objPref, objBalance*OBJECTIVE_SHIFT_USE_AVERAGE, objCustom);


//-------------------------------- Definition of constraints ------------------------------

subject to{

	// If there is a demand, there is an agent
	forall(s in SHIFTS, j in DAYS){
		demands[s][j] <= sum(i in AGENTS) (work[i][j] == s);
		if(demands[s][j] == 0) sum(i in AGENTS) (work[i][j] == s) == 0;
	}		

	// respect fixed shifts
	forall(j in DAYS, i in AGENTS){
		// If use relaxation, fixed shift can be violated, otherwise must be respected
		if (useRelaxation2 == 0){	
			if(fixedShift[i][j] == 1) work[i][j] == SHIFT[workday[i][j]];
		}	
		// If an agent must work a certain DAY, then he must have a shift
		if(fixedWork_[i][j] == 1 && workday[i][j] != "FO" && workday[i][j] != "EX") work[i][j] != 0;
		if(fixedWork_[i][j] == 0 || workday[i][j] == "FO" || workday[i][j] == "EX") work[i][j] == 0;
	}
	
//	// If an agent has only one DAY for hebdomary break, then he must have more than 36 hours of break (so no S-B-M or S-B-J sequence)
//  // changed to the constraint below
// 	forall(j in 2..d-1, i in AGENTS)
// 		SBMJ[i][j] <= 1;

	// each agent should have at least one 36h break each week 
 	forall (w in WEEKS, i in AGENTS){
 	    sum(d in 1..DAYS_PER_WEEK) (cons36hBreak[i][(w-1)*7+d]) >= 1;
 	  }
}

//execute PREPROCESS {
//	cplex.mipdisplay = 5
//}
//
//execute {
//	writeln("SM: ", SM);
//	writeln("preferences: ", preferences);
//	writeln("interdictions: ", interdictions);
//	writeln("objBalance: ", objBalance);
//	writeln("objRelax: ", objRelax);
//}

// PRINT THE RESULT
execute POSTPROCESS{
//	var no_work = true;
//	for(var j in DAYS) write(j + "\t");
//	writeln(); writeln();
//	for(var i in AGENTS) {
//		for(var j in DAYS){
//			if(work[i][j] > 0) {
//				if(work[i][j] == 2) write("S");
//				else if(work[i][j] == 1) write("M");
//				else if(work[i][j] == 3) write("J");
//				else write("?");			
//			}
//			else write("-")
//            write("\t");
//		}                                                 
//        writeln();
//	}
	for (var i in AGENTS){
	  for (var j in DAYS){
	    if (violatedFixedShift[i][j] == 1){
	      write("Day " + j + " Agent " + i + "'s fixed shift is violated: " + workday[i][j] + "->");
	      if (work[i][j] == 2) writeln("S");
	      else if (work[i][j] == 1) writeln("M");
	      else if (work[i][j] == 3) writeln("J");
	    }
	  }
	}       
}

