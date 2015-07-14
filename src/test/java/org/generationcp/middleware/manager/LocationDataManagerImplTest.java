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

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.DataManagerIntegrationTest;
import org.generationcp.middleware.MiddlewareIntegrationTest;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.domain.fieldbook.FieldmapBlockInfo;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.LocationDetails;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.utils.test.Debug;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class LocationDataManagerImplTest extends DataManagerIntegrationTest {

	private static LocationDataManager manager;
	private static Project commonTestProject;
	private static WorkbenchTestDataUtil workbenchTestDataUtil;
	private final static Integer COUNTRY_LTYPEID = 405;
	private final static Integer PHILIPPINES_CNTRYID = 171;

	@BeforeClass
	public static void setUp() throws Exception {
		LocationDataManagerImplTest.manager = DataManagerIntegrationTest.managerFactory.getLocationDataManager();
		LocationDataManagerImplTest.workbenchTestDataUtil = WorkbenchTestDataUtil.getInstance();
		LocationDataManagerImplTest.workbenchTestDataUtil.setUpWorkbench();
		LocationDataManagerImplTest.commonTestProject = LocationDataManagerImplTest.workbenchTestDataUtil.getCommonTestProject();
	}

	@Test
	public void testGetAllLocations() throws Exception {
		List<Location> locationList = LocationDataManagerImplTest.manager.getAllLocations();
		Assert.assertTrue(locationList != null);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetAllLocations(): ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, locationList);
	}

	@Test
	public void testGetLocationsByUniqueID() throws Exception {
		String programUUID = "030850c4-41f8-4baf-81a3-03b99669e996";
		List<Location> locationList = LocationDataManagerImplTest.manager.getLocationsByUniqueID(programUUID);
		Assert.assertNotNull("Expecting to have returned results.", locationList);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetLocationsByUniqueID(): ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, locationList);
	}

	@Test
	public void testCountLocationsByUniqueID() throws Exception {
		String programUUID = "030850c4-41f8-4baf-81a3-03b99669e996";
		long count = LocationDataManagerImplTest.manager.countLocationsByUniqueID(programUUID);
		Assert.assertTrue("Expecting to have returned results.", count > 0);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testCountLocationsByUniqueID(" + programUUID + "): " + count);
	}

	@Test
	public void testGetLocationsByIDs() throws Exception {

		// Attempt to get all locations so we can proceed
		List<Location> locationList = LocationDataManagerImplTest.manager.getAllLocations();
		Assert.assertTrue(locationList != null);
		Assert.assertTrue("we cannot proceed test if size < 0", locationList.size() > 0);

		List<Integer> ids = new ArrayList<Integer>();

		for (Location ls : locationList) {
			ids.add(ls.getLocid());

			// only get subset of locations
			if (ids.size() < 5) {
				break;
			}
		}

		List<Location> results = LocationDataManagerImplTest.manager.getLocationsByIDs(ids);
		Assert.assertTrue(results != null);
		Assert.assertTrue(results.size() < 5);

		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetLocationsByIDs(): ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, results);
	}

	@Test
	public void testGetLocationDetailsByIDs() throws Exception {

		// Attempt to get all locations so we can proceed
		List<Location> locationList = LocationDataManagerImplTest.manager.getAllLocations();
		Assert.assertTrue(locationList != null);
		Assert.assertTrue("we cannot proceed test if size < 0", locationList.size() > 0);

		List<Integer> ids = new ArrayList<Integer>();

		for (Location ls : locationList) {
			ids.add(ls.getLocid());

			// only get subset of locations
			if (ids.size() < 5) {
				break;
			}
		}

		List<LocationDetails> results = LocationDataManagerImplTest.manager.getLocationDetailsByLocationIDs(ids);
		Assert.assertTrue(results != null);
		Assert.assertTrue(results.size() < 5);

		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetLocationDetailsByIDs(): ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, results);
	}

	@Test
	public void testGetAllCountry() throws Exception {
		List<Country> countryList = LocationDataManagerImplTest.manager.getAllCountry();
		Assert.assertTrue(countryList != null);
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, countryList);
	}

	@Test
	public void testLocationDetails() throws Exception {
		List<LocationDetails> locationList = LocationDataManagerImplTest.manager.getLocationDetailsByLocId(1, 1, 1);
		Assert.assertTrue(locationList != null);
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, locationList);
	}

	@Test
	public void testCountAllLocations() throws Exception {
		long count = LocationDataManagerImplTest.manager.countAllLocations();
		Debug.println(MiddlewareIntegrationTest.INDENT, "testCountAllLocations(): " + count);
	}

	@Test
	public void testGetLocationsByName() throws Exception {
		String name = "AFGHANISTAN";
		int start = 0;
		int numOfRows = 5;

		List<Location> locationList = LocationDataManagerImplTest.manager.getLocationsByName(name, Operation.EQUAL);
		Assert.assertTrue(locationList != null);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetLocationsByName(" + name + "): " + locationList.size());
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, locationList);

		List<Location> locationList2 = LocationDataManagerImplTest.manager.getLocationsByName(name, start, numOfRows, Operation.EQUAL);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetLocationsByName(" + name + ", start=" + start + ", numOfRows=" + numOfRows
				+ "): ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, locationList2);
	}

	@Test
	public void testGetLocationsByNameWithProgramUUID() throws Exception {
		String name = "AFGHANISTAN";
		int start = 0;
		int numOfRows = 5;
		String programUUID = LocationDataManagerImplTest.commonTestProject.getUniqueID();

		List<Location> locationList = LocationDataManagerImplTest.manager.getLocationsByName(name, Operation.EQUAL, programUUID);
		Assert.assertTrue("Expecting the location list returned is not null.", locationList != null);

		List<Location> locationList2 =
				LocationDataManagerImplTest.manager.getLocationsByName(name, start, numOfRows, Operation.EQUAL, programUUID);
		Assert.assertTrue("Expecting the location list 2 returned is not null.", locationList2 != null);
	}

	@Test
	public void testCountLocationsByName() throws Exception {
		String name = "AFGHANISTAN";
		long count = LocationDataManagerImplTest.manager.countLocationsByName(name, Operation.EQUAL);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testCountLocationByName(" + name + "): " + count);
	}

	@Test
	public void testCountLocationsByNameWithProgramUUID() throws Exception {
		String name = "AFGHANISTAN";
		String programUUID = LocationDataManagerImplTest.commonTestProject.getUniqueID();
		long count = LocationDataManagerImplTest.manager.countLocationsByName(name, Operation.EQUAL, programUUID);
		Assert.assertTrue("Expecting the count returned is greated than 0", count > 0);
	}

	@Test
	public void testGetLocationsByCountry() throws MiddlewareQueryException {
		Integer id = 171; // Tested in rice db. 171 = Philippines
		Country country = LocationDataManagerImplTest.manager.getCountryById(id);
		int start = 0;
		int numOfRows = 5;

		List<Location> locations = LocationDataManagerImplTest.manager.getLocationsByCountry(country);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetLocationByCountry(country=" + country + "): " + locations.size());
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, locations);

		List<Location> locationList = LocationDataManagerImplTest.manager.getLocationsByCountry(country, start, numOfRows);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetLocationByCountry(country=" + country + ", start=" + start + ", numOfRows="
				+ numOfRows + "): " + locationList.size());
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, locationList);
	}

	@Test
	public void testGetLocationsByCountryAndType() throws MiddlewareQueryException {
		Integer id = 1; // Tested in rice db. 171 = Philippines
		Country country = LocationDataManagerImplTest.manager.getCountryById(id);
		int type = 405;

		List<Location> locations = LocationDataManagerImplTest.manager.getLocationsByCountryAndType(country, type);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetLocationByCountryAndType(country=" + country + "): type= " + type + ":"
				+ locations.size());
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, locations);
	}

	@Test
	public void testCountLocationsByCountry() throws Exception {
		Integer id = 171; // Tested in rice db. 171 = Philippines
		Country country = LocationDataManagerImplTest.manager.getCountryById(id);
		long count = LocationDataManagerImplTest.manager.countLocationsByCountry(country);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testCountLocationByCountry(country=" + country + "): " + count);
	}

	@Test
	public void testGetLocationsByType() throws MiddlewareQueryException {
		Integer type = 405; // Tested in rice db
		int start = 0;
		int numOfRows = 5;

		List<Location> locations = LocationDataManagerImplTest.manager.getLocationsByType(type);
		Assert.assertNotNull("Expecting to have returned results.", locations);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetLocationByType(type=" + type + "): " + locations.size());
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, locations);

		String programUUID = "030850c4-41f8-4baf-81a3-03b99669e996";
		List<Location> locationsByProgramUUID = LocationDataManagerImplTest.manager.getLocationsByType(type, programUUID);
		Assert.assertNotNull("Expecting to have returned results.", locationsByProgramUUID);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetLocationByType(type=" + type + "): " + locations.size());
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, locationsByProgramUUID);

		List<Location> locationList = LocationDataManagerImplTest.manager.getLocationsByType(type, start, numOfRows);
		Assert.assertNotNull("Expecting to have returned results.", locationList);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetLocationByType(type=" + type + ", start=" + start + ", numOfRows="
				+ numOfRows + "): " + locationList.size());
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, locations);
	}

	@Test
	public void testCountLocationsByType() throws Exception {
		Integer type = 405; // Tested in rice db
		long count = LocationDataManagerImplTest.manager.countLocationsByType(type);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testCountLocationByType(type=" + type + "): " + count);

		String programUUID = "030850c4-41f8-4baf-81a3-03b99669e996";
		type = 405;
		count = LocationDataManagerImplTest.manager.countLocationsByType(type, programUUID);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testCountLocationByType(type=" + type + "): " + count);
	}

	@Test
	public void testAddLocation() throws MiddlewareQueryException {
		Location location = new Location();
		location.setCntryid(1);
		location.setLabbr("");
		location.setLname("TEST-LOCATION-1");
		location.setLrplce(1);
		location.setLtype(1);
		location.setNllp(1);
		location.setSnl1id(1);
		location.setSnl2id(1);
		location.setSnl3id(1);

		// add the location
		Integer id = LocationDataManagerImplTest.manager.addLocation(location);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testAddLocation(" + location + "): " + id + "  \n  "
				+ LocationDataManagerImplTest.manager.getLocationsByName("TEST-LOCATION-1", 0, 5, Operation.EQUAL));

		// cleanup
		LocationDataManagerImplTest.manager.deleteLocation(LocationDataManagerImplTest.manager.getLocationsByName("TEST-LOCATION-1", 0, 5,
				Operation.EQUAL).get(0));
	}

	@Test
	public void testAddLocations() throws MiddlewareQueryException {

		List<Location> locations = new ArrayList<Location>();

		Location location1 = new Location();
		location1.setCntryid(1);
		location1.setLabbr("");
		location1.setLname("TEST-LOCATION-2");
		location1.setLrplce(1);
		location1.setLtype(1);
		location1.setNllp(1);
		location1.setSnl1id(1);
		location1.setSnl2id(1);
		location1.setSnl3id(1);

		Location location2 = new Location();
		location2.setCntryid(1);
		location2.setLabbr("");
		location2.setLname("TEST-LOCATION-3");
		location2.setLrplce(1);
		location2.setLtype(1);
		location2.setNllp(1);
		location2.setSnl1id(1);
		location2.setSnl2id(1);
		location2.setSnl3id(1);

		locations.add(location1);
		locations.add(location2);

		// add the location
		List<Integer> locationsAdded = LocationDataManagerImplTest.manager.addLocation(locations);

		Debug.println(MiddlewareIntegrationTest.INDENT, "testAddLocations() Locations added: " + locationsAdded.size());
		Debug.println(MiddlewareIntegrationTest.INDENT,
				LocationDataManagerImplTest.manager.getLocationsByName("TEST-LOCATION-2", 0, 5, Operation.EQUAL));
		Debug.println(MiddlewareIntegrationTest.INDENT,
				LocationDataManagerImplTest.manager.getLocationsByName("TEST-LOCATION-3", 0, 5, Operation.EQUAL));

		// cleanup
		LocationDataManagerImplTest.manager.deleteLocation(LocationDataManagerImplTest.manager.getLocationsByName("TEST-LOCATION-2", 0, 5,
				Operation.EQUAL).get(0));
		LocationDataManagerImplTest.manager.deleteLocation(LocationDataManagerImplTest.manager.getLocationsByName("TEST-LOCATION-3", 0, 5,
				Operation.EQUAL).get(0));
	}

	@Test
	public void testGetUserDefinedFieldByFieldTable() throws MiddlewareQueryException {
		String tableName = "LOCATION";
		String fieldType = "LTYPE";
		List<UserDefinedField> userDefinedField =
				LocationDataManagerImplTest.manager.getUserDefinedFieldByFieldTableNameAndType(tableName, fieldType);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetUserDefineFieldByTableNameAndType(type=" + tableName + "): "
				+ userDefinedField.size());
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, userDefinedField);
	}

	@Test
	public void testGetAllBreedingLocations() throws MiddlewareQueryException {
		List<Location> locations = LocationDataManagerImplTest.manager.getAllBreedingLocations();
		Debug.println(MiddlewareIntegrationTest.INDENT, "getAllBreedingLocations()  " + locations);
	}

	@Test
	public void testCountAllBreedingLocations() throws MiddlewareQueryException {
		Long locationCount = LocationDataManagerImplTest.manager.countAllBreedingLocations();
		Debug.println(MiddlewareIntegrationTest.INDENT, "countAllBreedingLocations() - Total Count = " + locationCount);
	}

	@Test
	public void testGetCountryById() throws Exception {
		Integer id = Integer.valueOf(1);
		Country countries = LocationDataManagerImplTest.manager.getCountryById(id);
		Assert.assertNotNull(countries);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetCountryById(" + id + "): ");
		Debug.println(MiddlewareIntegrationTest.INDENT, countries);
	}

	@Test
	public void testGetLocationByID() throws Exception {
		Integer id = Integer.valueOf(1);
		Location locid = LocationDataManagerImplTest.manager.getLocationByID(id);
		Assert.assertNotNull(locid);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetLocationByID(" + id + "): ");
		Debug.println(MiddlewareIntegrationTest.INDENT, locid);
	}

	@Test
	public void testGetLocationDetailsByLocId() throws Exception {
		Integer locationId = Integer.valueOf(2);
		List<LocationDetails> locdetails = LocationDataManagerImplTest.manager.getLocationDetailsByLocId(locationId, 0, 100);
		Assert.assertNotNull(locdetails);
		Assert.assertTrue(!locdetails.isEmpty());
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetLocationDetailsByLocId(" + locationId + "): ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, locdetails);
	}

	@Test
	public void testGetUserDefinedFieldByID() throws Exception {
		Integer id = Integer.valueOf(2);
		UserDefinedField result = LocationDataManagerImplTest.manager.getUserDefinedFieldByID(id);
		Assert.assertNotNull(result);
		Debug.println(3, result);
	}

	@Test
	public void testGetAllFieldLocations() throws Exception {
		Integer locationId = 17649; // TODO replace later with get field by id OR get first BREED ltype from location
		List<Location> result = LocationDataManagerImplTest.manager.getAllFieldLocations(locationId);
		Assert.assertNotNull(result);
		Debug.printObjects(3, result);
	}

	@Test
	public void testGetAllBlockLocations() throws Exception {
		Integer fieldId = -11; // TODO replace later with get field by id OR get first field from location
		List<Location> result = LocationDataManagerImplTest.manager.getAllBlockLocations(fieldId);
		Assert.assertNotNull(result);
		Debug.printObjects(3, result);
	}

	@Test
	public void testGetBlockInformation() throws Exception {
		Integer blockId = -15;
		FieldmapBlockInfo result = LocationDataManagerImplTest.manager.getBlockInformation(blockId);
		Assert.assertNotNull(result);
		Debug.println(3, result);
	}

	@Test
	public void testGetAllFields() throws Exception {
		List<Location> result = LocationDataManagerImplTest.manager.getAllFields();
		Assert.assertNotNull(result);
		Debug.printObjects(3, result);
	}

	@Test
	public void testGetProvincesByCountry() throws Exception {
		List<Location> provinces = LocationDataManagerImplTest.manager.getAllProvincesByCountry(101);

		Assert.assertNotNull(provinces);
		Assert.assertTrue(provinces.size() > 0);
		Debug.printObjects(3, provinces);
	}

	@Test
	public void testGeorefIntegration() throws Exception {
		// retrieve all breeding location
		List<Location> breedingLocations = LocationDataManagerImplTest.manager.getAllBreedingLocations();

		Assert.assertTrue(breedingLocations.size() > 0); // must have a data

		Location aculcoLoc = LocationDataManagerImplTest.manager.getLocationByID(25340); // this location exists on rice central

		// aculco is in wheat
		if (aculcoLoc != null) {

			// aculcoLoc has a georef
			Assert.assertNotNull(aculcoLoc.getGeoref());

			// check equivalency
			Assert.assertTrue(aculcoLoc.getGeoref().getLat() == 27.5);
			Assert.assertTrue(aculcoLoc.getGeoref().getLon() == 90.5);
			Assert.assertTrue(aculcoLoc.getGeoref().getAlt() == 2080);

			// test LocationDAO.getLocationDetails()
			List<LocationDetails> locationDetails =
					LocationDataManagerImplTest.manager.getLocationDetailsByLocId(aculcoLoc.getLocid(), 0, Integer.MAX_VALUE);

			locationDetails.get(0).getLongitude();
			locationDetails.get(0).getLatitude();
			locationDetails.get(0).getAltitude();

			Assert.assertTrue(aculcoLoc.getGeoref().getLat().equals(locationDetails.get(0).getLatitude()));
			Assert.assertTrue(aculcoLoc.getGeoref().getLon().equals(locationDetails.get(0).getLongitude()));
			Assert.assertTrue(aculcoLoc.getGeoref().getAlt().equals(locationDetails.get(0).getAltitude()));
		}

	}

	@Test
	public void getProgramLocationsAndDeleteByUniqueId() {
		// create program locations
		String programUUID = LocationDataManagerImplTest.commonTestProject.getUniqueID();
		int testLocationID1 = 100000;
		int testLocationID2 = 100001;
		Location testLocation1 = this.createLocationTestData(testLocationID1, programUUID);
		Location testLocation2 = this.createLocationTestData(testLocationID2, programUUID);
		try {
			LocationDataManagerImplTest.manager.addLocation(testLocation1);
			LocationDataManagerImplTest.manager.addLocation(testLocation2);
			// verify
			List<Location> locationList = LocationDataManagerImplTest.manager.getProgramLocations(programUUID);
			Assert.assertEquals("There should be 2 program locations with programUUID[" + programUUID + "]", 2, locationList.size());
			// delete locations
			LocationDataManagerImplTest.manager.deleteProgramLocationsByUniqueId(programUUID);
			locationList = LocationDataManagerImplTest.manager.getProgramLocations(programUUID);
			Assert.assertTrue("There should be no program locations with programUUID[" + programUUID + "]", locationList.isEmpty());
		} catch (MiddlewareQueryException e) {
			Assert.fail("Getting of Program Methods Failed ");
		}
	}

	private Location createLocationTestData(int id, String programUUID) {
		Location location = new Location();
		location.setUniqueID(programUUID);
		location.setLrplce(0);
		location.setLname("TEST-LOCATION" + id);
		location.setLabbr("");
		location.setLtype(1);
		location.setCntryid(1);
		location.setLrplce(1);
		location.setLtype(1);
		location.setNllp(0);
		location.setSnl1id(0);
		location.setSnl2id(0);
		location.setSnl3id(0);
		return location;
	}

	@Test
	public void testGetLocationsByNameCountryAndType_NullFilter() throws MiddlewareQueryException {
		String locationName = "";
		Country country = null;
		Integer locationTypeId = null;
		List<Location> locationList =
				LocationDataManagerImplTest.manager.getLocationsByNameCountryAndType(locationName, country, locationTypeId);
		Assert.assertFalse("Location list should not be empty", locationList.isEmpty());
	}

	@Test
	public void testGetLocationsByNameCountryAndType_NullFilterWithZeroLocationTypeId() throws MiddlewareQueryException {
		String locationName = "";
		Country country = null;
		Integer locationTypeId = 0;
		List<Location> locationList =
				LocationDataManagerImplTest.manager.getLocationsByNameCountryAndType(locationName, country, locationTypeId);
		Assert.assertFalse("Location list should not be empty", locationList.isEmpty());
		boolean hasLocationWithNonZeroLtypeId = false;
		for (Location location : locationList) {
			if (location.getLtype() != null && 0 != location.getLtype().intValue()) {
				hasLocationWithNonZeroLtypeId = true;
				break;
			}
		}
		Assert.assertTrue("Location list should contain items with non-zero lytpeId", hasLocationWithNonZeroLtypeId);
	}

	@Test
	public void testGetLocationsByNameCountryAndType_WithNonZeroLocationTypeId() throws MiddlewareQueryException {
		String locationName = "";
		Country country = null;
		Integer locationTypeId = LocationDataManagerImplTest.COUNTRY_LTYPEID;
		List<Location> locationList =
				LocationDataManagerImplTest.manager.getLocationsByNameCountryAndType(locationName, country, locationTypeId);
		Assert.assertFalse("Location list should not be empty", locationList.isEmpty());
		for (Location location : locationList) {
			Assert.assertEquals("Location should have a lytpeId = " + LocationDataManagerImplTest.COUNTRY_LTYPEID,
					LocationDataManagerImplTest.COUNTRY_LTYPEID, location.getLtype());
		}
	}

	@Test
	public void testGetLocationsByNameCountryAndType_WithNonZeroCountryId() throws MiddlewareQueryException {
		String locationName = "";
		Country country = new Country();
		country.setCntryid(LocationDataManagerImplTest.PHILIPPINES_CNTRYID);
		Integer locationTypeId = null;
		List<Location> locationList =
				LocationDataManagerImplTest.manager.getLocationsByNameCountryAndType(locationName, country, locationTypeId);
		Assert.assertFalse("Location list should not be empty", locationList.isEmpty());
		for (Location location : locationList) {
			Assert.assertEquals("Location should have a countryId = " + LocationDataManagerImplTest.PHILIPPINES_CNTRYID,
					LocationDataManagerImplTest.PHILIPPINES_CNTRYID, location.getCntryid());
		}
	}

	@Test
	public void testGetLocationsByNameCountryAndType_WithNonEmptyLocationName() throws MiddlewareQueryException {
		String locationName = "Phil";
		Country country = null;
		Integer locationTypeId = null;
		List<Location> locationList =
				LocationDataManagerImplTest.manager.getLocationsByNameCountryAndType(locationName, country, locationTypeId);
		Assert.assertFalse("Location list should not be empty", locationList.isEmpty());
		for (Location location : locationList) {
			Assert.assertTrue("Location should have a location name having the keyword " + locationName, location.getLname().toLowerCase()
					.contains(locationName.toLowerCase()));
		}
	}

}
