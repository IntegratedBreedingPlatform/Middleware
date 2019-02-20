package org.generationcp.middleware.service.pedigree.string.processors;

import org.junit.Assert;
import org.generationcp.middleware.service.pedigree.GermplasmNode;
import org.generationcp.middleware.service.pedigree.PedigreeString;
import org.generationcp.middleware.service.pedigree.string.util.FixedLineNameResolver;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.base.Optional;


public class SimpleCrossProcessorTest {

	private FixedLineNameResolver fixedLineNameResolver;

	@Before
	public void setUp() {
		fixedLineNameResolver = Mockito.mock(FixedLineNameResolver.class);
		// We use any and null value because in the test be do not want any fixed line based name resolution
		Mockito.when(fixedLineNameResolver.nameTypeBasedResolution(Mockito.any(GermplasmNode.class))).thenReturn(
				Optional.<String>fromNullable(null));
		Mockito.when(fixedLineNameResolver.nameTypeBasedResolution(null)).thenReturn(
				Optional.<String>fromNullable(null));
	}


	@Test
	public void testCreationOfAStandardSimpleCross() throws Exception {

		// Create a double cross because I want to ensure that the Simple cross does not traverse the pedigree tree and just
		// does a cross on immediate parents
		final GermplasmNode parentGermplasmNode = PedigreeStringTestUtil.createDoubleCrossTestGermplasmTree();

		final SimpleCrossProcessor simpleCrossProcessor = new SimpleCrossProcessor();
		final PedigreeString processGermplasmNode = simpleCrossProcessor.processGermplasmNode(parentGermplasmNode,
				5,
				fixedLineNameResolver, false);
		Assert.assertEquals("Incorrect simple cross generation",
				"A/D",
				processGermplasmNode.getPedigree());
	}

	@Test
	public void testCreationOfAStandardSimpleCrossWithNullParents() throws Exception {

		// Create a double cross because I want to ensure that the Simple cross does not traverse the pedigree tree and just
		// does a cross on immediate parents
		final GermplasmNode parentGermplasmNode = PedigreeStringTestUtil.createDoubleCrossTestGermplasmTree();
		parentGermplasmNode.setFemaleParent(null);
		parentGermplasmNode.setMaleParent(null);

		final SimpleCrossProcessor simpleCrossProcessor = new SimpleCrossProcessor();
		final PedigreeString processGermplasmNode = simpleCrossProcessor.processGermplasmNode(parentGermplasmNode,
				5,
				fixedLineNameResolver, false);
		Assert.assertEquals("Incorrect simple cross generation with null parents",
				"Unknown/Unknown",
				processGermplasmNode.getPedigree());
	}
}
