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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.data.initializer.WorkbookTestDataInitializer;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class WorkbookTest {

	private static final String TRIAL_INSTANCE_NO = "1";

	private static final int ENTRY_NO_INDEX = 0;
	private static final int PLOT_NO_INDEX = 1;
	private static final int ENTRY_TYPE_INDEX = 2;

	public static final ImmutableList<String> EXPERIMENT_WITH_CHECKS_1 = ImmutableList.of("5", "1", "Check Entry");
	public static final ImmutableList<String> EXPERIMENT_WITH_CHECKS_2 = ImmutableList.of("1", "2", "Test Entry");
	public static final ImmutableList<String> EXPERIMENT_WITH_CHECKS_3 = ImmutableList.of("2", "3", "Test Entry");
	public static final ImmutableList<String> EXPERIMENT_WITH_CHECKS_4 = ImmutableList.of("5", "4", "Check Entry");
	public static final ImmutableList<String> EXPERIMENT_WITH_CHECKS_5 = ImmutableList.of("3", "5", "Test Entry");
	public static final ImmutableList<String> EXPERIMENT_WITH_CHECKS_6 = ImmutableList.of("4", "6", "Test Entry");

	public static final ImmutableList<String> EXPERIMENT_1 = ImmutableList.of("1", "1");
	public static final ImmutableList<String> EXPERIMENT_2 = ImmutableList.of("2", "2");
	public static final ImmutableList<String> EXPERIMENT_3 = ImmutableList.of("3", "3");
	public static final ImmutableList<String> EXPERIMENT_4 = ImmutableList.of("4", "4");

	private static final List<ImmutableList<String>> ENTRY_NO_ENTRY_TYPE_PLOT_NO_LIST = new ArrayList<>();
	private static final List<ImmutableList<String>> ENTRY_NO_PLOT_NO_LIST = new ArrayList<>();

	static {
		WorkbookTest.ENTRY_NO_ENTRY_TYPE_PLOT_NO_LIST.add(WorkbookTest.EXPERIMENT_WITH_CHECKS_1);
		WorkbookTest.ENTRY_NO_ENTRY_TYPE_PLOT_NO_LIST.add(WorkbookTest.EXPERIMENT_WITH_CHECKS_2);
		WorkbookTest.ENTRY_NO_ENTRY_TYPE_PLOT_NO_LIST.add(WorkbookTest.EXPERIMENT_WITH_CHECKS_3);
		WorkbookTest.ENTRY_NO_ENTRY_TYPE_PLOT_NO_LIST.add(WorkbookTest.EXPERIMENT_WITH_CHECKS_4);
		WorkbookTest.ENTRY_NO_ENTRY_TYPE_PLOT_NO_LIST.add(WorkbookTest.EXPERIMENT_WITH_CHECKS_5);
		WorkbookTest.ENTRY_NO_ENTRY_TYPE_PLOT_NO_LIST.add(WorkbookTest.EXPERIMENT_WITH_CHECKS_6);

		WorkbookTest.ENTRY_NO_PLOT_NO_LIST.add(WorkbookTest.EXPERIMENT_1);
		WorkbookTest.ENTRY_NO_PLOT_NO_LIST.add(WorkbookTest.EXPERIMENT_2);
		WorkbookTest.ENTRY_NO_PLOT_NO_LIST.add(WorkbookTest.EXPERIMENT_3);
		WorkbookTest.ENTRY_NO_PLOT_NO_LIST.add(WorkbookTest.EXPERIMENT_4);
	}

	@Test
	public void testGetMeasurementDatasetVariablesViewForTrial() {
		final Workbook workbook = WorkbookTestDataInitializer.getTestWorkbook(1, StudyType.T);

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
		final Workbook workbook = WorkbookTestDataInitializer.getTestWorkbook(1, StudyType.N);

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

		Assert.assertEquals("1st element should have term id 20", 20, varList.get(0).getTermId());
		Assert.assertEquals("2nd element should have term id 30", 30, varList.get(1).getTermId());
		Assert.assertEquals("3rd element should have term id 10", 10, varList.get(2).getTermId());
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

		Assert.assertEquals("1st element should have term id 20", 20, newObservations.get(0).getDataList().get(0).getMeasurementVariable()
				.getTermId());
		Assert.assertEquals("1st element should have term id 30", 30, newObservations.get(0).getDataList().get(1).getMeasurementVariable()
				.getTermId());
		Assert.assertEquals("1st element should have term id 10", 10, newObservations.get(0).getDataList().get(2).getMeasurementVariable()
				.getTermId());
	}

	@Test
	public void testGetTrialObservationByTrialInstanceNoForNursery() {
		final Workbook workbook = WorkbookTestDataInitializer.getTestWorkbook(1, StudyType.N);
		WorkbookTestDataInitializer.createTrialObservations(1, workbook);

		final MeasurementRow trialObservation = workbook.getTrialObservationByTrialInstanceNo(1);
		Assert.assertNotNull("Expecting that every Nursery created has by default 1 instance level observation.", trialObservation);
	}

	@Test
	public void testGetTrialObservationByTrialInstanceNoForTrial() {
		final int noOfInstances = 2;
		final Workbook workbook = WorkbookTestDataInitializer.getTestWorkbook(noOfInstances, StudyType.T);
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

	@Test
	public void testGetPlotNumbersOfTestEntriesWithNullObservations() {
		final Workbook workbook = new Workbook();
		final Map<String, String> entryNoPlotNoMap = workbook.getPlotNumbersOfTestEntries();
		Assert.assertTrue("There should be no items found", entryNoPlotNoMap.isEmpty());
	}

	@Test
	public void testGetPlotNumbersOfTestEntriesWithEmptyObservations() {
		final Workbook workbook = new Workbook();
		workbook.setObservations(new ArrayList<MeasurementRow>());
		final Map<String, String> entryNoPlotNoMap = workbook.getPlotNumbersOfTestEntries();
		Assert.assertTrue("There should be no items found", entryNoPlotNoMap.isEmpty());
	}

	@Test
	public void testGetPlotNumbersOfTestEntriesWithNoListEntries() {
		final Workbook workbook = new Workbook();
		workbook.setObservations(this.createObservationsWithoutListEntries());
		final Map<String, String> entryNoPlotNoMap = workbook.getPlotNumbersOfTestEntries();
		Assert.assertTrue("There should be no items found", entryNoPlotNoMap.isEmpty());
	}

	private List<MeasurementRow> createObservationsWithoutListEntries() {
		// no list entries means no entry_no found
		final List<MeasurementRow> observations = new ArrayList<MeasurementRow>();
		final MeasurementRow measurementRow = new MeasurementRow();
		observations.add(measurementRow);

		final List<MeasurementData> dataList = new ArrayList<MeasurementData>();
		measurementRow.setDataList(dataList);

		dataList.add(WorkbookTestDataInitializer.createMeasurementData(TermId.TRIAL_INSTANCE_FACTOR.getId(), WorkbookTest.TRIAL_INSTANCE_NO));

		return observations;
	}

	@Test
	public void testGetPlotNumbersOfTestEntriesWithListEntries() {
		final Workbook workbook = new Workbook();
		final List<MeasurementRow> observations = this.createObservationsWithListEntries();
		workbook.setObservations(observations);
		final Map<String, String> entryNoPlotNoMap = workbook.getPlotNumbersOfTestEntries();
		Assert.assertEquals("There should be 4 items found as there are only 4 test entries added in the observations", 4,
				entryNoPlotNoMap.size());
		Assert.assertEquals("The number of items found should be equal to the number of observations "
				+ "as there are only test entries added in the observations", observations.size(), entryNoPlotNoMap.size());
	}

	@Test
	public void testGetPlotNumbersOfTestEntriesWithListEntriesAndChecks() {
		final Workbook workbook = new Workbook();
		final List<MeasurementRow> observations = this.createObservationsWithListEntriesAndChecks();
		workbook.setObservations(observations);
		final Map<String, String> entryNoPlotNoMap = workbook.getPlotNumbersOfTestEntries();
		Assert.assertEquals("There should be 4 items found as there are only 4 test entries added in the observations", 4,
				entryNoPlotNoMap.size());
		Assert.assertNotEquals("The number of items found should not be equal to the number of observations "
				+ "as there are non-test entries added in the observations", observations.size(), entryNoPlotNoMap.size());
	}

	public List<MeasurementRow> createObservationsWithListEntries() {
		// no list entries means no entry_no found
		final List<MeasurementRow> observations = new ArrayList<MeasurementRow>();

		for (final ImmutableList<String> entryNoEntryTypePlotNoData : WorkbookTest.ENTRY_NO_PLOT_NO_LIST) {

			final MeasurementRow measurementRow = new MeasurementRow();
			observations.add(measurementRow);

			final List<MeasurementData> dataList = new ArrayList<MeasurementData>();
			measurementRow.setDataList(dataList);

			dataList.add(WorkbookTestDataInitializer.createMeasurementData(TermId.TRIAL_INSTANCE_FACTOR.getId(),
					WorkbookTest.TRIAL_INSTANCE_NO));

			final String entryNo = entryNoEntryTypePlotNoData.get(WorkbookTest.ENTRY_NO_INDEX);
			final String plotNo = entryNoEntryTypePlotNoData.get(WorkbookTest.PLOT_NO_INDEX);

			dataList.add(WorkbookTestDataInitializer.createMeasurementData(TermId.ENTRY_NO.getId(), entryNo));
			dataList.add(WorkbookTestDataInitializer.createMeasurementData(TermId.PLOT_NO.getId(), plotNo));
		}

		return observations;
	}

	public List<MeasurementRow> createObservationsWithListEntriesAndChecks() {
		// no list entries means no entry_no found
		final List<MeasurementRow> observations = new ArrayList<MeasurementRow>();

		for (final ImmutableList<String> entryNoEntryTypePlotNoData : WorkbookTest.ENTRY_NO_ENTRY_TYPE_PLOT_NO_LIST) {

			final MeasurementRow measurementRow = new MeasurementRow();
			observations.add(measurementRow);

			final List<MeasurementData> dataList = new ArrayList<MeasurementData>();
			measurementRow.setDataList(dataList);

			dataList.add(WorkbookTestDataInitializer.createMeasurementData(TermId.TRIAL_INSTANCE_FACTOR.getId(),
					WorkbookTest.TRIAL_INSTANCE_NO));

			final String entryNo = entryNoEntryTypePlotNoData.get(WorkbookTest.ENTRY_NO_INDEX);
			final String plotNo = entryNoEntryTypePlotNoData.get(WorkbookTest.PLOT_NO_INDEX);
			final String entryType = entryNoEntryTypePlotNoData.get(WorkbookTest.ENTRY_TYPE_INDEX);

			dataList.add(WorkbookTestDataInitializer.createMeasurementData(TermId.ENTRY_NO.getId(), entryNo));
			dataList.add(WorkbookTestDataInitializer.createMeasurementData(TermId.PLOT_NO.getId(), plotNo));
			dataList.add(WorkbookTestDataInitializer.createMeasurementData(TermId.ENTRY_TYPE.getId(), entryType));
		}

		return observations;
	}

}
