/**
 * This file is part of nurse-rostering-solver, https://github.com/MatthiasPercelay/Model-PPC
 *
 * Copyright (c) 2020, Université Nice Sophia Antipolis. All rights reserved.
 *
 * Licensed under the BSD 3-clause license.
 * See LICENSE file in the project root for full license information.
 */
package nurses.pareto;

import nurses.Shift;
import nurses.planning.TimeTable;
import nurses.specs.IProblemInstance;
import nurses.specs.ITimetable;
import java.io.IOException;

import java.io.FileWriter;
import java.io.PrintWriter;

import javax.swing.plaf.basic.BasicComboBoxUI.FocusHandler;


public class NRSolutionStatistics {
    private IProblemInstance instance;
    private TimeTable timetable;

    public NRSolutionStatistics(IProblemInstance instance, TimeTable timetable) {
        this.instance = instance;
        this.timetable = timetable;
    }

    public static MOSolution makeMOSolution(IProblemInstance instance,  Shift[][] shifts) {
        NRSolutionStatistics sol = new NRSolutionStatistics(instance, new TimeTable(shifts));
        return new MOSolution(new TimeTable(shifts), sol.getObjectiveArray());
    }

    public double[] getObjectiveArray() {
        double[] res = new double[4];
        res[0] = -(double)getTotalWork();
        res[1] = (double)getTotalWeekends();
        res[2] = -(double)getTotalFiveDays();
        res[3] = -(double)getTotalSixDays();
        return res;
    }

    public int getTotalWork(int agent) {
        int worked = 0;
        for (int day = 1; day <= timetable.getNbDays(); day++) {
            if (timetable.getShift(agent, day).isWork()) {
                worked++;
            }
        }
        return worked;
    }

    public int getTotalWeekends(int agent) {
        int weekend = 0;

        for (int day = 1; day <= timetable.getNbDays(); day++) {

            // we have a break on a sunday and the previous saturday is a break too
            if (day % 7 == 0 && timetable.getShift(agent, day).isBreak() && timetable.getShift(agent, day - 1).isBreak()) {
                weekend++;
            }
        }
        return weekend;
    }

    private int getTotalNDays(int agent, int days) {
        int nDays = 0;
        int daysInARow = 0;
        for (int day = 1; day <= timetable.getNbDays(); day++) {
            if (timetable.getShift(agent, day).isWork()) {
                daysInARow++;
            }
            if (timetable.getShift(agent, day).isBreak()) {
                daysInARow = 0;
            }
            if (daysInARow >= days) {
                nDays++;
            }
        }

        return nDays;
    }

    public int getTotalFiveDays(int agent) {
        return getTotalNDays(agent, 5);
    }

    public int getTotalSixDays(int agent) {
        return getTotalNDays(agent, 6);
    }

    public int getTotalWork() {
        int total = 0;
        for (int i = 1; i <= timetable.getNbAgents(); i++) {
            total += getTotalWork(i);
        }
        return total;
    }

    public int getTotalWeekends() {
        int total = 0;
        for (int i = 1; i <= timetable.getNbAgents(); i++) {
            total += getTotalWeekends(i);
        }
        return total;
    }

    public int getTotalFiveDays() {
        int total = 0;
        for (int i = 1; i <= timetable.getNbAgents(); i++) {
            total += getTotalFiveDays(i);
        }
        return total;
    }

    public int getTotalSixDays() {
        int total = 0;
        for (int i = 1; i <= timetable.getNbAgents(); i++) {
            total += getTotalSixDays(i);
        }
        return total;
    }

    public IProblemInstance getInstance() {
        return this.instance;
    }

    public int getTotalSM(int agent) {
        // get the number of times the agent works an evening shift then a morning shift
        int count = 0;
        for (int day = 1; day <= instance.getNbDays()-1; day++) {
            if (timetable.getShift(agent, day) ==Shift.S && timetable.getShift(agent, day+1) == Shift.M) {
                count++;
            }
        }
        return count;
    }

    public int getTotalSM() {
        int count = 0;
        for (int agent = 1; agent <= instance.getNbAgents(); agent++) {
            count += getTotalSM(agent);
        }
        return count;
    }

    public int getTotalSameShift(int agent) {
        // count how many times the agent works the same shift 2 days in a row
        int count = 0;
        for (int day = 1; day <= instance.getNbDays()-1; day++) {
            if (timetable.getShift(agent, day) == timetable.getShift(agent, day+1)) {
                count++;
            }
        }
        return count;
    }

    public int getTotalSameShift() {
        int count = 0;
        for (int agent = 1; agent <= instance.getNbAgents(); agent++) {
            count += getTotalSameShift(agent);
        }
        return count;
    }

    public int[] getTotalBreakSatisfaction(){
        int nb_agent = timetable.getNbAgents();

        int[] breakprefScore = new int[nb_agent];
        Shift[][] data = timetable.getshifts();

        for(int i =0;i<nb_agent;i++){
            int totalDays = 0;
            int breakScore = 0;
            int[][] breakpref = instance.getBreakPreferences();
            for(int c=0;c<2;c++){

                for(int j =0; j<14;j++){

                    if((breakpref[i][j] != 0)&&(data[i][totalDays].isBreak())){
                        breakScore+=1;
                    }
                    totalDays+=1;
                }
            }
            breakprefScore[i] = breakScore;

        }
        return breakprefScore;
    }

    public int[] getTotalShitSatisfaction(){
        int nb_agent = timetable.getNbAgents();
        int[] shiftScore = new int[nb_agent];

        for(int i =0;i<nb_agent;i++){
            int totalDays = 0;
            int ShiftScore = 0;
            int[][][] shiftPref = instance.getShiftPreferences();
            // System.out.println(shiftPref[0].length);
            // System.out.println(shiftPref[0][0].length);
            // for(int a=0;a<shiftPref.length;a++){
            //     for(int b=0;b<shiftPref[a].length;b++){
            //         for(int c=0;c<shiftPref[a][b].length;c++){
            //             System.out.print(shiftPref[a][b][c]+" ");
            //         }
            //         System.out.println();
            //     }
            //     System.out.println();
            // }
            for(int c=0;c<2;c++){
                for(int j =0; j<14;j++){
                    for(int z=0;z<3;z++){
                        if((shiftPref[i][j][z] == 1) && (shiftToPrefIndex(timetable.getShift(i+1, totalDays+1))) == z   ){
                            // System.out.println("oui "+i +" "+ j + " "+ z +" ");
                            // System.out.println(j);
                            // System.out.println(z);
                            ShiftScore+=1;
                        }
                        else if((shiftPref[i][j][z] == -1) && (shiftToPrefIndex(timetable.getShift(i+1, totalDays+1))) == shiftPref[i][j][z]   ){
                            // System.out.println("non " + i +" "+ j + " "+ z +" ");
                            ShiftScore+=1;
                        }
                    }
                    totalDays+=1;
                }
            }
            shiftScore[i] = ShiftScore;

        }
        return shiftScore;
        
    }
    
    private int shiftToPrefIndex(Shift s) {
        if (s == Shift.M) return 0;
        else if (s == Shift.J) return 1;
        else if (s == Shift.S) return 2;
        else if (s == Shift.B) return -1;
        else return -2;
    }


    public void stats_for_dashboard(String filename){

        // FileWriter filewriter = new FileWriter(filename);
        try (PrintWriter out = new PrintWriter(filename)) {
        // PrintWriter out = new PrintWriter(filename);
        out.println(getTotalWork());
        out.println(getTotalWeekends());
        int[] tmp = getTotalBreakSatisfaction();
        for(int i =0;i<tmp.length;i++){
            out.print(tmp[i]+" ");
        }
        out.println();
        out.println(getTotalSixDays());
        int[] tmp2 = getTotalShitSatisfaction();
        for(int i =0;i<tmp2.length;i++){
            out.print(tmp2[i]+" ");
        }
        out.println();

        out.close();}
        catch(IOException e){
            e.printStackTrace();
        }
    }


    // private int getSatisfaction(int agent, int day) {
    //     Shift s = timetable.getShift(agent, day);
    //     if (timetable.getShift(agent, day).isBreak()) {
    //         return 0;
    //     }
    //     return instance.getShiftPreferences()[agent-1][day-1][shiftToPrefIndex(s)];
    // }

    // public int getTotalSatisfaction(int agent) {
    //     int count = 0;
    //     for (int day = 1; day <= timetable.getNbDays(); day++) {
    //         count += getSatisfaction(agent, day);
    //     }
    //     return count;
    // }

    // public int getTotalSatisfaction() {
    //     int count = 0;
    //     System.out.println("days "+timetable.getNbDays() + " agents " + timetable.getNbAgents());
    //     for (int agent = 1; agent <= timetable.getNbAgents(); agent++) {
    //         count += getTotalSatisfaction(agent);
    //     }
    //     return count;
    // }

    // public double avgSatisfaction() {
    //     return (double)getTotalSatisfaction() / (double)timetable.getNbAgents();
    // }

    // public double stdDevSatisfaction() {
    //     // standard deviation of the satisfaction
    //     double avgSat = avgSatisfaction();
    //     double variation = 0;
    //     for (int agent = 1; agent <= timetable.getNbAgents(); agent++) {
    //         variation += Math.pow((getTotalSatisfaction(agent) - avgSat),2);
    //     }
    //     return Math.sqrt(variation / timetable.getNbAgents());
    // }


}
