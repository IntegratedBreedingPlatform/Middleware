package org.generationcp.middleware.dao;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.data.initializer.LocationTestDataInitializer;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.LocationDetails;
import org.generationcp.middleware.service.api.location.LocationDetailsDto;
import org.generationcp.middleware.service.api.location.LocationFilters;
import org.generationcp.middleware.util.StringUtil;
import org.hamcrest.MatcherAssert;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class LocationDAOTest extends IntegrationTestBase {

	private static final String ABBR = "ABBR";
	private static final String LOCATION = "LOCATION";
	private static final int NON_BREEDING_LOC_TYPE = 22;
	private static final int HISTORICAL_LOC_ID = 10;
	private static final int[] PROGRAM1_BREEDING_LOC_IDS = {20, 21};
	private static final int[] PROGRAM2_BREEDING_LOC_IDS = {30, 31, 32};

	private static final String PROGRAM_UUID1 = "abcde-12345";
	private static final String PROGRAM_UUID2 = "qwerty-09876";
	private static final String PROGRAM_UUID3 = "asdfg-54321";

	private static LocationDAO locationDAO;

	@Before
	public void setUp() throws Exception {
		final Session session = this.sessionProvder.getSession();
		LocationDAOTest.locationDAO = new LocationDAO();
		LocationDAOTest.locationDAO.setSession(session);
	}

	@Test
	public void testGetBreedingLocationsByUniqueID() {
		this.createTestLocationsForPrograms();

		/*
		 * For program 1, verify there are breeding locations returned
		 */
		final List<Location> programOneLocations =
				LocationDAOTest.locationDAO.getBreedingLocationsByUniqueID(LocationDAOTest.PROGRAM_UUID1);
		Assert.assertTrue("Expecting breeding locations for program with ID " + LocationDAOTest.PROGRAM_UUID1,
				programOneLocations.size() > 0);

		/*
		 * For program 2, verify there are breeding locations returned
		 */
		final List<Location> programTwoLocations =
				LocationDAOTest.locationDAO.getBreedingLocationsByUniqueID(LocationDAOTest.PROGRAM_UUID2);
		Assert.assertTrue("Expecting breeding locations for program with ID " + LocationDAOTest.PROGRAM_UUID2,
				programTwoLocations.size() > 0);

		/*
		 * For program 3, verify there are breeding locations returned
		 */
		final List<Location> programThreeLocations =
				LocationDAOTest.locationDAO.getBreedingLocationsByUniqueID(LocationDAOTest.PROGRAM_UUID3);
		Assert.assertTrue("Expecting breeding locations for program with ID " + LocationDAOTest.PROGRAM_UUID3,
				programThreeLocations.size() > 0);
	}

	private void createTestLocationsForPrograms() {
		final List<Location> locations = new ArrayList<>();

		// add historical breeding location (null program uuid)
		locations.add(LocationTestDataInitializer
				.createLocation(LocationDAOTest.HISTORICAL_LOC_ID, LocationDAOTest.LOCATION + LocationDAOTest.HISTORICAL_LOC_ID,
						Location.BREEDING_LOCATION_TYPE_IDS[0], LocationDAOTest.ABBR + LocationDAOTest.HISTORICAL_LOC_ID, null));

		// Add locations for Program 1 - 2 breeding locations, 1 non-breeding
		// location
		Integer id = LocationDAOTest.PROGRAM1_BREEDING_LOC_IDS[0];
		locations.add(LocationTestDataInitializer
				.createLocation(id, LocationDAOTest.LOCATION + id, Location.BREEDING_LOCATION_TYPE_IDS[0], LocationDAOTest.ABBR + id,
						LocationDAOTest.PROGRAM_UUID1));
		id = LocationDAOTest.PROGRAM1_BREEDING_LOC_IDS[1];
		locations.add(LocationTestDataInitializer
				.createLocation(id, LocationDAOTest.LOCATION + id, Location.BREEDING_LOCATION_TYPE_IDS[1], LocationDAOTest.ABBR + id,
						LocationDAOTest.PROGRAM_UUID1));
		locations.add(LocationTestDataInitializer
				.createLocation(22, LocationDAOTest.LOCATION + 22, LocationDAOTest.NON_BREEDING_LOC_TYPE, LocationDAOTest.ABBR + 22,
						LocationDAOTest.PROGRAM_UUID1));

		// Add locations for Program 2 - 3 breeding locations, 2 non-breeding
		// location
		id = LocationDAOTest.PROGRAM2_BREEDING_LOC_IDS[0];
		locations.add(LocationTestDataInitializer
				.createLocation(id, LocationDAOTest.LOCATION + id, Location.BREEDING_LOCATION_TYPE_IDS[0], LocationDAOTest.ABBR + id,
						LocationDAOTest.PROGRAM_UUID2));
		id = LocationDAOTest.PROGRAM2_BREEDING_LOC_IDS[1];
		locations.add(LocationTestDataInitializer
				.createLocation(id, LocationDAOTest.LOCATION + id, Location.BREEDING_LOCATION_TYPE_IDS[1], LocationDAOTest.ABBR + id,
						LocationDAOTest.PROGRAM_UUID2));
		id = LocationDAOTest.PROGRAM2_BREEDING_LOC_IDS[2];
		locations.add(LocationTestDataInitializer
				.createLocation(id, LocationDAOTest.LOCATION + id, Location.BREEDING_LOCATION_TYPE_IDS[2], LocationDAOTest.ABBR + id,
						LocationDAOTest.PROGRAM_UUID2));
		locations.add(LocationTestDataInitializer
				.createLocation(33, LocationDAOTest.LOCATION + 33, LocationDAOTest.NON_BREEDING_LOC_TYPE, LocationDAOTest.ABBR + 33,
						LocationDAOTest.PROGRAM_UUID2));
		locations.add(LocationTestDataInitializer
				.createLocation(34, LocationDAOTest.LOCATION + 34, LocationDAOTest.NON_BREEDING_LOC_TYPE, LocationDAOTest.ABBR + 34,
						LocationDAOTest.PROGRAM_UUID2));

		for (final Location location : locations) {
			LocationDAOTest.locationDAO.save(location);
		}
	}

	@Test
	public void getLocalLocationsByFilter() {
		final HashMap<LocationFilters, Object> filters = new HashMap<>();
		filters.put(LocationFilters.LOCATION_TYPE, 405L);
		final List<LocationDetailsDto> locationList = LocationDAOTest.locationDAO.getLocationsByFilter(1, 100, filters);
		MatcherAssert.assertThat("Expected list of country location size > zero", locationList != null && locationList.size() > 0);

	}

	@Test
	public void getLocalLocationsByFilterNotRecoverData() {
		final HashMap<LocationFilters, Object> filters = new HashMap<>();
		filters.put(LocationFilters.LOCATION_TYPE, 000100000405L);
		final List<LocationDetailsDto> locationList = LocationDAOTest.locationDAO.getLocationsByFilter(1, 100, filters);
		MatcherAssert.assertThat("Expected list of location size equals to zero", locationList != null && locationList.size() == 0);

	}

	@Test
	public void countLocationsByFilter() {
		final HashMap<LocationFilters, Object> filters = new HashMap<LocationFilters, Object>();
		filters.put(LocationFilters.LOCATION_TYPE, 405L);
		final long countLocation = LocationDAOTest.locationDAO.countLocationsByFilter(filters);
		MatcherAssert.assertThat("Expected country location size > zero", countLocation > 0);
	}

	@Test
	public void countLocationsByFilterNotFoundLocation() {
		final HashMap<LocationFilters, Object> filters = new HashMap<LocationFilters, Object>();
		filters.put(LocationFilters.LOCATION_TYPE, 000100000405L);
		final long countLocation = LocationDAOTest.locationDAO.countLocationsByFilter(filters);
		MatcherAssert.assertThat("Expected country location size equals to zero by this locationType = 000100000405", countLocation == 0);

	}

	@Test
	public void testGetLocationDetails() {

		final String programUUID = "jahsdkajsd-78346-kjf364";
		final int ltype = 405;
		final String labbr = "ABCDEFG";
		final String lname = "MyLocation";

		// Country ID 1 = "Democratic Republic of Afghanistan"
		final int cntryid = 1;
		final Location location = LocationTestDataInitializer.createLocation(null, lname, ltype, labbr, programUUID);
		location.setCntryid(cntryid);
		// Province Badakhshan
		final int provinceId = 1001;
		location.setSnl1id(provinceId);

		locationDAO.saveOrUpdate(location);

		final List<LocationDetails> result = locationDAO.getLocationDetails(location.getLocid(), 0, Integer.MAX_VALUE);
		final LocationDetails locationDetails = result.get(0);
		final Location province = locationDAO.getById(provinceId);
		
		Assert.assertEquals(lname, locationDetails.getLocationName());
		Assert.assertEquals(location.getLocid(), locationDetails.getLocid());
		Assert.assertEquals(ltype, locationDetails.getLtype().intValue());
		Assert.assertEquals(labbr, locationDetails.getLocationAbbreviation());
		Assert.assertEquals(cntryid, locationDetails.getCntryid().intValue());
		Assert.assertEquals(province.getLname(), locationDetails.getProvinceName());
		Assert.assertEquals(programUUID, locationDetails.getProgramUUID());

		Assert.assertEquals("COUNTRY", locationDetails.getLocationType());
		Assert.assertEquals("-", locationDetails.getLocationDescription());
		Assert.assertEquals("Democratic Republic of Afghanistan", locationDetails.getCountryFullName());

	}

	@Test
	public void testGetFilteredLocations() {

		final String programUUID = "jahsdkajsd-78346-kjf364";
		final int ltype = 405;
		final String labbr = "ABCDEFG";
		final String lname = "MyLocation";

		// Country ID 1 = "Democratic Republic of Afghanistan"
		final int cntryid = 1;
		final Location location = LocationTestDataInitializer.createLocation(null, lname, ltype, labbr, programUUID);
		location.setCntryid(cntryid);
		// Province Badakhshan
		location.setSnl1id(1001);

		locationDAO.saveOrUpdate(location);
		final Location province = locationDAO.getById(1001);
		final Location country = locationDAO.getById(1);

		final List<LocationDetails> result = locationDAO.getFilteredLocations(cntryid, ltype, lname, programUUID);
		final LocationDetails locationDetails = result.get(0);

		Assert.assertEquals(lname, locationDetails.getLocationName());
		Assert.assertEquals(location.getLocid(), locationDetails.getLocid());
		Assert.assertEquals(ltype, locationDetails.getLtype().intValue());
		Assert.assertEquals(labbr, locationDetails.getLocationAbbreviation());
		Assert.assertEquals(cntryid, locationDetails.getCntryid().intValue());
		Assert.assertEquals(programUUID, locationDetails.getProgramUUID());
		Assert.assertEquals(country.getLname(), locationDetails.getCountryName());
		Assert.assertEquals(province.getLname(), locationDetails.getProvinceName());


		Assert.assertEquals("COUNTRY", locationDetails.getLocationType());
		Assert.assertEquals("-", locationDetails.getLocationDescription());
		Assert.assertEquals("Democratic Republic of Afghanistan", locationDetails.getCountryFullName());

	}

	@Test
	public void testGetFilteredLocationsFilterByCountryId() {

		final Integer cntryid = 1;

		final List<LocationDetails> locationDetailsList = locationDAO.getFilteredLocations(cntryid, null, null, null);

		// Verify that all locationDetails returned have cntryId = 1
		for (final LocationDetails locationDetails : locationDetailsList) {
			Assert.assertEquals(cntryid, locationDetails.getCntryid());
		}

	}

	@Test
	public void testGetFilteredLocationsFilterByLocationType() {

		final Integer ltype = 405;

		final List<LocationDetails> locationDetailsList = locationDAO.getFilteredLocations(null, ltype, null, null);

		// Verify that all locationDetails returned have cntryId = 1
		for (final LocationDetails locationDetails : locationDetailsList) {
			Assert.assertEquals(ltype, locationDetails.getLtype());
		}

	}

	@Test
	public void testGetFilteredLocationsFilterByLocationName() {

		final String lname = "Unknown";

		final List<LocationDetails> locationDetailsList = locationDAO.getFilteredLocations(null, null, lname, null);

		// Verify that all locationDetails returned have cntryId = 1
		for (final LocationDetails locationDetails : locationDetailsList) {
			Assert.assertEquals(lname, locationDetails.getLocationName());
		}

	}

	@Test
	public void testGetFilteredLocationsFilterByProgramUUID() {

		final String programUUID = "hvggfdhf-f34t6-24677";
		final int ltype = 405;
		final String labbr = "ABCDEFG";
		final String lname = "MyLocation";

		// Country ID 1 = "Democratic Republic of Afghanistan"
		final int cntryid = 1;
		final Location location = LocationTestDataInitializer.createLocation(null, lname, ltype, labbr, programUUID);
		location.setCntryid(cntryid);

		locationDAO.saveOrUpdate(location);

		final List<LocationDetails> locationDetailsList = locationDAO.getFilteredLocations(null, null, null, programUUID);

		final Collection<LocationDetails> locationsWithProgramUUID = Collections2.filter(locationDetailsList, new Predicate<LocationDetails>() {

			@Override
			public boolean apply(@Nullable final LocationDetails locationDetails) {
				return programUUID.equals(locationDetails.getProgramUUID());
			}
		});

		// Verify that only one LocationDetails with programUUID (hvggfdhf-f34t6-24677) is returned
		Assert.assertTrue(locationsWithProgramUUID.size() == 1);

		final Collection<LocationDetails> locationWithNullProgramUUID =
				Collections2.filter(locationDetailsList, new Predicate<LocationDetails>() {

					@Override
					public boolean apply(@Nullable final LocationDetails locationDetails) {
						return StringUtil.isEmpty(locationDetails.getProgramUUID());
					}
				});

		Assert.assertEquals(locationDetailsList.size(), locationWithNullProgramUUID.size() + locationsWithProgramUUID.size());

	}

	@Test
	public void testGetByUniqueIDAndExcludeLocationTypes() {

		final String programUUID = "hglghkk-527484-dgggt";
		final int ltype = 405;
		final String labbr = "ABCDEFG";
		final String lname = "MyLocation";

		// Country ID 1 = "Democratic Republic of Afghanistan"
		final int cntryid = 1;
		final Location location = LocationTestDataInitializer.createLocation(null, lname, ltype, labbr, programUUID);
		location.setCntryid(cntryid);

		locationDAO.saveOrUpdate(location);

		List<Location> result = locationDAO.getByUniqueIDAndExcludeLocationTypes(programUUID, new ArrayList<Integer>());

		final Collection<Location> programSpecificLocations = Collections2.filter(result, new Predicate<Location>() {

			@Override
			public boolean apply(@Nullable final Location location) {
				return programUUID.equals(location.getUniqueID());
			}
		});

		// Verify that only one Location with programUUID ("hglghkk-527484-dgggt") is returned
		Assert.assertTrue(programSpecificLocations.size() == 1);

		final Collection<Location> cropSpecificLocations = Collections2.filter(result, new Predicate<Location>() {

			@Override
			public boolean apply(@Nullable final Location location) {
				return null == location.getUniqueID();
			}
		});

		// Verify that there are crop specific locations returned
		Assert.assertTrue(!cropSpecificLocations.isEmpty());

	}

	@Test
	public void testGetByUniqueIDAndExcludeLocationTypesExcludeCountryLocationType() {

		// ltype 405 is "COUNTRY" location type
		final int countryLocationType = 405;
		List<Integer> excludeCountryType = new ArrayList<>();
		excludeCountryType.add(countryLocationType);

		final List<Location> resultWithoutCountryLocationType = locationDAO.getByUniqueIDAndExcludeLocationTypes("any-program-uuid", excludeCountryType);

		for (Location location : resultWithoutCountryLocationType) {
			// Verify that no country type locations are returned
			Assert.assertFalse(location.getLtype().intValue() == countryLocationType);
		}

	}

}
