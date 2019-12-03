/*********************************************
 * OPL 12.9.0.0 Model
 * Author: JUNG
 * Creation Date: 25 nov. 2019 at 13:45:45
 *********************************************/

int DAYS_PER_WEEK = 7;
int WEEKS_PER_CYCLE = 2;
 
// AGENTS
int n = ...; 					// number of agents
range AGENTS = 1..n;

// DAYS
int c = ...; 					// number of work cycles
int w = WEEKS_PER_CYCLE * c;	// number of weeks of the work period
int d = DAYS_PER_WEEK * w;		// number of days of the work period
range DAYS = 1..d;

// REPRESENTATION OF SHIFTS ON THE PLANNING
string morning = ...;
string evening = ...;
string day = ...;
string to_define = ...;

// IMPORTANT ! Same order as the demands array !!
{string} SHIFTS = {evening, morning, day};

// MAIN DATA
int demands[SHIFTS][DAYS] = ...;			// demands per shift per days
string planning[AGENTS][DAYS] = ...;		// already established planning
string shift_preference[AGENTS][DAYS] = ...;      // what each agent wants
string shift_forbidden[AGENTS][DAYS] = ...; // what each agent does not want

// DAY OF WORK AND FIXED SHIFT
int fixedWork[a in AGENTS][d in DAYS] = planning[a][d] == to_define || planning[a][d] == evening || planning[a][d] == day || planning[a][d] == morning;
int fixedShift[a in AGENTS][d in DAYS] = planning[a][d] == evening || planning[a][d] == day || planning[a][d] == morning;

// VARIABLES
// Assign an agent to a shift
dvar int shift_assign[SHIFTS][DAYS] in 0..n;

// OBJECTIVE FUNCTION UTILS
// Number of day where an agent does evening and morning the next day.
dexpr int SM[a in AGENTS] = sum(i in 1..d-1) (shift_assign[evening][i] == shift_assign[morning][i+1]);
// Number of consecutive days where we do the same shift
int CONSECUTIVE_DAYS = 3;
dexpr int sameShift[a in AGENTS][c in 2..CONSECUTIVE_DAYS] = sum(i in 1..d-c+1, s in SHIFTS) (sum(j in 0..c-1) (shift_assign[s][i+j] == a) == c);
// Number of preferences respected for each agent
dexpr int preferences[a in AGENTS] = sum(d in DAYS, s in SHIFTS) (shift_assign[s][d] == a && shift_preference[a][d] == s) ;
// Number of forbidden respected for each agent
dexpr int interdictions[a in AGENTS] = sum(d in DAYS, s in SHIFTS) (shift_assign[s][d] == a && shift_forbidden[a][d] != s) ;


/*	OBJECTIVE FUNCTION
	GOALS :
		- Minimize the shift sequence evening + morning
		- Minimize the sequence of length 3 of the same shift
		- Maximize the sequence of length 2 of the same shift
*/
minimize sum(a in AGENTS) (SM[a]*10 - sameShift[a][2]);

subject to{

	///////////////////////
	// BASIC CONSTRAINTS //
	///////////////////////
	// If there is a demand, there is an agent
	forall(s in SHIFTS, d in DAYS)
		if(demands[s][d] != 0) shift_assign[s][d] != 0;

	forall(d in DAYS, a in AGENTS){
		// If the shift is already shift, then we must respect it	
		if(fixedShift[a][d] != 0) shift_assign[planning[a][d]][d] == a;
		// If an agent must work a certain day, then he must have a shift
		fixedWork[a][d] == sum(s in SHIFTS) (shift_assign[s][d] == a);
	}
 	
 	/////////////////////////////
 	// ADDITIONNAL CONSTRAINTS //
 	/////////////////////////////
 	
 	// TODO ... ?
 	 
}

// PRINT THE RESULT
execute POSTPROCESS{
	var no_work = true;
	for(var d in DAYS) write(d + "\t");
	writeln(); writeln();
	for(var a in AGENTS) {
		for(var d in DAYS) {
			for(var s in SHIFTS){
				if(shift_assign[s][d] == a) {
					no_work = false;
					write(s);				
				}	
			}
			if(no_work) write("-");
            write("\t");
            no_work = true;
		}                                                 
        writeln();
	}       
}

