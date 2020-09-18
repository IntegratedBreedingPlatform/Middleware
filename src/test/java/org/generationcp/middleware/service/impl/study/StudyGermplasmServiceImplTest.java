
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
import org.generationcp.middleware.service.api.study.StudyEntryPropertyData;
import org.generationcp.middleware.service.api.study.StudyGermplasmDto;
import org.generationcp.middleware.service.api.study.StudyGermplasmService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * The class <code>StudyGermplasmListServiceImplTest</code> contains tests for the class <code>{@link StudyGermplasmServiceImpl}</code>.
 */
public class StudyGermplasmServiceImplTest extends IntegrationTestBase {

	private static final Integer NUMBER_OF_GERMPLASM = 5;

	private static final String GERMPLASM_PREFERRED_NAME_PREFIX = DataSetupTest.GERMPLSM_PREFIX + "PR-";
	private static final String ENTRYCODE = "ENTRYCODE-";
	private static final String CROSS = "ABC/XYZ-";
	private static final String SEEDSOURCE = "SEEDSOURCE-";

	@Autowired
	private GermplasmDataManager germplasmManager;

	@Autowired
	private StudyGermplasmService service;

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
			.createChildrenGermplasm(StudyGermplasmServiceImplTest.NUMBER_OF_GERMPLASM,
				StudyGermplasmServiceImplTest.GERMPLASM_PREFERRED_NAME_PREFIX,
				parentGermplasm);
		this.gids = Arrays.asList(gids);
		for (int i = 1; i <= StudyGermplasmServiceImplTest.NUMBER_OF_GERMPLASM; i++) {
			final Integer gid = gids[i - 1];
			this.daoFactory.getStockDao().save(new StockModel(studyId, this.createTestStudyGermplasm(i, gid)));
		}
	}

	private StudyGermplasmDto createTestStudyGermplasm(int i, Integer gid) {
		final StudyGermplasmDto germplasmDto = new StudyGermplasmDto();
		germplasmDto.setGermplasmId(gid);
		germplasmDto.setEntryNumber(i);
		germplasmDto.setDesignation(StudyGermplasmServiceImplTest.GERMPLASM_PREFERRED_NAME_PREFIX + i);
		germplasmDto.setEntryCode(StudyGermplasmServiceImplTest.ENTRYCODE + gid);
		germplasmDto.setCross(StudyGermplasmServiceImplTest.CROSS + i);
		germplasmDto.setEntryType(String.valueOf(SystemDefinedEntryType.TEST_ENTRY.getEntryTypeCategoricalId()));
		germplasmDto.setSeedSource(StudyGermplasmServiceImplTest.SEEDSOURCE + i);
		return germplasmDto;
	}

	@Test
	public void testGetAllStudyGermplasm() {
		final List<StudyGermplasmDto> studyGermplasm = this.service.getGermplasm(this.studyId);
		Assert.assertEquals(StudyGermplasmServiceImplTest.NUMBER_OF_GERMPLASM.intValue(), studyGermplasm.size());
		int index = 1;
		for (final StudyGermplasmDto dto : studyGermplasm) {
			final Integer gid = this.gids.get(index - 1);
			this.verifyStudyGermplasmDetails(gid, index++, dto);
		}
	}

	@Test
	public void testCountStudyGermplasm() {
		Assert.assertEquals(StudyGermplasmServiceImplTest.NUMBER_OF_GERMPLASM.intValue(), this.service.countStudyEntries(this.studyId));
	}

	@Test
	public void testDeleteStudyGermplasm() {
		this.service.deleteStudyEntries(this.studyId);
		Assert.assertTrue(this.service.countStudyEntries(this.studyId) == 0);
	}

	@Test
	public void testSaveStudyGermplasm() {
		final Integer gid = gids.get(0);
		final int index = StudyGermplasmServiceImplTest.NUMBER_OF_GERMPLASM + 1;
		final StudyGermplasmDto newStudyGermplasm = createTestStudyGermplasm(index, gid);
		final List<StudyGermplasmDto> addedStudyGermplasmList =
			this.service.saveStudyEntries(this.studyId, Collections.singletonList(newStudyGermplasm));
		Assert.assertEquals(1, addedStudyGermplasmList.size());
		final StudyGermplasmDto dto = addedStudyGermplasmList.get(0);
		this.verifyStudyGermplasmDetails(gid, index, dto);
		Assert.assertEquals(index, this.service.countStudyEntries(this.studyId));
	}

	@Test
	public void testGetStudyGermplasm() {
		final StudyGermplasmDto secondEntry = this.service.getGermplasm(this.studyId).get(1);
		Assert.assertNotNull(secondEntry);
		Assert.assertFalse(this.service.getStudyGermplasm(this.studyId, secondEntry.getEntryId() + 100).isPresent());
		final Optional<StudyGermplasmDto> studyGermplasm = this.service.getStudyGermplasm(this.studyId, secondEntry.getEntryId());
		Assert.assertTrue(studyGermplasm.isPresent());
		this.verifyStudyGermplasmDetails(secondEntry.getGermplasmId(), secondEntry.getEntryNumber(), studyGermplasm.get());
	}

	@Test
	public void testReplaceStudyGermplasm() {
		final StudyGermplasmDto oldEntry = this.service.getGermplasm(this.studyId).get(1);
		Assert.assertNotNull(oldEntry);
		final Integer newGid = gids.get(0);
		final String crossExpansion = RandomStringUtils.randomAlphabetic(20);
		this.service.replaceStudyGermplasm(this.studyId, oldEntry.getEntryId(), newGid, crossExpansion);

		final StudyGermplasmDto dto = this.service.getGermplasm(this.studyId).get(1);
		Assert.assertNotEquals(oldEntry.getEntryId(), dto.getEntryId());
		Assert.assertEquals(StudyGermplasmServiceImplTest.GERMPLASM_PREFERRED_NAME_PREFIX + 1, dto.getDesignation());
		Assert.assertEquals(crossExpansion, dto.getCross());
		Assert.assertEquals(newGid, dto.getGermplasmId());
		// Some fields should have been copied from old entry
		Assert.assertEquals(oldEntry.getEntryType(), dto.getEntryType());
		Assert.assertEquals(oldEntry.getEntryNumber(), dto.getEntryNumber());
		Assert.assertEquals(oldEntry.getEntryCode(), dto.getEntryCode());
	}

	@Test(expected = MiddlewareRequestException.class)
	public void testReplaceStudyGermplasm_InvalidEntryId() {
		final StudyGermplasmDto oldEntry = this.service.getGermplasm(this.studyId).get(1);
		Assert.assertNotNull(oldEntry);
		final Integer newGid = gids.get(0);
		this.service.replaceStudyGermplasm(this.studyId, oldEntry.getEntryId() + 10, newGid, RandomStringUtils.random(5));
	}

	@Test(expected = MiddlewareRequestException.class)
	public void testReplaceStudyGermplasm_InvalidEntryIdForStudy() {
		final StudyGermplasmDto oldEntry = this.service.getGermplasm(this.studyId).get(1);
		Assert.assertNotNull(oldEntry);
		final Integer newGid = gids.get(0);
		this.service.replaceStudyGermplasm(this.studyId + 1, oldEntry.getEntryId(), newGid, RandomStringUtils.random(5));
	}

	@Test(expected = MiddlewareRequestException.class)
	public void testReplaceStudyGermplasm_SameGidAsExistingEntry() {
		final StudyGermplasmDto oldEntry = this.service.getGermplasm(this.studyId).get(1);
		Assert.assertNotNull(oldEntry);
		this.service.replaceStudyGermplasm(this.studyId + 1, oldEntry.getEntryId(), oldEntry.getGermplasmId(), RandomStringUtils.random(5));
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

	private void verifyStudyGermplasmDetails(Integer gid, int index, StudyGermplasmDto dto) {
		Assert.assertEquals(index, dto.getEntryNumber().intValue());
		Assert.assertEquals(StudyGermplasmServiceImplTest.GERMPLASM_PREFERRED_NAME_PREFIX + index, dto.getDesignation());
		Assert.assertEquals(StudyGermplasmServiceImplTest.SEEDSOURCE + index, dto.getSeedSource());
		Assert.assertEquals(StudyGermplasmServiceImplTest.CROSS + index, dto.getCross());
		Assert.assertEquals(gid, dto.getGermplasmId());
		Assert.assertEquals(StudyGermplasmServiceImplTest.ENTRYCODE + gid, dto.getEntryCode());
		Assert.assertNotNull(dto.getEntryId());
	}

}
