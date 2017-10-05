/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.middleware.util;

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

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
	public static void setRepeatingRows(final Workbook workBook, final int sheetIndex, final int fromRow, final int toRow) {
		workBook.setRepeatingRowsAndColumns(sheetIndex, -1, -1, fromRow, toRow);
	}

	public static void setRepeatingColumns(final Workbook workBook, final int sheetIndex, final int fromCol, final int toCol) {
		workBook.setRepeatingRowsAndColumns(sheetIndex, fromCol, toCol, -1, -1);
	}

	public static void setRepeatingRowsAndColumns(final Workbook workBook, final int sheetIndex, final int fromCol, final int toCol,
			final int fromRow, final int toRow) {
		workBook.setRepeatingRowsAndColumns(sheetIndex, fromCol, toCol, fromRow, toRow);
	}

	public static Double getCellNumericValue(final Cell cell) {
		return cell == null ? null : cell.getNumericCellValue();
	}

	public static String getCellStringValue(final Workbook wb, final Integer sheetNumber, final Integer rowNumber,
			final Integer columnNumber) {

		final Sheet sheet = wb.getSheetAt(sheetNumber);
		final Row row = sheet.getRow(rowNumber);

		if (null == row) {
			return null;
		}

		final Cell cell = row.getCell(columnNumber);
		return PoiUtil.getCellStringValue(cell);

	}

	public static Double getCellNumericValue(final Workbook wb, final Integer sheetNumber, final Integer rowNumber,
			final Integer columnNumber) {

		final Sheet sheet = wb.getSheetAt(sheetNumber);
		final Row row = sheet.getRow(rowNumber);

		if (null == row) {
			return null;
		}

		final Cell cell = row.getCell(columnNumber);
		return PoiUtil.getCellNumericValue(cell);

	}

	public static String getCellStringValue(final Cell cell) {
		final Object out = PoiUtil.getCellValue(cell);

		if (out != null) {
			return out.toString().trim();
		}

		return null;
	}

	public static Object getCellValue(final Cell cell) {
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

	private static Object getFormulaValue(final Cell cell) {
		switch (cell.getCachedFormulaResultType()) {
			case Cell.CELL_TYPE_NUMERIC:
				return cell.getNumericCellValue();
			case Cell.CELL_TYPE_STRING:
				return cell.getRichStringCellValue();
			default:
				return cell.getCellFormula();
		}
	}

	private static Object getNumericValue(final Cell cell) {
		if (DateUtil.isCellDateFormatted(cell)) {
			final Date date = cell.getDateCellValue();
			return PoiUtil.EXCEL_DATE_FORMATTER.format(date);
		}

		final double doubleVal = cell.getNumericCellValue();
		if (doubleVal % 1 == 0) {
			return (int) doubleVal;
		} else {
			return doubleVal;
		}
	}

	public static void setCellValue(final Sheet sheet, final String cellName, final String value) {
		final Point cellIndex = PoiUtil.getCellIndex(cellName);
		PoiUtil.setCellValue(sheet, cellIndex.y, cellIndex.x, value);
	}

	public static void setCellValue(final Sheet sheet, final String cellName, final Integer value) {
		final Point cellIndex = PoiUtil.getCellIndex(cellName);
		PoiUtil.setCellValue(sheet, cellIndex.y, cellIndex.x, value);
	}

	public static void setCellValue(final Sheet sheet, final String cellName, final Long value) {
		final Point cellIndex = PoiUtil.getCellIndex(cellName);
		PoiUtil.setCellValue(sheet, cellIndex.y, cellIndex.x, value);
	}

	public static void setCellValue(final Sheet sheet, final String cellName, final Double value) {
		final Point cellIndex = PoiUtil.getCellIndex(cellName);
		PoiUtil.setCellValue(sheet, cellIndex.y, cellIndex.x, value);
	}

	public static void setCellValue(final Sheet sheet, final String cellName, final Date value) {
		final Point cellIndex = PoiUtil.getCellIndex(cellName);
		PoiUtil.setCellValue(sheet, cellIndex.y, cellIndex.x, value);
	}

	public static void setCellValue(final Sheet sheet, final String cellName, final Object value) {
		final Point cellIndex = PoiUtil.getCellIndex(cellName);
		PoiUtil.setCellValue(sheet, cellIndex.y, cellIndex.x, value);
	}

	// setCellValue with String column name and integer row index as parameter
	public static void setCellValue(final Sheet sheet, final String colName, final int rowIndex, final String value) {
		final int columnIndex = PoiUtil.getColumnIndex(colName);
		PoiUtil.setCellValue(sheet, columnIndex, rowIndex, value);
	}

	public static void setCellValue(final Sheet sheet, final String colName, final int rowIndex, final Integer value) {
		final int columnIndex = PoiUtil.getColumnIndex(colName);
		PoiUtil.setCellValue(sheet, columnIndex, rowIndex, value);
	}

	public static void setCellValue(final Sheet sheet, final String colName, final int rowIndex, final Long value) {
		final int columnIndex = PoiUtil.getColumnIndex(colName);
		PoiUtil.setCellValue(sheet, columnIndex, rowIndex, value);
	}

	public static void setCellValue(final Sheet sheet, final String colName, final int rowIndex, final Double value) {
		final int columnIndex = PoiUtil.getColumnIndex(colName);
		PoiUtil.setCellValue(sheet, columnIndex, rowIndex, value);
	}

	public static void setCellValue(final Sheet sheet, final String colName, final int rowIndex, final Date value) {
		final int columnIndex = PoiUtil.getColumnIndex(colName);
		PoiUtil.setCellValue(sheet, columnIndex, rowIndex, value);
	}

	public static void setCellValue(final Sheet sheet, final String colName, final int rowIndex, final Object value) {
		final int columnIndex = PoiUtil.getColumnIndex(colName);
		PoiUtil.setCellValue(sheet, columnIndex, rowIndex, value);
	}

	// setCellValue with integer indices as parameter

	public static void setCellValue(final Sheet sheet, final int columnIndex, final int rowIndex, final String value) {
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

	public static void setCellValue(final Sheet sheet, final int columnIndex, final int rowIndex, final Integer value) {
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

	public static void setCellValue(final Sheet sheet, final int columnIndex, final int rowIndex, final Long value) {
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

	public static void setCellValue(final Sheet sheet, final int columnIndex, final int rowIndex, final Double value) {
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

	public static void setCellValue(final Sheet sheet, final int columnIndex, final int rowIndex, final Date value) {
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

	public static void setCellValue(final Sheet sheet, final int columnIndex, final int rowIndex, final Object value) {
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

	public static void setCellAlignment(final Sheet sheet, final int rowIndex, final String columnName, final short alignment) {
		PoiUtil.setCellAlignment(sheet, rowIndex, PoiUtil.getColumnIndex(columnName), alignment);
	}

	public static void setCellAlignment(final Sheet sheet, final int rowIndex, final int columnIndex, final short alignment) {
		final Cell cell = PoiUtil.getCell(sheet, columnIndex, rowIndex);

		if (cell == null) {
			throw new IllegalArgumentException("Cell with col=" + columnIndex + " and row=" + rowIndex + " is null.");
		}

		CellUtil.setAlignment(cell, sheet.getWorkbook(), alignment);
	}

	// other convenience methods
	public static Cell getCell(final Sheet sheet, final String cellName) {
		final Point cellIndex = PoiUtil.getCellIndex(cellName);
		return PoiUtil.getCell(sheet, cellIndex.x, cellIndex.y);
	}

	public static Cell getCell(final Sheet sheet, final String columnName, final int rowIndex) {
		return PoiUtil.getCell(sheet, PoiUtil.getColumnIndex(columnName), rowIndex);
	}

	public static Cell getCell(final Sheet sheet, final int columnIndex, final int rowIndex) {
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

	public static int getColumnIndex(final String columnName) {
		final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

		int columnIndex = 0;
		int charPosition = 0;
		for (int index = columnName.length() - 1; index >= 0; index--) {
			final char ch = columnName.charAt(index);
			final int charIndex = chars.indexOf(ch);
			if (charIndex == -1) {
				throw new IllegalArgumentException("Invalid character in column name: " + ch);
			}

			columnIndex += (charIndex + 1) * Math.pow(26, charPosition);
			charPosition++;
		}

		return columnIndex - 1;
	}

	public static Point getCellIndex(final String cellName) {
		int indexOfRowNum = -1;
		for (int index = 0; index < cellName.length(); index++) {
			final char ch = cellName.charAt(index);
			if (Character.isDigit(ch)) {
				indexOfRowNum = index;
				break;
			}
		}

		final String columnName = cellName.substring(0, indexOfRowNum);
		final String rowStr = cellName.substring(indexOfRowNum);

		final int row = StringUtil.parseInt(rowStr, 1) - 1;
		final int col = PoiUtil.getColumnIndex(columnName);

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
	public static Cell createCell(final CellStyle cellStyle, final Row row, final short column, final short halign, final short valign) {
		final Cell cell = row.createCell(column);
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
	public static Boolean columnIsEmpty(final Sheet sheet, final int columnIndex) {
		Boolean b = true;
		int index = 0;
		try {
			Row row = sheet.getRow(index);
			while (row != null) {
				if (PoiUtil.getCellValue(row.getCell(columnIndex)) != null && !""
						.equalsIgnoreCase(PoiUtil.getCellValue(row.getCell(columnIndex)).toString())) {
					b = false;
					return false;
				}
				index++;
				row = sheet.getRow(index);

			}
		} catch (final Exception e) {
			PoiUtil.LOG.error(e.getMessage(), e);
		}
		return b;
	}

	/**
	 * @return false if all cells in a column is not empty or null true if one or more cells in the column is empty or null.
	 */
	public static Boolean columnHasEmpty(final Sheet sheet, final int columnIndex) {
		int index = 0;
		try {
			Row row = sheet.getRow(index);
			if (row == null) {
				return true;
			}
			final int lastRowNo = sheet.getLastRowNum();
			while (index <= lastRowNo) {
				if (row == null) {
					return true;
				}
				if (PoiUtil.getCellValue(row.getCell(columnIndex)) == null || ""
						.equalsIgnoreCase(PoiUtil.getCellValue(row.getCell(columnIndex)).toString())) {
					return true;
				}
				index++;
				row = sheet.getRow(index);
			}
		} catch (final Exception e) {
			PoiUtil.LOG.error(e.getMessage(), e);
		}
		return false;
	}

	public static Boolean isEmpty(final Sheet sheet, final int rowIndex, final int columnIndex) {
		final Row row = sheet.getRow(rowIndex);

		return row == null || PoiUtil.getCellValue(row.getCell(columnIndex)) == null || ""
				.equalsIgnoreCase(PoiUtil.getCellValue(row.getCell(columnIndex)).toString());
	}

	/**
	 * @return the content of the column into an array
	 */
	public static String[] asStringArrayColumn(final Sheet sheet, final int columnIndex) {

		final List<String> contents = new ArrayList<String>();
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
				} catch (final Exception e) {
					PoiUtil.LOG.error(e.getMessage(), e);
				}
				contents.add(cellvalue);
				index++;
				row = sheet.getRow(index);
			}
		} catch (final Exception e) {
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
	public static Boolean rowIsEmpty(final Sheet sheet, final int rowIndex, final int start, final int end) {

		final Row row = sheet.getRow(rowIndex);

		if (row == null) {
			return true;
		}

		for (int cn = start; cn <= end; cn++) {
			Cell c;
			try {
				c = row.getCell(cn, Row.RETURN_BLANK_AS_NULL);
			} catch (final Exception e) {
				PoiUtil.LOG.error(e.getMessage(), e);
				c = null;
			}
			if (c != null) {

				final Object cellValue = PoiUtil.getCellValue(c);
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
	public static Boolean rowHasEmpty(final Sheet sheet, final int rowIndex, final int start, final int end) {
		final Row row = sheet.getRow(rowIndex);
		Boolean hasEmpty = false;
		if (!PoiUtil.rowIsEmpty(sheet, rowIndex, start, end)) {

			for (int cn = start; cn <= end; cn++) {
				Cell c;
				try {
					c = row.getCell(cn, Row.RETURN_BLANK_AS_NULL);
				} catch (final Exception e) {
					PoiUtil.LOG.error(e.getMessage(), e);
					c = null;
				}
				if (c == null) {
					hasEmpty = true;
				} else {
					final Object cellValue = PoiUtil.getCellValue(c);
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

	public static String[] rowAsStringArray(final Sheet sheet, final int rowIndex) {
		final Row row = sheet.getRow(rowIndex);

		final int start = row.getFirstCellNum();
		final int end = row.getLastCellNum() - 1;

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
	public static String[] rowAsStringArray(final Sheet sheet, final int rowIndex, final int start, final int end) {
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
	public static String[] rowAsStringArray(final Sheet sheet, final int rowIndex, final int start, final int end, final int max) {
		final Row row = sheet.getRow(rowIndex);
		final List<String> values = new ArrayList<String>();

		if (!PoiUtil.rowIsEmpty(sheet, rowIndex, start, end)) {

			for (int cn = start; cn <= end && cn < max; cn++) {
				try {
					final Cell cell = row.getCell(cn, Row.RETURN_BLANK_AS_NULL);
					if (cell != null) {
						// assures that the row we'll be getting is a string
						cell.setCellType(Cell.CELL_TYPE_STRING);
						values.add(cell.getStringCellValue());

					}
				} catch (final Exception e) {
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
	public static String rowAsString(final Sheet sheet, final int rowIndex, final int start, final int end, final String delimiter) {

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
	public static String rowAsString(final Sheet sheet, final int rowIndex, final int start, final int end, final String delimiter,
			final int max) {

		return StringUtils.join(PoiUtil.rowAsStringArray(sheet, rowIndex, start, end, max), delimiter);

	}

	public static String rowAsString(final Sheet sheet, final int rowIndex, final String delimiter) {
		final Row row = sheet.getRow(rowIndex);
		if (row == null) {
			return "";
		}
		final int startCell = row.getFirstCellNum();
		final int endCell = row.getLastCellNum() - 1;

		return PoiUtil.rowAsString(sheet, rowIndex, startCell, endCell, delimiter);
	}

	public static String rowAsString(final Sheet sheet, final int rowIndex, final String delimiter, final int maxStringLength) {
		final String resultString = PoiUtil.rowAsString(sheet, rowIndex, delimiter);
		if (maxStringLength < resultString.length()) {
			return resultString.substring(0, maxStringLength);
		} else {
			return resultString;
		}
	}

	/**
	 * Gets the 1-based index of the last cell contained in this row or -1 if the row does not contain any cells.
	 *
	 * @param sheet
	 * @param rowNo
	 * @return
	 */
	public static int getLastCellNum(final Sheet sheet, final int rowNo) {
		if (sheet == null || sheet.getRow(rowNo) == null) {
			return -1;
		}

		short index = sheet.getRow(rowNo).getLastCellNum();

		while (index > 0) {
			final Cell cell = sheet.getRow(rowNo).getCell(index - 1);
			final Object cellValue = getCellValue(cell);
			if (cell == null || cellValue == null) {
				index--;
				continue;
			}
			if (!StringUtils.isBlank(String.valueOf(cellValue))) {
				break;
			}
			index--;
		}

		// index is 1-based
		if (index == 0) {
			return -1;
		}

		return index;
	}

	public static Integer getLastRowNum(final Sheet sheet) {
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

	public static boolean areSheetRowsOverMaxLimit(final String fileName, final int sheetIndex, final int maxLimit) {

		try {
			new PoiEventUserModel().areSheetRowsOverMaxLimit(fileName, sheetIndex, maxLimit);
		} catch (final Exception e) {
			PoiUtil.LOG.error(e.getMessage(), e);
			// Exception means parser has exeeded the set max limit
			return true;
		}

		return false;
	}

	public static boolean isAnySheetRowsOverMaxLimit(final String fileName, final int maxLimit) {

		try {
			new PoiEventUserModel().isAnySheetRowsOverMaxLimit(fileName, maxLimit);
		} catch (final Exception e) {
			PoiUtil.LOG.error(e.getMessage(), e);
			// Exception means parser has exeeded the set max limit
			return true;
		}

		return false;
	}

	/**
	 * Given a sheet, this method deletes a column from a sheet and moves
	 * all the columns to the right of it to the left one cell.
	 *
	 * Note, this method will not update any formula references.
	 *
	 * @param sheet
	 * @param column
	 */
	public static void deleteColumn(final Sheet sheet, final int columnToDelete) {
		int maxColumn = 0;
		for (int r = 0; r < sheet.getLastRowNum() + 1; r++) {
			final Row row = sheet.getRow(r);

			// if no row exists here; then nothing to do; next!
			if (row == null)
				continue;

			// if the row doesn't have this many columns then we are good; next!
			final int lastColumn = row.getLastCellNum();
			if (lastColumn > maxColumn)
				maxColumn = lastColumn;

			if (lastColumn < columnToDelete)
				continue;

			for (int x = columnToDelete + 1; x < lastColumn + 1; x++) {
				final Cell oldCell = row.getCell(x - 1);
				if (oldCell != null)
					row.removeCell(oldCell);

				final Cell nextCell = row.getCell(x);
				if (nextCell != null) {
					final Cell newCell = row.createCell(x - 1, nextCell.getCellType());
					cloneCell(newCell, nextCell);
				}
			}
		}

	}

	/*
	 * Takes an existing Cell and merges all the styles and forumla
	 * into the new one
	 */
	private static void cloneCell(final Cell cNew, final Cell cOld) {
		cNew.setCellComment(cOld.getCellComment());
		cNew.setCellStyle(cOld.getCellStyle());

		switch (cNew.getCellType()) {
			case Cell.CELL_TYPE_BOOLEAN: {
				cNew.setCellValue(cOld.getBooleanCellValue());
				break;
			}
			case Cell.CELL_TYPE_NUMERIC: {
				cNew.setCellValue(cOld.getNumericCellValue());
				break;
			}
			case Cell.CELL_TYPE_STRING: {
				cNew.setCellValue(cOld.getStringCellValue());
				break;
			}
			case Cell.CELL_TYPE_ERROR: {
				cNew.setCellValue(cOld.getErrorCellValue());
				break;
			}
			case Cell.CELL_TYPE_FORMULA: {
				cNew.setCellFormula(cOld.getCellFormula());
				break;
			}
		}

	}

}
