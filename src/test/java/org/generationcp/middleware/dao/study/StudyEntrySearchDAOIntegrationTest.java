package org.generationcp.middleware.dao.study;

import com.google.common.collect.Ordering;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.ims.LotDAO;
import org.generationcp.middleware.dao.ims.TransactionDAO;
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
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.GermplasmNameType;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.manager.ontology.daoElements.VariableFilter;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Name;
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
import org.generationcp.middleware.service.api.study.StudyEntryPropertyData;
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
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class StudyEntrySearchDAOIntegrationTest extends IntegrationTestBase {

	private static final String LIST = "LIST";
	private static final String GERMPLASM = "GERMPLSM";
	private static final int TEST_COUNT = 3;

	private DaoFactory daoFactory;
	private DmsProject project;
	private Geolocation environment;
	private List<Integer> gids;
	private Location location;
	private DmsProject plot;
	private List<MeasurementVariable> fixedEntryDescriptors;
	private List<MeasurementVariable> variableEntryDescriptors;

	private CVTerm customEntryDetailTerm1;
	private CVTerm customEntryDetailTerm2;

	private IntegrationTestDataInitializer testDataInitializer;

	@Autowired
	private OntologyVariableDataManager ontologyVariableDataManager;

	@Before
	public void setUp() throws Exception {
		this.daoFactory = new DaoFactory(this.sessionProvder);

		this.testDataInitializer = new IntegrationTestDataInitializer(this.sessionProvder, this.workbenchSessionProvider);

		this.project = this.createProject(null);

		this.plot = this.testDataInitializer
			.createDmsProject(
				RandomStringUtils.randomAlphanumeric(10), RandomStringUtils.randomAlphanumeric(10), this.project,
				this.project,
				DatasetTypeEnum.PLOT_DATA);

		this.fixedEntryDescriptors = this.createFixedEntryDescriptors(this.plot);
		this.variableEntryDescriptors = this.createVariableEntryDescriptors(this.plot);

		this.sessionProvder.getSession().flush();

		this.createSampleStocks(TEST_COUNT, this.project);
	}

	@Test
	public void testGetStudyEntries_EntryProps() {
		final List<StudyEntryDto> studyEntryDtos = this.daoFactory.getStudyEntrySearchDAO()
			.getStudyEntries(new StudyEntrySearchDto(this.project.getProjectId(), new ArrayList<>(), this.variableEntryDescriptors, null),
				null);
		Assert.assertEquals(TEST_COUNT, studyEntryDtos.size());
		for (final StudyEntryDto studyEntryDto : studyEntryDtos) {
			for (final Map.Entry<Integer, StudyEntryPropertyData> property : studyEntryDto.getProperties().entrySet()) {
				this.variableEntryDescriptors.stream().anyMatch(m -> m.getTermId() == property.getKey().intValue());
			}
		}
	}

	@Test
	public void testGetStudyEntries_InventoryColumns() {
		//Get Units
		final VariableFilter unitFilter = new VariableFilter();
		unitFilter.addPropertyId(TermId.INVENTORY_AMOUNT_PROPERTY.getId());
		final List<Variable> units = this.ontologyVariableDataManager.getWithFilter(unitFilter);

		//Create location
		this.createLocationForSearchLotTest();

		//Create lots for the GID's
		final Integer gidMixed = this.gids.get(0);
		final Integer gidUniqueUnit = this.gids.get(1);

		//Create Inventory Data
		final Lot lot1 = InventoryDetailsTestDataInitializer
			.createLot(1, GERMPLASM, gidMixed, this.location.getLocid(), units.get(0).getId(), 0, 1, "Comments", RandomStringUtils
				.randomAlphabetic(35));

		final Lot lot2 =
			InventoryDetailsTestDataInitializer.createLot(1, GERMPLASM, gidMixed, this.location.getLocid(), units.get(1).getId(), 0, 1,
				"Comments", RandomStringUtils
					.randomAlphabetic(35));

		final Lot lot3 =
			InventoryDetailsTestDataInitializer.createLot(1, GERMPLASM, gidUniqueUnit, this.location.getLocid(), units.get(0).getId(), 0, 1,
				"Comments", RandomStringUtils
					.randomAlphabetic(35));

		final Lot lot4 =
			InventoryDetailsTestDataInitializer.createLot(1, GERMPLASM, gidUniqueUnit, this.location.getLocid(), units.get(0).getId(), 0, 1,
				"Comments", RandomStringUtils
					.randomAlphabetic(35));

		final Transaction transaction1 = InventoryDetailsTestDataInitializer
			.createTransaction(20.0, TransactionStatus.CONFIRMED.getIntValue(), TransactionType.DEPOSIT.getValue(), lot1,
				this.findAdminUser(), 1, 1, LIST, TransactionType.DEPOSIT.getId());

		final Transaction transaction2 = InventoryDetailsTestDataInitializer
			.createTransaction(30.0, TransactionStatus.CONFIRMED.getIntValue(), TransactionType.DEPOSIT.getValue(), lot2,
				this.findAdminUser(), 1, 1, LIST, TransactionType.DEPOSIT.getId());

		final Transaction transaction3 = InventoryDetailsTestDataInitializer
			.createTransaction(10.0, TransactionStatus.CONFIRMED.getIntValue(), TransactionType.DEPOSIT.getValue(), lot3,
				this.findAdminUser(), 1, 1, LIST, TransactionType.DEPOSIT.getId());

		final Transaction transaction4 = InventoryDetailsTestDataInitializer
			.createTransaction(50.0, TransactionStatus.CONFIRMED.getIntValue(), TransactionType.DEPOSIT.getValue(), lot4,
				this.findAdminUser(), 1, 1, LIST, TransactionType.DEPOSIT.getId());

		final LotDAO lotDAO = this.daoFactory.getLotDao();
		lotDAO.save(lot1);
		lotDAO.save(lot2);
		lotDAO.save(lot3);
		lotDAO.save(lot4);
		final TransactionDAO transactionDAO = this.daoFactory.getTransactionDAO();
		transactionDAO.save(transaction1);
		transactionDAO.save(transaction2);
		transactionDAO.save(transaction3);
		transactionDAO.save(transaction4);

		//Assertions
		final List<StudyEntryDto> studyEntryDtos = this.daoFactory.getStudyEntrySearchDAO()
			.getStudyEntries(new StudyEntrySearchDto(this.project.getProjectId(), new ArrayList<>(), new ArrayList<>(), null), null);
		Assert.assertEquals(TEST_COUNT, studyEntryDtos.size());

		final StudyEntryDto studyEntryDtoGidMixed = studyEntryDtos.stream().filter(i -> i.getGid().equals(gidMixed)).findAny().get();
		Assert.assertEquals("Mixed", studyEntryDtoGidMixed.getUnit());
		Assert.assertEquals("Mixed", studyEntryDtoGidMixed.getAvailableBalance());
		Assert.assertEquals(studyEntryDtoGidMixed.getLotCount(), new Integer(2));

		final StudyEntryDto studyEntryDtoGidUnique = studyEntryDtos.stream().filter(i -> i.getGid().equals(gidUniqueUnit)).findAny().get();
		Assert.assertEquals(studyEntryDtoGidUnique.getUnit(), units.get(0).getName());
		Assert.assertEquals("60", studyEntryDtoGidUnique.getAvailableBalance());
		Assert.assertEquals(studyEntryDtoGidUnique.getLotCount(), new Integer(2));

		//Sort by gid asc
		final Pageable sortedByGidsAscPageable = new PageRequest(0, 20, new Sort(Sort.Direction.ASC, TermId.GID.name()));
		final List<StudyEntryDto> studyEntryDtosSortedByGidsAsc = this.daoFactory.getStudyEntrySearchDAO()
			.getStudyEntries(new StudyEntrySearchDto(this.project.getProjectId(), new ArrayList<>(), new ArrayList<>(), null),
				sortedByGidsAscPageable);
		Assert.assertEquals(TEST_COUNT, studyEntryDtosSortedByGidsAsc.size());
		assertThat(studyEntryDtosSortedByGidsAsc.get(0).getGid(), is(this.gids.get(0)));
		assertThat(studyEntryDtosSortedByGidsAsc.get(1).getGid(), is(this.gids.get(1)));
		assertThat(studyEntryDtosSortedByGidsAsc.get(2).getGid(), is(this.gids.get(2)));

		//Sort by gid desc
		final Pageable sortedByGidsDescPageable = new PageRequest(0, 20, new Sort(Sort.Direction.DESC, TermId.GID.name()));
		final List<StudyEntryDto> studyEntryDtosSortedByGidsDesc = this.daoFactory.getStudyEntrySearchDAO()
			.getStudyEntries(new StudyEntrySearchDto(this.project.getProjectId(), new ArrayList<>(), new ArrayList<>(), null),
				sortedByGidsDescPageable);
		Assert.assertEquals(TEST_COUNT, studyEntryDtosSortedByGidsDesc.size());
		assertThat(studyEntryDtosSortedByGidsDesc.get(0).getGid(), is(this.gids.get(2)));
		assertThat(studyEntryDtosSortedByGidsDesc.get(1).getGid(), is(this.gids.get(1)));
		assertThat(studyEntryDtosSortedByGidsDesc.get(2).getGid(), is(this.gids.get(0)));

		//Filter by lot count
		final StudyEntrySearchDto.Filter filterByLotCount = new StudyEntrySearchDto.Filter();
		filterByLotCount.setFilteredValues(new HashMap<String, List<String>>() {{
			this.put(String.valueOf(TermId.GID_ACTIVE_LOTS_COUNT.getId()), Arrays.asList("0"));
		}});
		filterByLotCount.setVariableTypeMap(new HashMap<String, String>() {{
			this.put(String.valueOf(TermId.GID_ACTIVE_LOTS_COUNT.getId()), null);
		}});
		final StudyEntrySearchDto studyEntrySearchDto =
			new StudyEntrySearchDto(this.project.getProjectId(), new ArrayList<>(), new ArrayList<>(), filterByLotCount);
		final long filteredCount =
			this.daoFactory.getStudyEntrySearchDAO().countFilteredStudyEntries(this.project.getProjectId(), studyEntrySearchDto);
		final List<StudyEntryDto> studyEntryDtosFilterByLotCount = this.daoFactory.getStudyEntrySearchDAO()
			.getStudyEntries(studyEntrySearchDto, null);
		Assert.assertEquals(filteredCount, studyEntryDtosFilterByLotCount.size());
		assertThat(studyEntryDtosFilterByLotCount.get(0).getGid(), is(this.gids.get(2)));

	}

	@Test
	public void testGetStudyEntries_FilterByEntryNo() {

		// Assert ORDER descending
		final List<StudyEntryDto> studyEntryDtosDescending = this.daoFactory.getStudyEntrySearchDAO()
			.getStudyEntries(new StudyEntrySearchDto(this.project.getProjectId(), new ArrayList<>(), this.variableEntryDescriptors, null),
				new PageRequest(0, 20, new Sort(Sort.Direction.DESC, "ENTRY_NO")));
		Assert.assertEquals(TEST_COUNT, studyEntryDtosDescending.size());
		Assert.assertTrue(Ordering.natural().reverse()
			.isOrdered(studyEntryDtosDescending.stream().map(StudyEntryDto::getEntryNumber).collect(Collectors.toList())));

		// Assert ORDER descending
		final List<StudyEntryDto> studyEntryDtosAscending = this.daoFactory.getStudyEntrySearchDAO()
			.getStudyEntries(new StudyEntrySearchDto(this.project.getProjectId(), new ArrayList<>(), this.variableEntryDescriptors, null),
				new PageRequest(0, 20, new Sort(Sort.Direction.ASC, "ENTRY_NO")));
		Assert.assertEquals(TEST_COUNT, studyEntryDtosAscending.size());
		Assert.assertTrue(
			Ordering.natural().isOrdered(studyEntryDtosAscending.stream().map(StudyEntryDto::getEntryNumber).collect(Collectors.toList())));

		// Filter by Entry No
		final StudyEntrySearchDto.Filter filterByEntryNo = new StudyEntrySearchDto.Filter();
		filterByEntryNo.setFilteredValues(new HashMap<String, List<String>>() {{
			this.put(String.valueOf(TermId.ENTRY_NO.getId()),
				Arrays.asList(String.valueOf(studyEntryDtosAscending.get(0).getEntryNumber())));
		}});
		filterByEntryNo.setVariableTypeMap(new HashMap<String, String>() {{
			this.put(String.valueOf(TermId.ENTRY_NO.getId()), VariableType.ENTRY_DETAIL.getName());
		}});

		final StudyEntrySearchDto studyEntrySearchDto =
			new StudyEntrySearchDto(this.project.getProjectId(), new ArrayList<>(), this.variableEntryDescriptors, filterByEntryNo);
		final long filteredCount =
			this.daoFactory.getStudyEntrySearchDAO().countFilteredStudyEntries(this.project.getProjectId(), studyEntrySearchDto);
		assertEquals(1, filteredCount);
		final List<StudyEntryDto> studyEntryDtosFilterByEntryNo = this.daoFactory.getStudyEntrySearchDAO()
			.getStudyEntries(studyEntrySearchDto, null);
		Assert.assertEquals(filteredCount, studyEntryDtosFilterByEntryNo.size());
		assertThat(studyEntryDtosFilterByEntryNo.get(0).getEntryNumber(), is(studyEntryDtosAscending.get(0).getEntryNumber()));

	}

	@Test
	public void testGetStudyEntries_FilterByEntryType() {

		// Assert ORDER descending
		final List<StudyEntryDto> studyEntryDtosDescending = this.daoFactory.getStudyEntrySearchDAO()
			.getStudyEntries(new StudyEntrySearchDto(this.project.getProjectId(), new ArrayList<>(), this.variableEntryDescriptors, null),
				new PageRequest(0, 20, new Sort(Sort.Direction.DESC, "ENTRY_TYPE")));
		Assert.assertEquals(TEST_COUNT, studyEntryDtosDescending.size());
		Assert.assertTrue(
			Ordering.natural().reverse().isOrdered(
				studyEntryDtosDescending.stream().map(e -> e.getProperties().get(TermId.ENTRY_TYPE.getId()).getValue())
					.collect(Collectors.toList())));

		// Assert ORDER ascending
		final List<StudyEntryDto> studyEntryDtosAscending = this.daoFactory.getStudyEntrySearchDAO()
			.getStudyEntries(new StudyEntrySearchDto(this.project.getProjectId(), new ArrayList<>(), this.variableEntryDescriptors, null),
				new PageRequest(0, 20, new Sort(Sort.Direction.ASC, "ENTRY_TYPE")));
		Assert.assertEquals(TEST_COUNT, studyEntryDtosAscending.size());
		Assert.assertTrue(
			Ordering.natural().isOrdered(
				studyEntryDtosAscending.stream().map(e -> e.getProperties().get(TermId.ENTRY_TYPE.getId()).getValue())
					.collect(Collectors.toList())));

		final String entryTypeSearchString = studyEntryDtosAscending.get(0).getProperties().get(TermId.ENTRY_TYPE.getId()).getValue();

		// Filter by Entry Type
		final StudyEntrySearchDto.Filter filterByEntryType = new StudyEntrySearchDto.Filter();
		filterByEntryType.setFilteredValues(new HashMap<String, List<String>>() {{
			this.put(String.valueOf(TermId.ENTRY_TYPE.getId()),
				Arrays.asList(entryTypeSearchString));
		}});
		filterByEntryType.setVariableTypeMap(new HashMap<String, String>() {{
			this.put(String.valueOf(TermId.ENTRY_TYPE.getId()), VariableType.ENTRY_DETAIL.name());
		}});

		final StudyEntrySearchDto studyEntrySearchDto =
			new StudyEntrySearchDto(this.project.getProjectId(), new ArrayList<>(), this.variableEntryDescriptors, filterByEntryType);
		final long filteredCount =
			this.daoFactory.getStudyEntrySearchDAO().countFilteredStudyEntries(this.project.getProjectId(), studyEntrySearchDto);
		assertEquals(1, filteredCount);
		final List<StudyEntryDto> studyEntryDtosFilterByEntryType = this.daoFactory.getStudyEntrySearchDAO()
			.getStudyEntries(studyEntrySearchDto, null);
		Assert.assertEquals(filteredCount, studyEntryDtosFilterByEntryType.size());
		assertThat(studyEntryDtosFilterByEntryType.get(0).getProperties().get(TermId.ENTRY_TYPE.getId()).getValue(),
			is(entryTypeSearchString));

	}

	@Test
	public void testGetStudyEntries_FilterByGID() {

		// Assert ORDER descending
		final List<StudyEntryDto> studyEntryDtosDescending = this.daoFactory.getStudyEntrySearchDAO()
			.getStudyEntries(new StudyEntrySearchDto(this.project.getProjectId(), new ArrayList<>(), this.variableEntryDescriptors, null),
				new PageRequest(0, 20, new Sort(Sort.Direction.DESC, "GID")));
		Assert.assertEquals(TEST_COUNT, studyEntryDtosDescending.size());
		Assert.assertTrue(
			Ordering.natural().reverse().isOrdered(
				studyEntryDtosDescending.stream().map(StudyEntryDto::getGid)
					.collect(Collectors.toList())));

		// Assert ORDER ascending
		final List<StudyEntryDto> studyEntryDtosAscending = this.daoFactory.getStudyEntrySearchDAO()
			.getStudyEntries(new StudyEntrySearchDto(this.project.getProjectId(), new ArrayList<>(), this.variableEntryDescriptors, null),
				new PageRequest(0, 20, new Sort(Sort.Direction.ASC, "GID")));
		Assert.assertEquals(TEST_COUNT, studyEntryDtosAscending.size());
		Assert.assertTrue(
			Ordering.natural().isOrdered(
				studyEntryDtosAscending.stream().map(StudyEntryDto::getGid)
					.collect(Collectors.toList())));

		// Filter by GID
		final StudyEntrySearchDto.Filter filterByGID = new StudyEntrySearchDto.Filter();
		filterByGID.setFilteredValues(new HashMap<String, List<String>>() {{
			this.put(String.valueOf(TermId.GID.getId()), Arrays.asList(String.valueOf(studyEntryDtosAscending.get(0).getGid())));
		}});
		filterByGID.setVariableTypeMap(new HashMap<String, String>() {{
			this.put(String.valueOf(TermId.GID.getId()), null);
		}});

		final StudyEntrySearchDto studyEntrySearchDto =
			new StudyEntrySearchDto(this.project.getProjectId(), new ArrayList<>(), this.variableEntryDescriptors, filterByGID);
		final long filteredCount =
			this.daoFactory.getStudyEntrySearchDAO().countFilteredStudyEntries(this.project.getProjectId(), studyEntrySearchDto);
		assertEquals(1, filteredCount);
		final List<StudyEntryDto> studyEntryDtosFilterByGID = this.daoFactory.getStudyEntrySearchDAO()
			.getStudyEntries(studyEntrySearchDto, null);
		Assert.assertEquals(filteredCount, studyEntryDtosFilterByGID.size());
		assertThat(studyEntryDtosFilterByGID.get(0).getGid(), is(studyEntryDtosAscending.get(0).getGid()));

	}

	@Test
	public void testGetStudyEntries_FilterByGUID() {

		// Assert ORDER descending
		final List<StudyEntryDto> studyEntryDtosDescending = this.daoFactory.getStudyEntrySearchDAO()
			.getStudyEntries(new StudyEntrySearchDto(this.project.getProjectId(), new ArrayList<>(), this.variableEntryDescriptors, null),
				new PageRequest(0, 20, new Sort(Sort.Direction.DESC, "GUID")));
		Assert.assertEquals(TEST_COUNT, studyEntryDtosDescending.size());
		Assert.assertTrue(
			Ordering.natural().reverse().isOrdered(
				studyEntryDtosDescending.stream().map(StudyEntryDto::getGuid)
					.collect(Collectors.toList())));

		// Assert ORDER ascending
		final List<StudyEntryDto> studyEntryDtosAscending = this.daoFactory.getStudyEntrySearchDAO()
			.getStudyEntries(new StudyEntrySearchDto(this.project.getProjectId(), new ArrayList<>(), this.variableEntryDescriptors, null),
				new PageRequest(0, 20, new Sort(Sort.Direction.ASC, "GUID")));
		Assert.assertEquals(TEST_COUNT, studyEntryDtosAscending.size());
		Assert.assertTrue(
			Ordering.natural().isOrdered(
				studyEntryDtosAscending.stream().map(StudyEntryDto::getGuid)
					.collect(Collectors.toList())));

		final List<StudyEntryDto> studyEntryDtos = this.daoFactory.getStudyEntrySearchDAO()
			.getStudyEntries(new StudyEntrySearchDto(this.project.getProjectId(), new ArrayList<>(), this.variableEntryDescriptors, null),
				null);
		Assert.assertEquals(TEST_COUNT, studyEntryDtos.size());

		// Filter by GUID
		final StudyEntrySearchDto.Filter filterByGUID = new StudyEntrySearchDto.Filter();
		filterByGUID.setFilteredTextValues(new HashMap<String, String>() {{
			this.put(String.valueOf(TermId.GUID.getId()), String.valueOf(studyEntryDtos.get(0).getGuid()));
		}});
		filterByGUID.setVariableTypeMap(new HashMap<String, String>() {{
			this.put(String.valueOf(TermId.GUID.getId()), null);
		}});

		final StudyEntrySearchDto studyEntrySearchDto =
			new StudyEntrySearchDto(this.project.getProjectId(), new ArrayList<>(), this.variableEntryDescriptors, filterByGUID);
		final long filteredCount =
			this.daoFactory.getStudyEntrySearchDAO().countFilteredStudyEntries(this.project.getProjectId(), studyEntrySearchDto);
		assertEquals(1, filteredCount);
		final List<StudyEntryDto> studyEntryDtosFilterByGUID = this.daoFactory.getStudyEntrySearchDAO()
			.getStudyEntries(studyEntrySearchDto, null);
		Assert.assertEquals(filteredCount, studyEntryDtosFilterByGUID.size());
		assertThat(studyEntryDtosFilterByGUID.get(0).getGuid(), is(studyEntryDtos.get(0).getGuid()));

	}

	@Test
	public void testGetStudyEntries_FilterByDesignation() {

		// Assert ORDER descending
		final List<StudyEntryDto> studyEntryDtosDescending = this.daoFactory.getStudyEntrySearchDAO()
			.getStudyEntries(new StudyEntrySearchDto(this.project.getProjectId(), new ArrayList<>(), this.variableEntryDescriptors, null),
				new PageRequest(0, 20, new Sort(Sort.Direction.DESC, "DESIGNATION")));
		Assert.assertEquals(TEST_COUNT, studyEntryDtosDescending.size());
		Assert.assertTrue(
			Ordering.natural().reverse().isOrdered(
				studyEntryDtosDescending.stream().map(StudyEntryDto::getDesignation)
					.collect(Collectors.toList())));

		// Assert ORDER ascending
		final List<StudyEntryDto> studyEntryDtosAscending = this.daoFactory.getStudyEntrySearchDAO()
			.getStudyEntries(new StudyEntrySearchDto(this.project.getProjectId(), new ArrayList<>(), this.variableEntryDescriptors, null),
				new PageRequest(0, 20, new Sort(Sort.Direction.ASC, "DESIGNATION")));
		Assert.assertEquals(TEST_COUNT, studyEntryDtosAscending.size());
		Assert.assertTrue(
			Ordering.natural().isOrdered(
				studyEntryDtosAscending.stream().map(StudyEntryDto::getDesignation)
					.collect(Collectors.toList())));

		// Filter by Designation
		final StudyEntrySearchDto.Filter filterByDesignation = new StudyEntrySearchDto.Filter();
		filterByDesignation.setFilteredTextValues(new HashMap<String, String>() {{
			this.put(String.valueOf(TermId.DESIG.getId()), String.valueOf(studyEntryDtosAscending.get(0).getDesignation()));
		}});
		filterByDesignation.setVariableTypeMap(new HashMap<String, String>() {{
			this.put(String.valueOf(TermId.DESIG.getId()), null);
		}});

		final StudyEntrySearchDto studyEntrySearchDto =
			new StudyEntrySearchDto(this.project.getProjectId(), new ArrayList<>(), this.variableEntryDescriptors, filterByDesignation);
		final long filteredCount =
			this.daoFactory.getStudyEntrySearchDAO().countFilteredStudyEntries(this.project.getProjectId(), studyEntrySearchDto);
		assertEquals(1, filteredCount);
		final List<StudyEntryDto> studyEntryDtosFilterByDesignation = this.daoFactory.getStudyEntrySearchDAO()
			.getStudyEntries(studyEntrySearchDto, null);
		Assert.assertEquals(filteredCount, studyEntryDtosFilterByDesignation.size());
		assertThat(studyEntryDtosFilterByDesignation.get(0).getDesignation(), is(studyEntryDtosAscending.get(0).getDesignation()));
	}

	@Test
	public void testGetStudyEntries_FilterByCross() {

		// Assert ORDER descending
		final List<StudyEntryDto> studyEntryDtosDescending = this.daoFactory.getStudyEntrySearchDAO()
			.getStudyEntries(new StudyEntrySearchDto(this.project.getProjectId(), new ArrayList<>(), this.variableEntryDescriptors, null),
				new PageRequest(0, 20, new Sort(Sort.Direction.DESC, "CROSS")));
		Assert.assertEquals(TEST_COUNT, studyEntryDtosDescending.size());
		Assert.assertTrue(
			Ordering.natural().reverse().isOrdered(
				studyEntryDtosDescending.stream().map(StudyEntryDto::getCross)
					.collect(Collectors.toList())));

		// Assert ORDER ascending
		final List<StudyEntryDto> studyEntryDtosAscending = this.daoFactory.getStudyEntrySearchDAO()
			.getStudyEntries(new StudyEntrySearchDto(this.project.getProjectId(), new ArrayList<>(), this.variableEntryDescriptors, null),
				new PageRequest(0, 20, new Sort(Sort.Direction.ASC, "CROSS")));
		Assert.assertEquals(TEST_COUNT, studyEntryDtosAscending.size());
		Assert.assertTrue(
			Ordering.natural().isOrdered(
				studyEntryDtosAscending.stream().map(StudyEntryDto::getCross)
					.collect(Collectors.toList())));

		// Filter by Cross
		final StudyEntrySearchDto.Filter filterByCross = new StudyEntrySearchDto.Filter();
		filterByCross.setFilteredTextValues(new HashMap<String, String>() {{
			this.put(String.valueOf(TermId.CROSS.getId()), String.valueOf(studyEntryDtosAscending.get(0).getCross()));
		}});
		filterByCross.setVariableTypeMap(new HashMap<String, String>() {{
			this.put(String.valueOf(TermId.CROSS.getId()), null);
		}});

		final StudyEntrySearchDto studyEntrySearchDto =
			new StudyEntrySearchDto(this.project.getProjectId(), new ArrayList<>(), this.variableEntryDescriptors, filterByCross);
		final long filteredCount =
			this.daoFactory.getStudyEntrySearchDAO().countFilteredStudyEntries(this.project.getProjectId(), studyEntrySearchDto);
		assertEquals(1, filteredCount);
		final List<StudyEntryDto> studyEntryDtosFilterByCross = this.daoFactory.getStudyEntrySearchDAO()
			.getStudyEntries(studyEntrySearchDto, null);
		Assert.assertEquals(filteredCount, studyEntryDtosFilterByCross.size());
		assertThat(studyEntryDtosFilterByCross.get(0).getCross(), is(studyEntryDtosAscending.get(0).getCross()));
	}

	@Test
	public void testGetStudyEntries_FilterByGroupGID() {
		// Assert ORDER descending
		final List<StudyEntryDto> studyEntryDtosDescending = this.daoFactory.getStudyEntrySearchDAO()
			.getStudyEntries(new StudyEntrySearchDto(this.project.getProjectId(), new ArrayList<>(), this.variableEntryDescriptors, null),
				new PageRequest(0, 20, new Sort(Sort.Direction.DESC, "GROUPGID")));
		Assert.assertEquals(TEST_COUNT, studyEntryDtosDescending.size());
		Assert.assertTrue(
			Ordering.natural().reverse().isOrdered(
				studyEntryDtosDescending.stream().map(StudyEntryDto::getGroupGid)
					.collect(Collectors.toList())));

		// Assert ORDER ascending
		final List<StudyEntryDto> studyEntryDtosAscending = this.daoFactory.getStudyEntrySearchDAO()
			.getStudyEntries(new StudyEntrySearchDto(this.project.getProjectId(), new ArrayList<>(), this.variableEntryDescriptors, null),
				new PageRequest(0, 20, new Sort(Sort.Direction.ASC, "GROUPGID")));
		Assert.assertEquals(TEST_COUNT, studyEntryDtosAscending.size());
		Assert.assertTrue(
			Ordering.natural().isOrdered(
				studyEntryDtosAscending.stream().map(StudyEntryDto::getGroupGid)
					.collect(Collectors.toList())));

		// Filter by Group GID
		final StudyEntrySearchDto.Filter filterByGroupGID = new StudyEntrySearchDto.Filter();
		filterByGroupGID.setFilteredValues(new HashMap<String, List<String>>() {{
			this.put(String.valueOf(TermId.GROUPGID.getId()), Arrays.asList(String.valueOf(studyEntryDtosAscending.get(0).getGroupGid())));
		}});
		filterByGroupGID.setVariableTypeMap(new HashMap<String, String>() {{
			this.put(String.valueOf(TermId.GROUPGID.getId()), null);
		}});
		final StudyEntrySearchDto studyEntrySearchDto =
			new StudyEntrySearchDto(this.project.getProjectId(), new ArrayList<>(), this.variableEntryDescriptors, filterByGroupGID);
		final long filteredCount =
			this.daoFactory.getStudyEntrySearchDAO().countFilteredStudyEntries(this.project.getProjectId(), studyEntrySearchDto);
		assertEquals(1, filteredCount);
		final List<StudyEntryDto> studyEntryDtosFilterByGroupGID = this.daoFactory.getStudyEntrySearchDAO()
			.getStudyEntries(studyEntrySearchDto, null);
		Assert.assertEquals(filteredCount, studyEntryDtosFilterByGroupGID.size());
		assertThat(studyEntryDtosFilterByGroupGID.get(0).getGroupGid(), is(studyEntryDtosAscending.get(0).getGroupGid()));
	}

	@Test
	public void testGetStudyEntries_FilterByImmediateSourceName() {
		// Assert ORDER descending
		final List<StudyEntryDto> studyEntryDtosDescending = this.daoFactory.getStudyEntrySearchDAO()
			.getStudyEntries(
				new StudyEntrySearchDto(this.project.getProjectId(), this.fixedEntryDescriptors, this.variableEntryDescriptors, null),
				new PageRequest(0, 20, new Sort(Sort.Direction.DESC, "IMMEDIATE_SOURCE_NAME")));
		Assert.assertEquals(TEST_COUNT, studyEntryDtosDescending.size());
		Assert.assertTrue(
			Ordering.natural().reverse().isOrdered(
				studyEntryDtosDescending.stream().map(e -> {
						System.out.println(e.getProperties().get(TermId.IMMEDIATE_SOURCE_NAME.getId()).getValue());
						return e.getProperties().get(TermId.IMMEDIATE_SOURCE_NAME.getId()).getValue();
					})
					.collect(Collectors.toList())));

		// Assert ORDER ascending
		final List<StudyEntryDto> studyEntryDtosAscending = this.daoFactory.getStudyEntrySearchDAO()
			.getStudyEntries(
				new StudyEntrySearchDto(this.project.getProjectId(), this.fixedEntryDescriptors, this.variableEntryDescriptors, null),
				new PageRequest(0, 20, new Sort(Sort.Direction.ASC, "IMMEDIATE_SOURCE_NAME")));
		Assert.assertEquals(TEST_COUNT, studyEntryDtosAscending.size());
		Assert.assertTrue(
			Ordering.natural().isOrdered(
				studyEntryDtosAscending.stream().map(e -> e.getProperties().get(TermId.IMMEDIATE_SOURCE_NAME.getId()).getValue())
					.collect(Collectors.toList())));

		final String immediateSourceNameSearchString =
			studyEntryDtosAscending.get(0).getProperties().get(TermId.IMMEDIATE_SOURCE_NAME.getId()).getValue();

		// Filter by Immediate Source Name
		final StudyEntrySearchDto.Filter filterByImmediateSourceName = new StudyEntrySearchDto.Filter();
		filterByImmediateSourceName.setFilteredTextValues(new HashMap<String, String>() {{
			this.put(String.valueOf(TermId.IMMEDIATE_SOURCE_NAME.getId()),
				immediateSourceNameSearchString);
		}});
		filterByImmediateSourceName.setVariableTypeMap(new HashMap<String, String>() {{
			this.put(String.valueOf(TermId.IMMEDIATE_SOURCE_NAME.getId()), VariableType.GERMPLASM_DESCRIPTOR.name());
		}});
		final StudyEntrySearchDto studyEntrySearchDto =
			new StudyEntrySearchDto(this.project.getProjectId(), this.fixedEntryDescriptors, this.variableEntryDescriptors,
				filterByImmediateSourceName);
		final long filteredCount =
			this.daoFactory.getStudyEntrySearchDAO().countFilteredStudyEntries(this.project.getProjectId(), studyEntrySearchDto);
		assertEquals(1, filteredCount);
		final List<StudyEntryDto> studyEntryDtosFilterByImmediateSourceName = this.daoFactory.getStudyEntrySearchDAO()
			.getStudyEntries(studyEntrySearchDto, null);
		Assert.assertEquals(filteredCount, studyEntryDtosFilterByImmediateSourceName.size());
		assertThat(studyEntryDtosFilterByImmediateSourceName.get(0).getProperties().get(TermId.IMMEDIATE_SOURCE_NAME.getId()).getValue(),
			is(immediateSourceNameSearchString));

	}

	@Test
	public void testGetStudyEntries_FilterByGroupSourceName() {
		// Assert ORDER descending
		final List<StudyEntryDto> studyEntryDtosDescending = this.daoFactory.getStudyEntrySearchDAO()
			.getStudyEntries(
				new StudyEntrySearchDto(this.project.getProjectId(), new ArrayList<>(), this.variableEntryDescriptors, null),
				new PageRequest(0, 20, new Sort(Sort.Direction.DESC, "GROUP_SOURCE_NAME")));
		Assert.assertEquals(TEST_COUNT, studyEntryDtosDescending.size());
		Assert.assertTrue(
			Ordering.natural().reverse().isOrdered(
				studyEntryDtosDescending.stream().map(e -> e.getProperties().get(TermId.GROUP_SOURCE_NAME.getId()).getValue())
					.collect(Collectors.toList())));

		// Assert ORDER ascending
		final List<StudyEntryDto> studyEntryDtosAscending = this.daoFactory.getStudyEntrySearchDAO()
			.getStudyEntries(
				new StudyEntrySearchDto(this.project.getProjectId(), new ArrayList<>(), this.variableEntryDescriptors, null),
				new PageRequest(0, 20, new Sort(Sort.Direction.ASC, "GROUP_SOURCE_NAME")));
		Assert.assertEquals(TEST_COUNT, studyEntryDtosAscending.size());
		Assert.assertTrue(
			Ordering.natural().isOrdered(
				studyEntryDtosAscending.stream().map(e -> e.getProperties().get(TermId.GROUP_SOURCE_NAME.getId()).getValue())
					.collect(Collectors.toList())));

		final String groupSourceNameSearchString =
			studyEntryDtosAscending.get(0).getProperties().get(TermId.GROUP_SOURCE_NAME.getId()).getValue();

		// Filter by Entry Type
		final StudyEntrySearchDto.Filter filterByGroupSourceName = new StudyEntrySearchDto.Filter();
		filterByGroupSourceName.setFilteredTextValues(new HashMap<String, String>() {{
			this.put(String.valueOf(TermId.GROUP_SOURCE_NAME.getId()),
				groupSourceNameSearchString);
		}});
		filterByGroupSourceName.setVariableTypeMap(new HashMap<String, String>() {{
			this.put(String.valueOf(TermId.GROUP_SOURCE_NAME.getId()), VariableType.GERMPLASM_DESCRIPTOR.name());
		}});
		final StudyEntrySearchDto studyEntrySearchDto =
			new StudyEntrySearchDto(this.project.getProjectId(), new ArrayList<>(), this.variableEntryDescriptors,
				filterByGroupSourceName);
		final long filteredCount =
			this.daoFactory.getStudyEntrySearchDAO().countFilteredStudyEntries(this.project.getProjectId(), studyEntrySearchDto);
		assertEquals(1, filteredCount);
		final List<StudyEntryDto> studyEntryDtosFilterByGroupSourceName = this.daoFactory.getStudyEntrySearchDAO()
			.getStudyEntries(studyEntrySearchDto, null);
		Assert.assertEquals(filteredCount, studyEntryDtosFilterByGroupSourceName.size());
		assertThat(studyEntryDtosFilterByGroupSourceName.get(0).getProperties().get(TermId.GROUP_SOURCE_NAME.getId()).getValue(),
			is(groupSourceNameSearchString));
	}

	@Test
	public void testGetStudyEntries_FilterByLotCount() {
		//Get Units
		final VariableFilter unitFilter = new VariableFilter();
		unitFilter.addPropertyId(TermId.INVENTORY_AMOUNT_PROPERTY.getId());
		final List<Variable> units = this.ontologyVariableDataManager.getWithFilter(unitFilter);

		//Create location
		this.createLocationForSearchLotTest();

		//Create lots for the GID's
		final Integer gidMixed = this.gids.get(0);
		final Integer gidUniqueUnit = this.gids.get(1);

		//Create Inventory Data
		final Lot lot1 = InventoryDetailsTestDataInitializer
			.createLot(1, GERMPLASM, gidMixed, this.location.getLocid(), units.get(0).getId(), 0, 1, "Comments", RandomStringUtils
				.randomAlphabetic(35));

		final Lot lot2 =
			InventoryDetailsTestDataInitializer.createLot(1, GERMPLASM, gidMixed, this.location.getLocid(), units.get(1).getId(), 0, 1,
				"Comments", RandomStringUtils
					.randomAlphabetic(35));

		final Lot lot3 =
			InventoryDetailsTestDataInitializer.createLot(1, GERMPLASM, gidUniqueUnit, this.location.getLocid(), units.get(0).getId(), 0, 1,
				"Comments", RandomStringUtils
					.randomAlphabetic(35));

		final Lot lot4 =
			InventoryDetailsTestDataInitializer.createLot(1, GERMPLASM, gidUniqueUnit, this.location.getLocid(), units.get(0).getId(), 0, 1,
				"Comments", RandomStringUtils
					.randomAlphabetic(35));

		final Transaction transaction1 = InventoryDetailsTestDataInitializer
			.createTransaction(20.0, TransactionStatus.CONFIRMED.getIntValue(), TransactionType.DEPOSIT.getValue(), lot1,
				this.findAdminUser(), 1, 1, LIST, TransactionType.DEPOSIT.getId());

		final Transaction transaction2 = InventoryDetailsTestDataInitializer
			.createTransaction(30.0, TransactionStatus.CONFIRMED.getIntValue(), TransactionType.DEPOSIT.getValue(), lot2,
				this.findAdminUser(), 1, 1, LIST, TransactionType.DEPOSIT.getId());

		final Transaction transaction3 = InventoryDetailsTestDataInitializer
			.createTransaction(10.0, TransactionStatus.CONFIRMED.getIntValue(), TransactionType.DEPOSIT.getValue(), lot3,
				this.findAdminUser(), 1, 1, LIST, TransactionType.DEPOSIT.getId());

		final Transaction transaction4 = InventoryDetailsTestDataInitializer
			.createTransaction(50.0, TransactionStatus.CONFIRMED.getIntValue(), TransactionType.DEPOSIT.getValue(), lot4,
				this.findAdminUser(), 1, 1, LIST, TransactionType.DEPOSIT.getId());

		final LotDAO lotDAO = this.daoFactory.getLotDao();
		lotDAO.save(lot1);
		lotDAO.save(lot2);
		lotDAO.save(lot3);
		lotDAO.save(lot4);
		final TransactionDAO transactionDAO = this.daoFactory.getTransactionDAO();
		transactionDAO.save(transaction1);
		transactionDAO.save(transaction2);
		transactionDAO.save(transaction3);
		transactionDAO.save(transaction4);

		// Assert ORDER descending
		final List<StudyEntryDto> studyEntryDtosDescending = this.daoFactory.getStudyEntrySearchDAO()
			.getStudyEntries(
				new StudyEntrySearchDto(this.project.getProjectId(), new ArrayList<>(), this.variableEntryDescriptors, null),
				new PageRequest(0, 20, new Sort(Sort.Direction.DESC, String.valueOf(TermId.GID_ACTIVE_LOTS_COUNT.getId()))));
		Assert.assertEquals(TEST_COUNT, studyEntryDtosDescending.size());
		Assert.assertTrue(
			Ordering.natural().reverse().isOrdered(
				studyEntryDtosDescending.stream().map(StudyEntryDto::getLotCount)
					.collect(Collectors.toList())));

		// Assert ORDER ascending
		final List<StudyEntryDto> studyEntryDtosAscending = this.daoFactory.getStudyEntrySearchDAO()
			.getStudyEntries(
				new StudyEntrySearchDto(this.project.getProjectId(), new ArrayList<>(), this.variableEntryDescriptors, null),
				new PageRequest(0, 20, new Sort(Sort.Direction.ASC, String.valueOf(TermId.GID_ACTIVE_LOTS_COUNT.getId()))));
		Assert.assertEquals(TEST_COUNT, studyEntryDtosAscending.size());
		Assert.assertTrue(
			Ordering.natural().isOrdered(
				studyEntryDtosAscending.stream().map(StudyEntryDto::getLotCount)
					.collect(Collectors.toList())));

		//Filter by lot count
		final StudyEntrySearchDto.Filter filterByLotCount = new StudyEntrySearchDto.Filter();
		filterByLotCount.setFilteredValues(new HashMap<String, List<String>>() {{
			this.put(String.valueOf(TermId.GID_ACTIVE_LOTS_COUNT.getId()), Arrays.asList("0"));
		}});
		filterByLotCount.setVariableTypeMap(new HashMap<String, String>() {{
			this.put(String.valueOf(TermId.GID_ACTIVE_LOTS_COUNT.getId()), null);
		}});
		final StudyEntrySearchDto studyEntrySearchDto =
			new StudyEntrySearchDto(this.project.getProjectId(), new ArrayList<>(), new ArrayList<>(), filterByLotCount);
		final long filteredCount =
			this.daoFactory.getStudyEntrySearchDAO().countFilteredStudyEntries(this.project.getProjectId(), studyEntrySearchDto);
		assertEquals(1, filteredCount);
		final List<StudyEntryDto> studyEntryDtosFilterByLotCount = this.daoFactory.getStudyEntrySearchDAO()
			.getStudyEntries(studyEntrySearchDto, null);
		Assert.assertEquals(filteredCount, studyEntryDtosFilterByLotCount.size());
		assertThat(studyEntryDtosFilterByLotCount.get(0).getGid(), is(this.gids.get(2)));

	}

	@Test
	public void testGetStudyEntries_FilterByAvailableBalance() {
		//Get Units
		final VariableFilter unitFilter = new VariableFilter();
		unitFilter.addPropertyId(TermId.INVENTORY_AMOUNT_PROPERTY.getId());
		final List<Variable> units = this.ontologyVariableDataManager.getWithFilter(unitFilter);

		//Create location
		this.createLocationForSearchLotTest();

		//Create lots for the GID's
		final Integer gidMixed = this.gids.get(0);
		final Integer gidUniqueUnit = this.gids.get(1);

		//Create Inventory Data
		final Lot lot1 = InventoryDetailsTestDataInitializer
			.createLot(1, GERMPLASM, gidMixed, this.location.getLocid(), units.get(0).getId(), 0, 1, "Comments", RandomStringUtils
				.randomAlphabetic(35));

		final Lot lot2 =
			InventoryDetailsTestDataInitializer.createLot(1, GERMPLASM, gidMixed, this.location.getLocid(), units.get(1).getId(), 0, 1,
				"Comments", RandomStringUtils
					.randomAlphabetic(35));

		final Lot lot3 =
			InventoryDetailsTestDataInitializer.createLot(1, GERMPLASM, gidUniqueUnit, this.location.getLocid(), units.get(0).getId(), 0, 1,
				"Comments", RandomStringUtils
					.randomAlphabetic(35));

		final Lot lot4 =
			InventoryDetailsTestDataInitializer.createLot(1, GERMPLASM, gidUniqueUnit, this.location.getLocid(), units.get(0).getId(), 0, 1,
				"Comments", RandomStringUtils
					.randomAlphabetic(35));

		final Transaction transaction1 = InventoryDetailsTestDataInitializer
			.createTransaction(20.0, TransactionStatus.CONFIRMED.getIntValue(), TransactionType.DEPOSIT.getValue(), lot1,
				this.findAdminUser(), 1, 1, LIST, TransactionType.DEPOSIT.getId());

		final Transaction transaction2 = InventoryDetailsTestDataInitializer
			.createTransaction(30.0, TransactionStatus.CONFIRMED.getIntValue(), TransactionType.DEPOSIT.getValue(), lot2,
				this.findAdminUser(), 1, 1, LIST, TransactionType.DEPOSIT.getId());

		final Transaction transaction3 = InventoryDetailsTestDataInitializer
			.createTransaction(10.0, TransactionStatus.CONFIRMED.getIntValue(), TransactionType.DEPOSIT.getValue(), lot3,
				this.findAdminUser(), 1, 1, LIST, TransactionType.DEPOSIT.getId());

		final Transaction transaction4 = InventoryDetailsTestDataInitializer
			.createTransaction(50.0, TransactionStatus.CONFIRMED.getIntValue(), TransactionType.DEPOSIT.getValue(), lot4,
				this.findAdminUser(), 1, 1, LIST, TransactionType.DEPOSIT.getId());

		final LotDAO lotDAO = this.daoFactory.getLotDao();
		lotDAO.save(lot1);
		lotDAO.save(lot2);
		lotDAO.save(lot3);
		lotDAO.save(lot4);
		final TransactionDAO transactionDAO = this.daoFactory.getTransactionDAO();
		transactionDAO.save(transaction1);
		transactionDAO.save(transaction2);
		transactionDAO.save(transaction3);
		transactionDAO.save(transaction4);

		// Assert ORDER descending
		final List<StudyEntryDto> studyEntryDtosDescending = this.daoFactory.getStudyEntrySearchDAO()
			.getStudyEntries(
				new StudyEntrySearchDto(this.project.getProjectId(), new ArrayList<>(), this.variableEntryDescriptors, null),
				new PageRequest(0, 20, new Sort(Sort.Direction.DESC, String.valueOf(TermId.GID_AVAILABLE_BALANCE.getId()))));
		Assert.assertEquals(TEST_COUNT, studyEntryDtosDescending.size());
		Assert.assertTrue(
			Ordering.natural().reverse().isOrdered(
				studyEntryDtosDescending.stream().map(StudyEntryDto::getAvailableBalance)
					.collect(Collectors.toList())));

		// Assert ORDER ascending
		final List<StudyEntryDto> studyEntryDtosAscending = this.daoFactory.getStudyEntrySearchDAO()
			.getStudyEntries(
				new StudyEntrySearchDto(this.project.getProjectId(), new ArrayList<>(), this.variableEntryDescriptors, null),
				new PageRequest(0, 20, new Sort(Sort.Direction.ASC, String.valueOf(TermId.GID_AVAILABLE_BALANCE.getId()))));
		Assert.assertEquals(TEST_COUNT, studyEntryDtosAscending.size());
		Assert.assertTrue(
			Ordering.natural().isOrdered(
				studyEntryDtosAscending.stream().map(StudyEntryDto::getAvailableBalance)
					.collect(Collectors.toList())));

		//Filter by available balance
		final StudyEntrySearchDto.Filter filterByAvailableBalance = new StudyEntrySearchDto.Filter();
		filterByAvailableBalance.setFilteredTextValues(new HashMap<String, String>() {{
			this.put(String.valueOf(TermId.GID_AVAILABLE_BALANCE.getId()), "60");
		}});
		filterByAvailableBalance.setVariableTypeMap(new HashMap<String, String>() {{
			this.put(String.valueOf(TermId.GID_AVAILABLE_BALANCE.getId()), null);
		}});
		final StudyEntrySearchDto studyEntrySearchDto =
			new StudyEntrySearchDto(this.project.getProjectId(), new ArrayList<>(), new ArrayList<>(), filterByAvailableBalance);
		final long filteredCount =
			this.daoFactory.getStudyEntrySearchDAO().countFilteredStudyEntries(this.project.getProjectId(), studyEntrySearchDto);
		assertEquals(1, filteredCount);
		final List<StudyEntryDto> studyEntryDtosFilterByAvailableBalance = this.daoFactory.getStudyEntrySearchDAO()
			.getStudyEntries(studyEntrySearchDto, null);
		Assert.assertEquals(filteredCount, studyEntryDtosFilterByAvailableBalance.size());
		assertThat(studyEntryDtosFilterByAvailableBalance.get(0).getAvailableBalance(), is("60"));
	}

	@Test
	public void testGetStudyEntries_FilterByUnit() {

		//Get Units
		final VariableFilter unitFilter = new VariableFilter();
		unitFilter.addPropertyId(TermId.INVENTORY_AMOUNT_PROPERTY.getId());
		final List<Variable> units = this.ontologyVariableDataManager.getWithFilter(unitFilter);

		//Create location
		this.createLocationForSearchLotTest();

		//Create lots for the GID's
		final Integer gidMixed = this.gids.get(0);
		final Integer gidUniqueUnit = this.gids.get(1);

		//Create Inventory Data
		final Lot lot1 = InventoryDetailsTestDataInitializer
			.createLot(1, GERMPLASM, gidMixed, this.location.getLocid(), units.get(0).getId(), 0, 1, "Comments", RandomStringUtils
				.randomAlphabetic(35));

		final Lot lot2 =
			InventoryDetailsTestDataInitializer.createLot(1, GERMPLASM, gidMixed, this.location.getLocid(), units.get(1).getId(), 0, 1,
				"Comments", RandomStringUtils
					.randomAlphabetic(35));

		final Lot lot3 =
			InventoryDetailsTestDataInitializer.createLot(1, GERMPLASM, gidUniqueUnit, this.location.getLocid(), units.get(0).getId(), 0, 1,
				"Comments", RandomStringUtils
					.randomAlphabetic(35));

		final Lot lot4 =
			InventoryDetailsTestDataInitializer.createLot(1, GERMPLASM, gidUniqueUnit, this.location.getLocid(), units.get(0).getId(), 0, 1,
				"Comments", RandomStringUtils
					.randomAlphabetic(35));

		final Transaction transaction1 = InventoryDetailsTestDataInitializer
			.createTransaction(20.0, TransactionStatus.CONFIRMED.getIntValue(), TransactionType.DEPOSIT.getValue(), lot1,
				this.findAdminUser(), 1, 1, LIST, TransactionType.DEPOSIT.getId());

		final Transaction transaction2 = InventoryDetailsTestDataInitializer
			.createTransaction(30.0, TransactionStatus.CONFIRMED.getIntValue(), TransactionType.DEPOSIT.getValue(), lot2,
				this.findAdminUser(), 1, 1, LIST, TransactionType.DEPOSIT.getId());

		final Transaction transaction3 = InventoryDetailsTestDataInitializer
			.createTransaction(10.0, TransactionStatus.CONFIRMED.getIntValue(), TransactionType.DEPOSIT.getValue(), lot3,
				this.findAdminUser(), 1, 1, LIST, TransactionType.DEPOSIT.getId());

		final Transaction transaction4 = InventoryDetailsTestDataInitializer
			.createTransaction(50.0, TransactionStatus.CONFIRMED.getIntValue(), TransactionType.DEPOSIT.getValue(), lot4,
				this.findAdminUser(), 1, 1, LIST, TransactionType.DEPOSIT.getId());

		final LotDAO lotDAO = this.daoFactory.getLotDao();
		lotDAO.save(lot1);
		lotDAO.save(lot2);
		lotDAO.save(lot3);
		lotDAO.save(lot4);
		final TransactionDAO transactionDAO = this.daoFactory.getTransactionDAO();
		transactionDAO.save(transaction1);
		transactionDAO.save(transaction2);
		transactionDAO.save(transaction3);
		transactionDAO.save(transaction4);

		// Assert ORDER descending
		final List<StudyEntryDto> studyEntryDtosDescending = this.daoFactory.getStudyEntrySearchDAO()
			.getStudyEntries(
				new StudyEntrySearchDto(this.project.getProjectId(), new ArrayList<>(), this.variableEntryDescriptors, null),
				new PageRequest(0, 20, new Sort(Sort.Direction.DESC, String.valueOf(TermId.GID_UNIT.getId()))));
		Assert.assertEquals(TEST_COUNT, studyEntryDtosDescending.size());
		Assert.assertTrue(
			Ordering.natural().reverse().isOrdered(
				studyEntryDtosDescending.stream().map(StudyEntryDto::getUnit)
					.collect(Collectors.toList())));

		// Assert ORDER ascending
		final List<StudyEntryDto> studyEntryDtosAscending = this.daoFactory.getStudyEntrySearchDAO()
			.getStudyEntries(
				new StudyEntrySearchDto(this.project.getProjectId(), new ArrayList<>(), this.variableEntryDescriptors, null),
				new PageRequest(0, 20, new Sort(Sort.Direction.ASC, String.valueOf(TermId.GID_UNIT.getId()))));
		Assert.assertEquals(TEST_COUNT, studyEntryDtosAscending.size());
		Assert.assertTrue(
			Ordering.natural().isOrdered(
				studyEntryDtosAscending.stream().map(StudyEntryDto::getUnit)
					.collect(Collectors.toList())));

		//Filter by unit
		final StudyEntrySearchDto.Filter filterByUnit = new StudyEntrySearchDto.Filter();
		filterByUnit.setFilteredTextValues(new HashMap<String, String>() {{
			this.put(String.valueOf(TermId.GID_UNIT.getId()), "Mixed");
		}});
		filterByUnit.setVariableTypeMap(new HashMap<String, String>() {{
			this.put(String.valueOf(TermId.GID_UNIT.getId()), null);
		}});
		final StudyEntrySearchDto studyEntrySearchDto =
			new StudyEntrySearchDto(this.project.getProjectId(), new ArrayList<>(), this.variableEntryDescriptors, filterByUnit);
		final long filteredCount =
			this.daoFactory.getStudyEntrySearchDAO().countFilteredStudyEntries(this.project.getProjectId(), studyEntrySearchDto);
		assertEquals(1, filteredCount);
		final List<StudyEntryDto> studyEntryDtosFilterByUnit = this.daoFactory.getStudyEntrySearchDAO()
			.getStudyEntries(studyEntrySearchDto, null);
		Assert.assertEquals(filteredCount, studyEntryDtosFilterByUnit.size());
		assertThat(studyEntryDtosFilterByUnit.get(0).getUnit(), is("Mixed"));
	}

	@Test
	public void testGetStudyEntries_FilterByCustomEntryDetail1() {

		// Assert ORDER descending
		final List<StudyEntryDto> studyEntryDtosDescending = this.daoFactory.getStudyEntrySearchDAO()
			.getStudyEntries(
				new StudyEntrySearchDto(this.project.getProjectId(), new ArrayList<>(), this.variableEntryDescriptors, null),
				new PageRequest(0, 20, new Sort(Sort.Direction.DESC, this.customEntryDetailTerm1.getName())));
		Assert.assertEquals(TEST_COUNT, studyEntryDtosDescending.size());
		Assert.assertTrue(
			Ordering.natural().reverse().isOrdered(
				studyEntryDtosDescending.stream().map(e -> e.getProperties().get(this.customEntryDetailTerm1.getCvTermId())
						.getValue())
					.collect(Collectors.toList())));

		// Assert ORDER ascending
		final List<StudyEntryDto> studyEntryDtosAscending = this.daoFactory.getStudyEntrySearchDAO()
			.getStudyEntries(
				new StudyEntrySearchDto(this.project.getProjectId(), new ArrayList<>(), this.variableEntryDescriptors, null),
				new PageRequest(0, 20, new Sort(Sort.Direction.ASC, this.customEntryDetailTerm1.getName())));
		Assert.assertEquals(TEST_COUNT, studyEntryDtosAscending.size());
		Assert.assertTrue(
			Ordering.natural().isOrdered(
				studyEntryDtosAscending.stream().map(e -> e.getProperties().get(this.customEntryDetailTerm1.getCvTermId())
						.getValue())
					.collect(Collectors.toList())));

		final String searchString =
			studyEntryDtosAscending.get(0).getProperties().get(this.customEntryDetailTerm1.getCvTermId()).getValue();

		final StudyEntrySearchDto.Filter filter = new StudyEntrySearchDto.Filter();
		filter.setFilteredTextValues(new HashMap<String, String>() {{
			this.put(String.valueOf(StudyEntrySearchDAOIntegrationTest.this.customEntryDetailTerm1.getCvTermId()),
				searchString);
		}});
		filter.setVariableTypeMap(new HashMap<String, String>() {{
			this.put(String.valueOf(StudyEntrySearchDAOIntegrationTest.this.customEntryDetailTerm1.getCvTermId()),
				VariableType.ENTRY_DETAIL.name());
		}});

		final StudyEntrySearchDto studyEntrySearchDto =
			new StudyEntrySearchDto(this.project.getProjectId(), new ArrayList<>(), this.variableEntryDescriptors,
				filter);
		final long filteredCount =
			this.daoFactory.getStudyEntrySearchDAO().countFilteredStudyEntries(this.project.getProjectId(), studyEntrySearchDto);
		assertEquals(1, filteredCount);
		final List<StudyEntryDto> studyEntryDtosFilter1 = this.daoFactory.getStudyEntrySearchDAO()
			.getStudyEntries(studyEntrySearchDto, null);
		Assert.assertEquals(filteredCount, studyEntryDtosFilter1.size());
		assertThat(
			studyEntryDtosFilter1.get(0).getProperties().get(this.customEntryDetailTerm1.getCvTermId())
				.getValue(),
			is(searchString));

	}

	@Test
	public void testGetStudyEntries_FilterByCustomEntryDetail2() {

		// Assert ORDER descending
		final List<StudyEntryDto> studyEntryDtosDescending = this.daoFactory.getStudyEntrySearchDAO()
			.getStudyEntries(
				new StudyEntrySearchDto(this.project.getProjectId(), new ArrayList<>(), this.variableEntryDescriptors, null),
				new PageRequest(0, 20, new Sort(Sort.Direction.DESC, this.customEntryDetailTerm2.getName())));
		Assert.assertEquals(TEST_COUNT, studyEntryDtosDescending.size());
		Assert.assertTrue(
			Ordering.natural().reverse().isOrdered(
				studyEntryDtosDescending.stream().map(e -> e.getProperties().get(this.customEntryDetailTerm2.getCvTermId())
						.getValue())
					.collect(Collectors.toList())));

		// Assert ORDER ascending
		final List<StudyEntryDto> studyEntryDtosAscending = this.daoFactory.getStudyEntrySearchDAO()
			.getStudyEntries(
				new StudyEntrySearchDto(this.project.getProjectId(), new ArrayList<>(), this.variableEntryDescriptors, null),
				new PageRequest(0, 20, new Sort(Sort.Direction.ASC, this.customEntryDetailTerm2.getName())));
		Assert.assertEquals(TEST_COUNT, studyEntryDtosAscending.size());
		Assert.assertTrue(
			Ordering.natural().isOrdered(
				studyEntryDtosAscending.stream().map(e -> e.getProperties().get(this.customEntryDetailTerm2.getCvTermId())
						.getValue())
					.collect(Collectors.toList())));

		final String searchString =
			studyEntryDtosAscending.get(0).getProperties().get(this.customEntryDetailTerm2.getCvTermId()).getValue();

		// Filter by Entry Type
		final StudyEntrySearchDto.Filter filter = new StudyEntrySearchDto.Filter();
		filter.setFilteredTextValues(new HashMap<String, String>() {{
			this.put(String.valueOf(StudyEntrySearchDAOIntegrationTest.this.customEntryDetailTerm2.getCvTermId()),
				searchString);
		}});
		filter.setVariableTypeMap(new HashMap<String, String>() {{
			this.put(String.valueOf(StudyEntrySearchDAOIntegrationTest.this.customEntryDetailTerm2.getCvTermId()),
				VariableType.ENTRY_DETAIL.name());
		}});
		final StudyEntrySearchDto studyEntrySearchDto =
			new StudyEntrySearchDto(this.project.getProjectId(), new ArrayList<>(), this.variableEntryDescriptors,
				filter);
		final long filteredCount =
			this.daoFactory.getStudyEntrySearchDAO().countFilteredStudyEntries(this.project.getProjectId(), studyEntrySearchDto);
		assertEquals(1, filteredCount);
		final List<StudyEntryDto> studyEntryDtosResult = this.daoFactory.getStudyEntrySearchDAO()
			.getStudyEntries(
				new StudyEntrySearchDto(this.project.getProjectId(), new ArrayList<>(), this.variableEntryDescriptors,
					filter),
				null);
		Assert.assertEquals(filteredCount, studyEntryDtosResult.size());
		assertThat(
			studyEntryDtosResult.get(0).getProperties().get(this.customEntryDetailTerm2.getCvTermId())
				.getValue(),
			is(searchString));

	}

	private DmsProject createProject(final DmsProject parent) {
		final DmsProject project = new DmsProject();
		project.setName("Test Project Name " + RandomStringUtils.randomAlphanumeric(5));
		project.setDescription("Test Project " + RandomStringUtils.randomAlphanumeric(5));
		project.setStudyType(this.daoFactory.getStudyTypeDao().getStudyTypeByName(StudyTypeDto.TRIAL_NAME));
		project.setProgramUUID(RandomStringUtils.randomAlphanumeric(20));
		project.setCreatedBy(this.findAdminUser().toString());
		project.setLocked(true);
		if (parent != null) {
			project.setParent(parent);
			project.setStudy(parent);
		}
		this.daoFactory.getDmsProjectDAO().save(project);
		return project;
	}

	private CVTerm createNewTerm() {
		final CVTerm variateTerm = CVTermTestDataInitializer.createTerm(RandomStringUtils.randomAlphanumeric(50), CvId.VARIABLES.getId());
		this.daoFactory.getCvTermDao().save(variateTerm);
		return variateTerm;
	}

	private void createSampleStocks(final Integer count, final DmsProject study) {
		// Save the experiments in the same instance
		this.environment = new Geolocation();
		this.daoFactory.getGeolocationDao().saveOrUpdate(this.environment);
		this.gids = new ArrayList<>();

		for (int i = 1; i <= count; i++) {
			final Germplasm germplasm = this.createDerivativeGermplasm(i);
			this.gids.add(germplasm.getGid());
			final StockModel stockModel = this.createTestStock(study, germplasm, i);
			this.createTestExperiment(study, stockModel);
		}

	}

	private Germplasm createDerivativeGermplasm(final int groupGID) {
		final Germplasm groupSource = this.createGermplasm();
		final Germplasm immediateSource = this.createGermplasm();
		final Germplasm germplasm = this.createGermplasm();
		germplasm.setGnpgs(-1);
		germplasm.setGpid1(groupSource.getGid());
		germplasm.setGpid2(immediateSource.getGid());
		germplasm.setMgid(groupGID);
		this.daoFactory.getGermplasmDao().update(germplasm);
		this.sessionProvder.getSession().flush();
		this.daoFactory.getGermplasmDao().refresh(germplasm);
		return germplasm;
	}

	private Germplasm createGermplasm() {
		final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasm(1);
		germplasm.setGid(null);
		germplasm.setGermplasmUUID(UUID.randomUUID().toString());
		this.daoFactory.getGermplasmDao().save(germplasm);
		this.daoFactory.getGermplasmDao().refresh(germplasm);
		this.addName(germplasm, GermplasmNameType.LINE_NAME.getUserDefinedFieldID(), RandomStringUtils.randomAlphabetic(8).toUpperCase(), 0,
			0, 1);
		return germplasm;
	}

	private StockModel createTestStock(final DmsProject study, final Germplasm germplasm, final int entryNumber) {
		final StockModel stockModel = new StockModel();
		stockModel.setUniqueName(String.valueOf(entryNumber));
		stockModel.setName(RandomStringUtils.randomAlphabetic(10).toUpperCase());
		stockModel.setIsObsolete(false);
		stockModel.setGermplasm(germplasm);
		stockModel.setCross("-");
		stockModel.setProject(study);
		stockModel.setCross(RandomStringUtils.randomAlphabetic(10).toUpperCase());
		final int i = 1;
		final Set<StockProperty> properties = new HashSet<>();
		for (final MeasurementVariable measurementVariable : this.variableEntryDescriptors) {
			final StockProperty stockProperty =
				new StockProperty(stockModel, measurementVariable.getTermId(), RandomStringUtils.randomAlphabetic(10).toUpperCase(), null);
			properties.add(stockProperty);
		}
		stockModel.setProperties(properties);
		this.daoFactory.getStockDao().saveOrUpdate(stockModel);
		return stockModel;
	}

	private void createTestExperiment(final DmsProject study, final StockModel stockModel) {
		final ExperimentModel experimentModel = new ExperimentModel();
		experimentModel.setGeoLocation(this.environment);
		experimentModel.setTypeId(TermId.PLOT_EXPERIMENT.getId());
		experimentModel.setProject(study);
		experimentModel.setStock(stockModel);
		this.daoFactory.getExperimentDao().saveOrUpdate(experimentModel);
	}

	private void createLocationForSearchLotTest() {
		final Country country = this.daoFactory.getCountryDao().getById(1);

		final int ltype = 405;
		final String labbr = org.apache.commons.lang3.RandomStringUtils.randomAlphabetic(7);
		final String lname = org.apache.commons.lang3.RandomStringUtils.randomAlphabetic(9);

		this.location = LocationTestDataInitializer.createLocation(null, lname, ltype, labbr);
		this.location.setCountry(country);

		final Location province = this.daoFactory.getLocationDAO().getById(1001);
		this.location.setProvince(province);
		this.daoFactory.getLocationDAO().saveOrUpdate(this.location);
	}

	private List<MeasurementVariable> createFixedEntryDescriptors(final DmsProject project) {

		final List<MeasurementVariable> variableFixedEntryDescriptors = new ArrayList<MeasurementVariable>();

		final CVTerm immediateSourceTerm = this.daoFactory.getCvTermDao().getById(TermId.IMMEDIATE_SOURCE_NAME.getId());
		variableFixedEntryDescriptors.add(
			this.addVariableToProject(null, immediateSourceTerm, project, VariableType.GERMPLASM_DESCRIPTOR, DataType.CHARACTER_VARIABLE,
				1));

		return variableFixedEntryDescriptors;
	}

	private List<MeasurementVariable> createVariableEntryDescriptors(final DmsProject project) {

		final List<MeasurementVariable> variableEntryDescriptors = new ArrayList<MeasurementVariable>();

		final CVTerm guidTerm = this.daoFactory.getCvTermDao().getById(TermId.GUID.getId());
		final CVTerm entryTypeTerm = this.daoFactory.getCvTermDao().getById(TermId.ENTRY_TYPE.getId());
		final CVTerm groupSourceTerm = this.daoFactory.getCvTermDao().getById(TermId.GROUP_SOURCE_NAME.getId());
		final CVTerm groupGIDTerm = this.daoFactory.getCvTermDao().getById(TermId.GROUPGID.getId());
		this.customEntryDetailTerm1 = this.createNewTerm();
		this.customEntryDetailTerm2 = this.createNewTerm();

		variableEntryDescriptors.add(
			this.addVariableToProject(null, guidTerm, project, VariableType.GERMPLASM_DESCRIPTOR, DataType.CHARACTER_VARIABLE, 1));
		variableEntryDescriptors.add(
			this.addVariableToProject(null, entryTypeTerm, project, VariableType.ENTRY_DETAIL, DataType.CATEGORICAL_VARIABLE, 2));
		variableEntryDescriptors.add(
			this.addVariableToProject(null, groupSourceTerm, project, VariableType.GERMPLASM_DESCRIPTOR, DataType.CHARACTER_VARIABLE, 3));
		variableEntryDescriptors.add(
			this.addVariableToProject(null, groupGIDTerm, project, VariableType.GERMPLASM_DESCRIPTOR, DataType.GERMPLASM_LIST, 4));
		variableEntryDescriptors.add(
			this.addVariableToProject("alias1", this.customEntryDetailTerm1, project, VariableType.ENTRY_DETAIL,
				DataType.CATEGORICAL_VARIABLE,
				5));
		variableEntryDescriptors.add(
			this.addVariableToProject("alias2", this.customEntryDetailTerm2, project, VariableType.ENTRY_DETAIL,
				DataType.CHARACTER_VARIABLE,
				6));
		return variableEntryDescriptors;
	}

	private MeasurementVariable addVariableToProject(final String alias, final CVTerm cvTerm, final DmsProject project,
		final VariableType variableType, final DataType dataType, final int rank) {
		this.testDataInitializer.addProjectProp(project, cvTerm.getCvTermId(), StringUtils.isEmpty(alias) ? cvTerm.getName() : alias,
			variableType,
			null, rank);
		final MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setTermId(cvTerm.getCvTermId());
		measurementVariable.setName(cvTerm.getName());
		measurementVariable.setDescription(cvTerm.getDefinition());
		measurementVariable.setVariableType(variableType);
		measurementVariable.setDataType(dataType.getName());
		return measurementVariable;
	}

	private Name addName(final Germplasm germplasm, final Integer nameId, final String nameVal, final Integer locId, final Integer date,
		final int preferred) {
		final Name name = new Name(null, germplasm, nameId, preferred, nameVal, locId, date, 0);
		this.daoFactory.getNameDao().save(name);
		this.sessionProvder.getSession().flush();
		this.daoFactory.getNameDao().refresh(name);

		return name;
	}

}
