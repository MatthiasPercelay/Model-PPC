/*********************************************
 * OPL 12.9.0.0 Model
 * Author: JUNG
 * Creation Date: 25 nov. 2019 at 13:45:45
 *********************************************/
 include "nursesCommon.mod";

//int DAYS_PER_WEEK = 7;
//int WEEKS_PER_CYCLE = 2;
 
// AGENTS
//int n = ...; 					// number of agents
//range AGENTS = 1..n;

// DAYS
//int c = ...; 					// number of work cycles
//int w = WEEKS_PER_CYCLE * c;	// number of weeks of the work period
//int d = DAYS_PER_WEEK * w;		// number of days of the work period
//range DAYS = 1..d;

// REPRESENTATION OF SHIFTS ON THE PLANNING
//string morning = ...;
//string evening = ...;
//string day = ...;
//string to_define = ...;
string hebdomary_break = ...;

// IMPORTANT ! Same order as the demands array !!
//{string} SHIFTS = {evening, morning, day};
int shift[SHIFTS] = [1, 2, 3];
string shift_s[1..3] = [evening, morning, day];

// MAIN DATA
//int demands[SHIFTS][DAYS] = ...;			// demands per shift per days
//string timetable[AGENTS][DAYS] = ...;		// already established planning
int shift_preference[AGENTS][DAYS][SHIFTS] = ...;    // what each agent wants or doesn't want

// DAY OF WORK AND FIXED SHIFT
//int fixedWork[a in AGENTS][d in DAYS] = timetable[a][d] == to_define || timetable[a][d] == evening || timetable[a][d] == day || timetable[a][d] == morning;
//int fixedShift[a in AGENTS][d in DAYS] = timetable[a][d] == evening || timetable[a][d] == day || timetable[a][d] == morning;

int is_preference[a in AGENTS][d in DAYS][s in SHIFTS] = shift_preference[a][d][s] > 0;
int is_forbidden[a in AGENTS][d in DAYS][s in SHIFTS] = shift_preference[a][d][s] < 0;

// VARIABLES
// Assign an agent to a shift
//dvar int shift_assign[SHIFTS][DAYS] in 0..n;
dvar int shift_assign[AGENTS][DAYS] in 0..3; 

// OBJECTIVE FUNCTION UTILS
// Number of day where an agent does evening and morning the next day.
dexpr int SM[a in AGENTS] = sum(i in 1..d-1) (shift_assign[a][i] == shift[evening] && shift_assign[a][i+1] == shift[morning]);
// Number of consecutive days where we do the same shift
int CONSECUTIVE_DAYS = 3;
dexpr int sameShift[a in AGENTS][c in 2..CONSECUTIVE_DAYS] = 0; // A CHANGER sum(i in 1..d-c+1, s in SHIFTS) (sum(j in 0..c-1) (shift_assign[s][i+j] == a) == c);

// Number of preferences respected for each agent
dexpr int preferences[a in AGENTS] = 0; //A CHANGER sum(d in DAYS, s in SHIFTS) (shift_assign[a][d] == shift[s] && is_preference[a][d][s] == 1) ;
// Number of forbidden not respected for each agent
dexpr int interdictions[a in AGENTS] = 0; //A CHANGER sum(d in DAYS, s in SHIFTS) (shift_assign[a][d] == shift[s] && is_forbidden[a][d][s] == 1) ;

// Evaluation for each agent
dexpr int objectiveValuePerAgent[a in AGENTS] = 10*SM[a] - sameShift[a][2] + 4*interdictions[a] - preferences[a];
// Global evaluation of the solution
dexpr int objectiveValue = sum(a in AGENTS) objectiveValuePerAgent[a];
// Difference of value to the average for each agent
dexpr float differenceToAveragePerAgent[a in AGENTS] = abs(objectiveValue - objectiveValuePerAgent[a]*n);
// Global difference to average
dexpr float differenceToAverage = sum(a in AGENTS) differenceToAveragePerAgent[a];

/*	OBJECTIVE FUNCTION
	GOALS :
		- Minimize the shift sequence evening + morning
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
	forall(s in SHIFTS, d in DAYS)
		demands[s][d] == sum(a in AGENTS) (shift_assign[a][d] == shift[s]);

	forall(d in DAYS, a in AGENTS){
		// If the shift is already shift, then we must respect it	
		if(fixedShift[a][d] == 1) shift_assign[a][d] == shift[timetable[a][d]];
		// If an agent must work a certain day, then he must have a shift
		if(fixedWork[a][d] == 1) shift_assign[a][d] != 0;
		if(fixedWork[a][d] == 0) shift_assign[a][d] == 0;
	}
	
	// If an agent has only one day for hebdomary break, then he must have more than 36 hours of break
 	forall(i in 2..d-1, a in AGENTS){
 		if(fixedWork[a][i] == 0 && fixedWork[a][i-1] == 1 && fixedWork[a][i+1] == 1 && (fixedShift[a][i-1] == 0 || fixedShift[a][i+1] == 0)){
 			(shift_assign[a][i-1] == shift[evening]) + (shift_assign[a][i+1] == shift[morning]) + (shift_assign[a][i+1] == shift[day]) <= 1;
		}
	}
 	
 	/////////////////////////////
 	// ADDITIONNAL CONSTRAINTS //
 	/////////////////////////////
 	
 		
 	 
}

// PRINT THE RESULT
execute POSTPROCESS{
	var no_work = true;
	for(var d in DAYS) write(d + "\t");
	writeln(); writeln();
	for(var a in AGENTS) {
		for(var d in DAYS){
			if(shift_assign[a][d] > 0) write(shift_s[shift_assign[a][d]]);
			else write("-")
            write("\t");
		}                                                 
        writeln();
	}       
}

