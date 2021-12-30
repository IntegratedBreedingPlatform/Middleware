/*******************************************************************************
 * Copyright (c) 2014, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.manager;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.domain.fieldbook.FieldmapBlockInfo;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.LocationDetails;
import org.generationcp.middleware.pojos.Locdes;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.utils.test.Debug;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocationDataManagerImplTest extends IntegrationTestBase {

	@Autowired
	private LocationDataManager manager;

	@Autowired
	private WorkbenchTestDataUtil workbenchTestDataUtil;

	private Project commonTestProject;

	private final static Integer COUNTRY_LTYPEID = 405;
	private final static Integer PHILIPPINES_CNTRYID = 171;

	@Before
	public void setUp() throws Exception {

		this.workbenchTestDataUtil.setUpWorkbench();

		if (this.commonTestProject == null) {
			this.commonTestProject = this.workbenchTestDataUtil.getCommonTestProject();
		}
	}

	@Test
	public void testGetAllLocations() {
		final List<Location> locationList = this.manager.getAllLocations();
		Assert.assertNotNull(locationList);
		Debug.println(IntegrationTestBase.INDENT, "testGetAllLocations(): ");
		Debug.printObjects(IntegrationTestBase.INDENT, locationList);
	}

	@Test
	public void testGetLocationsByIDs() {

		// Attempt to get all locations so we can proceed
		final List<Location> locationList = this.manager.getAllLocations();
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

		final List<Location> results = this.manager.getLocationsByIDs(ids);
		Assert.assertTrue("Result set must not be null", results != null && !results.isEmpty());
		Assert.assertEquals("Test must only return five results", 5, results.size());

		for (final Location location : results) {
			Assert.assertTrue("Make sure location id received is in the actual requested set.", ids.contains(location.getLocid()));
		}
	}

	@Test
	public void testCountByLocationAbbreviation() {
		final Country country = this.manager.getCountryById(1);
		final Location province = this.manager.getLocationByID(1001);
		final String labbr = "LABBRR";
		final long count = this.manager.countByLocationAbbreviation(labbr);

		final Location location = new Location();
		location.setCountry(country);
		location.setLabbr(labbr);
		location.setLname("ADDED LOCATION");
		location.setLrplce(1);
		location.setLtype(1);
		location.setNllp(1);
		location.setProvince(province);
		location.setSnl2id(1);
		location.setSnl3id(1);
		location.setLdefault(Boolean.FALSE);

		// add the location
		this.manager.addLocation(location);
		final long newCount = this.manager.countByLocationAbbreviation(labbr);
		Assert.assertEquals(count + 1, newCount);
	}

	@Test
	public void testHandlingOfNullOrEmptyLocationIdsInGetLocationsByIDs() {

		final List<Location> locationsByIdEmptyTest = this.manager.getLocationsByIDs(Collections.emptyList());
		Assert.assertTrue("Returned result must not be null and must be an empty list",
			locationsByIdEmptyTest != null && locationsByIdEmptyTest.isEmpty());

		final List<Location> locationsByIdNullTest = this.manager.getLocationsByIDs(null);
		Assert.assertTrue("Returned result must not be null and must be an empty list",
			locationsByIdNullTest != null && locationsByIdNullTest.isEmpty());

	}

	@Test
	public void testGetAllCountry() {
		final List<Country> countryList = this.manager.getAllCountry();
		Assert.assertNotNull(countryList);
		Debug.printObjects(IntegrationTestBase.INDENT, countryList);
	}

	@Test
	public void testLocationDetails() {
		final List<LocationDetails> locationList = this.manager.getLocationDetailsByLocId(1, 1, 1);
		Assert.assertNotNull(locationList);
		Debug.printObjects(IntegrationTestBase.INDENT, locationList);
	}

	@Test
	public void testCountAllLocations() {
		final long count = this.manager.countAllLocations();
		Debug.println(IntegrationTestBase.INDENT, "testCountAllLocations(): " + count);
	}

	@Test
	public void testGetLocationsByName() {
		final String name = "AFGHANISTAN";
		final int start = 0;
		final int numOfRows = 5;

		final List<Location> locationList = this.manager.getLocationsByName(name, Operation.EQUAL);
		Assert.assertNotNull(locationList);
		Debug.println(IntegrationTestBase.INDENT, "testGetLocationsByName(" + name + "): " + locationList.size());
		Debug.printObjects(IntegrationTestBase.INDENT, locationList);

		final List<Location> locationList2 = this.manager.getLocationsByName(name, start, numOfRows, Operation.EQUAL);
		Debug.println(IntegrationTestBase.INDENT,
			"testGetLocationsByName(" + name + ", start=" + start + ", numOfRows=" + numOfRows + "): ");
		Debug.printObjects(IntegrationTestBase.INDENT, locationList2);
	}

	@Test
	public void testGetLocationsByNameWithProgramUUID() {
		final String name = "AFGHANISTAN";
		final int start = 0;
		final int numOfRows = 5;

		final List<Location> locationList = this.manager.getLocationsByName(name, Operation.EQUAL);
		Assert.assertNotNull("Expecting the location list returned is not null.", locationList);

		final List<Location> locationList2 = this.manager.getLocationsByName(name, start, numOfRows, Operation.EQUAL);
		Assert.assertNotNull("Expecting the location list 2 returned is not null.", locationList2);
	}

	@Test
	public void testCountLocationsByName() {
		final String name = "AFGHANISTAN";
		final long count = this.manager.countLocationsByName(name, Operation.EQUAL);
		Debug.println(IntegrationTestBase.INDENT, "testCountLocationByName(" + name + "): " + count);
	}

	@Test
	public void testGetLocationsByCountry() throws MiddlewareQueryException {
		final Integer id = 171; // Tested in rice db. 171 = Philippines
		final Country country = this.manager.getCountryById(id);
		final int start = 0;
		final int numOfRows = 5;

		final List<Location> locations = this.manager.getLocationsByCountry(country);
		Debug.println(IntegrationTestBase.INDENT, "testGetLocationByCountry(country=" + country + "): " + locations.size());
		Debug.printObjects(IntegrationTestBase.INDENT, locations);

		final List<Location> locationList = this.manager.getLocationsByCountry(country, start, numOfRows);
		Debug.println(IntegrationTestBase.INDENT,
			"testGetLocationByCountry(country=" + country + ", start=" + start + ", numOfRows=" + numOfRows + "): " + locationList
				.size());
		Debug.printObjects(IntegrationTestBase.INDENT, locationList);
	}

	@Test
	public void testGetLocationsByCountryAndType() throws MiddlewareQueryException {
		final Integer id = 1; // Tested in rice db. 171 = Philippines
		final Country country = this.manager.getCountryById(id);
		final int type = 405;

		final List<Location> locations = this.manager.getLocationsByCountryAndType(country, type);
		Debug.println(IntegrationTestBase.INDENT,
			"testGetLocationByCountryAndType(country=" + country + "): type= " + type + ":" + locations.size());
		Debug.printObjects(IntegrationTestBase.INDENT, locations);
	}

	@Test
	public void testCountLocationsByCountry() {
		final Integer id = 171; // Tested in rice db. 171 = Philippines
		final Country country = this.manager.getCountryById(id);
		final long count = this.manager.countLocationsByCountry(country);
		Debug.println(IntegrationTestBase.INDENT, "testCountLocationByCountry(country=" + country + "): " + count);
	}

	@Test
	public void testGetLocationsByType() throws MiddlewareQueryException {
		final int type = 405; // Tested in rice db
		final int start = 0;
		final int numOfRows = 5;

		final List<Location> locations = this.manager.getLocationsByType(type);
		Assert.assertNotNull("Expecting to have returned results.", locations);
		Debug.println(IntegrationTestBase.INDENT, "testGetLocationByType(type=" + type + "): " + locations.size());
		Debug.printObjects(IntegrationTestBase.INDENT, locations);

		final List<Location> locationsByProgramUUID = this.manager.getLocationsByType(type);
		Assert.assertNotNull("Expecting to have returned results.", locationsByProgramUUID);
		Debug.println(IntegrationTestBase.INDENT, "testGetLocationByType(type=" + type + "): " + locations.size());
		Debug.printObjects(IntegrationTestBase.INDENT, locationsByProgramUUID);

		final List<Location> locationList = this.manager.getLocationsByType(type, start, numOfRows);
		Assert.assertNotNull("Expecting to have returned results.", locationList);
		Debug.println(IntegrationTestBase.INDENT,
			"testGetLocationByType(type=" + type + ", start=" + start + ", numOfRows=" + numOfRows + "): " + locationList.size());
		Debug.printObjects(IntegrationTestBase.INDENT, locations);
	}

	@Test
	public void testCountLocationsByType() {
		int type = 405; // Tested in rice db
		long count = this.manager.countLocationsByType(type);
		Debug.println(IntegrationTestBase.INDENT, "testCountLocationByType(type=" + type + "): " + count);
	}

	@Test
	public void testAddLocation() throws MiddlewareQueryException {
		final Country country = this.manager.getCountryById(1);
		final Location province = this.manager.getLocationByID(1001);
		final Location location = new Location();
		location.setCountry(country);
		location.setLabbr("");
		location.setLname("TEST-LOCATION-1");
		location.setLrplce(1);
		location.setLtype(1);
		location.setNllp(1);
		location.setProvince(province);
		location.setSnl2id(1);
		location.setSnl3id(1);
		location.setLdefault(Boolean.FALSE);

		// add the location
		final Integer id = this.manager.addLocation(location);
		Debug.println(IntegrationTestBase.INDENT, "testAddLocation(" + location + "): " + id + "  \n  " + this.manager
			.getLocationsByName("TEST-LOCATION-1", 0, 5, Operation.EQUAL));
	}

	@Test
	public void testAddLocations() throws MiddlewareQueryException {

		final Country country = this.manager.getCountryById(1);
		final Location province = this.manager.getLocationByID(1001);

		final List<Location> locations = new ArrayList<>();

		final Location location1 = new Location();
		location1.setCountry(country);
		location1.setLabbr(RandomStringUtils.randomAlphabetic(4).toUpperCase());
		location1.setLname("TEST-LOCATION-2");
		location1.setLrplce(1);
		location1.setLtype(1);
		location1.setNllp(1);
		location1.setProvince(province);
		location1.setSnl2id(1);
		location1.setSnl3id(1);
		location1.setLdefault(Boolean.FALSE);

		final Location location2 = new Location();
		location2.setCountry(country);
		location2.setLabbr(RandomStringUtils.randomAlphabetic(4).toUpperCase());
		location2.setLname("TEST-LOCATION-3");
		location2.setLrplce(1);
		location2.setLtype(1);
		location2.setNllp(1);
		location2.setProvince(province);
		location2.setSnl2id(1);
		location2.setSnl3id(1);
		location2.setLdefault(Boolean.FALSE);

		locations.add(location1);
		locations.add(location2);

		// add the location
		final List<Integer> locationsAdded = this.manager.addLocation(locations);

		Debug.println(IntegrationTestBase.INDENT, "testAddLocations() Locations added: " + locationsAdded.size());
		Debug.println(IntegrationTestBase.INDENT, this.manager.getLocationsByName("TEST-LOCATION-2", 0, 5, Operation.EQUAL));
		Debug.println(IntegrationTestBase.INDENT, this.manager.getLocationsByName("TEST-LOCATION-3", 0, 5, Operation.EQUAL));
	}

	@Test
	public void testGetUserDefinedFieldByFieldTable() throws MiddlewareQueryException {
		final String tableName = "LOCATION";
		final String fieldType = "LTYPE";
		final List<UserDefinedField> userDefinedField = this.manager.getUserDefinedFieldByFieldTableNameAndType(tableName, fieldType);
		Debug.println(IntegrationTestBase.INDENT,
			"testGetUserDefineFieldByTableNameAndType(type=" + tableName + "): " + userDefinedField.size());
		Debug.printObjects(IntegrationTestBase.INDENT, userDefinedField);
	}

	@Test
	public void testGetAllBreedingLocations() throws MiddlewareQueryException {
		final List<Location> locations = this.manager.getAllBreedingLocations();
		Debug.println(IntegrationTestBase.INDENT, "getAllBreedingLocations()  " + locations);
	}

	@Test
	public void testCountAllBreedingLocations() throws MiddlewareQueryException {
		final Long locationCount = this.manager.countAllBreedingLocations();
		Debug.println(IntegrationTestBase.INDENT, "countAllBreedingLocations() - Total Count = " + locationCount);
	}

	@Test
	public void testGetCountryById() {
		final int id = 1;
		final Country countries = this.manager.getCountryById(id);
		Assert.assertNotNull(countries);
		Debug.println(IntegrationTestBase.INDENT, "testGetCountryById(" + id + "): ");
		Debug.println(IntegrationTestBase.INDENT, countries);
	}

	@Test
	public void testGetLocationByID() {
		final int id = 1;
		final Location locid = this.manager.getLocationByID(id);
		Assert.assertNotNull(locid);
		Debug.println(IntegrationTestBase.INDENT, "testGetLocationByID(" + id + "): ");
		Debug.println(IntegrationTestBase.INDENT, locid);
	}

	@Test
	public void testGetLocationDetailsByLocId() {
		final int locationId = 1001;
		final List<LocationDetails> locdetails = this.manager.getLocationDetailsByLocId(locationId, 0, 100);
		Assert.assertNotNull(locdetails);
		Assert.assertFalse(locdetails.isEmpty());
		Debug.println(IntegrationTestBase.INDENT, "testGetLocationDetailsByLocId(" + locationId + "): ");
		Debug.printObjects(IntegrationTestBase.INDENT, locdetails);
	}

	@Test
	public void testGetUserDefinedFieldByID() {
		final int id = 2;
		final UserDefinedField result = this.manager.getUserDefinedFieldByID(id);
		Assert.assertNotNull(result);
		Debug.println(3, result);
	}

	@Test
	public void testGetAllFieldLocations() {
		final int locationId = 17649; // TODO replace later with get field by id OR get first BREED ltype from location
		final List<Location> result = this.manager.getAllFieldLocations(locationId);
		Assert.assertNotNull(result);
		Debug.printObjects(3, result);
	}

	@Test
	public void testGetAllBlockLocations() {
		final int fieldId = -11; // TODO replace later with get field by id OR get first field from location
		final List<Location> result = this.manager.getAllBlockLocations(fieldId);
		Assert.assertNotNull(result);
		Debug.printObjects(3, result);
	}

	@Test
	public void testGetBlockInformation() {
		final int blockId = -15;
		final FieldmapBlockInfo result = this.manager.getBlockInformation(blockId);
		Assert.assertNotNull(result);
		Debug.println(3, result);
	}

	@Test
	public void testGetAllFields() {
		final List<Location> result = this.manager.getAllFields();
		Assert.assertNotNull(result);
		Debug.printObjects(3, result);
	}

	@Test
	public void testGetProvincesByCountry() {
		final List<Location> provinces = this.manager.getAllProvincesByCountry(101);

		Assert.assertNotNull(provinces);
		Assert.assertTrue(provinces.size() > 0);
		Debug.printObjects(3, provinces);
	}

	@Test
	public void testGeorefIntegration() {
		// retrieve all breeding location
		final List<Location> breedingLocations = this.manager.getAllBreedingLocations();

		Assert.assertTrue(breedingLocations.size() > 0); // must have a data

		final Location aculcoLoc = this.manager.getLocationByID(25340); // this location exists on rice central

		// aculco is in wheat
		if (aculcoLoc != null) {

			// aculcoLoc has a georef
			Assert.assertNotNull(aculcoLoc.getGeoref());

			// check equivalency
			Assert.assertEquals("27.5", aculcoLoc.getGeoref().getLat().toString());
			Assert.assertEquals("90.5", aculcoLoc.getGeoref().getLon().toString());
			Assert.assertEquals("2080", aculcoLoc.getGeoref().getAlt().toString());

			final List<LocationDetails> locationDetails =
				this.manager.getLocationDetailsByLocId(aculcoLoc.getLocid(), 0, Integer.MAX_VALUE);

			Assert.assertEquals(aculcoLoc.getGeoref().getLat(), locationDetails.get(0).getLatitude());
			Assert.assertEquals(aculcoLoc.getGeoref().getLon(), locationDetails.get(0).getLongitude());
			Assert.assertEquals(aculcoLoc.getGeoref().getAlt(), locationDetails.get(0).getAltitude());
		}

	}

	@Test
	public void testGetLocationsByNameCountryAndType_NullFilter() throws MiddlewareQueryException {
		final String locationName = "";
		final Country country = null;
		final Integer locationTypeId = null;
		final List<Location> locationList = this.manager.getLocationsByNameCountryAndType(locationName, country, locationTypeId);
		Assert.assertFalse("Location list should not be empty", locationList.isEmpty());
	}

	@Test
	public void testGetLocationsByNameCountryAndType_NullFilterWithZeroLocationTypeId() throws MiddlewareQueryException {
		final String locationName = "";
		final Country country = null;
		final Integer locationTypeId = 0;
		final List<Location> locationList = this.manager.getLocationsByNameCountryAndType(locationName, country, locationTypeId);
		Assert.assertFalse("Location list should not be empty", locationList.isEmpty());
		boolean hasLocationWithNonZeroLtypeId = false;
		for (final Location location : locationList) {
			if (location.getLtype() != null && 0 != location.getLtype()) {
				hasLocationWithNonZeroLtypeId = true;
				break;
			}
		}
		Assert.assertTrue("Location list should contain items with non-zero lytpeId", hasLocationWithNonZeroLtypeId);
	}

	@Test
	public void testGetLocationsByNameCountryAndType_WithNonZeroLocationTypeId() throws MiddlewareQueryException {
		final String locationName = "";
		final Country country = null;
		final List<Location> locationList =
			this.manager.getLocationsByNameCountryAndType(locationName, country, LocationDataManagerImplTest.COUNTRY_LTYPEID);
		Assert.assertFalse("Location list should not be empty", locationList.isEmpty());
		for (final Location location : locationList) {
			Assert.assertEquals("Location should have a lytpeId = " + LocationDataManagerImplTest.COUNTRY_LTYPEID,
				LocationDataManagerImplTest.COUNTRY_LTYPEID, location.getLtype());
		}
	}

	@Test
	public void testGetLocationsByNameCountryAndType_WithNonZeroCountryId() throws MiddlewareQueryException {
		final String locationName = "";
		final Country country = new Country();
		country.setCntryid(LocationDataManagerImplTest.PHILIPPINES_CNTRYID);
		final Integer locationTypeId = null;
		final List<Location> locationList = this.manager.getLocationsByNameCountryAndType(locationName, country, locationTypeId);
		Assert.assertFalse("Location list should not be empty", locationList.isEmpty());
		for (final Location location : locationList) {
			Assert.assertEquals("Location should have a countryId = " + LocationDataManagerImplTest.PHILIPPINES_CNTRYID,
				LocationDataManagerImplTest.PHILIPPINES_CNTRYID, location.getCountry().getCntryid());
		}
	}

	@Test
	public void testGetLocationsByNameCountryAndType_WithNonEmptyLocationName() throws MiddlewareQueryException {
		final String locationName = "Phil";
		final Country country = null;
		final Integer locationTypeId = null;
		final List<Location> locationList = this.manager.getLocationsByNameCountryAndType(locationName, country, locationTypeId);
		Assert.assertFalse("Location list should not be empty", locationList.isEmpty());
		for (final Location location : locationList) {
			Assert.assertTrue("Location should have a location name having the keyword " + locationName,
				location.getLname().toLowerCase().contains(locationName.toLowerCase()));
		}
	}

	@Test
	public void testGetLocdesByLocId() throws MiddlewareQueryException {
		final Integer locationId = 700000019;
		final List<Locdes> locdesList = this.manager.getLocdesByLocId(locationId);
		Assert.assertNotNull(locdesList);
		for (final Locdes locdes : locdesList) {
			Assert.assertEquals(locationId, locdes.getLocationId());
		}
	}

	@Test
	public void testSaveOrUpdateLocdesList() throws MiddlewareQueryException {
		final Integer locationId = 700000019;
		final List<Locdes> existingLocdesList = this.manager.getLocdesByLocId(locationId);
		final int rowsInPlotTypeId = 308;
		final Map<Integer, String> ldidToRowsInPlotMap = new HashMap<>();
		if (existingLocdesList != null && !existingLocdesList.isEmpty()) {
			// update rows in plot to 5
			for (final Locdes locdes : existingLocdesList) {
				if (locdes.getTypeId() == rowsInPlotTypeId) {
					ldidToRowsInPlotMap.put(locdes.getLdid(), locdes.getDval());
					locdes.setDval("5");
				}
			}
			this.manager.saveOrUpdateLocdesList(locationId, existingLocdesList);
			final List<Locdes> newLocdesList = this.manager.getLocdesByLocId(locationId);
			Assert.assertEquals(existingLocdesList.size(), newLocdesList.size());
			for (final Locdes locdes : newLocdesList) {
				if (locdes.getTypeId() == rowsInPlotTypeId) {
					Assert.assertEquals("5", locdes.getDval());
					locdes.setDval(ldidToRowsInPlotMap.get(locdes.getLdid()));
				}
			}
			// revert to previous rows in plot
			this.manager.saveOrUpdateLocdesList(locationId, newLocdesList);
			final List<Locdes> revertedLocdesList = this.manager.getLocdesByLocId(locationId);
			for (final Locdes locdes : revertedLocdesList) {
				if (locdes.getTypeId() == rowsInPlotTypeId) {
					Assert.assertEquals(ldidToRowsInPlotMap.get(locdes.getLdid()), locdes.getDval());
				}
			}
		}
	}

	@Test
	public void testgetUserDefinedFieldIdOfName() throws MiddlewareQueryException {
		final Integer value =
			this.manager.getUserDefinedFieldIdOfName(org.generationcp.middleware.pojos.UDTableType.LOCATION_LTYPE, "Country");
		final Integer countryId = 405;
		Assert.assertEquals("Expected recovered id of the Contry", countryId, value);
	}

	@Test
	public void testRetrieveLocIdOfUnspecifiedLocation() {
		final String unspecifiedLocationID = this.manager.retrieveLocIdOfUnspecifiedLocation();
		final String unspecifiedLocationName = "Unspecified Location";
		final String unspecifiedLocationAbbr = "NOLOC";

		Assert.assertNotNull(unspecifiedLocationID);
		final Location unspecifiedLocation = this.manager.getLocationByID(Integer.parseInt(unspecifiedLocationID));
		Assert.assertEquals(unspecifiedLocationName, unspecifiedLocation.getLname());
		Assert.assertEquals(unspecifiedLocationAbbr, unspecifiedLocation.getLabbr());
	}

}
