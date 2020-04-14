package org.generationcp.middleware.dao.dms;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.PersonDAO;
import org.generationcp.middleware.dao.SampleDao;
import org.generationcp.middleware.dao.SampleListDao;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.data.initializer.SampleListTestDataInitializer;
import org.generationcp.middleware.data.initializer.SampleTestDataInitializer;
import org.generationcp.middleware.domain.dms.DatasetBasicDTO;
import org.generationcp.middleware.domain.dms.DatasetDTO;
import org.generationcp.middleware.domain.dms.DatasetReference;
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
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.dms.StudyType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.study.StudyDto;
import org.generationcp.middleware.service.api.study.StudyMetadata;
import org.generationcp.middleware.service.api.study.StudySearchFilter;
import org.generationcp.middleware.service.impl.study.StudyInstance;
import org.generationcp.middleware.utils.test.IntegrationTestDataInitializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;

public class DmsProjectDaoIntegrationTest extends IntegrationTestBase {

	private static final int NO_OF_GERMPLASM = 5;
	private static final int STUDY_TYPE_ID = 6;

	private ExperimentPropertyDao experimentPropertyDao;

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
		this.experimentPropertyDao = new ExperimentPropertyDao();
		this.experimentPropertyDao.setSession(this.sessionProvder.getSession());

		if (this.germplasmDao == null) {
			this.germplasmDao = new GermplasmDAO();
			this.germplasmDao.setSession(this.sessionProvder.getSession());
		}

		if (this.experimentDao == null) {
			this.experimentDao = new ExperimentDao();
			this.experimentDao.setSession(this.sessionProvder.getSession());
		}

		if (this.stockDao == null) {
			this.stockDao = new StockDao();
			this.stockDao.setSession(this.sessionProvder.getSession());
		}

		if (this.dmsProjectDao == null) {
			this.dmsProjectDao = new DmsProjectDao();
			this.dmsProjectDao.setSession(this.sessionProvder.getSession());
		}

		if (this.personDao == null) {
			this.personDao = new PersonDAO();
			this.personDao.setSession(this.sessionProvder.getSession());
		}

		if (this.sampleDao == null) {
			this.sampleDao = new SampleDao();
			this.sampleDao.setSession(this.sessionProvder.getSession());
		}

		if (this.sampleListDao == null) {
			this.sampleListDao = new SampleListDao();
			this.sampleListDao.setSession(this.sessionProvder.getSession());
		}

		if (this.projectPropDao == null) {
			this.projectPropDao = new ProjectPropertyDao();
			this.projectPropDao.setSession(this.sessionProvder.getSession());
		}

		if (this.cvTermDao == null) {
			this.cvTermDao = new CVTermDao();
			this.cvTermDao.setSession(this.sessionProvder.getSession());
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
		final DmsProject summaryDataset = this.testDataInitializer
			.createDmsProject("Summary Dataset", "Summary Dataset-Description", this.study, this.study, DatasetTypeEnum.SUMMARY_DATA);
		final ExperimentModel instanceModel1 = this.testDataInitializer.createInstanceExperimentModel(summaryDataset, 1, "1");
		this.testDataInitializer.addExperimentProp(instanceModel1, TermId.BLOCK_ID.getId(), "1", 2);
		final ExperimentModel instanceModel2= this.testDataInitializer.createInstanceExperimentModel(summaryDataset, 2, "2");
		this.testDataInitializer.addExperimentProp(instanceModel2, TermId.BLOCK_ID.getId(), "1", 2);
		final ExperimentModel instanceModel3 = this.testDataInitializer.createInstanceExperimentModel(summaryDataset, 3, "3");
		final List<StudyInstance> instances = this.dmsProjectDao.getDatasetInstances(summaryDataset.getProjectId(), new DatasetType( DatasetTypeEnum.SUMMARY_DATA.getId()));
		Assert.assertEquals(3, instances.size());

		final StudyInstance instance1 = instances.get(0);
		Assert.assertEquals(instanceModel1.getNdExperimentId().intValue(), instance1.getExperimentId());
		Assert.assertEquals(1, instance1.getInstanceNumber());
		Assert.assertEquals("Afghanistan", instance1.getLocationName());
		Assert.assertEquals("AFG", instance1.getLocationAbbreviation());
		Assert.assertNull(instance1.getCustomLocationAbbreviation());
		Assert.assertTrue(instance1.isHasFieldmap());

		final StudyInstance instance2 = instances.get(1);
		Assert.assertEquals(instanceModel2.getNdExperimentId().intValue(), instance2.getExperimentId());
		Assert.assertEquals(2, instance2.getInstanceNumber());
		Assert.assertEquals("Albania", instance2.getLocationName());
		Assert.assertEquals("ALB", instance2.getLocationAbbreviation());
		Assert.assertNull(instance2.getCustomLocationAbbreviation());
		Assert.assertTrue(instance2.isHasFieldmap());

		final StudyInstance instance3 = instances.get(2);
		Assert.assertEquals(instanceModel3.getNdExperimentId().intValue(), instance3.getExperimentId());
		Assert.assertEquals(3, instance3.getInstanceNumber());
		Assert.assertEquals("Algeria", instance3.getLocationName());
		Assert.assertEquals("DZA", instance3.getLocationAbbreviation());
		Assert.assertNull(instance3.getCustomLocationAbbreviation());
		Assert.assertFalse(instance3.isHasFieldmap());
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

		final ExperimentModel experimentModel = new ExperimentModel();
		experimentModel.setTypeId(TermId.SUMMARY_EXPERIMENT.getId());
		experimentModel.setProject(summary);
		this.experimentDao.saveOrUpdate(experimentModel);

		// Need to flush session to sync with underlying database before querying
		this.sessionProvder.getSessionFactory().getCurrentSession().flush();

		//  TODO IBP-3389 Fix assertions
//		final Integer result = this.dmsProjectDao.getProjectIdByStudyDbId(geolocation.getLocationId());
//		Assert.assertEquals(study.getProjectId(), result);
	}

	@Test
	public void testGetDataset() {
		final String studyName = "Study1";
		final String programUUID = UUID.randomUUID().toString();

		final DmsProject study = this.createProject(studyName, programUUID);
		final DmsProject summary =
			this.createDataset(studyName + " - Summary Dataset", programUUID, DatasetTypeEnum.SUMMARY_DATA.getId(), study, study);

		final DatasetBasicDTO retrievedProject = this.dmsProjectDao.getDataset(summary.getProjectId());
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
		stockModel.setTypeId(TermId.ENTRY_CODE.getId());
		stockModel.setName("Germplasm 1");
		stockModel.setIsObsolete(false);
		stockModel.setGermplasm(germplasm);

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
		final StudyDetails studyDetails = this.dmsProjectDao.getStudyDetails(study.getProjectId());
		Assert.assertEquals(study.getProjectId(), studyDetails.getId());
		Assert.assertEquals(study.getDescription(), studyDetails.getDescription());
		Assert.assertEquals(study.getObjective(), studyDetails.getObjective());
		Assert.assertEquals(study.getStartDate(), studyDetails.getStartDate());
		Assert.assertEquals(study.getEndDate(), studyDetails.getEndDate());
		Assert.assertEquals(study.getProgramUUID(), studyDetails.getProgramUUID());
		Assert.assertEquals(study.getStudyType().getStudyTypeId(), studyDetails.getStudyType().getId());
		Assert.assertEquals(DmsProject.SYSTEM_FOLDER_ID.longValue(), studyDetails.getParentFolderId());
		Assert.assertFalse(studyDetails.getIsLocked());
	}

	@Test
	public void testGetStudyMetadataForEnvironmentId() {
		final DmsProject summary =
			this.createDataset(this.study.getName() + " - Summary Dataset", this.study.getProgramUUID(), DatasetTypeEnum.SUMMARY_DATA.getId(),
				study, study);
		final Integer locationId = 1;
		final Integer instanceId = this.testDataInitializer.createInstanceExperimentModel(summary, 1, "1").getNdExperimentId();
		final StudyMetadata studyMetadata = this.dmsProjectDao.getStudyMetadataForEnvironmentId(instanceId);
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
	public void testGetStudies() {

		final Project workbenchProject = this.testDataInitializer.createWorkbenchProject();
		this.workbenchSessionProvider.getSessionFactory().getCurrentSession().flush();

		final String studyName = "Study Search";
		// Afghanistan location
		final String locationId = "1";
		final DmsProject study = this.createProject(studyName, workbenchProject.getUniqueID(), true);
		final DmsProject summary =
			this.createDataset(studyName + " - Summary Dataset", workbenchProject.getUniqueID(), DatasetTypeEnum.SUMMARY_DATA.getId(),
				study, study);

		this.testDataInitializer.createInstanceExperimentModel(summary, 1, "1");

		final StudySearchFilter studySearchFilter = new StudySearchFilter();
		final Long count = (Long) this.dmsProjectDao.countStudies(studySearchFilter);
		final List<StudyDto> studyDtos = this.dmsProjectDao.getStudies(studySearchFilter);
		Assert.assertEquals(count.intValue(), studyDtos.size());

	}

	@Test
	public void testGetStudiesWithStudyFilter() {

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

		final ExperimentModel instance = this.testDataInitializer.createInstanceExperimentModel(summary, 1, locationId);
		this.testDataInitializer.addExperimentProp(instance, TermId.SEASON_VAR.getId(), String.valueOf(TermId.SEASON_DRY.getId()), 2);

		final StudySearchFilter studySearchFilter = new StudySearchFilter();
		studySearchFilter.setTrialDbId(study.getProjectId().toString());
		studySearchFilter.setStudyDbId(String.valueOf(instance.getObservationUnitNo()));
		studySearchFilter.setLocationDbId(locationId);
		studySearchFilter.setStudyTypeDbId(String.valueOf(STUDY_TYPE_ID));
		studySearchFilter.setSeasonDbId(String.valueOf(TermId.SEASON_DRY.getId()));
		studySearchFilter.setActive(true);

		final Long count = (Long) this.dmsProjectDao.countStudies(studySearchFilter);
		final List<StudyDto> studyDtos = this.dmsProjectDao.getStudies(studySearchFilter);

		Assert.assertEquals(1, count.intValue());
		Assert.assertEquals(1, studyDtos.size());
		final StudyDto studyDto = studyDtos.get(0);
		final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

		Assert.assertEquals(String.valueOf(study.getProjectId()), studyDto.getTrialDbId());
		Assert.assertEquals(String.valueOf(study.getName()), studyDto.getTrialName());
		Assert.assertEquals(study.getStartDate(), dateFormat.format(studyDto.getStartDate()));
		Assert.assertEquals(study.getEndDate(), dateFormat.format(studyDto.getEndDate()));
		Assert.assertEquals(String.valueOf(study.getStudyType().getStudyTypeId()), studyDto.getStudyTypeDbId());
		Assert.assertEquals(study.getStudyType().getLabel(), studyDto.getStudyTypeName());
		Assert.assertEquals(String.valueOf(instance.getObservationUnitNo()), studyDto.getStudyDbId());
		Assert.assertEquals(study.getName() + " Environment Number 1", studyDto.getStudyName());
		Assert.assertEquals("true", studyDto.getActive());
		Assert.assertEquals("1", studyDto.getLocationDbId());
		Assert.assertEquals("Afghanistan", studyDto.getLocationName());
		Assert.assertEquals(String.valueOf(TermId.SEASON_DRY.getId()), studyDto.getSeasons().get(0).getSeasonDbId());
		Assert.assertEquals("Dry season", studyDto.getSeasons().get(0).getSeason());

	}

	private DmsProject createProject(final String name, final String programUUID) {
		return this.createProject(name, programUUID, true);
	}

	private DmsProject createProject(final String name, final String programUUID, final boolean isStudy) {
		final DmsProject project = new DmsProject();
		project.setName(name);
		project.setDescription(name + RandomStringUtils.randomAlphabetic(20));
		project.setProgramUUID(programUUID);
		if (isStudy) {
			final StudyType studyType = new StudyType();
			studyType.setStudyTypeId(STUDY_TYPE_ID);
			project.setStudyType(studyType);

			final DmsProject parent = new DmsProject();
			parent.setProjectId(DmsProject.SYSTEM_FOLDER_ID);
			project.setParent(parent);

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

}
