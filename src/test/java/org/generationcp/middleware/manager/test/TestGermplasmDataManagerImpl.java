/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
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
import java.util.Map;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.GermplasmNameType;
import org.generationcp.middleware.manager.GetGermplasmByNameModes;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GidNidElement;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.LocationDetails;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.Bibref;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.util.Debug;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class TestGermplasmDataManagerImpl{

    private static ManagerFactory factory;
    private static GermplasmDataManager manager;

	private long startTime;

	@Rule
	public TestName name = new TestName();

    @BeforeClass
    public static void setUp() throws Exception {
        DatabaseConnectionParameters local = new DatabaseConnectionParameters("testDatabaseConfig.properties", "local");
        DatabaseConnectionParameters central = new DatabaseConnectionParameters("testDatabaseConfig.properties", "central");
        factory = new ManagerFactory(local, central);
        manager = factory.getGermplasmDataManager();
    }

	@Before
	public void beforeEachTest() {
        Debug.println(0, "#####" + name.getMethodName() + " Start: ");
		startTime = System.nanoTime();
	}
	
	@After
	public void afterEachTest() {
		long elapsedTime = System.nanoTime() - startTime;
		Debug.println(0, "#####" + name.getMethodName() + ": Elapsed Time = " + elapsedTime + " ns = " + ((double) elapsedTime/1000000000) + " s");
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
    public void testGetMethodsByIDs() throws Exception {

        // Attempt to get all locations so we can proceed
        List<Method> locationList = manager.getAllMethods();
        assertTrue(locationList != null);
        assertTrue("we cannot proceed test if size < 0",locationList.size()>0);

        List<Integer> ids = new ArrayList<Integer>();

        for (Method ls : locationList) {
            ids.add(ls.getMid());

            // only get subset of locations
            if (ids.size() < 5)
                break;
        }

        List<Method> results = manager.getMethodsByIDs(ids);
        assertTrue(results != null);
        assertTrue(results.size() < 5);

        Debug.println(0, "testGetMethodsByIDs() RESULTS: ");
        for (Method l : results) {
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
        Debug.println(0, "testGetLocationsByName(" + name + ", start=" + start + ", numOfRows=" + numOfRows + ") RESULTS: ");
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
        Debug.println(0, "testGetLocationByCountry(country=" + country + ", start=" + start + ", numOfRows=" + numOfRows + "): "
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
        Debug.println(0, "testGetLocationByCountryAndType(country=" + country + "): type= "+type+ ":"+ locations.size());
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
        Debug.println(0, "testGetLocationByType(type=" + type + ", start=" + start + ", numOfRows=" + numOfRows + "): "
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
    public void testGetGermplasmByName() throws Exception {
        String name = "IR 10";
        
        List<Germplasm> germplasmList = manager.getGermplasmByName(name, 0, 5, GetGermplasmByNameModes.NORMAL, Operation.EQUAL, null, null,
                Database.CENTRAL);
        assertTrue(germplasmList != null);

        Debug.println(0, "testGetGermplasmByName(" + name + ") RESULTS: ");
        for (Germplasm g : germplasmList) {
            Debug.println(0, "  " + g);
        }
    }

    @Test
    public void testGetGermplasmByNameOriginalStandardizedAndNoSpace() throws Exception {
        String name = "IR  65";
        List<Germplasm> germplasmList = manager.getGermplasmByName(name, 0, Long.valueOf(manager.countGermplasmByName(name, Operation.EQUAL)).intValue(), Operation.EQUAL);

        Debug.println(0, "testGetGermplasmByNameOriginalStandardizedAndNoSpace(" + name + ") RESULTS: " + germplasmList.size());
        for (Germplasm g : germplasmList) {
            Debug.println(0, "  " + g);
        }
        
        name = "IR 65%";
        germplasmList = manager.getGermplasmByName(name, 0, Long.valueOf(manager.countGermplasmByName(name, Operation.LIKE)).intValue(), Operation.LIKE);

        Debug.println(0, "testGetGermplasmByNameOriginalStandardizedAndNoSpace(" + name + ") RESULTS: " + germplasmList.size());
        for (Germplasm g : germplasmList) {
            Debug.println(0, "  " + g);
        }
    }

    @Test
    public void testCountGermplasmByName() throws Exception {
        String name = "IR 10";
        
        long count = manager.countGermplasmByName(name, GetGermplasmByNameModes.NORMAL, Operation.EQUAL, null, null, Database.CENTRAL);
        Debug.println(0, "testCountGermplasmByName(" + name + ") RESULTS: " + count);
    }

    @Test
    public void testCountGermplasmByNameOriginalStandardizedAndNoSpace() throws Exception {
        String name = "IR  65";
        long count = manager.countGermplasmByName(name, Operation.EQUAL);
        Debug.println(0, "testCountGermplasmByNameOriginalStandardizedAndNoSpace(" + name + ") RESULTS: " + count);
    }

    @Test
    public void testGetGermplasmByNameUsingLike() throws Exception {
        String name = "IR%";
        
        List<Germplasm> germplasmList = manager.getGermplasmByName(name, 0, 5, GetGermplasmByNameModes.NORMAL, Operation.LIKE, null, null,
                Database.CENTRAL);
        assertTrue(germplasmList != null);

        Debug.println(0, "testGetGermplasmByNameUsingLike(" + name + ") RESULTS: ");
        for (Germplasm g : germplasmList) {
            Debug.println(0, "  " + g);
        }
    }

    @Test
    public void testCountGermplasmByNameUsingLike() throws Exception {
        String name = "IR%";
        
        long count = manager.countGermplasmByName(name, GetGermplasmByNameModes.NORMAL, Operation.LIKE, null, null, Database.CENTRAL);
        Debug.println(0, "testCountGermplasmByNameUsingLike(" + name + ") RESULTS:" + count);
    }

    @Test
    public void testGetGermplasmByNameWithStatus() throws Exception {
        String name = "IR 64";
        List<Germplasm> germplasmList = manager.getGermplasmByName(name, 0, 5, GetGermplasmByNameModes.NORMAL, Operation.EQUAL,
                Integer.valueOf(1), null, Database.CENTRAL);
        assertTrue(germplasmList != null);

        Debug.println(0, "testGetGermplasmByNameWithStatus(" + name + ") RESULTS: ");
        for (Germplasm g : germplasmList) {
            Debug.println(0, "  " + g);
        }
    }

    @Test
    public void testCountGermplasmByNameWithStatus() throws Exception {
        String name = "IR 64";
        long count = manager.countGermplasmByName(name, GetGermplasmByNameModes.NORMAL, Operation.EQUAL, Integer.valueOf(1), null,
                Database.CENTRAL);
        Debug.println(0, "testCountGermplasmByNameWithStatus(" + name + ") RESULTS: " + count);
    }

    @Test
    public void testGetGermplasmByNameWithStatusAndType() throws Exception {
        String name = "IR 64";
        List<Germplasm> germplasmList = manager.getGermplasmByName(name, 0, 5, GetGermplasmByNameModes.NORMAL, Operation.EQUAL,
                Integer.valueOf(1), GermplasmNameType.RELEASE_NAME, Database.CENTRAL);
        assertTrue(germplasmList != null);

        Debug.println(0, "testGetGermplasmByNameWithStatusAndType(" + name + ") RESULTS: ");
        for (Germplasm g : germplasmList) {
            Debug.println(0, "  " + g);
        }
    }

    @Test
    public void testCountGermplasmByNameWithStatusAndType() throws Exception {
        String name = "IR 64";
        long count = manager.countGermplasmByName(name, GetGermplasmByNameModes.NORMAL, Operation.EQUAL, Integer.valueOf(1),
                GermplasmNameType.RELEASE_NAME, Database.CENTRAL);
        Debug.println(0, "testCountGermplasmByNameWithStatusAndType(" + name + ") RESULTS: " + count);
    }

    @Test
    public void testGetGermplasmByNameWithStatusUsingLike() throws Exception {
        String name = "IR%";
        List<Germplasm> germplasmList = manager.getGermplasmByName(name, 0, 5, GetGermplasmByNameModes.NORMAL, Operation.LIKE, Integer.valueOf(
                1), null, Database.CENTRAL);
        assertTrue(germplasmList != null);
        Debug.println(0, "testGetGermplasmByNameWithStatusUsingLike(" + name + ") RESULTS: ");
        for (Germplasm g : germplasmList) {
            Debug.println(0, "  " + g);
        }
    }

    @Test
    public void testGetGermplasmByNameWithStatusAndTypeUsingLike() throws Exception {
        String name = "IR%";
        List<Germplasm> germplasmList = manager.getGermplasmByName(name, 0, 5, GetGermplasmByNameModes.NORMAL, Operation.LIKE, 
                                Integer.valueOf(1), GermplasmNameType.RELEASE_NAME, Database.CENTRAL);
        assertTrue(germplasmList != null);

        Debug.println(0, "testGetGermplasmByNameWithStatusAndTypeUsingLike(" + name + ") RESULTS: ");
        for (Germplasm g : germplasmList) {
            Debug.println(0, "  " + g);
        }
    }

    @Test
    public void testGetGermplasmByLocationNameUsingEqual() throws Exception {
        String name = "Philippines";
        List<Germplasm> germplasmList = manager.getGermplasmByLocationName(name, 0, 5, Operation.EQUAL, Database.CENTRAL);
        Debug.println(0, "testGetGermplasmByLocationNameUsingEqual(" + name + ") RESULTS: ");
        for (Germplasm g : germplasmList) {
            Debug.println(0, "  " + g);
        }
    }

    @Test
    public void testCountGermplasmByLocationNameUsingEqual() throws Exception {
        String name = "Philippines";
        long count = manager.countGermplasmByLocationName(name, Operation.EQUAL, Database.CENTRAL);
        Debug.println(0, "testCountGermplasmByLocationNameUsingEqual(" + name + ") RESULTS: " + count);
    }

    @Test
    public void testGetGermplasmByLocationNameUsingLike() throws Exception {
        String name = "International%";
        List<Germplasm> germplasmList = manager.getGermplasmByLocationName(name, 0, 5, Operation.LIKE, Database.CENTRAL);
        assertTrue(germplasmList != null);
        Debug.println(0, "testGetGermplasmByLocationNameUsingLike(" + name + ") RESULTS: ");
        for (Germplasm g : germplasmList) {
            Debug.println(0, "  " + g);
        }
    }

    @Test
    public void testCountGermplasmByLocationNameUsingLike() throws Exception {
        String name = "International%";
        long count = manager.countGermplasmByLocationName(name, Operation.LIKE, Database.CENTRAL);
        Debug.println(0, "testCountGermplasmByLocationNameUsingLike(" + name + ") RESULTS: " + count);
    }

    @Test
    public void testGetGermplasmByMethodNameUsingEqual() throws Exception {
        String name = "SINGLE CROSS";
        
        List<Germplasm> germplasmList = manager.getGermplasmByMethodName(name, 0, 5, Operation.EQUAL, Database.CENTRAL);
        assertTrue(germplasmList != null);

        Debug.println(0, "testGetGermplasmByMethodNameUsingEqual(" + name + ") RESULTS: ");
        for (Germplasm g : germplasmList) {
            Debug.println(0, "  " + g);
        }
    }

    @Test
    public void testCountGermplasmByMethodNameUsingEqual() throws Exception {
        String name = "SINGLE CROSS";
        
        long count = manager.countGermplasmByMethodName(name, Operation.EQUAL, Database.CENTRAL);
        Debug.println(0, "testCountGermplasmByMethodNameUsingEqual(" + name + ") RESULTS: " + count);
    }

    @Test
    public void testGetGermplasmByMethodNameUsingLike() throws Exception {
        String name = "%CROSS%";
        
        List<Germplasm> germplasmList = manager.getGermplasmByMethodName(name, 0, 5, Operation.LIKE, Database.CENTRAL);
        assertTrue(germplasmList != null);

        Debug.println(0, "testGetGermplasmByMethodNameUsingLike(" + name + ") RESULTS: ");
        for (Germplasm g : germplasmList) {
            Debug.println(0, "  " + g);
        }
    }

    @Test
    public void testCountGermplasmByMethodNameUsingLike() throws Exception {
        String name = "%CROSS%";
        
        long count = manager.countGermplasmByMethodName(name, Operation.LIKE, Database.CENTRAL);
        Debug.println(0, "testCountGermplasmByMethodNameUsingLike(" + name + ") RESULTS: " + count);
    }

    @Test
    public void testGetGermplasmByGID() throws Exception {
        Integer gid = Integer.valueOf(50533);
        Germplasm germplasm = manager.getGermplasmByGID(gid);
        Debug.println(0, "testGetGermplasmByGID(" + gid + "): " + germplasm);
    }

    @Test
    public void testGetGermplasmWithPrefName() throws Exception {
        Integer gid = Integer.valueOf(50533);
        Germplasm germplasm = manager.getGermplasmWithPrefName(gid);

        Debug.println(0, "testGetGermplasmWithPrefName(" + gid + ") RESULTS: " + germplasm);
        if (germplasm != null) {
            Debug.println(0, "  preferredName = " + germplasm.getPreferredName());
        }
    }

    @Test
    public void testGetGermplasmWithPrefAbbrev() throws Exception {
        Integer gid = Integer.valueOf(151);
        Germplasm germplasm = manager.getGermplasmWithPrefAbbrev(gid);

        Debug.println(0, "testGetGermplasmWithPrefAbbrev(" + gid + ") RESULTS: " + germplasm);
        Debug.println(0, "  preferredName = " + germplasm.getPreferredName());
        Debug.println(0, "  preferredAbbreviation = " + germplasm.getPreferredAbbreviation());
    }

    @Test
    public void testGetGermplasmNameByID() throws Exception {
        Integer gid = Integer.valueOf(42268);
        Name name = manager.getGermplasmNameByID(gid);
        Debug.println(0, "testGetGermplasmNameByID(" + gid + ") RESULTS: " + name);
    }

    @Test
    public void testGetNamesByGID() throws Exception {
        Integer gid = Integer.valueOf(50533);
        List<Name> names = manager.getNamesByGID(gid, null, null);
        Debug.println(0, "testGetNamesByGID(" + gid + ") RESULTS: " + names);
    }

    @Test
    public void testGetPreferredNameByGID() throws Exception {
        Integer gid = Integer.valueOf(1);
        Debug.println(0, "testGetPreferredNameByGID(" + gid + ") RESULTS: " + manager.getPreferredNameByGID(gid));
    }
    
    @Test
    public void testGetPreferredNameValueByGID() throws Exception {
        Integer gid = Integer.valueOf(1);
        Debug.println(0, "testGetPreferredNameValueByGID(" + gid + ") RESULTS: " + manager.getPreferredNameValueByGID(gid));
    }

    @Test
    public void testGetPreferredAbbrevByGID() throws Exception {
        Integer gid = Integer.valueOf(1);
        Debug.println(0, "testGetPreferredAbbrevByGID(" + gid + ") RESULTS: " + manager.getPreferredAbbrevByGID(gid));
    }
    
    @Test
    public void testGetPreferredIdByGID() throws Exception {
        //tested using Rice DB
        Integer gid = Integer.valueOf(986634);
        Debug.println(0, "testGetPreferredIdByGID(" + gid + ") RESULTS: " + manager.getPreferredIdByGID(gid));       
    }
    
    @Test
    public void testGetPreferredIdsByListId() throws Exception {
        //tested using Rice DB
        Integer listId = Integer.valueOf(2591);
        Debug.println(0, "testGetPreferredIdsByListId(" + listId + ") RESULTS: " + manager.getPreferredIdsByListId(listId));       
    }

    @Test
    public void testGetNameByGIDAndNval() throws Exception {
        Integer gid = Integer.valueOf(225266);
        String nVal = "C 65-44";
        Debug.println(0, "testGetNameByGIDAndNval(" + gid + ", " + nVal + ", GetGermplasmByNameModes.NORMAL) RESULTS: " 
        				+ manager.getNameByGIDAndNval(gid, nVal, GetGermplasmByNameModes.NORMAL));
        Debug.println(0, "testGetNameByGIDAndNval(" + gid + ", " + nVal + ", GetGermplasmByNameModes.SPACES_REMOVED) RESULTS: " 
        				+ manager.getNameByGIDAndNval(gid, nVal, GetGermplasmByNameModes.SPACES_REMOVED));
        Debug.println(0, "testGetNameByGIDAndNval(" + gid + ", " + nVal + ", GetGermplasmByNameModes.STANDARDIZED) RESULTS: " 
        				+ manager.getNameByGIDAndNval(gid, nVal, GetGermplasmByNameModes.STANDARDIZED));
    }

    @Test
    public void testGetNamesByGIDWithStatus() throws Exception {
        Integer gid = Integer.valueOf(50533);
        Integer status = Integer.valueOf(1);
        GermplasmNameType type = null;
        List<Name> names = manager.getNamesByGID(gid, status, type);
        Debug.println(0, "testGetNamesByGIDWithStatus(gid=" + gid + ", status" + status + ", type=" + type + ") RESULTS: " + names);
    }

    @Test
    public void testGetNamesByGIDWithStatusAndType() throws Exception {
        Integer gid = Integer.valueOf(50533);
        Integer status = Integer.valueOf(8);
        GermplasmNameType type = GermplasmNameType.INTERNATIONAL_TESTING_NUMBER;
        List<Name> names = manager.getNamesByGID(gid, status, type);
        System.out
                .println("testGetNamesByGIDWithStatusAndType(gid=" + gid + ", status" + status + ", type=" + type + ") RESULTS: " + names);
    }

    @Test
    public void testGetAttributesByGID() throws Exception {
        Integer gid = Integer.valueOf(50533);
        List<Attribute> attributes = manager.getAttributesByGID(gid);
        Debug.println(0, "testGetAttributesByGID(" + gid + ") RESULTS: " + attributes);
    }

    @Test
    public void testAddMethod() throws MiddlewareQueryException {
        Method method = new Method();
        method.setMid(-1);
        method.setMname("yesno");
        method.setGeneq(0);
        method.setLmid(2);
        method.setMattr(0);
        method.setMcode("UGM");
        method.setMdate(19980610);
        method.setMdesc("yay");
        method.setMfprg(0);
        method.setMgrp("S");
        method.setMprgn(0);
        method.setReference(0);
        method.setUser(0);

        method.setMtype("GEN");

        // add the method
        manager.addMethod(method);

        method = manager.getMethodByID(-1);
        Debug.println(0, "testAddMethod(" + method + ") RESULTS: " + method);

        // delete the method
        manager.deleteMethod(method);
    }

    @Test
    public void testAddMethods() throws MiddlewareQueryException {
        List<Method> methods = new ArrayList<Method>();
        methods.add(new Method(-1, "GEN", "S", "UGM", "yesno", "description 1", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0),
                Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(2), Integer.valueOf(19980610)));
        methods.add(new Method(-2, "GEN", "S", "UGM", "yesno", "description 2", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0),
                Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(2), Integer.valueOf(19980610)));
        methods.add(new Method(-3, "GEN", "S", "UGM", "yesno", "description 3", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0),
                Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(2), Integer.valueOf(19980610)));

        // add the methods
        List<Integer> methodsAdded = manager.addMethod(methods);

        Debug.println(0, "testAddMethods() Methods added: " + methodsAdded.size());

        for (Integer id: methodsAdded ) {
            Method method = manager.getMethodByID(id);
            Debug.println(0, "  " + method);
            // delete the method
            manager.deleteMethod(method);
        }

    }

    @Test
    public void testGetMethodsByType() throws MiddlewareQueryException {
        String type = "GEN"; // Tested with rice and cowpea
        int start = 0;
        int numOfRows = 5;

        List<Method> methods = manager.getMethodsByType(type);
        Debug.println(0, "testGetMethodsByType(type=" + type + "): " + methods.size());
        for (Method method : methods) {
            Debug.println(0, "  " + method);
        }
        List<Method> methodList = manager.getMethodsByType(type, start, numOfRows);
        Debug.println(0, "testGetMethodsByType(type=" + type + ", start=" + start + ", numOfRows=" + numOfRows + "): " + methodList.size());
        for (Method method : methodList) {
            Debug.println(0, "  " + method);
        }

    }

    @Test
    public void testCountMethodsByType() throws Exception {
        String type = "GEN"; // Tested with rice and cowpea
        long count = manager.countMethodsByType(type);
        Debug.println(0, "testCountMethodsByType(type=" + type + "): " + count);
    }

    @Test
    public void testGetMethodsByGroup() throws MiddlewareQueryException {
        String group = "S"; // Tested with rice and cowpea
        int start = 0;
        int numOfRows = 5;

        List<Method> methods = manager.getMethodsByGroup(group);
        Debug.println(0, "testGetMethodsByGroup(group=" + group + "): " + methods.size());
        for (Method method : methods) {
            Debug.println(0, "  " + method);
        }

        List<Method> methodList = manager.getMethodsByGroup(group, start, numOfRows);
        Debug.println(0, "testGetMethodsByGroup(group=" + group + ", start=" + start + ", numOfRows=" + numOfRows + "): " + methodList.size());
        for (Method method : methodList) {
            Debug.println(0, "  " + method);
        }

    }
    
    @Test
    public void testGetMethodsByGroupIncludesGgroup() throws MiddlewareQueryException {
        String group = "O"; // Tested with rice and cowpea

        List<Method> methods = manager.getMethodsByGroupIncludesGgroup(group);
        Debug.println(0, "testGetMethodsByGroup(group=" + group + "): " + methods.size());
        for (Method method : methods) {
            Debug.println(0, "  " + method);
        }


    }
    
    @Test
    public void testGetMethodsByGroupAndType() throws MiddlewareQueryException {
        String group = "O"; // Tested with rice and cowpea
        String type= "GEN"; // Tested with rice and cowpea

        List<Method> methods = manager.getMethodsByGroupAndType(group, type);
        Debug.println(0, "testGetMethodsByGroupAndType(group=" + group +"and "+type + "): " + methods.size());
        for (Method method : methods) {
            Debug.println(0, "  " + method);
        }
    }
    
    @Test
    public void testGetMethodsByGroupAndTypeAndName() throws MiddlewareQueryException {
        String group = "O"; // Tested with rice and cowpea
        String type= "GEN"; // Tested with rice and cowpea
        String name = "ALLO-POLYPLOID CF"; // Tested with rice and cowpea

        List<Method> methods = manager.getMethodsByGroupAndTypeAndName(group, type, name);
        Debug.println(0, "testGetMethodsByGroupAndTypeAndName(group=" + group +" and type="+type + " and name="+name + "): " + methods.size());
        for (Method method : methods) {
            Debug.println(0, "  " + method);
        }
    }

    @Test
    public void testCountMethodsByGroup() throws Exception {
        String group = "S"; // Tested with rice and cowpea
        long count = manager.countMethodsByGroup(group);
        Debug.println(0, "testCountMethodsByGroup(group=" + group + "): " + count);
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
        manager.deleteLocation(manager.getLocationsByName("TEST-LOCATION-1", 0, 5, Operation.EQUAL).get(0));
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
    public void testGetGidAndNidByGermplasmNames() throws Exception {
        List<String> germplasmNames = new ArrayList<String>();
        germplasmNames.add("UCR2010001");
        germplasmNames.add("UCR2010002");
        germplasmNames.add("UCR2010003");

        List<GidNidElement> results = manager.getGidAndNidByGermplasmNames(germplasmNames);
        Debug.println(0, "testGetGidAndNidByGermplasmNames(" + germplasmNames + ") RESULTS: " + results);
    }

    @Test
    public void testUpdateGermplasmName() throws Exception {
        Integer nameId = -1; //Assumption: id=-1 exists
        Name name = manager.getGermplasmNameByID(nameId); 
        String nameBefore = name.toString();
        name.setLocationId(manager.getLocationByID(1).getLocid()); //Assumption: location with id=1 exists
        manager.updateGermplasmName(name);
        Debug.println(0, "testUpdateGermplasmName(" + nameId + ") RESULTS: " 
                + "\n\tBEFORE: " + nameBefore
                + "\n\tAFTER: " + name.toString());
    }

    @Test
    public void testAddGermplasmAttribute() throws Exception {
        Integer gid = Integer.valueOf(50533);        
        Attribute attribute = new Attribute();
        attribute.setAdate(0);
        attribute.setAval("aval");
        attribute.setGermplasmId(gid);
        attribute.setLocationId(0);
        attribute.setUserId(0);
        attribute.setReferenceId(0);
        attribute.setTypeId(0);
        Integer id = manager.addGermplasmAttribute(attribute);
        Debug.println(0, "testAddGermplasmAttribute(" + gid + ") RESULTS: " + id + " = " + attribute);
    }


    @Test
    public void testUpdateGermplasmAttribute() throws Exception {        
        Integer attributeId = -1; //Assumption: attribute with id = -1 exists
        
        Attribute attribute = manager.getAttributeById(attributeId);

        if (attribute  != null){
            String attributeString = "";
            attributeString = attribute.toString();
            attribute.setAdate(0);
            attribute.setLocationId(0);
            attribute.setUserId(0);
            attribute.setReferenceId(0);
            attribute.setTypeId(0);
            manager.updateGermplasmAttribute(attribute);

            Debug.println(0, "testUpdateGermplasmAttribute(" + attributeId + ") RESULTS: "
                    + "\ntBEFORE: " + attributeString
                    + "\ntAFTER: " + attribute);
        }
    }
    
    @Test
    public void testGetUserDefinedFieldByFieldTable() throws MiddlewareQueryException {
    	String tableName="LOCATION";
    	String fieldType="LTYPE";
        List<UserDefinedField> userDefineField = manager.getUserDefinedFieldByFieldTableNameAndType(tableName, fieldType);
        Debug.println(0, "testGetUserDefineFieldByTableNameAndType(type=" + tableName + "): " + userDefineField.size());
        for (UserDefinedField u : userDefineField) {
            Debug.println(0, "  " + u);
        }
    }
    
    @Test
    public void testGetCrossExpansion() throws Exception {
        Debug.println(0, manager.getCrossExpansion(Integer.valueOf(75), 2));
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
    public void testGetNextSequenceNumberForCrossNameInDatabase() throws MiddlewareQueryException{
    	String prefix = "IR";
    	Database db = Database.CENTRAL;
    	Debug.println(0, "Next number in sequence for prefix (" + prefix + ") in " + db + " database: " + 
    			manager.getNextSequenceNumberForCrossName(prefix, db));
    }
    
    @Test
    public void testGetNextSequenceNumberForCrossName() throws MiddlewareQueryException{
    	String prefix = "C97-MNT-";
    	Debug.println(0, "Next number in sequence for prefix (" + prefix + "): " + 
    			manager.getNextSequenceNumberForCrossName(prefix));
    }
   
    @Test
    public void testGetPreferredIdsByGIDs() throws MiddlewareQueryException{
        List<Integer> gids = new ArrayList<Integer>();
        gids.add(Integer.valueOf(50533));
        gids.add(Integer.valueOf(50532));
        gids.add(Integer.valueOf(50531));
        gids.add(Integer.valueOf(404865));
        gids.add(Integer.valueOf(274017));
        
        Map<Integer, String> results = manager.getPrefferedIdsByGIDs(gids);
        for(Integer gid : results.keySet()){
            Debug.println(0, gid + " : " + results.get(gid));
        }
    }
    
    @Test
    public void testCountAllGermplasm() throws MiddlewareQueryException {
        long count = manager.countAllGermplasm((Database.CENTRAL));
        assertNotNull(count);
        Debug.println(0, "testCountAllGermplasm() Results: " + count);
    } 
    
    @Test
    public void testGetAllMethods() throws Exception {
    	List<Method> results = manager.getAllMethods();
        assertNotNull(results);
        assertTrue(!results.isEmpty());
        for (Method result : results){
        	Debug.println(0, result.toString());
        }
        Debug.println(0, "Number of record/s: " +results.size());
    }
    
    @Test
    public void testCountGermplasmByPrefName() throws Exception {
    	String name = "CHALIMBANA"; //change nval
    	long count = manager.countGermplasmByPrefName(name);
    	assertNotNull(count);
    	Debug.println(0, "testCountGermplasmByPrefName("+name+") Results: " + count);
    }

    @Test
    public void testGetAllGermplasm() throws Exception {
    	List<Germplasm> germplasms = manager.getAllGermplasm(0, 100, Database.CENTRAL);
    	assertNotNull(germplasms);
        assertTrue(!germplasms.isEmpty());
        for (Germplasm germplasm : germplasms) {
            Debug.println(0, "  " + germplasm);
        }
        Debug.println(0, "Number of record/s: " +germplasms.size());
    }
    
    @Test
    /* If database has no data, run testAddGermplasmAttribute first before running
     *  this method to insert a new record
     */
    public void testGetAttributeById() throws Exception {
    	Integer id = Integer.valueOf(-1);
        Attribute attributes = manager.getAttributeById(id);
        assertNotNull(attributes);
        Debug.println(0, "testGetAttributeById("+id+") Results:");
        Debug.println(0, "  " + attributes);
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
        Debug.println(0, "Number of record/s: " +locdetails.size() );
    }
    

    @Test
    public void testGetBibliographicalReferenceById() throws Exception {
        Integer id = Integer.valueOf(1);
        Bibref bibref = manager.getBibliographicReferenceByID(id);
        Debug.println(0, "testGetBibliographicalReferenceById(" + id + ") RESULTS: " + bibref);
    }

    
    @Test
    public void testGetMethodByID() throws Exception {
    	Integer id = Integer.valueOf(2);
    	Method methodid = manager.getMethodByID(id);
    	assertNotNull(methodid);
        Debug.println(0, "testGetMethodByID("+id+") Results: ");
        Debug.println(0, "  " + methodid);
    }
    
    @Test
    public void testGetUserDefinedFieldByID() throws Exception {
    	Integer id = Integer.valueOf(2);
    	UserDefinedField result = manager.getUserDefinedFieldByID(id);
    	assertNotNull(result);
        Debug.println(0, "  " + result);
    }
    
    @Test
    public void testGetBibliographicReferenceByID() throws Exception {
    	Integer id = Integer.valueOf(2);
    	Bibref result = manager.getBibliographicReferenceByID(id);
    	assertNotNull(result);
        Debug.println(0, "  " + result);
    }  
    
    @Test
    public void testGetGermplasmByLocationId() throws Exception {
    	String name = "RCH";
    	int locationID = 0;
    	
    	List<Germplasm> germplasmList = manager.getGermplasmByLocationId(name, locationID);
        assertTrue(germplasmList != null);

        Debug.println(0, "testGetGermplasmByLocationId(" + name + ") RESULTS: ");
        for (Germplasm g : germplasmList) {
            Debug.println(0, "  " + g);
        }
    }
    
    @Test
    public void testGetGermplasmByGidRange() throws Exception {
    	int startGID = 1;
    	int endGID = 3;
    	
    	List<Germplasm> germplasmList = manager.getGermplasmByGidRange(startGID, endGID);
        assertTrue(germplasmList != null);

        Debug.println(0, "testGetGermplasmByGidRange(" + startGID + "," + endGID + ") RESULTS: ");
        for (Germplasm g : germplasmList) {
            Debug.println(0, "  " + g);
        }
    }
    
    @Test
    public void testGetGermplasmByGIDList() throws Exception {
    	List<Integer> gids = new ArrayList<Integer>();
    	gids.add(1);
    	gids.add(-1);
    	gids.add(5);
    	
    	List<Germplasm> germplasmList = manager.getGermplasms(gids);
        assertTrue(germplasmList != null);

        Debug.println(0, "testGetGermplasmByGidList(" + gids + ") RESULTS: ");
        for (Germplasm g : germplasmList) {
            Debug.println(0, "  " + g);
        }
    }
    
    @Test
    public void testGetPreferredNamesByGIDs() throws MiddlewareQueryException{
        List<Integer> gids = new ArrayList<Integer>();
        gids.add(Integer.valueOf(50533));
        gids.add(Integer.valueOf(50532));
        gids.add(Integer.valueOf(50531));
        gids.add(Integer.valueOf(404865));
        gids.add(Integer.valueOf(274017));
        
        Map<Integer, String> results = manager.getPreferredNamesByGids(gids);
        Debug.println(0, "RESULTS:");
        for(Integer gid : results.keySet()){
            Debug.println(0, gid + " : " + results.get(gid));
        }
    }
    
    @Test
    public void testGetLocationNamesByGIDs() throws MiddlewareQueryException{
        List<Integer> gids = new ArrayList<Integer>();
        gids.add(Integer.valueOf(50532));
        gids.add(Integer.valueOf(1));
        gids.add(Integer.valueOf(42268));
        gids.add(Integer.valueOf(151));
        
        Map<Integer, String> results = manager.getLocationNamesByGids(gids);
        for(Integer gid : results.keySet()){
            Debug.println(0, gid + " : " + results.get(gid));
        }
    }
    
  @Test
  public void testSearchGermplasm() throws MiddlewareQueryException{
      //String q = "2003";
      String q = "dinurado";
            
      List<Germplasm> results = manager.searchForGermplasm(q);
      
      Debug.println(0, "###############################");
      Debug.println(0, " searchForGermplasm("+q+")");
      Debug.println(0, "###############################");
      
      for(Germplasm g : results){
    	  String name = "";
    	  if(g.getPreferredName()!=null)
    		  if(g.getPreferredName().getNval()!=null)
    			  name = g.getPreferredName().getNval().toString();
          Debug.println(3, g.getGid() + " - " + name);
      }
  }         
    
    @AfterClass
    public static void tearDown() throws Exception {
        factory.close();
    }
    
}
