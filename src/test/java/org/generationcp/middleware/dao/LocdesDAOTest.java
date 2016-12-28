
package org.generationcp.middleware.dao;

import java.util.HashMap;
import java.util.List;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.pojos.Locdes;
import org.hamcrest.MatcherAssert;
import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;

public class LocdesDAOTest extends IntegrationTestBase {

	private static LocdesDAO locdesDAO;

	@Before
	public void setUp() throws Exception {
		final Session session = this.sessionProvder.getSession();
		LocdesDAOTest.locdesDAO = new LocdesDAO();
		LocdesDAOTest.locdesDAO.setSession(session);
	}
	
	
	@Test
	public void getAllLocdesByFcode() {
		HashMap<String, String> filters = new HashMap<String, String>();
		filters.put("locationType", "0040500");
		List<Locdes>  countLocation = LocdesDAOTest.locdesDAO.getAllLocdesByFilters(null,null,null);
		MatcherAssert.assertThat("Expected did not count country location by this locationType = 0040500", countLocation.size() > 0);

	}
	
	@Test
	public void getAllLocdesBylocid() {
		HashMap<String, String> filters = new HashMap<String, String>();
		filters.put("locationType", "0040500");
		List<Locdes>  countLocation = LocdesDAOTest.locdesDAO.getAllLocdesByFilters(null,null,null);
		MatcherAssert.assertThat("Expected did not count country location by this locationType = 0040500", countLocation.size() > 0);

	}
	
	@Test
	public void getAllLocdesBydval() {
		HashMap<String, String> filters = new HashMap<String, String>();
		filters.put("locationType", "0040500");
		List<Locdes>  countLocation = LocdesDAOTest.locdesDAO.getAllLocdesByFilters(null,null,null);
		MatcherAssert.assertThat("Expected did not count country location by this locationType = 0040500", countLocation.size() > 0);

	}
	@Test
	public void getAllLocdesByFiltersNotFound() {
		HashMap<String, String> filters = new HashMap<String, String>();
		filters.put("locationType", "0040500");
		List<Locdes>  countLocation = LocdesDAOTest.locdesDAO.getAllLocdesByFilters(null,null,null);
		MatcherAssert.assertThat("Expected did not count country location by this locationType = 0040500", countLocation.size() == 0);

	}
}
