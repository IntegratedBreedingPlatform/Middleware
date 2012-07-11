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
import org.generationcp.middleware.pojos.gdms.DatasetElement;
import org.generationcp.middleware.pojos.gdms.Map;
import org.generationcp.middleware.pojos.gdms.MapInfo;
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
                System.out.println(nId);
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
                System.out.println(name);
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
            System.out.println(name);
        }
    }
    
    @Test
    public void testGetAllMaps() throws Exception {
        List<Map> maps = manager.getAllMaps(Database.LOCAL);
        System.out.println("RESULTS (testGetAllMaps)");
        
        if(maps == null || maps.isEmpty()) {
            System.out.println("No records found.");
        } else {
            for(Map map : maps) {
                System.out.println(map);
            }
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
                System.out.println(map);
            }
        }
    }
    
    
    @Test
    public void testGetMapInfoByMapName() throws Exception {
        String mapName = ""; //TODO: test with a given map name
        List<MapInfo> results = manager.getMapInfoByMapName(mapName);
        System.out.println("RESULTS (testGetMapInfoByMapName):");
        if (results == null || results.isEmpty()) {
            System.out.println(" No records found.");
        } else {
            for (MapInfo mapInfo : results){
                System.out.println(mapInfo);
            }
        }
    }

    @Test
    public void testGetDatasetNames() throws Exception {
        List<String> results = manager.getDatasetNames();
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
        List<DatasetElement> results = manager.getDatasetDetailsByDatasetName(datasetName);
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
        List<Integer> markerIds = manager.getMarkerIdsByMarkerNames(markerNames, 0, 100);
        System.out.println("getMarkerIdsByMarkerNames: " + markerIds);
    }
    
    @AfterClass
    public static void tearDown() throws Exception {
        factory.close();
    }

}
