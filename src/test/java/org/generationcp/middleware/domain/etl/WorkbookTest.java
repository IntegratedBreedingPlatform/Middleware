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

import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.Location;

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

	private static Workbook workbook;
	
	public static Workbook getTestWorkbook() {
		if (workbook == null) {
			createTestWorkbook(DEFAULT_NO_OF_OBSERVATIONS, StudyType.N);
		}
		return workbook;
	}

	public static Workbook getTestWorkbook(int noOfObservations, StudyType studyType) {
		if (workbook == null) {
			createTestWorkbook(noOfObservations, studyType);
		}
		return workbook;
	}
	
	public static void setTestWorkbook(Workbook workbookNew) {
		workbook = workbookNew;
	}

	private static void createTestWorkbook(int noOfObservations, StudyType studyType) {
		workbook = new Workbook();

		createStudyDetails(studyType);
		createConditions();
		createFactors();
		createConstants();
		createVariates();
		createObservations(noOfObservations);
	}

	private static void createStudyDetails(StudyType studyType) {
		StudyDetails details = new StudyDetails();
		details.setStudyName((studyType.equals(StudyType.N) ? NURSERY_NAME : TRIAL_NAME) + new Random().nextInt(10000));
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

	private static void createConditions() {
		//Create measurement variables and set its dataTypeId
		List<MeasurementVariable> conditions = new ArrayList<MeasurementVariable>();

		conditions.add(createMeasurementVariable(TermId.TRIAL_INSTANCE_FACTOR.getId(), 
				"TRIAL", "TRIAL NUMBER", NUMBER, ENUMERATED, TRIAL_INSTANCE, NUMERIC, "", 
				TRIAL, TermId.CHARACTER_VARIABLE.getId()));
		
		conditions.add(createMeasurementVariable(TermId.PI_NAME.getId(), "PI Name",
				"Name of Principal Investigator", DBCV, ASSIGNED,
				PERSON, CHAR, "", STUDY, TermId.CHARACTER_VARIABLE.getId()));

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

		workbook.setConditions(conditions);
	}

	private static void createFactors() {
		//Create measurement variables and set its dataTypeId
		List<MeasurementVariable> factors = new ArrayList<MeasurementVariable>();
		
		// Entry Factors
		factors.add(createMeasurementVariable(TermId.ENTRY_NO.getId(),
				ENTRY, "The germplasm entry number", NUMBER,
				ENUMERATED, GERMPLASM_ENTRY,
				NUMERIC, STUDY, ENTRY, TermId.CHARACTER_VARIABLE.getId()));

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

	private static void createConstants() {
		//Create measurement variables and set its dataTypeId
		List<MeasurementVariable> constants = new ArrayList<MeasurementVariable>();

		constants.add(createMeasurementVariable(GRAIN_SIZE_ID,
				"Grain_size", "Grain size - weigh 1000 dry grains (g)", GRAIN_SIZE_SCALE, DRY_GRAINS,
				GRAIN_SIZE_PROPERTY, NUMERIC,
				NUMERIC_VALUE, TRIAL, TermId.NUMERIC_VARIABLE.getId()));

		workbook.setConstants(constants);
	}

	private static void createVariates() {
		//Create measurement variables and set its dataTypeId
		List<MeasurementVariable> variates = new ArrayList<MeasurementVariable>();
		MeasurementVariable measurementVariable = createMeasurementVariable(GYLD_ID,
				GYLD, "Grain yield -dry and weigh (kg/ha)",
				KG_HA, DRY_AND_WEIGH, YIELD,
				NUMERIC, NUMERIC_VALUE, PLOT, TermId.NUMERIC_VARIABLE.getId());
		measurementVariable.setStoredIn(TermId.OBSERVATION_VARIATE.getId());
		variates.add(measurementVariable);
		
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

	private static void createObservations(int noOfObservations) {
		List<MeasurementRow> observations = new ArrayList<MeasurementRow>();

		MeasurementRow row;
		List<MeasurementData> dataList;
		Random random = new Random();
		DecimalFormat fmt = new DecimalFormat("#.##");

		//Create n number of observation rows
		for (int i = 0; i < noOfObservations; i++) {
			row = new MeasurementRow();
			dataList = new ArrayList<MeasurementData>();

			
			dataList.add(createMeasurementData(ENTRY, String.valueOf(i), TermId.ENTRY_NO.getId(), workbook.getFactors()));
			dataList.add(createMeasurementData(GID, computeGID(i), TermId.GID.getId(), workbook.getFactors()));
			dataList.add(createMeasurementData(DESIG, GERMPLASM_NAME + new Random().nextInt(10000), 
					TermId.DESIG.getId(), workbook.getFactors()));
			dataList.add(createMeasurementData(CROSS, "-", TermId.CROSS.getId(), workbook.getFactors()));
			dataList.add(createMeasurementData(SOURCE, "-", TermId.SEED_SOURCE.getId(), workbook.getFactors()));
			dataList.add(createMeasurementData(PLOT, String.valueOf(i), TermId.PLOT_NO.getId(), workbook.getFactors()));
			dataList.add(createMeasurementData(BLOCK, "", TermId.BLOCK_NO.getId(), workbook.getFactors()));
			dataList.add(createMeasurementData(REP, "", TermId.REP_NO.getId(), workbook.getFactors()));
			dataList.add(createMeasurementData("DAY_OBS", randomizeValue(random, fmt, 5000), DAY_OBS, workbook.getFactors()));
			dataList.add(createMeasurementData(GYLD, randomizeValue(random, fmt, 5000), GYLD_ID, workbook.getVariates()));
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

	private static MeasurementData createMeasurementData(String label, String value, int termId, List<MeasurementVariable> variables) {
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
		gid += i;
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
		workbook = new Workbook();

		createStudyDetails(StudyType.T);
		createConditions();
		
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

}
