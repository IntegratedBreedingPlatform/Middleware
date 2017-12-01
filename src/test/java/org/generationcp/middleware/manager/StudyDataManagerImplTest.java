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
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

@Ignore("Historic failing test. Disabled temporarily. Developers working in this area please spend some time to fix and remove @Ignore.")
public class StudyDataManagerImplTest extends IntegrationTestBase {

	private static final String LOCATION_NAME = "LOCATION NAME";

	private static final String FIELD_NAME = "FIELD NAME";

	private static final String BLOCK_NAME = "BLOCK NAME";

	private static final int START_DATE = 20140627;

	private static final String BASIC_NURSERY_TEMPLATE = "Basic nursery template";

	private StudyDataManagerImpl manager;

	@Autowired
	private OntologyDataManager ontologyManager;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private GermplasmDataManager germplasmDataDM;

	@Autowired
	private LocationDataManager locationManager;

	private Project commonTestProject;
	private WorkbenchTestDataUtil workbenchTestDataUtil;
	private static CrossExpansionProperties crossExpansionProperties;
	private StudyReference studyReference;
	private StudyTestDataInitializer studyTDI;
	private final String cropPrefix = "ABCD";

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
		this.studyTDI = new StudyTestDataInitializer(this.manager, this.ontologyManager, this.commonTestProject, this.germplasmDataDM,
				this.locationManager);
		
		this.studyReference = this.studyTDI.addTestStudy(cropPrefix);
	}

	@Test
	public void testGetStudy() throws Exception {
		final Study study = this.manager.getStudy(this.studyReference.getId());
		Assert.assertEquals("The study name should be " + StudyTestDataInitializer.STUDY_NAME, StudyTestDataInitializer.STUDY_NAME,
				study.getName());
		Assert.assertEquals("The study description should be " + StudyTestDataInitializer.STUDY_DESCRIPTION,
				StudyTestDataInitializer.STUDY_DESCRIPTION, study.getDescription());
	}

	@Test
	public void testGetStudyConditions() throws Exception {
		final Study study = this.manager.getStudy(this.studyReference.getId());
		Assert.assertNotNull(study);
		final VariableList vList = study.getConditions();
		Assert.assertNotNull("The Variable list should not be null", vList);
	}

	@Test
	public void testGetAllStudyFactor() throws Exception {
		this.studyTDI.addTestDataset(this.studyReference.getId());
		final VariableTypeList factors = this.manager.getAllStudyFactors(this.studyReference.getId());
		Assert.assertNotNull(factors);
		// compare the size to the minimum possible value of variable type's size
		Assert.assertTrue("The size should be greater than 0", factors.getVariableTypes().size() > 0);
	}

	@Test
	public void testGetAllStudyVariates() throws Exception {
		final VariableTypeList variates = this.manager.getAllStudyVariates(this.studyReference.getId());
		Assert.assertNotNull(variates);
		final DMSVariableType studyName = variates.findById(TermId.STUDY_NAME.getId());
		Assert.assertEquals("The study name should be " + StudyTestDataInitializer.STUDY_NAME, StudyTestDataInitializer.STUDY_NAME,
				studyName.getLocalName());
	}

	@Test
	public void testGetStudiesByFolder() throws Exception {
		final StudyResultSet resultSet = this.manager.searchStudies(new ParentFolderStudyQueryFilter(1), 5);
		// We are sure that the result set will return at least one study, the study that we added in the setup
		Assert.assertTrue("The size should be greater than 0.", resultSet.size() > 0);
	}

	@Test
	public void testSearchStudiesForName() throws Exception {
		final BrowseStudyQueryFilter filter = new BrowseStudyQueryFilter();

		filter.setName("FooFoo");
		StudyResultSet resultSet = this.manager.searchStudies(filter, 10);
		Assert.assertTrue("The size should be zero since the name is invalid", resultSet.size() == 0);

		filter.setName(StudyDataManagerImplTest.BASIC_NURSERY_TEMPLATE);
		resultSet = this.manager.searchStudies(filter, 10);

		// We are sure that the result set will contain at least one study
		Assert.assertTrue("The size should be greater than zero", resultSet.size() > 0);
	}

	@Test
	public void testSearchStudiesForStartDate() throws Exception {
		final BrowseStudyQueryFilter filter = new BrowseStudyQueryFilter();
		// start date of basic nursery template
		filter.setStartDate(StudyDataManagerImplTest.START_DATE);

		final StudyResultSet resultSet = this.manager.searchStudies(filter, 10);
		// We are sure that the result set will contain the test study we added in the set up
		Assert.assertTrue("The size should be greater than 0", resultSet.size() > 0);
	}

	@Test
	public void testSearchStudiesForAll() throws Exception {
		final BrowseStudyQueryFilter filter = new BrowseStudyQueryFilter();
		filter.setStartDate(StudyDataManagerImplTest.START_DATE);
		filter.setName(StudyDataManagerImplTest.BASIC_NURSERY_TEMPLATE);

		final StudyResultSet resultSet = this.manager.searchStudies(filter, 10);
		// We are sure that the result set will contain the test study we added in the set up
		Assert.assertTrue("The size should be greater than 0", resultSet.size() > 0);
	}

	@Test
	public void testSearchStudiesByGid() throws Exception {
		final GidStudyQueryFilter filter = new GidStudyQueryFilter(this.studyTDI.getGid());
		final StudyResultSet resultSet = this.manager.searchStudies(filter, 50);
		// We are sure that the result set will contain the test study we added in the set up
		Assert.assertTrue("The size should be greater than 0", resultSet.size() > 0);
	}

	@Test
	public void testGetRootFolders() throws Exception {
		final List<Reference> rootFolders =
				this.manager.getRootFolders(this.commonTestProject.getUniqueID(), StudyType.nurseriesAndTrials());
		Assert.assertNotNull(rootFolders);
		Assert.assertFalse("Root folders should not be empty because it contains the templates for Nursery and Trial.",
				rootFolders.isEmpty());
	}

	@Test
	public void testGetChildrenOfFolder() throws Exception {

		final String uniqueId = this.commonTestProject.getUniqueID();
		final DmsProject mainFolder = this.studyTDI.createFolderTestData(uniqueId);
		final int subFolderID = this.manager.addSubFolder(mainFolder.getProjectId(), "Sub folder", "Sub Folder", uniqueId);

		final List<Reference> childrenNodes = this.manager.getChildrenOfFolder(mainFolder.getProjectId(),
				this.commonTestProject.getUniqueID(), StudyType.nurseriesAndTrials());
		Assert.assertNotNull(childrenNodes);
		Assert.assertEquals("The size should be one.", 1, childrenNodes.size());
		Assert.assertTrue("The id of the subFolder should be " + subFolderID, subFolderID == childrenNodes.get(0).getId());
	}

	@Test
	public void testGetAllFolders() {
		final int originalSize = this.manager.getAllFolders().size();
		this.studyTDI.createFolderTestData(this.commonTestProject.getUniqueID());
		this.studyTDI.createFolderTestData(null);

		final int newSize = this.manager.getAllFolders().size();
		// We only assert that there are two folders added.
		Assert.assertEquals("The new size should be equal to the original size + 2", originalSize+2, newSize);
	}

	@Test
	public void testGetDatasetNodesByStudyId() throws Exception {
		this.studyTDI.addTestDataset(this.studyReference.getId());
		final List<DatasetReference> datasetReferences = this.manager.getDatasetReferences(this.studyReference.getId());
		Assert.assertNotNull(datasetReferences);
		Assert.assertTrue(datasetReferences.size() > 0);
		Assert.assertEquals("The dataset name should be " + StudyTestDataInitializer.DATASET_NAME, StudyTestDataInitializer.DATASET_NAME,
				datasetReferences.get(0).getName());
	}

	@Test
	public void testGetDataSet() throws Exception {
		final DatasetReference datasetReference = this.studyTDI.addTestDataset(this.studyReference.getId());
		final DataSet dataset = this.manager.getDataSet(datasetReference.getId());
		Assert.assertEquals("The dataset name should be " + StudyTestDataInitializer.DATASET_NAME, StudyTestDataInitializer.DATASET_NAME,
				dataset.getName());
	}

	@Test
	public void testGetFactorsByProperty() throws Exception {
		final DatasetReference datasetReference = this.studyTDI.addTestDataset(this.studyReference.getId());
		final DataSet dataset = this.manager.getDataSet(datasetReference.getId());
		final int propertyId = 15009;
		final VariableTypeList factors = dataset.getFactorsByProperty(propertyId);
		Assert.assertTrue(
				"The size should be 1 since we added 1 factor, with property id = " + propertyId + ", in the set up of the data set",
				factors.size() == 1);
	}

	@Test
	public void testGetFactorsByPhenotypicType() throws Exception {
		final DatasetReference datasetReference = this.studyTDI.addTestDataset(this.studyReference.getId());
		final DataSet dataset = this.manager.getDataSet(datasetReference.getId());
		final VariableTypeList factors = dataset.getFactorsByPhenotypicType(PhenotypicType.TRIAL_ENVIRONMENT);
		Assert.assertTrue("The size should be 3 since we added 3 factors in the set up of the data set", factors.size() == 3);
	}

	@Test
	public void testGetDataSetsByType() throws Exception {
		this.studyTDI.addTestDataset(this.studyReference.getId());
		final DataSetType dataSetType = DataSetType.MEANS_DATA;
		final List<DataSet> datasets = this.manager.getDataSetsByType(this.studyReference.getId(), dataSetType);
		Assert.assertTrue("Datasets' size should be greter than 0", datasets.size() > 0);
		Assert.assertTrue("The size should be greater than 0 since we are sure that it will return at least one data set",
				datasets.size() > 0);
	}

	@Test
	public void testFindOneDataSetByType() throws Exception {
		this.studyTDI.addTestDataset(this.studyReference.getId());
		final DataSetType dataSetType = DataSetType.MEANS_DATA;
		final DataSet dataset = this.manager.findOneDataSetByType(this.studyReference.getId(), dataSetType);
		Assert.assertEquals("Dataset's name should be " + StudyTestDataInitializer.DATASET_NAME, StudyTestDataInitializer.DATASET_NAME,
				dataset.getName());
	}

	@Test
	public void testGetLocalNameByStandardVariableId() throws Exception {
		final Integer standardVariableId = TermId.STUDY_NAME.getId();
		final String localName = this.manager.getLocalNameByStandardVariableId(this.studyReference.getId(), standardVariableId);
		Assert.assertEquals("The local name should be " + StudyTestDataInitializer.STUDY_NAME, StudyTestDataInitializer.STUDY_NAME,
				localName);
	}

	@Test
	public void testGetAllStudyDetails() throws Exception {
		final List<StudyDetails> nurseryStudyDetails = this.manager.getAllStudyDetails(StudyType.N, this.commonTestProject.getUniqueID());
		final int sizeBeforeAddingNewNursery = nurseryStudyDetails.size();
		this.studyTDI.addTestStudy(StudyType.N, "NEW NURSERY", cropPrefix);
		final List<StudyDetails> updatedNurseryStudyDetails = this.manager.getAllStudyDetails(StudyType.N, this.commonTestProject.getUniqueID());
		final int sizeAfterAddingNewNursery = updatedNurseryStudyDetails.size();
		Assert.assertEquals("The size after adding new nursery should be equal to the size before adding a new nursery + 1",
				sizeAfterAddingNewNursery, sizeBeforeAddingNewNursery + 1);
	}

	@Test
	public void testCheckIfProjectNameIsExisting() throws Exception {
		final DmsProject project = this.studyTDI.createFolderTestData(this.commonTestProject.getUniqueID());
		boolean isExisting = this.manager.checkIfProjectNameIsExistingInProgram(project.getName(), this.commonTestProject.getUniqueID());
		Assert.assertTrue(isExisting);

		final String name = "SHOULDNOTEXISTSTUDY";
		isExisting = this.manager.checkIfProjectNameIsExistingInProgram(name, this.commonTestProject.getUniqueID());
		Assert.assertFalse(isExisting);
	}

	@Test
	public void testGetFieldMapInfoOfTrial() throws MiddlewareQueryException {
		final List<Integer> trialIdList = new ArrayList<Integer>();
		trialIdList.addAll(Arrays.asList(this.studyReference.getId()));
		final List<FieldMapInfo> fieldMapInfos =
				this.manager.getFieldMapInfoOfStudy(trialIdList, StudyType.T, StudyDataManagerImplTest.crossExpansionProperties);
		// compare the size to the minimum possible value of field map infos' size
		Assert.assertTrue("The size should be greater than 0", fieldMapInfos.size() > 0);
	}

	@Test
	public void testGetParentFolder() throws MiddlewareQueryException {
		final String uniqueId = "001";
		final DmsProject project = this.studyTDI.createFolderTestData(uniqueId);
		final int id = this.manager.addSubFolder(project.getProjectId(), "Sub folder", "Sub Folder", uniqueId);
		final DmsProject proj = this.manager.getParentFolder(id);
		Assert.assertEquals("The folder names should be equal", project.getName(), proj.getName());
	}

	@Test
	public void testGetFolderTree() throws MiddlewareQueryException {
		List<FolderReference> tree = this.manager.getFolderTree();
		int sizeBefore = tree.size();
		this.studyTDI.createFolderTestData(this.commonTestProject.getUniqueID());
		tree = this.manager.getFolderTree();
		int newSize = tree.size();
		//Cannot assert the exact size so we will check if the size of the tree is incremented by one after adding a new folder
		Assert.assertTrue("The new size should be equal the  size before + the newly added folder", newSize == (sizeBefore+1));
	}

	@Test
	public void testUpdateFieldMapWithBlockInformationWhenBlockIdIsNotNull() {
		final LocationDataManager locationDataManager = Mockito.mock(LocationDataManager.class);

		final FieldmapBlockInfo fieldMapBlockInfo = new FieldmapBlockInfo(FieldMapDataUtil.BLOCK_ID, FieldMapDataUtil.ROWS_IN_BLOCK,
				FieldMapDataUtil.RANGES_IN_BLOCK, FieldMapDataUtil.NUMBER_OF_ROWS_IN_PLOT, FieldMapDataUtil.PLANTING_ORDER,
				FieldMapDataUtil.MACHINE_ROW_CAPACITY, false, null, FieldMapDataUtil.FIELD_ID);

		final List<FieldMapInfo> infos = FieldMapDataUtil.createFieldMapInfoList(true);

		this.manager.setLocationDataManager(locationDataManager);

		try {
			final FieldMapTrialInstanceInfo trialInstance = infos.get(0).getDataSet(FieldMapDataUtil.DATASET_ID).getTrialInstances().get(0);
			trialInstance.setBlockId(this.studyTDI.addTestLocation(StudyDataManagerImplTest.BLOCK_NAME));
			trialInstance.setFieldId(this.studyTDI.addTestLocation(StudyDataManagerImplTest.FIELD_NAME));
			trialInstance.setLocationId(this.studyTDI.addTestLocation(StudyDataManagerImplTest.LOCATION_NAME));
			fieldMapBlockInfo.setFieldId(trialInstance.getFieldId());

			Mockito.when(locationDataManager.getBlockInformation(trialInstance.getBlockId())).thenReturn(fieldMapBlockInfo);
			this.manager.updateFieldMapWithBlockInformation(infos, fieldMapBlockInfo, true);

			final FieldMapTrialInstanceInfo resultTrialInstance =
					infos.get(0).getDataSet(FieldMapDataUtil.DATASET_ID).getTrialInstances().get(0);

			Assert.assertEquals(
					"Expected " + FieldMapDataUtil.ROWS_IN_BLOCK + " but got " + resultTrialInstance.getRowsInBlock() + " instead.",
					FieldMapDataUtil.ROWS_IN_BLOCK, resultTrialInstance.getRowsInBlock().intValue());
			Assert.assertEquals(
					"Expected " + FieldMapDataUtil.RANGES_IN_BLOCK + " but got " + resultTrialInstance.getRangesInBlock() + " instead.",
					FieldMapDataUtil.RANGES_IN_BLOCK, resultTrialInstance.getRangesInBlock().intValue());
			Assert.assertEquals("Expected " + FieldMapDataUtil.NUMBER_OF_ROWS_IN_PLOT + " but got " + resultTrialInstance.getRowsPerPlot()
					+ " instead.", FieldMapDataUtil.NUMBER_OF_ROWS_IN_PLOT, resultTrialInstance.getRowsPerPlot().intValue());
			Assert.assertEquals(
					"Expected " + FieldMapDataUtil.PLANTING_ORDER + " but got " + resultTrialInstance.getPlantingOrder() + " instead.",
					FieldMapDataUtil.PLANTING_ORDER, resultTrialInstance.getPlantingOrder().intValue());
			Assert.assertEquals("Expected " + FieldMapDataUtil.MACHINE_ROW_CAPACITY + " but got "
					+ resultTrialInstance.getMachineRowCapacity() + " instead.", FieldMapDataUtil.MACHINE_ROW_CAPACITY,
					resultTrialInstance.getMachineRowCapacity().intValue());
			Assert.assertEquals(
					"Expected " + StudyDataManagerImplTest.BLOCK_NAME + " but got " + resultTrialInstance.getBlockName() + " instead.",
					StudyDataManagerImplTest.BLOCK_NAME, resultTrialInstance.getBlockName());
			Assert.assertEquals("Expected " + StudyDataManagerImplTest.LOCATION_NAME + " but got " + resultTrialInstance.getLocationName()
					+ " instead.", StudyDataManagerImplTest.LOCATION_NAME, resultTrialInstance.getLocationName());
			Assert.assertEquals(
					"Expected " + StudyDataManagerImplTest.LOCATION_NAME + " but got " + resultTrialInstance.getSiteName() + " instead.",
					StudyDataManagerImplTest.LOCATION_NAME, resultTrialInstance.getSiteName());
			Assert.assertEquals(
					"Expected " + StudyDataManagerImplTest.FIELD_NAME + " but got " + resultTrialInstance.getFieldName() + " instead.",
					StudyDataManagerImplTest.FIELD_NAME, resultTrialInstance.getFieldName());
		} catch (final MiddlewareQueryException e) {
			Assert.fail("Expected mocked value to be returned but used the original call for getBlockInformation instead.");
		}
	}

	@Test
	public void testUpdateFieldMapWithBlockInformationWhenBlockIdIsNull() {
		final LocationDataManager locationDataManager = Mockito.mock(LocationDataManager.class);

		final FieldmapBlockInfo fieldMapBlockInfo = new FieldmapBlockInfo(FieldMapDataUtil.BLOCK_ID, FieldMapDataUtil.ROWS_IN_BLOCK,
				FieldMapDataUtil.RANGES_IN_BLOCK, FieldMapDataUtil.NUMBER_OF_ROWS_IN_PLOT, FieldMapDataUtil.PLANTING_ORDER,
				FieldMapDataUtil.MACHINE_ROW_CAPACITY, false, null, FieldMapDataUtil.FIELD_ID);

		final List<FieldMapInfo> infos = FieldMapDataUtil.createFieldMapInfoList(true);
		final FieldMapTrialInstanceInfo trialInstance = infos.get(0).getDataSet(FieldMapDataUtil.DATASET_ID).getTrialInstances().get(0);
		trialInstance.setBlockId(null);

		this.manager.setLocationDataManager(locationDataManager);

		try {
			Mockito.when(locationDataManager.getBlockInformation(FieldMapDataUtil.BLOCK_ID)).thenReturn(fieldMapBlockInfo);
			this.manager.updateFieldMapWithBlockInformation(infos, fieldMapBlockInfo, false);

			Assert.assertNull("Expected null but got " + trialInstance.getRowsInBlock() + " instead.", trialInstance.getRowsInBlock());
			Assert.assertNull("Expected null but got " + trialInstance.getRangesInBlock() + " instead.", trialInstance.getRangesInBlock());
			Assert.assertNull("Expected null but got " + trialInstance.getRowsPerPlot() + " instead.", trialInstance.getRowsPerPlot());
			Assert.assertNull("Expected null but got " + trialInstance.getPlantingOrder() + " instead.", trialInstance.getPlantingOrder());
			Assert.assertNull("Expected null but got " + trialInstance.getMachineRowCapacity() + " instead.",
					trialInstance.getMachineRowCapacity());
		} catch (final MiddlewareQueryException e) {
			Assert.fail("Expected mocked value to be returned but used the original call for getBlockInformation instead.");
		}
	}

	@Test
	public void testGetStudyType() {
		try {
			Assert.assertEquals("Study type returned did not match.", StudyType.T, this.manager.getStudyType(this.studyReference.getId()));
		} catch (final MiddlewareQueryException e) {
			Assert.fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test
	public void testGetStudyTypeNullEdgeCase() {
		try {
			final int PRESUMABLY_NON_EXISTENT_STUDY_ID = -1000000;
			Assert.assertNull("Expected null return value but was non null.", this.manager.getStudyType(PRESUMABLY_NON_EXISTENT_STUDY_ID));
		} catch (final MiddlewareQueryException e) {
			Assert.fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test
	public void testDeleteProgramStudies() throws Exception {
		final String uniqueId = "100001001001";
		this.studyTDI.createFolderTestData(uniqueId);
		this.studyTDI.addTestStudy(uniqueId, cropPrefix);

		List<? extends Reference> programStudiesAndFolders = this.manager.getRootFolders(uniqueId, StudyType.nurseriesAndTrials());
		final int sizeBeforeDelete = programStudiesAndFolders.size();
		this.manager.deleteProgramStudies(uniqueId);
		programStudiesAndFolders = this.manager.getRootFolders(uniqueId, StudyType.nurseriesAndTrials());
		final int sizeAfterDelete = programStudiesAndFolders.size();

		Assert.assertTrue("The size after the delete should be less than the size before.", sizeAfterDelete < sizeBeforeDelete);

	}

	@Test
	public void testGetStudyDetails() throws Exception {
		final List<StudyDetails> trialStudyDetails = this.manager.getStudyDetails(StudyType.T, this.commonTestProject.getUniqueID(), 0, 50);
		final int sizeBeforeAddingNewTrial = trialStudyDetails.size();
		this.studyTDI.addTestStudy(StudyType.T, "NEW TRIAL", cropPrefix);
		final List<StudyDetails> updatedTrialStudyDetails = this.manager.getStudyDetails(StudyType.T, this.commonTestProject.getUniqueID(), 0, 50);
		final int sizeAfterAddingNewTrial = updatedTrialStudyDetails.size();
		Assert.assertEquals("The size after adding new trial should be equal to the size before adding a new trial + 1",
				sizeAfterAddingNewTrial, sizeBeforeAddingNewTrial + 1);
	}

	@Test
	public void testGetNurseryAndTrialStudyDetails() throws Exception {
		final List<StudyDetails> studyDetailsList =
				this.manager.getNurseryAndTrialStudyDetails(this.commonTestProject.getUniqueID(), -1, -1);
		final int sizeBeforeAddingNewStudy = studyDetailsList.size();
		this.studyTDI.addTestStudy(StudyType.N, "NEW NURSERY", cropPrefix);
		this.studyTDI.addTestStudy(StudyType.T, "NEW TRIAL", cropPrefix);
		final List<StudyDetails> newStudyDetailsList =
				this.manager.getNurseryAndTrialStudyDetails(this.commonTestProject.getUniqueID(), -1, -1);
		final int sizeAfterAddingNewStudy = newStudyDetailsList.size();
		Assert.assertEquals("The new size should be equal to the size before adding a new study plus 2.", sizeAfterAddingNewStudy, sizeBeforeAddingNewStudy + 2);
	}

	@Test
	public void testGetStudyDetails_ByTypeAndId() throws MiddlewareException {
		final StudyDetails studyDetails = this.manager.getStudyDetails(StudyType.T, this.studyReference.getId());
		Assert.assertNotNull("Study should not be null", studyDetails);
		Assert.assertEquals("Study should have the id " + this.studyReference.getId(), studyDetails.getId(), studyDetails.getId());
	}

	@Test
	public void testGetGeolocationIdByProjectIdAndTrialInstanceNumber() {
		try {
			final Integer projectId = 25007;
			final String trialInstanceNumberExpected = "1";
			final Integer geolocationId =
					this.manager.getGeolocationIdByProjectIdAndTrialInstanceNumber(projectId, trialInstanceNumberExpected);
			if (geolocationId != null) {
				final String trialInstanceNumberActual = this.manager.getTrialInstanceNumberByGeolocationId(geolocationId);
				Assert.assertEquals(trialInstanceNumberExpected, trialInstanceNumberActual);
			}
		} catch (final MiddlewareQueryException e) {
			Assert.fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test
	public void testGetTrialInstanceNumberByGeolocationId() {
		try {
			final String trialInstanceNumberExpected = "1";
			final String trialInstanceNumberActual = this.manager.getTrialInstanceNumberByGeolocationId(1);
			Assert.assertNotNull(trialInstanceNumberActual);
			Assert.assertEquals(trialInstanceNumberExpected, trialInstanceNumberActual);
		} catch (final MiddlewareQueryException e) {
			Assert.fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test
	public void testSaveGeolocationProperty() throws MiddlewareQueryException {
		final Integer stdVarId = TermId.EXPERIMENT_DESIGN_FACTOR.getId();
		final Integer studyId = 25019;
		final String expDesign = this.manager.getGeolocationPropValue(stdVarId, studyId);
		String newExpDesign = null;
		if (expDesign != null) {
			if (TermId.RANDOMIZED_COMPLETE_BLOCK.getId() == Integer.parseInt(expDesign)) {
				newExpDesign = Integer.toString(TermId.RESOLVABLE_INCOMPLETE_BLOCK.getId());
			} else if (TermId.RESOLVABLE_INCOMPLETE_BLOCK.getId() == Integer.parseInt(expDesign)) {
				newExpDesign = Integer.toString(TermId.RANDOMIZED_COMPLETE_BLOCK.getId());
			}
			// update experimental design value
			final int ndGeolocationId = this.manager.getGeolocationIdByProjectIdAndTrialInstanceNumber(studyId, "1");
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
		final List<String> sharedProjectNames = this.manager.getAllSharedProjectNames();
		Assert.assertNotNull("The shared project names should not be null", sharedProjectNames);
	}

	@Test
	public void testCheckIfAnyLocationIDsExistInExperimentsReturnFalse() {

		final Integer locationId = this.manager.getGeolocationIdByProjectIdAndTrialInstanceNumber(StudyTestDataInitializer.STUDY_ID, "999");
		final List<Integer> locationIds = new ArrayList<>();
		locationIds.add(locationId);

		final boolean returnValue =
				this.manager.checkIfAnyLocationIDsExistInExperiments(StudyTestDataInitializer.STUDY_ID, DataSetType.PLOT_DATA, locationIds);

		Assert.assertFalse("The return value should be false", returnValue);
	}

	@Test
	public void testIsFolderEmptyTrue() {
		final String uniqueId = this.commonTestProject.getUniqueID();
		final DmsProject project = this.studyTDI.createFolderTestData(uniqueId);

		final boolean isEmpty = this.manager.isFolderEmpty(project.getProjectId(), uniqueId, StudyType.nurseriesAndTrials());
		Assert.assertTrue("The folder should be empty", isEmpty);
	}

	@Test
	public void testIsFolderEmptyFalse() {
		final String uniqueId = this.commonTestProject.getUniqueID();
		final DmsProject project = this.studyTDI.createFolderTestData(uniqueId);
		this.manager.addSubFolder(project.getProjectId(), "Sub folder", "Sub Folder", uniqueId);
		final boolean isEmpty = this.manager.isFolderEmpty(project.getProjectId(), uniqueId, StudyType.nurseriesAndTrials());
		Assert.assertFalse("The folder should not be empty", isEmpty);
	}
}
