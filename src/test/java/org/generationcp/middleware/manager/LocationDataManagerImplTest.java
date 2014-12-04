/*******************************************************************************
 * Copyright (c) 2014, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/

package org.generationcp.middleware.manager;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.DataManagerIntegrationTest;
import org.generationcp.middleware.domain.fieldbook.FieldmapBlockInfo;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.LocationDetails;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.utils.test.Debug;
import org.junit.BeforeClass;
import org.junit.Test;

public class LocationDataManagerImplTest extends DataManagerIntegrationTest {

    private static LocationDataManager manager;

    @BeforeClass
    public static void setUp() throws Exception {
        manager = managerFactory.getLocationDataManager();
    }
    
    @Test
    public void testGetAllLocations() throws Exception {
        List<Location> locationList = manager.getAllLocations();
        assertTrue(locationList != null);
        Debug.println(INDENT, "testGetAllLocations(): ");
        Debug.printObjects(INDENT, locationList);
    }

    @Test
    public void testGetLocationsByIDs() throws Exception {

        // Attempt to get all locations so we can proceed
        List<Location> locationList = manager.getAllLocations();
        assertTrue(locationList != null);
        assertTrue("we cannot proceed test if size < 0",locationList.size()>0);

        List<Integer> ids = new ArrayList<Integer>();

        for (Location ls : locationList) {
            ids.add(ls.getLocid());

            // only get subset of locations
            if (ids.size() < 5) {
                break;
            }
        }

        List<Location> results = manager.getLocationsByIDs(ids);
        assertTrue(results != null);
        assertTrue(results.size() < 5);

        Debug.println(INDENT, "testGetLocationsByIDs(): ");
        Debug.printObjects(INDENT, results);
    }

    @Test
    public void testGetLocationDetailsByIDs() throws Exception {

        // Attempt to get all locations so we can proceed
        List<Location> locationList = manager.getAllLocations();
        assertTrue(locationList != null);
        assertTrue("we cannot proceed test if size < 0",locationList.size()>0);

        List<Integer> ids = new ArrayList<Integer>();

        for (Location ls : locationList) {
            ids.add(ls.getLocid());

            // only get subset of locations
            if (ids.size() < 5) {
                break;
            }
        }

        List<LocationDetails> results = manager.getLocationDetailsByLocationIDs(ids);
        assertTrue(results != null);
        assertTrue(results.size() < 5);

        Debug.println(INDENT, "testGetLocationDetailsByIDs(): ");
        Debug.printObjects(INDENT, results);
    }

    @Test
    public void testGetAllCountry() throws Exception {
        List<Country> countryList = manager.getAllCountry();
        assertTrue(countryList != null);
        Debug.printObjects(INDENT, countryList);
    }
    
    @Test
    public void testLocationDetails() throws Exception {
        List<LocationDetails> locationList = manager.getLocationDetailsByLocId(1, 1, 1);
        assertTrue(locationList != null);
        Debug.printObjects(INDENT, locationList);
    }

    @Test
    public void testCountAllLocations() throws Exception {
        long count = manager.countAllLocations();
        Debug.println(INDENT, "testCountAllLocations(): " + count);
    }

    @Test
    public void testGetLocationsByName() throws Exception {
        String name = "AFGHANISTAN";
        int start = 0;
        int numOfRows = 5;

        List<Location> locationList = manager.getLocationsByName(name, Operation.EQUAL);
        assertTrue(locationList != null);
        Debug.println(INDENT, "testGetLocationsByName(" + name + "): " + locationList.size());
        Debug.printObjects(INDENT, locationList);
        
        List<Location> locationList2 = manager.getLocationsByName(name, start, numOfRows, Operation.EQUAL);
        Debug.println(INDENT, "testGetLocationsByName(" + name + ", start=" + start 
                + ", numOfRows=" + numOfRows + "): ");
        Debug.printObjects(INDENT, locationList2);
    }

    @Test
    public void testCountLocationsByName() throws Exception {
        String name = "AFGHANISTAN";
        long count = manager.countLocationsByName(name, Operation.EQUAL);
        Debug.println(INDENT, "testCountLocationByName(" + name + "): " + count);
    }

    @Test
    public void testGetLocationsByCountry() throws MiddlewareQueryException {
        Integer id = 171; // Tested in rice db. 171 = Philippines
        Country country = manager.getCountryById(id);
        int start = 0;
        int numOfRows = 5;

        List<Location> locations = manager.getLocationsByCountry(country);
        Debug.println(INDENT, "testGetLocationByCountry(country=" + country + "): " + locations.size());
        Debug.printObjects(INDENT, locations);

        List<Location> locationList = manager.getLocationsByCountry(country, start, numOfRows);
        Debug.println(INDENT, "testGetLocationByCountry(country=" + country + ", start=" + start 
                + ", numOfRows=" + numOfRows + "): "
                + locationList.size());
        Debug.printObjects(INDENT, locationList);
    }
    
    @Test
    public void testGetLocationsByCountryAndType() throws MiddlewareQueryException {
        Integer id = 1; // Tested in rice db. 171 = Philippines
        Country country = manager.getCountryById(id);
        int type=405;

        List<Location> locations = manager.getLocationsByCountryAndType(country, type);
        Debug.println(INDENT, "testGetLocationByCountryAndType(country=" + country 
                + "): type= " + type + ":" + locations.size());
        Debug.printObjects(INDENT, locations);
    }


    @Test
    public void testCountLocationsByCountry() throws Exception {
        Integer id = 171; // Tested in rice db. 171 = Philippines
        Country country = manager.getCountryById(id);
        long count = manager.countLocationsByCountry(country);
        Debug.println(INDENT, "testCountLocationByCountry(country=" + country + "): " + count);
    }

    @Test
    public void testGetLocationsByType() throws MiddlewareQueryException {
        Integer type = 405; // Tested in rice db
        int start = 0;
        int numOfRows = 5;

        List<Location> locations = manager.getLocationsByType(type);
        Debug.println(INDENT, "testGetLocationByType(type=" + type + "): " + locations.size());
        Debug.printObjects(INDENT, locations);

        List<Location> locationList = manager.getLocationsByType(type, start, numOfRows);
        Debug.println(INDENT, "testGetLocationByType(type=" + type + ", start=" + start 
                + ", numOfRows=" + numOfRows + "): "
                + locationList.size());
        Debug.printObjects(INDENT, locations);
    }

    @Test
    public void testCountLocationsByType() throws Exception {
        Integer type = 405; // Tested in rice db
        long count = manager.countLocationsByType(type);
        Debug.println(INDENT, "testCountLocationByType(type=" + type + "): " + count);
    }

    @Test
    public void testAddLocation() throws MiddlewareQueryException {
        Location location = new Location();
        location.setLocid(-1);
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
        Integer id = manager.addLocation(location);
        Debug.println(INDENT, "testAddLocation(" + location + "): " + id + "  \n  " 
                + manager.getLocationsByName("TEST-LOCATION-1", 0, 5, Operation.EQUAL));

        // cleanup
        manager.deleteLocation(manager.getLocationsByName("TEST-LOCATION-1", 0, 5, 
                Operation.EQUAL).get(0));
    }

    @Test
    public void testAddLocations() throws MiddlewareQueryException {

        List<Location> locations = new ArrayList<Location>();

        Location location1 = new Location();
        location1.setLocid(-2);
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
        location2.setLocid(-3);
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
        List<Integer> locationsAdded = manager.addLocation(locations);

        Debug.println(INDENT, "testAddLocations() Locations added: " + locationsAdded.size());
        Debug.println(INDENT, manager.getLocationsByName("TEST-LOCATION-2", 0, 5, Operation.EQUAL));
        Debug.println(INDENT, manager.getLocationsByName("TEST-LOCATION-3", 0, 5, Operation.EQUAL));

        // cleanup
        manager.deleteLocation(manager.getLocationsByName("TEST-LOCATION-2", 0, 5, Operation.EQUAL).get(0));
        manager.deleteLocation(manager.getLocationsByName("TEST-LOCATION-3", 0, 5, Operation.EQUAL).get(0));
    }
    @Test
    public void testGetUserDefinedFieldByFieldTable() throws MiddlewareQueryException {
        String tableName="LOCATION";
        String fieldType="LTYPE";
        List<UserDefinedField> userDefinedField = manager
                .getUserDefinedFieldByFieldTableNameAndType(tableName, fieldType);
        Debug.println(INDENT, "testGetUserDefineFieldByTableNameAndType(type=" + tableName 
                + "): " + userDefinedField.size());
        Debug.printObjects(INDENT, userDefinedField);
    }
    
    @Test
    public void testGetAllBreedingLocations() throws MiddlewareQueryException {
        List<Location> locations = manager.getAllBreedingLocations();
        Debug.println(INDENT, "getAllBreedingLocations()  " + locations);
    }    
    
    @Test
    public void testCountAllBreedingLocations() throws MiddlewareQueryException {
        Long locationCount = (Long) manager.countAllBreedingLocations();
        Debug.println(INDENT, "countAllBreedingLocations() - Total Count = " + locationCount);
    }        
   
    @Test
    public void testGetCountryById() throws Exception {
        Integer id = Integer.valueOf(1);
        Country countries = manager.getCountryById(id);
        assertNotNull(countries);
        Debug.println(INDENT, "testGetCountryById("+id+"): ");
        Debug.println(INDENT, countries);
    }

    @Test
    public void testGetLocationByID() throws Exception {
        Integer id = Integer.valueOf(1);
        Location locid = manager.getLocationByID(id);
        assertNotNull(locid);
        Debug.println(INDENT, "testGetLocationByID("+id+"): ");
        Debug.println(INDENT, locid);
    }
    
    @Test
    public void testGetLocationDetailsByLocId() throws Exception {
        Integer locationId = Integer.valueOf(2);
        List<LocationDetails> locdetails = manager.getLocationDetailsByLocId(locationId, 0, 100);
        assertNotNull(locdetails);
        assertTrue(!locdetails.isEmpty());
        Debug.println(INDENT, "testGetLocationDetailsByLocId("+locationId+"): ");
        Debug.printObjects(INDENT, locdetails);
    }
    
    @Test
    public void testGetUserDefinedFieldByID() throws Exception {
        Integer id = Integer.valueOf(2);
        UserDefinedField result = manager.getUserDefinedFieldByID(id);
        assertNotNull(result);
        Debug.println(3, result);
    }

    @Test
    public void testGetAllFieldLocations() throws Exception {
        Integer locationId = 17649; //TODO replace later with get field by id OR get first BREED ltype from location
        List<Location> result = manager.getAllFieldLocations(locationId);
        assertNotNull(result);
        Debug.printObjects(3, result);
    }

    @Test
    public void testGetAllBlockLocations() throws Exception {
        Integer fieldId = -11; //TODO replace later with get field by id OR get first field from location
        List<Location> result = manager.getAllBlockLocations(fieldId);
        assertNotNull(result);
        Debug.printObjects(3, result);
    }

    @Test
    public void testGetBlockInformation() throws Exception {
        Integer blockId = -15;
        FieldmapBlockInfo result = manager.getBlockInformation(blockId);
        assertNotNull(result);
        Debug.println(3, result);
    }

    @Test
    public void testGetAllFields() throws Exception {
        List<Location> result = manager.getAllFields();
        assertNotNull(result);
        Debug.printObjects(3, result);
    }

    @Test
    public void testGetProvincesByCountry() throws Exception {
        List<Location> provinces = manager.getAllProvincesByCountry(101);

        assertNotNull(provinces);
        assertTrue(provinces.size() > 0);
        Debug.printObjects(3, provinces);
    }

    @Test
    public void testGeorefIntegration() throws Exception {
        // retrieve all breeding location
        List<Location> breedingLocations = manager.getAllBreedingLocations();

        assertTrue(breedingLocations.size() > 0);   // must have a data

        Location aculcoLoc = manager.getLocationByID(25340);    // this location exists on rice central

        // aculco is in wheat
        if (aculcoLoc != null) {

            // aculcoLoc has a georef
            assertNotNull(aculcoLoc.getGeoref());

            // check equivalency
            assertTrue(aculcoLoc.getGeoref().getLat() == 27.5);
            assertTrue(aculcoLoc.getGeoref().getLon() == 90.5);
            assertTrue(aculcoLoc.getGeoref().getAlt() == 2080);

            // test LocationDAO.getLocationDetails()
            List<LocationDetails> locationDetails = manager.getLocationDetailsByLocId(aculcoLoc.getLocid(),0,Integer.MAX_VALUE);

            locationDetails.get(0).getLongitude();
            locationDetails.get(0).getLatitude();
            locationDetails.get(0).getAltitude();

            assertTrue(aculcoLoc.getGeoref().getLat().equals(locationDetails.get(0).getLatitude()) );
            assertTrue(aculcoLoc.getGeoref().getLon().equals(locationDetails.get(0).getLongitude()) );
            assertTrue(aculcoLoc.getGeoref().getAlt().equals(locationDetails.get(0).getAltitude()) );
        }

    }

}
