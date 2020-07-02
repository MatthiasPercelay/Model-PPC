/*********************************************
 * OPL 12.8.0.0 Model
 * Author: Thomas
 * Creation Date: 25 nov. 2019 at 14:11:57
 *********************************************/ 
include "nursesCommon.mod";

//-------------------------------- Definition of constraint ------------------------------
constraint ctDemand[DAYS];
constraint ctWorkDays[AGENTS];
constraint ctBreak[AGENTS][CYCLES];
constraint ct4daysBreak[AGENTS][CYCLES];
constraint ctSunday[AGENTS][CYCLES];
constraint ct6DaysMax[AGENTS][1..(d-(MAX_CONSECUTIVE_WORKING_DAYS))];
constraint ct5ConsDaysMax[AGENTS][1..(d-(PREF_CONSECUTIVE_WORKING_DAYS))];
 
//-------------------------------- Definition of variables --------------------------------

// start/end days of all weeks  
int startW[k in WEEKS] = DAYS_PER_WEEK * (k-1) + 1;
int endW[k in WEEKS] = DAYS_PER_WEEK * k;
 
// start/end weeks of all cycles
int startC[k in CYCLES] = WEEKS_PER_CYCLE * (k-1) + 1;
int endC[k in CYCLES] = WEEKS_PER_CYCLE * k;
 
// solution 
dvar boolean work[AGENTS][DAYS];	
dexpr int break[i in AGENTS][j in DAYS] = 1 - work[i][j];
 
//weekend = 1 if the agent i has a 2-day weekend break in that week / si l'agent i a son week end pendant la semaine w'
dexpr int weekEnd[i in AGENTS][w in WEEKS] = minl(break[i][endW[w]],break[i][endW[w]-1]);
 
// number of working day for each agents over all the timetable
// actual workdays of each agent
dexpr int supplyWorkDay[i in AGENTS] = sum(j in DAYS) work[i][j];
 
 
 ////// for basic demands
 // supply for each day
dexpr int supply[j in DAYS] = sum(i in AGENTS) work[i][j];
// underdemand works for each day
dexpr int underDemand[j in DAYS] = maxl(demand[j]-supply[j],0);
// overdemand works for each day
dexpr int upperDemand[j in DAYS] = maxl(supply[j]-demand[j],0);

////// for preferences 
// the irregular weeks that work more than 5 days
dexpr int irregularWeeks[i in AGENTS][w in WEEKS] = sum(j in startW[w]..endW[w])work[i][j] >= (PREF_WORKDAY_PER_WEEK + 1);
// the score of break preferences for each agent in each cycle
dexpr int breakprefpC[i in AGENTS][l in CYCLES] = sum(j in 1..WEEKS_PER_CYCLE*DAYS_PER_WEEK)(break[i][startW[startC[l]] + (j-1)] * (breakPrefs[i][j] != 0)); // breakpref can designate mjs


////// for balance
// ratio of satisfied break preference for each agent
dexpr float breakPrefRate[i in AGENTS] = sum(l in CYCLES) breakprefpC[i][l] / (c*sum(j in 1..DAYS_PER_CYCLE)breakPrefs[i][j] + 0.001);
// range of satisfied break preference ratio (which we want to minimize)
dexpr float diffBreakPrefRate = max(i in AGENTS)breakPrefRate[i] - min(i in AGENTS)breakPrefRate[i];

// ratio of actual work days to expected work days for each agent
dexpr float workDayRate[i in AGENTS] = sum(d in DAYS) work[i][d] / (workDays[i] + 0.001);
// range of the above ratio (which we want to minimize)
dexpr float diffWorkDayRate = max(i in AGENTS)workDayRate[i] - min(i in AGENTS)workDayRate[i];


//-------------------------------- Definition of objectives --------------------------------

////// objs for Basic Demands
// Total sum of the under demand
// when use relaxation we min this
dexpr int TOTALunderDemand=sum(j in DAYS) underDemand[j];
// total sum of the upper demand, when not using relaxation we min this
dexpr int TOTALupperDemand=sum(j in DAYS) upperDemand[j];

////// objs for Preferences
// minimize the irregular weeks (work for more than 5 days)
dexpr int TOTALirregularWeeks = sum(i in AGENTS, w in WEEKS)irregularWeeks[i][w];
// total score of the preferences that are respected
dexpr int TOTALbreakprefpC = sum(i in AGENTS, l in CYCLES) breakprefpC[i][l];

////// objs for Balance
dexpr float TOTALbalance = diffBreakPrefRate * 1 + diffWorkDayRate * 1;


//-------------------------- Definition of final objective functions --------------------------

dexpr int objBasic = (useRelaxation1 == 0) ? TOTALupperDemand : TOTALunderDemand;
dexpr int objPref = TOTALirregularWeeks - TOTALbreakprefpC;
dexpr float objBalance = TOTALbalance;

minimize staticLex(objBasic, objPref, objBalance*OBJECTIVE_WORKDAY_USE_BALANCE);
//minimize staticLex(objBasic, objPref, objBalance);


//---------------------------------------- Constraints -----------------------------------------

subject to{
  
  	if(useRelaxation1 == 0){  // if use relaxation (1), we allow underdemand; and the solution should be corrected for model 2
    // satisfy demands for each day
	forall(j in DAYS) 
 		ctDemand[j]:
 		supply[j] >= demand[j]; // satisfy basic demand
	} 	
	
// 	// at most 5 working days per week
//  // this is too strict to give a solution, so is changed to an objective to be minimized
// 	forall(i in AGENTS, w in WEEKS)
// 	    sum(j in startW[w]..endW[w])work[i][j] <= 5;
 		
 	// at most 6 working days per 7 days
	forall(i in AGENTS) 
		forall(k in 1..(d-(MAX_CONSECUTIVE_WORKING_DAYS))) 
			ct6DaysMax[i][k]:
			sum(j in k..k+MAX_CONSECUTIVE_WORKING_DAYS) work[i][j] <= MAX_CONSECUTIVE_WORKING_DAYS ; //at most 6 working days over a rolling 7 day	
 		
 	// fixed work should be respected
	ctFixedWork:
		forall(i in AGENTS, j in DAYS : fixedWork[i][j] == 1) work[i][j] == 1;

	// fixed break should be respected
	ctFixedBreak:
		forall(i in AGENTS, j in DAYS : fixedBreak[i][j] == 1) work[i][j] == 0;
 		
	// for each agent actual workday <= desired workday
	forall(i in AGENTS)
	   ctWorkDays[i]:
	   supplyWorkDay[i] <= workDays[i];

	// for each agent acutal break per cycle >= expected break 
 	forall(i in AGENTS) 
 		forall(c in CYCLES) 
 		 	ctBreak[i][c]:
 			sum(j in startW[startC[c]]..endW[endC[c]]) break[i][j] >= breaksPerCycle[i] ;
 			
 	// at least 4 days' break per cycle
 	forall(i in AGENTS, c in CYCLES)
 	    ct4daysBreak[i][c]:
	 	    sum(k in 1..DAYS_PER_CYCLE) break[i][(c-1)*DAYS_PER_CYCLE+k] >= 4;
 		  
    // at least a two-day break that covers sunday each week
    forall(i in AGENTS)
        forall(c in CYCLES)
            ctSunday[i][c]:
            sum(w in startC[c]..endC[c]) weekEnd[i][w] >= 1;
    
	// at most 5 consecutive working days
	forall(i in AGENTS) 
		forall(k in 1..(d-PREF_CONSECUTIVE_WORKING_DAYS)) 
			ct5ConsDaysMax[i][k]:
			sum(j in k..k+PREF_CONSECUTIVE_WORKING_DAYS) work[i][j] <= PREF_CONSECUTIVE_WORKING_DAYS; // at most 5 consecutive working days over a rolling 6 day
}

//execute {
//	writeln("TOTALunderDemand:", TOTALunderDemand);
//	writeln("TOTALupperDemand:", TOTALunderDemand);
//	writeln("TOTALbreakprefpC:", TOTALbreakprefpC);
//	writeln("breakPrefRate:", breakPrefRate);
//	writeln("diffBreakPrefRate:", diffBreakPrefRate);
//	writeln("workDayRate:", workDayRate);
//	writeln("diffWorkDayRate:", diffWorkDayRate);
////	writeln("weekEnd:", weekEnd);
////	writeln("irregularWeeks:", irregularWeeks);
//}
//
//
//
//execute PREPROCESS{
//	cplex.mipdisplay = 5
//}
//
//
//
//execute POSTPROCESS{
//        for(var i in AGENTS) {
//            for(var j in DAYS) {
//                if(timetable[i][j] == "NA") {
//                    write(work[i][j])                   
//                } 
////                else if (timetable[i][j] == "M" || timetable[i][j] == "J" || timetable[i][j] == "S" || timetable[i][j] == "FO" || timetable[i][j]=="EX")
////                {
////                    write("1")
////                 }
////                 else{
////                   write("0")
////                   }
//                else{
//                  write(timetable[i][j])
//                  }
//                write(", ")               
//               }                                   
//        writeln();
//      }       
//}
