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
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.exceptions.WorkbookParserException;
import org.generationcp.middleware.util.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class WorkbookParser {
    
    private static final Logger LOG = LoggerFactory.getLogger(WorkbookParser.class);

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

    //GCP-5815
    private org.generationcp.middleware.domain.etl.Workbook currentWorkbook;
    private final static String[] DEFAULT_EXPECTED_VARIABLE_HEADERS = new String[]{"DESCRIPTION", "PROPERTY", "SCALE", "METHOD", "DATA TYPE", "VALUE", "LABEL"};
    private final static String[] EXPECTED_VARIATE_HEADERS = new String[]{"DESCRIPTION", "PROPERTY", "SCALE", "METHOD", "DATA TYPE", "", "SAMPLE LEVEL"};
    private final static String[] EXPECTED_CONSTANT_HEADERS = new String[]{"DESCRIPTION", "PROPERTY", "SCALE", "METHOD", "DATA TYPE", "VALUE", "SAMPLE LEVEL"};
    private final static String[] EXPECTED_FACTOR_HEADERS = new String[]{"DESCRIPTION", "PROPERTY", "SCALE", "METHOD", "DATA TYPE", "NESTED IN", "LABEL"};

    /**
     * Added handling for parsing the file if its xls or xlsx
     * @param file
     * @return
     * @throws IOException
     */
    private Workbook getCorrectWorkbook(File file) throws IOException{
        InputStream inp = new FileInputStream(file);
        InputStream inp2 = new FileInputStream(file);
        Workbook wb;
        try{
            wb = new HSSFWorkbook(inp);
        }catch (OfficeXmlFileException ee) {
            // TODO: handle exception
            wb = new XSSFWorkbook(inp2);
        }finally{
            inp.close();
            inp2.close();
        }
        return wb;
    }
    /**
     * Parses given file and transforms it into a Workbook
     *
     * @param file
     * @return workbook
     * @throws org.generationcp.middleware.exceptions.WorkbookParserException
     *
     */
    public org.generationcp.middleware.domain.etl.Workbook parseFile(File file, boolean performValidation) throws WorkbookParserException {

        currentWorkbook = new org.generationcp.middleware.domain.etl.Workbook();
        Workbook wb;

        currentRow = 0;
        locationId = 0;
        errorMessages = new LinkedList<Message>();

        try {

            
            wb = getCorrectWorkbook(file);

            //validations
            try {
                Sheet sheet1 = wb.getSheetAt(DESCRIPTION_SHEET);

                if (sheet1 == null || sheet1.getSheetName() == null || !(sheet1.getSheetName().equals("Description"))) {
                    errorMessages.add(new Message("error.missing.sheet.description"));
                    /*throw new Error("Error with reading file uploaded. File doesn't have the first sheet - Description");*/
                }
            } catch (Exception e) {
                throw new WorkbookParserException("Error encountered with parseFile(): " + e.getMessage(), e);
            }

            try {
                Sheet sheet2 = wb.getSheetAt(OBSERVATION_SHEET);

                if (sheet2 == null || sheet2.getSheetName() == null || !(sheet2.getSheetName().equals("Observation"))) {
                    errorMessages.add(new Message("error.missing.sheet.observation"));
                    /*throw new Error("Error with reading file uploaded. File doesn't have the second sheet - Observation");*/
                }
            } catch (Exception e) {
                throw new WorkbookParserException("Error encountered with parseFile(): " + e.getMessage(), e);
            }

            // throw an exception here if
            if (errorMessages.size() > 0 && performValidation) {
                throw new WorkbookParserException(errorMessages);
            }

            currentWorkbook.setStudyDetails(readStudyDetails(wb));
            currentWorkbook.setConditions(readMeasurementVariables(wb, "CONDITION"));
            currentWorkbook.setFactors(readMeasurementVariables(wb, "FACTOR"));
            currentWorkbook.setConstants(readMeasurementVariables(wb, "CONSTANT"));
            currentWorkbook.setVariates(readMeasurementVariables(wb, "VARIATE"));

            /*// check if required CONDITION is present for specific study types
            if (currentWorkbook.getStudyDetails().getStudyType() != StudyType.N && locationId == 0) {
                errorMessages.add(new Message("error.missing.trial.factor"));
            }*/

            if (errorMessages.size() > 0 && performValidation) {
                throw new WorkbookParserException(errorMessages);
            }

        } catch (FileNotFoundException e) {
            throw new WorkbookParserException("File not found " + e.getMessage(), e);
        } catch (IOException e) {
            throw new WorkbookParserException("Error accessing file " + e.getMessage(), e);
        } 

        return currentWorkbook;
    }

    public void parseAndSetObservationRows(File file, org.generationcp.middleware.domain.etl.Workbook workbook) throws WorkbookParserException {
        try {
            //InputStream inp = new FileInputStream(file);
            //Workbook wb = new HSSFWorkbook(inp);
            Workbook wb = getCorrectWorkbook(file);

            currentRow = 0;
            workbook.setObservations(readObservations(wb, workbook));
        } catch (IOException e) {
            throw new WorkbookParserException("Error accessing file " + e.getMessage(), e);
        }
    }

    private StudyDetails readStudyDetails(Workbook wb) throws WorkbookParserException {

        //get study details
        String study = getCellStringValue(wb, DESCRIPTION_SHEET, STUDY_NAME_ROW_INDEX, STUDY_DETAILS_VALUE_COLUMN_INDEX);
        String title = getCellStringValue(wb, DESCRIPTION_SHEET, STUDY_TITLE_ROW_INDEX, STUDY_DETAILS_VALUE_COLUMN_INDEX);
        String pmKey = getCellStringValue(wb, DESCRIPTION_SHEET, PMKEY_ROW_INDEX, STUDY_DETAILS_VALUE_COLUMN_INDEX);
        String objective = getCellStringValue(wb, DESCRIPTION_SHEET, OBJECTIVE_ROW_INDEX, STUDY_DETAILS_VALUE_COLUMN_INDEX);
        String startDateStr = getCellStringValue(wb, DESCRIPTION_SHEET, START_DATE_ROW_INDEX, STUDY_DETAILS_VALUE_COLUMN_INDEX);
        String endDateStr = getCellStringValue(wb, DESCRIPTION_SHEET, END_DATE_ROW_INDEX, STUDY_DETAILS_VALUE_COLUMN_INDEX);

        //determine study type
        String studyType = getCellStringValue(wb, DESCRIPTION_SHEET, STUDY_TYPE_ROW_INDEX, STUDY_DETAILS_VALUE_COLUMN_INDEX);
        StudyType studyTypeValue = StudyType.getStudyType(studyType);

        if (study != null) {
            if (study.trim().equals("")) errorMessages.add(new Message("error.blank.study.name"));
        }
        if (title != null) {
            if (title.trim().equals("")) errorMessages.add(new Message("error.blank.study.title"));
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        dateFormat.setLenient(false);
        Date startDate = null;
        Date endDate = null;

        if (startDateStr.length() != 0 && startDateStr.length() != 8) {
            errorMessages.add(new Message("error.start.date.invalid"));
        } else {
            try {
                if (!startDateStr.equals("")) startDate = dateFormat.parse(startDateStr);
            } catch (ParseException e) {
                errorMessages.add(new Message("error.start.date.invalid"));
            }
        }
        if (endDateStr.length() != 0 && endDateStr.length() != 8) {
            errorMessages.add(new Message("error.end.date.invalid"));
        } else {
            try {
                if (!endDateStr.equals("")) endDate = dateFormat.parse(endDateStr);
            } catch (ParseException e) {
                errorMessages.add(new Message("error.end.date.invalid"));
            }

        }

        if (startDate != null && endDate != null && startDate.after(endDate)) {
            errorMessages.add(new Message("error.start.is.after.end.date"));
        }


        if (studyTypeValue == null) {
            studyTypeValue = StudyType.E;
        }

        StudyDetails studyDetails = new StudyDetails(study, title, pmKey, objective, startDateStr, endDateStr, studyTypeValue, 0, null, null);
        
        /* for debugging purposes
        LOG.debug("Study:" + study);
        LOG.debug("Title:" + title);
        LOG.debug("PMKey:" + pmKey);
        LOG.debug("Objective:" + objective);
        LOG.debug("Start Date:" + startDate.toString());
        LOG.debug("End Date:" + endDate.toString());
        LOG.debug("Study Type:" + studyType);
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

            // GCP-5815
            String[] expectedHeaders = null;

            if (name.equals("FACTOR")) {
                expectedHeaders = EXPECTED_FACTOR_HEADERS;
            } else if (name.equals("VARIATE")) {
                expectedHeaders = EXPECTED_VARIATE_HEADERS;
            } else if (name.equals("CONSTANT")) {
                expectedHeaders = EXPECTED_CONSTANT_HEADERS;
            } else {
                expectedHeaders = DEFAULT_EXPECTED_VARIABLE_HEADERS;
            }

            boolean valid = checkHeadersValid(wb, DESCRIPTION_SHEET, currentRow, expectedHeaders);
            if (!valid && expectedHeaders != DEFAULT_EXPECTED_VARIABLE_HEADERS) {
                valid = checkHeadersValid(wb, DESCRIPTION_SHEET, currentRow, DEFAULT_EXPECTED_VARIABLE_HEADERS);
            }

            if (!valid) {
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
                    errorMessages.add(new Message("error.missing.field.name", Integer.toString(currentRow + 1)));
                }

                if (StringUtils.isEmpty(var.getDescription())) {
                    errorMessages.add(new Message("error.missing.field.description", Integer.toString(currentRow + 1)));
                }

                if (StringUtils.isEmpty(var.getProperty())) {
                    errorMessages.add(new Message("error.missing.field.property", Integer.toString(currentRow + 1)));
                }

                if (StringUtils.isEmpty(var.getScale())) {
                    errorMessages.add(new Message("error.missing.field.scale", Integer.toString(currentRow + 1)));
                }

                if (StringUtils.isEmpty(var.getMethod())) {
                    errorMessages.add(new Message("error.missing.field.method", Integer.toString(currentRow + 1)));
                }

                if (StringUtils.isEmpty(var.getDataType())) {
                    errorMessages.add(new Message("error.missing.field.datatype", Integer.toString(currentRow + 1)));
                }

                if (StringUtils.isEmpty(var.getLabel())) {
                    errorMessages.add(new Message("error.missing.field.label", Integer.toString(currentRow + 1)));
                }
                
                if ((name.equals("FACTOR") || name.equals("CONDITION")) && PhenotypicType.getPhenotypicTypeForLabel(var.getLabel()) == null){
                	 errorMessages.add(new Message("error.invalid.field.label", Integer.toString(currentRow + 1)));
                }

                measurementVariables.add(var);

                //set locationId
                if (name.equals("CONDITION") && getCellStringValue(wb, DESCRIPTION_SHEET, currentRow, 0).toUpperCase().equals("TRIAL")) {
                    if (!StringUtils.isEmpty(getCellStringValue(wb, DESCRIPTION_SHEET, currentRow, 6))) {
                        locationId = Long.parseLong(getCellStringValue(wb, DESCRIPTION_SHEET, currentRow, 6));
                    }
                }

	        	/* for debugging purposes
                LOG.debug("");
	            LOG.debug(""+name+":"+getCellStringValue(wb,currentSheet,currentRow,0));
	            LOG.debug("Description:"+getCellStringValue(wb,currentSheet,currentRow,1));
	            LOG.debug("Property:"+getCellStringValue(wb,currentSheet,currentRow,2));
	            LOG.debug("Scale:"+getCellStringValue(wb,currentSheet,currentRow,3));
	            LOG.debug("Method:"+getCellStringValue(wb,currentSheet,currentRow,4));
	            LOG.debug("Data Type:"+getCellStringValue(wb,currentSheet,currentRow,5));
	            LOG.debug("Value:"+getCellStringValue(wb,currentSheet,currentRow,6));
	            LOG.debug("Label:"+getCellStringValue(wb,currentSheet,currentRow,7));
				*/
                currentRow++;
            }

            return measurementVariables;
        } catch (Exception e) {
            throw new WorkbookParserException(e.getMessage(), e);
        }
    }

    private List<MeasurementRow> readObservations(Workbook wb, org.generationcp.middleware.domain.etl.Workbook
            workbook) throws WorkbookParserException {
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
                        LOG.debug(getCellStringValue(wb, OBSERVATION_SHEET, currentRow, col));
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

    // GCP-5815
    private boolean checkHeadersValid(Workbook workbook, int sheetNumber, int row, String[] expectedHeaders) {

        for (int i = 0; i < expectedHeaders.length; i++) {
            // a plus is added to the column count, since the first column is the name of the group; e.g., FACTOR, CONDITION, ETC
            String cellValue = getCellStringValue(workbook, sheetNumber, row, i + 1);
            if (!expectedHeaders[i].equals(cellValue)) {
                return false;
            }
        }

        return true;
    }


    private static Boolean rowIsEmpty(Workbook wb, Integer sheet, Integer row, int len) {
        Integer col = 0;
        for (col = 0; col < len; col++) {
            if (!getCellStringValue(wb, sheet, row, col).equals("") && getCellStringValue(wb, sheet, row, col) != null) {
                return false;
            }
            col++;
        }
        return true;
    }

}
