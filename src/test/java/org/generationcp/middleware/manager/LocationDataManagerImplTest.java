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

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.dao.CountryDAO;
import org.generationcp.middleware.domain.fieldbook.FieldmapBlockInfo;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.utils.test.Debug;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LocationDataManagerImplTest extends IntegrationTestBase {

	@Autowired
	private LocationDataManager manager;

	@Autowired
	private WorkbenchTestDataUtil workbenchTestDataUtil;

	private Project commonTestProject;
	private CountryDAO countryDAO;

	@Before
	public void setUp() throws Exception {

		this.workbenchTestDataUtil.setUpWorkbench();

		if (this.commonTestProject == null) {
			this.commonTestProject = this.workbenchTestDataUtil.getCommonTestProject();
		}

		this.countryDAO = new CountryDAO();
		this.countryDAO.setSession(this.sessionProvder.getSession());
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
	public void testHandlingOfNullOrEmptyLocationIdsInGetLocationsByIDs() {

		final List<Location> locationsByIdEmptyTest = this.manager.getLocationsByIDs(Collections.emptyList());
		Assert.assertTrue(
			"Returned result must not be null and must be an empty list",
			locationsByIdEmptyTest != null && locationsByIdEmptyTest.isEmpty());

		final List<Location> locationsByIdNullTest = this.manager.getLocationsByIDs(null);
		Assert.assertTrue(
			"Returned result must not be null and must be an empty list",
			locationsByIdNullTest != null && locationsByIdNullTest.isEmpty());

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

		final List<Location> locationList = this.manager.getLocationsByName(name, Operation.EQUAL);
		Assert.assertNotNull("Expecting the location list returned is not null.", locationList);

		final List<Location> locationList2 = this.manager.getLocationsByName(name, start, numOfRows, Operation.EQUAL);
		Assert.assertNotNull("Expecting the location list 2 returned is not null.", locationList2);
	}

	@Test
	public void testGetUserDefinedFieldByFieldTable() throws MiddlewareQueryException {
		final String tableName = "LOCATION";
		final String fieldType = "LTYPE";
		final List<UserDefinedField> userDefinedField = this.manager.getUserDefinedFieldByFieldTableNameAndType(tableName, fieldType);
		Debug.println(
			IntegrationTestBase.INDENT,
			"testGetUserDefineFieldByTableNameAndType(type=" + tableName + "): " + userDefinedField.size());
		Debug.printObjects(IntegrationTestBase.INDENT, userDefinedField);
	}

	@Test
	public void testGetAllBreedingLocations() throws MiddlewareQueryException {
		final List<Location> locations = this.manager.getAllBreedingLocations();
		Debug.println(IntegrationTestBase.INDENT, "getAllBreedingLocations()  " + locations);
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

			final Location location = this.manager.getLocationByID(aculcoLoc.getLocid());

			Assert.assertEquals(aculcoLoc.getGeoref().getLat(), location.getLatitude());
			Assert.assertEquals(aculcoLoc.getGeoref().getLon(), location.getLongitude());
			Assert.assertEquals(aculcoLoc.getGeoref().getAlt(), location.getAltitude());
		}
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
