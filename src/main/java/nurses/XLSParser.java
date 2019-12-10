package nurses;

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
	
	

	public static void main(String[] args) throws EncryptedDocumentException, InvalidFormatException, IOException {
		XLSParser parser = new XLSParser(new File("src/test/data/ucl-planning-december-19.xls"));
		parser.setUp();
		int[][] matrix = parser.getIntMatrix("demands");
		System.out.println(Arrays.deepToString(matrix));

	}

}
