package org.generationcp.middleware.service.pedigree.string.processors;

import static org.junit.Assert.assertEquals;

import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.service.pedigree.GermplasmNode;
import org.generationcp.middleware.service.pedigree.PedigreeString;
import org.generationcp.middleware.service.pedigree.string.processors.InbredProcessor;
import org.generationcp.middleware.service.pedigree.string.util.FixedLineNameResolver;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.base.Optional;



public class InbredProcessorTest {

	private FixedLineNameResolver fixedLineNameResolver;

	@Before
	public void setUp() {
		fixedLineNameResolver = Mockito.mock(FixedLineNameResolver.class);
		// We use any and null value because in the test be do not want any fixed line based name resolution
		Mockito.when(fixedLineNameResolver.nameTypeBasedResolution(Mockito.any(GermplasmNode.class))).thenReturn(
				Optional.<String>fromNullable(null));
	}

	@Test
	public void testInbredProcessorWhenMissingGermplasmNode() throws Exception {
		final InbredProcessor inbredProcessor = new InbredProcessor();
		final PedigreeString inbreadPedigreeString = inbredProcessor.processGermplasmNode(null, 3, fixedLineNameResolver);
		assertEquals("Pedigree string is Unknown since we inputted a null value", "Unknown",
				inbreadPedigreeString.getPedigree());
	}

	@Test
	public void testInbredProcessorWhenMissingGermplasm() throws Exception {
		final InbredProcessor inbredProcessor = new InbredProcessor();
		final PedigreeString inbreadPedigreeString = inbredProcessor.processGermplasmNode(new GermplasmNode(null), 3, fixedLineNameResolver);
		assertEquals("Pedigree string is Unknown since we inputted a null value", "Unknown",
				inbreadPedigreeString.getPedigree());
	}


	@Test
	public void testInbredProcessorWhenMissingGermplasmName() throws Exception {
		final InbredProcessor inbredProcessor = new InbredProcessor();
		final PedigreeString inbreadPedigreeString = inbredProcessor.processGermplasmNode(new GermplasmNode(new Germplasm(1)), 3, fixedLineNameResolver);
		assertEquals("Pedigree string is the gid i.e. 1 since we inputted a null name value", "1",
				inbreadPedigreeString.getPedigree());
	}

	@Test
	public void testInbredProcessorWhithFullyPopulatedGermplasmName() throws Exception {
		final InbredProcessor inbredProcessor = new InbredProcessor();
		GermplasmNode inbreadGermplasmNode = PedigreeStringTestUtil.createGermplasmNode(1, "A", PedigreeStringTestUtil.BULK_OR_POPULATION_SAMPLE_METHOD_ID,
				PedigreeStringTestUtil.BULK_OR_POPULATION_SAMPLE_METHOD_NAME, PedigreeStringTestUtil.BULK_OR_POPULATION_SAMPLE_METHOD_NUMBER_OF_PROGENITOR);

		final PedigreeString inbreadPedigreeString = inbredProcessor.processGermplasmNode(inbreadGermplasmNode, 3, fixedLineNameResolver);
		assertEquals("Pedigree string is A since it is the preferred name", "A",
				inbreadPedigreeString.getPedigree());
	}
}
