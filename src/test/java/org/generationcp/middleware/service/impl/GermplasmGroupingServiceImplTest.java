
package org.generationcp.middleware.service.impl;

import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.pojos.Germplasm;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

public class GermplasmGroupingServiceImplTest {

	@Test
	public void testMarkFixed() {
		GermplasmDAO germplasmDAO = Mockito.mock(GermplasmDAO.class);

		GermplasmGroupingServiceImpl groupingService = new GermplasmGroupingServiceImpl(germplasmDAO);
		Germplasm germplasmToFix = new Germplasm(1);

		Germplasm child1 = new Germplasm(2);
		Germplasm child2 = new Germplasm(3);

		Mockito.when(germplasmDAO.getAllChildren(germplasmToFix.getGid())).thenReturn(Lists.newArrayList(child1, child2));

		groupingService.markFixed(germplasmToFix, false);

		Assert.assertEquals("Expecting founder/parent mgid to be set the same as gid when there is no existing mgid.",
				germplasmToFix.getGid(),
				germplasmToFix.getMgid());

		Assert.assertEquals("Expecting child1 mgid to be set the same as founder/parent gid when there is no existing mgid.",
				germplasmToFix.getGid(),
				child1.getMgid());

		Assert.assertEquals("Expecting child2 mgid to be set the same as founder/parent gid when there is no existing mgid.",
				germplasmToFix.getGid(),
				child2.getMgid());
	}
}
