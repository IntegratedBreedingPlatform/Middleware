package org.generationcp.middleware.api.location;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.api.location.search.LocationSearchRequest;
import org.generationcp.middleware.api.program.ProgramBasicDetailsDto;
import org.generationcp.middleware.data.initializer.LocationTestDataInitializer;
import org.generationcp.middleware.domain.fieldbook.FieldmapBlockInfo;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.ProgramLocationDefault;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.generationcp.middleware.utils.test.Debug;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.Collections;
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

	@Test
	public void testGetAllLocations() {
		final List<Location> locationList = this.locationService.getAllLocations();
		Assert.assertNotNull(locationList);
		Debug.println(IntegrationTestBase.INDENT, "testGetAllLocations(): ");
		Debug.printObjects(IntegrationTestBase.INDENT, locationList);
	}

	@Test
	public void testGetLocationsByIDs() {

		// Attempt to get all locations so we can proceed
		final List<Location> locationList = this.locationService.getAllLocations();
		Assert.assertNotNull(locationList);
		Assert.assertTrue("we cannot proceed test if size < 0", locationList.size() > 0);

		final List<Integer> ids = new ArrayList<>();

		for (final Location ls : locationList) {
			ids.add(ls.getLocid());

			// only get subset of locations
			if (ids.size() == 5) {
				break;
			}
		}

		final List<Location> results = this.locationService.getLocationsByIDs(ids);
		Assert.assertTrue("Result set must not be null", results != null && !results.isEmpty());
		Assert.assertEquals("Test must only return five results", 5, results.size());

		for (final Location location : results) {
			Assert.assertTrue("Make sure location id received is in the actual requested set.", ids.contains(location.getLocid()));
		}
	}

	@Test
	public void testHandlingOfNullOrEmptyLocationIdsInGetLocationsByIDs() {

		final List<Location> locationsByIdEmptyTest = this.locationService.getLocationsByIDs(Collections.emptyList());
		Assert.assertTrue(
			"Returned result must not be null and must be an empty list",
			locationsByIdEmptyTest != null && locationsByIdEmptyTest.isEmpty());

		final List<Location> locationsByIdNullTest = this.locationService.getLocationsByIDs(null);
		Assert.assertTrue(
			"Returned result must not be null and must be an empty list",
			locationsByIdNullTest != null && locationsByIdNullTest.isEmpty());

	}

	@Test
	public void testGetLocationsByName() {
		final String name = "AFGHANISTAN";
		final int start = 0;
		final int numOfRows = 5;

		final List<Location> locationList = this.locationService.getLocationsByName(name, Operation.EQUAL);
		Assert.assertNotNull(locationList);
		Debug.println(IntegrationTestBase.INDENT, "testGetLocationsByName(" + name + "): " + locationList.size());
		Debug.printObjects(IntegrationTestBase.INDENT, locationList);

		final List<Location> locationList2 = this.locationService.getLocationsByName(name, start, numOfRows, Operation.EQUAL);
		Debug.println(
			IntegrationTestBase.INDENT,
			"testGetLocationsByName(" + name + ", start=" + start + ", numOfRows=" + numOfRows + "): ");
		Debug.printObjects(IntegrationTestBase.INDENT, locationList2);
	}

	@Test
	public void testGetLocationsByNameWithProgramUUID() {
		final String name = "AFGHANISTAN";
		final int start = 0;
		final int numOfRows = 5;

		final List<Location> locationList = this.locationService.getLocationsByName(name, Operation.EQUAL);
		Assert.assertNotNull("Expecting the location list returned is not null.", locationList);

		final List<Location> locationList2 = this.locationService.getLocationsByName(name, start, numOfRows, Operation.EQUAL);
		Assert.assertNotNull("Expecting the location list 2 returned is not null.", locationList2);
	}

	@Test
	public void testRetrieveLocIdOfUnspecifiedLocation() {
		final String unspecifiedLocationID = this.locationService.retrieveLocIdOfUnspecifiedLocation();
		final String unspecifiedLocationName = "Unspecified Location";
		final String unspecifiedLocationAbbr = "NOLOC";

		Assert.assertNotNull(unspecifiedLocationID);
		final Location unspecifiedLocation = this.locationService.getLocationByID(Integer.parseInt(unspecifiedLocationID));
		Assert.assertEquals(unspecifiedLocationName, unspecifiedLocation.getLname());
		Assert.assertEquals(unspecifiedLocationAbbr, unspecifiedLocation.getLabbr());
	}

	@Test
	public void testGetUserDefinedFieldByFieldTable() throws MiddlewareQueryException {
		final String tableName = "LOCATION";
		final String fieldType = "LTYPE";
		final List<UserDefinedField> userDefinedField = this.daoFactory.getUserDefinedFieldDAO().getByFieldTableNameAndType(tableName, ImmutableSet
			.of(fieldType));
		Debug.println(
			IntegrationTestBase.INDENT,
			"testGetUserDefineFieldByTableNameAndType(type=" + tableName + "): " + userDefinedField.size());
		Debug.printObjects(IntegrationTestBase.INDENT, userDefinedField);
	}

	@Test
	public void testGetAllBreedingLocations() throws MiddlewareQueryException {
		final List<Location> locations = this.locationService.getAllBreedingLocations();
		Debug.println(IntegrationTestBase.INDENT, "getAllBreedingLocations()  " + locations);
	}

	@Test
	public void testGetLocationByID() {
		final int id = 1;
		final Location locid = this.locationService.getLocationByID(id);
		Assert.assertNotNull(locid);
		Debug.println(IntegrationTestBase.INDENT, "testGetLocationByID(" + id + "): ");
		Debug.println(IntegrationTestBase.INDENT, locid);
	}

	@Test
	public void testGetAllFieldLocations() {
		final int locationId = 17649; // TODO replace later with get field by id OR get first BREED ltype from location
		final List<Location> result = this.locationService.getAllFieldLocations(locationId);
		Assert.assertNotNull(result);
		Debug.printObjects(3, result);
	}

	@Test
	public void testGetAllBlockLocations() {
		final int fieldId = -11; // TODO replace later with get field by id OR get first field from location
		final List<Location> result = this.locationService.getAllBlockLocations(fieldId);
		Assert.assertNotNull(result);
		Debug.printObjects(3, result);
	}

	@Test
	public void testGetBlockInformation() {
		final int blockId = -15;
		final FieldmapBlockInfo result = this.locationService.getBlockInformation(blockId);
		Assert.assertNotNull(result);
		Debug.println(3, result);
	}

	@Test
	public void testGetAllFields() {
		final List<Location> result = this.locationService.getAllFields();
		Assert.assertNotNull(result);
		Debug.printObjects(3, result);
	}

	@Test
	public void testGeorefIntegration() {
		// retrieve all breeding location
		final List<Location> breedingLocations = this.locationService.getAllBreedingLocations();

		Assert.assertTrue(breedingLocations.size() > 0); // must have a data

		final Location aculcoLoc = this.locationService.getLocationByID(25340); // this location exists on rice central

		// aculco is in wheat
		if (aculcoLoc != null) {

			// aculcoLoc has a georef
			Assert.assertNotNull(aculcoLoc.getGeoref());

			// check equivalency
			Assert.assertEquals("27.5", aculcoLoc.getGeoref().getLat().toString());
			Assert.assertEquals("90.5", aculcoLoc.getGeoref().getLon().toString());
			Assert.assertEquals("2080", aculcoLoc.getGeoref().getAlt().toString());

			final Location location = this.locationService.getLocationByID(aculcoLoc.getLocid());

			Assert.assertEquals(aculcoLoc.getGeoref().getLat(), location.getLatitude());
			Assert.assertEquals(aculcoLoc.getGeoref().getLon(), location.getLongitude());
			Assert.assertEquals(aculcoLoc.getGeoref().getAlt(), location.getAltitude());
		}
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
