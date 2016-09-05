package org.generationcp.middleware.dao;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.data.initializer.LocationTestDataInitializer;
import org.generationcp.middleware.pojos.Location;
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
		 * For program 1, verify there are 19 breeding locations returned: 16
		 * historical breeding location in maize 1 historical breeding location
		 * added in this test 2 breeding location specific to program
		 */
		final List<Location> programOneLocations = LocationDAOTest.locationDAO
				.getBreedingLocationsByUniqueID(LocationDAOTest.PROGRAM_UUID1);
		Assert.assertEquals("Expecting 19 breeding locations for program with ID " + LocationDAOTest.PROGRAM_UUID1, 19,
				programOneLocations.size());

		/*
		 * For program 1, verify there are 20 breeding locations returned: 16
		 * historical breeding location in maize 1 historical breeding location
		 * added in this test 3 breeding location specific to program
		 */
		final List<Location> programTwoLocations = LocationDAOTest.locationDAO
				.getBreedingLocationsByUniqueID(LocationDAOTest.PROGRAM_UUID2);
		Assert.assertEquals("Expecting 20 breeding locations for program with ID " + LocationDAOTest.PROGRAM_UUID2, 20,
				programTwoLocations.size());

		/*
		 * For program 1, verify there are 17 breeding locations returned: 16
		 * historical breeding location in maize 1 historical breeding location
		 * added in this test
		 */
		final List<Location> programThreeLocations = LocationDAOTest.locationDAO
				.getBreedingLocationsByUniqueID(LocationDAOTest.PROGRAM_UUID3);
		Assert.assertEquals("Expecting 17 breeding locations for program with ID " + LocationDAOTest.PROGRAM_UUID3, 17,
				programThreeLocations.size());
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


}
