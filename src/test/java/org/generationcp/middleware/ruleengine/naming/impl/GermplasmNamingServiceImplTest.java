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
		Mockito.doReturn(NEXT_NUMBER).when(this.keySequenceRegisterService).getNextSequence(PREFIX);
	}

	@Test
	public void testBuildDesignationNameInSequenceDefaultSetting() {
		final GermplasmNameSetting defaultSetting = new GermplasmNameSetting();
		defaultSetting.setPrefix(GermplasmNamingServiceImplTest.PREFIX);
		defaultSetting.setSuffix(GermplasmNamingServiceImplTest.SUFFIX);
		defaultSetting.setAddSpaceBetweenPrefixAndCode(false);
		defaultSetting.setAddSpaceBetweenSuffixAndCode(false);

		final int nextNumber = 10;
		final String designationName = this.germplasmNamingService.buildDesignationNameInSequence(nextNumber, defaultSetting);
		Assert.assertEquals(PREFIX + nextNumber + SUFFIX, designationName);
	}

	@Test
	public void testBuildDesignationNameInSequenceWithSpacesInPrefixSuffix() {
		final int nextNumber = 10;
		final String designationName =
			this.germplasmNamingService.buildDesignationNameInSequence(nextNumber, this.germplasmNameSetting);
		Assert.assertEquals(PREFIX + " 00000" + nextNumber + " " + SUFFIX, designationName);
	}

	@Test
	public void testBuildPrefixStringDefault() {
		final GermplasmNameSetting setting = new GermplasmNameSetting();
		setting.setPrefix(" A  ");
		final String prefix = this.germplasmNamingService.buildPrefixString(setting);
		Assert.assertEquals("A", prefix);
	}

	@Test
	public void testBuildPrefixStringWithSpace() {
		final GermplasmNameSetting setting = new GermplasmNameSetting();
		setting.setPrefix("   A");
		setting.setAddSpaceBetweenPrefixAndCode(true);
		final String prefix = this.germplasmNamingService.buildPrefixString(setting);
		Assert.assertEquals("A ", prefix);
	}

	@Test
	public void testBuildSuffixStringDefault() {
		final GermplasmNameSetting setting = new GermplasmNameSetting();
		setting.setSuffix("  B   ");
		final String suffix = this.germplasmNamingService.buildSuffixString(setting);
		Assert.assertEquals("B", suffix);
	}

	@Test
	public void testBuildSuffixStringWithSpace() {
		final GermplasmNameSetting setting = new GermplasmNameSetting();
		setting.setSuffix("   B   ");
		setting.setAddSpaceBetweenSuffixAndCode(true);
		final String suffix = this.germplasmNamingService.buildSuffixString(setting);
		Assert.assertEquals(" B", suffix);
	}

	@Test
	public void testGetNextSequence() {
		final int nextNumber = this.germplasmNamingService.getNextSequence(PREFIX);
		Assert.assertEquals(GermplasmNamingServiceImplTest.NEXT_NUMBER.intValue(), nextNumber);
		Mockito.verify(this.keySequenceRegisterService).getNextSequence(PREFIX);
		Mockito.verify(this.germplasmDataManager, Mockito.never()).getNextSequenceNumberAsString(ArgumentMatchers.anyString());
	}

	@Test
	public void testGetNextSequenceWhenPrefixIsEmpty() {
		final int nextNumber = this.germplasmNamingService.getNextSequence("");
		Assert.assertEquals(1, nextNumber);
		Mockito.verify(this.keySequenceRegisterService, Mockito.never()).getNextSequence(ArgumentMatchers.anyString());
		Mockito.verify(this.germplasmDataManager, Mockito.never()).getNextSequenceNumberAsString(ArgumentMatchers.anyString());
	}

	@Test
	public void testGetNextSequenceFromNames() {
		Mockito.doReturn(1).when(this.keySequenceRegisterService).getNextSequence(ArgumentMatchers.anyString());
		final Integer nextNumberFromNames = 101;
		Mockito.doReturn(String.valueOf(nextNumberFromNames)).when(this.germplasmDataManager).getNextSequenceNumberAsString(PREFIX);
		Assert.assertEquals(nextNumberFromNames.intValue(), this.germplasmNamingService.getNextSequence(PREFIX));

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
	public void testGetNextNameInSequenceWithNullStartNumber() {
		String nextNameInSequence = "";
		try {
			nextNameInSequence = this.germplasmNamingService.getNextNameInSequence(this.germplasmNameSetting);
		} catch (final InvalidGermplasmNameSettingException e) {
			Assert.fail("Not expecting InvalidGermplasmNameSettingException to be thrown but was thrown.");
		}
		Assert.assertEquals(this.buildExpectedNextName(), nextNameInSequence);
	}

	@Test
	public void testGetNextNameInSequenceWithZeroStartNumber() {
		final GermplasmNameSetting setting = this.createGermplasmNameSetting();
		setting.setStartNumber(0);
		String nextNameInSequence = "";
		try {
			nextNameInSequence = this.germplasmNamingService.getNextNameInSequence(setting);
		} catch (final InvalidGermplasmNameSettingException e) {
			Assert.fail("Not expecting InvalidGermplasmNameSettingException to be thrown but was thrown.");
		}
		Assert.assertEquals(this.buildExpectedNextName(), nextNameInSequence);
	}

	private String buildExpectedNextName() {
		return GermplasmNamingServiceImplTest.PREFIX + " 00000" + NEXT_NUMBER + " "
			+ GermplasmNamingServiceImplTest.SUFFIX;
	}

	@Test
	public void testGetNextNameInSequenceWhenSpecifiedSequenceStartingNumberIsGreater() {
		final GermplasmNameSetting setting = this.createGermplasmNameSetting();
		final int startNumber = 1000;
		setting.setStartNumber(startNumber);
		String nextNameInSequence = "";
		try {
			nextNameInSequence = this.germplasmNamingService.getNextNameInSequence(setting);
		} catch (final InvalidGermplasmNameSettingException e) {
			Assert.fail("Not expecting InvalidGermplasmNameSettingException to be thrown but was thrown.");
		}
		Assert.assertEquals("The specified starting sequence number will be used since it's larger.",
			GermplasmNamingServiceImplTest.PREFIX + " 000" + startNumber + " " + GermplasmNamingServiceImplTest.SUFFIX,
			nextNameInSequence);
	}

	@Test
	public void testGetNextNameInSequenceWhenSpecifiedSequenceStartingNumberIsLower() {
		final GermplasmNameSetting setting = this.createGermplasmNameSetting();
		final int startNumber = GermplasmNamingServiceImplTest.NEXT_NUMBER - 1;
		setting.setStartNumber(startNumber);
		try {
			this.germplasmNamingService.getNextNameInSequence(setting);
			Assert.fail("Expecting InvalidGermplasmNameSettingException to be thrown but was not.");
		} catch (final InvalidGermplasmNameSettingException e) {
			Assert.assertEquals(
				"Starting sequence number should be higher than or equal to next name in the sequence: " + this.buildExpectedNextName()
					+ ".", e.getMessage());
		}
	}

	@Test
	public void testGetNextNumberAndIncrementSequence() {
		final int nextNumber = this.germplasmNamingService.getNextNumberAndIncrementSequence(PREFIX);
		Assert.assertEquals(GermplasmNamingServiceImplTest.NEXT_NUMBER.intValue(), nextNumber);
		Mockito.verify(this.keySequenceRegisterService).getNextSequence(PREFIX);
		Mockito.verify(this.keySequenceRegisterService).saveLastSequenceUsed(PREFIX, nextNumber);
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
