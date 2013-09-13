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
package org.generationcp.middleware.utils.test;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.etl.Workbook;

public class TestWorkbookUtil {
	
	//PROPERTIES
	public static final String PERSON = "PERSON";
	public static final String TRIAL_INSTANCE = "TRIAL INSTANCE";
	public static final String LOCATION = "LOCATION";
	public static final String EXPERIMENTAL_DESIGN = "EXPERIMENTAL DESIGN";
	public static final String GERMPLASM_ENTRY = "GERMPLASM ENTRY";
	public static final String GERMPLASM_ID = "GERMPLASM ID";
	public static final String CROSS_HISTORY = "CROSS HISTORY";
	public static final String SEED_SOURCE = "SEED SOURCE";
	public static final String FIELD_PLOT = "FIELD PLOT";
	public static final String REPLICATION = "REPLICATION";
	public static final String ROW_LAYOUT = "ROW IN LAYOUT";
	public static final String COL_LAYOUT = "COLUMN IN LAYOUT";
	public static final String GRAIN_YIELD = "Grain Yield";
	public static final String PLANT_HEIGHT = "Plant Height";
	public static final String REPLICATION_FACTOR = "REPLICATION FACTOR";
	public static final String BLOCKING_FACTOR = "BLOCKING FACTOR";
	
	//SCALES
	public static final String DBCV = "DBCV";
	public static final String DBID = "DBID";
	public static final String NUMBER = "NUMBER";
	public static final String TYPE = "TYPE";
	public static final String PEDIGREE_STRING = "PEDIGREE STRING";
	public static final String NAME = "NAME";
	public static final String NESTED_NUMBER = "NESTED NUMBER";
	public static final String DATE = "Date (yyyymmdd)";
	public static final String CALCULATED = "CALCULATED";
	public static final String KG_HA = "kg/ha";
	public static final String GRAMS = "grams";
	public static final String CM = "cm";
			
	//METHODS
	public static final String ASSIGNED = "ASSIGNED";
	public static final String ENUMERATED = "ENUMERATED";
	public static final String CONDUCTED = "CONDUCTED";
	public static final String APPLIED = "APPLIED";
	public static final String NOT_SPECIFIED = "NOT SPECIFIED";
	public static final String SELECTED = "SELECTED";
			
	//LABELS
	public static final String STUDY = "STUDY";
	public static final String TRIAL = "TRIAL";
	public static final String ENTRY = "ENTRY";
	public static final String PLOT = "PLOT";
	
	//DATA TYPES
	public static final String CHAR = "C";
	public static final String NUMERIC = "N";
	
	//FACTORS
	public static final String GID = "GID";
	public static final String DESIG = "DESIG";
	public static final String CROSS = "CROSS";
	public static final String SOURCE = "SOURCE";
	public static final String BLOCK = "BLOCK";
	public static final String REP = "REP";
	public static final String ROW = "ROW";
	public static final String COL = "COL";
	
	//VARIATES
	public static final String VARIATE1 = "GYLD";
	public static final String VARIATE2 = "CHALK_PCT";
//	public static final String VARIATE2 = "NBEPm2";
//	public static final String VARIATE3 = "equi-Kkni";
//	public static final String VARIATE4 = "DTFL";
//	public static final String VARIATE5 = "DFLF";
//	public static final String VARIATE6 = "FDect";
//	public static final String VARIATE7 = "GDENS";
//	public static final String VARIATE8 = "TGW";
//	public static final String VARIATE9 = "PH1";
//	public static final String VARIATE10 = "PH2";
	
	public static final String[] G_NAMES = {"TIANDOUGOU-9" ,"KENINKENI-27"
		,"SM114-1A-1-1-1B" ,"SM114-1A-14-1-1B" ,"SM114-1A-361-1-1B"
		,"SM114-1A-86-1-1B" ,"SM114-1A-115-1-1B" ,"SM114-1A-281-1-1B"
		,"SM114-1A-134-1-1B" ,"SM114-1A-69-1-1B" ,"SM114-1A-157-1-1B"
		,"SM114-1A-179-1-1B" ,"TIANDOUGOU-9" ,"SM114-1A-36-1-1B"
		,"SM114-1A-201-1-1B" ,"SM114-1A-31-1-1B" ,"SM114-1A-353-1-1B"
		,"SM114-1A-26-1-1B" ,"SM114-1A-125-1-1B" ,"SM114-1A-384-1-1B"
		};
	
	private static Workbook workbook;
		
	public static Workbook getTestWorkbook(){
		if (workbook == null){
			createTestWorkbook();
		}
		return workbook;
	}
	
	
	private static void createTestWorkbook(){
		workbook = new Workbook();
		
		createStudyDetails();
		createConditions();
		createFactors();
		createConstants();
		createVariates();
		createObservations();
	}

	
	private static void createStudyDetails() {
		StudyDetails details = new StudyDetails();
		details.setStudyName("pheno_t7" + new Random().nextInt(10000));
		details.setTitle("Phenotyping trials of the Population 114");
		details.setPmKey("0");
		details.setObjective("To evaluate the Population 114");
		details.setStartDate("20130805");
		details.setEndDate("20130805");
		details.setParentFolderId(1);
		details.setStudyType("10010");
		
		workbook.setStudyDetails(details);
	}
	
	
	private static void createConditions(){
		List<MeasurementVariable> conditions = new ArrayList<MeasurementVariable>();
		
		conditions.add(new MeasurementVariable("PI Name", "Name of Principal Investigator", 
				DBCV, ASSIGNED, PERSON, CHAR, "", STUDY));		
		
		conditions.add(new MeasurementVariable("PI ID", "ID of Principal Investigator", 
				DBID, ASSIGNED, PERSON, NUMERIC, "", STUDY));		
		
		conditions.add(new MeasurementVariable("TRIAL", "TRIAL NUMBER", 
				NUMBER, ENUMERATED, TRIAL_INSTANCE, NUMERIC, "1", TRIAL));
		
//		conditions.add(new MeasurementVariable("COOPERATOR", "COOPERATOR NAME", 
//				DBCV, CONDUCTED, PERSON, CHAR, "", TRIAL));		
//		
//		conditions.add(new MeasurementVariable("COOPERATOR ID", "COOPERATOR ID", 
//				DBID, CONDUCTED, PERSON, NUMERIC, "", TRIAL));		
//		
		conditions.add(new MeasurementVariable("SITE", "TRIAL SITE NAME", 
				DBCV, ASSIGNED, LOCATION, CHAR, "", TRIAL));		
		
		conditions.add(new MeasurementVariable("SITE ID", "TRIAL SITE ID", 
				DBID, ASSIGNED, LOCATION, NUMERIC, "", TRIAL));		
		
		conditions.add(new MeasurementVariable("DESIGN", "EXPERIMENTAL DESIGN", 
				TYPE, ASSIGNED, EXPERIMENTAL_DESIGN, CHAR, "", TRIAL));
		
		workbook.setConditions(conditions);
	}
	
	
	private static void createFactors(){
		List<MeasurementVariable> factors = new ArrayList<MeasurementVariable>();
		// Entry Factors
		factors.add(new MeasurementVariable(ENTRY, "The germplasm entry number", 
				NUMBER, ENUMERATED, GERMPLASM_ENTRY, NUMERIC, STUDY, ENTRY));		
		
		factors.add(new MeasurementVariable(GID, "The GID of the germplasm", 
				DBID, ASSIGNED, GERMPLASM_ID, NUMERIC, STUDY, ENTRY));		
		
		factors.add(new MeasurementVariable(DESIG, "The name of the germplasm", 
				DBCV, ASSIGNED, GERMPLASM_ID, CHAR, STUDY, ENTRY));		
		
		factors.add(new MeasurementVariable(CROSS, "The pedigree string of the germplasm", 
				PEDIGREE_STRING, ASSIGNED, CROSS_HISTORY, CHAR, STUDY, ENTRY));		
		
		factors.add(new MeasurementVariable(SEED_SOURCE, "The seed source of the germplasm", 
				NAME, SELECTED, SEED_SOURCE, CHAR, STUDY, ENTRY));		
		
		factors.add(new MeasurementVariable(PLOT, "Plot number ", 
				NESTED_NUMBER, ENUMERATED, FIELD_PLOT, NUMERIC, STUDY, PLOT));		
		
		//Plot Factors
		factors.add(new MeasurementVariable(BLOCK, "INCOMPLETE BLOCK", 
				NUMBER, ENUMERATED, BLOCKING_FACTOR, NUMERIC, STUDY, PLOT));	
		
		factors.add(new MeasurementVariable(REP, REPLICATION, 
				NUMBER, ENUMERATED, REPLICATION_FACTOR, NUMERIC, STUDY, PLOT));
		
//		factors.add(new MeasurementVariable(ROW, "ROW NUMBER", 
//				NUMBER, ENUMERATED, ROW_LAYOUT, NUMERIC, STUDY, PLOT));	
//		
//		factors.add(new MeasurementVariable(COL, "COLUMN NUMBER", 
//				NUMBER, ENUMERATED, COL_LAYOUT, NUMERIC, STUDY, PLOT));
		
		workbook.setFactors(factors);
	}
	
	
	private static void createConstants(){
		List<MeasurementVariable> constants = new ArrayList<MeasurementVariable>();
		
		constants.add(new MeasurementVariable("DATE_SEEDED", "Date Seeded", 
				DATE, APPLIED, "Planting Date", NUMERIC, "0.0", STUDY));	
		
//		constants.add(new MeasurementVariable("DATE_TRANSPLATED", "Date Tranplanted", 
//				DATE, APPLIED, "Transplanting Date", NUMERIC, "0.0", STUDY));		
		
		constants.add(new MeasurementVariable("SOILPH", "Soil pH", 
				"pH", "Ph meter", "Soil Acidity", NUMERIC, "0.0", STUDY));		
		
		
		workbook.setConstants(constants);
	}
	
	
	private static void createVariates(){
		List<MeasurementVariable> variates = new ArrayList<MeasurementVariable>();
		
		variates.add(new MeasurementVariable(VARIATE1, "Grain yield -dry and weigh (kg/ha)", 
				KG_HA, "Dry and weigh", "Yield", NUMERIC, STUDY, STUDY));		
		variates.add(new MeasurementVariable(VARIATE2, "Chalkiness of endosperm - Cervitec (percent)", 
				"Average of Median Percentage", "Cervitec", "Chalkiness of endosperm", NUMERIC, STUDY, STUDY));	
//		variates.add(new MeasurementVariable(VARIATE2, "(formerly RDTG) Grain Yield", 
//				KG_HA, NOT_SPECIFIED, GRAIN_YIELD, NUMERIC, STUDY, STUDY));		
//		
//		variates.add(new MeasurementVariable(VARIATE3, "Yield value of Kkni control based on spatial interpolation model", 
//				KG_HA, "Kkni control ( spatial interpolation model)", GRAIN_YIELD, NUMERIC, STUDY, STUDY));	
//		
//		variates.add(new MeasurementVariable(VARIATE4, "(formerly SFD) Days to Flag Leaf", 
//				CALCULATED, "From date of sowing to ligulation of the flag leaf", "Days to Flag Leaf", NUMERIC, STUDY, STUDY));	
//		
//		variates.add(new MeasurementVariable(VARIATE5, "(formerly FDmoy) Date of flag leaf", 
//				DATE, "Date of ligulation of the flag leaf", "Date of flag leaf", CHAR, STUDY, STUDY));	
//		
//		variates.add(new MeasurementVariable(VARIATE6, "Standard deviation of date of Flag Leaf appearence", 
//				NOT_SPECIFIED, NOT_SPECIFIED, "Standard deviation of date of Flag Leaf appearence", NUMERIC, STUDY, STUDY));		
//		
//		variates.add(new MeasurementVariable(VARIATE7, "(formerly D250) Grain density", 
//				GRAMS, "Grain test weight", "Grain density", NUMERIC, STUDY, STUDY));		
//		
//		variates.add(new MeasurementVariable(VARIATE8, "(formerly P1000) Weight of 1000 grains", 
//				GRAMS, NOT_SPECIFIED, "Weight of 1000 grains", NUMERIC, STUDY, STUDY));	
//		
//		variates.add(new MeasurementVariable(VARIATE9, "(formerly LTIG1) Plant height 1", 
//				CM, "Plant 1", PLANT_HEIGHT, NUMERIC, STUDY, STUDY));	
//		
//		variates.add(new MeasurementVariable(VARIATE10, "(formerly LTIG2) Plant height 2", 
//				CM, "Plant 2", PLANT_HEIGHT, NUMERIC, STUDY, STUDY));	
//		
		
		workbook.setVariates(variates);
	}
	
	
	private static void createObservations(){
		List<MeasurementRow> observations = new ArrayList<MeasurementRow>();
		
		MeasurementRow row;
		List<MeasurementData> dataList;
		Random random = new Random();	
		DecimalFormat fmt = new DecimalFormat("#.##");
		
		for(int i=1; i<21; i++){
			row = new MeasurementRow();
			dataList = new ArrayList<MeasurementData>();
			
			dataList.add(new MeasurementData(ENTRY, String.valueOf(i)));
			dataList.add(new MeasurementData(GID, computeGID(i)));
			dataList.add(new MeasurementData(DESIG, G_NAMES[i-1]));
			dataList.add(new MeasurementData(CROSS, "-"));
			dataList.add(new MeasurementData(SOURCE, "-"));
			dataList.add(new MeasurementData(PLOT, String.valueOf(i)));
			dataList.add(new MeasurementData(BLOCK, ""));
			dataList.add(new MeasurementData(REP, ""));
//			dataList.add(new MeasurementData(ROW, String.valueOf(37+i)));
//			dataList.add(new MeasurementData(COL, "1"));
			dataList.add(new MeasurementData(VARIATE1, randomizeValue(random, fmt, 5000)));
			dataList.add(new MeasurementData(VARIATE2, randomizeValue(random, fmt, 100)));
			
			row.setDataList(dataList);
			observations.add(row);
		}
		
		workbook.setObservations(observations);
	}
	
	
	// Row 13 has same GID as first row
	private static String computeGID(int i){
		int gid = 1000000;
		if (i < 13){
			gid += i - 1;
		} else if (i > 13 ){
			gid += i - 2;
		}
		return String.valueOf(gid);
	}
	
	private static String randomizeValue(Random random, DecimalFormat fmt, int base){
		double value = random.nextDouble() * base;
		return fmt.format(value);
	}
	

}
