/*********************************************
 * OPL 12.8.0.0 Model
 * Author: steve
 * Creation Date: 9 d�c. 2019 at 10:23:52
 *********************************************/

 int DAYS_PER_WEEK = 7;
 range WEEKDAYS = 1..DAYS_PER_WEEK;
 int WEEKS_PER_CYCLE = 2;
 int DAYS_PER_CYCLE = DAYS_PER_WEEK * WEEKS_PER_CYCLE;
 range CYCLEDAYS= 1..DAYS_PER_CYCLE;
 int SUNDAYS_PER_CYCLE = 1;
 int TWODAYS_BREAKS_PER_CYCLE = 1;
 int MAX_CONSECUTIVE_WORKING_DAYS = 6;
 int PREF_CONSECUTIVE_WORKING_DAYS = 5;
 
 int n = ...; 					// number of agents
 range AGENTS = 1..n;
 
 int c = ...; 					// number of work cycles
 range CYCLES = 1..c;
 
 int w = WEEKS_PER_CYCLE * c;	// number of weeks of the work period
 range WEEKS = 1..w;
 
 int d = DAYS_PER_WEEK * w;		// number of days of the work period
 range DAYS = 1..d;
 
 // TODO Remove or define as constants (in uppercase) if really needed
 int EVENING = 1;
 int MORNING = 2;
 int DAY = 3;
 
 range SHIFTS = 1..3;
 int SHIFT[{"S", "M", "J"}] = [1, 2, 3];
 
 string timetable[AGENTS][DAYS] = ...;
 
 int demands[SHIFTS][DAYS] = ...;
 int demand[j in DAYS] = sum(k in SHIFTS) demands[k][j] + sum(i in AGENTS) (timetable[i][j] == "FO");
 
 
 int fixedWork[i in AGENTS][j in DAYS] = timetable[i][j] == "W" || timetable[i][j] == "M" || timetable[i][j] == "J" || timetable[i][j] == "S" || timetable[i][j] == "FO";
 int fixedBreak[i in AGENTS][j in DAYS] = timetable[i][j] == "CA" || timetable[i][j] == "RH" || timetable[i][j] == "RTT" || timetable[i][j] == "RC" || timetable[i][j] == "RH" || timetable[i][j] == "MPR" || timetable[i][j] == "JF";
 int fixedShift[i in AGENTS][j in DAYS] = timetable[i][j] == "M" || timetable[i][j] == "J" || timetable[i][j] == "S";
 
 int useRelaxation = ...; 
 
 int workDays[AGENTS] = ...;
 
int breaksPerCycle[AGENTS] = ...;
  
int breakPrefs[AGENTS][CYCLEDAYS] = ...;

int shiftPrefs[AGENTS][CYCLEDAYS][SHIFTS] = ...;    // what each agent wants or doesn't want
 
 
 
 