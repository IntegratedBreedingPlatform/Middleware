package org.generationcp.middleware.dao.study;

import com.google.common.collect.Lists;
import org.apache.commons.lang.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.CountryDAO;
import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.LocationDAO;
import org.generationcp.middleware.dao.StudyTypeDAO;
import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.dao.dms.ExperimentDao;
import org.generationcp.middleware.dao.dms.GeolocationDao;
import org.generationcp.middleware.dao.dms.StockDao;
import org.generationcp.middleware.dao.dms.StockPropertyDao;
import org.generationcp.middleware.dao.ims.LotDAO;
import org.generationcp.middleware.dao.ims.TransactionDAO;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.data.initializer.CVTermTestDataInitializer;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.data.initializer.InventoryDetailsTestDataInitializer;
import org.generationcp.middleware.data.initializer.LocationTestDataInitializer;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.domain.study.StudyEntrySearchDto;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.manager.ontology.daoElements.VariableFilter;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.dms.StockProperty;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.pojos.ims.TransactionStatus;
import org.generationcp.middleware.pojos.ims.TransactionType;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.service.api.study.StudyEntryDto;
import org.generationcp.middleware.utils.test.IntegrationTestDataInitializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class StudyEntrySearchDAOIntegrationTest extends IntegrationTestBase {

	private static final String LIST = "LIST";
	private static final String GERMPLASM = "GERMPLSM";
	private static final int TEST_COUNT = 3;

	private DmsProjectDao dmsProjectDao;
	private GermplasmDAO germplasmDao;
	private ExperimentDao experimentDao;
	private GeolocationDao geolocationDao;
	private StockDao stockDao;
	private StockPropertyDao stockPropertyDao;
	private CVTermDao cvtermDao;
	private StudyTypeDAO studyTypeDAO;
	private LocationDAO locationDAO;
	private CountryDAO countryDAO;
	private TransactionDAO transactionDAO;
	private LotDAO lotDAO;
	private StudyEntrySearchDAO studyEntrySearchDAO;

	private DmsProject project;
	private Geolocation environment;
	private List<Integer> gids;
	private Location location;
	private DmsProject plot;
	private List<MeasurementVariable> germplasmDescriptors;

	private IntegrationTestDataInitializer testDataInitializer;

	@Autowired
	private OntologyVariableDataManager ontologyVariableDataManager;

	@Before
	public void setUp() throws Exception {
		this.dmsProjectDao = new DmsProjectDao();
		this.dmsProjectDao.setSession(this.sessionProvder.getSession());

		this.germplasmDao = new GermplasmDAO(this.sessionProvder.getSession());

		this.experimentDao = new ExperimentDao();
		this.experimentDao.setSession(this.sessionProvder.getSession());

		this.geolocationDao = new GeolocationDao();
		this.geolocationDao.setSession(this.sessionProvder.getSession());

		this.stockDao = new StockDao();
		this.stockDao.setSession(this.sessionProvder.getSession());

		this.cvtermDao = new CVTermDao();
		this.cvtermDao.setSession(this.sessionProvder.getSession());

		this.stockPropertyDao = new StockPropertyDao();
		this.stockPropertyDao.setSession(this.sessionProvder.getSession());

		this.studyTypeDAO = new StudyTypeDAO();
		this.studyTypeDAO.setSession(this.sessionProvder.getSession());

		this.locationDAO = new LocationDAO(this.sessionProvder.getSession());

		this.countryDAO = new CountryDAO();
		this.countryDAO.setSession(this.sessionProvder.getSession());

		this.lotDAO = new LotDAO();
		this.lotDAO.setSession(this.sessionProvder.getSession());

		this.transactionDAO = new TransactionDAO();
		this.transactionDAO.setSession(this.sessionProvder.getSession());

		this.studyEntrySearchDAO = new StudyEntrySearchDAO(this.sessionProvder.getSession());

		this.testDataInitializer = new IntegrationTestDataInitializer(this.sessionProvder, this.workbenchSessionProvider);

		this.project = this.createProject(null);

		this.plot = this.testDataInitializer
			.createDmsProject(
				RandomStringUtils.randomAlphanumeric(10),RandomStringUtils.randomAlphanumeric(10), this.project,
				this.project,
				DatasetTypeEnum.PLOT_DATA);

		this.germplasmDescriptors = this.createGermplasmDescriptors(plot);

		this.createSampleStocks(TEST_COUNT, project);
	}

	@Test
	public void testGetStudyEntries_EntryProps() {
		final List<StudyEntryDto> studyEntryDtos = this.studyEntrySearchDAO
			.getStudyEntries(new StudyEntrySearchDto(project.getProjectId(), new ArrayList<>(), germplasmDescriptors, null), null);
		Assert.assertEquals(studyEntryDtos.size(), TEST_COUNT);
		for (final StudyEntryDto studyEntryDto: studyEntryDtos) {
			for (final MeasurementVariable measurementVariable: germplasmDescriptors) {
				Assert.assertEquals(Boolean.TRUE, studyEntryDto.getProperties().containsKey(measurementVariable.getTermId()));
			}
		}
	}

	@Test
	public void testGetStudyEntries_InventoryColumns() {
		//Get Units
		final VariableFilter unitFilter = new VariableFilter();
		unitFilter.addPropertyId(TermId.INVENTORY_AMOUNT_PROPERTY.getId());
		final List<Variable> units = ontologyVariableDataManager.getWithFilter(unitFilter);

		//Create location
		this.createLocationForSearchLotTest();

		//Create lots for the GID's
		final Integer gidMixed = this.gids.get(0);
		final Integer gidUniqueUnit = this.gids.get(1);

		//Create Inventory Data
		final Lot lot1 = InventoryDetailsTestDataInitializer
			.createLot(1, GERMPLASM, gidMixed, location.getLocid(), units.get(0).getId(), 0, 1, "Comments", RandomStringUtils
				.randomAlphabetic(35));

		final Lot lot2 = InventoryDetailsTestDataInitializer.createLot(1, GERMPLASM, gidMixed, location.getLocid(), units.get(1).getId(), 0, 1, "Comments", RandomStringUtils
			.randomAlphabetic(35));

		final Lot lot3 = InventoryDetailsTestDataInitializer.createLot(1, GERMPLASM, gidUniqueUnit,  location.getLocid(), units.get(0).getId(), 0, 1, "Comments", RandomStringUtils
			.randomAlphabetic(35));

		final Lot lot4 = InventoryDetailsTestDataInitializer.createLot(1, GERMPLASM, gidUniqueUnit,  location.getLocid(), units.get(0).getId(), 0, 1, "Comments", RandomStringUtils
			.randomAlphabetic(35));

		final Transaction transaction1 = InventoryDetailsTestDataInitializer
			.createTransaction(20.0, TransactionStatus.CONFIRMED.getIntValue(), TransactionType.DEPOSIT.getValue(), lot1, this.findAdminUser(), 1, 1, LIST, TransactionType.DEPOSIT.getId());

		final Transaction transaction2 = InventoryDetailsTestDataInitializer
			.createTransaction(30.0, TransactionStatus.CONFIRMED.getIntValue(), TransactionType.DEPOSIT.getValue(), lot2, this.findAdminUser(), 1, 1, LIST, TransactionType.DEPOSIT.getId());

		final Transaction transaction3 = InventoryDetailsTestDataInitializer
			.createTransaction(10.0, TransactionStatus.CONFIRMED.getIntValue(), TransactionType.DEPOSIT.getValue(), lot3, this.findAdminUser(), 1, 1, LIST, TransactionType.DEPOSIT.getId());

		final Transaction transaction4 = InventoryDetailsTestDataInitializer
			.createTransaction(50.0, TransactionStatus.CONFIRMED.getIntValue(), TransactionType.DEPOSIT.getValue(), lot4, this.findAdminUser(), 1, 1, LIST, TransactionType.DEPOSIT.getId());

		this.lotDAO.save(lot1);
		this.lotDAO.save(lot2);
		this.lotDAO.save(lot3);
		this.lotDAO.save(lot4);
		this.transactionDAO.save(transaction1);
		this.transactionDAO.save(transaction2);
		this.transactionDAO.save(transaction3);
		this.transactionDAO.save(transaction4);


		//Assertions
		final List<StudyEntryDto> studyEntryDtos = this.studyEntrySearchDAO
			.getStudyEntries(new StudyEntrySearchDto(project.getProjectId(), new ArrayList<>(), new ArrayList<>(), null), null);
		Assert.assertEquals(studyEntryDtos.size(), TEST_COUNT);

		final StudyEntryDto studyEntryDtoGidMixed = studyEntryDtos.stream().filter(i->i.getGid().equals(gidMixed)).findAny().get();
		Assert.assertEquals(studyEntryDtoGidMixed.getUnit(), "Mixed");
		Assert.assertEquals(studyEntryDtoGidMixed.getAvailableBalance(), "Mixed");
		Assert.assertEquals(studyEntryDtoGidMixed.getLotCount(), new Integer(2));

		final StudyEntryDto studyEntryDtoGidUnique = studyEntryDtos.stream().filter(i->i.getGid().equals(gidUniqueUnit)).findAny().get();
		Assert.assertEquals(studyEntryDtoGidUnique.getUnit(), units.get(0).getName());
		Assert.assertEquals(studyEntryDtoGidUnique.getAvailableBalance(), "60");
		Assert.assertEquals(studyEntryDtoGidUnique.getLotCount(), new Integer(2));

		//Sort by gid asc
		final Pageable sortedByGidsAscPageable = new PageRequest(0, 20, new Sort(Sort.Direction.ASC, TermId.GID.name()));
		final List<StudyEntryDto> studyEntryDtosSortedByGidsAsc = this.studyEntrySearchDAO
			.getStudyEntries(new StudyEntrySearchDto(project.getProjectId(), new ArrayList<>(), new ArrayList<>(), null), sortedByGidsAscPageable);
		Assert.assertEquals(studyEntryDtosSortedByGidsAsc.size(), TEST_COUNT);
		assertThat(studyEntryDtosSortedByGidsAsc.get(0).getGid(), is(this.gids.get(0)));
		assertThat(studyEntryDtosSortedByGidsAsc.get(1).getGid(), is(this.gids.get(1)));
		assertThat(studyEntryDtosSortedByGidsAsc.get(2).getGid(), is(this.gids.get(2)));

		//Sort by gid desc
		final Pageable sortedByGidsDescPageable = new PageRequest(0, 20, new Sort(Sort.Direction.DESC, TermId.GID.name()));
		final List<StudyEntryDto> studyEntryDtosSortedByGidsDesc = this.studyEntrySearchDAO
			.getStudyEntries(new StudyEntrySearchDto(project.getProjectId(), new ArrayList<>(), new ArrayList<>(), null), sortedByGidsDescPageable);
		Assert.assertEquals(studyEntryDtosSortedByGidsDesc.size(), TEST_COUNT);
		assertThat(studyEntryDtosSortedByGidsDesc.get(0).getGid(), is(this.gids.get(2)));
		assertThat(studyEntryDtosSortedByGidsDesc.get(1).getGid(), is(this.gids.get(1)));
		assertThat(studyEntryDtosSortedByGidsDesc.get(2).getGid(), is(this.gids.get(0)));

		//Filter by gid
		final StudyEntrySearchDto.Filter filterByGid = new StudyEntrySearchDto.Filter();
		filterByGid.setFilteredValues(new HashMap() {{
			put(String.valueOf(TermId.GID.getId()), Arrays.asList(gids.get(1)));
		}});
		filterByGid.setVariableTypeMap(new HashMap() {{
			put(String.valueOf(TermId.GID.getId()), null);
		}});
		final List<StudyEntryDto> studyEntryDtosFilterByGid = this.studyEntrySearchDAO
			.getStudyEntries(new StudyEntrySearchDto(project.getProjectId(), new ArrayList<>(), new ArrayList<>(), filterByGid), null);
		Assert.assertEquals(studyEntryDtosFilterByGid.size(), 1);
		assertThat(studyEntryDtosFilterByGid.get(0).getGid(), is(this.gids.get(1)));

		//Filter by lot count
		final StudyEntrySearchDto.Filter filterByLotCount = new StudyEntrySearchDto.Filter();
		filterByLotCount.setFilteredValues(new HashMap() {{
			put(String.valueOf(TermId.GID_ACTIVE_LOTS_COUNT.getId()), Arrays.asList(0));
		}});
		filterByLotCount.setVariableTypeMap(new HashMap() {{
			put(String.valueOf(TermId.GID_ACTIVE_LOTS_COUNT.getId()), null);
		}});
		final List<StudyEntryDto> studyEntryDtosFilterByLotCount = this.studyEntrySearchDAO
			.getStudyEntries(new StudyEntrySearchDto(project.getProjectId(), new ArrayList<>(), new ArrayList<>(), filterByLotCount), null);
		Assert.assertEquals(studyEntryDtosFilterByLotCount.size(), 1);
		assertThat(studyEntryDtosFilterByLotCount.get(0).getGid(), is(this.gids.get(2)));
	}


	private DmsProject createProject(final DmsProject parent) {
		final DmsProject project = new DmsProject();
		project.setName("Test Project Name " + RandomStringUtils.randomAlphanumeric(5));
		project.setDescription("Test Project " + RandomStringUtils.randomAlphanumeric(5));
		project.setStudyType(this.studyTypeDAO.getStudyTypeByName(StudyTypeDto.TRIAL_NAME));
		project.setProgramUUID(RandomStringUtils.randomAlphanumeric(20));
		project.setCreatedBy(this.findAdminUser().toString());
		project.setLocked(true);
		if (parent != null) {
			project.setParent(parent);
			project.setStudy(parent);
		}
		dmsProjectDao.save(project);
		return project;
	}

	private CVTerm createVariate() {
		final CVTerm variateTerm = CVTermTestDataInitializer.createTerm(RandomStringUtils.randomAlphanumeric(50), CvId.VARIABLES.getId());
		cvtermDao.save(variateTerm);
		return variateTerm;
	}

	private void createSampleStocks(final Integer count, final DmsProject study) {
		// Save the experiments in the same instance
		environment = new Geolocation();
		geolocationDao.saveOrUpdate(environment);
		this.gids = new ArrayList<>();

		for (int i = 0; i < count; i++) {
			final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasm(1);
			germplasm.setGid(null);
			this.germplasmDao.save(germplasm);
			this.germplasmDao.refresh(germplasm);
			this.gids.add(germplasm.getGid());

			final StockModel stockModel = createTestStock(study, germplasm);

			this.createTestExperiment(study, stockModel);
		}

	}

	private StockModel createTestStock(final DmsProject study, final Germplasm germplasm) {
		final StockModel stockModel = new StockModel();
		stockModel.setUniqueName(RandomStringUtils.randomAlphanumeric(10));
		stockModel.setName(RandomStringUtils.randomAlphanumeric(10));
		stockModel.setIsObsolete(false);
		stockModel.setGermplasm(germplasm);
		stockModel.setCross("-");
		stockModel.setProject(study);
		stockModel.setCross(RandomStringUtils.randomAlphanumeric(10));
		int i=1;
		final Set<StockProperty> properties = new HashSet<>();
		for (final MeasurementVariable measurementVariable: this.germplasmDescriptors) {
			final StockProperty stockProperty = new StockProperty(stockModel, measurementVariable.getTermId(), String.valueOf(i), null);
			properties.add(stockProperty);
		}
		stockModel.setProperties(properties);
		this.stockDao.saveOrUpdate(stockModel);
		return stockModel;
	}

	private void createTestExperiment(final DmsProject study, final StockModel stockModel) {
		final ExperimentModel experimentModel = new ExperimentModel();
		experimentModel.setGeoLocation(environment);
		experimentModel.setTypeId(TermId.PLOT_EXPERIMENT.getId());
		experimentModel.setProject(study);
		experimentModel.setStock(stockModel);
		experimentDao.saveOrUpdate(experimentModel);
	}

	private void createLocationForSearchLotTest() {
		final Country country = this.countryDAO.getById(1);

		final int ltype = 405;
		final String labbr = org.apache.commons.lang3.RandomStringUtils.randomAlphabetic(7);
		final String lname = org.apache.commons.lang3.RandomStringUtils.randomAlphabetic(9);

		location = LocationTestDataInitializer.createLocation(null, lname, ltype, labbr);
		location.setCountry(country);

		final Location province = this.locationDAO.getById(1001);
		location.setProvince(province);
		locationDAO.saveOrUpdate(location);
	}

	private List<MeasurementVariable> createGermplasmDescriptors(final DmsProject project) {
		final CVTerm descriptor1 = this.createVariate();
		final CVTerm descriptor2 = this.createVariate();

		this.testDataInitializer.addProjectProp(project, descriptor1.getCvTermId(), "alias1", VariableType.GERMPLASM_DESCRIPTOR,
			null, 1);
		this.testDataInitializer.addProjectProp(project, descriptor2.getCvTermId(), "alias2", VariableType.GERMPLASM_DESCRIPTOR,
			null, 1);

		final MeasurementVariable m1 = new MeasurementVariable();
		m1.setTermId(descriptor1.getCvTermId());
		m1.setName(descriptor1.getName());
		m1.setDescription(descriptor1.getDefinition());
		m1.setVariableType( VariableType.GERMPLASM_DESCRIPTOR);
		m1.setDataType(DataType.CATEGORICAL_VARIABLE.getName());

		final MeasurementVariable m2 = new MeasurementVariable();
		m2.setTermId(descriptor2.getCvTermId());
		m2.setName(descriptor2.getName());
		m2.setDescription(descriptor2.getDefinition());
		m2.setVariableType( VariableType.GERMPLASM_DESCRIPTOR);
		m2.setDataType(DataType.CATEGORICAL_VARIABLE.getName());

		return Lists.newArrayList(m1, m2);
	}

}