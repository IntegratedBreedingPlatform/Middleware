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

package org.generationcp.middleware.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.data.initializer.StudyTestDataInitializer;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.fieldbook.FieldMapInfo;
import org.generationcp.middleware.domain.fieldbook.FieldMapTrialInstanceInfo;
import org.generationcp.middleware.domain.fieldbook.FieldmapBlockInfo;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.search.StudyResultSet;
import org.generationcp.middleware.domain.search.filter.BrowseStudyQueryFilter;
import org.generationcp.middleware.domain.search.filter.GidStudyQueryFilter;
import org.generationcp.middleware.domain.search.filter.ParentFolderStudyQueryFilter;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.generationcp.middleware.utils.test.FieldMapDataUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

public class StudyDataManagerImplTest extends IntegrationTestBase {

	private static final int START_DATE = 20140627;

	private static final String BASIC_NURSERY_TEMPLATE = "Basic nursery template";

	private StudyDataManagerImpl manager;

	@Autowired
	private OntologyDataManager ontologyManager;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private GermplasmDataManager germplasmDataDM;
	
	private Project commonTestProject;
	private WorkbenchTestDataUtil workbenchTestDataUtil;
	private static CrossExpansionProperties crossExpansionProperties;
	private StudyReference studyReference;
	private StudyTestDataInitializer studyTDI;
	
	@Before
	public void setUp() throws Exception {
		this.manager = new StudyDataManagerImpl(this.sessionProvder);
		if (this.workbenchTestDataUtil == null) {
			this.workbenchTestDataUtil = new WorkbenchTestDataUtil(this.workbenchDataManager);
			this.workbenchTestDataUtil.setUpWorkbench();
		}

		if (this.commonTestProject == null) {
			this.commonTestProject = this.workbenchTestDataUtil.getCommonTestProject();
		}
		final Properties mockProperties = Mockito.mock(Properties.class);
		Mockito.when(mockProperties.getProperty("wheat.generation.level")).thenReturn("0");
		StudyDataManagerImplTest.crossExpansionProperties = new CrossExpansionProperties(mockProperties);
		StudyDataManagerImplTest.crossExpansionProperties.setDefaultLevel(1);
		this.studyTDI = new StudyTestDataInitializer(this.manager, this.ontologyManager, this.commonTestProject, this.germplasmDataDM);
		this.studyReference = this.studyTDI.addTestStudy();
	}

	@Test
	public void testGetStudy() throws Exception {
		Study study = this.manager.getStudy(this.studyReference.getId());
		Assert.assertEquals("The study name should be " + StudyTestDataInitializer.STUDY_NAME, StudyTestDataInitializer.STUDY_NAME, study.getName());
		Assert.assertEquals("The study description should be " + StudyTestDataInitializer.STUDY_DESCRIPTION, StudyTestDataInitializer.STUDY_DESCRIPTION, study.getTitle());
	}
	
	@Test
	public void testGetStudyConditions() throws Exception {
		Study study = this.manager.getStudy(this.studyReference.getId());
		Assert.assertNotNull(study);
		VariableList vList = study.getConditions();
		Assert.assertNotNull("The Variable list should not be null", vList);
	}

	@Test
	public void testGetAllStudyFactor() throws Exception {
		this.studyTDI.addTestDataset(this.studyReference.getId());
		VariableTypeList factors = this.manager.getAllStudyFactors(this.studyReference.getId());
		Assert.assertNotNull(factors);
		//compare the size to the minimum possible value of variable type's size
		Assert.assertTrue("The size should be greater than 0", factors.getVariableTypes().size() > 0);
	}
	
	@Test
	public void testGetAllStudyVariates() throws Exception {
		VariableTypeList variates = this.manager.getAllStudyVariates(this.studyReference.getId());
		Assert.assertNotNull(variates);
		DMSVariableType studyName = variates.findById(TermId.STUDY_NAME.getId());
		Assert.assertEquals("The study name should be " + StudyTestDataInitializer.STUDY_NAME, StudyTestDataInitializer.STUDY_NAME, studyName.getLocalName());
	}

	@Test
	public void testGetStudiesByFolder() throws Exception {
		StudyResultSet resultSet = this.manager.searchStudies(new ParentFolderStudyQueryFilter(1), 5);
		//We are sure that the result set will return at least one study, the study that we added in the setup
		Assert.assertTrue("The size should be greater than 0.", resultSet.size()>0);
	}

	@Test
	public void testSearchStudiesForName() throws Exception {
		BrowseStudyQueryFilter filter = new BrowseStudyQueryFilter();

		filter.setName("FooFoo"); 
		StudyResultSet resultSet = this.manager.searchStudies(filter, 10);
		Assert.assertTrue("The size should be zero since the name is invalid", resultSet.size() == 0);

		filter.setName(BASIC_NURSERY_TEMPLATE);
		resultSet = this.manager.searchStudies(filter, 10);
		
		//We are sure that the result set will contain at least one study
		Assert.assertTrue("The size should be greater than zero", resultSet.size() > 0);
	}

	@Test
	public void testSearchStudiesForStartDate() throws Exception {
		BrowseStudyQueryFilter filter = new BrowseStudyQueryFilter();
		//start date of basic nursery template
		filter.setStartDate(START_DATE);

		StudyResultSet resultSet = this.manager.searchStudies(filter, 10);
		//We are sure that the result set will contain the test study we added in the set up
		Assert.assertTrue("The size should be greater than 0", resultSet.size() > 0);
	}

	
	@Test
	public void testSearchStudiesForAll() throws Exception {
		BrowseStudyQueryFilter filter = new BrowseStudyQueryFilter();
		filter.setStartDate(START_DATE);
		filter.setName(BASIC_NURSERY_TEMPLATE);

		StudyResultSet resultSet = this.manager.searchStudies(filter, 10);
		//We are sure that the result set will contain the test study we added in the set up
		Assert.assertTrue("The size should be greater than 0", resultSet.size() > 0);
	}

	@Test
	public void testSearchStudiesByGid() throws Exception {
		GidStudyQueryFilter filter = new GidStudyQueryFilter(this.studyTDI.getGid());
		StudyResultSet resultSet = this.manager.searchStudies(filter, 50);
		//We are sure that the result set will contain the test study we added in the set up
		Assert.assertTrue("The size should be greater than 0", resultSet.size() > 0);
	}
	
	@Test
	public void testGetRootFolders() throws Exception {
		List<Reference> rootFolders = this.manager.getRootFolders(this.commonTestProject.getUniqueID(), StudyType.nurseriesAndTrials());
		Assert.assertNotNull(rootFolders);
		Assert.assertFalse("Root folders should not be empty because it contains the templates for Nursery and Trial.", rootFolders.isEmpty());
	}

	@Test
	public void testGetChildrenOfFolder() throws Exception {
		
		String uniqueId = this.commonTestProject.getUniqueID();
		DmsProject mainFolder = this.studyTDI.createFolderTestData(uniqueId);
		int subFolderID =  this.manager.addSubFolder(mainFolder.getProjectId(), "Sub folder", "Sub Folder",
				uniqueId);

		List<Reference> childrenNodes = this.manager.getChildrenOfFolder(mainFolder.getProjectId(), this.commonTestProject.getUniqueID(), StudyType.nurseriesAndTrials());
		Assert.assertNotNull(childrenNodes);
		Assert.assertTrue("The size should be one.", childrenNodes.size() == 1);
		Assert.assertTrue("The id of the subFolder should be " + subFolderID,  subFolderID == childrenNodes.get(0).getId());
	}

	@Test
	public void testGetAllFolders() {

		this.studyTDI.createFolderTestData(this.commonTestProject.getUniqueID());
		this.studyTDI.createFolderTestData(null);

		final List<FolderReference> allFolders = this.manager.getAllFolders();
		// We only assert that there are minimum two folders that we added in test.
		// The test database might already have some pre-init and developer created folders too which we dont want the test to depend on
		// because we do not reset the test database for each test run yet.
		Assert.assertTrue("The number of all folders should be greater than or equal to 2", allFolders.size() >= 2);
	}

	@Test
	public void testGetDatasetNodesByStudyId() throws Exception {
		this.studyTDI.addTestDataset(this.studyReference.getId());
		List<DatasetReference> datasetReferences = this.manager.getDatasetReferences(this.studyReference.getId());
		Assert.assertNotNull(datasetReferences);
		Assert.assertTrue(datasetReferences.size() > 0);
		Assert.assertEquals("The dataset name should be " + StudyTestDataInitializer.DATASET_NAME, StudyTestDataInitializer.DATASET_NAME, datasetReferences.get(0).getName());
	}

	@Test
	public void testGetDataSet() throws Exception {
		DatasetReference datasetReference = this.studyTDI.addTestDataset(this.studyReference.getId());
		DataSet dataset = this.manager.getDataSet(datasetReference.getId());
		Assert.assertEquals("The dataset name should be " + StudyTestDataInitializer.DATASET_NAME, StudyTestDataInitializer.DATASET_NAME, dataset.getName());
	}

	@Test
	public void testGetFactorsByProperty() throws Exception {
		DatasetReference datasetReference = this.studyTDI.addTestDataset(this.studyReference.getId());
		DataSet dataset = this.manager.getDataSet(datasetReference.getId());
		int propertyId = 15009;
		VariableTypeList factors = dataset.getFactorsByProperty(propertyId);
		Assert.assertTrue("The size should be 1 since we added 1 factor, with property id = " +propertyId+ ", in the set up of the data set", factors.size()==1);
	}

	@Test
	public void testGetFactorsByPhenotypicType() throws Exception {
		DatasetReference datasetReference = this.studyTDI.addTestDataset(this.studyReference.getId());
		DataSet dataset = this.manager.getDataSet(datasetReference.getId());
		VariableTypeList factors = dataset.getFactorsByPhenotypicType(PhenotypicType.TRIAL_ENVIRONMENT);
		Assert.assertTrue("The size should be 3 since we added 3 factors in the set up of the data set", factors.size()==3);
	}

	@Test
	public void testGetDataSetsByType() throws Exception {
		this.studyTDI.addTestDataset(this.studyReference.getId());
		DataSetType dataSetType = DataSetType.MEANS_DATA;
		List<DataSet> datasets = this.manager.getDataSetsByType(this.studyReference.getId(), dataSetType);
		Assert.assertTrue("Datasets' size should be greter than 0", datasets.size() > 0);
		Assert.assertTrue("The size should be greater than 0 since we are sure that it will return at least one data set", datasets.size()>0);
	}

	@Test
	public void testFindOneDataSetByType() throws Exception {
		this.studyTDI.addTestDataset(this.studyReference.getId());
		DataSetType dataSetType = DataSetType.MEANS_DATA;
		DataSet dataset = this.manager.findOneDataSetByType(this.studyReference.getId(), dataSetType);
		Assert.assertEquals("Dataset's name should be " + StudyTestDataInitializer.DATASET_NAME, StudyTestDataInitializer.DATASET_NAME, dataset.getName());
	}

	@Test
	public void testGetLocalNameByStandardVariableId() throws Exception {
		Integer standardVariableId = TermId.STUDY_NAME.getId();
		String localName = this.manager.getLocalNameByStandardVariableId(this.studyReference.getId(), standardVariableId);
		Assert.assertEquals("The local name should be " + StudyTestDataInitializer.STUDY_NAME, StudyTestDataInitializer.STUDY_NAME, localName);
	}

	@Test
	public void testGetAllStudyDetails() throws Exception {
		List<StudyDetails> nurseryStudyDetails = this.manager.getAllStudyDetails(StudyType.N, this.commonTestProject.getUniqueID());
		Assert.assertTrue("The size should be greater than 0 since we are sure that it will return at least one study details", nurseryStudyDetails.size()>0);
	}

	@Test
	public void testCountProjectsByVariable() throws Exception {
		int variableId = TermId.STUDY_NAME.getId();
		long count = this.manager.countProjectsByVariable(variableId);
		//Since there is no way for us to know the exact number of project
		//the count should not be zero since the Basic Nursery Template is always existing
		Assert.assertTrue("The count should be greater than 0", count > 0);
	}

	
	@Test
	public void testCountExperimentsByVariable() throws Exception {
		int variableId = 8230;
		int storedInId = 1041;
		long count = this.manager.countExperimentsByVariable(variableId, storedInId);
		Assert.assertTrue("Count should be greater than 0", count > 0);
	}

	@Test
	public void testCheckIfProjectNameIsExisting() throws Exception {
		DmsProject project = this.studyTDI.createFolderTestData(this.commonTestProject.getUniqueID());
		boolean isExisting = this.manager.checkIfProjectNameIsExistingInProgram(project.getName(), this.commonTestProject.getUniqueID());
		Assert.assertTrue(isExisting);

		String name = "SHOULDNOTEXISTSTUDY";
		isExisting = this.manager.checkIfProjectNameIsExistingInProgram(name, this.commonTestProject.getUniqueID());
		Assert.assertFalse(isExisting);
	}

	@Test
	public void testGetFieldMapInfoOfTrial() throws MiddlewareQueryException {
		List<Integer> trialIdList = new ArrayList<Integer>();
		trialIdList.addAll(Arrays.asList(this.studyReference.getId()));
		List<FieldMapInfo> fieldMapInfos =
				this.manager.getFieldMapInfoOfStudy(trialIdList, StudyType.T, StudyDataManagerImplTest.crossExpansionProperties);
		//compare the size to the minimum possible value of field map infos' size
		Assert.assertTrue("The size should be greater than 0", fieldMapInfos.size()>0);
	}

	@Test
	public void testGetParentFolder() throws MiddlewareQueryException {
		String uniqueId = "001";
		DmsProject project = this.studyTDI.createFolderTestData(uniqueId);
		int id = this.manager.addSubFolder(project.getProjectId(), "Sub folder", "Sub Folder",
				uniqueId);
		DmsProject proj = this.manager.getParentFolder(id);
		Assert.assertEquals("The folder names should be equal",  project.getName(), proj.getName());
	}


	@Test
	public void testGetFolderTree() throws MiddlewareQueryException {
		List<FolderReference> tree = this.manager.getFolderTree();
		Assert.assertTrue("The size should be 0 since it is empty", tree.size() == 0);
		this.studyTDI.createFolderTestData(this.commonTestProject.getUniqueID());
		tree = this.manager.getFolderTree();
		Assert.assertTrue("The size should be greater than 0 since it will contain the new folder added", tree.size() > 0);
	}

	@Test
	public void testUpdateFieldMapWithBlockInformationWhenBlockIdIsNotNull() {
		LocationDataManager locationDataManager = Mockito.mock(LocationDataManager.class);

		FieldmapBlockInfo fieldMapBlockInfo =
				new FieldmapBlockInfo(FieldMapDataUtil.BLOCK_ID, FieldMapDataUtil.ROWS_IN_BLOCK, FieldMapDataUtil.RANGES_IN_BLOCK,
						FieldMapDataUtil.NUMBER_OF_ROWS_IN_PLOT, FieldMapDataUtil.PLANTING_ORDER, FieldMapDataUtil.MACHINE_ROW_CAPACITY,
						false, null, FieldMapDataUtil.FIELD_ID);

		List<FieldMapInfo> infos = FieldMapDataUtil.createFieldMapInfoList(true);

		this.manager.setLocationDataManager(locationDataManager);

		try {
			Mockito.when(locationDataManager.getBlockInformation(FieldMapDataUtil.BLOCK_ID)).thenReturn(fieldMapBlockInfo);
			this.manager.updateFieldMapWithBlockInformation(infos, fieldMapBlockInfo, false);

			FieldMapTrialInstanceInfo trialInstance = infos.get(0).getDataSet(FieldMapDataUtil.DATASET_ID).getTrialInstances().get(0);

			Assert.assertEquals("Expected " + FieldMapDataUtil.ROWS_IN_BLOCK + " but got " + trialInstance.getRowsInBlock() + " instead.",
					FieldMapDataUtil.ROWS_IN_BLOCK, trialInstance.getRowsInBlock().intValue());
			Assert.assertEquals("Expected " + FieldMapDataUtil.RANGES_IN_BLOCK + " but got " + trialInstance.getRangesInBlock()
					+ " instead.", FieldMapDataUtil.RANGES_IN_BLOCK, trialInstance.getRangesInBlock().intValue());
			Assert.assertEquals("Expected " + FieldMapDataUtil.NUMBER_OF_ROWS_IN_PLOT + " but got " + trialInstance.getRowsPerPlot()
					+ " instead.", FieldMapDataUtil.NUMBER_OF_ROWS_IN_PLOT, trialInstance.getRowsPerPlot().intValue());
			Assert.assertEquals("Expected " + FieldMapDataUtil.PLANTING_ORDER + " but got " + trialInstance.getPlantingOrder()
					+ " instead.", FieldMapDataUtil.PLANTING_ORDER, trialInstance.getPlantingOrder().intValue());
			Assert.assertEquals("Expected " + FieldMapDataUtil.MACHINE_ROW_CAPACITY + " but got " + trialInstance.getMachineRowCapacity()
					+ " instead.", FieldMapDataUtil.MACHINE_ROW_CAPACITY, trialInstance.getMachineRowCapacity().intValue());
		} catch (MiddlewareQueryException e) {
			Assert.fail("Expected mocked value to be returned but used the original call for getBlockInformation instead.");
		}
	}

	@Test
	public void testUpdateFieldMapWithBlockInformationWhenBlockIdIsNull() {
		LocationDataManager locationDataManager = Mockito.mock(LocationDataManager.class);

		FieldmapBlockInfo fieldMapBlockInfo =
				new FieldmapBlockInfo(FieldMapDataUtil.BLOCK_ID, FieldMapDataUtil.ROWS_IN_BLOCK, FieldMapDataUtil.RANGES_IN_BLOCK,
						FieldMapDataUtil.NUMBER_OF_ROWS_IN_PLOT, FieldMapDataUtil.PLANTING_ORDER, FieldMapDataUtil.MACHINE_ROW_CAPACITY,
						false, null, FieldMapDataUtil.FIELD_ID);

		List<FieldMapInfo> infos = FieldMapDataUtil.createFieldMapInfoList(true);
		FieldMapTrialInstanceInfo trialInstance = infos.get(0).getDataSet(FieldMapDataUtil.DATASET_ID).getTrialInstances().get(0);
		trialInstance.setBlockId(null);

		this.manager.setLocationDataManager(locationDataManager);

		try {
			Mockito.when(locationDataManager.getBlockInformation(FieldMapDataUtil.BLOCK_ID)).thenReturn(fieldMapBlockInfo);
			((StudyDataManagerImpl) this.manager).updateFieldMapWithBlockInformation(infos, fieldMapBlockInfo, false);

			Assert.assertNull("Expected null but got " + trialInstance.getRowsInBlock() + " instead.", trialInstance.getRowsInBlock());
			Assert.assertNull("Expected null but got " + trialInstance.getRangesInBlock() + " instead.", trialInstance.getRangesInBlock());
			Assert.assertNull("Expected null but got " + trialInstance.getRowsPerPlot() + " instead.", trialInstance.getRowsPerPlot());
			Assert.assertNull("Expected null but got " + trialInstance.getPlantingOrder() + " instead.", trialInstance.getPlantingOrder());
			Assert.assertNull("Expected null but got " + trialInstance.getMachineRowCapacity() + " instead.",
					trialInstance.getMachineRowCapacity());
		} catch (MiddlewareQueryException e) {
			Assert.fail("Expected mocked value to be returned but used the original call for getBlockInformation instead.");
		}
	}
	
	@Test
	public void testGetStudyType() {
		try {
			Assert.assertEquals("Study type returned did not match.", StudyType.T,
					this.manager.getStudyType(this.studyReference.getId()));
		} catch (MiddlewareQueryException e) {
			Assert.fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test
	public void testGetStudyTypeNullEdgeCase() {
		try {
			final int PRESUMABLY_NON_EXISTENT_STUDY_ID = -1000000;
			Assert.assertNull("Expected null return value but was non null.", this.manager.getStudyType(PRESUMABLY_NON_EXISTENT_STUDY_ID));
		} catch (MiddlewareQueryException e) {
			Assert.fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test
	public void testDeleteProgramStudies() throws Exception {
		String uniqueId = "100001001001";
		this.studyTDI.createFolderTestData(uniqueId);
		this.studyTDI.addTestStudy(uniqueId);

		List<? extends  Reference> programStudiesAndFolders = this.manager.getRootFolders(uniqueId, StudyType.nurseriesAndTrials());
		int sizeBeforeDelete = programStudiesAndFolders.size();
		this.manager.deleteProgramStudies(uniqueId);
		programStudiesAndFolders = this.manager.getRootFolders(uniqueId, StudyType.nurseriesAndTrials());
		int sizeAfterDelete = programStudiesAndFolders.size();
		
		Assert.assertTrue("The size after the delete should be less than the size before.", sizeAfterDelete < sizeBeforeDelete);

	}

	@Test
	public void testGetStudyDetails() throws MiddlewareQueryException {
		List<StudyDetails> studyDetailsList = this.manager.getStudyDetails(StudyType.T, this.commonTestProject.getUniqueID(), 0, 50);
		//Compare size to one since we are sure that the result will include the test study we added in the set up
		Assert.assertTrue("The list should at least contain one Study Details", studyDetailsList.size() > 0);
	}

	@Test
	public void testGetNurseryAndTrialStudyDetails() throws MiddlewareQueryException {
		List<StudyDetails> studyDetailsList = this.manager.getNurseryAndTrialStudyDetails(this.commonTestProject.getUniqueID(), -1, -1);
		//Compare size to one since we are sure that the result will include the test study we added in the set up
		Assert.assertTrue("The list should at least contain one Study Details", studyDetailsList.size() > 0);
	}

	@Test
	public void testGetStudyDetails_ByTypeAndId() throws MiddlewareException {
		StudyDetails studyDetails = this.manager.getStudyDetails(StudyType.T, this.studyReference.getId());
		Assert.assertNotNull("Study should not be null", studyDetails);
		Assert.assertEquals("Study should have the id " + studyReference.getId(), studyDetails.getId(), studyDetails.getId());
	}

	@Test
	public void testGetGeolocationIdByProjectIdAndTrialInstanceNumber() {
		try {
			Integer projectId = 25007;
			String trialInstanceNumberExpected = "1";
			Integer geolocationId = this.manager.getGeolocationIdByProjectIdAndTrialInstanceNumber(projectId, trialInstanceNumberExpected);
			if (geolocationId != null) {
				String trialInstanceNumberActual = this.manager.getTrialInstanceNumberByGeolocationId(geolocationId);
				Assert.assertEquals(trialInstanceNumberExpected, trialInstanceNumberActual);
			}
		} catch (MiddlewareQueryException e) {
			Assert.fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test
	public void testGetTrialInstanceNumberByGeolocationId() {
		try {
			String trialInstanceNumberExpected = "1";
			String trialInstanceNumberActual = this.manager.getTrialInstanceNumberByGeolocationId(1);
			Assert.assertNotNull(trialInstanceNumberActual);
			Assert.assertEquals(trialInstanceNumberExpected, trialInstanceNumberActual);
		} catch (MiddlewareQueryException e) {
			Assert.fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test
	public void testSaveGeolocationProperty() throws MiddlewareQueryException {
		Integer stdVarId = TermId.EXPERIMENT_DESIGN_FACTOR.getId();
		Integer studyId = 25019;
		String expDesign = this.manager.getGeolocationPropValue(stdVarId, studyId);
		String newExpDesign = null;
		if (expDesign != null) {
			if (TermId.RANDOMIZED_COMPLETE_BLOCK.getId() == Integer.parseInt(expDesign)) {
				newExpDesign = Integer.toString(TermId.RESOLVABLE_INCOMPLETE_BLOCK.getId());
			} else if (TermId.RESOLVABLE_INCOMPLETE_BLOCK.getId() == Integer.parseInt(expDesign)) {
				newExpDesign = Integer.toString(TermId.RANDOMIZED_COMPLETE_BLOCK.getId());
			}
			// update experimental design value
			int ndGeolocationId = this.manager.getGeolocationIdByProjectIdAndTrialInstanceNumber(studyId, "1");
			this.manager.saveGeolocationProperty(ndGeolocationId, stdVarId, newExpDesign);
			String actualExpDesign = this.manager.getGeolocationPropValue(stdVarId, studyId);
			Assert.assertEquals(newExpDesign, actualExpDesign);
			Assert.assertNotEquals(expDesign, actualExpDesign);
			// revert to previous value
			this.manager.saveGeolocationProperty(ndGeolocationId, stdVarId, expDesign);
			actualExpDesign = this.manager.getGeolocationPropValue(stdVarId, studyId);
			Assert.assertEquals(expDesign, actualExpDesign);
			Assert.assertNotEquals(newExpDesign, actualExpDesign);
		}
	}

	@Test
	public void testGetAllSharedProjectNames() throws MiddlewareQueryException {
		List<String> sharedProjectNames = this.manager.getAllSharedProjectNames();
		Assert.assertNotNull("The shared project names should not be null", sharedProjectNames);
	}

	@Test
	public void testCheckIfAnyLocationIDsExistInExperimentsReturnFalse() {

		Integer locationId = this.manager.getGeolocationIdByProjectIdAndTrialInstanceNumber(StudyTestDataInitializer.STUDY_ID, "999");
		List<Integer> locationIds = new ArrayList<>();
		locationIds.add(locationId);

		boolean returnValue = this.manager.checkIfAnyLocationIDsExistInExperiments(StudyTestDataInitializer.STUDY_ID, DataSetType.PLOT_DATA, locationIds);

		Assert.assertFalse("The return value should be false", returnValue);
	}
	
	@Test
	public void testIsFolderEmptyTrue() {
		
		String uniqueId = this.commonTestProject.getUniqueID();
		DmsProject project = this.studyTDI.createFolderTestData(uniqueId);
		
		boolean isEmpty = this.manager.isFolderEmpty(project.getProjectId(), uniqueId, StudyType.nurseriesAndTrials());
		Assert.assertTrue("The folder should be empty", isEmpty);
	}
	
	@Test
	public void testIsFolderEmptyFalse() {
		
		String uniqueId = this.commonTestProject.getUniqueID();
		DmsProject project = this.studyTDI.createFolderTestData(uniqueId);
		this.manager.addSubFolder(project.getProjectId(), "Sub folder", "Sub Folder",
				uniqueId);
		boolean isEmpty = this.manager.isFolderEmpty(project.getProjectId(), uniqueId, StudyType.nurseriesAndTrials());
		Assert.assertFalse("The folder should not be empty", isEmpty);
	}
}
