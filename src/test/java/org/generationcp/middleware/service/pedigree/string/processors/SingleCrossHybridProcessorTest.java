
package org.generationcp.middleware.service.pedigree.string.processors;

import static org.junit.Assert.assertEquals;

import org.generationcp.middleware.service.pedigree.GermplasmNode;
import org.generationcp.middleware.service.pedigree.PedigreeString;
import org.generationcp.middleware.service.pedigree.string.processors.SingleCrossHybridProcessor;
import org.generationcp.middleware.service.pedigree.string.util.FixedLineNameResolver;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.base.Optional;

public class SingleCrossHybridProcessorTest {

	private FixedLineNameResolver fixedLineNameResolver;

	@Before
	public void setUp() {
		fixedLineNameResolver = Mockito.mock(FixedLineNameResolver.class);
		// We use any and null value because in the test be do not want any fixed line based name resolution
		Mockito.when(fixedLineNameResolver.nameTypeBasedResolution(Mockito.any(GermplasmNode.class))).thenReturn(
				Optional.<String>fromNullable(null));
	}

	@Test
	public void testCreationOfAStandardSingleCross() throws Exception {
		final SingleCrossHybridProcessor singleCrossHybridProcessor = new SingleCrossHybridProcessor();
		final GermplasmNode parentGermplasmNode = PedigreeStringTestUtil.createSingleCrossTestGermplasmNode();

		final PedigreeString resultantPedigreeString = singleCrossHybridProcessor.processGermplasmNode(parentGermplasmNode, new Integer(3), fixedLineNameResolver);
		assertEquals("Pedigree string is a cross of the female and male children ", "B/C", resultantPedigreeString.getPedigree());
		assertEquals("We have crated one cross.", 1, resultantPedigreeString.getNumberOfCrosses());

	}



	@Test
	public void testCreationOfAStandardSingleCrossWithParentsThatOnlyHaveGids() throws Exception {
		final SingleCrossHybridProcessor singleCrossHybridProcessor = new SingleCrossHybridProcessor();
		final GermplasmNode parentGermplasmNode =
				PedigreeStringTestUtil.createGermplasmNode(1, "A", PedigreeStringTestUtil.SINGLE_CROSS_METHOD_ID,
						PedigreeStringTestUtil.SINGLE_CROSS_METHOD_NAME, PedigreeStringTestUtil.SINGLE_CROSS_METHOD_NUMBER_OF_PROGENITOR);

		parentGermplasmNode.setFemaleParent(PedigreeStringTestUtil.createGermplasmNode(2, null,
				PedigreeStringTestUtil.BULK_OR_POPULATION_SAMPLE_METHOD_ID, PedigreeStringTestUtil.BULK_OR_POPULATION_SAMPLE_METHOD_NAME,
				PedigreeStringTestUtil.BULK_OR_POPULATION_SAMPLE_METHOD_NUMBER_OF_PROGENITOR));
		parentGermplasmNode.setMaleParent(PedigreeStringTestUtil.createGermplasmNode(3, null,
				PedigreeStringTestUtil.BULK_OR_POPULATION_SAMPLE_METHOD_ID, PedigreeStringTestUtil.BULK_OR_POPULATION_SAMPLE_METHOD_NAME,
				PedigreeStringTestUtil.BULK_OR_POPULATION_SAMPLE_METHOD_NUMBER_OF_PROGENITOR));

		final PedigreeString resultantPedigreeString = singleCrossHybridProcessor.processGermplasmNode(parentGermplasmNode, new Integer(3), fixedLineNameResolver);
		assertEquals("Pedigree string is 2/3 since we do not have a preferred name.", "2/3", resultantPedigreeString.getPedigree());
		assertEquals("We have crated one cross.", 1, resultantPedigreeString.getNumberOfCrosses());

	}

	@Test
	public void testCreationOfAStandardSingleCrossWithoutAnyParents() throws Exception {
		final SingleCrossHybridProcessor singleCrossHybridProcessor = new SingleCrossHybridProcessor();
		final GermplasmNode parentGermplasmNode =
				PedigreeStringTestUtil.createGermplasmNode(1, "A", PedigreeStringTestUtil.SINGLE_CROSS_METHOD_ID,
						PedigreeStringTestUtil.SINGLE_CROSS_METHOD_NAME, PedigreeStringTestUtil.SINGLE_CROSS_METHOD_NUMBER_OF_PROGENITOR);

		final PedigreeString resultantPedigreeString = singleCrossHybridProcessor.processGermplasmNode(parentGermplasmNode, new Integer(3), fixedLineNameResolver);
		assertEquals("Pedigree string is Unknown/Unknow since we cannot " + "determine the name or the GID", "Unknown/Unknown",
				resultantPedigreeString.getPedigree());
		assertEquals("We have crated one cross.", 1, resultantPedigreeString.getNumberOfCrosses());

	}

	@Test
	public void testCreationOfAStandardSingleCrossWithUnpopulatedParents() throws Exception {
		final SingleCrossHybridProcessor singleCrossHybridProcessor = new SingleCrossHybridProcessor();
		final GermplasmNode parentGermplasmNode =
				PedigreeStringTestUtil.createGermplasmNode(1, "A", PedigreeStringTestUtil.SINGLE_CROSS_METHOD_ID,
						PedigreeStringTestUtil.SINGLE_CROSS_METHOD_NAME, PedigreeStringTestUtil.SINGLE_CROSS_METHOD_NUMBER_OF_PROGENITOR);
		parentGermplasmNode.setFemaleParent(new GermplasmNode(null));
		parentGermplasmNode.setMaleParent(new GermplasmNode(null));

		final PedigreeString resultantPedigreeString = singleCrossHybridProcessor.processGermplasmNode(parentGermplasmNode, new Integer(3), fixedLineNameResolver);
		assertEquals("Pedigree string is Unknown/Unknow since we cannot " + "determine the name or the GID", "Unknown/Unknown",
				resultantPedigreeString.getPedigree());
		assertEquals("We have crated one cross.", 1, resultantPedigreeString.getNumberOfCrosses());

	}

}
