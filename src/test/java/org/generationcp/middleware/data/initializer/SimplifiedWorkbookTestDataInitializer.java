package org.generationcp.middleware.data.initializer;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.domain.study.StudyTypeDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimplifiedWorkbookTestDataInitializer {

	public static final String STUDY_NAME = "Study_";

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
	public static final String GERMPLASM_ID = "GERMPLASM ID";
	public static final String CROSS_HISTORY = "CROSS HISTORY";
	public static final String SEED_SOURCE = "SEED SOURCE";
	public static final String FIELD_PLOT = "FIELD PLOT";
	public static final String REPLICATION = "REPLICATION";
	public static final String REPLICATION_FACTOR = "REPLICATION FACTOR";
	public static final String BLOCKING_FACTOR = "BLOCKING FACTOR";

	// SCALES
	public static final String DBCV = "DBCV";
	public static final String DBID = "DBID";
	public static final String NUMBER = "NUMBER";
	public static final String PEDIGREE_STRING = "PEDIGREE STRING";
	public static final String NAME = "NAME";
	public static final String NESTED_NUMBER = "NESTED NUMBER";
	public static final String KG_HA = "kg/ha";
	public static final String PH = "ph";
	public static final String TEXT = "Text";

	// METHODS
	public static final String ASSIGNED = "ASSIGNED";
	public static final String ENUMERATED = "ENUMERATED";
	public static final String CONDUCTED = "CONDUCTED";
	public static final String SELECTED = "SELECTED";

	// LABELS
	public static final String STUDY = "STUDY";
	public static final String TRIAL = "TRIAL";
	public static final String ENTRY = "ENTRY";
	public static final String PLOT = "PLOT";
	public static final String OBS_UNIT_ID = "OBS_UNIT_ID";
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

	// CONDITIONS
	public static final String PI_NAME = "PI Name";
	public static final String PI_ID = "PI Id";
	public static final String COOPERATOR = "COOPERATOR";
	public static final String COOPERATOR_ID = "COOPERATOR ID";
	public static final String NUMERIC_VALUE = "1";
	public static final String SITE_ID = "Site Id";
	public static final String TYPE = "Type";

	private static final String CREATED_BY = "1";

	private Random random = new Random();

	public Workbook createWorkbookWithStudyDetails(final String studyName, final StudyTypeDto studyType) {
		final Workbook workbook = new Workbook();
		final StudyDetails details = new StudyDetails();
		details.setStudyName(studyName);
		details.setDescription(TITLE);
		details.setObjective(OBJECTIVE);
		details.setStartDate(START_DATE);
		details.setEndDate(END_DATE);
		details.setParentFolderId(FOLDER_ID);
		details.setStudyType(studyType);
		details.setCreatedBy(CREATED_BY);
		workbook.setStudyDetails(details);
		return workbook;
	}

	public List<MeasurementVariable> createFactors(final int trialInstanceNumber) {

		final List<MeasurementVariable> factors = new ArrayList<>();

		factors.add(createMeasurementVariable(TermId.TRIAL_INSTANCE_FACTOR.getId(), "TRIAL", "TRIAL NUMBER",
			NUMBER, ENUMERATED, TRIAL_INSTANCE,
			NUMERIC, String.valueOf(trialInstanceNumber), TRIAL,
			TermId.CHARACTER_VARIABLE.getId(), PhenotypicType.TRIAL_ENVIRONMENT, false));

		factors.add(createMeasurementVariable(TermId.PI_NAME.getId(), "PI Name",
			"Name of Principal Investigator", DBCV, ASSIGNED,
			PERSON, CHAR, "PI Name Value", STUDY,
			TermId.CHARACTER_VARIABLE.getId(), PhenotypicType.TRIAL_ENVIRONMENT, false));

		factors.add(createMeasurementVariable(TermId.PI_ID.getId(), "PI ID", "ID of Principal Investigator",
			DBID, ASSIGNED, PERSON,
			NUMERIC, NUMERIC_VALUE, STUDY,
			TermId.NUMERIC_VARIABLE.getId(), PhenotypicType.TRIAL_ENVIRONMENT, false));

		factors.add(createMeasurementVariable(TermId.COOPERATOR.getId(), "COOPERATOR", "COOPERATOR NAME",
			DBCV, CONDUCTED, PERSON,
			CHAR, "John Smith", TRIAL, TermId.CHARACTER_VARIABLE.getId(),
			PhenotypicType.TRIAL_ENVIRONMENT, false));

		factors
			.add(createMeasurementVariable(TermId.COOPERATOOR_ID.getId(), "COOPERATOR ID", "COOPERATOR ID",
				DBID, CONDUCTED, PERSON,
				NUMERIC, NUMERIC_VALUE, TRIAL,
				TermId.NUMERIC_VARIABLE.getId(), PhenotypicType.TRIAL_ENVIRONMENT, false));

		factors.add(createMeasurementVariable(TermId.TRIAL_LOCATION.getId(), "SITE", "TRIAL SITE NAME",
			DBCV, ASSIGNED, LOCATION,
			CHAR, "SITE " + trialInstanceNumber, TRIAL, TermId.CHARACTER_VARIABLE.getId(),
			PhenotypicType.TRIAL_ENVIRONMENT, false));

		factors.add(createMeasurementVariable(TermId.LOCATION_ID.getId(), "SITE ID", "TRIAL SITE ID",
			DBID, ASSIGNED, LOCATION,
			NUMERIC, String.valueOf(random.nextInt(1000)), TRIAL,
			TermId.NUMERIC_VARIABLE.getId(), PhenotypicType.TRIAL_ENVIRONMENT, false));

		factors.add(createMeasurementVariable(TermId.GID.getId(), GID,
			"The GID of the germplasm", DBID, ASSIGNED,
			GERMPLASM_ID, NUMERIC, NUMERIC_VALUE,
			ENTRY, TermId.NUMERIC_VARIABLE.getId(), PhenotypicType.GERMPLASM, false));

		factors.add(createMeasurementVariable(TermId.DESIG.getId(), DESIG,
			"The name of the germplasm", DBCV, ASSIGNED,
			GERMPLASM_ID, CHAR, STUDY,
			ENTRY, TermId.CHARACTER_VARIABLE.getId(), PhenotypicType.GERMPLASM, false));

		factors.add(createMeasurementVariable(TermId.CROSS.getId(), CROSS,
			"The pedigree string of the germplasm", PEDIGREE_STRING, ASSIGNED,
			CROSS_HISTORY, CHAR, STUDY,
			ENTRY, TermId.CHARACTER_VARIABLE.getId(), PhenotypicType.GERMPLASM, false));

		factors.add(createMeasurementVariable(TermId.PLOT_NO.getId(), PLOT,
			"Plot number ", NESTED_NUMBER, ENUMERATED,
			FIELD_PLOT, NUMERIC, NUMERIC_VALUE,
			PLOT, TermId.NUMERIC_VARIABLE.getId(), PhenotypicType.TRIAL_DESIGN, false));

		factors.add(createMeasurementVariable(TermId.OBS_UNIT_ID.getId(), OBS_UNIT_ID,
			"Field Observation Unit id - enumerated (number) ", TEXT, ASSIGNED,
			FIELD_PLOT, CHAR, STUDY,
			PLOT, TermId.CHARACTER_VARIABLE.getId(), PhenotypicType.TRIAL_DESIGN, false));
		// Plot Factors
		factors.add(createMeasurementVariable(TermId.BLOCK_NO.getId(), BLOCK,
			"INCOMPLETE BLOCK", NUMBER, ENUMERATED,
			BLOCKING_FACTOR, NUMERIC, NUMERIC_VALUE,
			PLOT, TermId.NUMERIC_VARIABLE.getId(), PhenotypicType.TRIAL_DESIGN, false));

		factors.add(createMeasurementVariable(TermId.REP_NO.getId(), REP,
			REPLICATION, NUMBER, ENUMERATED,
			REPLICATION_FACTOR, NUMERIC,
			NUMERIC_VALUE, PLOT, TermId.NUMERIC_VARIABLE.getId(),
			PhenotypicType.TRIAL_DESIGN, false));

		return factors;
	}

	public List<MeasurementRow> createTrialObservations(final int noOfObservations, final List<MeasurementVariable> factors) {

		final List<MeasurementRow> observations = new ArrayList<>();

		// Create n number of observation rows
		for (int i = 0; i < noOfObservations; i++) {
			final MeasurementRow measurementRow = new MeasurementRow();
			final List<MeasurementData> measurementDataList = new ArrayList<>();

			measurementDataList.add(createMeasurementData(TRIAL, String.valueOf(i),
				TermId.TRIAL_INSTANCE_FACTOR.getId(), factors));

			measurementDataList.add(createMeasurementData(PI_NAME, "",
				TermId.PI_NAME.getId(), factors));

			measurementDataList.add(createMeasurementData(PI_ID, "",
				TermId.PI_ID.getId(), factors));

			measurementDataList.add(createMeasurementData(COOPERATOR, "",
				TermId.COOPERATOR.getId(), factors));

			measurementDataList.add(createMeasurementData(COOPERATOR_ID, "",
				TermId.COOPERATOOR_ID.getId(), factors));

			measurementDataList.add(createMeasurementData(SITE_ID, "",
				TermId.LOCATION_ID.getId(), factors));

			measurementDataList.add(createMeasurementData(ENTRY, String.valueOf(i),
				TermId.ENTRY_NO.getId(), factors));

			measurementDataList.add(createMeasurementData(GID,
				String.valueOf(random.nextInt(1000)), TermId.GID.getId(), factors));

			measurementDataList.add(createMeasurementData(DESIG,
				RandomStringUtils.randomAlphabetic(10), TermId.DESIG.getId(), factors));

			measurementDataList.add(createMeasurementData(CROSS, "-", TermId.CROSS.getId(),
				factors));

			measurementDataList.add(createMeasurementData(PLOT, String.valueOf(i),
				TermId.PLOT_NO.getId(), factors));

			measurementDataList.add(createMeasurementData(OBS_UNIT_ID, "PLOT010203P" + String.valueOf(i),
				TermId.OBS_UNIT_ID.getId(), factors));

			measurementDataList.add(createMeasurementData(BLOCK, "", TermId.BLOCK_NO.getId(),
				factors));

			measurementDataList.add(createMeasurementData(REP, "", TermId.REP_NO.getId(),
				factors));

			measurementRow.setDataList(measurementDataList);
			observations.add(measurementRow);
		}

		return observations;

	}

	private MeasurementVariable createMeasurementVariable(
		final int termId, final String name, final String description,
		final String scale, final String method, final String property, final String dataType, final String value, final String label,
		final int dataTypeId, final PhenotypicType role, final boolean isAnalysisVariable) {
		final MeasurementVariable variable =
			new MeasurementVariable(termId, name, description, scale, method, property, dataType, value, label);
		variable.setRole(role);
		variable.setDataTypeId(dataTypeId);
		setDefaultVariableType(variable, isAnalysisVariable);
		return variable;
	}

	private void setDefaultVariableType(final MeasurementVariable variable, final boolean isAnalysisVariable) {
		if (variable.getRole() == PhenotypicType.STUDY) {
			variable.setVariableType(VariableType.STUDY_DETAIL);
		} else if (variable.getRole() == PhenotypicType.TRIAL_ENVIRONMENT) {
			variable.setVariableType(VariableType.ENVIRONMENT_DETAIL);
		} else if (variable.getRole() == PhenotypicType.GERMPLASM) {
			variable.setVariableType(VariableType.GERMPLASM_DESCRIPTOR);
		} else if (variable.getRole() == PhenotypicType.TRIAL_DESIGN) {
			variable.setVariableType(VariableType.EXPERIMENTAL_DESIGN);
		} else if (variable.getRole() == PhenotypicType.VARIATE && isAnalysisVariable) {
			variable.setVariableType(VariableType.ANALYSIS);
		} else if (variable.getRole() == PhenotypicType.VARIATE && !isAnalysisVariable) {
			variable.setVariableType(VariableType.TRAIT);
		}
	}

	private MeasurementData createMeasurementData(
		final String label, final String value, final int termId,
		final List<MeasurementVariable> variables) {
		final MeasurementData data = new MeasurementData(label, value);
		data.setMeasurementVariable(getMeasurementVariable(termId, variables));
		return data;
	}

	private MeasurementVariable getMeasurementVariable(final int termId, final List<MeasurementVariable> variables) {
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

}
