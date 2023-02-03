package org.generationcp.middleware.dao;

import com.google.common.collect.Ordering;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.dms.*;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.data.initializer.SampleListTestDataInitializer;
import org.generationcp.middleware.data.initializer.SampleTestDataInitializer;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.sample.SampleDTO;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.Sample;
import org.generationcp.middleware.pojos.SampleList;
import org.generationcp.middleware.pojos.dms.*;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.utils.test.IntegrationTestDataInitializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.CollectionUtils;

import java.util.*;

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
	private SampleDao sampleDao;
	private ExperimentDao experimentDao;
	private GeolocationDao geolocationDao;
	private StockDao stockDao;
	private PersonDAO personDAO;
	private DmsProjectDao dmsProjectDao;
	private GermplasmDAO germplasmDao;
	private ProjectPropertyDao projectPropertyDao;
	private IntegrationTestDataInitializer testDataInitializer;

	private DaoFactory daoFactory;

	private Integer ndExperimentId;

	private WorkbenchUser workbenchUser;

	private DmsProject study;

	@Before
	public void setUp() throws Exception {
		this.daoFactory = new DaoFactory(this.sessionProvder);

		this.sampleListDao = this.daoFactory.getSampleListDao();
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

		this.germplasmDao = new GermplasmDAO(this.sessionProvder.getSession());

		this.projectPropertyDao = new ProjectPropertyDao();
		this.projectPropertyDao.setSession(this.sessionProvder.getSession());

		this.testDataInitializer = new IntegrationTestDataInitializer(this.sessionProvder, this.workbenchSessionProvider);

		this.workbenchUser = this.testDataInitializer.createUserForTesting();

		this.study = this.createDmsProject(STUDY_NAME, STUDY_DESCRIPTION, null, this.dmsProjectDao.getById(1), null);

	}

	@Test
	public void testCountFilter() {

		final Integer listId =
			this.createStudyWithPlot(this.study, this.workbenchUser, SAMPLE_LIST_NAME_FOR_PLOT_DATA, TEST_SAMPLE_RECORD_COUNT);

		final Long countAllSample = this.sampleDao.countFilter(null, listId);

		Assert.assertEquals(TEST_SAMPLE_RECORD_COUNT.intValue(), countAllSample.intValue());

	}

	@Test
	public void testCountFilterWithObsUnitId() {

		final Integer listId =
			this.createStudyWithPlot(this.study, this.workbenchUser, SAMPLE_LIST_NAME_FOR_PLOT_DATA, TEST_SAMPLE_RECORD_COUNT);

		final Long countAllSample = this.sampleDao.countFilter(this.ndExperimentId, listId);

		Assert.assertEquals(1, countAllSample.intValue());

	}

	@Test
	public void testFilterPagination() {

		final Pageable pageable = Mockito.mock(Pageable.class);
		Mockito.when(pageable.getPageSize()).thenReturn(10);

		final Integer listId =
			this.createStudyWithPlot(this.study, this.workbenchUser, SAMPLE_LIST_NAME_FOR_PLOT_DATA, TEST_SAMPLE_RECORD_COUNT);

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

		final Integer listId =
			this.createStudyWithPlot(this.study, this.workbenchUser, SAMPLE_LIST_NAME_FOR_PLOT_DATA, TEST_SAMPLE_RECORD_COUNT);

		final List<SampleDTO> result = this.sampleDao.filter(this.ndExperimentId, listId, pageable);
		Assert.assertEquals(1, result.size());
	}

	@Test
	public void testFilterForPlotObservationDataset() {
		final Pageable pageable = Mockito.mock(Pageable.class);
		Mockito.when(pageable.getPageSize()).thenReturn(10);
		Mockito.when(pageable.getPageNumber()).thenReturn(0);

		final Integer listId =
			this.createStudyWithPlot(this.study, this.workbenchUser, SAMPLE_LIST_NAME_FOR_PLOT_DATA, TEST_SAMPLE_RECORD_COUNT);

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
		Assert.assertEquals("PLOT", sample.getDatasetType());
		Assert.assertEquals(this.workbenchUser.getUserid(), sample.getTakenByUserId());
		Assert.assertNotNull(sample.getEnumerator());
	}

	@Test
	public void testFilterForSubObservationDataset() {
		final Pageable pageable = Mockito.mock(Pageable.class);
		Mockito.when(pageable.getPageSize()).thenReturn(10);
		Mockito.when(pageable.getPageNumber()).thenReturn(0);

		final Integer listId =
			this.createStudyWithPlotAndSubObservation(this.study, this.workbenchUser, SAMPLE_LIST_NAME_FOR_SUBOBSERVATION_DATA,
				TEST_SAMPLE_RECORD_COUNT);

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
		Assert.assertEquals("PLANT", sample.getDatasetType());
		Assert.assertEquals(this.workbenchUser.getUserid(), sample.getTakenByUserId());
		Assert.assertNotNull(sample.getEnumerator());
	}

	@Test
	public void testFilterSortAscending() {

		final Integer listId =
			this.createStudyWithPlot(this.study, this.workbenchUser, SAMPLE_LIST_NAME_FOR_PLOT_DATA, TEST_SAMPLE_RECORD_COUNT);

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

		final Integer listId =
			this.createStudyWithPlot(this.study, this.workbenchUser, SAMPLE_LIST_NAME_FOR_PLOT_DATA, TEST_SAMPLE_RECORD_COUNT);

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

		final Integer listId =
			this.createStudyWithPlot(this.study, this.workbenchUser, SAMPLE_LIST_NAME_FOR_PLOT_DATA, TEST_SAMPLE_RECORD_COUNT);

		final Set<String> sampleUIDs = new HashSet<>();
		for (int i = 1; i < TEST_SAMPLE_RECORD_COUNT + 1; i++) {
			sampleUIDs.add("BUSINESS-KEY-" + SAMPLE_LIST_NAME_FOR_PLOT_DATA + i);
		}

		final Long count = this.sampleDao.countBySampleUIDs(sampleUIDs, listId);
		Assert.assertEquals(TEST_SAMPLE_RECORD_COUNT.intValue(), count.intValue());

	}

	@Test
	public void testGetBySampleBks() {

		final Integer listId =
			this.createStudyWithPlot(this.study, this.workbenchUser, SAMPLE_LIST_NAME_FOR_PLOT_DATA, TEST_SAMPLE_RECORD_COUNT);

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
		Assert.assertEquals(this.workbenchUser.getUserid(), sample.getTakenByUserId());
		Assert.assertNull(sample.getEnumerator());
		Assert.assertNull(sample.getObservationUnitId());
		Assert.assertNotNull(sample.getSamplingDate());
		Assert.assertTrue(sample.getDatasets().isEmpty());
	}

	@Test
	public void testGetSamples() {
		final Pageable pageable = Mockito.mock(Pageable.class);
		Mockito.when(pageable.getPageSize()).thenReturn(10);
		Mockito.when(pageable.getPageNumber()).thenReturn(0);

		final Integer listId =
				this.createStudyWithPlot(this.study, this.workbenchUser, SAMPLE_LIST_NAME_FOR_PLOT_DATA, TEST_SAMPLE_RECORD_COUNT);

		final List<SampleDTO> samples = this.sampleDao.filter(this.ndExperimentId, listId, pageable);
		final List<SampleDTO> sampleDTOS = this.sampleDao.getSamples(listId, Collections.singletonList(samples.get(0).getSampleId()));


		final SampleDTO sample = sampleDTOS.get(0);
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
		Assert.assertEquals(this.workbenchUser.getUserid(), sample.getTakenByUserId());
		Assert.assertNull(sample.getEnumerator());
		Assert.assertNull(sample.getObservationUnitId());
		Assert.assertNotNull(sample.getSamplingDate());
		Assert.assertTrue(sample.getDatasets().isEmpty());
	}

	@Test
	public void testDeleteBySampleIds() {
		final Pageable pageable = Mockito.mock(Pageable.class);
		Mockito.when(pageable.getPageSize()).thenReturn(10);
		Mockito.when(pageable.getPageNumber()).thenReturn(0);

		final Integer listId =
				this.createStudyWithPlot(this.study, this.workbenchUser, SAMPLE_LIST_NAME_FOR_PLOT_DATA, TEST_SAMPLE_RECORD_COUNT);

		final List<SampleDTO> samples = this.sampleDao.filter(this.ndExperimentId, listId, pageable);
		List<SampleDTO> sampleDTOS = this.sampleDao.getSamples(listId, Collections.singletonList(samples.get(0).getSampleId()));
		Assert.assertFalse(CollectionUtils.isEmpty(sampleDTOS));
		this.sampleDao.deleteBySampleIds(listId, Collections.singletonList(sampleDTOS.get(0).getSampleId()));
		sampleDTOS = this.sampleDao.getSamples(listId, Collections.singletonList(samples.get(0).getSampleId()));
		Assert.assertFalse(CollectionUtils.isEmpty(sampleDTOS));
	}

	@Test
	public void testReOrderEntries() {
		final Pageable pageable = Mockito.mock(Pageable.class);
		Mockito.when(pageable.getPageSize()).thenReturn(10);
		Mockito.when(pageable.getPageNumber()).thenReturn(0);

		final Integer listId =
				this.createStudyWithPlot(this.study, this.workbenchUser, SAMPLE_LIST_NAME_FOR_PLOT_DATA, TEST_SAMPLE_RECORD_COUNT);

		List<SampleDTO> samples = this.sampleDao.filter(this.ndExperimentId, listId, pageable);
		final SampleDTO secondEntry = samples.get(1);
		Assert.assertEquals("1", samples.get(0).getEntryNo().toString());
		Assert.assertEquals("2", secondEntry.getEntryNo().toString());

		//delete the first entry
		this.sampleDao.deleteBySampleIds(listId, Collections.singletonList(samples.get(0).getSampleId()));
		this.sampleDao.reOrderEntries(listId);

		samples = this.sampleDao.filter(this.ndExperimentId, listId, pageable);
		Assert.assertEquals("1", samples.get(0).getEntryNo().toString());
		Assert.assertEquals(secondEntry.getSampleId(), samples.get(0).getSampleId());
	}

	@Test
	public void testGetMaxSequenceNumber() {

		this.createStudyWithPlot(this.study, this.workbenchUser, SAMPLE_LIST_NAME_FOR_PLOT_DATA, TEST_SAMPLE_RECORD_COUNT);

		final ExperimentModel experimentModel = this.experimentDao.getById(this.ndExperimentId);
		final Integer gid = experimentModel.getStock().getGermplasm().getGid();

		final Map<Integer, Integer> result = this.sampleDao.getMaxSequenceNumber(Arrays.asList(gid));
		Assert.assertFalse(result.isEmpty());
		Assert.assertEquals(1, result.get(gid).intValue());
	}

	@Test
	public void testGetMaxSampleNumber() {

		this.createStudyWithPlot(this.study, this.workbenchUser, SAMPLE_LIST_NAME_FOR_PLOT_DATA, TEST_SAMPLE_RECORD_COUNT);

		final Map<Integer, Integer> result = this.sampleDao.getMaxSampleNumber(Arrays.asList(this.ndExperimentId));
		Assert.assertFalse(result.isEmpty());
		Assert.assertEquals(1, result.get(this.ndExperimentId).intValue());

	}

	@Test
	public void testGetExperimentSampleMap() {

		this.createStudyWithPlot(this.study, this.workbenchUser, SAMPLE_LIST_NAME_FOR_PLOT_DATA, TEST_SAMPLE_RECORD_COUNT);
		final Map<Integer, String> experimentSampleMap = this.sampleDao.getExperimentSampleMap(this.study.getProjectId());

		Assert.assertEquals(TEST_SAMPLE_RECORD_COUNT, Integer.valueOf(experimentSampleMap.size()));
		// All experiments have only 1 sample each.
		for (final String value : experimentSampleMap.values()) {
			Assert.assertEquals("1", value);
		}

	}

	@Test
	public void testHasSamples() {

		this.createStudyWithPlot(this.study, this.workbenchUser, SAMPLE_LIST_NAME_FOR_PLOT_DATA, TEST_SAMPLE_RECORD_COUNT);
		Assert.assertTrue(this.sampleDao.hasSamples(this.study.getProjectId()));

		final DmsProject studyWithoutExperimentAndSamples =
			this.createDmsProject("Any Name", "Any Description", null, this.dmsProjectDao.getById(1), null);
		Assert.assertFalse(this.sampleDao.hasSamples(studyWithoutExperimentAndSamples.getProjectId()));

	}

	private Integer createStudyWithPlot(final DmsProject study, final WorkbenchUser user, final String listName, final int sampleSize) {
		this.ndExperimentId = null;

		final DmsProject plotDataset =
			this.createDmsProject("PLOT DATASET", "PLOT DATASET DESCRIPTION", DatasetTypeEnum.PLOT_DATA.getId(), study, study);
		final SampleList sampleListForPlotDataset =
			this.createExperimentsWithSampleList(listName, study, plotDataset, user, sampleSize);

		return sampleListForPlotDataset.getId();
	}

	private Integer createStudyWithPlotAndSubObservation(final DmsProject study, final WorkbenchUser user, final String listName,
		final int sampleSize) {
		this.ndExperimentId = null;
		final DmsProject plotDataset =
			this.createDmsProject("PLOT DATASET", "PLOT DATASET DESCRIPTION", DatasetTypeEnum.PLOT_DATA.getId(), study, study);
		final DmsProject subObservationDataset =
			this.createDmsProject("SUB-OBSERVATION DATASET", "UB-OBSERVATION DATASET", DatasetTypeEnum.PLANT_SUBOBSERVATIONS.getId(),
				plotDataset, study);
		final SampleList sampleListForSubObservation =
			this.createExperimentsWithSampleList(listName, study, subObservationDataset, user, sampleSize);

		return sampleListForSubObservation.getId();
	}

	private DmsProject createDmsProject(
		final String name, final String description, final Integer datasetTypeId, final DmsProject parent, final DmsProject study) {

		final DmsProject dmsProject = new DmsProject();
		dmsProject.setName(name);
		dmsProject.setDescription(description);
		dmsProject.setDatasetType((datasetTypeId != null) ? new DatasetType(datasetTypeId) : null);
		dmsProject.setParent(parent);
		if (study != null) {
			dmsProject.setStudy(study);
		}
		this.dmsProjectDao.save(dmsProject);

		return dmsProject;
	}

	private SampleList createExperimentsWithSampleList(
		final String listName, final DmsProject study, final DmsProject dataset, final WorkbenchUser user, final int sampleSize) {

		final SampleList sampleList = SampleListTestDataInitializer.createSampleList(user.getUserid());
		sampleList.setListName(listName);
		sampleList.setDescription("DESCRIPTION-" + listName);
		this.sampleListDao.saveOrUpdate(sampleList);

		final Geolocation geolocation = new Geolocation();
		this.geolocationDao.saveOrUpdate(geolocation);

		// Create one sample for each experiment.
		for (int i = 1; i < sampleSize + 1; i++) {
			final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasm(1);
			final Name name = new Name(null, germplasm, 1, 1, "Germplasm SP", 0, 0, 0);
			germplasm.getNames().add(name);
			this.germplasmDao.save(germplasm);
			this.daoFactory.getNameDao().save(name);

			final StockModel stockModel = new StockModel();
			stockModel.setIsObsolete(false);
			stockModel.setUniqueName(String.valueOf(i));
			stockModel.setGermplasm(germplasm);
			stockModel.setCross("-");
			stockModel.setProject(study);
			this.stockDao.saveOrUpdate(stockModel);

			final ExperimentModel experimentModel = new ExperimentModel();
			experimentModel.setGeoLocation(geolocation);
			experimentModel.setTypeId(TermId.PLOT_EXPERIMENT.getId());
			experimentModel.setProject(dataset);
			experimentModel.setStock(stockModel);
			experimentModel.setObservationUnitNo(i);
			final ExperimentModel savedExperiment = this.experimentDao.saveOrUpdate(experimentModel);
			if (this.ndExperimentId == null) {
				this.ndExperimentId = savedExperiment.getNdExperimentId();
			}

			final Sample sample = SampleTestDataInitializer.createSample(sampleList, user.getUserid());
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

}
