
package org.generationcp.middleware.service.impl.study;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.DataSetupTest;
import org.generationcp.middleware.GermplasmTestDataGenerator;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.gms.SystemDefinedEntryType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.dms.StockProperty;
import org.generationcp.middleware.pojos.dms.StudyType;
import org.generationcp.middleware.service.api.study.StudyEntryDto;
import org.generationcp.middleware.service.api.study.StudyEntryPropertyData;
import org.generationcp.middleware.service.api.study.StudyEntryService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

	private GermplasmTestDataGenerator germplasmTestDataGenerator;
	private Integer studyId;
	private List<Integer> gids;
	private DaoFactory daoFactory;

	@Before
	public void setup() {
		if (this.germplasmTestDataGenerator == null) {
			this.germplasmTestDataGenerator = new GermplasmTestDataGenerator(this.germplasmManager);
		}
		this.daoFactory = new DaoFactory(this.sessionProvder);
		if (this.studyId == null) {
			final DmsProject study = new DmsProject(
				"TEST STUDY", "TEST DESCRIPTION", null, Collections.emptyList(),
				false,
				false, new StudyType(6), "20200606", null, null,
				null, "1");
			this.daoFactory.getDmsProjectDAO().save(study);
			this.studyId = study.getProjectId();
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
			this.daoFactory.getStockDao().save(new StockModel(studyId, this.createTestStudyEntry(i, gid)));
		}
	}

	private StudyEntryDto createTestStudyEntry(int i, Integer gid) {
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
	public void testCountStudyGermplasm() {
		Assert.assertEquals(StudyEntryServiceImplTest.NUMBER_OF_GERMPLASM.intValue(), this.service.countStudyEntries(this.studyId));
	}

	@Test
	public void testDeleteStudyGermplasm() {
		this.service.deleteStudyEntries(this.studyId);
		Assert.assertTrue(this.service.countStudyEntries(this.studyId) == 0);
	}

	@Test
	public void testSaveStudyGermplasm() {
		final Integer gid = gids.get(0);
		final int index = StudyEntryServiceImplTest.NUMBER_OF_GERMPLASM + 1;
		final StudyEntryDto studyEntryDto = createTestStudyEntry(index, gid);
		final List<StudyEntryDto> addedStudyEntries =
			this.service.saveStudyEntries(this.studyId, Collections.singletonList(studyEntryDto));
		Assert.assertEquals(1, addedStudyEntries.size());
		final StudyEntryDto dto = addedStudyEntries.get(0);
		this.verifyStudyGermplasmDetails(gid, index, dto);
		Assert.assertEquals(index, this.service.countStudyEntries(this.studyId));
	}

	@Test
	public void testReplaceStudyGermplasm() {
		final StudyEntryDto oldEntry = this.service.getStudyEntries(this.studyId, null, null).get(1);
		Assert.assertNotNull(oldEntry);
		final Integer newGid = gids.get(0);
		final String crossExpansion = RandomStringUtils.randomAlphabetic(20);
		this.service.replaceStudyEntry(this.studyId, oldEntry.getEntryId(), newGid, crossExpansion);

		final StudyEntryDto dto = this.service.getStudyEntries(this.studyId, null, null).get(1);
		Assert.assertNotEquals(oldEntry.getEntryId(), dto.getEntryId());
		Assert.assertEquals(StudyEntryServiceImplTest.GERMPLASM_PREFERRED_NAME_PREFIX + 1, dto.getDesignation());
		Assert.assertEquals(crossExpansion, dto.getProperties().get("CROSS").getValue());
		Assert.assertEquals(newGid, dto.getGid());
		// Some fields should have been copied from old entry
		Assert.assertEquals(oldEntry.getProperties().get("ENTRY_TYPE").getValue(), dto.getProperties().get("ENTRY_TYPE").getValue());
		Assert.assertEquals(oldEntry.getEntryNumber(), dto.getEntryNumber());
		Assert.assertEquals(oldEntry.getEntryCode(), dto.getEntryCode());
	}

	@Test(expected = MiddlewareRequestException.class)
	public void testReplaceStudyGermplasm_InvalidEntryId() {
		final StudyEntryDto dto = this.service.getStudyEntries(this.studyId, null, null).get(1);
		Assert.assertNotNull(dto);
		final Integer newGid = gids.get(0);
		this.service.replaceStudyEntry(this.studyId, dto.getEntryId() + 10, newGid, RandomStringUtils.random(5));
	}

	@Test(expected = MiddlewareRequestException.class)
	public void testReplaceStudyGermplasm_InvalidEntryIdForStudy() {
		final StudyEntryDto dto = this.service.getStudyEntries(this.studyId, null, null).get(1);
		Assert.assertNotNull(dto);
		final Integer newGid = gids.get(0);
		this.service.replaceStudyEntry(this.studyId + 1, dto.getEntryId(), newGid, RandomStringUtils.random(5));
	}

	@Test(expected = MiddlewareRequestException.class)
	public void testReplaceStudyGermplasm_SameGidAsExistingEntry() {
		final StudyEntryDto dto = this.service.getStudyEntries(this.studyId, null, null).get(1);
		Assert.assertNotNull(dto);
		this.service.replaceStudyEntry(this.studyId + 1, dto.getEntryId(), dto.getGid(), RandomStringUtils.random(5));
	}

	@Test
	public void testUpdateStudyEntryProperty() {

		final StockModel stockModel = this.daoFactory.getStockDao().getStocksForStudy(this.studyId).get(0);
		Optional<StockProperty> stockPropertyOptional =
			stockModel.getProperties().stream().filter(o -> o.getTypeId() == TermId.ENTRY_TYPE.getId()).findFirst();
		final StudyEntryPropertyData studyEntryPropertyData = new StudyEntryPropertyData();
		studyEntryPropertyData.setStudyEntryPropertyId(stockPropertyOptional.get().getStockPropId());
		studyEntryPropertyData.setVariableId(stockPropertyOptional.get().getTypeId());
		studyEntryPropertyData.setValue(String.valueOf(SystemDefinedEntryType.CHECK_ENTRY.getEntryTypeCategoricalId()));

		this.service.updateStudyEntryProperty(this.studyId, studyEntryPropertyData);

		Assert.assertEquals(String.valueOf(SystemDefinedEntryType.CHECK_ENTRY.getEntryTypeCategoricalId()),
			this.daoFactory.getStockPropertyDao().getById(studyEntryPropertyData.getStudyEntryPropertyId()).getValue());

	}

	private void verifyStudyGermplasmDetails(Integer gid, int index, StudyEntryDto dto) {
		Assert.assertEquals(index, dto.getEntryNumber().intValue());
		Assert.assertEquals(StudyEntryServiceImplTest.GERMPLASM_PREFERRED_NAME_PREFIX + index, dto.getDesignation());
		Assert.assertEquals(StudyEntryServiceImplTest.SEEDSOURCE + index, dto.getProperties().get(TermId.SEED_SOURCE.getId()).getValue());
		Assert.assertEquals(StudyEntryServiceImplTest.CROSS + index, dto.getProperties().get(TermId.CROSS.getId()).getValue());
		Assert.assertEquals(gid, dto.getGid());
		Assert.assertEquals(StudyEntryServiceImplTest.ENTRYCODE + gid, dto.getEntryCode());
		Assert.assertNotNull(dto.getEntryId());
	}

}
