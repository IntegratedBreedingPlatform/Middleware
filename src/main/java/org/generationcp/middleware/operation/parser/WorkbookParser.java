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
import org.generationcp.middleware.exceptions.MiddlewareQueryException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class WorkbookParser {
	
	private static Integer currentSheet;
	private static Integer currentRow;
	private static long locationId;
	
	/**
	 * Parses given file and transforms it into a Workbook
	 * @param file
	 * @return workbook
	 * @throws MiddlewareQueryException
	 */
	public static org.generationcp.middleware.domain.etl.Workbook parseFile(File file) throws MiddlewareQueryException{
		
		org.generationcp.middleware.domain.etl.Workbook workbook = new org.generationcp.middleware.domain.etl.Workbook();
		Workbook wb;
		
		currentSheet = 0;
		currentRow = 0;
		
		try {
			
			InputStream inp = new FileInputStream(file);
			wb = new HSSFWorkbook(inp);
			
			//validations
			try{
                Sheet sheet1 = wb.getSheetAt(0);
                
                if(sheet1 == null || sheet1.getSheetName() == null || !(sheet1.getSheetName().equals("Description"))){
                	throw new Error("Error with reading file uploaded. File doesn't have the first sheet - Description");
                }
            } catch(Exception e){
            	throw new MiddlewareQueryException("Error encountered with parseFile(): " + e.getMessage(), e);                
            }
            
            try{
                Sheet sheet2 = wb.getSheetAt(1);
                
                if(sheet2 == null || sheet2.getSheetName() == null || !(sheet2.getSheetName().equals("Observation"))){
                	throw new Error("Error with reading file uploaded. File doesn't have the second sheet - Observation");
                }
            } catch(Exception e){
            	throw new MiddlewareQueryException("Error encountered with parseFile(): " + e.getMessage(), e);
            }
			
            workbook.setStudyDetails(readStudyDetails(wb));
            workbook.setConditions(readMeasurementVariables(wb, "CONDITION"));
            workbook.setFactors(readMeasurementVariables(wb, "FACTOR"));
            workbook.setConstants(readMeasurementVariables(wb, "CONSTANT"));
            workbook.setVariates(readMeasurementVariables(wb, "VARIATE"));
            
            currentSheet++;
            currentRow = 0;
            
            workbook.setObservations(readObservations(wb, workbook));
            			
		} catch (FileNotFoundException e) {
            throw new MiddlewareQueryException("File not found " + e.getMessage(), e);
        } catch (IOException e) {
        	throw new MiddlewareQueryException("Error accessing file " + e.getMessage(), e);
        } 
		
		return workbook;
	}
	
	private static StudyDetails readStudyDetails(Workbook wb) throws MiddlewareQueryException {
		
		//get study details
		String study = getCellStringValue(wb,currentSheet,0,1);
        String title = getCellStringValue(wb,currentSheet,1,1);
        String pmKey = getCellStringValue(wb,currentSheet,2,1);
        String objective = getCellStringValue(wb,currentSheet,3,1);
        String startDate = getCellStringValue(wb,currentSheet,4,1);
        String endDate = getCellStringValue(wb,currentSheet,5,1);
        
        //determine study type
        String studyType = getCellStringValue(wb,currentSheet,6,1);
        StudyType studyTypeValue = getStudyTypeValue(studyType);
        
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
        
        while(!rowIsEmpty(wb, currentSheet, currentRow, 8)){
        	currentRow++;
        }
        return studyDetails;
	}
	
	private static List<MeasurementVariable> readMeasurementVariables(Workbook wb, String name) throws MiddlewareQueryException {
		List<MeasurementVariable> measurementVariables = new ArrayList<MeasurementVariable>();
		
		try {
		
			currentRow++; //Skip empty row
	        //Check if headers are correct
	        if(!getCellStringValue(wb, currentSheet,currentRow,0).toUpperCase().equals(name) 
	                || !getCellStringValue(wb,currentSheet,currentRow,1).toUpperCase().equals("DESCRIPTION")
	                || !getCellStringValue(wb,currentSheet,currentRow,2).toUpperCase().equals("PROPERTY")
	                || !getCellStringValue(wb,currentSheet,currentRow,3).toUpperCase().equals("SCALE")
	                || !getCellStringValue(wb,currentSheet,currentRow,4).toUpperCase().equals("METHOD")
	                || !getCellStringValue(wb,currentSheet,currentRow,5).toUpperCase().equals("DATA TYPE")
	                || !getCellStringValue(wb,currentSheet,currentRow,6).toUpperCase().equals("VALUE")
	                || !getCellStringValue(wb,currentSheet,currentRow,7).toUpperCase().equals("LABEL")) {
	            
	        	/* for debugging purposes
	            System.out.println("DEBUG | Invalid file on readMeasurementVariables");
	            System.out.println(getCellStringValue(wb, currentSheet,currentRow,0).toUpperCase());
	            */
	            
	            throw new Error("Incorrect headers for " + name);
	        }
	
	        //If file is still valid (after checking headers), proceed
	        currentRow++; 
	        while(!rowIsEmpty(wb, currentSheet, currentRow, 8)){
	        	measurementVariables.add(new MeasurementVariable(getCellStringValue(wb,currentSheet,currentRow,0)
	                    ,getCellStringValue(wb,currentSheet,currentRow,1)
	                    ,getCellStringValue(wb,currentSheet,currentRow,3)
	                    ,getCellStringValue(wb,currentSheet,currentRow,4)
	                    ,getCellStringValue(wb,currentSheet,currentRow,2)
	                    ,getCellStringValue(wb,currentSheet,currentRow,5)
	                    ,getCellStringValue(wb,currentSheet,currentRow,6)
	                    ,getCellStringValue(wb,currentSheet,currentRow,7)));
	
	        	//set locationId 
	        	if (name.equals("CONDITION") && getCellStringValue(wb,currentSheet,currentRow,0).toUpperCase().equals("TRIAL")) {
	        		if (getCellStringValue(wb,currentSheet,currentRow,6) != "" && getCellStringValue(wb,currentSheet,currentRow,6) != null) {
	        			locationId = Long.parseLong(getCellStringValue(wb,currentSheet,currentRow,6));
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
		} catch(Exception e) {
			throw new MiddlewareQueryException(e.getMessage(), e);
		}
	}
	
	private static List<MeasurementRow> readObservations(Workbook wb, org.generationcp.middleware.domain.etl.Workbook workbook) throws MiddlewareQueryException {
		List<MeasurementRow> observations = new ArrayList<MeasurementRow>();
		long stockId = 0;
		
		try {			
			//validate headers and set header labels
			List<MeasurementVariable> factors = workbook.getFactors();
	        List<MeasurementVariable> variates = workbook.getVariates();
	        List<String> measurementDataLabel = new ArrayList<String>();
	        for (int col = 0; col < factors.size()+variates.size(); col++){
	        	if (col < factors.size()) {
	            	if (!factors.get(col).getName().toUpperCase().equals(getCellStringValue(wb, currentSheet, currentRow, col).toUpperCase())) {
	            		throw new Error("Incorrect header for observations.");
	            	} 
	        	} else {
					if (!variates.get(col-factors.size()).getName().toUpperCase().equals(getCellStringValue(wb, currentSheet, currentRow, col).toUpperCase())) {
						throw new Error("Incorrect header for observations.");
					}
	        	}
	        	measurementDataLabel.add(getCellStringValue(wb, currentSheet, currentRow, col));
	        }  
	        
	        currentRow++;
	        
	        //add each row in observations
	        while (!rowIsEmpty(wb, currentSheet, currentRow, factors.size()+variates.size())){
	            List<MeasurementData> measurementData = new ArrayList<MeasurementData>();
	            
	            for (int col = 0; col < factors.size()+variates.size(); col++){
	            	if (col == 0) {
	            		stockId = Long.parseLong(getCellStringValue(wb, currentSheet, currentRow, col));
	            	} 
            		measurementData.add(new MeasurementData(measurementDataLabel.get(col), getCellStringValue(wb, currentSheet, currentRow, col)));
	            }
	            
	            observations.add(new MeasurementRow(stockId, locationId, measurementData));
	            currentRow++;
	        }
			
			return observations;
		} catch(Exception e) {
			throw new MiddlewareQueryException(e.getMessage(), e);
		}
	}
        
    private static String getCellStringValue(Workbook wb, Integer sheetNumber, Integer rowNumber, Integer columnNumber){
        try {
            Sheet sheet = wb.getSheetAt(sheetNumber);
            Row row = sheet.getRow(rowNumber);
            Cell cell = row.getCell(columnNumber);
            return cell.getStringCellValue();
        } catch(IllegalStateException e) {
            Sheet sheet = wb.getSheetAt(sheetNumber);
            Row row = sheet.getRow(rowNumber);
            Cell cell = row.getCell(columnNumber);
            return String.valueOf(Integer.valueOf((int) cell.getNumericCellValue()));
        } catch(NullPointerException e) {
            return "";
        }
    }
    
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
    }
        
    private static Boolean rowIsEmpty(Workbook wb, Integer sheet, Integer row, int len){
    	Integer col = 0;
        for (col = 0; col < len; col++) {
            if(getCellStringValue(wb, sheet, row, col) != "" && getCellStringValue(wb, sheet, row, col) != null){
                return false;
            }
            col++;
        }
        return true;            
    }
    
}
