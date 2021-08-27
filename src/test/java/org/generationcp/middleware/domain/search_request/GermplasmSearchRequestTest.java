package org.generationcp.middleware.domain.search_request;

import org.apache.commons.lang.RandomStringUtils;
import org.fest.util.Lists;
import org.generationcp.middleware.domain.search_request.brapi.v2.GermplasmSearchRequest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

@RunWith(MockitoJUnitRunner.class)
public class GermplasmSearchRequestTest {

	@Test
	public void testNoFiltersSpecified() {
		final GermplasmSearchRequest dto = new GermplasmSearchRequest();
		Assert.assertTrue(dto.noFiltersSpecified());
	}

	@Test
	public void testNoFiltersSpecified_WithAccessionNumbers() {
		final GermplasmSearchRequest dto = new GermplasmSearchRequest();
		dto.setAccessionNumbers(Collections.singletonList(RandomStringUtils.randomAlphabetic(20)));
		Assert.assertFalse(dto.noFiltersSpecified());
	}

	@Test
	public void testNoFiltersSpecified_WithCommonCropNames() {
		final GermplasmSearchRequest dto = new GermplasmSearchRequest();
		dto.setCommonCropNames(Collections.singletonList(RandomStringUtils.randomAlphabetic(20)));
		Assert.assertFalse(dto.noFiltersSpecified());
	}

	@Test
	public void testNoFiltersSpecified_WithGermplasmDbIds() {
		final GermplasmSearchRequest dto = new GermplasmSearchRequest();
		dto.setGermplasmDbIds(Collections.singletonList(RandomStringUtils.randomAlphabetic(20)));
		Assert.assertFalse(dto.noFiltersSpecified());
	}

	@Test
	public void testNoFiltersSpecified_WithGermplasmPUIs() {
		final GermplasmSearchRequest dto = new GermplasmSearchRequest();
		dto.setGermplasmPUIs(Collections.singletonList(RandomStringUtils.randomAlphabetic(20)));
		Assert.assertFalse(dto.noFiltersSpecified());
	}

	@Test
	public void testNoFiltersSpecified_WithGermplasmNames() {
		final GermplasmSearchRequest dto = new GermplasmSearchRequest();
		dto.setGermplasmNames(Collections.singletonList(RandomStringUtils.randomAlphabetic(20)));
		Assert.assertFalse(dto.noFiltersSpecified());
	}

	@Test
	public void testNoFiltersSpecified_WithSpecies() {
		final GermplasmSearchRequest dto = new GermplasmSearchRequest();
		dto.setSpecies(Collections.singletonList(RandomStringUtils.randomAlphabetic(20)));
		Assert.assertFalse(dto.noFiltersSpecified());
	}

	@Test
	public void testNoFiltersSpecified_WithGenus() {
		final GermplasmSearchRequest dto = new GermplasmSearchRequest();
		dto.setGenus(Collections.singletonList(RandomStringUtils.randomAlphabetic(20)));
		Assert.assertFalse(dto.noFiltersSpecified());
	}

	@Test
	public void testNoFiltersSpecified_WithProgenyDbIds() {
		final GermplasmSearchRequest dto = new GermplasmSearchRequest();
		dto.setProgenyDbIds(Lists.newArrayList(RandomStringUtils.randomNumeric(3)));
		Assert.assertFalse(dto.noFiltersSpecified());
	}

	@Test
	public void testNoFiltersSpecified_WithParentDbIds() {
		final GermplasmSearchRequest dto = new GermplasmSearchRequest();
		dto.setParentDbIds(Lists.newArrayList(RandomStringUtils.randomNumeric(3)));
		Assert.assertFalse(dto.noFiltersSpecified());
	}

	@Test
	public void testNoFiltersSpecified_WithStudyDbIds() {
		final GermplasmSearchRequest dto = new GermplasmSearchRequest();
		dto.setStudyDbIds(Lists.newArrayList(RandomStringUtils.randomNumeric(3)));
		Assert.assertFalse(dto.noFiltersSpecified());
	}

	@Test
	public void testNoFiltersSpecified_WithStudyNames() {
		final GermplasmSearchRequest dto = new GermplasmSearchRequest();
		dto.setStudyNames(Lists.newArrayList(RandomStringUtils.randomAlphabetic(3)));
		Assert.assertFalse(dto.noFiltersSpecified());
	}

	@Test
	public void testNoFiltersSpecified_WithExternalReferenceIds() {
		final GermplasmSearchRequest dto = new GermplasmSearchRequest();
		dto.setExternalReferenceIDs(Lists.newArrayList(RandomStringUtils.randomAlphabetic(20)));
		Assert.assertFalse(dto.noFiltersSpecified());
	}

	@Test
	public void testNoFiltersSpecified_WithExternalReferenceSources() {
		final GermplasmSearchRequest dto = new GermplasmSearchRequest();
		dto.setExternalReferenceSources(Lists.newArrayList(RandomStringUtils.randomAlphabetic(20)));
		Assert.assertFalse(dto.noFiltersSpecified());
	}

	@Test
	public void testNoFiltersSpecified_WithCollections() {
		final GermplasmSearchRequest dto = new GermplasmSearchRequest();
		dto.setCollections(Lists.newArrayList(RandomStringUtils.randomAlphabetic(20)));
		Assert.assertFalse(dto.noFiltersSpecified());
	}

	@Test
	public void testNoFiltersSpecified_WithSynonyms() {
		final GermplasmSearchRequest dto = new GermplasmSearchRequest();
		dto.setSynonyms(Lists.newArrayList(RandomStringUtils.randomAlphabetic(20)));
		Assert.assertFalse(dto.noFiltersSpecified());
	}

}
