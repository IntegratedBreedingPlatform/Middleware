package org.generationcp.middleware.ruleengine.naming.impl;

import org.generationcp.middleware.exceptions.InvalidGermplasmNameSettingException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.germplasm.GermplasmNameSetting;
import org.generationcp.middleware.service.api.KeySequenceRegisterService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class GermplasmNamingServiceImplTest {

	private static final String PREFIX = "ABH";
	private static final String SUFFIX = "CDE";
	private static final Integer NEXT_NUMBER = 31;

	@Mock
	private KeySequenceRegisterService keySequenceRegisterService;

	@Mock
	private GermplasmDataManager germplasmDataManager;

	@InjectMocks
	private GermplasmNamingServiceImpl germplasmNamingService;

	private GermplasmNameSetting germplasmNameSetting;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.germplasmNameSetting = this.createGermplasmNameSetting();
		Mockito.doReturn(NEXT_NUMBER).when(this.keySequenceRegisterService).getNextSequenceWithoutHibernate(PREFIX);
	}

	@Test
	public void testGetNumberWithLeadingZeroesAsStringDefault() {
		String formattedString = this.germplasmNamingService.getNumberWithLeadingZeroesAsString(1, 0);
		Assert.assertEquals("1", formattedString);

		formattedString = this.germplasmNamingService.getNumberWithLeadingZeroesAsString(1, null);
		Assert.assertEquals("1", formattedString);
	}

	@Test
	public void testGetNumberWithLeadingZeroesAsStringWithNumOfDigitsSpecified() {
		final String formattedString = this.germplasmNamingService.getNumberWithLeadingZeroesAsString(123, 8);
		Assert.assertEquals("00000123", formattedString);
	}

	@Test
	public void testGetNumberWithLeadingZeroesAsStringNumberGreaterThanhNumOfDigitsSpecified() {
		final String formattedString = this.germplasmNamingService.getNumberWithLeadingZeroesAsString(123, 2);
		Assert.assertEquals("123", formattedString);
	}

	@Test
	public void testGetNextNumberAndIncrementSequence() {
		final int nextNumber = this.germplasmNamingService.getNextNumberAndIncrementSequenceWithoutHibernate(PREFIX);
		Assert.assertEquals(GermplasmNamingServiceImplTest.NEXT_NUMBER.intValue(), nextNumber);
		Mockito.verify(this.keySequenceRegisterService).getNextSequenceWithoutHibernate(PREFIX);
		Mockito.verify(this.keySequenceRegisterService).saveLastSequenceUsedWithoutHibernate(PREFIX, nextNumber);
		Mockito.verifyZeroInteractions(this.germplasmDataManager);
	}

	private GermplasmNameSetting createGermplasmNameSetting() {
		final GermplasmNameSetting setting = new GermplasmNameSetting();

		setting.setPrefix(GermplasmNamingServiceImplTest.PREFIX);
		setting.setSuffix(GermplasmNamingServiceImplTest.SUFFIX);
		setting.setAddSpaceBetweenPrefixAndCode(true);
		setting.setAddSpaceBetweenSuffixAndCode(true);
		setting.setNumOfDigits(7);

		return setting;
	}

}
