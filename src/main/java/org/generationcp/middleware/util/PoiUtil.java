/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.util;

import java.awt.Point;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A convenience class for POI library.
 *
 * @author Glenn Marintes
 */
public class PoiUtil {

	private static final Logger LOG = LoggerFactory.getLogger(PoiUtil.class);

	public static final SimpleDateFormat EXCEL_DATE_FORMATTER = Util.getSimpleDateFormat(Util.DATE_AS_NUMBER_FORMAT);

	private PoiUtil() {
		// make the constructor private to hide the implicit public one
	}

	// WorkBook convenience methods
	public static void setRepeatingRows(Workbook workBook, int sheetIndex, int fromRow, int toRow) {
		workBook.setRepeatingRowsAndColumns(sheetIndex, -1, -1, fromRow, toRow);
	}

	public static void setRepeatingColumns(Workbook workBook, int sheetIndex, int fromCol, int toCol) {
		workBook.setRepeatingRowsAndColumns(sheetIndex, fromCol, toCol, -1, -1);
	}

	public static void setRepeatingRowsAndColumns(Workbook workBook, int sheetIndex, int fromCol, int toCol, int fromRow, int toRow) {
		workBook.setRepeatingRowsAndColumns(sheetIndex, fromCol, toCol, fromRow, toRow);
	}

	public static Double getCellNumericValue(final Cell cell) {
		return cell == null ? null : cell.getNumericCellValue();
	}

	public static String getCellStringValue(final Workbook wb, final Integer sheetNumber, final Integer rowNumber, final Integer columnNumber) {

		final Sheet sheet = wb.getSheetAt(sheetNumber);
		final Row row = sheet.getRow(rowNumber);

		if (null == row) {
			return null;
		}

		final Cell cell = row.getCell(columnNumber);
		return PoiUtil.getCellStringValue(cell);

	}

	public static Double getCellNumericValue(final Workbook wb, final Integer sheetNumber, final Integer rowNumber, final Integer columnNumber) {

		final Sheet sheet = wb.getSheetAt(sheetNumber);
		final Row row = sheet.getRow(rowNumber);

		if (null == row) {
			return null;
		}

		final Cell cell = row.getCell(columnNumber);
		return PoiUtil.getCellNumericValue(cell);

	}

	public static String getCellStringValue(Cell cell) {
		Object out = PoiUtil.getCellValue(cell);

		if (out != null) {
			return out.toString().trim();
		}

		return null;
	}

	public static Object getCellValue(Cell cell) {
		if (cell == null) {
			return null;
		}
		switch (cell.getCellType()) {
			case Cell.CELL_TYPE_BOOLEAN:
				return cell.getBooleanCellValue();
			case Cell.CELL_TYPE_STRING:
				return cell.getStringCellValue();
			case Cell.CELL_TYPE_NUMERIC:
				return PoiUtil.getNumericValue(cell);
			case Cell.CELL_TYPE_FORMULA:
				return PoiUtil.getFormulaValue(cell);
			default:
				return null;
		}
	}

	// setCellValue with cell name as parameter

	private static Object getFormulaValue(Cell cell) {
		switch (cell.getCachedFormulaResultType()) {
			case Cell.CELL_TYPE_NUMERIC:
				return cell.getNumericCellValue();
			case Cell.CELL_TYPE_STRING:
				return cell.getRichStringCellValue();
			default:
				return cell.getCellFormula();
		}
	}

	private static Object getNumericValue(Cell cell) {
		if (DateUtil.isCellDateFormatted(cell)) {
			Date date = cell.getDateCellValue();
			return PoiUtil.EXCEL_DATE_FORMATTER.format(date);
		}

		double doubleVal = cell.getNumericCellValue();
		if (doubleVal % 1 == 0) {
			return (int) doubleVal;
		} else {
			return doubleVal;
		}
	}

	public static void setCellValue(Sheet sheet, String cellName, String value) {
		Point cellIndex = PoiUtil.getCellIndex(cellName);
		PoiUtil.setCellValue(sheet, cellIndex.y, cellIndex.x, value);
	}

	public static void setCellValue(Sheet sheet, String cellName, Integer value) {
		Point cellIndex = PoiUtil.getCellIndex(cellName);
		PoiUtil.setCellValue(sheet, cellIndex.y, cellIndex.x, value);
	}

	public static void setCellValue(Sheet sheet, String cellName, Long value) {
		Point cellIndex = PoiUtil.getCellIndex(cellName);
		PoiUtil.setCellValue(sheet, cellIndex.y, cellIndex.x, value);
	}

	public static void setCellValue(Sheet sheet, String cellName, Double value) {
		Point cellIndex = PoiUtil.getCellIndex(cellName);
		PoiUtil.setCellValue(sheet, cellIndex.y, cellIndex.x, value);
	}

	public static void setCellValue(Sheet sheet, String cellName, Date value) {
		Point cellIndex = PoiUtil.getCellIndex(cellName);
		PoiUtil.setCellValue(sheet, cellIndex.y, cellIndex.x, value);
	}

	public static void setCellValue(Sheet sheet, String cellName, Object value) {
		Point cellIndex = PoiUtil.getCellIndex(cellName);
		PoiUtil.setCellValue(sheet, cellIndex.y, cellIndex.x, value);
	}

	// setCellValue with String column name and integer row index as parameter
	public static void setCellValue(Sheet sheet, String colName, int rowIndex, String value) {
		int columnIndex = PoiUtil.getColumnIndex(colName);
		PoiUtil.setCellValue(sheet, columnIndex, rowIndex, value);
	}

	public static void setCellValue(Sheet sheet, String colName, int rowIndex, Integer value) {
		int columnIndex = PoiUtil.getColumnIndex(colName);
		PoiUtil.setCellValue(sheet, columnIndex, rowIndex, value);
	}

	public static void setCellValue(Sheet sheet, String colName, int rowIndex, Long value) {
		int columnIndex = PoiUtil.getColumnIndex(colName);
		PoiUtil.setCellValue(sheet, columnIndex, rowIndex, value);
	}

	public static void setCellValue(Sheet sheet, String colName, int rowIndex, Double value) {
		int columnIndex = PoiUtil.getColumnIndex(colName);
		PoiUtil.setCellValue(sheet, columnIndex, rowIndex, value);
	}

	public static void setCellValue(Sheet sheet, String colName, int rowIndex, Date value) {
		int columnIndex = PoiUtil.getColumnIndex(colName);
		PoiUtil.setCellValue(sheet, columnIndex, rowIndex, value);
	}

	public static void setCellValue(Sheet sheet, String colName, int rowIndex, Object value) {
		int columnIndex = PoiUtil.getColumnIndex(colName);
		PoiUtil.setCellValue(sheet, columnIndex, rowIndex, value);
	}

	// setCellValue with integer indices as parameter

	public static void setCellValue(Sheet sheet, int columnIndex, int rowIndex, String value) {
		Row row = sheet.getRow(rowIndex);
		if (row == null) {
			row = sheet.createRow(rowIndex);
		}

		Cell cell = row == null ? null : row.getCell(columnIndex);
		if (cell == null) {
			cell = row == null ? null : row.createCell(columnIndex);
		}

		if (row == null || cell == null) {
			throw new IllegalArgumentException("Cell with col=" + columnIndex + " and row=" + rowIndex + " is null.");
		}

		if (value == null) {
			cell.setCellValue("");
		} else {
			cell.setCellValue(value);
		}
	}

	public static void setCellValue(Sheet sheet, int columnIndex, int rowIndex, Integer value) {
		Row row = sheet.getRow(rowIndex);
		if (row == null) {
			row = sheet.createRow(rowIndex);
		}

		Cell cell = row == null ? null : row.getCell(columnIndex);
		if (cell == null) {
			cell = row == null ? null : row.createCell(columnIndex);
		}

		if (row == null || cell == null) {
			throw new IllegalArgumentException("Cell with col=" + columnIndex + " and row=" + rowIndex + " is null.");
		}

		if (value == null) {
			cell.setCellValue("");
		} else {
			cell.setCellValue(value);
		}
	}

	public static void setCellValue(Sheet sheet, int columnIndex, int rowIndex, Long value) {
		Row row = sheet.getRow(rowIndex);
		if (row == null) {
			row = sheet.createRow(rowIndex);
		}

		Cell cell = row == null ? null : row.getCell(columnIndex);
		if (cell == null) {
			cell = row == null ? null : row.createCell(columnIndex);
		}

		if (row == null || cell == null) {
			throw new IllegalArgumentException("Cell with col=" + columnIndex + " and row=" + rowIndex + " is null.");
		}

		if (value == null) {
			cell.setCellValue("");
		} else {
			cell.setCellValue(value);
		}
	}

	public static void setCellValue(Sheet sheet, int columnIndex, int rowIndex, Double value) {
		Row row = sheet.getRow(rowIndex);
		if (row == null) {
			row = sheet.createRow(rowIndex);
		}

		Cell cell = row == null ? null : row.getCell(columnIndex);
		if (cell == null) {
			cell = row == null ? null : row.createCell(columnIndex);
		}

		if (row == null || cell == null) {
			throw new IllegalArgumentException("Cell with col=" + columnIndex + " and row=" + rowIndex + " is null.");
		}

		if (value == null) {
			cell.setCellValue("");
		} else {
			cell.setCellValue(value);
		}
	}

	public static void setCellValue(Sheet sheet, int columnIndex, int rowIndex, Date value) {
		Row row = sheet.getRow(rowIndex);
		if (row == null) {
			row = sheet.createRow(rowIndex);
		}

		Cell cell = row == null ? null : row.getCell(columnIndex);
		if (cell == null) {
			cell = row == null ? null : row.createCell(columnIndex);
		}

		if (row == null || cell == null) {
			throw new IllegalArgumentException("Cell with col=" + columnIndex + " and row=" + rowIndex + " is null.");
		}

		if (value == null) {
			cell.setCellValue("");
		} else {
			cell.setCellValue(value);
		}
	}

	public static void setCellValue(Sheet sheet, int columnIndex, int rowIndex, Object value) {
		Row row = sheet.getRow(rowIndex);
		if (row == null) {
			row = sheet.createRow(rowIndex);
		}

		Cell cell = row == null ? null : row.getCell(columnIndex);
		if (cell == null) {
			cell = row == null ? null : row.createCell(columnIndex);
		}

		if (row == null || cell == null) {
			throw new IllegalArgumentException("Cell with col=" + columnIndex + " and row=" + rowIndex + " is null.");
		}

		if (value == null) {
			cell.setCellValue("");
		} else {
			cell.setCellValue(value.toString());
		}
	}

	public static void setCellAlignment(Sheet sheet, int rowIndex, String columnName, short alignment) {
		PoiUtil.setCellAlignment(sheet, rowIndex, PoiUtil.getColumnIndex(columnName), alignment);
	}

	public static void setCellAlignment(Sheet sheet, int rowIndex, int columnIndex, short alignment) {
		Cell cell = PoiUtil.getCell(sheet, columnIndex, rowIndex);

		if (cell == null) {
			throw new IllegalArgumentException("Cell with col=" + columnIndex + " and row=" + rowIndex + " is null.");
		}

		CellUtil.setAlignment(cell, sheet.getWorkbook(), alignment);
	}

	// other convenience methods
	public static Cell getCell(Sheet sheet, String cellName) {
		Point cellIndex = PoiUtil.getCellIndex(cellName);
		return PoiUtil.getCell(sheet, cellIndex.x, cellIndex.y);
	}

	public static Cell getCell(Sheet sheet, String columnName, int rowIndex) {
		return PoiUtil.getCell(sheet, PoiUtil.getColumnIndex(columnName), rowIndex);
	}

	public static Cell getCell(Sheet sheet, int columnIndex, int rowIndex) {
		Row row = sheet.getRow(rowIndex);
		if (row == null) {
			row = sheet.createRow(rowIndex);
		}

		Cell cell = row == null ? null : row.getCell(columnIndex);
		if (cell == null) {
			cell = row == null ? null : row.createCell(columnIndex);
		}

		return cell;
	}

	public static int getColumnIndex(String columnName) {
		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

		int columnIndex = 0;
		int charPosition = 0;
		for (int index = columnName.length() - 1; index >= 0; index--) {
			char ch = columnName.charAt(index);
			int charIndex = chars.indexOf(ch);
			if (charIndex == -1) {
				throw new IllegalArgumentException("Invalid character in column name: " + ch);
			}

			columnIndex += (charIndex + 1) * Math.pow(26, charPosition);
			charPosition++;
		}

		return columnIndex - 1;
	}

	public static Point getCellIndex(String cellName) {
		int indexOfRowNum = -1;
		for (int index = 0; index < cellName.length(); index++) {
			char ch = cellName.charAt(index);
			if (Character.isDigit(ch)) {
				indexOfRowNum = index;
				break;
			}
		}

		String columnName = cellName.substring(0, indexOfRowNum);
		String rowStr = cellName.substring(indexOfRowNum);

		int row = StringUtil.parseInt(rowStr, 1) - 1;
		int col = PoiUtil.getColumnIndex(columnName);

		return new Point(row, col);
	}

	/**
	 * Creates a cell and aligns it a certain way.
	 * <p/>
	 * based from: http://poi.apache.org/spreadsheet/quick-guide.html#Alignment
	 *
	 * @param cellStyle cell style object to use
	 * @param row the row to create the cell in
	 * @param column the column number to create the cell in
	 * @param halign the horizontal alignment for the cell.
	 */
	public static Cell createCell(CellStyle cellStyle, Row row, short column, short halign, short valign) {
		Cell cell = row.createCell(column);
		cellStyle.setAlignment(halign);
		cellStyle.setVerticalAlignment(valign);
		cell.setCellStyle(cellStyle);
		return cell;
	}

	/**
	 ******* COLUMN *********
	 */

	/**
	 * @return true if all cells in a column is empty or null false if one or more cells in the column is empty or null.
	 */
	public static Boolean columnIsEmpty(Sheet sheet, int columnIndex) {
		Boolean b = true;
		int index = 0;
		try {
			Row row = sheet.getRow(index);
			while (row != null) {
				if (PoiUtil.getCellValue(row.getCell(columnIndex)) != null
						&& !"".equalsIgnoreCase(PoiUtil.getCellValue(row.getCell(columnIndex)).toString())) {
					b = false;
					return false;
				}
				index++;
				row = sheet.getRow(index);

			}
		} catch (Exception e) {
			PoiUtil.LOG.error(e.getMessage(), e);
		}
		return b;
	}

	/**
	 * @return false if all cells in a column is not empty or null true if one or more cells in the column is empty or null.
	 */
	public static Boolean columnHasEmpty(Sheet sheet, int columnIndex) {
		Boolean b = false;
		int index = 0;
		try {
			Row row = sheet.getRow(index);
			while (row != null) {
				if (PoiUtil.getCellValue(row.getCell(columnIndex)) == null
						|| "".equalsIgnoreCase(PoiUtil.getCellValue(row.getCell(columnIndex)).toString())) {
					b = true;
					return true;
				}
				index++;
				row = sheet.getRow(index);
			}
		} catch (Exception e) {
			PoiUtil.LOG.error(e.getMessage(), e);
		}
		return b;
	}

	public static Boolean isEmpty(Sheet sheet, int rowIndex, int columnIndex) {
		Row row = sheet.getRow(rowIndex);

		return row == null || PoiUtil.getCellValue(row.getCell(columnIndex)) == null
				|| "".equalsIgnoreCase(PoiUtil.getCellValue(row.getCell(columnIndex)).toString());
	}

	/**
	 * @return the content of the column into an array
	 */
	public static String[] asStringArrayColumn(Sheet sheet, int columnIndex) {

		List<String> contents = new ArrayList<String>();
		int index = 0;
		Row row = sheet.getRow(index);
		String cellvalue = "";
		try {
			while (row != null) {
				cellvalue = "";
				try {
					cellvalue = PoiUtil.getCellValue(row.getCell(columnIndex)).toString();
					if (cellvalue == null) {
						cellvalue = "";
					}
				} catch (Exception e) {
					PoiUtil.LOG.error(e.getMessage(), e);
				}
				contents.add(cellvalue);
				index++;
				row = sheet.getRow(index);
			}
		} catch (Exception e) {
			PoiUtil.LOG.error(e.getMessage(), e);
		}
		return contents.toArray(new String[0]);
	}

	/******* ROW **********/

	/**
	 * Checks whether row is empty or not
	 *
	 * @param sheet
	 * @param rowIndex
	 * @param start
	 * @param end
	 * @return true if the row is empty
	 */
	public static Boolean rowIsEmpty(Sheet sheet, int rowIndex, int start, int end) {

		Row row = sheet.getRow(rowIndex);

		if (row == null) {
			return true;
		}

		for (int cn = start; cn <= end; cn++) {
			Cell c;
			try {
				c = row.getCell(cn, Row.RETURN_BLANK_AS_NULL);
			} catch (Exception e) {
				PoiUtil.LOG.error(e.getMessage(), e);
				c = null;
			}
			if (c != null) {

				Object cellValue = PoiUtil.getCellValue(c);
				if (cellValue != null && !"".equals(String.valueOf(cellValue))) {
					return false;

				}
			}
		}

		return true;
	}

	/**
	 * Checks if the row has empty/blank values
	 *
	 * @param sheet
	 * @param rowIndex
	 * @param start
	 * @param end
	 * @return true if the row has empty values
	 */
	public static Boolean rowHasEmpty(Sheet sheet, int rowIndex, int start, int end) {
		Row row = sheet.getRow(rowIndex);
		Boolean hasEmpty = false;
		if (!PoiUtil.rowIsEmpty(sheet, rowIndex, start, end)) {

			for (int cn = start; cn <= end; cn++) {
				Cell c;
				try {
					c = row.getCell(cn, Row.RETURN_BLANK_AS_NULL);
				} catch (Exception e) {
					PoiUtil.LOG.error(e.getMessage(), e);
					c = null;
				}
				if (c == null) {
					hasEmpty = true;
				} else {
					Object cellValue = PoiUtil.getCellValue(c);
					if (cellValue == null || "".equals(String.valueOf(cellValue))) {
						hasEmpty = true;
					}
				}
			}
			return hasEmpty;
		} else {
			return true;
		}

	}

	/**
	 * Returns the content of the row into an array using start and end values defined in the row
	 *
	 * @param sheet
	 * @param rowIndex
	 * @return The String array representation of the row
	 */

	public static String[] rowAsStringArray(Sheet sheet, int rowIndex) {
		Row row = sheet.getRow(rowIndex);

		int start = row.getFirstCellNum();
		int end = row.getLastCellNum() - 1;

		return PoiUtil.rowAsStringArray(sheet, rowIndex, start, end);
	}

	/**
	 * Returns the content of the row into an array
	 *
	 * @param sheet
	 * @param rowIndex
	 * @param start
	 * @param end
	 * @return The String array representation of the row
	 */
	public static String[] rowAsStringArray(Sheet sheet, int rowIndex, int start, int end) {
		return PoiUtil.rowAsStringArray(sheet, rowIndex, start, end, Integer.MAX_VALUE);

	}

	/**
	 * Returns the content of the row into an array
	 *
	 * @param sheet
	 * @param rowIndex
	 * @param start
	 * @param end
	 * @param max
	 * @return The String array representation of the row
	 */
	public static String[] rowAsStringArray(Sheet sheet, int rowIndex, int start, int end, int max) {
		Row row = sheet.getRow(rowIndex);
		List<String> values = new ArrayList<String>();

		if (!PoiUtil.rowIsEmpty(sheet, rowIndex, start, end)) {

			for (int cn = start; cn <= end && cn < max; cn++) {
				try {
					Cell cell = row.getCell(cn, Row.RETURN_BLANK_AS_NULL);
					if (cell != null) {
						// assures that the row we'll be getting is a string
						cell.setCellType(Cell.CELL_TYPE_STRING);
						values.add(cell.getStringCellValue());

					}
				} catch (Exception e) {
					PoiUtil.LOG.error(e.getMessage(), e);
					values.add("");
				}

			}
		}
		return values.toArray(new String[0]);

	}

	/**
	 * Returns the content of the row into a delimited string
	 *
	 * @param sheet
	 * @param rowIndex
	 * @param start
	 * @param end
	 * @return The delimited-String representation of the row
	 */
	public static String rowAsString(Sheet sheet, int rowIndex, int start, int end, String delimiter) {

		return PoiUtil.rowAsString(sheet, rowIndex, start, end, delimiter, Integer.MAX_VALUE);

	}

	/**
	 * Returns the content of the row into a delimited string
	 *
	 * @param sheet
	 * @param rowIndex
	 * @param start
	 * @param end
	 * @param max
	 * @return The delimited-String representation of the row
	 */
	public static String rowAsString(Sheet sheet, int rowIndex, int start, int end, String delimiter, int max) {

		return StringUtils.join(PoiUtil.rowAsStringArray(sheet, rowIndex, start, end, max), delimiter);

	}

	public static String rowAsString(Sheet sheet, int rowIndex, String delimiter) {
		Row row = sheet.getRow(rowIndex);
		if (row == null) {
			return "";
		}
		int startCell = row.getFirstCellNum();
		int endCell = row.getLastCellNum() - 1;

		return PoiUtil.rowAsString(sheet, rowIndex, startCell, endCell, delimiter);
	}

	public static String rowAsString(Sheet sheet, int rowIndex, String delimiter, int maxStringLength) {
		String resultString = PoiUtil.rowAsString(sheet, rowIndex, delimiter);
		if (maxStringLength < resultString.length()) {
			return resultString.substring(0, maxStringLength);
		} else {
			return resultString;
		}
	}

	public static Integer getLastRowNum(Sheet sheet) {
		Integer lastRowNum = sheet.getLastRowNum() + 1;

		if (lastRowNum == 1) {
			return 0;
		}

		Row row = null;
		int start = 0;
		int end = 0;

		do {
			lastRowNum--;

			row = sheet.getRow(lastRowNum);
			if (row == null) {
				continue;
			}
			start = row.getFirstCellNum();
			end = row.getLastCellNum() - 1;

		} while (PoiUtil.rowIsEmpty(sheet, lastRowNum, start, end) && lastRowNum > 0);

		return lastRowNum;
	}

	public static boolean areSheetRowsOverMaxLimit(String fileName, int sheetIndex, int maxLimit) {

		try {
			new PoiEventUserModel().areSheetRowsOverMaxLimit(fileName, sheetIndex, maxLimit);
		} catch (Exception e) {
			PoiUtil.LOG.error(e.getMessage(), e);
			// Exception means parser has exeeded the set max limit
			return true;
		}

		return false;
	}

	public static boolean isAnySheetRowsOverMaxLimit(String fileName, int maxLimit) {

		try {
			new PoiEventUserModel().isAnySheetRowsOverMaxLimit(fileName, maxLimit);
		} catch (Exception e) {
			PoiUtil.LOG.error(e.getMessage(), e);
			// Exception means parser has exeeded the set max limit
			return true;
		}

		return false;
	}
}
