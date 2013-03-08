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
import java.util.Date;
import java.util.List;

import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
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
import org.generationcp.middleware.pojos.gdms.ParentElement;
import org.generationcp.middleware.pojos.gdms.Qtl;
import org.generationcp.middleware.pojos.gdms.QtlDetailElement;
import org.generationcp.middleware.pojos.gdms.QtlDetails;
import org.generationcp.middleware.pojos.gdms.QtlDetailsPK;
import org.junit.AfterClass;
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
        System.out.println("testGetNameIdsByGermplasmIds(" + germplasmIds + ") RESULTS: " + results);
    }

    @Test
    public void testGetNamesByNameIds() throws Exception {
        List<Integer> nameIds = new ArrayList<Integer>();
        nameIds.add(Integer.valueOf(-1));
        nameIds.add(Integer.valueOf(-2));
        nameIds.add(Integer.valueOf(-3));
        List<Name> results = manager.getNamesByNameIds(nameIds);
        System.out.println("testGetNamesByNameIds(" + nameIds + ") RESULTS: " + results);
    }

    @Test
    public void testGetNameByNameId() throws Exception {
        Integer nameId = -1;
        Name name = manager.getNameByNameId(nameId);
        System.out.println("testGetNameByNameId(nameId=" + nameId + ") RESULTS: " + name);
    }

    @Test
    public void testGetFirstFiveMaps() throws Exception {
        List<Map> maps = manager.getAllMaps(0, 5, Database.LOCAL);
        System.out.println("RESULTS (testGetFirstFiveMaps): " + maps);
    }

    @Test
    public void testGetMapInfoByMapName() throws Exception {
        String mapName = ""; //TODO: test with a given map name
        List<MapInfo> results = manager.getMapInfoByMapName(mapName, Database.LOCAL);
        System.out.println("testGetMapInfoByMapName(mapName=" + mapName + ") RESULTS: " + results);
    }

    @Test
    public void testCountDatasetNames() throws Exception {
        long results = manager.countDatasetNames(Database.LOCAL);
        System.out.println("testCountDatasetNames(Database.LOCAL) RESULTS: " + results);
    }

    @Test
    public void testGetDatasetNames() throws Exception {
        List<String> results = manager.getDatasetNames(0, 5, Database.LOCAL);
        System.out.println("testGetDatasetNames(0,5,Database.Local) RESULTS: " + results);
    }

    @Test
    public void testGetDatasetDetailsByDatasetName() throws Exception {
        String datasetName = "MARS";
        List<DatasetElement> results = manager.getDatasetDetailsByDatasetName(datasetName, Database.LOCAL);
        System.out.println("testGetDatasetDetailsByDatasetName(" + datasetName + ",LOCAL) RESULTS: " + results);
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
        System.out.println("getMarkerIdsByMarkerNames (" + markerNames + ") RESULTS: " + markerIds);
    }

    @Test
    public void testGetMarkerIdsByDatasetId() throws Exception {
        Integer datasetId = Integer.valueOf(2);
        List<Integer> results = manager.getMarkerIdsByDatasetId(datasetId);
        System.out.println("testGetMarkerIdByDatasetId(" + datasetId + ") RESULTS: " + results);
    }

    @Test
    public void testGetParentsByDatasetId() throws Exception {
        Integer datasetId = Integer.valueOf(2);
        List<ParentElement> results = manager.getParentsByDatasetId(datasetId);
        System.out.println("testGetParentsByDatasetId(" + datasetId + ") RESULTS: " + results);
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
        System.out.println("testGetMarkerTypeByMarkerIds(" + markerIds + ") RESULTS: " + results);
    }

    @Test
    public void testGetMarkerNamesByGIds() throws Exception {
        List<Integer> gIds = new ArrayList<Integer>();
        gIds.add(Integer.valueOf(-4072));
        gIds.add(Integer.valueOf(-4070));
        gIds.add(Integer.valueOf(-4069));

        List<MarkerNameElement> results = manager.getMarkerNamesByGIds(gIds);
        System.out.println("testGetMarkerNamesByGIds(" + gIds + ") RESULTS: " + results);
    }

    @Test
    public void testGetGermplasmNamesByMarkerNames() throws Exception {
        List<String> markerNames = new ArrayList<String>();
        markerNames.add("1_0001");
        markerNames.add("1_0007");
        markerNames.add("1_0013");

        List<GermplasmMarkerElement> results = (List<GermplasmMarkerElement>) manager.getGermplasmNamesByMarkerNames(markerNames,
                Database.LOCAL);
        System.out.println("testGetGermplasmNamesByMarkerNames(" + markerNames + ")  RESULTS: " + results);
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
        System.out.println("testGetMappingValuesByGidsAndMarkerNames(" + gids + ", " + markerNames + ") RESULTS: " + mappingValues);
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
        System.out.println("testGetAllelicValuesByGidsAndMarkerNames(" + gids + ", " + markerNames + ") RESULTS: " + allelicValues);
    }

    @Test
    public void testGetAllelicValuesFromCharValuesByDatasetId() throws Exception {
        Integer datasetId = Integer.valueOf(2);
        long count = manager.countAllelicValuesFromCharValuesByDatasetId(datasetId);
        List<AllelicValueWithMarkerIdElement> allelicValues = manager.getAllelicValuesFromCharValuesByDatasetId(datasetId, 0, (int) count);
        System.out.println("testGetAllelicValuesFromCharValuesByDatasetId(" + datasetId + ") RESULTS: " + allelicValues);
    }

    @Test
    public void testGetAllelicValuesFromAlleleValuesByDatasetId() throws Exception {
        Integer datasetId = Integer.valueOf(2);
        long count = manager.countAllelicValuesFromCharValuesByDatasetId(datasetId);
        List<AllelicValueWithMarkerIdElement> allelicValues = manager
                .getAllelicValuesFromAlleleValuesByDatasetId(datasetId, 0, (int) count);
        System.out.println("testGetAllelicValuesFromAlleleValuesByDatasetId(" + datasetId + ") RESULTS: " + allelicValues);
    }

    @Test
    public void testGetAllelicValuesFromMappingPopValuesByDatasetId() throws Exception {
        Integer datasetId = Integer.valueOf(2);
        long count = manager.countAllelicValuesFromCharValuesByDatasetId(datasetId);
        List<AllelicValueWithMarkerIdElement> allelicValues = manager.getAllelicValuesFromMappingPopValuesByDatasetId(datasetId, 0,
                (int) count);
        System.out.println("testGetAllelicValuesFromMappingPopValuesByDatasetId(" + datasetId + ") RESULTS: " + allelicValues);
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
        System.out.println("testGetMarkerNamesByMarkerIds(" + markerIds + ") RESULTS: ");
        for (MarkerIdMarkerNameElement e : markerNames) {
            System.out.println(e.getMarkerId() + " : " + e.getMarkerName());
        }
    }

    @Test
    public void testGetAllMarkerTypes() throws Exception {
        List<String> markerTypes = manager.getAllMarkerTypes(0, 10);
        System.out.println("testGetAllMarkerTypes(0,10) RESULTS: " + markerTypes);
    }

    @Test
    public void testCountAllMarkerTypesLocal() throws Exception {
        long result = manager.countAllMarkerTypes(Database.LOCAL);
        System.out.println("testCountAllMarkerTypes(Database.LOCAL) RESULTS: " + result);
    }

    @Test
    public void testCountAllMarkerTypesCentral() throws Exception {
        long result = manager.countAllMarkerTypes(Database.CENTRAL);
        System.out.println("testCountAllMarkerTypes(Database.CENTRAL) RESULTS: " + result);
    }

    @Test
    public void testGetMarkerNamesByMarkerType() throws Exception {
        String markerType = "asdf";
        List<String> markerNames = manager.getMarkerNamesByMarkerType(markerType, 1, 10);
        System.out.println("testGetMarkerNamesByMarkerType(" + markerType + ") RESULTS: " + markerNames);
    }

    @Test
    public void testCountMarkerNamesByMarkerType() throws Exception {
        String markerType = "asdf";
        long result = manager.countMarkerNamesByMarkerType(markerType);
        System.out.println("testCountMarkerNamesByMarkerType(" + markerType + ") RESULTS: " + result);
    }

    @Test
    public void testGetMarkerInfoByMarkerName() throws Exception {
        String markerName = "1_0437";
        long count = manager.countMarkerInfoByMarkerName(markerName);
        System.out.println("countMarkerInfoByMarkerName(" + markerName + ")  RESULTS: " + count);
        List<MarkerInfo> results = manager.getMarkerInfoByMarkerName(markerName, 0, (int) count);
        System.out.println("testGetMarkerInfoByMarkerName(" + markerName + ") RESULTS: " + results);
    }

    @Test
    public void testGetMarkerInfoByGenotype() throws Exception {
        String genotype = "";
        long count = manager.countMarkerInfoByGenotype(genotype);
        System.out.println("countMarkerInfoByGenotype(" + genotype + ") RESULTS: " + count);
        List<MarkerInfo> results = manager.getMarkerInfoByGenotype(genotype, 0, (int) count);
        System.out.println("testGetMarkerInfoByGenotype(" + genotype + ") RESULTS: " + results);
    }

    @Test
    public void testGetMarkerInfoByDbAccessionId() throws Exception {
        String dbAccessionId = "";
        long count = manager.countMarkerInfoByDbAccessionId(dbAccessionId);
        System.out.println("countMarkerInfoByDbAccessionId(" + dbAccessionId + ")  RESULTS: " + count);
        List<MarkerInfo> results = manager.getMarkerInfoByDbAccessionId(dbAccessionId, 0, (int) count);
        System.out.println("testGetMarkerInfoByDbAccessionId(" + dbAccessionId + ")  RESULTS: " + results);
    }

    @Test
    public void testGetGidsFromCharValuesByMarkerId() throws Exception {
        Integer markerId = 1;
        List<Integer> gids = manager.getGIDsFromCharValuesByMarkerId(markerId, 1, 10);
        System.out.println("testGetGidsFromCharValuesByMarkerId(" + markerId + ") RESULTS: " + gids);
    }

    @Test
    public void testCountGidsFromCharValuesByMarkerId() throws Exception {
        Integer markerId = 1;
        long result = manager.countGIDsFromCharValuesByMarkerId(markerId);
        System.out.println("testCountGidsFromCharValuesByMarkerId(" + markerId + ") RESULTS: " + result);
    }

    @Test
    public void testGetGidsFromAlleleValuesByMarkerId() throws Exception {
        Integer markerId = 1;
        List<Integer> gids = manager.getGIDsFromCharValuesByMarkerId(markerId, 1, 10);
        System.out.println("testGetGidsFromAlleleValuesByMarkerId(" + markerId + ") RESULTS: " + gids);
    }

    @Test
    public void testCountGidsFromAlleleValuesByMarkerId() throws Exception {
        Integer markerId = 1;
        long result = manager.countGIDsFromCharValuesByMarkerId(markerId);
        System.out.println("testCountGidsFromAlleleValuesByMarkerId(" + markerId + ") RESULTS: " + result);
    }

    @Test
    public void testGetGidsFromMappingPopValuesByMarkerId() throws Exception {
        Integer markerId = 1;
        List<Integer> gids = manager.getGIDsFromMappingPopValuesByMarkerId(markerId, 1, 10);
        System.out.println("testGetGidsFromMappingPopValuesByMarkerId(" + markerId + ") RESULTS: " + gids);
    }

    @Test
    public void testCountGidsFromMappingPopValuesByMarkerId() throws Exception {
        Integer markerId = 1;
        long result = manager.countGIDsFromMappingPopValuesByMarkerId(markerId);
        System.out.println("testCountGidsFromMappingPopValuesByMarkerId(" + markerId + ") RESULTS: " + result);
    }

    @Test
    public void testGetAllDbAccessionIdsFromMarker() throws Exception {
        List<String> dbAccessionIds = manager.getAllDbAccessionIdsFromMarker(1, 10);
        System.out.println("testGetAllDbAccessionIdsFromMarker(1,10) RESULTS: " + dbAccessionIds);
    }

    @Test
    public void testCountAllDbAccessionIdsFromMarker() throws Exception {
        long result = manager.countAllDbAccessionIdsFromMarker();
        System.out.println("testCountAllDbAccessionIdsFromMarker() RESULTS: " + result);
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

        System.out.println("testGetNidsFromAccMetadatasetByDatasetIds RESULTS: " + nids);
        System.out.println("testGetNidsFromAccMetadatasetByDatasetIds with gid filter RESULTS: " + nidsWithGidFilter);
    }

    @Test
    public void testGetDatasetIdsForFingerPrinting() throws Exception {
        List<Integer> datasetIds = manager.getDatasetIdsForFingerPrinting(0, (int) manager.countDatasetIdsForFingerPrinting());
        System.out.println("testGetDatasetIdsForFingerPrinting() RESULTS: " + datasetIds);
    }

    @Test
    public void testCountDatasetIdsForFingerPrinting() throws Exception {
        long count = manager.countDatasetIdsForFingerPrinting();
        System.out.println("testCountDatasetIdsForFingerPrinting() RESULTS: " + count);
    }

    @Test
    public void testGetDatasetIdsForMapping() throws Exception {
        List<Integer> datasetIds = manager.getDatasetIdsForMapping(0, (int) manager.countDatasetIdsForMapping());
        System.out.println("testGetDatasetIdsForMapping() RESULTS: " + datasetIds);
    }

    @Test
    public void testCountDatasetIdsForMapping() throws Exception {
        long count = manager.countDatasetIdsForMapping();
        System.out.println("testCountDatasetIdsForMapping() RESULTS: " + count);
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
        System.out.println("testGetGdmsAccMetadatasetByGid() RESULTS: ");
        for (AccMetadataSetPK accMetadataSet : accMetadataSets) {
            System.out.println(accMetadataSet.toString());
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
        System.out.println("testCountGdmsAccMetadatasetByGid() RESULTS: " + count);
    }

    @Test
    public void testGetMarkersByGidAndDatasetIds() throws Exception {
        Integer gid = Integer.valueOf(-2215);    // Crop Tested: Groundnut

        List<Integer> datasetIds = new ArrayList<Integer>();
        datasetIds.add(Integer.valueOf(1));
        datasetIds.add(Integer.valueOf(2));

        List<Integer> markerIds = manager.getMarkersByGidAndDatasetIds(gid, datasetIds, 0, 
                (int) manager.countMarkersByGidAndDatasetIds(gid, datasetIds));
        System.out.println("testGetMarkersByGidAndDatasetIds() RESULTS: " + markerIds);
    }

    @Test
    public void testCountMarkersByGidAndDatasetIds() throws Exception { 
        Integer gid = Integer.valueOf(-2215);    // Crop Tested: Groundnut

        List<Integer> datasetIds = new ArrayList<Integer>();
        datasetIds.add(Integer.valueOf(1));
        datasetIds.add(Integer.valueOf(2));

        long count = manager.countMarkersByGidAndDatasetIds(gid, datasetIds);
        System.out.println("testCountGdmsAccMetadatasetByGid() RESULTS: " + count);
    }
    
    @Test
    public void testCountAlleleValuesByGids() throws Exception { 
        List<Integer> germplasmIds = new ArrayList<Integer>(); 
        germplasmIds.add(Integer.valueOf(956)); // Crop Tested: Groundnut
        germplasmIds.add(Integer.valueOf(1042));
        germplasmIds.add(Integer.valueOf(-2213));
        germplasmIds.add(Integer.valueOf(-2215));
        long count = manager.countAlleleValuesByGids(germplasmIds);
        System.out.println("testCountAlleleValuesByGids() RESULTS: " + count);
    }
    

    @Test
    public void testCountCharValuesByGids() throws Exception { 
        List<Integer> germplasmIds = new ArrayList<Integer>(); 
        germplasmIds.add(Integer.valueOf(956)); // Please replace the gids found in the target crop to be used in testing
        germplasmIds.add(Integer.valueOf(1042));
        germplasmIds.add(Integer.valueOf(-2213));
        germplasmIds.add(Integer.valueOf(-2215));
        long count = manager.countCharValuesByGids(germplasmIds);
        System.out.println("testCountCharValuesByGids() RESULTS: " + count);
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
        System.out.println("testGetIntAlleleValuesForPolymorphicMarkersRetrieval() RESULTS: " + results);
    }

    @Test
    public void testCountIntAlleleValuesForPolymorphicMarkersRetrieval() throws Exception { 
        List<Integer> germplasmIds = new ArrayList<Integer>(); 
        germplasmIds.add(Integer.valueOf(956)); // Crop Tested: Groundnut
        germplasmIds.add(Integer.valueOf(1042));
        germplasmIds.add(Integer.valueOf(-2213));
        germplasmIds.add(Integer.valueOf(-2215));
        long count = manager.countIntAlleleValuesForPolymorphicMarkersRetrieval(germplasmIds);
        System.out.println("testCountIntAlleleValuesForPolymorphicMarkersRetrieval() RESULTS: " + count);
    }    
    
    @Test
    public void testGetCharAlleleValuesForPolymorphicMarkersRetrieval() throws Exception {
        List<Integer> germplasmIds = new ArrayList<Integer>(); 
        germplasmIds.add(Integer.valueOf(956)); // Please replace the gids found in the target crop to be used in testing
        germplasmIds.add(Integer.valueOf(1042));
        germplasmIds.add(Integer.valueOf(-2213));
        germplasmIds.add(Integer.valueOf(-2215));

        List<AllelicValueElement> results = manager.getCharAlleleValuesForPolymorphicMarkersRetrieval(germplasmIds, 0, 
                (int) manager.countCharAlleleValuesForPolymorphicMarkersRetrieval(germplasmIds));
        System.out.println("testGetCharAlleleValuesForPolymorphicMarkersRetrieval() RESULTS: " + results);
    }


    @Test
    public void testCountCharAlleleValuesForPolymorphicMarkersRetrieval() throws Exception { 
        List<Integer> germplasmIds = new ArrayList<Integer>(); 
        germplasmIds.add(Integer.valueOf(956)); // Please replace the gids found in the target crop to be used in testing
        germplasmIds.add(Integer.valueOf(1042));
        germplasmIds.add(Integer.valueOf(-2213));
        germplasmIds.add(Integer.valueOf(-2215));
        long count = manager.countCharAlleleValuesForPolymorphicMarkersRetrieval(germplasmIds);
        System.out.println("testCountCharAlleleValuesForPolymorphicMarkersRetrieval() RESULTS: " + count);
    }    
    
    @Test
    public void testGetNIdsByDatasetIdsAndMarkerIdsAndNotGIds() throws Exception { 
        List<Integer> gIds = new ArrayList<Integer>(); 
        gIds.add(Integer.valueOf(29)); // Please replace the germplasm ids found in the target crop to be used in testing
        gIds.add(Integer.valueOf(303));
        gIds.add(Integer.valueOf(4950));
        
        List<Integer> datasetIds = new ArrayList<Integer>(); 
        datasetIds.add(Integer.valueOf(2)); // Please replace the dataset ids found in the target crop to be used in testing
        
        List<Integer> markerIds = new ArrayList<Integer>(); 
        markerIds.add(Integer.valueOf(6803)); // Please replace the marker ids found in the target crop to be used in testing

        List<Integer> nIdList = manager.getNIdsByMarkerIdsAndDatasetIdsAndNotGIds(datasetIds, markerIds, gIds, 1, 5);
        System.out.println("testGetNIdsByDatasetIdsAndMarkerIdsAndNotGIds() RESULTS: " + nIdList);
    }  
    
    @Test
    public void testcCountNIdsByDatasetIdsAndMarkerIdsAndNotGIds() throws Exception { 
        List<Integer> gIds = new ArrayList<Integer>(); 
        gIds.add(Integer.valueOf(29)); // Please replace the germplasm ids found in the target crop to be used in testing
        gIds.add(Integer.valueOf(303));
        gIds.add(Integer.valueOf(4950));
        
        List<Integer> datasetIds = new ArrayList<Integer>(); 
        datasetIds.add(Integer.valueOf(2)); // Please replace the dataset ids found in the target crop to be used in testing
        
        List<Integer> markerIds = new ArrayList<Integer>(); 
        markerIds.add(Integer.valueOf(6803)); // Please replace the marker ids found in the target crop to be used in testing

        int count = manager.countNIdsByMarkerIdsAndDatasetIdsAndNotGIds(datasetIds, markerIds, gIds);
        System.out.println("testcCountNIdsByDatasetIdsAndMarkerIdsAndNotGIds() RESULTS: " + count);
    }  
    
    @Test
    public void testGetNIdsByDatasetIdsAndMarkerIds() throws Exception { 
        
        List<Integer> datasetIds = new ArrayList<Integer>(); 
        datasetIds.add(Integer.valueOf(2)); // Please replace the dataset ids found in the target crop to be used in testing
        
        List<Integer> markerIds = new ArrayList<Integer>(); 
        markerIds.add(Integer.valueOf(6803)); // Please replace the marker ids found in the target crop to be used in testing

        List<Integer> nIdList = manager.getNIdsByMarkerIdsAndDatasetIds(datasetIds, markerIds, 5, 7);
        System.out.println("testGetNIdsByDatasetIdsAndMarkerIds() RESULTS: " + nIdList);
    }  
    
    @Test
    public void testCountNIdsByDatasetIdsAndMarkerIds() throws Exception { 
        
        List<Integer> datasetIds = new ArrayList<Integer>(); 
        datasetIds.add(Integer.valueOf(2)); // Please replace the dataset ids found in the target crop to be used in testing
        
        List<Integer> markerIds = new ArrayList<Integer>(); 
        markerIds.add(Integer.valueOf(6803)); // Please replace the marker ids found in the target crop to be used in testing

        int count = manager.countNIdsByMarkerIdsAndDatasetIds(datasetIds, markerIds);
        System.out.println("testCountNIdsByDatasetIdsAndMarkerIds() RESULTS: " + count);
    }  
    
    

    @Test
    public void testGetMappingAlleleValuesForPolymorphicMarkersRetrieval() throws Exception {
        List<Integer> germplasmIds = new ArrayList<Integer>(); 
        germplasmIds.add(Integer.valueOf(1434)); // Please replace the gids found in the target crop to be used in testing
        germplasmIds.add(Integer.valueOf(1435));

        List<AllelicValueElement> results = manager.getMappingAlleleValuesForPolymorphicMarkersRetrieval(germplasmIds, 0, 
                (int) manager.countMappingAlleleValuesForPolymorphicMarkersRetrieval(germplasmIds));
        System.out.println("testGetMappingAlleleValuesForPolymorphicMarkersRetrieval() RESULTS: " + results);
    }


    @Test
    public void testCountMappingAlleleValuesForPolymorphicMarkersRetrieval() throws Exception { 
        List<Integer> germplasmIds = new ArrayList<Integer>(); 
        germplasmIds.add(Integer.valueOf(1434)); // Please replace the gids found in the target crop to be used in testing
        germplasmIds.add(Integer.valueOf(1435));
        long count = manager.countMappingAlleleValuesForPolymorphicMarkersRetrieval(germplasmIds);
        System.out.println("testCountMappingAlleleValuesForPolymorphicMarkersRetrieval() RESULTS: " + count);
    }    
    
    @Test
    public void testGetAllQtl() throws Exception {
        List<Qtl> qtls = manager.getAllQtl(0, (int) manager.countAllQtl());
        System.out.println("testGetAllQtl() RESULTS: " + qtls);
    }

    @Test
    public void testCountAllQtl() throws Exception {
        long result = manager.countAllQtl();
        System.out.println("testCountAllQtl() RESULTS: " + result);
    }


    

    @Test
    public void testGetQtlByName() throws Exception {
        String qtlName = "SLA%";     // Crop tested: Groundnut

        List<QtlDetailElement> results = manager.getQtlByName(qtlName, 0, 
                (int) manager.countQtlByName(qtlName));
        System.out.println("testGetQtlByName() RESULTS: " + results);
    }


    @Test
    public void testCountQtlByName() throws Exception { 
        String qtlName = "SLA%";     // Crop tested: Groundnut
        long count = manager.countQtlByName(qtlName);
        System.out.println("testCountQtlByName() RESULTS: " + count);
    }    


    @Test
    public void testGetQtlByTrait() throws Exception {
        String qtlTrait = "SL%";     // Crop tested: Groundnut

        List<Integer> results = manager.getQtlByTrait(qtlTrait, 0, 
                (int) manager.countQtlByTrait(qtlTrait));
        System.out.println("testGetQtlByTrait() RESULTS: " + results);
    }


    @Test
    public void testCountQtlByTrait() throws Exception { 
        String qtlTrait = "SL%";     // Crop tested: Groundnut
        long count = manager.countQtlByTrait(qtlTrait);
        System.out.println("testCountQtlByTrait() RESULTS: " + count);
    }    
    
    @Test
    public void testGetMarkersByQtl() throws Exception { 
        String qtlName = "HI Control 08_AhI";     // Crop tested: Groundnut
        String chromosome = "LG01";
        int min = 0;
        int max = 10;
        List<Marker> results = manager.getMarkersByQtl(qtlName, chromosome, min, max, 0, (int) manager.countMarkersByQtl(qtlName, chromosome, min, max));
        System.out.println("testGetMarkersByQtl() RESULTS: " + results);
    }

    @Test
    public void testCountMarkersByQtl() throws Exception { 
        String qtlName = "HI Control 08_AhI";     // Crop tested: Groundnut
        String chromosome = "LG01";
        int min = 0;
        int max = 10;
        long count = manager.countMarkersByQtl(qtlName, chromosome, min, max);
        System.out.println("testCountMarkersByQtl() RESULTS: " + count);

    }


    @Test
    public void getAllParentsFromMappingPopulation() throws Exception {
    	int start = 0;
    	int end = 10;
        List<ParentElement> parentElements = manager.getAllParentsFromMappingPopulation(start, end);
        System.out.println("getAllParentsFromMappingPopulation(" + start + "," + end + ")");
        for (ParentElement parentElement : parentElements) {
            System.out.println("Parent A GId: " + parentElement.getParentAGId() + "  |  Parent B GId: "+parentElement.getParentBGId());
        }
        
    }    

    @Test
    public void countAllParentsFromMappingPopulation() throws Exception {
        long parentElementsCount = manager.countAllParentsFromMappingPopulation();
        System.out.println("countAllParentsFromMappingPopulation()");
        System.out.println("Count: " + parentElementsCount);
    }
    
    @Test
    public void getMapDetailsByName() throws Exception {
    	String nameLike = "tag%";
    	int start = 0;
    	int end = 10;
        List<Map> maps = manager.getMapDetailsByName(nameLike, start, end);
        System.out.println("getMapDetailsByName('"+nameLike+"'," + start + "," + end + ")");
        for (Map map : maps) {
            System.out.println("MarkerCount: " + map.getMarkerCount() + "  |  maxStartPosition: " + map.getMaxStartPosition() + "  |  linkageGroup: " + map.getLinkageGroup() + "  |  mapName: " + map.getMapName() + "  |  mapType:  " + map.getMapType());
        }
        
    }    

    @Test
    public void countMapDetailsByName() throws Exception {
        String nameLike = "tag%";
        Long parentElementsCount = manager.countMapDetailsByName(nameLike);
        System.out.println("countMapDetailsByName('"+nameLike+"')");
        System.out.println("Count: " + parentElementsCount);
    }

    @Test
    public void testAddQtlDetails() throws Exception {
        Integer qtlId = 4;          // Crop tested: Groundnut
        Integer mapId = 1; 
        Float minPosition = 0f; 
        Float maxPosition = 8f; 
        String trait = "HI"; 
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
                
        QtlDetails qtlDetails = new QtlDetails(qtlId, mapId, minPosition, maxPosition, trait, experiment, effect,
                scoreValue, rSquare, linkageGroup, interactions, leftFlankingMarker,
                rightFlankingMarker, position, clen, seAdditive, hvParent, hvAllele, lvParent, lvAllele);
        
        QtlDetailsPK idAdded = manager.addQtlDetails(qtlDetails);
        System.out.println("testAddQtlDetails() Added: " + (idAdded != null ? qtlDetails : null));
    }


    @Test
    public void testAddMarkerDetails() throws Exception {
        Integer markerId = 1;           // Replace with desired values
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
        System.out.println("testAddMarkerDetails() Added: " + (idAdded != null ? markerDetails : null));
    }


    @Test
    public void testAddMarkerUserInfo() throws Exception {
        Integer markerId = 1;           // Replace with desired values
        String principalInvestigator = "Juan Dela Cruz";
        String contact = "juan@irri.com.ph";
        String institute = "IRRI";
        
        MarkerUserInfo markerUserInfo = new MarkerUserInfo(markerId, principalInvestigator, contact, institute);
        
        Integer idAdded = manager.addMarkerUserInfo(markerUserInfo);
        System.out.println("testAddMarkerUserInfo() Added: " + (idAdded != null ? markerUserInfo : null));
    }
    
    @Test
    public void testAddAccMetadataSet() throws Exception {
        Integer datasetId = 4;  // Crop tested: Groundnut
        Integer germplasmId = 1; 
        Integer nameId = 1;
        
        AccMetadataSet accMetadataSet = new AccMetadataSet(datasetId, germplasmId, nameId);
        
        AccMetadataSetPK idAdded = manager.addAccMetadataSet(accMetadataSet);
        System.out.println("testAccMetadataSet() Added: " + (idAdded != null ? accMetadataSet : null));
    }

    @Test
    public void testAddMarkerMetadataSet() throws Exception {
        Integer datasetId = 4;  // Crop tested: Groundnut
        Integer markerId = 1; 
        
        MarkerMetadataSet markerMetadataSet = new MarkerMetadataSet(datasetId, markerId);
        
        MarkerMetadataSetPK idAdded = manager.addMarkerMetadataSet(markerMetadataSet);
        System.out.println("testAddMarkerMetadataSet() Added: " + (idAdded != null ? markerMetadataSet : null));
    }

    @Test
    public void testAddDataset() throws Exception {
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
        
        Dataset dataset = new Dataset(datasetId, datasetName, datasetDesc, datasetType, genus, species, uploadTemplateDate, remarks,
                dataType, missingData, method, score);
        
        Integer idAdded = manager.addDataset(dataset);
        System.out.println("testAddDataset() Added: " + (idAdded != null ? dataset : null));
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
        System.out.println("testAddGDMSMarker() Added: " + (idAdded != null ? marker : null));
    }

    @Test
    public void testAddGDMSMarkerAlias() throws Exception {
  	
        Integer markerId = 0;
        String alias = "testalias";
      
        MarkerAlias markerAlias = new MarkerAlias(markerId, alias);
      
        Integer idAdded = manager.addGDMSMarkerAlias(markerAlias);
        System.out.println("testAddGDMSMarkerAlias() Added: " + (idAdded != null ? markerAlias : null));
    }
    
    @Test
    public void testAddDatasetUser() throws Exception {
  	
        Integer datasetId = 7;
        Integer userId = 123;
      
        DatasetUsers datasetUser = new DatasetUsers(datasetId, userId);
      
        Integer idAdded = manager.addDatasetUser(datasetUser);
        System.out.println("testAddDatasetUser() Added: " + (idAdded != null ? datasetUser : null));
    }  

    @Test
    public void testAddAlleleValues() throws Exception {
        Integer anId = null;                // Crop tested: Groundnut
        Integer datasetId = 1; 
        Integer gId = 1920; 
        Integer markerId = 1037; 
        String alleleBinValue = "alleleBinValue"; 
        String alleleRawValue = "alleleRawValue";
      
        AlleleValues alleleValues = new AlleleValues(anId, datasetId, gId, markerId, alleleBinValue, alleleRawValue);
      
        Integer idAdded = manager.addAlleleValues(alleleValues);
        System.out.println("testAddAlleleValues() Added: " + (idAdded != null ? alleleValues : null));
    }  
  
    @Test
    public void testAddCharValues() throws Exception {
        Integer acId = null;            // Crop tested: Groundnut
        Integer datasetId = 1; 
        Integer gId = 1920; 
        Integer markerId = 1037; 
        String charValue = "CV"; 
      
        CharValues charValues = new CharValues(acId, datasetId, gId, markerId, charValue);
      
        Integer idAdded = manager.addCharValues(charValues);
        System.out.println("testAddCharValues() Added: " + (idAdded != null ? charValues : null));
    }  

    @Test
    public void testAddMappingPop() throws Exception {
  	
    	Integer datasetId = 2;
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
        System.out.println("testAddMappingPop() Added: " + (idAdded != null ? mappingPop : null));
    }    
    
    @Test
    public void testAddMappingPopValue() throws Exception {
  	
    	Integer mpId = null;
        String mapCharValue = "X";
        Integer datasetId = 2;
        Integer gid = 1434;
        Integer markerId = 2537;
      
        MappingPopValues mappingPopValue = new MappingPopValues(mpId, mapCharValue, datasetId, gid, markerId);
      
        Integer idAdded = manager.addMappingPopValue(mappingPopValue);
        System.out.println("testAddMappingPopValue() Added: " + (idAdded != null ? mappingPopValue : null));
    }    

    
    @Test
    public void testAddMarkerOnMap() throws Exception {
  	
    	Integer mapId = 1;
    	Integer markerId = 260;
    	Float startPosition = Float.valueOf("123.4");
    	Float endPosition = Float.valueOf("567.8");
    	String mapUnit = "TS";
    	String linkageGroup = "Test";
      
        MarkerOnMap markerOnMap = new MarkerOnMap(mapId, markerId, startPosition, endPosition, mapUnit, linkageGroup);
      
        Integer idAdded = manager.addMarkerOnMap(markerOnMap);
        System.out.println("testAddMarkerOnMap() Added: " + (idAdded != null ? markerOnMap : null));
    }    
    
    @Test
    public void testAddDartValue() throws Exception {
  	
    	Integer adId = null;
    	Integer datasetId = -6;
    	Integer cloneId = -1;
    	Float qValue = Float.valueOf("123.45");
    	Float reproducibility = Float.valueOf("543.21");
    	Float callRate = Float.valueOf("12.3");
    	Float picValue = Float.valueOf("543.2");
    	Float discordance = Float.valueOf("1.2");
    	
        DartValues dartValue = new DartValues(adId, datasetId, cloneId, qValue, reproducibility, callRate, picValue, discordance);
      
        Integer idAdded = manager.addDartValue(dartValue);
        System.out.println("testAddDartValue() Added: " + (idAdded != null ? dartValue : null));
    }    
    
    @Test
    public void testAddQtl() throws Exception {
  	
    	Integer qtlId = null;
    	String qtlName = "TestQTL";
    	Integer datasetId = -6;
    	
        Qtl qtl = new Qtl(qtlId, qtlName, datasetId);
      
        Integer idAdded = manager.addQtl(qtl);
        System.out.println("testAddQtl() Added: " + (idAdded != null ? qtl : null));
    }    

    
    @AfterClass
    public static void tearDown() throws Exception {
        factory.close();
    }

}
