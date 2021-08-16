package org.generationcp.middleware.domain.search_request;

import org.apache.commons.lang.RandomStringUtils;
import org.fest.util.Lists;
import org.generationcp.middleware.domain.search_request.brapi.v2.GermplasmSearchRequestDto;
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
		dto.setSpecies(Collections.singletonList(RandomStringUtils.randomAlphabetic(20)));
		Assert.assertFalse(dto.noFiltersSpecified());
	}

	@Test
	public void testNoFiltersSpecified_WithGermplasmGenus() {
		final GermplasmSearchRequestDto dto = new GermplasmSearchRequestDto();
		dto.setGenus(Collections.singletonList(RandomStringUtils.randomAlphabetic(20)));
		Assert.assertFalse(dto.noFiltersSpecified());
	}

	@Test
	public void testNoFiltersSpecified_WithProgenyDbId() {
		final GermplasmSearchRequestDto dto = new GermplasmSearchRequestDto();
		dto.setProgenyDbIds(Lists.newArrayList(RandomStringUtils.randomNumeric(3)));
		Assert.assertFalse(dto.noFiltersSpecified());
	}

	@Test
	public void testNoFiltersSpecified_WithParentDbId() {
		final GermplasmSearchRequestDto dto = new GermplasmSearchRequestDto();
		dto.setParentDbIds(Lists.newArrayList(RandomStringUtils.randomNumeric(3)));
		Assert.assertFalse(dto.noFiltersSpecified());
	}

	@Test
	public void testNoFiltersSpecified_WithStudyDbId() {
		final GermplasmSearchRequestDto dto = new GermplasmSearchRequestDto();
		dto.setStudyDbIds(Lists.newArrayList(RandomStringUtils.randomNumeric(3)));
		Assert.assertFalse(dto.noFiltersSpecified());
	}

	@Test
	public void testNoFiltersSpecified_WithExternalReferenceId() {
		final GermplasmSearchRequestDto dto = new GermplasmSearchRequestDto();
		dto.setExternalReferenceIDs(Lists.newArrayList(RandomStringUtils.randomAlphabetic(20)));
		Assert.assertFalse(dto.noFiltersSpecified());
	}

	@Test
	public void testNoFiltersSpecified_WithExternalReferenceSource() {
		final GermplasmSearchRequestDto dto = new GermplasmSearchRequestDto();
		dto.setExternalReferenceSources(Lists.newArrayList(RandomStringUtils.randomAlphabetic(20)));
		Assert.assertFalse(dto.noFiltersSpecified());
	}

}
