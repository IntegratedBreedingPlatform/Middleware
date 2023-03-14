package org.generationcp.middleware.dao.gdms;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.pojos.gdms.MapDetailElement;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class MapDAOTest extends IntegrationTestBase {

	private MapDAO mapDAO;

	@Before
	public void setUp() throws Exception {
		this.mapDAO = new MapDAO(this.sessionProvder.getSession());
	}

	@Test
	public void testGetAllMapDetails() {

		final List<MapDetailElement> result = this.mapDAO.getAllMapDetails(0, 1);

		Assert.assertEquals("Expecting getAllMapDetails will return 1 record", 1, result.size());

	}
}
