## Problem Requirements:

### A.2

1. The actual working time do not exceed 48h (6 days) per 7 days. (workday assignment)

2. Legally an agent cannot work more than 6 days per week. In practice cannot work more than 5 days per  week. (workday assignment)

3. The agent must have at least one weekly 36h consecutive rest. So the working pattern S-B-M and S-B-J do not satisfied this. (shift assignment)

4. An agent cannot work more than 39h (5 days) per week,  44h (6 days) in the case of a irregular week. (workday assignment)

5. An agent work on average 5 days a week. (workday assignment)

6. The number of rest days is fixed at 4 days per 2 weeks, at least 2 of which must be consecutive, including a Sunday. (workday assignment)

#### A.3

1. number of working days is fixed based on the agent's work quota and other factors.

2. certain days of work, rest, or holidays are pre-defined by the agents.

#### A.4

1. satisfy demand and maximize preferences.
2. when no solution, satisfy it to the best.
3. each agent expresses preferences on: a) fixed works and shifts, b) number of consecutive working days, c) les repos de 2 jours consecutifs, d) les repos du week-end. (c and d are not clear to me, they refer to the numbers or fixed dates?)

   

## Objectives

|                          | present model                                                | previous model                                               |
| ------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| objectives               | **Basic Demands:**<br />*underDemand* - upperdemand works for each day (minimize when no relax)<br />*upperDemand* - underdemand works for each day (minimize when relax)<br />1. ***TOTALunderDemand*** - sum of *underDemand*<br />2. ***TOTALupperDemand*** - sum of *upperDemand*<br />**Preferences:**<br />*breakprefpC* - respected break preferences for each agent in each cycle<br />*irregularWeeks* - indicator for weeks that work more than 5 days<br />3. ***TOTALbreakprefpC*** - sum of *breakprefpC*<br />4. ***TOTALirregularWeeks*** - sum of *irregularWeeks*<br />**Balance:**<br />*diffBreakPrefRate* - difference of the max and min of the ratio of respected breaks to expected breaks among all agents, to balance their respected ratios of break<br />*diffWorkDayRate* - difference of the max and min of the ratio of actual work days to expected workdays among all agents, to balance their assigned work days<br />5. ***TOTALbalance*** - sum of *diffBreakPrefRate* and *diffWorkDayRate* | 1. ***TOTALunderDemand*** - sum of underDemand<br />2. ***TOTALupperDemand*** - sum of upperDemand<br />3. ***MAXDIFFworkSupply*** - the max difference between demand and actual work over all days<br />4. ***TOTALbreakPrefpW*** - sum of break preferences that are respected<br />5. ***TotalworkDayDiff*** - sum of the differences between desired work days and actual work days of all agents<br />6. ***TotalweekEnd*** - sum of the full weekends<br /> |
| final objective function | **objBasic** - *TOTALupperDemand* when no relaxation, *TOTALunderDemand* when using relaxation<br />**objPref** - maximize *TOTALbreakprefpC* and minimize *TOTALirregularWeeks*<br />**objBalance** - minimize *TOTALbalance*<br />**final**: staticLex(objBasic, objPref, objBalance\*useBalanceFlag) | combination of 123456<br />(123, 1234, 1236, 12346, 54, 564, etc.)<br />using the MAXValue of each item as weights |

### Problems with former objectives (objective numbers refer to the previous model):

1. objective 1 and 2 are mixed together. When no relaxation, by constraints we won't have underdemands; when using relaxation, we just minimized the total number of underdemand days (*TOTALunderDemand*). 
2. objective 3 is somehow overlapped with 1 and 2 (3 is minimizing the difference between actual work and required work)
3. objective 4 is kept.
4. objective 5 uses the difference of agents' expected work days and actual workdays to balance, which is changed to using the ratio. And the ratio of satisfied breaks is also added as a metric for balancing.
5. objective 6 is not required in the problem description, so it is removed.

### New objectives in the present model:

I group the requirements into 3 groups, objBasic is for satisfying basic demands; objPref is for respecting more preferences (here we only have break preference); objBalance is to balance the respected breaks and actual works assigned to each agent using their ratios.



## Constraints:

1. **ctDemand** (kept): satisfied when not using relaxation.
2. **ctWorkDays** (kept): for each agent actual workday <= desired workday.
3. **ctBreak** (kept): for each agent acutal break per cycle >= expected break.
4. **ctSunday** (revised): in the previous model this is to ensure at least one Sunday's break per cycle, while the requirement is "at least one 2-day break per cycle, and it should cover a Sunday". So it is revised.
5. **ct2ConsBreak** (deleted): this is abundant as 4 has already given at least one 2 consecutive days of break per cycle.
6. **ct6DaysMax** (kept): at most 6 work days per 7 days.
7. **ct5ConsDaysMax** (kept): at most 5 consecutive working days.
8. **ctFixedWork** and **ctFixedBreak** (kept): fixed works and breaks are satisfied.
9. \* (Added to objective) An agent works on average 5 days per week, in irregular weeks no more than 6 days. The constraint of 5 days is too strict to give a solution so is relaxed to be at most 6 workdays per week, and we minimize this kind of irregular weeks.
10. **ct4daysBreak** (added): An agent should have at least 4 days' break per 2 weeks.

