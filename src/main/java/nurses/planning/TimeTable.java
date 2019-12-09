package nurses.planning;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;

import ilog.opl.IloCustomOplDataSource;
import ilog.opl.IloOplDataHandler;
import ilog.opl.IloOplFactory;
import nurses.Shift;
import nurses.specs.ITimetable;

public class TimeTable implements ITimetable {
	private Shift[][] days;
	private int cycles;
	private int agents;

	public TimeTable(int cycles, int agents) {
		this.cycles = cycles;
		this.agents = agents;
		this.days = new Shift[agents][14 * cycles];
	}

	private void setShift(Shift shift, int day, int agent) {
		this.days[agent][day] = shift;
	}

	public int getCycles() {
		return cycles;
	}

	/**
	 * Importing constructor that populates the planning from an Excel file
	 * @param file The .xls file to read from
	 * @param cycles The number of cycles to plan for
	 * @param agents The number of agents to plan for
	 */
	public TimeTable(File file, int cycles, int agents) {
		this(cycles, agents);
		org.apache.poi.ss.usermodel.Workbook wb = null;
		try {
			wb = WorkbookFactory.create(file);
		} catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
			e.printStackTrace();
		}
		String cname = "planning";
		assert wb != null;
		int namedCellIdx = wb.getNameIndex(cname);
		Name aNamedCell = wb.getNameAt(namedCellIdx);
		AreaReference aref = new AreaReference(aNamedCell.getRefersToFormula(), wb.getSpreadsheetVersion());
		CellReference[] crefs = aref.getAllReferencedCells();
		for (int i = 0; i < crefs.length; i++) {
			Sheet s = wb.getSheet(crefs[i].getSheetName());
			int agent = crefs[i].getRow();
			Row r = s.getRow(agent);
			int day = crefs[i].getCol();

			Cell c = r.getCell(agent);
			String cont = c.getStringCellValue().toUpperCase();
			Shift shift;
			if (cont.equals("")) {
				shift = Shift.NA;
			} else {
				shift = Shift.valueOf(cont);
			}
			System.out.println("" + day + ", " + agent + ", " + cont);
			this.setShift(shift, day - 1, agent - 1);
		}
	}

	public void customRead(IloOplDataHandler handler) {
		handler.startElement("timetable");
		handler.startArray();
		for (int i=1;i<=days.length;i++) {
			handler.startArray();
			for (int j=1;j<=days[i].length;j++)
				handler.addStringItem(days[i][j].toString());
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
		String[] letters = weekdayLetters(this.cycles);
		for (int i = 0; i < 14 * this.cycles; i++) {
			row1.createCell(i + 1).setCellValue(letters[i]);
		}

		for (int i = 0; i < agents; i++) {
			Row currentRow = sheet.createRow(i+1);
			currentRow.createCell(0).setCellValue("A" + (i+1));
			for (int j = 0; j < 14 * cycles; j++) {
				currentRow.createCell(j + 1).setCellValue(days[i][j].toString());
			}
		}

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
		return days[k];
	}

	@Override
	public Shift getShift(int i, int j) {
		return days[i][j];
	}

	@Override
	public boolean isWorkdayAssignment() {
		for (int i = 0; i < days.length; i++) {
			for (int j = 0; j < days[i].length; j++) {
				if (days[i][j] == Shift.NA) return false;
			}
		}
		return true;
	}

	@Override
	public boolean isShiftAssignment() {
		for (int i = 0; i < days.length; i++) {
			for (int j = 0; j < days[i].length; j++) {
				if (days[i][j] == Shift.NA || days[i][j] == Shift.W) return false;
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
		return cycles * 14;
	}
}