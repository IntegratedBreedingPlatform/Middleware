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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
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
    public void testGetDatasetNamesByQtlId() throws Exception {
    	Integer qtlId = 1;    		// Crop tested: Groundnut
        List<String> results = manager.getDatasetNamesByQtlId(qtlId, 0, 5);
        System.out.println("testGetDatasetNamesByQtlId(0,5) RESULTS: " + results);
    }

    @Test
    public void testCountDatasetNamesByQtlId() throws Exception {
    	Integer qtlId = 1;   		// Crop tested: Groundnut 
        long results = manager.countDatasetNamesByQtlId(qtlId);
        System.out.println("testCountDatasetNamesByQtlId() RESULTS: " + results);
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
    	//23-Jul-2013, currently no test data with datasetid = 2 in groundnut central, using datasetid=3 instead
        //Integer datasetId = Integer.valueOf(2);
        Integer datasetId = Integer.valueOf(3);
        long count = manager.countAllelicValuesFromMappingPopValuesByDatasetId(datasetId); 
        		//manager.countAllelicValuesFromCharValuesByDatasetId(datasetId);
        List<AllelicValueWithMarkerIdElement> allelicValues = manager.getAllelicValuesFromMappingPopValuesByDatasetId(datasetId, 0,
                (int) count);
        System.out.println("COUNT IS " + count);
        System.out.println("testGetAllelicValuesFromMappingPopValuesByDatasetId(" + datasetId + ") RESULTS: ");
        if (allelicValues != null) {
        	for (AllelicValueWithMarkerIdElement elem : allelicValues) {
        		System.out.println(elem);
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
        System.out.println("testGetIntAlleleValuesForPolymorphicMarkersRetrieval() RESULTS: ");
        for (AllelicValueElement result : results){
        	System.out.println("  " + result);
        }
       System.out.println("Number of record/s: " +results.size() );
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
        //For rice db(v20), you can use germplasmIds: 58, 29811
        germplasmIds.add(Integer.valueOf(956)); // Please replace the gids found in the target crop to be used in testing
        germplasmIds.add(Integer.valueOf(1042));
        germplasmIds.add(Integer.valueOf(-2213));
        germplasmIds.add(Integer.valueOf(-2215));

        List<AllelicValueElement> results = manager.getCharAlleleValuesForPolymorphicMarkersRetrieval(germplasmIds, 0, 
                (int) manager.countCharAlleleValuesForPolymorphicMarkersRetrieval(germplasmIds));
        System.out.println("testGetIntAlleleValuesForPolymorphicMarkersRetrieval() RESULTS: ");
        for (AllelicValueElement result : results){
        	System.out.println("  " + result);
        }
       System.out.println("Number of record/s: " +results.size() );
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
        System.out.println("testGetNIdsByDatasetIdsAndMarkerIdsAndNotGIds(datasetIds="+datasetIds+") RESULTS: " + nIdList);
        
        nIdList = manager.getNIdsByMarkerIdsAndDatasetIdsAndNotGIds(null, gIds, markerIds, 0, 5);
        System.out.println("testGetNIdsByDatasetIdsAndMarkerIdsAndNotGIds(datasetIds=null) RESULTS: " + nIdList);
        
        nIdList = manager.getNIdsByMarkerIdsAndDatasetIdsAndNotGIds(datasetIds, null, markerIds, 0, 5);
        System.out.println("testGetNIdsByDatasetIdsAndMarkerIdsAndNotGIds(gIds=null) RESULTS: " + nIdList);
        
        nIdList = manager.getNIdsByMarkerIdsAndDatasetIdsAndNotGIds(datasetIds, gIds, null, 0, 5);
        System.out.println("testGetNIdsByDatasetIdsAndMarkerIdsAndNotGIds(markerIds=null) RESULTS: " + nIdList);
        
        
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
        System.out.println("testcCountNIdsByDatasetIdsAndMarkerIdsAndNotGIds() RESULTS: " + count);
    }  
    
    @Test
    public void testGetNIdsByDatasetIdsAndMarkerIds() throws Exception { 
        
        List<Integer> datasetIds = new ArrayList<Integer>(); 
        datasetIds.add(Integer.valueOf(2)); // Please replace the dataset ids found in the target crop to be used in testing
        
        List<Integer> markerIds = new ArrayList<Integer>(); 
        markerIds.add(Integer.valueOf(6803)); // Please replace the marker ids found in the target crop to be used in testing

        List<Integer> nIdList = manager.getNIdsByMarkerIdsAndDatasetIds(datasetIds, markerIds, 0, 
                manager.countNIdsByMarkerIdsAndDatasetIds(datasetIds, markerIds));
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
        System.out.println("testGetMappingAlleleValuesForPolymorphicMarkersRetrieval() RESULTS: ");
        for (AllelicValueElement result : results){
        	System.out.println("  " + result);
        }
       System.out.println("Number of record/s: " +results.size() );
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
        System.out.println("testGetAllQtl() RESULTS: " + qtls.size());
        for (Qtl qtl : qtls){
            System.out.println("    " + qtl);
        }
    }

    @Test
    public void testCountAllQtl() throws Exception {
        long result = manager.countAllQtl();
        System.out.println("testCountAllQtl() RESULTS: " + result);
    }
    
    @Test
    public void testCountQtlIdByName() throws Exception { 
        String qtlName = "SLA%";     // Crop tested: Groundnut
        long count = manager.countQtlIdByName(qtlName);
        System.out.println("testCountQtlIdByName() RESULTS: " + count);
    }

    @Test
    public void testGetQtlIdByName() throws Exception {
        String qtlName = "SLA%";     // Crop tested: Groundnut

        List<Integer> results = manager.getQtlIdByName(qtlName, 0, 
                (int) manager.countQtlIdByName(qtlName));
        System.out.println("testGetQtlIdByName() RESULTS: " + results);
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
        Integer qtlTraitId = 1001; // "SL%";     // Crop tested: Groundnut
        List<Integer> results = manager.getQtlByTrait(qtlTraitId, 0, 
                (int) manager.countQtlByTrait(qtlTraitId));
        System.out.println("testGetQtlByTrait() RESULTS: " + results);
    }


    @Test
    public void testCountQtlByTrait() throws Exception { 
        Integer qtlTraitId = 1001; // "SL%";     // Crop tested: Groundnut
        long count = manager.countQtlByTrait(qtlTraitId);
        System.out.println("testCountQtlByTrait() RESULTS: " + count);
    }    
    
    @Test
    public void testGetQtlTraitsByDatasetId() throws Exception {
    	Integer datasetId = 7;		// Crop tested: Groundnut
        List<Integer> results = manager.getQtlTraitsByDatasetId(datasetId, 0, 
                (int) manager.countQtlTraitsByDatasetId(datasetId));
        System.out.println("testGetQtlTraitsByDatasetId() RESULTS: " + results);
    }


    @Test
    public void testCountQtlTraitsByDatasetId() throws Exception { 
    	Integer datasetId = 7;		// Crop tested: Groundnut
        long count = manager.countQtlTraitsByDatasetId(datasetId);
        System.out.println("testCountQtlTraitsByDatasetId() RESULTS: " + count);
    }    
    
    @Test
    public void testGetMapIdsByQtlName() throws Exception { 
        String qtlName = "HI Control 08_AhI";     // Crop tested: Groundnut
        List<Integer> results = manager.getMapIdsByQtlName(qtlName, 0, (int) manager.countMapIdsByQtlName(qtlName));
        System.out.println("testGetMapIdsByQtlName() RESULTS: " + results);
    }

    @Test
    public void testCountMapIdsByQtlName() throws Exception { 
        String qtlName = "HI Control 08_AhI";     // Crop tested: Groundnut
        long count = manager.countMapIdsByQtlName(qtlName);
        System.out.println("testCountMapIdsByQtlName() RESULTS: " + count);
    }


    @Test
    public void testGetMarkersByQtl() throws Exception { 
        String qtlName = "HI Control 08_AhI";     // Crop tested: Groundnut
        String chromosome = "LG01";
        int min = 0;
        int max = 10;
        List<Integer> results = manager.getMarkerIdsByQtl(qtlName, chromosome, min, max, 0, (int) manager.countMarkerIdsByQtl(qtlName, chromosome, min, max));
        System.out.println("testGetMarkersByQtl() RESULTS: " + results);
    }

    @Test
    public void testCountMarkersByQtl() throws Exception { 
        String qtlName = "HI Control 08_AhI";     // Crop tested: Groundnut
        String chromosome = "LG01";
        int min = 0;
        int max = 10;
        long count = manager.countMarkerIdsByQtl(qtlName, chromosome, min, max);
        System.out.println("testCountMarkersByQtl() RESULTS: " + count);

    }


    @Test
    public void getAllParentsFromMappingPopulation() throws Exception {
    	int start = 0;
    	int end = 10;
        List<ParentElement> parentElements = manager.getAllParentsFromMappingPopulation(start, end);
        System.out.println("getAllParentsFromMappingPopulation(" + start + "," + end + ")");
        for (ParentElement parentElement : parentElements) {
            System.out.println("Parent A NId: " + parentElement.getParentANId() + "  |  Parent B NId: "+parentElement.getParentBGId());
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
        List<MapDetailElement> maps = manager.getMapDetailsByName(nameLike, start, end);
        System.out.println("getMapDetailsByName('"+nameLike+"'," + start + "," + end + ")");
        for (MapDetailElement map : maps) {
            System.out.println("Map: " + map.getMarkerCount() + "  |  maxStartPosition: " + map.getMaxStartPosition() + "  |  linkageGroup: " + map.getLinkageGroup() + "  |  mapName: " + map.getMapName() + "  |  mapType:  " + map.getMapType());
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
    public void testGetAllMapDetails() throws Exception {
        int start = 0;
        int end = (int) manager.countAllMapDetails();
        List<MapDetailElement> maps = manager.getAllMapDetails(start, end);
        System.out.println("testGetAllMapDetails(" + start + "," + end + ")");
        for (MapDetailElement map : maps) {
            System.out.println(map);
        }
        
    }    

    @Test
    public void testCountAllMapDetails() throws Exception {
        System.out.println("testCountAllMapDetails(): " + manager.countAllMapDetails());
    }

    @Test
    public void testAddQtlDetails() throws Exception {
        Integer qtlId = -5;          // Crop tested: Groundnut
        Integer mapId = 1; 
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
        System.out.println("testAddQtlDetails() Added: " + (idAdded != null ? qtlDetails : null));
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
        System.out.println("testAddMarkerDetails() Added: " + (idAdded != null ? markerDetails : null));
    }


    @Test
    public void testAddMarkerUserInfo() throws Exception {
        Integer markerId = -1;           // Replace with desired values
        String principalInvestigator = "Juan Dela Cruz";
        String contact = "juan@irri.com.ph";
        String institute = "IRRI";
        
        MarkerUserInfo markerUserInfo = new MarkerUserInfo(markerId, principalInvestigator, contact, institute);
        
        Integer idAdded = manager.addMarkerUserInfo(markerUserInfo);
        System.out.println("testAddMarkerUserInfo() Added: " + (idAdded != null ? markerUserInfo : null));
    }
    
    @Test
    public void testAddAccMetadataSet() throws Exception {
        Integer datasetId = -1;  // Crop tested: Groundnut
        Integer germplasmId = 1; 
        Integer nameId = 1;
        
        AccMetadataSet accMetadataSet = new AccMetadataSet(datasetId, germplasmId, nameId);
        
        AccMetadataSetPK idAdded = manager.addAccMetadataSet(accMetadataSet);
        System.out.println("testAccMetadataSet() Added: " + (idAdded != null ? accMetadataSet : null));
    }

    @Test
    public void testAddMarkerMetadataSet() throws Exception {
        Integer datasetId = -1;  // Crop tested: Groundnut
        Integer markerId = -1; 
        
        MarkerMetadataSet markerMetadataSet = new MarkerMetadataSet(datasetId, markerId);
        
        MarkerMetadataSetPK idAdded = manager.addMarkerMetadataSet(markerMetadataSet);
        System.out.println("testAddMarkerMetadataSet() Added: " + (idAdded != null ? markerMetadataSet : null));
    }

    @Test
    public void testAddDataset() throws Exception {
    	Dataset dataset = createDataset();
    	manager.addDataset(dataset);
        System.out.println("testAddDataset() Added: " + (dataset.getDatasetId() != null ? dataset : null));
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
        System.out.println("testAddGDMSMarker() Added: " + (idAdded != null ? marker : null));
    }

    @Test
    public void testAddGDMSMarkerAlias() throws Exception {
  	
        Integer markerId = -1;
        String alias = "testalias";
      
        MarkerAlias markerAlias = new MarkerAlias(markerId, alias);
      
        Integer idAdded = manager.addGDMSMarkerAlias(markerAlias);
        System.out.println("testAddGDMSMarkerAlias() Added: " + (idAdded != null ? markerAlias : null));
    }
    
    @Test
    public void testAddDatasetUser() throws Exception {
  	
        Integer datasetId = -1;
        Integer userId = 123;
      
        DatasetUsers datasetUser = new DatasetUsers(datasetId, userId);
      
        Integer idAdded = manager.addDatasetUser(datasetUser);
        System.out.println("testAddDatasetUser() Added: " + (idAdded != null ? datasetUser : null));
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
        System.out.println("testAddAlleleValues() Added: " + (idAdded != null ? alleleValues : null));
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
        System.out.println("testAddCharValues() Added: " + (idAdded != null ? charValues : null));
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
        System.out.println("testAddMappingPop() Added: " + (idAdded != null ? mappingPop : null));
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
        System.out.println("testAddMappingPopValue() Added: " + (idAdded != null ? mappingPopValue : null));
    }    

    
    @Test
    public void testAddMarkerOnMap() throws Exception {
  	
    	Integer mapId = -1;
    	Integer markerId = -1;
    	Float startPosition = Float.valueOf("123.4");
    	Float endPosition = Float.valueOf("567.8");
//    	String mapUnit = "TS";
    	String linkageGroup = "Test";
      
        MarkerOnMap markerOnMap = new MarkerOnMap(mapId, markerId, startPosition, endPosition, linkageGroup);
      
        Integer idAdded = manager.addMarkerOnMap(markerOnMap);
        System.out.println("testAddMarkerOnMap() Added: " + (idAdded != null ? markerOnMap : null));
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
        System.out.println("testAddMap() Added: " + (idAdded != null ? map : null));
    }    

    @Test
    public void testSetSSRMarkers() throws Exception {
    	List<Object> markerRecrods = createMarkerMarkeRecords();
        Marker marker = (Marker) markerRecrods.get(0);
        MarkerAlias markerAlias = (MarkerAlias) markerRecrods.get(1);
        MarkerDetails markerDetails = (MarkerDetails) markerRecrods.get(2);
        MarkerUserInfo markerUserInfo = (MarkerUserInfo) markerRecrods.get(3);
        
        Boolean addStatus = manager.setSSRMarkers(marker, markerAlias, markerDetails, markerUserInfo);
        System.out.println("testSetSSRMarkers() Added: " + (addStatus != null ? marker : null) + (addStatus != null ? markerAlias : null) + (addStatus != null ? markerDetails : null) + (addStatus != null ? markerUserInfo : null));
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
        System.out.println("testSetSNPMarkers() Added: " + (addStatus != null ? marker : null) + (addStatus != null ? markerAlias : null) + (addStatus != null ? markerDetails : null) + (addStatus != null ? markerUserInfo : null));
    }

    @Test
    public void testSetCAPMarkers() throws Exception {
    	List<Object> markerRecrods = createMarkerMarkeRecords();
        Marker marker = (Marker) markerRecrods.get(0);
        MarkerAlias markerAlias = (MarkerAlias) markerRecrods.get(1);
        MarkerDetails markerDetails = (MarkerDetails) markerRecrods.get(2);
        MarkerUserInfo markerUserInfo = (MarkerUserInfo) markerRecrods.get(3);

        Boolean addStatus = manager.setCAPMarkers(marker, markerAlias, markerDetails, markerUserInfo);
        System.out.println("testSetCAPMarkers() Added: " + (addStatus != null ? marker : null) + (addStatus != null ? markerAlias : null) + (addStatus != null ? markerDetails : null) + (addStatus != null ? markerUserInfo : null));
    }

    @Test
    public void testSetCISRMarkers() throws Exception {
    	List<Object> markerRecrods = createMarkerMarkeRecords();
        Marker marker = (Marker) markerRecrods.get(0);
        MarkerAlias markerAlias = (MarkerAlias) markerRecrods.get(1);
        MarkerDetails markerDetails = (MarkerDetails) markerRecrods.get(2);
        MarkerUserInfo markerUserInfo = (MarkerUserInfo) markerRecrods.get(3);

        Boolean addStatus = manager.setCISRMarkers(marker, markerAlias, markerDetails, markerUserInfo);
        System.out.println("testSetCISRMarkers() Added: " + (addStatus != null ? marker : null) + (addStatus != null ? markerAlias : null) + (addStatus != null ? markerDetails : null) + (addStatus != null ? markerUserInfo : null));
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
        System.out.println("testSetQTL() Added: " + (addStatus != null ? datasetUser : null) + (addStatus != null ? dataset : null) + (addStatus != null ? qtlDetails : null) + (addStatus != null ? qtl : null));
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
        
        Boolean addStatus = manager.setDart(accMetadataSet, markerMetadataSet, datasetUser, alleleValues, dataset, dartValues);
        System.out.println("testSetDArT() Added: " + (addStatus != null ? accMetadataSet : null) 
                    + " | " + (addStatus != null ? markerMetadataSet : null) 
                    + " | " + (addStatus != null ? datasetUser : null) 
                    + " | " + (addStatus != null ? alleleValues : null)  
                    + " | " + (addStatus != null ? dataset : null) 
                    + " | " + (addStatus != null ? dartValues : null));
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
        
        Boolean addStatus = manager.setSSR(accMetadataSet, markerMetadataSet, datasetUser, alleleValues, dataset);
        System.out.println("testSetSSR() Added: " + (addStatus != null ? accMetadataSet : null) 
                    + " | " + (addStatus != null ? markerMetadataSet : null) 
                    + " | " + (addStatus != null ? datasetUser : null) 
                    + " | " + (addStatus != null ? alleleValues : null)  
                    + " | " + (addStatus != null ? dataset : null)); 
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
     
        Boolean addStatus = manager.setSNP(accMetadataSet, markerMetadataSet, datasetUser, charValues, dataset);
        System.out.println("testSetSNP() Added: " + (addStatus != null ? accMetadataSet : null) 
                    + " | " + (addStatus != null ? markerMetadataSet : null) 
                    + " | " + (addStatus != null ? datasetUser : null) 
                    + " | " + (addStatus != null ? charValues : null) 
                    + " | " + (addStatus != null ? dataset : null));
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
        
        Boolean addStatus = manager.setMappingData(accMetadataSet, markerMetadataSet, datasetUser, mappingPop, mappingPopValues, dataset);
        System.out.println("testSetMappingData() Added: " + (addStatus != null ? accMetadataSet : null) 
                    + " | " + (addStatus != null ? markerMetadataSet : null) 
                    + " | " + (addStatus != null ? datasetUser : null) 
                    + " | " + (addStatus != null ? mappingPop : null)
                    + " | " + (addStatus != null ? mappingPopValues : null)
                    + " | " + (addStatus != null ? dataset : null));
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
        
        Boolean addStatus = manager.setMaps(marker, markerOnMap, map);
        System.out.println("testSetMaps() Added: " + (addStatus != null ? marker : null) 
                    + " | " + (addStatus != null ? markerOnMap : null) 
                    + " | " + (addStatus != null ? map : null));
    }
    
    @Test
    public void TestGetMapIDsByQTLName() throws Exception {
        String qtlName = "HI Control 08_AhI";
        long start = System.currentTimeMillis();
        List<Integer> mapIDs = manager.getMapIDsByQTLName(qtlName, 0, 2);

        for (Integer mapID : mapIDs) {
            System.out.println("Map ID: " + mapID);
        }
        
        long end = System.currentTimeMillis();
        System.out.println("TestGetMapIDsByQTLName(" + qtlName + ")");
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }
    
    @Test
    public void TestCountMapIDsByQTLName() throws Exception {
        String qtlName = "HI Control 08_AhI";
        long start = System.currentTimeMillis();
        long count = manager.countMapIDsByQTLName(qtlName);

        System.out.println("Count of Map IDs: " + count);
        
        long end = System.currentTimeMillis();
        System.out.println("TestCountMapIDsByQTLName(" + qtlName + ")");
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
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
            System.out.println("Marker ID: " + markerID);
        }
        
        long end = System.currentTimeMillis();
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }
    
    @Test
    public void testCountMarkerIDsByMapIDAndLinkageBetweenStartPosition() throws Exception {
        int mapID = 1;
        String linkage = "LG01";
        double startPos = 0;
        double endPos = 2.2;
        long start = System.currentTimeMillis();
        long count = manager.countMarkerIDsByMapIDAndLinkageBetweenStartPosition(mapID, linkage, startPos, endPos);

        System.out.println("Count of Marker IDs: " + count);
        
        long end = System.currentTimeMillis();
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }
    
    @Test
    public void testGetMarkersByMarkerIDs() throws Exception {
        List<Integer> markerIDs = new ArrayList<Integer>();
        markerIDs.add(1317);
        
        long start = System.currentTimeMillis();
        List<Marker> markerList = manager.getMarkersByMarkerIds(markerIDs, 0, 5);

        for (Marker marker : markerList) {
            System.out.println("Marker: " + marker);
        }
        
        long end = System.currentTimeMillis();
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }
    
    @Test
    public void testCountMarkersByMarkerIDs() throws Exception {
        List<Integer> markerIDs = new ArrayList<Integer>();
        markerIDs.add(1317);
        long start = System.currentTimeMillis();
        long count = manager.countMarkersByMarkerIds(markerIDs);

        System.out.println("Count of Markers: " + count);
        
        long end = System.currentTimeMillis();
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }
    
    @Test
    public void testGetQTLByQTLIDs() throws Exception {
        List<Integer> qtlIDs = new ArrayList<Integer>();
        qtlIDs.add(1);
      

        List<QtlDetailElement> results = manager.getQtlByQtlIds(qtlIDs, 0, 
                (int) manager.countQtlByQtlIds(qtlIDs));
        System.out.println("testGetQtlByQTLIDs() RESULTS: " + results);
    }


    @Test
    public void testCountQTLByQTLIDs() throws Exception { 
        List<Integer> qtlIDs = new ArrayList<Integer>();
        qtlIDs.add(1);
        qtlIDs.add(2);
        qtlIDs.add(3);
        long count = manager.countQtlByQtlIds(qtlIDs);
        System.out.println("testCountQtlByQTLIDs() RESULTS: " + count);
    }   
    
    
    @Test
    public void testGetQtlDataByQtlTraits() throws Exception {
        List<Integer> qtlTraits = new ArrayList<Integer>();   // Crop tested: Groundnut
		qtlTraits.add(1001);     // "DE"
        List<QtlDataElement> results = manager.getQtlDataByQtlTraits(qtlTraits, 0, (int) manager.countQtlDataByQtlTraits(qtlTraits)); 
        System.out.println("testGetQtlDataByQtlTraits() RESULTS: " + results.size());
        for (QtlDataElement element : results){
            System.out.println("    " + element);
        }
    }


    @Test
    public void testCountQtlDataByQtlTraits() throws Exception { 
        List<Integer> qtlTraits = new ArrayList<Integer>();   // Crop tested: Groundnut
		qtlTraits.add(1001);     // "DE"
        long count =  manager.countQtlDataByQtlTraits(qtlTraits); 
        System.out.println("testCountQtlDataByQtlTraits() RESULTS: " + count);
    }
    
    
    @Test
    public void testCountAllelicValuesFromAlleleValuesByDatasetId() throws Exception { 
        Integer datasetId = -1; // change value of the datasetid
        long count = manager.countAllelicValuesFromAlleleValuesByDatasetId(datasetId);
        Assert.assertNotNull(count);
        System.out.println("testCountAllelicValuesFromAlleleValuesByDatasetId("+datasetId+") Results: " + count);
    }
    
    @Test
    public void testCountAllelicValuesFromCharValuesByDatasetId() throws Exception { 
        Integer datasetId = -1; // change value of the datasetid
        long count = manager.countAllelicValuesFromCharValuesByDatasetId(datasetId);
        Assert.assertNotNull(count);
        System.out.println("testCountAllelicValuesFromCharValuesByDatasetId("+datasetId+") Results: " + count);
    }
    
    @Test
    public void testCountAllelicValuesFromMappingPopValuesByDatasetId() throws Exception { 
        Integer datasetId = 3; // change value of the datasetid
        long count = manager.countAllelicValuesFromMappingPopValuesByDatasetId(datasetId);
        Assert.assertNotNull(count);
        System.out.println("testCountAllelicValuesFromMappingPopValuesByDatasetId("+datasetId+") Results: " + count);
    }
    
    @Test
    public void testCountAllParentsFromMappingPopulation() throws Exception {
    	long count = manager.countAllParentsFromMappingPopulation();
        Assert.assertNotNull(count);
        System.out.println("testCountAllParentsFromMappingPopulation() Results: " + count);
    }
    
    @Test
    public void testCountMapDetailsByName() throws Exception {
    	String nameLike = "GCP-833TestMap"; //change map_name
    	long count = manager.countMapDetailsByName(nameLike);
        Assert.assertNotNull(count);
        System.out.println("testCountMapDetailsByName("+nameLike+") Results: " + count);
    }
    
    @Test
    public void testCountMarkerInfoByDbAccessionId() throws Exception {
    	String dbAccessionId = ""; //change dbAccessionId
    	long count = manager.countMarkerInfoByDbAccessionId(dbAccessionId);
        Assert.assertNotNull(count);
        System.out.println("testCountMarkerInfoByDbAccessionId("+dbAccessionId+") Results: " + count);
    }
    
    @Test
    public void testCountMarkerInfoByGenotype() throws Exception {
    	String genotype = "cv. Tatu"; //change genotype
    	long count = manager.countMarkerInfoByGenotype(genotype);
        Assert.assertNotNull(count);
        System.out.println("testCountMarkerInfoByGenotype("+genotype+") Results: " + count);
    }
    
    @Test
    public void testCountMarkerInfoByMarkerName() throws Exception {
    	String markerName = "SeqTEST"; //change markerName
    	long count = manager.countMarkerInfoByMarkerName(markerName);
        Assert.assertNotNull(count);
        System.out.println("testCountMarkerInfoByMarkerName("+markerName+") Results: " + count);
    }
    
    @Test
    public void testGetAllParentsFromMappingPopulation() throws Exception {
    	long count = manager.countAllParentsFromMappingPopulation();
    	List<ParentElement> results = manager.getAllParentsFromMappingPopulation(0, (int) count);
    	assertNotNull(results);
        Assert.assertTrue(!results.isEmpty());
        System.out.println("testGetAllParentsFromMappingPopulation() Results: ");
        for (ParentElement result : results){
        	System.out.println("  " + result);
        }
        System.out.println("Number of record/s: "+count+" ");
    }
 
    @Test
    public void testGetMapDetailsByName() throws Exception {
    	String nameLike = "GCP-833TestMap"; //change map_name
    	long count = manager.countMapDetailsByName(nameLike);
    	List<MapDetailElement> results = manager.getMapDetailsByName(nameLike, 0, (int) count);
    	assertNotNull(results);
        Assert.assertTrue(!results.isEmpty());
        System.out.println("testGetMapDetailsByName("+nameLike+")");
        for (MapDetailElement result : results){
        	System.out.println("  " + result);
        }
        System.out.println("Number of record/s: "+count+" ");
    }
    
    @Test
    public void testGetMarkersByIds() throws Exception {
    	List<Integer> markerIds = new ArrayList<Integer>();
    	markerIds.add(1);
    	markerIds.add(3);
    	
    	List<Marker> results = manager.getMarkersByIds(markerIds, 0,100);
    	assertNotNull(results);
        Assert.assertTrue(!results.isEmpty());
        System.out.println("testGetMarkersByIds("+markerIds+") Results: ");
        for (Marker result : results){
        	System.out.println("  " + result);
        }
        System.out.println("Number of record/s: " +results.size() );
    }
    
    @Test
    public void testCountAllMaps() throws Exception {
	long count = manager.countAllMaps(Database.LOCAL);
    Assert.assertNotNull(count);
    System.out.println("testCountAllMaps("+Database.LOCAL+") Results: " + count);	
    }
    
    @Test
    public void testGetAllMaps() throws Exception {
    List<Map> results= manager.getAllMaps(1, 100, Database.LOCAL);
    Assert.assertNotNull(results);
    Assert.assertTrue(!results.isEmpty());
    System.out.println("testGetAllMaps("+Database.LOCAL+") Results:");
    for (Map result : results){
    	System.out.println("  " + result);
    }
    System.out.println("Number of record/s: " +results.size() );
    
    }

    @Test
    public void testCountNidsFromAccMetadatasetByDatasetIds() throws Exception {
    	//GROUNDNUT DATABASE
    	List<Integer> datasetIds = Arrays.asList(2, 3, 4);
    	long count = manager.countNidsFromAccMetadatasetByDatasetIds(datasetIds);
    	System.out.println("testCountNidsFromAccMetadatasetByDatasetIds(" + datasetIds + ") = " + count);
    }
    
    @Test
    public void testGetMapIdByName() throws Exception {
    	//GROUNDNUT DATABASE
    	String mapName = "Consensus_Biotic";  //CENTRAL test data
    	Integer mapId = manager.getMapIdByName(mapName);
    	System.out.println("Map ID for " + mapName + " = " + mapId);
    	mapName = "name that does not exist";
    	mapId = manager.getMapIdByName(mapName);
    	System.out.println("Map ID for " + mapName + " = " + mapId);
    	mapName = "TEST"; //local test data INSERT INTO gdms_map (map_id, map_name, map_type, mp_id, map_desc, map_unit) values(-1, 'TEST', 'genetic', 0, 'TEST', 'cM');
    	mapId = manager.getMapIdByName(mapName);
    	System.out.println("Map ID for " + mapName + " = " + mapId);
    }
    
    @Test
    public void testCountMappingPopValuesByGids() throws Exception {
        List<Integer> gIds = Arrays.asList(1434, 1435, 1436);   // IBDBv2 Groundnut
        long count = manager.countMappingPopValuesByGids(gIds);
        System.out.println("countMappingPopValuesByGids(" + gIds + ") = " + count);
    }
    
    @Test
    public void testGetAllFromMarkerMetadatasetByMarker() throws Exception {
        Integer markerId = 3302; 
        List<MarkerMetadataSet> result = manager.getAllFromMarkerMetadatasetByMarker(markerId);
        System.out.println("testGetAllFromMarkerMetadatasetByMarker(" + markerId + "): " + result.size());
        if (result != null) {
            for (MarkerMetadataSet elem : result) {
                Debug.println(4, elem.toString());
            }
        }
    }
    @Test
    public void testGetDatasetDetailsByDatasetId() throws Exception {
        Integer datasetId = 5; 
        Dataset dataset = manager.getDatasetDetailsByDatasetId(datasetId);
        System.out.println("testGetDatasetDetailsByDatasetId(" + datasetId + "): " + dataset);
    }
    
    @Test
    public void testGetQTLIdsByDatasetIds() throws Exception {
        List<Integer> datasetIds = Arrays.asList(1, 2, 3, 4);   // IBDBv2 Groundnut
        List<Integer> qtlIds = manager.getQTLIdsByDatasetIds(datasetIds);
        System.out.println("testGetQTLIdsByDatasetIds(" + datasetIds + "): " + qtlIds);
    }
    
    @Test
    public void testGetAllFromAccMetadataset() throws Exception {
        List<Integer> gids = new ArrayList<Integer>();
        gids.add(2012);
        gids.add(2014);
        gids.add(2016);
        
        Integer datasetId = 5; 
        List<AccMetadataSetPK> result = manager.getAllFromAccMetadataset(gids, datasetId, SetOperation.NOT_IN);
        System.out.println("testGetAllFromAccMetadataset(gid=" + gids + ", datasetId=" + datasetId + "): " + result.size());
        if (result != null) {
            for (AccMetadataSetPK elem : result) {
                Debug.println(4, elem.toString());
            }
        }
    }
    
    @Test
    public void testGetMapAndMarkerCountByMarkers() throws Exception {
        List<Integer> markerIds = Arrays.asList(1317, 1318, 1788, 1053, 2279, 50);
        System.out.println("testGetMapAndMarkerCountByMarkers(" + markerIds + ")");
        List<MapDetailElement> details = manager.getMapAndMarkerCountByMarkers(markerIds);
        if (details != null && details.size() > 0) {
            System.out.println("FOUND " + details.size() + " records");
            for (MapDetailElement detail : details) {
                System.out.println(detail.getMapName() + " - " + detail.getMarkerCount());
            }
        } else {
            System.out.println("NO RECORDS FOUND");
        }
    }
    
    @Test
    public void testGetAllMTAs() throws Exception {
        List<Mta> result = manager.getAllMTAs();
        System.out.println("testGetAllMTAs(): " + result.size());
        if (result != null) {
            for (Mta elem : result) {
                Debug.println(4, elem.toString());
            }
        }
    }
    
    @Test
    public void testCountAllMTAs() throws Exception {
        long count = manager.countAllMTAs();
        System.out.println("testCountAllMTAs(): " + count);
    }
    
    @Test
    public void testGetMTAsByTrait() throws Exception {
        Integer traitId = 1;
        List<Mta> result = manager.getMTAsByTrait(traitId);
        System.out.println("testGetMTAsByTrait(): " + result.size());
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
    	System.out.println("testDeleteQTLs(qtlIds=" + qtlIds + ", datasetId=" + datasetId);
    	manager.deleteQTLs(qtlIds, datasetId);
    	System.out.println("done with testDeleteQTLs");
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
    	System.out.println("testDeleteSSRGenotypingDatasets(" + datasetId + ")");
    	manager.deleteSSRGenotypingDatasets(datasetId);
    	System.out.println("done with testDeleteSSRGenotypingDatasets");
    }
    
    @AfterClass
    public static void tearDown() throws Exception {
        factory.close();
    }

}
