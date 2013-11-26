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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.GdmsTable;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.SetOperation;
import org.generationcp.middleware.manager.api.GenotypicDataManager;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.gdms.AccMetadataSet;
import org.generationcp.middleware.pojos.gdms.AccMetadataSetPK;
import org.generationcp.middleware.pojos.gdms.AlleleValues;
import org.generationcp.middleware.pojos.gdms.AllelicValueElement;
import org.generationcp.middleware.pojos.gdms.AllelicValueWithMarkerIdElement;
import org.generationcp.middleware.pojos.gdms.CharValues;
import org.generationcp.middleware.pojos.gdms.DartValues;
import org.generationcp.middleware.pojos.gdms.Dataset;
import org.generationcp.middleware.pojos.gdms.DatasetElement;
import org.generationcp.middleware.pojos.gdms.DatasetUsers;
import org.generationcp.middleware.pojos.gdms.GermplasmMarkerElement;
import org.generationcp.middleware.pojos.gdms.Map;
import org.generationcp.middleware.pojos.gdms.MapDetailElement;
import org.generationcp.middleware.pojos.gdms.MapInfo;
import org.generationcp.middleware.pojos.gdms.MappingPop;
import org.generationcp.middleware.pojos.gdms.MappingPopValues;
import org.generationcp.middleware.pojos.gdms.MappingValueElement;
import org.generationcp.middleware.pojos.gdms.Marker;
import org.generationcp.middleware.pojos.gdms.MarkerAlias;
import org.generationcp.middleware.pojos.gdms.MarkerDetails;
import org.generationcp.middleware.pojos.gdms.MarkerIdMarkerNameElement;
import org.generationcp.middleware.pojos.gdms.MarkerInfo;
import org.generationcp.middleware.pojos.gdms.MarkerMetadataSet;
import org.generationcp.middleware.pojos.gdms.MarkerMetadataSetPK;
import org.generationcp.middleware.pojos.gdms.MarkerNameElement;
import org.generationcp.middleware.pojos.gdms.MarkerOnMap;
import org.generationcp.middleware.pojos.gdms.MarkerUserInfo;
import org.generationcp.middleware.pojos.gdms.Mta;
import org.generationcp.middleware.pojos.gdms.ParentElement;
import org.generationcp.middleware.pojos.gdms.Qtl;
import org.generationcp.middleware.pojos.gdms.QtlDataElement;
import org.generationcp.middleware.pojos.gdms.QtlDetailElement;
import org.generationcp.middleware.pojos.gdms.QtlDetails;
import org.generationcp.middleware.pojos.gdms.QtlDetailsPK;
import org.generationcp.middleware.util.Debug;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class TestGenotypicDataManagerImpl{

    private static ManagerFactory factory;
    private static GenotypicDataManager manager;
    
    private long startTime;

    @Rule
    public TestName name = new TestName();



    @BeforeClass
    public static void setUp() throws Exception {
        DatabaseConnectionParameters local = new DatabaseConnectionParameters("testDatabaseConfig.properties", "localgroundnut");
        DatabaseConnectionParameters central = new DatabaseConnectionParameters("testDatabaseConfig.properties", "centralgroundnut");
        factory = new ManagerFactory(local, central);
        manager = factory.getGenotypicDataManager();
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
    public void testGetNameIdsByGermplasmIds() throws Exception {
        List<Integer> germplasmIds = new ArrayList<Integer>();
        germplasmIds.add(Integer.valueOf(-3787));
        germplasmIds.add(Integer.valueOf(-6785));
        germplasmIds.add(Integer.valueOf(-4070));
        List<Integer> results = manager.getNameIdsByGermplasmIds(germplasmIds);
        Debug.println(0, "testGetNameIdsByGermplasmIds(" + germplasmIds + ") RESULTS: " + results);
    }

    @Test
    public void testGetNamesByNameIds() throws Exception {
        List<Integer> nameIds = new ArrayList<Integer>();
        nameIds.add(Integer.valueOf(-1));
        nameIds.add(Integer.valueOf(-2));
        nameIds.add(Integer.valueOf(-3));
        List<Name> results = manager.getNamesByNameIds(nameIds);
        Debug.println(0, "testGetNamesByNameIds(" + nameIds + ") RESULTS: " + results);
    }

    @Test
    public void testGetNameByNameId() throws Exception {
        Integer nameId = -1;
        Name name = manager.getNameByNameId(nameId);
        Debug.println(0, "testGetNameByNameId(nameId=" + nameId + ") RESULTS: " + name);
    }

    @Test
    public void testGetFirstFiveMaps() throws Exception {
        List<Map> maps = manager.getAllMaps(0, 5, Database.LOCAL);
        Debug.println(0, "RESULTS (testGetFirstFiveMaps): " + maps);
    }

    @Test
    public void testGetMapInfoByMapName() throws Exception {
        String mapName = "RIL-8 (Yueyou13 x J 11)"; //TODO: test with a given map name
        List<MapInfo> results = manager.getMapInfoByMapName(mapName, Database.CENTRAL);
        Debug.println(0, "testGetMapInfoByMapName(mapName=" + mapName + ") RESULTS size: " + results.size());
        for (MapInfo mapInfo : results){
            Debug.println(0, mapInfo.toString());
        }
    }

    @Test
    public void testCountDatasetNames() throws Exception {
        long results = manager.countDatasetNames(Database.LOCAL);
        Debug.println(0, "testCountDatasetNames(Database.LOCAL) RESULTS: " + results);
    }

    @Test
    public void testGetDatasetNames() throws Exception {
        List<String> results = manager.getDatasetNames(0, 5, Database.LOCAL);
        Debug.println(0, "testGetDatasetNames(0,5,Database.Local) RESULTS: " + results);
    }

    @Test
    public void testGetDatasetNamesByQtlId() throws Exception {
    	Integer qtlId = 1;    		// Crop tested: Groundnut
        List<String> results = manager.getDatasetNamesByQtlId(qtlId, 0, 5);
        Debug.println(0, "testGetDatasetNamesByQtlId(0,5) RESULTS: " + results);
    }

    @Test
    public void testCountDatasetNamesByQtlId() throws Exception {
    	Integer qtlId = 1;   		// Crop tested: Groundnut 
        long results = manager.countDatasetNamesByQtlId(qtlId);
        Debug.println(0, "testCountDatasetNamesByQtlId() RESULTS: " + results);
    }

    @Test
    public void testGetDatasetDetailsByDatasetName() throws Exception {
        String datasetName = "MARS";
        List<DatasetElement> results = manager.getDatasetDetailsByDatasetName(datasetName, Database.LOCAL);
        Debug.println(0, "testGetDatasetDetailsByDatasetName(" + datasetName + ",LOCAL) RESULTS: " + results);
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
        Debug.println(0, "getMarkerIdsByMarkerNames (" + markerNames + ") RESULTS: " + markerIds);
    }

    @Test
    public void testGetMarkerIdsByDatasetId() throws Exception {
        Integer datasetId = Integer.valueOf(2);
        List<Integer> results = manager.getMarkerIdsByDatasetId(datasetId);
        Debug.println(0, "testGetMarkerIdByDatasetId(" + datasetId + ") RESULTS: " + results);
    }

    @Test
    public void testGetParentsByDatasetId() throws Exception {
        Integer datasetId = Integer.valueOf(2);
        List<ParentElement> results = manager.getParentsByDatasetId(datasetId);
        Debug.println(0, "testGetParentsByDatasetId(" + datasetId + ") RESULTS: " + results);
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
        Debug.println(0, "testGetMarkerTypeByMarkerIds(" + markerIds + ") RESULTS: " + results);
    }

    @Test
    public void testGetMarkerNamesByGIds() throws Exception {
        List<Integer> gIds = new ArrayList<Integer>();
        gIds.add(Integer.valueOf(-4072));
        gIds.add(Integer.valueOf(-4070));
        gIds.add(Integer.valueOf(-4069));

        List<MarkerNameElement> results = manager.getMarkerNamesByGIds(gIds);
        Debug.println(0, "testGetMarkerNamesByGIds(" + gIds + ") RESULTS: " + results);
    }

    @Test
    public void testGetGermplasmNamesByMarkerNames() throws Exception {
        List<String> markerNames = new ArrayList<String>();
        markerNames.add("1_0001");
        markerNames.add("1_0007");
        markerNames.add("1_0013");

        List<GermplasmMarkerElement> results = (List<GermplasmMarkerElement>) manager.getGermplasmNamesByMarkerNames(markerNames,
                Database.LOCAL);
        Debug.println(0, "testGetGermplasmNamesByMarkerNames(" + markerNames + ")  RESULTS: " + results);
    }

    @Test
    public void testGetMappingValuesByGidsAndMarkerNames() throws Exception {
        List<Integer> gids = new ArrayList<Integer>();
        gids.add(-3785);
        gids.add(-3786);
        gids.add(-3787);
        List<String> markerNames = new ArrayList<String>();
        markerNames.add("1_0085");
        markerNames.add("1_0319");
        markerNames.add("1_0312");
        /* Expected results are: [datasetId=2, mappingType=allelic, parentAGid=-6785, parentBGid=-6786, markerType=S]
         * This is based on the sample input data templates uploaded to GDMS */
        List<MappingValueElement> mappingValues = manager.getMappingValuesByGidsAndMarkerNames(gids, markerNames, 0, 100);
        Debug.println(0, "testGetMappingValuesByGidsAndMarkerNames(" + gids + ", " + markerNames + ") RESULTS: " + mappingValues);
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
        Debug.println(0, "testGetAllelicValuesByGidsAndMarkerNames(" + gids + ", " + markerNames + ") RESULTS: " + allelicValues);
    }

    @Test
    public void testGetAllelicValuesFromCharValuesByDatasetId() throws Exception {
        Integer datasetId = Integer.valueOf(2);
        long count = manager.countAllelicValuesFromCharValuesByDatasetId(datasetId);
        List<AllelicValueWithMarkerIdElement> allelicValues = manager.getAllelicValuesFromCharValuesByDatasetId(datasetId, 0, (int) count);
        Debug.println(0, "testGetAllelicValuesFromCharValuesByDatasetId(" + datasetId + ") RESULTS: " + allelicValues.size());
        for(AllelicValueWithMarkerIdElement results : allelicValues) {
        	Debug.println(0, results.toString());
        }
    }

    @Test
    public void testGetAllelicValuesFromAlleleValuesByDatasetId() throws Exception {
        Integer datasetId = Integer.valueOf(2);
        long count = manager.countAllelicValuesFromAlleleValuesByDatasetId(datasetId);
        List<AllelicValueWithMarkerIdElement> allelicValues = manager
                .getAllelicValuesFromAlleleValuesByDatasetId(datasetId, 0, (int) count);
        Debug.println(0, "testGetAllelicValuesFromAlleleValuesByDatasetId(dataset=" + datasetId + ") RESULTS: " + allelicValues.size());
        for(AllelicValueWithMarkerIdElement results : allelicValues) {
        	Debug.println(0, results.toString());
        }
    }

    @Test
    public void testGetAllelicValuesFromMappingPopValuesByDatasetId() throws Exception {
    	//23-Jul-2013, currently no test data with datasetid = 2 in groundnut central, using datasetid=3 instead
        //Integer datasetId = Integer.valueOf(2);
        Integer datasetId = Integer.valueOf(3);
        long count = manager.countAllelicValuesFromMappingPopValuesByDatasetId(datasetId); 
        		//manager.countAllelicValuesFromCharValuesByDatasetId(datasetId);
        List<AllelicValueWithMarkerIdElement> allelicValues = manager.getAllelicValuesFromMappingPopValuesByDatasetId(datasetId, 0,
                (int) count);
        Debug.println(0, "COUNT IS " + count);
        Debug.println(0, "testGetAllelicValuesFromMappingPopValuesByDatasetId(" + datasetId + ") RESULTS: ");
        if (allelicValues != null) {
        	for (AllelicValueWithMarkerIdElement elem : allelicValues) {
        		Debug.println(0, elem.toString());
        	}
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

        List<MarkerIdMarkerNameElement> markerNames = manager.getMarkerNamesByMarkerIds(markerIds);
        Debug.println(0, "testGetMarkerNamesByMarkerIds(" + markerIds + ") RESULTS: ");
        for (MarkerIdMarkerNameElement e : markerNames) {
            Debug.println(0, e.getMarkerId() + " : " + e.getMarkerName());
        }
    }

    @Test
    public void testGetAllMarkerTypes() throws Exception {
        List<String> markerTypes = manager.getAllMarkerTypes(0, 10);
        Debug.println(0, "testGetAllMarkerTypes(0,10) RESULTS: " + markerTypes);
    }

    @Test
    public void testCountAllMarkerTypesLocal() throws Exception {
        long result = manager.countAllMarkerTypes(Database.LOCAL);
        Debug.println(0, "testCountAllMarkerTypes(Database.LOCAL) RESULTS: " + result);
    }

    @Test
    public void testCountAllMarkerTypesCentral() throws Exception {
        long result = manager.countAllMarkerTypes(Database.CENTRAL);
        Debug.println(0, "testCountAllMarkerTypes(Database.CENTRAL) RESULTS: " + result);
    }

    @Test
    public void testGetMarkerNamesByMarkerType() throws Exception {
        String markerType = "asdf";
        List<String> markerNames = manager.getMarkerNamesByMarkerType(markerType, 1, 10);
        Debug.println(0, "testGetMarkerNamesByMarkerType(" + markerType + ") RESULTS: " + markerNames);
    }

    @Test
    public void testCountMarkerNamesByMarkerType() throws Exception {
        String markerType = "asdf";
        long result = manager.countMarkerNamesByMarkerType(markerType);
        Debug.println(0, "testCountMarkerNamesByMarkerType(" + markerType + ") RESULTS: " + result);
    }

    @Test
    public void testGetMarkerInfoByMarkerName() throws Exception {
        String markerName = "1_0437";
        long count = manager.countMarkerInfoByMarkerName(markerName);
        Debug.println(0, "countMarkerInfoByMarkerName(" + markerName + ")  RESULTS: " + count);
        List<MarkerInfo> results = manager.getMarkerInfoByMarkerName(markerName, 0, (int) count);
        Debug.println(0, "testGetMarkerInfoByMarkerName(" + markerName + ") RESULTS: " + results);
    }

    @Test
    public void testGetMarkerInfoByGenotype() throws Exception {
        String genotype = "";
        long count = manager.countMarkerInfoByGenotype(genotype);
        Debug.println(0, "countMarkerInfoByGenotype(" + genotype + ") RESULTS: " + count);
        List<MarkerInfo> results = manager.getMarkerInfoByGenotype(genotype, 0, (int) count);
        Debug.println(0, "testGetMarkerInfoByGenotype(" + genotype + ") RESULTS: " + results);
    }

    @Test
    public void testGetMarkerInfoByDbAccessionId() throws Exception {
        String dbAccessionId = "";
        long count = manager.countMarkerInfoByDbAccessionId(dbAccessionId);
        Debug.println(0, "countMarkerInfoByDbAccessionId(" + dbAccessionId + ")  RESULTS: " + count);
        List<MarkerInfo> results = manager.getMarkerInfoByDbAccessionId(dbAccessionId, 0, (int) count);
        Debug.println(0, "testGetMarkerInfoByDbAccessionId(" + dbAccessionId + ")  RESULTS: " + results);
    }

    @Test
    public void testGetGidsFromCharValuesByMarkerId() throws Exception {
        Integer markerId = 1;
        List<Integer> gids = manager.getGIDsFromCharValuesByMarkerId(markerId, 1, 10);
        Debug.println(0, "testGetGidsFromCharValuesByMarkerId(" + markerId + ") RESULTS: " + gids);
    }

    @Test
    public void testCountGidsFromCharValuesByMarkerId() throws Exception {
        Integer markerId = 1;
        long result = manager.countGIDsFromCharValuesByMarkerId(markerId);
        Debug.println(0, "testCountGidsFromCharValuesByMarkerId(" + markerId + ") RESULTS: " + result);
    }

    @Test
    public void testGetGidsFromAlleleValuesByMarkerId() throws Exception {
        Integer markerId = 1;
        List<Integer> gids = manager.getGIDsFromCharValuesByMarkerId(markerId, 1, 10);
        Debug.println(0, "testGetGidsFromAlleleValuesByMarkerId(" + markerId + ") RESULTS: " + gids);
    }

    @Test
    public void testCountGidsFromAlleleValuesByMarkerId() throws Exception {
        Integer markerId = 1;
        long result = manager.countGIDsFromCharValuesByMarkerId(markerId);
        Debug.println(0, "testCountGidsFromAlleleValuesByMarkerId(" + markerId + ") RESULTS: " + result);
    }

    @Test
    public void testGetGidsFromMappingPopValuesByMarkerId() throws Exception {
        Integer markerId = 1;
        List<Integer> gids = manager.getGIDsFromMappingPopValuesByMarkerId(markerId, 1, 10);
        Debug.println(0, "testGetGidsFromMappingPopValuesByMarkerId(" + markerId + ") RESULTS: " + gids);
    }

    @Test
    public void testCountGidsFromMappingPopValuesByMarkerId() throws Exception {
        Integer markerId = 1;
        long result = manager.countGIDsFromMappingPopValuesByMarkerId(markerId);
        Debug.println(0, "testCountGidsFromMappingPopValuesByMarkerId(" + markerId + ") RESULTS: " + result);
    }

    @Test
    public void testGetAllDbAccessionIdsFromMarker() throws Exception {
        List<String> dbAccessionIds = manager.getAllDbAccessionIdsFromMarker(1, 10);
        Debug.println(0, "testGetAllDbAccessionIdsFromMarker(1,10) RESULTS: " + dbAccessionIds);
    }

    @Test
    public void testCountAllDbAccessionIdsFromMarker() throws Exception {
        long result = manager.countAllDbAccessionIdsFromMarker();
        Debug.println(0, "testCountAllDbAccessionIdsFromMarker() RESULTS: " + result);
    }

    @Test
    public void testGetNidsFromAccMetadatasetByDatasetIds() throws Exception {
        List<Integer> datasetIds = new ArrayList<Integer>();
        datasetIds.add(-1);
        datasetIds.add(-2);
        datasetIds.add(-3);

        List<Integer> gids = new ArrayList<Integer>();
        gids.add(-2);

        List<Integer> nids = manager.getNidsFromAccMetadatasetByDatasetIds(datasetIds, 0, 10);
        List<Integer> nidsWithGidFilter = manager.getNidsFromAccMetadatasetByDatasetIds(datasetIds, gids, 0, 10);

        Debug.println(0, "testGetNidsFromAccMetadatasetByDatasetIds RESULTS: " + nids);
        Debug.println(0, "testGetNidsFromAccMetadatasetByDatasetIds with gid filter RESULTS: " + nidsWithGidFilter);
    }

    @Test
    public void testGetDatasetIdsForFingerPrinting() throws Exception {
        List<Integer> datasetIds = manager.getDatasetIdsForFingerPrinting(0, (int) manager.countDatasetIdsForFingerPrinting());
        Debug.println(0, "testGetDatasetIdsForFingerPrinting() RESULTS: " + datasetIds);
    }

    @Test
    public void testCountDatasetIdsForFingerPrinting() throws Exception {
        long count = manager.countDatasetIdsForFingerPrinting();
        Debug.println(0, "testCountDatasetIdsForFingerPrinting() RESULTS: " + count);
    }

    @Test
    public void testGetDatasetIdsForMapping() throws Exception {
        List<Integer> datasetIds = manager.getDatasetIdsForMapping(0, (int) manager.countDatasetIdsForMapping());
        Debug.println(0, "testGetDatasetIdsForMapping() RESULTS: " + datasetIds);
    }

    @Test
    public void testCountDatasetIdsForMapping() throws Exception {
        long count = manager.countDatasetIdsForMapping();
        Debug.println(0, "testCountDatasetIdsForMapping() RESULTS: " + count);
    }

    @Test
    public void testGetGdmsAccMetadatasetByGid() throws Exception {
        List<Integer> germplasmIds = new ArrayList<Integer>();
        germplasmIds.add(Integer.valueOf(956)); // Crop Tested: Groundnut
        germplasmIds.add(Integer.valueOf(1042));
        germplasmIds.add(Integer.valueOf(-2213));
        germplasmIds.add(Integer.valueOf(-2215));
        List<AccMetadataSetPK> accMetadataSets = manager.getGdmsAccMetadatasetByGid(germplasmIds, 0, 
                (int) manager.countGdmsAccMetadatasetByGid(germplasmIds));
        Debug.println(0, "testGetGdmsAccMetadatasetByGid() RESULTS: ");
        for (AccMetadataSetPK accMetadataSet : accMetadataSets) {
            Debug.println(0, accMetadataSet.toString());
        }

    }

    @Test
    public void testCountGdmsAccMetadatasetByGid() throws Exception { 
        List<Integer> germplasmIds = new ArrayList<Integer>(); 
        germplasmIds.add(Integer.valueOf(956)); // Crop Tested: Groundnut
        germplasmIds.add(Integer.valueOf(1042));
        germplasmIds.add(Integer.valueOf(-2213));
        germplasmIds.add(Integer.valueOf(-2215));
        long count = manager.countGdmsAccMetadatasetByGid(germplasmIds);
        Debug.println(0, "testCountGdmsAccMetadatasetByGid() RESULTS: " + count);
    }

    @Test
    public void testGetMarkersByGidAndDatasetIds() throws Exception {
        Integer gid = Integer.valueOf(-2215);    // Crop Tested: Groundnut

        List<Integer> datasetIds = new ArrayList<Integer>();
        datasetIds.add(Integer.valueOf(1));
        datasetIds.add(Integer.valueOf(2));

        List<Integer> markerIds = manager.getMarkersByGidAndDatasetIds(gid, datasetIds, 0, 
                (int) manager.countMarkersByGidAndDatasetIds(gid, datasetIds));
        Debug.println(0, "testGetMarkersByGidAndDatasetIds() RESULTS: " + markerIds);
    }

    @Test
    public void testCountMarkersByGidAndDatasetIds() throws Exception { 
        Integer gid = Integer.valueOf(-2215);    // Crop Tested: Groundnut

        List<Integer> datasetIds = new ArrayList<Integer>();
        datasetIds.add(Integer.valueOf(1));
        datasetIds.add(Integer.valueOf(2));

        long count = manager.countMarkersByGidAndDatasetIds(gid, datasetIds);
        Debug.println(0, "testCountGdmsAccMetadatasetByGid() RESULTS: " + count);
    }
    
    @Test
    public void testCountAlleleValuesByGids() throws Exception { 
        List<Integer> germplasmIds = new ArrayList<Integer>(); 
        germplasmIds.add(Integer.valueOf(956)); // Crop Tested: Groundnut
        germplasmIds.add(Integer.valueOf(1042));
        germplasmIds.add(Integer.valueOf(-2213));
        germplasmIds.add(Integer.valueOf(-2215));
        long count = manager.countAlleleValuesByGids(germplasmIds);
        Debug.println(0, "testCountAlleleValuesByGids() RESULTS: " + count);
    }
    

    @Test
    public void testCountCharValuesByGids() throws Exception { 
        List<Integer> germplasmIds = new ArrayList<Integer>(); 
        germplasmIds.add(Integer.valueOf(956)); // Please replace the gids found in the target crop to be used in testing
        germplasmIds.add(Integer.valueOf(1042));
        germplasmIds.add(Integer.valueOf(-2213));
        germplasmIds.add(Integer.valueOf(-2215));
        long count = manager.countCharValuesByGids(germplasmIds);
        Debug.println(0, "testCountCharValuesByGids() RESULTS: " + count);
    }
    

    @Test
    public void testGetIntAlleleValuesForPolymorphicMarkersRetrieval() throws Exception {
        List<Integer> germplasmIds = new ArrayList<Integer>(); 
        germplasmIds.add(Integer.valueOf(956)); // Crop Tested: Groundnut
        germplasmIds.add(Integer.valueOf(1042));
        germplasmIds.add(Integer.valueOf(-2213));
        germplasmIds.add(Integer.valueOf(-2215));

        List<AllelicValueElement> results = manager.getIntAlleleValuesForPolymorphicMarkersRetrieval(germplasmIds, 0, 
                (int) manager.countIntAlleleValuesForPolymorphicMarkersRetrieval(germplasmIds));
        Debug.println(0, "testGetIntAlleleValuesForPolymorphicMarkersRetrieval() RESULTS: ");
        for (AllelicValueElement result : results){
        	Debug.println(0, "  " + result);
        }
       Debug.println(0, "Number of record/s: " +results.size() );
       }

    @Test
    public void testCountIntAlleleValuesForPolymorphicMarkersRetrieval() throws Exception { 
        List<Integer> germplasmIds = new ArrayList<Integer>(); 
        germplasmIds.add(Integer.valueOf(956)); // Crop Tested: Groundnut
        germplasmIds.add(Integer.valueOf(1042));
        germplasmIds.add(Integer.valueOf(-2213));
        germplasmIds.add(Integer.valueOf(-2215));
        long count = manager.countIntAlleleValuesForPolymorphicMarkersRetrieval(germplasmIds);
        Debug.println(0, "testCountIntAlleleValuesForPolymorphicMarkersRetrieval() RESULTS: " + count);
    }    
    
    @Test
    public void testGetCharAlleleValuesForPolymorphicMarkersRetrieval() throws Exception {
        List<Integer> germplasmIds = new ArrayList<Integer>(); 
        //For rice db(v20), you can use germplasmIds: 58, 29811
        germplasmIds.add(Integer.valueOf(956)); // Please replace the gids found in the target crop to be used in testing
        germplasmIds.add(Integer.valueOf(1042));
        germplasmIds.add(Integer.valueOf(-2213));
        germplasmIds.add(Integer.valueOf(-2215));

        List<AllelicValueElement> results = manager.getCharAlleleValuesForPolymorphicMarkersRetrieval(germplasmIds, 0, 
                (int) manager.countCharAlleleValuesForPolymorphicMarkersRetrieval(germplasmIds));
        Debug.println(0, "testGetIntAlleleValuesForPolymorphicMarkersRetrieval() RESULTS: ");
        for (AllelicValueElement result : results){
        	Debug.println(0, "  " + result);
        }
       Debug.println(0, "Number of record/s: " +results.size() );
    }


    @Test
    public void testCountCharAlleleValuesForPolymorphicMarkersRetrieval() throws Exception { 
        List<Integer> germplasmIds = new ArrayList<Integer>(); 
        germplasmIds.add(Integer.valueOf(956)); // Please replace the gids found in the target crop to be used in testing
        germplasmIds.add(Integer.valueOf(1042));
        germplasmIds.add(Integer.valueOf(-2213));
        germplasmIds.add(Integer.valueOf(-2215));
        long count = manager.countCharAlleleValuesForPolymorphicMarkersRetrieval(germplasmIds);
        Debug.println(0, "testCountCharAlleleValuesForPolymorphicMarkersRetrieval() RESULTS: " + count);
    }    
    
    @Test
    public void testGetNIdsByDatasetIdsAndMarkerIdsAndNotGIds() throws Exception { 
        List<Integer> gIds = new ArrayList<Integer>(); 
        gIds.add(Integer.valueOf(956)); // Please replace the germplasm ids found in the target crop to be used in testing
        gIds.add(Integer.valueOf(1042));
        gIds.add(Integer.valueOf(1128));
        
        List<Integer> datasetIds = new ArrayList<Integer>(); 
        datasetIds.add(Integer.valueOf(2)); // Please replace the dataset ids found in the target crop to be used in testing
        
        List<Integer> markerIds = new ArrayList<Integer>(); 
        markerIds.add(Integer.valueOf(6)); // Please replace the marker ids found in the target crop to be used in testing
        markerIds.add(Integer.valueOf(10)); 

        List<Integer> nIdList = manager.getNIdsByMarkerIdsAndDatasetIdsAndNotGIds(datasetIds, markerIds, gIds, 
                0, manager.countNIdsByMarkerIdsAndDatasetIdsAndNotGIds(datasetIds, markerIds, gIds));
        Debug.println(0, "testGetNIdsByDatasetIdsAndMarkerIdsAndNotGIds(datasetIds="+datasetIds+") RESULTS: " + nIdList);
        
        nIdList = manager.getNIdsByMarkerIdsAndDatasetIdsAndNotGIds(null, gIds, markerIds, 0, 5);
        Debug.println(0, "testGetNIdsByDatasetIdsAndMarkerIdsAndNotGIds(datasetIds=null) RESULTS: " + nIdList);
        
        nIdList = manager.getNIdsByMarkerIdsAndDatasetIdsAndNotGIds(datasetIds, null, markerIds, 0, 5);
        Debug.println(0, "testGetNIdsByDatasetIdsAndMarkerIdsAndNotGIds(gIds=null) RESULTS: " + nIdList);
        
        nIdList = manager.getNIdsByMarkerIdsAndDatasetIdsAndNotGIds(datasetIds, gIds, null, 0, 5);
        Debug.println(0, "testGetNIdsByDatasetIdsAndMarkerIdsAndNotGIds(markerIds=null) RESULTS: " + nIdList);
        
        
    }  
    
    @Test
    public void testCountNIdsByDatasetIdsAndMarkerIdsAndNotGIds() throws Exception { 
        List<Integer> gIds = new ArrayList<Integer>(); 
        gIds.add(Integer.valueOf(29)); // Please replace the germplasm ids found in the target crop to be used in testing
        gIds.add(Integer.valueOf(303));
        gIds.add(Integer.valueOf(4950));
        
        List<Integer> datasetIds = new ArrayList<Integer>(); 
        datasetIds.add(Integer.valueOf(2)); // Please replace the dataset ids found in the target crop to be used in testing
        
        List<Integer> markerIds = new ArrayList<Integer>(); 
        markerIds.add(Integer.valueOf(6803)); // Please replace the marker ids found in the target crop to be used in testing

        int count = manager.countNIdsByMarkerIdsAndDatasetIdsAndNotGIds(datasetIds, markerIds, gIds);
        Debug.println(0, "testcCountNIdsByDatasetIdsAndMarkerIdsAndNotGIds() RESULTS: " + count);
    }  
    
    @Test
    public void testGetNIdsByDatasetIdsAndMarkerIds() throws Exception { 
        
        List<Integer> datasetIds = new ArrayList<Integer>(); 
        datasetIds.add(Integer.valueOf(2)); // Please replace the dataset ids found in the target crop to be used in testing
        
        List<Integer> markerIds = new ArrayList<Integer>(); 
        markerIds.add(Integer.valueOf(6803)); // Please replace the marker ids found in the target crop to be used in testing

        List<Integer> nIdList = manager.getNIdsByMarkerIdsAndDatasetIds(datasetIds, markerIds, 0, 
                manager.countNIdsByMarkerIdsAndDatasetIds(datasetIds, markerIds));
        Debug.println(0, "testGetNIdsByDatasetIdsAndMarkerIds() RESULTS: " + nIdList);
    }  
    
    @Test
    public void testCountNIdsByDatasetIdsAndMarkerIds() throws Exception { 
        
        List<Integer> datasetIds = new ArrayList<Integer>(); 
        datasetIds.add(Integer.valueOf(2)); // Please replace the dataset ids found in the target crop to be used in testing
        
        List<Integer> markerIds = new ArrayList<Integer>(); 
        markerIds.add(Integer.valueOf(6803)); // Please replace the marker ids found in the target crop to be used in testing

        int count = manager.countNIdsByMarkerIdsAndDatasetIds(datasetIds, markerIds);
        Debug.println(0, "testCountNIdsByDatasetIdsAndMarkerIds() RESULTS: " + count);
    }  

    @Test
    public void testGetMappingAlleleValuesForPolymorphicMarkersRetrieval() throws Exception {
        List<Integer> germplasmIds = new ArrayList<Integer>(); 
        germplasmIds.add(Integer.valueOf(1434)); // Please replace the gids found in the target crop to be used in testing
        germplasmIds.add(Integer.valueOf(1435));

        List<AllelicValueElement> results = manager.getMappingAlleleValuesForPolymorphicMarkersRetrieval(germplasmIds, 0, 
                (int) manager.countMappingAlleleValuesForPolymorphicMarkersRetrieval(germplasmIds));
        Debug.println(0, "testGetMappingAlleleValuesForPolymorphicMarkersRetrieval() RESULTS: ");
        for (AllelicValueElement result : results){
        	Debug.println(0, "  " + result);
        }
       Debug.println(0, "Number of record/s: " +results.size() );
    }


    @Test
    public void testCountMappingAlleleValuesForPolymorphicMarkersRetrieval() throws Exception { 
        List<Integer> germplasmIds = new ArrayList<Integer>(); 
        germplasmIds.add(Integer.valueOf(1434)); // Please replace the gids found in the target crop to be used in testing
        germplasmIds.add(Integer.valueOf(1435));
        long count = manager.countMappingAlleleValuesForPolymorphicMarkersRetrieval(germplasmIds);
        Debug.println(0, "testCountMappingAlleleValuesForPolymorphicMarkersRetrieval() RESULTS: " + count);
    }    
    
    @Test
    public void testGetAllQtl() throws Exception {
        List<Qtl> qtls = manager.getAllQtl(0, (int) manager.countAllQtl());
        Debug.println(0, "testGetAllQtl() RESULTS: " + qtls.size());
        for (Qtl qtl : qtls){
            Debug.println(0, "    " + qtl);
        }
    }

    @Test
    public void testCountAllQtl() throws Exception {
        long result = manager.countAllQtl();
        Debug.println(0, "testCountAllQtl() RESULTS: " + result);
    }
    
    @Test
    public void testCountQtlIdByName() throws Exception { 
        String qtlName = "SLA%";     // Crop tested: Groundnut
        long count = manager.countQtlIdByName(qtlName);
        Debug.println(0, "testCountQtlIdByName() RESULTS: " + count);
    }

    @Test
    public void testGetQtlIdByName() throws Exception {
        String qtlName = "SLA%";     // Crop tested: Groundnut

        List<Integer> results = manager.getQtlIdByName(qtlName, 0, 
                (int) manager.countQtlIdByName(qtlName));
        Debug.println(0, "testGetQtlIdByName() RESULTS: " + results);
    }
    
    @Test
    public void testGetQtlByName() throws Exception {
        String qtlName = "HI Control%";     // Crop tested: Groundnut

        List<QtlDetailElement> results = manager.getQtlByName(qtlName, 0, 
                (int) manager.countQtlByName(qtlName));
        Debug.println(0, "testGetQtlByName() RESULTS: " + results);
    } 

    @Test
    public void testCountQtlByName() throws Exception { 
        String qtlName = "HI Control%";     // Crop tested: Groundnut
        long count = manager.countQtlByName(qtlName);
        Debug.println(0, "testCountQtlByName() RESULTS: " + count);
    }    


    @Test
    public void testGetQtlByTrait() throws Exception {
        Integer qtlTraitId = 1001; // "SL%";     // Crop tested: Groundnut
        List<Integer> results = manager.getQtlByTrait(qtlTraitId, 0, 
                (int) manager.countQtlByTrait(qtlTraitId));
        Debug.println(0, "testGetQtlByTrait() RESULTS: " + results);
    }


    @Test
    public void testCountQtlByTrait() throws Exception { 
        Integer qtlTraitId = 1001; // "SL%";     // Crop tested: Groundnut
        long count = manager.countQtlByTrait(qtlTraitId);
        Debug.println(0, "testCountQtlByTrait() RESULTS: " + count);
    }    
    
    @Test
    public void testGetQtlTraitsByDatasetId() throws Exception {
    	Integer datasetId = 7;		// Crop tested: Groundnut
        List<Integer> results = manager.getQtlTraitsByDatasetId(datasetId, 0, 
                (int) manager.countQtlTraitsByDatasetId(datasetId));
        Debug.println(0, "testGetQtlTraitsByDatasetId() RESULTS: " + results);
    }


    @Test
    public void testCountQtlTraitsByDatasetId() throws Exception { 
    	Integer datasetId = 7;		// Crop tested: Groundnut
        long count = manager.countQtlTraitsByDatasetId(datasetId);
        Debug.println(0, "testCountQtlTraitsByDatasetId() RESULTS: " + count);
    }    
    
    @Test
    public void testGetMapIdsByQtlName() throws Exception { 
        String qtlName = "HI Control 08_AhI";     // Crop tested: Groundnut
        List<Integer> results = manager.getMapIdsByQtlName(qtlName, 0, (int) manager.countMapIdsByQtlName(qtlName));
        Debug.println(0, "testGetMapIdsByQtlName() RESULTS: " + results);
    }

    @Test
    public void testCountMapIdsByQtlName() throws Exception { 
        String qtlName = "HI Control 08_AhI";     // Crop tested: Groundnut
        long count = manager.countMapIdsByQtlName(qtlName);
        Debug.println(0, "testCountMapIdsByQtlName() RESULTS: " + count);
    }


    @Test
    public void testGetMarkersByQtl() throws Exception { 
        String qtlName = "HI Control 08_AhI";     // Crop tested: Groundnut
        String chromosome = "LG01";
        int min = 0;
        int max = 10;
        List<Integer> results = manager.getMarkerIdsByQtl(qtlName, chromosome, min, max, 0, (int) manager.countMarkerIdsByQtl(qtlName, chromosome, min, max));
        Debug.println(0, "testGetMarkersByQtl() RESULTS: " + results);
    }

    @Test
    public void testCountMarkersByQtl() throws Exception { 
        String qtlName = "HI Control 08_AhI";     // Crop tested: Groundnut
        String chromosome = "LG01";
        int min = 0;
        int max = 10;
        long count = manager.countMarkerIdsByQtl(qtlName, chromosome, min, max);
        Debug.println(0, "testCountMarkersByQtl() RESULTS: " + count);

    }


    @Test
    public void getAllParentsFromMappingPopulation() throws Exception {
    	int start = 0;
    	int end = 10;
        List<ParentElement> parentElements = manager.getAllParentsFromMappingPopulation(start, end);
        Debug.println(0, "getAllParentsFromMappingPopulation(" + start + "," + end + ")");
        for (ParentElement parentElement : parentElements) {
            Debug.println(0, "Parent A NId: " + parentElement.getParentANId() + "  |  Parent B NId: "+parentElement.getParentBGId());
        }
        
    }    

    @Test
    public void countAllParentsFromMappingPopulation() throws Exception {
        long parentElementsCount = manager.countAllParentsFromMappingPopulation();
        Debug.println(0, "countAllParentsFromMappingPopulation()");
        Debug.println(0, "Count: " + parentElementsCount);
    }
    
    @Test
    public void getMapDetailsByName() throws Exception {
        String nameLike = "tag%";
        int start = 0;
        int end = 10;
        List<MapDetailElement> maps = manager.getMapDetailsByName(nameLike, start, end);
        Debug.println(0, "getMapDetailsByName('"+nameLike+"'," + start + "," + end + ")");
        for (MapDetailElement map : maps) {
            Debug.println(0, "Map: " + map.getMarkerCount() + "  |  maxStartPosition: " + map.getMaxStartPosition() + "  |  linkageGroup: " + map.getLinkageGroup() + "  |  mapName: " + map.getMapName() + "  |  mapType:  " + map.getMapType());
        }
        
    }    

    @Test
    public void countMapDetailsByName() throws Exception {
        String nameLike = "tag%";
        Long parentElementsCount = manager.countMapDetailsByName(nameLike);
        Debug.println(0, "countMapDetailsByName('"+nameLike+"')");
        Debug.println(0, "Count: " + parentElementsCount);
    }

    @Test
    public void testGetAllMapDetails() throws Exception {
        int start = 0;
        int end = (int) manager.countAllMapDetails();
        List<MapDetailElement> maps = manager.getAllMapDetails(start, end);
        Debug.println(0, "testGetAllMapDetails(" + start + "," + end + ")");
        for (MapDetailElement map : maps) {
            Debug.println(0, map.toString());
        }
        
    }    

    @Test
    public void testCountAllMapDetails() throws Exception {
        Debug.println(0, "testCountAllMapDetails(): " + manager.countAllMapDetails());
    }

    @Test
    public void testAddQtlDetails() throws Exception {
        Integer qtlId = -1;          // Crop tested: Groundnut
        Integer mapId = -2; 
        Float minPosition = 0f; 
        Float maxPosition = 8f; 
        Integer traitId = 1001; // "DE"; 
        String experiment = ""; 
        Float effect =0f;
        Float scoreValue = 2.5f;
        Float rSquare = 10f; 
        String linkageGroup = "LG06"; 
        String interactions = ""; 
        String leftFlankingMarker = "Ah4-101";
        String rightFlankingMarker = "GM2536"; 
        Float position = 34.71f; 
        Float clen = 0f; 
        String seAdditive = null; 
        String hvParent = null; 
        String hvAllele = null; 
        String lvParent = null;
        String lvAllele = null;
                
        QtlDetails qtlDetails = new QtlDetails(qtlId, mapId, minPosition, maxPosition, traitId, experiment, effect,
                scoreValue, rSquare, linkageGroup, interactions, leftFlankingMarker,
                rightFlankingMarker, position, clen, seAdditive, hvParent, hvAllele, lvParent, lvAllele);
        
        QtlDetailsPK idAdded = manager.addQtlDetails(qtlDetails);
        Debug.println(0, "testAddQtlDetails() Added: " + (idAdded != null ? qtlDetails : null));
    }


    @Test
    public void testAddMarkerDetails() throws Exception {
        Integer markerId = -1;           // Replace with desired values
        Integer noOfRepeats = 0; 
        String motifType = ""; 
        String sequence = ""; 
        Integer sequenceLength = 0;
        Integer minAllele = 0; 
        Integer maxAllele = 0; 
        Integer ssrNr = 0; 
        Float forwardPrimerTemp = 0f; 
        Float reversePrimerTemp = 0f; 
        Float elongationTemp = 0f;
        Integer fragmentSizeExpected = 0; 
        Integer fragmentSizeObserved = 0; 
        Integer expectedProductSize = 0; 
        Integer positionOnReferenceSequence = 0;
        String restrictionEnzymeForAssay = null;
        
        MarkerDetails markerDetails = new MarkerDetails(markerId, noOfRepeats, motifType, sequence, sequenceLength,
                minAllele, maxAllele, ssrNr, forwardPrimerTemp, reversePrimerTemp, elongationTemp,
                fragmentSizeExpected, fragmentSizeObserved, expectedProductSize, positionOnReferenceSequence,
                restrictionEnzymeForAssay);
        
        Integer idAdded = manager.addMarkerDetails(markerDetails);
        Debug.println(0, "testAddMarkerDetails() Added: " + (idAdded != null ? markerDetails : null));
    }


    @Test
    public void testAddMarkerUserInfo() throws Exception {
        Integer markerId = -1;           // Replace with desired values
        String principalInvestigator = "Juan Dela Cruz";
        String contact = "juan@irri.com.ph";
        String institute = "IRRI";
        
        MarkerUserInfo markerUserInfo = new MarkerUserInfo(markerId, principalInvestigator, contact, institute);
        
        Integer idAdded = manager.addMarkerUserInfo(markerUserInfo);
        Debug.println(0, "testAddMarkerUserInfo() Added: " + (idAdded != null ? markerUserInfo : null));
    }
    
    @Test
    public void testAddAccMetadataSet() throws Exception {
        Integer datasetId = -1;  // Crop tested: Groundnut
        Integer germplasmId = 1; 
        Integer nameId = 1;
        
        AccMetadataSet accMetadataSet = new AccMetadataSet(datasetId, germplasmId, nameId);
        
        AccMetadataSetPK idAdded = manager.addAccMetadataSet(accMetadataSet);
        Debug.println(0, "testAccMetadataSet() Added: " + (idAdded != null ? accMetadataSet : null));
    }

    @Test
    public void testAddMarkerMetadataSet() throws Exception {
        Integer datasetId = -1;  // Crop tested: Groundnut
        Integer markerId = -1; 
        
        MarkerMetadataSet markerMetadataSet = new MarkerMetadataSet(datasetId, markerId);
        
        MarkerMetadataSetPK idAdded = manager.addMarkerMetadataSet(markerMetadataSet);
        Debug.println(0, "testAddMarkerMetadataSet() Added: " + (idAdded != null ? markerMetadataSet : null));
    }

    @Test
    public void testAddDataset() throws Exception {
    	Dataset dataset = createDataset();
    	manager.addDataset(dataset);
        Debug.println(0, "testAddDataset() Added: " + (dataset.getDatasetId() != null ? dataset : null));
    }
    
    private Dataset createDataset() throws Exception{
        Integer datasetId = null;       // Crop tested: Groundnut
        String datasetName = " QTL_ ICGS 44 X ICGS 78";
        String datasetDesc = "ICGS 44 X ICGS 78";
        String datasetType = "QTL";
        String genus = "Groundnut"; 
        String species = ""; 
        Date uploadTemplateDate = new Date(System.currentTimeMillis()); 
        String remarks = ""; 
        String dataType = "int"; 
        String missingData = null;
        String method = null;
        String score = null;
        String institute = null;
        String principalInvestigator = null;
        String email = null;
        String purposeOfStudy = null;
        
        return new Dataset(datasetId, datasetName, datasetDesc, datasetType, genus, species, uploadTemplateDate, remarks,
                dataType, missingData, method, score, institute, principalInvestigator, email, purposeOfStudy);
    }


    @Test
    public void testAddGDMSMarker() throws Exception {
    	
    	Integer markerId = 0;
        String markerType = "SSR";
        String markerName = "SeqTEST";
        String species = "Groundnut";
        String dbAccessionId = null;
        String reference = null;
        String genotype = null;
        String ploidy = null; 
        String primerId = null;
        String remarks = null;
        String assayType = null;
        String motif = null;
        String forwardPrimer = null;
        String reversePrimer = null;
        String productSize = null;
        Float annealingTemp = Float.valueOf(0);
        String amplification = null;
        
        Marker marker = new Marker(markerId, markerType, markerName, species, dbAccessionId, reference, genotype, ploidy, primerId, remarks, assayType, motif, forwardPrimer, reversePrimer, productSize, annealingTemp, amplification);
        
        Integer idAdded = manager.addGDMSMarker(marker);
        Debug.println(0, "testAddGDMSMarker() Added: " + (idAdded != null ? marker : null));
    }

    @Test
    public void testAddGDMSMarkerAlias() throws Exception {
  	
        Integer markerId = -1;
        String alias = "testalias";
      
        MarkerAlias markerAlias = new MarkerAlias(markerId, alias);
      
        Integer idAdded = manager.addGDMSMarkerAlias(markerAlias);
        Debug.println(0, "testAddGDMSMarkerAlias() Added: " + (idAdded != null ? markerAlias : null));
    }
    
    @Test
    public void testAddDatasetUser() throws Exception {
  	
        Integer datasetId = -1;
        Integer userId = 123;
      
        DatasetUsers datasetUser = new DatasetUsers(datasetId, userId);
      
        Integer idAdded = manager.addDatasetUser(datasetUser);
        Debug.println(0, "testAddDatasetUser() Added: " + (idAdded != null ? datasetUser : null));
    }  

    @Test
    public void testAddAlleleValues() throws Exception {
        Integer anId = null;                // Crop tested: Groundnut
        Integer datasetId = -1; 
        Integer gId = 1920; 
        Integer markerId = 1037; 
        String alleleBinValue = "alleleBinValue"; 
        String alleleRawValue = "alleleRawValue";
        Integer peakHeight = 10;
      
        AlleleValues alleleValues = new AlleleValues(anId, datasetId, gId, markerId, alleleBinValue, alleleRawValue, peakHeight);
      
        Integer idAdded = manager.addAlleleValues(alleleValues);
        Debug.println(0, "testAddAlleleValues() Added: " + (idAdded != null ? alleleValues : null));
    }  
  
    @Test
    public void testAddCharValues() throws Exception {
        Integer acId = null;            // Crop tested: Groundnut
        Integer datasetId = -1; 
        Integer gId = 1920; 
        Integer markerId = 1037; 
        String charValue = "CV"; 
      
        CharValues charValues = new CharValues(acId, datasetId, markerId, gId, charValue);
      
        Integer idAdded = manager.addCharValues(charValues);
        Debug.println(0, "testAddCharValues() Added: " + (idAdded != null ? charValues : null));
    }  

    @Test
    public void testAddMappingPop() throws Exception {
  	
    	Integer datasetId = -1;
        String mappingType = "test";
        Integer parentAGId = 956;
        Integer parentBGId = 1042;
        Integer populationSize = 999;
        String populationType = "test";
        String mapDataDescription = "test";
        String scoringScheme = "test";
        Integer mapId = 0;
      
        MappingPop mappingPop = new MappingPop(datasetId, mappingType, parentAGId, parentBGId, populationSize, populationType, mapDataDescription, scoringScheme, mapId);
      
        Integer idAdded = manager.addMappingPop(mappingPop);
        Debug.println(0, "testAddMappingPop() Added: " + (idAdded != null ? mappingPop : null));
    }    
    
    @Test
    public void testAddMappingPopValue() throws Exception {
  	
    	Integer mpId = null;
        String mapCharValue = "X";
        Integer datasetId = -1;
        Integer gid = 1434;
        Integer markerId = 2537;
      
        MappingPopValues mappingPopValue = new MappingPopValues(mpId, mapCharValue, datasetId, gid, markerId);
      
        Integer idAdded = manager.addMappingPopValue(mappingPopValue);
        Debug.println(0, "testAddMappingPopValue() Added: " + (idAdded != null ? mappingPopValue : null));
    }    

    
    @Test
    public void testAddMarkerOnMap() throws Exception {
  	
    	Integer mapId = -2;
    	Integer markerId = -6;
    	Float startPosition = Float.valueOf("123.4");
    	Float endPosition = Float.valueOf("567.8");
//    	String mapUnit = "TS";
    	String linkageGroup = "Test";
      
        MarkerOnMap markerOnMap = new MarkerOnMap(mapId, markerId, startPosition, endPosition, linkageGroup);

        try{
        	Integer idAdded = manager.addMarkerOnMap(markerOnMap);
            Debug.println(0, "testAddMarkerOnMap() Added: " + (idAdded != null ? markerOnMap : null));
        }catch(MiddlewareQueryException e){
        	if (e.getMessage().contains("Map Id not found")){
        		Debug.println(0, "Foreign key constraint violation: Map Id not found");
        	}
        }
    }    
    
    @Test
    public void testAddDartValue() throws Exception {
  	
    	Integer adId = null;
    	Integer datasetId = -6;
    	Integer markerId = -1;
    	Integer cloneId = -1;
    	Float qValue = Float.valueOf("123.45");
    	Float reproducibility = Float.valueOf("543.21");
    	Float callRate = Float.valueOf("12.3");
    	Float picValue = Float.valueOf("543.2");
    	Float discordance = Float.valueOf("1.2");
    	
        DartValues dartValue = new DartValues(adId, datasetId, markerId, cloneId, qValue, reproducibility, callRate, picValue, discordance);
      
        Integer idAdded = manager.addDartValue(dartValue);
        Debug.println(0, "testAddDartValue() Added: " + (idAdded != null ? dartValue : null));
    }    
    
    @Test
    public void testAddQtl() throws Exception {
    
        Integer qtlId = null;
        String qtlName = "TestQTL";
        Integer datasetId = -6;
        
        Qtl qtl = new Qtl(qtlId, qtlName, datasetId);
      
        Integer idAdded = manager.addQtl(qtl);
        Debug.println(0, "testAddQtl() Added: " + (idAdded != null ? qtl : null));
    }    

    @Test
    public void testAddMap() throws Exception {
        Integer mapId = null;
        String mapName = "ICGS 44 X ICGS 76";
        String mapType = "genetic";
        Integer mpId = 0;
        String mapDesc = null;
        String mapUnit = null;        		
        
        Map map = new Map(mapId, mapName, mapType, mpId, mapDesc, mapUnit);
      
        Integer idAdded = manager.addMap(map);
        Debug.println(0, "testAddMap() Added: " + (idAdded != null ? map : null));
    }    

    @Test
    public void testSetSSRMarkers() throws Exception {
    	List<Object> markerRecords = createMarkerMarkeRecords();
        Marker marker = (Marker) markerRecords.get(0);
        MarkerAlias markerAlias = (MarkerAlias) markerRecords.get(1);
        MarkerDetails markerDetails = (MarkerDetails) markerRecords.get(2);
        MarkerUserInfo markerUserInfo = (MarkerUserInfo) markerRecords.get(3);
        
        Boolean addStatus = manager.setSSRMarkers(marker, markerAlias, markerDetails, markerUserInfo);
        if (addStatus){
            Debug.println(0, "testSetSSRMarkers() Added: ");
            Debug.println(3, marker.toString());
            Debug.println(3, markerAlias.toString());
            Debug.println(3, markerDetails.toString());
            Debug.println(3, markerUserInfo.toString());
        }
    }
    
	private List<Object> createMarkerMarkeRecords() {

    	Integer markerId = null; //Will be set/overridden by the function
        String markerType = null; //Will be set/overridden by the function
        String markerName = "SeqTEST";
        String species = "Groundnut";
        String dbAccessionId = null;
        String reference = null;
        String genotype = null;
        String ploidy = null; 
        String primerId = null;
        String remarks = null;
        String assayType = null;
        String motif = null;
        String forwardPrimer = null;
        String reversePrimer = null;
        String productSize = null;
        Float annealingTemp = Float.valueOf(0);
        String amplification = null;

        String alias = "testalias";
        Integer noOfRepeats = 0; 
        String motifType = ""; 
        String sequence = ""; 
        Integer sequenceLength = 0;
        Integer minAllele = 0; 
        Integer maxAllele = 0; 
        Integer ssrNr = 0; 
        Float forwardPrimerTemp = 0f; 
        Float reversePrimerTemp = 0f; 
        Float elongationTemp = 0f;
        Integer fragmentSizeExpected = 0; 
        Integer fragmentSizeObserved = 0; 
        Integer expectedProductSize = 0; 
        Integer positionOnReferenceSequence = 0;
        String restrictionEnzymeForAssay = null;
        String principalInvestigator = "Juan Dela Cruz";
        String contact = "juan@irri.com.ph";
        String institute = "IRRI";
        
        Marker marker = new Marker(markerId, markerType, markerName, species, dbAccessionId, reference, genotype, ploidy, primerId, remarks, assayType, motif, forwardPrimer, reversePrimer, productSize, annealingTemp, amplification);
        MarkerAlias markerAlias = new MarkerAlias(markerId, alias);
        MarkerDetails markerDetails = new MarkerDetails(markerId, noOfRepeats, motifType, sequence, sequenceLength,
                minAllele, maxAllele, ssrNr, forwardPrimerTemp, reversePrimerTemp, elongationTemp,
                fragmentSizeExpected, fragmentSizeObserved, expectedProductSize, positionOnReferenceSequence,
                restrictionEnzymeForAssay);
        MarkerUserInfo markerUserInfo = new MarkerUserInfo(markerId, principalInvestigator, contact, institute);
        
        List<Object> markerRecords = new ArrayList<Object>();
        markerRecords.add(marker);
        markerRecords.add(markerAlias);
        markerRecords.add(markerDetails);
        markerRecords.add(markerUserInfo);
        return markerRecords;
        
    }
    
    @Test
    public void testSetSNPMarkers() throws Exception {
    	List<Object> markerRecrods = createMarkerMarkeRecords();
        Marker marker = (Marker) markerRecrods.get(0);
        MarkerAlias markerAlias = (MarkerAlias) markerRecrods.get(1);
        MarkerDetails markerDetails = (MarkerDetails) markerRecrods.get(2);
        MarkerUserInfo markerUserInfo = (MarkerUserInfo) markerRecrods.get(3);

        Boolean addStatus = manager.setSNPMarkers(marker, markerAlias, markerDetails, markerUserInfo);
        if (addStatus){
            Debug.println(0, "testSetSNPMarkers() Added: ");
            Debug.println(3, marker.toString());
            Debug.println(3, markerAlias.toString());
            Debug.println(3, markerDetails.toString());
            Debug.println(3, markerUserInfo.toString());
        }
    }

    @Test
    public void testSetCAPMarkers() throws Exception {
    	List<Object> markerRecrods = createMarkerMarkeRecords();
        Marker marker = (Marker) markerRecrods.get(0);
        MarkerAlias markerAlias = (MarkerAlias) markerRecrods.get(1);
        MarkerDetails markerDetails = (MarkerDetails) markerRecrods.get(2);
        MarkerUserInfo markerUserInfo = (MarkerUserInfo) markerRecrods.get(3);

        Boolean addStatus = manager.setCAPMarkers(marker, markerAlias, markerDetails, markerUserInfo);
        if (addStatus){
            Debug.println(0, "testSetCAPMarkers() Added: ");
            Debug.println(3, marker.toString());
            Debug.println(3, markerAlias.toString());
            Debug.println(3, markerDetails.toString());
            Debug.println(3, markerUserInfo.toString());
        }
    }

    @Test
    public void testSetCISRMarkers() throws Exception {
    	List<Object> markerRecrods = createMarkerMarkeRecords();
        Marker marker = (Marker) markerRecrods.get(0);
        MarkerAlias markerAlias = (MarkerAlias) markerRecrods.get(1);
        MarkerDetails markerDetails = (MarkerDetails) markerRecrods.get(2);
        MarkerUserInfo markerUserInfo = (MarkerUserInfo) markerRecrods.get(3);

        Boolean addStatus = manager.setCISRMarkers(marker, markerAlias, markerDetails, markerUserInfo);
        if (addStatus){
            Debug.println(0, "testSetCISRMarkers() Added: ");
            Debug.println(3, marker.toString());
            Debug.println(3, markerAlias.toString());
            Debug.println(3, markerDetails.toString());
            Debug.println(3, markerUserInfo.toString());
        }
    }
    
    
    private List<Object> createMappingRecords() throws Exception{
        List<Object> records = new ArrayList<Object>();
        Dataset dataset = createDataset();
        
        //TODO
        
        return records;
    }
    
    
    @Test
    public void testSetQTL() throws Exception {
        Integer datasetId = null; //Will be set/overridden by the function
        Integer userId = 123;
        
        Integer qtlId = null; //Will be set/overridden by the function
        Integer mapId = 1; 
        Float minPosition = 0f; 
        Float maxPosition = 8f; 
        Integer traitId = 1001; //"DE"; 
        String experiment = ""; 
        Float effect =0f;
        Float scoreValue = 2.5f;
        Float rSquare = 10f; 
        String linkageGroup = "LG06"; 
        String interactions = ""; 
        String leftFlankingMarker = "Ah4-101";
        String rightFlankingMarker = "GM2536"; 
        Float position = 34.71f; 
        Float clen = 0f; 
        String seAdditive = null; 
        String hvParent = null; 
        String hvAllele = null; 
        String lvParent = null;
        String lvAllele = null;
                
    	String qtlName = "TestQTL";
    	        
        DatasetUsers datasetUser = new DatasetUsers(datasetId, userId);
        
        Dataset dataset = createDataset();
        
        QtlDetails qtlDetails = new QtlDetails(qtlId, mapId, minPosition, maxPosition, traitId, experiment, effect,
                scoreValue, rSquare, linkageGroup, interactions, leftFlankingMarker,
                rightFlankingMarker, position, clen, seAdditive, hvParent, hvAllele, lvParent, lvAllele);        

        Qtl qtl = new Qtl(qtlId, qtlName, datasetId);
        
        Boolean addStatus = manager.setQTL(datasetUser, dataset, qtlDetails, qtl);
        Debug.println(0, "testSetQTL() Added: " + (addStatus != null ? datasetUser : null) 
                + (addStatus != null ? dataset : null) + (addStatus != null ? qtlDetails : null) + (addStatus != null ? qtl : null));
        if (addStatus){
            Debug.println(0, "testSetQTL() Added: ");
            Debug.println(3, datasetUser.toString()); 
            Debug.println(3, dataset.toString()); 
            Debug.println(3, qtlDetails.toString()); 
            Debug.println(3, qtl.toString()); 
        }

    }
    
    @Test
    public void testSetDart() throws Exception {
        
        // DatasetUser Fields
        Integer datasetId = null; //Will be set/overridden by the function
        Integer userId = 123;

        // AccMetadataSet Additional Fields
        Integer gId = 1; 
        Integer nameId = 1;

        // MarkerMetadataSet Additional Field
        Integer markerId = 1;

        // AlleleValues Additional Fields
        Integer anId = null;     //Will be set/overridden by the function
        String alleleBinValue = "238:238";
        String alleleRawValue = "0.0:0.0";
        Integer peakHeight = 10;
        
        // DartValues Additional Fields
        Integer adId = null;  //Will be set/overridden by the function
        Integer cloneId = 1;
        Float qValue = 0f; 
        Float reproducibility = 0f;
        Float callRate = 0f; 
        Float picValue = 0f; 
        Float discordance = 0f;
        
        Dataset dataset = createDataset();
        
        AccMetadataSet accMetadataSet = new AccMetadataSet(datasetId, gId, nameId);
        
        MarkerMetadataSet markerMetadataSet = new MarkerMetadataSet(datasetId, markerId);
        
        DatasetUsers datasetUser = new DatasetUsers(datasetId, userId);
        
        AlleleValues alleleValues = new AlleleValues(anId, datasetId, gId, markerId, alleleBinValue, alleleRawValue, peakHeight);
        
        DartValues dartValues = new DartValues(adId, datasetId, markerId, cloneId, qValue, reproducibility, callRate, picValue, discordance);
        
        Marker marker = (Marker) createMarkerMarkeRecords().get(0);

        Boolean addStatus = manager.setDart(accMetadataSet, markerMetadataSet, datasetUser, alleleValues, dataset, dartValues, marker);
        if (addStatus){
            Debug.println(0, "testSetDArT() Added: ");
            Debug.println(3, accMetadataSet.toString()); 
            Debug.println(3, markerMetadataSet.toString()); 
            Debug.println(3, datasetUser.toString()); 
            Debug.println(3, alleleValues.toString()); 
            Debug.println(3, dataset.toString()); 
            Debug.println(3, marker.toString()); 
        }
    }
    
    @Test
    public void testSetSSR() throws Exception {
        
        // DatasetUser Fields
        Integer datasetId = null; //Will be set/overridden by the function
        Integer userId = 123;

        // AccMetadataSet Additional Fields
        Integer gId = 1; 
        Integer nameId = 1;

        // MarkerMetadataSet Additional Field
        Integer markerId = 1;

        // AlleleValues Additional Fields
        Integer anId = null;     //Will be set/overridden by the function
        String alleleBinValue = "238:238";
        String alleleRawValue = "0.0:0.0";
        Integer peakHeight = 10;
        
        Dataset dataset = createDataset();        
        
        AccMetadataSet accMetadataSet = new AccMetadataSet(datasetId, gId, nameId);
        
        MarkerMetadataSet markerMetadataSet = new MarkerMetadataSet(datasetId, markerId);
        
        DatasetUsers datasetUser = new DatasetUsers(datasetId, userId);
        
        AlleleValues alleleValues = new AlleleValues(anId, datasetId, gId, markerId, alleleBinValue, alleleRawValue, peakHeight);
        
        Marker marker = (Marker) createMarkerMarkeRecords().get(0);

        Boolean addStatus = manager.setSSR(accMetadataSet, markerMetadataSet, datasetUser, alleleValues, dataset, marker);
        if (addStatus){
            Debug.println(0, "testSetSSR() Added: ");
            Debug.println(3, accMetadataSet.toString()); 
            Debug.println(3, markerMetadataSet.toString()); 
            Debug.println(3, datasetUser.toString()); 
            Debug.println(3, alleleValues.toString()); 
            Debug.println(3, dataset.toString()); 
            Debug.println(3, marker.toString()); 
        }
    }
    
    
    @Test
    public void testSetSNP() throws Exception {
        
        // DatasetUser Fields
        Integer datasetId = null; //Will be set/overridden by the function
        Integer userId = 123;

        // AccMetadataSet Additional Fields
        Integer gId = 1; 
        Integer nameId = 1;

        // MarkerMetadataSet Additional Field
        Integer markerId = 1;

        // charValues Additional Fields
        Integer acId = null;            // Crop tested: Groundnut
        String charValue = "CV"; 
       
        Dataset dataset = createDataset();        
        
        AccMetadataSet accMetadataSet = new AccMetadataSet(datasetId, gId, nameId);
        
        MarkerMetadataSet markerMetadataSet = new MarkerMetadataSet(datasetId, markerId);
        
        DatasetUsers datasetUser = new DatasetUsers(datasetId, userId);
        CharValues charValues = new CharValues(acId, datasetId, markerId, gId, charValue);
     
        Marker marker = (Marker) createMarkerMarkeRecords().get(0);

        Boolean addStatus = manager.setSNP(accMetadataSet, markerMetadataSet, datasetUser, charValues, dataset, marker);
        if (addStatus){
            Debug.println(0, "testSetSNP() Added: ");
            Debug.println(3, accMetadataSet.toString()); 
            Debug.println(3, markerMetadataSet.toString()); 
            Debug.println(3, datasetUser.toString()); 
            Debug.println(3, charValues.toString()); 
            Debug.println(3, dataset.toString()); 
            Debug.println(3, marker.toString()); 
        }
    }
    
    @Test
    public void testSetMappingData() throws Exception {
        // Tested on Groundnut DB

        // DatasetUser Fields
        Integer datasetId = null; //Will be set/overridden by the function
        Integer userId = 123;

        // Dataset Fields
        String datasetName = "Map_Pop GCP-832 Test";
        String datasetDesc = "Map_Pop GCP-832 Test Description";
        String datasetType = "mapping";
        String species = "Groundnut"; 
        String dataType = "map"; 
        String genus = "Groundnut"; 

        // AccMetadataSet Additional Fields
        Integer gId = 1; 
        Integer nameId = 1;

        // MarkerMetadataSet Additional Field
        Integer markerId = 1;

        // MappingPop Additional Fields
        String mappingType = "abh";
        Integer parentAGId = 1035;
        Integer parentBGId = 1036;
        Integer populationSize = 999;
        String populationType = "";
        String mapDataDescription = "Flood resistant";
        String scoringScheme = "";
        Integer mapId = 1;
        
        // MappingPopValues Additional Fields
        Integer mpId = null;  //Will be set/overridden by the function
        String mapCharValue = "-";
        
        Dataset dataset = createDataset();
        dataset.setDatasetName(datasetName);
        dataset.setDatasetDesc(datasetDesc);
        dataset.setDatasetType(datasetType);
        dataset.setDatasetType(datasetType);
        dataset.setSpecies(species);
        dataset.setDataType(dataType);
        dataset.setGenus(genus);
        
        
        AccMetadataSet accMetadataSet = new AccMetadataSet(datasetId, gId, nameId);
        
        MarkerMetadataSet markerMetadataSet = new MarkerMetadataSet(datasetId, markerId);
        
        DatasetUsers datasetUser = new DatasetUsers(datasetId, userId);
        
        MappingPop mappingPop = new MappingPop(datasetId, mappingType, parentAGId, parentBGId, populationSize, populationType, mapDataDescription, scoringScheme, mapId);
        
        MappingPopValues mappingPopValues = new MappingPopValues(mpId, mapCharValue, datasetId, gId, markerId);
        
        Marker marker = (Marker) createMarkerMarkeRecords().get(0);
        
        Boolean addStatus = manager.setMappingData(accMetadataSet, markerMetadataSet, datasetUser, mappingPop, mappingPopValues, dataset, marker);
        if (addStatus){
            Debug.println(0, "testSetMappingData() Added: ");
            Debug.println(3, accMetadataSet.toString()); 
            Debug.println(3, markerMetadataSet.toString()); 
            Debug.println(3, datasetUser.toString()); 
            Debug.println(3, mappingPopValues.toString()); 
            Debug.println(3, dataset.toString()); 
            Debug.println(3, marker.toString()); 
        }

    }

    @Test
    public void testSetMappingAllelicSNP() throws Exception {
        //TODO
    }
        
    @Test
    public void testSetMappingAllelicSSRDArT() throws Exception {
        //TODO
    }
        
        
    
    @Test
    public void testSetMaps() throws Exception {
        
        // Marker Fields
        Integer markerId = null; // Value will be set/overriden by the function 
        String markerType = "UA";
        String markerName = "GCP-833TestMarker";
        String species = "Groundnut";
        
        Marker marker = new Marker();
        marker.setMarkerId(markerId);
        marker.setMarkerType(markerType);
        marker.setMarkerName(markerName);
        marker.setSpecies(species);
        marker.setAnnealingTemp(new Float(0));
        
        // Map Fields
        Integer mapId = null; // Value will be set/overriden by the function
        String mapName = "GCP-833TestMap";
        String mapType = "genetic";
        Integer mpId = 0;
        String mapDesc = null;
        
        // MarkerOnMap Fields
        Float startPosition = new Float(0);
        Float endPosition = new Float(0);
        String mapUnit = "CM";
        String linkageGroup = "LG23";

        Map map = new Map(mapId, mapName, mapType, mpId, mapDesc, mapUnit);

        MarkerOnMap markerOnMap = new MarkerOnMap(mapId, markerId, startPosition, endPosition, linkageGroup);
        
        Boolean addStatus = null;
        try{
	        addStatus = manager.setMaps(marker, markerOnMap, map);
        } catch(MiddlewareQueryException e){
        	if (e.getMessage().contains("The marker on map combination already exists")){
        			Debug.println(0, "The marker on map combination already exists");        		
        	} else {
	        		Debug.println(0, "testSetMaps() Added: " + (addStatus != null ? marker : null) 
	                    + " | " + (addStatus != null ? markerOnMap : null) 
	                    + " | " + (addStatus != null ? map : null));
        	}
        }   
    }
    
    @Test
    public void TestGetMapIDsByQTLName() throws Exception {
        String qtlName = "HI Control 08_AhI";
        long start = System.currentTimeMillis();
        List<Integer> mapIDs = manager.getMapIDsByQTLName(qtlName, 0, 2);

        for (Integer mapID : mapIDs) {
            Debug.println(0, "Map ID: " + mapID);
        }
        
        long end = System.currentTimeMillis();
        Debug.println(0, "TestGetMapIDsByQTLName(" + qtlName + ")");
        Debug.println(0, "  QUERY TIME: " + (end - start) + " ms");
    }
    
    @Test
    public void TestCountMapIDsByQTLName() throws Exception {
        String qtlName = "HI Control 08_AhI";
        long start = System.currentTimeMillis();
        long count = manager.countMapIDsByQTLName(qtlName);

        Debug.println(0, "Count of Map IDs: " + count);
        
        long end = System.currentTimeMillis();
        Debug.println(0, "TestCountMapIDsByQTLName(" + qtlName + ")");
        Debug.println(0, "  QUERY TIME: " + (end - start) + " ms");
    }
    
    @Test
    public void testGetMarkerIDsByMapIDAndLinkageBetweenStartPosition() throws Exception {
        int mapID = 1;
        String linkage = "LG01";
        double startPos = 0;
        double endPos = 2.2;
        long start = System.currentTimeMillis();
        Set<Integer> markerIDs = manager.getMarkerIDsByMapIDAndLinkageBetweenStartPosition(mapID, linkage, startPos, endPos, 0, 1);

        for (Integer markerID : markerIDs) {
            Debug.println(0, "Marker ID: " + markerID);
        }
        
        long end = System.currentTimeMillis();
        Debug.println(0, "  QUERY TIME: " + (end - start) + " ms");
    }
    
    @Test
    public void testCountMarkerIDsByMapIDAndLinkageBetweenStartPosition() throws Exception {
        int mapID = 1;
        String linkage = "LG01";
        double startPos = 0;
        double endPos = 2.2;
        long start = System.currentTimeMillis();
        long count = manager.countMarkerIDsByMapIDAndLinkageBetweenStartPosition(mapID, linkage, startPos, endPos);

        Debug.println(0, "Count of Marker IDs: " + count);
        
        long end = System.currentTimeMillis();
        Debug.println(0, "  QUERY TIME: " + (end - start) + " ms");
    }
    
    @Test
    public void testGetMarkersByMarkerIDs() throws Exception {
        List<Integer> markerIDs = new ArrayList<Integer>();
        markerIDs.add(1317);
        
        long start = System.currentTimeMillis();
        List<Marker> markerList = manager.getMarkersByMarkerIds(markerIDs, 0, 5);

        for (Marker marker : markerList) {
            Debug.println(0, "Marker: " + marker);
        }
        
        long end = System.currentTimeMillis();
        Debug.println(0, "  QUERY TIME: " + (end - start) + " ms");
    }
    
    @Test
    public void testCountMarkersByMarkerIDs() throws Exception {
        List<Integer> markerIDs = new ArrayList<Integer>();
        markerIDs.add(1317);
        long start = System.currentTimeMillis();
        long count = manager.countMarkersByMarkerIds(markerIDs);

        Debug.println(0, "Count of Markers: " + count);
        
        long end = System.currentTimeMillis();
        Debug.println(0, "  QUERY TIME: " + (end - start) + " ms");
    }
    
    @Test
    public void testGetQTLByQTLIDs() throws Exception {
        List<Integer> qtlIDs = new ArrayList<Integer>();
        qtlIDs.add(1);
      
        List<QtlDetailElement> results = manager.getQtlByQtlIds(qtlIDs, 0, 
                (int) manager.countQtlByQtlIds(qtlIDs));
        Debug.println(0, "testGetQtlByQTLIDs() RESULTS: " + results);
    }
   

    @Test
    public void testCountQTLByQTLIDs() throws Exception { 
        List<Integer> qtlIDs = new ArrayList<Integer>();
        qtlIDs.add(1);
        qtlIDs.add(2);
        qtlIDs.add(3);
        long count = manager.countQtlByQtlIds(qtlIDs);
        Debug.println(0, "testCountQtlByQTLIDs() RESULTS: " + count);
    }   
    
    
    @Test
    public void testGetQtlDataByQtlTraits() throws Exception {
        List<Integer> qtlTraits = new ArrayList<Integer>();   // Crop tested: Groundnut
		qtlTraits.add(1001);     // "DE"
        List<QtlDataElement> results = manager.getQtlDataByQtlTraits(qtlTraits, 0, (int) manager.countQtlDataByQtlTraits(qtlTraits)); 
        Debug.println(0, "testGetQtlDataByQtlTraits() RESULTS: " + results.size());
        for (QtlDataElement element : results){
            Debug.println(0, "    " + element);
        }
    }


    @Test
    public void testCountQtlDataByQtlTraits() throws Exception { 
        List<Integer> qtlTraits = new ArrayList<Integer>();   // Crop tested: Groundnut
		qtlTraits.add(1001);     // "DE"
        long count =  manager.countQtlDataByQtlTraits(qtlTraits); 
        Debug.println(0, "testCountQtlDataByQtlTraits() RESULTS: " + count);
    }
    
    
    @Test
    public void testCountAllelicValuesFromAlleleValuesByDatasetId() throws Exception { 
        Integer datasetId = -1; // change value of the datasetid
        long count = manager.countAllelicValuesFromAlleleValuesByDatasetId(datasetId);
        assertNotNull(count);
        Debug.println(0, "testCountAllelicValuesFromAlleleValuesByDatasetId("+datasetId+") Results: " + count);
    }
    
    @Test
    public void testCountAllelicValuesFromCharValuesByDatasetId() throws Exception { 
        Integer datasetId = -1; // change value of the datasetid
        long count = manager.countAllelicValuesFromCharValuesByDatasetId(datasetId);
        assertNotNull(count);
        Debug.println(0, "testCountAllelicValuesFromCharValuesByDatasetId("+datasetId+") Results: " + count);
    }
    
    @Test
    public void testCountAllelicValuesFromMappingPopValuesByDatasetId() throws Exception { 
        Integer datasetId = 3; // change value of the datasetid
        long count = manager.countAllelicValuesFromMappingPopValuesByDatasetId(datasetId);
        assertNotNull(count);
        Debug.println(0, "testCountAllelicValuesFromMappingPopValuesByDatasetId("+datasetId+") Results: " + count);
    }
    
    @Test
    public void testCountAllParentsFromMappingPopulation() throws Exception {
    	long count = manager.countAllParentsFromMappingPopulation();
        assertNotNull(count);
        Debug.println(0, "testCountAllParentsFromMappingPopulation() Results: " + count);
    }
    
    @Test
    public void testCountMapDetailsByName() throws Exception {
    	String nameLike = "GCP-833TestMap"; //change map_name
    	long count = manager.countMapDetailsByName(nameLike);
        assertNotNull(count);
        Debug.println(0, "testCountMapDetailsByName("+nameLike+") Results: " + count);
    }
    
    @Test
    public void testCountMarkerInfoByDbAccessionId() throws Exception {
    	String dbAccessionId = ""; //change dbAccessionId
    	long count = manager.countMarkerInfoByDbAccessionId(dbAccessionId);
        assertNotNull(count);
        Debug.println(0, "testCountMarkerInfoByDbAccessionId("+dbAccessionId+") Results: " + count);
    }
    
    @Test
    public void testCountMarkerInfoByGenotype() throws Exception {
    	String genotype = "cv. Tatu"; //change genotype
    	long count = manager.countMarkerInfoByGenotype(genotype);
        assertNotNull(count);
        Debug.println(0, "testCountMarkerInfoByGenotype("+genotype+") Results: " + count);
    }
    
    @Test
    public void testCountMarkerInfoByMarkerName() throws Exception {
    	String markerName = "SeqTEST"; //change markerName
    	long count = manager.countMarkerInfoByMarkerName(markerName);
        assertNotNull(count);
        Debug.println(0, "testCountMarkerInfoByMarkerName("+markerName+") Results: " + count);
    }
    
    @Test
    public void testGetAllParentsFromMappingPopulation() throws Exception {
    	long count = manager.countAllParentsFromMappingPopulation();
    	List<ParentElement> results = manager.getAllParentsFromMappingPopulation(0, (int) count);
    	assertNotNull(results);
        assertFalse(results.isEmpty());
        Debug.println(0, "testGetAllParentsFromMappingPopulation() Results: ");
        for (ParentElement result : results){
        	Debug.println(0, "  " + result);
        }
        Debug.println(0, "Number of record/s: "+count+" ");
    }
 
    @Test
    public void testGetMapDetailsByName() throws Exception {
    	String nameLike = "GCP-833TestMap"; //change map_name
    	long count = manager.countMapDetailsByName(nameLike);
    	List<MapDetailElement> results = manager.getMapDetailsByName(nameLike, 0, (int) count);
    	assertNotNull(results);
        assertFalse(results.isEmpty());
        Debug.println(0, "testGetMapDetailsByName("+nameLike+")");
        for (MapDetailElement result : results){
        	Debug.println(0, "  " + result);
        }
        Debug.println(0, "Number of record/s: "+count+" ");
    }
    
    @Test
    public void testGetMarkersByIds() throws Exception {
    	List<Integer> markerIds = new ArrayList<Integer>();
    	markerIds.add(1);
    	markerIds.add(3);
    	
    	List<Marker> results = manager.getMarkersByIds(markerIds, 0,100);
    	assertNotNull(results);
        assertFalse(results.isEmpty());
        Debug.println(0, "testGetMarkersByIds("+markerIds+") Results: ");
        for (Marker result : results){
        	Debug.println(0, "  " + result);
        }
        Debug.println(0, "Number of record/s: " +results.size() );
    }
    
    @Test
    public void testCountAllMaps() throws Exception {
	long count = manager.countAllMaps(Database.LOCAL);
    assertNotNull(count);
    Debug.println(0, "testCountAllMaps("+Database.LOCAL+") Results: " + count);	
    }
    
    @Test
    public void testGetAllMaps() throws Exception {
    List<Map> results= manager.getAllMaps(0, 100, Database.LOCAL);
    assertNotNull(results);
    assertFalse(results.isEmpty());
    Debug.println(0, "testGetAllMaps("+Database.LOCAL+") Results:");
    for (Map result : results){
    	Debug.println(0, "  " + result);
    }
    Debug.println(0, "Number of record/s: " +results.size() );
    
    }

    @Test
    public void testCountNidsFromAccMetadatasetByDatasetIds() throws Exception {
        //GROUNDNUT DATABASE
        List<Integer> datasetIds = Arrays.asList(2, 3, 4);
        long count = manager.countNidsFromAccMetadatasetByDatasetIds(datasetIds);
        Debug.println(0, "testCountNidsFromAccMetadatasetByDatasetIds(" + datasetIds + ") = " + count);
    }
    
    @Test
    public void testCountMarkersFromMarkerMetadatasetByDatasetIds() throws Exception {
        //GROUNDNUT DATABASE
        List<Integer> datasetIds = Arrays.asList(2, 3, 4);
        long count = manager.countMarkersFromMarkerMetadatasetByDatasetIds(datasetIds);
        Debug.println(0, "testCountMarkersFromAccMetadatasetByDatasetIds(" + datasetIds + ") = " + count);
    }
    
    @Test
    public void testGetMapIdByName() throws Exception {
    	//GROUNDNUT DATABASE
    	String mapName = "Consensus_Biotic";  //CENTRAL test data
    	Integer mapId = manager.getMapIdByName(mapName);
    	Debug.println(0, "Map ID for " + mapName + " = " + mapId);
    	mapName = "name that does not exist";
    	mapId = manager.getMapIdByName(mapName);
    	Debug.println(0, "Map ID for " + mapName + " = " + mapId);
    	mapName = "TEST"; //local test data INSERT INTO gdms_map (map_id, map_name, map_type, mp_id, map_desc, map_unit) values(-1, 'TEST', 'genetic', 0, 'TEST', 'cM');
    	mapId = manager.getMapIdByName(mapName);
    	Debug.println(0, "Map ID for " + mapName + " = " + mapId);
    }
    
    @Test
    public void testCountMappingPopValuesByGids() throws Exception {
        List<Integer> gIds = Arrays.asList(1434, 1435, 1436);   // IBDBv2 Groundnut
        long count = manager.countMappingPopValuesByGids(gIds);
        Debug.println(0, "countMappingPopValuesByGids(" + gIds + ") = " + count);
    }
    
    @Test
    public void testCountMappingAlleleValuesByGids() throws Exception {
        List<Integer> gIds = Arrays.asList(2213, 2214);
        long count = manager.countMappingAlleleValuesByGids(gIds);
        Debug.println(0, "testCountMappingAlleleValuesByGids(" + gIds + ") = " + count);
    }
    
    @Test
    public void testGetAllFromMarkerMetadatasetByMarker() throws Exception {
        Integer markerId = 3302; 
        List<MarkerMetadataSet> result = manager.getAllFromMarkerMetadatasetByMarker(markerId);
        Debug.println(0, "testGetAllFromMarkerMetadatasetByMarker(" + markerId + "): " 
                    + (result != null? result.size() : 0));
        if (result != null) {
            for (MarkerMetadataSet elem : result) {
                Debug.println(4, elem.toString());
            }
        }
    }
    @Test
    public void testGetDatasetDetailsByDatasetIds() throws Exception {
        List<Integer> datasetIds = Arrays.asList(3, 4, 5); 
        List<Dataset> result = manager.getDatasetDetailsByDatasetIds(datasetIds);
        Debug.println(0, "testGetDatasetDetailsByDatasetId(" + datasetIds + "): " 
                + (result != null? result.size() : 0));
        if (result != null) {
            for (Dataset elem : result) {
                Debug.println(4, elem.toString());
            }
        }
    }
    
    @Test
    public void testGetQTLIdsByDatasetIds() throws Exception {
        List<Integer> datasetIds = Arrays.asList(1, 2, 3, 4);   // IBDBv2 Groundnut
        List<Integer> qtlIds = manager.getQTLIdsByDatasetIds(datasetIds);
        Debug.println(0, "testGetQTLIdsByDatasetIds(" + datasetIds + "): " + qtlIds);
    }
    
    @Test
    public void testGetAllFromAccMetadataset() throws Exception {
        List<Integer> gids = new ArrayList<Integer>();
        gids.add(2012);
        gids.add(2014);
        gids.add(2016);
        
        Integer datasetId = 5; 
        List<AccMetadataSetPK> result = manager.getAllFromAccMetadataset(gids, datasetId, SetOperation.NOT_IN);
        Debug.println(0, "testGetAllFromAccMetadataset(gid=" + gids + ", datasetId=" + datasetId + "): " 
                + (result != null? result.size() : 0));
        if (result != null) {
            for (AccMetadataSetPK elem : result) {
                Debug.println(4, elem.toString());
            }
        }
    }
    
    @Test
    public void testGetMapAndMarkerCountByMarkers() throws Exception {
        List<Integer> markerIds = Arrays.asList(1317, 1318, 1788, 1053, 2279, 50);
        Debug.println(0, "testGetMapAndMarkerCountByMarkers(" + markerIds + ")");
        List<MapDetailElement> details = manager.getMapAndMarkerCountByMarkers(markerIds);
        if (details != null && details.size() > 0) {
            Debug.println(0, "FOUND " + details.size() + " records");
            for (MapDetailElement detail : details) {
                Debug.println(0, detail.getMapName() + " - " + detail.getMarkerCount());
            }
        } else {
            Debug.println(0, "NO RECORDS FOUND");
        }
    }
    
    @Test
    public void testGetAllMTAs() throws Exception {
        List<Mta> result = manager.getAllMTAs();
        Debug.println(0, "testGetAllMTAs(): " 
                + (result != null? result.size() : 0));
        if (result != null) {
            for (Mta elem : result) {
                Debug.println(4, elem.toString());
            }
        }
    }
    
    @Test
    public void testCountAllMTAs() throws Exception {
        long count = manager.countAllMTAs();
        Debug.println(0, "testCountAllMTAs(): " + count);
    }
    
    @Test
    public void testGetMTAsByTrait() throws Exception {
        Integer traitId = 1;
        List<Mta> result = manager.getMTAsByTrait(traitId);
        Debug.println(0, "testGetMTAsByTrait(): " 
                + (result != null? result.size() : 0));
        if (result != null) {
            for (Mta elem : result) {
                Debug.println(4, elem.toString());
            }
        }
    }
    
    @Test
    public void testDeleteQTLs() throws Exception {
        /*TEST DATA - GROUNDNUT
        insert into gdms_dataset values(-7, 'TEST DATASET', 'TEST', 'QTL', 'Groundnut', 'Groundnut', '2013-07-24', null, 'int', null, null, null, null,  null, null, null);
        insert into gdms_dataset_users values (-7, 123);
        insert into gdms_qtl values (-1, 'TEST QTL1', -7);
        insert into gdms_qtl values (-2, 'TEST QTL2', -7);
        insert into gdms_qtl_details values (-1, -1, 0, 8.1, 0, null, 0, 2.3, 5.4, 'LG01', null, 'GM1959', 'GM2050', 12.01, 0, null, null, null, null, null);
        insert into gdms_qtl_details values (-2, -2, 0, 8.1, 0, null, 0, 2.3, 5.4, 'LG01', null, 'GM1959', 'GM2050', 12.01, 0, null, null, null, null, null);
        */
        List<Integer> qtlIds = Arrays.asList(-1, -2);
        int datasetId = -7;
        Debug.println(0, "testDeleteQTLs(qtlIds=" + qtlIds + ", datasetId=" + datasetId);
        manager.deleteQTLs(qtlIds, datasetId);
        Debug.println(0, "done with testDeleteQTLs");
    }
    
    @Test
    public void testDeleteSSRGenotypingDatasets() throws Exception {
        /* TEST DATA - GROUNDNUT
        insert into gdms_dataset values(-7, 'TEST DATASET', 'TEST', 'QTL', 'Groundnut', 'Groundnut', '2013-07-24', null, 'int', null, null, null, null,  null, null, null);
        insert into gdms_dataset_users values (-7, 123);
        insert into gdms_allele_values values(-4, -7, 1, 1, '238:238', '0.0:0.0', 10);
        insert into gdms_acc_metadataset values (-7, 1, 1);
        insert into gdms_marker_metadataset values(-7, 1);
         */
        int datasetId = -7;
        Debug.println(0, "testDeleteSSRGenotypingDatasets(" + datasetId + ")");
        manager.deleteSSRGenotypingDatasets(datasetId);
        Debug.println(0, "done with testDeleteSSRGenotypingDatasets");
    }
    
    
    @Test
    public void testDeleteSNPGenotypingDatasets() throws Exception {
        /* TEST DATA - GROUNDNUT
        insert into gdms_dataset values(-5, 'TEST DATASET', 'TEST', 'QTL', 'Groundnut', 'Groundnut', '2013-07-24', null, 'int', null, null, null, null,  null, null, null);
        insert into gdms_dataset_users values (-5, 123);
        insert into gdms_char_values values(-4, -5, 1, 1, 'TEST VALUE');
        insert into gdms_acc_metadataset values (-5, 1, 1);
        insert into gdms_marker_metadataset values(-5, 1);
         */
        int datasetId = -5;
        Debug.println(0, "testDeleteSNPGenotypingDatasets(" + datasetId + ")");
        manager.deleteSNPGenotypingDatasets(datasetId);
        Debug.println(0, "done with testDeleteSNPGenotypingDatasets");
    }
    
    @Test
    public void testDeleteDArTGenotypingDatasets() throws Exception {
        /* TEST DATA - GROUNDNUT
        insert into gdms_dataset values(-5, 'TEST DATASET', 'TEST', 'QTL', 'Groundnut', 'Groundnut', '2013-07-24', null, 'int', null, null, null, null,  null, null, null);
        insert into gdms_dataset_users values (-5, 123);
        insert into gdms_allele_values values(-35, -5, 1, 1, '238:238', '0.0:0.0', 10);
        insert into gdms_dart_values values(-25, -5, -1, -1, 1.0, 2.0, 3.0, 4.0, 5.0);
        insert into gdms_acc_metadataset values (-5, 1, 1);
        insert into gdms_marker_metadataset values(-5, 1);
         */
        int datasetId = -5;
        Debug.println(0, "testDeleteDArTGenotypingDatasets(" + datasetId + ")");
        manager.deleteDArTGenotypingDatasets(datasetId);
        Debug.println(0, "done with testDeleteDArTGenotypingDatasets");
    }

    @Test
    public void testDeleteMappingPopulationDatasets() throws Exception {
        /* TEST DATA - GROUNDNUT
        insert into gdms_dataset values(-5, 'TEST DATASET', 'TEST', 'QTL', 'Groundnut', 'Groundnut', '2013-07-24', null, 'int', null, null, null, null,  null, null, null);
        insert into gdms_dataset_users values (-5, 123);
        insert into gdms_mapping_pop_values values( -25, 'X', -5, 1434, 2537);
        insert into gdms_mapping_pop values(-5, 'test', 956, 1042, 999, 'test', 'test', 'test', 0);
        insert into gdms_acc_metadataset values (-5, 1, 1);
        insert into gdms_marker_metadataset values(-5, 1);
         */
        int datasetId = -5;
        Debug.println(0, "testDeleteMappingPopulationDatasets(" + datasetId + ")");
        manager.deleteMappingPopulationDatasets(datasetId);
        Debug.println(0, "done with testDeleteMappingPopulationDatasets");
    }
    
    @Test
    public void testGetQtlDetailsByMapId() throws Exception {
        int mapId = 7;
        Debug.println(0, "testGetQTLsByMapId(" + mapId + ")");
        List<QtlDetails> qtls = manager.getQtlDetailsByMapId(mapId);
        if (qtls != null && qtls.size() > 0) {
                Debug.println(0, "RESULTS");
                for (QtlDetails qtl : qtls) {
                        Debug.println(0, qtl.toString());
                }
        } else {
                Debug.println(0, "NO QTLs FOUND");
        }
    }

    @Test
    public void testCountQtlDetailsByMapId() throws Exception {
        int mapId = 7;
        Debug.println(0, "testCountQTLsByMapId(" + mapId + ")");
        long count = manager.countQtlDetailsByMapId(mapId);
        Debug.println(0, "COUNT = " + count);
    }
    
    @Test
    public void testDeleteMaps() throws Exception {
        /* TEST DATA - GROUNDNUT
        INSERT INTO gdms_map VALUES(-1,'BC-1','genetic',0,'BC-1','cM');
        INSERT INTO gdms_map VALUES(-2,'Consensus_Abiotic','genetic',0,'Consensus_Abiotic','cM');
        INSERT INTO gdms_markers_onmap VALUES(-1,1317,0,0,'BC-1_b11');
        INSERT INTO gdms_markers_onmap VALUES(-1,1318,4.2,4.2,'BC-1_b11');
        INSERT INTO gdms_markers_onmap VALUES(-1,1321,0,0,'BC-1_b10');
        INSERT INTO gdms_markers_onmap VALUES(-1,1345,0,0,'BC-1_b09');
        INSERT INTO gdms_markers_onmap VALUES(-2,1776,102.84,102.84,'LG11');
        INSERT INTO gdms_markers_onmap VALUES(-2,1794,33.3,33.3,'LG14');
        INSERT INTO gdms_markers_onmap VALUES(-2,1782,158.9,158.9,'LG12');
         */
        int mapId = -1;
        Debug.println(0, "testDeleteMaps(" + mapId + ")");
        manager.deleteMaps(mapId);
        Debug.println(0, "done with testDeleteMaps");
    }
    
    @Test
    public void testGetMarkerFromCharValuesByGids() throws Exception {
        List<Integer> gIds = Arrays.asList(2012, 2014, 2016, 310544);
        List<Integer> result = manager.getMarkerFromCharValuesByGids(gIds);
        Debug.println(0, "testGetMarkerFromCharValuesByGids(): " + result.size() +"\n\t" + result);
    }
    
    @Test
    public void testGetMarkerFromAlleleValuesByGids() throws Exception {
        List<Integer> gIds = Arrays.asList(2213, 2214);
        List<Integer> result = manager.getMarkerFromAlleleValuesByGids(gIds);
        Debug.println(0, "testGetMarkerFromAlleleValuesByGids(): " + result.size() +"\n\t" + result);
    }
    
    @Test
    public void testGetMarkerFromMappingPopValuesByGids() throws Exception {
        List<Integer> gIds = Arrays.asList(1434, 1435);
        List<Integer> result = manager.getMarkerFromMappingPopByGids(gIds);
        Debug.println(0, "testGetMarkerFromMappingPopValuesByGids(): " + result.size() +"\n\t" + result);
    }
    
    @Test
    public void testGetLastId() throws Exception {
    	Database instance = Database.LOCAL;
    	for (GdmsTable gdmsTable : GdmsTable.values()) {
        	long lastId = manager.getLastId(instance, gdmsTable);
        	Debug.println(0, "testGetLastId(" + gdmsTable + ") in " + instance + " = " + lastId);
    	}    	

    	instance = Database.CENTRAL;
    	for (GdmsTable gdmsTable : GdmsTable.values()) {
        	long lastId = manager.getLastId(instance, gdmsTable);
        	Debug.println(0, "testGetLastId(" + gdmsTable + ") in " + instance + " = " + lastId);
    	}    	
    }
    
    @Test
    public void testAddMTA() throws Exception {
    	Dataset dataset = new Dataset(null, "TEST DATASET NAME", "DATASET DESC", "MTA", "GENUS", "SPECIES", null, "REMARKS", 
    			"int", null, "METHOD", "0.43", "INSTITUTE", "PI", "EMAIL", "OBJECTIVE");
    	Mta mta = new Mta(null, 1, null, 1, "LINKAGE GROUP", 2.1f, 3, 1, "HVALLELE", "EXPERIMENT", 3.3f, 2.2f);
    	DatasetUsers users = new DatasetUsers(null, 1);
    	manager.addMTA(dataset, mta, users);
    	Debug.println(0, "done with testAddMTA");
    }
    
    @AfterClass
    public static void tearDown() throws Exception {
        factory.close();
    }

}
