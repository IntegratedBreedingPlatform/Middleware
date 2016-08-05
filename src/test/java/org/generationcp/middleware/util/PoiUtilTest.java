
package org.generationcp.middleware.util;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PoiUtilTest {

	private Sheet sheet;
	private Workbook workbook;

	@Before
	public void setUp() {
		this.workbook = new HSSFWorkbook();
		this.sheet = this.workbook.createSheet();
	}

	@Test
	public void testGetLastCellNum() {
		int rowNo = 33;
		int col = 0;
		Row row = this.sheet.createRow(rowNo);
		Cell cell = row.createCell(col);
		cell.setCellValue("Cell value");
		Assert.assertEquals(col + 1, PoiUtil.getLastCellNum(this.sheet, rowNo));

		col = 27;
		rowNo = 17;
		row = this.sheet.createRow(rowNo);
		cell = row.createCell(col);
		cell.setCellValue("Cell value");
		Assert.assertEquals(col + 1, PoiUtil.getLastCellNum(this.sheet, rowNo));

		row = this.sheet.createRow(rowNo);
		cell = row.createCell(col);
		Assert.assertEquals(-1, PoiUtil.getLastCellNum(this.sheet, rowNo));

		row = this.sheet.createRow(rowNo);
		cell = row.createCell(col);
		cell.setCellValue("");
		Assert.assertEquals(-1, PoiUtil.getLastCellNum(this.sheet, rowNo));

		row = this.sheet.createRow(rowNo);
		cell = row.createCell(col);
		cell.setCellValue(1);
		Assert.assertEquals(col + 1, PoiUtil.getLastCellNum(this.sheet, rowNo));

		this.sheet.removeRow(row);
		Assert.assertEquals(-1, PoiUtil.getLastCellNum(this.sheet, rowNo));
	}
}
