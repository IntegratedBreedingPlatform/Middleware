
package org.generationcp.middleware.operation.saver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.dao.dms.ProjectPropertyDao;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

public class ProjectPropertySaverTest {

	private static final List<Integer> DATASET_STUDY_TRIAL_IDS = Arrays.asList(8150, 8155, 8160, 8190, 8180,
			TermId.TRIAL_INSTANCE_FACTOR.getId());
	private static final List<Integer> GERMPLASM_PLOT_VARIATE_IDS = Arrays.asList(8230, 8250, 8377, 8240, 8200, 20345, 20325, 20338, 20310,
			20314, 20327, 20307, 8390, 8263, 8255, 8400, 8410);
	private static final List<Integer> VARS_TO_DELETE = Arrays.asList(8377, 8263, 20310);

	private ProjectPropertySaver projectPropSaver;
	private ProjectPropertyDao projectPropDao;

	private int datasetId;
	private Map<Integer, List<Integer>> dummyProjectPropIds;

	@Before
	public void setup() {
		this.projectPropDao = Mockito.mock(ProjectPropertyDao.class);
		final Saver mockSaver = Mockito.mock(Saver.class);
		Mockito.when(mockSaver.getProjectPropertyDao()).thenReturn(this.projectPropDao);
		this.projectPropSaver = new ProjectPropertySaver(mockSaver);

		this.datasetId = -11;
		this.dummyProjectPropIds = ProjectPropertySaverTest.getDummyProjectPropIds();
	}

	@Test
	public void testUpdateVariablesRanking_AllGermplasmPlotAndVariates() throws MiddlewareQueryException {
		List<Integer> variableIds = new ArrayList<Integer>(ProjectPropertySaverTest.GERMPLASM_PLOT_VARIATE_IDS);
		Collections.shuffle(variableIds);

		int startRank = this.callUpdateVariablesRankingWIthMockDaoReturnsAndAssertions(variableIds, this.dummyProjectPropIds);
		this.verifyUpdateVariablesRankingAssertions(variableIds, this.dummyProjectPropIds, startRank);
	}

	@Test
	public void testUpdateVariablesRanking_IncludeTrialInstanceVar() throws MiddlewareQueryException {
		List<Integer> variableIds = new ArrayList<Integer>(ProjectPropertySaverTest.GERMPLASM_PLOT_VARIATE_IDS);
		variableIds.add(TermId.TRIAL_INSTANCE_FACTOR.getId());
		Collections.shuffle(variableIds);

		int startRank = this.callUpdateVariablesRankingWIthMockDaoReturnsAndAssertions(variableIds, this.dummyProjectPropIds);
		this.verifyUpdateVariablesRankingAssertions(variableIds, this.dummyProjectPropIds, startRank);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testUpdateVariablesRanking_NewVariableInDBAndNotInVarListParameter() throws MiddlewareQueryException {
		List<Integer> variableIds = new ArrayList<Integer>(ProjectPropertySaverTest.GERMPLASM_PLOT_VARIATE_IDS);
		Collections.shuffle(variableIds);

		// New variables exist in DB but not included in passed in list of variables
		List<Integer> newVariableIds = Arrays.asList(123, 456, 789);
		Map<Integer, List<Integer>> newMap = new HashMap<Integer, List<Integer>>(this.dummyProjectPropIds);
		for (int i = 0; i < newVariableIds.size(); i++) {
			int start = this.dummyProjectPropIds.size() + i * 3;
			newMap.put(newVariableIds.get(i), Arrays.asList(start + 1, start + 2, start + 3));
		}
		Mockito.doReturn(newVariableIds).when(this.projectPropDao)
		.getDatasetVariableIdsForGivenStoredInIds(Matchers.anyInt(), Matchers.anyList(), Matchers.anyList());

		int startRank = this.callUpdateVariablesRankingWIthMockDaoReturnsAndAssertions(variableIds, newMap);
		List<Integer> idsToUpdate = new ArrayList<>(variableIds);
		idsToUpdate.addAll(newVariableIds);
		this.verifyUpdateVariablesRankingAssertions(idsToUpdate, newMap, startRank);
	}

	@Test
	public void testUpdateVariablesRanking_DeletedVariableStillInVarListParameter() throws MiddlewareQueryException {
		List<Integer> variableIds = new ArrayList<Integer>(ProjectPropertySaverTest.GERMPLASM_PLOT_VARIATE_IDS);
		Collections.shuffle(variableIds);

		// Variable ID was included in list of variables but actually already deleted from DB
		Map<Integer, List<Integer>> newMap = new HashMap<Integer, List<Integer>>(this.dummyProjectPropIds);
		List<Integer> idsToUpdate = new ArrayList<Integer>(variableIds);
		for (Integer deletedId : ProjectPropertySaverTest.VARS_TO_DELETE) {
			newMap.remove(deletedId);
			idsToUpdate.remove(deletedId);
		}

		int startRank = this.callUpdateVariablesRankingWIthMockDaoReturnsAndAssertions(variableIds, newMap);
		this.verifyUpdateVariablesRankingAssertions(idsToUpdate, newMap, startRank);
	}

	@Test
	public void testSaveVariableTypeShouldCheckSuppliedVariableTypeFirstThenRole(){
		DmsProject dmsProject = new DmsProject();
		dmsProject.setProjectId(1);
		dmsProject.setName("ProjectName");
		dmsProject.setDescription("ProjectDescription");
		dmsProject.setProgramUUID("UUID");

		DMSVariableType dmsVariableType = new DMSVariableType();
		dmsVariableType.setLocalName("DMSName");
		dmsVariableType.setLocalDescription("DMSDescription");
		dmsVariableType.setRank(1);

		StandardVariable standardVariable = new StandardVariable();
		standardVariable.setId(1);
		standardVariable.setName("Name");
		standardVariable.setDescription("Description");
		standardVariable.setPhenotypicType(PhenotypicType.STUDY);

		dmsVariableType.setStandardVariable(standardVariable);

		//role and null variable type
		this.projectPropSaver.saveVariableType(dmsProject, dmsVariableType);
		dmsVariableType.setRole(PhenotypicType.STUDY);
		dmsVariableType.setVariableType(null);
		Assert.assertEquals(dmsProject.getProperties().size(), 3);
		Assert.assertEquals(dmsProject.getProperties().get(0).getTypeId(), VariableType.STUDY_DETAIL.getId());

		//Clearing properties
		dmsProject.setProperties(new ArrayList<ProjectProperty>());

		//role and variable type
		dmsVariableType.setRole(PhenotypicType.STUDY);
		dmsVariableType.setVariableType(VariableType.ANALYSIS);
		this.projectPropSaver.saveVariableType(dmsProject, dmsVariableType);
		Assert.assertEquals(dmsProject.getProperties().size(), 3);
		Assert.assertEquals(dmsProject.getProperties().get(0).getTypeId(), VariableType.ANALYSIS.getId());

		//Clearing properties
		dmsProject.setProperties(new ArrayList<ProjectProperty>());

		//null role and variable type
		dmsVariableType.setRole(null);
		dmsVariableType.setVariableType(VariableType.TRAIT);
		this.projectPropSaver.saveVariableType(dmsProject, dmsVariableType);
		Assert.assertEquals(dmsProject.getProperties().size(), 3);
		Assert.assertEquals(dmsProject.getProperties().get(0).getTypeId(), VariableType.TRAIT.getId());
	}

	@Test
	public void testCreateVariableTypeShouldMapProperties(){
		MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setVariableType(VariableType.ANALYSIS);
		measurementVariable.setRole(PhenotypicType.STUDY);

		int rank = 0;
		DMSVariableType dmsVariableType = this.projectPropSaver.createVariableType(measurementVariable, rank);

		Assert.assertNotNull(dmsVariableType);
		StandardVariable standardVariable = dmsVariableType.getStandardVariable();
		Assert.assertEquals(standardVariable.getPhenotypicType(), measurementVariable.getRole());
		Assert.assertEquals(dmsVariableType.getRole(), measurementVariable.getRole());
		Assert.assertEquals(dmsVariableType.getVariableType(), measurementVariable.getVariableType());
		Assert.assertEquals(dmsVariableType.getRank(), rank);
	}

	private static Map<Integer, List<Integer>> getDummyProjectPropIds() {
		List<Integer> allVariableIds = new ArrayList<Integer>();
		allVariableIds.addAll(ProjectPropertySaverTest.DATASET_STUDY_TRIAL_IDS);
		allVariableIds.addAll(ProjectPropertySaverTest.GERMPLASM_PLOT_VARIATE_IDS);

		Map<Integer, List<Integer>> idsMap = new HashMap<Integer, List<Integer>>();

		for (int i = 0; i < allVariableIds.size(); i++) {
			int start = i * 3;
			idsMap.put(allVariableIds.get(i), Arrays.asList(start + 1, start + 2, start + 3));
		}

		return idsMap;
	}

	@SuppressWarnings("unchecked")
	private void verifyUpdateVariablesRankingAssertions(List<Integer> variableIds, Map<Integer, List<Integer>> idsMap, int startRank) {
		Mockito.verify(this.projectPropDao, Mockito.times(variableIds.size())).updateRank(Matchers.anyList(),
				Matchers.anyInt());
		int rank = startRank;
		for (Integer id : variableIds) {
			Mockito.verify(this.projectPropDao).updateRank(idsMap.get(id), rank++);
		}
	}

	private int callUpdateVariablesRankingWIthMockDaoReturnsAndAssertions(List<Integer> variableIds, Map<Integer, List<Integer>> idsMap)
			throws MiddlewareQueryException {
		int startRank = idsMap.size() + 1;
		Mockito.doReturn(startRank).when(this.projectPropDao).getNextRank(this.datasetId);
		Mockito.doReturn(idsMap).when(this.projectPropDao).getProjectPropertyIDsPerVariableId(this.datasetId);

		this.projectPropSaver.updateVariablesRanking(this.datasetId, variableIds);

		return startRank;
	}

}
