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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.GdmsTable;
import org.generationcp.middleware.manager.GdmsType;
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
import org.generationcp.middleware.pojos.gdms.QtlDataRow;
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

    private static final int INDENT = 3;

    @Rule
    public TestName name = new TestName();

    @BeforeClass
    public static void setUp() throws Exception {
        DatabaseConnectionParameters local = new DatabaseConnectionParameters("testDatabaseConfig.properties",
                "localgroundnut");
        DatabaseConnectionParameters central = new DatabaseConnectionParameters("testDatabaseConfig.properties",
                "centralgroundnut");
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
        Debug.println(0, "#####" + name.getMethodName() + ": Elapsed Time = " + elapsedTime + " ns = "
                + ((double) elapsedTime / 1000000000) + " s");
    }

    @Test
    public void testGetNameIdsByGermplasmIds() throws Exception {
        List<Integer> germplasmIds = Arrays.asList(-3787, -6785, -4070, 1434, 2219);
        // For test data, get gids from LOCAL and CENTRAL: SELECT nid, gid FROM gdms_acc_metadataset 

    	List<Integer> results = manager.getNameIdsByGermplasmIds(germplasmIds);
        Debug.println(0, "testGetNameIdsByGermplasmIds(" + germplasmIds + ") RESULTS: " + results.size() + " = " + results);
    }

    @Test
    public void testGetNamesByNameIds() throws Exception {
        List<Integer> nameIds = Arrays.asList(1, 2, 3, -1, -2, -3);
        // For test data, get gids from LOCAL and CENTRAL: SELECT * FROM names where nid in (:nidList)
        
        List<Name> results = manager.getNamesByNameIds(nameIds);
        Debug.println(0, "testGetNamesByNameIds(" + nameIds + ") RESULTS: " + results.size() + " = " + results);
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
        String mapName = "RIL-8 (Yueyou13 x J 11)"; // CENTRAL test data
        List<MapInfo> results = manager.getMapInfoByMapName(mapName, Database.CENTRAL);
        Debug.printObjects(INDENT, results);
    }

    @Test
    public void testGetMapInfoByMapNameBothDB() throws Exception {
        String mapName = "GCP-833TestMap"; // LOCAL test data
        List<MapInfo> results = manager.getMapInfoByMapName(mapName, Database.LOCAL);
        Debug.printObjects(INDENT, results);
    }
    
    @Test
    public void testgetMarkerInfoByMarkerIds() throws Exception {
        List<Integer> markerIds = Arrays.asList(1, -1, 2, -2, 3, 4, 5);
        List<MarkerInfo> results = manager.getMarkerInfoByMarkerIds(markerIds);
        Debug.printObjects(INDENT, results);
    }   
    
    @Test
    public void testGetMapInfoByMapAndChromosome() throws Exception {
    	// Replace with a gdms_markers_onmap.map_id, linkage_group in local
        int mapId = -2; //1;
    	String chromosome = "LG23"; //"BC-1_b11";
        
        List<MapInfo> results = manager.getMapInfoByMapAndChromosome(mapId, chromosome);
        Debug.printObjects(INDENT, results);
    }
    
    @Test
    public void testGetMapInfoByMapChromosomeAndPosition() throws Exception {
    	// Replace with a gdms_markers_onmap.map_id, linkage_group, start_position in local
    	int mapId = -2;	 
        String chromosome = "LG23";
        float startPosition = 123.4f;
        
        List<MapInfo> results = manager.getMapInfoByMapChromosomeAndPosition(mapId, chromosome, startPosition);
        Debug.printObjects(INDENT, results);
    }
    
    @Test
    public void testGetMapInfoByMarkersAndMap() throws Exception {
        List<Integer> markerList = new ArrayList<Integer>();

        int mapId = -2; 	// Replace with a gdms_markers_onmap.map_id in local 
        markerList.add(-4); // Replace with a (-) gdms_markers_onmap.marker_id in local
        markerList.add(3407); // Replace with a (+) gdms_markers_onmap.marker_id in central
        
        List<MapInfo> results = manager.getMapInfoByMarkersAndMap(markerList, mapId);
        Debug.printObjects(INDENT, results);
    }
    
    @Test
    public void testCountDatasetNames() throws Exception {
        long results = manager.countDatasetNames(Database.LOCAL);
        Debug.println(0, "testCountDatasetNames(Database.LOCAL) RESULTS: " + results);
    }

    @Test
    public void testGetDatasetNames() throws Exception {
        List<String> result = manager.getDatasetNames(0, Integer.MAX_VALUE, Database.LOCAL);
        Debug.printObjects(0, result);
    }

    @Test
    public void testGetDatasetNamesByQtlId() throws Exception {
        Integer qtlId = 1; // Crop tested: Groundnut
        List<String> results = manager.getDatasetNamesByQtlId(qtlId, 0, 5);
        Debug.println(0, "testGetDatasetNamesByQtlId(0,5) RESULTS: " + results);
    }

    @Test
    public void testCountDatasetNamesByQtlId() throws Exception {
        Integer qtlId = 1; // Crop tested: Groundnut
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
    public void testGetMarkersByMarkerNames() throws Exception {
        List<String> markerNames = new ArrayList<String>();
        markerNames.add("GA101");
        markerNames.add("GA110");
        markerNames.add("GA122");
        List<Marker> markers = manager.getMarkersByMarkerNames(markerNames, 0, 100, Database.CENTRAL);
        assertTrue(markers.size() > 0);
        Debug.printObjects(3, markers);
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
        List<Integer> markerIds = Arrays.asList(1, 2, 3, 4, 5, -1, -2, -3);
        List<String> results = manager.getMarkerTypesByMarkerIds(markerIds);
        Debug.println(0, "testGetMarkerTypeByMarkerIds(" + markerIds + ") RESULTS: " + results);
    }

    @Test
    public void testGetMarkerNamesByGIds() throws Exception {
        List<Integer> gIds = Arrays.asList(1920, 1895, 1434);
        // For test data, SELECT marker_id, gid from gdms_allele_values / gdms_char_values / gdms_mapping_pop_values
       
        List<MarkerNameElement> results = manager.getMarkerNamesByGIds(gIds);
        Debug.println(0, "testGetMarkerNamesByGIds(" + gIds + ") RESULTS: " + results.size() + " = " + results);
    }

    @Test
    public void testGetGermplasmNamesByMarkerNames() throws Exception {
        List<String> markerNames = Arrays.asList("1_0001", "1_0007", "1_0013");

        List<GermplasmMarkerElement> results = (List<GermplasmMarkerElement>) manager.getGermplasmNamesByMarkerNames(
                markerNames, Database.LOCAL);
        Debug.println(0, "testGetGermplasmNamesByMarkerNames(" + markerNames + ")  RESULTS: " + results);
    }

    @Test
    public void testGetMappingValuesByGidsAndMarkerNames() throws Exception {
//    	List<Integer> gids = Arrays.asList(-3785, -3786, -3787);
//      List<String> markerNames = Arrays.asList("1_0085", "1_0319", "1_0312");
    	
    	List<Integer> gids = Arrays.asList(2212, 2208, 1);
        List<String> markerNames = Arrays.asList("Ah26", "AC2C05", "Ah193", "Seq14H06", "SeqTEST843", "SeqTEST129", "SeqTEST564");
        
        // For test input, please run the following in central/local, and choose select gid / marker_ids
        // SELECT DISTINCT gid, gdms_mapping_pop_values.marker_id
        // FROM gdms_mapping_pop_values 
       	// JOIN gdms_mapping_pop ON gdms_mapping_pop_values.dataset_id = gdms_mapping_pop.dataset_id 
        //   LEFT JOIN gdms_marker ON gdms_mapping_pop_values.marker_id = gdms_marker.marker_id; 

        List<MappingValueElement> mappingValues = manager.getMappingValuesByGidsAndMarkerNames(gids, markerNames, 0, 100);
        Debug.println(0, "testGetMappingValuesByGidsAndMarkerNames(" + gids + ", " + markerNames + ") RESULTS: " 
        		+ mappingValues.size() + " = " + mappingValues);
    }

    @Test
    public void testGetAllelicValuesByGidsAndMarkerNames() throws Exception {
        List<String> markerNames = Arrays.asList("CaM0038", "CaM0463", "CaM0539", "CaM0639",
        		"CaM0658", "1_0001", "1_0007","1_0013", "1_0025", "1_0031");
        List<Integer> gids = Arrays.asList(-5276, -5287, -5484, -5485, -6786, -6785, -3785, -3786);
        /*
         * Results will vary depending on the database connected to. As of the
         * moment, we have no data that contains test values in all 3 source
         * tables
         */
        List<AllelicValueElement> allelicValues = manager.getAllelicValuesByGidsAndMarkerNames(gids, markerNames);
        Debug.println(0, "testGetAllelicValuesByGidsAndMarkerNames(" + gids + ", " + markerNames + ") RESULTS: " + allelicValues);
    }

    @Test
    public void testGetAllelicValuesByGidsAndMarkerNamesForGDMS() throws Exception {

        List<String> markerNames = Arrays.asList("TC03A12", "Seq19E09", "TC7E04", "SeqTEST"); // marker id = 3295, 3296, 1044;
        List<Integer> gids = Arrays.asList(1434, 1435, 1);

        // Local test data
        testAddGDMSMarker(); // add Marker with markerName = "SeqTEST"

        List<AllelicValueElement> allelicValues = manager.getAllelicValuesByGidsAndMarkerNames(gids, markerNames);
        Debug.println(0, "testGetAllelicValuesByGidsAndMarkerNamesForGDMS(" + gids + ", " + markerNames + ") RESULTS: ");
        Debug.printFormattedObjects(INDENT, allelicValues);
    }

    @Test
    public void testGetAllelicValuesFromCharValuesByDatasetId() throws Exception {
        Integer datasetId = Integer.valueOf(2);
        long count = manager.countAllelicValuesFromCharValuesByDatasetId(datasetId);
        List<AllelicValueWithMarkerIdElement> allelicValues = manager.getAllelicValuesFromCharValuesByDatasetId(
                datasetId, 0, (int) count);
        Debug.println(0,
                "testGetAllelicValuesFromCharValuesByDatasetId(" + datasetId + ") RESULTS: " + allelicValues.size());
        for (AllelicValueWithMarkerIdElement results : allelicValues) {
            Debug.println(0, results.toString());
        }
    }

    @Test
    public void testGetAllelicValuesFromAlleleValuesByDatasetId() throws Exception {
        Integer datasetId = Integer.valueOf(2);
        long count = manager.countAllelicValuesFromAlleleValuesByDatasetId(datasetId);
        List<AllelicValueWithMarkerIdElement> allelicValues = manager.getAllelicValuesFromAlleleValuesByDatasetId(
                datasetId, 0, (int) count);
        Debug.println(0, "testGetAllelicValuesFromAlleleValuesByDatasetId(dataset=" + datasetId + ") RESULTS: "
                + allelicValues.size());
        for (AllelicValueWithMarkerIdElement results : allelicValues) {
            Debug.println(0, results.toString());
        }
    }

    @Test
    public void testGetAllelicValuesFromMappingPopValuesByDatasetId() throws Exception {
        // 23-Jul-2013, currently no test data with datasetid = 2 in groundnut
        // central, using datasetid=3 instead
        // Integer datasetId = Integer.valueOf(2);
        Integer datasetId = Integer.valueOf(3);
        long count = manager.countAllelicValuesFromMappingPopValuesByDatasetId(datasetId);
        // manager.countAllelicValuesFromCharValuesByDatasetId(datasetId);
        List<AllelicValueWithMarkerIdElement> allelicValues = manager.getAllelicValuesFromMappingPopValuesByDatasetId(
                datasetId, 0, (int) count);
        Debug.println(0, "COUNT IS " + count);
        Debug.println(0, "testGetAllelicValuesFromMappingPopValuesByDatasetId(" + datasetId + ") RESULTS: ");
        if (allelicValues != null) {
            Debug.printObjects(INDENT, allelicValues);
        }
    }

    @Test
    public void testGetMarkerNamesByMarkerIds() throws Exception {
        List<Integer> markerIds = Arrays.asList(1, -1, 2, -2, 3, -3, 4, -4, 5, -5);
        List<MarkerIdMarkerNameElement> markerNames = manager.getMarkerNamesByMarkerIds(markerIds);
        Debug.println(0, "testGetMarkerNamesByMarkerIds(" + markerIds + ") RESULTS: ");
        Debug.printObjects(INDENT, markerNames);
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
    public void testGetGidsByMarkersAndAlleleValues() throws Exception {
        List<Integer> markerIdList = Arrays.asList(2325);
        List<String> alleleValueList = Arrays.asList("238/238");
        
        List<Integer> gids = manager.getGidsByMarkersAndAlleleValues(Database.CENTRAL, markerIdList, alleleValueList);
        Debug.println(0, "testGetGidsByMarkersAndAlleleValues(" + markerIdList + "," + alleleValueList + ") RESULTS: " + gids);
    }
    
    @Test
    public void testGetAllGidsByMarkersAndAlleleValues() throws Exception {
        List<Integer> markerIdList = Arrays.asList(2325);
        List<String> alleleValueList = Arrays.asList("238/238");
        
        List<Integer> gids = manager.getAllGidsByMarkersAndAlleleValues(markerIdList, alleleValueList);
        Debug.println(0, "testGetAllGidsByMarkersAndAlleleValues(" + markerIdList + "," + alleleValueList + ") RESULTS: " + gids);
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
        List<Integer> datasetIds = Arrays.asList(-1, -2, -3, 1, 2, 3);
        List<Integer> gids = Arrays.asList(-2);

        List<Integer> nids = manager.getNidsFromAccMetadatasetByDatasetIds(datasetIds, 0, 10);
        List<Integer> nidsWithGidFilter = manager.getNidsFromAccMetadatasetByDatasetIds(datasetIds, gids, 0, 10);

        Debug.println(0, "testGetNidsFromAccMetadatasetByDatasetIds RESULTS: " + nids);
        Debug.println(0, "testGetNidsFromAccMetadatasetByDatasetIds with gid filter RESULTS: " + nidsWithGidFilter);
    }

    @Test
    public void testGetDatasetIdsForFingerPrinting() throws Exception {
        List<Integer> datasetIds = manager.getDatasetIdsForFingerPrinting(0,
                (int) manager.countDatasetIdsForFingerPrinting());
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
        List<Integer> germplasmIds = Arrays.asList(956, 1042, -2213, -2215);;
        List<AccMetadataSetPK> accMetadataSets = manager.getGdmsAccMetadatasetByGid(germplasmIds, 0,
                (int) manager.countGdmsAccMetadatasetByGid(germplasmIds));
        Debug.println(0, "testGetGdmsAccMetadatasetByGid() RESULTS: ");
        Debug.printObjects(INDENT, accMetadataSets);
    }

    @Test
    public void testCountGdmsAccMetadatasetByGid() throws Exception {
        List<Integer> germplasmIds = Arrays.asList(956, 1042, -2213, -2215);
        long count = manager.countGdmsAccMetadatasetByGid(germplasmIds);
        Debug.println(0, "testCountGdmsAccMetadatasetByGid() RESULTS: " + count);
    }

    @Test
    public void testGetMarkersByGidAndDatasetIds() throws Exception {
        Integer gid = Integer.valueOf(-2215); // Crop Tested: Groundnut
        List<Integer> datasetIds = Arrays.asList(1, 2);

        List<Integer> markerIds = manager.getMarkersByGidAndDatasetIds(gid, datasetIds, 0,
                (int) manager.countMarkersByGidAndDatasetIds(gid, datasetIds));
        Debug.println(0, "testGetMarkersByGidAndDatasetIds() RESULTS: " + markerIds);
    }

    @Test
    public void testCountMarkersByGidAndDatasetIds() throws Exception {
        Integer gid = Integer.valueOf(-2215); // Crop Tested: Groundnut

        List<Integer> datasetIds = Arrays.asList(1, 2);

        long count = manager.countMarkersByGidAndDatasetIds(gid, datasetIds);
        Debug.println(0, "testCountGdmsAccMetadatasetByGid() RESULTS: " + count);
    }

    @Test
    public void testCountAlleleValuesByGids() throws Exception {
        List<Integer> germplasmIds = Arrays.asList(956, 1042, -2213, -2215);
        long count = manager.countAlleleValuesByGids(germplasmIds);
        Debug.println(0, "testCountAlleleValuesByGids() RESULTS: " + count);
    }

    @Test
    public void testCountCharValuesByGids() throws Exception {
        List<Integer> germplasmIds = Arrays.asList(956, 1042, -2213, -2215);
        long count = manager.countCharValuesByGids(germplasmIds);
        Debug.println(0, "testCountCharValuesByGids() RESULTS: " + count);
    }

    @Test
    public void testGetIntAlleleValuesForPolymorphicMarkersRetrieval() throws Exception {
        // For test data, get gids from LOCAL and CENTRAL: SELECT distinct FROM gdms_allele_values; 
        List<Integer> germplasmIds = Arrays.asList(1, 956, 1042, -2213, -2215);

        List<AllelicValueElement> results = manager.getIntAlleleValuesForPolymorphicMarkersRetrieval(germplasmIds, 
        		0, Integer.MAX_VALUE);
        Debug.println(0, "testGetIntAlleleValuesForPolymorphicMarkersRetrieval() RESULTS: ");
        Debug.printObjects(INDENT, results);
    }

    @Test
    public void testCountIntAlleleValuesForPolymorphicMarkersRetrieval() throws Exception {
        // For test data, get gids from LOCAL and CENTRAL: SELECT distinct FROM gdms_allele_values; 
        List<Integer> germplasmIds = Arrays.asList(1, 956, 1042, -2213, -2215);
        
        long count = manager.countIntAlleleValuesForPolymorphicMarkersRetrieval(germplasmIds);
        Debug.println(0, "testCountIntAlleleValuesForPolymorphicMarkersRetrieval() RESULTS: " + count);
    }

    @Test
    public void testGetCharAlleleValuesForPolymorphicMarkersRetrieval() throws Exception {
        // For test data, get gids from LOCAL and CENTRAL: SELECT distinct FROM gdms_char_values; 
        List<Integer> germplasmIds = Arrays.asList(1, 956, 1042, -2213, -2215);
        
        List<AllelicValueElement> results = manager.getCharAlleleValuesForPolymorphicMarkersRetrieval(germplasmIds, 
        		0, Integer.MAX_VALUE);
        Debug.println(0, "testGetIntAlleleValuesForPolymorphicMarkersRetrieval() RESULTS: ");
        Debug.printObjects(INDENT, results);
    }

    @Test
    public void testCountCharAlleleValuesForPolymorphicMarkersRetrieval() throws Exception {
        // For test data, get gids from LOCAL and CENTRAL: SELECT distinct FROM gdms_char_values; 
        List<Integer> germplasmIds = Arrays.asList(1, 956, 1042, -2213, -2215);
        
        long count = manager.countCharAlleleValuesForPolymorphicMarkersRetrieval(germplasmIds);
        Debug.println(0, "testCountCharAlleleValuesForPolymorphicMarkersRetrieval() RESULTS: " + count);
    }
    
    @Test
    public void testGetMappingAlleleValuesForPolymorphicMarkersRetrieval() throws Exception {
        // For test data, get gids from LOCAL and CENTRAL: SELECT distinct FROM gdms_mapping_pop_values; 
        List<Integer> germplasmIds = Arrays.asList(1, 1434, 1435);

        List<AllelicValueElement> results = manager.getMappingAlleleValuesForPolymorphicMarkersRetrieval(germplasmIds,
        		0, Integer.MAX_VALUE);
        Debug.println(0, "testGetMappingAlleleValuesForPolymorphicMarkersRetrieval() RESULTS: ");
        Debug.printObjects(INDENT, results);
    }

    @Test
    public void testCountMappingAlleleValuesForPolymorphicMarkersRetrieval() throws Exception {
        // For test data, get gids from LOCAL and CENTRAL: SELECT distinct FROM gdms_mapping_pop_values; 
        List<Integer> germplasmIds = Arrays.asList(1, 1434, 1435);

        long count = manager.countMappingAlleleValuesForPolymorphicMarkersRetrieval(germplasmIds);
        Debug.println(0, "testCountMappingAlleleValuesForPolymorphicMarkersRetrieval() RESULTS: " + count);
    }


    @Test
    public void testGetNIdsByDatasetIdsAndMarkerIdsAndNotGIds() throws Exception {
        List<Integer> gIds = Arrays.asList(956, 1042, 1128);
        List<Integer> datasetIds = Arrays.asList(2);
        List<Integer> markerIds = Arrays.asList(6, 10);

        List<Integer> nIdList = manager.getNIdsByMarkerIdsAndDatasetIdsAndNotGIds(datasetIds, markerIds, gIds, 0,
                manager.countNIdsByMarkerIdsAndDatasetIdsAndNotGIds(datasetIds, markerIds, gIds));
        Debug.println(0, "testGetNIdsByDatasetIdsAndMarkerIdsAndNotGIds(datasetIds=" + datasetIds + ") RESULTS: "
                + nIdList);

        nIdList = manager.getNIdsByMarkerIdsAndDatasetIdsAndNotGIds(null, gIds, markerIds, 0, 5);
        Debug.println(0, "testGetNIdsByDatasetIdsAndMarkerIdsAndNotGIds(datasetIds=null) RESULTS: " + nIdList);

        nIdList = manager.getNIdsByMarkerIdsAndDatasetIdsAndNotGIds(datasetIds, null, markerIds, 0, 5);
        Debug.println(0, "testGetNIdsByDatasetIdsAndMarkerIdsAndNotGIds(gIds=null) RESULTS: " + nIdList);

        nIdList = manager.getNIdsByMarkerIdsAndDatasetIdsAndNotGIds(datasetIds, gIds, null, 0, 5);
        Debug.println(0, "testGetNIdsByDatasetIdsAndMarkerIdsAndNotGIds(markerIds=null) RESULTS: " + nIdList);

    }

    @Test
    public void testCountNIdsByDatasetIdsAndMarkerIdsAndNotGIds() throws Exception {
        List<Integer> gIds = Arrays.asList(29, 303, 4950);
        List<Integer> datasetIds =Arrays.asList(2);
        List<Integer> markerIds = Arrays.asList(6803);
        int count = manager.countNIdsByMarkerIdsAndDatasetIdsAndNotGIds(datasetIds, markerIds, gIds);
        Debug.println(0, "testcCountNIdsByDatasetIdsAndMarkerIdsAndNotGIds() RESULTS: " + count);
    }

    @Test
    public void testGetNIdsByDatasetIdsAndMarkerIds() throws Exception {
        List<Integer> datasetIds = Arrays.asList(2);
        List<Integer> markerIds = Arrays.asList(6803);
        List<Integer> nIdList = manager.getNIdsByMarkerIdsAndDatasetIds(datasetIds, markerIds, 0,
                manager.countNIdsByMarkerIdsAndDatasetIds(datasetIds, markerIds));
        Debug.println(0, "testGetNIdsByDatasetIdsAndMarkerIds() RESULTS: " + nIdList);
    }

    @Test
    public void testCountNIdsByDatasetIdsAndMarkerIds() throws Exception {
        List<Integer> datasetIds = Arrays.asList(2);
        List<Integer> markerIds = Arrays.asList(6803);
        int count = manager.countNIdsByMarkerIdsAndDatasetIds(datasetIds, markerIds);
        Debug.println(0, "testCountNIdsByDatasetIdsAndMarkerIds() RESULTS: " + count);
    }

    @Test
    public void testGetAllQtl() throws Exception {
        List<Qtl> qtls = manager.getAllQtl(0, (int) manager.countAllQtl());
        Debug.println(0, "testGetAllQtl() RESULTS: ");
        Debug.printObjects(INDENT, qtls);
    }

    @Test
    public void testCountAllQtl() throws Exception {
        long result = manager.countAllQtl();
        Debug.println(0, "testCountAllQtl() RESULTS: " + result);
    }

    @Test
    public void testCountQtlIdByName() throws Exception {
        String qtlName = "SLA%"; // Crop tested: Groundnut
        long count = manager.countQtlIdByName(qtlName);
        Debug.println(0, "testCountQtlIdByName() RESULTS: " + count);
    }

    @Test
    public void testGetQtlIdByName() throws Exception {
        String qtlName = "TWW09_AhV"; // Crop tested: Groundnut
        
        List<Integer> results = manager.getQtlIdByName(qtlName, 0, (int) manager.countQtlIdByName(qtlName));
        Debug.println(0, "testGetQtlIdByName() RESULTS: " + results);
    }

    @Test
    public void testGetQtlByNameFromCentral() throws Exception {
        String qtlName = "HI Control%"; // Crop tested: Groundnut

        List<QtlDetailElement> results = manager.getQtlByName(qtlName, 0, (int) manager.countQtlByName(qtlName));

        assertTrue(results.size() > 0);

        Debug.printFormattedObjects(3, results);
        Debug.println(0, "testGetQtlByNameFromCentral() #records: " + results.size());
    }

    @Test
    public void testGetQtlByNameFromLocal() throws Exception {
        try {
            testSetQTL(); // to add a qtl_details entry with name = "TestQTL"
        } catch (MiddlewareQueryException e) {
            // Ignore. There is already a record for testing.
        }
        String qtlName = "TestQTL%";

        List<QtlDetailElement> results = manager.getQtlByName(qtlName, 0, (int) manager.countQtlByName(qtlName));

        assertTrue(results.size() > 0);

        Debug.printFormattedObjects(3, results);
        Debug.println(0, "testGetQtlByNameFromLocal() #records: " + results.size());
    }

    @Test
    public void testCountQtlByName() throws Exception {
        String qtlName = "HI Control%"; // Crop tested: Groundnut
        long count = manager.countQtlByName(qtlName);
        assertTrue(count > 0);
        Debug.println(0, "testCountQtlByName() RESULTS: " + count);
    }

    @Test
    public void testgetQtlNamesByQtlIds() throws Exception {
        List<Integer> qtlIds = Arrays.asList(1, 2, 3, -4, -5);
        java.util.Map<Integer, String> qtlNames = manager.getQtlNamesByQtlIds(qtlIds);
        assertTrue(qtlNames.size() > 0);
        for (int i = 0; i < qtlIds.size(); i++) {
            Debug.println(3, "QTL ID = " + qtlIds.get(i) + " : QTL NAME = " + qtlNames.get(qtlIds.get(i)));
        }

    }

    @Test
    public void testGetQtlByTrait() throws Exception {
        Integer qtlTraitId = 1001; // "SL%"; // Crop tested: Groundnut
        List<Integer> results = manager.getQtlByTrait(qtlTraitId, 0, (int) manager.countQtlByTrait(qtlTraitId));
        Debug.println(0, "testGetQtlByTrait() RESULTS: " + results);
    }

    @Test
    public void testCountQtlByTrait() throws Exception {
        Integer qtlTraitId = 1001; // "SL%"; // Crop tested: Groundnut
        long count = manager.countQtlByTrait(qtlTraitId);
        Debug.println(0, "testCountQtlByTrait() RESULTS: " + count);
    }

    @Test
    public void testGetQtlTraitsByDatasetId() throws Exception {
        Integer datasetId = 7; // Crop tested: Groundnut
        List<Integer> results = manager.getQtlTraitsByDatasetId(datasetId, 0,
                (int) manager.countQtlTraitsByDatasetId(datasetId));
        Debug.println(0, "testGetQtlTraitsByDatasetId() RESULTS: " + results);
    }

    @Test
    public void testCountQtlTraitsByDatasetId() throws Exception {
        Integer datasetId = 7; // Crop tested: Groundnut
        long count = manager.countQtlTraitsByDatasetId(datasetId);
        Debug.println(0, "testCountQtlTraitsByDatasetId() RESULTS: " + count);
    }

    @Test
    public void testGetMapIdsByQtlName() throws Exception {
        String qtlName = "HI Control 08_AhI"; // Crop tested: Groundnut
        List<Integer> results = manager.getMapIdsByQtlName(qtlName, 0, (int) manager.countMapIdsByQtlName(qtlName));
        Debug.println(0, "testGetMapIdsByQtlName() RESULTS: " + results);
    }

    @Test
    public void testCountMapIdsByQtlName() throws Exception {
        String qtlName = "HI Control 08_AhI"; // Crop tested: Groundnut
        long count = manager.countMapIdsByQtlName(qtlName);
        Debug.println(0, "testCountMapIdsByQtlName() RESULTS: " + count);
    }

    @Test
    public void testGetMarkersByQtl() throws Exception {
        String qtlName = "HI Control 08_AhI"; // Crop tested: Groundnut
        String chromosome = "LG01";
        int min = 0;
        int max = 10;
        List<Integer> results = manager.getMarkerIdsByQtl(qtlName, chromosome, min, max, 0,
                (int) manager.countMarkerIdsByQtl(qtlName, chromosome, min, max));
        Debug.println(0, "testGetMarkersByQtl() RESULTS: " + results);
    }

    @Test
    public void testCountMarkersByQtl() throws Exception {
        String qtlName = "HI Control 08_AhI"; // Crop tested: Groundnut
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
            Debug.println(
                    3,
                    "Parent A NId: " + parentElement.getParentANId() + "  |  Parent B NId: "
                            + parentElement.getParentBGId());
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
        Debug.println(0, "getMapDetailsByName('" + nameLike + "'," + start + "," + end + ")");
        for (MapDetailElement map : maps) {
            Debug.println(0, "Map: " + map.getMarkerCount() + "  |  maxStartPosition: " + map.getMaxStartPosition()
                    + "  |  linkageGroup: " + map.getLinkageGroup() + "  |  mapName: " + map.getMapName()
                    + "  |  mapType:  " + map.getMapType());
        }
    }

    @Test
    public void countMapDetailsByName() throws Exception {
        String nameLike = "tag%";
        Long parentElementsCount = manager.countMapDetailsByName(nameLike);
        Debug.println(0, "countMapDetailsByName('" + nameLike + "')");
        Debug.println(0, "Count: " + parentElementsCount);
    }

    @Test
    public void testGetAllMapDetails() throws Exception {
        int start = 0;
        int end = (int) manager.countAllMapDetails();
        List<MapDetailElement> maps = manager.getAllMapDetails(start, end);
        Debug.println(0, "testGetAllMapDetails(" + start + "," + end + ")");
        Debug.printObjects(INDENT, maps);
    }

    @Test
    public void testCountAllMapDetails() throws Exception {
        Debug.println(0, "testCountAllMapDetails(): " + manager.countAllMapDetails());
    }

    @Test
    public void testAddQtlDetails() throws Exception {
        Integer qtlId = -1; // Crop tested: Groundnut
        Integer mapId = -2;
        Float minPosition = 0f;
        Float maxPosition = 8f;
        Integer traitId = 1001; // "DE";
        String experiment = "";
        Integer effect = 0;
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
                scoreValue, rSquare, linkageGroup, interactions, leftFlankingMarker, rightFlankingMarker, position,
                clen, seAdditive, hvParent, hvAllele, lvParent, lvAllele);

        QtlDetailsPK idAdded = manager.addQtlDetails(qtlDetails);
        Debug.println(0, "testAddQtlDetails() Added: " + (idAdded != null ? qtlDetails : null));
    }

    @Test
    public void testAddMarkerDetails() throws Exception {
        Integer markerId = -1; // Replace with desired values
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
        Integer markerId = -1; // Replace with desired values
        String principalInvestigator = "Juan Dela Cruz";
        String contact = "juan@irri.com.ph";
        String institute = "IRRI";

        MarkerUserInfo markerUserInfo = new MarkerUserInfo(markerId, principalInvestigator, contact, institute);

        Integer idAdded = manager.addMarkerUserInfo(markerUserInfo);
        Debug.println(0, "testAddMarkerUserInfo() Added: " + (idAdded != null ? markerUserInfo : null));
    }

    @Test
    public void testAddAccMetadataSet() throws Exception {
        Integer datasetId = -1; // Crop tested: Groundnut
        Integer germplasmId = 1;
        Integer nameId = 1;

        AccMetadataSet accMetadataSet = new AccMetadataSet(datasetId, germplasmId, nameId);

        AccMetadataSetPK idAdded = manager.addAccMetadataSet(accMetadataSet);
        Debug.println(0, "testAccMetadataSet() Added: " + (idAdded != null ? accMetadataSet : null));
    }

    @Test
    public void testAddMarkerMetadataSet() throws Exception {
        Integer datasetId = -1; // Crop tested: Groundnut
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

    private Dataset createDataset() throws Exception {
        Integer datasetId = null; // Crop tested: Groundnut
        String datasetName = " QTL_ ICGS 44 X ICGS 78" + (int) (Math.random() * 1000);
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

        return new Dataset(datasetId, datasetName, datasetDesc, datasetType, genus, species, uploadTemplateDate,
                remarks, dataType, missingData, method, score, institute, principalInvestigator, email, purposeOfStudy);
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

        try{
	        Marker marker = new Marker(markerId, markerType, markerName, species, dbAccessionId, reference, genotype,
	                ploidy, primerId, remarks, assayType, motif, forwardPrimer, reversePrimer, productSize, annealingTemp,
	                amplification);
	        Integer idAdded = manager.addGDMSMarker(marker);
	        Debug.println(0, "testAddGDMSMarker() Added: " + (idAdded != null ? marker : null));
        } catch (MiddlewareQueryException e){
        	Debug.println(e.getMessage() + "\n");
        }

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
        Integer anId = null; // Crop tested: Groundnut
        Integer datasetId = -1;
        Integer gId = 1920;
        Integer markerId = 1037;
        String alleleBinValue = "alleleBinValue";
        String alleleRawValue = "alleleRawValue";
        Integer peakHeight = 10;

        AlleleValues alleleValues = new AlleleValues(anId, datasetId, gId, markerId, alleleBinValue, alleleRawValue,
                peakHeight);

        Integer idAdded = manager.addAlleleValues(alleleValues);
        Debug.println(0, "testAddAlleleValues() Added: " + (idAdded != null ? alleleValues : null));
    }

    @Test
    public void testAddCharValues() throws Exception {
        Integer acId = null; // Crop tested: Groundnut
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

        MappingPop mappingPop = new MappingPop(datasetId, mappingType, parentAGId, parentBGId, populationSize,
                populationType, mapDataDescription, scoringScheme, mapId);

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
        // String mapUnit = "TS";
        String linkageGroup = "Test";

        MarkerOnMap markerOnMap = new MarkerOnMap(mapId, markerId, startPosition, endPosition, linkageGroup);

        try {
            Integer idAdded = manager.addMarkerOnMap(markerOnMap);
            Debug.println(0, "testAddMarkerOnMap() Added: " + (idAdded != null ? markerOnMap : null));
        } catch (MiddlewareQueryException e) {
            if (e.getMessage().contains("Map Id not found")) {
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

        DartValues dartValue = new DartValues(adId, datasetId, markerId, cloneId, qValue, reproducibility, callRate,
                picValue, discordance);

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
        List<Object> markerRecords = createMarkerRecords();
        Marker marker = (Marker) markerRecords.get(0);
        MarkerAlias markerAlias = (MarkerAlias) markerRecords.get(1);
        MarkerDetails markerDetails = (MarkerDetails) markerRecords.get(2);
        MarkerUserInfo markerUserInfo = (MarkerUserInfo) markerRecords.get(3);

        Boolean addStatus = manager.setSSRMarkers(marker, markerAlias, markerDetails, markerUserInfo);
        if (addStatus) {
            Debug.println(0, "testSetSSRMarkers() Added: ");
            Debug.println(3, marker.toString());
            Debug.println(3, markerAlias.toString());
            Debug.println(3, markerDetails.toString());
            Debug.println(3, markerUserInfo.toString());
        }
    }

    private List<Object> createMarkerRecords() {

        Integer markerId = null; // Will be set/overridden by the function
        String markerType = null; // Will be set/overridden by the function
        String markerName = "SeqTEST" + (int) (Math.random() * 1000);
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

        Marker marker = new Marker(markerId, markerType, markerName, species, dbAccessionId, reference, genotype,
                ploidy, primerId, remarks, assayType, motif, forwardPrimer, reversePrimer, productSize, annealingTemp,
                amplification);
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
        List<Object> markerRecords = createMarkerRecords();
        Marker marker = (Marker) markerRecords.get(0);
        MarkerAlias markerAlias = (MarkerAlias) markerRecords.get(1);
        MarkerDetails markerDetails = (MarkerDetails) markerRecords.get(2);
        MarkerUserInfo markerUserInfo = (MarkerUserInfo) markerRecords.get(3);

        Boolean addStatus = manager.setSNPMarkers(marker, markerAlias, markerDetails, markerUserInfo);
        if (addStatus) {
            Debug.println(0, "testSetSNPMarkers() Added: ");
            Debug.println(3, marker.toString());
            Debug.println(3, markerAlias.toString());
            Debug.println(3, markerDetails.toString());
            Debug.println(3, markerUserInfo.toString());
        }
    }
    
    @Test
    public void testSetSNPMarkersGCP8066() throws Exception {

		//Marker [markerId=null, markerType=SNP, markerName=GKAM0001, species=Groundnut, dbAccessionId=, 
		//reference=, genotype=, ploidy=, primerId=null, remarks=null, assayType=KASPar, 
		//motif=A/T, forwardPrimer=AGCTTAACAATGAAGGAAATGGTGAGGAGAGGAGGAGGTTTGGTGAGAGACGAGGACCTG, 
		//reversePrimer=TCGTTCTTTCAGGCCACCTTACAATGGTAATGTTAATGAGAACTTTCACCTTAATGCT, 
		//productSize=, annealingTemp=null, amplification=null]
        Integer markerId = null; // Will be set/overridden by the function
        String markerType = GdmsType.TYPE_SNP.getValue(); // Will be set/overridden by the function
        String markerName = "GKAM0001";
        String species = "Groundnut";
        String dbAccessionId = "";
        String reference = "";
        String genotype = "";
        String ploidy = "";
        String primerId = null;
        String remarks = null;
        String assayType = "KASPar";
        String motif = "A/T";
        String forwardPrimer = "AGCTTAACAATGAAGGAAATGGTGAGGAGAGGAGGAGGTTTGGTGAGAGACGAGGACCTG";
        String reversePrimer = "TCGTTCTTTCAGGCCACCTTACAATGGTAATGTTAATGAGAACTTTCACCTTAATGCT";
        String productSize = "";
        Float annealingTemp = null;
        String amplification = null;

        //MarkerAlias [markerId=null, alias=]
        String alias = "";

		//MarkerDetails [markerId=null, noOfRepeats=null, motifType=null, sequence=, sequenceLength=null, 
		//minAllele=null, maxAllele=null, ssrNr=null, forwardPrimerTemp=null, reversePrimerTemp=null, 
		//elongationTemp=null, fragmentSizeExpected=null, fragmentSizeObserved=null, expectedProductSize=null, 
		//positionOnReferenceSequence=null, restrictionEnzymeForAssay=null]
        Integer noOfRepeats = null;
        String motifType = null;
        String sequence = "";
        Integer sequenceLength = null;
        Integer minAllele = null;
        Integer maxAllele = null;
        Integer ssrNr = null;
        Float forwardPrimerTemp = null;
        Float reversePrimerTemp = null;
        Float elongationTemp = null;
        Integer fragmentSizeExpected = null;
        Integer fragmentSizeObserved = null;
        Integer expectedProductSize = null;
        Integer positionOnReferenceSequence = null;
        String restrictionEnzymeForAssay = null;

//        MarkerUserInfo [markerId=null, principalInvestigator=Rajeev K Varshney, contact=, institute=ICRISAT]
        String principalInvestigator = "Rajeev K Varshney";
        String contact = "";
        String institute = "ICRISAT";

        Marker marker = new Marker(markerId, markerType, markerName, species, dbAccessionId, reference, genotype,
                ploidy, primerId, remarks, assayType, motif, forwardPrimer, reversePrimer, productSize, annealingTemp,
                amplification);
        MarkerAlias markerAlias = new MarkerAlias(markerId, alias);
        MarkerDetails markerDetails = new MarkerDetails(markerId, noOfRepeats, motifType, sequence, sequenceLength,
                minAllele, maxAllele, ssrNr, forwardPrimerTemp, reversePrimerTemp, elongationTemp,
                fragmentSizeExpected, fragmentSizeObserved, expectedProductSize, positionOnReferenceSequence,
                restrictionEnzymeForAssay);
        MarkerUserInfo markerUserInfo = new MarkerUserInfo(markerId, principalInvestigator, contact, institute);

        try{
        	manager.setSNPMarkers(marker, markerAlias, markerDetails, markerUserInfo);
        } catch (Exception e){
        	assertTrue(e.getMessage().contains("Marker already exists in Central or Local and cannot be added."));
        }
    }    
    

    @Test
    public void testSetCAPMarkers() throws Exception {
        List<Object> markerRecords = createMarkerRecords();
        Marker marker = (Marker) markerRecords.get(0);
        MarkerAlias markerAlias = (MarkerAlias) markerRecords.get(1);
        MarkerDetails markerDetails = (MarkerDetails) markerRecords.get(2);
        MarkerUserInfo markerUserInfo = (MarkerUserInfo) markerRecords.get(3);

        Boolean addStatus = manager.setCAPMarkers(marker, markerAlias, markerDetails, markerUserInfo);
        if (addStatus) {
            Debug.println(0, "testSetCAPMarkers() Added: ");
            Debug.println(3, marker.toString());
            Debug.println(3, markerAlias.toString());
            Debug.println(3, markerDetails.toString());
            Debug.println(3, markerUserInfo.toString());
        }
    }

    @Test
    public void testSetCISRMarkers() throws Exception {
        List<Object> markerRecords = createMarkerRecords();
        Marker marker = (Marker) markerRecords.get(0);
        MarkerAlias markerAlias = (MarkerAlias) markerRecords.get(1);
        MarkerDetails markerDetails = (MarkerDetails) markerRecords.get(2);
        MarkerUserInfo markerUserInfo = (MarkerUserInfo) markerRecords.get(3);

        Boolean addStatus = manager.setCISRMarkers(marker, markerAlias, markerDetails, markerUserInfo);
        if (addStatus) {
            Debug.println(0, "testSetCISRMarkers() Added: ");
            Debug.println(3, marker.toString());
            Debug.println(3, markerAlias.toString());
            Debug.println(3, markerDetails.toString());
            Debug.println(3, markerUserInfo.toString());
        }
    }

    @Test
    public void testSetQTL() throws Exception {
        Integer datasetId = null; // Will be set/overridden by the function
        Integer userId = 123;

        Integer qtlId = null; // Will be set/overridden by the function
        Integer mapId = 1;
        Float minPosition = 0f;
        Float maxPosition = 8f;
        Integer traitId = 1001; // "DE";
        String experiment = "";
        Integer effect = 0;
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
        dataset.setDatasetName(dataset.getDatasetName() + (int) (Math.random() * 100)); // Used to insert a new dataset

        QtlDetails qtlDetails = new QtlDetails(qtlId, mapId, minPosition, maxPosition, traitId, experiment, effect,
                scoreValue, rSquare, linkageGroup, interactions, leftFlankingMarker, rightFlankingMarker, position,
                clen, seAdditive, hvParent, hvAllele, lvParent, lvAllele);

        Qtl qtl = new Qtl(qtlId, qtlName, datasetId);

        List<QtlDataRow> dataRows = new ArrayList<QtlDataRow>();
        dataRows.add(new QtlDataRow(qtl, qtlDetails));

        Boolean addStatus = manager.setQTL(dataset, datasetUser, dataRows);

        assertTrue(addStatus);

        Debug.println(0, "testSetQTL() Added: ");
        Debug.println(3, datasetUser.toString());
        Debug.println(3, dataset.toString());
        Debug.printObjects(INDENT, dataRows);
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
        try {
            addStatus = manager.setMaps(marker, markerOnMap, map);
        } catch (MiddlewareQueryException e) {
            if (e.getMessage().contains("The marker on map combination already exists")) {
                Debug.println(0, "The marker on map combination already exists");
            } else {
                Debug.println(0, "testSetMaps() Added: " + (addStatus != null ? marker : null) + " | "
                        + (addStatus != null ? markerOnMap : null) + " | " + (addStatus != null ? map : null));
            }
        }
    }

    @Test
    public void testGetMapIDsByQTLName() throws Exception {
        String qtlName = "HI Control 08_AhI";

        List<Integer> mapIDs = manager.getMapIDsByQTLName(qtlName, 0, 2);

        Debug.println(0, "TestGetMapIDsByQTLName(" + qtlName + ")");
        for (Integer mapID : mapIDs) {
            Debug.println(3, "Map ID: " + mapID);
        }
    }

    @Test
    public void testCountMapIDsByQTLName() throws Exception {
        String qtlName = "HI Control 08_AhI";

        long count = manager.countMapIDsByQTLName(qtlName);

        Debug.println(0, "TestCountMapIDsByQTLName(" + qtlName + ")");
        Debug.println(INDENT, "Count of Map IDs: " + count);

    }

    @Test
    public void testGetMarkerIDsByMapIDAndLinkageBetweenStartPosition() throws Exception {
        int mapID = 1;
        String linkage = "LG01";
        double startPos = 0;
        double endPos = 2.2;
        Set<Integer> markerIDs = manager.getMarkerIDsByMapIDAndLinkageBetweenStartPosition(mapID, linkage, startPos,
                endPos, 0, 1);

        for (Integer markerID : markerIDs) {
            Debug.println(INDENT, "Marker ID: " + markerID);
        }
    }

    @Test
    public void testCountMarkerIDsByMapIDAndLinkageBetweenStartPosition() throws Exception {
        int mapID = 1;
        String linkage = "LG01";
        double startPos = 0;
        double endPos = 2.2;

        long count = manager.countMarkerIDsByMapIDAndLinkageBetweenStartPosition(mapID, linkage, startPos, endPos);

        Debug.println(0, "Count of Marker IDs: " + count);
    }

    @Test
    public void testGetMarkersByMarkerIDs() throws Exception {
        List<Integer> markerIDs = new ArrayList<Integer>();
        markerIDs.add(1317);

        List<Marker> markerList = manager.getMarkersByMarkerIds(markerIDs, 0, 5);

        Debug.printObjects(INDENT, markerList);
    }

    @Test
    public void testCountMarkersByMarkerIDs() throws Exception {
        List<Integer> markerIDs = new ArrayList<Integer>();
        markerIDs.add(1317);

        long count = manager.countMarkersByMarkerIds(markerIDs);

        Debug.println(0, "Count of Markers: " + count);
    }

    @Test
    public void testGetQtlByQtlIdsFromCentral() throws Exception {
        List<Integer> qtlIds = Arrays.asList(1, 2, 3);

        List<QtlDetailElement> results = manager.getQtlByQtlIds(qtlIds, 0, (int) manager.countQtlByQtlIds(qtlIds));

        assertTrue(results.size() > 0);
        Debug.printFormattedObjects(3, results);
    }

    @Test
    public void testGetQtlByQtlIdsFromLocal() throws Exception {
        try {
            testSetQTL(); // to add a qtl_details entry
        } catch (MiddlewareQueryException e) {
            // Ignore. There is already a record for testing.
        }
        List<Integer> qtlIds = Arrays.asList(-1, -2, -3);

        List<QtlDetailElement> results = manager.getQtlByQtlIds(qtlIds, 0, (int) manager.countQtlByQtlIds(qtlIds));

        Debug.printFormattedObjects(INDENT, results);
    }

    @Test
    public void testGetQtlByQtlIdsBothDB() throws Exception {
        List<Integer> qtlIds = Arrays.asList(1, 2, 3, -1, -2, -3);

        List<QtlDetailElement> results = manager.getQtlByQtlIds(qtlIds, 0, (int) manager.countQtlByQtlIds(qtlIds));

        assertTrue(results.size() > 0);
        Debug.printFormattedObjects(3, results);
    }

    @Test
    public void testCountQtlByQtlIdsFromCentral() throws Exception {
        List<Integer> qtlIds = Arrays.asList(1, 2, 3);
        long count = manager.countQtlByQtlIds(qtlIds);
        assertTrue(count > 0);
        Debug.println(0, "testCountQtlByQtlIdsFromCentral() RESULTS: " + count);
    }

    @Test
    public void testCountQtlByQtlIds() throws Exception {
        List<Integer> qtlIds = Arrays.asList(1, 2, 3, -1, -2, -3);
        long count = manager.countQtlByQtlIds(qtlIds);
        assertTrue(count > 0);
        Debug.println(0, "testCountQtlByQtlIds() RESULTS: " + count);
    }

    @Test
    public void testGetQtlDataByQtlTraits() throws Exception {
        List<Integer> qtlTraits = new ArrayList<Integer>(); // Crop tested:  Groundnut
        qtlTraits.add(1001); // "DE"
        List<QtlDataElement> results = manager.getQtlDataByQtlTraits(qtlTraits, 0,
                (int) manager.countQtlDataByQtlTraits(qtlTraits));
        Debug.println(0, "testGetQtlDataByQtlTraits() RESULTS: " + results.size());
        Debug.printObjects(INDENT, results);
    }

    @Test
    public void testCountQtlDataByQtlTraits() throws Exception {
        List<Integer> qtlTraits = new ArrayList<Integer>(); // Crop tested: Groundnut
        qtlTraits.add(1001); // "DE"
        long count = manager.countQtlDataByQtlTraits(qtlTraits);
        Debug.println(0, "testCountQtlDataByQtlTraits() RESULTS: " + count);
    }

    @Test
    public void testGetQtlDetailsByQtlTraits() throws Exception {
        List<Integer> qtlTraits = new ArrayList<Integer>(); // Crop tested: Groundnut
        qtlTraits.add(22396); 
        List<QtlDetailElement> results = manager.getQtlDetailsByQtlTraits(qtlTraits, 0, Integer.MAX_VALUE);
        
        assertTrue(results.size() >= 3);
        Debug.printObjects(INDENT, results);
    }

    @Test
    public void testCountQtlDetailsByQtlTraits() throws Exception {
        List<Integer> qtlTraits = new ArrayList<Integer>(); // Crop tested:  Groundnut
        qtlTraits.add(22396); 
        long count = manager.countQtlDetailsByQtlTraits(qtlTraits);
        assertTrue(count >= 3);
        Debug.println(0, "testCountQtlDataByQtlTraits() RESULTS: " + count);
    }

    @Test
    public void testCountAllelicValuesFromAlleleValuesByDatasetId() throws Exception {
        Integer datasetId = -1; // change value of the datasetid
        long count = manager.countAllelicValuesFromAlleleValuesByDatasetId(datasetId);
        assertNotNull(count);
        Debug.println(0, "testCountAllelicValuesFromAlleleValuesByDatasetId(" + datasetId + ") Results: " + count);
    }

    @Test
    public void testCountAllelicValuesFromCharValuesByDatasetId() throws Exception {
        Integer datasetId = -1; // change value of the datasetid
        long count = manager.countAllelicValuesFromCharValuesByDatasetId(datasetId);
        assertNotNull(count);
        Debug.println(0, "testCountAllelicValuesFromCharValuesByDatasetId(" + datasetId + ") Results: " + count);
    }

    @Test
    public void testCountAllelicValuesFromMappingPopValuesByDatasetId() throws Exception {
        Integer datasetId = 3; // change value of the datasetid
        long count = manager.countAllelicValuesFromMappingPopValuesByDatasetId(datasetId);
        assertNotNull(count);
        Debug.println(0, "testCountAllelicValuesFromMappingPopValuesByDatasetId(" + datasetId + ") Results: " + count);
    }

    @Test
    public void testCountAllParentsFromMappingPopulation() throws Exception {
        long count = manager.countAllParentsFromMappingPopulation();
        assertNotNull(count);
        Debug.println(0, "testCountAllParentsFromMappingPopulation() Results: " + count);
    }

    @Test
    public void testCountMapDetailsByName() throws Exception {
        String nameLike = "GCP-833TestMap"; // change map_name
        long count = manager.countMapDetailsByName(nameLike);
        assertNotNull(count);
        Debug.println(0, "testCountMapDetailsByName(" + nameLike + ") Results: " + count);
    }

    @Test
    public void testGetMapNamesByMarkerIds() throws Exception {
        List<Integer> markerIds = Arrays.asList(-6, 1317, 621, 825, 211);
        java.util.Map<Integer, List<String>> markerMaps = manager.getMapNamesByMarkerIds(markerIds);
        assertTrue(markerMaps.size() > 0);
        for (int i = 0; i < markerIds.size(); i++) {
            Debug.println(3, "Marker ID = " + markerIds.get(i) + " : Map Name/s = " + markerMaps.get(markerIds.get(i)));
        }
    }

    @Test
    public void testCountMarkerInfoByDbAccessionId() throws Exception {
        String dbAccessionId = ""; // change dbAccessionId
        long count = manager.countMarkerInfoByDbAccessionId(dbAccessionId);
        assertNotNull(count);
        Debug.println(0, "testCountMarkerInfoByDbAccessionId(" + dbAccessionId + ") Results: " + count);
    }

    @Test
    public void testCountMarkerInfoByGenotype() throws Exception {
        String genotype = "cv. Tatu"; // change genotype
        long count = manager.countMarkerInfoByGenotype(genotype);
        assertNotNull(count);
        Debug.println(0, "testCountMarkerInfoByGenotype(" + genotype + ") Results: " + count);
    }

    @Test
    public void testCountMarkerInfoByMarkerName() throws Exception {
        String markerName = "SeqTEST"; // change markerName
        long count = manager.countMarkerInfoByMarkerName(markerName);
        assertNotNull(count);
        Debug.println(0, "testCountMarkerInfoByMarkerName(" + markerName + ") Results: " + count);
    }

    @Test
    public void testGetAllParentsFromMappingPopulation() throws Exception {
        long count = manager.countAllParentsFromMappingPopulation();
        List<ParentElement> results = manager.getAllParentsFromMappingPopulation(0, (int) count);
        assertNotNull(results);
        assertFalse(results.isEmpty());
        Debug.println(0, "testGetAllParentsFromMappingPopulation() Results: ");
        Debug.printObjects(INDENT, results);

    }

    @Test
    public void testGetMapDetailsByName() throws Exception {
        String nameLike = "GCP-833TestMap"; // change map_name
        long count = manager.countMapDetailsByName(nameLike);
        List<MapDetailElement> results = manager.getMapDetailsByName(nameLike, 0, (int) count);
        assertNotNull(results);
        assertFalse(results.isEmpty());
        Debug.println(0, "testGetMapDetailsByName(" + nameLike + ")");
        Debug.printObjects(3, results);
    }

    @Test
    public void testGetMarkersByIds() throws Exception {
        List<Integer> markerIds = new ArrayList<Integer>();
        markerIds.add(1);
        markerIds.add(3);

        List<Marker> results = manager.getMarkersByIds(markerIds, 0, 100);
        assertNotNull(results);
        assertFalse(results.isEmpty());
        Debug.println(0, "testGetMarkersByIds(" + markerIds + ") Results: ");
        Debug.printObjects(INDENT, results);
    }
    
    @Test
    public void testGetSNPsByHaplotype() throws Exception {
        String haplotype = "TRIAL";

        List<Marker> results = manager.getSNPsByHaplotype(haplotype);
        Debug.println(0, "testGetSNPsByHaplotype(" + haplotype + ") Results: ");
        Debug.printObjects(INDENT, results);
    }

    @Test
    public void testCountAllMaps() throws Exception {
        long count = manager.countAllMaps(Database.LOCAL);
        assertNotNull(count);
        Debug.println(0, "testCountAllMaps(" + Database.LOCAL + ") Results: " + count);
    }

    @Test
    public void testGetAllMaps() throws Exception {
        List<Map> results = manager.getAllMaps(0, 100, Database.LOCAL);
        assertNotNull(results);
        assertFalse(results.isEmpty());
        Debug.println(0, "testGetAllMaps(" + Database.LOCAL + ") Results:");
        Debug.printObjects(INDENT, results);
    }

    @Test
    public void testCountNidsFromAccMetadatasetByDatasetIds() throws Exception {
        // GROUNDNUT DATABASE
        List<Integer> datasetIds = Arrays.asList(2, 3, 4);
        long count = manager.countNidsFromAccMetadatasetByDatasetIds(datasetIds);
        Debug.println(0, "testCountNidsFromAccMetadatasetByDatasetIds(" + datasetIds + ") = " + count);
    }

    @Test
    public void testCountMarkersFromMarkerMetadatasetByDatasetIds() throws Exception {
        // GROUNDNUT DATABASE
        List<Integer> datasetIds = Arrays.asList(2, 3, 4);
        long count = manager.countMarkersFromMarkerMetadatasetByDatasetIds(datasetIds);
        Debug.println(0, "testCountMarkersFromAccMetadatasetByDatasetIds(" + datasetIds + ") = " + count);
    }

    @Test
    public void testGetMapIdByName() throws Exception {
        // GROUNDNUT DATABASE
        String mapName = "Consensus_Biotic"; // CENTRAL test data
        Integer mapId = manager.getMapIdByName(mapName);
        Debug.println(0, "Map ID for " + mapName + " = " + mapId);
        mapName = "name that does not exist";
        mapId = manager.getMapIdByName(mapName);
        Debug.println(0, "Map ID for " + mapName + " = " + mapId);
        mapName = "TEST"; // local test data INSERT INTO gdms_map (map_id,
                          // map_name, map_type, mp_id, map_desc, map_unit)
                          // values(-1, 'TEST', 'genetic', 0, 'TEST', 'cM');
        mapId = manager.getMapIdByName(mapName);
        Debug.println(0, "Map ID for " + mapName + " = " + mapId);
    }

    @Test
    public void testCountMappingPopValuesByGids() throws Exception {
        List<Integer> gIds = Arrays.asList(1434, 1435, 1436); // IBDBv2
                                                              // Groundnut
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
    public void testGetAllFromMarkerMetadatasetByMarkers() throws Exception {
        List<Integer> markerIds = new ArrayList<Integer>();
        markerIds.add(3302);
        markerIds.add(-1);
        List<MarkerMetadataSet> result = manager.getAllFromMarkerMetadatasetByMarkers(markerIds);
        Debug.println(0, "testGetAllFromMarkerMetadatasetByMarker(" + markerIds + "): ");
        Debug.printObjects(INDENT, result);
    }

    @Test
    public void testGetDatasetDetailsByDatasetIds() throws Exception {
        List<Integer> datasetIds = Arrays.asList(-1, -2, -3, 3, 4, 5);
        List<Dataset> result = manager.getDatasetDetailsByDatasetIds(datasetIds);
        Debug.println(0, "testGetDatasetDetailsByDatasetId(" + datasetIds + "): ");
        Debug.printObjects(INDENT, result);
    }

    @Test
    public void testGetQTLIdsByDatasetIds() throws Exception {
        List<Integer> datasetIds = Arrays.asList(1, -6); // IBDBv2 	Groundnut
        //To get test dataset_ids, run in Local and Central: select qtl_id, dataset_id from gdms_qtl;
        List<Integer> qtlIds = manager.getQTLIdsByDatasetIds(datasetIds);
        Debug.println(0, "testGetQTLIdsByDatasetIds(" + datasetIds + "): " + qtlIds);
    }

    @Test
    public void testGetAllFromAccMetadataset() throws Exception {
        List<Integer> gids = Arrays.asList(2012, 2014, 2016);
        Integer datasetId = 5;
        List<AccMetadataSetPK> result = manager.getAllFromAccMetadataset(gids, datasetId, SetOperation.NOT_IN);
        Debug.println(0, "testGetAllFromAccMetadataset(gid=" + gids + ", datasetId=" + datasetId + "): ");
        Debug.printObjects(INDENT, result);
    }

    @Test
    public void testGetMapAndMarkerCountByMarkers() throws Exception {
        List<Integer> markerIds = Arrays.asList(1317, 1318, 1788, 1053, 2279, 50);
        List<MapDetailElement> details = manager.getMapAndMarkerCountByMarkers(markerIds);
        Debug.println(0, "testGetMapAndMarkerCountByMarkers(" + markerIds + ")");
        Debug.printObjects(INDENT, details);
    }
    
    @Test
    public void testGetAllMTAs() throws Exception {
        List<Mta> result = manager.getAllMTAs();
        Debug.println(0, "testGetAllMTAs(): ");
        Debug.printObjects(INDENT, result);
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
        Debug.println(0, "testGetMTAsByTrait(): ");
        Debug.printObjects(INDENT, result);
    }
    
    @Test
    public void testGetAllSNPs() throws Exception {
        List<Marker> result = manager.getAllSNPMarkers();
        Debug.println(0, "testGetAllSNPs(): ");
        Debug.printObjects(INDENT, result);
    }
    
    @Test
    public void testGetAlleleValuesByMarkers() throws Exception {
    	List<Integer> markerIds = Arrays.asList(-1, -2, 956, 1037);
        List<AllelicValueElement> result = manager.getAlleleValuesByMarkers(markerIds);
        Debug.println(0, "testGetAlleleValuesByMarkers(): ");
        Debug.printObjects(INDENT, result);
    }

    @Test
    public void testDeleteQTLs() throws Exception {
        /*
         * TEST DATA - GROUNDNUT insert into gdms_dataset values(-7, 'TEST
         * DATASET', 'TEST', 'QTL', 'Groundnut', 'Groundnut', '2013-07-24',
         * null, 'int', null, null, null, null, null, null, null); insert into
         * gdms_dataset_users values (-7, 123); insert into gdms_qtl values (-1,
         * 'TEST QTL1', -7); insert into gdms_qtl values (-2, 'TEST QTL2', -7);
         * insert into gdms_qtl_details values (-1, -1, 0, 8.1, 0, null, 0, 2.3,
         * 5.4, 'LG01', null, 'GM1959', 'GM2050', 12.01, 0, null, null, null,
         * null, null); insert into gdms_qtl_details values (-2, -2, 0, 8.1, 0,
         * null, 0, 2.3, 5.4, 'LG01', null, 'GM1959', 'GM2050', 12.01, 0, null,
         * null, null, null, null); //List<Integer> qtlIds = Arrays.asList(-1,
         * -2); //int datasetId = -7;
         */

        List<Qtl> qtls = manager.getAllQtl(0, 1);
        List<Integer> qtlIds = new ArrayList<Integer>();
        if (qtls != null && qtls.size() > 0) {
            qtlIds.add(qtls.get(0).getQtlId());
            int datasetId = qtls.get(0).getDatasetId();
            Debug.println(0, "testDeleteQTLs(qtlIds=" + qtlIds + ", datasetId=" + datasetId);
            manager.deleteQTLs(qtlIds, datasetId);
            Debug.println(0, "done with testDeleteQTLs");
        }

    }

    @Test
    public void testDeleteSSRGenotypingDatasets() throws Exception {
        List<Qtl> qtls = manager.getAllQtl(0, 1);
        if (qtls != null && qtls.size() > 0) {
            int datasetId = qtls.get(0).getDatasetId();
            Debug.println(0, "testDeleteSSRGenotypingDatasets(" + datasetId + ")");
            manager.deleteSSRGenotypingDatasets(datasetId);
            Debug.println(0, "done with testDeleteSSRGenotypingDatasets");
        }
    }

    @Test
    public void testDeleteSNPGenotypingDatasets() throws Exception {
        /*
         * TEST DATA - GROUNDNUT insert into gdms_dataset values(-5, 'TEST
         * DATASET', 'TEST', 'QTL', 'Groundnut', 'Groundnut', '2013-07-24',
         * null, 'int', null, null, null, null, null, null, null); insert into
         * gdms_dataset_users values (-5, 123); insert into gdms_char_values
         * values(-4, -5, 1, 1, 'TEST VALUE'); insert into gdms_acc_metadataset
         * values (-5, 1, 1); insert into gdms_marker_metadataset values(-5, 1);
         */
        int datasetId = -5;
        Debug.println(0, "testDeleteSNPGenotypingDatasets(" + datasetId + ")");
        manager.deleteSNPGenotypingDatasets(datasetId);
        Debug.println(0, "done with testDeleteSNPGenotypingDatasets");
    }

    @Test
    public void testDeleteDArTGenotypingDatasets() throws Exception {
        /*
         * TEST DATA - GROUNDNUT insert into gdms_dataset values(-5, 'TEST
         * DATASET', 'TEST', 'QTL', 'Groundnut', 'Groundnut', '2013-07-24',
         * null, 'int', null, null, null, null, null, null, null); insert into
         * gdms_dataset_users values (-5, 123); insert into gdms_allele_values
         * values(-35, -5, 1, 1, '238:238', '0.0:0.0', 10); insert into
         * gdms_dart_values values(-25, -5, -1, -1, 1.0, 2.0, 3.0, 4.0, 5.0);
         * insert into gdms_acc_metadataset values (-5, 1, 1); insert into
         * gdms_marker_metadataset values(-5, 1);
         */
        int datasetId = -5;
        Debug.println(0, "testDeleteDArTGenotypingDatasets(" + datasetId + ")");
        manager.deleteDArTGenotypingDatasets(datasetId);
        Debug.println(0, "done with testDeleteDArTGenotypingDatasets");
    }

    @Test
    public void testDeleteMappingPopulationDatasets() throws Exception {
        /*
         * TEST DATA - GROUNDNUT insert into gdms_dataset values(-5, 'TEST
         * DATASET', 'TEST', 'QTL', 'Groundnut', 'Groundnut', '2013-07-24',
         * null, 'int', null, null, null, null, null, null, null); insert into
         * gdms_dataset_users values (-5, 123); insert into
         * gdms_mapping_pop_values values( -25, 'X', -5, 1434, 2537); insert
         * into gdms_mapping_pop values(-5, 'test', 956, 1042, 999, 'test',
         * 'test', 'test', 0); insert into gdms_acc_metadataset values (-5, 1,
         * 1); insert into gdms_marker_metadataset values(-5, 1);
         */
        int datasetId = -5;
        Debug.println(0, "testDeleteMappingPopulationDatasets(" + datasetId + ")");
        manager.deleteMappingPopulationDatasets(datasetId);
        Debug.println(0, "done with testDeleteMappingPopulationDatasets");
    }

    @Test
    public void testGetQtlDetailsByMapId() throws Exception {
        try {
            testSetQTL(); // to add a qtl_details entry with map_id = 1
        } catch (MiddlewareQueryException e) {
            // Ignore. There is already a record for testing.
        }
        int mapId = 1;
        List<QtlDetails> qtls = manager.getQtlDetailsByMapId(mapId);
        Debug.println(0, "testGetQtlDetailsByMapId(" + mapId + ")");
        Debug.printObjects(INDENT, qtls);
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
        /*
         * TEST DATA - GROUNDNUT INSERT INTO gdms_map
         * VALUES(-1,'BC-1','genetic',0,'BC-1','cM'); INSERT INTO gdms_map
         * VALUES(-2,'Consensus_Abiotic','genetic',0,'Consensus_Abiotic','cM');
         * INSERT INTO gdms_markers_onmap VALUES(-1,1317,0,0,'BC-1_b11'); INSERT
         * INTO gdms_markers_onmap VALUES(-1,1318,4.2,4.2,'BC-1_b11'); INSERT
         * INTO gdms_markers_onmap VALUES(-1,1321,0,0,'BC-1_b10'); INSERT INTO
         * gdms_markers_onmap VALUES(-1,1345,0,0,'BC-1_b09'); INSERT INTO
         * gdms_markers_onmap VALUES(-2,1776,102.84,102.84,'LG11'); INSERT INTO
         * gdms_markers_onmap VALUES(-2,1794,33.3,33.3,'LG14'); INSERT INTO
         * gdms_markers_onmap VALUES(-2,1782,158.9,158.9,'LG12');
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
        Debug.println(0, "testGetMarkerFromCharValuesByGids(): " + result.size() + "\n\t" + result);
    }

    @Test
    public void testGetMarkerFromAlleleValuesByGids() throws Exception {
        List<Integer> gIds = Arrays.asList(2213, 2214);
        List<Integer> result = manager.getMarkerFromAlleleValuesByGids(gIds);
        Debug.println(0, "testGetMarkerFromAlleleValuesByGids(): " + result.size() + "\n\t" + result);
    }

    @Test
    public void testGetMarkerFromMappingPopValuesByGids() throws Exception {
        List<Integer> gIds = Arrays.asList(1434, 1435);
        List<Integer> result = manager.getMarkerFromMappingPopByGids(gIds);
        Debug.println(0, "testGetMarkerFromMappingPopValuesByGids(): " + result.size() + "\n\t" + result);
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
        Dataset dataset = new Dataset(null, "TEST DATASET NAME", "DATASET DESC", "MTA", "GENUS", "SPECIES", null,
                "REMARKS", "int", null, "METHOD", "0.43", "INSTITUTE", "PI", "EMAIL", "OBJECTIVE");
        Mta mta = new Mta(null, 1, null, 1, "LINKAGE GROUP", 2.1f, 3, 1, "HVALLELE", "EXPERIMENT", 3.3f, 2.2f);
        DatasetUsers users = new DatasetUsers(null, 1);
        manager.addMTA(dataset, mta, users);
        Debug.println(0, "done with testAddMTA");
    }
    
    @Test
    public void testGetDartMarkerDetails() throws Exception {
    	List<Integer> markerIds = Arrays.asList(-1, -2);
        List<DartValues> result = manager.getDartMarkerDetails(markerIds);
        Debug.printObjects(0, result);
    }
    

    @Test
    public void testUpdateMarkerInfoExisting() throws Exception {
    	// Update existing Marker, MarkerAlias, MarkerDetails, MarkerUserInfo
        List<Object> markerRecords = createMarkerRecords();
        Marker marker = (Marker) markerRecords.get(0);
        MarkerAlias markerAlias = (MarkerAlias) markerRecords.get(1);
        MarkerDetails markerDetails = (MarkerDetails) markerRecords.get(2);
        MarkerUserInfo markerUserInfo = (MarkerUserInfo) markerRecords.get(3);

        // Insert first
        Boolean addStatus = manager.setSNPMarkers(marker, markerAlias, markerDetails, markerUserInfo);
        if (addStatus) {
            Debug.println(0, "ADDED: ");
            Debug.println(3, marker.toString());
            Debug.println(3, markerAlias.toString());
            Debug.println(3, markerDetails.toString());
            Debug.println(3, markerUserInfo.toString());
        }

        // Then update the newly-inserted set of records
        Integer updateId = (int) (Math.random() * 100);
        marker.setRemarks("UPDATE" + updateId);
        markerAlias.setAlias(markerAlias.getAlias() + updateId);
        markerDetails.setSequence(updateId.toString());
        markerUserInfo.setContact(updateId.toString());
        
        addStatus = manager.updateMarkerInfo(marker, markerAlias, markerDetails, markerUserInfo);
        if (addStatus) {
            Debug.println(0, "UPDATED: ");
            Debug.println(3, marker.toString());
            Debug.println(3, markerAlias.toString());
            Debug.println(3, markerDetails.toString());
            Debug.println(3, markerUserInfo.toString());
        }
    }
    
    
    @Test
    public void testUpdateMarkerInfoNewRecords() throws Exception {
        
        // Add a new set of MarkerAlias, MarkerDetails, MarkerUserInfo for the given Marker - should insert, but update
        List<Object> markerRecords = createMarkerRecords();
        Marker markerUA = (Marker) markerRecords.get(0);
        markerUA.setMarkerType(GdmsType.TYPE_UA.getValue());

        Integer markerId = manager.addMarker(markerUA);
        
        Debug.println(0, "MARKER ADDED: " + markerUA);
        
        MarkerAlias markerAlias = (MarkerAlias) markerRecords.get(1);
        markerAlias.setMarkerId(markerId);
        MarkerDetails markerDetails = (MarkerDetails) markerRecords.get(2);
        markerDetails.setMarkerId(markerId);
        MarkerUserInfo markerUserInfo = (MarkerUserInfo) markerRecords.get(3);
        markerUserInfo.setMarkerId(markerId);
    	
    	Boolean addStatus = manager.updateMarkerInfo(markerUA, markerAlias, markerDetails, markerUserInfo);
        if (addStatus) {
            Debug.println(0, "MarkerAlias/MarkerDetails/MarkerUserInfo ADDED TO AN EXISTING Marker: ");
            Debug.println(3, markerUA.toString());
            Debug.println(3, markerAlias.toString());
            Debug.println(3, markerDetails.toString());
            Debug.println(3, markerUserInfo.toString());
        }
    	
    }
    
    @Test
    public void testUpdateMarkerInfoModifyMarkerNameSpecies() throws Exception {

    	// Get an existing marker from local db 
    	Marker marker = manager.getMarkersByIds(Arrays.asList(-1, -2, -3, -4, -5, -6, -7, -8, -9, -10), 0, 1).get(0);
    	Debug.println(3, "Existing marker:" + marker.toString());
    	
    	List<Object> markerRecords = createMarkerRecords();
        MarkerAlias markerAlias = (MarkerAlias) markerRecords.get(1);
        MarkerDetails markerDetails = (MarkerDetails) markerRecords.get(2);
        MarkerUserInfo markerUserInfo = (MarkerUserInfo) markerRecords.get(3);

        Integer updateId = (int) (Math.random() * 100);

        // Try to update marker name - should not be allowed.
        try{
        	marker.setMarkerName(marker.getMarkerName() + updateId);
        	manager.updateMarkerInfo(marker, markerAlias, markerDetails, markerUserInfo);
        } catch (MiddlewareQueryException e){
        	Debug.println(0, "Caught exception: Marker name and species cannot be updated.");
        	assertTrue(e.getMessage().contains("Marker name and species cannot be updated."));
        }

        // Try to update species - should not be allowed.
        try{
        	marker.setSpecies(marker.getSpecies() + updateId);
        	manager.updateMarkerInfo(marker, markerAlias, markerDetails, markerUserInfo);
        } catch (MiddlewareQueryException e){
        	Debug.println(0, "Caught exception: Marker name and species cannot be updated.");
        	assertTrue(e.getMessage().contains("Marker name and species cannot be updated."));
        }

    }
    
    @Test
    public void testUpdateMarkerInfoInCentral() throws Exception {

    	// Get an existing marker from local db 
    	Marker marker = manager.getMarkersByIds(Arrays.asList(3316), 0, 1).get(0);
    	Debug.println(3, "Existing marker:" + marker.toString());
    	
    	List<Object> markerRecords = createMarkerRecords();
        MarkerAlias markerAlias = (MarkerAlias) markerRecords.get(1);
        MarkerDetails markerDetails = (MarkerDetails) markerRecords.get(2);
        MarkerUserInfo markerUserInfo = (MarkerUserInfo) markerRecords.get(3);

        try{
        	manager.updateMarkerInfo(marker, markerAlias, markerDetails, markerUserInfo);
        } catch (MiddlewareQueryException e){
        	Debug.println(0, "Marker is in central database and cannot be updated.");
        	assertTrue(e.getMessage().contains("Marker is in central database and cannot be updated."));
        }

    }
    

    @AfterClass
    public static void tearDown() throws Exception {
        factory.close();
    }

}
