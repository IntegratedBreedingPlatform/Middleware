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

package org.generationcp.middleware.manager.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.fieldbook.FieldmapBlockInfo;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.LocationDetails;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.util.Debug;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class TestLocationDataManagerImpl{

    private static ManagerFactory factory;
    private static LocationDataManager manager;

    private long startTime;

    @Rule
    public TestName name = new TestName();

    @BeforeClass
    public static void setUp() throws Exception {
        DatabaseConnectionParameters local = 
                new DatabaseConnectionParameters("testDatabaseConfig.properties", "local");
        DatabaseConnectionParameters central = 
                new DatabaseConnectionParameters("testDatabaseConfig.properties", "central");
        factory = new ManagerFactory(local, central);
        manager = factory.getLocationDataManager();
    }

    @Before
    public void beforeEachTest() {
        Debug.println(0, "#####" + name.getMethodName() + " Start: ");
        startTime = System.nanoTime();
    }
    
    @After
    public void afterEachTest() {
        long elapsedTime = System.nanoTime() - startTime;
        Debug.println(0, "#####" + name.getMethodName() + ": Elapsed Time = " 
                + elapsedTime + " ns = " + ((double) elapsedTime/1000000000) + " s");
    }
    
    @Test
    public void testGetAllLocationsWithStartNumRows() throws Exception {
        List<Location> locationList = manager.getAllLocations(5, 10);
        assertTrue(locationList != null);
        Debug.println(0, "testGetAllLocations(5,10) RESULTS: ");
        for (Location l : locationList) {
            Debug.println(0, "  " + l);
        }
    }
    

    @Test
    public void testGetAllLocations() throws Exception {
        List<Location> locationList = manager.getAllLocations();
        assertTrue(locationList != null);
        Debug.println(0, "testGetAllLocations() RESULTS: ");
        for (Location l : locationList) {
            Debug.println(0, "  " + l);
        }
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
            if (ids.size() < 5)
                break;
        }

        List<Location> results = manager.getLocationsByIDs(ids);
        assertTrue(results != null);
        assertTrue(results.size() < 5);

        Debug.println(0, "testGetLocationsByIDs() RESULTS: ");
        for (Location l : results) {
            Debug.println(0, "  " + l);
        }

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
            if (ids.size() < 5)
                break;
        }

        List<LocationDetails> results = manager.getLocationDetailsByLocationIDs(ids);
        assertTrue(results != null);
        assertTrue(results.size() < 5);

        Debug.println(0, "testGetLocationDetailsByIDs() RESULTS: ");
        for (LocationDetails l : results) {
            Debug.println(0, "  " + l);
        }

    }

    @Test
    public void testGetAllCountry() throws Exception {
        List<Country> countryList = manager.getAllCountry();
        assertTrue(countryList != null);
        for (Country c : countryList) {
            Debug.println(0, "  " + c);
        }
    }
    
    @Test
    public void testLocationDetails() throws Exception {
        List<LocationDetails> locationList = manager.getLocationDetailsByLocId(1, 1, 1);
        assertTrue(locationList != null);
        for (LocationDetails c : locationList) {
            Debug.println(0, "  " + c);
        }
    }

    @Test
    public void testCountAllLocations() throws Exception {
        long count = manager.countAllLocations();
        Debug.println(0, "testCountAllLocations(): " + count);
    }

    @Test
    public void testGetLocationsByName() throws Exception {
        String name = "AFGHANISTAN";
        int start = 0;
        int numOfRows = 5;

        List<Location> locationList = manager.getLocationsByName(name, Operation.EQUAL);
        assertTrue(locationList != null);
        Debug.println(0, "testGetLocationsByName(" + name + ") RESULTS: " + locationList.size());
        for (Location l : locationList) {
            Debug.println(0, "  " + l);
        }
        
        List<Location> locationList2 = manager.getLocationsByName(name, start, numOfRows, Operation.EQUAL);
        Debug.println(0, "testGetLocationsByName(" + name + ", start=" + start 
                + ", numOfRows=" + numOfRows + ") RESULTS: ");
        for (Location l : locationList2) {
            Debug.println(0, "  " + l);
        }
    }

    @Test
    public void testCountLocationsByName() throws Exception {
        String name = "AFGHANISTAN";
        long count = manager.countLocationsByName(name, Operation.EQUAL);
        Debug.println(0, "testCountLocationByName(" + name + "): " + count);
    }

    @Test
    public void testGetLocationsByCountry() throws MiddlewareQueryException {
        Integer id = 171; // Tested in rice db. 171 = Philippines
        Country country = manager.getCountryById(id);
        int start = 0;
        int numOfRows = 5;

        List<Location> locations = manager.getLocationsByCountry(country);
        Debug.println(0, "testGetLocationByCountry(country=" + country + "): " + locations.size());
        for (Location location : locations) {
            Debug.println(0, "  " + location);
        }

        List<Location> locationList = manager.getLocationsByCountry(country, start, numOfRows);
        Debug.println(0, "testGetLocationByCountry(country=" + country + ", start=" + start 
                + ", numOfRows=" + numOfRows + "): "
                + locationList.size());
        for (Location location : locationList) {
            Debug.println(0, "  " + location);
        }
    }
    
    @Test
    public void testGetLocationsByCountryAndType() throws MiddlewareQueryException {
        Integer id = 1; // Tested in rice db. 171 = Philippines
        Country country = manager.getCountryById(id);
        int type=405;

        List<Location> locations = manager.getLocationsByCountryAndType(country, type);
        Debug.println(0, "testGetLocationByCountryAndType(country=" + country 
                + "): type= " + type + ":" + locations.size());
        for (Location location : locations) {
            Debug.println(0, "  " + location);
        }
    }


    @Test
    public void testCountLocationsByCountry() throws Exception {
        Integer id = 171; // Tested in rice db. 171 = Philippines
        Country country = manager.getCountryById(id);
        long count = manager.countLocationsByCountry(country);
        Debug.println(0, "testCountLocationByCountry(country=" + country + "): " + count);
    }

    @Test
    public void testGetLocationsByType() throws MiddlewareQueryException {
        Integer type = 405; // Tested in rice db
        int start = 0;
        int numOfRows = 5;

        List<Location> locations = manager.getLocationsByType(type);
        Debug.println(0, "testGetLocationByType(type=" + type + "): " + locations.size());
        for (Location location : locations) {
            Debug.println(0, "  " + location);
        }

        List<Location> locationList = manager.getLocationsByType(type, start, numOfRows);
        Debug.println(0, "testGetLocationByType(type=" + type + ", start=" + start 
                + ", numOfRows=" + numOfRows + "): "
                + locationList.size());
        for (Location location : locationList) {
            Debug.println(0, "  " + location);
        }
    }

    @Test
    public void testCountLocationsByType() throws Exception {
        Integer type = 405; // Tested in rice db
        long count = manager.countLocationsByType(type);
        Debug.println(0, "testCountLocationByType(type=" + type + "): " + count);
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
        Debug.println(0, "testAddLocation(" + location + ") RESULTS: " + id + "  \n  " 
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

        Debug.println(0, "testAddLocations() Locations added: " + locationsAdded.size());
        Debug.println(0, "  " + manager.getLocationsByName("TEST-LOCATION-2", 0, 5, Operation.EQUAL));
        Debug.println(0, "  " + manager.getLocationsByName("TEST-LOCATION-3", 0, 5, Operation.EQUAL));

        // cleanup
        manager.deleteLocation(manager.getLocationsByName("TEST-LOCATION-2", 0, 5, Operation.EQUAL).get(0));
        manager.deleteLocation(manager.getLocationsByName("TEST-LOCATION-3", 0, 5, Operation.EQUAL).get(0));
    }
    @Test
    public void testGetUserDefinedFieldByFieldTable() throws MiddlewareQueryException {
        String tableName="LOCATION";
        String fieldType="LTYPE";
        List<UserDefinedField> userDefineField = manager
                .getUserDefinedFieldByFieldTableNameAndType(tableName, fieldType);
        Debug.println(0, "testGetUserDefineFieldByTableNameAndType(type=" + tableName 
                + "): " + userDefineField.size());
        for (UserDefinedField u : userDefineField) {
            Debug.println(0, "  " + u);
        }
    }
    
    @Test
    public void testGetAllBreedingLocations() throws MiddlewareQueryException {
        List<Location> locations = manager.getAllBreedingLocations();
        Debug.println(0, "getAllBreedingLocations()  " + locations);
    }    
    
    @Test
    public void testCountAllBreedingLocations() throws MiddlewareQueryException {
        Long locationCount = (Long) manager.countAllBreedingLocations();
        Debug.println(0, "countAllBreedingLocations() - Total Count = " + locationCount);
    }        
   
    @Test
    public void testGetNextSequenceNumberForCrossName() throws MiddlewareQueryException{
        String prefix = "PARA7A2";
//      Debug.println(0, "Next number in sequence for prefix (" + prefix + "): " + 
//              manager.getNextSequenceNumberForCrossName(prefix));
        Debug.println(0, prefix.substring(prefix.length()-1) + " " 
                    + prefix.substring(prefix.length()-1).matches("\\d"));
            
    }
    
    @Test
    public void testGetCountryById() throws Exception {
        Integer id = Integer.valueOf(1);
        Country countries = manager.getCountryById(id);
        assertNotNull(countries);
        Debug.println(0, "testGetCountryById("+id+") Results:");
        Debug.println(0, "  " + countries);
    }

    @Test
    public void testGetLocationByID() throws Exception {
        Integer id = Integer.valueOf(1);
        Location locid = manager.getLocationByID(id);
        assertNotNull(locid);
        Debug.println(0, "testGetLocationByID("+id+") Results: ");
        Debug.println(0, "  " + locid);
    }
    
    @Test
    public void testGetLocationDetailsByLocId() throws Exception {
        Integer locationId = Integer.valueOf(2);
        List<LocationDetails> locdetails = manager.getLocationDetailsByLocId(locationId, 0, 100);
        assertNotNull(locdetails);
        assertTrue(!locdetails.isEmpty());
        Debug.println(0, "testGetLocationDetailsByLocId("+locationId+") Results: ");
        for (LocationDetails locdetail : locdetails) {
            Debug.println(0, "  " + locdetail);
        }
        Debug.println(0, "Number of record/s: " + locdetails.size() );
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
        Integer blockId = 1;
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
    @AfterClass
    public static void tearDown() throws Exception {
        factory.close();
    }
    
}
