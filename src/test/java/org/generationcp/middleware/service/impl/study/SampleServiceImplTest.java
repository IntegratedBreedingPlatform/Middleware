package org.generationcp.middleware.service.impl.study;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.PersonDAO;
import org.generationcp.middleware.dao.SampleDao;
import org.generationcp.middleware.dao.SampleListDao;
import org.generationcp.middleware.dao.UserDAO;
import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.dao.dms.ExperimentDao;
import org.generationcp.middleware.dao.dms.ExperimentPropertyDao;
import org.generationcp.middleware.dao.dms.GeolocationDao;
import org.generationcp.middleware.dao.dms.ProjectPropertyDao;
import org.generationcp.middleware.dao.dms.StockDao;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.data.initializer.PersonTestDataInitializer;
import org.generationcp.middleware.data.initializer.SampleListTestDataInitializer;
import org.generationcp.middleware.data.initializer.SampleTestDataInitializer;
import org.generationcp.middleware.data.initializer.UserTestDataInitializer;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.domain.sample.SampleDetailsDTO;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.manager.DaoFactory;
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
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

public class SampleServiceImplTest extends IntegrationTestBase {

	private SampleListDao sampleListDao;
	private UserDAO userDao;
	private SampleDao sampleDao;
	private ExperimentDao experimentDao;
	private ExperimentPropertyDao experimentPropertyDao;
	private GeolocationDao geolocationDao;
	private StockDao stockDao;
	private PersonDAO personDAO;
	private DmsProjectDao dmsProjectDao;
	private GermplasmDAO germplasmDao;
	private ProjectPropertyDao projectPropertyDao;

	private DaoFactory daoFactory;

	@Before
	public void setUp() {
		this.daoFactory = new DaoFactory(this.sessionProvder);
		this.sampleListDao = this.daoFactory.getSampleListDao();
		this.userDao = this.daoFactory.getUserDao();
		this.sampleDao = this.daoFactory.getSampleDao();
		this.experimentDao = this.daoFactory.getExperimentDao();
		this.geolocationDao = this.daoFactory.getGeolocationDao();
		this.stockDao = this.daoFactory.getStockDao();
		this.personDAO = this.daoFactory.getPersonDAO();
		this.dmsProjectDao = this.daoFactory.getDmsProjectDAO();
		this.germplasmDao = this.daoFactory.getGermplasmDao();
		this.projectPropertyDao = this.daoFactory.getProjectPropertyDAO();
		this.experimentPropertyDao = new ExperimentPropertyDao();
		this.experimentPropertyDao.setSession(this.sessionProvder.getSession());
	}

	@Test
	public void testSampleDetailsDTO() {

		final User user = this.createTestUser();
		final DmsProject study = this.createDmsProject("Study1", "Study-Description", null, this.dmsProjectDao.getById(1), null);
		this.createProjectProperties(study);
		final DmsProject plot = this.createDmsProject("Plot Dataset", "Plot Dataset-Description", study, study, DatasetTypeEnum.PLOT_DATA);

		final ExperimentModel experimentModel = this.createTestExperiment(plot);
		this.createTestStock(experimentModel);

		final SampleList sampleList = this.createTestSampleList("List1", user);
		final Sample sample = this.createTestSample(sampleList, user, experimentModel);

		final SampleServiceImpl sampleService = new SampleServiceImpl(this.sessionProvder);

		final SampleDetailsDTO sampleDetailsDTO = sampleService.getSampleObservation(sample.getSampleBusinessKey());

		Assert.assertEquals("BUSINESS-KEY-1", sampleDetailsDTO.getSampleBusinessKey());
		Assert.assertEquals("John Doe", sampleDetailsDTO.getTakenBy());
		Assert.assertEquals("SAMPLE1", sampleDetailsDTO.getSampleName());
		Assert.assertEquals("Germplasm 1", sampleDetailsDTO.getDesignation());
		Assert.assertEquals(sampleDetailsDTO.getSampleDate(), sampleDetailsDTO.getSampleDate());
		Assert.assertEquals(1, sampleDetailsDTO.getEntryNo().intValue());
		Assert.assertEquals(1, sampleDetailsDTO.getPlotNo().intValue());
		Assert.assertNotNull(sampleDetailsDTO.getGid());
		Assert.assertEquals("20190101", sampleDetailsDTO.getSeedingDate());
		Assert.assertEquals("Wet", sampleDetailsDTO.getSeason());
		Assert.assertEquals(101, sampleDetailsDTO.getLocationDbId().intValue());

	}

	private DmsProject createDmsProject(final String name, final String description, final DmsProject study, final DmsProject parent,
		final DatasetTypeEnum datasetTypeEnum) {
		final DmsProject dmsProject = new DmsProject();
		dmsProject.setName(name);
		dmsProject.setDescription(description);
		dmsProject.setStudy(study);
		dmsProject.setParent(parent);
		if (datasetTypeEnum != null) {
			dmsProject.setDatasetType(new DatasetType(datasetTypeEnum.getId()));
		}
		this.dmsProjectDao.save(dmsProject);
		this.dmsProjectDao.refresh(dmsProject);
		return dmsProject;
	}

	private SampleList createTestSampleList(final String listName, final User user) {

		final SampleList sampleList = SampleListTestDataInitializer.createSampleList(user);
		sampleList.setListName(listName);
		sampleList.setDescription("DESCRIPTION-" + listName);

		this.sampleListDao.saveOrUpdate(sampleList);

		return sampleList;

	}

	private Sample createTestSample(final SampleList sampleList, final User user, final ExperimentModel experimentModel) {

		final Sample sample = SampleTestDataInitializer.createSample(sampleList, user);
		sample.setSampleName("SAMPLE1");
		sample.setSampleBusinessKey("BUSINESS-KEY-1");
		sample.setEntryNumber(1);
		sample.setExperiment(experimentModel);
		sample.setSampleNumber(1);
		sample.setPlateId("PLATEID");
		sample.setWell("WELLID");
		this.sampleDao.saveOrUpdate(sample);

		return sample;
	}

	private User createTestUser() {

		final Person person = PersonTestDataInitializer.createPerson();
		person.setFirstName("John");
		person.setLastName("Doe");
		this.personDAO.saveOrUpdate(person);

		final User user = UserTestDataInitializer.createUser();
		user.setName("John");
		user.setUserid(null);
		user.setPersonid(person.getId());
		user.setPerson(person);
		this.userDao.saveOrUpdate(user);

		return user;
	}

	private ExperimentModel createTestExperiment(final DmsProject project) {

		final ExperimentModel experimentModel = new ExperimentModel();
		final Geolocation geolocation = new Geolocation();

		final GeolocationProperty locationId = new GeolocationProperty();
		locationId.setValue("101");
		locationId.setRank(1);
		locationId.setType(TermId.LOCATION_ID.getId());
		locationId.setGeolocation(geolocation);

		geolocation.setProperties(Arrays.asList(locationId));
		this.geolocationDao.saveOrUpdate(geolocation);
		this.geolocationDao.refresh(geolocation);

		experimentModel.setGeoLocation(geolocation);
		experimentModel.setTypeId(TermId.PLOT_EXPERIMENT.getId());
		experimentModel.setProject(project);
		experimentModel.setObservationUnitNo(1);
		this.experimentDao.saveOrUpdate(experimentModel);

		final ExperimentProperty experimentProperty = new ExperimentProperty();
		experimentProperty.setExperiment(experimentModel);
		experimentProperty.setTypeId(TermId.PLOT_NO.getId());
		experimentProperty.setValue("1");
		experimentProperty.setRank(1);
		this.experimentPropertyDao.saveOrUpdate(experimentProperty);
		this.experimentDao.refresh(experimentModel);

		return experimentModel;

	}

	private StockModel createTestStock(final ExperimentModel experimentModel) {
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

		return stockModel;

	}

	private void createProjectProperties(final DmsProject dmsProject) {
		final ProjectProperty seedingDate = new ProjectProperty();
		seedingDate.setTypeId(VariableType.STUDY_DETAIL.getId());
		seedingDate.setValue("20190101");
		seedingDate.setProject(dmsProject);
		seedingDate.setRank(1);
		seedingDate.setVariableId(TermId.SEEDING_DATE.getId());
		this.projectPropertyDao.save(seedingDate);

		final ProjectProperty season = new ProjectProperty();
		season.setTypeId(VariableType.STUDY_DETAIL.getId());
		season.setValue("Wet");
		season.setProject(dmsProject);
		season.setRank(2);
		season.setVariableId(TermId.SEASON_VAR_TEXT.getId());
		this.projectPropertyDao.save(season);

		this.dmsProjectDao.refresh(dmsProject);

	}

}
