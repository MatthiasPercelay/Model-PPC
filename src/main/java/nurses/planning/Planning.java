package nurses.planning;

import nurses.Shift;
import java.io.File;
import java.io.IOException;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;

public class Planning {
    private Shift[][] days;
    int cycles;
    int agents;

    public Planning(int cycles, int agents) {
        this.days = new Shift[agents][14 * cycles];
    }

    private void setShift(Shift shift, int day, int agent) {
        this.days[agent][day] = shift;
    }

    public int getCycles() {
        return cycles;
    }

    public int getAgents() {
        return agents;
    }

    public Planning(File file, int cycles, int agents) {
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
        AreaReference aref = new AreaReference(aNamedCell.getRefersToFormula());
        CellReference[] crefs = aref.getAllReferencedCells();
        for (int i = 0; i < crefs.length; i++) {
            Sheet s = wb.getSheet(crefs[i].getSheetName());
            int day = crefs[i].getRow();
            Row r = s.getRow(day);
            int agent = crefs[i].getCol();
            Cell c = r.getCell(agent);
            String cont = c.getStringCellValue().toUpperCase();
            Shift shift;
            if (cont.equals("")) {
                shift = Shift.NA;
            } else {
                shift = Shift.valueOf(cont);
            }
            this.setShift(shift, day, agent);
        }
    }
}