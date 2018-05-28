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

import com.google.common.collect.Lists;
import org.generationcp.middleware.data.initializer.WorkbookTestDataInitializer;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class WorkbookTest {

	@Test
	public void testGetMeasurementDatasetVariablesViewForTrial() {
		final Workbook workbook = WorkbookTestDataInitializer.getTestWorkbook(1, new StudyTypeDto("T"));

		final List<MeasurementVariable> list = workbook.getMeasurementDatasetVariablesView();

		final List<MeasurementVariable> factors = workbook.getFactors();
		final List<MeasurementVariable> variates = workbook.getVariates();

		final int noOfFactors = factors.size();
		final int noOfVariates = variates.size();
		final int totalMeasurementVariableCount = noOfFactors + noOfVariates;

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
		final Workbook workbook = WorkbookTestDataInitializer.getTestWorkbook(1, new StudyTypeDto(StudyTypeDto.NURSERY_NAME));

		final List<MeasurementVariable> list = workbook.getMeasurementDatasetVariablesView();
		final int totalMeasurementVariableCount = workbook.getFactors().size() + workbook.getVariates().size();

		Assert.assertEquals("MeasurementDatasetVariablesView size should be the total no of non trial factors, variates",
				totalMeasurementVariableCount, list.size());

	}

	@Test
	public void testArrangeMeasurementVariables() {
		final Workbook workbook = new Workbook();
		List<MeasurementVariable> varList = new ArrayList<MeasurementVariable>();
		varList.add(WorkbookTestDataInitializer.createMeasurementVariable(10));
		varList.add(WorkbookTestDataInitializer.createMeasurementVariable(20));
		varList.add(WorkbookTestDataInitializer.createMeasurementVariable(30));
		final List<Integer> columnOrderedList = new ArrayList<Integer>();
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
	public void testArrangeMeasurementObservation() {
		final Workbook workbook = new Workbook();
		final List<MeasurementRow> observations = new ArrayList<MeasurementRow>();
		final MeasurementRow row = new MeasurementRow();
		final List<MeasurementData> dataList = new ArrayList<MeasurementData>();
		dataList.add(WorkbookTestDataInitializer.createMeasurementData(10));
		dataList.add(WorkbookTestDataInitializer.createMeasurementData(20));
		dataList.add(WorkbookTestDataInitializer.createMeasurementData(30));
		row.setDataList(dataList);
		observations.add(row);

		final List<Integer> columnOrderedList = new ArrayList<Integer>();
		columnOrderedList.add(new Integer(20));
		columnOrderedList.add(new Integer(30));
		columnOrderedList.add(new Integer(10));
		workbook.setColumnOrderedLists(columnOrderedList);
		final List<MeasurementRow> newObservations = workbook.arrangeMeasurementObservation(observations);

		assertEquals("1st element should have term id 20", 20, newObservations.get(0).getDataList().get(0).getMeasurementVariable()
				.getTermId());
		assertEquals("1st element should have term id 30", 30, newObservations.get(0).getDataList().get(1).getMeasurementVariable()
				.getTermId());
		assertEquals("1st element should have term id 10", 10, newObservations.get(0).getDataList().get(2).getMeasurementVariable()
				.getTermId());
	}

	@Test
	public void testGetTrialObservationByTrialInstanceNoForNursery() {
		final Workbook workbook = WorkbookTestDataInitializer.getTestWorkbook(1, new StudyTypeDto(StudyTypeDto.NURSERY_NAME));
		WorkbookTestDataInitializer.createTrialObservations(1, workbook);

		final MeasurementRow trialObservation = workbook.getTrialObservationByTrialInstanceNo(1);
		Assert.assertNotNull("Expecting that every Nursery created has by default 1 instance level observation.", trialObservation);
	}

	@Test
	public void testGetTrialObservationByTrialInstanceNoForTrial() {
		final int noOfInstances = 2;
		final Workbook workbook = WorkbookTestDataInitializer.getTestWorkbook(noOfInstances, new StudyTypeDto(StudyTypeDto.TRIAL_NAME));
		WorkbookTestDataInitializer.createTrialObservations(noOfInstances, workbook);

		for (int trialInstanceNo = 1; trialInstanceNo <= noOfInstances; trialInstanceNo++) {
			final MeasurementRow trialObservation = workbook.getTrialObservationByTrialInstanceNo(trialInstanceNo);
			Assert.assertNotNull(
					"Expecting that there will be a corresponding trial observation instance for every valid trial instance no.",
					trialObservation);

			final MeasurementData measurementData = trialObservation.getMeasurementData(TermId.TRIAL_INSTANCE_FACTOR.getId());
			Assert.assertEquals("Expecting that the return instance level observation row corresponds to the given trial instance no.",
					Integer.valueOf(measurementData.getValue()).intValue(), trialInstanceNo);

		}

	}

	@Test
	public void testHasExistingExperimentalDesign() {
		final Workbook workbook = new Workbook();
		final List<MeasurementVariable> expVariables = new ArrayList<>();

		// we add an RCBD variable which is an experimental design variable
		expVariables.add(WorkbookTestDataInitializer.createExperimentalRCBDVariable());
		workbook.setExperimentalDesignVariables(expVariables);

		Assert.assertTrue("Workbook has a design", workbook.hasExistingExperimentalDesign());
	}

	@Test
	public void testNoExistingExperimentalDesign() {
		final Workbook workbook = new Workbook();
		Assert.assertFalse("Expected hasExistingExperimentalDesign() to return false when there is no design but it didn't.",
				workbook.hasExistingExperimentalDesign());
	}

	@Test
	public void testFindConditionById() {
		final Workbook workbook = new Workbook();

		Assert.assertNull(workbook.findConditionById(TermId.LOCATION_ABBR.getId()));
		Assert.assertNull(workbook.findConditionById(TermId.SEASON_VAR.getId()));

		final MeasurementVariable locationMV = new MeasurementVariable();
		locationMV.setTermId(TermId.LOCATION_ABBR.getId());
		locationMV.setValue("MEX");

		final MeasurementVariable seasonMV = new MeasurementVariable();
		seasonMV.setTermId(TermId.SEASON_VAR.getId());
		seasonMV.setValue("10290");

		workbook.setConditions(Lists.newArrayList(locationMV, seasonMV));

		final MeasurementVariable location = workbook.findConditionById(TermId.LOCATION_ABBR.getId());
		Assert.assertNotNull(location);
		Assert.assertEquals(locationMV, location);

		final MeasurementVariable season = workbook.findConditionById(TermId.SEASON_VAR.getId());
		Assert.assertNotNull(season);
		Assert.assertEquals(seasonMV, season);
	}

}
