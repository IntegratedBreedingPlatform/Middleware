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

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import org.apache.commons.lang.RandomStringUtils;
import org.generationcp.middleware.GermplasmTestDataGenerator;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.api.program.ProgramService;
import org.generationcp.middleware.dao.dms.InstanceMetadata;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.data.initializer.DMSVariableTestDataInitializer;
import org.generationcp.middleware.data.initializer.StudyTestDataInitializer;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.DatasetValues;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.dms.ExperimentValues;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.dms.StudySearchMatchingOption;
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
import org.generationcp.middleware.api.role.RoleService;
import org.generationcp.middleware.manager.ontology.OntologyVariableDataManagerImpl;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.operation.builder.DataSetBuilder;
import org.generationcp.middleware.operation.builder.TrialEnvironmentBuilder;
import org.generationcp.middleware.operation.saver.ExperimentModelSaver;
import org.generationcp.middleware.operation.saver.StandardVariableSaver;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.dms.StudyType;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.user.UserService;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.generationcp.middleware.utils.test.FieldMapDataUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
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
	private RoleService roleService;

	@Autowired
	private GermplasmDataManager germplasmDataDM;

	@Autowired
	private LocationDataManager locationManager;

	@Autowired
	private UserService userService;

	@Autowired
	private ProgramService programService;

	@Autowired
	private WorkbenchTestDataUtil workbenchTestDataUtil;

	@Autowired
	private DataSetBuilder datasetBuilder;

	@Autowired
	private TrialEnvironmentBuilder trialEnvironmentBuilder;

	private OntologyVariableDataManager variableDataManager;

	private CVTermDao cvTermDao;

	private Project commonTestProject;

	private DaoFactory daoFactory;

	private ExperimentModelSaver experimentModelSaver;

	private static CrossExpansionProperties crossExpansionProperties;
	private StudyReference studyReference;
	private StudyTestDataInitializer studyTDI;
	private GermplasmTestDataGenerator germplasmTestDataGenerator;
	private CropType crop;

	private StandardVariableSaver standardVariableSaver;

	@Before
	public void setUp() throws Exception {
		this.manager = new StudyDataManagerImpl(this.sessionProvder);

		this.daoFactory = new DaoFactory(this.sessionProvder);

		this.manager.setUserService(this.userService);

		this.cvTermDao = new CVTermDao();
		this.cvTermDao.setSession(this.sessionProvder.getSessionFactory().getCurrentSession());

		this.variableDataManager = Mockito.mock(OntologyVariableDataManagerImpl.class);
		final Optional<DataType> dataTypeOptional = Optional.of(DataType.CATEGORICAL_VARIABLE);
		Mockito.when(this.variableDataManager.getDataType(Matchers.anyInt())).thenReturn(dataTypeOptional);

		this.standardVariableSaver = new StandardVariableSaver(this.sessionProvder);

		this.workbenchTestDataUtil.setUpWorkbench();

		if (this.commonTestProject == null) {
			this.commonTestProject = this.workbenchTestDataUtil.getCommonTestProject();
			this.crop = this.programService.getProjectByUuid(this.commonTestProject.getUniqueID()).getCropType();
		}

		if (this.experimentModelSaver == null) {
			this.experimentModelSaver = new ExperimentModelSaver(this.sessionProvder);
		}

		if (this.germplasmTestDataGenerator == null) {
			this.germplasmTestDataGenerator = new GermplasmTestDataGenerator(this.daoFactory);
		}
		final Properties mockProperties = Mockito.mock(Properties.class);
		Mockito.when(mockProperties.getProperty("wheat.generation.level")).thenReturn("0");
		StudyDataManagerImplTest.crossExpansionProperties = new CrossExpansionProperties(mockProperties);
		StudyDataManagerImplTest.crossExpansionProperties.setDefaultLevel(1);
		this.studyTDI = new StudyTestDataInitializer(this.manager, this.ontologyManager, this.commonTestProject,
			this.locationManager, this.sessionProvder);

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
		final Germplasm parentGermplasm = this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames();
		final Integer[] gids = this.germplasmTestDataGenerator
			.createChildrenGermplasm(1, "PREF-ABC", parentGermplasm);
		this.studyTDI.addStudyGermplasm(this.studyReference.getId(), 1, Arrays.asList(gids));

		// Flushing to force Hibernate to synchronize with the underlying database before the search
		// Without this the inserted experiment is not retrieved properly
		this.manager.getActiveSession().flush();

		final GidStudyQueryFilter filter = new GidStudyQueryFilter(gids[0]);
		final List<StudyReference> studyReferences = this.manager.searchStudies(filter);
		// We are sure that the result set will contain the test study we added in the set up
		Assert.assertTrue("The size should be greater than 0", studyReferences.size() > 0);
		for (final StudyReference study : studyReferences) {
			Assert.assertNotNull(study.getOwnerId());
			final WorkbenchUser workbenchUser = this.userService.getUserById(study.getOwnerId());
			Assert.assertEquals(
				workbenchUser.getPerson().getFirstName() + " " + workbenchUser.getPerson().getLastName(),
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
				Assert.assertEquals(
					workbenchUser.getPerson().getFirstName() + " " + workbenchUser.getPerson().getLastName(),
					study.getOwnerName());
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
		this.studyTDI.addTestStudy(StudyTypeDto.getNurseryDto(), "NEW NURSERY");
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

			Assert.assertNotNull(
				"Expected maximum number of rows " + trialInstance.getRowsInBlock() + " instead.",
				trialInstance.getRowsInBlock());
			Assert.assertNotNull(
				"Expected maximum number of range " + trialInstance.getRangesInBlock() + " instead.",
				trialInstance.getRangesInBlock());
			Assert.assertEquals("Expected with default value of 1 ", 1, (int) trialInstance.getRowsPerPlot());
			Assert.assertEquals("Expected with default value of 1", 1, (int) trialInstance.getPlantingOrder());
			Assert.assertEquals("Expected with default value of 1", 1, (int) trialInstance.getMachineRowCapacity());

		} catch (final MiddlewareQueryException e) {
			Assert.fail("Expected mocked value to be returned but used the original call for getBlockInformation instead.");
		}
	}

	@Test
	public void testGetStudyDetailsByStudyType() throws Exception {
		final List<StudyDetails> trialStudyDetails =
			this.manager.getStudyDetails(StudyTypeDto.getTrialDto(), this.commonTestProject.getUniqueID(), 0, 50);
		final int sizeBeforeAddingNewTrial = trialStudyDetails.size();
		final StudyReference newStudy = this.studyTDI.addTestStudy(StudyTypeDto.getTrialDto(), "NEW STUDY");
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
		final StudyReference trial = this.studyTDI.addTestStudy(StudyTypeDto.getTrialDto(), "NEW TRIAL");
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
	public void testGetProjectStartDateByProjectId() {
		// Create project record
		final DmsProject project = new DmsProject();
		final String programUUID = "74364-9075-asdhaskj-74825";
		project.setName("Project Name");
		project.setDescription("Project Description");
		project.setProgramUUID(programUUID);
		project.setDeleted(false);
		project.setStartDate("20180403");
		this.daoFactory.getDmsProjectDAO().save(project);

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
		Assert.assertEquals(
			workbenchUser.getPerson().getFirstName() + " " + workbenchUser.getPerson().getLastName(),
			studyFromDB.getOwnerName());
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
		values.setLocationId(this.manager.getExperimentModelSaver().createNewGeoLocation().getLocationId());
		//Save the experiment
		this.experimentModelSaver.addExperiment(this.crop, 1, ExperimentType.TRIAL_ENVIRONMENT, values);

		final ExperimentModel experiment =
			this.daoFactory.getExperimentDao().getExperimentByProjectIdAndLocation(1, values.getLocationId());
		Phenotype updatedPhenotype =
			this.daoFactory.getPhenotypeDAO().getPhenotypeByExperimentIdAndObservableId(experiment.getNdExperimentId(), 1001);
		Assert.assertEquals("999", updatedPhenotype.getValue());

		//Change the value of the variable
		factors.getVariables().get(0).setValue("900");

		//Add another variable for the save scenario
		values.getVariableList().add(
			DMSVariableTestDataInitializer.createVariable(1002, "1000", DataType.NUMERIC_VARIABLE.getId(), VariableType.TRAIT));

		this.manager.updateExperimentValues(Arrays.asList(values), 1);

		updatedPhenotype =
			this.daoFactory.getPhenotypeDAO().getPhenotypeByExperimentIdAndObservableId(experiment.getNdExperimentId(), 1001);
		final Phenotype savedPhenotype =
			this.daoFactory.getPhenotypeDAO().getPhenotypeByExperimentIdAndObservableId(experiment.getNdExperimentId(), 1002);
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
		project = this.daoFactory.getDmsProjectDAO().save(project);

		final DatasetReference plotdata =
			this.addTestDataset(project.getProjectId(), project.getName() + PLOTDATA, DatasetTypeEnum.PLOT_DATA.getId());

		final DatasetReference environment =
			this.addTestDataset(project.getProjectId(), project.getName() + ENVIRONMENT, DatasetTypeEnum.SUMMARY_DATA.getId());

		final String newStudyName = "newStudyName";
		this.manager.renameStudy(newStudyName, project.getProjectId(), programUUID);

		final DmsProject plotDataset = this.daoFactory.getDmsProjectDAO().getById(plotdata.getId());
		final DmsProject environmentDataset = this.daoFactory.getDmsProjectDAO().getById(environment.getId());

		Assert.assertEquals(newStudyName + PLOTDATA, plotDataset.getName());
		Assert.assertEquals(newStudyName + ENVIRONMENT, environmentDataset.getName());
		Assert.assertEquals(newStudyName, project.getName());

	}

	@Test
	public void testIsStudy() {
		Assert.assertTrue(this.manager.isStudy(this.studyReference.getId()));
		final String uniqueId = this.commonTestProject.getUniqueID();
		final DmsProject mainFolder = this.studyTDI.createFolderTestData(uniqueId);
		Assert.assertFalse(this.manager.isStudy(mainFolder.getProjectId()));
	}

}
