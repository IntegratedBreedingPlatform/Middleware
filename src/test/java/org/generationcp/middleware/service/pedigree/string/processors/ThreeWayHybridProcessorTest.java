
package org.generationcp.middleware.service.pedigree.string.processors;

import static org.junit.Assert.assertEquals;

import org.generationcp.middleware.service.pedigree.GermplasmNode;
import org.generationcp.middleware.service.pedigree.PedigreeString;
import org.generationcp.middleware.service.pedigree.string.processors.ThreeWayHybridProcessor;
import org.generationcp.middleware.service.pedigree.string.util.FixedLineNameResolver;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.base.Optional;

public class ThreeWayHybridProcessorTest {

	private FixedLineNameResolver fixedLineNameResolver;

	@Before
	public void setUp() {
		fixedLineNameResolver = Mockito.mock(FixedLineNameResolver.class);
		// We use any and null value because in the test be do not want any fixed line based name resolution
		Mockito.when(fixedLineNameResolver.nameTypeBasedResolution(Mockito.any(GermplasmNode.class))).thenReturn(
				Optional.<String>fromNullable(null));
	}

	@Test
	public void testCreationOfStandardThreeWayCross() throws Exception {
		final GermplasmNode threeWayCrossFemaleNode = PedigreeStringTestUtil.createSingleCrossTestGermplasmTree();
		final GermplasmNode threeWayCrossMaleNode =
				PedigreeStringTestUtil.createGermplasmNode(4, "D", PedigreeStringTestUtil.BULK_OR_POPULATION_SAMPLE_METHOD_ID,
						PedigreeStringTestUtil.BULK_OR_POPULATION_SAMPLE_METHOD_NAME,
						PedigreeStringTestUtil.BULK_OR_POPULATION_SAMPLE_METHOD_NUMBER_OF_PROGENITOR);

		final GermplasmNode threeWayCrossParentNode =
				PedigreeStringTestUtil.createGermplasmNode(5, "E", PedigreeStringTestUtil.THREE_WAY_CROSS_METHOD_ID,
						PedigreeStringTestUtil.THREE_WAY_CROSS_METHOD_ID_METHOD_NAME,
						PedigreeStringTestUtil.THREE_WAY_CROSS_METHOD_NUMBER_OF_PROGENITOR);
		threeWayCrossParentNode.setFemaleParent(threeWayCrossFemaleNode);
		threeWayCrossParentNode.setMaleParent(threeWayCrossMaleNode);

		final ThreeWayHybridProcessor threeWayHybridProcessor = new ThreeWayHybridProcessor();

		final PedigreeString resultantPedigreeString =
				threeWayHybridProcessor.processGermplasmNode(threeWayCrossParentNode, new Integer(3), fixedLineNameResolver);
		assertEquals("Incorrect three way cross generation where female parent has the single cross", "B/C//D",
				resultantPedigreeString.getPedigree());
		assertEquals("We have crated one cross.", 2, resultantPedigreeString.getNumberOfCrosses());

	}

	@Test
	public void testCreationOfStandardThreeWayCrossWhereMaleParentHasTheSingleCross() throws Exception {
		final GermplasmNode threeWayCrossFemaleNode =
				PedigreeStringTestUtil.createGermplasmNode(4, "D", PedigreeStringTestUtil.BULK_OR_POPULATION_SAMPLE_METHOD_ID,
						PedigreeStringTestUtil.BULK_OR_POPULATION_SAMPLE_METHOD_NAME,
						PedigreeStringTestUtil.BULK_OR_POPULATION_SAMPLE_METHOD_NUMBER_OF_PROGENITOR);

		final GermplasmNode threeWayCrossMaleNode = PedigreeStringTestUtil.createSingleCrossTestGermplasmTree();

		final GermplasmNode threeWayCrossParentNode =
				PedigreeStringTestUtil.createGermplasmNode(5, "E", PedigreeStringTestUtil.THREE_WAY_CROSS_METHOD_ID,
						PedigreeStringTestUtil.THREE_WAY_CROSS_METHOD_ID_METHOD_NAME,
						PedigreeStringTestUtil.THREE_WAY_CROSS_METHOD_NUMBER_OF_PROGENITOR);
		threeWayCrossParentNode.setFemaleParent(threeWayCrossFemaleNode );
		threeWayCrossParentNode.setMaleParent(threeWayCrossMaleNode);

		final ThreeWayHybridProcessor threeWayHybridProcessor = new ThreeWayHybridProcessor();

		final PedigreeString resultantPedigreeString =
				threeWayHybridProcessor.processGermplasmNode(threeWayCrossParentNode, new Integer(3), fixedLineNameResolver);
		assertEquals("Incorrect three way cross generation where male parent has the single cross", "B/C//D",
				resultantPedigreeString.getPedigree());
		assertEquals("We have crated one cross.", 2, resultantPedigreeString.getNumberOfCrosses());

	}

	@Test
	public void testCreationOfThreeWayCrossWhereMaleParentIsMissing() throws Exception {
		final GermplasmNode threeWayCrossFemaleNode =
				PedigreeStringTestUtil.createSingleCrossTestGermplasmTree();

		final GermplasmNode threeWayCrossParentNode =
				PedigreeStringTestUtil.createGermplasmNode(5, "E", PedigreeStringTestUtil.THREE_WAY_CROSS_METHOD_ID,
						PedigreeStringTestUtil.THREE_WAY_CROSS_METHOD_ID_METHOD_NAME,
						PedigreeStringTestUtil.THREE_WAY_CROSS_METHOD_NUMBER_OF_PROGENITOR);
		threeWayCrossParentNode.setFemaleParent(threeWayCrossFemaleNode );
		threeWayCrossParentNode.setMaleParent(null);

		final ThreeWayHybridProcessor threeWayHybridProcessor = new ThreeWayHybridProcessor();

		final PedigreeString resultantPedigreeString =
				threeWayHybridProcessor.processGermplasmNode(threeWayCrossParentNode, new Integer(3), fixedLineNameResolver);
		assertEquals("Incorrect three way cross generation where male parent is null ", "B/C//Unknown",
				resultantPedigreeString.getPedigree());
		assertEquals("We have crated one cross.", 2, resultantPedigreeString.getNumberOfCrosses());

	}

	@Test
	public void testCreationOfThreeWayCrossWhereFemaleParentIsMissing() throws Exception {

		final GermplasmNode threeWayCrossMaleNode = PedigreeStringTestUtil.createSingleCrossTestGermplasmTree();
		final GermplasmNode threeWayCrossParentNode =
				PedigreeStringTestUtil.createGermplasmNode(5, "E", PedigreeStringTestUtil.THREE_WAY_CROSS_METHOD_ID,
						PedigreeStringTestUtil.THREE_WAY_CROSS_METHOD_ID_METHOD_NAME,
						PedigreeStringTestUtil.THREE_WAY_CROSS_METHOD_NUMBER_OF_PROGENITOR);
		threeWayCrossParentNode.setFemaleParent(null );
		threeWayCrossParentNode.setMaleParent(threeWayCrossMaleNode);

		final ThreeWayHybridProcessor threeWayHybridProcessor = new ThreeWayHybridProcessor();

		final PedigreeString resultantPedigreeString =
				threeWayHybridProcessor.processGermplasmNode(threeWayCrossParentNode, new Integer(3), fixedLineNameResolver);
		assertEquals("Incorrect three way cross generation where female parent is null ", "B/C//Unknown",
				resultantPedigreeString.getPedigree());
		assertEquals("We have crated one cross.", 2, resultantPedigreeString.getNumberOfCrosses());

	}


	@Test
	public void testCreationOfThreeWayCrossWhereBothParentsAreMissing() throws Exception {

		final GermplasmNode threeWayCrossParentNode =
				PedigreeStringTestUtil.createGermplasmNode(5, "E", PedigreeStringTestUtil.THREE_WAY_CROSS_METHOD_ID,
						PedigreeStringTestUtil.THREE_WAY_CROSS_METHOD_ID_METHOD_NAME,
						PedigreeStringTestUtil.THREE_WAY_CROSS_METHOD_NUMBER_OF_PROGENITOR);
		threeWayCrossParentNode.setFemaleParent(null );
		threeWayCrossParentNode.setMaleParent(null);

		final ThreeWayHybridProcessor threeWayHybridProcessor = new ThreeWayHybridProcessor();

		final PedigreeString resultantPedigreeString =
				threeWayHybridProcessor.processGermplasmNode(threeWayCrossParentNode, new Integer(3), fixedLineNameResolver);
		assertEquals("Incorrect three way cross generation where both parent is null ", "Unknown/Unknown",
				resultantPedigreeString.getPedigree());
		assertEquals("We have crated one cross.", 1, resultantPedigreeString.getNumberOfCrosses());

	}
}
