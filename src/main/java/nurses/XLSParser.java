package nurses;

import java.awt.geom.Area;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;

public class XLSParser {

	private final File file;

	private Workbook wb;

	public XLSParser(File file) {
		this.file = file;
	}

	public void setUp() throws EncryptedDocumentException, InvalidFormatException, IOException {
		wb = WorkbookFactory.create(file);
	}
	
	class AreaDimension {

		public final int x0;
		public final int x1;
		public final int n;
		public final int y0;
		public final int y1;
		public final int m;

		public AreaDimension(AreaReference aref) {
			super();
			final CellReference first = aref.getFirstCell();
			final CellReference last = aref.getLastCell();
			x0 = Math.min(first.getRow(), last.getRow());
			y0 = Math.min(first.getCol(), last.getCol());
			x1 = Math.max(first.getRow(), last.getRow());
			y1 = Math.max(first.getCol(), last.getCol());
			n = x1 - x0 + 1;
			m = y1 - y0 + 1;
		} 
		
		public final int getX(CellReference cref) {
			return cref.getRow()-x0;
		}
		
		public final int getY(CellReference cref) {
			return cref.getCol()-y0;
		}

		public final int getLength() {
			return n > m ? n : m;
		}
	}
	
	private Cell getCell(CellReference cref) {
		return wb.getSheet(cref.getSheetName())
		.getRow(cref.getRow())
		.getCell(cref.getCol());
	}

	public int getRegionHeight(String namedRegion) {
		final Name aNamedCell = wb.getName(namedRegion);
		final AreaReference aref = new AreaReference(aNamedCell.getRefersToFormula(), wb.getSpreadsheetVersion());
		AreaDimension adim = new AreaDimension(aref);
		return adim.n;
	}

	public int getRegionWidth(String namedRegion) {
		final Name aNamedCell = wb.getName(namedRegion);
		final AreaReference aref = new AreaReference(aNamedCell.getRefersToFormula(), wb.getSpreadsheetVersion());
		AreaDimension adim = new AreaDimension(aref);
		return adim.m;
	}

	public int[] getIntRange(String namedRegion) {
		final Name aNamedCell = wb.getName(namedRegion);
		final AreaReference aref = new AreaReference(aNamedCell.getRefersToFormula(), wb.getSpreadsheetVersion());
		AreaDimension adim = new AreaDimension(aref);

		final int[] values = new int[adim.getLength()];
		CellReference[] crefs = aref.getAllReferencedCells();
		for (int i = 0; i < values.length; i++) {
			final Cell c = getCell(crefs[i]);
			values[i] = (int) c.getNumericCellValue();
		}
		return values;
	}

	public  int[][] getIntMatrix(String namedRegion) {
		final Name aNamedCell = wb.getName(namedRegion);
		final AreaReference aref = new AreaReference(aNamedCell.getRefersToFormula(), wb.getSpreadsheetVersion());
		AreaDimension adim = new AreaDimension(aref);
		
		final int[][] values = new int[adim.n][adim.m];
		CellReference[] crefs = aref.getAllReferencedCells();
		for (int i = 0; i < crefs.length; i++) {
			final Cell c = getCell(crefs[i]);
			values[adim.getX(crefs[i])][adim.getY(crefs[i])] = (int) c.getNumericCellValue();
		}
		return values;
	}
	
	public String[][] getStringMatrix(String namedRegion) {
		final Name aNamedCell = wb.getName(namedRegion);
		final AreaReference aref = new AreaReference(aNamedCell.getRefersToFormula(), wb.getSpreadsheetVersion());
		AreaDimension adim = new AreaDimension(aref);

		final String[][] values = new String[adim.n][adim.m];
		CellReference[] crefs = aref.getAllReferencedCells();
		for (int i = 0; i < crefs.length; i++) {
			final Cell c = getCell(crefs[i]);
			values[adim.getX(crefs[i])][adim.getY(crefs[i])] = c.getStringCellValue();
		}
		return values;
	}

	public Shift[][] getShiftMatrix(String namedRegion) {
		String[][] strings = getStringMatrix(namedRegion);
		Shift[][] res = new Shift[strings.length][strings[0].length];
		for (int i = 0; i < strings.length; i++) {
			for (int j = 0; j < strings[i].length; j++) {
				Shift shift;
				String val = strings[i][j];
				if (val.equals("")) {
					shift = Shift.NA;
				} else {
					shift = Shift.valueOf(val);
				}
				res[i][j] = shift;
			}
		}
		return res;
	}

	public int[][][] getPrefsMatrix(String namedRegion) {
		final String[][] strings = getStringMatrix(namedRegion);
		final int width = getRegionWidth(namedRegion);
		final int height = getRegionHeight(namedRegion);

		String[][] matin = new String[height / 3][width];
		String[][] jour = new String[height / 3][width];
		String[][] soir = new String[height / 3][width];

		for (int i = 0; i < strings.length / 3; i++) {
			matin[i] = strings[3 * i];
			jour[i] = strings[3 * i + 1];
			soir[i] = strings[3 * i + 2];
		}

		int[][][] prefints = new int[height / 3][width][3];
		for (int i = 0; i < matin.length; i++) {
			for (int j = 0; j < matin[0].length; j++) {
				prefints[i][j] = prefInts(matin[i][j], jour[i][j], soir[i][j]);
			}
		}
		return prefints;
	}

	public int[][] getBreaksMatrix(String namedRegion) {
		String[][] strings = getStringMatrix(namedRegion);
		int[][] res = new int[strings.length][strings[0].length];
		for (int i = 0; i < strings.length; i++) {
			for (int j = 0; j < strings[i].length; j++) {
				int pref = breakFromString(strings[i][j]);
				res[i][j] = pref;
			}
		}
		return res;
	}

	private int[] prefInts(String matin, String jour, String soir) {
		int[] res = new int[3];
		res[0] = prefFromString(matin);
		res[1] = prefFromString(jour);
		res[2] = prefFromString(soir);
		return res;
	}

	private int prefFromString(String pref) {
		String val = pref.toUpperCase();
		if (val.equals("NON")) return -1;
		else if (val.equals("OUI")) return 1;
		else return 0;
	}

	private int breakFromString(String pref) {
		String val = pref.toUpperCase();
		if (val.equals("OUI")) return 1;
		else return 0;
	}

	public static void main(String[] args) throws EncryptedDocumentException, InvalidFormatException, IOException {
		XLSParser parser = new XLSParser(new File("src/test/data/ucl-planning-december-19.xls"));
		parser.setUp();
		/*int[][] matrix = parser.getIntMatrix("demands");
		System.out.println(Arrays.deepToString(matrix));
		System.out.println("shiftPrefs width: "+parser.getRegionWidth("shiftPrefs"));
		System.out.println("shiftPrefs height: "+parser.getRegionHeight("shiftPrefs"));
		System.out.println("demands width: "+parser.getRegionWidth("demands"));
		System.out.println("demands height: "+parser.getRegionHeight("demands"));


		int[][][] prefs = parser.getPrefsMatrix("shiftPrefs");
		System.out.println(Arrays.deepToString(prefs));
		String[][] sprefs = parser.getStringMatrix("shiftPrefs");
		System.out.println(Arrays.deepToString(sprefs));*/
		NRProblemInstance instance = new NRProblemInstance(new File("src/test/data/ucl-planning-december-19.xls"));
		Shift[][] shifts = parser.getShiftMatrix("planning");
		System.out.println(Arrays.deepToString(shifts));
		System.out.println(Arrays.toString(instance.getWorkdays()));
		System.out.println(Arrays.toString(instance.getBreaksPerCycle()));
		System.out.println(Arrays.deepToString(instance.getBreakPreferences()));
		System.out.println(Arrays.deepToString(instance.getDemands()));
		System.out.println(Arrays.deepToString(instance.getShiftPreferences()));
	}

}
