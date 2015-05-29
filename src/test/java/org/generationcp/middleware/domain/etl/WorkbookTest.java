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
package org.generationcp.middleware.domain.etl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import junit.framework.Assert;

import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.Location;
import org.junit.Test;

public class WorkbookTest {
	
	private static final int DAY_OBS = 8284;
	private static final int ASPERGILLUS_FLAVUSPPB = 20369;
	private static final int ASPERGILLUS_FLAVUS1_5 = 20368;
	private static final String NURSERY_NAME = "Nursery_";
	private static final String TRIAL_NAME = "Trial_";

	// STUDY DETAILS
	private static final String TITLE = "Nursery Workbook";
	private static final String OBJECTIVE = "To evaluate the Population 114";
	private static final String START_DATE = "20130805";
	private static final String END_DATE = "20130805";
	private static final int FOLDER_ID = 1;

	// PROPERTIES
	private static final String PERSON = "PERSON";
	private static final String TRIAL_INSTANCE = "TRIAL";
	private static final String LOCATION = "LOCATION";
	private static final String GERMPLASM_ENTRY = "GERMPLASM ENTRY";
	private static final String GERMPLASM_ID = "GERMPLASM ID";
	private static final String CROSS_HISTORY = "CROSS HISTORY";
	private static final String SEED_SOURCE = "SEED SOURCE";
	private static final String FIELD_PLOT = "FIELD PLOT";
	private static final String REPLICATION = "REPLICATION";
	private static final String REPLICATION_FACTOR = "REPLICATION FACTOR";
	private static final String BLOCKING_FACTOR = "BLOCKING FACTOR";
	private static final String SOIL_ACIDITY = "Soil Acidity";
	private static final String YIELD = "Yield";

	// SCALES
	private static final String DBCV = "DBCV";
	private static final String DBID = "DBID";
	private static final String NUMBER = "NUMBER";
	private static final String PEDIGREE_STRING = "PEDIGREE STRING";
	private static final String NAME = "NAME";
	private static final String NESTED_NUMBER = "NESTED NUMBER";
	private static final String KG_HA = "kg/ha";
	private static final String PH = "ph";
	private static final String SCORE_1_5 = "Score (1-5)";
	private static final String VISUAL_SCORING = "Visual scoring";
	private static final String COMMON_RUST = "Common rust";

	// METHODS
	private static final String ASSIGNED = "ASSIGNED";
	private static final String ENUMERATED = "ENUMERATED";
	private static final String CONDUCTED = "CONDUCTED";
	private static final String SELECTED = "SELECTED";
	private static final String PH_METER = "PH Meter";
	private static final String DRY_AND_WEIGH = "Dry and Weigh";

	// LABELS
	private static final String STUDY = "STUDY";
	private static final String TRIAL = "TRIAL";
	private static final String ENTRY = "ENTRY";
	private static final String PLOT = "PLOT";

	// DATA TYPES
	private static final String CHAR = "C";
	private static final String NUMERIC = "N";

	// FACTORS
	private static final String GID = "GID";
	private static final String DESIG = "DESIG";
	private static final String CROSS = "CROSS";
	private static final String SOURCE = "SOURCE";
	private static final String BLOCK = "BLOCK";
	private static final String REP = "REP";

	// CONSTANTS
	private static final int GRAIN_SIZE_ID = 18110;
	private static final int SOILPH_ID = 8270;
	private static final String GRAIN_SIZE_PROPERTY = "Grain size";
	private static final String DRY_GRAINS = "Weigh 1000 dry grains";
	private static final String GRAIN_SIZE_SCALE = "g";


	// VARIATES
	private static final String GYLD = "GYLD";
	private static final int GYLD_ID = 18000;
	private static final String CRUST = "CRUST";
	public static final int CRUST_ID = 20310;

	// CONDITIONS
	private static final String PI_NAME = "PI Name";
	private static final String PI_ID = "PI Id";
	private static final String COOPERATOR = "COOPERATOR";
	private static final String COOPERATOR_ID = "COOPERATOR ID";
	private static final int COOPERATOR_ID_ID = 8372;
	private static final int COOPERATOR_NAME_ID = 8373;

	private static final String GERMPLASM_NAME = "TIANDOUGOU-9";
	private static final String NUMERIC_VALUE = "1";
	
	public static final Integer LOCATION_ID_1 = 1;
	public static final Integer LOCATION_ID_2 = 2;
	private static final Integer LTYPE = 1;
	private static final Integer NLLP = 1;
	public static final String LNAME = "Location";
	private static final String LABBR = "LOC2";
	private static final Integer SNL3ID = 1;
	private static final Integer SNL2ID = 1;
	private static final Integer SNL1ID = 1;
	private static final Integer CNTRYID = 1;
	private static final Integer LRPLCE = 1;
	
	private static final String VALID_CRUST_VALUE = "1";
	private static final String VALID_CRUST_CVALUE_ID = "50723";
	private static final String INVALID_CRUST_VALUE_NUMERIC = "11";
	private static final String INVALID_CRUST_VALUE_CHAR = "test";
	private static final String INVALID_CRUST_VALUE_MISSING = "missing";
	
	private static final int DEFAULT_NO_OF_OBSERVATIONS = 10;
	private static final int LOCATION_NAME_ID = 8180;
	private static final int LOCATION_ID_ID = 8190;
	private static final int EXPT_DESIGN_ID = 8135;
	private static final String EXPERIMENT_DESIGN = "Experimental design";
	private static final String TYPE = "Type";
	private static final int CHALK_PCT_ID = 61193;
	private static final String CHALK_PCT = "CHALK_PCT";
	
	public static final String[] G_NAMES = {"TIANDOUGOU-9" ,"KENINKENI-27"
		,"SM114-1A-1-1-1B" ,"SM114-1A-14-1-1B" ,"SM114-1A-361-1-1B"
		,"SM114-1A-86-1-1B" ,"SM114-1A-115-1-1B" ,"SM114-1A-281-1-1B"
		,"SM114-1A-134-1-1B" ,"SM114-1A-69-1-1B" ,"SM114-1A-157-1-1B"
		,"SM114-1A-179-1-1B" ,"TIANDOUGOU-9" ,"SM114-1A-36-1-1B"
		,"SM114-1A-201-1-1B" ,"SM114-1A-31-1-1B" ,"SM114-1A-353-1-1B"
		,"SM114-1A-26-1-1B" ,"SM114-1A-125-1-1B" ,"SM114-1A-384-1-1B"
		};

	private static Workbook workbook;
	private static List<Workbook> workbooks;
	
	public static Workbook getTestWorkbook() {
		if (workbook == null) {
			createTestWorkbook(DEFAULT_NO_OF_OBSERVATIONS, StudyType.N, null, 1, false);
		}
		return workbook;
	}
	
	public static Workbook getTestWorkbook(int noOfObservations, StudyType studyType) {
		if (workbook == null) {
			createTestWorkbook(noOfObservations, studyType, null, 1, false);
		}
		return workbook;
	}
	
	public static List<Workbook> getTestWorkbooks(int noOfTrial,int noOfObservations){
		if (workbooks == null){
			workbooks = new ArrayList<Workbook>();
			String studyName = "pheno_t7" + new Random().nextInt(10000);
			for (int i = 1; i <= noOfTrial; i++) {
				workbooks.add(createTestWorkbook(noOfObservations, StudyType.T, studyName, i, true));
			}
		}
		return workbooks;
	}
	
	public static void setTestWorkbook(Workbook workbookNew) {
		workbook = workbookNew;
	}

	private static Workbook createTestWorkbook(
			int noOfObservations, StudyType studyType,
			String studyName, int trialNo, boolean hasMultipleLocations) {
		workbook = new Workbook();

		createStudyDetails(workbook,studyName,studyType);
		createConditions(workbook,!hasMultipleLocations,trialNo);
		createFactors(workbook,true,hasMultipleLocations,trialNo);
		createConstants(workbook);
		createVariates(workbook);
		createObservations(workbook,noOfObservations,hasMultipleLocations,trialNo);
		
		return workbook;
	}

	private static void createStudyDetails(Workbook workbook, String studyName, StudyType studyType) {
		StudyDetails details = new StudyDetails();
		if(studyName!=null) {
			//this is used for adding multiple locations to one study
			details.setStudyName(studyName);
		} else {
			details.setStudyName((studyType.equals(StudyType.N) ? NURSERY_NAME : TRIAL_NAME) + 
					new Random().nextInt(10000));
		}
		details.setTitle(TITLE);
		details.setObjective(OBJECTIVE);
		details.setStartDate(START_DATE);
		details.setEndDate(END_DATE);
		details.setParentFolderId(FOLDER_ID);
		details.setStudyType(studyType);

		workbook.setStudyDetails(details);
	}
	
	private static MeasurementVariable createMeasurementVariable(int termId, String name, String description, String scale, 
			String method, String property, String dataType, String value, String label, int dataTypeId) {
		MeasurementVariable variable = new MeasurementVariable(
				termId, name, description, scale, description, property, 
				dataType, value, label);
		variable.setDataTypeId(dataTypeId);
		return variable;
	}
	
	public static MeasurementVariable createTrialInstanceMeasurementVariable(int trialNo) {
		return createMeasurementVariable(TermId.TRIAL_INSTANCE_FACTOR.getId(), 
				"TRIAL", "TRIAL NUMBER", NUMBER, ENUMERATED, TRIAL_INSTANCE, NUMERIC, 
				String.valueOf(trialNo), TRIAL, TermId.CHARACTER_VARIABLE.getId());
	}

	private static void createConditions(Workbook workbook, boolean withTrial, int trialNo) {
		//Create measurement variables and set its dataTypeId
		List<MeasurementVariable> conditions = new ArrayList<MeasurementVariable>();

		if(withTrial) {
			conditions.add(createTrialInstanceMeasurementVariable(trialNo));
		}
		
		conditions.add(createMeasurementVariable(TermId.PI_NAME.getId(), "PI Name",
				"Name of Principal Investigator", DBCV, ASSIGNED,
				PERSON, CHAR, "PI Name Value", STUDY, TermId.CHARACTER_VARIABLE.getId()));

		conditions.add(createMeasurementVariable(TermId.PI_ID.getId(), "PI ID",
				"ID of Principal Investigator", DBID, ASSIGNED,
				PERSON, NUMERIC, NUMERIC_VALUE,
				STUDY, TermId.NUMERIC_VARIABLE.getId()));
		
		conditions.add(createMeasurementVariable(COOPERATOR_NAME_ID, "COOPERATOR",
				"COOPERATOR NAME", DBCV, CONDUCTED,
				PERSON, CHAR, "John Smith",
				TRIAL,TermId.CHARACTER_VARIABLE.getId()));

		conditions.add(createMeasurementVariable(COOPERATOR_ID_ID, "COOPERATOR ID",
				"COOPERATOR ID", DBID, CONDUCTED,
				PERSON, NUMERIC, NUMERIC_VALUE,
				TRIAL, TermId.NUMERIC_VARIABLE.getId()));
		
		conditions.add(new MeasurementVariable(LOCATION_NAME_ID, "SITE", "TRIAL SITE NAME", 
				DBCV, ASSIGNED, LOCATION, CHAR, "SITE " + trialNo, TRIAL));		
		
		conditions.add(new MeasurementVariable(LOCATION_ID_ID, "SITE ID", "TRIAL SITE ID", 
				DBID, ASSIGNED, LOCATION, NUMERIC, String.valueOf(trialNo), TRIAL));		
		
		conditions.add(new MeasurementVariable(EXPT_DESIGN_ID, "DESIGN", "EXPERIMENTAL DESIGN", 
				TYPE, ASSIGNED, EXPERIMENT_DESIGN, CHAR, 
				String.valueOf(TermId.RANDOMIZED_COMPLETE_BLOCK.getId()), TRIAL));
		

		workbook.setConditions(conditions);
	}

	private static void createFactors(Workbook workbook, boolean withEntry, boolean withTrial, int trialNo) {
		//Create measurement variables and set its dataTypeId
		List<MeasurementVariable> factors = new ArrayList<MeasurementVariable>();
		
		if(withTrial) {
			factors.add(createTrialInstanceMeasurementVariable(trialNo));
		}
		
		// Entry Factors
		if(withEntry) {
			factors.add(createMeasurementVariable(TermId.ENTRY_NO.getId(),
					ENTRY, "The germplasm entry number", NUMBER,
					ENUMERATED, GERMPLASM_ENTRY,
					NUMERIC, STUDY, ENTRY, TermId.NUMERIC_VARIABLE.getId()));
		}

		factors.add(createMeasurementVariable(TermId.GID.getId(), GID,
				"The GID of the germplasm", DBID, ASSIGNED,
				GERMPLASM_ID, NUMERIC,
				NUMERIC_VALUE, ENTRY, TermId.NUMERIC_VARIABLE.getId()));

		factors.add(createMeasurementVariable(TermId.DESIG.getId(), DESIG,
				"The name of the germplasm", DBCV, ASSIGNED,
				GERMPLASM_ID, CHAR, STUDY,
				ENTRY, TermId.CHARACTER_VARIABLE.getId()));

		factors.add(createMeasurementVariable(TermId.CROSS.getId(), CROSS,
				"The pedigree string of the germplasm", PEDIGREE_STRING,
				ASSIGNED, CROSS_HISTORY, CHAR,
				STUDY, ENTRY, TermId.CHARACTER_VARIABLE.getId()));

		factors.add(createMeasurementVariable(TermId.SEED_SOURCE.getId(),
				SEED_SOURCE, "The seed source of the germplasm",
				NAME, SELECTED, SEED_SOURCE,
				CHAR, STUDY, ENTRY, TermId.CHARACTER_VARIABLE.getId()));

		factors.add(createMeasurementVariable(TermId.PLOT_NO.getId(), PLOT,
				"Plot number ", NESTED_NUMBER, ENUMERATED,
				FIELD_PLOT, NUMERIC,
				NUMERIC_VALUE, PLOT, TermId.NUMERIC_VARIABLE.getId()));

		// Plot Factors
		factors.add(createMeasurementVariable(TermId.BLOCK_NO.getId(), BLOCK,
				"INCOMPLETE BLOCK", NUMBER, ENUMERATED,
				BLOCKING_FACTOR, NUMERIC,
				NUMERIC_VALUE, PLOT, TermId.NUMERIC_VARIABLE.getId()));

		factors.add(createMeasurementVariable(TermId.REP_NO.getId(), REP,
				REPLICATION, NUMBER, ENUMERATED,
				REPLICATION_FACTOR, NUMERIC,
				NUMERIC_VALUE, PLOT, TermId.NUMERIC_VARIABLE.getId()));
		
		factors.add(createMeasurementVariable(DAY_OBS, "DAY_OBS",
				REPLICATION, NUMBER, ENUMERATED,
				REPLICATION_FACTOR, NUMERIC,
				NUMERIC_VALUE, PLOT, TermId.NUMERIC_VARIABLE.getId()));

		workbook.setFactors(factors);
	}

	private static void createConstants(Workbook workbook) {
		//Create measurement variables and set its dataTypeId
		List<MeasurementVariable> constants = new ArrayList<MeasurementVariable>();

		constants.add(createMeasurementVariable(GRAIN_SIZE_ID,
				"Grain_size", "Grain size - weigh 1000 dry grains (g)", GRAIN_SIZE_SCALE, DRY_GRAINS,
				GRAIN_SIZE_PROPERTY, NUMERIC,
				NUMERIC_VALUE, TRIAL, TermId.NUMERIC_VARIABLE.getId()));

		workbook.setConstants(constants);
	}

	private static void createVariates(Workbook workbook) {
		//Create measurement variables and set its dataTypeId
		List<MeasurementVariable> variates = new ArrayList<MeasurementVariable>();
		MeasurementVariable measurementVariable = createMeasurementVariable(GYLD_ID,
				GYLD, "Grain yield -dry and weigh (kg/ha)",
				KG_HA, DRY_AND_WEIGH, YIELD,
				NUMERIC, NUMERIC_VALUE, PLOT, TermId.NUMERIC_VARIABLE.getId());
		measurementVariable.setStoredIn(TermId.OBSERVATION_VARIATE.getId());
		variates.add(measurementVariable);
		
		MeasurementVariable chalkPct = createMeasurementVariable(CHALK_PCT_ID,
				CHALK_PCT, "Grain yield -dry and weigh (kg/ha)",
				KG_HA, DRY_AND_WEIGH, YIELD,
				NUMERIC, NUMERIC_VALUE, PLOT, TermId.NUMERIC_VARIABLE.getId());
		chalkPct.setStoredIn(TermId.OBSERVATION_VARIATE.getId());
		variates.add(chalkPct);
		
		workbook.setVariates(variates);
	}
	
	private static void createVariatesWithDuplicatePSM(Workbook workbook){
		List<MeasurementVariable> variates = new ArrayList<MeasurementVariable>();
		
		MeasurementVariable gyld = createMeasurementVariable(GYLD_ID,
				GYLD, "Grain yield -dry and weigh (kg/ha)",
				KG_HA, DRY_AND_WEIGH, YIELD,
				NUMERIC, NUMERIC_VALUE, PLOT, TermId.NUMERIC_VARIABLE.getId());
		gyld.setStoredIn(TermId.OBSERVATION_VARIATE.getId());
		variates.add(gyld);
		
		MeasurementVariable chalkPct = createMeasurementVariable(CHALK_PCT_ID,
				CHALK_PCT, "Chalkiness of endosperm - Cervitec (percent)",
				"Average of Median Percentage", "Cervitec", "Chalkiness of endosperm", 
				NUMERIC, NUMERIC_VALUE, PLOT, TermId.NUMERIC_VARIABLE.getId());
		chalkPct.setStoredIn(TermId.OBSERVATION_VARIATE.getId());
		variates.add(chalkPct);
		
		workbook.setVariates(variates);
	}
	public static void addVariatesAndObservations(Workbook currentWorkbook) {
		
		List<MeasurementVariable> variates = currentWorkbook.getVariates();
		MeasurementVariable measurementVariable = createMeasurementVariable(CRUST_ID,
				CRUST, "Score for the severity of common rust, (In highlands and mid altitude, Puccinia sorghi) symptoms rated on a scale from 1 (= clean, no infection) to 5 (= severely diseased).",
				SCORE_1_5, VISUAL_SCORING, COMMON_RUST,
				CHAR, null, PLOT, TermId.CHARACTER_VARIABLE.getId());
		measurementVariable.setStoredIn(TermId.CATEGORICAL_VARIATE.getId());
		measurementVariable.setOperation(Operation.ADD);
		variates.add(measurementVariable);
		
		addObservations(currentWorkbook);
	}

	private static void createObservations(Workbook workbook, int noOfObservations,boolean withTrial,int trialNo) {
		List<MeasurementRow> observations = new ArrayList<MeasurementRow>();

		MeasurementRow row;
		List<MeasurementData> dataList;
		Random random = new Random();
		DecimalFormat fmt = new DecimalFormat("#.##");

		//Create n number of observation rows
		for (int i = 0; i < noOfObservations; i++) {
			row = new MeasurementRow();
			dataList = new ArrayList<MeasurementData>();
			if(withTrial) {
				dataList.add(WorkbookTest.createMeasurementData(
						TRIAL,String.valueOf(trialNo),
						TermId.TRIAL_INSTANCE_FACTOR.getId(),workbook.getFactors()));
			}
			
			dataList.add(createMeasurementData(ENTRY, String.valueOf(i), TermId.ENTRY_NO.getId(), workbook.getFactors()));
			dataList.add(createMeasurementData(GID, computeGID(i), TermId.GID.getId(), workbook.getFactors()));
			dataList.add(createMeasurementData(DESIG, G_NAMES[i], 
					TermId.DESIG.getId(), workbook.getFactors()));
			dataList.add(createMeasurementData(CROSS, "-", TermId.CROSS.getId(), workbook.getFactors()));
			dataList.add(createMeasurementData(SOURCE, "-", TermId.SEED_SOURCE.getId(), workbook.getFactors()));
			dataList.add(createMeasurementData(PLOT, String.valueOf(i), TermId.PLOT_NO.getId(), workbook.getFactors()));
			dataList.add(createMeasurementData(BLOCK, "", TermId.BLOCK_NO.getId(), workbook.getFactors()));
			dataList.add(createMeasurementData(REP, "", TermId.REP_NO.getId(), workbook.getFactors()));
			dataList.add(createMeasurementData("DAY_OBS", randomizeValue(random, fmt, 5000), DAY_OBS, workbook.getFactors()));
			dataList.add(createMeasurementData(GYLD, randomizeValue(random, fmt, 5000), GYLD_ID, workbook.getVariates()));
			dataList.add(createMeasurementData(CHALK_PCT, randomizeValue(random, fmt, 5000), CHALK_PCT_ID, workbook.getVariates()));
			row.setDataList(dataList);
			observations.add(row);
		}

		workbook.setObservations(observations);
	}
	
	private static void addObservations(Workbook currentWorkbook) {
		List<MeasurementRow> observations = currentWorkbook.getObservations();

		MeasurementRow row;
		List<MeasurementData> dataList;
		Random random = new Random();
		DecimalFormat fmt = new DecimalFormat("#.##");

		//Create n number of observation rows
		for (int i = 0; i < observations.size(); i++) {
			row = observations.get(i);
			dataList = row.getDataList();
			
			String crustValue = randomizeValue(random, fmt, 5000);
			String crustCValueId = null;
			switch(i) {
				case 0: crustValue = ""; break;
				case 1: crustValue = null; break;
				case 2: crustValue = VALID_CRUST_VALUE; crustCValueId = VALID_CRUST_CVALUE_ID; break;
				case 3: crustValue = INVALID_CRUST_VALUE_NUMERIC; break;
				case 4: crustValue = INVALID_CRUST_VALUE_CHAR; break;
				case 5: crustValue = INVALID_CRUST_VALUE_MISSING; break;
			}
			MeasurementData measurementData = createMeasurementData(CRUST, crustValue, CRUST_ID, workbook.getVariates());
			measurementData.setcValueId(crustCValueId);
			dataList.add(measurementData);
		}
	}

	public static MeasurementData createMeasurementData(String label, String value, int termId, List<MeasurementVariable> variables) {
		MeasurementData data = new MeasurementData(label, value);
		data.setMeasurementVariable(getMeasurementVariable(
				termId, variables));
		return data;
	}

	private static MeasurementVariable getMeasurementVariable(int termId,
			List<MeasurementVariable> variables) {
		if (variables != null) {
			//get matching MeasurementVariable object given the term id
			for (MeasurementVariable var : variables) {
				if (var.getTermId() == termId) {
					return var;
				}
			}
		}
		return null;
	}

	private static String computeGID(int i) {
		int gid = 1000000;
		if (i < 13){
			gid += i - 1;
		} else if (i > 13 ){
			gid += i - 2;
		}
		return String.valueOf(gid);
	}

	private static String randomizeValue(Random random, DecimalFormat fmt, int base) {
		double value = random.nextDouble() * base;
		return fmt.format(value);
	}
	
	public static List<Location> createLocationData() {
		List<Location> locations = new ArrayList<Location>();
		locations.add(new Location(LOCATION_ID_1, LTYPE, NLLP, LNAME + " 1", LABBR, SNL3ID, SNL2ID,
	            SNL1ID, CNTRYID, LRPLCE));
		locations.add(new Location(LOCATION_ID_2, LTYPE, NLLP, LNAME + " 2", LABBR, SNL3ID, SNL2ID,
	            SNL1ID, CNTRYID, LRPLCE));
		return locations;
	}
	
	public static MeasurementRow createTrialObservationWithoutSite() {
		Workbook workbook = new Workbook();

		createStudyDetails(workbook,null,StudyType.T);
		createConditions(workbook,true,1);
		
		MeasurementRow row = new MeasurementRow();
		List<MeasurementData> dataList = new ArrayList<MeasurementData>();

		dataList.add(createMeasurementData(TRIAL_INSTANCE, NUMERIC_VALUE, 
				TermId.TRIAL_INSTANCE_FACTOR.getId(), workbook.getConditions()));
		dataList.add(createMeasurementData(PI_NAME, "", TermId.PI_NAME.getId(), workbook.getConditions()));
		dataList.add(createMeasurementData(PI_ID, "", TermId.PI_ID.getId(), workbook.getConditions()));
		dataList.add(createMeasurementData(COOPERATOR, "", COOPERATOR_NAME_ID, workbook.getConditions()));
		dataList.add(createMeasurementData(COOPERATOR_ID, "", COOPERATOR_ID_ID, workbook.getConditions()));

		row.setDataList(dataList);
		return row;
	}

	public static Workbook addEnvironmentAndConstantVariables(Workbook createdWorkbook) {
		addConditions(createdWorkbook.getConditions());
		addConstants(createdWorkbook.getConstants());		
		return createdWorkbook;
	}	
	
	private static void addConstants(List<MeasurementVariable> constants) {
		MeasurementVariable variable = createMeasurementVariable(SOILPH_ID, "SITE_SOIL_PH", "Soil acidity - ph meter (pH)",
				PH, PH_METER, SOIL_ACIDITY,
				NUMERIC, NUMERIC_VALUE, TRIAL, TermId.NUMERIC_VARIABLE.getId());
		variable.setOperation(Operation.ADD);
		variable.setStoredIn(TermId.OBSERVATION_VARIATE.getId());
		constants.add(variable);
	}

	private static void addConditions(List<MeasurementVariable> conditions) {		
		MeasurementVariable variable = createMeasurementVariable(TermId.TRIAL_LOCATION.getId(), "SITE", "TRIAL SITE NAME",
				DBCV, ASSIGNED, LOCATION,
				CHAR, "", TRIAL, TermId.CHARACTER_VARIABLE.getId()); 
		variable.setOperation(Operation.ADD);
		variable.setStoredIn(TermId.TRIAL_ENVIRONMENT_INFO_STORAGE.getId());
		conditions.add(variable);

		variable = createMeasurementVariable(TermId.LOCATION_ID.getId(), "SITE ID", "TRIAL SITE ID",
				DBID, ASSIGNED, LOCATION,
				NUMERIC, NUMERIC_VALUE, TRIAL, TermId.NUMERIC_VARIABLE.getId());
		variable.setOperation(Operation.ADD);
		variable.setStoredIn(TermId.TRIAL_ENVIRONMENT_INFO_STORAGE.getId());
		conditions.add(variable);
	}

	public static boolean areTrialVariablesSame(List<MeasurementVariable> trialVariablesTestData,
			List<MeasurementVariable> trialVariablesRetrieved) {
		
		if (trialVariablesTestData != null && trialVariablesRetrieved != null) {
			for (MeasurementVariable var : trialVariablesTestData){
				if (notInRetrievedList(var.getTermId(), trialVariablesRetrieved)) {
					return false;
				}
			} 
		}
		return true;
	}

	private static boolean notInRetrievedList(int termId, List<MeasurementVariable> trialVariablesRetrieved) {
		for (MeasurementVariable var : trialVariablesRetrieved) {
			if (termId == var.getTermId()) {
				return false;
			}
		}
		return true;
	}

	public static void addNewEnvironment(Workbook createdWorkbook) {
		addObservations(1, createdWorkbook.getTrialObservations());
		addObservations(1, createdWorkbook.getObservations());			
	}
	
	

	private static void addObservations(int newEnvironmentCount, List<MeasurementRow> observations) {		
		List<MeasurementRow> originalObservations = new ArrayList<MeasurementRow>(getFirstTrialInstance(observations));
		int currentObsCount = observations.size()/originalObservations.size();
		
		for (int i = 0; i < newEnvironmentCount; i++) {
			List<MeasurementRow> newInstance = new ArrayList<MeasurementRow>();
			for (MeasurementRow row : originalObservations) {
				newInstance.add(new MeasurementRow(row));
			}
			currentObsCount++;
			observations.addAll(setValuesPerInstance(newInstance, currentObsCount));
		}
		
		for (MeasurementRow row : observations) {
			for (MeasurementData data : row.getDataList()) {
				if (data.getMeasurementVariable().getTermId() == ASPERGILLUS_FLAVUS1_5 ||
						data.getMeasurementVariable().getTermId() == ASPERGILLUS_FLAVUSPPB) {
					data.setValue(String.valueOf(new Random().nextInt(10000)));
				}
			}
		}
	}

	private static List<MeasurementRow> getFirstTrialInstance(
			List<MeasurementRow> observations) {
		List<MeasurementRow> firstTrialInstance = new ArrayList<MeasurementRow>();
		long oldLocationId = 0;
		for (MeasurementRow row : observations) {
			long locationId = row.getLocationId();
			if (oldLocationId != locationId && oldLocationId != 0) {
				break;
			}
			firstTrialInstance.add(row);
			oldLocationId = locationId;
		}
		return firstTrialInstance;
	}

	private static List<MeasurementRow> setValuesPerInstance(List<MeasurementRow> newInstance, int currentObsCount) {
		for (MeasurementRow row : newInstance) {
			for (MeasurementData data : row.getDataList()) {
				if (data.getMeasurementVariable().getTermId() == TermId.TRIAL_INSTANCE_FACTOR.getId()) {
					data.setValue(String.valueOf(currentObsCount));
				}
				data.setPhenotypeId(null);
			}
			row.setExperimentId(0);
			row.setLocationId(0);
			row.setStockId(0);
		}
		return newInstance;
	}

	public static void deleteExperimentPropVar(Workbook createdWorkbook) {
		if (createdWorkbook.getFactors() != null) {
			for (MeasurementVariable var : createdWorkbook.getFactors()) {
				if (var.getTermId() == DAY_OBS) {
					var.setOperation(Operation.DELETE);
				}
			}
		}
	}
	
	private MeasurementVariable createMeasurementVariable(Integer termId){
		MeasurementVariable var = new MeasurementVariable();
		var.setTermId(termId);
		return var;
	}
	@Test
	public void testArrangeMeasurementVariables(){
		Workbook workbook = new Workbook();
		List<MeasurementVariable> varList = new ArrayList<MeasurementVariable>();
		varList.add(createMeasurementVariable(10));
		varList.add(createMeasurementVariable(20));
		varList.add(createMeasurementVariable(30));
		List<Integer> columnOrderedList = new ArrayList<Integer>();
		columnOrderedList.add(new Integer(20));
		columnOrderedList.add(new Integer(30));
		columnOrderedList.add(new Integer(10));
		workbook.setColumnOrderedLists(columnOrderedList);
		varList = workbook.arrangeMeasurementVariables(varList);
		
		Assert.assertEquals("1st element should have term id 20", 20, varList.get(0).getTermId());
		Assert.assertEquals("2nd element should have term id 30", 30, varList.get(1).getTermId());
		Assert.assertEquals("3rd element should have term id 10", 10, varList.get(2).getTermId());
	}
	
	private MeasurementData createMeasurementData(int termId){
		MeasurementData data = new MeasurementData();
		MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setTermId(termId);
		data.setMeasurementVariable(measurementVariable);
		return data;
	}
	@Test
	public void testArrangeMeasurementObservation(){
		Workbook workbook = new Workbook();
		List<MeasurementRow> observations = new ArrayList<MeasurementRow>();
		MeasurementRow row = new MeasurementRow();
		List<MeasurementData> dataList = new ArrayList<MeasurementData>();		
		dataList.add(createMeasurementData(10));
		dataList.add(createMeasurementData(20));
		dataList.add(createMeasurementData(30));
		row.setDataList(dataList);
		observations.add(row);
		
		List<Integer> columnOrderedList = new ArrayList<Integer>();
		columnOrderedList.add(new Integer(20));
		columnOrderedList.add(new Integer(30));
		columnOrderedList.add(new Integer(10));
		workbook.setColumnOrderedLists(columnOrderedList);
		List<MeasurementRow> newObservations = workbook.arrangeMeasurementObservation(observations);
		
		Assert.assertEquals("1st element should have term id 20", 20, newObservations.get(0).getDataList().get(0).getMeasurementVariable().getTermId());
		Assert.assertEquals("1st element should have term id 30", 30, newObservations.get(0).getDataList().get(1).getMeasurementVariable().getTermId());
		Assert.assertEquals("1st element should have term id 10", 10, newObservations.get(0).getDataList().get(2).getMeasurementVariable().getTermId());
	}
	
	public static Workbook getTestWorkbookWithErrors(){
		return createTestWorkbookWithErrors();
	}
	
	private static Workbook createTestWorkbookWithErrors(){
		Workbook workbook = new Workbook();
		
		String studyName = "workbookWithErrors" + new Random().nextInt(10000);
		createStudyDetails(workbook,studyName,StudyType.T);
		createConditions(workbook,false,1);
		createFactors(workbook,false,false,1);
		createConstants(workbook);
		createVariatesWithDuplicatePSM(workbook);
		createObservations(workbook,10,false,1);
		
		return workbook;
	}
	
	public static Workbook getTestWorkbookForWizard(String studyName,int trialNo) {
		return createTestWorkbookForWizard(studyName,trialNo);
	}
	
	public static Workbook createTestWorkbookForWizard(String studyName, int trialNo){
		Workbook wbook = new Workbook();
		
		createStudyDetails(wbook, studyName, StudyType.T);
		createConditions(wbook, false, trialNo);
		createFactors(wbook,true,true,trialNo);
		createConstants(wbook);
		createVariates(wbook);
		createObservations(wbook,10,true,trialNo);
		
		return wbook;
	}

}
