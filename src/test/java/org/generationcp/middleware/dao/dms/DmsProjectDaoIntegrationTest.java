package org.generationcp.middleware.dao.dms;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.api.brapi.v2.observationlevel.ObservationLevel;
import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.PersonDAO;
import org.generationcp.middleware.dao.SampleDao;
import org.generationcp.middleware.dao.SampleListDao;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.data.initializer.SampleListTestDataInitializer;
import org.generationcp.middleware.data.initializer.SampleTestDataInitializer;
import org.generationcp.middleware.domain.dms.DatasetDTO;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Sample;
import org.generationcp.middleware.pojos.SampleList;
import org.generationcp.middleware.pojos.dms.DatasetType;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.ExperimentProperty;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.GeolocationProperty;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.dms.StudyType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.study.StudyInstanceDto;
import org.generationcp.middleware.service.api.study.StudyMetadata;
import org.generationcp.middleware.service.api.study.StudySearchFilter;
import org.generationcp.middleware.service.impl.study.StudyInstance;
import org.generationcp.middleware.utils.test.IntegrationTestDataInitializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class DmsProjectDaoIntegrationTest extends IntegrationTestBase {

	private static final int NO_OF_GERMPLASM = 5;
	private static final int STUDY_TYPE_ID = 6;

	private ExperimentPropertyDao experimentPropertyDao;

	private GeolocationDao geolocationDao;

	private GeolocationPropertyDao geolocPropDao;

	private ExperimentDao experimentDao;

	private StockDao stockDao;

	private GermplasmDAO germplasmDao;

	private DmsProjectDao dmsProjectDao;

	private PersonDAO personDao;

	private SampleListDao sampleListDao;

	private SampleDao sampleDao;

	private ProjectPropertyDao projectPropDao;

	private CVTermDao cvTermDao;

	private DmsProject study;

	private DmsProject plot;

	private IntegrationTestDataInitializer testDataInitializer;

	@Before
	public void setUp() {
		this.experimentPropertyDao = new ExperimentPropertyDao(this.sessionProvder.getSession());

		if (this.geolocationDao == null) {
			this.geolocationDao = new GeolocationDao(this.sessionProvder.getSession());
		}

		if (this.geolocPropDao == null) {
			this.geolocPropDao = new GeolocationPropertyDao(this.sessionProvder.getSession());
		}

		if (this.germplasmDao == null) {
			this.germplasmDao = new GermplasmDAO(this.sessionProvder.getSession());
		}

		if (this.experimentDao == null) {
			this.experimentDao = new ExperimentDao(this.sessionProvder.getSession());
		}

		if (this.stockDao == null) {
			this.stockDao = new StockDao(this.sessionProvder.getSession());
		}

		if (this.dmsProjectDao == null) {
			this.dmsProjectDao = new DmsProjectDao(this.sessionProvder.getSession());
		}

		if (this.personDao == null) {
			this.personDao = new PersonDAO(this.sessionProvder.getSession());
		}

		if (this.sampleDao == null) {
			this.sampleDao = new SampleDao(this.sessionProvder.getSession());
		}

		if (this.sampleListDao == null) {
			this.sampleListDao = new SampleListDao(this.sessionProvder.getSession());
		}

		if (this.projectPropDao == null) {
			this.projectPropDao = new ProjectPropertyDao(this.sessionProvder.getSession());
		}

		if (this.cvTermDao == null) {
			this.cvTermDao = new CVTermDao(this.sessionProvder.getSession());
		}

		if (this.testDataInitializer == null) {
			this.testDataInitializer = new IntegrationTestDataInitializer(this.sessionProvder, this.workbenchSessionProvider);
		}

		if (this.study == null) {
			this.study = this.createProject("Study " + UUID.randomUUID().toString(), UUID.randomUUID().toString());
		}

		if (this.plot == null) {
			this.plot = this.testDataInitializer
				.createDmsProject("Plot Dataset", "Plot Dataset-Description", this.study, this.study, DatasetTypeEnum.PLOT_DATA);
		}
	}

	@Test
	public void testGetDatasetInstances() {
		final Integer env1 = this.createEnvironmentData("1", 1, Optional.<String>absent(), Optional.of(1), true);
		final Integer env2 = this.createEnvironmentData("2", 2, Optional.<String>absent(), Optional.of(2), true);
		final String customLocation = RandomStringUtils.randomAlphabetic(10);
		final Integer env3 = this.createEnvironmentData("3", 3, Optional.of(customLocation), Optional.<Integer>absent(), false);
		final List<StudyInstance> instances = this.dmsProjectDao.getDatasetInstances(this.study.getProjectId());
		Assert.assertEquals(3, instances.size());

		final StudyInstance instance1 = instances.get(0);
		Assert.assertEquals(env1.intValue(), instance1.getInstanceId());
		Assert.assertEquals(1, instance1.getInstanceNumber());
		Assert.assertEquals("Afghanistan", instance1.getLocationName());
		Assert.assertEquals("AFG", instance1.getLocationAbbreviation());
		Assert.assertNull(instance1.getCustomLocationAbbreviation());
		Assert.assertTrue(instance1.getHasFieldLayout());

		assertTrue(this.experimentDao.hasFieldLayout(this.study.getProjectId()));

		final StudyInstance instance2 = instances.get(1);
		Assert.assertEquals(env2.intValue(), instance2.getInstanceId());
		Assert.assertEquals(2, instance2.getInstanceNumber());
		Assert.assertEquals("Albania", instance2.getLocationName());
		Assert.assertEquals("ALB", instance2.getLocationAbbreviation());
		Assert.assertNull(instance2.getCustomLocationAbbreviation());
		Assert.assertTrue(instance2.getHasFieldLayout());

		final StudyInstance instance3 = instances.get(2);
		Assert.assertEquals(env3.intValue(), instance3.getInstanceId());
		Assert.assertEquals(3, instance3.getInstanceNumber());
		Assert.assertEquals("Algeria", instance3.getLocationName());
		Assert.assertEquals("DZA", instance3.getLocationAbbreviation());
		Assert.assertEquals(customLocation, instance3.getCustomLocationAbbreviation());
		Assert.assertFalse(instance3.getHasFieldLayout());
	}

	@Test
	public void testGetDatasetsByTypeForStudy() {

		final String studyName = "Study1";
		final String programUUID = UUID.randomUUID().toString();

		final DmsProject study = this.createProject(studyName, programUUID);
		final DmsProject plot =
			this.createDataset(studyName + " - Plot Dataset", programUUID, DatasetTypeEnum.PLOT_DATA.getId(), study, study);

		final List<DmsProject> resultPlot =
			this.dmsProjectDao.getDatasetsByTypeForStudy(study.getProjectId(), DatasetTypeEnum.PLOT_DATA.getId());
		Assert.assertEquals(1, resultPlot.size());
		Assert.assertEquals(plot.getProjectId(), resultPlot.get(0).getProjectId());

		final List<DmsProject> result =
			this.dmsProjectDao.getDatasetsByTypeForStudy(study.getProjectId(), DatasetTypeEnum.PLANT_SUBOBSERVATIONS.getId());
		Assert.assertEquals(0, result.size());

	}

	@Test
	public void testGetProjectIdByStudyDbId() {

		final String studyName = "Study1";
		final String programUUID = UUID.randomUUID().toString();

		final DmsProject study = this.createProject(studyName, programUUID);
		final DmsProject summary =
			this.createDataset(studyName + " - Summary Dataset", programUUID, DatasetTypeEnum.SUMMARY_DATA.getId(), study, study);

		final Geolocation geolocation = new Geolocation();
		geolocation.setDescription("1");
		this.geolocationDao.saveOrUpdate(geolocation);

		final ExperimentModel experimentModel = new ExperimentModel();
		experimentModel.setGeoLocation(geolocation);
		experimentModel.setTypeId(TermId.SUMMARY_EXPERIMENT.getId());
		experimentModel.setProject(summary);
		this.experimentDao.saveOrUpdate(experimentModel);

		// Need to flush session to sync with underlying database before querying
		this.sessionProvder.getSessionFactory().getCurrentSession().flush();

		final Integer result = this.dmsProjectDao.getProjectIdByStudyDbId(geolocation.getLocationId());
		Assert.assertEquals(study.getProjectId(), result);

	}

	@Test
	public void testGetDataset() {
		final String studyName = "Study1";
		final String programUUID = UUID.randomUUID().toString();

		final DmsProject study = this.createProject(studyName, programUUID);
		final DmsProject summary =
			this.createDataset(studyName + " - Summary Dataset", programUUID, DatasetTypeEnum.SUMMARY_DATA.getId(), study, study);

		final DatasetDTO retrievedProject = this.dmsProjectDao.getDataset(summary.getProjectId());
		Assert.assertNotNull(retrievedProject);
		Assert.assertEquals(summary.getName(), retrievedProject.getName());
		Assert.assertEquals(summary.getDatasetType().getDatasetTypeId(), retrievedProject.getDatasetTypeId());
	}

	@Test
	public void testGetDatasets() {
		final String studyName = "Study1";
		final String programUUID = UUID.randomUUID().toString();

		final DmsProject study = this.createProject(studyName, programUUID);
		final DmsProject summary =
			this.createDataset(studyName + " - Summary Dataset", programUUID, DatasetTypeEnum.SUMMARY_DATA.getId(), study, study);

		final List<DatasetDTO> retrievedProject = this.dmsProjectDao.getDatasets(study.getProjectId());
		Assert.assertFalse(retrievedProject.isEmpty());
		Assert.assertEquals(summary.getName(), retrievedProject.get(0).getName());
		Assert.assertEquals(summary.getDatasetType().getDatasetTypeId(), retrievedProject.get(0).getDatasetTypeId());
	}

	@Test
	public void testGetDatasetOfSampleList() {
		final String studyName = "Study1";
		final String programUUID = UUID.randomUUID().toString();

		final DmsProject study = this.createProject(studyName, programUUID);
		final DmsProject plot =
			this.createDataset(studyName + " - Plot Dataset", programUUID, DatasetTypeEnum.PLOT_DATA.getId(), study, study);

		final WorkbenchUser user = this.testDataInitializer.createUserForTesting();

		final ExperimentModel experimentModel = new ExperimentModel();
		final Geolocation geolocation = new Geolocation();
		this.geolocationDao.saveOrUpdate(geolocation);

		experimentModel.setGeoLocation(geolocation);
		experimentModel.setTypeId(TermId.PLOT_EXPERIMENT.getId());
		experimentModel.setProject(plot);
		experimentModel.setObservationUnitNo(1);
		this.experimentDao.saveOrUpdate(experimentModel);

		final ExperimentProperty experimentProperty = new ExperimentProperty();
		experimentProperty.setExperiment(experimentModel);
		experimentProperty.setTypeId(TermId.PLOT_NO.getId());
		experimentProperty.setValue("1");
		experimentProperty.setRank(1);
		this.experimentPropertyDao.saveOrUpdate(experimentProperty);

		final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasm(1);
		germplasm.setGid(null);
		this.germplasmDao.save(germplasm);

		final StockModel stockModel = new StockModel();
		stockModel.setUniqueName("1");
		stockModel.setIsObsolete(false);
		stockModel.setGermplasm(germplasm);
		stockModel.setCross("-");
		stockModel.setProject(this.study);
		this.stockDao.saveOrUpdate(stockModel);
		experimentModel.setStock(stockModel);
		this.experimentDao.saveOrUpdate(experimentModel);

		final SampleList sampleList = SampleListTestDataInitializer.createSampleList(user.getUserid());
		sampleList.setListName("listName");
		sampleList.setDescription("DESCRIPTION-listName");

		final Sample sample = SampleTestDataInitializer.createSample(sampleList, user.getUserid());
		sample.setSampleName("SAMPLE-listName");
		sample.setSampleBusinessKey("BUSINESS-KEY-listName");
		sample.setEntryNumber(1);
		sample.setExperiment(experimentModel);
		sample.setSampleNumber(1);
		sample.setPlateId("PLATEID");
		sample.setWell("WELLID");

		this.sampleListDao.saveOrUpdate(sampleList);
		this.sampleDao.saveOrUpdate(sample);

		final DatasetDTO retrievedProject = this.dmsProjectDao.getDatasetOfSampleList(sampleList.getId());
		Assert.assertNotNull(retrievedProject);
		Assert.assertEquals(plot.getName(), retrievedProject.getName());
		Assert.assertEquals(plot.getDatasetType().getDatasetTypeId(), retrievedProject.getDatasetTypeId());
	}

	@Test
	public void testGetAllProgramStudiesAndFolders() {
		final String studyName = "Study " + UUID.randomUUID().toString();
		final String folderName = "Folder " + UUID.randomUUID().toString();
		final String programUUID = UUID.randomUUID().toString();

		final DmsProject study = this.createProject(studyName, programUUID);
		final DmsProject folder = this.createProject(folderName, programUUID, false);
		final List<Integer> list = this.dmsProjectDao.getAllProgramStudiesAndFolders(programUUID);
		Assert.assertNotNull(list);
		Assert.assertEquals(2, list.size());
		Assert.assertTrue(list.contains(study.getProjectId()));
		Assert.assertTrue(list.contains(folder.getProjectId()));
	}

	@Test
	public void testGetDatasetNodesByStudyId() {
		final String studyName = "Study " + UUID.randomUUID().toString();
		final String programUUID = UUID.randomUUID().toString();
		final DmsProject study = this.createProject(studyName, programUUID);
		final DmsProject dataset1 =
			this.createDataset("Dataset1 of " + studyName, programUUID, DatasetTypeEnum.PLOT_DATA.getId(), study, study);
		final DmsProject dataset2 =
			this.createDataset("Dataset2 of " + studyName, programUUID, DatasetTypeEnum.MEANS_DATA.getId(), study, study);
		final DmsProject subobsDataset =
			this.createDataset("Subobs Dataset of Dataset 2", programUUID, DatasetTypeEnum.PLANT_SUBOBSERVATIONS.getId(), dataset2, study);
		final List<DatasetReference> list = this.dmsProjectDao.getDirectChildDatasetsOfStudy(study.getProjectId());
		Assert.assertNotNull(list);
		Assert.assertEquals(2, list.size());
		final List<Integer> idList = Lists.transform(list, new Function<DatasetReference, Integer>() {

			@Override
			public Integer apply(final DatasetReference dataset) {
				return dataset.getId();
			}
		});
		Assert.assertTrue(idList.contains(dataset1.getProjectId()));
		Assert.assertTrue(idList.contains(dataset2.getProjectId()));
		Assert.assertFalse(idList.contains(subobsDataset.getProjectId()));
	}

	@Test
	public void testGetObservationSetVariables() {
		final int variableId = 8206;
		final DmsProject plantSubObsDataset =
			this.testDataInitializer.createDmsProject("Plant SubObs Dataset", "Plot Dataset-Description", this.study, this.plot,
				DatasetTypeEnum.PLANT_SUBOBSERVATIONS);
		this.testDataInitializer.addProjectProp(plantSubObsDataset, variableId, "PLANT_NO", VariableType.OBSERVATION_UNIT, "", 1);
		this.sessionProvder.getSession().flush();
		final List<MeasurementVariable> measurementVariables = this.dmsProjectDao
			.getObservationSetVariables(plantSubObsDataset.getProjectId(), Lists.newArrayList(VariableType.OBSERVATION_UNIT.getId()));
		Assert.assertEquals(1, measurementVariables.size());
		Assert.assertEquals(variableId, measurementVariables.get(0).getTermId());
	}

	@Test
	public void testGetStudyDetails() {
		final StudyDetails studyDetails = this.dmsProjectDao.getStudyDetails(this.study.getProjectId());
		Assert.assertEquals(this.study.getProjectId(), studyDetails.getId());
		Assert.assertEquals(this.study.getDescription(), studyDetails.getDescription());
		Assert.assertEquals(this.study.getObjective(), studyDetails.getObjective());
		Assert.assertEquals(this.study.getStartDate(), studyDetails.getStartDate());
		Assert.assertEquals(this.study.getEndDate(), studyDetails.getEndDate());
		Assert.assertEquals(this.study.getProgramUUID(), studyDetails.getProgramUUID());
		Assert.assertEquals(this.study.getStudyType().getStudyTypeId(), studyDetails.getStudyType().getId());
		Assert.assertEquals(DmsProject.SYSTEM_FOLDER_ID.longValue(), studyDetails.getParentFolderId());
		Assert.assertFalse(studyDetails.getIsLocked());
	}

	@Test
	public void testGetStudyMetadataForInstanceId() {
		final DmsProject plot =
			this.createDataset(this.study.getName() + " - Plot Dataset", this.study.getProgramUUID(), DatasetTypeEnum.PLOT_DATA.getId(),
				this.study, this.study);
		final Integer locationId = 3;
		final Integer instanceId = this.createEnvironmentData(plot, "1", locationId, Optional.<String>absent(), Optional.<Integer>absent(), false);
		final StudyMetadata studyMetadata = this.dmsProjectDao.getStudyMetadataForInstanceId(instanceId);
		Assert.assertNotNull(studyMetadata);
		Assert.assertEquals(instanceId, studyMetadata.getStudyDbId());
		Assert.assertEquals(locationId, studyMetadata.getLocationId());
		Assert.assertEquals(this.study.getProjectId(), studyMetadata.getTrialDbId());
		Assert.assertEquals(this.study.getProjectId(), studyMetadata.getNurseryOrTrialId());
		Assert.assertEquals(this.study.getName(), studyMetadata.getTrialName());
		Assert.assertEquals(this.study.getName() + " Environment Number " + 1, studyMetadata.getStudyName());
		Assert.assertEquals(String.valueOf(STUDY_TYPE_ID), studyMetadata.getStudyType());
	}

	@Test
	public void testGetDataSets() {
		final String studyName = "Study1";
		final String programUUID = UUID.randomUUID().toString();

		final DmsProject study = this.createProject(studyName, programUUID);
		final DmsProject summary = this.createDataset(studyName, programUUID, DatasetTypeEnum.SUMMARY_DATA.getId(), study, study);

		final List<DatasetDTO> datasets = this.dmsProjectDao.getDatasets(study.getProjectId());
		Assert.assertEquals(1, datasets.size());
		Assert.assertEquals(summary.getName(), datasets.get(0).getName());
		Assert.assertEquals(summary.getDatasetType().getDatasetTypeId(), datasets.get(0).getDatasetTypeId());
	}

	@Test
	public void testGetStudyInstances() {

		final Project workbenchProject = this.testDataInitializer.createWorkbenchProject();
		this.workbenchSessionProvider.getSessionFactory().getCurrentSession().flush();

		final String studyName = "Study Search";
		// Afghanistan location
		final String locationId = "1";
		final DmsProject study = this.createProject(studyName, workbenchProject.getUniqueID(), true);
		final DmsProject plot =
			this.createDataset(studyName + " - Plot Dataset", workbenchProject.getUniqueID(), DatasetTypeEnum.PLOT_DATA.getId(),
				study, study);
		final DmsProject summary =
			this.createDataset(studyName + " - Summary Dataset", workbenchProject.getUniqueID(), DatasetTypeEnum.SUMMARY_DATA.getId(),
				study, study);

		final Geolocation instance1 = this.testDataInitializer.createInstance(summary, locationId, 1);
		this.testDataInitializer.addGeolocationProp(instance1, TermId.SEASON_VAR.getId(), String.valueOf(TermId.SEASON_DRY.getId()), 1);

		final StudySearchFilter studySearchFilter = new StudySearchFilter();
		final Long count = (Long) this.dmsProjectDao.countStudyInstances(studySearchFilter);
		final List<StudyInstanceDto> studyInstanceDtos =
			this.dmsProjectDao.getStudyInstances(studySearchFilter, new PageRequest(0, Integer.MAX_VALUE));
		Assert.assertEquals(count.intValue(), studyInstanceDtos.size());

	}

	@Test
	public void testGetObservationLevelsMap() {
		final Project workbenchProject = this.testDataInitializer.createWorkbenchProject();
		this.workbenchSessionProvider.getSessionFactory().getCurrentSession().flush();

		final String studyName = "Study Search";
		// Afghanistan location
		final String locationId = "1";
		final DmsProject study = this.createProject(studyName, workbenchProject.getUniqueID(), true);
		final DmsProject plot =
			this.createDataset(studyName + " - Plot Dataset", workbenchProject.getUniqueID(), DatasetTypeEnum.PLOT_DATA.getId(),
				study, study);
		final DmsProject summary =
			this.createDataset(studyName + " - Summary Dataset", workbenchProject.getUniqueID(), DatasetTypeEnum.SUMMARY_DATA.getId(),
				study, study);
		final DmsProject subObs =
			this.createDataset("Plant Sub", workbenchProject.getUniqueID(), DatasetTypeEnum.PLANT_SUBOBSERVATIONS.getId(),
				plot, study);

		final Map<Integer, List<ObservationLevel>> observationLevelsMap = this.dmsProjectDao.getObservationLevelsMap(
			Collections.singletonList(study.getProjectId()));

		Assert.assertTrue(observationLevelsMap.containsKey(study.getProjectId()));
		Assert.assertEquals(2, observationLevelsMap.get(study.getProjectId()).size());
	}

	@Test
	public void testGetStudyIdEnvironmentDatasetIdMap() {
		final DmsProject summary =
			this.createDataset(this.study.getName() + " - Summary Dataset", this.study.getProgramUUID(),
				DatasetTypeEnum.SUMMARY_DATA.getId(),
				this.study, this.study);
		final Map<Integer, Integer> studyIdEnvIdMap = this.dmsProjectDao.getStudyIdEnvironmentDatasetIdMap(
			Collections.singletonList(this.study.getProjectId()));
		Assert.assertEquals(summary.getProjectId(), studyIdEnvIdMap.get(this.study.getProjectId()));
	}

	@Test
	public void testGetStudyInstancesWithStudyFilter() {

		final Project workbenchProject = this.testDataInitializer.createWorkbenchProject();
		this.workbenchSessionProvider.getSessionFactory().getCurrentSession().flush();

		final String studyName = "Study Search";
		// Afghanistan location
		final String locationId = "1";
		final DmsProject study = this.createProject(studyName, workbenchProject.getUniqueID(), true);
		final DmsProject plot =
			this.createDataset(studyName + " - Plot Dataset", workbenchProject.getUniqueID(), DatasetTypeEnum.PLOT_DATA.getId(),
				study, study);
		final DmsProject summary =
			this.createDataset(studyName + " - Summary Dataset", workbenchProject.getUniqueID(), DatasetTypeEnum.SUMMARY_DATA.getId(),
				study, study);
		final String externalReferenceId = RandomStringUtils.randomAlphabetic(10);
		final String externalReferenceSource = RandomStringUtils.randomAlphabetic(10);

		final Geolocation instance1 = this.testDataInitializer.createInstance(summary, locationId, 1);
		this.testDataInitializer.addGeolocationProp(instance1, TermId.SEASON_VAR.getId(), String.valueOf(TermId.SEASON_DRY.getId()), 1);
		this.testDataInitializer.addInstanceExternalReferenceSource(instance1, externalReferenceId, externalReferenceSource);

		this.sessionProvder.getSession().flush();

		final StudySearchFilter studySearchFilter = new StudySearchFilter();
		studySearchFilter.setTrialDbIds(Collections.singletonList(study.getProjectId().toString()));
		studySearchFilter.setStudyDbIds(Collections.singletonList(String.valueOf(instance1.getLocationId())));
		studySearchFilter.setLocationDbId(locationId);
		studySearchFilter.setStudyTypeDbId(String.valueOf(STUDY_TYPE_ID));
		studySearchFilter.setSeasonDbId(String.valueOf(TermId.SEASON_DRY.getId()));
		studySearchFilter.setExternalReferenceSource(externalReferenceSource);
		studySearchFilter.setExternalReferenceID(externalReferenceId);
		studySearchFilter.setActive(false);

		final Long count = (Long) this.dmsProjectDao.countStudyInstances(studySearchFilter);
		final List<StudyInstanceDto> studyInstanceDtos =
			this.dmsProjectDao.getStudyInstances(studySearchFilter, new PageRequest(0, Integer.MAX_VALUE));

		Assert.assertEquals(1, count.intValue());
		Assert.assertEquals(1, studyInstanceDtos.size());
		final StudyInstanceDto studyInstanceDto = studyInstanceDtos.get(0);
		final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

		Assert.assertEquals(String.valueOf(study.getProjectId()), studyInstanceDto.getTrialDbId());
		Assert.assertEquals(String.valueOf(study.getName()), studyInstanceDto.getTrialName());
		Assert.assertEquals(study.getStartDate(), dateFormat.format(studyInstanceDto.getStartDate()));
		Assert.assertEquals(study.getEndDate(), dateFormat.format(studyInstanceDto.getEndDate()));
		Assert.assertEquals(String.valueOf(study.getStudyType().getStudyTypeId()), studyInstanceDto.getStudyTypeDbId());
		Assert.assertEquals(study.getStudyType().getLabel(), studyInstanceDto.getStudyTypeName());
		Assert.assertEquals(String.valueOf(instance1.getLocationId()), studyInstanceDto.getStudyDbId());
		Assert.assertEquals(study.getName() + " Environment Number 1", studyInstanceDto.getStudyName());
		Assert.assertEquals("false", studyInstanceDto.getActive());
		Assert.assertEquals("1", studyInstanceDto.getLocationDbId());
		Assert.assertEquals("Afghanistan", studyInstanceDto.getLocationName());
		Assert.assertEquals(String.valueOf(TermId.SEASON_DRY.getId()), studyInstanceDto.getSeasons().get(0).getSeasonDbId());
		Assert.assertEquals("Dry season", studyInstanceDto.getSeasons().get(0).getSeason());

	}

	@Test
	public void getFolderByParentAndName() {
		final String programUUID = UUID.randomUUID().toString();
		final String studyName = RandomStringUtils.randomAlphabetic(10);
		final DmsProject study = this.createProject(studyName, programUUID, true);
		assertFalse(this.dmsProjectDao.getFolderByParentAndName(study.getParent().getProjectId(), studyName, programUUID).isPresent());

		final DmsProject folderAnotherProgram = this.createProject(studyName, UUID.randomUUID().toString(), false);
		assertFalse(this.dmsProjectDao.getFolderByParentAndName(folderAnotherProgram.getParent().getProjectId(), studyName, programUUID).isPresent());

		final DmsProject folder = this.createProject(studyName, programUUID, false);
		final java.util.Optional<FolderReference> optionalFolder =
			this.dmsProjectDao.getFolderByParentAndName(folder.getParent().getProjectId(), studyName, programUUID);
		assertTrue(optionalFolder.isPresent());
		final FolderReference expectedFolder = optionalFolder.get();
		assertThat(expectedFolder.getId(), is(folder.getProjectId()));
		assertThat(expectedFolder.getName(), is(folder.getName()));
		assertThat(expectedFolder.getDescription(), is(folder.getDescription()));
		assertThat(expectedFolder.getProgramUUID(), is(programUUID));
		assertThat(expectedFolder.getParentFolderId(), is(folder.getParent().getProjectId()));
	}

	private DmsProject createProject(final String name, final String programUUID) {
		return this.createProject(name, programUUID, true);
	}

	private DmsProject createProject(final String name, final String programUUID, final boolean isStudy) {
		final DmsProject project = new DmsProject();
		project.setName(name);
		project.setDescription(name + RandomStringUtils.randomAlphabetic(20));
		project.setProgramUUID(programUUID);

		final DmsProject parent = new DmsProject();
		parent.setProjectId(DmsProject.SYSTEM_FOLDER_ID);
		project.setParent(parent);

		if (isStudy) {
			final StudyType studyType = new StudyType();
			studyType.setStudyTypeId(STUDY_TYPE_ID);
			project.setStudyType(studyType);

			project.setObjective(RandomStringUtils.randomAlphabetic(20));
			project.setStartDate("20190101");
			project.setEndDate("20190630");
		}
		this.dmsProjectDao.save(project);
		this.dmsProjectDao.refresh(project);
		return project;
	}

	private DmsProject createDataset(final String name, final String programUUID, final int datasetType, final DmsProject parent,
		final DmsProject study) {
		final DmsProject dataset = new DmsProject();
		dataset.setName(name);
		dataset.setDescription(name);
		dataset.setProgramUUID(programUUID);
		dataset.setDatasetType(new DatasetType(datasetType));
		dataset.setParent(parent);
		dataset.setStudy(study);
		this.dmsProjectDao.save(dataset);
		return dataset;
	}

	private Integer createEnvironmentData(
		final String instanceNumber, final Integer locationId, final Optional<String> customAbbev, final Optional<Integer> blockId,
		final boolean addFieldLayout) {
		return this.createEnvironmentData(this.study, instanceNumber, locationId, customAbbev, blockId, addFieldLayout);
	}

	private Integer createEnvironmentData(final DmsProject project,
		final String instanceNumber, final Integer locationId, final Optional<String> customAbbev, final Optional<Integer> blockId,
		final boolean addFieldLayout) {
		final Geolocation geolocation = new Geolocation();
		geolocation.setDescription(instanceNumber);
		this.geolocationDao.saveOrUpdate(geolocation);

		final GeolocationProperty prop = new GeolocationProperty();
		prop.setGeolocation(geolocation);
		prop.setType(TermId.LOCATION_ID.getId());
		prop.setRank(1);
		prop.setValue(locationId.toString());
		this.geolocPropDao.save(prop);

		if (customAbbev.isPresent()) {
			final GeolocationProperty prop2 = new GeolocationProperty();
			prop2.setGeolocation(geolocation);
			prop2.setType(TermId.LOCATION_ABBR.getId());
			prop2.setRank(2);
			prop2.setValue(customAbbev.get());
			this.geolocPropDao.save(prop2);
		}

		if (blockId.isPresent()) {
			final GeolocationProperty prop3 = new GeolocationProperty();
			prop3.setGeolocation(geolocation);
			prop3.setType(TermId.BLOCK_ID.getId());
			prop3.setRank(3);
			prop3.setValue(blockId.get().toString());
			this.geolocPropDao.save(prop3);
		}

		for (int i = 1; i < NO_OF_GERMPLASM + 1; i++) {
			final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasm(1);
			germplasm.setGid(null);
			this.germplasmDao.save(germplasm);

			final StockModel stockModel = new StockModel();
			stockModel.setIsObsolete(false);
			stockModel.setUniqueName(String.valueOf(i));
			stockModel.setGermplasm(germplasm);
			stockModel.setCross("-");
			stockModel.setProject(this.study);
			this.stockDao.saveOrUpdate(stockModel);

			final ExperimentModel experimentModel = new ExperimentModel();
			experimentModel.setGeoLocation(geolocation);
			experimentModel.setTypeId(TermId.TRIAL_ENVIRONMENT_EXPERIMENT.getId());
			experimentModel.setObsUnitId(RandomStringUtils.randomAlphabetic(13));
			experimentModel.setProject(project);
			experimentModel.setStock(stockModel);
			this.experimentDao.saveOrUpdate(experimentModel);

			if (addFieldLayout) {
				this.saveExperimentProperty(experimentModel, TermId.FIELDMAP_COLUMN.getId(), "1");
				this.saveExperimentProperty(experimentModel, TermId.FIELDMAP_RANGE.getId(), "1");
			}
		}

		return geolocation.getLocationId();
	}

	private void saveExperimentProperty(final ExperimentModel experimentModel, final Integer typeId, final String value) {
		final ExperimentProperty experimentProperty = new ExperimentProperty();
		experimentModel.setProperties(new ArrayList<>(Collections.singleton(experimentProperty)));
		experimentProperty.setExperiment(experimentModel);
		experimentProperty.setTypeId(typeId);
		experimentProperty.setValue(value);
		experimentProperty.setRank(1);
		this.experimentPropertyDao.saveOrUpdate(experimentProperty);
	}

}
