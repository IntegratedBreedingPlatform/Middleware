package org.generationcp.middleware.utils.test;

import liquibase.util.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.api.crop.CropService;
import org.generationcp.middleware.api.crop.CropServiceImpl;
import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.NameDAO;
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
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmStudySource;
import org.generationcp.middleware.pojos.GermplasmStudySourceType;
import org.generationcp.middleware.pojos.InstanceExternalReference;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.Sample;
import org.generationcp.middleware.pojos.SampleList;
import org.generationcp.middleware.pojos.StudyExternalReference;
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
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.generationcp.middleware.pojos.oms.CVTermRelationship;
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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class IntegrationTestDataInitializer {

	private final ExperimentDao experimentDao;
	private final ExperimentPropertyDao experimentPropertyDao;
	private final GeolocationDao geolocationDao;
	private final GeolocationPropertyDao geolocationPropertyDao;
	private final StockDao stockDao;
	private final DmsProjectDao dmsProjectDao;
	private final GermplasmDAO germplasmDao;
	private final PhenotypeDao phenotypeDao;
	private final CVTermDao cvTermDao;
	private final SampleDao sampleDao;
	private final SampleListDao sampleListDao;
	private final ProjectPropertyDao projectPropertyDao;
	private final StudyTypeDAO studyTypeDAO;
	private final NameDAO nameDAO;

	private final WorkbenchDaoFactory workbenchDaoFactory;
	private final DaoFactory daoFactory;
	private final UserService userService;
	private final CropService cropService;

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
		this.cropService = new CropServiceImpl(workbenchSessionProvider);
		this.userService = new UserServiceImpl(workbenchSessionProvider);
		this.studyTypeDAO = new StudyTypeDAO();
		this.studyTypeDAO.setSession(hibernateSessionProvider.getSession());
		this.nameDAO = new NameDAO(hibernateSessionProvider.getSession());
	}

	public DmsProject createStudy(final String name, final String description, final int studyTypeId, final String programUUID,
		final String createdBy, final String startDate, final String endDate) {
		final DmsProject dmsProject = new DmsProject();
		dmsProject.setName(name);
		dmsProject.setDescription(description);
		dmsProject.setStudyType(this.studyTypeDAO.getById(studyTypeId));
		dmsProject.setProgramUUID(programUUID);
		dmsProject.setCreatedBy(createdBy);
		dmsProject.setStartDate(startDate);
		dmsProject.setEndDate(endDate);
		this.dmsProjectDao.save(dmsProject);
		this.dmsProjectDao.refresh(dmsProject);
		return dmsProject;
	}

	public StudyExternalReference createStudyExternalReference(final DmsProject study, final String referenceId,
		final String referenceSource) {
		final StudyExternalReference studyExternalReference = new StudyExternalReference();
		studyExternalReference.setStudy(study);
		studyExternalReference.setReferenceId(referenceId);
		studyExternalReference.setSource(referenceSource);
		this.daoFactory.getStudyExternalReferenceDAO().save(studyExternalReference);
		return studyExternalReference;
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

	public List<ExperimentModel> createTestExperimentsWithStock(final DmsProject study, final DmsProject dataset,
		final ExperimentModel parent,
		final Geolocation geolocation,
		final int noOfExperiments) {

		final List<ExperimentModel> experimentModels = new ArrayList<>();

		for (int i = 1; i <= noOfExperiments; i++) {
			final ExperimentModel experimentModel =
				this.createTestExperiment(dataset, geolocation, TermId.PLOT_EXPERIMENT.getId(), String.valueOf(i), parent);
			this.createTestStock(study, experimentModel);
			experimentModels.add(experimentModel);
		}

		return experimentModels;
	}

	public ExperimentModel createTestExperiment(final DmsProject project, final Geolocation geolocation, final int experimentType,
		final String value, final ExperimentModel parent) {
		return this.createTestExperiment(project, geolocation, experimentType, value, parent, false);
	}

	public ExperimentModel createTestExperiment(final DmsProject project, final Geolocation geolocation, final int experimentType,
		final String value, final ExperimentModel parent, final boolean addFieldmapProps) {

		final ExperimentModel experimentModel = new ExperimentModel();
		experimentModel.setGeoLocation(geolocation);
		experimentModel.setTypeId(experimentType);
		experimentModel.setProject(project);
		experimentModel.setObservationUnitNo(1);
		experimentModel.setParent(parent);
		this.experimentDao.saveOrUpdate(experimentModel);

		if (!StringUtils.isEmpty(value)) {
			this.saveExperimentProperty(experimentModel, TermId.PLOT_NO.getId(), value);
			if (addFieldmapProps) {
				this.saveExperimentProperty(experimentModel, TermId.FIELDMAP_COLUMN.getId(), "1");
				this.saveExperimentProperty(experimentModel, TermId.FIELDMAP_RANGE.getId(), "1");
			}
		}

		return experimentModel;
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

	public CVTerm createTrait(final String name) {
		return this.createCVTerm(name, CvId.VARIABLES.getId());
	}

	public CVTerm createCVTerm(final String name, final Integer cvId) {
		final CVTerm trait = CVTermTestDataInitializer.createTerm(name, cvId);
		this.cvTermDao.save(trait);
		return trait;
	}

	public CVTerm createCVTerm(final String name, final String definition, final Integer cvId) {
		final CVTerm trait = CVTermTestDataInitializer.createTerm(name, definition, cvId);
		this.cvTermDao.save(trait);
		return trait;
	}

	public StockModel createTestStock(final DmsProject study, final ExperimentModel experimentModel) {
		final StockModel stockModel = this.createTestStock(study);
		experimentModel.setStock(stockModel);
		return stockModel;

	}

	public StockModel createTestStock(final DmsProject study) {
		final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasm(1);
		germplasm.setGid(null);
		this.germplasmDao.save(germplasm);

		final Name germplasmName = GermplasmTestDataInitializer.createGermplasmName(germplasm.getGid());
		germplasmName.setTypeId(2);
		this.nameDAO.save(germplasmName);

		final StockModel stockModel = new StockModel();
		stockModel.setUniqueName("1");
		stockModel.setName("Germplasm " + RandomStringUtils.randomAlphanumeric(5));
		stockModel.setIsObsolete(false);
		stockModel.setGermplasm(germplasm);
		stockModel.setCross("-");
		stockModel.setProject(study);

		this.stockDao.saveOrUpdate(stockModel);
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

	public void addInstanceExternalReferenceSource(final Geolocation geolocation, final String externalReferenceId,
		final String externalReferenceSource) {
		final InstanceExternalReference instanceExternalReference = new InstanceExternalReference();
		instanceExternalReference.setInstance(geolocation);
		instanceExternalReference.setReferenceId(externalReferenceId);
		instanceExternalReference.setSource(externalReferenceSource);
		this.daoFactory.getStudyInstanceExternalReferenceDao().save(instanceExternalReference);
	}

	public void addExperimentProp(final ExperimentModel experimentModel, final int typeId, final String value, final int rank) {

		final ExperimentProperty experimentProperty = new ExperimentProperty();
		experimentProperty.setExperiment(experimentModel);
		experimentProperty.setTypeId(typeId);
		experimentProperty.setValue(value);
		experimentProperty.setRank(rank);
		this.experimentPropertyDao.save(experimentProperty);

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
		observationUnitsSearchDTO.setDatasetVariables(new ArrayList<MeasurementVariableDto>());
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
		project.setCropType(this.cropService.getCropTypeByName(CropType.CropEnum.MAIZE.toString()));
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
		crops.add(this.cropService.getCropTypeByName(CropType.CropEnum.MAIZE.toString()));
		this.userService.addUser(workbenchUser);

		return workbenchUser;

	}

	public GermplasmStudySource addGermplasmStudySource(final DmsProject study, final DmsProject plot, final Geolocation geolocation,
		final String plotNumber,
		final String replicationNumber) {
		final StockModel stockModel = this.createTestStock(study);

		final ExperimentModel experimentModel =
			this.createExperimentModel(plot, geolocation, ExperimentType.PLOT.getTermId(), stockModel);

		this.addExperimentProp(experimentModel, TermId.PLOT_NO.getId(), plotNumber, 1);
		this.addExperimentProp(experimentModel, TermId.REP_NO.getId(), replicationNumber, 1);

		final GermplasmStudySource germplasmStudySource =
			new GermplasmStudySource(stockModel.getGermplasm(), study, experimentModel,
				GermplasmStudySourceType.ADVANCE);
		this.daoFactory.getGermplasmStudySourceDAO().save(germplasmStudySource);

		return germplasmStudySource;
	}

	public CVTerm createVariableWithScale(final DataType dataType, final VariableType variableType) {
		final CVTerm variable = this.createTrait(RandomStringUtils.randomAlphabetic(20));
		final CVTerm scale = this.createCVTerm(RandomStringUtils.randomAlphabetic(20), CvId.SCALES.getId());
		this.daoFactory.getCvTermRelationshipDao()
			.save(new CVTermRelationship(TermId.HAS_SCALE.getId(), variable.getCvTermId(), scale.getCvTermId()));

		final CVTerm property = this.createCVTerm(RandomStringUtils.randomAlphabetic(20), CvId.PROPERTIES.getId());
		this.daoFactory.getCvTermRelationshipDao()
			.save(new CVTermRelationship(TermId.HAS_PROPERTY.getId(), variable.getCvTermId(), property.getCvTermId()));

		final CVTerm method = this.createCVTerm(RandomStringUtils.randomAlphabetic(20), CvId.METHODS.getId());
		this.daoFactory.getCvTermRelationshipDao()
			.save(new CVTermRelationship(TermId.HAS_METHOD.getId(), variable.getCvTermId(), method.getCvTermId()));

		this.daoFactory.getCvTermRelationshipDao()
			.save(new CVTermRelationship(TermId.HAS_TYPE.getId(), scale.getCvTermId(), dataType.getId()));
		this.daoFactory.getCvTermPropertyDao()
			.save(new CVTermProperty(TermId.VARIABLE_TYPE.getId(), variableType.getName(), 1, variable.getCvTermId()));

		return variable;
	}

	public CVTerm createCategoricalVariable(final VariableType variableType, final List<String> possibleValues) {
		final CVTerm variable = this.createTrait(RandomStringUtils.randomAlphabetic(20));
		final CVTerm scale = this.createCVTerm(RandomStringUtils.randomAlphabetic(20), CvId.SCALES.getId());
		this.daoFactory.getCvTermRelationshipDao()
			.save(new CVTermRelationship(TermId.HAS_SCALE.getId(), variable.getCvTermId(), scale.getCvTermId()));

		final CVTerm property = this.createCVTerm(RandomStringUtils.randomAlphabetic(20), CvId.PROPERTIES.getId());
		this.daoFactory.getCvTermRelationshipDao()
			.save(new CVTermRelationship(TermId.HAS_PROPERTY.getId(), variable.getCvTermId(), property.getCvTermId()));

		final CVTerm method = this.createCVTerm(RandomStringUtils.randomAlphabetic(20), CvId.METHODS.getId());
		this.daoFactory.getCvTermRelationshipDao()
			.save(new CVTermRelationship(TermId.HAS_METHOD.getId(), variable.getCvTermId(), method.getCvTermId()));

		this.daoFactory.getCvTermRelationshipDao()
			.save(new CVTermRelationship(TermId.HAS_TYPE.getId(), scale.getCvTermId(), DataType.CATEGORICAL_VARIABLE
				.getId()));
		this.daoFactory.getCvTermPropertyDao()
			.save(new CVTermProperty(TermId.VARIABLE_TYPE.getId(), variableType.getName(), 1, variable.getCvTermId()));

		for (final String value : possibleValues) {
			final CVTerm categoricalValue = this.createCVTerm(value, value, CvId.IBDB_TERMS.getId());
			this.daoFactory.getCvTermRelationshipDao()
				.save(new CVTermRelationship(TermId.HAS_VALUE.getId(), scale.getCvTermId(), categoricalValue.getCvTermId()));
		}

		return variable;
	}
}
