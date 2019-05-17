package org.generationcp.middleware.dao.dms;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.PersonDAO;
import org.generationcp.middleware.dao.SampleDao;
import org.generationcp.middleware.dao.SampleListDao;
import org.generationcp.middleware.dao.UserDAO;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.data.initializer.PersonTestDataInitializer;
import org.generationcp.middleware.data.initializer.SampleListTestDataInitializer;
import org.generationcp.middleware.data.initializer.SampleTestDataInitializer;
import org.generationcp.middleware.data.initializer.UserTestDataInitializer;
import org.generationcp.middleware.domain.dms.DatasetDTO;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.Sample;
import org.generationcp.middleware.pojos.SampleList;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.dms.DatasetType;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.ExperimentProperty;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.GeolocationProperty;
import org.generationcp.middleware.pojos.dms.ProjectRelationship;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.service.impl.study.StudyInstance;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Optional;


public class DmsProjectDaoIntegrationTest extends IntegrationTestBase {

	private static final int NO_OF_GERMPLASM = 5;

	private ExperimentPropertyDao experimentPropertyDao;

	private GeolocationDao geolocationDao;

	private GeolocationPropertyDao geolocPropDao;

	private ExperimentDao experimentDao;

	private StockDao stockDao;

	private GermplasmDAO germplasmDao;

	private DmsProjectDao dmsProjectDao;

	private ProjectRelationshipDao projectRelationshipDao;

	private PersonDAO personDao;

	private UserDAO userDao;

	private SampleListDao sampleListDao;

	private SampleDao sampleDao;

	private DmsProject study;

	@Before
	public void setUp() {
		this.experimentPropertyDao = new ExperimentPropertyDao();
		this.experimentPropertyDao.setSession(this.sessionProvder.getSession());

		if (this.geolocationDao == null) {
			this.geolocationDao = new GeolocationDao();
			this.geolocationDao.setSession(this.sessionProvder.getSession());
		}

		if (this.geolocPropDao == null) {
			this.geolocPropDao = new GeolocationPropertyDao();
			this.geolocPropDao.setSession(this.sessionProvder.getSession());
		}

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

		if (this.projectRelationshipDao == null) {
			this.projectRelationshipDao = new ProjectRelationshipDao();
			this.projectRelationshipDao.setSession(this.sessionProvder.getSession());
		}

		if (this.personDao == null) {
			this.personDao = new PersonDAO();
			this.personDao.setSession(this.sessionProvder.getSession());
		}

		if (this.userDao == null) {
			this.userDao = new UserDAO();
			this.userDao.setSession(this.sessionProvder.getSession());
		}

		if (this.sampleDao == null) {
			this.sampleDao = new SampleDao();
			this.sampleDao.setSession(this.sessionProvder.getSession());
		}

		if (this.sampleListDao == null) {
			this.sampleListDao = new SampleListDao();
			this.sampleListDao.setSession(this.sessionProvder.getSession());
		}

		if (this.study == null) {
			this.study = new DmsProject();
			this.study.setName("Test Project");
			this.study.setDescription("Test Project");
			this.dmsProjectDao.save(this.study);
		}
	}

	@Test
	public void testGetDatasetInstances() {
		final Integer env1 = this.createEnvironmentData("1", 1, Optional.<String>absent(), Optional.of(1));
		final Integer env2 = this.createEnvironmentData("2", 2, Optional.<String>absent(), Optional.of(2));
		final String customLocation = RandomStringUtils.randomAlphabetic(10);
		final Integer env3 = this.createEnvironmentData("3", 3, Optional.of(customLocation), Optional.<Integer>absent());
		final List<StudyInstance> instances = this.dmsProjectDao.getDatasetInstances(this.study.getProjectId());
		Assert.assertEquals(3, instances.size());

		final StudyInstance instance1 = instances.get(0);
		Assert.assertEquals(env1.intValue(), instance1.getInstanceDbId());
		Assert.assertEquals(1, instance1.getInstanceNumber());
		Assert.assertEquals("Afghanistan", instance1.getLocationName());
		Assert.assertEquals("AFG", instance1.getLocationAbbreviation());
		Assert.assertNull(instance1.getCustomLocationAbbreviation());
		Assert.assertTrue(instance1.isHasFieldmap());

		final StudyInstance instance2 = instances.get(1);
		Assert.assertEquals(env2.intValue(), instance2.getInstanceDbId());
		Assert.assertEquals(2, instance2.getInstanceNumber());
		Assert.assertEquals("Albania", instance2.getLocationName());
		Assert.assertEquals("ALB", instance2.getLocationAbbreviation());
		Assert.assertNull(instance2.getCustomLocationAbbreviation());
		Assert.assertTrue(instance2.isHasFieldmap());

		final StudyInstance instance3 = instances.get(2);
		Assert.assertEquals(env3.intValue(), instance3.getInstanceDbId());
		Assert.assertEquals(3, instance3.getInstanceNumber());
		Assert.assertEquals("Algeria", instance3.getLocationName());
		Assert.assertEquals("DZA", instance3.getLocationAbbreviation());
		Assert.assertEquals(customLocation, instance3.getCustomLocationAbbreviation());
		Assert.assertFalse(instance3.isHasFieldmap());
	}

	@Test
	public void testGetByStudyAndDatasetType() {

		final String studyName = "Study1";
		final String programUUID = UUID.randomUUID().toString();

		final DmsProject study = this.createStudy(studyName, programUUID);
		final DmsProject plot = this.createDataset(studyName + " - Plot Dataset", programUUID, DatasetType.PLOT_DATA);
		this.createProjectRelationship(study, plot);

		final List<DmsProject> resultPlot = this.dmsProjectDao.getByStudyAndDatasetType(study.getProjectId(), DatasetType.PLOT_DATA);
		Assert.assertEquals(1, resultPlot.size());
		Assert.assertEquals(plot.getProjectId(), resultPlot.get(0).getProjectId());

		final List<DmsProject> result = this.dmsProjectDao.getByStudyAndDatasetType(study.getProjectId(), DatasetType.PLANT_SUBOBSERVATIONS);
		Assert.assertEquals(0, result.size());

	}

	@Test
	public void testGetProjectIdByStudyDbId() {

		final String studyName = "Study1";
		final String programUUID = UUID.randomUUID().toString();

		final DmsProject study = this.createStudy(studyName, programUUID);
		final DmsProject summary = this.createDataset(studyName + " - Summary Dataset", programUUID, DatasetType.SUMMARY_DATA);
		this.createProjectRelationship(study, summary);

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

		final DmsProject study = this.createStudy(studyName, programUUID);
		final DmsProject summary = this.createDataset(studyName + " - Summary Dataset", programUUID, DatasetType.SUMMARY_DATA);
		this.createProjectRelationship(study, summary);

		final DatasetDTO retrievedProject = this.dmsProjectDao.getDataset(summary.getProjectId());
		Assert.assertNotNull(retrievedProject);
		Assert.assertEquals(summary.getName(), retrievedProject.getName());
		Assert.assertEquals(summary.getDatasetType().getDatasetTypeId(), retrievedProject.getDatasetTypeId());
	}

	@Test
	public void testGetDatasets() {
		final String studyName = "Study1";
		final String programUUID = UUID.randomUUID().toString();

		final DmsProject study = this.createStudy(studyName, programUUID);
		final DmsProject summary = this.createDataset(studyName + " - Summary Dataset", programUUID, DatasetType.SUMMARY_DATA);
		this.createProjectRelationship(study, summary);

		final List<DatasetDTO> retrievedProject = this.dmsProjectDao.getDatasets(study.getProjectId());
		Assert.assertFalse(retrievedProject.isEmpty());
		Assert.assertEquals(summary.getName(), retrievedProject.get(0).getName());
		Assert.assertEquals(summary.getDatasetType().getDatasetTypeId(), retrievedProject.get(0).getDatasetTypeId());
	}

	@Test
	public void testGetDatasetOfSampleList() {
		final String studyName = "Study1";
		final String programUUID = UUID.randomUUID().toString();

		final DmsProject study = this.createStudy(studyName, programUUID);
		final DmsProject plot = this.createDataset(studyName + " - Plot Dataset", programUUID, DatasetType.PLOT_DATA);
		this.createProjectRelationship(study, plot);

		final Person person = PersonTestDataInitializer.createPerson();
		person.setFirstName("John");
		person.setLastName("Doe");
		this.personDao.saveOrUpdate(person);

		final User user = UserTestDataInitializer.createUser();
		user.setName("USER");
		user.setUserid(null);
		user.setPersonid(person.getId());
		user.setPerson(person);
		this.userDao.saveOrUpdate(user);

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
		stockModel.setTypeId(TermId.ENTRY_CODE.getId());
		stockModel.setName("Germplasm 1");
		stockModel.setIsObsolete(false);
		stockModel.setGermplasm(germplasm);

		this.stockDao.saveOrUpdate(stockModel);
		experimentModel.setStock(stockModel);
		this.experimentDao.saveOrUpdate(experimentModel);

		final SampleList sampleList = SampleListTestDataInitializer.createSampleList(user);
		sampleList.setListName("listName");
		sampleList.setDescription("DESCRIPTION-listName");

		final Sample sample = SampleTestDataInitializer.createSample(sampleList, user);
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

	private void createProjectRelationship(final DmsProject study, final DmsProject subject) {
		final ProjectRelationship projectRelationship = new ProjectRelationship();
		projectRelationship.setTypeId(TermId.BELONGS_TO_STUDY.getId());
		projectRelationship.setObjectProject(study);
		projectRelationship.setSubjectProject(subject);
		this.projectRelationshipDao.save(projectRelationship);
	}

	private DmsProject createStudy(final String studyName, final String programUUID) {
		final DmsProject study = new DmsProject();
		study.setName(studyName);
		study.setDescription(studyName);
		study.setProgramUUID(programUUID);
		this.dmsProjectDao.save(study);
		return study;
	}

	private DmsProject createDataset(final String studyName, final String programUUID, final int datasetType) {
		final DmsProject dataset = new DmsProject();
		dataset.setName(studyName);
		dataset.setDescription(studyName);
		dataset.setProgramUUID(programUUID);
		dataset.setDatasetType(new DatasetType(datasetType));
		this.dmsProjectDao.save(dataset);
		return dataset;
	}

	@Test
	public void testGetDataSets() {
		final String studyName = "Study1";
		final String programUUID = UUID.randomUUID().toString();

		final DmsProject study = new DmsProject();
		study.setName(studyName);
		study.setDescription(studyName);
		study.setProgramUUID(programUUID);
		this.dmsProjectDao.save(study);

		final DmsProject summary = new DmsProject();
		summary.setName(studyName + " - Summary Dataset");
		summary.setDescription(studyName + " - Summary Dataset");
		summary.setProgramUUID(programUUID);
		summary.setDatasetType(new DatasetType(DatasetType.SUMMARY_DATA));
		this.dmsProjectDao.save(summary);

		final ProjectRelationship projectRelationship = new ProjectRelationship();
		projectRelationship.setTypeId(TermId.BELONGS_TO_STUDY.getId());
		projectRelationship.setObjectProject(study);
		projectRelationship.setSubjectProject(summary);
		this.projectRelationshipDao.save(projectRelationship);

		final List<DatasetDTO> datasets = this.dmsProjectDao.getDatasets(study.getProjectId());
		Assert.assertEquals(1, datasets.size());
		Assert.assertEquals(summary.getName(), datasets.get(0).getName());
		Assert.assertEquals(summary.getDatasetType().getDatasetTypeId(),  datasets.get(0).getDatasetTypeId());
	}

	private Integer createEnvironmentData(final String instanceNumber, final Integer locationId, final Optional<String> customAbbev, final Optional<Integer> blockId) {
		final Geolocation geolocation = new Geolocation();
		geolocation.setDescription(instanceNumber);
		this.geolocationDao.saveOrUpdate(geolocation);

		final GeolocationProperty prop = new GeolocationProperty();
		prop.setGeolocation(geolocation);
		prop.setType(TermId.LOCATION_ID.getId());
		prop.setRank(1);
		prop.setValue(locationId.toString());
		this.geolocPropDao.save(prop);

		if (customAbbev.isPresent()){
			final GeolocationProperty prop2 = new GeolocationProperty();
			prop2.setGeolocation(geolocation);
			prop2.setType(TermId.LOCATION_ABBR.getId());
			prop2.setRank(2);
			prop2.setValue(customAbbev.get());
			this.geolocPropDao.save(prop2);
		}

		if (blockId.isPresent()){
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
			stockModel.setName("Germplasm " + i);
			stockModel.setIsObsolete(false);
			stockModel.setTypeId(TermId.ENTRY_CODE.getId());
			stockModel.setUniqueName(String.valueOf(i));
			stockModel.setGermplasm(germplasm);
			this.stockDao.saveOrUpdate(stockModel);

			final ExperimentModel experimentModel = new ExperimentModel();
			experimentModel.setGeoLocation(geolocation);
			experimentModel.setTypeId(TermId.PLOT_EXPERIMENT.getId());
			experimentModel.setObsUnitId(RandomStringUtils.randomAlphabetic(13));
			experimentModel.setProject(this.study);
			experimentModel.setStock(stockModel);
			this.experimentDao.saveOrUpdate(experimentModel);
		}

		return geolocation.getLocationId();
	}



}
