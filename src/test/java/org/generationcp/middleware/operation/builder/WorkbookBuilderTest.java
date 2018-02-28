/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.operation.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.data.initializer.MeasurementVariableTestDataInitializer;
import org.generationcp.middleware.data.initializer.VariableListTestDataInitializer;
import org.generationcp.middleware.data.initializer.WorkbookTestDataInitializer;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.operation.transformer.etl.MeasurementVariableTransformer;
import org.generationcp.middleware.operation.transformer.etl.VariableTypeListTransformer;
import org.generationcp.middleware.pojos.ErrorCode;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.service.api.DataImportService;
import org.generationcp.middleware.service.api.FieldbookService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class WorkbookBuilderTest extends IntegrationTestBase {

	private static final Logger LOG = LoggerFactory.getLogger(WorkbookBuilderTest.class);
	private static final String CROP_PREFIX = "ABCD";

	@Autowired
	private DataImportService dataImportService;

	@Autowired
	private FieldbookService fieldbookService;

	private WorkbookBuilder workbookBuilder;

	private MeasurementVariableTransformer measurementVariableTransformer;

	private StandardVariableBuilder standardVariableBuilder;

	private VariableTypeListTransformer variableTypeListTransformer;

	private static final int SITE_SOIL_PH = 8270;
	private static final int CRUST = 20310;
	private static final int CUSTOM_VARIATE = 18020;

	private static final String PROGRAM_UUID = "12345678";

	@BeforeClass
	public static void setUpOnce() {
		// Variable caching relies on the context holder to determine current
		// crop database in use
		ContextHolder.setCurrentCrop("maize");
		ContextHolder.setCurrentProgram(WorkbookBuilderTest.PROGRAM_UUID);
	}

	@Before
	public void setUp() throws Exception {
		if (this.workbookBuilder == null) {
			this.workbookBuilder = new WorkbookBuilder(this.sessionProvder);
		}

		if (this.measurementVariableTransformer == null) {
			this.measurementVariableTransformer = new MeasurementVariableTransformer(this.sessionProvder);
		}

		if (this.standardVariableBuilder == null) {
			this.standardVariableBuilder = new StandardVariableBuilder(this.sessionProvder);
		}

		if (this.variableTypeListTransformer == null) {
			this.variableTypeListTransformer = new VariableTypeListTransformer(this.sessionProvder);
		}
	}

	@Test
	public void testCheckingOfMeasurementDatasetWithError() {
		try {
			this.workbookBuilder.checkMeasurementDataset(null);
		} catch (final MiddlewareQueryException e) {
			WorkbookBuilderTest.LOG.error(e.getMessage(), e);
			Assert.assertEquals("Expected code ", ErrorCode.STUDY_FORMAT_INVALID.getCode(), e.getCode());
		}
	}

	@Test
	public void testCheckingOfMeasurementDatasetWithoutError() {
		boolean hasError = false;
		try {
			this.workbookBuilder.checkMeasurementDataset(1);
		} catch (final MiddlewareQueryException e) {
			WorkbookBuilderTest.LOG.error(e.getMessage(), e);
			Assert.fail("Expected no error but got one");
			hasError = true;
		}
		Assert.assertFalse("Expected no error but got one", hasError);
	}

	@Ignore
	@Test
	public void testGetTrialObservationsForNursery() throws MiddlewareException {
		final Workbook workbook = WorkbookTestDataInitializer.getTestWorkbook(10, StudyType.N);

		final int id = this.dataImportService.saveDataset(workbook, WorkbookBuilderTest.PROGRAM_UUID,
				WorkbookBuilderTest.CROP_PREFIX);

		final Workbook createdWorkbook = this.fieldbookService.getNurseryDataSet(id);

		Assert.assertTrue("Expected correct values for constants but did not match with old workbook.",
				this.areConstantsMatch(workbook.getConstants(), createdWorkbook.getConstants()));

	}

	private boolean areConstantsMatch(final List<MeasurementVariable> constants,
			final List<MeasurementVariable> constants2) {
		if (constants != null && constants2 != null) {
			for (final MeasurementVariable var : constants) {
				if (!this.isMactchInNewConstantList(constants2, var.getTermId(), var.getValue())) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean isMactchInNewConstantList(final List<MeasurementVariable> constants, final int termId,
			final String value) {
		if (constants != null) {
			for (final MeasurementVariable var : constants) {
				if (var.getTermId() == termId) {
					return var.getValue().equals(value);
				}
			}
		}
		return false;
	}

	@Ignore
	@Test
	public void testGetTrialObservationsForTrial() throws MiddlewareException {
		final Workbook workbook = WorkbookTestDataInitializer.getTestWorkbook(10, StudyType.T);

		final int id = this.dataImportService.saveDataset(workbook, WorkbookBuilderTest.PROGRAM_UUID,
				WorkbookBuilderTest.CROP_PREFIX);

		final Workbook createdWorkbook = this.fieldbookService.getTrialDataSet(id);

		Assert.assertTrue("Expected correct values for trial observations but did not match with old workbook.",
				this.areConstantsCorrect(createdWorkbook.getConstants(), createdWorkbook.getTrialObservations()));
	}

	private boolean areConstantsCorrect(final List<MeasurementVariable> constants,
			final List<MeasurementRow> trialObservations) {
		if (trialObservations != null && constants != null) {
			for (final MeasurementRow row : trialObservations) {
				return this.areConstantsInRow(row.getDataList(), constants);
			}
		}
		return false;
	}

	private boolean areConstantsInRow(final List<MeasurementData> dataList, final List<MeasurementVariable> constants) {
		for (final MeasurementVariable var : constants) {
			for (final MeasurementData data : dataList) {
				if (data.getMeasurementVariable().getTermId() == var.getTermId() && !data.getValue().equals("1")) {
					return false;
				}
			}
		}
		return true;
	}

	@Test
	public void testPopulateMeasurementData_AllEmptyList() throws MiddlewareQueryException {
		final List<MeasurementVariable> measurementVariableList = new ArrayList<MeasurementVariable>();
		final VariableList variableList = new VariableList();
		final List<MeasurementData> measurementDataList = new ArrayList<MeasurementData>();
		this.workbookBuilder.populateMeasurementData(measurementVariableList, variableList, measurementDataList);
		Assert.assertTrue("Measurement data should be empty", measurementDataList.isEmpty());
	}

	@Test
	public void testPopulateMeasurementData_EmptyMeasurementVariableList() throws MiddlewareException {
		final List<MeasurementVariable> measurementVariableList = new ArrayList<MeasurementVariable>();
		final VariableList variableList = this.createVariableList(this.createMeasurementVariableList());
		final List<MeasurementData> measurementDataList = new ArrayList<MeasurementData>();
		this.workbookBuilder.populateMeasurementData(measurementVariableList, variableList, measurementDataList);
		Assert.assertTrue("Measurement data should be empty", measurementDataList.isEmpty());
	}

	@Test
	public void testPopulateMeasurementData_EmptyVariableList() throws MiddlewareException {
		final List<MeasurementVariable> measurementVariableList = this.createMeasurementVariableList();
		final VariableList variableList = new VariableList();
		final List<MeasurementData> measurementDataList = new ArrayList<MeasurementData>();
		this.workbookBuilder.populateMeasurementData(measurementVariableList, variableList, measurementDataList);
		Assert.assertFalse("Measurement data should not be empty", measurementDataList.isEmpty());
		for (final MeasurementData measurementData : measurementDataList) {
			Assert.assertEquals("Measurement data value should be empty", "", measurementData.getValue());
		}
	}

	@Test
	public void testAddMeasurementDataForFactors() {
		final List<MeasurementVariable> factorList = new ArrayList<>(Arrays.asList(
				MeasurementVariableTestDataInitializer.createMeasurementVariable(TermId.CROSS.getId(), "CROSS")));
		final VariableList factors = VariableListTestDataInitializer.createVariableList();
		final List<MeasurementData> measurementDataList = new ArrayList<>();
		this.workbookBuilder.addMeasurementDataForFactors(factorList, false, null, factors, measurementDataList);
		Assert.assertEquals("List should not be empty.", measurementDataList.size(), 1);
		final MeasurementData measurementData = measurementDataList.get(0);
		Assert.assertEquals(TermId.CROSS.name(), measurementData.getLabel());
		Assert.assertEquals(factors.getVariables().get(0).getValue(), measurementData.getValue());
		Assert.assertNull(measurementData.getcValueId());
	}

	@Test
	public void testAddMeasurementDataForFactorsForCategoricalVariable() {
		final List<MeasurementVariable> factorList = new ArrayList<>(Arrays.asList(
				MeasurementVariableTestDataInitializer.createMeasurementVariable(TermId.CROSS.getId(), "CROSS")));
		final VariableList factors = VariableListTestDataInitializer.createVariableListWithCategoricalVariable();
		final List<MeasurementData> measurementDataList = new ArrayList<>();
		this.workbookBuilder.addMeasurementDataForFactors(factorList, false, null, factors, measurementDataList);
		Assert.assertEquals("List should not be empty.", measurementDataList.size(), 1);
		final MeasurementData measurementData = measurementDataList.get(0);
		Assert.assertEquals(TermId.CROSS.name(), measurementData.getLabel());
		Assert.assertEquals(factors.getVariables().get(0).getValue(), measurementData.getValue());
		Assert.assertEquals(factors.getVariables().get(0).getValue(), measurementData.getcValueId());
	}

	@Test
	public void testAddMeasurementDataForFactorsNotFound() {
		final List<MeasurementVariable> factorList = new ArrayList<>(
				Arrays.asList(MeasurementVariableTestDataInitializer.createMeasurementVariable(TermId.CROSS.getId(),
						TermId.CROSS.name(), "CROSS")));
		final VariableList factors = new VariableList();
		final List<MeasurementData> measurementDataList = new ArrayList<>();
		this.workbookBuilder.addMeasurementDataForFactors(factorList, false, null, factors, measurementDataList);
		Assert.assertEquals("List should not be empty.", measurementDataList.size(), 1);
		final MeasurementData measurementData = measurementDataList.get(0);
		Assert.assertEquals(TermId.CROSS.name(), measurementData.getLabel());
		Assert.assertEquals("", measurementData.getValue());
		Assert.assertNull(measurementData.getcValueId());
	}

	@Test
	public void testAddMeasurementDataForFactorsNotFoundPlotId() {
		final List<MeasurementVariable> factorList = new ArrayList<>(
				Arrays.asList(MeasurementVariableTestDataInitializer.createMeasurementVariable(TermId.PLOT_ID.getId(),
						TermId.PLOT_ID.name(), "PLOT")));
		final Experiment experiment = new Experiment();
		experiment.setPlotId("plot id 1");
		final VariableList factors = new VariableList();
		final List<MeasurementData> measurementDataList = new ArrayList<>();
		this.workbookBuilder.addMeasurementDataForFactors(factorList, false, experiment, factors, measurementDataList);
		Assert.assertEquals("List should not be empty.", measurementDataList.size(), 1);
		final MeasurementData measurementData = measurementDataList.get(0);
		Assert.assertEquals(TermId.PLOT_ID.name(), measurementData.getLabel());
		Assert.assertEquals(experiment.getPlotId(), measurementData.getValue());
		Assert.assertNull(measurementData.getcValueId());
	}

	@Test
	public void testPopulateMeasurementData() throws MiddlewareException {
		final List<MeasurementVariable> measurementVariableList = this.createMeasurementVariableList();
		final VariableList variableList = this.createVariableList(measurementVariableList);
		final List<MeasurementData> measurementDataList = new ArrayList<MeasurementData>();
		this.workbookBuilder.populateMeasurementData(measurementVariableList, variableList, measurementDataList);
		Assert.assertFalse("Measurement data should not be empty", measurementDataList.isEmpty());
		for (final MeasurementData measurementData : measurementDataList) {
			if (TermId.CATEGORICAL_VARIABLE.getId() != measurementData.getMeasurementVariable().getDataTypeId()) {
				Assert.assertNull("Categorical value id should be null", measurementData.getcValueId());
			} else {
				Assert.assertEquals("Categorical value id should equal to " + measurementData.getValue(),
						measurementData.getValue(), measurementData.getcValueId());
			}
		}
	}

	private VariableList createVariableList(final List<MeasurementVariable> measurementVariableList)
			throws MiddlewareException {
		final VariableList variableList = new VariableList();
		int count = 0;
		for (final MeasurementVariable measurementVariable : measurementVariableList) {
			count++;
			String value = Integer.toString(count);
			if (TermId.CHARACTER_VARIABLE.getId() == measurementVariable.getDataTypeId().intValue()) {
				value = "CODE_" + value;
			}
			final Variable variable = this.createVariable(measurementVariable, value);
			variableList.add(variable);
		}
		return variableList;
	}

	private Variable createVariable(final MeasurementVariable measurementVariable, final String value)
			throws MiddlewareException {
		final Variable variable = new Variable();
		final DMSVariableType variableType = this.createVariableType(measurementVariable);
		variable.setVariableType(variableType);
		variable.setValue(value);
		return variable;
	}

	private DMSVariableType createVariableType(final MeasurementVariable measurementVariable)
			throws MiddlewareException {
		final DMSVariableType variableType = this.transformMeasurementVariable(measurementVariable,
				this.getStandardVariable(measurementVariable.getTermId()));
		return variableType;
	}

	private List<MeasurementVariable> createMeasurementVariableList() throws MiddlewareException {
		final List<MeasurementVariable> measurementVariableList = new ArrayList<MeasurementVariable>();
		measurementVariableList.add(this.getMeasurementVariable(TermId.ENTRY_CODE.getId(), true));
		measurementVariableList.add(this.getMeasurementVariable(TermId.ENTRY_NO.getId(), true));
		measurementVariableList.add(this.getMeasurementVariable(TermId.GID.getId(), true));
		measurementVariableList.add(this.getMeasurementVariable(TermId.PLOT_ID.getId(), true));
		measurementVariableList.add(this.getMeasurementVariable(TermId.REP_NO.getId(), true));
		measurementVariableList.add(this.getMeasurementVariable(TermId.PLOT_NO.getId(), true));
		measurementVariableList.add(this.getMeasurementVariable(WorkbookBuilderTest.SITE_SOIL_PH, false));
		measurementVariableList.add(this.getMeasurementVariable(WorkbookBuilderTest.CRUST, false));
		return measurementVariableList;
	}

	private MeasurementVariable getMeasurementVariable(final int termId, final boolean isFactor)
			throws MiddlewareException {
		return this.measurementVariableTransformer.transform(this.getStandardVariable(termId), isFactor);
	}

	private StandardVariable getStandardVariable(final int id) throws MiddlewareException {
		final StandardVariable standardVariable = this.standardVariableBuilder.create(id, "1234567");
		standardVariable.setPhenotypicType(PhenotypicType.VARIATE);
		return standardVariable;
	}

	private DMSVariableType transformMeasurementVariable(final MeasurementVariable measurementVariable,
			final StandardVariable standardVariable) {
		return new DMSVariableType(measurementVariable.getName(), measurementVariable.getDescription(),
				standardVariable, 0);
	}

	@Ignore
	@Test
	public void testRemoveTrialDatasetVariables() throws MiddlewareException {
		final Workbook workbook = WorkbookTestDataInitializer.getTestWorkbook(10, StudyType.T);
		// add trial instance (also added in conditions)
		workbook.getFactors().add(WorkbookTestDataInitializer.createTrialInstanceMeasurementVariable(1));
		final VariableTypeList factorsVariableTypeList = this.variableTypeListTransformer
				.transform(workbook.getFactors(), WorkbookBuilderTest.PROGRAM_UUID);
		final VariableTypeList conditionsVariableTypeList = this.variableTypeListTransformer
				.transform(workbook.getConditions(), WorkbookBuilderTest.PROGRAM_UUID);
		final VariableTypeList constantsVariableTypeList = this.variableTypeListTransformer
				.transform(workbook.getConstants(), WorkbookBuilderTest.PROGRAM_UUID);
		final VariableList conditions = this.transformMeasurementVariablesToVariableList(workbook.getConditions(),
				conditionsVariableTypeList);
		final VariableList constants = this.transformMeasurementVariablesToVariableList(workbook.getConstants(),
				constantsVariableTypeList);
		// find the trial instance variable before removing it as a factor
		final DMSVariableType trialInstance = factorsVariableTypeList.findById(TermId.TRIAL_INSTANCE_FACTOR.getId());
		final VariableList toBeDeleted = new VariableList();
		toBeDeleted.addAll(conditions);
		toBeDeleted.addAll(constants);
		// call the method to test: remove trial instance
		final VariableTypeList finalFactors = this.workbookBuilder.removeTrialDatasetVariables(factorsVariableTypeList,
				toBeDeleted);
		// verify if the trial instance is no longer found in the final factors
		Assert.assertFalse(finalFactors.getVariableTypes().contains(trialInstance));

	}

	@Test
	public void testSetMeasurementVarRoles() {
		final List<MeasurementVariable> measurementVariableLists = new ArrayList<MeasurementVariable>();
		final MeasurementVariable measurementVar = new MeasurementVariable();
		measurementVariableLists.add(measurementVar);
		this.workbookBuilder.setMeasurementVarRoles(measurementVariableLists, false, true);
		for (final MeasurementVariable var : measurementVariableLists) {
			Assert.assertEquals("Should have a phenotype role of variate since its not a factor", var.getRole(),
					PhenotypicType.VARIATE);
		}

		this.workbookBuilder.setMeasurementVarRoles(measurementVariableLists, true, true);
		for (final MeasurementVariable var : measurementVariableLists) {
			Assert.assertEquals("Should have a phenotype role of STUDY", var.getRole(), PhenotypicType.STUDY);
		}

		this.workbookBuilder.setMeasurementVarRoles(measurementVariableLists, true, false);
		for (final MeasurementVariable var : measurementVariableLists) {
			Assert.assertEquals("Should have a phenotype role of Trial Environment", var.getRole(),
					PhenotypicType.TRIAL_ENVIRONMENT);
		}
	}

	@Ignore
	@Test
	public void testBuildTrialObservations() {

		final Workbook workbook = WorkbookTestDataInitializer.getTestWorkbook(10, StudyType.N);

		this.dataImportService.saveDataset(workbook, true, false, WorkbookBuilderTest.PROGRAM_UUID,
				WorkbookBuilderTest.CROP_PREFIX);

		final List<MeasurementRow> result = this.workbookBuilder.buildTrialObservations(workbook.getTrialDatasetId(),
				workbook.getTrialConditions(), workbook.getTrialConstants());

		Assert.assertEquals(
				"The trial observation should only contain one measurement row since the study is Nursery Type", 1,
				result.size());
		Assert.assertEquals(5, result.get(0).getDataList().size());
		Assert.assertTrue(this.isTermIdExists(TermId.TRIAL_INSTANCE_FACTOR.getId(), result.get(0).getDataList()));
		Assert.assertTrue(this.isTermIdExists(TermId.COOPERATOOR_ID.getId(), result.get(0).getDataList()));
		Assert.assertTrue(this.isTermIdExists(TermId.COOPERATOR.getId(), result.get(0).getDataList()));
		Assert.assertTrue(this.isTermIdExists(TermId.LOCATION_ID.getId(), result.get(0).getDataList()));
		Assert.assertTrue(this.isTermIdExists(TermId.TRIAL_LOCATION.getId(), result.get(0).getDataList()));

	}

	@Ignore
	@Test
	public void testBuildConditionVariablesOnTrial() {
		// final int noOfObservations, final StudyType studyType, final String
		// studyName, final int trialNo, final boolean hasMultipleLocations
		final Workbook workbook = WorkbookTestDataInitializer.createTestWorkbook(10, StudyType.T, "Test study", 1,
				true);
		this.dataImportService.saveDataset(workbook, true, false, WorkbookBuilderTest.PROGRAM_UUID,
				WorkbookBuilderTest.CROP_PREFIX);

		// create measurement variables of condition types
		final List<MeasurementVariable> conditionMeasurementVariableList = workbook.getStudyConditions();
		final VariableTypeList conditionVariableTypeList = this.variableTypeListTransformer
				.transform(conditionMeasurementVariableList, WorkbookBuilderTest.PROGRAM_UUID);

		// create measurement variables of trial environement types
		final List<MeasurementVariable> trialEnvironmentMeasurementVariableList = workbook.getTrialConditions();
		final VariableTypeList trialEnvironmentVariableTypeList = this.variableTypeListTransformer
				.transform(trialEnvironmentMeasurementVariableList, WorkbookBuilderTest.PROGRAM_UUID);

		final VariableList conditionVariables = this.transformMeasurementVariablesToVariableList(
				conditionMeasurementVariableList, conditionVariableTypeList);
		final VariableList trialEnvironmentVariables = this.transformMeasurementVariablesToVariableList(
				trialEnvironmentMeasurementVariableList, trialEnvironmentVariableTypeList);

		final List<MeasurementVariable> result = this.workbookBuilder.buildConditionVariables(conditionVariables,
				trialEnvironmentVariables, true);

		int noOfConditionsWithTrialEnvironmentPhenotypicType = 0;
		for (final MeasurementVariable measurementVariable : result) {
			if (measurementVariable.getRole().equals(PhenotypicType.TRIAL_ENVIRONMENT)) {
				noOfConditionsWithTrialEnvironmentPhenotypicType++;
			}
		}

		Assert.assertTrue(
				"Expecting that the number of condition with trial environment phenotypic type is "
						+ trialEnvironmentVariables.size() + " but returned "
						+ noOfConditionsWithTrialEnvironmentPhenotypicType,
				trialEnvironmentVariables.size() >= noOfConditionsWithTrialEnvironmentPhenotypicType);

		final List<String> trialEnvironmentVariableNames = new ArrayList<String>();
		for (final Variable variable : trialEnvironmentVariables.getVariables()) {
			trialEnvironmentVariableNames.add(variable.getVariableType().getLocalName());
		}

		for (final MeasurementVariable measurementVariable : result) {
			if (trialEnvironmentVariableNames.contains(measurementVariable.getName())) {
				Assert.assertTrue(
						"Expecting that TRIAL_ENVIRONMENT is set as the phenotypic type for trial environment variables",
						measurementVariable.getRole().equals(PhenotypicType.TRIAL_ENVIRONMENT));
			}
		}
	}

	private boolean isTermIdExists(final int termId, final List<MeasurementData> dataList) {
		for (final MeasurementData data : dataList) {
			if (data.getMeasurementVariable().getTermId() == termId) {
				return true;
			}
		}
		return false;
	}

	// derived from VariableListTransformer.transformTrialEnvironment (but with
	// no specific role to filter)
	private VariableList transformMeasurementVariablesToVariableList(final List<MeasurementVariable> mVarList,
			final VariableTypeList variableTypeList) {
		final VariableList variableList = new VariableList();
		if (mVarList != null && variableTypeList != null && variableTypeList.getVariableTypes() != null) {
			if (mVarList.size() == variableTypeList.getVariableTypes().size()) {

				final List<DMSVariableType> varTypes = variableTypeList.getVariableTypes();
				for (int i = 0, l = mVarList.size(); i < l; i++) {
					DMSVariableType varTypeFinal = null;
					final String value = mVarList.get(i).getValue();
					for (final DMSVariableType varType : varTypes) {
						if (mVarList.get(i).getTermId() == varType.getId()) {
							varTypeFinal = varType;
						}
					}
					variableList.add(new Variable(varTypeFinal, value));
				}
			}
		}

		return variableList;
	}

	@Test
	public void testCreateMeasurementVariable() {
		final int termId = TermId.TRIAL_INSTANCE_FACTOR.getId();
		final StandardVariable stdVariable = this.createStandardVariable(termId, "TRIAL INSTANCE",
				"Trial instance - enumerated (number)", "Trial instance", "Number", "Enumerated",
				DataType.NUMERIC_VARIABLE.getName());
		final VariableType varType = VariableType.ENVIRONMENT_DETAIL;
		final DmsProject project = this.createDmsProject(1);
		final int rank = 1;
		final ProjectProperty localNameProjectProp = this.createProjectProperty(project, varType.getId(), "VAR_NAME",
				rank);
		final String value = "1";
		final Double minRange = null;
		final Double maxRange = null;
		final MeasurementVariable measurementVariable = this.workbookBuilder.createMeasurementVariable(stdVariable,
				localNameProjectProp, value, minRange, maxRange, varType);
		Assert.assertNotNull(measurementVariable);
		Assert.assertEquals(stdVariable.getId(), measurementVariable.getTermId());
		Assert.assertEquals(localNameProjectProp.getAlias(), measurementVariable.getName());
		Assert.assertEquals(stdVariable.getDescription(), measurementVariable.getDescription());
		Assert.assertEquals(stdVariable.getProperty().getName(), measurementVariable.getProperty());
		Assert.assertEquals(stdVariable.getScale().getName(), measurementVariable.getScale());
		Assert.assertEquals(stdVariable.getMethod().getName(), measurementVariable.getMethod());
		Assert.assertEquals(stdVariable.getDataType().getName(), measurementVariable.getDataType());
		Assert.assertEquals(value, measurementVariable.getValue());
		Assert.assertEquals(minRange, measurementVariable.getMinRange());
		Assert.assertEquals(maxRange, measurementVariable.getMaxRange());
		Assert.assertTrue(measurementVariable.isFactor());
		Assert.assertEquals(stdVariable.getDataType().getId(), measurementVariable.getDataTypeId().intValue());
		Assert.assertEquals(this.workbookBuilder.getMeasurementVariableTransformer()
				.transformPossibleValues(stdVariable.getEnumerations()), measurementVariable.getPossibleValues());
		Assert.assertEquals(varType.getRole(), measurementVariable.getRole());
		Assert.assertEquals(varType, measurementVariable.getVariableType());
	}

	@Test
	public void testGetMeasurementDataWithSample() {

		final int experimentId = 999;
		final String samplesValue = "some value";

		final Map<Integer, String> samplesMap = new HashMap<>();
		samplesMap.put(experimentId, samplesValue);

		final MeasurementData samplesMeasurementData = this.workbookBuilder.getMeasurementDataWithSample(samplesMap,
				experimentId);

		Assert.assertEquals(String.valueOf(TermId.SAMPLES.getId()), samplesMeasurementData.getLabel());
		Assert.assertFalse(samplesMeasurementData.isEditable());
		Assert.assertEquals(samplesValue, samplesMeasurementData.getValue());
		Assert.assertEquals("C", samplesMeasurementData.getDataType());

		final MeasurementVariable samplesMeasurementVariable = samplesMeasurementData.getMeasurementVariable();
		Assert.assertEquals(TermId.SAMPLES.getId(), samplesMeasurementVariable.getTermId());
		Assert.assertEquals(String.valueOf(TermId.SAMPLES.getId()), samplesMeasurementVariable.getName());
		Assert.assertEquals(String.valueOf(TermId.SAMPLES.getId()), samplesMeasurementVariable.getLabel());
		Assert.assertTrue(samplesMeasurementVariable.isFactor());
		Assert.assertEquals(Integer.valueOf(DataType.CHARACTER_VARIABLE.getId()),
				samplesMeasurementVariable.getDataTypeId());
		Assert.assertNotNull(samplesMeasurementVariable.getPossibleValues());

	}

	private DmsProject createDmsProject(final int id) {
		final DmsProject project = new DmsProject();
		project.setProjectId(id);
		return project;
	}

	private ProjectProperty createProjectProperty(final DmsProject project, final Integer typeId, final String value,
			final int rank) {
		final ProjectProperty projectProperty = new ProjectProperty();
		projectProperty.setProject(project);
		projectProperty.setTypeId(typeId);
		projectProperty.setValue(value);
		projectProperty.setRank(rank);
		return projectProperty;
	}

	private StandardVariable createStandardVariable(final int id, final String name, final String description,
			final String property, final String scale, final String method, final String datatype) {
		final StandardVariable stdVariable = new StandardVariable();
		stdVariable.setId(id);
		stdVariable.setName(name);
		stdVariable.setDescription(description);
		stdVariable.setProperty(this.createTerm(100, property));
		stdVariable.setScale(this.createTerm(200, scale));
		stdVariable.setMethod(this.createTerm(300, method));
		stdVariable.setDataType(this.createTerm(DataType.getByName(datatype).getId(), datatype));
		return stdVariable;
	}

	private Term createTerm(final Integer id, final String name) {
		final Term term = new Term();
		term.setId(id);
		term.setName(name);
		return term;
	}

}
