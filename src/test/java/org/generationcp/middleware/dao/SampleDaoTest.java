package org.generationcp.middleware.dao;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.dao.dms.ExperimentDao;
import org.generationcp.middleware.dao.dms.GeolocationDao;
import org.generationcp.middleware.dao.dms.ProjectPropertyDao;
import org.generationcp.middleware.dao.dms.ProjectRelationshipDao;
import org.generationcp.middleware.dao.dms.StockDao;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.data.initializer.PersonTestDataInitializer;
import org.generationcp.middleware.data.initializer.SampleListTestDataInitializer;
import org.generationcp.middleware.data.initializer.SampleTestDataInitializer;
import org.generationcp.middleware.data.initializer.UserTestDataInitializer;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.sample.SampleDTO;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.Sample;
import org.generationcp.middleware.pojos.SampleList;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.dms.ProjectRelationship;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.google.common.collect.Ordering;

public class SampleDaoTest extends IntegrationTestBase {

	public static final String USER_NAME = "JohnDoe";
	public static final String USER_FIRST_NAME = "John";
	public static final String USER_LAST_NAME = "Doe";
	public static final Integer TEST_SAMPLE_RECORD_COUNT = 23;
	public static final String STUDY_NAME = "Study1";
	public static final String STUDY_DESCRIPTION = "Study Project";
	private static final String SAMPLE_LIST_NAME_FOR_PLOT_DATA = "PlotSampleList";
	private static final String SAMPLE_LIST_NAME_FOR_SUBOBSERVATION_DATA = "SubObsSampleList";

	private SampleListDao sampleListDao;
	private UserDAO userDao;
	private SampleDao sampleDao;
	private ExperimentDao experimentDao;
	private GeolocationDao geolocationDao;
	private StockDao stockDao;
	private PersonDAO personDAO;
	private DmsProjectDao dmsProjectDao;
	private GermplasmDAO germplasmDao;
	private ProjectRelationshipDao projectRelationshipDao;
	private ProjectPropertyDao projectPropertyDao;

	private DaoFactory daoFactory;

	private Integer ndExperimentId;

	private User user;

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

		this.projectRelationshipDao = new ProjectRelationshipDao();
		this.projectRelationshipDao.setSession(this.sessionProvder.getSession());

		this.projectPropertyDao = new ProjectPropertyDao();
		this.projectPropertyDao.setSession(this.sessionProvder.getSession());

		this.user = this.createUserForTesting();

	}

	@Test
	public void testCountFilter() {

		final Integer listId = this.createStudyWithPlot(user, SAMPLE_LIST_NAME_FOR_PLOT_DATA, TEST_SAMPLE_RECORD_COUNT);

		final Long countAllSample = this.sampleDao.countFilter(null, listId);

		Assert.assertEquals(TEST_SAMPLE_RECORD_COUNT.intValue(), countAllSample.intValue());

	}

	@Test
	public void testCountFilterWithObsUnitId() {

		final Integer listId = this.createStudyWithPlot(user, SAMPLE_LIST_NAME_FOR_PLOT_DATA, TEST_SAMPLE_RECORD_COUNT);

		final Long countAllSample = this.sampleDao.countFilter(this.ndExperimentId, listId);

		Assert.assertEquals(1, countAllSample.intValue());

	}

	@Test
	public void testFilterPagination() {

		final Pageable pageable = Mockito.mock(Pageable.class);
		Mockito.when(pageable.getPageSize()).thenReturn(10);

		final Integer listId = this.createStudyWithPlot(user, SAMPLE_LIST_NAME_FOR_PLOT_DATA, TEST_SAMPLE_RECORD_COUNT);

		// Page 1
		Mockito.when(pageable.getPageNumber()).thenReturn(0);
		final List<SampleDTO> result1 = this.sampleDao.filter(null, listId, pageable);
		Assert.assertEquals(10, result1.size());

		// Page 2
		Mockito.when(pageable.getPageNumber()).thenReturn(1);
		final List<SampleDTO> result2 = this.sampleDao.filter(null, listId, pageable);
		Assert.assertEquals(10, result2.size());

		// Page 3
		Mockito.when(pageable.getPageNumber()).thenReturn(2);
		final List<SampleDTO> result3 = this.sampleDao.filter(null, listId, pageable);
		Assert.assertEquals(3, result3.size());

	}

	@Test
	public void testFilterPaginationWithObsUnitId() {
		final Pageable pageable = Mockito.mock(Pageable.class);
		Mockito.when(pageable.getPageSize()).thenReturn(10);
		Mockito.when(pageable.getPageNumber()).thenReturn(0);

		final Integer listId = this.createStudyWithPlot(user, SAMPLE_LIST_NAME_FOR_PLOT_DATA, TEST_SAMPLE_RECORD_COUNT);

		final List<SampleDTO> result = this.sampleDao.filter(this.ndExperimentId, listId, pageable);
		Assert.assertEquals(1, result.size());
	}

	@Test
	public void testFilterForPlotObservationDataset() {
		final Pageable pageable = Mockito.mock(Pageable.class);
		Mockito.when(pageable.getPageSize()).thenReturn(10);
		Mockito.when(pageable.getPageNumber()).thenReturn(0);

		final Integer listId = this.createStudyWithPlot(user, SAMPLE_LIST_NAME_FOR_PLOT_DATA, TEST_SAMPLE_RECORD_COUNT);

		final List<SampleDTO> result = this.sampleDao.filter(this.ndExperimentId, listId, pageable);
		Assert.assertEquals(1, result.size());
		final SampleDTO sample = result.get(0);
		Assert.assertNotNull(sample.getSampleId());
		Assert.assertEquals(1, sample.getEntryNo().intValue());
		Assert.assertEquals("SAMPLE-" + SAMPLE_LIST_NAME_FOR_PLOT_DATA + ":" + 1, sample.getSampleName());
		Assert.assertEquals("BUSINESS-KEY-" + SAMPLE_LIST_NAME_FOR_PLOT_DATA + 1, sample.getSampleBusinessKey());
		Assert.assertEquals(SAMPLE_LIST_NAME_FOR_PLOT_DATA, sample.getSampleList());
		Assert.assertNotNull(sample.getGid());
		Assert.assertNotNull(sample.getDesignation());
		Assert.assertEquals("PLATEID-" + 1, sample.getPlateId());
		Assert.assertEquals("WELL-" + 1, sample.getWell());
		Assert.assertEquals(1, sample.getSampleNumber().intValue());
		Assert.assertNotNull(sample.getStudyId());
		Assert.assertNotNull(STUDY_NAME, sample.getStudyName());
		Assert.assertEquals(DataSetType.PLOT_DATA.getReadableName(), sample.getDatasetType());
		Assert.assertNotNull(USER_FIRST_NAME + " " + USER_LAST_NAME, sample.getTakenBy());
		Assert.assertNotNull(sample.getEnumerator());
	}

	@Test
	public void testFilterForSubObservationDataset() {
		final Pageable pageable = Mockito.mock(Pageable.class);
		Mockito.when(pageable.getPageSize()).thenReturn(10);
		Mockito.when(pageable.getPageNumber()).thenReturn(0);

		final Integer listId =
			this.createStudyWithPlotAndSubObservation(user, SAMPLE_LIST_NAME_FOR_SUBOBSERVATION_DATA, TEST_SAMPLE_RECORD_COUNT);

		final List<SampleDTO> result = this.sampleDao.filter(this.ndExperimentId, listId, pageable);
		Assert.assertEquals(1, result.size());
		final SampleDTO sample = result.get(0);
		Assert.assertNotNull(sample.getSampleId());
		Assert.assertEquals(1, sample.getEntryNo().intValue());
		Assert.assertEquals("SAMPLE-" + SAMPLE_LIST_NAME_FOR_SUBOBSERVATION_DATA + ":" + 1, sample.getSampleName());
		Assert.assertEquals("BUSINESS-KEY-" + SAMPLE_LIST_NAME_FOR_SUBOBSERVATION_DATA + 1, sample.getSampleBusinessKey());
		Assert.assertEquals(SAMPLE_LIST_NAME_FOR_SUBOBSERVATION_DATA, sample.getSampleList());
		Assert.assertNotNull(sample.getGid());
		Assert.assertNotNull(sample.getDesignation());
		Assert.assertEquals("PLATEID-" + 1, sample.getPlateId());
		Assert.assertEquals("WELL-" + 1, sample.getWell());
		Assert.assertEquals(1, sample.getSampleNumber().intValue());
		Assert.assertNotNull(sample.getStudyId());
		Assert.assertNotNull(STUDY_NAME, sample.getStudyName());
		Assert.assertEquals(DataSetType.PLANT_SUBOBSERVATIONS.getReadableName(), sample.getDatasetType());
		Assert.assertNotNull(USER_FIRST_NAME + " " + USER_LAST_NAME, sample.getTakenBy());
		Assert.assertNotNull(sample.getEnumerator());
	}

	@Test
	public void testFilterSortAscending() {

		final Integer listId = this.createStudyWithPlot(user, SAMPLE_LIST_NAME_FOR_PLOT_DATA, TEST_SAMPLE_RECORD_COUNT);

		final Pageable pageable = Mockito.mock(Pageable.class);
		Mockito.when(pageable.getPageSize()).thenReturn(TEST_SAMPLE_RECORD_COUNT);
		Mockito.when(pageable.getPageNumber()).thenReturn(0);
		final Sort.Order order = new Sort.Order(Sort.Direction.ASC, "sampleName");
		Mockito.when(pageable.getSort()).thenReturn(new Sort(order));

		final List<SampleDTO> queryResult = this.sampleDao.filter(null, listId, pageable);

		final List<String> result = new LinkedList<>();
		for (final SampleDTO sampleDTO : queryResult) {
			result.add(sampleDTO.getSampleName());
		}

		// Check if the sampleName is in ascending order
		Assert.assertTrue(Ordering.natural().isOrdered(result));

	}

	@Test
	public void testFilterSortDescending() {

		final Integer listId = this.createStudyWithPlot(user, SAMPLE_LIST_NAME_FOR_PLOT_DATA, TEST_SAMPLE_RECORD_COUNT);

		final Pageable pageable = Mockito.mock(Pageable.class);
		Mockito.when(pageable.getPageSize()).thenReturn(TEST_SAMPLE_RECORD_COUNT);
		Mockito.when(pageable.getPageNumber()).thenReturn(0);
		final Sort.Order order = new Sort.Order(Sort.Direction.DESC, "sampleName");
		Mockito.when(pageable.getSort()).thenReturn(new Sort(order));

		final List<SampleDTO> queryResult = this.sampleDao.filter(null, listId, pageable);

		final List<String> result = new LinkedList<>();
		for (final SampleDTO sampleDTO : queryResult) {
			result.add(sampleDTO.getSampleName());
		}

		// Check if the sampleName is in descending order
		Assert.assertTrue(Ordering.natural().reverse().isOrdered(result));

	}

	@Test
	public void testCountBySampleUIDs() {

		final Integer listId = this.createStudyWithPlot(user, SAMPLE_LIST_NAME_FOR_PLOT_DATA, TEST_SAMPLE_RECORD_COUNT);

		final Set<String> sampleUIDs = new HashSet<>();
		for (int i = 1; i < TEST_SAMPLE_RECORD_COUNT + 1; i++) {
			sampleUIDs.add("BUSINESS-KEY-" + SAMPLE_LIST_NAME_FOR_PLOT_DATA + i);
		}

		final Long count = this.sampleDao.countBySampleUIDs(sampleUIDs, listId);
		Assert.assertEquals(TEST_SAMPLE_RECORD_COUNT.intValue(), count.intValue());

	}

	@Test
	public void testGetBySampleBks() {

		final Integer listId = this.createStudyWithPlot(user, SAMPLE_LIST_NAME_FOR_PLOT_DATA, TEST_SAMPLE_RECORD_COUNT);

		final Set<String> sampleUIDs = new HashSet<>();
		for (int i = 1; i < TEST_SAMPLE_RECORD_COUNT + 1; i++) {
			sampleUIDs.add("BUSINESS-KEY-" + SAMPLE_LIST_NAME_FOR_PLOT_DATA + i);
		}
		final List<SampleDTO> sampleDtos = this.sampleDao.getBySampleBks(sampleUIDs);
		Assert.assertNotNull(sampleDtos);
		Assert.assertEquals(TEST_SAMPLE_RECORD_COUNT.intValue(), sampleDtos.size());

		final SampleDTO sample = sampleDtos.get(0);
		Assert.assertNotNull(sample.getSampleId());
		Assert.assertEquals("SAMPLE-" + SAMPLE_LIST_NAME_FOR_PLOT_DATA + ":" + 1, sample.getSampleName());
		Assert.assertEquals("BUSINESS-KEY-" + SAMPLE_LIST_NAME_FOR_PLOT_DATA + 1, sample.getSampleBusinessKey());
		Assert.assertEquals(SAMPLE_LIST_NAME_FOR_PLOT_DATA, sample.getSampleList());
		Assert.assertNotNull(sample.getGid());
		Assert.assertNotNull(sample.getDesignation());
		Assert.assertEquals(1, sample.getEntryNo().intValue());
		Assert.assertEquals("PLATEID-" + 1, sample.getPlateId());
		Assert.assertEquals("WELL-" + 1, sample.getWell());
		Assert.assertNull(sample.getSampleNumber());
		Assert.assertNull(sample.getStudyId());
		Assert.assertNull(sample.getStudyName());
		Assert.assertNull(sample.getDatasetType());
		Assert.assertNotNull(USER_FIRST_NAME + " " + USER_LAST_NAME, sample.getTakenBy());
		Assert.assertNull(sample.getEnumerator());
		Assert.assertNull(sample.getObservationUnitId());
		Assert.assertNotNull(sample.getSamplingDate());
		Assert.assertTrue(sample.getDatasets().isEmpty());
	}

	@Test
	public void testGetMaxSequenceNumber() {

		this.createStudyWithPlot(user, SAMPLE_LIST_NAME_FOR_PLOT_DATA, TEST_SAMPLE_RECORD_COUNT);

		final ExperimentModel experimentModel = this.experimentDao.getById(this.ndExperimentId);
		final Integer gid = experimentModel.getStock().getGermplasm().getGid();

		final Map<Integer, Integer> result = this.sampleDao.getMaxSequenceNumber(Arrays.asList(gid));
		Assert.assertFalse(result.isEmpty());
		Assert.assertEquals(1, result.get(gid).intValue());
	}

	@Test
	public void testGetMaxSampleNumber() {

		this.createStudyWithPlot(user, SAMPLE_LIST_NAME_FOR_PLOT_DATA, TEST_SAMPLE_RECORD_COUNT);

		final Map<Integer, Integer> result = this.sampleDao.getMaxSampleNumber(Arrays.asList(this.ndExperimentId));
		Assert.assertFalse(result.isEmpty());
		Assert.assertEquals(1, result.get(this.ndExperimentId).intValue());

	}

	private Integer createStudyWithPlot(final User user, String listName, final int sampleSize) {
		this.ndExperimentId = null;

		final DmsProject study = createDmsProject(STUDY_NAME, STUDY_DESCRIPTION, null, this.dmsProjectDao.getById(1));
		final DmsProject plotDataset = createDmsProject("PLOT DATASET", "PLOT DATASET DESCRIPTION", DataSetType.PLOT_DATA, study);
		final SampleList sampleListForPlotDataset =
			this.createExperimentsWithSampleList(listName, plotDataset, user, sampleSize);

		return sampleListForPlotDataset.getId();
	}

	private Integer createStudyWithPlotAndSubObservation(final User user, String listName, final int sampleSize) {
		this.ndExperimentId = null;

		final DmsProject study = createDmsProject(STUDY_NAME, STUDY_DESCRIPTION, null, this.dmsProjectDao.getById(1));
		final DmsProject plotDataset = createDmsProject("PLOT DATASET", "PLOT DATASET DESCRIPTION", DataSetType.PLOT_DATA, study);
		final DmsProject subObservationDataset =
			createDmsProject("SUB-OBSERVATION DATASET", "UB-OBSERVATION DATASET", DataSetType.PLANT_SUBOBSERVATIONS, plotDataset);
		final SampleList sampleListForSubObservation =
			this.createExperimentsWithSampleList(listName, subObservationDataset, user, sampleSize);

		return sampleListForSubObservation.getId();
	}

	private DmsProject createDmsProject(
		final String name, final String description, final DataSetType dataSetType, final DmsProject parent) {

		final DmsProject dmsProject = new DmsProject();
		dmsProject.setName(name);
		dmsProject.setDescription(description);
		this.dmsProjectDao.save(dmsProject);

		final ProjectRelationship plotDmsProjectToStudyProjectRelationship = new ProjectRelationship();
		plotDmsProjectToStudyProjectRelationship.setSubjectProject(dmsProject);
		plotDmsProjectToStudyProjectRelationship.setObjectProject(parent);
		plotDmsProjectToStudyProjectRelationship.setTypeId(TermId.BELONGS_TO_STUDY.getId());
		this.projectRelationshipDao.save(plotDmsProjectToStudyProjectRelationship);

		if (dataSetType != null) {
			final ProjectProperty datasetTypeProperty =
				new ProjectProperty(dmsProject, 1805, String.valueOf(dataSetType.getId()), 1, TermId.DATASET_TYPE.getId(), "");
			this.projectPropertyDao.save(datasetTypeProperty);
		}

		return dmsProject;
	}

	private SampleList createExperimentsWithSampleList(
		final String listName, final DmsProject dmsProject, final User user, final int sampleSize) {

		final SampleList sampleList = SampleListTestDataInitializer.createSampleList(user);
		sampleList.setListName(listName);
		sampleList.setDescription("DESCRIPTION-" + listName);
		this.sampleListDao.saveOrUpdate(sampleList);

		final Geolocation geolocation = new Geolocation();
		this.geolocationDao.saveOrUpdate(geolocation);

		// Create one sample for each experiment.
		for (int i = 1; i < sampleSize + 1; i++) {
			final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasm(1);
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
			experimentModel.setProject(dmsProject);
			experimentModel.setStock(stockModel);
			experimentModel.setObservationUnitNo(i);
			final ExperimentModel savedExperiment = this.experimentDao.saveOrUpdate(experimentModel);
			if (this.ndExperimentId == null) {
				this.ndExperimentId = savedExperiment.getNdExperimentId();
			}

			final Sample sample = SampleTestDataInitializer.createSample(sampleList, user);
			sample.setSampleName("SAMPLE-" + sampleList.getListName() + ":" + i);
			sample.setSampleBusinessKey("BUSINESS-KEY-" + sampleList.getListName() + i);
			sample.setEntryNumber(i);
			sample.setPlateId("PLATEID-" + i);
			sample.setWell("WELL-" + i);
			sample.setExperiment(experimentModel);
			sample.setSampleNumber(i);
			this.sampleDao.saveOrUpdate(sample);

		}

		return sampleList;

	}

	private User createUserForTesting() {

		final Person person = PersonTestDataInitializer.createPerson(SampleDaoTest.USER_FIRST_NAME, SampleDaoTest.USER_LAST_NAME);
		this.personDAO.saveOrUpdate(person);

		final User user = UserTestDataInitializer.createUser();
		user.setName(USER_NAME);
		user.setUserid(null);
		user.setPersonid(person.getId());
		this.userDao.saveOrUpdate(user);

		return user;

	}

}
