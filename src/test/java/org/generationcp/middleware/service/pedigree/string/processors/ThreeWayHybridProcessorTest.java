
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
		this.fixedLineNameResolver = Mockito.mock(FixedLineNameResolver.class);
		// We use any and null value because in the test be do not want any fixed line based name resolution
		Mockito.when(this.fixedLineNameResolver.nameTypeBasedResolution(Mockito.any(GermplasmNode.class))).thenReturn(
				Optional.<String>fromNullable(null));
		Mockito.when(this.fixedLineNameResolver.nameTypeBasedResolution(null)).thenReturn(
				Optional.<String>fromNullable(null));
	}

	@Test
	public void testCreationOfStandardThreeWayCross() {
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
				threeWayHybridProcessor.processGermplasmNode(threeWayCrossParentNode, new Integer(3), this.fixedLineNameResolver, false);
		assertEquals("Incorrect three way cross generation where female parent has the single cross", "B/C//D",
				resultantPedigreeString.getPedigree());
		assertEquals("We have crated one cross.", 2, resultantPedigreeString.getNumberOfCrosses());

	}

	@Test
	public void testCreationOfStandardThreeWayCrossWhereMaleParentHasTheSingleCross() {
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
				threeWayHybridProcessor.processGermplasmNode(threeWayCrossParentNode, new Integer(3), this.fixedLineNameResolver, false);
		assertEquals("Incorrect three way cross generation where male parent has the single cross", "D//B/C",
				resultantPedigreeString.getPedigree());
		assertEquals("We have created one cross.", 2, resultantPedigreeString.getNumberOfCrosses());

	}

	@Test
	public void testCreationOfThreeWayCrossWhereMaleParentIsMissing() {
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
				threeWayHybridProcessor.processGermplasmNode(threeWayCrossParentNode, new Integer(3), this.fixedLineNameResolver, false);
		assertEquals("Incorrect three way cross generation where male parent is null ", "B/C//Unknown",
				resultantPedigreeString.getPedigree());
		assertEquals("We have crated one cross.", 2, resultantPedigreeString.getNumberOfCrosses());

	}

	@Test
	public void testCreationOfThreeWayCrossWhereFemaleParentIsMissing() {

		final GermplasmNode threeWayCrossMaleNode = PedigreeStringTestUtil.createSingleCrossTestGermplasmTree();
		final GermplasmNode threeWayCrossParentNode =
				PedigreeStringTestUtil.createGermplasmNode(5, "E", PedigreeStringTestUtil.THREE_WAY_CROSS_METHOD_ID,
						PedigreeStringTestUtil.THREE_WAY_CROSS_METHOD_ID_METHOD_NAME,
						PedigreeStringTestUtil.THREE_WAY_CROSS_METHOD_NUMBER_OF_PROGENITOR);
		threeWayCrossParentNode.setFemaleParent(null );
		threeWayCrossParentNode.setMaleParent(threeWayCrossMaleNode);

		final ThreeWayHybridProcessor threeWayHybridProcessor = new ThreeWayHybridProcessor();

		final PedigreeString resultantPedigreeString =
				threeWayHybridProcessor.processGermplasmNode(threeWayCrossParentNode, new Integer(3), this.fixedLineNameResolver, false);
		assertEquals("Incorrect three way cross generation where female parent is null ", "Unknown//B/C",
				resultantPedigreeString.getPedigree());
		assertEquals("We have crated one cross.", 2, resultantPedigreeString.getNumberOfCrosses());

	}


	@Test
	public void testCreationOfThreeWayCrossWhereBothParentsAreMissing() {

		final GermplasmNode threeWayCrossParentNode =
				PedigreeStringTestUtil.createGermplasmNode(5, "E", PedigreeStringTestUtil.THREE_WAY_CROSS_METHOD_ID,
						PedigreeStringTestUtil.THREE_WAY_CROSS_METHOD_ID_METHOD_NAME,
						PedigreeStringTestUtil.THREE_WAY_CROSS_METHOD_NUMBER_OF_PROGENITOR);
		threeWayCrossParentNode.setFemaleParent(null );
		threeWayCrossParentNode.setMaleParent(null);

		final ThreeWayHybridProcessor threeWayHybridProcessor = new ThreeWayHybridProcessor();

		final PedigreeString resultantPedigreeString =
				threeWayHybridProcessor.processGermplasmNode(threeWayCrossParentNode, new Integer(3), this.fixedLineNameResolver, false);
		assertEquals("Incorrect three way cross generation where both parent is null ", "Unknown/Unknown",
				resultantPedigreeString.getPedigree());
		assertEquals("We have crated one cross.", 1, resultantPedigreeString.getNumberOfCrosses());

	}

	@Test
	public void testCreationOfThreeWayCrossWhereBothGrandParentsAreMissing() {

		final GermplasmNode femaleParent =
			PedigreeStringTestUtil.createGermplasmNode(1, "FemaleParent1", PedigreeStringTestUtil.SINGLE_CROSS_METHOD_ID,
				PedigreeStringTestUtil.SINGLE_CROSS_METHOD_NAME, PedigreeStringTestUtil.SINGLE_CROSS_METHOD_NUMBER_OF_PROGENITOR);
		femaleParent.setFemaleParent(this.getGrandParents("gFFemale", false));
		femaleParent.setMaleParent(this.getGrandParents("gFMale", false));

		final GermplasmNode maleParent =
			PedigreeStringTestUtil.createGermplasmNode(1, "MaleParent1", PedigreeStringTestUtil.SINGLE_CROSS_METHOD_ID,
				PedigreeStringTestUtil.SINGLE_CROSS_METHOD_NAME, PedigreeStringTestUtil.SINGLE_CROSS_METHOD_NUMBER_OF_PROGENITOR);
		maleParent.setFemaleParent(this.getGrandParents("gMFemale", false));
		maleParent.setMaleParent(this.getGrandParents("gMMale", false));

		final GermplasmNode threeWayCrossParentNode =
			PedigreeStringTestUtil.createGermplasmNode(5, "E", PedigreeStringTestUtil.THREE_WAY_CROSS_METHOD_ID,
				PedigreeStringTestUtil.THREE_WAY_CROSS_METHOD_ID_METHOD_NAME,
				PedigreeStringTestUtil.THREE_WAY_CROSS_METHOD_NUMBER_OF_PROGENITOR);
		threeWayCrossParentNode.setFemaleParent(femaleParent);
		threeWayCrossParentNode.setMaleParent(maleParent);

		final ThreeWayHybridProcessor threeWayHybridProcessor = new ThreeWayHybridProcessor();

		final PedigreeString resultantPedigreeString =
			threeWayHybridProcessor.processGermplasmNode(threeWayCrossParentNode, 4, this.fixedLineNameResolver, false);
		assertEquals("Incorrect three way cross generation where both parent is null ", "gFFemale/gFMale//MaleParent1",
			resultantPedigreeString.getPedigree());
		assertEquals("We have crated one cross.", 2, resultantPedigreeString.getNumberOfCrosses());

	}

	public GermplasmNode getGrandParents(final String name, final boolean withParent) {
		final GermplasmNode germplasm =
			PedigreeStringTestUtil.createGermplasmNode(6, name, PedigreeStringTestUtil.SINGLE_CROSS_METHOD_ID,
				PedigreeStringTestUtil.SINGLE_CROSS_METHOD_NAME, PedigreeStringTestUtil.SINGLE_CROSS_METHOD_NUMBER_OF_PROGENITOR);
		if(withParent) {
			germplasm.setMaleParent(this.getGrandParents("GGParent1", false));
			germplasm.setFemaleParent(this.getGrandParents("GGParent2", false));

		} else {
			germplasm.setMaleParent(null);
			germplasm.setFemaleParent(null);
		}

		return germplasm;
	}
}
