
package org.generationcp.middleware.data.initializer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.Location;

public class WorkbookTestDataInitializer {

	public static final int DAY_OBS = 8284;
	public static final int ASPERGILLUS_FLAVUSPPB = 20369;
	public static final int ASPERGILLUS_FLAVUS1_5 = 20368;
	public static final String NURSERY_NAME = "Nursery_";
	public static final String TRIAL_NAME = "Trial_";

	// STUDY DETAILS
	public static final String TITLE = "Nursery Workbook";
	public static final String OBJECTIVE = "To evaluate the Population 114";
	public static final String START_DATE = "20130805";
	public static final String END_DATE = "20130805";
	public static final int FOLDER_ID = 1;

	// PROPERTIES
	public static final String PERSON = "PERSON";
	public static final String TRIAL_INSTANCE = "TRIAL";
	public static final String LOCATION = "LOCATION";
	public static final String GERMPLASM_ENTRY = "GERMPLASM ENTRY";
	public static final String GERMPLASM_ID = "GERMPLASM ID";
	public static final String CROSS_HISTORY = "CROSS HISTORY";
	public static final String SEED_SOURCE = "SEED SOURCE";
	public static final String FIELD_PLOT = "FIELD PLOT";
	public static final String REPLICATION = "REPLICATION";
	public static final String REPLICATION_FACTOR = "REPLICATION FACTOR";
	public static final String BLOCKING_FACTOR = "BLOCKING FACTOR";
	public static final String SOIL_ACIDITY = "Soil Acidity";
	public static final String YIELD = "Yield";

	// SCALES
	public static final String DBCV = "DBCV";
	public static final String DBID = "DBID";
	public static final String NUMBER = "NUMBER";
	public static final String PEDIGREE_STRING = "PEDIGREE STRING";
	public static final String NAME = "NAME";
	public static final String NESTED_NUMBER = "NESTED NUMBER";
	public static final String KG_HA = "kg/ha";
	public static final String PH = "ph";
	public static final String SCORE_1_5 = "Score (1-5)";
	public static final String VISUAL_SCORING = "Visual scoring";
	public static final String COMMON_RUST = "Common rust";

	// METHODS
	public static final String ASSIGNED = "ASSIGNED";
	public static final String ENUMERATED = "ENUMERATED";
	public static final String CONDUCTED = "CONDUCTED";
	public static final String SELECTED = "SELECTED";
	public static final String PH_METER = "PH Meter";
	public static final String DRY_AND_WEIGH = "Dry and Weigh";
	public static final String MEASURED = "MEASURED";

	// LABELS
	public static final String STUDY = "STUDY";
	public static final String TRIAL = "TRIAL";
	public static final String ENTRY = "ENTRY";
	public static final String PLOT = "PLOT";

	// DATA TYPES
	public static final String CHAR = "C";
	public static final String NUMERIC = "N";

	// FACTORS
	public static final String GID = "GID";
	public static final String DESIG = "DESIG";
	public static final String CROSS = "CROSS";
	public static final String SOURCE = "SOURCE";
	public static final String BLOCK = "BLOCK";
	public static final String REP = "REP";

	// CONSTANTS
	public static final int GRAIN_SIZE_ID = 18110;
	public static final int SOILPH_ID = 8270;
	public static final String GRAIN_SIZE_PROPERTY = "Grain size";
	public static final String DRY_GRAINS = "Weigh 1000 dry grains";
	public static final String GRAIN_SIZE_SCALE = "g";

	// VARIATES
	public static final String GYLD = "GYLD";
	public static final int GYLD_ID = 18000;
	public static final String CRUST = "CRUST";
	public static final int CRUST_ID = 20310;

	// CONDITIONS
	public static final String PI_NAME = "PI Name";
	public static final String PI_ID = "PI Id";
	public static final String COOPERATOR = "COOPERATOR";
	public static final String COOPERATOR_ID = "COOPERATOR ID";
	public static final int COOPERATOR_ID_ID = 8372;
	public static final int COOPERATOR_NAME_ID = 8373;
	public static final String NUMERIC_VALUE = "1";
	public static final String SITE = "Site";
	public static final String SITE_ID = "Site Id";

	public static final Integer LOCATION_ID_1 = 1;
	public static final Integer LOCATION_ID_2 = 2;
	public static final Integer LTYPE = 1;
	public static final Integer NLLP = 1;
	public static final String LNAME = "Location";
	public static final String LABBR = "LOC2";
	public static final Integer SNL3ID = 1;
	public static final Integer SNL2ID = 1;
	public static final Integer SNL1ID = 1;
	public static final Integer CNTRYID = 1;
	public static final Integer LRPLCE = 1;

	public static final String VALID_CRUST_VALUE = "1";
	public static final String VALID_CRUST_CVALUE_ID = "50723";
	public static final String INVALID_CRUST_VALUE_NUMERIC = "11";
	public static final String INVALID_CRUST_VALUE_CHAR = "test";
	public static final String INVALID_CRUST_VALUE_MISSING = "missing";

	public static final int DEFAULT_NO_OF_OBSERVATIONS = 10;
	public static final int LOCATION_NAME_ID = 8180;
	public static final int LOCATION_ID_ID = 8190;
	public static final int EXPT_DESIGN_ID = 8135;
	public static final String EXPERIMENT_DESIGN = "Experimental design";
	public static final String TYPE = "Type";
	public static final int SITE_SOIL_PH_ID = 8270;
	public static final String SITE_SOIL_PH = "SITE_SOIL_PH";

	public static final String[] G_NAMES = {"TIANDOUGOU-9", "KENINKENI-27", "SM114-1A-1-1-1B", "SM114-1A-14-1-1B", "SM114-1A-361-1-1B",
			"SM114-1A-86-1-1B", "SM114-1A-115-1-1B", "SM114-1A-281-1-1B", "SM114-1A-134-1-1B", "SM114-1A-69-1-1B", "SM114-1A-157-1-1B",
			"SM114-1A-179-1-1B", "TIANDOUGOU-9", "SM114-1A-36-1-1B", "SM114-1A-201-1-1B", "SM114-1A-31-1-1B", "SM114-1A-353-1-1B",
			"SM114-1A-26-1-1B", "SM114-1A-125-1-1B", "SM114-1A-384-1-1B"};

	public static Workbook getTestWorkbook() {
		return createTestWorkbook(WorkbookTestDataInitializer.DEFAULT_NO_OF_OBSERVATIONS, StudyType.N, null, 1, false);
	}

	public static Workbook getTestWorkbook(final int noOfObservations, final StudyType studyType) {
		return createTestWorkbook(noOfObservations, studyType, null, 1, false);
	}

	public static Workbook getTestWorkbookWithErrors() {
		return WorkbookTestDataInitializer.createTestWorkbookWithErrors();
	}

	public static Workbook getTestWorkbookForWizard(final String studyName, final int trialNo) {
		return WorkbookTestDataInitializer.createTestWorkbookForWizard(studyName, trialNo);
	}

	public static List<Workbook> getTestWorkbooks(final int noOfTrial, final int noOfObservations) {
		List<Workbook> workbooks = new ArrayList<Workbook>();
		final String studyName = "pheno_t7" + new Random().nextInt(10000);
		for (int i = 1; i <= noOfTrial; i++) {
			workbooks.add(createTestWorkbook(noOfObservations, StudyType.T, studyName, i, true));
		}
		return workbooks;
	}

	public static Workbook createTestWorkbook(final int noOfObservations, final StudyType studyType, final String studyName,
			final int trialNo, final boolean hasMultipleLocations) {
		Workbook workbook = new Workbook();

		createStudyDetails(workbook, studyName, studyType);
		createConditions(workbook, !hasMultipleLocations, trialNo);
		createFactors(workbook, true, hasMultipleLocations, trialNo);
		createConstants(workbook);
		createVariates(workbook);
		createObservations(workbook, noOfObservations, hasMultipleLocations, trialNo);

		return workbook;
	}

	public static Workbook createTestWorkbookWithErrors() {
		final Workbook workbook = new Workbook();

		final String studyName = "workbookWithErrors" + new Random().nextInt(10000);
		createStudyDetails(workbook, studyName, StudyType.T);
		createConditions(workbook, false, 1);
		createFactors(workbook, false, false, 1);
		createConstants(workbook);
		createVariatesWithDuplicatePSM(workbook);
		createObservations(workbook, 10, false, 1);

		return workbook;
	}

	public static Workbook createTestWorkbookForWizard(final String studyName, final int trialNo) {
		final Workbook wbook = new Workbook();

		createStudyDetails(wbook, studyName, StudyType.T);
		createConditions(wbook, false, trialNo);
		createFactors(wbook, true, true, trialNo);
		createConstants(wbook);
		createVariates(wbook);
		createObservations(wbook, 10, true, trialNo);

		return wbook;
	}

	public static void createStudyDetails(final Workbook workbook, final String studyName, final StudyType studyType) {
		final StudyDetails details = new StudyDetails();
		if (studyName != null) {
			// this is used for adding multiple locations to one study
			details.setStudyName(studyName);
		} else {
			details.setStudyName((studyType.equals(StudyType.N) ? WorkbookTestDataInitializer.NURSERY_NAME
					: WorkbookTestDataInitializer.TRIAL_NAME) + new Random().nextInt(10000));
		}
		details.setTitle(WorkbookTestDataInitializer.TITLE);
		details.setObjective(WorkbookTestDataInitializer.OBJECTIVE);
		details.setStartDate(WorkbookTestDataInitializer.START_DATE);
		details.setEndDate(WorkbookTestDataInitializer.END_DATE);
		details.setParentFolderId(WorkbookTestDataInitializer.FOLDER_ID);
		details.setStudyType(studyType);

		workbook.setStudyDetails(details);
	}

	public static void createConditions(final Workbook workbook, final boolean withTrial, final int trialNo) {
		// Create measurement variables and set its dataTypeId
		final List<MeasurementVariable> conditions = new ArrayList<MeasurementVariable>();

		if (withTrial) {
			conditions.add(createTrialInstanceMeasurementVariable(trialNo));
		}

		conditions.add(createMeasurementVariable(TermId.PI_NAME.getId(), "PI Name", "Name of Principal Investigator",
				WorkbookTestDataInitializer.DBCV, WorkbookTestDataInitializer.ASSIGNED, WorkbookTestDataInitializer.PERSON,
				WorkbookTestDataInitializer.CHAR, "PI Name Value", WorkbookTestDataInitializer.STUDY, TermId.CHARACTER_VARIABLE.getId(),
				withTrial ? PhenotypicType.TRIAL_ENVIRONMENT : PhenotypicType.STUDY));

		conditions.add(createMeasurementVariable(TermId.PI_ID.getId(), "PI ID", "ID of Principal Investigator",
				WorkbookTestDataInitializer.DBID, WorkbookTestDataInitializer.ASSIGNED, WorkbookTestDataInitializer.PERSON,
				WorkbookTestDataInitializer.NUMERIC, WorkbookTestDataInitializer.NUMERIC_VALUE, WorkbookTestDataInitializer.STUDY,
				TermId.NUMERIC_VARIABLE.getId(), withTrial ? PhenotypicType.TRIAL_ENVIRONMENT : PhenotypicType.STUDY));

		conditions.add(createMeasurementVariable(WorkbookTestDataInitializer.COOPERATOR_NAME_ID, "COOPERATOR", "COOPERATOR NAME",
				WorkbookTestDataInitializer.DBCV, WorkbookTestDataInitializer.CONDUCTED, WorkbookTestDataInitializer.PERSON,
				WorkbookTestDataInitializer.CHAR, "John Smith", WorkbookTestDataInitializer.TRIAL, TermId.CHARACTER_VARIABLE.getId(),
				withTrial ? PhenotypicType.TRIAL_ENVIRONMENT : PhenotypicType.STUDY));

		conditions.add(createMeasurementVariable(WorkbookTestDataInitializer.COOPERATOR_ID_ID, "COOPERATOR ID", "COOPERATOR ID",
				WorkbookTestDataInitializer.DBID, WorkbookTestDataInitializer.CONDUCTED, WorkbookTestDataInitializer.PERSON,
				WorkbookTestDataInitializer.NUMERIC, WorkbookTestDataInitializer.NUMERIC_VALUE, WorkbookTestDataInitializer.TRIAL,
				TermId.NUMERIC_VARIABLE.getId(), withTrial ? PhenotypicType.TRIAL_ENVIRONMENT : PhenotypicType.STUDY));

		conditions.add(createMeasurementVariable(WorkbookTestDataInitializer.LOCATION_NAME_ID, "SITE", "TRIAL SITE NAME",
				WorkbookTestDataInitializer.DBCV, WorkbookTestDataInitializer.ASSIGNED, WorkbookTestDataInitializer.LOCATION,
				WorkbookTestDataInitializer.CHAR, "SITE " + trialNo, WorkbookTestDataInitializer.TRIAL, TermId.CHARACTER_VARIABLE.getId(),
				PhenotypicType.TRIAL_ENVIRONMENT));

		conditions.add(createMeasurementVariable(WorkbookTestDataInitializer.LOCATION_ID_ID, "SITE ID", "TRIAL SITE ID",
				WorkbookTestDataInitializer.DBID, WorkbookTestDataInitializer.ASSIGNED, WorkbookTestDataInitializer.LOCATION,
				WorkbookTestDataInitializer.NUMERIC, String.valueOf(trialNo), WorkbookTestDataInitializer.TRIAL,
				TermId.NUMERIC_VARIABLE.getId(), PhenotypicType.TRIAL_ENVIRONMENT));

		conditions.add(createExperimentalRCBDVariable());

		workbook.setConditions(conditions);
	}

	public static MeasurementVariable createExperimentalRCBDVariable() {
		return createMeasurementVariable(WorkbookTestDataInitializer.EXPT_DESIGN_ID, "DESIGN", "EXPERIMENTAL DESIGN",
				WorkbookTestDataInitializer.TYPE, WorkbookTestDataInitializer.ASSIGNED, WorkbookTestDataInitializer.EXPERIMENT_DESIGN,
				WorkbookTestDataInitializer.CHAR, String.valueOf(TermId.RANDOMIZED_COMPLETE_BLOCK.getId()),
				WorkbookTestDataInitializer.TRIAL, TermId.CHARACTER_VARIABLE.getId(), PhenotypicType.TRIAL_ENVIRONMENT);
	}

	public static void createFactors(final Workbook workbook, final boolean withEntry, final boolean withTrial, final int trialNo) {
		// Create measurement variables and set its dataTypeId
		final List<MeasurementVariable> factors = new ArrayList<MeasurementVariable>();

		if (withTrial) {
			factors.add(createTrialInstanceMeasurementVariable(trialNo));
		}

		// Entry Factors
		if (withEntry) {
			factors.add(createMeasurementVariable(TermId.ENTRY_NO.getId(), WorkbookTestDataInitializer.ENTRY, "The germplasm entry number",
					WorkbookTestDataInitializer.NUMBER, WorkbookTestDataInitializer.ENUMERATED,
					WorkbookTestDataInitializer.GERMPLASM_ENTRY, WorkbookTestDataInitializer.NUMERIC, WorkbookTestDataInitializer.STUDY,
					WorkbookTestDataInitializer.ENTRY, TermId.NUMERIC_VARIABLE.getId(), PhenotypicType.GERMPLASM));
		}

		factors.add(createMeasurementVariable(TermId.GID.getId(), WorkbookTestDataInitializer.GID, "The GID of the germplasm",
				WorkbookTestDataInitializer.DBID, WorkbookTestDataInitializer.ASSIGNED, WorkbookTestDataInitializer.GERMPLASM_ID,
				WorkbookTestDataInitializer.NUMERIC, WorkbookTestDataInitializer.NUMERIC_VALUE, WorkbookTestDataInitializer.ENTRY,
				TermId.NUMERIC_VARIABLE.getId(), PhenotypicType.GERMPLASM));

		factors.add(createMeasurementVariable(TermId.DESIG.getId(), WorkbookTestDataInitializer.DESIG, "The name of the germplasm",
				WorkbookTestDataInitializer.DBCV, WorkbookTestDataInitializer.ASSIGNED, WorkbookTestDataInitializer.GERMPLASM_ID,
				WorkbookTestDataInitializer.CHAR, WorkbookTestDataInitializer.STUDY, WorkbookTestDataInitializer.ENTRY,
				TermId.CHARACTER_VARIABLE.getId(), PhenotypicType.GERMPLASM));

		factors.add(createMeasurementVariable(TermId.CROSS.getId(), WorkbookTestDataInitializer.CROSS,
				"The pedigree string of the germplasm", WorkbookTestDataInitializer.PEDIGREE_STRING, WorkbookTestDataInitializer.ASSIGNED,
				WorkbookTestDataInitializer.CROSS_HISTORY, WorkbookTestDataInitializer.CHAR, WorkbookTestDataInitializer.STUDY,
				WorkbookTestDataInitializer.ENTRY, TermId.CHARACTER_VARIABLE.getId(), PhenotypicType.GERMPLASM));

		factors.add(createMeasurementVariable(TermId.SEED_SOURCE.getId(), WorkbookTestDataInitializer.SEED_SOURCE,
				"The seed source of the germplasm", WorkbookTestDataInitializer.NAME, WorkbookTestDataInitializer.SELECTED,
				WorkbookTestDataInitializer.SEED_SOURCE, WorkbookTestDataInitializer.CHAR, WorkbookTestDataInitializer.STUDY,
				WorkbookTestDataInitializer.ENTRY, TermId.CHARACTER_VARIABLE.getId(), PhenotypicType.GERMPLASM));

		factors.add(createMeasurementVariable(TermId.PLOT_NO.getId(), WorkbookTestDataInitializer.PLOT, "Plot number ",
				WorkbookTestDataInitializer.NESTED_NUMBER, WorkbookTestDataInitializer.ENUMERATED, WorkbookTestDataInitializer.FIELD_PLOT,
				WorkbookTestDataInitializer.NUMERIC, WorkbookTestDataInitializer.NUMERIC_VALUE, WorkbookTestDataInitializer.PLOT,
				TermId.NUMERIC_VARIABLE.getId(), PhenotypicType.TRIAL_DESIGN));

		// Plot Factors
		factors.add(createMeasurementVariable(TermId.BLOCK_NO.getId(), WorkbookTestDataInitializer.BLOCK, "INCOMPLETE BLOCK",
				WorkbookTestDataInitializer.NUMBER, WorkbookTestDataInitializer.ENUMERATED, WorkbookTestDataInitializer.BLOCKING_FACTOR,
				WorkbookTestDataInitializer.NUMERIC, WorkbookTestDataInitializer.NUMERIC_VALUE, WorkbookTestDataInitializer.PLOT,
				TermId.NUMERIC_VARIABLE.getId(), PhenotypicType.TRIAL_DESIGN));

		factors.add(createMeasurementVariable(TermId.REP_NO.getId(), WorkbookTestDataInitializer.REP,
				WorkbookTestDataInitializer.REPLICATION, WorkbookTestDataInitializer.NUMBER, WorkbookTestDataInitializer.ENUMERATED,
				WorkbookTestDataInitializer.REPLICATION_FACTOR, WorkbookTestDataInitializer.NUMERIC,
				WorkbookTestDataInitializer.NUMERIC_VALUE, WorkbookTestDataInitializer.PLOT, TermId.NUMERIC_VARIABLE.getId(),
				PhenotypicType.TRIAL_DESIGN));

		factors.add(createMeasurementVariable(WorkbookTestDataInitializer.DAY_OBS, "DAY_OBS", WorkbookTestDataInitializer.REPLICATION,
				WorkbookTestDataInitializer.NUMBER, WorkbookTestDataInitializer.ENUMERATED, WorkbookTestDataInitializer.REPLICATION_FACTOR,
				WorkbookTestDataInitializer.NUMERIC, WorkbookTestDataInitializer.NUMERIC_VALUE, WorkbookTestDataInitializer.PLOT,
				TermId.NUMERIC_VARIABLE.getId(), PhenotypicType.TRIAL_DESIGN));

		workbook.setFactors(factors);
	}

	public static void createConstants(final Workbook workbook) {
		// Create measurement variables and set its dataTypeId
		final List<MeasurementVariable> constants = new ArrayList<MeasurementVariable>();

		constants.add(createMeasurementVariable(WorkbookTestDataInitializer.GRAIN_SIZE_ID, "Grain_size",
				"Grain size - weigh 1000 dry grains (g)", WorkbookTestDataInitializer.GRAIN_SIZE_SCALE,
				WorkbookTestDataInitializer.DRY_GRAINS, WorkbookTestDataInitializer.GRAIN_SIZE_PROPERTY,
				WorkbookTestDataInitializer.NUMERIC, WorkbookTestDataInitializer.NUMERIC_VALUE, WorkbookTestDataInitializer.TRIAL,
				TermId.NUMERIC_VARIABLE.getId(), PhenotypicType.VARIATE));
		workbook.setConstants(constants);
	}

	public static void createVariates(final Workbook workbook) {
		// Create measurement variables and set its dataTypeId
		final List<MeasurementVariable> variates = new ArrayList<MeasurementVariable>();
		final MeasurementVariable measurementVariable =
				createMeasurementVariable(WorkbookTestDataInitializer.GYLD_ID, WorkbookTestDataInitializer.GYLD,
						"Grain yield -dry and weigh (kg/ha)", WorkbookTestDataInitializer.KG_HA, WorkbookTestDataInitializer.DRY_AND_WEIGH,
						WorkbookTestDataInitializer.YIELD, WorkbookTestDataInitializer.NUMERIC, WorkbookTestDataInitializer.NUMERIC_VALUE,
						WorkbookTestDataInitializer.PLOT, TermId.NUMERIC_VARIABLE.getId(), PhenotypicType.VARIATE);

		variates.add(measurementVariable);

		final MeasurementVariable siteSoilPh =
				createMeasurementVariable(WorkbookTestDataInitializer.SITE_SOIL_PH_ID, WorkbookTestDataInitializer.SITE_SOIL_PH,
						"Soil acidity - ph meter (pH)", WorkbookTestDataInitializer.PH, WorkbookTestDataInitializer.MEASURED,
						WorkbookTestDataInitializer.SOIL_ACIDITY, WorkbookTestDataInitializer.NUMERIC,
						WorkbookTestDataInitializer.NUMERIC_VALUE, WorkbookTestDataInitializer.STUDY, TermId.NUMERIC_VARIABLE.getId(),
						PhenotypicType.VARIATE);
		variates.add(siteSoilPh);
		workbook.setVariates(variates);
	}

	public static void createObservations(final Workbook workbook, final int noOfObservations, final boolean withTrial, final int trialNo) {
		final List<MeasurementRow> observations = new ArrayList<MeasurementRow>();

		MeasurementRow row;
		List<MeasurementData> dataList;
		final Random random = new Random();
		final DecimalFormat fmt = new DecimalFormat("#.##");

		// Create n number of observation rows
		for (int i = 0; i < noOfObservations; i++) {
			row = new MeasurementRow();
			dataList = new ArrayList<MeasurementData>();
			if (withTrial) {
				dataList.add(createMeasurementData(WorkbookTestDataInitializer.TRIAL, String.valueOf(trialNo),
						TermId.TRIAL_INSTANCE_FACTOR.getId(), workbook.getFactors()));
			}

			dataList.add(createMeasurementData(WorkbookTestDataInitializer.ENTRY, String.valueOf(i), TermId.ENTRY_NO.getId(),
					workbook.getFactors()));
			dataList.add(createMeasurementData(WorkbookTestDataInitializer.GID, computeGID(i), TermId.GID.getId(), workbook.getFactors()));
			dataList.add(createMeasurementData(WorkbookTestDataInitializer.DESIG, WorkbookTestDataInitializer.G_NAMES[i],
					TermId.DESIG.getId(), workbook.getFactors()));
			dataList.add(createMeasurementData(WorkbookTestDataInitializer.CROSS, "-", TermId.CROSS.getId(), workbook.getFactors()));
			dataList.add(createMeasurementData(WorkbookTestDataInitializer.SOURCE, "-", TermId.SEED_SOURCE.getId(), workbook.getFactors()));
			dataList.add(createMeasurementData(WorkbookTestDataInitializer.PLOT, String.valueOf(i), TermId.PLOT_NO.getId(),
					workbook.getFactors()));
			dataList.add(createMeasurementData(WorkbookTestDataInitializer.BLOCK, "", TermId.BLOCK_NO.getId(), workbook.getFactors()));
			dataList.add(createMeasurementData(WorkbookTestDataInitializer.REP, "", TermId.REP_NO.getId(), workbook.getFactors()));
			dataList.add(createMeasurementData("DAY_OBS", randomizeValue(random, fmt, 5000), WorkbookTestDataInitializer.DAY_OBS,
					workbook.getFactors()));
			dataList.add(createMeasurementData(WorkbookTestDataInitializer.GYLD, randomizeValue(random, fmt, 5000),
					WorkbookTestDataInitializer.GYLD_ID, workbook.getVariates()));
			dataList.add(createMeasurementData(WorkbookTestDataInitializer.SITE_SOIL_PH, "1", WorkbookTestDataInitializer.SITE_SOIL_PH_ID,
					workbook.getVariates()));
			row.setDataList(dataList);
			observations.add(row);
		}

		workbook.setObservations(observations);
	}

	public static MeasurementVariable createMeasurementVariable(final int termId, final String name, final String description,
			final String scale, final String method, final String property, final String dataType, final String value, final String label,
			final int dataTypeId, final PhenotypicType role) {
		final MeasurementVariable variable =
				new MeasurementVariable(termId, name, description, scale, method, property, dataType, value, label);
		variable.setRole(role);
		variable.setDataTypeId(dataTypeId);
		return variable;
	}

	public static MeasurementVariable createMeasurementVariable(final Integer termId) {
		final MeasurementVariable var = new MeasurementVariable();
		var.setTermId(termId);
		return var;
	}

	public static MeasurementData createMeasurementData(final String label, final String value, final int termId,
			final List<MeasurementVariable> variables) {
		final MeasurementData data = new MeasurementData(label, value);
		data.setMeasurementVariable(getMeasurementVariable(termId, variables));
		return data;
	}

	public static MeasurementData createMeasurementData(final int termId) {
		final MeasurementData data = new MeasurementData();
		final MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setTermId(termId);
		data.setMeasurementVariable(measurementVariable);
		return data;
	}

	public static MeasurementVariable createTrialInstanceMeasurementVariable(final int trialNo) {
		return createMeasurementVariable(TermId.TRIAL_INSTANCE_FACTOR.getId(), "TRIAL", "TRIAL NUMBER", WorkbookTestDataInitializer.NUMBER,
				WorkbookTestDataInitializer.ENUMERATED, WorkbookTestDataInitializer.TRIAL_INSTANCE, WorkbookTestDataInitializer.NUMERIC,
				String.valueOf(trialNo), WorkbookTestDataInitializer.TRIAL, TermId.CHARACTER_VARIABLE.getId(),
				PhenotypicType.TRIAL_ENVIRONMENT);
	}

	public static String randomizeValue(final Random random, final DecimalFormat fmt, final int base) {
		final double value = random.nextDouble() * base;
		return fmt.format(value);
	}

	public static String computeGID(final int i) {
		int gid = 1000000;
		if (i < 13) {
			gid += i - 1;
		} else if (i > 13) {
			gid += i - 2;
		}
		return String.valueOf(gid);
	}

	public static MeasurementVariable getMeasurementVariable(final int termId, final List<MeasurementVariable> variables) {
		if (variables != null) {
			// get matching MeasurementVariable object given the term id
			for (final MeasurementVariable var : variables) {
				if (var.getTermId() == termId) {
					return var;
				}
			}
		}
		return null;
	}

	private static void createVariatesWithDuplicatePSM(final Workbook workbook) {
		final List<MeasurementVariable> variates = new ArrayList<MeasurementVariable>();

		final MeasurementVariable gyld =
				WorkbookTestDataInitializer.createMeasurementVariable(WorkbookTestDataInitializer.GYLD_ID,
						WorkbookTestDataInitializer.GYLD, "Grain yield -dry and weigh (kg/ha)", WorkbookTestDataInitializer.KG_HA,
						WorkbookTestDataInitializer.DRY_AND_WEIGH, WorkbookTestDataInitializer.YIELD, WorkbookTestDataInitializer.NUMERIC,
						WorkbookTestDataInitializer.NUMERIC_VALUE, WorkbookTestDataInitializer.PLOT, TermId.NUMERIC_VARIABLE.getId(),
						PhenotypicType.VARIATE);

		variates.add(gyld);

		final MeasurementVariable siteSoilPh =
				WorkbookTestDataInitializer.createMeasurementVariable(WorkbookTestDataInitializer.SITE_SOIL_PH_ID,
						WorkbookTestDataInitializer.SITE_SOIL_PH, "Soil acidity - ph meter (pH)", WorkbookTestDataInitializer.PH,
						WorkbookTestDataInitializer.MEASURED, WorkbookTestDataInitializer.SOIL_ACIDITY,
						WorkbookTestDataInitializer.NUMERIC, WorkbookTestDataInitializer.NUMERIC_VALUE, WorkbookTestDataInitializer.PLOT,
						TermId.NUMERIC_VARIABLE.getId(), PhenotypicType.VARIATE);
		variates.add(siteSoilPh);

		workbook.setVariates(variates);
	}

	public static void addVariatesAndObservations(final Workbook currentWorkbook) {

		final List<MeasurementVariable> variates = currentWorkbook.getVariates();
		final MeasurementVariable measurementVariable =
				createMeasurementVariable(
						WorkbookTestDataInitializer.CRUST_ID,
						WorkbookTestDataInitializer.CRUST,
						"Score for the severity of common rust, (In highlands and mid altitude, Puccinia sorghi) symptoms rated on a scale from 1 (= clean, no infection) to 5 (= severely diseased).",
						WorkbookTestDataInitializer.SCORE_1_5, WorkbookTestDataInitializer.VISUAL_SCORING,
						WorkbookTestDataInitializer.COMMON_RUST, WorkbookTestDataInitializer.CHAR, null, WorkbookTestDataInitializer.PLOT,
						TermId.CATEGORICAL_VARIABLE.getId(), PhenotypicType.VARIATE);

		measurementVariable.setOperation(Operation.ADD);

		variates.add(measurementVariable);

		addObservations(currentWorkbook);
	}

	private static void addObservations(final Workbook currentWorkbook) {
		final List<MeasurementRow> observations = currentWorkbook.getObservations();

		MeasurementRow row;
		List<MeasurementData> dataList;
		final Random random = new Random();
		final DecimalFormat fmt = new DecimalFormat("#.##");

		// Create n number of observation rows
		for (int i = 0; i < observations.size(); i++) {
			row = observations.get(i);
			dataList = row.getDataList();

			String crustValue = randomizeValue(random, fmt, 5000);
			String crustCValueId = null;
			switch (i) {
				case 0:
					crustValue = "";
					break;
				case 1:
					crustValue = null;
					break;
				case 2:
					crustValue = WorkbookTestDataInitializer.VALID_CRUST_VALUE;
					crustCValueId = WorkbookTestDataInitializer.VALID_CRUST_CVALUE_ID;
					break;
				case 3:
					crustValue = WorkbookTestDataInitializer.INVALID_CRUST_VALUE_NUMERIC;
					break;
				case 4:
					crustValue = WorkbookTestDataInitializer.INVALID_CRUST_VALUE_CHAR;
					break;
				case 5:
					crustValue = WorkbookTestDataInitializer.INVALID_CRUST_VALUE_MISSING;
					break;
			}
			final MeasurementData measurementData =
					createMeasurementData(WorkbookTestDataInitializer.CRUST, crustValue, WorkbookTestDataInitializer.CRUST_ID,
							currentWorkbook.getVariates());
			measurementData.setcValueId(crustCValueId);
			dataList.add(measurementData);
		}
	}

	public static List<Location> createLocationData() {
		final List<Location> locations = new ArrayList<Location>();
		locations.add(new Location(WorkbookTestDataInitializer.LOCATION_ID_1, WorkbookTestDataInitializer.LTYPE,
				WorkbookTestDataInitializer.NLLP, WorkbookTestDataInitializer.LNAME + " 1", WorkbookTestDataInitializer.LABBR,
				WorkbookTestDataInitializer.SNL3ID, WorkbookTestDataInitializer.SNL2ID, WorkbookTestDataInitializer.SNL1ID,
				WorkbookTestDataInitializer.CNTRYID, WorkbookTestDataInitializer.LRPLCE));
		locations.add(new Location(WorkbookTestDataInitializer.LOCATION_ID_2, WorkbookTestDataInitializer.LTYPE,
				WorkbookTestDataInitializer.NLLP, WorkbookTestDataInitializer.LNAME + " 2", WorkbookTestDataInitializer.LABBR,
				WorkbookTestDataInitializer.SNL3ID, WorkbookTestDataInitializer.SNL2ID, WorkbookTestDataInitializer.SNL1ID,
				WorkbookTestDataInitializer.CNTRYID, WorkbookTestDataInitializer.LRPLCE));
		return locations;
	}

	public static MeasurementRow createTrialObservationWithoutSite() {
		final Workbook workbook = new Workbook();

		createStudyDetails(workbook, null, StudyType.T);
		createConditions(workbook, true, 1);

		final MeasurementRow row = new MeasurementRow();
		final List<MeasurementData> dataList = new ArrayList<MeasurementData>();

		dataList.add(createMeasurementData(WorkbookTestDataInitializer.TRIAL_INSTANCE, WorkbookTestDataInitializer.NUMERIC_VALUE,
				TermId.TRIAL_INSTANCE_FACTOR.getId(), workbook.getConditions()));
		dataList.add(createMeasurementData(WorkbookTestDataInitializer.PI_NAME, "", TermId.PI_NAME.getId(), workbook.getConditions()));
		dataList.add(createMeasurementData(WorkbookTestDataInitializer.PI_ID, "", TermId.PI_ID.getId(), workbook.getConditions()));
		dataList.add(createMeasurementData(WorkbookTestDataInitializer.COOPERATOR, "", WorkbookTestDataInitializer.COOPERATOR_NAME_ID,
				workbook.getConditions()));
		dataList.add(createMeasurementData(WorkbookTestDataInitializer.COOPERATOR_ID, "", WorkbookTestDataInitializer.COOPERATOR_ID_ID,
				workbook.getConditions()));

		row.setDataList(dataList);
		return row;
	}

	public static Workbook addEnvironmentAndConstantVariables(final Workbook createdWorkbook) {
		addConditions(createdWorkbook.getConditions());
		addConstants(createdWorkbook.getConstants());
		return createdWorkbook;
	}

	private static void addConstants(final List<MeasurementVariable> constants) {
		final MeasurementVariable variable =
				createMeasurementVariable(WorkbookTestDataInitializer.SOILPH_ID, "SITE_SOIL_PH", "Soil acidity - ph meter (pH)",
						WorkbookTestDataInitializer.PH, WorkbookTestDataInitializer.PH_METER, WorkbookTestDataInitializer.SOIL_ACIDITY,
						WorkbookTestDataInitializer.NUMERIC, WorkbookTestDataInitializer.NUMERIC_VALUE, WorkbookTestDataInitializer.TRIAL,
						TermId.NUMERIC_VARIABLE.getId(), PhenotypicType.VARIATE);
		variable.setOperation(Operation.ADD);
		constants.add(variable);
	}

	private static void addConditions(final List<MeasurementVariable> conditions) {
		MeasurementVariable variable =
				createMeasurementVariable(TermId.TRIAL_LOCATION.getId(), "SITE", "TRIAL SITE NAME", WorkbookTestDataInitializer.DBCV,
						WorkbookTestDataInitializer.ASSIGNED, WorkbookTestDataInitializer.LOCATION, WorkbookTestDataInitializer.CHAR, "",
						WorkbookTestDataInitializer.TRIAL, TermId.CHARACTER_VARIABLE.getId(), PhenotypicType.TRIAL_ENVIRONMENT);
		variable.setOperation(Operation.ADD);
		conditions.add(variable);

		variable =
				createMeasurementVariable(TermId.LOCATION_ID.getId(), "SITE ID", "TRIAL SITE ID", WorkbookTestDataInitializer.DBID,
						WorkbookTestDataInitializer.ASSIGNED, WorkbookTestDataInitializer.LOCATION, WorkbookTestDataInitializer.NUMERIC,
						WorkbookTestDataInitializer.NUMERIC_VALUE, WorkbookTestDataInitializer.TRIAL, TermId.NUMERIC_VARIABLE.getId(),
						PhenotypicType.TRIAL_ENVIRONMENT);
		variable.setOperation(Operation.ADD);
		conditions.add(variable);
	}

	public static boolean areTrialVariablesSame(final List<MeasurementVariable> trialVariablesTestData,
			final List<MeasurementVariable> trialVariablesRetrieved) {

		if (trialVariablesTestData != null && trialVariablesRetrieved != null) {
			for (final MeasurementVariable var : trialVariablesTestData) {
				if (notInRetrievedList(var.getTermId(), trialVariablesRetrieved)) {
					return false;
				}
			}
		}
		return true;
	}

	private static boolean notInRetrievedList(final int termId, final List<MeasurementVariable> trialVariablesRetrieved) {
		for (final MeasurementVariable var : trialVariablesRetrieved) {
			if (termId == var.getTermId()) {
				return false;
			}
		}
		return true;
	}

	public static void addNewEnvironment(final Workbook createdWorkbook) {
		addObservations(1, createdWorkbook.getTrialObservations());
		addObservations(1, createdWorkbook.getObservations());
	}

	private static void addObservations(final int newEnvironmentCount, final List<MeasurementRow> observations) {
		final List<MeasurementRow> originalObservations = new ArrayList<MeasurementRow>(getFirstTrialInstance(observations));
		int currentObsCount = observations.size() / originalObservations.size();

		for (int i = 0; i < newEnvironmentCount; i++) {
			final List<MeasurementRow> newInstance = new ArrayList<MeasurementRow>();
			for (final MeasurementRow row : originalObservations) {
				newInstance.add(new MeasurementRow(row));
			}
			currentObsCount++;
			observations.addAll(setValuesPerInstance(newInstance, currentObsCount));
		}

		for (final MeasurementRow row : observations) {
			for (final MeasurementData data : row.getDataList()) {
				if (data.getMeasurementVariable().getTermId() == WorkbookTestDataInitializer.ASPERGILLUS_FLAVUS1_5
						|| data.getMeasurementVariable().getTermId() == WorkbookTestDataInitializer.ASPERGILLUS_FLAVUSPPB) {
					data.setValue(String.valueOf(new Random().nextInt(10000)));
				}
			}
		}
	}

	public static List<MeasurementRow> setValuesPerInstance(final List<MeasurementRow> newInstance, final int currentObsCount) {
		for (final MeasurementRow row : newInstance) {
			for (final MeasurementData data : row.getDataList()) {
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

	public static List<MeasurementRow> getFirstTrialInstance(final List<MeasurementRow> observations) {
		final List<MeasurementRow> firstTrialInstance = new ArrayList<MeasurementRow>();
		long oldLocationId = 0;
		for (final MeasurementRow row : observations) {
			final long locationId = row.getLocationId();
			if (oldLocationId != locationId && oldLocationId != 0) {
				break;
			}
			firstTrialInstance.add(row);
			oldLocationId = locationId;
		}
		return firstTrialInstance;
	}

	public static void deleteExperimentPropVar(final Workbook createdWorkbook) {
		if (createdWorkbook.getFactors() != null) {
			for (final MeasurementVariable var : createdWorkbook.getFactors()) {
				if (var.getTermId() == WorkbookTestDataInitializer.DAY_OBS) {
					var.setOperation(Operation.DELETE);
				}
			}
		}
	}

	public static void createTrialObservations(final int noOfTrialInstances, Workbook workbook) {
		final List<MeasurementRow> trialObservations = new ArrayList<MeasurementRow>();

		MeasurementRow row;
		List<MeasurementData> dataList;

		for (int i = 0; i < noOfTrialInstances; i++) {
			row = new MeasurementRow();
			dataList = new ArrayList<MeasurementData>();

			MeasurementData data = new MeasurementData(TRIAL_INSTANCE, String.valueOf(i + 1));
			data.setMeasurementVariable(getMeasurementVariable(TermId.TRIAL_INSTANCE_FACTOR.getId(), workbook.getConditions()));
			dataList.add(data);
			data = new MeasurementData(PI_NAME, "");
			data.setMeasurementVariable(getMeasurementVariable(TermId.PI_NAME.getId(), workbook.getConditions()));
			dataList.add(data);
			data = new MeasurementData(PI_ID, "");
			data.setMeasurementVariable(getMeasurementVariable(TermId.PI_ID.getId(), workbook.getConditions()));
			dataList.add(data);
			data = new MeasurementData(COOPERATOR, "");
			data.setMeasurementVariable(getMeasurementVariable(COOPERATOR_NAME_ID, workbook.getConditions()));
			dataList.add(data);
			data = new MeasurementData(COOPERATOR_ID, "");
			data.setMeasurementVariable(getMeasurementVariable(COOPERATOR_ID_ID, workbook.getConditions()));
			dataList.add(data);
			data = new MeasurementData(WorkbookTestDataInitializer.SITE, LNAME + "_" + (i + 1));
			data.setMeasurementVariable(getMeasurementVariable(TermId.TRIAL_LOCATION.getId(), workbook.getConditions()));
			dataList.add(data);
			data = new MeasurementData(WorkbookTestDataInitializer.SITE_ID, String.valueOf(i + 1));
			data.setMeasurementVariable(getMeasurementVariable(TermId.LOCATION_ID.getId(), workbook.getConditions()));
			dataList.add(data);

			// Check variables
			data = new MeasurementData("CHECK_START", String.valueOf(i + 1));
			data.setMeasurementVariable(getMeasurementVariable(TermId.CHECK_START.getId(), workbook.getConditions()));
			dataList.add(data);
			data = new MeasurementData("CHECK_INTERVAL", String.valueOf(i + 1));
			data.setMeasurementVariable(getMeasurementVariable(TermId.CHECK_PLAN.getId(), workbook.getConditions()));
			dataList.add(data);
			data = new MeasurementData("CHECK_PLAN", "1");
			data.setMeasurementVariable(getMeasurementVariable(TermId.CHECK_INTERVAL.getId(), workbook.getConditions()));
			dataList.add(data);

			row.setDataList(dataList);
			trialObservations.add(row);
		}

		workbook.setTrialObservations(trialObservations);
	}

}
