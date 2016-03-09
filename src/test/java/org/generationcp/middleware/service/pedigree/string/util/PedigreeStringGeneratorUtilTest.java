
package org.generationcp.middleware.service.pedigree.string.util;

import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.service.pedigree.GermplasmNode;
import org.generationcp.middleware.service.pedigree.PedigreeDataManagerFactory;
import org.generationcp.middleware.service.pedigree.PedigreeString;
import org.generationcp.middleware.service.pedigree.cache.keys.CropNameTypeKey;
import org.generationcp.middleware.service.pedigree.string.util.PedigreeStringGeneratorUtil;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.generationcp.middleware.util.cache.FunctionBasedGuavaCacheLoader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.base.Optional;

public class PedigreeStringGeneratorUtilTest {

	private static final String CROP_NAME = "maize";
	private static final String LEVEL_1_FIXED_LINE_NAME = "Level 1";
	private PedigreeString femalePedigreeString;
	private PedigreeString malePedigreeString;

	private CrossExpansionProperties mockCrossExpansionProperties;
	private ImmutablePair<String, String> defaultBackcrossNotation;
	private ImmutablePair<String, String> customBackcrossNotation;

	@Before
	public void setUp() throws Exception {
		femalePedigreeString = new PedigreeString();
		malePedigreeString = new PedigreeString();

		mockCrossExpansionProperties = Mockito.mock(CrossExpansionProperties.class);
		defaultBackcrossNotation = new ImmutablePair<String, String>("*", "*");
		customBackcrossNotation = new ImmutablePair<String, String>("<", ">");

	}

	@Test
	public void testGerneratePedigreeStringWithSingleCross() throws Exception {
		femalePedigreeString.setPedigree("A");
		malePedigreeString.setPedigree("B");
		final String resultantPedigree = PedigreeStringGeneratorUtil.gerneratePedigreeString(femalePedigreeString, malePedigreeString);
		Assert.assertEquals("A/B", resultantPedigree);
	}

	@Test
	public void testGerneratePedigreeStringWithOnePerviousCrosses() throws Exception {
		femalePedigreeString.setPedigree("A/B");
		femalePedigreeString.setNumberOfCrosses(1);
		malePedigreeString.setPedigree("C");
		final String resultantPedigree = PedigreeStringGeneratorUtil.gerneratePedigreeString(femalePedigreeString, malePedigreeString);
		Assert.assertEquals("A/B//C", resultantPedigree);
	}

	@Test
	public void testGerneratePedigreeStringWithTwoPerviousCrosses() throws Exception {
		femalePedigreeString.setPedigree("A/B//C");
		femalePedigreeString.setNumberOfCrosses(2);
		malePedigreeString.setPedigree("D");
		final String resultantPedigree = PedigreeStringGeneratorUtil.gerneratePedigreeString(femalePedigreeString, malePedigreeString);
		Assert.assertEquals("A/B//C///D", resultantPedigree);
	}

	@Test
	public void testGerneratePedigreeStringWithThreePerviousCrosses() throws Exception {
		femalePedigreeString.setPedigree("A/B//C///D");
		femalePedigreeString.setNumberOfCrosses(3);
		malePedigreeString.setPedigree("E");
		final String resultantPedigree = PedigreeStringGeneratorUtil.gerneratePedigreeString(femalePedigreeString, malePedigreeString);
		Assert.assertEquals("A/B//C///D/4/E", resultantPedigree);
	}

	@Test
	public void testGernerateBackcrossPedigreeStringWithMaleRecurringParent() throws Exception {
		femalePedigreeString.setPedigree("Donor");
		malePedigreeString.setPedigree("Recurring");
		Mockito.when(mockCrossExpansionProperties.getBackcrossNotation(CROP_NAME)).thenReturn(defaultBackcrossNotation);

		final String gernerateBackcrossPedigreeString = generateBackCrossString(femalePedigreeString, malePedigreeString, false);
		Assert.assertEquals("Recurring/5*Donor", gernerateBackcrossPedigreeString);
	}

	@Test
	public void testGernerateBackcrossPedigreeStringWithFemaleRecurringParent() throws Exception {
		femalePedigreeString.setPedigree("Recurring");
		malePedigreeString.setPedigree("Donor");
		Mockito.when(mockCrossExpansionProperties.getBackcrossNotation(CROP_NAME)).thenReturn(defaultBackcrossNotation);

		final String gernerateBackcrossPedigreeString = generateBackCrossString(femalePedigreeString, malePedigreeString, true);
		Assert.assertEquals("Donor*5/Recurring", gernerateBackcrossPedigreeString);
	}

	@Test
	public void testGernerateBackcrossPedigreeStringWithFemaleRecurringParentAndCustomBackcrossNotation() throws Exception {
		femalePedigreeString.setPedigree("Donor");
		malePedigreeString.setPedigree("Recurring");
		Mockito.when(mockCrossExpansionProperties.getBackcrossNotation(CROP_NAME)).thenReturn(customBackcrossNotation);

		final String gernerateBackcrossPedigreeString = generateBackCrossString(femalePedigreeString, malePedigreeString, false);
		Assert.assertEquals("Recurring/5>Donor", gernerateBackcrossPedigreeString);
	}

	@Test
	public void testGernerateBackcrossPedigreeStringWithMaleRecurringParentAndCustomBackcrossNotation() throws Exception {
		femalePedigreeString.setPedigree("Recurring");
		malePedigreeString.setPedigree("Donor");
		Mockito.when(mockCrossExpansionProperties.getBackcrossNotation(CROP_NAME)).thenReturn(customBackcrossNotation);

		final String gernerateBackcrossPedigreeString = generateBackCrossString(femalePedigreeString, malePedigreeString, true);
		Assert.assertEquals("Donor<5/Recurring", gernerateBackcrossPedigreeString);
	}

	@SuppressWarnings("unchecked")
	private String generateBackCrossString(final PedigreeString donorParent, final PedigreeString recurringParent,
			final boolean isFemaleRecurringParent) {
		final FixedLineNameResolver fixedLineNameResolver =
				new FixedLineNameResolver(mockCrossExpansionProperties, Mockito.mock(PedigreeDataManagerFactory.class),
						(FunctionBasedGuavaCacheLoader<CropNameTypeKey, List<Integer>>) Mockito.mock(FunctionBasedGuavaCacheLoader.class), CROP_NAME);
		final String gernerateBackcrossPedigreeString =
				PedigreeStringGeneratorUtil.gernerateBackcrossPedigreeString(donorParent, recurringParent, fixedLineNameResolver, 5,
						isFemaleRecurringParent);
		return gernerateBackcrossPedigreeString;
	}

	@Test
	public void testGettingNoFixedlineName() throws Exception {
		final GermplasmNode germplasmNode = new GermplasmNode(new Germplasm());
		final FixedLineNameResolver mockFixedLineNameResolver = Mockito.mock(FixedLineNameResolver.class);
		Mockito.when(mockFixedLineNameResolver.nameTypeBasedResolution(Mockito.eq(germplasmNode))).thenReturn(
				Optional.<String>fromNullable(null));
		final Optional<PedigreeString> fixedLineName =
				PedigreeStringGeneratorUtil.getFixedLineName(germplasmNode, mockFixedLineNameResolver);
		Assert.assertEquals("Ensure that we do not have a fixed line name.", false, fixedLineName.isPresent());

	}

	@Test
	public void testGettingFixedlineName() throws Exception {
		final GermplasmNode germplasmNode = new GermplasmNode(new Germplasm());
		final FixedLineNameResolver mockFixedLineNameResolver = Mockito.mock(FixedLineNameResolver.class);
		Mockito.when(mockFixedLineNameResolver.nameTypeBasedResolution(Mockito.eq(germplasmNode))).thenReturn(
				Optional.<String>fromNullable(LEVEL_1_FIXED_LINE_NAME));
		final Optional<PedigreeString> fixedLineName =
				PedigreeStringGeneratorUtil.getFixedLineName(germplasmNode, mockFixedLineNameResolver);
		Assert.assertEquals("Ensure that we do have a fixed line name.", true, fixedLineName.isPresent());
		Assert.assertEquals(String.format("Ensure that we do have a fixed line name with the name ", LEVEL_1_FIXED_LINE_NAME),
				LEVEL_1_FIXED_LINE_NAME, fixedLineName.get().getPedigree());

	}
}
