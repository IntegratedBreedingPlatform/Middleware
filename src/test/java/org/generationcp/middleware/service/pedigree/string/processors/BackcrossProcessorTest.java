
package org.generationcp.middleware.service.pedigree.string.processors;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.generationcp.middleware.service.pedigree.GermplasmNode;
import org.generationcp.middleware.service.pedigree.PedigreeString;
import org.generationcp.middleware.service.pedigree.string.util.FixedLineNameResolver;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.base.Optional;

public class BackcrossProcessorTest {

	private FixedLineNameResolver mockFixedLineNameResolver;
	private CrossExpansionProperties mockCrossExpansionProperties;

	@Before
	public void setUp() throws Exception {
		mockFixedLineNameResolver = Mockito.mock(FixedLineNameResolver.class);

		// Main goal is to diable any nametype based name resolution
		Mockito.when(mockFixedLineNameResolver.nameTypeBasedResolution(Mockito.any(GermplasmNode.class))).thenReturn(
				Optional.<String>fromNullable(null));
		mockCrossExpansionProperties = Mockito.mock(CrossExpansionProperties.class);

		// Main goal is to return default backcross notation
		Mockito.when(mockCrossExpansionProperties.getBackcrossNotation(Mockito.anyString())).thenReturn(
				new ImmutablePair<String, String>("*", "*"));
		Mockito.when(mockFixedLineNameResolver.getCrossExpansionProperties()).thenReturn(mockCrossExpansionProperties);
	}

	@Test
	public void testBackcrossWithFemaleRecurringParent() throws Exception {
		final BackcrossProcessor backcrossProcessor = new BackcrossProcessor();

		final GermplasmNode createBackCrossTestGermplasmNode =
				PedigreeStringTestUtil.createBackCrossTestGermplasmTree("DonorParent", "RecurringParent", 5, true);

		final PedigreeString processGermplasmNode =
				backcrossProcessor.processGermplasmNode(createBackCrossTestGermplasmNode, 5, mockFixedLineNameResolver);

		Assert.assertEquals("B/C/5*RecurringParent", processGermplasmNode.getPedigree());
	}


	@Test
	public void testBackcrossWithMaleRecurringParent() throws Exception {
		final BackcrossProcessor backcrossProcessor = new BackcrossProcessor();

		final GermplasmNode createBackCrossTestGermplasmNode =
				PedigreeStringTestUtil.createBackCrossTestGermplasmTree("DonorParent", "RecurringParent", 5, false);

		final PedigreeString processGermplasmNode =
				backcrossProcessor.processGermplasmNode(createBackCrossTestGermplasmNode, 5, mockFixedLineNameResolver);

		Assert.assertEquals("RecurringParent*5/B/C", processGermplasmNode.getPedigree());
	}

	@Test
	public void testBackcrossWithoutRecurringParentIENotABackcross() throws Exception {
		final BackcrossProcessor backcrossProcessor = new BackcrossProcessor();

		final GermplasmNode createBackCrossTestGermplasmNode =
				PedigreeStringTestUtil.createGermplasmNode(100, "RootBackcrossNode", PedigreeStringTestUtil.BACKCROSS_METHOD_ID,
						PedigreeStringTestUtil.BACKCROSS_METHOD_NAME, 2);
		PedigreeString processGermplasmNode =
				backcrossProcessor.processGermplasmNode(createBackCrossTestGermplasmNode, 5, mockFixedLineNameResolver);
		Assert.assertEquals("Unknown/Unknown", processGermplasmNode.getPedigree());
	}
}
