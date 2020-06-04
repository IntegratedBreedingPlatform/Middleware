package org.generationcp.middleware.utils.test;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.SampleDao;
import org.generationcp.middleware.dao.SampleListDao;
import org.generationcp.middleware.dao.StudyTypeDAO;
import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.dao.dms.ExperimentDao;
import org.generationcp.middleware.dao.dms.ExperimentPropertyDao;
import org.generationcp.middleware.dao.dms.GeolocationDao;
import org.generationcp.middleware.dao.dms.GeolocationPropertyDao;
import org.generationcp.middleware.dao.dms.PhenotypeDao;
import org.generationcp.middleware.dao.dms.ProjectPropertyDao;
import org.generationcp.middleware.dao.dms.StockDao;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.data.initializer.CVTermTestDataInitializer;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.data.initializer.SampleListTestDataInitializer;
import org.generationcp.middleware.data.initializer.SampleTestDataInitializer;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.manager.WorkbenchDataManagerImpl;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.Sample;
import org.generationcp.middleware.pojos.SampleList;
import org.generationcp.middleware.pojos.dms.DatasetType;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.ExperimentProperty;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.GeolocationProperty;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.RoleType;
import org.generationcp.middleware.pojos.workbench.UserRole;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.dataset.ObservationUnitsSearchDTO;
import org.generationcp.middleware.service.api.study.MeasurementVariableDto;
import org.generationcp.middleware.service.api.user.UserService;
import org.generationcp.middleware.service.impl.user.UserServiceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class IntegrationTestDataInitializer {

	private ExperimentDao experimentDao;
	private ExperimentPropertyDao experimentPropertyDao;
	private GeolocationDao geolocationDao;
	private GeolocationPropertyDao geolocationPropertyDao;
	private StockDao stockDao;
	private DmsProjectDao dmsProjectDao;
	private GermplasmDAO germplasmDao;
	private PhenotypeDao phenotypeDao;
	private CVTermDao cvTermDao;
	private SampleDao sampleDao;
	private SampleListDao sampleListDao;
	private ProjectPropertyDao projectPropertyDao;
	private StudyTypeDAO studyTypeDAO;

	private WorkbenchDaoFactory workbenchDaoFactory;
	private DaoFactory daoFactory;
	private UserService userService;
	private WorkbenchDataManager workbenchDataManager;

	public IntegrationTestDataInitializer(final HibernateSessionProvider hibernateSessionProvider,
		final HibernateSessionProvider workbenchSessionProvider) {
		this.workbenchDaoFactory = new WorkbenchDaoFactory(workbenchSessionProvider);
		this.daoFactory = new DaoFactory(hibernateSessionProvider);
		this.experimentDao = this.daoFactory.getExperimentDao();
		this.geolocationDao = this.daoFactory.getGeolocationDao();
		this.geolocationPropertyDao = this.daoFactory.getGeolocationPropertyDao();
		this.stockDao = this.daoFactory.getStockDao();
		this.dmsProjectDao = this.daoFactory.getDmsProjectDAO();
		this.germplasmDao = this.daoFactory.getGermplasmDao();
		this.experimentPropertyDao = new ExperimentPropertyDao();
		this.phenotypeDao = this.daoFactory.getPhenotypeDAO();
		this.experimentPropertyDao.setSession(hibernateSessionProvider.getSession());
		this.cvTermDao = this.daoFactory.getCvTermDao();
		this.sampleDao = this.daoFactory.getSampleDao();
		this.sampleListDao = this.daoFactory.getSampleListDao();
		this.projectPropertyDao = this.daoFactory.getProjectPropertyDAO();
		this.workbenchDataManager = new WorkbenchDataManagerImpl(workbenchSessionProvider);
		this.userService = new UserServiceImpl(workbenchSessionProvider);
		this.studyTypeDAO = new StudyTypeDAO();
		this.studyTypeDAO.setSession(hibernateSessionProvider.getSession());
	}

	public DmsProject createStudy(final String name, final String description, final int studyTypeId) {
		final DmsProject dmsProject = new DmsProject();
		dmsProject.setName(name);
		dmsProject.setDescription(description);
		dmsProject.setStudyType(this.studyTypeDAO.getById(studyTypeId));
		this.dmsProjectDao.save(dmsProject);
		this.dmsProjectDao.refresh(dmsProject);
		return dmsProject;
	}

	public DmsProject createDmsProject(final String name, final String description, final DmsProject study, final DmsProject parent,
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

	public Geolocation createTestGeolocation(final String trialNumber, final int locationId) {

		final Geolocation geolocation = new Geolocation();

		final GeolocationProperty geolocationPropertyLocationId = new GeolocationProperty();
		geolocationPropertyLocationId.setValue(String.valueOf(locationId));
		geolocationPropertyLocationId.setRank(1);
		geolocationPropertyLocationId.setType(TermId.LOCATION_ID.getId());
		geolocationPropertyLocationId.setGeolocation(geolocation);

		geolocation.setProperties(Arrays.asList(geolocationPropertyLocationId));
		geolocation.setDescription(trialNumber);
		this.geolocationDao.saveOrUpdate(geolocation);
		this.geolocationDao.refresh(geolocation);

		return geolocation;

	}

	public Geolocation createInstance(final DmsProject dmsProject, final String trialNumber, final int locationId) {
		final Geolocation geolocation = this.createTestGeolocation(trialNumber, locationId);
		this.createExperimentModel(dmsProject, geolocation, TermId.TRIAL_ENVIRONMENT_EXPERIMENT.getId(), null);
		return geolocation;
	}

	public List<ExperimentModel> createTestExperiments(final DmsProject project, final ExperimentModel parent,
		final Geolocation geolocation,
		final int noOfExperiments) {

		final List<ExperimentModel> experimentModels = new ArrayList<>();

		for (int i = 1; i <= noOfExperiments; i++) {
			final ExperimentModel experimentModel =
				this.createTestExperiment(project, geolocation, TermId.PLOT_EXPERIMENT.getId(), String.valueOf(i), parent);
			this.createTestStock(experimentModel);
			experimentModels.add(experimentModel);
		}

		return experimentModels;
	}

	public ExperimentModel createTestExperiment(final DmsProject project, final Geolocation geolocation, final int experimentType,
		final String value, final ExperimentModel parent) {

		final ExperimentModel experimentModel = new ExperimentModel();
		experimentModel.setGeoLocation(geolocation);
		experimentModel.setTypeId(experimentType);
		experimentModel.setProject(project);
		experimentModel.setObservationUnitNo(1);
		experimentModel.setParent(parent);
		this.experimentDao.saveOrUpdate(experimentModel);

		final ExperimentProperty experimentProperty = new ExperimentProperty();
		experimentProperty.setExperiment(experimentModel);
		experimentProperty.setTypeId(TermId.PLOT_NO.getId());
		experimentProperty.setValue(value);
		experimentProperty.setRank(1);
		this.experimentPropertyDao.saveOrUpdate(experimentProperty);
		this.experimentDao.refresh(experimentModel);

		return experimentModel;
	}

	public CVTerm createTrait(final String name) {
		final CVTerm trait = CVTermTestDataInitializer.createTerm(name, CvId.VARIABLES.getId());
		this.cvTermDao.save(trait);
		return trait;
	}

	public StockModel createTestStock(final ExperimentModel experimentModel) {
		final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasm(1);
		germplasm.setGid(null);
		this.germplasmDao.save(germplasm);

		final StockModel stockModel = new StockModel();
		stockModel.setUniqueName("1");
		stockModel.setTypeId(TermId.ENTRY_CODE.getId());
		stockModel.setName("Germplasm " + RandomStringUtils.randomAlphanumeric(5));
		stockModel.setIsObsolete(false);
		stockModel.setGermplasm(germplasm);

		this.stockDao.saveOrUpdate(stockModel);
		experimentModel.setStock(stockModel);

		return stockModel;

	}

	public SampleList createTestSampleList(final String listName, final Integer userId) {

		final SampleList sampleList = SampleListTestDataInitializer.createSampleList(userId);
		sampleList.setListName(listName);
		sampleList.setDescription("DESCRIPTION-" + listName);
		sampleList.setCreatedByUserId(userId);
		this.sampleListDao.saveOrUpdate(sampleList);
		return sampleList;

	}

	public List<Sample> addSamples(final List<ExperimentModel> experimentModels, final SampleList sampleList, final Integer userId) {

		final List<Sample> samples = new ArrayList<>();
		int i = 1;
		for (final ExperimentModel experimentModel : experimentModels) {
			final Sample sample = SampleTestDataInitializer.createSample(sampleList, userId);
			sample.setSampleName("SAMPLE-" + sampleList.getListName() + ":" + i);
			sample.setSampleBusinessKey("BUSINESS-KEY-" + sampleList.getListName() + i);
			sample.setEntryNumber(i);
			sample.setPlateId("PLATEID-" + i);
			sample.setWell("WELL-" + i);
			sample.setExperiment(experimentModel);
			sample.setSampleNumber(i);
			this.sampleDao.saveOrUpdate(sample);
			samples.add(sample);
			i++;
		}
		return samples;
	}

	public void addPhenotypes(final List<ExperimentModel> experimentModels, final int traitId, final String value) {

		for (final ExperimentModel experimentModel : experimentModels) {
			final Phenotype phenotype = new Phenotype();
			phenotype.setObservableId(traitId);
			phenotype.setExperiment(experimentModel);
			phenotype.setValue(value);
			phenotype.setDraftValue(value);
			this.phenotypeDao.save(phenotype);
		}

	}

	public void addProjectProp(final DmsProject project, final int variableId, final String alias, final VariableType variableType,
		final String value, final int rank) {

		final ProjectProperty projectProperty = new ProjectProperty();
		projectProperty.setVariableId(variableId);
		projectProperty.setProject(project);
		projectProperty.setRank(rank);
		projectProperty.setAlias(alias);
		projectProperty.setValue(value);
		projectProperty.setTypeId(variableType.getId());

		this.projectPropertyDao.save(projectProperty);
		this.projectPropertyDao.refresh(projectProperty);

	}

	public void addGeolocationProp(final Geolocation geolocation, final int type, final String value, final int rank) {

		final GeolocationProperty geolocationProperty = new GeolocationProperty();
		geolocationProperty.setValue(value);
		geolocationProperty.setRank(rank);
		geolocationProperty.setType(type);
		geolocationProperty.setGeolocation(geolocation);
		this.geolocationPropertyDao.save(geolocationProperty);
		this.geolocationPropertyDao.refresh(geolocationProperty);

	}

	public void addExperimentProp(final ExperimentModel experimentModel, final int typeId, final String value, final int rank) {

		final ExperimentProperty experimentProperty = new ExperimentProperty();
		experimentProperty.setExperiment(experimentModel);
		experimentProperty.setTypeId(typeId);
		experimentProperty.setValue(value);
		experimentProperty.setRank(rank);
		this.experimentPropertyDao.save(experimentProperty);
		this.experimentPropertyDao.refresh(experimentProperty);

	}

	public ExperimentModel createExperimentModel(final DmsProject dmsProject, final Geolocation geolocation, final Integer typeId,
		final StockModel stockModel) {

		final ExperimentModel experimentModel = new ExperimentModel();
		experimentModel.setGeoLocation(geolocation);
		experimentModel.setTypeId(typeId);
		experimentModel.setObsUnitId(RandomStringUtils.randomAlphabetic(13));
		experimentModel.setProject(dmsProject);
		experimentModel.setStock(stockModel);
		this.experimentDao.saveOrUpdate(experimentModel);

		return experimentModel;

	}

	public ObservationUnitsSearchDTO createTestObservationUnitsDTO() {
		final ObservationUnitsSearchDTO observationUnitsSearchDTO = new ObservationUnitsSearchDTO();
		observationUnitsSearchDTO.setSelectionMethodsAndTraits(new ArrayList<MeasurementVariableDto>());
		observationUnitsSearchDTO.setEnvironmentConditions(new ArrayList<MeasurementVariableDto>());
		observationUnitsSearchDTO.setAdditionalDesignFactors(new ArrayList<String>());
		observationUnitsSearchDTO.setGenericGermplasmDescriptors(new ArrayList<String>());
		return observationUnitsSearchDTO;
	}

	public Project createWorkbenchProject() {

		final String programUUID = UUID.randomUUID().toString();
		final Project project = new Project();
		project.setUserId(1);
		project.setProjectName("Test Project " + programUUID);
		project.setStartDate(new Date(System.currentTimeMillis()));
		project.setCropType(this.workbenchDataManager.getCropTypeByName(CropType.CropEnum.MAIZE.toString()));
		project.setLastOpenDate(new Date(System.currentTimeMillis()));
		project.setUniqueID(programUUID);

		this.workbenchDaoFactory.getProjectDAO().saveOrUpdate(project);
		this.workbenchDaoFactory.getProjectDAO().refresh(project);

		return project;

	}

	public WorkbenchUser createUserForTesting() {

		final Person person = new Person();
		person.setInstituteId(1);
		person.setFirstName("John");
		person.setMiddleName("M");
		final int randomNumber = new Random().nextInt();
		person.setLastName("Doe");
		person.setPositionName("King of Icewind Dale");
		person.setTitle("His Highness");
		person.setExtension("Ext");
		person.setFax("Fax");
		person.setEmail("lichking" + randomNumber + "@blizzard.com");
		person.setNotes("notes");
		person.setContact("Contact");
		person.setLanguage(1);
		person.setPhone("Phone");

		this.userService.addPerson(person);

		final Role role = new Role(1, "Admin");
		final RoleType roleType = new RoleType("INSTANCE");
		roleType.setId(1);
		role.setRoleType(roleType);

		final WorkbenchUser workbenchUser = new WorkbenchUser();
		workbenchUser.setInstalid(1);
		workbenchUser.setStatus(1);
		workbenchUser.setAccess(1);
		workbenchUser.setType(1);
		workbenchUser.setName("user_test" + new Random().nextInt());
		workbenchUser.setPassword("user_password");
		workbenchUser.setPerson(person);
		workbenchUser.setAssignDate(20150101);
		workbenchUser.setCloseDate(20150101);
		workbenchUser.setRoles(Arrays.asList(new UserRole(workbenchUser, role)));
		final List<CropType> crops = new ArrayList<>();
		crops.add(this.workbenchDataManager.getCropTypeByName(CropType.CropEnum.MAIZE.toString()));
		this.userService.addUser(workbenchUser);

		return workbenchUser;

	}
}
