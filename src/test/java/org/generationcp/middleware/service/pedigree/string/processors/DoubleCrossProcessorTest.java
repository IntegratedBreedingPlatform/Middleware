
package org.generationcp.middleware.service.pedigree.string.processors;

import static org.junit.Assert.assertEquals;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.generationcp.middleware.service.pedigree.GermplasmNode;
import org.generationcp.middleware.service.pedigree.PedigreeString;
import org.generationcp.middleware.service.pedigree.string.processors.DoubleCrossProcessor;
import org.generationcp.middleware.service.pedigree.string.util.FixedLineNameResolver;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.base.Optional;

public class DoubleCrossProcessorTest {

	private FixedLineNameResolver fixedLineNameResolver;

	final DoubleCrossProcessor doubleCrossProcessor = new DoubleCrossProcessor();

	@Before
	public void setUp() {
		fixedLineNameResolver = Mockito.mock(FixedLineNameResolver.class);
		// We use any and null value because in the test be do not want any fixed line based name resolution
		Mockito.when(fixedLineNameResolver.nameTypeBasedResolution(Mockito.any(GermplasmNode.class))).thenReturn(
				Optional.<String>fromNullable(null));
	}

	@Test
	public void testCreationOfAStandardDoubleCross() throws Exception {

		final GermplasmNode parentGermplasmNode = PedigreeStringTestUtil.createDoubleCrossTestGermplasmNode();

		final PedigreeString resultantPedigreeString =
				doubleCrossProcessor.processGermplasmNode(parentGermplasmNode, new Integer(3), fixedLineNameResolver);
		assertEquals("Pedigree string is a cross of the female and male children ", "B/C//E/F", resultantPedigreeString.getPedigree());
		assertEquals("We have crated one cross.", 2, resultantPedigreeString.getNumberOfCrosses());

	}



	@Test
	public void testCreationOfDoubleCrossWithMissingMale() throws Exception {
		final GermplasmNode femaleGermplasmNode =
				PedigreeStringTestUtil.createSingleCrossTestGermplasmNode(new ImmutablePair<Integer, String>(1, "A"),
						new ImmutablePair<Integer, String>(2, "B"), new ImmutablePair<Integer, String>(3, "C"));


		final GermplasmNode parentGermplasmNode =
				PedigreeStringTestUtil.createGermplasmNode(6, "G", PedigreeStringTestUtil.DOUBLE_CROSS_METHOD_ID,
						PedigreeStringTestUtil.DOUBLE_CROSS_METHOD_NAME, PedigreeStringTestUtil.DOUBLE_CROSS_METHOD_NUMBER_OF_PROGENITOR);
		parentGermplasmNode.setFemaleParent(femaleGermplasmNode);
		parentGermplasmNode.setMaleParent(null);
		final PedigreeString resultantPedigreeString =
				doubleCrossProcessor.processGermplasmNode(parentGermplasmNode, new Integer(3), fixedLineNameResolver);
		assertEquals("Pedigree string is a cross of the female and male children ", "B/C//Unknown", resultantPedigreeString.getPedigree());
		assertEquals("We have crated one cross.", 1, resultantPedigreeString.getNumberOfCrosses());

	}
}
