/*********************************************
 * OPL 12.8.0.0 Model
 * Author: Thomas
 * Creation Date: 25 nov. 2019 at 14:11:57
 *********************************************/
int DAYS_PER_WEEK = 7;
range WEEKDAYS = 1..DAYS_PER_WEEK;
 
int WEEKS_PER_CYCLE = 2;
int SUNDAYS_PER_CYCLE = 1;
int MIN_BREAKS_PER_CYCLE = 4;
int TWODAYS_BREAKS_PER_CYCLE = 1;
int MIN_BREAKS_PER_WEEK = 1;
int MAX_CONSECUTIVE_WORKING_DAYS = 6;
int PREF_CONSECUTIVE_WORKING_DAYS = 5;

 int n = ...; // number of agents
 range AGENTS = 1..n;
 int c = ...; // number of work cycles
 range CYCLES = 1..c;
 
 int w = WEEKS_PER_CYCLE * c; // number of weeks of the work period
 range WEEKS = 1..w;
 
 int d = DAYS_PER_WEEK * w; // number of days of the work period
 range DAYS = 1..d;
 
 {string} SHIFTS = {"M", "J", "S"};
 int demands[SHIFTS][DAYS] = ...;
 
 int workDays[AGENTS] = ...;
 
 int breaksPerCycle[AGENTS] = ...;
 
 string planning[AGENTS][DAYS] = ...;
 int fixedWork[i in AGENTS][j in DAYS] = planning[i][j] == "M" || planning[i][j] == "J" || planning[i][j] == "S";
 int fixedBreak[i in AGENTS][j in DAYS] = planning[i][j] == "RA" || planning[i][j] == "JF" || planning[i][j] == "CA" || planning[i][j] == "RH" || planning[i][j] == "RTT" || planning[i][j] == "RC" || planning[i][j] == "RH" || planning[i][j] == "MPR";
 
 int breakPrefs[AGENTS][WEEKDAYS] = ...;
 
 int demand[j in DAYS] = sum(k in SHIFTS) demands[k][j] + sum(i in AGENTS) (planning[i][j] == "FO");
 
 int startW[k in WEEKS] = DAYS_PER_WEEK * (k-1) + 1;
 
 int endW[k in WEEKS] = DAYS_PER_WEEK * k;
 
 int startC[k in CYCLES] = WEEKS_PER_CYCLE * (k-1) + 1;
 int endC[k in CYCLES] = WEEKS_PER_CYCLE * k;
 
 //-------------------------------- Definition of variable --------------------------------
  
 dvar boolean work[AGENTS][DAYS];	

 dexpr int break[i in AGENTS][j in DAYS] = 1 - work[i][j];
 
 dexpr int break2Days[i in AGENTS][j in DAYS] = (j==d) ? 0 : minl( break[i][j], break[i][j+1]); // break2Days[i][j] = 1 means that the agent i  have a 2days break (j is the first day)
 
 dexpr int work5Days[i in AGENTS][j in DAYS] = (j>=d-3) ? 0 : minl( break[i][j], break[i][j+1],break[i][j+2],break[i][j+3],break[i][j+4]); // break2Days[i][j] = 1 means that the agent i  have a 2days break (j is the first day)
 
 
 
 //for each agent this is the score of his preferences for each week
 dexpr int breakprefpW[i in AGENTS][w in WEEKS] = sum(j in 1..DAYS_PER_WEEK)(break[i][startW[w]+(j-1)]*breakPrefs[i][j]);
 // global score of the preferences
 dexpr int TOTALbreakPrefpW =sum(i in AGENTS, w in WEEKS)breakprefpW[i in AGENTS][w in WEEKS];



 // for each agent number of day worked by week
 dexpr int WdayPweek[i in AGENTS][w in WEEKS] = sum(j in 1..DAYS_PER_WEEK)(work[i][startW[w]+(j-1)]);
 //maximum of day work in a week for each agent
 dexpr int MAXWdayPweek[i in AGENTS]= sum(w in WEEKS)(WdayPweek[i][w]);
 //sum of max day worked in a week for each agent
 dexpr int TOTALMAXWdayPweek= sum(i in AGENTS)(MAXWdayPweek[i]);
 
subject to{

 	forall(j in DAYS) 
 		ctDemand:
 		sum(i in AGENTS) work[i][j] >= demand[j]; // satisfy demand

/*	ctWork: 	
 	forall(i in AGENTS) 
 		sum(d in DAYS) work[i][d] >= workDays[i]; // satisfy the number of work Days
 */	
 	forall(i in AGENTS) 
 		forall(c in CYCLES) 
 		 	ctBreak:
 			sum(j in startW[startC[c]]..endW[endC[c]]) break[i][j] >= breaksPerCycle[i] ; //satisfy the number of breaks per cycles
 	
 	forall(i in AGENTS) 
 		forall(c in CYCLES)
 		   	ctSunday:
 		  sum(w in startC[c]..endC[c]) break[i][endW[w]] >= SUNDAYS_PER_CYCLE ; // satisfy the number of sundays per cycle
 		  
 	forall(i in AGENTS)
 		forall(c in CYCLES) 
 		 	ct2ConsBreak:
			sum(j in startW[startC[c]]..endW[endC[c]]) break2Days[i][j] >= TWODAYS_BREAKS_PER_CYCLE;	//satisfy the number two days breaks per  cylcle
	
	forall(i in AGENTS) 
		forall(k in 1..(d-(MAX_CONSECUTIVE_WORKING_DAYS))) 
			ct6DaysMax:
			sum(j in k..k+MAX_CONSECUTIVE_WORKING_DAYS) work[i][j] <= MAX_CONSECUTIVE_WORKING_DAYS ; //at most 6 working days over a rolling 7 day	
	
	forall(i in AGENTS) 
		forall(k in 1..(d-PREF_CONSECUTIVE_WORKING_DAYS)) 
			ct5ConsDaysMax:
			sum(j in k..k+PREF_CONSECUTIVE_WORKING_DAYS) work[i][j] <= PREF_CONSECUTIVE_WORKING_DAYS; // at most 5 consecutive working days over a roliing 6 day

	ctFixedWork:
	forall(i in AGENTS, j in DAYS : fixedWork[i][j] == 1) work[i][j] == 1; // the fixed work day has to be respect
	
	ctFixedBreak:
	forall(i in AGENTS, j in DAYS : fixedBreak[i][j] == 1) work[i][j] == 0; // the fixed break day has to be respect
}

execute POSTPROCESS{
		write("[")
        for(var i in AGENTS) {        
        	write("[");
            for(var j in DAYS) {
                if(planning[i][j] == "NA") {
                    if(work[i][j] == 1) write("\"W\"");
                    else write("\"BRK\"");                   
                } else {      
                	write("\"" + planning[i][j] + "\"");
                 }
                write(",")               
               }                                   
        writeln("],");
      }     
      write("]")  
}
