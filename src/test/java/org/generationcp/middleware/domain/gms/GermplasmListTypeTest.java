package org.generationcp.middleware.domain.gms;

import org.junit.Assert;
import org.junit.Test;

import static org.generationcp.middleware.domain.gms.GermplasmListType.*;

public class GermplasmListTypeTest {

	@Test
	public void testIsCrosses() {
		Assert.assertTrue(isCrosses(CRT_CROSS));
		Assert.assertTrue(isCrosses(CROSSES));
		Assert.assertTrue(isCrosses(IMP_CROSS));
		Assert.assertFalse(isCrosses(ADVANCED));

		Assert.assertTrue(isCrosses("CRT_CROSS"));
		Assert.assertTrue(isCrosses("CROSSES"));
		Assert.assertTrue(isCrosses("IMP_CROSS"));
		Assert.assertFalse(isCrosses("ADVANCED"));
		Assert.assertFalse(isCrosses(""));
	}
}
