package org.generationcp.middleware.api.location;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.api.location.search.LocationSearchRequest;
import org.generationcp.middleware.api.program.ProgramBasicDetailsDto;
import org.generationcp.middleware.data.initializer.LocationTestDataInitializer;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.ProgramLocationDefault;
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
		final List<LocationDTO> locations = this.locationService
			.searchLocations(new LocationSearchRequest(), new PageRequest(0, 10), null);
		Assert.assertThat(10, equalTo(locations.size()));
	}

	@Test
	public void testCountLocationsByFilterNotRecoveredLocation() {
		final LocationSearchRequest locationSearchRequest = new LocationSearchRequest();
		locationSearchRequest.setLocationTypeName("DUMMYLOCTYOE");
		final long countLocation = this.locationService.countFilteredLocations(locationSearchRequest, null);
		assertThat("Expected country location size equals to zero", 0 == countLocation);
	}

	@Test
	public void testGetLocationsByFilter() {
		final LocationSearchRequest locationSearchRequest = new LocationSearchRequest();
		locationSearchRequest.setLocationTypeName("COUNTRY");
		final List<org.generationcp.middleware.api.location.Location> locationList =
			this.locationService.getLocations(locationSearchRequest, new PageRequest(0, 10));
		assertThat("Expected list of location size > zero", !locationList.isEmpty());
	}

	@Test
	public void testGetLocationsByFilterNotRecoveredLocation() {
		final LocationSearchRequest locationSearchRequest = new LocationSearchRequest();
		locationSearchRequest.setLocationTypeName("DUMMYLOCTYPE");
		final List<org.generationcp.middleware.api.location.Location> locationList =
			this.locationService.getLocations(locationSearchRequest, new PageRequest(0, 10));
		assertThat("Expected list of location size equals to zero", locationList.isEmpty());
	}

	@Test
	public void testGetFilteredLocations_FavoritesOnly() {

		final Country country = this.daoFactory.getCountryDao().getById(1);
		final String programUUID = UUID.randomUUID().toString();
		final Location location = LocationTestDataInitializer
			.createLocation(null, RandomStringUtils.randomAlphabetic(10), 405, RandomStringUtils.randomAlphabetic(3));
		location.setCountry(country);

		this.daoFactory.getLocationDAO().saveOrUpdate(location);

		final ProgramFavorite programFavorite = new ProgramFavorite();
		programFavorite.setUniqueID(programUUID);
		programFavorite.setEntityId(location.getLocid());
		programFavorite.setEntityType(ProgramFavorite.FavoriteType.LOCATION);
		this.daoFactory.getProgramFavoriteDao().save(programFavorite);

		final LocationSearchRequest locationSearchRequest = new LocationSearchRequest();
		locationSearchRequest.setFavoriteProgramUUID(programUUID);
		locationSearchRequest.setFilterFavoriteProgramUUID(true);

		final List<LocationDTO> locations = this.locationService
			.searchLocations(locationSearchRequest, new PageRequest(0, 10), programUUID);

		Assert.assertThat(1, equalTo(locations.size()));
		Assert.assertThat(location.getLocid(), equalTo(locations.get(0).getId()));

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
		Assert.assertThat("Expected diferent Location abbr", locationRequestDto.getAbbreviation(),
			not(equalTo(locationDTO.getAbbreviation())));
		Assert.assertThat("Expected diferent Location type", locationRequestDto.getType(), not(equalTo(locationDTO.getType())));

		Assert.assertThat("Expected diferent altitude", locationRequestDto.getAltitude(), not(equalTo(locationDTO.getAltitude())));
		Assert.assertThat("Expected diferent latitude", locationRequestDto.getLatitude(), not(equalTo(locationDTO.getLatitude())));
		Assert.assertThat("Expected diferent longitude", locationRequestDto.getLongitude(), not(equalTo(locationDTO.getLongitude())));
		Assert.assertThat("Expected diferent country id", locationRequestDto.getCountryId(), not(equalTo(locationDTO.getCountryId())));
		Assert.assertThat("Expected diferent province id", locationRequestDto.getProvinceId(), not(equalTo(locationDTO.getProvinceId())));

	}

	@Test
	public void testSaveProgramLocationDefault() {
		final LocationDTO breedingLocationDTO = this.locationService.createLocation(this.buildLocationRequestDto());
		final LocationDTO storageLocationDTO = this.locationService.createLocation(this.buildLocationRequestDto());
		final ProgramLocationDefault programLocationDefault =
			this.locationService.saveProgramLocationDefault(RandomStringUtils.randomAlphabetic(10), breedingLocationDTO.getId(), storageLocationDTO.getId());
		final ProgramLocationDefault savedProgramLocationDefault = this.daoFactory.getProgramLocationDefaultDAO()
			.getById(programLocationDefault.getId());
		Assert.assertEquals(savedProgramLocationDefault, programLocationDefault);
	}

	@Test
	public void testUpdateProgramLocationDefault() {
		final LocationDTO breedingLocationDTO = this.locationService.createLocation(this.buildLocationRequestDto());
		final LocationDTO storageLocationDTO = this.locationService.createLocation(this.buildLocationRequestDto());
		final ProgramLocationDefault programLocationDefault =
			this.locationService.saveProgramLocationDefault(RandomStringUtils.randomAlphabetic(10), breedingLocationDTO.getId(), storageLocationDTO.getId());
		final ProgramLocationDefault savedProgramLocationDefault = this.daoFactory.getProgramLocationDefaultDAO()
			.getById(programLocationDefault.getId());
		final LocationDTO newBreedingLocationDTO = this.locationService.createLocation(this.buildLocationRequestDto());
		final LocationDTO newStorageLocationDTO = this.locationService.createLocation(this.buildLocationRequestDto());
		final ProgramBasicDetailsDto programBasicDetailsDto = new ProgramBasicDetailsDto();
		programBasicDetailsDto.setBreedingLocationDefaultId(newBreedingLocationDTO.getId());
		programBasicDetailsDto.setStorageLocationDefaultId(newStorageLocationDTO.getId());
		this.locationService.updateProgramLocationDefault(savedProgramLocationDefault.getProgramUUID(), programBasicDetailsDto);
		final ProgramLocationDefault updatedProgramLocationDefault =
			this.locationService.getProgramLocationDefault(savedProgramLocationDefault.getProgramUUID());
		Assert.assertEquals(newBreedingLocationDTO.getId(), updatedProgramLocationDefault.getBreedingLocationId());
		Assert.assertEquals(newStorageLocationDTO.getId(), updatedProgramLocationDefault.getStorageLocationId());
	}

	@Test
	public void testGetProgramLocationDefault() {
		final LocationDTO breedingLocationDTO = this.locationService.createLocation(this.buildLocationRequestDto());
		final LocationDTO storageLocationDTO = this.locationService.createLocation(this.buildLocationRequestDto());
		final ProgramLocationDefault programLocationDefault =
			this.locationService.saveProgramLocationDefault(RandomStringUtils.randomAlphabetic(10), breedingLocationDTO.getId(), storageLocationDTO.getId());
		final ProgramLocationDefault savedProgramLocationDefault =
			this.locationService.getProgramLocationDefault(programLocationDefault.getProgramUUID());
		Assert.assertEquals(programLocationDefault, savedProgramLocationDefault);
	}

	@Test
	public void testGetDefaultBreedingLocation() {
		final LocationDTO breedingLocationDTO = this.locationService.createLocation(this.buildLocationRequestDto());
		final LocationDTO storageLocationDTO = this.locationService.createLocation(this.buildLocationRequestDto());
		final ProgramLocationDefault programLocationDefault =
			this.locationService.saveProgramLocationDefault(RandomStringUtils.randomAlphabetic(10), breedingLocationDTO.getId(), storageLocationDTO.getId());
		final LocationDTO defaultBreedingLocation = this.locationService.getDefaultBreedingLocation(programLocationDefault.getProgramUUID());
		Assert.assertNotNull(defaultBreedingLocation);
		Assert.assertThat("Expected same Location id", breedingLocationDTO.getId(), equalTo(defaultBreedingLocation.getId()));
		Assert.assertThat("Expected same Location name", breedingLocationDTO.getName(), equalTo(defaultBreedingLocation.getName()));
		Assert.assertThat("Expected same Location Abbr", breedingLocationDTO.getAbbreviation(), equalTo(defaultBreedingLocation.getAbbreviation()));
		Assert.assertThat("Expected same Location type", breedingLocationDTO.getType(), equalTo(defaultBreedingLocation.getType()));

		Assert.assertThat("Expected same altitude", breedingLocationDTO.getAltitude(), equalTo(defaultBreedingLocation.getAltitude()));
		Assert.assertThat("Expected same latitude", breedingLocationDTO.getLatitude(), equalTo(defaultBreedingLocation.getLatitude()));
		Assert.assertThat("Expected same longitude", breedingLocationDTO.getLongitude(), equalTo(defaultBreedingLocation.getLongitude()));
		Assert.assertThat("Expected same country id", breedingLocationDTO.getCountryId(), equalTo(defaultBreedingLocation.getCountryId()));
		Assert.assertThat("Expected same province id", breedingLocationDTO.getProvinceId(), equalTo(defaultBreedingLocation.getProvinceId()));
	}

	@Test
	public void testGetDefaultStorageLocation() {
		final LocationDTO breedingLocationDTO = this.locationService.createLocation(this.buildLocationRequestDto());
		final LocationDTO storageLocationDTO = this.locationService.createLocation(this.buildLocationRequestDto());
		final ProgramLocationDefault programLocationDefault =
			this.locationService.saveProgramLocationDefault(RandomStringUtils.randomAlphabetic(10), breedingLocationDTO.getId(), storageLocationDTO.getId());
		final LocationDTO defaultStorageLocation = this.locationService.getDefaultStorageLocation(programLocationDefault.getProgramUUID());
		Assert.assertNotNull(defaultStorageLocation);
		Assert.assertThat("Expected same Location id", storageLocationDTO.getId(), equalTo(defaultStorageLocation.getId()));
		Assert.assertThat("Expected same Location name", storageLocationDTO.getName(), equalTo(defaultStorageLocation.getName()));
		Assert.assertThat("Expected same Location Abbr", storageLocationDTO.getAbbreviation(), equalTo(defaultStorageLocation.getAbbreviation()));
		Assert.assertThat("Expected same Location type", storageLocationDTO.getType(), equalTo(defaultStorageLocation.getType()));

		Assert.assertThat("Expected same altitude", storageLocationDTO.getAltitude(), equalTo(defaultStorageLocation.getAltitude()));
		Assert.assertThat("Expected same latitude", storageLocationDTO.getLatitude(), equalTo(defaultStorageLocation.getLatitude()));
		Assert.assertThat("Expected same longitude", storageLocationDTO.getLongitude(), equalTo(defaultStorageLocation.getLongitude()));
		Assert.assertThat("Expected same country id", storageLocationDTO.getCountryId(), equalTo(defaultStorageLocation.getCountryId()));
		Assert.assertThat("Expected same province id", storageLocationDTO.getProvinceId(), equalTo(defaultStorageLocation.getProvinceId()));
	}

	@Test
	public void testIsProgramBreedingLocationDefault() {
		final LocationDTO breedingLocationDTO = this.locationService.createLocation(this.buildLocationRequestDto());
		final LocationDTO storageLocationDTO = this.locationService.createLocation(this.buildLocationRequestDto());
		final ProgramLocationDefault programLocationDefault =
			this.locationService.saveProgramLocationDefault(RandomStringUtils.randomAlphabetic(10), breedingLocationDTO.getId(), storageLocationDTO.getId());
		Assert.assertTrue(this.locationService.isProgramBreedingLocationDefault(breedingLocationDTO.getId()));
	}

	@Test
	public void testIsProgramStorageLocationDefault() {
		final LocationDTO breedingLocationDTO = this.locationService.createLocation(this.buildLocationRequestDto());
		final LocationDTO storageLocationDTO = this.locationService.createLocation(this.buildLocationRequestDto());
		final ProgramLocationDefault programLocationDefault =
			this.locationService.saveProgramLocationDefault(RandomStringUtils.randomAlphabetic(10), breedingLocationDTO.getId(), storageLocationDTO.getId());
		Assert.assertTrue(this.locationService.isProgramStorageLocationDefault(storageLocationDTO.getId()));
	}

	private LocationRequestDto buildLocationRequestDto() {
		final LocationRequestDto locationRequestDto = new LocationRequestDto();
		locationRequestDto.setType(new Random().nextInt());
		locationRequestDto.setName(RandomStringUtils.randomAlphabetic(10));
		locationRequestDto.setAbbreviation(RandomStringUtils.randomAlphabetic(5));
		locationRequestDto.setCountryId(1);
		locationRequestDto.setProvinceId(1001);
		locationRequestDto.setAltitude(new Random().nextDouble());
		locationRequestDto.setLatitude(new Random().nextDouble());
		locationRequestDto.setLongitude(new Random().nextDouble());
		return locationRequestDto;
	}

}
