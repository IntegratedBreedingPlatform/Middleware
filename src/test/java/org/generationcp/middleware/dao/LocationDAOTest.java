package org.generationcp.middleware.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.data.initializer.LocationTestDataInitializer;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.service.api.location.LocationDetailsDto;
import org.generationcp.middleware.service.api.location.LocationFilters;
import org.hamcrest.MatcherAssert;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LocationDAOTest extends IntegrationTestBase {
	
	private static final String ABBR = "ABBR";
	private static final String LOCATION = "LOCATION";
	private static final int NON_BREEDING_LOC_TYPE = 22;
	private static final int HISTORICAL_LOC_ID = 10;
	private static final int[] PROGRAM1_BREEDING_LOC_IDS = { 20, 21 };
	private static final int[] PROGRAM2_BREEDING_LOC_IDS = { 30, 31, 32 };

	private static final String PROGRAM_UUID1 = "abcde-12345";
	private static final String PROGRAM_UUID2 = "qwerty-09876";
	private static final String PROGRAM_UUID3 = "asdfg-54321";

	private static LocationDAO locationDAO;
	private static LocationTestDataInitializer locationDataInitializer;

	@Before
	public void setUp() throws Exception {
		final Session session = this.sessionProvder.getSession();
		LocationDAOTest.locationDAO = new LocationDAO();
		LocationDAOTest.locationDAO.setSession(session);

		LocationDAOTest.locationDataInitializer = new LocationTestDataInitializer();
	}

	@Test
	public void testGetBreedingLocationsByUniqueID() {
		this.createTestLocationsForPrograms();

		/*
		 * For program 1, verify there are breeding locations returned
		 */
		final List<Location> programOneLocations = LocationDAOTest.locationDAO
				.getBreedingLocationsByUniqueID(LocationDAOTest.PROGRAM_UUID1);
		Assert.assertTrue("Expecting breeding locations for program with ID " + LocationDAOTest.PROGRAM_UUID1,
				programOneLocations.size() > 0);

		/*
		 * For program 2, verify there are breeding locations returned
		 */
		final List<Location> programTwoLocations = LocationDAOTest.locationDAO
				.getBreedingLocationsByUniqueID(LocationDAOTest.PROGRAM_UUID2);
		Assert.assertTrue("Expecting breeding locations for program with ID " + LocationDAOTest.PROGRAM_UUID2,
			programOneLocations.size() > 0);

		/*
		 * For program 3, verify there are breeding locations returned
		 */
		final List<Location> programThreeLocations = LocationDAOTest.locationDAO
				.getBreedingLocationsByUniqueID(LocationDAOTest.PROGRAM_UUID3);
		Assert.assertTrue("Expecting breeding locations for program with ID " + LocationDAOTest.PROGRAM_UUID3,
			programOneLocations.size() > 0);
	}

	private void createTestLocationsForPrograms() {
		final List<Location> locations = new ArrayList<>();

		// add historical breeding location (null program uuid)
		locations.add(LocationDAOTest.locationDataInitializer.createLocation(LocationDAOTest.HISTORICAL_LOC_ID,
				LocationDAOTest.LOCATION + LocationDAOTest.HISTORICAL_LOC_ID, Location.BREEDING_LOCATION_TYPE_IDS[0],
				LocationDAOTest.ABBR + LocationDAOTest.HISTORICAL_LOC_ID, null));

		// Add locations for Program 1 - 2 breeding locations, 1 non-breeding
		// location
		Integer id = LocationDAOTest.PROGRAM1_BREEDING_LOC_IDS[0];
		locations.add(LocationDAOTest.locationDataInitializer.createLocation(id, LocationDAOTest.LOCATION + id,
				Location.BREEDING_LOCATION_TYPE_IDS[0], LocationDAOTest.ABBR + id, LocationDAOTest.PROGRAM_UUID1));
		id = LocationDAOTest.PROGRAM1_BREEDING_LOC_IDS[1];
		locations.add(LocationDAOTest.locationDataInitializer.createLocation(id, LocationDAOTest.LOCATION + id,
				Location.BREEDING_LOCATION_TYPE_IDS[1], LocationDAOTest.ABBR + id, LocationDAOTest.PROGRAM_UUID1));
		locations.add(LocationDAOTest.locationDataInitializer.createLocation(22, LocationDAOTest.LOCATION + 22,
				LocationDAOTest.NON_BREEDING_LOC_TYPE, LocationDAOTest.ABBR + 22, LocationDAOTest.PROGRAM_UUID1));

		// Add locations for Program 2 - 3 breeding locations, 2 non-breeding
		// location
		id = LocationDAOTest.PROGRAM2_BREEDING_LOC_IDS[0];
		locations.add(LocationDAOTest.locationDataInitializer.createLocation(id, LocationDAOTest.LOCATION + id,
				Location.BREEDING_LOCATION_TYPE_IDS[0], LocationDAOTest.ABBR + id, LocationDAOTest.PROGRAM_UUID2));
		id = LocationDAOTest.PROGRAM2_BREEDING_LOC_IDS[1];
		locations.add(LocationDAOTest.locationDataInitializer.createLocation(id, LocationDAOTest.LOCATION + id,
				Location.BREEDING_LOCATION_TYPE_IDS[1], LocationDAOTest.ABBR + id, LocationDAOTest.PROGRAM_UUID2));
		id = LocationDAOTest.PROGRAM2_BREEDING_LOC_IDS[2];
		locations.add(LocationDAOTest.locationDataInitializer.createLocation(id, LocationDAOTest.LOCATION + id,
				Location.BREEDING_LOCATION_TYPE_IDS[2], LocationDAOTest.ABBR + id, LocationDAOTest.PROGRAM_UUID2));
		locations.add(LocationDAOTest.locationDataInitializer.createLocation(33, LocationDAOTest.LOCATION + 33,
				LocationDAOTest.NON_BREEDING_LOC_TYPE, LocationDAOTest.ABBR + 33, LocationDAOTest.PROGRAM_UUID2));
		locations.add(LocationDAOTest.locationDataInitializer.createLocation(34, LocationDAOTest.LOCATION + 34,
				LocationDAOTest.NON_BREEDING_LOC_TYPE, LocationDAOTest.ABBR + 34, LocationDAOTest.PROGRAM_UUID2));

		for (final Location location : locations) {
			LocationDAOTest.locationDAO.save(location);
		}
	}

	@Test
	public void getLocalLocationsByFilter() {
		HashMap<LocationFilters, Object> filters = new HashMap<>();
		filters.put(LocationFilters.LOCATION_TYPE, 405L);
		final List<LocationDetailsDto> locationList = LocationDAOTest.locationDAO.getLocationsByFilter(1, 100, filters);
		MatcherAssert.assertThat("Expected list of country location size > zero", locationList != null && locationList.size() > 0);

	}

	@Test
	public void getLocalLocationsByFilterNotRecoverData() {
		HashMap<LocationFilters, Object> filters = new HashMap<>();
		filters.put(LocationFilters.LOCATION_TYPE, 000100000405L);
		final List<LocationDetailsDto> locationList = LocationDAOTest.locationDAO.getLocationsByFilter(1, 100, filters);
		MatcherAssert.assertThat("Expected list of location size equals to zero", locationList != null && locationList.size() == 0);


	}

	@Test
	public void countLocationsByFilter() {
		HashMap<LocationFilters, Object> filters = new HashMap<LocationFilters, Object>();
		filters.put(LocationFilters.LOCATION_TYPE, 405L);
		long countLocation = LocationDAOTest.locationDAO.countLocationsByFilter(filters);
		MatcherAssert.assertThat("Expected country location size > zero", countLocation > 0);
	}

	@Test
	public void countLocationsByFilterNotFoundLocation() {
		HashMap<LocationFilters, Object> filters = new HashMap<LocationFilters, Object>();
		filters.put(LocationFilters.LOCATION_TYPE, 000100000405L);
		long countLocation = LocationDAOTest.locationDAO.countLocationsByFilter(filters);
		MatcherAssert.assertThat("Expected country location size equals to zero by this locationType = 000100000405", countLocation == 0);

	}
}
