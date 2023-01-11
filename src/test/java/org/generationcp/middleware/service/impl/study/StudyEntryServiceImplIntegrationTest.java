
package org.generationcp.middleware.service.impl.study;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.DataSetupTest;
import org.generationcp.middleware.GermplasmTestDataGenerator;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.api.germplasmlist.GermplasmListService;
import org.generationcp.middleware.domain.dataset.PlotDatasetPropertiesDTO;
import org.generationcp.middleware.domain.gms.SystemDefinedEntryType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.GermplasmListDataDetail;
import org.generationcp.middleware.pojos.dms.DatasetType;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.dms.StockProperty;
import org.generationcp.middleware.pojos.dms.StudyType;
import org.generationcp.middleware.service.api.dataset.DatasetService;
import org.generationcp.middleware.service.api.study.StudyEntryColumnDTO;
import org.generationcp.middleware.service.api.study.StudyEntryDto;
import org.generationcp.middleware.service.api.study.StudyEntryPropertyData;
import org.generationcp.middleware.service.api.study.StudyEntryService;
import org.generationcp.middleware.utils.test.IntegrationTestDataInitializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * The class <code>StudyGermplasmListServiceImplTest</code> contains tests for the class <code>{@link StudyEntryServiceImpl}</code>.
 */
public class StudyEntryServiceImplIntegrationTest extends IntegrationTestBase {

	private static final Integer NUMBER_OF_GERMPLASM = 5;

	private static final String GERMPLASM_PREFERRED_NAME_PREFIX = DataSetupTest.GERMPLSM_PREFIX + "PR-";
	private static final String ENTRYCODE = "ENTRYCODE-";
	private static final String CROSS = "ABC/XYZ-";
	private static final String SEEDSOURCE = "SEEDSOURCE-";
	private static final String ACCNO = "ACCNO";

	@Autowired
	private StudyEntryService service;

	private IntegrationTestDataInitializer testDataInitializer;

	private GermplasmTestDataGenerator germplasmTestDataGenerator;

	private DaoFactory daoFactory;

	@Autowired
	private GermplasmListManager germplasmListManager;

	@Autowired
	private GermplasmListService germplasmListService;

	@Autowired
	private DatasetService studyDatasetService;

	@Before
	public void setup() {
		this.daoFactory = new DaoFactory(this.sessionProvder);
		this.testDataInitializer = new IntegrationTestDataInitializer(this.sessionProvder, this.workbenchSessionProvider);
		this.germplasmTestDataGenerator = new GermplasmTestDataGenerator(this.daoFactory);

	}

	@Test
	public void testCountStudyEntries() {
		final DmsProject study = this.createStudy();
		this.createTestGermplasm(study.getProjectId());
		Assert.assertEquals(NUMBER_OF_GERMPLASM.intValue(), this.service.countStudyEntries(study.getProjectId()));
	}

	@Test
	public void testDeleteStudyEntries() {
		final DmsProject study = this.createStudy();
		this.createTestGermplasm(study.getProjectId());

		this.service.deleteStudyEntries(study.getProjectId());
		Assert.assertEquals(0, this.service.countStudyEntries(study.getProjectId()));
	}

	@Test
	public void testSaveStudyEntries_shouldCopyEntryTypeValuesFromGermplasmListForAllEntries() {
		final DmsProject study = this.createStudy();
		final Germplasm germplasm1 = this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames();
		final Germplasm germplasm2 = this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames();

		final GermplasmList
			germplasmList = new GermplasmList(null, RandomStringUtils.randomAlphabetic(10), 20222302L, GermplasmList.LIST_TYPE, this.findAdminUser(), "", null, GermplasmList.Status.LIST.getCode());
		this.daoFactory.getGermplasmListDAO().save(germplasmList);

		// Add germplasm list entries
		final GermplasmListData germplasmListData1 = this.addGermplasmListData(germplasmList, germplasm1.getGid(), 1);
		this.addGermplasmListDataDetail(germplasmListData1, TermId.ENTRY_TYPE.getId(),
			SystemDefinedEntryType.NON_REPLICATED_ENTRY.getEntryTypeValue(), SystemDefinedEntryType.NON_REPLICATED_ENTRY.getEntryTypeCategoricalId());
		this.addGermplasmListDataDetail(germplasmListData1, TermId.ENTRY_CODE.getId(), "entryCodeValue", null);

		final GermplasmListData germplasmListData2 = this.addGermplasmListData(germplasmList, germplasm2.getGid(), 2);
		this.addGermplasmListDataDetail(germplasmListData2, TermId.ENTRY_TYPE.getId(),
			SystemDefinedEntryType.DISEASE_CHECK.getEntryTypeValue(), SystemDefinedEntryType.DISEASE_CHECK.getEntryTypeCategoricalId());

		this.service.saveStudyEntries(study.getProjectId(), germplasmList.getId());

		final List<StudyEntryDto> studyEntries = this.service.getStudyEntries(study.getProjectId());
		assertThat(studyEntries, hasSize(2));

		// Assert entry 1
		final StudyEntryDto studyEntryDto1 = studyEntries.get(0);
		this.assertStudyEntryDto(studyEntryDto1, germplasm1, 1);
		this.assertStudyEntryProperty(studyEntryDto1.getProperties().get(TermId.ENTRY_TYPE.getId()), TermId.ENTRY_TYPE,
			SystemDefinedEntryType.NON_REPLICATED_ENTRY.getEntryTypeValue(), SystemDefinedEntryType.NON_REPLICATED_ENTRY.getEntryTypeCategoricalId());

		// Assert entry 2
		final StudyEntryDto studyEntryDto2 = studyEntries.get(1);
		this.assertStudyEntryDto(studyEntryDto2, germplasm2, 2);
		this.assertStudyEntryProperty(studyEntryDto2.getProperties().get(TermId.ENTRY_TYPE.getId()), TermId.ENTRY_TYPE,
			SystemDefinedEntryType.DISEASE_CHECK.getEntryTypeValue(), SystemDefinedEntryType.DISEASE_CHECK.getEntryTypeCategoricalId());
	}

	@Test
	public void testSaveStudyEntries_shouldAddDefaultEntryTypeToAllEntries() {
		final DmsProject study = this.createStudy();
		final Germplasm germplasm1 = this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames();
		final Germplasm germplasm2 = this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames();

		final GermplasmList
			germplasmList = new GermplasmList(null, RandomStringUtils.randomAlphabetic(10), 20222302L, GermplasmList.LIST_TYPE, this.findAdminUser(), "", null, GermplasmList.Status.LIST.getCode());
		this.daoFactory.getGermplasmListDAO().save(germplasmList);

		// Add germplasm list entries
		this.addGermplasmListData(germplasmList, germplasm1.getGid(), 1);
		this.addGermplasmListData(germplasmList, germplasm2.getGid(), 2);

		this.service.saveStudyEntries(study.getProjectId(), germplasmList.getId());

		final List<StudyEntryDto> studyEntries = this.service.getStudyEntries(study.getProjectId());
		assertThat(studyEntries, hasSize(2));

		// Assert entry 1
		final StudyEntryDto studyEntryDto1 = studyEntries.get(0);
		this.assertStudyEntryDto(studyEntryDto1, germplasm1, 1);
		this.assertStudyEntryProperty(studyEntryDto1.getProperties().get(TermId.ENTRY_TYPE.getId()), TermId.ENTRY_TYPE,
			SystemDefinedEntryType.TEST_ENTRY.getEntryTypeValue(), SystemDefinedEntryType.TEST_ENTRY.getEntryTypeCategoricalId());

		// Assert entry 2
		final StudyEntryDto studyEntryDto2 = studyEntries.get(1);
		this.assertStudyEntryDto(studyEntryDto2, germplasm2, 2);
		this.assertStudyEntryProperty(studyEntryDto2.getProperties().get(TermId.ENTRY_TYPE.getId()), TermId.ENTRY_TYPE,
			SystemDefinedEntryType.TEST_ENTRY.getEntryTypeValue(), SystemDefinedEntryType.TEST_ENTRY.getEntryTypeCategoricalId());
	}

	@Test
	public void testSaveStudyEntries_shouldAddDefaultEntryTypeOnlyForEntriesWithoutEntryTypeValues() {
		final DmsProject study = this.createStudy();
		final Germplasm germplasm1 = this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames();
		final Germplasm germplasm2 = this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames();

		final GermplasmList
			germplasmList = new GermplasmList(null, RandomStringUtils.randomAlphabetic(10), 20222302L, GermplasmList.LIST_TYPE, this.findAdminUser(), "", null, GermplasmList.Status.LIST.getCode());
		this.daoFactory.getGermplasmListDAO().save(germplasmList);

		// Add germplasm list entries
		final GermplasmListData germplasmListData1 = this.addGermplasmListData(germplasmList, germplasm1.getGid(), 1);
		this.addGermplasmListDataDetail(germplasmListData1, TermId.ENTRY_TYPE.getId(),
			SystemDefinedEntryType.NON_REPLICATED_ENTRY.getEntryTypeValue(), SystemDefinedEntryType.NON_REPLICATED_ENTRY.getEntryTypeCategoricalId());

		this.addGermplasmListData(germplasmList, germplasm2.getGid(), 2);

		this.service.saveStudyEntries(study.getProjectId(), germplasmList.getId());

		final List<StudyEntryDto> studyEntries = this.service.getStudyEntries(study.getProjectId());
		assertThat(studyEntries, hasSize(2));

		// Assert entry 1
		final StudyEntryDto studyEntryDto1 = studyEntries.get(0);
		this.assertStudyEntryDto(studyEntryDto1, germplasm1, 1);
		this.assertStudyEntryProperty(studyEntryDto1.getProperties().get(TermId.ENTRY_TYPE.getId()), TermId.ENTRY_TYPE,
			SystemDefinedEntryType.NON_REPLICATED_ENTRY.getEntryTypeValue(), SystemDefinedEntryType.NON_REPLICATED_ENTRY.getEntryTypeCategoricalId());

		// Assert entry 2
		final StudyEntryDto studyEntryDto2 = studyEntries.get(1);
		this.assertStudyEntryDto(studyEntryDto2, germplasm2, 2);
		this.assertStudyEntryProperty(studyEntryDto2.getProperties().get(TermId.ENTRY_TYPE.getId()), TermId.ENTRY_TYPE,
			SystemDefinedEntryType.TEST_ENTRY.getEntryTypeValue(), SystemDefinedEntryType.TEST_ENTRY.getEntryTypeCategoricalId());
	}

	@Test
	public void testGetNextEntryNumber() {
		final DmsProject study = this.createStudy();
		final Germplasm germplasm = this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames();

		final GermplasmList
			germplasmList = new GermplasmList(null, RandomStringUtils.randomAlphabetic(10), 20222302L, GermplasmList.LIST_TYPE, this.findAdminUser(), "", null, GermplasmList.Status.LIST.getCode());
		this.daoFactory.getGermplasmListDAO().save(germplasmList);

		final GermplasmListData
			germplasmListData = new GermplasmListData(null, germplasmList, germplasm.getGid(), 1, "Unknown", "LNAME", 0, null);
		this.daoFactory.getGermplasmListDataDAO().save(germplasmListData);

		this.service.saveStudyEntries(study.getProjectId(), germplasmList.getId());

		final List<StudyEntryDto> studyEntries = this.service.getStudyEntries(study.getProjectId());

		final Integer nextEntryNumber = this.service.getNextEntryNumber(study.getProjectId());
		final int expectedValue = studyEntries.get(0).getEntryNumber() + 1;
		Assert.assertEquals(Integer.toString(expectedValue), nextEntryNumber.toString());
	}

	@Test
	public void testHasUnassignedEntries() {
		final DmsProject study = this.createStudy();
		final List<Integer> gids = this.createTestGermplasm(study.getProjectId());

		Assert.assertTrue(this.service.hasUnassignedEntries(study.getProjectId()));
		final List<StockModel> stocks = this.daoFactory.getStockDao().getStocksForStudy(study.getProjectId());
		this.addExperimentsForStocks(stocks, study);
		// Need to flush session to sync with underlying database before querying
		this.sessionProvder.getSession().flush();
		Assert.assertFalse(this.service.hasUnassignedEntries(study.getProjectId()));
	}

	@Test
	public void testReplaceStudyEntry() {
		final DmsProject study = this.createStudy();
		final List<Integer> gids = this.createTestGermplasm(study.getProjectId());

		final StudyEntryDto oldEntry = this.service.getStudyEntries(study.getProjectId()).get(1);
		Assert.assertNotNull(oldEntry);
		final Integer newGid = gids.get(0);
		this.service.replaceStudyEntry(study.getProjectId(), oldEntry.getEntryId(), newGid);

		final StockModel newEntry = this.daoFactory.getStockDao().getStocksForStudy(study.getProjectId()).stream()
			.filter(stockModel -> stockModel.getGermplasm().getGid().equals(newGid) &&
				Integer.valueOf(stockModel.getUniqueName()).equals(oldEntry.getEntryNumber())).findFirst().get();

		Assert.assertNotEquals(oldEntry.getEntryId(), newEntry.getStockId());
		Assert.assertEquals(newGid, newEntry.getGermplasm().getGid());
		// Some fields should have been copied from old entry
		final Optional<StockProperty> entryType =
			newEntry.getProperties().stream().filter(stockProperty -> stockProperty.getTypeId().equals(TermId.ENTRY_TYPE.getId()))
				.findFirst();
		assertTrue(entryType.isPresent());
		Assert.assertEquals(oldEntry.getProperties().get(TermId.ENTRY_TYPE.getId()).getValue(),
			entryType.get().getValue());
		Assert.assertEquals(oldEntry.getEntryNumber(), Integer.valueOf(newEntry.getUniqueName()));
	}

	@Test(expected = MiddlewareRequestException.class)
	public void testReplaceStudyEntry_InvalidEntryId() {
		final DmsProject study = this.createStudy();
		final List<Integer> gids = this.createTestGermplasm(study.getProjectId());

		final StudyEntryDto dto = this.service.getStudyEntries(study.getProjectId()).get(1);
		Assert.assertNotNull(dto);
		final Integer newGid = gids.get(0);
		this.service.replaceStudyEntry(study.getProjectId(), dto.getEntryId() + 10, newGid);
	}

	@Test(expected = MiddlewareRequestException.class)
	public void testReplaceStudyEntry_InvalidEntryIdForStudy() {
		final DmsProject study = this.createStudy();
		final List<Integer> gids = this.createTestGermplasm(study.getProjectId());

		final StudyEntryDto dto = this.service.getStudyEntries(study.getProjectId()).get(1);
		Assert.assertNotNull(dto);
		final Integer newGid = gids.get(0);
		this.service.replaceStudyEntry(study.getProjectId() + 1, dto.getEntryId(), newGid);
	}

	@Test(expected = MiddlewareRequestException.class)
	public void testReplaceStudyEntry_SameGidAsExistingEntry() {
		final DmsProject study = this.createStudy();
		this.createTestGermplasm(study.getProjectId());

		final StudyEntryDto dto = this.service.getStudyEntries(study.getProjectId()).get(1);
		Assert.assertNotNull(dto);
		this.service.replaceStudyEntry(study.getProjectId() + 1, dto.getEntryId(), dto.getGid());
	}

	@Test
	public void testReplaceStudyEntries() {

		final Germplasm targetGermplasm = this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames();
		final Germplasm germplasm1 = this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames();
		final Germplasm germplasm2 = this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames();

		final DmsProject study1 = this.createStudy();
		this.daoFactory.getStockDao().save(new StockModel(study1.getProjectId(), this.createTestStudyEntry(1, germplasm1.getGid())));
		this.daoFactory.getStockDao().save(new StockModel(study1.getProjectId(), this.createTestStudyEntry(2, germplasm2.getGid())));

		final DmsProject study2 = this.createStudy();
		this.daoFactory.getStockDao().save(new StockModel(study2.getProjectId(), this.createTestStudyEntry(1, germplasm1.getGid())));
		this.daoFactory.getStockDao().save(new StockModel(study2.getProjectId(), this.createTestStudyEntry(2, germplasm2.getGid())));

		this.service.replaceStudyEntries(Arrays.asList(germplasm1.getGid(), germplasm2.getGid()), targetGermplasm.getGid(),
			RandomStringUtils.randomAlphabetic(10));

		this.sessionProvder.getSession().flush();

		// Check if the study entries are replaced with the target germplasm id for the first study
		final List<StudyEntryDto> studyEntries1 = this.service.getStudyEntries(study1.getProjectId());
		Assert.assertEquals(targetGermplasm.getGid(), studyEntries1.get(0).getGid());
		Assert.assertEquals(targetGermplasm.getGid(), studyEntries1.get(1).getGid());

		// Check if the study entries are replaced with the target germplasm id for the second study
		final List<StudyEntryDto> studyEntries2 = this.service.getStudyEntries(study2.getProjectId());
		Assert.assertEquals(targetGermplasm.getGid(), studyEntries2.get(0).getGid());
		Assert.assertEquals(targetGermplasm.getGid(), studyEntries2.get(1).getGid());

	}

	@Test
	public void testGetStudyEntries() {
		final DmsProject study = this.createStudy();
		final List<Integer> gids = this.createTestGermplasm(study.getProjectId());

		final List<StudyEntryDto> studyEntries = this.service.getStudyEntries(study.getProjectId());
		int index = 0;
		for (final StudyEntryDto dto : studyEntries) {
			this.verifyStudyEntryDetails(gids.get(index), index + 1, dto);
			index++;
		}
	}

	@Test
	public void updatePlotDatasetProperties_shouldAddNameType() {
		final DmsProject study = this.createStudyWithGermplasmListAsociated();

		final List<StudyEntryColumnDTO> columns =
			this.service.getStudyEntryColumns(study.getProjectId(), null);
		final StudyEntryColumnDTO aCCNONameTypeColumn =
			columns.stream().filter(column -> //
					column.getName().equalsIgnoreCase(StudyEntryServiceImplIntegrationTest.ACCNO) && column.getTypeId() == null)
				.findFirst().get();

		Assert.assertFalse(aCCNONameTypeColumn.isSelected());

		final PlotDatasetPropertiesDTO plotDatasetPropertiesDTO = new PlotDatasetPropertiesDTO();
		plotDatasetPropertiesDTO.setNameTypeIds(Arrays.asList(1));
		plotDatasetPropertiesDTO.setVariableIds(Collections.emptyList());
		this.studyDatasetService.updatePlotDatasetProperties(study.getProjectId(), plotDatasetPropertiesDTO, null);

		final List<StudyEntryColumnDTO> updatedColumns =
			this.service.getStudyEntryColumns(study.getProjectId(), null);

		final StudyEntryColumnDTO selectedACCNONameTypeColumn = updatedColumns.stream().filter(column -> //
				column.getName().equalsIgnoreCase(StudyEntryServiceImplIntegrationTest.ACCNO) && column.getTypeId() == null)
			.findFirst().get();
		Assert.assertTrue(selectedACCNONameTypeColumn.isSelected());
	}

	@Test
	public void testUpdatePlotDatasetProperties_shouldRemoveNameType(){
		final DmsProject study = this.createStudyWithGermplasmListAsociated();

		final PlotDatasetPropertiesDTO plotDatasetPropertiesDTO = new PlotDatasetPropertiesDTO();
		plotDatasetPropertiesDTO.setNameTypeIds(Arrays.asList(1));
		plotDatasetPropertiesDTO.setVariableIds(Collections.emptyList());
		this.studyDatasetService.updatePlotDatasetProperties(study.getProjectId(), plotDatasetPropertiesDTO, null);

		final List<StudyEntryColumnDTO> columns = this.service.getStudyEntryColumns(study.getProjectId(), null);
		StudyEntryColumnDTO aCCNONameTypeColumn =
			columns.stream().filter( column -> //
					column.getName().equalsIgnoreCase(StudyEntryServiceImplIntegrationTest.ACCNO) && column.getTypeId() == null)
				.findFirst().get();

		Assert.assertTrue(aCCNONameTypeColumn.isSelected());

		plotDatasetPropertiesDTO.setNameTypeIds(Collections.emptyList());
		this.studyDatasetService.updatePlotDatasetProperties(study.getProjectId(), plotDatasetPropertiesDTO, null);
		final List<StudyEntryColumnDTO> Updatedcolumns = this.service.getStudyEntryColumns(study.getProjectId(), null);

		aCCNONameTypeColumn =
			Updatedcolumns.stream().filter(column -> column.getName().equalsIgnoreCase(StudyEntryServiceImplIntegrationTest.ACCNO ) && column.getTypeId() == null).findFirst().get();
		Assert.assertFalse(aCCNONameTypeColumn.isSelected());
	}

	private void verifyStudyEntryDetails(final Integer gid, final int index, final StudyEntryDto dto) {
		Assert.assertEquals(index, dto.getEntryNumber().intValue());
		Assert.assertEquals(GERMPLASM_PREFERRED_NAME_PREFIX + index, dto.getDesignation());
		Assert.assertEquals(CROSS + index, dto.getCross());
		Assert.assertEquals(gid, dto.getGid());
		// TODO: assert entry code from properties
//		Assert.assertEquals(StudyEntryServiceImplTest.ENTRYCODE + gid, dto.getEntryCode());
		Assert.assertNotNull(dto.getEntryId());
	}

	private List<Integer> createTestGermplasm(final int studyId) {
		final Germplasm parentGermplasm = this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames();
		final Integer[] gids = this.germplasmTestDataGenerator
			.createChildrenGermplasm(NUMBER_OF_GERMPLASM,
				GERMPLASM_PREFERRED_NAME_PREFIX,
				parentGermplasm);
		for (int i = 1; i <= NUMBER_OF_GERMPLASM; i++) {
			final Integer gid = gids[i - 1];
			this.daoFactory.getStockDao().save(new StockModel(studyId, this.createTestStudyEntry(i, gid)));
		}
		return Arrays.asList(gids);
	}

	private StudyEntryDto createTestStudyEntry(final int i, final Integer gid) {
		final StudyEntryDto studyEntryDto = new StudyEntryDto();
		studyEntryDto.setGid(gid);
		studyEntryDto.setEntryNumber(i);
		studyEntryDto.setDesignation(GERMPLASM_PREFERRED_NAME_PREFIX + i);
		studyEntryDto.setCross(CROSS + i);

		studyEntryDto.getProperties()
			.put(TermId.CROSS.getId(), new StudyEntryPropertyData(null, TermId.CROSS.getId(), CROSS + i, null));
		studyEntryDto.getProperties()
			.put(TermId.ENTRY_TYPE.getId(), new StudyEntryPropertyData(null, TermId.ENTRY_TYPE.getId(),
				null, SystemDefinedEntryType.TEST_ENTRY.getEntryTypeCategoricalId()));
		studyEntryDto.getProperties()
			.put(TermId.ENTRY_CODE.getId(), new StudyEntryPropertyData(null, TermId.ENTRY_CODE.getId(),
				ENTRYCODE + gid, null));


		return studyEntryDto;
	}

	private DmsProject createStudy() {
		final DmsProject study = new DmsProject(
			"TEST STUDY " + RandomStringUtils.randomAlphanumeric(10), "TEST DESCRIPTION", null, Collections.emptyList(),
			false,
			false, new StudyType(6), "20200606", null, null,
			null, "1");
		this.daoFactory.getDmsProjectDAO().save(study);
		final DmsProject plotDataset = new DmsProject(
			"TEST DATASET", "TEST DATASET DESC", null, Collections.emptyList(),
			false,
			false, new StudyType(6), "20200606", null, null,
			null, "1");
		plotDataset.setDatasetType(new DatasetType(DatasetTypeEnum.PLOT_DATA.getId()));
		plotDataset.setStudy(study);
		this.daoFactory.getDmsProjectDAO().save(plotDataset);

		final ProjectProperty gidProp =
			new ProjectProperty(plotDataset, VariableType.GERMPLASM_DESCRIPTOR.getId(), "", 1, TermId.GID.getId(), "GID");
		final ProjectProperty desigProp =
			new ProjectProperty(plotDataset, VariableType.GERMPLASM_DESCRIPTOR.getId(), "", 2, TermId.DESIG.getId(), "DESIG");
		final ProjectProperty entryNoProp =
			new ProjectProperty(plotDataset, VariableType.ENTRY_DETAIL.getId(), "", 3, TermId.ENTRY_NO.getId(), "ENTRY_NO");
		final ProjectProperty crossProp =
			new ProjectProperty(plotDataset, VariableType.GERMPLASM_DESCRIPTOR.getId(), "", 5, TermId.CROSS.getId(), "CROSS");
		final ProjectProperty entryTypeProp =
			new ProjectProperty(plotDataset, VariableType.ENTRY_DETAIL.getId(), "", 6, TermId.ENTRY_TYPE.getId(),
				"ENTRY_TYPE");
		this.daoFactory.getProjectPropertyDAO().save(gidProp);
		this.daoFactory.getProjectPropertyDAO().save(desigProp);
		this.daoFactory.getProjectPropertyDAO().save(entryNoProp);
		this.daoFactory.getProjectPropertyDAO().save(crossProp);
		this.daoFactory.getProjectPropertyDAO().save(entryTypeProp);
		this.sessionProvder.getSession().refresh(plotDataset);
		return study;
	}

	private DmsProject createStudyWithGermplasmListAsociated() {
		final DmsProject study = this.createStudy();

		final Germplasm parentGermplasm = this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames();
		final Integer[] gids = this.germplasmTestDataGenerator.createChildrenGermplasm(DataSetupTest.NUMBER_OF_GERMPLASM, "PREFF", parentGermplasm);

		final Integer listId = this.createGermplasmList(null, gids);
		this.service.saveStudyEntries(study.getProjectId(), listId);
		return study;
	}

	void addExperimentsForStocks(final List<StockModel> stocks, final DmsProject study) {
		final Geolocation geolocation = this.testDataInitializer.createTestGeolocation("1", 101);
		for (final StockModel stock : stocks) {
			final ExperimentModel experimentModel = new ExperimentModel();
			experimentModel.setGeoLocation(geolocation);
			experimentModel.setTypeId(TermId.PLOT_EXPERIMENT.getId());
			experimentModel.setProject(study);
			experimentModel.setStock(stock);
			this.daoFactory.getExperimentDao().save(experimentModel);
		}
	}

	private GermplasmListData addGermplasmListData(final GermplasmList germplasmList, final Integer gid, final Integer entryId) {
		final String designation = RandomStringUtils.randomAlphabetic(10);
		final GermplasmListData
			germplasmListData = new GermplasmListData(null, germplasmList, gid, entryId, "Unknown", "LNAME", 0, null);
		return this.daoFactory.getGermplasmListDataDAO().save(germplasmListData);
	}

	private GermplasmListDataDetail addGermplasmListDataDetail(final GermplasmListData germplasmListData, final Integer variableId,
		final String value, final Integer categoricalValueId) {

		final GermplasmListDataDetail germplasmListDataDetail =
			new GermplasmListDataDetail(germplasmListData, variableId, value, categoricalValueId);
		return this.daoFactory.getGermplasmListDataDetailDAO().save(germplasmListDataDetail);
	}

	private void assertStudyEntryDto(final StudyEntryDto studyEntryDto, final Germplasm germplasm, final Integer entryNumber) {
		assertNotNull(studyEntryDto.getEntryId());
		assertThat(studyEntryDto.getEntryNumber(), is(entryNumber));
		assertThat(studyEntryDto.getGid(), is(germplasm.getGid()));
		assertThat(studyEntryDto.getDesignation(), is(germplasm.getPreferredName().getNval()));
		assertNotNull(studyEntryDto.getProperties());
	}

	private void assertStudyEntryProperty(final StudyEntryPropertyData entryPropertyData, final TermId termId, final String value, final Integer categoricalValueId) {
		assertNotNull(entryPropertyData);
		assertNotNull(entryPropertyData.getStudyEntryPropertyId());
		assertThat(entryPropertyData.getVariableId(), is(termId.getId()));
		assertThat(entryPropertyData.getValue(), is(value));
		assertThat(entryPropertyData.getCategoricalValueId(), is(categoricalValueId));
	}

	public Integer createGermplasmList(final String programUUID, final Integer[] gids) {
		final int randomInt = new Random().nextInt(100);

		// Germplasm list
		final GermplasmList germplasmList = new GermplasmList(null, "Test Germplasm List " + randomInt,
			Long.valueOf(20141014), "LST", 1, "Test Germplasm List", null, 1);

		this.germplasmListManager.addGermplasmList(germplasmList);
		germplasmList.setProgramUUID(programUUID);

		// Germplasm list data
		final List<GermplasmListData> germplasmListData = new ArrayList<>();
		for (int i = 0; i < DataSetupTest.NUMBER_OF_GERMPLASM; i++) {
			germplasmListData.add(new GermplasmListData(null, germplasmList, gids[i], i,
				DataSetupTest.GERMPLSM_PREFIX + i + " Source",
				DataSetupTest.GERMPLSM_PREFIX + "Group A", 0, 0));
		}
		this.germplasmListService.addGermplasmListData(germplasmListData);
		return germplasmList.getId();
	}

}
