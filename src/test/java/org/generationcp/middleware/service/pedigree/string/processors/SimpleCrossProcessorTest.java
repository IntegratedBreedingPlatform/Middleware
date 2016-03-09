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
	}


	@Test
	public void testCreationOfAStandardDoubleCross() throws Exception {

		// Create a double cross because I want to ensure that the Simple cross does not traverse the pedigree tree and just
		// does a cross on immediate parents
		final GermplasmNode parentGermplasmNode = PedigreeStringTestUtil.createDoubleCrossTestGermplasmNode();

		final SimpleCrossProcessor simpleCrossProcessor = new SimpleCrossProcessor();
		final PedigreeString processGermplasmNode = simpleCrossProcessor.processGermplasmNode(parentGermplasmNode,
				5,
				fixedLineNameResolver);
		Assert.assertEquals("The pedigree string generated must be a simply a crossing of the immediate parents",
				"A/D",
				processGermplasmNode.getPedigree());
	}
}
