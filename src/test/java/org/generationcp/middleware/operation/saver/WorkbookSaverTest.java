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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.data.initializer.WorkbookTestDataInitializer;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.Method;
import org.generationcp.middleware.domain.ontology.Property;
import org.generationcp.middleware.domain.ontology.Scale;
import org.generationcp.middleware.manager.ontology.OntologyDataHelper;
import org.generationcp.middleware.operation.transformer.etl.VariableTypeListTransformer;
import org.generationcp.middleware.utils.test.VariableTypeListDataUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class WorkbookSaverTest extends IntegrationTestBase {

	private WorkbookSaver workbookSaver;
	
	private static final String PROGRAM_UUID = "1234567890";
	private static final String STUDY_NAME_PREFIX = "studyName";
	private static final int NO_OF_OBSERVATIONS_PER_TRIAL_INSTANCE = 3;

	@Before
	public void setUp() {
		super.beforeEachTest();
		workbookSaver = new WorkbookSaver(sessionProvder);
	}

	@Test
	public void testPropagationOfTrialFactorsWithTrialVariablesAndWOTrialFactorWithEnvironmentAndVariates() {
		VariableTypeList effectVariables = VariableTypeListDataUtil.createPlotVariableTypeList(false);
		VariableTypeList trialVariables = VariableTypeListDataUtil.createTrialVariableTypeList(true);

		VariableTypeList plotVariables = workbookSaver.propagateTrialFactorsIfNecessary(effectVariables, trialVariables);

		Assert.assertEquals("Expected an aditional entry for trial instance but found none.", effectVariables.size() + 1,
				plotVariables.size());
		Assert.assertFalse("Expected non trial environment and non constant variables but found at least one.",
				this.areTrialAndConstantsInList(plotVariables, effectVariables));
	}

	private boolean areTrialAndConstantsInList(VariableTypeList plotVariables, VariableTypeList effectVariables) {
		if (plotVariables != null) {
			for (DMSVariableType var : plotVariables.getVariableTypes()) {
				if (var.getStandardVariable().getId() != TermId.TRIAL_INSTANCE_FACTOR.getId()
						&& (PhenotypicType.TRIAL_ENVIRONMENT == 
								var.getRole() || PhenotypicType.VARIATE == var.getRole()
								&& !this.isInOriginalPlotDataset(var.getStandardVariable().getId(), effectVariables))) {
					return true;
				}
			}
		}

		return false;
	}

	private boolean isInOriginalPlotDataset(int id, VariableTypeList effectVariables) {
		if (effectVariables != null) {
			for (DMSVariableType var : effectVariables.getVariableTypes()) {
				if (var.getStandardVariable().getId() == id) {
					return true;
				}
			}
		}
		return false;
	}

	@Test
	public void testPropagationOfTrialFactorsWithTrialVariablesAndWOTrialFactorWOEnvironmentAndVariates() {
		VariableTypeList effectVariables = VariableTypeListDataUtil.createPlotVariableTypeList(false);
		VariableTypeList trialVariables = VariableTypeListDataUtil.createTrialVariableTypeList(false);

		VariableTypeList plotVariables = workbookSaver.propagateTrialFactorsIfNecessary(effectVariables, trialVariables);

		Assert.assertEquals("Expected an aditional entry for trial instance but found none.", effectVariables.size() + 1,
				plotVariables.size());
		Assert.assertFalse("Expected non trial environment and non constant variables but found at least one.",
				this.areTrialAndConstantsInList(plotVariables, effectVariables));
	}

	@Test
	public void testPropagationOfTrialFactorsWithTrialVariablesAndTrialFactor() {
		VariableTypeList effectVariables = VariableTypeListDataUtil.createPlotVariableTypeList(true);
		VariableTypeList trialVariables = VariableTypeListDataUtil.createTrialVariableTypeList(false);

		VariableTypeList plotVariables = workbookSaver.propagateTrialFactorsIfNecessary(effectVariables, trialVariables);

		Assert.assertEquals("Expected no change in the plot dataset but found one.", effectVariables.size(), plotVariables.size());
	}

	@Test
	public void testPropagationOfTrialFactorsWOTrialVariablesWithTrialFactor() {
		VariableTypeList effectVariables = VariableTypeListDataUtil.createPlotVariableTypeList(true);
		VariableTypeList trialVariables = null;

		VariableTypeList plotVariables = workbookSaver.propagateTrialFactorsIfNecessary(effectVariables, trialVariables);

		Assert.assertEquals("Expected no change in the plot dataset but found one.", effectVariables.size(), plotVariables.size());
	}

	@Test
	public void testPropagationOfTrialFactorsWOTrialVariablesAndTrialFactor() {
		VariableTypeList effectVariables = VariableTypeListDataUtil.createPlotVariableTypeList(false);
		VariableTypeList trialVariables = null;

		VariableTypeList plotVariables = workbookSaver.propagateTrialFactorsIfNecessary(effectVariables, trialVariables);

		Assert.assertEquals("Expected no change in the plot dataset but found one.", effectVariables.size(), plotVariables.size());
	}

	@Test
	public void testSaveVariables() throws Exception {

		final String programUUID = "abc";
		final String studyName = "nursery_1" + new Random().nextInt(10000);

		Workbook workbook = WorkbookTestDataInitializer.createTestWorkbook(2, StudyType.N, studyName, 1, true);
		WorkbookSaver workbookSaver = Mockito.mock(WorkbookSaver.class, Mockito.CALLS_REAL_METHODS);

		VariableTypeListTransformer transformer = Mockito.mock(VariableTypeListTransformer.class); //new VariableTypeListTransformer(Mockito.mock(HibernateSessionProvider.class));

		VariableTypeList trialConditionsVariableTypeList = createVariableTypeList(workbook.getTrialConditions(), 1);
		Mockito.doReturn(trialConditionsVariableTypeList).when(transformer).transform(workbook.getTrialConditions(), programUUID);

		VariableTypeList nonTrialFactorsVariableTypeList = createVariableTypeList(workbook.getNonTrialFactors(), 1);
		Mockito.doReturn(nonTrialFactorsVariableTypeList).when(transformer).transform(workbook.getNonTrialFactors(), programUUID);

		VariableTypeList trialFactorsVariableTypeList= createVariableTypeList(workbook.getTrialFactors(), 1);
		Mockito.doReturn(trialFactorsVariableTypeList).when(transformer).transform(workbook.getTrialFactors(),
				workbook.getTrialConditions().size() + 1, programUUID);

		VariableTypeList trialConstantsVariableTypeList= createVariableTypeList(workbook.getTrialConstants(), 1);
		Mockito.doReturn(trialConstantsVariableTypeList).when(transformer).transform(workbook.getTrialConstants(),
				workbook.getTrialConditions().size() + workbook.getTrialFactors().size() + 1, programUUID);

		VariableTypeList variatesVariableTypeList= createVariableTypeList(workbook.getVariates(), 1);
		Mockito.doReturn(variatesVariableTypeList).when(transformer).transform(workbook.getVariates(), 10, programUUID);

		Mockito.doReturn(transformer).when(workbookSaver).getVariableTypeListTransformer();

		Map variableMap = workbookSaver.saveVariables(workbook, programUUID);

		Map<String, VariableTypeList> variableTypeMap = (Map<String, VariableTypeList>) variableMap.get("variableTypeMap");
		Map<String, List<MeasurementVariable>> measurementVariableMap =
				(Map<String, List<MeasurementVariable>>) variableMap.get("measurementVariableMap");
		Map<String, List<String>> headerMap = (Map<String, List<String>>) variableMap.get("headerMap");

		final List<String> trialHeaders = headerMap.get("trialHeaders");

		VariableTypeList trialVariableTypeList = variableTypeMap.get("trialVariableTypeList");
		final VariableTypeList trialVariables = variableTypeMap.get("trialVariables");
		final VariableTypeList effectVariables = variableTypeMap.get("effectVariables");

		final List<MeasurementVariable> trialMV = measurementVariableMap.get("trialMV");
		List<MeasurementVariable> effectMV = measurementVariableMap.get("effectMV");

		Assert.assertNotEquals(0, trialHeaders.size());

		Assert.assertNotEquals(0, trialMV.size());
		Assert.assertNotEquals(0, effectMV.size());

		Assert.assertNotEquals(0, effectVariables.getVariableTypes().size());
		Assert.assertNotEquals(0, trialVariables.getVariableTypes().size());
		Assert.assertNotEquals(0, trialVariableTypeList.getVariableTypes().size());
	}

	private StandardVariable transformMeasurementVariableToVariable(MeasurementVariable measurementVariable){
		StandardVariable standardVariable = new StandardVariable();

		standardVariable.setId(measurementVariable.getTermId());
		standardVariable.setName(measurementVariable.getName());
		standardVariable.setDescription(measurementVariable.getDescription());

		Integer methodId = new Random().nextInt(10000);
		Integer propertyId = new Random().nextInt(10000);
		Integer scaleId = new Random().nextInt(10000);

		standardVariable.setMethod(new Method(new Term(methodId, measurementVariable.getMethod(), "Method Description")));
		standardVariable.setProperty(new Property(new Term(propertyId, measurementVariable.getProperty(), "Property Description")));
		standardVariable.setScale(new Scale(new Term(scaleId, measurementVariable.getScale(), "Scale Description")));
		standardVariable.setDataType(new Term(DataType.NUMERIC_VARIABLE.getId(), DataType.NUMERIC_VARIABLE.getName(), "Data Type Description"));
		standardVariable.setIsA(new Term(new Random().nextInt(1000), "IsA", "IsA Description"));
		standardVariable.setPhenotypicType(measurementVariable.getRole());
		standardVariable.setCropOntologyId("CO:100");
		standardVariable.setVariableTypes(new HashSet<>(
				new ArrayList<>(Collections.singletonList(OntologyDataHelper.mapFromPhenotype(measurementVariable.getRole(), measurementVariable
						.getProperty())))));

		return standardVariable;
	}

	private DMSVariableType transformToDMSVariableType(MeasurementVariable measurementVariable, int rank){
		StandardVariable standardVariable = transformMeasurementVariableToVariable(measurementVariable);

		DMSVariableType dmsVariableType = new DMSVariableType(measurementVariable.getName(), measurementVariable.getDescription(), standardVariable, rank++);
		dmsVariableType.setTreatmentLabel(measurementVariable.getTreatmentLabel());
		return dmsVariableType;
	}

	private VariableTypeList createVariableTypeList(List<MeasurementVariable> measurementVariables, int rank){
		VariableTypeList variableTypeList = new VariableTypeList();

		for(MeasurementVariable measurementVariable : measurementVariables){
			variableTypeList.add(transformToDMSVariableType(measurementVariable, rank));
		}
		return variableTypeList;
	}
	
	
	
	@Test
	public void testCreateLocationsAndSetToObservationsForTrialWithTrialObservations() {
		final StudyType studyType = StudyType.T;
		final boolean withTrialObservations = true;
		boolean hasMultipleLocations = true;
		testCreateLocationsAndSetToObservations(studyType, withTrialObservations, hasMultipleLocations);
	}
	
	@Test
	public void testCreateLocationsAndSetToObservationsForTrialWithoutTrialObservations() {
		final StudyType studyType = StudyType.T;
		final boolean withTrialObservations = false;
		boolean hasMultipleLocations = true;
		testCreateLocationsAndSetToObservations(studyType, withTrialObservations, hasMultipleLocations);
	}
	
	@Test
	public void testCreateLocationsAndSetToObservationsForTrialWithTrialObservationsSingleLocation() {
		final StudyType studyType = StudyType.T;
		final boolean withTrialObservations = true;
		boolean hasMultipleLocations = false;
		testCreateLocationsAndSetToObservations(studyType, withTrialObservations, hasMultipleLocations);
	}
	
	@Test
	public void testCreateLocationsAndSetToObservationsForNurseryWithTrialObservations() {
		final StudyType studyType = StudyType.N;
		//for a nursery, the method is only called when there is a trial observation
		final boolean withTrialObservations = true;
		//nursery can only have 1 location
		boolean hasMultipleLocations = false;
		testCreateLocationsAndSetToObservations(studyType, withTrialObservations, hasMultipleLocations);
	}
	
	public void testCreateLocationsAndSetToObservations(final StudyType studyType, final boolean withTrialObservations, boolean hasMultipleLocations) {
		//the variable to verify value correctness (location ids created and variates per location id)
		final List<Integer> locationIds = new ArrayList<>();
		final Map<Integer, VariableList> trialVariatesMap = new HashMap<Integer, VariableList>();
		//the trial workbook populated with trial observations
		final Workbook workbook = this.createWorkbookTestData(studyType, withTrialObservations, hasMultipleLocations);
		final VariableTypeList trialFactors = this.getTrialFactors(workbook, withTrialObservations);
		final List<String> trialHeaders = workbook.getTrialHeaders();
		final boolean isDeleteTrialObservations = false;
		
		//test method
		final int studyLocationId = workbookSaver.createLocationsAndSetToObservations(locationIds, workbook, trialFactors, 
				trialHeaders, trialVariatesMap, isDeleteTrialObservations, PROGRAM_UUID);
		
		//verify the value of locationIds and the studyLocationId which is the first location id
		int expectedNumberOfLocations = 1;
		if(hasMultipleLocations) {
			expectedNumberOfLocations = 2;
		}
		Assert.assertEquals("There should be " + expectedNumberOfLocations + " location ids created", 
				expectedNumberOfLocations, locationIds.size());
		int expectedStudyLocationId = new Integer(locationIds.get(0)).intValue();
		Assert.assertEquals("The studyLocationId should be the first location id created", expectedStudyLocationId, studyLocationId);
		//verify the value of trial variates per location id
		for (Integer locationId : locationIds) {
			VariableList trialVariates = trialVariatesMap.get(locationId);
			
			if(withTrialObservations) {
				//since this is a trial observation, trial constants/variates should be found
				Assert.assertNotNull("Trial variates should be found", trialVariates);
				Assert.assertEquals("There should be two trial variates found", 2, trialVariates.size());
				List<Integer> expectedTrialVariateIds = Arrays.asList(WorkbookTestDataInitializer.PLANT_HEIGHT_UNIT_ERRORS_ID,
						WorkbookTestDataInitializer.GRAIN_SIZE_ID);
				for (Variable variable : trialVariates.getVariables()) {
					int variableId = variable.getVariableType().getStandardVariable().getId();
					Assert.assertTrue("The variable id should be found in " + expectedTrialVariateIds, expectedTrialVariateIds.contains(variableId));
				}
			} else {
				Assert.assertNull("Trial variates should not be found", trialVariates);
			}
		}
		//verify the locationId set for the trial observation matches the location ids
		for (final MeasurementRow measurementRow : workbook.getTrialObservations()) {
			Assert.assertTrue("The location id of the measurement row should be one of the location ids created", 
					locationIds.contains(new Long(measurementRow.getLocationId()).intValue()));
		}
	}

	private VariableTypeList getTrialFactors(Workbook workbook, boolean withTrialObservations) {
		VariableTypeList trialFactors = new VariableTypeList();
		//condition variables are only part of trial observations, not observations
		if(withTrialObservations) {
			trialFactors = workbookSaver.getVariableTypeListTransformer().
					transform(workbook.getTrialConditions(), PROGRAM_UUID);
		}
		int rank = trialFactors.size() + 1;
		trialFactors.addAll(workbookSaver.getVariableTypeListTransformer().transform(workbook.getTrialFactors(), rank, PROGRAM_UUID));
		//constant variables are only part of trial observations, not observations
		if(withTrialObservations) {
			rank = trialFactors.size() + 1;
			trialFactors.addAll(workbookSaver.getVariableTypeListTransformer().transform(workbook.getTrialConstants(), PROGRAM_UUID));
		}
		return trialFactors;
	}

	private Workbook createWorkbookTestData(StudyType studyType, boolean withTrialObservations, boolean hasMultipleLocations) {
		String studyName = STUDY_NAME_PREFIX + studyType.getLabel();
		boolean isForMeansDataset = false;
		
		//create observations for trial instance 1
		int trialInstanceNumber = 1;
		final Workbook workbook = WorkbookTestDataInitializer.createTestWorkbook(NO_OF_OBSERVATIONS_PER_TRIAL_INSTANCE, studyType, 
				studyName, trialInstanceNumber, hasMultipleLocations, isForMeansDataset);
		workbook.setTrialObservations(new ArrayList<MeasurementRow>());
		if(withTrialObservations) {
			this.addTrialObservationsFromWorkbook(workbook, workbook.getTrialObservations(), trialInstanceNumber, hasMultipleLocations);
		}
		
		if(hasMultipleLocations) {
			//create for trial instance 2
			trialInstanceNumber = 2;
			final List<MeasurementRow> observationsWithTrialInstace2 = WorkbookTestDataInitializer.createObservations(
					workbook, NO_OF_OBSERVATIONS_PER_TRIAL_INSTANCE, hasMultipleLocations, trialInstanceNumber, isForMeansDataset);
			workbook.getObservations().addAll(observationsWithTrialInstace2);
			if(withTrialObservations) {
				this.addTrialObservationsFromWorkbook(workbook, workbook.getTrialObservations(), trialInstanceNumber, hasMultipleLocations);
			}
		}
		return workbook;
	}

	private void addTrialObservationsFromWorkbook(final Workbook workbook, final List<MeasurementRow> trialObservations, int trialInstanceNumber,
			boolean hasMultipleLocations) {
		trialObservations.addAll(WorkbookTestDataInitializer.createTrialObservations(
			NO_OF_OBSERVATIONS_PER_TRIAL_INSTANCE, String.valueOf(trialInstanceNumber), 
			workbook.getFactors(), workbook.getConditions(), workbook.getConstants(), hasMultipleLocations));
	}
}
