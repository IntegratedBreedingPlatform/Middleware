
package org.generationcp.middleware.service.pedigree.string.processors;

import static org.junit.Assert.assertEquals;

import org.generationcp.middleware.service.pedigree.GermplasmNode;
import org.generationcp.middleware.service.pedigree.PedigreeString;
import org.generationcp.middleware.service.pedigree.string.util.FixedLineNameResolver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.google.common.base.Optional;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SingleCrossHybridProcessorTest {

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
	public void testCreationOfAStandardSingleCross() {
		final SingleCrossHybridProcessor singleCrossHybridProcessor = new SingleCrossHybridProcessor();
		final GermplasmNode parentGermplasmNode = PedigreeStringTestUtil.createSingleCrossTestGermplasmTree();

		final PedigreeString resultantPedigreeString = singleCrossHybridProcessor.processGermplasmNode(parentGermplasmNode, new Integer(3),
			this.fixedLineNameResolver, false);
		assertEquals("Pedigree string is a cross of the female and male children ", "B/C", resultantPedigreeString.getPedigree());
		assertEquals("We have crated one cross.", 1, resultantPedigreeString.getNumberOfCrosses());

	}



	@Test
	public void testCreationOfAStandardSingleCrossWithParentsThatOnlyHaveGids() {
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

		final PedigreeString resultantPedigreeString = singleCrossHybridProcessor.processGermplasmNode(parentGermplasmNode, new Integer(3),
			this.fixedLineNameResolver, false);
		assertEquals("Pedigree string is 2/3 since we do not have a preferred name.", "2/3", resultantPedigreeString.getPedigree());
		assertEquals("We have crated one cross.", 1, resultantPedigreeString.getNumberOfCrosses());

	}

	@Test
	public void testCreationOfAStandardSingleCrossWithoutAnyParents() {
		final SingleCrossHybridProcessor singleCrossHybridProcessor = new SingleCrossHybridProcessor();
		final GermplasmNode parentGermplasmNode =
				PedigreeStringTestUtil.createGermplasmNode(1, "A", PedigreeStringTestUtil.SINGLE_CROSS_METHOD_ID,
						PedigreeStringTestUtil.SINGLE_CROSS_METHOD_NAME, PedigreeStringTestUtil.SINGLE_CROSS_METHOD_NUMBER_OF_PROGENITOR);

		final PedigreeString resultantPedigreeString = singleCrossHybridProcessor.processGermplasmNode(parentGermplasmNode, new Integer(3),
			this.fixedLineNameResolver, false);
		assertEquals("Pedigree string is Unknown/Unknow since we cannot " + "determine the name or the GID", "Unknown/Unknown",
				resultantPedigreeString.getPedigree());
		assertEquals("We have crated one cross.", 1, resultantPedigreeString.getNumberOfCrosses());

	}

	@Test
	public void testCreationOfAStandardSingleCrossWithUnpopulatedParents() {
		final SingleCrossHybridProcessor singleCrossHybridProcessor = new SingleCrossHybridProcessor();
		final GermplasmNode parentGermplasmNode =
				PedigreeStringTestUtil.createGermplasmNode(1, "A", PedigreeStringTestUtil.SINGLE_CROSS_METHOD_ID,
						PedigreeStringTestUtil.SINGLE_CROSS_METHOD_NAME, PedigreeStringTestUtil.SINGLE_CROSS_METHOD_NUMBER_OF_PROGENITOR);
		parentGermplasmNode.setFemaleParent(new GermplasmNode(null));
		parentGermplasmNode.setMaleParent(new GermplasmNode(null));

		final PedigreeString resultantPedigreeString = singleCrossHybridProcessor.processGermplasmNode(parentGermplasmNode, new Integer(3),
			this.fixedLineNameResolver, false);
		assertEquals("Pedigree string is Unknown/Unknow since we cannot " + "determine the name or the GID", "Unknown/Unknown",
				resultantPedigreeString.getPedigree());
		assertEquals("We have crated one cross.", 1, resultantPedigreeString.getNumberOfCrosses());

	}

	@Test
	public void testUnknownParentLevel2() {
		final SingleCrossHybridProcessor singleCrossHybridProcessor = new SingleCrossHybridProcessor();

		final GermplasmNode femaleParent =
			PedigreeStringTestUtil.createGermplasmNode(1, "FemaleParent1", PedigreeStringTestUtil.SINGLE_CROSS_METHOD_ID,
				PedigreeStringTestUtil.SINGLE_CROSS_METHOD_NAME, PedigreeStringTestUtil.SINGLE_CROSS_METHOD_NUMBER_OF_PROGENITOR);
		femaleParent.setFemaleParent(null);
		femaleParent.setMaleParent(null);

		final GermplasmNode maleParent =
			PedigreeStringTestUtil.createGermplasmNode(1, "MaleParent1", PedigreeStringTestUtil.SINGLE_CROSS_METHOD_ID,
				PedigreeStringTestUtil.SINGLE_CROSS_METHOD_NAME, PedigreeStringTestUtil.SINGLE_CROSS_METHOD_NUMBER_OF_PROGENITOR);
		maleParent.setFemaleParent(null);
		maleParent.setMaleParent(null);

		final GermplasmNode germplasmNode =
			PedigreeStringTestUtil.createGermplasmNode(1, "A", PedigreeStringTestUtil.SINGLE_CROSS_METHOD_ID,
				PedigreeStringTestUtil.SINGLE_CROSS_METHOD_NAME, PedigreeStringTestUtil.SINGLE_CROSS_METHOD_NUMBER_OF_PROGENITOR);
		germplasmNode.setFemaleParent(femaleParent);
		germplasmNode.setMaleParent(maleParent);

		final PedigreeString resultantPedigreeString = singleCrossHybridProcessor.processGermplasmNode(germplasmNode, 2,
			this.fixedLineNameResolver, false);
		assertEquals("Pedigree string is Unknown/Unknow since we cannot " + "determine the name or the GID", "FemaleParent1/MaleParent1",
			resultantPedigreeString.getPedigree());
		assertEquals("We have crated one cross.", 1, resultantPedigreeString.getNumberOfCrosses());

	}

}
