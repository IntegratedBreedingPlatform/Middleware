package org.generationcp.middleware.service.impl.study;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.dao.dms.ExperimentDao;
import org.generationcp.middleware.dao.dms.ExperimentPropertyDao;
import org.generationcp.middleware.dao.dms.GeolocationDao;
import org.generationcp.middleware.dao.dms.PhenotypeDao;
import org.generationcp.middleware.dao.dms.StockDao;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.data.initializer.CVTermTestDataInitializer;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.dms.DatasetType;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.ExperimentProperty;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.GeolocationProperty;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.service.api.study.StudyService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StudyServiceImplIntegrationTest extends IntegrationTestBase {

	private ExperimentDao experimentDao;
	private ExperimentPropertyDao experimentPropertyDao;
	private GeolocationDao geolocationDao;
	private StockDao stockDao;
	private DmsProjectDao dmsProjectDao;
	private GermplasmDAO germplasmDao;
	private PhenotypeDao phenotypeDao;
	private CVTermDao cvTermDao;

	private DaoFactory daoFactory;

	private StudyService studyService;

	private DmsProject study;
	private DmsProject plot;
	private CVTerm testTrait;

	@Before
	public void setUp() {
		this.daoFactory = new DaoFactory(this.sessionProvder);
		this.experimentDao = this.daoFactory.getExperimentDao();
		this.geolocationDao = this.daoFactory.getGeolocationDao();
		this.stockDao = this.daoFactory.getStockDao();
		this.dmsProjectDao = this.daoFactory.getDmsProjectDAO();
		this.germplasmDao = this.daoFactory.getGermplasmDao();
		this.experimentPropertyDao = new ExperimentPropertyDao();
		this.phenotypeDao = this.daoFactory.getPhenotypeDAO();
		this.experimentPropertyDao.setSession(this.sessionProvder.getSession());
		this.cvTermDao = this.daoFactory.getCvTermDao();

		this.studyService = new StudyServiceImpl(this.sessionProvder);
		this.study = this.createDmsProject("Study1", "Study-Description", null, this.dmsProjectDao.getById(1), null);
		this.plot = this.createDmsProject("Plot Dataset", "Plot Dataset-Description", this.study, this.study, DatasetTypeEnum.PLOT_DATA);
		this.testTrait = this.createTrait();
	}

	@Test
	public void testCountTotalObservationUnits() {

		final Geolocation geolocation = this.createTestGeolocation("1", 101);
		this.createTestExperiments(this.plot, geolocation, 5);

		Assert.assertEquals(5, this.studyService.countTotalObservationUnits(this.study.getProjectId(), geolocation.getLocationId()));
	}

	@Test
	public void testHasMeasurementDataEntered() {
		final Geolocation geolocation = this.createTestGeolocation("1", 101);
		final List<ExperimentModel> experimentModels = this.createTestExperiments(this.plot, geolocation, 5);

		Assert.assertFalse(
			this.studyService.hasMeasurementDataEntered(Arrays.asList(this.testTrait.getCvTermId()), this.study.getProjectId()));

		this.addPhenotypes(experimentModels, this.testTrait.getCvTermId(), RandomStringUtils.randomNumeric(5));
		// Need to flush session to sync with underlying database before querying
		this.sessionProvder.getSession().flush();
		Assert.assertTrue(
			this.studyService.hasMeasurementDataEntered(Arrays.asList(this.testTrait.getCvTermId()), this.study.getProjectId()));
	}

	@Test
	public void testHasMeasurementDataOnEnvironment() {
		final Geolocation geolocation = this.createTestGeolocation("1", 101);
		final List<ExperimentModel> experimentModels = this.createTestExperiments(this.plot, geolocation, 5);
		Assert.assertFalse(this.studyService.hasMeasurementDataOnEnvironment(this.study.getProjectId(), geolocation.getLocationId()));

		this.addPhenotypes(experimentModels, this.testTrait.getCvTermId(), RandomStringUtils.randomNumeric(5));
		// Need to flush session to sync with underlying database before querying
		this.sessionProvder.getSession().flush();
		Assert.assertTrue(this.studyService.hasMeasurementDataOnEnvironment(this.study.getProjectId(), geolocation.getLocationId()));
	}

	@Test
	public void testGetStudyInstances() {

		final DmsProject someStudy = this.createDmsProject("Study1", "Study-Description", null, this.dmsProjectDao.getById(1), null);
		final DmsProject someSummary =
			this.createDmsProject("Summary Dataset", "Summary Dataset-Description", someStudy, someStudy, DatasetTypeEnum.SUMMARY_DATA);

		final Geolocation instance1 = this.createTestGeolocation("1", 1);
		final Geolocation instance2 = this.createTestGeolocation("2", 2);

		this.createTestExperiment(someSummary, instance1, TermId.SUMMARY_EXPERIMENT.getId(), "0");
		this.createTestExperiment(someSummary, instance2, TermId.SUMMARY_EXPERIMENT.getId(), "0");

		final List<StudyInstance> studyInstances = this.studyService.getStudyInstances(someStudy.getProjectId());

		Assert.assertEquals(2, studyInstances.size());

		final StudyInstance studyInstance1 = studyInstances.get(0);

		Assert.assertEquals(instance1.getLocationId().intValue(), studyInstance1.getInstanceDbId());
		Assert.assertEquals(1, studyInstance1.getInstanceNumber());
		Assert.assertNull(studyInstance1.getCustomLocationAbbreviation());
		Assert.assertEquals("AFG", studyInstance1.getLocationAbbreviation());
		Assert.assertEquals("Afghanistan", studyInstance1.getLocationName());
		Assert.assertFalse(studyInstance1.isHasFieldmap());

		final StudyInstance studyInstance2 = studyInstances.get(1);

		Assert.assertEquals(instance2.getLocationId().intValue(), studyInstance2.getInstanceDbId());
		Assert.assertEquals(2, studyInstance2.getInstanceNumber());
		Assert.assertNull(studyInstance2.getCustomLocationAbbreviation());
		Assert.assertEquals("ALB", studyInstance2.getLocationAbbreviation());
		Assert.assertEquals("Albania", studyInstance2.getLocationName());
		Assert.assertFalse(studyInstance2.isHasFieldmap());

	}

	private void addPhenotypes(final List<ExperimentModel> experimentModels, final int traitId, final String value) {

		for (final ExperimentModel experimentModel : experimentModels) {
			final Phenotype phenotype = new Phenotype();
			phenotype.setObservableId(traitId);
			phenotype.setExperiment(experimentModel);
			phenotype.setValue(value);
			this.phenotypeDao.save(phenotype);
		}

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

	private Geolocation createTestGeolocation(final String trialNumber, final int locationId) {

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

	private List<ExperimentModel> createTestExperiments(final DmsProject project, final Geolocation geolocation,
		final int noOfExperiments) {

		final List<ExperimentModel> experimentModels = new ArrayList<>();

		for (int i = 1; i <= noOfExperiments; i++) {
			final ExperimentModel experimentModel =
				this.createTestExperiment(project, geolocation, TermId.PLOT_EXPERIMENT.getId(), String.valueOf(i));
			this.createTestStock(experimentModel);
		}

		return experimentModels;
	}

	private ExperimentModel createTestExperiment(final DmsProject project, final Geolocation geolocation, final int experimentType,
		final String value) {

		final ExperimentModel experimentModel = new ExperimentModel();
		experimentModel.setGeoLocation(geolocation);
		experimentModel.setTypeId(experimentType);
		experimentModel.setProject(project);
		experimentModel.setObservationUnitNo(1);
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

	private CVTerm createTrait() {
		final CVTerm trait = CVTermTestDataInitializer.createTerm(RandomStringUtils.randomAlphanumeric(50), CvId.VARIABLES.getId());
		this.cvTermDao.save(trait);
		return trait;
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

}
