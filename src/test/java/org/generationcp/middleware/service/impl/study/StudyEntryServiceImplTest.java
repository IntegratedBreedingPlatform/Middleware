
package org.generationcp.middleware.service.impl.study;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.DataSetupTest;
import org.generationcp.middleware.GermplasmTestDataGenerator;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.gms.SystemDefinedEntryType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.dms.DatasetType;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.dms.StudyType;
import org.generationcp.middleware.service.api.study.StudyEntryDto;
import org.generationcp.middleware.service.api.study.StudyEntryPropertyData;
import org.generationcp.middleware.service.api.study.StudyEntryService;
import org.generationcp.middleware.utils.test.IntegrationTestDataInitializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The class <code>StudyGermplasmListServiceImplTest</code> contains tests for the class <code>{@link StudyEntryServiceImpl}</code>.
 */
public class StudyEntryServiceImplTest extends IntegrationTestBase {

	private static final Integer NUMBER_OF_GERMPLASM = 5;

	private static final String GERMPLASM_PREFERRED_NAME_PREFIX = DataSetupTest.GERMPLSM_PREFIX + "PR-";
	private static final String ENTRYCODE = "ENTRYCODE-";
	private static final String CROSS = "ABC/XYZ-";
	private static final String SEEDSOURCE = "SEEDSOURCE-";

	@Autowired
	private GermplasmDataManager germplasmManager;

	@Autowired
	private StudyEntryService service;

	private IntegrationTestDataInitializer testDataInitializer;

	private GermplasmTestDataGenerator germplasmTestDataGenerator;

	private DaoFactory daoFactory;

	@Before
	public void setup() {
		this.daoFactory = new DaoFactory(this.sessionProvder);
		this.testDataInitializer = new IntegrationTestDataInitializer(this.sessionProvder, this.workbenchSessionProvider);
		this.germplasmTestDataGenerator = new GermplasmTestDataGenerator(daoFactory);

	}

	@Test
	public void testCountStudyEntries() {
		final DmsProject study = this.createStudy();
		this.createTestGermplasm(study.getProjectId());
		Assert.assertEquals(StudyEntryServiceImplTest.NUMBER_OF_GERMPLASM.intValue(), this.service.countStudyEntries(study.getProjectId()));
	}

	@Test
	public void testDeleteStudyEntries() {
		final DmsProject study = this.createStudy();
		this.createTestGermplasm(study.getProjectId());

		this.service.deleteStudyEntries(study.getProjectId());
		Assert.assertEquals(0, this.service.countStudyEntries(study.getProjectId()));
	}

	@Test
	public void testSaveStudyEntries() {
		final DmsProject study = this.createStudy();
		final List<Integer> gids = this.createTestGermplasm(study.getProjectId());

		final Integer gid = gids.get(0);
		final int index = StudyEntryServiceImplTest.NUMBER_OF_GERMPLASM + 1;
		final StudyEntryDto studyEntryDto = this.createTestStudyEntry(index, gid);
		final List<StudyEntryDto> addedStudyEntries =
			this.service.saveStudyEntries(study.getProjectId(), Collections.singletonList(studyEntryDto));
		Assert.assertEquals(1, addedStudyEntries.size());
		final StudyEntryDto dto = addedStudyEntries.get(0);
		this.verifyStudyEntryDetails(gid, index, dto);
		Assert.assertEquals(index, this.service.countStudyEntries(study.getProjectId()));
	}

	@Test
	public void testGetNextEntryNumber() {
		final DmsProject study = this.createStudy();
		final List<Integer> gids = this.createTestGermplasm(study.getProjectId());

		final Integer gid = gids.get(0);
		final int index = StudyEntryServiceImplTest.NUMBER_OF_GERMPLASM + 1;
		final StudyEntryDto studyEntryDto = this.createTestStudyEntry(index, gid);
		final List<StudyEntryDto> addedStudyEntries =
			this.service.saveStudyEntries(study.getProjectId(), Collections.singletonList(studyEntryDto));
		final Integer nextEntryNumber = this.service.getNextEntryNumber(study.getProjectId());
		final int expectedValue = addedStudyEntries.get(0).getEntryNumber() + 1;
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
		final String crossExpansion = RandomStringUtils.randomAlphabetic(20);
		final StudyEntryDto newEntry = this.service.replaceStudyEntry(study.getProjectId(), oldEntry.getEntryId(), newGid, crossExpansion);

		Assert.assertNotEquals(oldEntry.getEntryId(), newEntry.getEntryId());
		Assert.assertEquals(StudyEntryServiceImplTest.GERMPLASM_PREFERRED_NAME_PREFIX + 1, newEntry.getDesignation());
		Assert.assertEquals(crossExpansion, newEntry.getProperties().get(TermId.CROSS.getId()).getValue());
		Assert.assertEquals(newGid, newEntry.getGid());
		// Some fields should have been copied from old entry
		Assert.assertEquals(oldEntry.getProperties().get(TermId.ENTRY_TYPE.getId()).getValue(),
			newEntry.getProperties().get(TermId.ENTRY_TYPE.getId()).getValue());
		Assert.assertEquals(oldEntry.getEntryNumber(), newEntry.getEntryNumber());
	}

	@Test(expected = MiddlewareRequestException.class)
	public void testReplaceStudyEntry_InvalidEntryId() {
		final DmsProject study = this.createStudy();
		final List<Integer> gids = this.createTestGermplasm(study.getProjectId());

		final StudyEntryDto dto = this.service.getStudyEntries(study.getProjectId()).get(1);
		Assert.assertNotNull(dto);
		final Integer newGid = gids.get(0);
		this.service.replaceStudyEntry(study.getProjectId(), dto.getEntryId() + 10, newGid, RandomStringUtils.random(5));
	}

	@Test(expected = MiddlewareRequestException.class)
	public void testReplaceStudyEntry_InvalidEntryIdForStudy() {
		final DmsProject study = this.createStudy();
		final List<Integer> gids = this.createTestGermplasm(study.getProjectId());

		final StudyEntryDto dto = this.service.getStudyEntries(study.getProjectId()).get(1);
		Assert.assertNotNull(dto);
		final Integer newGid = gids.get(0);
		this.service.replaceStudyEntry(study.getProjectId() + 1, dto.getEntryId(), newGid, RandomStringUtils.random(5));
	}

	@Test(expected = MiddlewareRequestException.class)
	public void testReplaceStudyEntry_SameGidAsExistingEntry() {
		final DmsProject study = this.createStudy();
		final List<Integer> gids = this.createTestGermplasm(study.getProjectId());

		final StudyEntryDto dto = this.service.getStudyEntries(study.getProjectId()).get(1);
		Assert.assertNotNull(dto);
		this.service.replaceStudyEntry(study.getProjectId() + 1, dto.getEntryId(), dto.getGid(), RandomStringUtils.random(5));
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

	private void verifyStudyEntryDetails(final Integer gid, final int index, final StudyEntryDto dto) {
		Assert.assertEquals(index, dto.getEntryNumber().intValue());
		Assert.assertEquals(StudyEntryServiceImplTest.GERMPLASM_PREFERRED_NAME_PREFIX + index, dto.getDesignation());
		Assert.assertEquals(StudyEntryServiceImplTest.SEEDSOURCE + index, dto.getProperties().get(TermId.SEED_SOURCE.getId()).getValue());
		Assert.assertEquals(StudyEntryServiceImplTest.CROSS + index, dto.getProperties().get(TermId.CROSS.getId()).getValue());
		Assert.assertEquals(gid, dto.getGid());
		// TODO: assert entry code from properties
//		Assert.assertEquals(StudyEntryServiceImplTest.ENTRYCODE + gid, dto.getEntryCode());
		Assert.assertNotNull(dto.getEntryId());
	}

	private List<Integer> createTestGermplasm(final int studyId) {
		final Germplasm parentGermplasm = this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames();
		final Integer[] gids = this.germplasmTestDataGenerator
			.createChildrenGermplasm(StudyEntryServiceImplTest.NUMBER_OF_GERMPLASM,
				StudyEntryServiceImplTest.GERMPLASM_PREFERRED_NAME_PREFIX,
				parentGermplasm);
		for (int i = 1; i <= StudyEntryServiceImplTest.NUMBER_OF_GERMPLASM; i++) {
			final Integer gid = gids[i - 1];
			this.daoFactory.getStockDao().save(new StockModel(studyId, this.createTestStudyEntry(i, gid)));
		}
		return Arrays.asList(gids);
	}

	private StudyEntryDto createTestStudyEntry(final int i, final Integer gid) {
		final StudyEntryDto studyEntryDto = new StudyEntryDto();
		studyEntryDto.setGid(gid);
		studyEntryDto.setEntryNumber(i);
		studyEntryDto.setDesignation(StudyEntryServiceImplTest.GERMPLASM_PREFERRED_NAME_PREFIX + i);

		studyEntryDto.getProperties()
			.put(TermId.CROSS.getId(), new StudyEntryPropertyData(null, TermId.CROSS.getId(), StudyEntryServiceImplTest.CROSS + i, null));
		studyEntryDto.getProperties()
			.put(TermId.ENTRY_TYPE.getId(), new StudyEntryPropertyData(null, TermId.ENTRY_TYPE.getId(),
				null, SystemDefinedEntryType.TEST_ENTRY.getEntryTypeCategoricalId()));
		studyEntryDto.getProperties()
			.put(TermId.SEED_SOURCE.getId(), new StudyEntryPropertyData(null, TermId.SEED_SOURCE.getId(),
				StudyEntryServiceImplTest.SEEDSOURCE + i, null));
		studyEntryDto.getProperties()
			.put(TermId.ENTRY_CODE.getId(), new StudyEntryPropertyData(null, TermId.ENTRY_CODE.getId(),
				StudyEntryServiceImplTest.ENTRYCODE + gid, null));


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
			new ProjectProperty(plotDataset, VariableType.GERMPLASM_DESCRIPTOR.getId(), "", 3, TermId.ENTRY_NO.getId(), "ENTRY_NO");
		final ProjectProperty seedSourceProp =
			new ProjectProperty(plotDataset, VariableType.GERMPLASM_DESCRIPTOR.getId(), "", 4, TermId.SEED_SOURCE.getId(),
				"SEED_SOURCE");
		final ProjectProperty crossProp =
			new ProjectProperty(plotDataset, VariableType.GERMPLASM_DESCRIPTOR.getId(), "", 5, TermId.CROSS.getId(), "CROSS");
		final ProjectProperty entryTypeProp =
			new ProjectProperty(plotDataset, VariableType.GERMPLASM_DESCRIPTOR.getId(), "", 6, TermId.ENTRY_TYPE.getId(),
				"ENTRY_TYPE");
		this.daoFactory.getProjectPropertyDAO().save(gidProp);
		this.daoFactory.getProjectPropertyDAO().save(desigProp);
		this.daoFactory.getProjectPropertyDAO().save(entryNoProp);
		this.daoFactory.getProjectPropertyDAO().save(seedSourceProp);
		this.daoFactory.getProjectPropertyDAO().save(crossProp);
		this.daoFactory.getProjectPropertyDAO().save(entryTypeProp);

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

}
