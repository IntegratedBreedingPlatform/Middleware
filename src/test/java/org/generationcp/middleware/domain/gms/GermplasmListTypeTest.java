package org.generationcp.middleware.domain.gms;

import org.junit.Assert;
import org.junit.Test;

import static org.generationcp.middleware.domain.gms.GermplasmListType.*;

public class GermplasmListTypeTest {

	@Test
	public void testIsCrosses() {
		Assert.assertTrue(isCrosses(F1CRT));
		Assert.assertTrue(isCrosses(CROSSES));
		Assert.assertTrue(isCrosses(F1IMP));
		Assert.assertFalse(isCrosses(ADVANCED));

		Assert.assertTrue(isCrosses("F1CRT"));
		Assert.assertTrue(isCrosses("CROSSES"));
		Assert.assertTrue(isCrosses("F1IMP"));
		Assert.assertFalse(isCrosses("ADVANCED"));
		Assert.assertFalse(isCrosses(""));
	}
}
