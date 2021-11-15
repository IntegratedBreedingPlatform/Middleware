package org.generationcp.middleware.api.location;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.api.location.search.LocationSearchRequest;
import org.generationcp.middleware.data.initializer.LocationTestDataInitializer;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class LocationServiceImplIntegrationTest extends IntegrationTestBase {

	private DaoFactory daoFactory;

	@Autowired
	private LocationService locationService;

	@Before
	public void setUp() throws Exception {
		this.daoFactory = new DaoFactory(this.sessionProvder);
	}

	@Test
	public void testGetFilteredLocations() {
		final List<Location> locations = this.locationService
			.getFilteredLocations(new LocationSearchRequest(), new PageRequest(0, 10));
		Assert.assertThat(10, equalTo(locations.size()));
	}

	@Test
	public void testCountLocationsByFilterNotRecoveredLocation() {
		final LocationSearchRequest locationSearchRequest = new LocationSearchRequest();
		locationSearchRequest.setLocationTypeName("DUMMYLOCTYOE");
		final long countLocation = this.locationService.countFilteredLocations(locationSearchRequest);
		assertThat("Expected country location size equals to zero", 0 == countLocation);
	}

	@Test
	public void testGetLocationsByFilter(){
		final LocationSearchRequest locationSearchRequest = new LocationSearchRequest();
		locationSearchRequest.setLocationTypeName("COUNTRY");
		final List<org.generationcp.middleware.api.location.Location> locationList = this.locationService.getLocations(locationSearchRequest, new PageRequest(0, 10));
		assertThat("Expected list of location size > zero", !locationList.isEmpty());
	}

	@Test
	public void testGetLocationsByFilterNotRecoveredLocation() {
		final LocationSearchRequest locationSearchRequest = new LocationSearchRequest();
		locationSearchRequest.setLocationTypeName("DUMMYLOCTYPE");
		final List<org.generationcp.middleware.api.location.Location> locationList = this.locationService.getLocations(locationSearchRequest, new PageRequest(0, 10));
		assertThat("Expected list of location size equals to zero", locationList.isEmpty());
	}

	@Test
	public void testGetFilteredLocations_FavoritesOnly() {

		final String programUUID = UUID.randomUUID().toString();
		final int cntryid = 1;
		final Location location = LocationTestDataInitializer
			.createLocation(null, RandomStringUtils.randomAlphabetic(10), 405, RandomStringUtils.randomAlphabetic(3));
		location.setCntryid(cntryid);
		location.setLdefault(Boolean.FALSE);

		this.daoFactory.getLocationDAO().saveOrUpdate(location);

		final ProgramFavorite programFavorite = new ProgramFavorite();
		programFavorite.setUniqueID(programUUID);
		programFavorite.setEntityId(location.getLocid());
		programFavorite.setEntityType("LOCATION");
		this.daoFactory.getProgramFavoriteDao().save(programFavorite);

		final LocationSearchRequest locationSearchRequest = new LocationSearchRequest();
		locationSearchRequest.setFavoriteProgramUUID(programUUID);

		final List<Location> locations = this.locationService
			.getFilteredLocations(locationSearchRequest, new PageRequest(0, 10));

		Assert.assertThat(1, equalTo(locations.size()));
		Assert.assertThat(location.getLocid(), equalTo(locations.get(0).getLocid()));

	}

}
