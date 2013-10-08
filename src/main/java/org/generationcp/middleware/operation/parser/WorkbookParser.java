/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/
package org.generationcp.middleware.operation.parser;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.util.Message;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class WorkbookParser {

    public static final int DESCRIPTION_SHEET = 0;
    public static final int OBSERVATION_SHEET = 1;

    public static final int STUDY_NAME_ROW_INDEX = 0;
    public static final int STUDY_TITLE_ROW_INDEX = 1;
    public static final int PMKEY_ROW_INDEX = 2;
    public static final int OBJECTIVE_ROW_INDEX = 3;
    public static final int START_DATE_ROW_INDEX = 4;
    public static final int END_DATE_ROW_INDEX = 5;
    public static final int STUDY_TYPE_ROW_INDEX = 6;

    public static final int STUDY_DETAILS_VALUE_COLUMN_INDEX = 1;

    private int currentRow;
    private long locationId;
    private List<Message> errorMessages;

    /*private static Integer currentSheet;
	private static Integer currentRow;
	private static long locationId;
*/

    /**
     * Parses given file and transforms it into a Workbook
     *
     * @param file
     * @return workbook
     * @throws WorkbookParserException
     */
    public org.generationcp.middleware.domain.etl.Workbook parseFile(File file) throws WorkbookParserException {

        org.generationcp.middleware.domain.etl.Workbook workbook = new org.generationcp.middleware.domain.etl.Workbook();
        Workbook wb;

        currentRow = 0;
        locationId = 0;
        errorMessages = new LinkedList<Message>();

        try {

            InputStream inp = new FileInputStream(file);
            wb = new HSSFWorkbook(inp);

            //validations
            try {
                Sheet sheet1 = wb.getSheetAt(DESCRIPTION_SHEET);

                if (sheet1 == null || sheet1.getSheetName() == null || !(sheet1.getSheetName().equals("Description"))) {
                    errorMessages.add(new Message("missing.sheet.description"));
                    /*throw new Error("Error with reading file uploaded. File doesn't have the first sheet - Description");*/
                }
            } catch (Exception e) {
                throw new WorkbookParserException("Error encountered with parseFile(): " + e.getMessage(), e);
            }

            try {
                Sheet sheet2 = wb.getSheetAt(OBSERVATION_SHEET);

                if (sheet2 == null || sheet2.getSheetName() == null || !(sheet2.getSheetName().equals("Observation"))) {
                    errorMessages.add(new Message("missing.sheet.observation"));
                    /*throw new Error("Error with reading file uploaded. File doesn't have the second sheet - Observation");*/
                }
            } catch (Exception e) {
                throw new WorkbookParserException("Error encountered with parseFile(): " + e.getMessage(), e);
            }

            // throw an exception here if
            if (errorMessages.size() > 0) {
                throw new WorkbookParserException(errorMessages);
            }

            workbook.setStudyDetails(readStudyDetails(wb));
            workbook.setConditions(readMeasurementVariables(wb, "CONDITION"));
            workbook.setFactors(readMeasurementVariables(wb, "FACTOR"));
            workbook.setConstants(readMeasurementVariables(wb, "CONSTANT"));
            workbook.setVariates(readMeasurementVariables(wb, "VARIATE"));

            if (errorMessages.size() > 0) {
                throw new WorkbookParserException(errorMessages);
            }

            currentRow = 0;

            workbook.setObservations(readObservations(wb, workbook));

        } catch (FileNotFoundException e) {
            throw new WorkbookParserException("File not found " + e.getMessage(), e);
        } catch (IOException e) {
            throw new WorkbookParserException("Error accessing file " + e.getMessage(), e);
        }

        return workbook;
    }

    private StudyDetails readStudyDetails(Workbook wb) throws WorkbookParserException {

        //get study details
        String study = getCellStringValue(wb, DESCRIPTION_SHEET, STUDY_NAME_ROW_INDEX, STUDY_DETAILS_VALUE_COLUMN_INDEX);
        String title = getCellStringValue(wb, DESCRIPTION_SHEET, STUDY_TITLE_ROW_INDEX, STUDY_DETAILS_VALUE_COLUMN_INDEX);
        String pmKey = getCellStringValue(wb, DESCRIPTION_SHEET, PMKEY_ROW_INDEX, STUDY_DETAILS_VALUE_COLUMN_INDEX);
        String objective = getCellStringValue(wb, DESCRIPTION_SHEET, OBJECTIVE_ROW_INDEX, STUDY_DETAILS_VALUE_COLUMN_INDEX);
        String startDate = getCellStringValue(wb, DESCRIPTION_SHEET, START_DATE_ROW_INDEX, STUDY_DETAILS_VALUE_COLUMN_INDEX);
        String endDate = getCellStringValue(wb, DESCRIPTION_SHEET, END_DATE_ROW_INDEX, STUDY_DETAILS_VALUE_COLUMN_INDEX);

        //determine study type
        String studyType = getCellStringValue(wb, DESCRIPTION_SHEET, STUDY_TYPE_ROW_INDEX, STUDY_DETAILS_VALUE_COLUMN_INDEX);
        StudyType studyTypeValue = StudyType.getStudyType(studyType);
        
        if (study != null){
        	if (study.trim().equals("")) errorMessages.add(new Message("error.blank.study.name"));
        }
        if (title != null) {
        	if (title.trim().equals("")) errorMessages.add(new Message("error.blank.study.title"));
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        
        if (startDate.length() > 8) errorMessages.add(new Message("error.start.date.invalid"));
        try {
			if (!startDate.equals("")) dateFormat.parse(startDate);
		} catch (ParseException e) {
			errorMessages.add(new Message("start.date.invalid"));
		}
        if (endDate.length() > 8) errorMessages.add(new Message("error.end.date.invalid"));
        try {
        	if (!endDate.equals("")) dateFormat.parse(endDate);
		} catch (ParseException e) {
			errorMessages.add(new Message("error.end.date.invalid"));
		}
        
        if (studyTypeValue == null) {
            studyTypeValue = StudyType.E;
        }

        StudyDetails studyDetails = new StudyDetails(study, title, pmKey, objective, startDate, endDate, studyTypeValue, 0, null, null);
        
        /* for debugging purposes
        System.out.println("DEBUG | Study:" + study);
        System.out.println("DEBUG | Title:" + title);
        System.out.println("DEBUG | PMKey:" + pmKey);
        System.out.println("DEBUG | Objective:" + objective);
        System.out.println("DEBUG | Start Date:" + startDate.toString());
        System.out.println("DEBUG | End Date:" + endDate.toString());
        System.out.println("DEBUG | Study Type:" + studyType);
        */

        while (!rowIsEmpty(wb, DESCRIPTION_SHEET, currentRow, 8)) {
            currentRow++;
        }
        return studyDetails;
    }

    private List<MeasurementVariable> readMeasurementVariables(Workbook wb, String name) throws WorkbookParserException {
        List<MeasurementVariable> measurementVariables = new ArrayList<MeasurementVariable>();

        try {

            currentRow++; //Skip empty row
            //Check if headers are correct
            if (!getCellStringValue(wb, DESCRIPTION_SHEET, currentRow, 0).toUpperCase().equals(name)
                    || !getCellStringValue(wb, DESCRIPTION_SHEET, currentRow, 1).toUpperCase().equals("DESCRIPTION")
                    || !getCellStringValue(wb, DESCRIPTION_SHEET, currentRow, 2).toUpperCase().equals("PROPERTY")
                    || !getCellStringValue(wb, DESCRIPTION_SHEET, currentRow, 3).toUpperCase().equals("SCALE")
                    || !getCellStringValue(wb, DESCRIPTION_SHEET, currentRow, 4).toUpperCase().equals("METHOD")
                    || !getCellStringValue(wb, DESCRIPTION_SHEET, currentRow, 5).toUpperCase().equals("DATA TYPE")
                    || !getCellStringValue(wb, DESCRIPTION_SHEET, currentRow, 6).toUpperCase().equals("VALUE")
                    || !getCellStringValue(wb, DESCRIPTION_SHEET, currentRow, 7).toUpperCase().equals("LABEL")) {
	            
	        	/* for debugging purposes
	            System.out.println("DEBUG | Invalid file on readMeasurementVariables");
	            System.out.println(getCellStringValue(wb, currentSheet,currentRow,0).toUpperCase());
	            */

                // TODO change this so that it's in line with exception strategy
                throw new WorkbookParserException("Incorrect headers for " + name);
            }

            //If file is still valid (after checking headers), proceed
            currentRow++;
            while (!rowIsEmpty(wb, DESCRIPTION_SHEET, currentRow, 8)) {

                // GCP-5802
                MeasurementVariable var = new MeasurementVariable(getCellStringValue(wb, DESCRIPTION_SHEET, currentRow, 0)
                        , getCellStringValue(wb, DESCRIPTION_SHEET, currentRow, 1)
                        , getCellStringValue(wb, DESCRIPTION_SHEET, currentRow, 3)
                        , getCellStringValue(wb, DESCRIPTION_SHEET, currentRow, 4)
                        , getCellStringValue(wb, DESCRIPTION_SHEET, currentRow, 2)
                        , getCellStringValue(wb, DESCRIPTION_SHEET, currentRow, 5)
                        , getCellStringValue(wb, DESCRIPTION_SHEET, currentRow, 6)
                        , getCellStringValue(wb, DESCRIPTION_SHEET, currentRow, 7));

                if (StringUtils.isEmpty(var.getName())) {
                    errorMessages.add(new Message("missing.field.name", Integer.toString(currentRow + 1)));
                }

                if (StringUtils.isEmpty(var.getDescription())) {
                    errorMessages.add(new Message("missing.field.description", Integer.toString(currentRow + 1)));
                }

                if (StringUtils.isEmpty(var.getProperty())) {
                    errorMessages.add(new Message("missing.field.property", Integer.toString(currentRow + 1)));
                }

                if (StringUtils.isEmpty(var.getScale())) {
                    errorMessages.add(new Message("missing.field.scale", Integer.toString(currentRow + 1)));
                }

                if (StringUtils.isEmpty(var.getMethod())) {
                    errorMessages.add(new Message("missing.field.method", Integer.toString(currentRow + 1)));
                }

                if (StringUtils.isEmpty(var.getDataType())) {
                    errorMessages.add(new Message("missing.field.datatype", Integer.toString(currentRow + 1)));
                }

                if (StringUtils.isEmpty(var.getLabel())) {
                    errorMessages.add(new Message("missing.field.label", Integer.toString(currentRow + 1)));
                }

                measurementVariables.add(var);

                //set locationId
                if (name.equals("CONDITION") && getCellStringValue(wb, DESCRIPTION_SHEET, currentRow, 0).toUpperCase().equals("TRIAL")) {
                    if (getCellStringValue(wb, DESCRIPTION_SHEET, currentRow, 6) != null && !getCellStringValue(wb, DESCRIPTION_SHEET, currentRow, 6).equals("")) {
                        locationId = Long.parseLong(getCellStringValue(wb, DESCRIPTION_SHEET, currentRow, 6));
                    }
                }

	        	/* for debugging purposes
	            System.out.println("");
	            System.out.println("DEBUG | "+name+":"+getCellStringValue(wb,currentSheet,currentRow,0));
	            System.out.println("DEBUG | Description:"+getCellStringValue(wb,currentSheet,currentRow,1));
	            System.out.println("DEBUG | Property:"+getCellStringValue(wb,currentSheet,currentRow,2));
	            System.out.println("DEBUG | Scale:"+getCellStringValue(wb,currentSheet,currentRow,3));
	            System.out.println("DEBUG | Method:"+getCellStringValue(wb,currentSheet,currentRow,4));
	            System.out.println("DEBUG | Data Type:"+getCellStringValue(wb,currentSheet,currentRow,5));
	            System.out.println("DEBUG | Value:"+getCellStringValue(wb,currentSheet,currentRow,6));
	            System.out.println("DEBUG | Label:"+getCellStringValue(wb,currentSheet,currentRow,7));
				*/
                currentRow++;
            }

            return measurementVariables;
        } catch (Exception e) {
            throw new WorkbookParserException(e.getMessage(), e);
        }
    }

    private List<MeasurementRow> readObservations(Workbook wb, org.generationcp.middleware.domain.etl.Workbook workbook) throws WorkbookParserException {
        List<MeasurementRow> observations = new ArrayList<MeasurementRow>();
        long stockId = 0;

        try {
            //validate headers and set header labels
            List<MeasurementVariable> factors = workbook.getFactors();
            List<MeasurementVariable> variates = workbook.getVariates();
            List<String> measurementDataLabel = new ArrayList<String>();
            for (int col = 0; col < factors.size() + variates.size(); col++) {
                if (col < factors.size()) {

                    if (!factors.get(col).getName().toUpperCase().equals(getCellStringValue(wb, OBSERVATION_SHEET, currentRow, col).toUpperCase())) {
                        // TODO change this so that it's in line with exception strategy
                        throw new WorkbookParserException("Incorrect header for observations.");
                    }

                } else {

                    if (!variates.get(col - factors.size()).getName().toUpperCase().equals(getCellStringValue(wb, OBSERVATION_SHEET, currentRow, col).toUpperCase())) {
                        // TODO change this so that it's in line with exception strategy
                        throw new WorkbookParserException("Incorrect header for observations.");
                    }

                }
                measurementDataLabel.add(getCellStringValue(wb, OBSERVATION_SHEET, currentRow, col));
            }

            currentRow++;

            //add each row in observations
            while (!rowIsEmpty(wb, OBSERVATION_SHEET, currentRow, factors.size() + variates.size())) {
                List<MeasurementData> measurementData = new ArrayList<MeasurementData>();

                for (int col = 0; col < factors.size() + variates.size(); col++) {
                    if (col == 0) {
                        stockId = Long.parseLong(getCellStringValue(wb, OBSERVATION_SHEET, currentRow, col));
                    }

                    // TODO verify usefulness / validity of next statement.
                    if (measurementDataLabel.get(col).equals("GYLD")) {
                        System.out.println(getCellStringValue(wb, OBSERVATION_SHEET, currentRow, col));
                    }
                    measurementData.add(new MeasurementData(measurementDataLabel.get(col), getCellStringValue(wb, OBSERVATION_SHEET, currentRow, col)));
                }

                observations.add(new MeasurementRow(stockId, locationId, measurementData));
                currentRow++;
            }

            return observations;
        } catch (Exception e) {
            throw new WorkbookParserException(e.getMessage(), e);
        }
    }

    private static String getCellStringValue(Workbook wb, Integer sheetNumber, Integer rowNumber, Integer columnNumber) {
        try {
            Sheet sheet = wb.getSheetAt(sheetNumber);
            Row row = sheet.getRow(rowNumber);
            Cell cell = row.getCell(columnNumber);
            return cell.getStringCellValue();
        } catch (IllegalStateException e) {
            Sheet sheet = wb.getSheetAt(sheetNumber);
            Row row = sheet.getRow(rowNumber);
            Cell cell = row.getCell(columnNumber);

            if (cell.getNumericCellValue() == Math.floor(cell.getNumericCellValue())) {
                return String.valueOf(Integer.valueOf((int) cell.getNumericCellValue()));
            } else {
                return String.valueOf(Double.valueOf(cell.getNumericCellValue()));
            }

        } catch (NullPointerException e) {
            return "";
        }
    }


/*
// Refactored logic to StudyType, since it is more appropriate
    private static StudyType getStudyTypeValue(String studyType) {
        if (studyType.toUpperCase().equals(StudyType.N.getName().toUpperCase())) {
            return StudyType.N;
        } else if (studyType.toUpperCase().equals(StudyType.HB.getName().toUpperCase())) {
            return StudyType.HB;
        } else if (studyType.toUpperCase().equals(StudyType.PN.getName().toUpperCase())) {
            return StudyType.PN;
        } else if (studyType.toUpperCase().equals(StudyType.CN.getName().toUpperCase())) {
            return StudyType.CN;
        } else if (studyType.toUpperCase().equals(StudyType.OYT.getName().toUpperCase())) {
            return StudyType.OYT;
        } else if (studyType.toUpperCase().equals(StudyType.BON.getName().toUpperCase())) {
            return StudyType.BON;
        } else if (studyType.toUpperCase().equals(StudyType.T.getName().toUpperCase())) {
            return StudyType.T;
        } else if (studyType.toUpperCase().equals(StudyType.RYT.getName().toUpperCase())) {
            return StudyType.RYT;
        } else if (studyType.toUpperCase().equals(StudyType.OFT.getName().toUpperCase())) {
            return StudyType.OFT;
        } else if (studyType.toUpperCase().equals(StudyType.S.getName().toUpperCase())) {
            return StudyType.S;
        } else {
            return StudyType.E;
        }
    }*/

    // TODO refactor calls to this method to use PoiUtil in IBPCommons
    private static Boolean rowIsEmpty(Workbook wb, Integer sheet, Integer row, int len) {
        Integer col = 0;
        for (col = 0; col < len; col++) {
            if (getCellStringValue(wb, sheet, row, col) != "" && getCellStringValue(wb, sheet, row, col) != null) {
                return false;
            }
            col++;
        }
        return true;
    }
}
