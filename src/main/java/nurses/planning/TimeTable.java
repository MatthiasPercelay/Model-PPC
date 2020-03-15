/**
 * This file is part of nurse-rostering-solver, https://github.com/MatthiasPercelay/Model-PPC
 *
 * Copyright (c) 2020, Université Nice Sophia Antipolis. All rights reserved.
 *
 * Licensed under the BSD 3-clause license.
 * See LICENSE file in the project root for full license information.
 */
package nurses.planning;

import ilog.opl.IloOplDataHandler;
import nurses.Shift;
import nurses.XLSParser;
import nurses.specs.ITimetable;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class TimeTable implements ITimetable {
    private Shift[][] shifts;
    private int days;
    private int agents;

    public TimeTable(int days, int agents) {
        this.days = days;
        this.agents = agents;
        this.shifts = new Shift[agents][days];
    }

    private void setShift(Shift shift, int day, int agent) {
        this.shifts[agent][day] = shift;
    }

    /**
     * Importing constructor that populates the planning from an Excel file
     * @param file The .xls file to read from
     */
    public TimeTable(File file) {
        XLSParser parser = new XLSParser(file);

        try {
            parser.setUp();
        } catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
            e.printStackTrace();
        }

        String regionName = "planning";

        this.days = parser.getRegionWidth(regionName);
        this.agents = parser.getRegionHeight(regionName);
        this.shifts = parser.getShiftMatrix(regionName);
    }

    public TimeTable(Shift[][] shifts){
    	this(shifts[0].length, shifts.length);
    	setShifts(shifts);
	}

	public void customRead(IloOplDataHandler handler) {
		handler.startElement("timetable");
		handler.startArray();
		for (int i=1;i<=shifts.length;i++) {
			handler.startArray();
			for (int j=1;j<=shifts[i].length;j++)
				handler.addStringItem(shifts[i][j].toString());
			handler.endArray();
		}
		handler.endArray();
		handler.endElement();
	}

    /**
     * Exports the contents of the current planning to an Excel file
     * @param filename name of the output file
     */
    public void exportToExcel(String filename) {
        Workbook wb = new HSSFWorkbook();
        Sheet sheet = wb.createSheet("Planning");
        Row row1 = sheet.createRow(0);
        String[] letters = weekdayLetters(getNbCycles());
        for (int i = 0; i < days; i++) {
            row1.createCell(i + 1).setCellValue(letters[i]);
        }
        for (int i = 0; i < agents; i++) {
            Row currentRow = sheet.createRow(i+1);
            currentRow.createCell(0).setCellValue("A" + (i+1));
            for (int j = 0; j < days; j++) {
                currentRow.createCell(j + 1).setCellValue(shifts[i][j].toString());
            }
        }
        CellReference corner1 = new CellReference(1, 1);
        CellReference corner2 = new CellReference(agents, days);
        AreaReference aref = new AreaReference(corner1, corner2);
        String sref = aref.toString();

		try (OutputStream fileOut = new FileOutputStream(filename)) {
			wb.write(fileOut);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String[] weekdayLetters(int cycles) {
		String[] week = new String[]{"L", "M", "M", "J", "V", "S", "D"};
		String[] res = new String[14 * cycles];
		for (int i = 0; i < 2 * cycles; i++) {
			for (int j = 0; j < 7; j++) {
				res[7 * i + j] = week[j];
			}
		}
		return res;
	}

	/**
	 *
	 * @param k an agent
	 * @return agent k's schedule on this planning
	 */
	public Shift[] getAgentsSchedule(int k) {
		return shifts[k];
	}

	@Override
	public Shift getShift(int i, int j) {
		return shifts[i - 1][j - 1];
	}

	@Override
	public boolean isWorkdayAssignment() {
		for (int i = 0; i < shifts.length; i++) {
			for (int j = 0; j < shifts[i].length; j++) {
				if (shifts[i][j] == Shift.NA) return false;
			}
		}
		return true;
	}

	@Override
	public boolean isShiftAssignment() {
		for (int i = 0; i < shifts.length; i++) {
			for (int j = 0; j < shifts[i].length; j++) {
				if (shifts[i][j] == Shift.NA || shifts[i][j] == Shift.W) return false;
			}
		}
		return true;
	}

	@Override
	public int getNbAgents() {
		return agents;
	}

    @Override
    public int getNbDays() {
        return days;
    }

    public int getNbCycles() {
        return days / 14;
    }

	public void setShifts(Shift[][] days) {
		this.shifts = days;
	}
}