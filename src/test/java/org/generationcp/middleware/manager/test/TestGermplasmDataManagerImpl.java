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
import java.util.Arrays;
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
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmNameDetails;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.Bibref;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.util.Debug;
import org.generationcp.middleware.utils.test.TestOutputFormatter;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

// Test using RICE database
public class TestGermplasmDataManagerImpl extends TestOutputFormatter{

    private static ManagerFactory factory;
    private static GermplasmDataManager manager;
    private static LocationDataManager locationManager;

    @BeforeClass
    public static void setUp() throws Exception {
        DatabaseConnectionParameters local = 
                new DatabaseConnectionParameters("testDatabaseConfig.properties", "local");
        DatabaseConnectionParameters central = 
                new DatabaseConnectionParameters("testDatabaseConfig.properties", "central");
        factory = new ManagerFactory(local, central);
        manager = factory.getGermplasmDataManager();
        locationManager = factory.getLocationDataManager();
    }

    @Test
    public void testGetMethodsByIDs() throws Exception {

        // Attempt to get all locations so we can proceed
        List<Method> locationList = manager.getAllMethods();
        assertTrue(locationList != null);
        assertTrue("we cannot proceed test if size < 0", locationList.size() > 0);

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

        Debug.printObjects(INDENT, results);
    }

    @Test
    public void testGetGermplasmByName() throws Exception {
        String name = "IR 10";
        
        List<Germplasm> germplasmList = manager.getGermplasmByName(
                            name, 0, 5, GetGermplasmByNameModes.NORMAL, 
                            Operation.EQUAL, null, null, Database.CENTRAL);
        assertTrue(germplasmList != null);

        Debug.println(INDENT, "testGetGermplasmByName(" + name + "): ");
        Debug.printObjects(INDENT, germplasmList);
    }

    @Test
    public void testGetGermplasmByNameOriginalStandardizedAndNoSpace() throws Exception {
        String name = "IR  65";
        List<Germplasm> germplasmList = manager.getGermplasmByName(
                name, 0, Long.valueOf(manager.countGermplasmByName(
                                        name, Operation.EQUAL)).intValue(), Operation.EQUAL);

        Debug.println(INDENT, "testGetGermplasmByNameOriginalStandardizedAndNoSpace(" 
                + name + "): " + germplasmList.size());
        Debug.printObjects(INDENT, germplasmList);
        
        name = "IR 65%";
        germplasmList = manager.getGermplasmByName(name, 0, 
                Long.valueOf(manager.countGermplasmByName(name, Operation.LIKE)).intValue(), 
                Operation.LIKE);

        Debug.println(INDENT, "testGetGermplasmByNameOriginalStandardizedAndNoSpace(" + name 
                + "): " + germplasmList.size());
        Debug.printObjects(INDENT, germplasmList);
    }

    @Test
    public void testCountGermplasmByName() throws Exception {
        String name = "IR 10";
        long count = manager.countGermplasmByName(name, GetGermplasmByNameModes.NORMAL, 
                Operation.EQUAL, null, null, Database.CENTRAL);
        Debug.println(INDENT, "testCountGermplasmByName(" + name + "): " + count);
    }

    @Test
    public void testCountGermplasmByNameOriginalStandardizedAndNoSpace() throws Exception {
        String name = "IR  65";
        long count = manager.countGermplasmByName(name, Operation.EQUAL);
        Debug.println(INDENT, "testCountGermplasmByNameOriginalStandardizedAndNoSpace(" 
                + name + "): " + count);
    }

    @Test
    public void testGetGermplasmByNameUsingLike() throws Exception {
        String name = "IR%";
        
        List<Germplasm> germplasmList = manager.getGermplasmByName(name, 0, 5, 
                GetGermplasmByNameModes.NORMAL, Operation.LIKE, null, null, Database.CENTRAL);
        assertTrue(germplasmList != null);

        Debug.println(INDENT, "testGetGermplasmByNameUsingLike(" + name + "): ");
        Debug.printObjects(INDENT, germplasmList);
    }

    @Test
    public void testCountGermplasmByNameUsingLike() throws Exception {
        String name = "IR%";
        
        long count = manager.countGermplasmByName(name, GetGermplasmByNameModes.NORMAL, 
                Operation.LIKE, null, null, Database.CENTRAL);
        Debug.println(INDENT, "testCountGermplasmByNameUsingLike(" + name + ") RESULTS:" + count);
    }

    @Test
    public void testGetGermplasmByNameWithStatus() throws Exception {
        String name = "IR 64";
        List<Germplasm> germplasmList = manager.getGermplasmByName(name, 0, 5, 
                GetGermplasmByNameModes.NORMAL, Operation.EQUAL,
                Integer.valueOf(1), null, Database.CENTRAL);
        assertTrue(germplasmList != null);

        Debug.println(INDENT, "testGetGermplasmByNameWithStatus(" + name + "): ");
        Debug.printObjects(INDENT, germplasmList);
    }

    @Test
    public void testCountGermplasmByNameWithStatus() throws Exception {
        String name = "IR 64";
        long count = manager.countGermplasmByName(name, GetGermplasmByNameModes.NORMAL, 
                Operation.EQUAL, Integer.valueOf(1), null,
                Database.CENTRAL);
        Debug.println(INDENT, "testCountGermplasmByNameWithStatus(" + name + "): " + count);
    }

    @Test
    public void testGetGermplasmByNameWithStatusAndType() throws Exception {
        String name = "IR 64";
        List<Germplasm> germplasmList = manager.getGermplasmByName(name, 0, 5, 
                GetGermplasmByNameModes.NORMAL, Operation.EQUAL,
                Integer.valueOf(1), GermplasmNameType.RELEASE_NAME, Database.CENTRAL);
        assertTrue(germplasmList != null);

        Debug.println(INDENT, "testGetGermplasmByNameWithStatusAndType(" + name + "): ");
        Debug.printObjects(INDENT, germplasmList);
    }

    @Test
    public void testCountGermplasmByNameWithStatusAndType() throws Exception {
        String name = "IR 64";
        long count = manager.countGermplasmByName(name, GetGermplasmByNameModes.NORMAL, 
                Operation.EQUAL, Integer.valueOf(1),
                GermplasmNameType.RELEASE_NAME, Database.CENTRAL);
        Debug.println(INDENT, "testCountGermplasmByNameWithStatusAndType(" + name + "): " + count);
    }

    @Test
    public void testGetGermplasmByNameWithStatusUsingLike() throws Exception {
        String name = "IR%";
        List<Germplasm> germplasmList = manager.getGermplasmByName(name, 0, 5, 
                GetGermplasmByNameModes.NORMAL, Operation.LIKE, Integer.valueOf(
                1), null, Database.CENTRAL);
        assertTrue(germplasmList != null);
        Debug.println(INDENT, "testGetGermplasmByNameWithStatusUsingLike(" + name + "): ");
        Debug.printObjects(INDENT, germplasmList);
    }

    @Test
    public void testGetGermplasmByNameWithStatusAndTypeUsingLike() throws Exception {
        String name = "IR%";
        List<Germplasm> germplasmList = manager.getGermplasmByName(name, 0, 5, 
                GetGermplasmByNameModes.NORMAL, Operation.LIKE, 
                Integer.valueOf(1), GermplasmNameType.RELEASE_NAME, Database.CENTRAL);
        assertTrue(germplasmList != null);

        Debug.println(INDENT, "testGetGermplasmByNameWithStatusAndTypeUsingLike(" + name + "): ");
        Debug.printObjects(INDENT, germplasmList);
    }

    @Test
    public void testGetGermplasmByLocationNameUsingEqual() throws Exception {
        String name = "Philippines";
        List<Germplasm> germplasmList = manager.getGermplasmByLocationName(name, 0, 5, 
                Operation.EQUAL, Database.CENTRAL);
        Debug.println(INDENT, "testGetGermplasmByLocationNameUsingEqual(" + name + "): ");
        Debug.printObjects(INDENT, germplasmList);
    }

    @Test
    public void testCountGermplasmByLocationNameUsingEqual() throws Exception {
        String name = "Philippines";
        long count = manager.countGermplasmByLocationName(name, Operation.EQUAL, Database.CENTRAL);
        Debug.println(INDENT, "testCountGermplasmByLocationNameUsingEqual(" + name + "): " + count);
    }

    @Test
    public void testGetGermplasmByLocationNameUsingLike() throws Exception {
        String name = "International%";
        List<Germplasm> germplasmList = manager.getGermplasmByLocationName(name, 0, 5, 
                Operation.LIKE, Database.CENTRAL);
        assertTrue(germplasmList != null);
        Debug.println(INDENT, "testGetGermplasmByLocationNameUsingLike(" + name + "): ");
        Debug.printObjects(INDENT, germplasmList);
    }

    @Test
    public void testCountGermplasmByLocationNameUsingLike() throws Exception {
        String name = "International%";
        long count = manager.countGermplasmByLocationName(name, Operation.LIKE, Database.CENTRAL);
        Debug.println(INDENT, "testCountGermplasmByLocationNameUsingLike(" + name + "): " + count);
    }

    @Test
    public void testGetGermplasmByMethodNameUsingEqual() throws Exception {
        String name = "SINGLE CROSS";
        
        List<Germplasm> germplasmList = manager.getGermplasmByMethodName(name, 0, 5, 
                Operation.EQUAL, Database.CENTRAL);
        assertTrue(germplasmList != null);

        Debug.println(INDENT, "testGetGermplasmByMethodNameUsingEqual(" + name + "): ");
        Debug.printObjects(INDENT, germplasmList);
    }

    @Test
    public void testCountGermplasmByMethodNameUsingEqual() throws Exception {
        String name = "SINGLE CROSS";
        long count = manager.countGermplasmByMethodName(name, Operation.EQUAL, Database.CENTRAL);
        Debug.println(INDENT, "testCountGermplasmByMethodNameUsingEqual(" + name + "): " + count);
    }

    @Test
    public void testGetGermplasmByMethodNameUsingLike() throws Exception {
        String name = "%CROSS%";
        
        List<Germplasm> germplasmList = manager.getGermplasmByMethodName(name, 0, 5, 
                Operation.LIKE, Database.CENTRAL);
        assertTrue(germplasmList != null);

        Debug.println(INDENT, "testGetGermplasmByMethodNameUsingLike(" + name + "): ");
        Debug.printObjects(INDENT, germplasmList);
    }

    @Test
    public void testCountGermplasmByMethodNameUsingLike() throws Exception {
        String name = "%CROSS%";
        long count = manager.countGermplasmByMethodName(name, Operation.LIKE, Database.CENTRAL);
        Debug.println(INDENT, "testCountGermplasmByMethodNameUsingLike(" + name + "): " + count);
    }

    @Test
    public void testGetGermplasmByGID() throws Exception {
        Integer gid = Integer.valueOf(50533);
        Germplasm germplasm = manager.getGermplasmByGID(gid);
        Debug.println(INDENT, "testGetGermplasmByGID(" + gid + "): " + germplasm);
    }

    @Test
    public void testGetGermplasmWithPrefName() throws Exception {
        Integer gid = Integer.valueOf(50533);
        Germplasm germplasm = manager.getGermplasmWithPrefName(gid);

        Debug.println(INDENT, "testGetGermplasmWithPrefName(" + gid + "): " + germplasm);
        if (germplasm != null) {
            Debug.println("  preferredName = " + germplasm.getPreferredName());
        }
    }

    @Test
    public void testGetGermplasmWithPrefAbbrev() throws Exception {
        Integer gid = Integer.valueOf(151);
        Germplasm germplasm = manager.getGermplasmWithPrefAbbrev(gid);
        Debug.println(INDENT, "testGetGermplasmWithPrefAbbrev(" + gid + "): " + germplasm);
        Debug.println("  preferredName = " + germplasm.getPreferredName());
        Debug.println("  preferredAbbreviation = " + germplasm.getPreferredAbbreviation());
    }

    @Test
    public void testGetGermplasmNameByID() throws Exception {
        Integer gid = Integer.valueOf(42268);
        Name name = manager.getGermplasmNameByID(gid);
        Debug.println(INDENT, "testGetGermplasmNameByID(" + gid + "): " + name);
    }

    @Test
    public void testGetNamesByGID() throws Exception {
        Integer gid = Integer.valueOf(2434138);
        List<Name> names = manager.getNamesByGID(gid, null, null);
        Debug.println(INDENT, "testGetNamesByGID(" + gid + "): " + names.size());
        Debug.printObjects(names);
    }

    @Test
    public void testGetPreferredNameByGID() throws Exception {
        Integer gid = Integer.valueOf(1);
        Debug.println(INDENT, "testGetPreferredNameByGID(" + gid + "): " 
                + manager.getPreferredNameByGID(gid));
    }
    
    @Test
    public void testGetPreferredNameValueByGID() throws Exception {
        Integer gid = Integer.valueOf(1);
        Debug.println(INDENT, "testGetPreferredNameValueByGID(" + gid + "): " 
                + manager.getPreferredNameValueByGID(gid));
    }

    @Test
    public void testGetPreferredAbbrevByGID() throws Exception {
        Integer gid = Integer.valueOf(1);
        Debug.println(INDENT, "testGetPreferredAbbrevByGID(" + gid + "): " 
                + manager.getPreferredAbbrevByGID(gid));
    }
    
    @Test
    public void testGetPreferredIdByGID() throws Exception {
        Integer gid = Integer.valueOf(986634);
        Debug.println(INDENT, "testGetPreferredIdByGID(" + gid + "): " 
                + manager.getPreferredIdByGID(gid));       
    }
    
    @Test
    public void testGetPreferredIdsByListId() throws Exception {
        Integer listId = Integer.valueOf(2591);
        Debug.println(INDENT, "testGetPreferredIdsByListId(" + listId + "): " 
                + manager.getPreferredIdsByListId(listId));       
    }

    @Test
    public void testGetNameByGIDAndNval() throws Exception {
        Integer gid = Integer.valueOf(225266);
        String nVal = "C 65-44";
        Debug.println(INDENT, "testGetNameByGIDAndNval(" + gid + ", " + nVal 
                        + ", GetGermplasmByNameModes.NORMAL) : " 
                        + manager.getNameByGIDAndNval(gid, nVal, GetGermplasmByNameModes.NORMAL));
        Debug.println(INDENT, "testGetNameByGIDAndNval(" + gid + ", " + nVal 
                        + ", GetGermplasmByNameModes.SPACES_REMOVED) : " 
                        + manager.getNameByGIDAndNval(gid, nVal, GetGermplasmByNameModes.SPACES_REMOVED));
        Debug.println(INDENT, "testGetNameByGIDAndNval(" + gid + ", " + nVal 
                        + ", GetGermplasmByNameModes.STANDARDIZED) : " 
                        + manager.getNameByGIDAndNval(gid, nVal, GetGermplasmByNameModes.STANDARDIZED));
    }

    @Test
    public void testGetNamesByGIDWithStatus() throws Exception {
        Integer gid = Integer.valueOf(50533);
        Integer status = Integer.valueOf(1);
        GermplasmNameType type = null;
        List<Name> names = manager.getNamesByGID(gid, status, type);
        Debug.println(INDENT, "testGetNamesByGIDWithStatus(gid=" + gid + ", status" + status 
                + ", type=" + type + "): " + names);
    }

    @Test
    public void testGetNamesByGIDWithStatusAndType() throws Exception {
        Integer gid = Integer.valueOf(50533);
        Integer status = Integer.valueOf(8);
        GermplasmNameType type = GermplasmNameType.INTERNATIONAL_TESTING_NUMBER;
        List<Name> names = manager.getNamesByGID(gid, status, type);
        Debug.println(INDENT, "testGetNamesByGIDWithStatusAndType(gid=" + gid 
                + ", status" + status + ", type=" + type + "): " + names);
    }

    @Test
    public void testGetAttributesByGID() throws Exception {
        Integer gid = Integer.valueOf(50533);
        List<Attribute> attributes = manager.getAttributesByGID(gid);
        Debug.println(INDENT, "testGetAttributesByGID(" + gid + "): " + attributes);
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

        manager.addMethod(method);

        method = manager.getMethodByID(-1);
        Debug.println(INDENT, "testAddMethod(" + method + "): " + method);

        manager.deleteMethod(method);
    }

    @Test
    public void testAddMethods() throws MiddlewareQueryException {
        List<Method> methods = new ArrayList<Method>();
        methods.add(new Method(-1, "GEN", "S", "UGM", "yesno", "description 1", 
                Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0),
                Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), 
                Integer.valueOf(2), Integer.valueOf(19980610)));
        methods.add(new Method(-2, "GEN", "S", "UGM", "yesno", "description 2", 
                Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0),
                Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), 
                Integer.valueOf(2), Integer.valueOf(19980610)));
        methods.add(new Method(-3, "GEN", "S", "UGM", "yesno", "description 3", 
                Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0),
                Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), 
                Integer.valueOf(2), Integer.valueOf(19980610)));

        List<Integer> methodsAdded = manager.addMethod(methods);
        Debug.println(INDENT, "testAddMethods() Methods added: " + methodsAdded.size());

        for (Integer id: methodsAdded ) {
            Method method = manager.getMethodByID(id);
            Debug.println(INDENT, method);
            manager.deleteMethod(method);
        }
    }

    @Test
    public void testGetMethodsByType() throws MiddlewareQueryException {
        String type = "GEN"; // Tested with rice and cowpea
        int start = 0;
        int numOfRows = 5;

        List<Method> methods = manager.getMethodsByType(type);
        Debug.println(INDENT, "testGetMethodsByType(type=" + type + "): " + methods.size());
        Debug.printObjects(INDENT*2, methods);

        List<Method> methodList = manager.getMethodsByType(type, start, numOfRows);
        Debug.println(INDENT, "testGetMethodsByType(type=" + type + ", start=" + start 
                + ", numOfRows=" + numOfRows + "): " + methodList.size());
        Debug.printObjects(INDENT*2, methods);
    }

    @Test
    public void testCountMethodsByType() throws Exception {
        String type = "GEN"; // Tested with rice and cowpea
        long count = manager.countMethodsByType(type);
        Debug.println(INDENT, "testCountMethodsByType(type=" + type + "): " + count);
    }

    @Test
    public void testGetMethodsByGroup() throws MiddlewareQueryException {
        String group = "S"; // Tested with rice and cowpea
        int start = 0;
        int numOfRows = 5;

        List<Method> methods = manager.getMethodsByGroup(group);
        Debug.println(INDENT, "testGetMethodsByGroup(group=" + group + "): " + methods.size());
        Debug.printObjects(INDENT*2, methods);

        List<Method> methodList = manager.getMethodsByGroup(group, start, numOfRows);
        Debug.println(INDENT, "testGetMethodsByGroup(group=" + group + ", start=" + start 
                + ", numOfRows=" + numOfRows + "): " + methodList.size());
        Debug.printObjects(INDENT, methodList);
    }
    
    @Test
    public void testGetMethodsByGroupIncludesGgroup() throws MiddlewareQueryException {
        String group = "O"; // Tested with rice and cowpea
        List<Method> methods = manager.getMethodsByGroupIncludesGgroup(group);
        Debug.println(INDENT, "testGetMethodsByGroup(group=" + group + "): " + methods.size());
        Debug.printObjects(INDENT, methods);
    }
    
    @Test
    public void testGetMethodsByGroupAndType() throws MiddlewareQueryException {
        String group = "O"; // Tested with rice and cowpea
        String type= "GEN"; // Tested with rice and cowpea

        List<Method> methods = manager.getMethodsByGroupAndType(group, type);
        Debug.println(INDENT, "testGetMethodsByGroupAndType(group=" + group +"and "
                + type + "): " + methods.size());
        Debug.printObjects(INDENT, methods);
    }
    
    @Test
    public void testGetMethodsByGroupAndTypeAndName() throws MiddlewareQueryException {
        String group = "O"; // Tested with rice and cowpea
        String type= "GEN"; // Tested with rice and cowpea
        String name = "ALLO-POLYPLOID CF"; // Tested with rice and cowpea

        List<Method> methods = manager.getMethodsByGroupAndTypeAndName(group, type, name);
        Debug.println(INDENT, "testGetMethodsByGroupAndTypeAndName(group=" + group  
                    + " and type=" + type + " and name=" + name + "): " + methods.size());
        Debug.printObjects(INDENT, methods);
    }

    @Test
    public void testCountMethodsByGroup() throws Exception {
        String group = "S"; // Tested with rice and cowpea
        long count = manager.countMethodsByGroup(group);
        Debug.println(INDENT, "testCountMethodsByGroup(group=" + group + "): " + count);
    }

    @Test
    public void testGetGermplasmDetailsByGermplasmNames() throws Exception {
        List<String> germplasmNames = Arrays.asList("C 65 CU   79", "C 65 CU 80", "C 65 CU 81", "Kevin 64", "Kevin 65");
        // SQL TO VERIFY (CENTRAL AND LOCAL): select gid, nid, nval from names where nval in (:germplasmNames);
        
        List<GermplasmNameDetails> results = manager.getGermplasmNameDetailsByGermplasmNames(germplasmNames, GetGermplasmByNameModes.NORMAL);
        Debug.println(INDENT, "GetGermplasmByNameModes.NORMAL:");
        Debug.printObjects(INDENT, results);
        
        results = manager.getGermplasmNameDetailsByGermplasmNames(germplasmNames, GetGermplasmByNameModes.SPACES_REMOVED);
        Debug.println(INDENT, "GetGermplasmByNameModes.SPACES_REMOVED:");
        Debug.printObjects(INDENT, results);

        results = manager.getGermplasmNameDetailsByGermplasmNames(germplasmNames, GetGermplasmByNameModes.STANDARDIZED);
        Debug.println(INDENT, "GetGermplasmByNameModes.STANDARDIZED:");
        Debug.printObjects(INDENT, results);
    }

    @Test
    public void testUpdateGermplasmName() throws Exception {
        Integer nameId = -1; //Assumption: id=-1 exists
        Name name = manager.getGermplasmNameByID(nameId); 
        if (name != null){
            String nameBefore = name.toString();
            name.setLocationId(locationManager.getLocationByID(1).getLocid()); //Assumption: location with id=1 exists
            manager.updateGermplasmName(name);
            Debug.println(INDENT, "testUpdateGermplasmName(" + nameId + "): " 
                    + "\n\tBEFORE: " + nameBefore
                    + "\n\tAFTER: " + name.toString());
        }
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
        Debug.println(INDENT, "testAddGermplasmAttribute(" + gid + "): " + id + " = " + attribute);
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

            Debug.println(INDENT, "testUpdateGermplasmAttribute(" + attributeId + "): "
                    + "\ntBEFORE: " + attributeString
                    + "\ntAFTER: " + attribute);
        }
    }
    
    @Test
    public void testGetUserDefinedFieldByFieldTable() throws MiddlewareQueryException {
        String tableName="LOCATION";
        String fieldType="LTYPE";
        List<UserDefinedField> userDefineField = manager
                .getUserDefinedFieldByFieldTableNameAndType(tableName, fieldType);
        Debug.println(INDENT, "testGetUserDefineFieldByTableNameAndType(type=" + tableName 
                + "): " + userDefineField.size());
        for (UserDefinedField u : userDefineField) {
            Debug.println(INDENT, u);
        }
    }
    
    @Test
    public void testGetCrossExpansion() throws Exception {
        Debug.println(manager.getCrossExpansion(Integer.valueOf(75), 2));
    }
    
    @Test
    public void testGetNextSequenceNumberForCrossNameInDatabase() throws MiddlewareQueryException{
        String prefix = "IR";
        Database db = Database.CENTRAL;
        Debug.println("Next number in sequence for prefix (" + prefix + ") in " + db + " database: " + 
                manager.getNextSequenceNumberForCrossName(prefix, db));
    }
    
    @Test
    public void testGetNextSequenceNumberForCrossName() throws MiddlewareQueryException{
        String prefix = "PARA7A2";
//      Debug.println("Next number in sequence for prefix (" + prefix + "): " + 
//              manager.getNextSequenceNumberForCrossName(prefix));
        Debug.println(prefix.substring(prefix.length()-1) + " " 
                    + prefix.substring(prefix.length()-1).matches("\\d"));
            
    }
   
    @Test
    public void testGetPreferredIdsByGIDs() throws MiddlewareQueryException{
        List<Integer> gids = Arrays.asList(50533, 50532, 50531, 404865, 274017);
        Map<Integer, String> results = manager.getPrefferedIdsByGIDs(gids);
        for(Integer gid : results.keySet()){
            Debug.println(INDENT, gid + " : " + results.get(gid));
        }
    }
    
    @Test
    public void testCountAllGermplasm() throws MiddlewareQueryException {
        long count = manager.countAllGermplasm((Database.CENTRAL));
        assertNotNull(count);
        Debug.println(INDENT, "testCountAllGermplasm() : " + count);
    } 
    
    @Test
    public void testGetAllMethods() throws Exception {
        List<Method> results = manager.getAllMethods();
        assertNotNull(results);
        assertTrue(!results.isEmpty());
        Debug.printObjects(INDENT, results);
    }
    
    @Test
    public void testCountGermplasmByPrefName() throws Exception {
        String name = "CHALIMBANA"; //change nval
        long count = manager.countGermplasmByPrefName(name);
        assertNotNull(count);
        Debug.println(INDENT, "testCountGermplasmByPrefName("+name+"): " + count);
    }

    @Test
    public void testGetAllGermplasm() throws Exception {
        List<Germplasm> germplasms = manager.getAllGermplasm(0, 100, Database.CENTRAL);
        assertNotNull(germplasms);
        assertTrue(!germplasms.isEmpty());
        Debug.printObjects(INDENT, germplasms);
    }
    
    @Test
    /* If database has no data, run testAddGermplasmAttribute first before running
     *  this method to insert a new record
     */
    public void testGetAttributeById() throws Exception {
        Integer id = Integer.valueOf(-1);
        Attribute attributes = manager.getAttributeById(id);
        assertNotNull(attributes);
        Debug.println(INDENT, "testGetAttributeById("+id+") Results:");
        Debug.println(INDENT, attributes);
    }
    
    @Test
    public void testGetBibliographicalReferenceById() throws Exception {
        Integer id = Integer.valueOf(1);
        Bibref bibref = manager.getBibliographicReferenceByID(id);
        Debug.println(INDENT, "testGetBibliographicalReferenceById(" + id + "): " + bibref);
    }
    
    @Test
    public void testGetMethodByID() throws Exception {
        Integer id = Integer.valueOf(2);
        Method methodid = manager.getMethodByID(id);
        assertNotNull(methodid);
        Debug.println(INDENT, "testGetMethodByID("+id+"): ");
        Debug.println(INDENT, methodid);
    }
    
    @Test
    public void testGetUserDefinedFieldByID() throws Exception {
        Integer id = Integer.valueOf(2);
        UserDefinedField result = manager.getUserDefinedFieldByID(id);
        assertNotNull(result);
        Debug.println(INDENT, result);
    }
    
    @Test
    public void testGetBibliographicReferenceByID() throws Exception {
        Integer id = Integer.valueOf(2);
        Bibref result = manager.getBibliographicReferenceByID(id);
        assertNotNull(result);
        Debug.println(INDENT, result);
    }  
    
    @Test
    public void testGetGermplasmByLocationId() throws Exception {
        String name = "RCH";
        int locationID = 0;
        
        List<Germplasm> germplasmList = manager.getGermplasmByLocationId(name, locationID);
        assertTrue(germplasmList != null);

        Debug.println(INDENT, "testGetGermplasmByLocationId(" + name + "): ");
        Debug.printObjects(INDENT, germplasmList);
    }
    
    @Test
    public void testGetGermplasmByGidRange() throws Exception {
        int startGID = 1;
        int endGID = 3;
        
        List<Germplasm> germplasmList = manager.getGermplasmByGidRange(startGID, endGID);
        assertTrue(germplasmList != null);

        Debug.println(INDENT, "testGetGermplasmByGidRange(" + startGID + "," + endGID + "): ");
        Debug.printObjects(INDENT, germplasmList);
    }
    
    @Test
    public void testGetGermplasmByGIDList() throws Exception {
        List<Integer> gids = Arrays.asList(1, -1, 5);
        
        List<Germplasm> germplasmList = manager.getGermplasms(gids);
        assertTrue(germplasmList != null);

        Debug.println(INDENT, "testGetGermplasmByGidList(" + gids + "): ");
        for (Germplasm g : germplasmList) {
            Debug.println(INDENT, g);
        }
    }
    
    @Test
    public void testGetPreferredNamesByGIDs() throws MiddlewareQueryException{
        List<Integer> gids = Arrays.asList(50533, 50532, 50531, 404865, 274017);
        Map<Integer, String> results = manager.getPreferredNamesByGids(gids);
        for(Integer gid : results.keySet()){
            Debug.println(INDENT, gid + " : " + results.get(gid));
        }
    }
    
    @Test
    public void testGetLocationNamesByGIDs() throws MiddlewareQueryException{
        List<Integer> gids = Arrays.asList(50532, 1, 42268, 151);
        Map<Integer, String> results = manager.getLocationNamesByGids(gids);
        for(Integer gid : results.keySet()){
            Debug.println(INDENT, gid + " : " + results.get(gid));
        }
    }
    
  @Test
  public void testSearchGermplasm() throws MiddlewareQueryException{
      //String q = "2003";
      String q = "dinurado";
      boolean includeParents = true;
            
      List<Germplasm> results = manager.searchForGermplasm(q, Operation.LIKE, includeParents);
      
      Debug.println(INDENT, "searchForGermplasm(" + q + "): ");
      for(Germplasm g : results){
          String name = "";
          if(g.getPreferredName()!= null)
              if(g.getPreferredName().getNval() != null)
                  name = g.getPreferredName().getNval().toString();
          Debug.println(INDENT, g.getGid() + " : " + name);
      }
  }         
  
  @Test
  public void getGermplasmDatesByGids() throws MiddlewareQueryException{
      List<Integer> gids = Arrays.asList(50533, -1);
      Map<Integer,Integer> results = manager.getGermplasmDatesByGids(gids);
      Debug.println(INDENT, "getGermplasmDatesByGids(" + gids + "): ");
      Debug.println(INDENT, results.toString());
  }
  
  @Test
  public void getMethodsByGids() throws MiddlewareQueryException{
      List<Integer> gids = Arrays.asList(50533, -145);
      Map<Integer,Object> results = manager.getMethodsByGids(gids);
      Debug.println(INDENT, "getGermplasmDatesByGids(" + gids + "): ");
      Debug.println(INDENT, results.toString());
  }  
  
    @Test
    public void getAttributeTypesByGIDList() throws MiddlewareQueryException {
        List<Integer> gids = Arrays.asList(110, 50533, 202580);
        List<UserDefinedField> results = manager.getAttributeTypesByGIDList(gids);
        Debug.println(INDENT, "getAttributeTypesByGIDList(" + gids + "): ");
        for(UserDefinedField field : results) {
            Debug.println(INDENT, field.getFname());
        }
    }
    
    @Test
    public void getAttributeValuesByTypeAndGIDList() throws MiddlewareQueryException {
        List<Integer> gids = Arrays.asList(110, 50533, 202580);
        Integer attributeType = 1115;
        Map<Integer, String> results = manager.getAttributeValuesByTypeAndGIDList(attributeType, gids);
        Debug.println(INDENT, "getAttributeValuesByTypeAndGIDList(" + attributeType + ", " + gids + "): ");
        Debug.println(INDENT, results.toString());
    }
  
    @AfterClass
    public static void tearDown() throws Exception {
        factory.close();
    }
    
}
