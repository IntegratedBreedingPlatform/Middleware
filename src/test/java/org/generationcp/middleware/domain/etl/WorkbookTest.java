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

package org.generationcp.middleware.domain.etl;

import static org.junit.Assert.assertEquals;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.Location;
import org.junit.Assert;
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
	private static final String MEASURED = "MEASURED";

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
	private static final int SITE_SOIL_PH_ID = 8270;
	private static final String SITE_SOIL_PH = "SITE_SOIL_PH";

	public static final String[] G_NAMES = {"TIANDOUGOU-9", "KENINKENI-27", "SM114-1A-1-1-1B", "SM114-1A-14-1-1B", "SM114-1A-361-1-1B",
			"SM114-1A-86-1-1B", "SM114-1A-115-1-1B", "SM114-1A-281-1-1B", "SM114-1A-134-1-1B", "SM114-1A-69-1-1B", "SM114-1A-157-1-1B",
			"SM114-1A-179-1-1B", "TIANDOUGOU-9", "SM114-1A-36-1-1B", "SM114-1A-201-1-1B", "SM114-1A-31-1-1B", "SM114-1A-353-1-1B",
			"SM114-1A-26-1-1B", "SM114-1A-125-1-1B", "SM114-1A-384-1-1B"};

	private static Workbook workbook;
	private static List<Workbook> workbooks;

	public static Workbook getTestWorkbook() {
		return WorkbookTest.createTestWorkbook(WorkbookTest.DEFAULT_NO_OF_OBSERVATIONS, StudyType.N, null, 1, false);
	}

	public static Workbook getTestWorkbook(int noOfObservations, StudyType studyType) {
		return WorkbookTest.createTestWorkbook(noOfObservations, studyType, null, 1, false);
	}

	public static List<Workbook> getTestWorkbooks(int noOfTrial, int noOfObservations) {
		if (WorkbookTest.workbooks == null) {
			WorkbookTest.workbooks = new ArrayList<Workbook>();
			String studyName = "pheno_t7" + new Random().nextInt(10000);
			for (int i = 1; i <= noOfTrial; i++) {
				WorkbookTest.workbooks.add(WorkbookTest.createTestWorkbook(noOfObservations, StudyType.T, studyName, i, true));
			}
		}
		return WorkbookTest.workbooks;
	}

	public static void setTestWorkbook(Workbook workbookNew) {
		WorkbookTest.workbook = workbookNew;
	}

	private static Workbook createTestWorkbook(int noOfObservations, StudyType studyType, String studyName, int trialNo,
			boolean hasMultipleLocations) {
		WorkbookTest.workbook = new Workbook();

		WorkbookTest.createStudyDetails(WorkbookTest.workbook, studyName, studyType);
		WorkbookTest.createConditions(WorkbookTest.workbook, !hasMultipleLocations, trialNo);
		WorkbookTest.createFactors(WorkbookTest.workbook, true, hasMultipleLocations, trialNo);
		WorkbookTest.createConstants(WorkbookTest.workbook);
		WorkbookTest.createVariates(WorkbookTest.workbook);
		WorkbookTest.createObservations(WorkbookTest.workbook, noOfObservations, hasMultipleLocations, trialNo);

		return WorkbookTest.workbook;
	}

	private static void createStudyDetails(Workbook workbook, String studyName, StudyType studyType) {
		StudyDetails details = new StudyDetails();
		if (studyName != null) {
			// this is used for adding multiple locations to one study
			details.setStudyName(studyName);
		} else {
			details.setStudyName((studyType.equals(StudyType.N) ? WorkbookTest.NURSERY_NAME : WorkbookTest.TRIAL_NAME)
					+ new Random().nextInt(10000));
		}
		details.setTitle(WorkbookTest.TITLE);
		details.setObjective(WorkbookTest.OBJECTIVE);
		details.setStartDate(WorkbookTest.START_DATE);
		details.setEndDate(WorkbookTest.END_DATE);
		details.setParentFolderId(WorkbookTest.FOLDER_ID);
		details.setStudyType(studyType);

		workbook.setStudyDetails(details);
	}

	private static MeasurementVariable createMeasurementVariable(int termId, String name, String description, String scale, String method,
			String property, String dataType, String value, String label, int dataTypeId, PhenotypicType role) {
		MeasurementVariable variable =
				new MeasurementVariable(termId, name, description, scale, description, property, dataType, value, label);
		variable.setRole(role);
		variable.setDataTypeId(dataTypeId);
		return variable;
	}

	public static MeasurementVariable createTrialInstanceMeasurementVariable(int trialNo) {
		return WorkbookTest.createMeasurementVariable(TermId.TRIAL_INSTANCE_FACTOR.getId(), "TRIAL", "TRIAL NUMBER", WorkbookTest.NUMBER,
				WorkbookTest.ENUMERATED, WorkbookTest.TRIAL_INSTANCE, WorkbookTest.NUMERIC, String.valueOf(trialNo), WorkbookTest.TRIAL,
				TermId.CHARACTER_VARIABLE.getId(), PhenotypicType.TRIAL_ENVIRONMENT);
	}

	private static void createConditions(Workbook workbook, boolean withTrial, int trialNo) {
		// Create measurement variables and set its dataTypeId
		List<MeasurementVariable> conditions = new ArrayList<MeasurementVariable>();

		if (withTrial) {
			conditions.add(WorkbookTest.createTrialInstanceMeasurementVariable(trialNo));
		}

		conditions.add(WorkbookTest.createMeasurementVariable(TermId.PI_NAME.getId(), "PI Name", "Name of Principal Investigator",
				WorkbookTest.DBCV, WorkbookTest.ASSIGNED, WorkbookTest.PERSON, WorkbookTest.CHAR, "PI Name Value", WorkbookTest.STUDY,
				TermId.CHARACTER_VARIABLE.getId(), PhenotypicType.STUDY));

		conditions.add(WorkbookTest.createMeasurementVariable(TermId.PI_ID.getId(), "PI ID", "ID of Principal Investigator",
				WorkbookTest.DBID, WorkbookTest.ASSIGNED, WorkbookTest.PERSON, WorkbookTest.NUMERIC, WorkbookTest.NUMERIC_VALUE,
				WorkbookTest.STUDY, TermId.NUMERIC_VARIABLE.getId(), PhenotypicType.STUDY));

		conditions.add(WorkbookTest.createMeasurementVariable(WorkbookTest.COOPERATOR_NAME_ID, "COOPERATOR", "COOPERATOR NAME",
				WorkbookTest.DBCV, WorkbookTest.CONDUCTED, WorkbookTest.PERSON, WorkbookTest.CHAR, "John Smith", WorkbookTest.TRIAL,
				TermId.CHARACTER_VARIABLE.getId(), PhenotypicType.STUDY));

		conditions.add(WorkbookTest.createMeasurementVariable(WorkbookTest.COOPERATOR_ID_ID, "COOPERATOR ID", "COOPERATOR ID",
				WorkbookTest.DBID, WorkbookTest.CONDUCTED, WorkbookTest.PERSON, WorkbookTest.NUMERIC, WorkbookTest.NUMERIC_VALUE,
				WorkbookTest.TRIAL, TermId.NUMERIC_VARIABLE.getId(), PhenotypicType.STUDY));

		conditions.add(WorkbookTest.createMeasurementVariable(WorkbookTest.LOCATION_NAME_ID, "SITE", "TRIAL SITE NAME", WorkbookTest.DBCV,
				WorkbookTest.ASSIGNED, WorkbookTest.LOCATION, WorkbookTest.CHAR, "SITE " + trialNo, WorkbookTest.TRIAL,
				TermId.CHARACTER_VARIABLE.getId(), PhenotypicType.TRIAL_ENVIRONMENT));

		conditions.add(WorkbookTest.createMeasurementVariable(WorkbookTest.LOCATION_ID_ID, "SITE ID", "TRIAL SITE ID", WorkbookTest.DBID,
				WorkbookTest.ASSIGNED, WorkbookTest.LOCATION, WorkbookTest.NUMERIC, String.valueOf(trialNo), WorkbookTest.TRIAL,
				TermId.NUMERIC_VARIABLE.getId(), PhenotypicType.TRIAL_ENVIRONMENT));

		conditions.add(WorkbookTest.createMeasurementVariable(WorkbookTest.EXPT_DESIGN_ID, "DESIGN", "EXPERIMENTAL DESIGN",
				WorkbookTest.TYPE, WorkbookTest.ASSIGNED, WorkbookTest.EXPERIMENT_DESIGN, WorkbookTest.CHAR,
				String.valueOf(TermId.RANDOMIZED_COMPLETE_BLOCK.getId()), WorkbookTest.TRIAL, TermId.CHARACTER_VARIABLE.getId(),
				PhenotypicType.TRIAL_ENVIRONMENT));

		workbook.setConditions(conditions);
	}

	private static void createFactors(Workbook workbook, boolean withEntry, boolean withTrial, int trialNo) {
		// Create measurement variables and set its dataTypeId
		List<MeasurementVariable> factors = new ArrayList<MeasurementVariable>();

		if (withTrial) {
			factors.add(WorkbookTest.createTrialInstanceMeasurementVariable(trialNo));
		}

		// Entry Factors
		if (withEntry) {
			factors.add(WorkbookTest.createMeasurementVariable(TermId.ENTRY_NO.getId(), WorkbookTest.ENTRY, "The germplasm entry number",
					WorkbookTest.NUMBER, WorkbookTest.ENUMERATED, WorkbookTest.GERMPLASM_ENTRY, WorkbookTest.NUMERIC, WorkbookTest.STUDY,
					WorkbookTest.ENTRY, TermId.NUMERIC_VARIABLE.getId(), PhenotypicType.GERMPLASM));
		}

		factors.add(WorkbookTest.createMeasurementVariable(TermId.GID.getId(), WorkbookTest.GID, "The GID of the germplasm",
				WorkbookTest.DBID, WorkbookTest.ASSIGNED, WorkbookTest.GERMPLASM_ID, WorkbookTest.NUMERIC, WorkbookTest.NUMERIC_VALUE,
				WorkbookTest.ENTRY, TermId.NUMERIC_VARIABLE.getId(), PhenotypicType.GERMPLASM));

		factors.add(WorkbookTest.createMeasurementVariable(TermId.DESIG.getId(), WorkbookTest.DESIG, "The name of the germplasm",
				WorkbookTest.DBCV, WorkbookTest.ASSIGNED, WorkbookTest.GERMPLASM_ID, WorkbookTest.CHAR, WorkbookTest.STUDY,
				WorkbookTest.ENTRY, TermId.CHARACTER_VARIABLE.getId(), PhenotypicType.GERMPLASM));

		factors.add(WorkbookTest.createMeasurementVariable(TermId.CROSS.getId(), WorkbookTest.CROSS,
				"The pedigree string of the germplasm", WorkbookTest.PEDIGREE_STRING, WorkbookTest.ASSIGNED, WorkbookTest.CROSS_HISTORY,
				WorkbookTest.CHAR, WorkbookTest.STUDY, WorkbookTest.ENTRY, TermId.CHARACTER_VARIABLE.getId(), PhenotypicType.GERMPLASM));

		factors.add(WorkbookTest.createMeasurementVariable(TermId.SEED_SOURCE.getId(), WorkbookTest.SEED_SOURCE,
				"The seed source of the germplasm", WorkbookTest.NAME, WorkbookTest.SELECTED, WorkbookTest.SEED_SOURCE, WorkbookTest.CHAR,
				WorkbookTest.STUDY, WorkbookTest.ENTRY, TermId.CHARACTER_VARIABLE.getId(), PhenotypicType.GERMPLASM));

		factors.add(WorkbookTest.createMeasurementVariable(TermId.PLOT_NO.getId(), WorkbookTest.PLOT, "Plot number ",
				WorkbookTest.NESTED_NUMBER, WorkbookTest.ENUMERATED, WorkbookTest.FIELD_PLOT, WorkbookTest.NUMERIC,
				WorkbookTest.NUMERIC_VALUE, WorkbookTest.PLOT, TermId.NUMERIC_VARIABLE.getId(), PhenotypicType.TRIAL_DESIGN));

		// Plot Factors
		factors.add(WorkbookTest.createMeasurementVariable(TermId.BLOCK_NO.getId(), WorkbookTest.BLOCK, "INCOMPLETE BLOCK",
				WorkbookTest.NUMBER, WorkbookTest.ENUMERATED, WorkbookTest.BLOCKING_FACTOR, WorkbookTest.NUMERIC,
				WorkbookTest.NUMERIC_VALUE, WorkbookTest.PLOT, TermId.NUMERIC_VARIABLE.getId(), PhenotypicType.TRIAL_DESIGN));

		factors.add(WorkbookTest.createMeasurementVariable(TermId.REP_NO.getId(), WorkbookTest.REP, WorkbookTest.REPLICATION,
				WorkbookTest.NUMBER, WorkbookTest.ENUMERATED, WorkbookTest.REPLICATION_FACTOR, WorkbookTest.NUMERIC,
				WorkbookTest.NUMERIC_VALUE, WorkbookTest.PLOT, TermId.NUMERIC_VARIABLE.getId(), PhenotypicType.TRIAL_DESIGN));

		factors.add(WorkbookTest.createMeasurementVariable(WorkbookTest.DAY_OBS, "DAY_OBS", WorkbookTest.REPLICATION, WorkbookTest.NUMBER,
				WorkbookTest.ENUMERATED, WorkbookTest.REPLICATION_FACTOR, WorkbookTest.NUMERIC, WorkbookTest.NUMERIC_VALUE,
				WorkbookTest.PLOT, TermId.NUMERIC_VARIABLE.getId(), PhenotypicType.TRIAL_DESIGN));

		workbook.setFactors(factors);
	}

	private static void createConstants(Workbook workbook) {
		// Create measurement variables and set its dataTypeId
		List<MeasurementVariable> constants = new ArrayList<MeasurementVariable>();

		constants.add(WorkbookTest.createMeasurementVariable(WorkbookTest.GRAIN_SIZE_ID, "Grain_size",
				"Grain size - weigh 1000 dry grains (g)", WorkbookTest.GRAIN_SIZE_SCALE, WorkbookTest.DRY_GRAINS,
				WorkbookTest.GRAIN_SIZE_PROPERTY, WorkbookTest.NUMERIC, WorkbookTest.NUMERIC_VALUE, WorkbookTest.TRIAL,
				TermId.NUMERIC_VARIABLE.getId(), PhenotypicType.VARIATE));
		workbook.setConstants(constants);
	}

	private static void createVariates(Workbook workbook) {
		// Create measurement variables and set its dataTypeId
		List<MeasurementVariable> variates = new ArrayList<MeasurementVariable>();
		MeasurementVariable measurementVariable =
				WorkbookTest.createMeasurementVariable(WorkbookTest.GYLD_ID, WorkbookTest.GYLD, "Grain yield -dry and weigh (kg/ha)",
						WorkbookTest.KG_HA, WorkbookTest.DRY_AND_WEIGH, WorkbookTest.YIELD, WorkbookTest.NUMERIC,
						WorkbookTest.NUMERIC_VALUE, WorkbookTest.PLOT, TermId.NUMERIC_VARIABLE.getId(), PhenotypicType.VARIATE);

		variates.add(measurementVariable);

		MeasurementVariable siteSoilPh =
				WorkbookTest.createMeasurementVariable(WorkbookTest.SITE_SOIL_PH_ID, WorkbookTest.SITE_SOIL_PH,
						"Soil acidity - ph meter (pH)", WorkbookTest.PH, WorkbookTest.MEASURED, WorkbookTest.SOIL_ACIDITY,
						WorkbookTest.NUMERIC, WorkbookTest.NUMERIC_VALUE, WorkbookTest.STUDY, TermId.NUMERIC_VARIABLE.getId(),
						PhenotypicType.VARIATE);
		variates.add(siteSoilPh);
		workbook.setVariates(variates);
	}

	private static void createVariatesWithDuplicatePSM(Workbook workbook) {
		List<MeasurementVariable> variates = new ArrayList<MeasurementVariable>();

		MeasurementVariable gyld =
				WorkbookTest.createMeasurementVariable(WorkbookTest.GYLD_ID, WorkbookTest.GYLD, "Grain yield -dry and weigh (kg/ha)",
						WorkbookTest.KG_HA, WorkbookTest.DRY_AND_WEIGH, WorkbookTest.YIELD, WorkbookTest.NUMERIC,
						WorkbookTest.NUMERIC_VALUE, WorkbookTest.PLOT, TermId.NUMERIC_VARIABLE.getId(), PhenotypicType.VARIATE);

		variates.add(gyld);

		MeasurementVariable siteSoilPh =
				WorkbookTest.createMeasurementVariable(WorkbookTest.SITE_SOIL_PH_ID, WorkbookTest.SITE_SOIL_PH,
						"Soil acidity - ph meter (pH)", WorkbookTest.PH, WorkbookTest.MEASURED, WorkbookTest.SOIL_ACIDITY,
						WorkbookTest.NUMERIC, WorkbookTest.NUMERIC_VALUE, WorkbookTest.PLOT, TermId.NUMERIC_VARIABLE.getId(),
						PhenotypicType.VARIATE);
		variates.add(siteSoilPh);

		workbook.setVariates(variates);
	}

	public static void addVariatesAndObservations(Workbook currentWorkbook) {

		List<MeasurementVariable> variates = currentWorkbook.getVariates();
		MeasurementVariable measurementVariable =
				WorkbookTest
						.createMeasurementVariable(
								WorkbookTest.CRUST_ID,
								WorkbookTest.CRUST,
								"Score for the severity of common rust, (In highlands and mid altitude, Puccinia sorghi) symptoms rated on a scale from 1 (= clean, no infection) to 5 (= severely diseased).",
								WorkbookTest.SCORE_1_5, WorkbookTest.VISUAL_SCORING, WorkbookTest.COMMON_RUST, WorkbookTest.CHAR, null,
								WorkbookTest.PLOT, TermId.CATEGORICAL_VARIABLE.getId(), PhenotypicType.VARIATE);

		measurementVariable.setOperation(Operation.ADD);

		variates.add(measurementVariable);

		WorkbookTest.addObservations(currentWorkbook);
	}

	private static void createObservations(Workbook workbook, int noOfObservations, boolean withTrial, int trialNo) {
		List<MeasurementRow> observations = new ArrayList<MeasurementRow>();

		MeasurementRow row;
		List<MeasurementData> dataList;
		Random random = new Random();
		DecimalFormat fmt = new DecimalFormat("#.##");

		// Create n number of observation rows
		for (int i = 0; i < noOfObservations; i++) {
			row = new MeasurementRow();
			dataList = new ArrayList<MeasurementData>();
			if (withTrial) {
				dataList.add(WorkbookTest.createMeasurementData(WorkbookTest.TRIAL, String.valueOf(trialNo),
						TermId.TRIAL_INSTANCE_FACTOR.getId(), workbook.getFactors()));
			}

			dataList.add(WorkbookTest.createMeasurementData(WorkbookTest.ENTRY, String.valueOf(i), TermId.ENTRY_NO.getId(),
					workbook.getFactors()));
			dataList.add(WorkbookTest.createMeasurementData(WorkbookTest.GID, WorkbookTest.computeGID(i), TermId.GID.getId(),
					workbook.getFactors()));
			dataList.add(WorkbookTest.createMeasurementData(WorkbookTest.DESIG, WorkbookTest.G_NAMES[i], TermId.DESIG.getId(),
					workbook.getFactors()));
			dataList.add(WorkbookTest.createMeasurementData(WorkbookTest.CROSS, "-", TermId.CROSS.getId(), workbook.getFactors()));
			dataList.add(WorkbookTest.createMeasurementData(WorkbookTest.SOURCE, "-", TermId.SEED_SOURCE.getId(), workbook.getFactors()));
			dataList.add(WorkbookTest.createMeasurementData(WorkbookTest.PLOT, String.valueOf(i), TermId.PLOT_NO.getId(),
					workbook.getFactors()));
			dataList.add(WorkbookTest.createMeasurementData(WorkbookTest.BLOCK, "", TermId.BLOCK_NO.getId(), workbook.getFactors()));
			dataList.add(WorkbookTest.createMeasurementData(WorkbookTest.REP, "", TermId.REP_NO.getId(), workbook.getFactors()));
			dataList.add(WorkbookTest.createMeasurementData("DAY_OBS", WorkbookTest.randomizeValue(random, fmt, 5000),
					WorkbookTest.DAY_OBS, workbook.getFactors()));
			dataList.add(WorkbookTest.createMeasurementData(WorkbookTest.GYLD, WorkbookTest.randomizeValue(random, fmt, 5000),
					WorkbookTest.GYLD_ID, workbook.getVariates()));
			dataList.add(WorkbookTest.createMeasurementData(WorkbookTest.SITE_SOIL_PH, "1", WorkbookTest.SITE_SOIL_PH_ID,
					workbook.getVariates()));
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

		// Create n number of observation rows
		for (int i = 0; i < observations.size(); i++) {
			row = observations.get(i);
			dataList = row.getDataList();

			String crustValue = WorkbookTest.randomizeValue(random, fmt, 5000);
			String crustCValueId = null;
			switch (i) {
				case 0:
					crustValue = "";
					break;
				case 1:
					crustValue = null;
					break;
				case 2:
					crustValue = WorkbookTest.VALID_CRUST_VALUE;
					crustCValueId = WorkbookTest.VALID_CRUST_CVALUE_ID;
					break;
				case 3:
					crustValue = WorkbookTest.INVALID_CRUST_VALUE_NUMERIC;
					break;
				case 4:
					crustValue = WorkbookTest.INVALID_CRUST_VALUE_CHAR;
					break;
				case 5:
					crustValue = WorkbookTest.INVALID_CRUST_VALUE_MISSING;
					break;
			}
			MeasurementData measurementData =
					WorkbookTest.createMeasurementData(WorkbookTest.CRUST, crustValue, WorkbookTest.CRUST_ID,
							WorkbookTest.workbook.getVariates());
			measurementData.setcValueId(crustCValueId);
			dataList.add(measurementData);
		}
	}

	public static MeasurementData createMeasurementData(String label, String value, int termId, List<MeasurementVariable> variables) {
		MeasurementData data = new MeasurementData(label, value);
		data.setMeasurementVariable(WorkbookTest.getMeasurementVariable(termId, variables));
		return data;
	}

	private static MeasurementVariable getMeasurementVariable(int termId, List<MeasurementVariable> variables) {
		if (variables != null) {
			// get matching MeasurementVariable object given the term id
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
		if (i < 13) {
			gid += i - 1;
		} else if (i > 13) {
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
		locations.add(new Location(WorkbookTest.LOCATION_ID_1, WorkbookTest.LTYPE, WorkbookTest.NLLP, WorkbookTest.LNAME + " 1",
				WorkbookTest.LABBR, WorkbookTest.SNL3ID, WorkbookTest.SNL2ID, WorkbookTest.SNL1ID, WorkbookTest.CNTRYID,
				WorkbookTest.LRPLCE));
		locations.add(new Location(WorkbookTest.LOCATION_ID_2, WorkbookTest.LTYPE, WorkbookTest.NLLP, WorkbookTest.LNAME + " 2",
				WorkbookTest.LABBR, WorkbookTest.SNL3ID, WorkbookTest.SNL2ID, WorkbookTest.SNL1ID, WorkbookTest.CNTRYID,
				WorkbookTest.LRPLCE));
		return locations;
	}

	public static MeasurementRow createTrialObservationWithoutSite() {
		Workbook workbook = new Workbook();

		WorkbookTest.createStudyDetails(workbook, null, StudyType.T);
		WorkbookTest.createConditions(workbook, true, 1);

		MeasurementRow row = new MeasurementRow();
		List<MeasurementData> dataList = new ArrayList<MeasurementData>();

		dataList.add(WorkbookTest.createMeasurementData(WorkbookTest.TRIAL_INSTANCE, WorkbookTest.NUMERIC_VALUE,
				TermId.TRIAL_INSTANCE_FACTOR.getId(), workbook.getConditions()));
		dataList.add(WorkbookTest.createMeasurementData(WorkbookTest.PI_NAME, "", TermId.PI_NAME.getId(), workbook.getConditions()));
		dataList.add(WorkbookTest.createMeasurementData(WorkbookTest.PI_ID, "", TermId.PI_ID.getId(), workbook.getConditions()));
		dataList.add(WorkbookTest.createMeasurementData(WorkbookTest.COOPERATOR, "", WorkbookTest.COOPERATOR_NAME_ID,
				workbook.getConditions()));
		dataList.add(WorkbookTest.createMeasurementData(WorkbookTest.COOPERATOR_ID, "", WorkbookTest.COOPERATOR_ID_ID,
				workbook.getConditions()));

		row.setDataList(dataList);
		return row;
	}

	public static Workbook addEnvironmentAndConstantVariables(Workbook createdWorkbook) {
		WorkbookTest.addConditions(createdWorkbook.getConditions());
		WorkbookTest.addConstants(createdWorkbook.getConstants());
		return createdWorkbook;
	}

	private static void addConstants(List<MeasurementVariable> constants) {
		MeasurementVariable variable =
				WorkbookTest.createMeasurementVariable(WorkbookTest.SOILPH_ID, "SITE_SOIL_PH", "Soil acidity - ph meter (pH)",
						WorkbookTest.PH, WorkbookTest.PH_METER, WorkbookTest.SOIL_ACIDITY, WorkbookTest.NUMERIC,
						WorkbookTest.NUMERIC_VALUE, WorkbookTest.TRIAL, TermId.NUMERIC_VARIABLE.getId(), PhenotypicType.VARIATE);
		variable.setOperation(Operation.ADD);
		constants.add(variable);
	}

	private static void addConditions(List<MeasurementVariable> conditions) {
		MeasurementVariable variable =
				WorkbookTest.createMeasurementVariable(TermId.TRIAL_LOCATION.getId(), "SITE", "TRIAL SITE NAME", WorkbookTest.DBCV,
						WorkbookTest.ASSIGNED, WorkbookTest.LOCATION, WorkbookTest.CHAR, "", WorkbookTest.TRIAL,
						TermId.CHARACTER_VARIABLE.getId(), PhenotypicType.TRIAL_ENVIRONMENT);
		variable.setOperation(Operation.ADD);
		conditions.add(variable);

		variable =
				WorkbookTest.createMeasurementVariable(TermId.LOCATION_ID.getId(), "SITE ID", "TRIAL SITE ID", WorkbookTest.DBID,
						WorkbookTest.ASSIGNED, WorkbookTest.LOCATION, WorkbookTest.NUMERIC, WorkbookTest.NUMERIC_VALUE, WorkbookTest.TRIAL,
						TermId.NUMERIC_VARIABLE.getId(), PhenotypicType.TRIAL_ENVIRONMENT);
		variable.setOperation(Operation.ADD);
		conditions.add(variable);
	}

	public static boolean areTrialVariablesSame(List<MeasurementVariable> trialVariablesTestData,
			List<MeasurementVariable> trialVariablesRetrieved) {

		if (trialVariablesTestData != null && trialVariablesRetrieved != null) {
			for (MeasurementVariable var : trialVariablesTestData) {
				if (WorkbookTest.notInRetrievedList(var.getTermId(), trialVariablesRetrieved)) {
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
		WorkbookTest.addObservations(1, createdWorkbook.getTrialObservations());
		WorkbookTest.addObservations(1, createdWorkbook.getObservations());
	}

	private static void addObservations(int newEnvironmentCount, List<MeasurementRow> observations) {
		List<MeasurementRow> originalObservations = new ArrayList<MeasurementRow>(WorkbookTest.getFirstTrialInstance(observations));
		int currentObsCount = observations.size() / originalObservations.size();

		for (int i = 0; i < newEnvironmentCount; i++) {
			List<MeasurementRow> newInstance = new ArrayList<MeasurementRow>();
			for (MeasurementRow row : originalObservations) {
				newInstance.add(new MeasurementRow(row));
			}
			currentObsCount++;
			observations.addAll(WorkbookTest.setValuesPerInstance(newInstance, currentObsCount));
		}

		for (MeasurementRow row : observations) {
			for (MeasurementData data : row.getDataList()) {
				if (data.getMeasurementVariable().getTermId() == WorkbookTest.ASPERGILLUS_FLAVUS1_5
						|| data.getMeasurementVariable().getTermId() == WorkbookTest.ASPERGILLUS_FLAVUSPPB) {
					data.setValue(String.valueOf(new Random().nextInt(10000)));
				}
			}
		}
	}

	private static List<MeasurementRow> getFirstTrialInstance(List<MeasurementRow> observations) {
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
				if (var.getTermId() == WorkbookTest.DAY_OBS) {
					var.setOperation(Operation.DELETE);
				}
			}
		}
	}

	private MeasurementVariable createMeasurementVariable(Integer termId) {
		MeasurementVariable var = new MeasurementVariable();
		var.setTermId(termId);
		return var;
	}

	@Test
	public void testGetMeasurementDatasetVariablesViewForTrial() {
		WorkbookTest.getTestWorkbook(1, StudyType.T);

		List<MeasurementVariable> list = workbook.getMeasurementDatasetVariablesView();

		List<MeasurementVariable> factors = workbook.getFactors();
		List<MeasurementVariable> variates = workbook.getVariates();

		int noOfFactors = factors.size();
		int noOfVariates = variates.size();
		int totalMeasurementVariableCount = noOfFactors + noOfVariates;

		Assert.assertEquals(
				"MeasurementDatasetVariablesView size should be the total no of non trial factors, variates and trial_instance",
				totalMeasurementVariableCount + 1, list.size());

		// Testing the order of the variables
		Assert.assertEquals("Expecting that the TRIAL_INSTANCE is the first variable from the list.", 8170, list.get(0).getTermId());
		for (int i = 1; i < totalMeasurementVariableCount; i++) {
			if (i < noOfFactors) {
				Assert.assertEquals("Expecting the next variables are of factors type.", list.get(i + 1).getTermId(), factors.get(i)
						.getTermId());
			} else if (i + 1 < totalMeasurementVariableCount) {
				Assert.assertEquals("Expecting the next variables are of variates type.", list.get(i + 1).getTermId(),
						variates.get(i - noOfFactors).getTermId());
			}
		}
	}

	@Test
	public void testGetMeasurementDatasetVariablesViewForNursery() {

		WorkbookTest.getTestWorkbook(1, StudyType.N);

		List<MeasurementVariable> list = workbook.getMeasurementDatasetVariablesView();

		int totalMeasurementVariableCount = workbook.getFactors().size() + workbook.getVariates().size();

		Assert.assertEquals("MeasurementDatasetVariablesView size should be the total no of non trial factors, variates",
				totalMeasurementVariableCount, list.size());

	}

	@Test
	public void testArrangeMeasurementVariables() {
		Workbook workbook = new Workbook();
		List<MeasurementVariable> varList = new ArrayList<MeasurementVariable>();
		varList.add(this.createMeasurementVariable(10));
		varList.add(this.createMeasurementVariable(20));
		varList.add(this.createMeasurementVariable(30));
		List<Integer> columnOrderedList = new ArrayList<Integer>();
		columnOrderedList.add(new Integer(20));
		columnOrderedList.add(new Integer(30));
		columnOrderedList.add(new Integer(10));
		workbook.setColumnOrderedLists(columnOrderedList);
		varList = workbook.arrangeMeasurementVariables(varList);

		assertEquals("1st element should have term id 20", 20, varList.get(0).getTermId());
		assertEquals("2nd element should have term id 30", 30, varList.get(1).getTermId());
		assertEquals("3rd element should have term id 10", 10, varList.get(2).getTermId());
	}

	@Test
	public void testGetStudyID() {
		Integer studyId = 5;
		Workbook workbook = createTestWorkbook(DEFAULT_NO_OF_OBSERVATIONS, StudyType.N, NURSERY_NAME, 1, false);
		workbook.getStudyDetails().setId(studyId);

		assertEquals("Workbook should be able to use the study details ID as the study ID", studyId, workbook.getStudyId());
	}

	private MeasurementData createMeasurementData(int termId) {
		MeasurementData data = new MeasurementData();
		MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setTermId(termId);
		data.setMeasurementVariable(measurementVariable);
		return data;
	}

	@Test
	public void testArrangeMeasurementObservation() {
		Workbook workbook = new Workbook();
		List<MeasurementRow> observations = new ArrayList<MeasurementRow>();
		MeasurementRow row = new MeasurementRow();
		List<MeasurementData> dataList = new ArrayList<MeasurementData>();
		dataList.add(this.createMeasurementData(10));
		dataList.add(this.createMeasurementData(20));
		dataList.add(this.createMeasurementData(30));
		row.setDataList(dataList);
		observations.add(row);

		List<Integer> columnOrderedList = new ArrayList<Integer>();
		columnOrderedList.add(new Integer(20));
		columnOrderedList.add(new Integer(30));
		columnOrderedList.add(new Integer(10));
		workbook.setColumnOrderedLists(columnOrderedList);
		List<MeasurementRow> newObservations = workbook.arrangeMeasurementObservation(observations);

		assertEquals("1st element should have term id 20", 20, newObservations.get(0).getDataList().get(0).getMeasurementVariable()
				.getTermId());
		assertEquals("1st element should have term id 30", 30, newObservations.get(0).getDataList().get(1).getMeasurementVariable()
				.getTermId());
		assertEquals("1st element should have term id 10", 10, newObservations.get(0).getDataList().get(2).getMeasurementVariable()
				.getTermId());
	}

	public static Workbook getTestWorkbookWithErrors() {
		return WorkbookTest.createTestWorkbookWithErrors();
	}

	private static Workbook createTestWorkbookWithErrors() {
		Workbook workbook = new Workbook();

		String studyName = "workbookWithErrors" + new Random().nextInt(10000);
		WorkbookTest.createStudyDetails(workbook, studyName, StudyType.T);
		WorkbookTest.createConditions(workbook, false, 1);
		WorkbookTest.createFactors(workbook, false, false, 1);
		WorkbookTest.createConstants(workbook);
		WorkbookTest.createVariatesWithDuplicatePSM(workbook);
		WorkbookTest.createObservations(workbook, 10, false, 1);

		return workbook;
	}

	public static Workbook getTestWorkbookForWizard(String studyName, int trialNo) {
		return WorkbookTest.createTestWorkbookForWizard(studyName, trialNo);
	}

	public static Workbook createTestWorkbookForWizard(String studyName, int trialNo) {
		Workbook wbook = new Workbook();

		WorkbookTest.createStudyDetails(wbook, studyName, StudyType.T);
		WorkbookTest.createConditions(wbook, false, trialNo);
		WorkbookTest.createFactors(wbook, true, true, trialNo);
		WorkbookTest.createConstants(wbook);
		WorkbookTest.createVariates(wbook);
		WorkbookTest.createObservations(wbook, 10, true, trialNo);

		return wbook;
	}

}
