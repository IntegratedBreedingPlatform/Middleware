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
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.dao.dms.InstanceMetadata;
import org.generationcp.middleware.data.initializer.DMSVariableTestDataInitializer;
import org.generationcp.middleware.data.initializer.StudyTestDataInitializer;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.dms.ExperimentValues;
import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.dms.StudySearchMatchingOption;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.fieldbook.FieldMapInfo;
import org.generationcp.middleware.domain.fieldbook.FieldMapTrialInstanceInfo;
import org.generationcp.middleware.domain.fieldbook.FieldmapBlockInfo;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.domain.search.StudyResultSet;
import org.generationcp.middleware.domain.search.filter.BrowseStudyQueryFilter;
import org.generationcp.middleware.domain.search.filter.GidStudyQueryFilter;
import org.generationcp.middleware.domain.search.filter.ParentFolderStudyQueryFilter;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.manager.api.UserDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.dms.StudyType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.generationcp.middleware.utils.test.FieldMapDataUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

public class StudyDataManagerImplTest extends IntegrationTestBase {

	private static final String LOCATION_NAME = "LOCATION NAME";

	private static final String FIELD_NAME = "FIELD NAME";

	private static final String BLOCK_NAME = "BLOCK NAME";

	private static final int PRESUMABLY_NON_EXISTENT_STUDY_TYPE_ID = -10000;

	private StudyDataManagerImpl manager;

	@Autowired
	private OntologyDataManager ontologyManager;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private GermplasmDataManager germplasmDataDM;

	@Autowired
	private LocationDataManager locationManager;

	@Autowired
	private UserDataManager userDataManager;

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
				this.locationManager, this.userDataManager);

		this.studyReference = this.studyTDI.addTestStudy(cropPrefix);

		final StudyType studyType = new StudyType();
		studyType.setStudyTypeId(6);
		studyType.setLabel(StudyTypeDto.TRIAL_LABEL);
		studyType.setName(StudyTypeDto.TRIAL_NAME);
		studyType.setCvTermId(10010);
		studyType.setVisible(true);
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
	public void testGetStudiesByFolder() throws Exception {
		final StudyResultSet resultSet = this.manager.searchStudies(new ParentFolderStudyQueryFilter(1), 5);
		// We are sure that the result set will return at least one study, the study that we added in the setup
		Assert.assertTrue("The size should be greater than 0.", resultSet.size() > 0);
	}

	@Test
	public void testSearchStudiesForName() throws Exception {
		// Study search query expect datasets for studies to be returned
		this.studyTDI.addTestDataset(this.studyReference.getId());
		
		final BrowseStudyQueryFilter filter = new BrowseStudyQueryFilter();
		filter.setStudySearchMatchingOption(StudySearchMatchingOption.EXACT_MATCHES);
		filter.setProgramUUID(this.studyReference.getProgramUUID());

		filter.setName(RandomStringUtils.randomAlphanumeric(100));
		StudyResultSet resultSet = this.manager.searchStudies(filter, 10);
		Assert.assertEquals("The size should be zero since the study is not existing", 0, resultSet.size());

		filter.setName(this.studyReference.getName());
		resultSet = this.manager.searchStudies(filter, 10);

		Assert.assertTrue("The study search by name results should contain test study", resultSet.size() > 0);
	}

	@Test
	public void testSearchStudiesForStartDate() throws Exception {
		// Study search query expect datasets for studies to be returned
		this.studyTDI.addTestDataset(this.studyReference.getId());
		
		final BrowseStudyQueryFilter filter = new BrowseStudyQueryFilter();
		filter.setStartDate(new Integer(StudyTestDataInitializer.START_DATE));
		filter.setProgramUUID(this.studyReference.getProgramUUID());

		final StudyResultSet resultSet = this.manager.searchStudies(filter, 10);
		Assert.assertTrue("The study search by start date results should contain test study", resultSet.size() > 0);
	}

	@Test
	public void testSearchStudiesByGid() throws Exception {
		// Flushing to force Hibernate to synchronize with the underlying database before the search
		// Without this the inserted experiment is not retrieved properly
		this.manager.getActiveSession().flush();

		final Integer gid = this.studyTDI.getGid();
		final GidStudyQueryFilter filter = new GidStudyQueryFilter(gid);
		final StudyResultSet resultSet = this.manager.searchStudies(filter, 50);
		// We are sure that the result set will contain the test study we added in the set up
		Assert.assertTrue("The size should be greater than 0", resultSet.size() > 0);
	}

	@Test
	public void testGetRootFolders() throws Exception {
		final List<Reference> rootFolders = this.manager.getRootFolders(this.commonTestProject.getUniqueID());
		Assert.assertNotNull(rootFolders);
		Assert.assertFalse("Root folders should not be empty because it contains the templates for Studies.", rootFolders.isEmpty());
	}

	@Test
	public void testGetChildrenOfFolder() throws Exception {

		final String uniqueId = this.commonTestProject.getUniqueID();
		final DmsProject mainFolder = this.studyTDI.createFolderTestData(uniqueId);
		final String subFolderName = "Sub folder Name";
		final String subFolderDescription = "Sub Folder Description";
		final int subFolderID =
				this.manager.addSubFolder(mainFolder.getProjectId(), subFolderName, subFolderDescription, uniqueId, "objective");
		this.manager.moveDmsProject(this.studyReference.getId(), mainFolder.getProjectId(), true);

		final List<Reference> childrenNodes = this.manager.getChildrenOfFolder(mainFolder.getProjectId(), uniqueId);
		Assert.assertNotNull(childrenNodes);
		Assert.assertEquals("The size should be one.", 2, childrenNodes.size());
		for (final Reference reference : childrenNodes) {
			if (reference.isFolder()) {
				Assert.assertEquals(subFolderID, reference.getId().intValue());
				Assert.assertEquals(subFolderName, reference.getName());
				Assert.assertEquals(subFolderDescription, reference.getDescription());
				Assert.assertEquals(uniqueId, reference.getProgramUUID());
			} else {
				final StudyReference study = (StudyReference) reference;
				Assert.assertEquals(this.studyReference.getId(), study.getId());
				Assert.assertEquals(this.studyReference.getName(), study.getName());
				Assert.assertEquals(this.studyReference.getDescription(), study.getDescription());
				Assert.assertEquals(uniqueId, study.getProgramUUID());
				Assert.assertEquals(this.studyReference.getStudyType(), study.getStudyType());
				Assert.assertFalse(study.getIsLocked());
				Assert.assertEquals(this.studyReference.getOwnerId(), study.getOwnerId());
				final User user = this.userDataManager.getUserById(this.studyReference.getOwnerId());
				final Person person = this.userDataManager.getPersonById(user.getPersonid());
				Assert.assertEquals(person.getFirstName() + " " + person.getLastName(), study.getOwnerName());
			}
		}
	}

	@Test
	public void testGetAllFolders() {
		final int originalSize = this.manager.getAllFolders().size();
		this.studyTDI.createFolderTestData(this.commonTestProject.getUniqueID());
		this.studyTDI.createFolderTestData(null);

		final int newSize = this.manager.getAllFolders().size();
		// We only assert that there are two folders added.
		Assert.assertEquals("The new size should be equal to the original size + 2", originalSize + 2, newSize);
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
		final int propertyId = 2120;
		final VariableTypeList factors = dataset.getFactorsByProperty(propertyId);
		Assert.assertEquals(
				"The size should be 1 since we added 1 factor, with property id = " + propertyId + ", in the set up of the data set", 1,
				factors.size());
	}

	@Test
	public void testGetFactorsByPhenotypicType() throws Exception {
		final DatasetReference datasetReference = this.studyTDI.addTestDataset(this.studyReference.getId());
		final DataSet dataset = this.manager.getDataSet(datasetReference.getId());
		final VariableTypeList factors = dataset.getFactorsByPhenotypicType(PhenotypicType.TRIAL_ENVIRONMENT);
		Assert.assertEquals("The size should be 3 since we added 3 factors in the set up of the data set", 3, factors.size());
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
	public void testGetAllStudyDetails() throws Exception {
		final List<StudyDetails> nurseryStudyDetails =
				this.manager.getAllStudyDetails(StudyTypeDto.getNurseryDto(), this.commonTestProject.getUniqueID());
		final int sizeBeforeAddingNewNursery = nurseryStudyDetails.size();
		this.studyTDI.addTestStudy(StudyTypeDto.getNurseryDto(), "NEW NURSERY", cropPrefix);
		final List<StudyDetails> updatedNurseryStudyDetails =
				this.manager.getAllStudyDetails(StudyTypeDto.getNurseryDto(), this.commonTestProject.getUniqueID());
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
	public void testGetFieldMapInfoOfStudy() {
		final List<Integer> trialIdList = new ArrayList<Integer>();
		trialIdList.addAll(Arrays.asList(this.studyReference.getId()));
		final List<FieldMapInfo> fieldMapInfos =
				this.manager.getFieldMapInfoOfStudy(trialIdList, StudyDataManagerImplTest.crossExpansionProperties);
		// compare the size to the minimum possible value of field map infos' size
		Assert.assertTrue("The size should be greater than 0", fieldMapInfos.size() > 0);
	}

	@Test
	public void testGetParentFolder() {
		final String uniqueId = "001";
		final DmsProject project = this.studyTDI.createFolderTestData(uniqueId);
		final int id = this.manager.addSubFolder(project.getProjectId(), "Sub folder", "Sub Folder", uniqueId, "objective");
		final DmsProject proj = this.manager.getParentFolder(id);
		Assert.assertEquals("The folder names should be equal", project.getName(), proj.getName());
	}

	@Test
	public void testGetFolderTree() {
		List<FolderReference> tree = this.manager.getFolderTree();
		final int sizeBefore = tree.size();
		this.studyTDI.createFolderTestData(this.commonTestProject.getUniqueID());
		tree = this.manager.getFolderTree();
		final int newSize = tree.size();
		// Cannot assert the exact size so we will check if the size of the tree is incremented by one after adding a new folder
		Assert.assertEquals("The new size should be equal the  size before + the newly added folder", newSize, (sizeBefore + 1));
	}

	@Test
	public void testUpdateFieldMapWithBlockInformationWhenBlockIdIsNotNull() {
		final LocationDataManager locationDataManager = Mockito.mock(LocationDataManager.class);

		final FieldmapBlockInfo fieldMapBlockInfo = new FieldmapBlockInfo(FieldMapDataUtil.BLOCK_ID, FieldMapDataUtil.ROWS_IN_BLOCK,
				FieldMapDataUtil.RANGES_IN_BLOCK, FieldMapDataUtil.NUMBER_OF_ROWS_IN_PLOT, FieldMapDataUtil.PLANTING_ORDER,
				FieldMapDataUtil.MACHINE_ROW_CAPACITY, false, null, FieldMapDataUtil.FIELD_ID);

		final List<FieldMapInfo> infos = FieldMapDataUtil.createFieldMapInfoList();

		this.manager.setLocationDataManager(locationDataManager);

		try {
			final FieldMapTrialInstanceInfo trialInstance = infos.get(0).getDataSet(FieldMapDataUtil.DATASET_ID).getTrialInstances().get(0);
			trialInstance.setBlockId(this.studyTDI.addTestLocation(StudyDataManagerImplTest.BLOCK_NAME));
			trialInstance.setFieldId(this.studyTDI.addTestLocation(StudyDataManagerImplTest.FIELD_NAME));
			trialInstance.setLocationId(this.studyTDI.addTestLocation(StudyDataManagerImplTest.LOCATION_NAME));
			fieldMapBlockInfo.setFieldId(trialInstance.getFieldId());

			Mockito.when(locationDataManager.getBlockInformation(trialInstance.getBlockId())).thenReturn(fieldMapBlockInfo);
			this.manager.updateFieldMapWithBlockInformation(infos, true);

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

		final List<FieldMapInfo> infos = FieldMapDataUtil.createFieldMapInfoList();
		final FieldMapTrialInstanceInfo trialInstance = infos.get(0).getDataSet(FieldMapDataUtil.DATASET_ID).getTrialInstances().get(0);
		trialInstance.setBlockId(null);

		this.manager.setLocationDataManager(locationDataManager);

		try {
			Mockito.when(locationDataManager.getBlockInformation(FieldMapDataUtil.BLOCK_ID)).thenReturn(fieldMapBlockInfo);
			this.manager.updateFieldMapWithBlockInformation(infos, false);

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
			Assert.assertEquals("Study type returned did not match.", StudyTypeDto.getTrialDto(),
					this.manager.getStudyType(this.studyReference.getStudyType().getId()));
		} catch (final MiddlewareQueryException e) {
			Assert.fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test
	public void testGetStudyTypeNullEdgeCase() {
		try {
			Assert.assertNull("Expected null return value but was non null.",
					this.manager.getStudyType(PRESUMABLY_NON_EXISTENT_STUDY_TYPE_ID));
		} catch (final MiddlewareQueryException e) {
			Assert.fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test
	public void testDeleteProgramStudies() throws Exception {
		final String uniqueId = "100001001001";
		this.studyTDI.createFolderTestData(uniqueId);
		this.studyTDI.addTestStudy(uniqueId, cropPrefix);

		List<? extends Reference> programStudiesAndFolders = this.manager.getRootFolders(uniqueId);
		final int sizeBeforeDelete = programStudiesAndFolders.size();
		this.manager.deleteProgramStudies(uniqueId);
		programStudiesAndFolders = this.manager.getRootFolders(uniqueId);
		final int sizeAfterDelete = programStudiesAndFolders.size();

		Assert.assertTrue("The size after the delete should be less than the size before.", sizeAfterDelete < sizeBeforeDelete);

	}

	@Test
	public void testGetStudyDetailsByStudyType() throws Exception {
		final List<StudyDetails> trialStudyDetails =
				this.manager.getStudyDetails(StudyTypeDto.getTrialDto(), this.commonTestProject.getUniqueID(), 0, 50);
		final int sizeBeforeAddingNewTrial = trialStudyDetails.size();
		final StudyReference newStudy = this.studyTDI.addTestStudy(StudyTypeDto.getTrialDto(), "NEW STUDY", cropPrefix);
		final List<StudyDetails> updatedStudyDetails =
				this.manager.getStudyDetails(StudyTypeDto.getTrialDto(), this.commonTestProject.getUniqueID(), 0, 50);
		final int sizeAfterAddingNewStudy = updatedStudyDetails.size();
		Assert.assertEquals("The size after adding new study should be equal to the size before adding a new study + 1",
				sizeAfterAddingNewStudy, sizeBeforeAddingNewTrial + 1);
		for (final StudyDetails details : updatedStudyDetails) {
			if (this.studyReference.getId().equals(details.getId())) {
				Assert.assertEquals(this.studyReference.getName(), details.getStudyName());
				Assert.assertEquals(this.studyReference.getStudyType(), details.getStudyType());
				Assert.assertEquals(this.studyReference.getOwnerId().toString(), details.getCreatedBy());
			} else if (newStudy.getId().equals(details.getId())) {
				Assert.assertEquals(newStudy.getName(), details.getStudyName());
				Assert.assertEquals(newStudy.getStudyType(), details.getStudyType());
				Assert.assertEquals(newStudy.getOwnerId().toString(), details.getCreatedBy());
			}
			// Do not verify unseeded data like study templates which are also retrieved
			if (details.getProgramUUID() != null) {
				this.verifyCommonStudyDetails(details);
			}
		}
	}

	private void verifyCommonStudyDetails(final StudyDetails details) {
		Assert.assertEquals(StudyTestDataInitializer.STUDY_DESCRIPTION, details.getDescription());
		Assert.assertEquals(StudyTestDataInitializer.START_DATE, details.getStartDate());
		Assert.assertEquals(StudyTestDataInitializer.END_DATE, details.getEndDate());
		Assert.assertEquals(StudyTestDataInitializer.OBJECTIVE, details.getObjective());
		Assert.assertEquals(this.commonTestProject.getUniqueID(), details.getProgramUUID());
		Assert.assertFalse(details.getIsLocked());
	}

	@Test
	public void testGetNurseryAndTrialStudyDetails() throws Exception {
		final List<StudyDetails> studyDetailsList =
				this.manager.getNurseryAndTrialStudyDetails(this.commonTestProject.getUniqueID(), -1, -1);
		final int sizeBeforeAddingNewStudy = studyDetailsList.size();
		final StudyReference nursery = this.studyTDI.addTestStudy(StudyTypeDto.getNurseryDto(), "NEW NURSERY", cropPrefix);
		final StudyReference trial = this.studyTDI.addTestStudy(StudyTypeDto.getTrialDto(), "NEW TRIAL", cropPrefix);
		final List<StudyDetails> newStudyDetailsList =
				this.manager.getNurseryAndTrialStudyDetails(this.commonTestProject.getUniqueID(), -1, -1);
		final int sizeAfterAddingNewStudy = newStudyDetailsList.size();
		Assert.assertEquals("The new size should be equal to the size before adding a new study plus 2.", sizeAfterAddingNewStudy,
				sizeBeforeAddingNewStudy + 2);
		for (final StudyDetails details : newStudyDetailsList) {
			if (nursery.getId().equals(details.getId())) {
				Assert.assertEquals(nursery.getName(), details.getStudyName());
				Assert.assertEquals(nursery.getStudyType(), details.getStudyType());
			} else if (trial.getId().equals(details.getId())) {
				Assert.assertEquals(trial.getName(), details.getStudyName());
				Assert.assertEquals(trial.getStudyType(), details.getStudyType());
			}
			// Do not verify unseeded data like study templates which are also retrieved
			if (details.getProgramUUID() != null) {
				this.verifyCommonStudyDetails(details);
			}
		}
	}

	@Test
	public void testGetStudyDetails() {
		final StudyDetails studyDetails = this.manager.getStudyDetails(this.studyReference.getId());
		Assert.assertNotNull("Study should not be null", studyDetails);
		Assert.assertEquals(this.studyReference.getId(), studyDetails.getId());
		Assert.assertEquals(this.studyReference.getName(), studyDetails.getStudyName());
		Assert.assertEquals(this.studyReference.getStudyType(), studyDetails.getStudyType());
		this.verifyCommonStudyDetails(studyDetails);
	}

	@Test
	public void testGetTrialInstanceNumberByGeolocationId() throws Exception {
		final Integer studyId = this.studyReference.getId();
		final Integer dataSetId = this.studyTDI.addEnvironmentDataset(studyId, "1", "1");
		final TrialEnvironments trialEnvironments = this.manager.getTrialEnvironmentsInDataset(dataSetId);
		Assert.assertNotNull(trialEnvironments.getTrialEnvironments());
		Assert.assertFalse(trialEnvironments.getTrialEnvironments().isEmpty());
		
		final String trialInstanceNumberActual = this.manager.getTrialInstanceNumberByGeolocationId(trialEnvironments.getTrialEnvironments().iterator().next().getId());
		Assert.assertEquals("1", trialInstanceNumberActual);
		
	}

	@Test
	public void testGetAllSharedProjectNames() {
		final List<String> sharedProjectNames = this.manager.getAllSharedProjectNames();
		Assert.assertNotNull("The shared project names should not be null", sharedProjectNames);
	}

	@Test
	public void testIsFolderEmptyTrue() {
		final String uniqueId = this.commonTestProject.getUniqueID();
		final DmsProject project = this.studyTDI.createFolderTestData(uniqueId);

		final boolean isEmpty = this.manager.isFolderEmpty(project.getProjectId(), uniqueId);
		Assert.assertTrue("The folder should be empty", isEmpty);
	}

	@Test
	public void testIsFolderEmptyFalse() {
		final String uniqueId = this.commonTestProject.getUniqueID();
		final DmsProject project = this.studyTDI.createFolderTestData(uniqueId);
		this.manager.addSubFolder(project.getProjectId(), "Sub folder", "Sub Folder", uniqueId, "objective");
		final boolean isEmpty = this.manager.isFolderEmpty(project.getProjectId(), uniqueId);
		Assert.assertFalse("The folder should not be empty", isEmpty);
	}

	@Test
	public void testIsVariableUsedInStudyEnvironmentInOtherProgramsVariableExistsInStudyLevel() {

		// Create project record
		final DmsProject project = new DmsProject();
		final String programUUID = "74364-9075-asdhaskj-74825";
		final String locationNameIdValue = "999999";
		project.setName("projectName");
		project.setDescription("projectDescription");
		project.setProgramUUID(programUUID);

		this.manager.getDmsProjectDao().save(project);

		final DMSVariableType dmsVariableType = new DMSVariableType();
		dmsVariableType.setVariableType(VariableType.ENVIRONMENT_DETAIL);
		dmsVariableType.setLocalName("LOCATION_NAME_ID");
		dmsVariableType.setRank(1);
		dmsVariableType.setStandardVariable(this.manager.getStandardVariableBuilder().create(TermId.LOCATION_ID.getId(), programUUID));

		// Create projectproperty record
		this.manager.getProjectPropertySaver().saveVariableType(project, dmsVariableType, locationNameIdValue);

		Assert.assertTrue(this.manager.isVariableUsedInStudyOrTrialEnvironmentInOtherPrograms(String.valueOf(TermId.LOCATION_ID.getId()),
				locationNameIdValue, ""));
		Assert.assertFalse(this.manager.isVariableUsedInStudyOrTrialEnvironmentInOtherPrograms(String.valueOf(TermId.LOCATION_ID.getId()),
				locationNameIdValue, programUUID));

	}

	@Test
	public void testIsVariableUsedInStudyEnvironmentInOtherProgramsVariableExistsInStudyLevelStudyIsDeleted() {

		// Create project record
		final DmsProject project = new DmsProject();
		final String programUUID = "74364-9075-asdhaskj-74825";
		final String locationNameIdValue = "999999";
		project.setName("projectName");
		project.setDescription("projectDescription");
		project.setProgramUUID(programUUID);
		project.setDeleted(true);

		this.manager.getDmsProjectDao().save(project);

		final DMSVariableType dmsVariableType = new DMSVariableType();
		dmsVariableType.setVariableType(VariableType.ENVIRONMENT_DETAIL);
		dmsVariableType.setLocalName("LOCATION_NAME_ID");
		dmsVariableType.setRank(1);
		dmsVariableType.setStandardVariable(this.manager.getStandardVariableBuilder().create(TermId.LOCATION_ID.getId(), programUUID));

		// Create projectproperty record
		this.manager.getProjectPropertySaver().saveVariableType(project, dmsVariableType, locationNameIdValue);

		Assert.assertFalse(this.manager.isVariableUsedInStudyOrTrialEnvironmentInOtherPrograms(String.valueOf(TermId.LOCATION_ID.getId()),
				locationNameIdValue, ""));
		Assert.assertFalse(this.manager.isVariableUsedInStudyOrTrialEnvironmentInOtherPrograms(String.valueOf(TermId.LOCATION_ID.getId()),
				locationNameIdValue, programUUID));

	}

	@Test
	public void testIsVariableUsedInStudEnvironmentInOtherProgramsVariableExistsInEnvironmentLevel() {

		// Create project record
		final DmsProject project = new DmsProject();
		final String programUUID = "74364-9075-asdhaskj-74825";
		final String locationNameIdValue = "999999";
		project.setName("projectName");
		project.setDescription("projectDescription");
		project.setProgramUUID(programUUID);
		this.manager.getDmsProjectDao().save(project);

		final DMSVariableType dmsVariableType = new DMSVariableType();
		dmsVariableType.setVariableType(VariableType.ENVIRONMENT_DETAIL);
		dmsVariableType.setLocalName("LOCATION_NAME_ID");
		dmsVariableType.setRank(1);
		dmsVariableType.setStandardVariable(this.manager.getStandardVariableBuilder().create(TermId.LOCATION_ID.getId(), programUUID));
		dmsVariableType.setRole(PhenotypicType.TRIAL_ENVIRONMENT);

		// Create nd_geolocation and nd_geolocation prop records
		final VariableList variableList = new VariableList();
		final Variable variable = new Variable();
		variable.setVariableType(dmsVariableType);
		variable.setValue(locationNameIdValue);
		variableList.add(variable);
		final Geolocation geolocation = this.manager.getGeolocationSaver().saveGeolocation(variableList, null, false);

		// Create experiment record
		final ExperimentModel experimentModel = new ExperimentModel();
		experimentModel.setTypeId(TermId.TRIAL_ENVIRONMENT_EXPERIMENT.getId());
		experimentModel.setGeoLocation(geolocation);
		experimentModel.setProject(project);
		this.manager.getExperimentModelSaver().getExperimentDao().save(experimentModel);

		Assert.assertFalse(this.manager.isVariableUsedInStudyOrTrialEnvironmentInOtherPrograms(String.valueOf(TermId.LOCATION_ID.getId()),
				locationNameIdValue, programUUID));

	}

	@Test
	public void testIsVariableUsedInStudyEnvironmentInOtherProgramsVariableExistsInEnvironmentLevelStudyIsDeleted() {

		// Create project record
		final DmsProject project = new DmsProject();
		final String programUUID = "74364-9075-asdhaskj-74825";
		final String locationNameIdValue = "999999";
		project.setName("projectName");
		project.setDescription("projectDescription");
		project.setProgramUUID(programUUID);
		project.setDeleted(true);
		this.manager.getDmsProjectDao().save(project);

		final DMSVariableType dmsVariableType = new DMSVariableType();
		dmsVariableType.setVariableType(VariableType.ENVIRONMENT_DETAIL);
		dmsVariableType.setLocalName("LOCATION_NAME_ID");
		dmsVariableType.setRank(1);
		dmsVariableType.setStandardVariable(this.manager.getStandardVariableBuilder().create(TermId.LOCATION_ID.getId(), programUUID));
		dmsVariableType.setRole(PhenotypicType.TRIAL_ENVIRONMENT);

		// Create nd_geolocation and nd_geolocation prop records
		final VariableList variableList = new VariableList();
		final Variable variable = new Variable();
		variable.setVariableType(dmsVariableType);
		variable.setValue(locationNameIdValue);
		variableList.add(variable);
		final Geolocation geolocation = this.manager.getGeolocationSaver().saveGeolocation(variableList, null, false);

		// Create experiment record
		final ExperimentModel experimentModel = new ExperimentModel();
		experimentModel.setTypeId(TermId.TRIAL_ENVIRONMENT_EXPERIMENT.getId());
		experimentModel.setGeoLocation(geolocation);
		experimentModel.setProject(project);
		this.manager.getExperimentModelSaver().getExperimentDao().save(experimentModel);

		Assert.assertFalse(this.manager.isVariableUsedInStudyOrTrialEnvironmentInOtherPrograms(String.valueOf(TermId.LOCATION_ID.getId()),
				locationNameIdValue, ""));
		Assert.assertFalse(this.manager.isVariableUsedInStudyOrTrialEnvironmentInOtherPrograms(String.valueOf(TermId.LOCATION_ID.getId()),
				locationNameIdValue, programUUID));

	}

	@Test
	public void testGetProjectStartDateByProjectId() {
		// Create project record
		final DmsProject project = new DmsProject();
		final String programUUID = "74364-9075-asdhaskj-74825";
		project.setName("Project Name");
		project.setDescription("Project Description");
		project.setProgramUUID(programUUID);
		project.setDeleted(false);
		project.setStartDate("20180403");
		this.manager.getDmsProjectDao().save(project);

		final String startDate = this.manager.getProjectStartDateByProjectId(project.getProjectId());
		Assert.assertEquals(project.getStartDate(), startDate);
	}

	@Test
	public void testIsLocationIdVariable() throws Exception {

		this.studyTDI.addTestDataset(this.studyReference.getId(), DataSetType.SUMMARY_DATA);

		Assert.assertTrue(this.manager.isLocationIdVariable(this.studyReference.getId(), "LOCATION_NAME"));
		Assert.assertFalse(this.manager.isLocationIdVariable(this.studyReference.getId(), "EXPERIMENT_DESIGN_FACTOR"));
		Assert.assertFalse(this.manager.isLocationIdVariable(this.studyReference.getId(), "DUMMY NAME"));

	}

	@Test
	public void testCreateInstanceLocationIdToNameMapFromStudy() throws Exception {

		final String afghanistanLocationId = "1";
		final String albaniaLocationId = "2";
		final String algeriaLocationId = "3";

		this.studyTDI.addTestDataset(this.studyReference.getId(), DataSetType.SUMMARY_DATA);

		this.studyTDI.addEnvironmentDataset(this.studyReference.getId(), afghanistanLocationId, "1");
		this.studyTDI.addEnvironmentDataset(this.studyReference.getId(), albaniaLocationId, "1");
		this.studyTDI.addEnvironmentDataset(this.studyReference.getId(), algeriaLocationId, "1");

		this.sessionProvder.getSession().flush();
		final Map<String, String> result = this.manager.createInstanceLocationIdToNameMapFromStudy(this.studyReference.getId());

		Assert.assertEquals(3, result.size());
		Assert.assertEquals("Afghanistan", result.get(afghanistanLocationId));
		Assert.assertEquals("Albania", result.get(albaniaLocationId));
		Assert.assertEquals("Algeria", result.get(algeriaLocationId));

	}

	@Test
	public void testGetRootFoldersByStudyType() throws Exception {
		final List<Reference> rootFolders = this.manager.getRootFoldersByStudyType(this.commonTestProject.getUniqueID(), null);
		Assert.assertNotNull(rootFolders);
		Assert.assertFalse("Root folders should not be empty because it contains the templates for Studies.", rootFolders.isEmpty());
	}

	@Test
	public void testGetChildrenOfFolderByStudyType() throws Exception {

		final String uniqueId = this.commonTestProject.getUniqueID();
		final DmsProject mainFolder = this.studyTDI.createFolderTestData(uniqueId);
		final int subFolderID = this.manager.addSubFolder(mainFolder.getProjectId(), "Sub folder", "Sub Folder", uniqueId, "objective");

		final List<Reference> childrenNodes =
				this.manager.getChildrenOfFolderByStudyType(mainFolder.getProjectId(), this.commonTestProject.getUniqueID(), null);
		Assert.assertNotNull(childrenNodes);
		Assert.assertEquals("The size should be one.", 1, childrenNodes.size());
		Assert.assertEquals("The id of the subFolder should be " + subFolderID, subFolderID, (int) childrenNodes.get(0).getId());
	}

	@Test
	public void testGetStudyReference() throws Exception {
		final Integer studyId = this.studyReference.getId();
		final StudyReference studyFromDB = this.manager.getStudyReference(studyId);
		Assert.assertEquals(this.studyReference.getId(), studyFromDB.getId());
		Assert.assertEquals(this.studyReference.getName(), studyFromDB.getName());
		Assert.assertEquals(this.studyReference.getDescription(), studyFromDB.getDescription());
		Assert.assertEquals(this.studyReference.getProgramUUID(), studyFromDB.getProgramUUID());
		Assert.assertEquals(this.studyReference.getStudyType(), studyFromDB.getStudyType());
		Assert.assertFalse(studyFromDB.getIsLocked());
		Assert.assertEquals(this.studyReference.getOwnerId(), studyFromDB.getOwnerId());
		final User user = this.userDataManager.getUserById(this.studyReference.getOwnerId());
		final Person person = this.userDataManager.getPersonById(user.getPersonid());
		Assert.assertEquals(person.getFirstName() + " " + person.getLastName(), studyFromDB.getOwnerName());
	}

	@Test
	public void testUpdateStudyLockedStatus() {
		final Integer studyId = this.studyReference.getId();
		Assert.assertFalse(this.manager.getStudyDetails(studyId).getIsLocked());
		this.manager.updateStudyLockedStatus(studyId, true);
		// Flushing to force Hibernate to synchronize with the underlying database
		this.manager.getActiveSession().flush();
		Assert.assertTrue(this.manager.getStudyDetails(studyId).getIsLocked());

		this.manager.updateStudyLockedStatus(studyId, false);
		this.manager.getActiveSession().flush();
		Assert.assertFalse(this.manager.getStudyDetails(studyId).getIsLocked());
	}

	public void testUpdateExperimentValues() {
		final VariableList factors = new VariableList();
		factors.add(DMSVariableTestDataInitializer.createVariable(1001, "999", DataType.NUMERIC_VARIABLE.getId(), VariableType.TRAIT));
		final ExperimentValues values = new ExperimentValues();
		values.setVariableList(factors);
		values.setLocationId(manager.getExperimentModelSaver().createNewGeoLocation().getLocationId());
		//Save the experiment
		this.manager.addExperiment(1, ExperimentType.TRIAL_ENVIRONMENT, values, cropPrefix);
		final ExperimentModel experiment = this.manager.getExperimentDao().getExperimentByProjectIdAndLocation(1, values.getLocationId());
		Phenotype updatedPhenotype = this.manager.getPhenotypeDao().getPhenotypeByExperimentIdAndObservableId(experiment.getNdExperimentId(), 1001);
		Assert.assertEquals("999", updatedPhenotype.getValue());

		//Change the value of the variable
		factors.getVariables().get(0).setValue("900");

		//Add another variable for the save scenario
		values.getVariableList().add(
				DMSVariableTestDataInitializer.createVariable(1002, "1000", DataType.NUMERIC_VARIABLE.getId(), VariableType.TRAIT));

		this.manager.updateExperimentValues(Arrays.asList(values), 1);

		updatedPhenotype = this.manager.getPhenotypeDao().getPhenotypeByExperimentIdAndObservableId(experiment.getNdExperimentId(), 1001);
		final Phenotype savedPhenotype = this.manager.getPhenotypeDao().getPhenotypeByExperimentIdAndObservableId(experiment.getNdExperimentId(), 1002);
		Assert.assertEquals("900", updatedPhenotype.getValue());
		Assert.assertEquals("1000", savedPhenotype.getValue());
	}

}
