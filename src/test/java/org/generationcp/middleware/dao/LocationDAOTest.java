package org.generationcp.middleware.dao;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.api.location.LocationDTO;
import org.generationcp.middleware.api.location.search.LocationSearchRequest;
import org.generationcp.middleware.data.initializer.LocationTestDataInitializer;
import org.generationcp.middleware.domain.sqlfilter.SqlTextFilter;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.LocationDetails;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.not;

public class LocationDAOTest extends IntegrationTestBase {

	private LocationDAO locationDAO;
	private CountryDAO countryDAO;

	@Before
	public void setUp() throws Exception {
		this.locationDAO = new LocationDAO(this.sessionProvder.getSession());
		this.countryDAO = new CountryDAO();
		this.countryDAO.setSession(this.sessionProvder.getSession());
	}

	@Test
	public void testGetLocations() {
		final LocationSearchRequest locationSearchRequest = new LocationSearchRequest();
		locationSearchRequest.setLocationTypeName("Country");
		final List<org.generationcp.middleware.api.location.Location> locationList =
			this.locationDAO.getLocations(locationSearchRequest, new PageRequest(0, 10));
		MatcherAssert.assertThat("Expected list of country location size > zero", locationList != null && locationList.size() > 0);
	}

	@Test
	public void testGetLocations_ByAbbreviation() {
		final Location location = this.saveTestLocation();

		final LocationSearchRequest locationSearchRequest = new LocationSearchRequest();
		locationSearchRequest.setLocationAbbreviations(Collections.singletonList(location.getLabbr()));
		final List<org.generationcp.middleware.api.location.Location> locationList =
			this.locationDAO.getLocations(locationSearchRequest, new PageRequest(0, 10));
		MatcherAssert.assertThat("Expected to return filtered list by abbreviation", locationList != null && locationList.size() > 0);
	}

	@Test
	public void testGetLocationsWithWrongLocType() {
		final LocationSearchRequest locationSearchRequest = new LocationSearchRequest();
		locationSearchRequest.setLocationTypeName("DUMMYLOCTYPE");
		final List<org.generationcp.middleware.api.location.Location> locationList =
			this.locationDAO.getLocations(locationSearchRequest, new PageRequest(0, 10));
		MatcherAssert.assertThat("Expected list of location size equals to zero", locationList != null && locationList.size() == 0);

	}

	@Test
	public void testCountLocations() {
		final LocationSearchRequest locationSearchRequest = new LocationSearchRequest();
		locationSearchRequest.setLocationTypeName("COUNTRY");
		final long countLocation = this.locationDAO.countSearchLocation(locationSearchRequest, null);
		MatcherAssert.assertThat("Expected country location size > zero", countLocation > 0);
	}

	@Test
	public void testCountLocationsNotFoundLocation() {
		final LocationSearchRequest locationSearchRequest = new LocationSearchRequest();
		locationSearchRequest.setLocationTypeName("DUMMYLOCTYPE");
		final long countLocation = this.locationDAO.countSearchLocation(locationSearchRequest, null);
		MatcherAssert.assertThat("Expected country location size equals to zero by this locationType = 000100000405", countLocation == 0);

	}

	@Test
	public void testGetLocationDetails() {

		final int ltype = 405;
		final String labbr = "ABCDEFG";
		final String lname = "MyLocation";

		// Country ID 1 = "Democratic Republic of Afghanistan"
		final int cntryid = 1;
		final Country country = this.countryDAO.getById(cntryid);
		final Location location = LocationTestDataInitializer.createLocation(null, lname, ltype, labbr);
		location.setCountry(country);
		// Province Badakhshan
		final int provinceId = 1001;
		final Location province = this.locationDAO.getById(provinceId);
		location.setProvince(province);
		location.setLdefault(Boolean.FALSE);

		this.locationDAO.saveOrUpdate(location);

		final List<LocationDetails> result = this.locationDAO.getLocationDetails(location.getLocid(), 0, Integer.MAX_VALUE);
		final LocationDetails locationDetails = result.get(0);

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

		final Country country = this.countryDAO.getById(1);

		final int ltype = 405;
		final String labbr = RandomStringUtils.randomAlphabetic(7);
		final String lname = RandomStringUtils.randomAlphabetic(9);

		final Location location = LocationTestDataInitializer.createLocation(null, lname, ltype, labbr);
		location.setCountry(country);

		final Location province = this.locationDAO.getById(1001);
		location.setProvince(province);
		location.setLdefault(Boolean.FALSE);

		this.locationDAO.saveOrUpdate(location);

		this.sessionProvder.getSession().flush();

		final LocationDTO locationDTO = this.locationDAO.getLocationDTO(location.getLocid());

		Assert.assertThat(locationDTO.getName(), is(location.getLname()));
		Assert.assertThat(locationDTO.getAbbreviation(), is(location.getLabbr()));
		Assert.assertThat(locationDTO.getCountryId(), is(country.getCntryid()));
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
		final List<LocationDTO> locations =
			this.locationDAO.searchLocations(locationSearchRequest, pageRequest, null);
		Assert.assertThat(pageRequest.getPageSize(), equalTo(locations.size()));

		// Page 2
		final PageRequest pageRequest2 = new PageRequest(1, 10);
		final List<LocationDTO> locations2 =
			this.locationDAO.searchLocations(locationSearchRequest, pageRequest2, null);
		Assert.assertThat(pageRequest2.getPageSize(), equalTo(locations2.size()));
		Assert.assertThat(locations, not(equalTo(locations2)));

	}

	@Test
	public void testFilterLocations_SearchByLocationName() {
		final String locationName = "Philippines";
		final LocationSearchRequest locationSearchRequest = new LocationSearchRequest();
		final SqlTextFilter locationNameFilter = new SqlTextFilter();
		locationNameFilter.setValue(locationName);
		locationNameFilter.setType(SqlTextFilter.Type.EXACTMATCH);
		locationSearchRequest.setLocationNameFilter(locationNameFilter);
		final List<LocationDTO> locations =
			this.locationDAO
				.searchLocations(locationSearchRequest, new PageRequest(0, 10), null);
		Assert.assertThat(locationName, equalTo(locations.get(0).getName()));

	}

	@Test
	public void testFilterLocations_FilterByLocationType() {
		final Integer locationType = 405;
		final LocationSearchRequest locationSearchRequest = new LocationSearchRequest();
		locationSearchRequest.setLocationTypeIds(Collections.singleton(locationType));
		final List<LocationDTO> locations =
			this.locationDAO
				.searchLocations(locationSearchRequest,
					new PageRequest(0, 10), null);

		locations.stream().forEach((location) -> {
			Assert.assertThat(locationType, equalTo(location.getType()));
		});
	}

	@Test
	public void testFilterLocations_FilterByLocationId() {
		// Philippines
		final Integer locationId = 171;
		final LocationSearchRequest locationSearchRequest = new LocationSearchRequest();
		locationSearchRequest.setLocationIds(Collections.singletonList(locationId));
		final List<LocationDTO> locations =
			this.locationDAO
				.searchLocations(locationSearchRequest,
					new PageRequest(0, 10), null);

		Assert.assertThat(locationId, equalTo(locations.get(0).getId()));
	}

	@Test
	public void testFilterLocations_FilterByAbbreviation() {
		// Philippines
		final String locationAbbreviation = "PHL";
		final LocationSearchRequest locationSearchRequest = new LocationSearchRequest();
		locationSearchRequest.setLocationAbbreviations(Collections.singletonList(locationAbbreviation));
		final List<LocationDTO> locations =
			this.locationDAO
				.searchLocations(locationSearchRequest,
					new PageRequest(0, 10), null);

		Assert.assertThat(locationAbbreviation, equalTo(locations.get(0).getAbbreviation()));
	}

	private Location saveTestLocation() {
		final String labbr = RandomStringUtils.randomAlphabetic(6);
		final String lname = RandomStringUtils.randomAlphabetic(10);

		final Country country = this.countryDAO.getById(1);
		final Location location = LocationTestDataInitializer.createLocation(null, lname, 0, labbr);
		location.setCountry(country);
		location.setLdefault(Boolean.FALSE);
		this.locationDAO.saveOrUpdate(location);

		return location;
	}

}
