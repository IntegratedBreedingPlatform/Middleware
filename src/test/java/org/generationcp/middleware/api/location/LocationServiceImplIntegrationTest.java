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
import java.util.Random;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;

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
	public void testGetLocationsByFilter() {
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

		final Location country = this.daoFactory.getLocationDAO().getById(1);
		final String programUUID = UUID.randomUUID().toString();
		final Location location = LocationTestDataInitializer
			.createLocation(null, RandomStringUtils.randomAlphabetic(10), 405, RandomStringUtils.randomAlphabetic(3));
		location.setCountry(country);
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

	@Test
	public void testDeleteLocation() {
		final LocationRequestDto locationRequestDto = this.buildLocationRequestDto();

		final LocationDTO newLocationDTO = this.locationService.createLocation(locationRequestDto);
		final LocationDTO locationDTO = this.locationService.getLocation(newLocationDTO.getId());

		Assert.assertThat(newLocationDTO.getId(), equalTo(locationDTO.getId()));

		this.locationService.deleteLocation(newLocationDTO.getId());
		this.sessionProvder.getSession().flush();

		final LocationDTO locationDTODeleted = this.locationService.getLocation(newLocationDTO.getId());
		Assert.assertNull(locationDTODeleted);
	}

	@Test
	public void testCreateLocation() {
		final LocationRequestDto locationRequestDto = this.buildLocationRequestDto();

		final LocationDTO newLocationDTO = this.locationService.createLocation(locationRequestDto);
		this.sessionProvder.getSession().flush();
		final LocationDTO locationDTO = this.locationService.getLocation(newLocationDTO.getId());

		Assert.assertNotNull(locationDTO);
		Assert.assertThat("Expected same Location id", newLocationDTO.getId(), equalTo(locationDTO.getId()));
		Assert.assertThat("Expected same Location name", locationRequestDto.getName(), equalTo(locationDTO.getName()));
		Assert.assertThat("Expected same Location Abbr", locationRequestDto.getAbbreviation(), equalTo(locationDTO.getAbbreviation()));
		Assert.assertThat("Expected same Location type", locationRequestDto.getType(), equalTo(locationDTO.getType()));

		Assert.assertThat("Expected same altitude", locationRequestDto.getAltitude(), equalTo(locationDTO.getAltitude()));
		Assert.assertThat("Expected same latitude", locationRequestDto.getLatitude(), equalTo(locationDTO.getLatitude()));
		Assert.assertThat("Expected same longitude", locationRequestDto.getLongitude(), equalTo(locationDTO.getLongitude()));
		Assert.assertThat("Expected same country id", locationRequestDto.getCountryId(), equalTo(locationDTO.getCountryId()));
		Assert.assertThat("Expected same province id", locationRequestDto.getProvinceId(), equalTo(locationDTO.getProvinceId()));
	}

	@Test
	public void testUpdateLocation() {
		final LocationRequestDto locationRequestDto = this.buildLocationRequestDto();

		final LocationDTO newLocationDTO = this.locationService.createLocation(locationRequestDto);
		final LocationDTO locationDTO = this.locationService.getLocation(newLocationDTO.getId());

		locationRequestDto.setName(RandomStringUtils.randomAlphabetic(10));
		locationRequestDto.setAbbreviation(RandomStringUtils.randomAlphabetic(5));
		locationRequestDto.setType(new Random().nextInt());
		locationRequestDto.setAltitude(new Random().nextDouble());
		locationRequestDto.setLongitude(new Random().nextDouble());
		locationRequestDto.setLatitude(new Random().nextDouble());
		locationRequestDto.setCountryId(new Random().nextInt());
		locationRequestDto.setProvinceId(new Random().nextInt());

		this.locationService.updateLocation(newLocationDTO.getId(), locationRequestDto);

		Assert.assertNotNull(locationDTO);
		Assert.assertThat("Expected same Location id", newLocationDTO.getId(), equalTo(locationDTO.getId()));
		Assert.assertThat("Expected diferent Location name", locationRequestDto.getName(), not(equalTo(locationDTO.getName())));
		Assert.assertThat("Expected diferent Location abbr", locationRequestDto.getAbbreviation(), not(equalTo(locationDTO.getAbbreviation())));
		Assert.assertThat("Expected diferent Location type", locationRequestDto.getType(), not(equalTo(locationDTO.getType())));

		Assert.assertThat("Expected diferent altitude", locationRequestDto.getAltitude(), not(equalTo(locationDTO.getAltitude())));
		Assert.assertThat("Expected diferent latitude", locationRequestDto.getLatitude(), not(equalTo(locationDTO.getLatitude())));
		Assert.assertThat("Expected diferent longitude", locationRequestDto.getLongitude(), not(equalTo(locationDTO.getLongitude())));
		Assert.assertThat("Expected diferent country id", locationRequestDto.getCountryId(), not(equalTo(locationDTO.getCountryId())));
		Assert.assertThat("Expected diferent province id", locationRequestDto.getProvinceId(), not(equalTo(locationDTO.getProvinceId())));

	}

	private LocationRequestDto buildLocationRequestDto() {
		final LocationRequestDto locationRequestDto = new LocationRequestDto();
		locationRequestDto.setType(new Random().nextInt());
		locationRequestDto.setName(RandomStringUtils.randomAlphabetic(10));
		locationRequestDto.setAbbreviation(RandomStringUtils.randomAlphabetic(5));
		locationRequestDto.setCountryId(1);
		locationRequestDto.setProvinceId(new Random().nextInt());
		locationRequestDto.setAltitude(new Random().nextDouble());
		locationRequestDto.setLatitude(new Random().nextDouble());
		locationRequestDto.setLongitude(new Random().nextDouble());
		return locationRequestDto;
	}

}
