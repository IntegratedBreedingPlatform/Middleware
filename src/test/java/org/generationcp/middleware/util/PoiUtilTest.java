
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

		Assert.assertEquals(-1, PoiUtil.getLastCellNum(null, rowNo));
	}

	@Test
	public void testRowAsString() {
		Assert.assertEquals("", PoiUtil.rowAsString(this.sheet, 0, ""));

		final int rowNo = 33;
		final Row row = this.sheet.createRow(rowNo);
		row.createCell(0).setCellValue("First Cell");
		row.createCell(1).setCellValue("Second Cell");
		Assert.assertEquals("First Cell-Second Cell", PoiUtil.rowAsString(this.sheet, rowNo, "-"));

	}

	@Test
	public void testRowAsStringMaxStringLength() {
		final int maxStringLength = 100;

		Assert.assertEquals("", PoiUtil.rowAsString(this.sheet, 0, "", maxStringLength));

		final int rowNo = 33;
		final Row row = this.sheet.createRow(rowNo);
		row.createCell(0).setCellValue("First Cell");
		row.createCell(1).setCellValue("Second Cell");

		Assert.assertEquals("First Cell-Second Cell", PoiUtil.rowAsString(this.sheet, rowNo, "-", maxStringLength));
		Assert.assertEquals("First Cell-", PoiUtil.rowAsString(this.sheet, rowNo, "-", 11));
		Assert.assertEquals("First Cell-Second", PoiUtil.rowAsString(this.sheet, rowNo, "-", 17));
	}

	@Test(expected = NullPointerException.class)
	public void testRowAsStringArrayNullRow() {
		final String[] arr = new String[] {};
		Assert.assertArrayEquals(arr, PoiUtil.rowAsStringArray(this.sheet, 0));
	}

	@Test
	public void testRowAsStringArrayEmpty() {
		final String[] arr = new String[] {};
		this.sheet.createRow(0);
		Assert.assertArrayEquals(arr, PoiUtil.rowAsStringArray(this.sheet, 0));
	}

	@Test
	public void testRowAsStringArrayRange() {
		final String[] arr = new String[] {"First Cell", "Second Cell"};
		this.sheet.createRow(0);
		final Row row = this.sheet.createRow(0);
		row.createCell(0).setCellValue("First Cell");
		row.createCell(1).setCellValue("Second Cell");
		Assert.assertArrayEquals(arr, PoiUtil.rowAsStringArray(this.sheet, 0, 0, 17, 100));
	}

	@Test
	public void testRowAsStringArrayRangeMax() {
		final String[] arr = new String[] {"First Cell", "Second Cell"};
		this.sheet.createRow(0);
		final Row row = this.sheet.createRow(0);
		row.createCell(0).setCellValue("First Cell");
		row.createCell(1).setCellValue("Second Cell");
		Assert.assertArrayEquals(arr, PoiUtil.rowAsStringArray(this.sheet, 0, 0, 17, 12));
	}

	@Test
	public void testRowAsStringArray() {
		final String[] arr = new String[] {"First Cell", "Second Cell"};
		final Row row = this.sheet.createRow(0);
		row.createCell(0).setCellValue("First Cell");
		row.createCell(1).setCellValue("Second Cell");
		Assert.assertArrayEquals(arr, PoiUtil.rowAsStringArray(this.sheet, 0));
	}

	@Test
	public void testAsStringArrayColumn() {
		this.sheet.createRow(0).createCell(0).setCellValue("First Cell");
		this.sheet.createRow(1).createCell(0).setCellValue("Second Cell");
		Assert.assertArrayEquals(new String[] {"First Cell", "Second Cell"}, PoiUtil.asStringArrayColumn(this.sheet, 0));
		this.sheet.getRow(1).getCell(0).setCellValue("");
		;
		Assert.assertArrayEquals(new String[] {"First Cell", ""}, PoiUtil.asStringArrayColumn(this.sheet, 0));
		Assert.assertArrayEquals(new String[] {"", ""}, PoiUtil.asStringArrayColumn(this.sheet, 1));
	}

	@Test
	public void testColumnHasEmpty() {
		Assert.assertEquals(true, PoiUtil.columnHasEmpty(this.sheet, 1));
		final Row row = this.sheet.createRow(0);
		row.createCell(1).setCellValue("Second Cell");
		Assert.assertEquals(true, PoiUtil.columnHasEmpty(this.sheet, 0));
		Assert.assertEquals(false, PoiUtil.columnHasEmpty(this.sheet, 1));
		final Row row3 = this.sheet.createRow(3);
		row3.createCell(0).setCellValue("First Cell");
		row3.createCell(1).setCellValue("Second Cell");
		Assert.assertEquals(true, PoiUtil.columnHasEmpty(this.sheet, 0));
		Assert.assertEquals(true, PoiUtil.columnHasEmpty(this.sheet, 1));
	}

	@Test
	public void testRowHasEmpty() {
		Assert.assertEquals(true, PoiUtil.rowHasEmpty(this.sheet, 0, 1, 4));
		final Row row = this.sheet.createRow(0);
		row.createCell(2).setCellValue("Second Cell");
		Assert.assertEquals(true, PoiUtil.rowHasEmpty(this.sheet, 0, 1, 2));
		row.createCell(1).setCellValue("First Cell");
		Assert.assertEquals(false, PoiUtil.rowHasEmpty(this.sheet, 0, 1, 2));
		row.getCell(1).setCellValue("");
		Assert.assertEquals(true, PoiUtil.rowHasEmpty(this.sheet, 0, 1, 2));
	}

	@Test
	public void testRowIsEmpty() {
		Assert.assertEquals(true, PoiUtil.rowIsEmpty(this.sheet, 0, 1, 4));
		final Row row = this.sheet.createRow(0);
		Assert.assertEquals(true, PoiUtil.rowIsEmpty(this.sheet, 0, 1, 4));
		row.createCell(2).setCellValue("Second Cell");
		Assert.assertEquals(false, PoiUtil.rowIsEmpty(this.sheet, 0, 1, 4));
		row.getCell(2).setCellValue("");
		Assert.assertEquals(true, PoiUtil.rowIsEmpty(this.sheet, 0, 1, 4));
	}

}
