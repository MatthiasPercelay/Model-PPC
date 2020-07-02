/**
 * This file is part of nurse-rostering-solver, https://github.com/MatthiasPercelay/Model-PPC
 *
 * Copyright (c) 2020, Université Nice Sophia Antipolis. All rights reserved.
 *
 * Licensed under the BSD 3-clause license.
 * See LICENSE file in the project root for full license information.
 */
package nurses.pareto;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Arrays;
import java.util.List;
import nurses.specs.IParetoArchive;
import nurses.specs.IProblemInstance;
import nurses.specs.ITimetableReports;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.*;

import nurses.planning.TimeTable;
import nurses.pareto.ParetoArchiveL;
import nurses.Shift;

public class TimetableReports implements ITimetableReports {

	public ParetoArchiveL archive;
	public IProblemInstance instance_problem;
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");

	public TimetableReports() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void generateReports(IProblemInstance instance, ParetoArchiveL archive) {
		
		List<MOSolution> solutions = archive.getSolutions();
		System.out.println(solutions.size());
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String time = sdf.format(timestamp);
		String path = "solutions/" + time;
		File directory = new File(path);
		if (! directory.exists()){
			directory.mkdir();
		}

		for(int i=0; i < solutions.size();i++ ){

			NRSolutionStatistics stats = new NRSolutionStatistics(instance,solutions.get(i).getSolution());

			int[] tmp = stats.getTotalBreakSatisfaction();
			int score = 0;
			for(int jj = 0; jj<tmp.length;jj++){
				System.out.print(tmp[jj] + " ");
				score+=tmp[jj];
			}
			System.out.println();
			System.out.println(score);

			int[] tmp2 = stats.getTotalShitSatisfaction();
			for(int jj = 0; jj<tmp2.length;jj++){
				System.out.print(tmp2[jj]+" ");
			}
			System.out.println();

			System.out.println("solution print csv-----------------------------------------------");
			

			String csv_filename = path + "/timetable" + String.valueOf(i) + ".csv";
			stats.stats_for_dashboard( path+"/timetable" + String.valueOf(i) + ".txt");
			Shift[][] s = solutions.get(i).getSolution().shifts;

			try{
			FileWriter csvWriter = new FileWriter(csv_filename);
			csvWriter.append(" ;" + "L;" + "M;" + "M;"+"J;"+"V;"+"S;"+"D;");
			csvWriter.append("L;" + "M;" + "M;"+"J;"+"V;"+"S;"+"D;");
			csvWriter.append("L;" + "M;" + "M;"+"J;"+"V;"+"S;"+"D;");
			csvWriter.append("L;" + "M;" + "M;"+"J;"+"V;"+"S;"+"D;");
			csvWriter.append("\n");
			for(int ii = 0 ; ii < s.length;ii++){

				csvWriter.append("Nurse "+String.valueOf(ii)+";");
				for(int jj = 0; jj < s[ii].length;jj++){
					csvWriter.append(s[ii][jj].pseudo_data.replaceAll("\\s+",""));
					csvWriter.append(";");
				}
				csvWriter.append("\n");
			}
			
			csvWriter.flush();
			csvWriter.close();
			}


			catch (IOException ex){

			}
			

		}


	}

	
}
