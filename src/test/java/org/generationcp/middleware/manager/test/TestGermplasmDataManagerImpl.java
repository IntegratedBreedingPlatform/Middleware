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
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestGermplasmDataManagerImpl{

    private static ManagerFactory factory;
    private static GermplasmDataManager manager;

    @BeforeClass
    public static void setUp() throws Exception {
        DatabaseConnectionParameters local = new DatabaseConnectionParameters("testDatabaseConfig.properties", "local");
        DatabaseConnectionParameters central = new DatabaseConnectionParameters("testDatabaseConfig.properties", "central");
        factory = new ManagerFactory(local, central);
        manager = factory.getGermplasmDataManager();
    }
    
    @Test
    public void testGetAllLocations() throws Exception {
        long start = System.currentTimeMillis();
        List<Location> locationList = manager.getAllLocations(5, 10);
        Assert.assertTrue(locationList != null);

        System.out.println("testGetAllLocations(5,10) RESULTS: ");
        for (Location l : locationList) {
            System.out.println("  " + l);
        }
        long end = System.currentTimeMillis();
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }
    
    @Test
    public void testGetAllCountry() throws Exception {
        long start = System.currentTimeMillis();
        List<Country> countryList = manager.getAllCountry();
        Assert.assertTrue(countryList != null);

        System.out.println("testGetAllCountry RESULTS: ");
        for (Country c : countryList) {
            System.out.println("  " + c);
        }
        long end = System.currentTimeMillis();
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }
    
    @Test
    public void testLocationDetails() throws Exception {
        long start = System.currentTimeMillis();
        List<LocationDetails> locationList = manager.getLocationDetailsByLocId(1, 1, 1);
        Assert.assertTrue(locationList != null);

        System.out.println("testLocationDetails RESULTS: ");
        for (LocationDetails c : locationList) {
            System.out.println("  " + c);
        }
        long end = System.currentTimeMillis();
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }

    @Test
    public void testCountAllLocations() throws Exception {
        long start = System.currentTimeMillis();
        long count = manager.countAllLocations();
        System.out.println("testCountAllLocations(): " + count);
        long end = System.currentTimeMillis();
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }

    @Test
    public void testGetLocationsByName() throws Exception {
        String name = "AFGHANISTAN";
        int start = 0;
        int numOfRows = 5;

        long startTime = System.currentTimeMillis();
        List<Location> locationList = manager.getLocationsByName(name, Operation.EQUAL);
        Assert.assertTrue(locationList != null);
        System.out.println("testGetLocationsByName(" + name + ") RESULTS: " + locationList.size());
        for (Location l : locationList) {
            System.out.println("  " + l);
        }
        long end = System.currentTimeMillis();
        System.out.println("  QUERY TIME: " + (end - startTime) + " ms");

        List<Location> locationList2 = manager.getLocationsByName(name, start, numOfRows, Operation.EQUAL);
        System.out.println("testGetLocationsByName(" + name + ", start=" + start + ", numOfRows=" + numOfRows + ") RESULTS: ");
        for (Location l : locationList2) {
            System.out.println("  " + l);
        }

    }

    @Test
    public void testCountLocationsByName() throws Exception {
        String name = "AFGHANISTAN";
        long start = System.currentTimeMillis();
        long count = manager.countLocationsByName(name, Operation.EQUAL);
        System.out.println("testCountLocationByName(" + name + "): " + count);
        long end = System.currentTimeMillis();
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }

    @Test
    public void testGetLocationsByCountry() throws MiddlewareQueryException {
        Integer id = 171; // Tested in rice db. 171 = Philippines
        Country country = manager.getCountryById(id);
        int start = 0;
        int numOfRows = 5;

        List<Location> locations = manager.getLocationsByCountry(country);
        System.out.println("testGetLocationByCountry(country=" + country + "): " + locations.size());
        for (Location location : locations) {
            System.out.println("  " + location);
        }

        List<Location> locationList = manager.getLocationsByCountry(country, start, numOfRows);
        System.out.println("testGetLocationByCountry(country=" + country + ", start=" + start + ", numOfRows=" + numOfRows + "): "
                + locationList.size());
        for (Location location : locationList) {
            System.out.println("  " + location);
        }
    }
    
    @Test
    public void testGetLocationsByCountryAndType() throws MiddlewareQueryException {
        Integer id = 1; // Tested in rice db. 171 = Philippines
        Country country = manager.getCountryById(id);
        int type=405;

        List<Location> locations = manager.getLocationsByCountryAndType(country, type);
        System.out.println("testGetLocationByCountryAndType(country=" + country + "): type= "+type+ ":"+ locations.size());
        for (Location location : locations) {
            System.out.println("  " + location);
        }

    }


    @Test
    public void testCountLocationsByCountry() throws Exception {
        Integer id = 171; // Tested in rice db. 171 = Philippines
        Country country = manager.getCountryById(id);
        long count = manager.countLocationsByCountry(country);
        System.out.println("testCountLocationByCountry(country=" + country + "): " + count);
    }

    @Test
    public void testGetLocationsByType() throws MiddlewareQueryException {
        Integer type = 405; // Tested in rice db
        int start = 0;
        int numOfRows = 5;

        List<Location> locations = manager.getLocationsByType(type);
        System.out.println("testGetLocationByType(type=" + type + "): " + locations.size());
        for (Location location : locations) {
            System.out.println("  " + location);
        }

        List<Location> locationList = manager.getLocationsByType(type, start, numOfRows);
        System.out.println("testGetLocationByType(type=" + type + ", start=" + start + ", numOfRows=" + numOfRows + "): "
                + locationList.size());
        for (Location location : locationList) {
            System.out.println("  " + location);
        }

    }

    @Test
    public void testCountLocationsByType() throws Exception {
        Integer type = 405; // Tested in rice db
        long count = manager.countLocationsByType(type);
        System.out.println("testCountLocationByType(type=" + type + "): " + count);
    }

    @Test
    public void testGetGermplasmByName() throws Exception {
        String name = "IR 10";
        long start = System.currentTimeMillis();
        List<Germplasm> germplasmList = manager.getGermplasmByName(name, 0, 5, GetGermplasmByNameModes.NORMAL, Operation.EQUAL, null, null,
                Database.CENTRAL);
        Assert.assertTrue(germplasmList != null);

        System.out.println("testGetGermplasmByName(" + name + ") RESULTS: ");
        for (Germplasm g : germplasmList) {
            System.out.println("  " + g);
        }
        long end = System.currentTimeMillis();
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }

    @Test
    public void testGetGermplasmByNameOriginalStandardizedAndNoSpace() throws Exception {
        String name = "IR  65";
        List<Germplasm> germplasmList = manager.getGermplasmByName(name, 0, new Long(manager.countGermplasmByName(name, Operation.EQUAL)).intValue(), Operation.EQUAL);

        System.out.println("testGetGermplasmByNameOriginalStandardizedAndNoSpace(" + name + ") RESULTS: " + germplasmList.size());
        for (Germplasm g : germplasmList) {
            System.out.println("  " + g);
        }
        
        name = "IR 65%";
        germplasmList = manager.getGermplasmByName(name, 0, new Long(manager.countGermplasmByName(name, Operation.LIKE)).intValue(), Operation.LIKE);

        System.out.println("testGetGermplasmByNameOriginalStandardizedAndNoSpace(" + name + ") RESULTS: " + germplasmList.size());
        for (Germplasm g : germplasmList) {
            System.out.println("  " + g);
        }
        
                
        
    }

    @Test
    public void testCountGermplasmByName() throws Exception {
        String name = "IR 10";
        long start = System.currentTimeMillis();
        long count = manager.countGermplasmByName(name, GetGermplasmByNameModes.NORMAL, Operation.EQUAL, null, null, Database.CENTRAL);
        System.out.println("testCountGermplasmByName(" + name + ") RESULTS: " + count);
        long end = System.currentTimeMillis();
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }

    @Test
    public void testCountGermplasmByNameOriginalStandardizedAndNoSpace() throws Exception {
        String name = "IR  65";
        long count = manager.countGermplasmByName(name, Operation.EQUAL);
        System.out.println("testCountGermplasmByNameOriginalStandardizedAndNoSpace(" + name + ") RESULTS: " + count);
    }

    @Test
    public void testGetGermplasmByNameUsingLike() throws Exception {
        String name = "IR%";
        long start = System.currentTimeMillis();
        List<Germplasm> germplasmList = manager.getGermplasmByName(name, 0, 5, GetGermplasmByNameModes.NORMAL, Operation.LIKE, null, null,
                Database.CENTRAL);
        Assert.assertTrue(germplasmList != null);

        System.out.println("testGetGermplasmByNameUsingLike(" + name + ") RESULTS: ");
        for (Germplasm g : germplasmList) {
            System.out.println("  " + g);
        }
        long end = System.currentTimeMillis();
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }

    @Test
    public void testCountGermplasmByNameUsingLike() throws Exception {
        String name = "IR%";
        long start = System.currentTimeMillis();
        long count = manager.countGermplasmByName(name, GetGermplasmByNameModes.NORMAL, Operation.LIKE, null, null, Database.CENTRAL);
        System.out.println("testCountGermplasmByNameUsingLike(" + name + ") RESULTS:" + count);
        long end = System.currentTimeMillis();
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }

    @Test
    public void testGetGermplasmByNameWithStatus() throws Exception {
        String name = "IR 64";
        long start = System.currentTimeMillis();
        List<Germplasm> germplasmList = manager.getGermplasmByName(name, 0, 5, GetGermplasmByNameModes.NORMAL, Operation.EQUAL,
                new Integer(1), null, Database.CENTRAL);
        Assert.assertTrue(germplasmList != null);

        System.out.println("testGetGermplasmByNameWithStatus(" + name + ") RESULTS: ");
        for (Germplasm g : germplasmList) {
            System.out.println("  " + g);
        }
        long end = System.currentTimeMillis();
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }

    @Test
    public void testCountGermplasmByNameWithStatus() throws Exception {
        String name = "IR 64";
        long start = System.currentTimeMillis();
        long count = manager.countGermplasmByName(name, GetGermplasmByNameModes.NORMAL, Operation.EQUAL, new Integer(1), null,
                Database.CENTRAL);
        System.out.println("testCountGermplasmByNameWithStatus(" + name + ") RESULTS: " + count);
        long end = System.currentTimeMillis();
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }

    @Test
    public void testGetGermplasmByNameWithStatusAndType() throws Exception {
        String name = "IR 64";
        long start = System.currentTimeMillis();
        List<Germplasm> germplasmList = manager.getGermplasmByName(name, 0, 5, GetGermplasmByNameModes.NORMAL, Operation.EQUAL,
                new Integer(1), GermplasmNameType.RELEASE_NAME, Database.CENTRAL);
        Assert.assertTrue(germplasmList != null);

        System.out.println("testGetGermplasmByNameWithStatusAndType(" + name + ") RESULTS: ");
        for (Germplasm g : germplasmList) {
            System.out.println("  " + g);
        }
        long end = System.currentTimeMillis();
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }

    @Test
    public void testCountGermplasmByNameWithStatusAndType() throws Exception {
        String name = "IR 64";
        long start = System.currentTimeMillis();
        long count = manager.countGermplasmByName(name, GetGermplasmByNameModes.NORMAL, Operation.EQUAL, new Integer(1),
                GermplasmNameType.RELEASE_NAME, Database.CENTRAL);
        System.out.println("testCountGermplasmByNameWithStatusAndType(" + name + ") RESULTS: " + count);
        long end = System.currentTimeMillis();
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }

    @Test
    public void testGetGermplasmByNameWithStatusUsingLike() throws Exception {
        String name = "IR%";
        long start = System.currentTimeMillis();
        List<Germplasm> germplasmList = manager.getGermplasmByName(name, 0, 5, GetGermplasmByNameModes.NORMAL, Operation.LIKE, new Integer(
                1), null, Database.CENTRAL);
        Assert.assertTrue(germplasmList != null);

        System.out.println("testGetGermplasmByNameWithStatusUsingLike(" + name + ") RESULTS: ");
        for (Germplasm g : germplasmList) {
            System.out.println("  " + g);
        }
        long end = System.currentTimeMillis();
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }

    @Test
    public void testGetGermplasmByNameWithStatusAndTypeUsingLike() throws Exception {
        String name = "IR%";
        long start = System.currentTimeMillis();
        List<Germplasm> germplasmList = manager.getGermplasmByName(name, 0, 5, GetGermplasmByNameModes.NORMAL, Operation.LIKE, new Integer(
                1), GermplasmNameType.RELEASE_NAME, Database.CENTRAL);
        Assert.assertTrue(germplasmList != null);

        System.out.println("testGetGermplasmByNameWithStatusAndTypeUsingLike(" + name + ") RESULTS: ");
        for (Germplasm g : germplasmList) {
            System.out.println("  " + g);
        }
        long end = System.currentTimeMillis();
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }

    @Test
    public void testGetGermplasmByLocationNameUsingEqual() throws Exception {
        String name = "Philippines";
        long start = System.currentTimeMillis();
        List<Germplasm> germplasmList = manager.getGermplasmByLocationName(name, 0, 5, Operation.EQUAL, Database.CENTRAL);

        System.out.println("testGetGermplasmByLocationNameUsingEqual(" + name + ") RESULTS: ");
        for (Germplasm g : germplasmList) {
            System.out.println("  " + g);
        }
        long end = System.currentTimeMillis();
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }

    @Test
    public void testCountGermplasmByLocationNameUsingEqual() throws Exception {
        String name = "Philippines";
        long start = System.currentTimeMillis();
        long count = manager.countGermplasmByLocationName(name, Operation.EQUAL, Database.CENTRAL);
        System.out.println("testCountGermplasmByLocationNameUsingEqual(" + name + ") RESULTS: " + count);
        long end = System.currentTimeMillis();
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }

    @Test
    public void testGetGermplasmByLocationNameUsingLike() throws Exception {
        String name = "International%";
        long start = System.currentTimeMillis();
        List<Germplasm> germplasmList = manager.getGermplasmByLocationName(name, 0, 5, Operation.LIKE, Database.CENTRAL);
        Assert.assertTrue(germplasmList != null);

        System.out.println("testGetGermplasmByLocationNameUsingLike(" + name + ") RESULTS: ");
        for (Germplasm g : germplasmList) {
            System.out.println("  " + g);
        }
        long end = System.currentTimeMillis();
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }

    @Test
    public void testCountGermplasmByLocationNameUsingLike() throws Exception {
        String name = "International%";
        long start = System.currentTimeMillis();
        long count = manager.countGermplasmByLocationName(name, Operation.LIKE, Database.CENTRAL);
        System.out.println("testCountGermplasmByLocationNameUsingLike(" + name + ") RESULTS: " + count);
        long end = System.currentTimeMillis();
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }

    @Test
    public void testGetGermplasmByMethodNameUsingEqual() throws Exception {
        String name = "SINGLE CROSS";
        long start = System.currentTimeMillis();
        List<Germplasm> germplasmList = manager.getGermplasmByMethodName(name, 0, 5, Operation.EQUAL, Database.CENTRAL);
        Assert.assertTrue(germplasmList != null);

        System.out.println("testGetGermplasmByMethodNameUsingEqual(" + name + ") RESULTS: ");
        for (Germplasm g : germplasmList) {
            System.out.println("  " + g);
        }
        long end = System.currentTimeMillis();
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }

    @Test
    public void testCountGermplasmByMethodNameUsingEqual() throws Exception {
        String name = "SINGLE CROSS";
        long start = System.currentTimeMillis();
        long count = manager.countGermplasmByMethodName(name, Operation.EQUAL, Database.CENTRAL);
        System.out.println("testCountGermplasmByMethodNameUsingEqual(" + name + ") RESULTS: " + count);
        long end = System.currentTimeMillis();
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }

    @Test
    public void testGetGermplasmByMethodNameUsingLike() throws Exception {
        String name = "%CROSS%";
        long start = System.currentTimeMillis();
        List<Germplasm> germplasmList = manager.getGermplasmByMethodName(name, 0, 5, Operation.LIKE, Database.CENTRAL);
        Assert.assertTrue(germplasmList != null);

        System.out.println("testGetGermplasmByMethodNameUsingLike(" + name + ") RESULTS: ");
        for (Germplasm g : germplasmList) {
            System.out.println("  " + g);
        }
        long end = System.currentTimeMillis();
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }

    @Test
    public void testCountGermplasmByMethodNameUsingLike() throws Exception {
        String name = "%CROSS%";
        long start = System.currentTimeMillis();
        long count = manager.countGermplasmByMethodName(name, Operation.LIKE, Database.CENTRAL);
        System.out.println("testCountGermplasmByMethodNameUsingLike(" + name + ") RESULTS: " + count);
        long end = System.currentTimeMillis();
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }

    @Test
    public void testGetGermplasmByGID() throws Exception {
        Integer gid = Integer.valueOf(50533);
        Germplasm germplasm = manager.getGermplasmByGID(gid);
        System.out.println("testGetGermplasmByGID(" + gid + "): " + germplasm);
    }

    @Test
    public void testGetGermplasmWithPrefName() throws Exception {
        Integer gid = Integer.valueOf(50533);
        Germplasm germplasm = manager.getGermplasmWithPrefName(gid);

        System.out.println("testGetGermplasmWithPrefName(" + gid + ") RESULTS: " + germplasm);
        if (germplasm != null) {
            System.out.println("  preferredName = " + germplasm.getPreferredName());
        }
    }

    @Test
    public void testGetGermplasmWithPrefAbbrev() throws Exception {
        Integer gid = Integer.valueOf(151);
        Germplasm germplasm = manager.getGermplasmWithPrefAbbrev(gid);

        System.out.println("testGetGermplasmWithPrefAbbrev(" + gid + ") RESULTS: " + germplasm);
        System.out.println("  preferredName = " + germplasm.getPreferredName());
        System.out.println("  preferredAbbreviation = " + germplasm.getPreferredAbbreviation());
    }

    @Test
    public void testGetGermplasmNameByID() throws Exception {
        Integer gid = Integer.valueOf(42268);
        Name name = manager.getGermplasmNameByID(gid);
        System.out.println("testGetGermplasmNameByID(" + gid + ") RESULTS: " + name);
    }

    @Test
    public void testGetNamesByGID() throws Exception {
        Integer gid = Integer.valueOf(50533);
        List<Name> names = manager.getNamesByGID(gid, null, null);
        System.out.println("testGetNamesByGID(" + gid + ") RESULTS: " + names);
    }

    @Test
    public void testGetPreferredNameByGID() throws Exception {
        Integer gid = Integer.valueOf(1);
        System.out.println("testGetPreferredNameByGID(" + gid + ") RESULTS: " + manager.getPreferredNameByGID(gid));
    }
    
    @Test
    public void testGetPreferredNameValueByGID() throws Exception {
        Integer gid = Integer.valueOf(1);
        System.out.println("testGetPreferredNameValueByGID(" + gid + ") RESULTS: " + manager.getPreferredNameValueByGID(gid));
    }

    @Test
    public void testGetPreferredAbbrevByGID() throws Exception {
        Integer gid = Integer.valueOf(1);
        System.out.println("testGetPreferredAbbrevByGID(" + gid + ") RESULTS: " + manager.getPreferredAbbrevByGID(gid));
    }
    
    @Test
    public void testGetPreferredIdByGID() throws Exception {
        //tested using Rice DB
        Integer gid = Integer.valueOf(986634);
        System.out.println("testGetPreferredIdByGID(" + gid + ") RESULTS: " + manager.getPreferredIdByGID(gid));       
    }
    
    @Test
    public void testGetPreferredIdsByListId() throws Exception {
        //tested using Rice DB
        Integer listId = Integer.valueOf(2591);
        System.out.println("testGetPreferredIdsByListId(" + listId + ") RESULTS: " + manager.getPreferredIdsByListId(listId));       
    }

    @Test
    public void testGetNameByGIDAndNval() throws Exception {
        Integer gid = Integer.valueOf(225266);
        String nVal = "C 65-44";
        System.out.println("testGetNameByGIDAndNval(" + gid + ", " + nVal + ", GetGermplasmByNameModes.NORMAL) RESULTS: " 
        				+ manager.getNameByGIDAndNval(gid, nVal, GetGermplasmByNameModes.NORMAL));
        System.out.println("testGetNameByGIDAndNval(" + gid + ", " + nVal + ", GetGermplasmByNameModes.SPACES_REMOVED) RESULTS: " 
        				+ manager.getNameByGIDAndNval(gid, nVal, GetGermplasmByNameModes.SPACES_REMOVED));
        System.out.println("testGetNameByGIDAndNval(" + gid + ", " + nVal + ", GetGermplasmByNameModes.STANDARDIZED) RESULTS: " 
        				+ manager.getNameByGIDAndNval(gid, nVal, GetGermplasmByNameModes.STANDARDIZED));
    }

    @Test
    public void testGetNamesByGIDWithStatus() throws Exception {
        Integer gid = Integer.valueOf(50533);
        Integer status = Integer.valueOf(1);
        GermplasmNameType type = null;
        List<Name> names = manager.getNamesByGID(gid, status, type);
        System.out.println("testGetNamesByGIDWithStatus(gid=" + gid + ", status" + status + ", type=" + type + ") RESULTS: " + names);
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
        System.out.println("testGetAttributesByGID(" + gid + ") RESULTS: " + attributes);
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
        System.out.println("testAddMethod(" + method + ") RESULTS: " + method);

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

        System.out.println("testAddMethods() Methods added: " + methodsAdded.size());

        for (Integer id: methodsAdded ) {
            Method method = manager.getMethodByID(id);
            System.out.println("  " + method);
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
        System.out.println("testGetMethodsByType(type=" + type + "): " + methods.size());
        for (Method method : methods) {
            System.out.println("  " + method);
        }
        List<Method> methodList = manager.getMethodsByType(type, start, numOfRows);
        System.out.println("testGetMethodsByType(type=" + type + ", start=" + start + ", numOfRows=" + numOfRows + "): " + methodList.size());
        for (Method method : methodList) {
            System.out.println("  " + method);
        }

    }

    @Test
    public void testCountMethodsByType() throws Exception {
        String type = "GEN"; // Tested with rice and cowpea
        long count = manager.countMethodsByType(type);
        System.out.println("testCountMethodsByType(type=" + type + "): " + count);
    }

    @Test
    public void testGetMethodsByGroup() throws MiddlewareQueryException {
        String group = "S"; // Tested with rice and cowpea
        int start = 0;
        int numOfRows = 5;

        List<Method> methods = manager.getMethodsByGroup(group);
        System.out.println("testGetMethodsByGroup(group=" + group + "): " + methods.size());
        for (Method method : methods) {
            System.out.println("  " + method);
        }

        List<Method> methodList = manager.getMethodsByGroup(group, start, numOfRows);
        System.out.println("testGetMethodsByGroup(group=" + group + ", start=" + start + ", numOfRows=" + numOfRows + "): " + methodList.size());
        for (Method method : methodList) {
            System.out.println("  " + method);
        }

    }
    
    @Test
    public void testGetMethodsByGroupIncludesGgroup() throws MiddlewareQueryException {
        String group = "O"; // Tested with rice and cowpea

        List<Method> methods = manager.getMethodsByGroupIncludesGgroup(group);
        System.out.println("testGetMethodsByGroup(group=" + group + "): " + methods.size());
        for (Method method : methods) {
            System.out.println("  " + method);
        }


    }
    
    @Test
    public void testGetMethodsByGroupAndType() throws MiddlewareQueryException {
        String group = "O"; // Tested with rice and cowpea
        String type= "GEN"; // Tested with rice and cowpea

        List<Method> methods = manager.getMethodsByGroupAndType(group, type);
        System.out.println("testGetMethodsByGroupAndType(group=" + group +"and "+type + "): " + methods.size());
        for (Method method : methods) {
            System.out.println("  " + method);
        }
    }

    @Test
    public void testCountMethodsByGroup() throws Exception {
        String group = "S"; // Tested with rice and cowpea
        long count = manager.countMethodsByGroup(group);
        System.out.println("testCountMethodsByGroup(group=" + group + "): " + count);
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
        System.out.println("testAddLocation(" + location + ") RESULTS: " + id + "  \n  " 
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

        System.out.println("testAddLocations() Locations added: " + locationsAdded.size());
        System.out.println("  " + manager.getLocationsByName("TEST-LOCATION-2", 0, 5, Operation.EQUAL));
        System.out.println("  " + manager.getLocationsByName("TEST-LOCATION-3", 0, 5, Operation.EQUAL));

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
        System.out.println("testGetGidAndNidByGermplasmNames(" + germplasmNames + ") RESULTS: " + results);
    }

    @Test
    public void testUpdateGermplasmName() throws Exception {
        Integer nameId = -1; //Assumption: id=-1 exists
        Name name = manager.getGermplasmNameByID(nameId); 
        String nameBefore = name.toString();
        name.setLocationId(manager.getLocationByID(1).getLocid()); //Assumption: location with id=1 exists
        manager.updateGermplasmName(name);
        System.out.println("testUpdateGermplasmName(" + nameId + ") RESULTS: " 
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
        System.out.println("testAddGermplasmAttribute(" + gid + ") RESULTS: " + id + " = " + attribute);
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
            Integer id = manager.updateGermplasmAttribute(attribute);

            System.out.println("testUpdateGermplasmAttribute(" + attributeId + ") RESULTS: "
                    + "\ntBEFORE: " + attributeString
                    + "\ntAFTER: " + attribute);
        }
    }
    
    @Test
    public void testGetUserDefinedFieldByFieldTable() throws MiddlewareQueryException {
    	String tableName="LOCATION";
    	String fieldType="LTYPE";
        List<UserDefinedField> userDefineField = manager.getUserDefinedFieldByFieldTableNameAndType(tableName, fieldType);
        System.out.println("testGetUserDefineFieldByTableNameAndType(type=" + tableName + "): " + userDefineField.size());
        for (UserDefinedField u : userDefineField) {
            System.out.println("  " + u);
        }
    }
    
    @Test
    public void testGetCrossExpansion() throws Exception {
        System.out.println(manager.getCrossExpansion(Integer.valueOf(75), 2));
    }
    
    @Test
    public void testGetAllBreedingLocations() throws MiddlewareQueryException {
        List<Location> locations = manager.getAllBreedingLocations();
        System.out.println("getAllBreedingLocations()  " + locations);
    }    
    
    @Test
    public void testCountAllBreedingLocations() throws MiddlewareQueryException {
        Long locationCount = (Long) manager.countAllBreedingLocations();
        System.out.println("countAllBreedingLocations() - Total Count = " + locationCount);
    }        
    
    @Test
    public void testGetNextSequenceNumberForCrossNameInDatabase() throws MiddlewareQueryException{
    	String prefix = "ASDF";
    	Database db = Database.CENTRAL;
    	System.out.println("Next number in sequence for prefix (" + prefix + ") in " + db + " database: " + 
    			manager.getNextSequenceNumberForCrossName(prefix, db));
    }
    
    @Test
    public void testGetNextSequenceNumberForCrossName() throws MiddlewareQueryException{
    	String prefix = "C97-MNT-0";
    	System.out.println("Next number in sequence for prefix (" + prefix + "): " + 
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
        System.out.println("RESULTS:");
        for(Integer gid : results.keySet()){
            System.out.println(gid + " : " + results.get(gid));
        }
    }
    
    @Test
    public void testCountAllGermplasm() throws MiddlewareQueryException {
        long count = manager.countAllGermplasm((Database.CENTRAL));
        Assert.assertNotNull(count);
        System.out.println("testCountAllGermplasm() Results: " + count);
    } 
    
    @Test
    public void testGetAllMethods() throws Exception {
    	List<Method> results = manager.getAllMethods();
        Assert.assertNotNull(results);
        Assert.assertTrue(!results.isEmpty());
        System.out.println("testGetAllMethods() Results: ");
        for (Method result : results){
        	System.out.println(result);
        }
        System.out.println("Number of record/s: " +results.size());
    }
    
    @Test
    public void testCountGermplasmByPrefName() throws Exception {
    	String name = "CHALIMBANA"; //change nval
    	long count = manager.countGermplasmByPrefName(name);
    	Assert.assertNotNull(count);
    	System.out.println("testCountGermplasmByPrefName("+name+") Results: " + count);
    }

    @Test
    public void testGetAllGermplasm() throws Exception {
    	List<Germplasm> germplasms = manager.getAllGermplasm(0, 100, Database.CENTRAL);
    	Assert.assertNotNull(germplasms);
        Assert.assertTrue(!germplasms.isEmpty());
        System.out.println("testGetAllLocations Results: ");
        for (Germplasm germplasm : germplasms) {
            System.out.println("  " + germplasm);
        }
        System.out.println("Number of record/s: " +germplasms.size());
    }
    
    @Test
    public void testGetAttributeById() throws Exception {
    	Integer id = Integer.valueOf(-1);
        Attribute attributes = manager.getAttributeById(id);
        assertNotNull(attributes);
        System.out.println("testGetAttributeById("+id+") Results:");
        System.out.println("  " + attributes);
    }
    
    @Test
    public void testGetCountryById() throws Exception {
    	Integer id = Integer.valueOf(1);
    	Country countries = manager.getCountryById(id);
    	assertNotNull(countries);
        System.out.println("testGetCountryById("+id+") Results:");
        System.out.println("  " + countries);
    }

    @Test
    public void testGetLocationByID() throws Exception {
    	Integer id = Integer.valueOf(1);
    	Location locid = manager.getLocationByID(id);
    	assertNotNull(locid);
        System.out.println("testGetLocationByID("+id+") Results: ");
        System.out.println("  " + locid);
    }
    
    @Test
    public void testGetLocationDetailsByLocId() throws Exception {
    	Integer locationId = Integer.valueOf(2);
    	List<LocationDetails> locdetails = manager.getLocationDetailsByLocId(locationId, 0, 100);
    	Assert.assertNotNull(locdetails);
        Assert.assertTrue(!locdetails.isEmpty());
        System.out.println("testGetLocationDetailsByLocId("+locationId+") Results: ");
        for (LocationDetails locdetail : locdetails) {
            System.out.println("  " + locdetail);
        }
        System.out.println("Number of record/s: " +locdetails.size() );
    }
    

    @Test
    public void testGetBibliographicalReferenceById() throws Exception {
        Integer id = Integer.valueOf(1);
        Bibref bibref = manager.getBibliographicReferenceByID(id);
        System.out.println("testGetBibliographicalReferenceById(" + id + ") RESULTS: " + bibref);
    }

    
    @Test
    public void testGetMethodByID() throws Exception {
    	Integer id = Integer.valueOf(2);
    	Method methodid = manager.getMethodByID(id);
    	assertNotNull(methodid);
        System.out.println("testGetMethodByID("+id+") Results: ");
        System.out.println("  " + methodid);
    }
    
    @Test
    public void testGetUserDefinedFieldByID() throws Exception {
    	Integer id = Integer.valueOf(2);
    	UserDefinedField result = manager.getUserDefinedFieldByID(id);
    	assertNotNull(result);
        System.out.println("testGetUserDefinedFieldByID Results: ");
        System.out.println("  " + result);
    }
    
    @Test
    public void testGetBibliographicReferenceByID() throws Exception {
    	Integer id = Integer.valueOf(2);
    	Bibref result = manager.getBibliographicReferenceByID(id);
    	assertNotNull(result);
        System.out.println("testGetBibliographicReferenceByID Results: ");
        System.out.println("  " + result);
    }  
    
    @Test
    public void testGetGermplasmByLocationId() throws Exception {
    	String name = "RCH";
    	int locationID = 0;
    	
    	long start = System.currentTimeMillis();
    	List<Germplasm> germplasmList = manager.getGermplasmByLocationId(name, locationID);
        Assert.assertTrue(germplasmList != null);

        System.out.println("testGetGermplasmByLocationId(" + name + ") RESULTS: ");
        for (Germplasm g : germplasmList) {
            System.out.println("  " + g);
        }
        long end = System.currentTimeMillis();
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }
    
    @AfterClass
    public static void tearDown() throws Exception {
        factory.close();
    }
    
}
