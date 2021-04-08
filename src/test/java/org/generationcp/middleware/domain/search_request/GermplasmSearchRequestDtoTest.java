package org.generationcp.middleware.domain.search_request;

import org.apache.commons.lang.RandomStringUtils;
import org.generationcp.middleware.domain.search_request.brapi.v1.GermplasmSearchRequestDto;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

@RunWith(MockitoJUnitRunner.class)
public class GermplasmSearchRequestDtoTest {

	@Test
	public void testNoFiltersSpecified() {
		final GermplasmSearchRequestDto dto = new GermplasmSearchRequestDto();
		Assert.assertTrue(dto.noFiltersSpecified());
	}

	@Test
	public void testNoFiltersSpecified_WithAccessionNumbers() {
		final GermplasmSearchRequestDto dto = new GermplasmSearchRequestDto();
		dto.setAccessionNumbers(Collections.singletonList(RandomStringUtils.randomAlphabetic(20)));
		Assert.assertFalse(dto.noFiltersSpecified());
	}

	@Test
	public void testNoFiltersSpecified_WithCommonCropNames() {
		final GermplasmSearchRequestDto dto = new GermplasmSearchRequestDto();
		dto.setCommonCropNames(Collections.singletonList(RandomStringUtils.randomAlphabetic(20)));
		Assert.assertFalse(dto.noFiltersSpecified());
	}

	@Test
	public void testNoFiltersSpecified_WithGermplasmDbIds() {
		final GermplasmSearchRequestDto dto = new GermplasmSearchRequestDto();
		dto.setGermplasmDbIds(Collections.singletonList(RandomStringUtils.randomAlphabetic(20)));
		Assert.assertFalse(dto.noFiltersSpecified());
	}

	@Test
	public void testNoFiltersSpecified_WithGermplasmPUIs() {
		final GermplasmSearchRequestDto dto = new GermplasmSearchRequestDto();
		dto.setGermplasmPUIs(Collections.singletonList(RandomStringUtils.randomAlphabetic(20)));
		Assert.assertFalse(dto.noFiltersSpecified());
	}

	@Test
	public void testNoFiltersSpecified_WithGermplasmNames() {
		final GermplasmSearchRequestDto dto = new GermplasmSearchRequestDto();
		dto.setGermplasmNames(Collections.singletonList(RandomStringUtils.randomAlphabetic(20)));
		Assert.assertFalse(dto.noFiltersSpecified());
	}

	@Test
	public void testNoFiltersSpecified_WithGermplasmSpecies() {
		final GermplasmSearchRequestDto dto = new GermplasmSearchRequestDto();
		dto.setGermplasmSpecies(Collections.singletonList(RandomStringUtils.randomAlphabetic(20)));
		Assert.assertFalse(dto.noFiltersSpecified());
	}

	@Test
	public void testNoFiltersSpecified_WithGermplasmGenus() {
		final GermplasmSearchRequestDto dto = new GermplasmSearchRequestDto();
		dto.setGermplasmGenus(Collections.singletonList(RandomStringUtils.randomAlphabetic(20)));
		Assert.assertFalse(dto.noFiltersSpecified());
	}

	@Test
	public void testNoFiltersSpecified_WithPreferredName() {
		final GermplasmSearchRequestDto dto = new GermplasmSearchRequestDto();
		dto.setPreferredName(RandomStringUtils.randomAlphabetic(20));
		Assert.assertFalse(dto.noFiltersSpecified());
	}

	@Test
	public void testNoFiltersSpecified_WithProgenyDbId() {
		final GermplasmSearchRequestDto dto = new GermplasmSearchRequestDto();
		dto.setProgenyDbId(RandomStringUtils.randomNumeric(3));
		Assert.assertFalse(dto.noFiltersSpecified());
	}

	@Test
	public void testNoFiltersSpecified_WithParentDbId() {
		final GermplasmSearchRequestDto dto = new GermplasmSearchRequestDto();
		dto.setParentDbId(RandomStringUtils.randomNumeric(3));
		Assert.assertFalse(dto.noFiltersSpecified());
	}

	@Test
	public void testNoFiltersSpecified_WithStudyDbId() {
		final GermplasmSearchRequestDto dto = new GermplasmSearchRequestDto();
		dto.setStudyDbId(RandomStringUtils.randomNumeric(3));
		Assert.assertFalse(dto.noFiltersSpecified());
	}

	@Test
	public void testNoFiltersSpecified_WithExternalReferenceId() {
		final GermplasmSearchRequestDto dto = new GermplasmSearchRequestDto();
		dto.setExternalReferenceId(RandomStringUtils.randomAlphabetic(20));
		Assert.assertFalse(dto.noFiltersSpecified());
	}

	@Test
	public void testNoFiltersSpecified_WithExternalReferenceSource() {
		final GermplasmSearchRequestDto dto = new GermplasmSearchRequestDto();
		dto.setExternalReferenceSource(RandomStringUtils.randomAlphabetic(20));
		Assert.assertFalse(dto.noFiltersSpecified());
	}

}
