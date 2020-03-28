/**
 * This file is part of nurse-rostering-solver, https://github.com/MatthiasPercelay/Model-PPC
 *
 * Copyright (c) 2020, Université Nice Sophia Antipolis. All rights reserved.
 *
 * Licensed under the BSD 3-clause license.
 * See LICENSE file in the project root for full license information.
 */
package nurses.pareto;

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

import nurses.planning.TimeTable;
import nurses.pareto.ParetoArchiveL;
import nurses.Shift;

public class TimetableReports implements ITimetableReports {

	public ParetoArchiveL archive;
	public IProblemInstance instance_problem;

	public TimetableReports() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void generateReports(IProblemInstance instance, ParetoArchiveL archive) {
		List<MOSolution> solutions = archive.getSolutions();
		System.out.println(solutions.size());
		for(int i=0; i < solutions.size();i++ ){
			System.out.println("solution print csv-----------------------------------------------");
			String csv_filename = "timetable" + String.valueOf(i) + ".csv";

			Shift[][] s = solutions.get(i).getSolution().shifts;

			try{
			FileWriter csvWriter = new FileWriter(csv_filename);
			csvWriter.append(" ;" + "Lundi;" + "Mardi;" + "Mercredi;"+"Jeudi;"+"Vendredi;"+"Samedi;"+"Dimanche;");
			csvWriter.append("Lundi;" + "Mardi;" + "Mercredi;"+"Jeudi;"+"Vendredi;"+"Samedi;"+"Dimanche;");
			csvWriter.append("Lundi;" + "Mardi;" + "Mercredi;"+"Jeudi;"+"Vendredi;"+"Samedi;"+"Dimanche;");
			csvWriter.append("Lundi;" + "Mardi;" + "Mercredi;"+"Jeudi;"+"Vendredi;"+"Samedi;"+"Dimanche;");
			csvWriter.append("\n");
			for(int ii = 0 ; ii < s.length;ii++){

				csvWriter.append("Nurse "+String.valueOf(ii)+";");
				for(int jj = 0; jj < s[ii].length;jj++){
					csvWriter.append(s[ii][jj].pseudo_data);
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
