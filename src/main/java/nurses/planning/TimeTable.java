package nurses.planning;

import nurses.Shift;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import nurses.specs.ITimetable;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;

public class TimeTable implements ITimetable {
    private Shift[][] shifts;
    private int days;
    private int cycles;
    private int agents;

    public TimeTable(int days, int agents) {
        this.days = days;
        this.cycles = days / 14;
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
        AreaReference aref = new AreaReference(aNamedCell.getRefersToFormula());
        CellReference corner1 = aref.getFirstCell();
        CellReference corner2 = aref.getLastCell();
        int height = Math.abs(corner1.getRow() - corner2.getRow());
        int length = Math.abs(corner1.getCol() - corner2.getCol());

        this.days = length;
        this.cycles = days / 14;
        this.agents = height;
        this.shifts = new Shift[agents][days];

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
            //System.out.println("" + day + ", " + agent + ", " + cont);
            this.shifts[agent - 1][day - 1] = shift;
        }
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
        return shifts[i][j];
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
        return cycles;
    }
}