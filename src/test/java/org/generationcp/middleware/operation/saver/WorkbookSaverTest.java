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

package org.generationcp.middleware.operation.saver;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.dao.LocationDAO;
import org.generationcp.middleware.data.initializer.MeasurementRowTestDataInitializer;
import org.generationcp.middleware.data.initializer.MeasurementVariableTestDataInitializer;
import org.generationcp.middleware.data.initializer.ValueReferenceTestDataInitializer;
import org.generationcp.middleware.data.initializer.WorkbookTestDataInitializer;
import org.generationcp.middleware.domain.dms.*;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.Method;
import org.generationcp.middleware.domain.ontology.Property;
import org.generationcp.middleware.domain.ontology.Scale;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.ontology.OntologyDataHelper;
import org.generationcp.middleware.operation.transformer.etl.VariableTypeListTransformer;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.utils.test.TestOutputFormatter;
import org.generationcp.middleware.utils.test.VariableTypeListDataUtil;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.*;

public class WorkbookSaverTest extends TestOutputFormatter {

	private static WorkbookSaver workbookSaver;

	private static final String COOPERATOR = "Cooperator";
	private static final int COOPERATOR_NAME = 8373;

	@BeforeClass
	public static void setUp() {
		WorkbookSaverTest.workbookSaver = new WorkbookSaver(Mockito.mock(HibernateSessionProvider.class));
	}

	@Test
	public void testPropagationOfStudyFactorsWithStudyVariablesAndWOStudyFactorWithEnvironmentAndVariates() {
		final VariableTypeList effectVariables = VariableTypeListDataUtil.createPlotVariableTypeList(false);
		final VariableTypeList trialVariables = VariableTypeListDataUtil.createVariableTypeList(true);

		final VariableTypeList plotVariables =
				WorkbookSaverTest.workbookSaver.propagateTrialFactorsIfNecessary(effectVariables, trialVariables);

		Assert.assertEquals("Expected an aditional entry for trial instance but found none.", effectVariables.size() + 1,
				plotVariables.size());
		Assert.assertFalse("Expected non trial environment and non constant variables but found at least one.",
				this.areStudyAndConstantsInList(plotVariables, effectVariables));
	}

	private boolean areStudyAndConstantsInList(final VariableTypeList plotVariables, final VariableTypeList effectVariables) {
		if (plotVariables != null) {
			for (final DMSVariableType var : plotVariables.getVariableTypes()) {
				if (var.getStandardVariable().getId() != TermId.TRIAL_INSTANCE_FACTOR.getId() && (
						PhenotypicType.TRIAL_ENVIRONMENT == var.getRole() || PhenotypicType.VARIATE == var.getRole() && !this
								.isInOriginalPlotDataset(var.getStandardVariable().getId(), effectVariables))) {
					return true;
				}
			}
		}

		return false;
	}

	private boolean isInOriginalPlotDataset(final int id, final VariableTypeList effectVariables) {
		if (effectVariables != null) {
			for (final DMSVariableType var : effectVariables.getVariableTypes()) {
				if (var.getStandardVariable().getId() == id) {
					return true;
				}
			}
		}
		return false;
	}

	@Test
	public void testPropagationOfStudyFactorsWithStudyVariablesAndWOStudyFactorWOEnvironmentAndVariates() {
		final VariableTypeList effectVariables = VariableTypeListDataUtil.createPlotVariableTypeList(false);
		final VariableTypeList trialVariables = VariableTypeListDataUtil.createVariableTypeList(false);

		final VariableTypeList plotVariables =
				WorkbookSaverTest.workbookSaver.propagateTrialFactorsIfNecessary(effectVariables, trialVariables);

		Assert.assertEquals("Expected an aditional entry for trial instance but found none.", effectVariables.size() + 1,
				plotVariables.size());
		Assert.assertFalse("Expected non trial environment and non constant variables but found at least one.",
				this.areStudyAndConstantsInList(plotVariables, effectVariables));
	}

	@Test
	public void testPropagationOfStudyFactorsWithStudyVariablesAndStudyFactor() {
		final VariableTypeList effectVariables = VariableTypeListDataUtil.createPlotVariableTypeList(true);
		final VariableTypeList trialVariables = VariableTypeListDataUtil.createVariableTypeList(false);

		final VariableTypeList plotVariables =
				WorkbookSaverTest.workbookSaver.propagateTrialFactorsIfNecessary(effectVariables, trialVariables);

		Assert.assertEquals("Expected no change in the plot dataset but found one.", effectVariables.size(), plotVariables.size());
	}

	@Test
	public void testPropagationOfStudyFactorsWOStudyVariablesWithStudyFactor() {
		final VariableTypeList effectVariables = VariableTypeListDataUtil.createPlotVariableTypeList(true);
		final VariableTypeList trialVariables = null;

		final VariableTypeList plotVariables =
				WorkbookSaverTest.workbookSaver.propagateTrialFactorsIfNecessary(effectVariables, trialVariables);

		Assert.assertEquals("Expected no change in the plot dataset but found one.", effectVariables.size(), plotVariables.size());
	}

	@Test
	public void testRemoveConstantsVariables() {
		final Workbook workbook = WorkbookTestDataInitializer.createTestWorkbook(2, StudyTypeDto.getNurseryDto(), "TEST STUDY", 1, true);
		final VariableTypeList variableTypeList = this.createVariableTypeList(workbook.getConstants(), 1);
		Assert.assertTrue("The variable type list should have contents.", variableTypeList.getVariableTypes().size() > 0);
		WorkbookSaverTest.workbookSaver.removeConstantsVariables(variableTypeList, workbook.getConstants());
		Assert.assertEquals("All the variable should be removed.", 0, variableTypeList.getVariableTypes().size());
	}

	@Test
	public void testSetVariableListValues() {
		final Workbook workbook = WorkbookTestDataInitializer.createTestWorkbook(2, StudyTypeDto.getNurseryDto(), "TEST STUDY", 1, true);
		WorkbookTestDataInitializer.setTrialObservations(workbook);
		final VariableTypeList variableTypeList = this.createVariableTypeList(workbook.getConditions(), 1);
		final VariableList variableList = WorkbookSaverTest.workbookSaver.getVariableListTransformer()
				.transformTrialEnvironment(workbook.getTrialObservation(0), variableTypeList);

		for (final Variable variable : variableList.getVariables()) {
			// set values to null to check if the values are really set properly
			variable.setValue(null);
		}
		WorkbookSaverTest.workbookSaver.setVariableListValues(variableList, workbook.getConditions());

		for (final Variable variable : variableList.getVariables()) {
			Assert.assertNotNull(variable.getValue());
		}
	}

	@Test
	public void testSetCategoricalVariableValues() {
		final MeasurementVariable mvar = MeasurementVariableTestDataInitializer.createMeasurementVariable(1001, "1");
		mvar.setPossibleValues(ValueReferenceTestDataInitializer.createPossibleValues());
		final Variable variable = new Variable();
		WorkbookSaverTest.workbookSaver.setCategoricalVariableValues(mvar, variable);
		Assert.assertNotNull(variable.getValue());
		Assert.assertEquals("1", variable.getValue());
	}

	@Test
	public void testPropagationOfStudyFactorsWOStudyVariablesAndStudyFactor() {
		final VariableTypeList effectVariables = VariableTypeListDataUtil.createPlotVariableTypeList(false);
		final VariableTypeList variables = null;

		final VariableTypeList plotVariables = WorkbookSaverTest.workbookSaver.propagateTrialFactorsIfNecessary(effectVariables, variables);

		Assert.assertEquals("Expected no change in the plot dataset but found one.", effectVariables.size(), plotVariables.size());
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Test
	public void testSaveVariables() {

		final String programUUID = "abc";
		final String studyName = "nursery_1" + new Random().nextInt(10000);

		final Workbook workbook = WorkbookTestDataInitializer.createTestWorkbook(2, StudyTypeDto.getNurseryDto(), studyName, 1, true);
		final WorkbookSaver workbookSaver = Mockito.mock(WorkbookSaver.class, Mockito.CALLS_REAL_METHODS);

		final VariableTypeListTransformer transformer = Mockito.mock(VariableTypeListTransformer.class); // new
		// VariableTypeListTransformer(Mockito.mock(HibernateSessionProvider.class));

		final VariableTypeList conditionsVariableTypeList = this.createVariableTypeList(workbook.getTrialConditions(), 1);
		Mockito.doReturn(conditionsVariableTypeList).when(transformer).transform(workbook.getTrialConditions(), programUUID);

		final VariableTypeList nonFactorsVariableTypeList = this.createVariableTypeList(workbook.getNonTrialFactors(), 1);
		Mockito.doReturn(nonFactorsVariableTypeList).when(transformer).transform(workbook.getNonTrialFactors(), programUUID);

		final VariableTypeList factorsVariableTypeList = this.createVariableTypeList(workbook.getTrialFactors(), 1);
		Mockito.doReturn(factorsVariableTypeList).when(transformer)
				.transform(workbook.getTrialFactors(), workbook.getTrialConditions().size() + 1, programUUID);

		final VariableTypeList constantsVariableTypeList = this.createVariableTypeList(workbook.getTrialConstants(), 1);
		Mockito.doReturn(constantsVariableTypeList).when(transformer)
			.transform(workbook.getTrialConstants(), workbook.getTrialConditions().size() + workbook.getTrialFactors().size() + 1, programUUID);

		final VariableTypeList variatesVariableTypeList = this.createVariableTypeList(workbook.getVariates(), 1);
		Mockito.doReturn(variatesVariableTypeList).when(transformer)
			.transform(workbook.getVariates(), workbook.getNonTrialFactors().size() + 1, programUUID);

		final VariableTypeList entryDetailsVariableTypeList = this.createVariableTypeList(workbook.getEntryDetails(), 1);
		Mockito.doReturn(entryDetailsVariableTypeList).when(transformer).transform(workbook.getEntryDetails(), workbook.getNonTrialFactors().size() + workbook.getVariates().size() + 1, programUUID);

		Mockito.doReturn(transformer).when(workbookSaver).getVariableTypeListTransformer();

		final Map variableMap = workbookSaver.saveVariables(workbook, programUUID);

		final Map<String, VariableTypeList> variableTypeMap = (Map<String, VariableTypeList>) variableMap.get("variableTypeMap");
		final Map<String, List<MeasurementVariable>> measurementVariableMap =
				(Map<String, List<MeasurementVariable>>) variableMap.get("measurementVariableMap");
		final Map<String, List<String>> headerMap = (Map<String, List<String>>) variableMap.get("headerMap");

		final List<String> headers = headerMap.get("trialHeaders");

		final VariableTypeList types = variableTypeMap.get("trialVariableTypeList");
		final VariableTypeList variableTypes = variableTypeMap.get("trialVariables");
		final VariableTypeList effectVariables = variableTypeMap.get("effectVariables");

		final List<MeasurementVariable> measurementVariables = measurementVariableMap.get("trialMV");
		final List<MeasurementVariable> effectMV = measurementVariableMap.get("effectMV");

		Assert.assertNotEquals(0, headers.size());

		Assert.assertNotEquals(0, measurementVariables.size());
		Assert.assertNotEquals(0, effectMV.size());

		Assert.assertNotEquals(0, effectVariables.getVariableTypes().size());
		Assert.assertNotEquals(0, variableTypes.getVariableTypes().size());
		Assert.assertNotEquals(0, types.getVariableTypes().size());
	}

	private StandardVariable transformMeasurementVariableToVariable(final MeasurementVariable measurementVariable) {
		final StandardVariable standardVariable = new StandardVariable();

		standardVariable.setId(measurementVariable.getTermId());
		standardVariable.setName(measurementVariable.getName());
		standardVariable.setDescription(measurementVariable.getDescription());

		final int methodId = new Random().nextInt(10000);
		final int propertyId = new Random().nextInt(10000);
		final int scaleId = new Random().nextInt(10000);

		standardVariable.setMethod(new Method(new Term(methodId, measurementVariable.getMethod(), "Method Description")));
		standardVariable.setProperty(new Property(new Term(propertyId, measurementVariable.getProperty(), "Property Description")));
		standardVariable.setScale(new Scale(new Term(scaleId, measurementVariable.getScale(), "Scale Description")));
		standardVariable
				.setDataType(new Term(DataType.NUMERIC_VARIABLE.getId(), DataType.NUMERIC_VARIABLE.getName(), "Data Type Description"));
		standardVariable.setIsA(new Term(new Random().nextInt(1000), "IsA", "IsA Description"));
		standardVariable.setPhenotypicType(measurementVariable.getRole());
		standardVariable.setCropOntologyId("CO:100");
		standardVariable.setVariableTypes(new HashSet<>(new ArrayList<>(Collections
				.singletonList(OntologyDataHelper.mapFromPhenotype(measurementVariable.getRole(), measurementVariable.getProperty())))));

		return standardVariable;
	}

	private DMSVariableType transformToDMSVariableType(final MeasurementVariable measurementVariable, int rank) {
		final StandardVariable standardVariable = this.transformMeasurementVariableToVariable(measurementVariable);

		final DMSVariableType dmsVariableType =
				new DMSVariableType(measurementVariable.getName(), measurementVariable.getDescription(), standardVariable, rank++);
		dmsVariableType.setTreatmentLabel(measurementVariable.getTreatmentLabel());
		return dmsVariableType;
	}

	private VariableTypeList createVariableTypeList(final List<MeasurementVariable> measurementVariables, final int rank) {
		final VariableTypeList variableTypeList = new VariableTypeList();

		for (final MeasurementVariable measurementVariable : measurementVariables) {
			variableTypeList.add(this.transformToDMSVariableType(measurementVariable, rank));
		}
		return variableTypeList;
	}

	@Test
	public void testRemoveDeletedStudyObservations() {

		final String studyName = "nursery_1" + new Random().nextInt(10000);

		final Workbook workbook = WorkbookTestDataInitializer.createTestWorkbook(2, StudyTypeDto.getNurseryDto(), studyName, 1, true);
		final WorkbookSaver workbookSaver = Mockito.mock(WorkbookSaver.class, Mockito.CALLS_REAL_METHODS);

		final VariableTypeListTransformer transformer = Mockito.mock(VariableTypeListTransformer.class);

		workbook.setTrialObservations(this.createObservations(1, workbook));

		Mockito.doReturn(transformer).when(workbookSaver).getVariableTypeListTransformer();

		final MeasurementRow measurementRow = workbook.getTrialObservations().get(0);
		final List<MeasurementData> dataList = measurementRow.getDataList();
		for (final Iterator<MeasurementData> iterator = dataList.iterator(); iterator.hasNext(); ) {
			final MeasurementData measurementData = iterator.next();
			final MeasurementVariable measurementVariable = measurementData.getMeasurementVariable();

			if (measurementVariable != null && WorkbookSaverTest.COOPERATOR_NAME == measurementVariable.getTermId()) {
				measurementVariable.setOperation(Operation.DELETE);
			}

		}

		WorkbookSaverTest.workbookSaver.removeDeletedVariablesAndObservations(workbook);

		Assert.assertEquals(0, workbook.getTrialObservations().get(0).getMeasurementVariables().size());
	}

	@Test
	public void testAssignLocationVariableWithUnspecifiedLocationIfEmptyValueIsEmpty() {

		final LocationDAO locationDAO = Mockito.mock(LocationDAO.class);
		final VariableList variableList = new VariableList();

		// Set the LOCATION_ID variable value to empty
		final Variable locationVariable = this.createLocationVariable();
		locationVariable.setValue(null);
		variableList.add(locationVariable);

		final Location unspecifiedLocation = new Location();
		final int unspecifiedLocationlocid = 111;
		unspecifiedLocation.setLocid(unspecifiedLocationlocid);
		final List<Location> locations = Arrays.asList(unspecifiedLocation);
		Mockito.when(locationDAO.getByName(Location.UNSPECIFIED_LOCATION, Operation.EQUAL)).thenReturn(locations);

		workbookSaver.assignLocationVariableWithUnspecifiedLocationIfEmptyOrInvalid(variableList, locationDAO);

		Assert.assertEquals(String.valueOf(unspecifiedLocationlocid), locationVariable.getValue());

		// Invalid Location
		final List<Integer> invalidLocationId = new ArrayList<>();
		final int invalidLocationIdValue = 9016;
		invalidLocationId.add(invalidLocationIdValue);

		final List<Location> nullLocation = new ArrayList<>();
		Mockito.when(locationDAO.getByIds(invalidLocationId)).thenReturn(nullLocation);

		workbookSaver.assignLocationVariableWithUnspecifiedLocationIfEmptyOrInvalid(variableList, locationDAO);
		Assert.assertEquals(String.valueOf(unspecifiedLocationlocid), locationVariable.getValue());
	}

	@Test
	public void testAssignLocationVariableWithUnspecifiedLocationIfLocationIdExists() {

		final LocationDAO locationDAO = Mockito.mock(LocationDAO.class);
		final VariableList variableList = new VariableList();

		final Variable locationVariable = this.createLocationVariable();
		// Set the value of LOCATION_ID variable
		final String locationIdVariableValue = "999";
		locationVariable.setValue(locationIdVariableValue);
		variableList.add(locationVariable);

		// Existing Location
		final Location existingLocation = new Location();
		final int existingLocationIdValue = Integer.valueOf(locationIdVariableValue);
		existingLocation.setLocid(existingLocationIdValue);

		final List<Integer> existingLocationId = new ArrayList<>();
		existingLocationId.add(Integer.valueOf(locationIdVariableValue));

		final List<Location> retrievedLocation = new ArrayList<>();
		retrievedLocation.add(existingLocation);

		Mockito.when(locationDAO.getByIds(existingLocationId)).thenReturn(retrievedLocation);
		workbookSaver.assignLocationVariableWithUnspecifiedLocationIfEmptyOrInvalid(variableList, locationDAO);

		Assert.assertEquals(Integer.valueOf(locationIdVariableValue), retrievedLocation.get(0).getLocid());

	}

	@Test
	public void testSetDatasetStocks() {
		final Workbook workbook = WorkbookTestDataInitializer.getTestWorkbook();
		workbook.getStudyDetails().setId(1);

		final Random random = new Random();
		final StockModel stock = new StockModel(random.nextInt(), random.nextInt(), RandomStringUtils.randomAlphabetic(10), "1",
			RandomStringUtils.randomAlphabetic(10), false);
		workbook.setObservations(MeasurementRowTestDataInitializer.
			createMeasurementRowList(TermId.ENTRY_NO.getId(), TermId.ENTRY_NO.name(), "1",
				MeasurementVariableTestDataInitializer.createMeasurementVariable(TermId.ENTRY_NO.getId(), "1")));
		workbookSaver.setDatasetStocks(workbook, Collections.singletonList(stock));
		Assert.assertEquals(stock.getStockId().toString(), String.valueOf(workbook.getObservations().get(0).getStockId()));
	}

	private Variable createLocationVariable() {

		final DMSVariableType locationVariableType = new DMSVariableType();
		final StandardVariable standardVariable = new StandardVariable();
		standardVariable.setId(TermId.LOCATION_ID.getId());
		locationVariableType.setStandardVariable(standardVariable);

		final Variable locationVariable = new Variable();
		locationVariable.setVariableType(locationVariableType);
		return locationVariable;

	}

	private List<MeasurementRow> createObservations(final int noOfTrialInstances, final Workbook workbook) {
		final List<MeasurementRow> observations = new ArrayList<MeasurementRow>();

		MeasurementRow row;
		List<MeasurementData> dataList;

		for (int i = 0; i < noOfTrialInstances; i++) {
			row = new MeasurementRow();
			dataList = new ArrayList<MeasurementData>();

			MeasurementData data = new MeasurementData();
			data = new MeasurementData(WorkbookSaverTest.COOPERATOR, "COOPERATOR_NAME");
			data.setMeasurementVariable(this.getMeasurementVariable(WorkbookSaverTest.COOPERATOR_NAME, workbook.getConditions()));
			dataList.add(data);

			row.setDataList(dataList);
			observations.add(row);
		}

		return observations;
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
