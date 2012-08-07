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

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.GenotypicDataManager;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.gdms.AllelicValueElement;
import org.generationcp.middleware.pojos.gdms.AllelicValueWithMarkerIdElement;
import org.generationcp.middleware.pojos.gdms.DatasetElement;
import org.generationcp.middleware.pojos.gdms.GermplasmMarkerElement;
import org.generationcp.middleware.pojos.gdms.Map;
import org.generationcp.middleware.pojos.gdms.MapInfo;
import org.generationcp.middleware.pojos.gdms.MarkerInfo;
import org.generationcp.middleware.pojos.gdms.MappingValueElement;
import org.generationcp.middleware.pojos.gdms.MarkerNameElement;
import org.generationcp.middleware.pojos.gdms.ParentElement;
import org.hibernate.QueryException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestGenotypicDataManagerImpl{

    private static ManagerFactory factory;
    private static GenotypicDataManager manager;
    
    @BeforeClass
    public static void setUp() throws Exception {
        DatabaseConnectionParameters local = new DatabaseConnectionParameters("testDatabaseConfig.properties", "local"); 
        DatabaseConnectionParameters central = new DatabaseConnectionParameters("testDatabaseConfig.properties", "central");
        factory = new ManagerFactory(local, central);
        manager = factory.getGenotypicDataManager();
    }
    
    @Test
    public void testGetNameIdsByGermplasmIds() throws Exception {
        List<Integer> germplasmIds = new ArrayList<Integer>();
        germplasmIds.add(Integer.valueOf(-3787));
        germplasmIds.add(Integer.valueOf(-6785));
        germplasmIds.add(Integer.valueOf(-4070));
        
        List<Integer> results = manager.getNameIdsByGermplasmIds(germplasmIds);
        
        System.out.println("RESULTS (testGetNameIdsByGermplasmIds):");
        if (results == null || results.isEmpty()) {
            System.out.println(" No records found.");
        } else {
            for (Integer nId : results){
                System.out.println(" " + nId);
            }
        }
    }

    @Test
    public void testGetNamesByNameIds() throws Exception {
        List<Integer> nameIds = new ArrayList<Integer>();
        nameIds.add(Integer.valueOf(-1));
        nameIds.add(Integer.valueOf(-2));
        nameIds.add(Integer.valueOf(-3));
        
        List<Name> results = manager.getNamesByNameIds(nameIds);
        System.out.println("RESULTS (testGetNamesByNameIds):");
        if (results == null || results.isEmpty()) {
            System.out.println(" No records found.");
        } else {
            for (Name name : results){
                System.out.println(" " + name);
            }
        }
    }
    
    @Test
    public void testGetNameByNameId() throws Exception {
        Name name = manager.getNameByNameId(-1);
        System.out.println("RESULTS (testGetNameByNameId):");
        
        if(name == null) {
            System.out.println("No record found.");
        } else {
            System.out.println(" " + name);
        }
    }
    
    @Test
    public void testGetFirstFiveMaps() throws Exception {
        List<Map> maps = manager.getAllMaps(0, 5, Database.LOCAL);
        System.out.println("RESULTS (testGetFirstFiveMaps)");
        
        if(maps == null || maps.isEmpty()) {
            System.out.println("No records found.");
        } else {
            for(Map map : maps) {
                System.out.println(" " + map);
            }
        }
    }
    
    
    @Test
    public void testGetMapInfoByMapName() throws Exception {
        String mapName = ""; //TODO: test with a given map name
        List<MapInfo> results = manager.getMapInfoByMapName(mapName, Database.LOCAL);
        System.out.println("RESULTS (testGetMapInfoByMapName):");
        if (results == null || results.isEmpty()) {
            System.out.println(" No records found.");
        } else {
            for (MapInfo mapInfo : results){
                System.out.println(" " + mapInfo);
            }
        }
    }
    

    @Test
    public void testCountDatasetNames() throws Exception {
        int results = manager.countDatasetNames(Database.LOCAL);
        System.out.println("RESULTS (testCountDatasetNames): " + results);
    }

    
    @Test
    public void testGetDatasetNames() throws Exception {
        List<String> results = manager.getDatasetNames(0, 5, Database.LOCAL);
        System.out.println("RESULTS (testGetDatasetNames):");
        if (results == null || results.isEmpty()) {
            System.out.println(" No records found.");
        } else {
            for (Object obj : results){
                Assert.assertTrue(obj instanceof String);
                Assert.assertTrue(obj != null);
                String element = (String) obj;
                System.out.println(" " + element);
            }
        }
    }
    

    @Test
    public void testGetDatasetDetailsByDatasetName() throws Exception {
        String datasetName = "MARS";
        List<DatasetElement> results = manager.getDatasetDetailsByDatasetName(datasetName, Database.LOCAL);
        System.out.println("RESULTS (testGetDatasetDetailsByDatasetName):");
        if (results == null || results.isEmpty()) {
            System.out.println(" No records found.");
        } else {
            for (Object obj : results){
                Assert.assertTrue(obj instanceof DatasetElement);
                Assert.assertTrue(obj != null);
                DatasetElement element = (DatasetElement) obj;
                System.out.println(" " + element);
            }
        }
    }
    
    @Test
    public void testGetMarkerIdsByMarkerNames() throws Exception {
        List<String> markerNames = new ArrayList<String>();
        markerNames.add("1_0085");
        markerNames.add("1_0319");
        markerNames.add("1_0312");
        
        /* Expected results are: [1, 2, 3, 174, 199, 201]
         * This is based on the sample input data templates uploaded to GDMS */
        List<Integer> markerIds = manager.getMarkerIdsByMarkerNames(markerNames, 0, 100, Database.LOCAL);
        System.out.println("getMarkerIdsByMarkerNames: " + markerIds);
    }

    @Test
    public void testGetMarkerIdsByDatasetId() throws Exception {
        Integer datasetId = Integer.valueOf(2);
        List<Integer> results = manager.getMarkerIdsByDatasetId(datasetId);
        System.out.println("RESULTS (testGetMarkerIdByDatasetId):");
        if (results == null || results.isEmpty()) {
            System.out.println(" No records found.");
        } else {
            for (Object obj : results){
                Assert.assertTrue(obj instanceof Integer);
                Assert.assertTrue(obj != null);
                Integer element = (Integer) obj;
                System.out.println(" " + element);
            }
        }
    }
    
    @Test
    public void testGetParentsByDatasetId() throws Exception {
        Integer datasetId = Integer.valueOf(2);
        List<ParentElement> results = manager.getParentsByDatasetId(datasetId);
        System.out.println("RESULTS (testGetParentsByDatasetId):");
        if (results == null || results.isEmpty()) {
            System.out.println(" No records found.");
        } else {
            for (Object obj : results){
                Assert.assertTrue(obj instanceof ParentElement);
                Assert.assertTrue(obj != null);
                ParentElement element = (ParentElement) obj;
                System.out.println(" " + element);
            }
        }
    }
    
    @Test
    public void testGetMarkerTypesByMarkerIds() throws Exception {
        List<Integer> markerIds = new ArrayList<Integer>();
        markerIds.add(Integer.valueOf(1));
        markerIds.add(Integer.valueOf(2));
        markerIds.add(Integer.valueOf(3));
        markerIds.add(Integer.valueOf(4));
        markerIds.add(Integer.valueOf(5));
        
        List<String> results = manager.getMarkerTypesByMarkerIds(markerIds);
        System.out.println("RESULTS (testGetMarkerTypeByMarkerIds):");
        if (results == null || results.isEmpty()) {
            System.out.println(" No records found.");
        } else {
            for (Object obj : results){
                Assert.assertTrue(obj instanceof String);
                Assert.assertTrue(obj != null);
                String element = (String) obj;
                System.out.println(" " + element);
            }
        }
    }
    
    @Test
    public void testGetMarkerNamesByGIds() throws Exception {
        List<Integer> gIds = new ArrayList<Integer>();
        gIds.add(Integer.valueOf(-4072));
        gIds.add(Integer.valueOf(-4070));
        gIds.add(Integer.valueOf(-4069));
        
        List<MarkerNameElement> results = manager.getMarkerNamesByGIds(gIds);
        System.out.println("RESULTS (testGetMarkerNamesByGIds):");
        if (results == null || results.isEmpty()) {
            System.out.println(" No records found.");
        } else {
            for (Object obj : results){
                Assert.assertTrue(obj instanceof MarkerNameElement);
                Assert.assertTrue(obj != null);
                MarkerNameElement element = (MarkerNameElement) obj;
                System.out.println(" " + element);
            }
        }
    }
     
    
    @Test
    public void testGetGermplasmNamesByMarkerNames() throws Exception {
        List<String> markerNames = new ArrayList<String>();
        markerNames.add("1_0001");
        markerNames.add("1_0007");
        markerNames.add("1_0013");
        
        List<GermplasmMarkerElement> results = (List<GermplasmMarkerElement>) manager.getGermplasmNamesByMarkerNames(markerNames, Database.LOCAL);
        System.out.println("RESULTS (testGetGermplasmNamesByMarkerNames):");
        if (results == null || results.isEmpty()) {
            System.out.println(" No records found.");
        } else {
            for (Object obj : results){
                Assert.assertTrue(obj instanceof GermplasmMarkerElement);
                Assert.assertTrue(obj != null);
                GermplasmMarkerElement element = (GermplasmMarkerElement) obj;
                System.out.println(" " + element);
            }
        }
    }
    
    @Test
    public void testGetMappingValuesByGidsAndMarkerNames() throws Exception {
        List<String> markerNames = new ArrayList<String>();
        markerNames.add("1_0085");
        markerNames.add("1_0319");
        markerNames.add("1_0312");
        List<Integer> gids = new ArrayList<Integer>();
        gids.add(-3785);
        gids.add(-3786);
        gids.add(-3787);
        /* Expected results are: [datasetId=2, mappingType=allelic, parentAGid=-6785, parentBGid=-6786, markerType=S]
         * This is based on the sample input data templates uploaded to GDMS */
        List<MappingValueElement> mappingValues = manager.getMappingValuesByGidsAndMarkerNames(gids, markerNames, 0, 100);
        System.out.println("getMappingValuesByGidsAndMarkerNames: " + mappingValues);
    }
    
    @Test
    public void testGetAllelicValuesByGidsAndMarkerNames() throws Exception {
        List<String> markerNames = new ArrayList<String>();
        markerNames.add("CaM0038");
        markerNames.add("CaM0463");
        markerNames.add("CaM0539");
        markerNames.add("CaM0639");
        markerNames.add("CaM0658");
        markerNames.add("1_0001");
        markerNames.add("1_0007");
        markerNames.add("1_0013");
        markerNames.add("1_0025");
        markerNames.add("1_0031");
        List<Integer> gids = new ArrayList<Integer>();
        gids.add(-5276);
        gids.add(-5287);
        gids.add(-5484);
        gids.add(-5485);
        gids.add(-6786);
        gids.add(-6785);
        gids.add(-3785);
        gids.add(-3786);
        /* Results will vary depending on the database connected to.
         * As of the moment, we have no data that contains test values in all 3 source tables */
        List<AllelicValueElement> allelicValues = manager.getAllelicValuesByGidsAndMarkerNames(gids, markerNames);
        System.out.println("getAllelicValuesByGidsAndMarkerNames: " + allelicValues);
    }
    
    @Test
    public void testGetAllelicValuesFromCharValuesByDatasetId() throws Exception {        
        Integer datasetId = Integer.valueOf(2);
        try {
            int count = manager.countAllelicValuesFromCharValuesByDatasetId(datasetId);
            List<AllelicValueWithMarkerIdElement> allelicValues = manager.getAllelicValuesFromCharValuesByDatasetId(datasetId, 0, count);
            System.out.println("RESULTS (testGetAllelicValuesFromCharValuesByDatasetId): ");
            for (Object o: allelicValues){
                System.out.println("  " + o);
            }
        } catch(QueryException e){
           e.printStackTrace();
        }

    }

    @Test
    public void testGetAllelicValuesFromAlleleValuesByDatasetId() throws Exception {        
        Integer datasetId = Integer.valueOf(2);
        try {
            int count = manager.countAllelicValuesFromCharValuesByDatasetId(datasetId);
            List<AllelicValueWithMarkerIdElement> allelicValues = manager.getAllelicValuesFromAlleleValuesByDatasetId(datasetId, 0, count);
            System.out.println("RESULTS (testGetAllelicValuesFromAlleleValuesByDatasetId): ");
            for (Object o: allelicValues){
                System.out.println("  " + o);
            }
        } catch(QueryException e){
           e.printStackTrace();
        }

    }

    @Test
    public void testGetAllelicValuesFromMappingPopValuesByDatasetId() throws Exception {        
        Integer datasetId = Integer.valueOf(2);
        try {
            int count = manager.countAllelicValuesFromCharValuesByDatasetId(datasetId);
            List<AllelicValueWithMarkerIdElement> allelicValues = manager.getAllelicValuesFromMappingPopValuesByDatasetId(datasetId, 0, count);
            System.out.println("RESULTS (testGetAllelicValuesFromMappingPopValuesByDatasetId): ");
            for (Object o: allelicValues){
                System.out.println("  " + o);
            }
        } catch(QueryException e){
           e.printStackTrace();
        }

    }
    

    @Test
    public void testGetMarkerNamesByMarkerIds() throws Exception {
        List<Integer> markerIds = new ArrayList<Integer>();
        markerIds.add(-1);
        markerIds.add(-2);
        markerIds.add(-3);
        markerIds.add(-4);
        markerIds.add(-5);
        
        List<String> markerNames = manager.getMarkerNamesByMarkerIds(markerIds);
        
        System.out.println("testGetMarkerNamesByMarkerIds: " + markerNames);
    }
    
    @Test
    public void testGetAllMarkerTypes() throws Exception {
        List<String> markerTypes = manager.getAllMarkerTypes(0, 10);
        System.out.println("testGetAllMarkerTypes: " + markerTypes);
    }
    
    @Test
    public void testCountAllMarkerTypes() throws Exception {
        long result = manager.countAllMarkerTypes(Database.LOCAL);
        System.out.println("testCountAllMarkerTypes: " + result);
    }
    
    @Test 
    public void testGetMarkerNamesByMarkerType() throws Exception {
        List<String> markerNames = manager.getMarkerNamesByMarkerType("asdf", 1, 2);
        System.out.println("testGetMarkerNamesByMarkerType: " + markerNames);
    }
    
    @Test
    public void testCountMarkerNamesByMarkerType() throws Exception {
        long result = manager.countMarkerNamesByMarkerType("asdf", Database.CENTRAL);
        System.out.println("testCountMarkerNamesByMarkerType: " + result);
    }
    
    @Test
    public void testGetMarkerInfoByMarkerName() throws Exception {        
        String markerName = "1_0437";
        try {
            int count = manager.countMarkerInfoByMarkerName(markerName);
            System.out.println("RESULT (countMarkerInfoByMarkerName) = " + count);
            List<MarkerInfo> results = manager.getMarkerInfoByMarkerName(markerName, 0, count);
            System.out.println("RESULTS (getMarkerInfoByMarkerName): ");
            if (results == null || results.isEmpty()) {
                System.out.println(" No records found.");
            } else {
                for (MarkerInfo markerInfo : results) {
                    System.out.println(" " + markerInfo);
                }
            }
        } catch(QueryException e){
           e.printStackTrace();
        }

    }
    
    @Test
    public void testGetMarkerInfoByGenotype() throws Exception {        
        String genotype = "";
        try {
            int count = manager.countMarkerInfoByGenotype(genotype);
            System.out.println("RESULT (countMarkerInfoByGenotype) = " + count);
            List<MarkerInfo> results = manager.getMarkerInfoByGenotype(genotype, 0, count);
            System.out.println("RESULTS (getMarkerInfoByGenotype): ");
            if (results == null || results.isEmpty()) {
                System.out.println(" No records found.");
            } else {
                for (MarkerInfo markerInfo : results) {
                    System.out.println(" " + markerInfo);
                }
            }
        } catch(QueryException e){
           e.printStackTrace();
        }

    }

    @Test 
    public void testGetGidsFromCharValuesByMarkerId() throws Exception {
        List<Integer> gids = manager.getGIDsFromCharValuesByMarkerId(1, 1, 10);
        System.out.println("testGetGidsFromCharValuesByMarkerId: " + gids);
    }
    
    @Test
    public void testCountGidsFromCharValuesByMarkerId() throws Exception {
        Long result = manager.countGIDsFromCharValuesByMarkerId(1);
        System.out.println("testCountGidsFromCharValuesByMarkerId: " + result);
    }
    
    @Test 
    public void testGetGidsFromAlleleValuesByMarkerId() throws Exception {
        List<Integer> gids = manager.getGIDsFromCharValuesByMarkerId(1, 1, 10);
        System.out.println("testGetGidsFromAlleleValuesByMarkerId: " + gids);
    }
    
    @Test
    public void testCountGidsFromAlleleValuesByMarkerId() throws Exception {
        Long result = manager.countGIDsFromCharValuesByMarkerId(1);
        System.out.println("testCountGidsFromAlleleValuesByMarkerId: " + result);
    }
    
    @AfterClass
    public static void tearDown() throws Exception {
        factory.close();
    }

}
