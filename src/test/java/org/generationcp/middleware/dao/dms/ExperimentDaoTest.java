/*******************************************************************************
 *
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.dao.dms;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.data.initializer.WorkbookTestDataInitializer;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.service.api.DataImportService;
import org.generationcp.middleware.service.api.FieldbookService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ExperimentDaoTest extends IntegrationTestBase {

	private ExperimentDao dao;
	
	private static final String programUUID = "123456789";

	private static final String EXPERIMENT_IDS = "EXPERIMENT_IDS";
	private static final String OBSERVATION_VAR_IDS = "OBSERVATION_VAR_IDS";
	
	@Autowired
	private DataImportService dataImportService;
	
	@Autowired
	private FieldbookService fieldbookService;

	@Before
	public void setUp() throws Exception {
		this.dao = new ExperimentDao();
		this.dao.setSession(this.sessionProvder.getSession());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testDeleteEnvironmentsAndRelationshipsByLocationIds() throws Exception {
		//setup data - make sure to create at least 2 environments then have at least one deleted environment, 
		//and at least one that will remain in the database
		final List<Workbook> workbooks = WorkbookTestDataInitializer.getTestWorkbooks(2,5);
		Integer trialId = null;
		for (final Workbook workbook : workbooks) {
			Integer studyId = dataImportService.saveDataset(workbook, true, false, ExperimentDaoTest.programUUID);
			if(trialId == null) {
				trialId = studyId;
			} else {
				//assert that there's only one study created for all workbooks to make sure that our test data is correct
				Assert.assertEquals("There should only be one study created for all workbooks", trialId, studyId);
			}
		}
		//get all saved location ids and their relationships for assertion later
		final Map<Integer, Map<String,Object>> locationIdsAndRelationshipsMap = 
				collectLocationIdsAndRelationshipsOfWorkbooks(workbooks);
		
		//assert that there's only 2 locations created for all workbooks to make sure that our test data is correct
		Assert.assertEquals("There should only be 2 locations created for all workbooks", 2, locationIdsAndRelationshipsMap.keySet().size());
		
		//test - delete one environment and its relationships
		Integer locationToBeDeleted = locationIdsAndRelationshipsMap.keySet().iterator().next();
		this.dao.deleteEnvironmentsAndRelationshipsByLocationIds(Arrays.asList(locationToBeDeleted));
		
		//verify deletion
		Workbook trialWorkbook = this.fieldbookService.getTrialDataSet(trialId);
		//get all saved location ids and their relationships of the trial
		//it should no longer contain the deleted environment and its relationships but still have the rest
		final Map<Integer, Map<String,Object>> trialLocationIdsAndRelationshipsMap = 
				collectLocationIdsAndRelationshipsOfWorkbooks(Arrays.asList(trialWorkbook));
		for(final Integer locationId : trialLocationIdsAndRelationshipsMap.keySet()) {
			Assert.assertNotEquals("The deleted location id should not be found", locationToBeDeleted, locationId);
			final Map<String,Object> trialLocationIdRelationships = trialLocationIdsAndRelationshipsMap.get(locationId);
			//compare this to the previous list - it should have the same set of ids
			final Map<String,Object> previousLocationIdRelationships = locationIdsAndRelationshipsMap.get(locationId);
			
			//compare experimentids
			final Set<Integer> currentExperimentIds = (Set<Integer>) trialLocationIdRelationships.get(EXPERIMENT_IDS);
			final Set<Integer> previousExperimentIds = (Set<Integer>) previousLocationIdRelationships.get(EXPERIMENT_IDS);
			Assert.assertEquals("The number of experiment ids should be the same", 
					currentExperimentIds.size(), previousExperimentIds.size());
			Assert.assertEquals("The experiment ids should be the same", 
					currentExperimentIds, previousExperimentIds);
			
			//compare observation variable ids
			final Set<Integer> currentObservationVariableIds = (Set<Integer>) trialLocationIdRelationships.get(OBSERVATION_VAR_IDS);
			final Set<Integer> previousObservationVariableIds = (Set<Integer>) previousLocationIdRelationships.get(OBSERVATION_VAR_IDS);
			Assert.assertEquals("The number of observation variable ids should be the same", 
					currentObservationVariableIds.size(), previousObservationVariableIds.size());
			Assert.assertEquals("The observation variable ids should be the same", 
					currentObservationVariableIds, previousObservationVariableIds);
			
		}
		
		
	}

	@SuppressWarnings("unchecked")
	private Map<Integer, Map<String,Object>> collectLocationIdsAndRelationshipsOfWorkbooks(final List<Workbook> workbooks) {
		final Map<Integer, Map<String,Object>> locationIdsAndRelationshipsMap = new HashMap<>();
		for (final Workbook workbook : workbooks) {
			//get the location ids and map it to their experiment ids and observations variable ids
			for(final MeasurementRow measurementRow : workbook.getObservations()) {
				final Integer locationId = new Long(measurementRow.getLocationId()).intValue();
				Map<String,Object> locationIdRelationships = null;
				if(!locationIdsAndRelationshipsMap.containsKey(locationId)) {
					locationIdRelationships = new HashMap<>();
					locationIdRelationships.put(EXPERIMENT_IDS,new LinkedHashSet<>());
					locationIdRelationships.put(OBSERVATION_VAR_IDS,new LinkedHashSet<>());
					locationIdsAndRelationshipsMap.put(locationId, locationIdRelationships);
				} else {
					locationIdRelationships = locationIdsAndRelationshipsMap.get(locationId);
				}
				
				final Set<Integer> experimentIds = (Set<Integer>) locationIdRelationships.get(EXPERIMENT_IDS);
				final Set<Integer> observationVariableIds = (Set<Integer>) locationIdRelationships.get(OBSERVATION_VAR_IDS);
				
				final long experimentId = measurementRow.getExperimentId();
				if(experimentId != 0) {
					experimentIds.add(new Long(experimentId).intValue());
				}
				
				for (final MeasurementData measurementData : measurementRow.getDataList()) {
					observationVariableIds.add(measurementData.getMeasurementVariable().getTermId());
				}
			}
		}
		return locationIdsAndRelationshipsMap;
	}
}
