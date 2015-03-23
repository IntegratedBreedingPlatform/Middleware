package org.generationcp.middleware.operation.saver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.dao.dms.ProjectPropertyDao;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ProjectPropertySaverTest {
	
	private static final List<Integer> DATASET_STUDY_TRIAL_IDS =  Arrays.asList(8150, 8155, 8160, 8190, 8180, TermId.TRIAL_INSTANCE_FACTOR.getId());
	private static final List<Integer> GERMPLASM_PLOT_VARIATE_IDS =  Arrays.asList(8230, 8250, 8377, 8240, 8200, 
			20345, 20325, 20338, 20310, 20314, 20327, 20307, 8390, 8263, 8255, 8400, 8410);
	private static final List<Integer> VARS_TO_DELETE = Arrays.asList(8377, 8263, 20310);
			
	private static ProjectPropertySaver projectPropSaver;
	private static ProjectPropertyDao projectPropDao;
	
	private int datasetId;
	private Map<Integer, List<Integer>> dummyProjectPropIds;

	@Before
	public void setup(){
		projectPropDao = Mockito.mock(ProjectPropertyDao.class);
		projectPropSaver = new ProjectPropertySaver(Mockito.mock(HibernateSessionProvider.class));
		projectPropSaver.setProjectPropertyDao(projectPropDao);
		
		datasetId = -11;
    	dummyProjectPropIds = getDummyProjectPropIds();
	}
	 
	@Test
    public void testUpdateVariablesRanking_AllGermplasmPlotAndVariates() throws MiddlewareQueryException{
    	List<Integer> variableIds = new ArrayList<Integer>(GERMPLASM_PLOT_VARIATE_IDS);
    	Collections.shuffle(variableIds);

    	int startRank = callUpdateVariablesRankingWIthMockDaoReturnsAndAssertions(variableIds, dummyProjectPropIds);
    	verifyUpdateVariablesRankingAssertions(variableIds, dummyProjectPropIds, startRank);
    }
	
	@Test
    public void testUpdateVariablesRanking_IncludeTrialInstanceVar() throws MiddlewareQueryException{
    	List<Integer> variableIds = new ArrayList<Integer>(GERMPLASM_PLOT_VARIATE_IDS);
    	variableIds.add(TermId.TRIAL_INSTANCE_FACTOR.getId());
    	Collections.shuffle(variableIds);

    	
    	int startRank = callUpdateVariablesRankingWIthMockDaoReturnsAndAssertions(variableIds, dummyProjectPropIds);
    	verifyUpdateVariablesRankingAssertions(variableIds, dummyProjectPropIds, startRank);
    }
	
	@SuppressWarnings("unchecked")
	@Test
    public void testUpdateVariablesRanking_NewVariableInDBAndNotInVarListParameter() throws MiddlewareQueryException{
    	List<Integer> variableIds = new ArrayList<Integer>(GERMPLASM_PLOT_VARIATE_IDS);
    	Collections.shuffle(variableIds);
    	
    	// New variables exist in DB but not included in passed in list of variables
    	List<Integer> newVariableIds = Arrays.asList(123, 456, 789);
    	Map<Integer, List<Integer>> newMap = new HashMap<Integer, List<Integer>>(dummyProjectPropIds);
    	for (int i = 0; i < newVariableIds.size(); i++){
    		int start = dummyProjectPropIds.size() + (i*3);
    		newMap.put(newVariableIds.get(i), Arrays.asList(start+1, start+2, start+3));
    	}
		Mockito.doReturn(newVariableIds).when(projectPropDao)
    		.getDatasetVariableIdsForGivenStoredInIds(Mockito.anyInt(), Mockito.anyList(), Mockito.anyList());
		
    	int startRank = callUpdateVariablesRankingWIthMockDaoReturnsAndAssertions(variableIds, newMap);
    	List<Integer> idsToUpdate = new ArrayList<>(variableIds);
    	idsToUpdate.addAll(newVariableIds);
    	verifyUpdateVariablesRankingAssertions(idsToUpdate, newMap, startRank);
    }
	
	@Test
    public void testUpdateVariablesRanking_DeletedVariableStillInVarListParameter() throws MiddlewareQueryException{
    	List<Integer> variableIds = new ArrayList<Integer>(GERMPLASM_PLOT_VARIATE_IDS);
    	Collections.shuffle(variableIds);
    	
    	// Variable ID was included in list of variables but actually already deleted from DB
    	Map<Integer, List<Integer>> newMap = new HashMap<Integer, List<Integer>>(dummyProjectPropIds);
    	List<Integer> idsToUpdate = new ArrayList<Integer>(variableIds);
    	for (Integer deletedId : VARS_TO_DELETE){
    		newMap.remove(deletedId);
    		idsToUpdate.remove(deletedId);
    	}
		
    	int startRank = callUpdateVariablesRankingWIthMockDaoReturnsAndAssertions(variableIds, newMap);
    	verifyUpdateVariablesRankingAssertions(idsToUpdate, newMap, startRank);
    }
	
	
    private static Map<Integer, List<Integer>> getDummyProjectPropIds(){
    	List<Integer> allVariableIds = new ArrayList<Integer>();
    	allVariableIds.addAll(DATASET_STUDY_TRIAL_IDS);
    	allVariableIds.addAll(GERMPLASM_PLOT_VARIATE_IDS);
    	
    	Map<Integer, List<Integer>> idsMap = new HashMap<Integer, List<Integer>>();
    	
    	for (int i = 0; i < allVariableIds.size(); i++){
    		int start = i * 3;
    		idsMap.put(allVariableIds.get(i), Arrays.asList(start+1, start+2, start+3));
    	}
    	
    	return idsMap;
    }
    
    
    @SuppressWarnings("unchecked")
    private void verifyUpdateVariablesRankingAssertions(List<Integer> variableIds, Map<Integer, List<Integer>> idsMap, int startRank){
    	Mockito.verify(projectPropDao, Mockito.times(variableIds.size())).updateRank(Mockito.anyList(), Mockito.anyInt());
    	int rank = startRank;
    	for (Integer id : variableIds){
    		Mockito.verify(projectPropDao).updateRank(idsMap.get(id), rank++);
    	}
    }
    
    private int callUpdateVariablesRankingWIthMockDaoReturnsAndAssertions(List<Integer> variableIds, Map<Integer, List<Integer>> idsMap)
			throws MiddlewareQueryException {
		int startRank = idsMap.size() + 1;
    	Mockito.doReturn(startRank).when(projectPropDao).getNextRank(datasetId);
		Mockito.doReturn(idsMap).when(projectPropDao).getProjectPropertyIDsPerVariableId(datasetId);
    	
    	projectPropSaver.updateVariablesRanking(datasetId, variableIds);
    	
    	return startRank;
	}

}
