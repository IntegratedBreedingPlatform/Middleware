/*******************************************************************************
 *
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.dao.dms;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.RandomStringUtils;
import org.generationcp.middleware.DataSetupTest;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.LocationDAO;
import org.generationcp.middleware.dao.PersonDAO;
import org.generationcp.middleware.dao.StudyTypeDAO;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.data.initializer.CVTermTestDataInitializer;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.data.initializer.InventoryDetailsTestDataInitializer;
import org.generationcp.middleware.data.initializer.LocationTestDataInitializer;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.domain.study.StudyEntrySearchDto;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.manager.ontology.daoElements.VariableFilter;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.dms.StockProperty;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.pojos.ims.TransactionStatus;
import org.generationcp.middleware.pojos.ims.TransactionType;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class StockDaoIntegrationTest extends IntegrationTestBase {

	private static final int TEST_COUNT = 3;
	private DmsProjectDao dmsProjectDao;
	private GermplasmDAO germplasmDao;
	private ExperimentDao experimentDao;
	private GeolocationDao geolocationDao;
	private StockDao stockDao;
	private StockPropertyDao stockPropertyDao;
	private CVTermDao cvtermDao;
	private PhenotypeDao phenotypeDao;
	private StudyTypeDAO studyTypeDAO;
	private PersonDAO personDao;
	private LocationDAO locationDAO;
	private DmsProject project;
	private List<StockModel> testStocks;
	private List<ExperimentModel> experiments;
	private Geolocation environment;
	private WorkbenchUser workbenchUser;
	private IntegrationTestDataInitializer testDataInitializer;
	private List<Integer> gids;
	private Location location;
	private Integer adminUserId;
	private DmsProject plot;
	private List<MeasurementVariable> germplasmDescriptors;

	private static final String GERMPLASM = "GERMPLSM";

	private static final String LIST = "LIST";

	@Autowired
	private InventoryDataManager inventoryDataManager;

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

		this.phenotypeDao = new PhenotypeDao();
		this.phenotypeDao.setSession(this.sessionProvder.getSession());

		this.stockPropertyDao = new StockPropertyDao();
		this.stockPropertyDao.setSession(this.sessionProvder.getSession());

		this.studyTypeDAO = new StudyTypeDAO();
		this.studyTypeDAO.setSession(this.sessionProvder.getSession());

		this.personDao = new PersonDAO();
		this.personDao.setSession(this.sessionProvder.getSession());

		this.locationDAO = new LocationDAO();
		this.locationDAO.setSession(this.sessionProvder.getSession());

		this.testDataInitializer = new IntegrationTestDataInitializer(this.sessionProvder, this.workbenchSessionProvider);
		this.workbenchUser = this.testDataInitializer.createUserForTesting();

		this.adminUserId = findAdminUser();
		this.project = this.createProject(null);

		this.testStocks= new ArrayList<>();
		this.experiments = new ArrayList<>();

		this.plot = this.testDataInitializer
			.createDmsProject(
				RandomStringUtils.randomAlphanumeric(10),RandomStringUtils.randomAlphanumeric(10), this.project,
				this.project,
				DatasetTypeEnum.PLOT_DATA);

		this.germplasmDescriptors = this.createGermplasmDescriptors(plot);

		this.createSampleStocks(TEST_COUNT, project);

	}

	private DmsProject createProject(final DmsProject parent) {
		final DmsProject project = new DmsProject();
		project.setName("Test Project Name " + RandomStringUtils.randomAlphanumeric(5));
		project.setDescription("Test Project " + RandomStringUtils.randomAlphanumeric(5));
		project.setStudyType(this.studyTypeDAO.getStudyTypeByName(StudyTypeDto.TRIAL_NAME));
		project.setProgramUUID(RandomStringUtils.randomAlphanumeric(20));
		project.setCreatedBy(this.workbenchUser.getUserid().toString());
		project.setLocked(true);
		if (parent != null) {
			project.setParent(parent);
			project.setStudy(parent);
		}
		dmsProjectDao.save(project);
		return project;
	}

	@Test
	public void testGetStockIdsByProperty_UsingDbxrefId() {
		final StockModel testStock = this.testStocks.get(0);
		final Integer gid = testStock.getGermplasm().getGid();
		final List<Integer> stockIds = this.stockDao.getStockIdsByProperty(StockDao.DBXREF_ID, gid.toString());
		Assert.assertNotNull(stockIds);
		Assert.assertEquals(testStock.getStockId(), stockIds.get(0));
	}

	@Test
	public void testGetStockIdsByProperty_UsingUniqueName() {
		final StockModel testStock = this.testStocks.get(0);
		final List<Integer> stockIds = this.stockDao.getStockIdsByProperty("uniqueName", testStock.getUniqueName());
		Assert.assertNotNull(stockIds);
		Assert.assertEquals(testStock.getStockId(), stockIds.get(0));
	}

	@Test
	public void testGetStockIdsByProperty_UsingName() {
		final StockModel testStock = this.testStocks.get(0);
		final List<Integer> stockIds = this.stockDao.getStockIdsByProperty("name", testStock.getName());
		Assert.assertNotNull(stockIds);
		Assert.assertEquals(testStock.getStockId(), stockIds.get(0));
	}

	@Test
	public void testGetStockIdsByProperty_UsingValue() {
		final StockModel testStock = this.testStocks.get(0);
		final List<Integer> stockIds = this.stockDao.getStockIdsByProperty("value", testStock.getValue());
		Assert.assertNotNull(stockIds);
		Assert.assertEquals(testStock.getStockId(), stockIds.get(0));
	}

	@Test
	public void testGetStocksByIds() {
		final List<Integer> ids = new ArrayList<>();
		for (final StockModel stock : this.testStocks){
			ids.add(stock.getStockId());
		}
		final Map<Integer, StockModel> stocksMap = this.stockDao.getStocksByIds(ids);
		Assert.assertEquals(TEST_COUNT, stocksMap.size());
		for (final StockModel stock : this.testStocks){
			Assert.assertEquals(stock, stocksMap.get(stock.getStockId()));
		}
	}

	@Test
	public void testCountStocks() {
		final CVTerm variateTerm = createVariate();
		for (final ExperimentModel experiment : experiments) {
			this.createTestObservations(experiment, variateTerm);
		}
		// Need to flush session to sync with underlying database before querying
		this.sessionProvder.getSession().flush();
		final long count = this.stockDao.countStocks(project.getProjectId(), environment.getLocationId(), variateTerm.getCvTermId());
		Assert.assertEquals(TEST_COUNT, count);
	}

	@Test
	public void testCountStudiesByGid() {
		final Germplasm germplasm = this.testStocks.get(0).getGermplasm();
		this.createTestStock(this.project, germplasm);
		final DmsProject study2 = this.createProject(null);
		this.createTestStock(study2, germplasm);


		// Need to flush session to sync with underlying database before querying
		this.sessionProvder.getSession().flush();
		final long count = this.stockDao.countStudiesByGids(Collections.singletonList(germplasm.getGid()));
		Assert.assertEquals(2, count);
	}

	@Test
	public void testGetStudiesByGid() {
		final Germplasm germplasm = this.testStocks.get(0).getGermplasm();
		this.createTestStock(this.project, germplasm);
		final DmsProject study2 = this.createProject(null);
		this.createTestStock(study2, germplasm);
		;

		// Need to flush session to sync with underlying database before querying
		this.sessionProvder.getSession().flush();
		final List<StudyReference> studies = this.stockDao.getStudiesByGid(germplasm.getGid());
		final ImmutableMap<Integer, StudyReference> resultsMap = Maps.uniqueIndex(studies, new Function<StudyReference, Integer>() {
			@Override
			public Integer apply(final StudyReference input) {
				return input.getId();
			}
		});
		Assert.assertEquals(2, resultsMap.size());
		final List<DmsProject> expectedStudies = Arrays.asList(project, study2);
		for (final DmsProject study : expectedStudies) {
			final Integer id = study.getProjectId();
			final StudyReference studyReference = resultsMap.get(id);
			Assert.assertNotNull(studyReference);
			Assert.assertEquals(id, studyReference.getId());
			Assert.assertEquals(study.getName(), studyReference.getName());
			Assert.assertEquals(study.getDescription(), studyReference.getDescription());
			Assert.assertEquals(study.getProgramUUID(), studyReference.getProgramUUID());
			Assert.assertEquals(study.getStudyType().getName(), studyReference.getStudyType().getName());
			Assert.assertEquals(this.workbenchUser.getUserid(), studyReference.getOwnerId());
			Assert.assertNull(studyReference.getOwnerName());
			Assert.assertTrue(studyReference.getIsLocked());
		}
	}

	@Test
	public void testGetPlotEntriesMap() {
		Map<Integer, StudyEntryDto> plotEntriesMap = this.stockDao.getPlotEntriesMap(this.project.getProjectId(), new HashSet<>(Arrays.asList(1, 2, 3, 4, 5)));

		final List<Integer> gids = this.testStocks.stream().map(s -> s.getGermplasm().getGid()).collect(Collectors.toList());
		for (final Map.Entry<Integer, StudyEntryDto> entry : plotEntriesMap.entrySet()) {
			final Integer plot = entry.getKey();
			Assert.assertEquals(DataSetupTest.GERMPLSM_PREFIX + plot, entry.getValue().getDesignation());
			Assert.assertTrue(gids.contains(entry.getValue().getGid()));
		}

		//Retrieve non existent plots in study
		plotEntriesMap = this.stockDao.getPlotEntriesMap(this.project.getProjectId(), new HashSet<>(Arrays.asList(51, 49)));
		Assert.assertTrue(plotEntriesMap.isEmpty());

	}

	@Test
	public void testGetStudyEntries_EntryProps() {
		final List<StudyEntryDto> studyEntryDtos = this.stockDao
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
			.createTransaction(20.0, TransactionStatus.CONFIRMED.getIntValue(), TransactionType.DEPOSIT.getValue(), lot1, adminUserId, 1, 1, LIST, TransactionType.DEPOSIT.getId());

		final Transaction transaction2 = InventoryDetailsTestDataInitializer
			.createTransaction(30.0, TransactionStatus.CONFIRMED.getIntValue(), TransactionType.DEPOSIT.getValue(), lot2, adminUserId, 1, 1, LIST, TransactionType.DEPOSIT.getId());

		final Transaction transaction3 = InventoryDetailsTestDataInitializer
			.createTransaction(10.0, TransactionStatus.CONFIRMED.getIntValue(), TransactionType.DEPOSIT.getValue(), lot3, adminUserId, 1, 1, LIST, TransactionType.DEPOSIT.getId());

		final Transaction transaction4 = InventoryDetailsTestDataInitializer
			.createTransaction(50.0, TransactionStatus.CONFIRMED.getIntValue(), TransactionType.DEPOSIT.getValue(), lot4, adminUserId, 1, 1, LIST, TransactionType.DEPOSIT.getId());

		this.inventoryDataManager.addLots(Lists.newArrayList(lot1, lot2, lot3, lot4));

		this.inventoryDataManager.addTransactions(Lists.newArrayList(transaction1, transaction2, transaction3, transaction4));

		//Assertions
		final List<StudyEntryDto> studyEntryDtos = this.stockDao
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
		final List<StudyEntryDto> studyEntryDtosSortedByGidsAsc = this.stockDao
			.getStudyEntries(new StudyEntrySearchDto(project.getProjectId(), new ArrayList<>(), new ArrayList<>(), null), sortedByGidsAscPageable);
		Assert.assertEquals(studyEntryDtosSortedByGidsAsc.size(), TEST_COUNT);
		assertThat(studyEntryDtosSortedByGidsAsc.get(0).getGid(), is(this.gids.get(0)));
		assertThat(studyEntryDtosSortedByGidsAsc.get(1).getGid(), is(this.gids.get(1)));
		assertThat(studyEntryDtosSortedByGidsAsc.get(2).getGid(), is(this.gids.get(2)));

		//Sort by gid desc
		final Pageable sortedByGidsDescPageable = new PageRequest(0, 20, new Sort(Sort.Direction.DESC, TermId.GID.name()));
		final List<StudyEntryDto> studyEntryDtosSortedByGidsDesc = this.stockDao
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
		final List<StudyEntryDto> studyEntryDtosFilterByGid = this.stockDao
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
		final List<StudyEntryDto> studyEntryDtosFilterByLotCount = this.stockDao
			.getStudyEntries(new StudyEntrySearchDto(project.getProjectId(), new ArrayList<>(), new ArrayList<>(), filterByLotCount), null);
		Assert.assertEquals(studyEntryDtosFilterByLotCount.size(), 1);
		assertThat(studyEntryDtosFilterByLotCount.get(0).getGid(), is(this.gids.get(2)));
	}

	private void createTestObservations(final ExperimentModel experiment, final CVTerm variateTerm) {
		final Phenotype phenotype = new Phenotype();
		phenotype.setObservableId(variateTerm.getCvTermId());
		phenotype.setValue(RandomStringUtils.randomNumeric(5));
		phenotype.setExperiment(experiment);
		phenotypeDao.save(phenotype);
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
		stockModel.setTypeId(TermId.ENTRY_CODE.getId());
		stockModel.setName(RandomStringUtils.randomAlphanumeric(10));
		stockModel.setIsObsolete(false);
		stockModel.setGermplasm(germplasm);
		stockModel.setValue(RandomStringUtils.randomAlphanumeric(5));
		stockModel.setProject(study);
		int i=1;
		for (final MeasurementVariable measurementVariable: this.germplasmDescriptors) {
			final StockProperty stockProperty = new StockProperty();
			stockProperty.setValue(String.valueOf(i));
			stockProperty.setTypeId(measurementVariable.getTermId());
			stockProperty.setStock(stockModel);
		}
		this.stockDao.saveOrUpdate(stockModel);
		this.testStocks.add(stockModel);
		return stockModel;
	}

	private void createTestExperiment(final DmsProject study, final StockModel stockModel) {
		final ExperimentModel experimentModel = new ExperimentModel();
		experimentModel.setGeoLocation(environment);
		experimentModel.setTypeId(TermId.PLOT_EXPERIMENT.getId());
		experimentModel.setProject(study);
		experimentModel.setStock(stockModel);
		experimentDao.saveOrUpdate(experimentModel);
		this.experiments.add(experimentModel);
	}

	private void createLocationForSearchLotTest() {
		final String programUUID = org.apache.commons.lang3.RandomStringUtils.randomAlphabetic(16);
		final int ltype = 405;
		final String labbr = org.apache.commons.lang3.RandomStringUtils.randomAlphabetic(7);
		final String lname = org.apache.commons.lang3.RandomStringUtils.randomAlphabetic(9);

		final int cntryid = 1;
		location = LocationTestDataInitializer.createLocation(null, lname, ltype, labbr);
		location.setCntryid(cntryid);

		final int provinceId = 1001;
		location.setSnl1id(provinceId);
		location.setLdefault(Boolean.FALSE);

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

		final MeasurementVariable m2 = new MeasurementVariable();
		m2.setTermId(descriptor2.getCvTermId());
		m2.setName(descriptor2.getName());
		m2.setDescription(descriptor2.getDefinition());
		m2.setVariableType( VariableType.GERMPLASM_DESCRIPTOR);

		return Lists.newArrayList(m1, m2);
	}
}
