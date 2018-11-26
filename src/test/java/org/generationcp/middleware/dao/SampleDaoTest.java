package org.generationcp.middleware.dao;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.dao.dms.ExperimentDao;
import org.generationcp.middleware.dao.dms.GeolocationDao;
import org.generationcp.middleware.dao.dms.StockDao;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.data.initializer.PersonTestDataInitializer;
import org.generationcp.middleware.data.initializer.PlantTestDataInitializer;
import org.generationcp.middleware.data.initializer.SampleListTestDataInitializer;
import org.generationcp.middleware.data.initializer.SampleTestDataInitializer;
import org.generationcp.middleware.data.initializer.UserTestDataInitializer;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.sample.SampleDTO;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.Plant;
import org.generationcp.middleware.pojos.Sample;
import org.generationcp.middleware.pojos.SampleList;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.google.common.collect.Ordering;

public class SampleDaoTest extends IntegrationTestBase {

	private static final String LIST_NAME = "TEST-LIST-FOR-SAMPLE-DAO-1";
	public static final String ADMIN = "Admin";
	public static final Integer TEST_SAMPLE_RECORD_COUNT = 23;

	private SampleListDao sampleListDao;
	private UserDAO userDao;
	private SampleDao sampleDao;
	private ExperimentDao experimentDao;
	private GeolocationDao geolocationDao;
	private StockDao stockDao;
	private PersonDAO personDAO;
	private DmsProjectDao dmsProjectDao;
	private GermplasmDAO germplasmDao;

	private Integer listId;

	private DaoFactory daoFactory;
	
	private String sampleObsUnitId;

	@Before
	public void setUp() throws Exception {
		this.daoFactory = new DaoFactory(this.sessionProvder);

		this.sampleListDao = this.daoFactory.getSampleListDao();
		this.userDao = this.daoFactory.getUserDao();
		this.sampleDao = this.daoFactory.getSampleDao();

		this.experimentDao = new ExperimentDao();
		this.experimentDao.setSession(this.sessionProvder.getSession());

		this.geolocationDao = new GeolocationDao();
		this.geolocationDao.setSession(this.sessionProvder.getSession());

		this.stockDao = new StockDao();
		this.stockDao.setSession(this.sessionProvder.getSession());

		this.personDAO = new PersonDAO();
		this.personDAO.setSession(this.sessionProvder.getSession());

		this.dmsProjectDao = new DmsProjectDao();
		this.dmsProjectDao.setSession(this.sessionProvder.getSession());
		
		this.germplasmDao = new GermplasmDAO();
		this.germplasmDao.setSession(this.sessionProvder.getSession());

		this.listId = this.createSampleListForFilter(LIST_NAME, false, TEST_SAMPLE_RECORD_COUNT);
	}

	@Test
	public void testCountFilter() {

		final Long countAllSample = this.sampleDao.countFilter(null, this.listId);

		Assert.assertEquals(TEST_SAMPLE_RECORD_COUNT.intValue(), countAllSample.intValue());

	}

	@Test
	public void testCountFilterWithObsUnitId() {

		final Long countAllSample = this.sampleDao.countFilter(this.sampleObsUnitId, this.listId);

		Assert.assertEquals(1, countAllSample.intValue());

	}

	@Test
	public void testFilterPagination() {

		final Pageable pageable = Mockito.mock(Pageable.class);
		Mockito.when(pageable.getPageSize()).thenReturn(10);

		// Page 1
		Mockito.when(pageable.getPageNumber()).thenReturn(0);
		final List<SampleDTO> result1 = this.sampleDao.filter(null, this.listId, pageable);
		Assert.assertEquals(10, result1.size());

		// Page 2
		Mockito.when(pageable.getPageNumber()).thenReturn(1);
		final List<SampleDTO> result2 = this.sampleDao.filter(null, this.listId, pageable);
		Assert.assertEquals(10, result2.size());

		// Page 3
		Mockito.when(pageable.getPageNumber()).thenReturn(2);
		final List<SampleDTO> result3 = this.sampleDao.filter(null, this.listId, pageable);
		Assert.assertEquals(3, result3.size());

	}

	@Test
	public void testFilterPaginationWithObsUnitId() {
		final Pageable pageable = Mockito.mock(Pageable.class);
		Mockito.when(pageable.getPageSize()).thenReturn(10);
		Mockito.when(pageable.getPageNumber()).thenReturn(0);
		final List<SampleDTO> result = this.sampleDao.filter(this.sampleObsUnitId, this.listId, pageable);
		Assert.assertEquals(1, result.size());
	}

	@Test
	public void testFilter() {
		final Pageable pageable = Mockito.mock(Pageable.class);
		Mockito.when(pageable.getPageSize()).thenReturn(10);
		Mockito.when(pageable.getPageNumber()).thenReturn(0);
		final List<SampleDTO> result = this.sampleDao.filter(this.sampleObsUnitId, this.listId, pageable);
		Assert.assertEquals(1, result.size());
		final SampleDTO sample = result.get(0);
		Assert.assertNotNull(sample.getSampleId());
		Assert.assertEquals("SAMPLE-" + LIST_NAME + 1, sample.getSampleName());
		Assert.assertEquals("BUSINESS-KEY-" + LIST_NAME + 1, sample.getSampleBusinessKey());
		Assert.assertEquals("TEST-LIST-FOR-SAMPLE-DAO-1", sample.getSampleList());
		Assert.assertEquals("0", sample.getPlantNumber().toString());
		Assert.assertEquals("PABCD", sample.getPlantBusinessKey());
		Assert.assertNotNull(sample.getGid());
		Assert.assertEquals("Germplasm 1", sample.getDesignation());
		Assert.assertEquals("PLATEID-1", sample.getPlateId());
		Assert.assertEquals("WELL-1", sample.getWell());
	}

	@Test
	public void testFilterWhereTakenByIsNull() {
		//Create a new sample list
		this.listId = this.createSampleListForFilter(LIST_NAME, true, TEST_SAMPLE_RECORD_COUNT);
		final Pageable pageable = Mockito.mock(Pageable.class);
		Mockito.when(pageable.getPageSize()).thenReturn(10);
		Mockito.when(pageable.getPageNumber()).thenReturn(0);
		final List<SampleDTO> result = this.sampleDao.filter(this.sampleObsUnitId, this.listId, pageable);
		Assert.assertEquals(1, result.size());
		final SampleDTO sample = result.get(0);
		Assert.assertNotNull(sample.getSampleId());
		Assert.assertEquals("SAMPLE-" + LIST_NAME + 1, sample.getSampleName());
		Assert.assertEquals("BUSINESS-KEY-" + LIST_NAME + 1, sample.getSampleBusinessKey());
		Assert.assertNull(sample.getTakenBy());
		Assert.assertEquals("TEST-LIST-FOR-SAMPLE-DAO-1", sample.getSampleList());
		Assert.assertEquals("0", sample.getPlantNumber().toString());
		Assert.assertEquals("PABCD", sample.getPlantBusinessKey());
		Assert.assertNotNull(sample.getGid());
		Assert.assertEquals("Germplasm 1", sample.getDesignation());
		Assert.assertEquals("PLATEID-1", sample.getPlateId());
		Assert.assertEquals("WELL-1", sample.getWell());
	}

	@Test
	public void testFilterSortAscending() {

		final Pageable pageable = Mockito.mock(Pageable.class);
		Mockito.when(pageable.getPageSize()).thenReturn(TEST_SAMPLE_RECORD_COUNT);
		Mockito.when(pageable.getPageNumber()).thenReturn(0);
		final Sort.Order order = new Sort.Order(Sort.Direction.ASC, "sampleName");
		Mockito.when(pageable.getSort()).thenReturn(new Sort(order));

		final List<SampleDTO> queryResult = this.sampleDao.filter(null, this.listId, pageable);

		final List<String> result = new LinkedList<>();
		for (final SampleDTO sampleDTO : queryResult) {
			result.add(sampleDTO.getSampleName());
		}

		// Check if the sampleName is in ascending order
		Assert.assertTrue(Ordering.natural().isOrdered(result));

	}

	@Test
	public void testFilterSortDescending() {

		final Pageable pageable = Mockito.mock(Pageable.class);
		Mockito.when(pageable.getPageSize()).thenReturn(TEST_SAMPLE_RECORD_COUNT);
		Mockito.when(pageable.getPageNumber()).thenReturn(0);
		final Sort.Order order = new Sort.Order(Sort.Direction.DESC, "sampleName");
		Mockito.when(pageable.getSort()).thenReturn(new Sort(order));

		final List<SampleDTO> queryResult = this.sampleDao.filter(null, this.listId, pageable);

		final List<String> result = new LinkedList<>();
		for (final SampleDTO sampleDTO : queryResult) {
			result.add(sampleDTO.getSampleName());
		}

		// Check if the sampleName is in descending order
		Assert.assertTrue(Ordering.natural().reverse().isOrdered(result));

	}

	@Test
	public void testCountBySampleUIDs() {

		final Set<String> sampleUIDs = new HashSet<>();
		for (int i = 1; i < TEST_SAMPLE_RECORD_COUNT + 1; i++) {
			sampleUIDs.add("BUSINESS-KEY-" + LIST_NAME + i);
		}

		final Long count = this.sampleDao.countBySampleUIDs(sampleUIDs, this.listId);
		Assert.assertEquals(TEST_SAMPLE_RECORD_COUNT.intValue(), count.intValue());

	}
	
	@Test
	public void testGetBySampleBks(){
		final Set<String> sampleUIDs = new HashSet<>();
		for (int i = 1; i < TEST_SAMPLE_RECORD_COUNT + 1; i++) {
			sampleUIDs.add("BUSINESS-KEY-" + LIST_NAME + i);
		}
		final List<SampleDTO> sampleDtos = this.sampleDao.getBySampleBks(sampleUIDs);
		Assert.assertNotNull(sampleDtos);
		Assert.assertEquals(TEST_SAMPLE_RECORD_COUNT.intValue(), sampleDtos.size());
	}

	private Integer createSampleListForFilter(final String listName, final boolean takenByIsNull, final int sampleSize) {
		this.sampleObsUnitId = "";
		
		final DmsProject project = new DmsProject();
		project.setName("Test Project");
		project.setDescription("Test Project");
		this.dmsProjectDao.save(project);

		User user = this.userDao.getUserByUserName(SampleListDaoTest.ADMIN);
		if (user == null) {
			final Person person = PersonTestDataInitializer.createPerson(SampleDaoTest.ADMIN, SampleDaoTest.ADMIN);
			this.personDAO.saveOrUpdate(person);

			user = UserTestDataInitializer.createUser();
			user.setName(ADMIN);
			user.setUserid(null);
			user.setPersonid(person.getId());
			this.userDao.saveOrUpdate(user);
		}

		final Geolocation geolocation = new Geolocation();
		this.geolocationDao.saveOrUpdate(geolocation);

		final SampleList sampleList = SampleListTestDataInitializer.createSampleList(user);
		sampleList.setListName(listName);
		sampleList.setDescription("DESCRIPTION-" + listName);

		this.sampleListDao.saveOrUpdate(sampleList);


		for (int i = 1; i < sampleSize + 1; i++) {
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
			experimentModel.setProject(project);
			experimentModel.setStock(stockModel);
			final ExperimentModel savedExperiment = this.experimentDao.saveOrUpdate(experimentModel);
			if (StringUtils.isEmpty(this.sampleObsUnitId)) {
				this.sampleObsUnitId = savedExperiment.getObsUnitId();
			}

			final Plant plant = PlantTestDataInitializer.createPlant();
			plant.setExperiment(experimentModel);

			final Sample sample = SampleTestDataInitializer.createSample(sampleList, plant, user);
			sample.setSampleName("SAMPLE-" + listName + i);
			sample.setSampleBusinessKey("BUSINESS-KEY-" + listName + i);
			sample.setEntryNumber(i);
			sample.setPlateId("PLATEID-" + i);
			sample.setWell("WELL-" + i);
			if (takenByIsNull)
				sample.setTakenBy(null);
			this.sampleDao.saveOrUpdate(sample);

		}

		return sampleList.getId();

	}

}
