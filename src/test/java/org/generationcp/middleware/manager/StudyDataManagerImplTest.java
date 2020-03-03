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

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.dao.dms.InstanceMetadata;
import org.generationcp.middleware.data.initializer.DMSVariableTestDataInitializer;
import org.generationcp.middleware.data.initializer.StudyTestDataInitializer;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.DatasetValues;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.dms.ExperimentValues;
import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.dms.StudySearchMatchingOption;
import org.generationcp.middleware.domain.dms.StudySummary;
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
import org.generationcp.middleware.domain.search.filter.BrowseStudyQueryFilter;
import org.generationcp.middleware.domain.search.filter.GidStudyQueryFilter;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.operation.builder.DataSetBuilder;
import org.generationcp.middleware.operation.builder.TrialEnvironmentBuilder;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.dms.StudyType;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.study.StudyFilters;
import org.generationcp.middleware.service.api.user.UserService;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.generationcp.middleware.utils.test.FieldMapDataUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import static org.generationcp.middleware.operation.saver.WorkbookSaver.ENVIRONMENT;
import static org.generationcp.middleware.operation.saver.WorkbookSaver.PLOTDATA;

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
	private UserService userService;

	@Autowired
	private WorkbenchTestDataUtil workbenchTestDataUtil;

	@Autowired
	private DataSetBuilder datasetBuilder;

	@Autowired
	private TrialEnvironmentBuilder trialEnvironmentBuilder;

	private Project commonTestProject;

	private static CrossExpansionProperties crossExpansionProperties;
	private StudyReference studyReference;
	private StudyTestDataInitializer studyTDI;
	private CropType crop;

	@Before
	public void setUp() throws Exception {
		this.manager = new StudyDataManagerImpl(this.sessionProvder);
		this.manager.setUserService(this.userService);

		this.workbenchTestDataUtil.setUpWorkbench();

		if (this.commonTestProject == null) {
			this.commonTestProject = this.workbenchTestDataUtil.getCommonTestProject();
			this.crop = this.workbenchDataManager.getProjectByUuid(this.commonTestProject.getUniqueID()).getCropType();
		}
		final Properties mockProperties = Mockito.mock(Properties.class);
		Mockito.when(mockProperties.getProperty("wheat.generation.level")).thenReturn("0");
		StudyDataManagerImplTest.crossExpansionProperties = new CrossExpansionProperties(mockProperties);
		StudyDataManagerImplTest.crossExpansionProperties.setDefaultLevel(1);
		this.studyTDI = new StudyTestDataInitializer(this.manager, this.ontologyManager, this.commonTestProject, this.germplasmDataDM,
			this.locationManager);

		this.studyReference = this.studyTDI.addTestStudy();

		final StudyType studyType = new StudyType();
		studyType.setStudyTypeId(6);
		studyType.setLabel(StudyTypeDto.TRIAL_LABEL);
		studyType.setName(StudyTypeDto.TRIAL_NAME);
		studyType.setCvTermId(10010);
		studyType.setVisible(true);

		this.manager.setDataSetBuilder(this.datasetBuilder);
		this.manager.setTrialEnvironmentBuilder(this.trialEnvironmentBuilder);
	}

	@Test
	public void testGetStudy() {
		final Study study = this.manager.getStudy(this.studyReference.getId());
		Assert.assertEquals("The study name should be " + StudyTestDataInitializer.STUDY_NAME, StudyTestDataInitializer.STUDY_NAME,
			study.getName());
		Assert.assertEquals("The study description should be " + StudyTestDataInitializer.STUDY_DESCRIPTION,
			StudyTestDataInitializer.STUDY_DESCRIPTION, study.getDescription());
	}

	@Test
	public void testGetStudyConditions() {
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
	public void testSearchStudiesForName() throws Exception {
		// Study search query expect datasets for studies to be returned
		this.studyTDI.addTestDataset(this.studyReference.getId());

		final BrowseStudyQueryFilter filter = new BrowseStudyQueryFilter();
		filter.setStudySearchMatchingOption(StudySearchMatchingOption.EXACT_MATCHES);
		filter.setProgramUUID(this.studyReference.getProgramUUID());

		filter.setName(RandomStringUtils.randomAlphanumeric(100));
		List<StudyReference> studyReferences = this.manager.searchStudies(filter);
		Assert.assertEquals("The size should be zero since the study is not existing", 0, studyReferences.size());

		filter.setName(this.studyReference.getName());
		studyReferences = this.manager.searchStudies(filter);

		Assert.assertTrue("The study search by name results should contain test study", studyReferences.size() > 0);
		// The owner ID is not retrieved in query hence, owner name is null too
		for (final StudyReference study : studyReferences) {
			Assert.assertNull(study.getOwnerId());
			Assert.assertNull(study.getOwnerName());
		}
	}

	@Test
	public void testSearchStudiesForStartDate() throws Exception {
		// Study search query expect datasets for studies to be returned
		this.studyTDI.addTestDataset(this.studyReference.getId());

		final BrowseStudyQueryFilter filter = new BrowseStudyQueryFilter();
		filter.setStartDate(new Integer(StudyTestDataInitializer.START_DATE));
		filter.setProgramUUID(this.studyReference.getProgramUUID());

		final List<StudyReference> studyReferences = this.manager.searchStudies(filter);
		Assert.assertTrue("The study search by start date results should contain test study", studyReferences.size() > 0);
		// The owner ID is not retrieved in query hence, owner name is null too
		for (final StudyReference study : studyReferences) {
			Assert.assertNull(study.getOwnerId());
			Assert.assertNull(study.getOwnerName());
		}
	}

	@Test
	public void testSearchStudiesByGid() throws Exception {
		final Integer gid = this.studyTDI.getGid();
		final Integer geolocationId = this.studyTDI.getGeolocationId();
		final Integer stockId = this.studyTDI.getStockId();

		// Create test dataset
		final DatasetReference dataset = this.studyTDI.addTestDataset(this.studyReference.getId());
		final ExperimentValues values = new ExperimentValues();
		values.setLocationId(geolocationId);
		values.setGermplasmId(stockId);
		this.manager.addExperiment(this.crop, dataset.getId(), ExperimentType.PLOT, values);

		// Flushing to force Hibernate to synchronize with the underlying database before the search
		// Without this the inserted experiment is not retrieved properly
		this.manager.getActiveSession().flush();

		final GidStudyQueryFilter filter = new GidStudyQueryFilter(gid);
		final List<StudyReference> studyReferences = this.manager.searchStudies(filter);
		// We are sure that the result set will contain the test study we added in the set up
		Assert.assertTrue("The size should be greater than 0", studyReferences.size() > 0);
		for (final StudyReference study : studyReferences) {
			Assert.assertNotNull(study.getOwnerId());
			final WorkbenchUser workbenchUser = this.userService.getUserById(study.getOwnerId());
			Assert.assertEquals(workbenchUser.getPerson().getFirstName() + " " + workbenchUser.getPerson().getLastName(),
				study.getOwnerName());

		}
	}

	@Test
	public void testGetRootFolders() {
		final List<Reference> rootFolders = this.manager.getRootFolders(this.commonTestProject.getUniqueID());
		Assert.assertNotNull(rootFolders);
		Assert.assertFalse("Root folders should not be empty because it contains the templates for Studies.", rootFolders.isEmpty());
	}

	@Test
	public void testMoveDmsProject() {
		final String uniqueId = this.commonTestProject.getUniqueID();
		final DmsProject mainFolder = this.studyTDI.createFolderTestData(uniqueId);
		this.manager.moveDmsProject(this.studyReference.getId(), mainFolder.getProjectId());
		this.sessionProvder.getSession().flush();

		final List<Reference> childrenNodes = this.manager.getChildrenOfFolder(mainFolder.getProjectId(), uniqueId);
		Assert.assertNotNull(childrenNodes);
		Assert.assertEquals("Study should have been moved to new folder.", 1, childrenNodes.size());
	}

	@Test
	public void testGetChildrenOfFolder() {

		final String uniqueId = this.commonTestProject.getUniqueID();
		final DmsProject mainFolder = this.studyTDI.createFolderTestData(uniqueId);
		final String subFolderName = "Sub folder Name";
		final String subFolderDescription = "Sub Folder Description";
		final int subFolderID =
			this.manager.addSubFolder(mainFolder.getProjectId(), subFolderName, subFolderDescription, uniqueId, "objective");
		this.manager.moveDmsProject(this.studyReference.getId(), mainFolder.getProjectId());

		this.sessionProvder.getSession().flush();
		final List<Reference> childrenNodes = this.manager.getChildrenOfFolder(mainFolder.getProjectId(), uniqueId);
		Assert.assertNotNull(childrenNodes);
		Assert.assertEquals(2, childrenNodes.size());
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
				final WorkbenchUser workbenchUser = this.userService.getUserById(this.studyReference.getOwnerId());
				Assert.assertEquals(workbenchUser.getPerson().getFirstName() + " " + workbenchUser.getPerson().getLastName(),
					study.getOwnerName());
			}
		}
	}

	@Test
	public void testGetAllFolders() {
		final int originalSize = this.manager.getAllFolders().size();
		final String uniqueID = this.commonTestProject.getUniqueID();
		final DmsProject folder1 = this.studyTDI.createFolderTestData(uniqueID);
		final DmsProject folder2 = this.studyTDI.createFolderTestData(null);
		final DmsProject folder3 = this.studyTDI.createFolderTestData(uniqueID, folder1.getProjectId());
		this.sessionProvder.getSession().flush();

		final List<FolderReference> allFolders = this.manager.getAllFolders();
		final int newSize = allFolders.size();
		final List<Integer> idList = Lists.transform(allFolders, new Function<FolderReference, Integer>() {

			@Override
			public Integer apply(final FolderReference dataset) {
				return dataset.getId();
			}
		});
		Assert.assertEquals("The new size should be equal to the original size + 3", originalSize + 3, newSize);
		Assert.assertTrue(idList.contains(folder1.getProjectId()));
		Assert.assertTrue(idList.contains(folder2.getProjectId()));
		Assert.assertTrue(idList.contains(folder3.getProjectId()));
		for (final FolderReference folder : allFolders) {
			final Integer id = folder.getId();
			if (id.equals(folder1.getProjectId())) {
				Assert.assertEquals(folder1.getProjectId(), id);
				Assert.assertEquals(folder1.getName(), folder.getName());
				Assert.assertEquals(folder1.getDescription(), folder.getDescription());
				Assert.assertEquals(DmsProject.SYSTEM_FOLDER_ID, folder.getParentFolderId());
			} else if (id.equals(folder2.getProjectId())) {
				Assert.assertEquals(folder2.getProjectId(), id);
				Assert.assertEquals(folder2.getName(), folder.getName());
				Assert.assertEquals(folder2.getDescription(), folder.getDescription());
				Assert.assertEquals(DmsProject.SYSTEM_FOLDER_ID, folder.getParentFolderId());
			} else if (id.equals(folder3.getProjectId())) {
				Assert.assertEquals(folder3.getProjectId(), id);
				Assert.assertEquals(folder3.getName(), folder.getName());
				Assert.assertEquals(folder3.getDescription(), folder.getDescription());
				Assert.assertEquals(folder1.getProjectId(), folder.getParentFolderId());
			}
		}
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
		Assert.assertEquals(DatasetTypeEnum.MEANS_DATA.getId(), dataset.getDatasetType().getDatasetTypeId().intValue());
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
		final List<DataSet> datasets = this.manager.getDataSetsByType(this.studyReference.getId(), DatasetTypeEnum.MEANS_DATA.getId());
		Assert.assertTrue("Datasets' size should be greter than 0", datasets.size() > 0);
		Assert.assertTrue(
			"The size should be greater than 0 since we are sure that it will return at least one data set",
			datasets.size() > 0);
	}

	@Test
	public void testFindOneDataSetByType() throws Exception {
		this.studyTDI.addTestDataset(this.studyReference.getId());
		final DataSet dataset = this.manager.findOneDataSetByType(this.studyReference.getId(), DatasetTypeEnum.MEANS_DATA.getId());
		Assert.assertEquals("Dataset's name should be " + StudyTestDataInitializer.DATASET_NAME, StudyTestDataInitializer.DATASET_NAME,
			dataset.getName());
	}

	@Test
	public void testGetAllStudyDetails() throws Exception {
		final List<StudyDetails> nurseryStudyDetails =
			this.manager.getAllStudyDetails(StudyTypeDto.getNurseryDto(), this.commonTestProject.getUniqueID());
		final int sizeBeforeAddingNewNursery = nurseryStudyDetails.size();

		final StudyReference study = this.studyTDI.addTestStudy(StudyTypeDto.getNurseryDto(), "NEW NURSERY");
		final DatasetReference environment =
			this.addTestDataset(study.getId(), study.getName() + ENVIRONMENT, DatasetTypeEnum.SUMMARY_DATA.getId());

		final List<StudyDetails> updatedNurseryStudyDetails =
			this.manager.getAllStudyDetails(StudyTypeDto.getNurseryDto(), this.commonTestProject.getUniqueID());
		final int sizeAfterAddingNewNursery = updatedNurseryStudyDetails.size();
		Assert.assertEquals("The size after adding new nursery should be equal to the size before adding a new nursery + 1",
			sizeAfterAddingNewNursery, sizeBeforeAddingNewNursery + 1);
	}

	@Test
	public void testCheckIfProjectNameIsExisting() {
		final DmsProject project = this.studyTDI.createFolderTestData(this.commonTestProject.getUniqueID());
		boolean isExisting = this.manager.checkIfProjectNameIsExistingInProgram(project.getName(), this.commonTestProject.getUniqueID());
		Assert.assertTrue(isExisting);

		final String name = "SHOULDNOTEXISTSTUDY";
		isExisting = this.manager.checkIfProjectNameIsExistingInProgram(name, this.commonTestProject.getUniqueID());
		Assert.assertFalse(isExisting);
	}

	@Test
	public void testGetFieldMapInfoOfStudy() {
		final List<Integer> trialIdList = new ArrayList<>();
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
			Assert.assertNull(
				"Expected null but got " + trialInstance.getMachineRowCapacity() + " instead.",
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
			Assert.assertNull(
				"Expected null return value but was non null.",
				this.manager.getStudyType(PRESUMABLY_NON_EXISTENT_STUDY_TYPE_ID));
		} catch (final MiddlewareQueryException e) {
			Assert.fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test
	public void testDeleteProgramStudies() throws Exception {
		final String uniqueId = "100001001001";
		this.studyTDI.createFolderTestData(uniqueId);
		this.studyTDI.addTestStudy(uniqueId);

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

		final StudyReference newStudy = this.studyTDI.addTestStudy(StudyTypeDto.getTrialDto(), "NEW STUDY");
		final DatasetReference environment =
			this.addTestDataset(newStudy.getId(), newStudy.getName() + ENVIRONMENT, DatasetTypeEnum.SUMMARY_DATA.getId());

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
		final StudyReference nursery = this.studyTDI.addTestStudy(StudyTypeDto.getNurseryDto(), "NEW NURSERY");
		this.addTestDataset(nursery.getId(), nursery.getName() + ENVIRONMENT, DatasetTypeEnum.SUMMARY_DATA.getId());

		final StudyReference trial = this.studyTDI.addTestStudy(StudyTypeDto.getTrialDto(), "NEW TRIAL");
		this.addTestDataset(trial.getId(), trial.getName() + ENVIRONMENT, DatasetTypeEnum.SUMMARY_DATA.getId());

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
	public void testGetStudyDetails() throws Exception {

		final DatasetReference environment =
			this.addTestDataset(this.studyReference.getId(), this.studyReference.getName() + ENVIRONMENT,
				DatasetTypeEnum.SUMMARY_DATA.getId());

		final StudyDetails studyDetails = this.manager.getStudyDetails(this.studyReference.getId());
		Assert.assertNotNull("Study should not be null", studyDetails);
		Assert.assertEquals(this.studyReference.getId(), studyDetails.getId());
		Assert.assertEquals(this.studyReference.getName(), studyDetails.getStudyName());
		Assert.assertEquals(this.studyReference.getStudyType(), studyDetails.getStudyType());
		this.verifyCommonStudyDetails(studyDetails);
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
		//		final Geolocation geolocation = this.manager.getGeolocationSaver().saveGeolocation(variableList, null, false);

		// Create experiment record
		final ExperimentModel experimentModel = new ExperimentModel();
		experimentModel.setTypeId(TermId.TRIAL_ENVIRONMENT_EXPERIMENT.getId());
		experimentModel.setProject(project);
		this.manager.getExperimentDao().save(experimentModel);

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
		//		final Geolocation geolocation = this.manager.getGeolocationSaver().saveGeolocation(variableList, null, false);

		// Create experiment record
		final ExperimentModel experimentModel = new ExperimentModel();
		experimentModel.setTypeId(TermId.TRIAL_ENVIRONMENT_EXPERIMENT.getId());
		experimentModel.setProject(project);
		this.manager.getExperimentDao().save(experimentModel);

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
		this.studyTDI.addTestDataset(this.studyReference.getId(), DatasetTypeEnum.SUMMARY_DATA.getId());

		Assert.assertTrue(this.manager.isLocationIdVariable(this.studyReference.getId(), "LOCATION_NAME"));
		Assert.assertFalse(this.manager.isLocationIdVariable(this.studyReference.getId(), "EXPERIMENT_DESIGN_FACTOR"));
		Assert.assertFalse(this.manager.isLocationIdVariable(this.studyReference.getId(), "DUMMY NAME"));

	}

	@Test
	public void testCreateInstanceLocationIdToNameMapFromStudy() throws Exception {
		final String afghanistanLocationId = "1";
		final String albaniaLocationId = "2";
		final String algeriaLocationId = "3";

		this.studyTDI.addTestDataset(this.studyReference.getId(), DatasetTypeEnum.SUMMARY_DATA.getId());

		this.studyTDI.createEnvironmentDataset(this.crop, this.studyReference.getId(), afghanistanLocationId, "1");
		this.studyTDI.createEnvironmentDataset(this.crop, this.studyReference.getId(), albaniaLocationId, "1");
		this.studyTDI.createEnvironmentDataset(this.crop, this.studyReference.getId(), algeriaLocationId, "1");

		this.sessionProvder.getSession().flush();
		final Map<String, String> result = this.manager.createInstanceLocationIdToNameMapFromStudy(this.studyReference.getId());

		Assert.assertEquals(3, result.size());
		Assert.assertEquals("Afghanistan", result.get(afghanistanLocationId));
		Assert.assertEquals("Albania", result.get(albaniaLocationId));
		Assert.assertEquals("Algeria", result.get(algeriaLocationId));

	}

	@Test
	public void testGetRootFoldersByStudyType() {
		final List<Reference> rootFolders = this.manager.getRootFoldersByStudyType(this.commonTestProject.getUniqueID(), null);
		Assert.assertNotNull(rootFolders);
		Assert.assertFalse("Root folders should not be empty because it contains the templates for Studies.", rootFolders.isEmpty());
	}

	@Test
	public void testGetChildrenOfFolderByStudyType() {
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
	public void testGetStudyReference() {
		final Integer studyId = this.studyReference.getId();
		final StudyReference studyFromDB = this.manager.getStudyReference(studyId);
		Assert.assertEquals(this.studyReference.getId(), studyFromDB.getId());
		Assert.assertEquals(this.studyReference.getName(), studyFromDB.getName());
		Assert.assertEquals(this.studyReference.getDescription(), studyFromDB.getDescription());
		Assert.assertEquals(this.studyReference.getProgramUUID(), studyFromDB.getProgramUUID());
		Assert.assertEquals(this.studyReference.getStudyType(), studyFromDB.getStudyType());
		Assert.assertFalse(studyFromDB.getIsLocked());
		Assert.assertEquals(this.studyReference.getOwnerId(), studyFromDB.getOwnerId());
		final WorkbenchUser workbenchUser = this.userService.getUserById(this.studyReference.getOwnerId());
		Assert.assertEquals(workbenchUser.getPerson().getFirstName() + " " + workbenchUser.getPerson().getLastName(),
			studyFromDB.getOwnerName());
	}

	@Test
	public void testUpdateStudyLockedStatus() throws Exception {
		final Integer studyId = this.studyReference.getId();
		this.addTestDataset(this.studyReference.getId(), this.studyReference.getName() + ENVIRONMENT, DatasetTypeEnum.SUMMARY_DATA.getId());

		Assert.assertFalse(this.manager.getStudyDetails(studyId).getIsLocked());
		this.manager.updateStudyLockedStatus(studyId, true);
		// Flushing to force Hibernate to synchronize with the underlying database
		this.manager.getActiveSession().flush();
		Assert.assertTrue(this.manager.getStudyDetails(studyId).getIsLocked());

		this.manager.updateStudyLockedStatus(studyId, false);
		this.manager.getActiveSession().flush();
		Assert.assertFalse(this.manager.getStudyDetails(studyId).getIsLocked());
	}

	@Test
	public void testAreAllInstancesExistInDataset() throws Exception {

		final Random random = new Random();
		final Integer studyId = this.studyReference.getId();
		this.studyTDI.addTestDataset(studyId, DatasetTypeEnum.PLOT_DATA.getId());
		final Integer datasetId = this.studyTDI.createEnvironmentDataset(this.crop, studyId, String.valueOf(random.nextInt()), "1");

		// Flushing to force Hibernate to synchronize with the underlying database
		this.manager.getActiveSession().flush();

		final List<InstanceMetadata> instanceMetadataList = this.manager.getInstanceMetadata(studyId);
		final Integer instanceId = instanceMetadataList.get(0).getInstanceDbId();

		Assert.assertTrue(this.manager.areAllInstancesExistInDataset(datasetId, new HashSet<>(Arrays.asList(instanceId))));

	}

	@Test
	public void testAreAllInstancesExistInDatasetOnlyOneInstanceIdExists() throws Exception {

		final Random random = new Random();
		final Integer studyId = this.studyReference.getId();
		this.studyTDI.addTestDataset(studyId, DatasetTypeEnum.PLOT_DATA.getId());
		final Integer datasetId = this.studyTDI.createEnvironmentDataset(this.crop, studyId, String.valueOf(random.nextInt()), "1");

		// Flushing to force Hibernate to synchronize with the underlying database
		this.manager.getActiveSession().flush();

		final List<InstanceMetadata> instanceMetadataList = this.manager.getInstanceMetadata(studyId);
		final Integer instanceId = instanceMetadataList.get(0).getInstanceDbId();

		Assert.assertFalse(this.manager.areAllInstancesExistInDataset(datasetId, Sets.newHashSet(instanceId, 999)));

	}

	@Test
	public void testAreAllInstancesExistInDatasetInstanceInstancesDoNotExist() throws Exception {

		final Random random = new Random();
		final Integer studyId = this.studyReference.getId();
		this.studyTDI.addTestDataset(studyId, DatasetTypeEnum.PLOT_DATA.getId());
		final Integer datasetId = this.studyTDI.createEnvironmentDataset(this.crop, studyId, String.valueOf(random.nextInt()), "1");

		Assert.assertFalse(this.manager.areAllInstancesExistInDataset(datasetId, Sets.newHashSet(999)));

	}

	public void testUpdateExperimentValues() {
		final VariableList factors = new VariableList();
		factors.add(DMSVariableTestDataInitializer.createVariable(1001, "999", DataType.NUMERIC_VARIABLE.getId(), VariableType.TRAIT));
		final ExperimentValues values = new ExperimentValues();
		values.setVariableList(factors);
		//Save the experiment
		this.manager.addExperiment(this.crop, 1, ExperimentType.TRIAL_ENVIRONMENT, values);
		final ExperimentModel experiment = this.manager.getExperimentDao().getById(values.getLocationId());
		Phenotype updatedPhenotype =
			this.manager.getPhenotypeDao().getPhenotypeByExperimentIdAndObservableId(experiment.getNdExperimentId(), 1001);
		Assert.assertEquals("999", updatedPhenotype.getValue());

		//Change the value of the variable
		factors.getVariables().get(0).setValue("900");

		//Add another variable for the save scenario
		values.getVariableList().add(
			DMSVariableTestDataInitializer.createVariable(1002, "1000", DataType.NUMERIC_VARIABLE.getId(), VariableType.TRAIT));

		this.manager.updateExperimentValues(Arrays.asList(values));

		updatedPhenotype = this.manager.getPhenotypeDao().getPhenotypeByExperimentIdAndObservableId(experiment.getNdExperimentId(), 1001);
		final Phenotype savedPhenotype =
			this.manager.getPhenotypeDao().getPhenotypeByExperimentIdAndObservableId(experiment.getNdExperimentId(), 1002);
		Assert.assertEquals("900", updatedPhenotype.getValue());
		Assert.assertEquals("1000", savedPhenotype.getValue());
	}

	private DMSVariableType createVariableType(final int termId, final String name, final String description, final int rank) {
		final StandardVariable stdVar = this.ontologyManager.getStandardVariable(termId, this.commonTestProject.getUniqueID());
		final DMSVariableType vtype = new DMSVariableType();
		vtype.setLocalName(name);
		vtype.setLocalDescription(description);
		vtype.setRank(rank);
		vtype.setStandardVariable(stdVar);
		vtype.setRole(PhenotypicType.TRIAL_ENVIRONMENT);

		return vtype;
	}

	private DatasetReference addTestDataset(final int studyId, final String name, final int datasetTypeId) throws Exception {
		final VariableTypeList typeList = new VariableTypeList();

		final DatasetValues datasetValues = new DatasetValues();
		datasetValues.setName(name);
		datasetValues.setDescription("My Dataset Description");

		DMSVariableType variableType =
			this.createVariableType(51570, "GY_Adj_kgha", "Grain yield BY Adjusted GY - Computation IN Kg/ha", 4);
		variableType.setLocalName("GY_Adj_kgha");
		typeList.add(variableType);

		variableType =
			this.createVariableType(20444, "SCMVInc_Cmp_pct", "Sugarcane mosaic virus incidence BY SCMVInc - Computation IN %", 5);
		variableType.setLocalName("Aphid damage");
		typeList.add(variableType);

		variableType = this.createVariableType(TermId.PLOT_NO.getId(), "Plot No", "Plot No", 6);
		variableType.setLocalName("Plot No");
		typeList.add(variableType);

		return this.manager.addDataSet(studyId, typeList, datasetValues, null, datasetTypeId);
	}

	@Test
	public void testRenameStudy() throws Exception {
		// Create project record
		DmsProject project = new DmsProject();
		final String programUUID = "74364-9075-asdhaskj-74825";
		project.setProjectId(1);
		project.setName("projectName");
		project.setDescription("ProjectDescription");
		project.setProgramUUID(programUUID);
		project = this.manager.getDmsProjectDao().save(project);

		final DatasetReference plotdata =
			this.addTestDataset(project.getProjectId(), project.getName() + PLOTDATA, DatasetTypeEnum.PLOT_DATA.getId());

		final DatasetReference environment =
			this.addTestDataset(project.getProjectId(), project.getName() + ENVIRONMENT, DatasetTypeEnum.SUMMARY_DATA.getId());

		final String newStudyName = "newStudyName";
		this.manager.renameStudy(newStudyName, project.getProjectId(), programUUID);

		final DmsProject plotDataset = this.manager.getDmsProjectDao().getById(plotdata.getId());
		final DmsProject environmentDataset = this.manager.getDmsProjectDao().getById(environment.getId());

		Assert.assertEquals(newStudyName + PLOTDATA, plotDataset.getName());
		Assert.assertEquals(newStudyName + ENVIRONMENT, environmentDataset.getName());
		Assert.assertEquals(newStudyName, project.getName());

	}

	@Test
	public void testCountStudies() throws Exception {
		// Empty filter will retrieve all studies in crop
		final Map<StudyFilters, String> map = new HashMap<>();
		final Long initialCount = this.manager.countAllStudies(map);

		// Add new study with new location ID
		final StudyReference newStudy = this.studyTDI.addTestStudy();
		final Integer studyId = newStudy.getId();
		this.studyTDI.addTestDataset(studyId, DatasetTypeEnum.PLOT_DATA.getId());
		final Random random = new Random();
		final String location1 = String.valueOf(random.nextInt());
		final String season = String.valueOf(random.nextInt());
		this.studyTDI.createEnvironmentDataset(this.crop, studyId, location1, season);

		// Flushing to force Hibernate to synchronize with the underlying database
		this.manager.getActiveSession().flush();

		// New study should be retrieved for empty filter
		Assert.assertEquals(initialCount.intValue() + 1, this.manager.countAllStudies(map).intValue());
		map.put(StudyFilters.PROGRAM_ID, newStudy.getProgramUUID());
		// Expecting only seeded studies for this test class/method to be retrieved when filtered by programUUID
		Assert.assertEquals(2, this.manager.countAllStudies(map).intValue());
		map.put(StudyFilters.LOCATION_ID, location1);
		// Expecting only one to be retrieved when filtered by location
		Assert.assertEquals(1, this.manager.countAllStudies(map).intValue());
	}

	@Test
	public void testFindPagedProjects() throws Exception {
		// Add new study with 2 environments assigned new location IDs
		final StudyReference newStudy = this.studyTDI.addTestStudy();
		final Integer studyId = newStudy.getId();
		this.studyTDI.addTestDataset(studyId, DatasetTypeEnum.PLOT_DATA.getId());
		final Random random = new Random();
		final String location1 = String.valueOf(random.nextInt());
		final String season = String.valueOf(random.nextInt());
		final Integer datasetId = this.studyTDI.createEnvironmentDataset(this.crop, studyId, location1, season);
		final String location2 = String.valueOf(random.nextInt());
		this.studyTDI.addEnvironmentToDataset(this.crop, datasetId, 2, location2, season);

		// Flushing to force Hibernate to synchronize with the underlying database
		this.manager.getActiveSession().flush();

		final Map<StudyFilters, String> map = new HashMap<>();
		map.put(StudyFilters.PROGRAM_ID, newStudy.getProgramUUID());
		// Expecting only seeded studies for this test class/method to be retrieved when filtered by programUUID
		List<StudySummary> studies = this.manager.findPagedProjects(map, 10, 1);
		Assert.assertEquals(2, studies.size());
		StudySummary study1 = studies.get(0);
		Assert.assertEquals(this.studyReference.getId(), study1.getStudyDbid());
		final StudySummary study2 = studies.get(1);
		Assert.assertEquals(newStudy.getId(), study2.getStudyDbid());
		Assert.assertEquals(2, study2.getInstanceMetaData().size());

		map.put(StudyFilters.LOCATION_ID, location1);
		// Expecting only one study to be retrieved when filtered by location
		studies = this.manager.findPagedProjects(map, 10, 1);
		Assert.assertEquals(1, studies.size());
		study1 = studies.get(0);
		Assert.assertEquals(newStudy.getId(), study1.getStudyDbid());
		// Expecting environments of retrieved study to also be filtered by location
		Assert.assertEquals(1, study1.getInstanceMetaData().size());
		Assert.assertEquals(location1, study1.getInstanceMetaData().get(0).getLocationDbId().toString());
	}

	@Test
	public void testIsStudy() {
		Assert.assertTrue(this.manager.isStudy(this.studyReference.getId()));
		final String uniqueId = this.commonTestProject.getUniqueID();
		final DmsProject mainFolder = this.studyTDI.createFolderTestData(uniqueId);
		Assert.assertFalse(this.manager.isStudy(mainFolder.getProjectId()));
	}

}
