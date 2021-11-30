package org.generationcp.middleware.dao;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.api.location.LocationDTO;
import org.generationcp.middleware.api.location.search.LocationSearchRequest;
import org.generationcp.middleware.data.initializer.LocationTestDataInitializer;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.LocationDetails;
import org.generationcp.middleware.util.StringUtil;
import org.hamcrest.MatcherAssert;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.not;

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

	private LocationDAO locationDAO;

	@Before
	public void setUp() throws Exception {
		final Session session = this.sessionProvder.getSession();
		this.locationDAO = new LocationDAO(this.sessionProvder.getSession());
	}

	@Test
	public void testGetBreedingLocations() {
		this.createTestLocationsForPrograms();

		/*
		 * For program 1, verify there are breeding locations returned
		 */
		final List<Location> programOneLocations =
			this.locationDAO.getBreedingLocations();
		Assert.assertTrue("Expecting breeding locations for program with ID " + LocationDAOTest.PROGRAM_UUID1,
			programOneLocations.size() > 0);

		/*
		 * For program 2, verify there are breeding locations returned
		 */
		final List<Location> programTwoLocations =
			this.locationDAO.getBreedingLocations();
		Assert.assertTrue("Expecting breeding locations for program with ID " + LocationDAOTest.PROGRAM_UUID2,
			programTwoLocations.size() > 0);

		/*
		 * For program 3, verify there are breeding locations returned
		 */
		final List<Location> programThreeLocations =
			this.locationDAO.getBreedingLocations();
		Assert.assertTrue("Expecting breeding locations for program with ID " + LocationDAOTest.PROGRAM_UUID3,
			programThreeLocations.size() > 0);
	}

	private void createTestLocationsForPrograms() {
		final List<Location> locations = new ArrayList<>();

		// add historical breeding location (null program uuid)
		locations.add(LocationTestDataInitializer
			.createLocation(LocationDAOTest.HISTORICAL_LOC_ID, LocationDAOTest.LOCATION + LocationDAOTest.HISTORICAL_LOC_ID,
				Location.BREEDING_LOCATION_TYPE_IDS[0], LocationDAOTest.ABBR + LocationDAOTest.HISTORICAL_LOC_ID));

		// Add locations for Program 1 - 2 breeding locations, 1 non-breeding
		// location
		Integer id = LocationDAOTest.PROGRAM1_BREEDING_LOC_IDS[0];
		locations.add(LocationTestDataInitializer
			.createLocation(id, LocationDAOTest.LOCATION + id, Location.BREEDING_LOCATION_TYPE_IDS[0], LocationDAOTest.ABBR + id
			));
		id = LocationDAOTest.PROGRAM1_BREEDING_LOC_IDS[1];
		locations.add(LocationTestDataInitializer
			.createLocation(id, LocationDAOTest.LOCATION + id, Location.BREEDING_LOCATION_TYPE_IDS[1], LocationDAOTest.ABBR + id
			));
		locations.add(LocationTestDataInitializer
			.createLocation(22, LocationDAOTest.LOCATION + 22, LocationDAOTest.NON_BREEDING_LOC_TYPE, LocationDAOTest.ABBR + 22
			));

		// Add locations for Program 2 - 3 breeding locations, 2 non-breeding
		// location
		id = LocationDAOTest.PROGRAM2_BREEDING_LOC_IDS[0];
		locations.add(LocationTestDataInitializer
			.createLocation(id, LocationDAOTest.LOCATION + id, Location.BREEDING_LOCATION_TYPE_IDS[0], LocationDAOTest.ABBR + id
			));
		id = LocationDAOTest.PROGRAM2_BREEDING_LOC_IDS[1];
		locations.add(LocationTestDataInitializer
			.createLocation(id, LocationDAOTest.LOCATION + id, Location.BREEDING_LOCATION_TYPE_IDS[1], LocationDAOTest.ABBR + id
			));
		id = LocationDAOTest.PROGRAM2_BREEDING_LOC_IDS[2];
		locations.add(LocationTestDataInitializer
			.createLocation(id, LocationDAOTest.LOCATION + id, Location.BREEDING_LOCATION_TYPE_IDS[2], LocationDAOTest.ABBR + id
			));
		locations.add(LocationTestDataInitializer
			.createLocation(33, LocationDAOTest.LOCATION + 33, LocationDAOTest.NON_BREEDING_LOC_TYPE, LocationDAOTest.ABBR + 33
			));
		locations.add(LocationTestDataInitializer
			.createLocation(34, LocationDAOTest.LOCATION + 34, LocationDAOTest.NON_BREEDING_LOC_TYPE, LocationDAOTest.ABBR + 34
			));

		for (final Location location : locations) {
			this.locationDAO.save(location);
		}
	}

	@Test
	public void testGetLocations() {
		final LocationSearchRequest locationSearchRequest = new LocationSearchRequest();
		locationSearchRequest.setLocationTypeName("Country");
		final List<org.generationcp.middleware.api.location.Location> locationList = this.locationDAO.getLocations(locationSearchRequest, new PageRequest(0, 10));
		MatcherAssert.assertThat("Expected list of country location size > zero", locationList != null && locationList.size() > 0);
	}

	@Test
	public void testGetLocations_ByAbbreviation() {
		final Location location = this.saveTestLocation();

		final LocationSearchRequest locationSearchRequest = new LocationSearchRequest();
		locationSearchRequest.setLocationAbbreviations(Collections.singletonList(location.getLabbr()));
		final List<org.generationcp.middleware.api.location.Location> locationList = this.locationDAO.getLocations(locationSearchRequest, new PageRequest(0, 10));
		MatcherAssert.assertThat("Expected to return filtered list by abbreviation", locationList != null && locationList.size() > 0);
	}

	@Test
	public void testGetLocationsWithWrongLocType() {
		final LocationSearchRequest locationSearchRequest = new LocationSearchRequest();
		locationSearchRequest.setLocationTypeName("DUMMYLOCTYPE");
		final List<org.generationcp.middleware.api.location.Location> locationList = this.locationDAO.getLocations(locationSearchRequest, new PageRequest(0, 10));
		MatcherAssert.assertThat("Expected list of location size equals to zero", locationList != null && locationList.size() == 0);

	}

	@Test
	public void testCountLocations() {
		final LocationSearchRequest locationSearchRequest = new LocationSearchRequest();
		locationSearchRequest.setLocationTypeName("COUNTRY");
		final long countLocation = this.locationDAO.countLocations(locationSearchRequest);
		MatcherAssert.assertThat("Expected country location size > zero", countLocation > 0);
	}

	@Test
	public void testCountLocationsNotFoundLocation() {
		final LocationSearchRequest locationSearchRequest = new LocationSearchRequest();
		locationSearchRequest.setLocationTypeName("DUMMYLOCTYPE");
		final long countLocation = this.locationDAO.countLocations(locationSearchRequest);
		MatcherAssert.assertThat("Expected country location size equals to zero by this locationType = 000100000405", countLocation == 0);

	}

	@Test
	public void testGetLocationDetails() {

		final int ltype = 405;
		final String labbr = "ABCDEFG";
		final String lname = "MyLocation";

		// Country ID 1 = "Democratic Republic of Afghanistan"
		final int cntryid = 1;
		final Location country = this.locationDAO.getById(cntryid);
		final Location location = LocationTestDataInitializer.createLocation(null, lname, ltype, labbr);
		location.setCountry(country);
		// Province Badakhshan
		final int provinceId = 1001;
		location.setSnl1id(provinceId);
		location.setLdefault(Boolean.FALSE);

		this.locationDAO.saveOrUpdate(location);

		final List<LocationDetails> result = this.locationDAO.getLocationDetails(location.getLocid(), 0, Integer.MAX_VALUE);
		final LocationDetails locationDetails = result.get(0);
		final Location province = this.locationDAO.getById(provinceId);

		Assert.assertEquals(lname, locationDetails.getLocationName());
		Assert.assertEquals(location.getLocid(), locationDetails.getLocid());
		Assert.assertEquals(ltype, locationDetails.getLtype().intValue());
		Assert.assertEquals(labbr, locationDetails.getLocationAbbreviation());
		Assert.assertEquals(cntryid, locationDetails.getCntryid().intValue());
		Assert.assertEquals(province.getLname(), locationDetails.getProvinceName());
		Assert.assertEquals(provinceId, locationDetails.getProvinceId().intValue());

		Assert.assertEquals("COUNTRY", locationDetails.getLocationType());
		Assert.assertEquals("-", locationDetails.getLocationDescription());
		Assert.assertEquals("Democratic Republic of Afghanistan", locationDetails.getCountryFullName());

	}

	@Test
	public void testGetLocationDTO() {

		final Location country = this.locationDAO.getById(1);

		final int ltype = 405;
		final String labbr = RandomStringUtils.randomAlphabetic(7);
		final String lname = RandomStringUtils.randomAlphabetic(9);

		final Location location = LocationTestDataInitializer.createLocation(null, lname, ltype, labbr);
		location.setCountry(country);

		final int provinceId = 1001;
		location.setSnl1id(provinceId);
		location.setLdefault(Boolean.FALSE);

		this.locationDAO.saveOrUpdate(location);

		this.sessionProvder.getSession().flush();

		final LocationDTO locationDTO = this.locationDAO.getLocationDTO(location.getLocid());

		Assert.assertThat(locationDTO.getName(), is(location.getLname()));
		Assert.assertThat(locationDTO.getAbbreviation(), is(location.getLabbr()));
		Assert.assertThat(locationDTO.getCountryId(), is(country.getLocid()));
	}

	@Test
	public void testGetFilteredLocations() {

		final int ltype = 405;
		final String labbr = "ABCDEFG";
		final String lname = "MyLocation";

		// Country ID 1 = "Democratic Republic of Afghanistan"
		final int cntryid = 1;
		final Location country = this.locationDAO.getById(cntryid);
		final Location location = LocationTestDataInitializer.createLocation(null, lname, ltype, labbr);
		location.setCountry(country);
		// Province Badakhshan
		final int provinceId = 1001;
		location.setSnl1id(provinceId);
		location.setLdefault(Boolean.FALSE);

		this.locationDAO.saveOrUpdate(location);
		final Location province = this.locationDAO.getById(1001);

		final List<LocationDetails> result = this.locationDAO.getFilteredLocations(cntryid, ltype, lname);
		final LocationDetails locationDetails = result.get(0);

		Assert.assertEquals(lname, locationDetails.getLocationName());
		Assert.assertEquals(location.getLocid(), locationDetails.getLocid());
		Assert.assertEquals(ltype, locationDetails.getLtype().intValue());
		Assert.assertEquals(labbr, locationDetails.getLocationAbbreviation());
		Assert.assertEquals(country.getLocid(), locationDetails.getCntryid());
		Assert.assertEquals(country.getLname(), locationDetails.getCountryName());
		Assert.assertEquals(province.getLname(), locationDetails.getProvinceName());
		Assert.assertEquals(provinceId, locationDetails.getProvinceId().intValue());

		Assert.assertEquals("COUNTRY", locationDetails.getLocationType());
		Assert.assertEquals("-", locationDetails.getLocationDescription());
		Assert.assertEquals("Democratic Republic of Afghanistan", locationDetails.getCountryFullName());

	}

	@Test
	public void testGetFilteredLocationsFilterByCountryId() {

		final Integer cntryid = 1;

		final List<LocationDetails> locationDetailsList = this.locationDAO.getFilteredLocations(cntryid, null, null);

		// Verify that all locationDetails returned have cntryId = 1
		for (final LocationDetails locationDetails : locationDetailsList) {
			Assert.assertEquals(cntryid, locationDetails.getCntryid());
		}

	}

	@Test
	public void testGetFilteredLocationsFilterByLocationType() {

		final Integer ltype = 405;

		final List<LocationDetails> locationDetailsList = this.locationDAO.getFilteredLocations(null, ltype, null);

		// Verify that all locationDetails returned have cntryId = 1
		for (final LocationDetails locationDetails : locationDetailsList) {
			Assert.assertEquals(ltype, locationDetails.getLtype());
		}

	}

	@Test
	public void testGetFilteredLocationsFilterByLocationName() {

		final String lname = "Unknown";

		final List<LocationDetails> locationDetailsList = this.locationDAO.getFilteredLocations(null, null, lname);

		// Verify that all locationDetails returned have cntryId = 1
		for (final LocationDetails locationDetails : locationDetailsList) {
			Assert.assertEquals(lname, locationDetails.getLocationName());
		}

	}

	@Test
	public void testGetByAbbreviations() {
		final Location location = this.saveTestLocation();

		final List<Location> locations =
			this.locationDAO.getByAbbreviations(Collections.singletonList(location.getLabbr()));

		Assert.assertEquals(locations.size(), 1);
		Assert.assertEquals(locations.get(0).getLabbr(), location.getLabbr());
	}

	@Test
	public void testFilterLocations_Pagination() {
		final LocationSearchRequest locationSearchRequest = new LocationSearchRequest();

		// Page 1
		final PageRequest pageRequest = new PageRequest(0, 10);
		final List<Location> locations =
			this.locationDAO.filterLocations(locationSearchRequest, pageRequest);
		Assert.assertThat(pageRequest.getPageSize(), equalTo(locations.size()));

		// Page 2
		final PageRequest pageRequest2 = new PageRequest(1, 10);
		final List<Location> locations2 =
			this.locationDAO.filterLocations(locationSearchRequest, pageRequest2);
		Assert.assertThat(pageRequest2.getPageSize(), equalTo(locations2.size()));
		Assert.assertThat(locations, not(equalTo(locations2)));

	}

	@Test
	public void testFilterLocations_SearchByLocationName() {
		final String locationName = "Philippines";
		final LocationSearchRequest locationSearchRequest = new LocationSearchRequest();
		locationSearchRequest.setLocationName(locationName);
		final List<Location> locations =
			this.locationDAO
				.filterLocations(locationSearchRequest, new PageRequest(0, 10));
		Assert.assertThat(locationName, equalTo(locations.get(0).getLname()));

	}

	@Test
	public void testFilterLocations_FilterByLocationType() {
		final Integer locationType = 405;
		final LocationSearchRequest locationSearchRequest = new LocationSearchRequest();
		locationSearchRequest.setLocationTypeIds(Collections.singleton(locationType));
		final List<Location> locations =
			this.locationDAO
				.filterLocations(locationSearchRequest,
					new PageRequest(0, 10));

		locations.stream().forEach((location) -> {
			Assert.assertThat(locationType, equalTo(location.getLtype()));
		});
	}

	@Test
	public void testFilterLocations_FilterByLocationId() {
		// Philippines
		final Integer locationId = 171;
		final LocationSearchRequest locationSearchRequest = new LocationSearchRequest();
		locationSearchRequest.setLocationIds(Collections.singletonList(locationId));
		final List<Location> locations =
			this.locationDAO
				.filterLocations(locationSearchRequest,
					new PageRequest(0, 10));

		Assert.assertThat(locationId, equalTo(locations.get(0).getLocid()));
	}

	@Test
	public void testFilterLocations_FilterByAbbreviation() {
		// Philippines
		final String locationAbbreviation = "PHL";
		final LocationSearchRequest locationSearchRequest = new LocationSearchRequest();
		locationSearchRequest.setLocationAbbreviations(Collections.singletonList(locationAbbreviation));
		final List<Location> locations =
			this.locationDAO
				.filterLocations(locationSearchRequest,
					new PageRequest(0, 10));

		Assert.assertThat(locationAbbreviation, equalTo(locations.get(0).getLabbr()));
	}

	private Location saveTestLocation() {
		final String labbr = RandomStringUtils.randomAlphabetic(6);
		final String lname = RandomStringUtils.randomAlphabetic(10);

		final Location country = this.locationDAO.getById(1);
		final Location location = LocationTestDataInitializer.createLocation(null, lname, 0, labbr);
		location.setCountry(country);
		location.setLdefault(Boolean.FALSE);
		this.locationDAO.saveOrUpdate(location);

		return location;
	}

}
