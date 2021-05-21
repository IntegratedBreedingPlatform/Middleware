
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
	private Integer studyId;
	private List<Integer> gids;
	private DaoFactory daoFactory;
	private DmsProject study;
	private DmsProject plotDataset;

	@Before
	public void setup() {
		if (this.germplasmTestDataGenerator == null) {
			this.germplasmTestDataGenerator = new GermplasmTestDataGenerator(this.germplasmManager);
		}
		this.daoFactory = new DaoFactory(this.sessionProvder);

		this.testDataInitializer = new IntegrationTestDataInitializer(this.sessionProvder, this.workbenchSessionProvider);
		if (this.studyId == null) {
			this.study = new DmsProject(
				"TEST STUDY", "TEST DESCRIPTION", null, Collections.emptyList(),
				false,
				false, new StudyType(6), "20200606", null, null,
				null, "1");
			this.daoFactory.getDmsProjectDAO().save(this.study);
			this.studyId = this.study.getProjectId();
			this.plotDataset = new DmsProject(
					"TEST DATASET", "TEST DATASET DESC", null, Collections.emptyList(),
					false,
					false, new StudyType(6), "20200606", null, null,
					null, "1");
			this.plotDataset.setDatasetType(new DatasetType(DatasetTypeEnum.PLOT_DATA.getId()));
			this.plotDataset.setStudy(this.study);
			this.daoFactory.getDmsProjectDAO().save(this.plotDataset);


			final ProjectProperty gidProp = new ProjectProperty(this.plotDataset, VariableType.GERMPLASM_DESCRIPTOR.getId(), "", 1, TermId.GID.getId(), "GID");
			final ProjectProperty desigProp = new ProjectProperty(this.plotDataset, VariableType.GERMPLASM_DESCRIPTOR.getId(), "", 2, TermId.DESIG.getId(), "DESIG");
			final ProjectProperty entryNoProp = new ProjectProperty(this.plotDataset, VariableType.GERMPLASM_DESCRIPTOR.getId(), "", 3, TermId.ENTRY_NO.getId(), "ENTRY_NO");
			final ProjectProperty seedSourceProp = new ProjectProperty(this.plotDataset, VariableType.GERMPLASM_DESCRIPTOR.getId(), "", 4, TermId.SEED_SOURCE.getId(), "SEED_SOURCE");
			final ProjectProperty crossProp = new ProjectProperty(this.plotDataset, VariableType.GERMPLASM_DESCRIPTOR.getId(), "", 5, TermId.CROSS.getId(), "CROSS");
			final ProjectProperty entryTypeProp = new ProjectProperty(this.plotDataset, VariableType.GERMPLASM_DESCRIPTOR.getId(), "", 6, TermId.ENTRY_TYPE.getId(), "ENTRY_TYPE");
			this.daoFactory.getProjectPropertyDAO().save(gidProp);
			this.daoFactory.getProjectPropertyDAO().save(desigProp);
			this.daoFactory.getProjectPropertyDAO().save(entryNoProp);
			this.daoFactory.getProjectPropertyDAO().save(seedSourceProp);
			this.daoFactory.getProjectPropertyDAO().save(crossProp);
			this.daoFactory.getProjectPropertyDAO().save(entryTypeProp);
		}

		if (this.gids == null) {
			this.createTestGermplasm();
		}
	}

	private void createTestGermplasm() {
		final Germplasm parentGermplasm = this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames();
		final Integer[] gids = this.germplasmTestDataGenerator
			.createChildrenGermplasm(StudyEntryServiceImplTest.NUMBER_OF_GERMPLASM,
				StudyEntryServiceImplTest.GERMPLASM_PREFERRED_NAME_PREFIX,
				parentGermplasm);
		this.gids = Arrays.asList(gids);
		for (int i = 1; i <= StudyEntryServiceImplTest.NUMBER_OF_GERMPLASM; i++) {
			final Integer gid = gids[i - 1];
			this.daoFactory.getStockDao().save(new StockModel(this.studyId, this.createTestStudyEntry(i, gid)));
		}
	}

	private StudyEntryDto createTestStudyEntry(final int i, final Integer gid) {
		final StudyEntryDto studyEntryDto = new StudyEntryDto();
		studyEntryDto.setGid(gid);
		studyEntryDto.setEntryNumber(i);
		studyEntryDto.setDesignation(StudyEntryServiceImplTest.GERMPLASM_PREFERRED_NAME_PREFIX + i);
		studyEntryDto.setEntryCode(StudyEntryServiceImplTest.ENTRYCODE + gid);

		studyEntryDto.getProperties()
			.put(TermId.CROSS.getId(), new StudyEntryPropertyData(null, TermId.CROSS.getId(), StudyEntryServiceImplTest.CROSS + i));
		studyEntryDto.getProperties()
			.put(TermId.ENTRY_TYPE.getId(), new StudyEntryPropertyData(null, TermId.ENTRY_TYPE.getId(),
				String.valueOf(SystemDefinedEntryType.TEST_ENTRY.getEntryTypeCategoricalId())));
		studyEntryDto.getProperties()
			.put(TermId.SEED_SOURCE.getId(), new StudyEntryPropertyData(null, TermId.SEED_SOURCE.getId(),
				StudyEntryServiceImplTest.SEEDSOURCE + i));

		return studyEntryDto;
	}

	@Test
	public void testCountStudyEntries() {
		Assert.assertEquals(StudyEntryServiceImplTest.NUMBER_OF_GERMPLASM.intValue(), this.service.countStudyEntries(this.studyId));
	}

	@Test
	public void testDeleteStudyEntries() {
		this.service.deleteStudyEntries(this.studyId);
		Assert.assertEquals(0, this.service.countStudyEntries(this.studyId));
	}

	@Test
	public void testSaveStudyEntries() {
		final Integer gid = this.gids.get(0);
		final int index = StudyEntryServiceImplTest.NUMBER_OF_GERMPLASM + 1;
		final StudyEntryDto studyEntryDto = this.createTestStudyEntry(index, gid);
		final List<StudyEntryDto> addedStudyEntries =
			this.service.saveStudyEntries(this.studyId, Collections.singletonList(studyEntryDto));
		Assert.assertEquals(1, addedStudyEntries.size());
		final StudyEntryDto dto = addedStudyEntries.get(0);
		this.verifyStudyEntryDetails(gid, index, dto);
		Assert.assertEquals(index, this.service.countStudyEntries(this.studyId));
	}

	@Test
	public void testGetNextEntryNumber() {
		final Integer gid = this.gids.get(0);
		final int index = StudyEntryServiceImplTest.NUMBER_OF_GERMPLASM + 1;
		final StudyEntryDto studyEntryDto = this.createTestStudyEntry(index, gid);
		final List<StudyEntryDto> addedStudyEntries =
			this.service.saveStudyEntries(this.studyId, Collections.singletonList(studyEntryDto));
		final Integer nextEntryNumber = this.service.getNextEntryNumber(this.studyId);
		final int expectedValue = addedStudyEntries.get(0).getEntryNumber() + 1;
		Assert.assertEquals(Integer.toString(expectedValue), nextEntryNumber.toString());
	}

	@Test
	public void testHasUnassignedEntries() {
		Assert.assertTrue(this.service.hasUnassignedEntries(this.studyId));
		final List<StockModel> stocks = this.daoFactory.getStockDao().getStocksForStudy(this.studyId);
		this.addExperimentsForStocks(stocks);
		// Need to flush session to sync with underlying database before querying
		this.sessionProvder.getSession().flush();
		Assert.assertFalse(this.service.hasUnassignedEntries(this.studyId));
	}

	@Test
	public void testReplaceStudyEntries() {
		final StudyEntryDto oldEntry = this.service.getStudyEntries(this.studyId).get(1);
		Assert.assertNotNull(oldEntry);
		final Integer newGid = this.gids.get(0);
		final String crossExpansion = RandomStringUtils.randomAlphabetic(20);
		final StudyEntryDto newEntry = this.service.replaceStudyEntry(this.studyId, oldEntry.getEntryId(), newGid, crossExpansion);

		Assert.assertNotEquals(oldEntry.getEntryId(), newEntry.getEntryId());
		Assert.assertEquals(StudyEntryServiceImplTest.GERMPLASM_PREFERRED_NAME_PREFIX + 1, newEntry.getDesignation());
		Assert.assertEquals(crossExpansion, newEntry.getProperties().get(TermId.CROSS.getId()).getValue());
		Assert.assertEquals(newGid, newEntry.getGid());
		// Some fields should have been copied from old entry
		Assert.assertEquals(oldEntry.getProperties().get(TermId.ENTRY_TYPE.getId()).getValue(), newEntry.getProperties().get(TermId.ENTRY_TYPE.getId()).getValue());
		Assert.assertEquals(oldEntry.getEntryNumber(), newEntry.getEntryNumber());
		Assert.assertEquals(oldEntry.getEntryCode(), newEntry.getEntryCode());
	}

	@Test(expected = MiddlewareRequestException.class)
	public void testReplaceStudyEntry_InvalidEntryId() {
		final StudyEntryDto dto = this.service.getStudyEntries(this.studyId).get(1);
		Assert.assertNotNull(dto);
		final Integer newGid = this.gids.get(0);
		this.service.replaceStudyEntry(this.studyId, dto.getEntryId() + 10, newGid, RandomStringUtils.random(5));
	}

	@Test(expected = MiddlewareRequestException.class)
	public void testReplaceStudyEntry_InvalidEntryIdForStudy() {
		final StudyEntryDto dto = this.service.getStudyEntries(this.studyId).get(1);
		Assert.assertNotNull(dto);
		final Integer newGid = this.gids.get(0);
		this.service.replaceStudyEntry(this.studyId + 1, dto.getEntryId(), newGid, RandomStringUtils.random(5));
	}

	@Test(expected = MiddlewareRequestException.class)
	public void testReplaceStudyEntry_SameGidAsExistingEntry() {
		final StudyEntryDto dto = this.service.getStudyEntries(this.studyId).get(1);
		Assert.assertNotNull(dto);
		this.service.replaceStudyEntry(this.studyId + 1, dto.getEntryId(), dto.getGid(), RandomStringUtils.random(5));
	}

	void addExperimentsForStocks(final List<StockModel> stocks) {
		final Geolocation geolocation = this.testDataInitializer.createTestGeolocation("1", 101);
		for (final StockModel stock : stocks) {
			final ExperimentModel experimentModel = new ExperimentModel();
			experimentModel.setGeoLocation(geolocation);
			experimentModel.setTypeId(TermId.PLOT_EXPERIMENT.getId());
			experimentModel.setProject(this.study);
			experimentModel.setStock(stock);
			this.daoFactory.getExperimentDao().save(experimentModel);
		}
	}

	@Test
	public void testGetStudyEntries() {
		final List<StudyEntryDto> studyEntries = this.service.getStudyEntries(this.studyId);
		int index = 0;
		for (final StudyEntryDto dto : studyEntries) {
			this.verifyStudyEntryDetails(this.gids.get(index), index + 1, dto);
			index++;
		}
	}

	private void verifyStudyEntryDetails(final Integer gid, final int index, final StudyEntryDto dto) {
		Assert.assertEquals(index, dto.getEntryNumber().intValue());
		Assert.assertEquals(StudyEntryServiceImplTest.GERMPLASM_PREFERRED_NAME_PREFIX + index, dto.getDesignation());
		Assert.assertEquals(StudyEntryServiceImplTest.SEEDSOURCE + index, dto.getProperties().get(TermId.SEED_SOURCE.getId()).getValue());
		Assert.assertEquals(StudyEntryServiceImplTest.CROSS + index, dto.getProperties().get(TermId.CROSS.getId()).getValue());
		Assert.assertEquals(gid, dto.getGid());
		Assert.assertEquals(StudyEntryServiceImplTest.ENTRYCODE + gid, dto.getEntryCode());
		Assert.assertNotNull(dto.getEntryId());
	}

}
