/*********************************************
 * OPL 12.8.0.0 Model
 * Author: Thomas
 * Creation Date: 25 nov. 2019 at 14:11:57
 *********************************************/ 
include "nursesCommon.mod";
  
// start/end days of all weeks  
int startW[k in WEEKS] = DAYS_PER_WEEK * (k-1) + 1;
int endW[k in WEEKS] = DAYS_PER_WEEK * k;
 
// start/end weeks of all cycles
int startC[k in CYCLES] = WEEKS_PER_CYCLE * (k-1) + 1;
int endC[k in CYCLES] = WEEKS_PER_CYCLE * k;
 
//-------------------------------- Definition of constraint ------------------------------
constraint ctDemand[DAYS];
constraint ctWorkDays[AGENTS];
constraint ctBreak[AGENTS][CYCLES];
constraint ctSunday[AGENTS][CYCLES];
constraint ct2ConsBreak[AGENTS][CYCLES];
constraint ct6DaysMax[AGENTS][1..(d-(MAX_CONSECUTIVE_WORKING_DAYS))];
constraint ct5ConsDaysMax[AGENTS][1..(d-(PREF_CONSECUTIVE_WORKING_DAYS))];
 
//-------------------------------- Definition of variable --------------------------------
 
// solution 
dvar boolean work[AGENTS][DAYS];	

dexpr int break[i in AGENTS][j in DAYS] = 1 - work[i][j];
 
// break2Days[i][j] = 1 means that the agent i  have a 2days break (j is the first day) 
// OK
dexpr int break2Days[i in AGENTS][j in DAYS] = (j==d) ? 0 : minl( break[i][j], break[i][j+1]); 
 
//weekend = 1 if the agent i has a weekend during th week / si l'agent i a son week end pendant la semaine w'
// OK
dexpr int weekEnd[i in AGENTS][w in WEEKS] = minl(break[i][endW[w]],break[i][endW[w]-1]);
 
// work5days[i][j] = 1 means that the agent i  will work 5 consecutif days starting from the day j. 
//dexpr int work5Days[i in AGENTS][j in DAYS] = (j>=d-3) ? 0 : minl( work[i][j], work[i][j+1],work[i][j+2],work[i][j+3],work[i][j+4]); 
 
// number of working day for each agents over all the timetable
// OK, actual workdays of each agent
dexpr int supplyWorkDay[i in AGENTS] = sum(j in DAYS) work[i][j];
 
// difference between the number of working days in the timetable  and the number of working days expected to do
// difference between workdays pref and actual assign of each agent
dexpr int workDayDiff[i in AGENTS] = maxl(workDays[i],supplyWorkDay[i])-minl(workDays[i],supplyWorkDay[i]); 
 
//for each agent this is the score of his break preferences for each week // CYCLE??
dexpr int breakprefpW[i in AGENTS][l in CYCLES] = sum(j in 1..WEEKS_PER_CYCLE*DAYS_PER_WEEK)(break[i][startW[startC[l]] + (j-1)] * breakPrefs[i][j]);


// number of nurse working the day j in the timetable
// OK, supply for each day
dexpr int supply[j in DAYS] = sum(i in AGENTS) work[i][j];
 
// if demand[j] < supply[j] then 0 else demand[j]-supply[j] 
// when demand is not satisfied
// OK, underdemand works for each day
dexpr int underDemand[j in DAYS] = maxl(demand[j]-supply[j],0);

// if demand[j] > supply[j] then 0 else supply[j]-demand[j]
// when demand is over satisfied
// OK, overdemand works for each day
dexpr int upperDemand[j in DAYS] = maxl(supply[j]-demand[j],0);
 
/* 
// for each agent number of day worked by week
dexpr int WdayPweek[i in AGENTS][w in WEEKS] = sum(j in 1..DAYS_PER_WEEK)(work[i][startW[w]+(j-1)]);

//maximum of day work in a week for each agent
dexpr int MAXWdayPweek[i in AGENTS]= sum(w in WEEKS)(WdayPweek[i][w]);

//sum of max day worked in a week for each agent
dexpr int TOTALMAXWdayPweek= sum(i in AGENTS)(MAXWdayPweek[i]);*/



//Total sum of the under demand
//contrainte 1
// OK
dexpr int TOTALunderDemand=sum(j in DAYS) underDemand[j];
//Total sum of the upper demand
//contrainte 2
// OK
dexpr int TOTALupperDemand=sum(j in DAYS) upperDemand[j];
//Maximum difference between the demand and the subdemand for one day d
//contrainte 3
// OK， can be negative, if all over demand - good
dexpr int MAXDIFFworkSupply=max(j in DAYS)(demand[j]-supply[j]);
//contrainte 4
// global  of the preferences
// OK, total sum of breakPrefpW
dexpr int TOTALbreakPrefpW = sum(i in AGENTS, l in CYCLES) breakprefpW[i][l];

//contrainte 5
// total of the difference between the number of working days in the timetable and the number of working days expected to do
// OK, total of workday differences
dexpr int TotalworkDayDiff = sum(i in AGENTS) (workDayDiff[i]);
//contrainte 6
// total of the number of free weekend
// OK, how to measure each agent's weekends
dexpr int TotalweekEnd=sum(i in AGENTS,w in WEEKS)weekEnd[i][w];

//maximum de la contrainte TOTALunderDemand (1)
////// MAXVAL of underdemand works, why not sum all but using the max daily demand*28, plus 1 means open interval?
dexpr int MAXValueOF_TOTALunderDemand=(max(j in DAYS) (demand[j])*d+1);
//maximum de la contrainte TOTALupperDemand (2)
// OK, MAXVAL of overdemand works
dexpr int MAXValueOF_TOTALupperDemand=(max(j in DAYS) (n-demand[j])*d+1);
//maximum de la contrainte MAXDIFFworkSupply (3)
// OK, MAXVAL of demanded works per day
dexpr int MAXValueOF_MAXDIFFworkSupply=(max(j in DAYS) demand[j]+1);
//maximum de la contrainte TOTALbreakPrefpW (4)
dexpr int MAXValueOF_TOTALbreakPrefpW=(14*c*n + 1);

//maximum de la contrainte TotalworkDayDiff (5)
dexpr int MAXValueOF_TotalworkDayDiff=(d*n+1);
//maximum de la contrainte TotalweekEnd (6)
// OK, MAXVAL of total weekEnds
dexpr int MAXValueOF_TotalweekEnd=(w*n+1);

//most interesting constraint with relaxation
//combination of the constraints  1 2 3
// minimize total underdemand works + overdemand works + maxi diff between demand and supply
// weight using each MAXVAL
dexpr int ObjectifCombi123 = (MAXValueOF_TOTALupperDemand*MAXValueOF_MAXDIFFworkSupply)*(TOTALunderDemand) +(MAXValueOF_MAXDIFFworkSupply)*(TOTALupperDemand)+(MAXDIFFworkSupply);

//combination of the constraints 1 2 3 4
dexpr int ObjectifCombi1234 = MAXValueOF_TOTALbreakPrefpW*ObjectifCombi123-TOTALbreakPrefpW;
//dexpr int ObjectifCombi1234 = MAXValueOF_TOTALbreakPrefpW*ObjectifCombi123-TOTALbreakPrefpW-TOTALworkPrefpW;

//combination of the constraints 1 2 3 6
dexpr int ObjectifCombi1236 = MAXValueOF_TotalweekEnd*ObjectifCombi123-TotalweekEnd;

//combination of the constraints 1 2 3 4 6
dexpr int ObjectifCombi12346 = MAXValueOF_TotalweekEnd*ObjectifCombi1234-TotalweekEnd;

//combination of the constraints 1 2 3 6 4
dexpr int ObjectifCombi12364 = MAXValueOF_TOTALbreakPrefpW*ObjectifCombi1236-TOTALbreakPrefpW;


//most interesting constraint without relaxation -> 5
//combination of the constraints 5 4
dexpr int ObjectifCombi54 = MAXValueOF_TOTALbreakPrefpW*TotalworkDayDiff-TOTALbreakPrefpW;
//combination of the constraints 5 6
dexpr int ObjectifCombi56 = MAXValueOF_TotalweekEnd*TotalworkDayDiff-TotalweekEnd;
//combination of the constraints 5 4 6
dexpr int ObjectifCombi546 = MAXValueOF_TotalweekEnd*ObjectifCombi54-TotalweekEnd;
//combination of the constraints 5 6 4
dexpr int ObjectifCombi564 = MAXValueOF_TOTALbreakPrefpW*ObjectifCombi56-TOTALbreakPrefpW;


//minimize staticLex(TOTALupperDemand, TOTALunderDemand, MAXDIFFworkSupply, -TOTALbreakPrefpW);
//minimize staticLex(-TOTALbreakPrefpW, TOTALupperDemand, TOTALunderDemand, MAXDIFFworkSupply);
minimize staticLex(TOTALupperDemand, TOTALunderDemand, MAXDIFFworkSupply, -TOTALbreakPrefpW, -TotalweekEnd);

// if (relaxation == 1) then minimize in first sum(j in DAYS) (underDemand[j]) and then (sum(j in DAYS) (upperDemand[j])
//else minimize the difference between the number of days 
// (1 2 3 4) else (5)
//dexpr int objectif = (useRelaxation==0) ? ObjectifCombi1234 : TotalworkDayDiff;
//minimize objectif;
//change the case or relaxation=0; for the moment, maximize the cover of demands
//changer le cas ou relaxation = 0; pour le moment , emploie au maximum  qui a isurcharger la demande

subject to{
	forall(j in DAYS) 
 		ctDemand[j]:
 		// for each day supply >= demand
 		supply[j] >= demand[j]; // satisfy basic demand
 		
	if(useRelaxation == 0){ //this was misplaced, the basic demand needs should be satisfied whenever, or just remove it
		forall(i in AGENTS)
		   ctWorkDays[i]:
		   // for each agent supply workday <= desire workday
		   supplyWorkDay[i] <= workDays[i]; // satisfy the maximum number of working days for each agents.
	
	 	forall(i in AGENTS) 
	 		forall(c in CYCLES) 
	 		 	ctBreak[i][c]:
	 		 	// actual break per cycle >= breaksPerCycle
	 			sum(j in startW[startC[c]]..endW[endC[c]]) break[i][j] >= breaksPerCycle[i] ; //satisfy the number of breaks per cycles
	 	
	 	forall(i in AGENTS) 
	 		forall(c in CYCLES)
	 		   	ctSunday[i][c]:
	 		   	// at least 1 sunday per cycle
	 		  sum(w in startC[c]..endC[c]) break[i][endW[w]] >= SUNDAYS_PER_CYCLE ; // satisfy the number of sundays per cycle
	 		  
	 	forall(i in AGENTS)
	 		forall(c in CYCLES) 
	 		 	ct2ConsBreak[i][c]:
	 		 	// at least 1 twodays break per cycle
				sum(j in startW[startC[c]]..endW[endC[c]]) break2Days[i][j] >= TWODAYS_BREAKS_PER_CYCLE;	//satisfy the number two days breaks per  cylcle
		
		forall(i in AGENTS) 
			forall(k in 1..(d-(MAX_CONSECUTIVE_WORKING_DAYS))) 
				ct6DaysMax[i][k]:
				// at most 6 working days over 7
				sum(j in k..k+MAX_CONSECUTIVE_WORKING_DAYS) work[i][j] <= MAX_CONSECUTIVE_WORKING_DAYS ; //at most 6 working days over a rolling 7 day	
		
		forall(i in AGENTS) 
			forall(k in 1..(d-PREF_CONSECUTIVE_WORKING_DAYS)) 
				ct5ConsDaysMax[i][k]:
				// at most 5 consecutive working days over 6 (combined with the above)
				sum(j in k..k+PREF_CONSECUTIVE_WORKING_DAYS) work[i][j] <= PREF_CONSECUTIVE_WORKING_DAYS; // at most 5 consecutive working days over a rolling 6 day
	}	
	ctFixedWork:
	// OK
	forall(i in AGENTS, j in DAYS : fixedWork[i][j] == 1) work[i][j] == 1; // the fixed work day has to be respect
	
	ctFixedBreak:
	// OK
	forall(i in AGENTS, j in DAYS : fixedBreak[i][j] == 1) work[i][j] == 0; // the fixed break day has to be respect
	
}

execute {
	writeln("TOTALunderDemand:", TOTALunderDemand);
	writeln("TOTALupperDemand:", TOTALunderDemand);
	writeln("MAXDIFFworkSupply:", MAXDIFFworkSupply);
	writeln("TOTALbreakPrefpW:", TOTALbreakPrefpW);
	writeln("TotalweekEnd:", TotalweekEnd);
}


execute PREPROCESS {
	cplex.mipdisplay = 5
}



execute POSTPROCESS{
        for(var i in AGENTS) {
            for(var j in DAYS) {
                if(timetable[i][j] == "NA") {
                    write(work[i][j])                   
                } else {
                    write(timetable[i][j])
                 }
                write(", ")               
               }                                   
        writeln();
      }       
}
