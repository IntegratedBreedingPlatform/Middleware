
package org.generationcp.middleware.service.impl;

import org.generationcp.middleware.pojos.Germplasm;
import org.junit.Assert;
import org.junit.Test;

public class GermplasmGroupingServiceImplTest {

	@Test
	public void testMarkFixed() {
		GermplasmGroupingServiceImpl groupingService = new GermplasmGroupingServiceImpl();
		Germplasm germplasmToFix = new Germplasm();
		germplasmToFix.setGid(1);

		groupingService.markFixed(germplasmToFix);

		Assert.assertEquals("Expecting mgid to be set the same as gid when there is no existing mgid.", germplasmToFix.getGid(),
				germplasmToFix.getMgid());
	}
}
