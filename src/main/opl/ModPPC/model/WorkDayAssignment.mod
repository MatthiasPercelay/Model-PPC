/*********************************************
 * OPL 12.8.0.0 Model
 * Author: Thomas
 * Creation Date: 25 nov. 2019 at 14:11:57
 *********************************************/ 
include "nursesCommon.mod";
  
int startW[k in WEEKS] = DAYS_PER_WEEK * (k-1) + 1;

int endW[k in WEEKS] = DAYS_PER_WEEK * k;
 
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
 
dvar boolean work[AGENTS][DAYS];	

dexpr int break[i in AGENTS][j in DAYS] = 1 - work[i][j];
 
// break2Days[i][j] = 1 means that the agent i  have a 2days break (j is the first day) 
dexpr int break2Days[i in AGENTS][j in DAYS] = (j==d) ? 0 : minl( break[i][j], break[i][j+1]); 
 
 
 //weekend = 1 si l'agent i a son week end pendant la semaine w'
 dexpr int weekEnd[i in AGENTS][w in WEEKS] = minl(break[i][endW[w]],break[i][endW[w]-1]);
 
// work5days[i][j] = 1 means that the agent i  will work 5 consecutif days starting from the day j. 
//dexpr int work5Days[i in AGENTS][j in DAYS] = (j>=d-3) ? 0 : minl( work[i][j], work[i][j+1],work[i][j+2],work[i][j+3],work[i][j+4]); 
 
// number of working day for each agents over all the timetable
dexpr int supplyWorkDay[i in AGENTS] = sum(j in DAYS) work[i][j];
 
// difference between the number of working days in the timetable  and the number of working days expected to do
dexpr int workDayDiff[i in AGENTS] = maxl(workDays[i],supplyWorkDay[i])-minl(workDays[i],supplyWorkDay[i]); 
 
//for each agent this is the score of his preferences for each week
dexpr int breakprefpW[i in AGENTS][l in CYCLES] = sum(j in 1..WEEKS_PER_CYCLE*DAYS_PER_WEEK)(break[i][startW[startC[l]]+(j-1)]*breakPrefs[i][j]);

// number of nurse working the day j in the timetable
dexpr int supply[j in DAYS] = sum(i in AGENTS) work[i][j];
 
// if demand[j] < supply[j] then 0 else demand[j]-supply[j] 
dexpr int underDemand[j in DAYS] = maxl(demand[j]-supply[j],0);

// if demand[j] > supply[j] then 0 else supply[j]-demand[j]
dexpr int upperDemand[j in DAYS] = maxl(supply[j]-demand[j],0);
 
/* 
// for each agent number of day worked by week
dexpr int WdayPweek[i in AGENTS][w in WEEKS] = sum(j in 1..DAYS_PER_WEEK)(work[i][startW[w]+(j-1)]);

//maximum of day work in a week for each agent
dexpr int MAXWdayPweek[i in AGENTS]= sum(w in WEEKS)(WdayPweek[i][w]);

//sum of max day worked in a week for each agent
dexpr int TOTALMAXWdayPweek= sum(i in AGENTS)(MAXWdayPweek[i]);*/

//coeff de la contrainte 2
dexpr int coeff1=(max(j in DAYS) demand[j]*d+1);
//coeff de la contrainte 3
dexpr int coeff2=(max(j in DAYS) demand[j]+1);
//coeff de la contrainte 4
dexpr int coeff3=(14*c*n);

//contrainte 1
dexpr int TOTALunderDemand=sum(j in DAYS) underDemand[j];
//contrainte 2
dexpr int TOTALupperDemand=sum(j in DAYS) upperDemand[j];
//contrainte 3
dexpr int MAXDIFFworkSupply=max(j in DAYS)(demand[j]-supply[j]);
//contrainte 4
// global score of the preferences
dexpr int TOTALbreakPrefpW = sum(i in AGENTS, l in CYCLES) breakprefpW[i][l];


// if (relaxation == 1) then minimize in first sum(j in DAYS) (underDemand[j]) and then (sum(j in DAYS) (upperDemand[j])
//else minimize the difference between the number of days 
dexpr int objectif = (useRelaxation==1) ? (coeff1*coeff2*coeff3)*(TOTALunderDemand) +(coeff2*coeff3)*(TOTALupperDemand)+coeff3*(MAXDIFFworkSupply)+TOTALbreakPrefpW : sum(i in AGENTS) (workDayDiff[i]);

minimize objectif;
//changer le cas ou relaxation = 0; pour le moment , emploie au maximum  qui a isurcharger la demande
subject to{
	if(useRelaxation == 0){
		    forall(j in DAYS) 
 		ctDemand[j]:
 		supply[j] >= demand[j]; // satisfy demand
	}
	
	forall(i in AGENTS)
	   ctWorkDays[i]:
	   supplyWorkDay[i] <= workDays[i]; // satisfy the maximum number of working days for each agents.

 	forall(i in AGENTS) 
 		forall(c in CYCLES) 
 		 	ctBreak[i][c]:
 			sum(j in startW[startC[c]]..endW[endC[c]]) break[i][j] >= breaksPerCycle[i] ; //satisfy the number of breaks per cycles
 	
 	forall(i in AGENTS) 
 		forall(c in CYCLES)
 		   	ctSunday[i][c]:
 		  sum(w in startC[c]..endC[c]) break[i][endW[w]] >= SUNDAYS_PER_CYCLE ; // satisfy the number of sundays per cycle
 		  
 	forall(i in AGENTS)
 		forall(c in CYCLES) 
 		 	ct2ConsBreak[i][c]:
			sum(j in startW[startC[c]]..endW[endC[c]]) break2Days[i][j] >= TWODAYS_BREAKS_PER_CYCLE;	//satisfy the number two days breaks per  cylcle
	
	forall(i in AGENTS) 
		forall(k in 1..(d-(MAX_CONSECUTIVE_WORKING_DAYS))) 
			ct6DaysMax[i][k]:
			sum(j in k..k+MAX_CONSECUTIVE_WORKING_DAYS) work[i][j] <= MAX_CONSECUTIVE_WORKING_DAYS ; //at most 6 working days over a rolling 7 day	
	
	forall(i in AGENTS) 
		forall(k in 1..(d-PREF_CONSECUTIVE_WORKING_DAYS)) 
			ct5ConsDaysMax[i][k]:
			sum(j in k..k+PREF_CONSECUTIVE_WORKING_DAYS) work[i][j] <= PREF_CONSECUTIVE_WORKING_DAYS; // at most 5 consecutive working days over a rolling 6 day

	ctFixedWork:
	forall(i in AGENTS, j in DAYS : fixedWork[i][j] == 1) work[i][j] == 1; // the fixed work day has to be respect
	
	ctFixedBreak:
	forall(i in AGENTS, j in DAYS : fixedBreak[i][j] == 1) work[i][j] == 0; // the fixed break day has to be respect
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
