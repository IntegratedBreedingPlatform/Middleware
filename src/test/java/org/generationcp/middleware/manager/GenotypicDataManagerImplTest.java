/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.DataManagerIntegrationTest;
import org.generationcp.middleware.MiddlewareIntegrationTest;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GenotypicDataManager;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.gdms.AccMetadataSet;
import org.generationcp.middleware.pojos.gdms.AlleleValues;
import org.generationcp.middleware.pojos.gdms.AllelicValueElement;
import org.generationcp.middleware.pojos.gdms.AllelicValueWithMarkerIdElement;
import org.generationcp.middleware.pojos.gdms.CharValues;
import org.generationcp.middleware.pojos.gdms.DartValues;
import org.generationcp.middleware.pojos.gdms.Dataset;
import org.generationcp.middleware.pojos.gdms.DatasetElement;
import org.generationcp.middleware.pojos.gdms.DatasetUsers;
import org.generationcp.middleware.pojos.gdms.ExtendedMarkerInfo;
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
import org.generationcp.middleware.pojos.gdms.MarkerNameElement;
import org.generationcp.middleware.pojos.gdms.MarkerOnMap;
import org.generationcp.middleware.pojos.gdms.MarkerSampleId;
import org.generationcp.middleware.pojos.gdms.MarkerUserInfo;
import org.generationcp.middleware.pojos.gdms.Mta;
import org.generationcp.middleware.pojos.gdms.MtaMetadata;
import org.generationcp.middleware.pojos.gdms.ParentElement;
import org.generationcp.middleware.pojos.gdms.Qtl;
import org.generationcp.middleware.pojos.gdms.QtlDataElement;
import org.generationcp.middleware.pojos.gdms.QtlDataRow;
import org.generationcp.middleware.pojos.gdms.QtlDetailElement;
import org.generationcp.middleware.pojos.gdms.QtlDetails;
import org.generationcp.middleware.pojos.gdms.TrackData;
import org.generationcp.middleware.pojos.gdms.TrackMarker;
import org.generationcp.middleware.utils.test.Debug;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

// Test using Groundnut Database
public class GenotypicDataManagerImplTest extends DataManagerIntegrationTest {

	private static GenotypicDataManager manager;

	@BeforeClass
	public static void setUp() {
		GenotypicDataManagerImplTest.manager = DataManagerIntegrationTest.managerFactory.getGenotypicDataManager();
	}

	@Test
	public void testGetNameIdsByGermplasmIds() throws Exception {
		List<Integer> germplasmIds = Arrays.asList(-3787, -6785, -4070, 1434, 2219);
		// For test data, get gids from LOCAL and CENTRAL: SELECT nid, gid FROM gdms_acc_metadataset

		List<Integer> results = GenotypicDataManagerImplTest.manager.getNameIdsByGermplasmIds(germplasmIds);
		Debug.println("testGetNameIdsByGermplasmIds(" + germplasmIds + ") RESULTS: " + results.size() + " = " + results);
	}

	@Test
	public void testGetNamesByNameIds() throws Exception {
		List<Integer> nameIds = Arrays.asList(1, 2, 3, -1, -2, -3);
		// For test data, get gids from LOCAL and CENTRAL: SELECT * FROM names where nid in (:nidList)

		List<Name> results = GenotypicDataManagerImplTest.manager.getNamesByNameIds(nameIds);
		Debug.println("testGetNamesByNameIds(" + nameIds + ") RESULTS: " + results.size() + " = " + results);
	}

	@Test
	public void testGetMapNameByMapId() throws Exception {
		Integer mapID = 5; // FROM CENTRAL GROUNDNUT DATABASE
		String expectedName = "RIL-1 (TAG 24 x ICGV 86031)";

		String retrievedName = GenotypicDataManagerImplTest.manager.getMapNameById(mapID);
		Assert.assertEquals(expectedName, retrievedName);
	}

	@Test
	public void testGetNameByNameId() throws Exception {
		Integer nameId = -1;
		Name name = GenotypicDataManagerImplTest.manager.getNameByNameId(nameId);
		Debug.println("testGetNameByNameId(nameId=" + nameId + ") RESULTS: " + name);
	}

	@Test
	public void testGetFirstFiveMaps() throws Exception {
		List<Map> maps = GenotypicDataManagerImplTest.manager.getAllMaps(0, 5, Database.CENTRAL);
		Debug.println("RESULTS (testGetFirstFiveMaps): " + maps);
	}

	@Test
	public void testGetMapInfoByMapName() throws Exception {
		String mapName = "RIL-8 (Yueyou13 x J 11)"; // CENTRAL test data
		List<MapInfo> results = GenotypicDataManagerImplTest.manager.getMapInfoByMapName(mapName, Database.CENTRAL);
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, results);
	}

	@Test
	public void testGetMapInfoByMapNameBothDB() throws Exception {
		String mapName = "GCP-833TestMap"; // LOCAL test data
		List<MapInfo> results = GenotypicDataManagerImplTest.manager.getMapInfoByMapName(mapName, Database.LOCAL);
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, results);
	}

	@Test
	public void testgetMarkerInfoByMarkerIds() throws Exception {
		List<Integer> markerIds = Arrays.asList(1, -1, 2, -2, 3, 4, 5);
		List<MarkerInfo> results = GenotypicDataManagerImplTest.manager.getMarkerInfoByMarkerIds(markerIds);
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, results);
	}

	@Test
	public void testGetMapInfoByMapAndChromosome() throws Exception {
		// Replace with a gdms_markers_onmap.map_id, linkage_group in local
		int mapId = -2; // 1;
		String chromosome = "LG23"; // "BC-1_b11";

		List<MapInfo> results = GenotypicDataManagerImplTest.manager.getMapInfoByMapAndChromosome(mapId, chromosome);
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, results);
	}

	@Test
	public void testGetMapInfoByMapChromosomeAndPosition() throws Exception {
		// Replace with a gdms_markers_onmap.map_id, linkage_group, start_position in local
		int mapId = -2;
		String chromosome = "LG23";
		float startPosition = 123.4f;

		List<MapInfo> results = GenotypicDataManagerImplTest.manager.getMapInfoByMapChromosomeAndPosition(mapId, chromosome, startPosition);
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, results);
	}

	@Test
	public void testGetMapInfoByMarkersAndMap() throws Exception {
		int mapId = -2; // Replace with a gdms_markers_onmap.map_id in local
		List<Integer> markerList = new ArrayList<Integer>();
		markerList.add(-4); // Replace with a (-) gdms_markers_onmap.marker_id in local
		markerList.add(3407); // Replace with a (+) gdms_markers_onmap.marker_id in central

		List<MapInfo> results = GenotypicDataManagerImplTest.manager.getMapInfoByMarkersAndMap(markerList, mapId);
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, results);
	}

	@Test
	public void testGetMarkerOnMaps() throws Exception {
		List<Integer> mapIds = Arrays.asList(-2, 2, 3, 4);
		String linkageGroup = "LG23";
		double startPos = 0d;
		double endPos = 100d;

		List<MarkerOnMap> markersOnMap = GenotypicDataManagerImplTest.manager.getMarkerOnMaps(mapIds, linkageGroup, startPos, endPos);

		Debug.printObjects(MiddlewareIntegrationTest.INDENT, markersOnMap);
	}

	@Test
	public void testGetMarkersOnMapByMarkerIds() throws Exception {
		List<Integer> markerIds = Arrays.asList(-1, -2, -6, 153, 994, 1975);
		List<MarkerOnMap> markersOnMap = GenotypicDataManagerImplTest.manager.getMarkersOnMapByMarkerIds(markerIds);
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, markersOnMap);
	}

	@Test
	public void testGetAllMarkerNamesFromMarkersOnMap() throws Exception {
		List<String> markerNames = GenotypicDataManagerImplTest.manager.getAllMarkerNamesFromMarkersOnMap();
		Debug.println(MiddlewareIntegrationTest.INDENT, "Marker names (" + markerNames.size() + "): " + markerNames);
	}

	@Test
	public void testCountDatasetNames() throws Exception {
		long results = GenotypicDataManagerImplTest.manager.countDatasetNames(Database.LOCAL);
		Debug.println("testCountDatasetNames(Database.LOCAL) RESULTS: " + results);
	}

	@Test
	public void testGetDatasetNames() throws Exception {
		List<String> result = GenotypicDataManagerImplTest.manager.getDatasetNames(0, Integer.MAX_VALUE, Database.LOCAL);
		Debug.printObjects(0, result);
	}

	@Test
	public void testGetDatasetNamesByQtlId() throws Exception {
		Integer qtlId = 1;
		List<String> results = GenotypicDataManagerImplTest.manager.getDatasetNamesByQtlId(qtlId, 0, 5);
		Debug.println("testGetDatasetNamesByQtlId(0,5) RESULTS: " + results);
	}

	@Test
	public void testCountDatasetNamesByQtlId() throws Exception {
		Integer qtlId = 1;
		long results = GenotypicDataManagerImplTest.manager.countDatasetNamesByQtlId(qtlId);
		Debug.println("testCountDatasetNamesByQtlId() RESULTS: " + results);
	}

	@Test
	public void testGetDatasetDetailsByDatasetName() throws Exception {
		String datasetName = "MARS";
		List<DatasetElement> results = GenotypicDataManagerImplTest.manager.getDatasetDetailsByDatasetName(datasetName, Database.LOCAL);
		Debug.println("testGetDatasetDetailsByDatasetName(" + datasetName + ",LOCAL) RESULTS: " + results);
	}

	@Test
	public void testGetMarkersByMarkerNames() throws Exception {
		List<String> markerNames = new ArrayList<String>();
		markerNames.add("GA101");
		markerNames.add("GA110");
		markerNames.add("GA122");
		List<Marker> markers = GenotypicDataManagerImplTest.manager.getMarkersByMarkerNames(markerNames, 0, 100, Database.CENTRAL);
		Assert.assertTrue(markers.size() > 0);
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, markers);
	}

	@Test
	public void testGetMarkerIdsByDatasetId() throws Exception {
		Integer datasetId = Integer.valueOf(2);
		List<Integer> results = GenotypicDataManagerImplTest.manager.getMarkerIdsByDatasetId(datasetId);
		Debug.println("testGetMarkerIdByDatasetId(" + datasetId + ") RESULTS: " + results);
	}

	@Test
	public void testGetParentsByDatasetId() throws Exception {
		Integer datasetId = Integer.valueOf(2);
		List<ParentElement> results = GenotypicDataManagerImplTest.manager.getParentsByDatasetId(datasetId);
		Debug.println("testGetParentsByDatasetId(" + datasetId + ") RESULTS: " + results);
	}

	@Test
	public void testGetMarkerTypesByMarkerIds() throws Exception {
		List<Integer> markerIds = Arrays.asList(1, 2, 3, 4, 5, -1, -2, -3);
		List<String> results = GenotypicDataManagerImplTest.manager.getMarkerTypesByMarkerIds(markerIds);
		Debug.println("testGetMarkerTypeByMarkerIds(" + markerIds + ") RESULTS: " + results);
	}

	@Test
	public void testGetMarkerNamesByGIds() throws Exception {
		List<Integer> gIds = Arrays.asList(1920, 1895, 1434);
		// For test data, SELECT marker_id, gid from gdms_allele_values / gdms_char_values / gdms_mapping_pop_values

		List<MarkerNameElement> results = GenotypicDataManagerImplTest.manager.getMarkerNamesByGIds(gIds);
		Debug.println("testGetMarkerNamesByGIds(" + gIds + ") RESULTS: " + results.size() + " = " + results);
	}

	@Test
	public void testGetGermplasmNamesByMarkerNames() throws Exception {
		List<String> markerNames = Arrays.asList("1_0001", "1_0007", "1_0013");

		List<GermplasmMarkerElement> results =
				GenotypicDataManagerImplTest.manager.getGermplasmNamesByMarkerNames(markerNames, Database.LOCAL);
		Debug.println("testGetGermplasmNamesByMarkerNames(" + markerNames + ")  RESULTS: " + results);
	}

	@Test
	public void testGetMappingValuesByGidsAndMarkerNames() throws Exception {
		List<Integer> gids = Arrays.asList(2212, 2208, 1, -1);
		List<String> markerNames = Arrays.asList("Ah26", "AC2C05", "Ah193", "Seq14H06", "SeqTEST843", "GA1");

		// For test input, please run the following in central/local, and choose select gid / marker_ids
		// SELECT DISTINCT gid, gdms_mapping_pop_values.marker_id
		// FROM gdms_mapping_pop_values
		// JOIN gdms_mapping_pop ON gdms_mapping_pop_values.dataset_id = gdms_mapping_pop.dataset_id
		// LEFT JOIN gdms_marker ON gdms_mapping_pop_values.marker_id = gdms_marker.marker_id;

		List<MappingValueElement> mappingValues =
				GenotypicDataManagerImplTest.manager.getMappingValuesByGidsAndMarkerNames(gids, markerNames, 0, 100);
		Debug.println("testGetMappingValuesByGidsAndMarkerNames(" + gids + ", " + markerNames + "): ");
		Debug.printFormattedObjects(MiddlewareIntegrationTest.INDENT, mappingValues);
	}

	@Test
	public void testGetAllelicValuesByGidsAndMarkerNames() throws Exception {
		List<String> markerNames = Arrays.asList("RN18H05");
		List<Integer> gids = Arrays.asList(7, 1035);
		List<AllelicValueElement> allelicValues =
				GenotypicDataManagerImplTest.manager.getAllelicValuesByGidsAndMarkerNames(gids, markerNames);
		Debug.printObjects(allelicValues);
	}

	@Test
	public void testGetAllelicValuesByGidsAndMarkerNamesForGDMS() throws Exception {

		List<String> markerNames = Arrays.asList("TC03A12", "Seq19E09", "TC7E04", "SeqTEST"); // marker id = 3295, 3296, 1044;
		List<Integer> gids = Arrays.asList(1434, 1435, 1);

		this.testAddGDMSMarker(); // Local test data: add Marker with markerName = "SeqTEST"

		List<AllelicValueElement> allelicValues =
				GenotypicDataManagerImplTest.manager.getAllelicValuesByGidsAndMarkerNames(gids, markerNames);
		Debug.println("testGetAllelicValuesByGidsAndMarkerNamesForGDMS(" + gids + ", " + markerNames + ") RESULTS: ");
		Debug.printFormattedObjects(MiddlewareIntegrationTest.INDENT, allelicValues);
	}

	@Test
	public void testGetAllelicValuesByGid() throws Exception {
		Integer gid = 7;
		List<AllelicValueElement> allelicValues = GenotypicDataManagerImplTest.manager.getAllelicValuesByGid(gid);
		Debug.printObjects(allelicValues);
	}

	@Test
	public void testGetAllelicValuesFromCharValuesByDatasetId() throws Exception {
		Integer datasetId = Integer.valueOf(6);
		long count = GenotypicDataManagerImplTest.manager.countAllelicValuesFromCharValuesByDatasetId(datasetId);
		List<AllelicValueWithMarkerIdElement> allelicValues =
				GenotypicDataManagerImplTest.manager.getAllelicValuesFromCharValuesByDatasetId(datasetId, 0, (int) count);
		Debug.println(0, "testGetAllelicValuesFromCharValuesByDatasetId(" + datasetId + ") RESULTS: " + allelicValues.size());
		Debug.printObjects(allelicValues);
	}

	@Test
	public void testGetAllelicValuesFromAlleleValuesByDatasetId() throws Exception {
		Integer datasetId = Integer.valueOf(2);
		long count = GenotypicDataManagerImplTest.manager.countAllelicValuesFromAlleleValuesByDatasetId(datasetId);
		List<AllelicValueWithMarkerIdElement> allelicValues =
				GenotypicDataManagerImplTest.manager.getAllelicValuesFromAlleleValuesByDatasetId(datasetId, 0, (int) count);
		Debug.println("testGetAllelicValuesFromAlleleValuesByDatasetId(dataset=" + datasetId + ") RESULTS: " + allelicValues.size());
		Debug.printObjects(allelicValues);
	}

	@Test
	public void testGetAllelicValuesFromMappingPopValuesByDatasetId() throws Exception {
		Integer datasetId = Integer.valueOf(3);
		long count = GenotypicDataManagerImplTest.manager.countAllelicValuesFromMappingPopValuesByDatasetId(datasetId);
		// manager.countAllelicValuesFromCharValuesByDatasetId(datasetId);
		List<AllelicValueWithMarkerIdElement> allelicValues =
				GenotypicDataManagerImplTest.manager.getAllelicValuesFromMappingPopValuesByDatasetId(datasetId, 0, (int) count);
		Debug.println("testGetAllelicValuesFromMappingPopValuesByDatasetId(" + datasetId + ") RESULTS: ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, allelicValues);
	}

	@Test
	public void testGetMarkerNamesByMarkerIds() throws Exception {
		List<Integer> markerIds = Arrays.asList(1, -1, 2, -2, 3, -3, 4, -4, 5, -5);
		List<MarkerIdMarkerNameElement> markerNames = GenotypicDataManagerImplTest.manager.getMarkerNamesByMarkerIds(markerIds);
		Debug.println("testGetMarkerNamesByMarkerIds(" + markerIds + ") RESULTS: ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, markerNames);
	}

	@Test
	public void testGetAllMarkerTypes() throws Exception {
		List<String> markerTypes = GenotypicDataManagerImplTest.manager.getAllMarkerTypes(0, 10);
		Debug.println("testGetAllMarkerTypes(0,10) RESULTS: " + markerTypes);
	}

	@Test
	public void testCountAllMarkerTypesLocal() throws Exception {
		long result = GenotypicDataManagerImplTest.manager.countAllMarkerTypes(Database.LOCAL);
		Debug.println("testCountAllMarkerTypes(Database.LOCAL) RESULTS: " + result);
	}

	@Test
	public void testCountAllMarkerTypesCentral() throws Exception {
		long result = GenotypicDataManagerImplTest.manager.countAllMarkerTypes(Database.CENTRAL);
		Debug.println("testCountAllMarkerTypes(Database.CENTRAL) RESULTS: " + result);
	}

	@Test
	public void testGetMarkerNamesByMarkerType() throws Exception {
		String markerType = "asdf";
		List<String> markerNames = GenotypicDataManagerImplTest.manager.getMarkerNamesByMarkerType(markerType, 0, 10);
		Debug.println("testGetMarkerNamesByMarkerType(" + markerType + ") RESULTS: " + markerNames);
	}

	@Test
	public void testCountMarkerNamesByMarkerType() throws Exception {
		String markerType = "asdf";
		long result = GenotypicDataManagerImplTest.manager.countMarkerNamesByMarkerType(markerType);
		Debug.println("testCountMarkerNamesByMarkerType(" + markerType + ") RESULTS: " + result);
	}

	@Test
	public void testGetMarkerInfoByMarkerName() throws Exception {
		String markerName = "1_0437";
		long count = GenotypicDataManagerImplTest.manager.countMarkerInfoByMarkerName(markerName);
		Debug.println("countMarkerInfoByMarkerName(" + markerName + ")  RESULTS: " + count);
		List<MarkerInfo> results = GenotypicDataManagerImplTest.manager.getMarkerInfoByMarkerName(markerName, 0, (int) count);
		Debug.println("testGetMarkerInfoByMarkerName(" + markerName + ") RESULTS: " + results);
	}

	@Test
	public void testGetMarkerInfoByGenotype() throws Exception {
		String genotype = "";
		long count = GenotypicDataManagerImplTest.manager.countMarkerInfoByGenotype(genotype);
		Debug.println("countMarkerInfoByGenotype(" + genotype + ") RESULTS: " + count);
		List<MarkerInfo> results = GenotypicDataManagerImplTest.manager.getMarkerInfoByGenotype(genotype, 0, (int) count);
		Debug.println("testGetMarkerInfoByGenotype(" + genotype + ") RESULTS: " + results);
	}

	@Test
	public void testGetMarkerInfoByDbAccessionId() throws Exception {
		String dbAccessionId = "";
		long count = GenotypicDataManagerImplTest.manager.countMarkerInfoByDbAccessionId(dbAccessionId);
		Debug.println("countMarkerInfoByDbAccessionId(" + dbAccessionId + ")  RESULTS: " + count);
		List<MarkerInfo> results = GenotypicDataManagerImplTest.manager.getMarkerInfoByDbAccessionId(dbAccessionId, 0, (int) count);
		Debug.println("testGetMarkerInfoByDbAccessionId(" + dbAccessionId + ")  RESULTS: " + results);
	}

	@Test
	public void testGetGidsFromCharValuesByMarkerId() throws Exception {
		Integer markerId = 1;
		List<Integer> gids = GenotypicDataManagerImplTest.manager.getGIDsFromCharValuesByMarkerId(markerId, 0, 10);
		Debug.println("testGetGidsFromCharValuesByMarkerId(" + markerId + ") RESULTS: " + gids);
	}

	@Test
	public void testCountGidsFromCharValuesByMarkerId() throws Exception {
		Integer markerId = 1;
		long result = GenotypicDataManagerImplTest.manager.countGIDsFromCharValuesByMarkerId(markerId);
		Debug.println("testCountGidsFromCharValuesByMarkerId(" + markerId + ") RESULTS: " + result);
	}

	@Test
	public void testGetGidsFromAlleleValuesByMarkerId() throws Exception {
		Integer markerId = 1;
		List<Integer> gids = GenotypicDataManagerImplTest.manager.getGIDsFromAlleleValuesByMarkerId(markerId, 0, 10);
		Debug.println("testGetGidsFromAlleleValuesByMarkerId(" + markerId + ") RESULTS: " + gids);
	}

	@Test
	public void testCountGidsFromAlleleValuesByMarkerId() throws Exception {
		Integer markerId = 1;
		long result = GenotypicDataManagerImplTest.manager.countGIDsFromCharValuesByMarkerId(markerId);
		Debug.println("testCountGidsFromAlleleValuesByMarkerId(" + markerId + ") RESULTS: " + result);
	}

	@Test
	public void testGetGidsFromMappingPopValuesByMarkerId() throws Exception {
		Integer markerId = 1;
		List<Integer> gids = GenotypicDataManagerImplTest.manager.getGIDsFromMappingPopValuesByMarkerId(markerId, 0, 10);
		Debug.println("testGetGidsFromMappingPopValuesByMarkerId(" + markerId + ") RESULTS: " + gids);
	}

	@Test
	public void testGetAllelicValuesByMarkersAndAlleleValues() throws Exception {
		List<Integer> markerIdList = Arrays.asList(2325, 3345);
		List<String> alleleValueList = Arrays.asList("238/238", "T/T");
		List<AllelicValueElement> values =
				GenotypicDataManagerImplTest.manager.getAllelicValuesByMarkersAndAlleleValues(Database.CENTRAL, markerIdList,
						alleleValueList);
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, values);
	}

	@Test
	public void testGetAllAllelicValuesByMarkersAndAlleleValues() throws Exception {
		List<Integer> markerIdList = Arrays.asList(2325);
		List<String> alleleValueList = Arrays.asList("238/238");
		List<AllelicValueElement> values =
				GenotypicDataManagerImplTest.manager.getAllAllelicValuesByMarkersAndAlleleValues(markerIdList, alleleValueList);
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, values);
	}

	@Test
	public void testGetGidsByMarkersAndAlleleValues() throws Exception {
		List<Integer> markerIdList = Arrays.asList(2325);
		List<String> alleleValueList = Arrays.asList("238/238");
		List<Integer> values = GenotypicDataManagerImplTest.manager.getGidsByMarkersAndAlleleValues(markerIdList, alleleValueList);
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, values);
	}

	@Test
	public void testCountGidsFromMappingPopValuesByMarkerId() throws Exception {
		Integer markerId = 1;
		long result = GenotypicDataManagerImplTest.manager.countGIDsFromMappingPopValuesByMarkerId(markerId);
		Debug.println("testCountGidsFromMappingPopValuesByMarkerId(" + markerId + ") RESULTS: " + result);
	}

	@Test
	public void testGetAllDbAccessionIdsFromMarker() throws Exception {
		List<String> dbAccessionIds = GenotypicDataManagerImplTest.manager.getAllDbAccessionIdsFromMarker(0, 10);
		Debug.println("testGetAllDbAccessionIdsFromMarker(1,10) RESULTS: " + dbAccessionIds);
	}

	@Test
	public void testCountAllDbAccessionIdsFromMarker() throws Exception {
		long result = GenotypicDataManagerImplTest.manager.countAllDbAccessionIdsFromMarker();
		Debug.println("testCountAllDbAccessionIdsFromMarker() RESULTS: " + result);
	}

	@Test
	public void testGetAccMetadatasetsByDatasetIds() throws Exception {
		List<Integer> datasetIds = Arrays.asList(-1, -2, -3, 1, 2, 3);
		List<Integer> gids = Arrays.asList(-2);

		List<AccMetadataSet> nids = GenotypicDataManagerImplTest.manager.getAccMetadatasetsByDatasetIds(datasetIds, 0, 10);
		List<AccMetadataSet> nidsWithGidFilter =
				GenotypicDataManagerImplTest.manager.getAccMetadatasetsByDatasetIdsAndNotGids(datasetIds, gids, 0, 10);

		Debug.println("testGgetAccMetadatasetByDatasetIds: " + nids);
		Debug.println("testGetgetAccMetadatasetByDatasetIds with gid filter: " + nidsWithGidFilter);
	}

	@Test
	public void testGetDatasetIdsForFingerPrinting() throws Exception {
		List<Integer> datasetIds =
				GenotypicDataManagerImplTest.manager.getDatasetIdsForFingerPrinting(0,
						(int) GenotypicDataManagerImplTest.manager.countDatasetIdsForFingerPrinting());
		Debug.println("testGetDatasetIdsForFingerPrinting() RESULTS: " + datasetIds);
	}

	@Test
	public void testCountDatasetIdsForFingerPrinting() throws Exception {
		long count = GenotypicDataManagerImplTest.manager.countDatasetIdsForFingerPrinting();
		Debug.println("testCountDatasetIdsForFingerPrinting() RESULTS: " + count);
	}

	@Test
	public void testGetDatasetIdsForMapping() throws Exception {
		List<Integer> datasetIds =
				GenotypicDataManagerImplTest.manager.getDatasetIdsForMapping(0,
						(int) GenotypicDataManagerImplTest.manager.countDatasetIdsForMapping());
		Debug.println("testGetDatasetIdsForMapping() RESULTS: " + datasetIds);
	}

	@Test
	public void testCountDatasetIdsForMapping() throws Exception {
		long count = GenotypicDataManagerImplTest.manager.countDatasetIdsForMapping();
		Debug.println("testCountDatasetIdsForMapping() RESULTS: " + count);
	}

	@Test
	public void testGetGdmsAccMetadatasetByGid() throws Exception {
		List<Integer> germplasmIds = Arrays.asList(1, -1, -2);
		List<AccMetadataSet> accMetadataSets =
				GenotypicDataManagerImplTest.manager.getGdmsAccMetadatasetByGid(germplasmIds, 0,
						(int) GenotypicDataManagerImplTest.manager.countGdmsAccMetadatasetByGid(germplasmIds));
		Debug.println("testGetGdmsAccMetadatasetByGid() RESULTS: ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, accMetadataSets);
	}

	@Test
	public void testCountGdmsAccMetadatasetByGid() throws Exception {
		List<Integer> germplasmIds = Arrays.asList(956, 1042, -2213, -2215);
		long count = GenotypicDataManagerImplTest.manager.countGdmsAccMetadatasetByGid(germplasmIds);
		Debug.println("testCountGdmsAccMetadatasetByGid() RESULTS: " + count);
	}

	@Test
	public void testGetMarkersByGidAndDatasetIds() throws Exception {
		Integer gid = Integer.valueOf(-2215);
		List<Integer> datasetIds = Arrays.asList(1, 2);
		List<Integer> markerIds =
				GenotypicDataManagerImplTest.manager.getMarkersByGidAndDatasetIds(gid, datasetIds, 0,
						(int) GenotypicDataManagerImplTest.manager.countMarkersByGidAndDatasetIds(gid, datasetIds));
		Debug.println("testGetMarkersByGidAndDatasetIds() RESULTS: " + markerIds);
	}

	@Test
	public void testCountMarkersByGidAndDatasetIds() throws Exception {
		Integer gid = Integer.valueOf(-2215);
		List<Integer> datasetIds = Arrays.asList(1, 2);
		long count = GenotypicDataManagerImplTest.manager.countMarkersByGidAndDatasetIds(gid, datasetIds);
		Debug.println("testCountGdmsAccMetadatasetByGid() RESULTS: " + count);
	}

	@Test
	public void testCountAlleleValuesByGids() throws Exception {
		List<Integer> germplasmIds = Arrays.asList(956, 1042, -2213, -2215);
		long count = GenotypicDataManagerImplTest.manager.countAlleleValuesByGids(germplasmIds);
		Debug.println("testCountAlleleValuesByGids() RESULTS: " + count);
	}

	@Test
	public void testCountCharValuesByGids() throws Exception {
		List<Integer> germplasmIds = Arrays.asList(956, 1042, -2213, -2215);
		long count = GenotypicDataManagerImplTest.manager.countCharValuesByGids(germplasmIds);
		Debug.println("testCountCharValuesByGids() RESULTS: " + count);
	}

	@Test
	public void testGetIntAlleleValuesForPolymorphicMarkersRetrieval() throws Exception {
		// For test data, get gids from LOCAL and CENTRAL: SELECT distinct FROM gdms_allele_values;
		List<Integer> germplasmIds = Arrays.asList(1, 956, 1042, -2213, -2215);

		List<AllelicValueElement> results =
				GenotypicDataManagerImplTest.manager.getIntAlleleValuesForPolymorphicMarkersRetrieval(germplasmIds, 0, Integer.MAX_VALUE);
		Debug.println("testGetIntAlleleValuesForPolymorphicMarkersRetrieval() RESULTS: ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, results);
	}

	@Test
	public void testCountIntAlleleValuesForPolymorphicMarkersRetrieval() throws Exception {
		// For test data, get gids from LOCAL and CENTRAL: SELECT distinct FROM gdms_allele_values;
		List<Integer> germplasmIds = Arrays.asList(1, 956, 1042, -2213, -2215);
		long count = GenotypicDataManagerImplTest.manager.countIntAlleleValuesForPolymorphicMarkersRetrieval(germplasmIds);
		Debug.println("testCountIntAlleleValuesForPolymorphicMarkersRetrieval() RESULTS: " + count);
	}

	@Test
	public void testGetCharAlleleValuesForPolymorphicMarkersRetrieval() throws Exception {
		// For test data, get gids from LOCAL and CENTRAL: SELECT distinct FROM gdms_char_values;
		List<Integer> germplasmIds = Arrays.asList(1, 956, 1042, -2213, -2215);
		List<AllelicValueElement> results =
				GenotypicDataManagerImplTest.manager.getCharAlleleValuesForPolymorphicMarkersRetrieval(germplasmIds, 0, Integer.MAX_VALUE);
		Debug.println("testGetIntAlleleValuesForPolymorphicMarkersRetrieval() RESULTS: ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, results);
	}

	@Test
	public void testCountCharAlleleValuesForPolymorphicMarkersRetrieval() throws Exception {
		// For test data, get gids from LOCAL and CENTRAL: SELECT distinct FROM gdms_char_values;
		List<Integer> germplasmIds = Arrays.asList(1, 956, 1042, -2213, -2215);
		long count = GenotypicDataManagerImplTest.manager.countCharAlleleValuesForPolymorphicMarkersRetrieval(germplasmIds);
		Debug.println("testCountCharAlleleValuesForPolymorphicMarkersRetrieval() RESULTS: " + count);
	}

	@Test
	public void testGetMappingAlleleValuesForPolymorphicMarkersRetrieval() throws Exception {
		// For test data, get gids from LOCAL and CENTRAL: SELECT distinct FROM gdms_mapping_pop_values;
		List<Integer> germplasmIds = Arrays.asList(1, 1434, 1435);
		List<AllelicValueElement> results =
				GenotypicDataManagerImplTest.manager.getMappingAlleleValuesForPolymorphicMarkersRetrieval(germplasmIds, 0,
						Integer.MAX_VALUE);
		Debug.println("testGetMappingAlleleValuesForPolymorphicMarkersRetrieval() RESULTS: ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, results);
	}

	@Test
	public void testCountMappingAlleleValuesForPolymorphicMarkersRetrieval() throws Exception {
		// For test data, get gids from LOCAL and CENTRAL: SELECT distinct FROM gdms_mapping_pop_values;
		List<Integer> germplasmIds = Arrays.asList(1, 1434, 1435);
		long count = GenotypicDataManagerImplTest.manager.countMappingAlleleValuesForPolymorphicMarkersRetrieval(germplasmIds);
		Debug.println("testCountMappingAlleleValuesForPolymorphicMarkersRetrieval() RESULTS: " + count);
	}

	@Test
	public void testGetNIdsByDatasetIdsAndMarkerIdsAndNotGIds() throws Exception {
		List<Integer> gIds = Arrays.asList(956, 1042, 1128);
		List<Integer> datasetIds = Arrays.asList(2);
		List<Integer> markerIds = Arrays.asList(6, 10);

		List<Integer> nIdList =
				GenotypicDataManagerImplTest.manager.getNIdsByMarkerIdsAndDatasetIdsAndNotGIds(datasetIds, markerIds, gIds, 0,
						GenotypicDataManagerImplTest.manager.countNIdsByMarkerIdsAndDatasetIdsAndNotGIds(datasetIds, markerIds, gIds));
		Debug.println("testGetNIdsByDatasetIdsAndMarkerIdsAndNotGIds(datasetIds=" + datasetIds + ") RESULTS: " + nIdList);

		nIdList = GenotypicDataManagerImplTest.manager.getNIdsByMarkerIdsAndDatasetIdsAndNotGIds(null, gIds, markerIds, 0, 5);
		Debug.println("testGetNIdsByDatasetIdsAndMarkerIdsAndNotGIds(datasetIds=null) RESULTS: " + nIdList);

		nIdList = GenotypicDataManagerImplTest.manager.getNIdsByMarkerIdsAndDatasetIdsAndNotGIds(datasetIds, null, markerIds, 0, 5);
		Debug.println("testGetNIdsByDatasetIdsAndMarkerIdsAndNotGIds(gIds=null) RESULTS: " + nIdList);

		nIdList = GenotypicDataManagerImplTest.manager.getNIdsByMarkerIdsAndDatasetIdsAndNotGIds(datasetIds, gIds, null, 0, 5);
		Debug.println("testGetNIdsByDatasetIdsAndMarkerIdsAndNotGIds(markerIds=null) RESULTS: " + nIdList);

	}

	@Test
	public void testCountNIdsByDatasetIdsAndMarkerIdsAndNotGIds() throws Exception {
		List<Integer> gIds = Arrays.asList(29, 303, 4950);
		List<Integer> datasetIds = Arrays.asList(2);
		List<Integer> markerIds = Arrays.asList(6803);
		int count = GenotypicDataManagerImplTest.manager.countNIdsByMarkerIdsAndDatasetIdsAndNotGIds(datasetIds, markerIds, gIds);
		Debug.println("testcCountNIdsByDatasetIdsAndMarkerIdsAndNotGIds() RESULTS: " + count);
	}

	@Test
	public void testGetNIdsByDatasetIdsAndMarkerIds() throws Exception {
		List<Integer> datasetIds = Arrays.asList(2);
		List<Integer> markerIds = Arrays.asList(6803);
		List<Integer> nIdList =
				GenotypicDataManagerImplTest.manager.getNIdsByMarkerIdsAndDatasetIds(datasetIds, markerIds, 0,
						GenotypicDataManagerImplTest.manager.countNIdsByMarkerIdsAndDatasetIds(datasetIds, markerIds));
		Debug.println("testGetNIdsByDatasetIdsAndMarkerIds() RESULTS: " + nIdList);
	}

	@Test
	public void testCountNIdsByDatasetIdsAndMarkerIds() throws Exception {
		List<Integer> datasetIds = Arrays.asList(2);
		List<Integer> markerIds = Arrays.asList(6803);
		int count = GenotypicDataManagerImplTest.manager.countNIdsByMarkerIdsAndDatasetIds(datasetIds, markerIds);
		Debug.println("testCountNIdsByDatasetIdsAndMarkerIds() RESULTS: " + count);
	}

	@Test
	public void testGetAllQtl() throws Exception {
		List<Qtl> qtls = GenotypicDataManagerImplTest.manager.getAllQtl(0, (int) GenotypicDataManagerImplTest.manager.countAllQtl());
		Debug.println("testGetAllQtl() RESULTS: ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, qtls);
	}

	@Test
	public void testCountAllQtl() throws Exception {
		long result = GenotypicDataManagerImplTest.manager.countAllQtl();
		Debug.println("testCountAllQtl() RESULTS: " + result);
	}

	@Test
	public void testCountQtlIdByName() throws Exception {
		String qtlName = "SLA%";
		long count = GenotypicDataManagerImplTest.manager.countQtlIdByName(qtlName);
		Debug.println("testCountQtlIdByName() RESULTS: " + count);
	}

	@Test
	public void testGetQtlIdByName() throws Exception {
		String qtlName = "TWW09_AhV";

		List<Integer> results =
				GenotypicDataManagerImplTest.manager.getQtlIdByName(qtlName, 0,
						(int) GenotypicDataManagerImplTest.manager.countQtlIdByName(qtlName));
		Debug.println("testGetQtlIdByName() RESULTS: " + results);
	}

	@Test
	public void testGetQtlByNameFromCentral() throws Exception {
		String qtlName = "HI Control%";
		List<QtlDetailElement> results =
				GenotypicDataManagerImplTest.manager.getQtlByName(qtlName, 0,
						(int) GenotypicDataManagerImplTest.manager.countQtlByName(qtlName));
		Assert.assertTrue(results.size() > 0);
		Debug.printFormattedObjects(MiddlewareIntegrationTest.INDENT, results);
		Debug.println("testGetQtlByNameFromCentral() #records: " + results.size());
	}

	@Test
	public void testGetQtlByNameFromLocal() throws Exception {
		try {
			this.testSetQTL(); // to add a qtl_details entry with name = "TestQTL"
		} catch (MiddlewareQueryException e) {
			// Ignore. There is already a record for testing.
		}
		String qtlName = "TestQTL%";
		List<QtlDetailElement> results =
				GenotypicDataManagerImplTest.manager.getQtlByName(qtlName, 0,
						(int) GenotypicDataManagerImplTest.manager.countQtlByName(qtlName));
		Assert.assertTrue(results.size() > 0);
		Debug.printFormattedObjects(MiddlewareIntegrationTest.INDENT, results);
		Debug.println("testGetQtlByNameFromLocal() #records: " + results.size());
	}

	@Test
	public void testCountQtlByName() throws Exception {
		String qtlName = "HI Control%";
		long count = GenotypicDataManagerImplTest.manager.countQtlByName(qtlName);
		Assert.assertTrue(count > 0);
		Debug.println("testCountQtlByName() RESULTS: " + count);
	}

	@Test
	public void testgetQtlNamesByQtlIds() throws Exception {
		List<Integer> qtlIds = Arrays.asList(1, 2, 3, -4, -5);
		java.util.Map<Integer, String> qtlNames = GenotypicDataManagerImplTest.manager.getQtlNamesByQtlIds(qtlIds);
		Assert.assertTrue(qtlNames.size() > 0);
		for (int i = 0; i < qtlIds.size(); i++) {
			Debug.println(MiddlewareIntegrationTest.INDENT, "QTL ID = " + qtlIds.get(i) + " : QTL NAME = " + qtlNames.get(qtlIds.get(i)));
		}
	}

	@Test
	public void testGetQtlByTrait() throws Exception {
		Integer qtlTraitId = 1001; // "SL%";
		List<Integer> results =
				GenotypicDataManagerImplTest.manager.getQtlByTrait(qtlTraitId, 0,
						(int) GenotypicDataManagerImplTest.manager.countQtlByTrait(qtlTraitId));
		Debug.println("testGetQtlByTrait() RESULTS: " + results);
	}

	@Test
	public void testCountQtlByTrait() throws Exception {
		Integer qtlTraitId = 1001; // "SL%";
		long count = GenotypicDataManagerImplTest.manager.countQtlByTrait(qtlTraitId);
		Debug.println("testCountQtlByTrait() RESULTS: " + count);
	}

	@Test
	public void testGetQtlTraitsByDatasetId() throws Exception {
		Integer datasetId = 7;
		List<Integer> results =
				GenotypicDataManagerImplTest.manager.getQtlTraitsByDatasetId(datasetId, 0,
						(int) GenotypicDataManagerImplTest.manager.countQtlTraitsByDatasetId(datasetId));
		Debug.println("testGetQtlTraitsByDatasetId() RESULTS: " + results);
	}

	@Test
	public void testCountQtlTraitsByDatasetId() throws Exception {
		Integer datasetId = 7;
		long count = GenotypicDataManagerImplTest.manager.countQtlTraitsByDatasetId(datasetId);
		Debug.println("testCountQtlTraitsByDatasetId() RESULTS: " + count);
	}

	@Test
	public void testGetMapIdsByQtlName() throws Exception {
		String qtlName = "HI Control 08_AhI";
		List<Integer> results =
				GenotypicDataManagerImplTest.manager.getMapIdsByQtlName(qtlName, 0,
						(int) GenotypicDataManagerImplTest.manager.countMapIdsByQtlName(qtlName));
		Debug.println("testGetMapIdsByQtlName() RESULTS: " + results);
	}

	@Test
	public void testCountMapIdsByQtlName() throws Exception {
		String qtlName = "HI Control 08_AhI";
		long count = GenotypicDataManagerImplTest.manager.countMapIdsByQtlName(qtlName);
		Debug.println("testCountMapIdsByQtlName() RESULTS: " + count);
	}

	@Test
	public void testGetMarkersByQtl() throws Exception {
		String qtlName = "HI Control 08_AhI";
		String chromosome = "RIL-3 _LG01";
		float min = 0.0f;
		float max = 10f;
		List<Integer> results =
				GenotypicDataManagerImplTest.manager.getMarkerIdsByQtl(qtlName, chromosome, min, max, 0,
						(int) GenotypicDataManagerImplTest.manager.countMarkerIdsByQtl(qtlName, chromosome, min, max));
		Assert.assertTrue(results.size() > 0);
		Debug.println("testGetMarkersByQtl() RESULTS: " + results);
	}

	@Test
	public void testCountMarkersByQtl() throws Exception {
		String qtlName = "HI Control 08_AhI";
		String chromosome = "RIL-3 _LG01";
		float min = 0f;
		float max = 10f;
		long count = GenotypicDataManagerImplTest.manager.countMarkerIdsByQtl(qtlName, chromosome, min, max);
		Assert.assertTrue(count > 0);
		Debug.println("testCountMarkersByQtl() RESULTS: " + count);
	}

	@Test
	public void getAllParentsFromMappingPopulation() throws Exception {
		int start = 0;
		int end = 10;
		List<ParentElement> parentElements = GenotypicDataManagerImplTest.manager.getAllParentsFromMappingPopulation(start, end);
		Debug.println("getAllParentsFromMappingPopulation(" + start + "," + end + ")");
		for (ParentElement parentElement : parentElements) {
			Debug.println(MiddlewareIntegrationTest.INDENT, "Parent A NId: " + parentElement.getParentANId() + "  |  Parent B NId: "
					+ parentElement.getParentBGId());
		}
	}

	@Test
	public void countAllParentsFromMappingPopulation() throws Exception {
		long parentElementsCount = GenotypicDataManagerImplTest.manager.countAllParentsFromMappingPopulation();
		Debug.println("countAllParentsFromMappingPopulation()");
		Debug.println("Count: " + parentElementsCount);
	}

	@Test
	public void getMapDetailsByName() throws Exception {
		String nameLike = "tag%";
		int start = 0;
		int end = 10;
		List<MapDetailElement> maps = GenotypicDataManagerImplTest.manager.getMapDetailsByName(nameLike, start, end);
		Debug.println("getMapDetailsByName('" + nameLike + "'," + start + "," + end + ")");
		for (MapDetailElement map : maps) {
			Debug.println("Map: " + map.getMarkerCount() + "  |  maxStartPosition: " + map.getMaxStartPosition() + "  |  linkageGroup: "
					+ map.getLinkageGroup() + "  |  mapName: " + map.getMapName() + "  |  mapType:  " + map.getMapType());
		}
	}

	@Test
	public void countMapDetailsByName() throws Exception {
		String nameLike = "tag%";
		Long parentElementsCount = GenotypicDataManagerImplTest.manager.countMapDetailsByName(nameLike);
		Debug.println("countMapDetailsByName('" + nameLike + "')");
		Debug.println("Count: " + parentElementsCount);
	}

	@Test
	public void testGetAllMapDetails() throws Exception {
		int start = 0;
		int end = (int) GenotypicDataManagerImplTest.manager.countAllMapDetails();
		List<MapDetailElement> maps = GenotypicDataManagerImplTest.manager.getAllMapDetails(start, end);
		Debug.println("testGetAllMapDetails(" + start + "," + end + ")");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, maps);
	}

	@Test
	public void testCountAllMapDetails() throws Exception {
		Debug.println("testCountAllMapDetails(): " + GenotypicDataManagerImplTest.manager.countAllMapDetails());
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

		MarkerDetails markerDetails =
				new MarkerDetails(markerId, noOfRepeats, motifType, sequence, sequenceLength, minAllele, maxAllele, ssrNr,
						forwardPrimerTemp, reversePrimerTemp, elongationTemp, fragmentSizeExpected, fragmentSizeObserved,
						expectedProductSize, positionOnReferenceSequence, restrictionEnzymeForAssay);

		Integer idAdded = GenotypicDataManagerImplTest.manager.addMarkerDetails(markerDetails);
		Debug.println("testAddMarkerDetails() Added: " + (idAdded != null ? markerDetails : null));
	}

	@Test
	public void testAddMarkerUserInfo() throws Exception {
		Integer markerId = -1; // Replace with desired values
		String principalInvestigator = "Juan Dela Cruz";
		String contact = "juan@irri.com.ph";
		String institute = "IRRI";

		MarkerUserInfo markerUserInfo = new MarkerUserInfo(markerId, principalInvestigator, contact, institute);

		Integer idAdded = GenotypicDataManagerImplTest.manager.addMarkerUserInfo(markerUserInfo);
		Debug.println("testAddMarkerUserInfo() Added: " + (idAdded != null ? markerUserInfo : null));
	}

	@Test
	public void testAddAccMetadataSet() throws Exception {
		Integer datasetId = -1;
		Integer germplasmId = 1;
		Integer nameId = 1;
		Integer sampleId = 1;
		AccMetadataSet accMetadataSet = new AccMetadataSet(null, datasetId, germplasmId, nameId, sampleId);

		Integer idAdded = GenotypicDataManagerImplTest.manager.addAccMetadataSet(accMetadataSet);
		Debug.println("testAccMetadataSet() Added: " + (idAdded != null ? accMetadataSet : null));
	}

	@Test
	public void testAddMarkerMetadataSet() throws Exception {
		Integer markerMetadatasetId = null;
		Integer datasetId = -1;
		Integer markerId = -1;
		Integer markerSampleId = 1;
		MarkerMetadataSet markerMetadataSet = new MarkerMetadataSet(markerMetadatasetId, datasetId, markerId, markerSampleId);

		Integer idAdded = GenotypicDataManagerImplTest.manager.addMarkerMetadataSet(markerMetadataSet);
		Debug.println("testAddMarkerMetadataSet() Added: " + (idAdded != null ? markerMetadataSet : null));
	}

	@Test
	public void testAddDataset() throws Exception {
		Dataset dataset = this.createDataset();
		GenotypicDataManagerImplTest.manager.addDataset(dataset);
		Debug.println("testAddDataset() Added: " + (dataset.getDatasetId() != null ? dataset : null));
	}

	private Dataset createDataset() throws Exception {
		Integer datasetId = null;
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

		return new Dataset(datasetId, datasetName, datasetDesc, datasetType, genus, species, uploadTemplateDate, remarks, dataType,
				missingData, method, score, institute, principalInvestigator, email, purposeOfStudy);
	}

	@Test
	public void testAddGDMSMarker() throws Exception {
		Integer markerId = null;
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

		Marker marker =
				new Marker(markerId, markerType, markerName, species, dbAccessionId, reference, genotype, ploidy, primerId, remarks,
						assayType, motif, forwardPrimer, reversePrimer, productSize, annealingTemp, amplification);

		// On first run, this will insert record to local
		try {
			Integer idAdded = GenotypicDataManagerImplTest.manager.addGDMSMarker(marker);
			Debug.println("testAddGDMSMarker() Added: " + (idAdded != null ? marker : null));
		} catch (MiddlewareQueryException e) {
			Debug.println(e.getMessage() + "\n");
		}

		// Try adding record that exists in central
		try {
			marker.setMarkerName("GA101");
			Integer idAdded = GenotypicDataManagerImplTest.manager.addGDMSMarker(marker);
			Debug.println("testAddGDMSMarker() Added: " + (idAdded != null ? marker : null));
		} catch (MiddlewareQueryException e) {
			Assert.assertTrue(e.getMessage().contains("Marker already exists in Central or Local and cannot be added"));
		}

	}

	@Test
	public void testAddGDMSMarkerAlias() throws Exception {
		Integer markerId = -1;
		String alias = "testalias";
		MarkerAlias markerAlias = new MarkerAlias(null, markerId, alias);

		Integer idAdded = GenotypicDataManagerImplTest.manager.addGDMSMarkerAlias(markerAlias);
		Debug.println("testAddGDMSMarkerAlias() Added: " + (idAdded != null ? markerAlias : null));
	}

	@Test
	public void testAddDatasetUser() throws Exception {

		Integer datasetId = -1;
		Integer userId = 123;
		DatasetUsers datasetUser = new DatasetUsers(datasetId, userId);

		Integer idAdded = GenotypicDataManagerImplTest.manager.addDatasetUser(datasetUser);
		Debug.println("testAddDatasetUser() Added: " + (idAdded != null ? datasetUser : null));

	}

	@Test
	public void testAddAlleleValues() throws Exception {
		Integer anId = null;
		Integer datasetId = -1;
		Integer gId = 1920;
		Integer markerId = 1037;
		String alleleBinValue = "alleleBinValue";
		String alleleRawValue = "alleleRawValue";
		Integer peakHeight = 10;
		AlleleValues alleleValues = new AlleleValues(anId, datasetId, gId, markerId, alleleBinValue, alleleRawValue, peakHeight);

		Integer idAdded = GenotypicDataManagerImplTest.manager.addAlleleValues(alleleValues);
		Debug.println("testAddAlleleValues() Added: " + (idAdded != null ? alleleValues : null));
	}

	@Test
	public void testAddCharValues() throws Exception {
		Integer acId = null;
		Integer datasetId = -1;
		Integer gId = 1920;
		Integer markerId = 1037;
		String charValue = "CV";
		Integer markerSampleId = 1;
		Integer accSampleId = 1;
		CharValues charValues = new CharValues(acId, datasetId, markerId, gId, charValue, markerSampleId, accSampleId);

		Integer idAdded = GenotypicDataManagerImplTest.manager.addCharValues(charValues);
		Debug.println("testAddCharValues() Added: " + (idAdded != null ? charValues : null));
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

		MappingPop mappingPop =
				new MappingPop(datasetId, mappingType, parentAGId, parentBGId, populationSize, populationType, mapDataDescription,
						scoringScheme, mapId);

		Integer idAdded = GenotypicDataManagerImplTest.manager.addMappingPop(mappingPop);
		Debug.println("testAddMappingPop() Added: " + (idAdded != null ? mappingPop : null));
	}

	@Test
	public void testAddMappingPopValue() throws Exception {
		Integer mpId = null;
		String mapCharValue = "X";
		Integer datasetId = -1;
		Integer gid = 1434;
		Integer markerId = 2537;
		Integer markerSampleId = 1;
		Integer accSampleId = 1;
		MappingPopValues mappingPopValue = new MappingPopValues(mpId, mapCharValue, datasetId, gid, markerId, markerSampleId, accSampleId);

		Integer idAdded = GenotypicDataManagerImplTest.manager.addMappingPopValue(mappingPopValue);
		Debug.println("testAddMappingPopValue() Added: " + (idAdded != null ? mappingPopValue : null));
	}

	@Test
	public void testAddMarkerOnMap() throws Exception {
		Integer markerOnMapId = null;
		Integer mapId = -2;
		Integer markerId = -6;
		Float startPosition = Float.valueOf("123.4");
		Float endPosition = Float.valueOf("567.8");
		// String mapUnit = "TS";
		String linkageGroup = "Test";
		MarkerOnMap markerOnMap = new MarkerOnMap(markerOnMapId, mapId, markerId, startPosition, endPosition, linkageGroup);

		try {
			Integer idAdded = GenotypicDataManagerImplTest.manager.addMarkerOnMap(markerOnMap);
			Debug.println("testAddMarkerOnMap() Added: " + (idAdded != null ? markerOnMap : null));
		} catch (MiddlewareQueryException e) {
			if (e.getMessage().contains("Map Id not found")) {
				Debug.println("Foreign key constraint violation: Map Id not found");
			}
		}
	}

	@Test
	public void testAddDartValue() throws Exception {
		Integer adId = null;
		Integer datasetId = -1;
		Integer markerId = -1;
		Integer cloneId = -1;
		Float qValue = Float.valueOf("123.45");
		Float reproducibility = Float.valueOf("543.21");
		Float callRate = Float.valueOf("12.3");
		Float picValue = Float.valueOf("543.2");
		Float discordance = Float.valueOf("1.2");

		DartValues dartValue = new DartValues(adId, datasetId, markerId, cloneId, qValue, reproducibility, callRate, picValue, discordance);

		Integer idAdded = GenotypicDataManagerImplTest.manager.addDartValue(dartValue);
		Debug.println("testAddDartValue() Added: " + (idAdded != null ? dartValue : null));
	}

	@Test
	public void testAddQtl() throws Exception {
		Integer qtlId = null;
		String qtlName = "TestQTL";
		Dataset dataset = this.createDataset();
		Integer datasetId = GenotypicDataManagerImplTest.manager.addDataset(dataset);

		Qtl qtl = new Qtl(qtlId, qtlName, datasetId);
		Integer idAdded = GenotypicDataManagerImplTest.manager.addQtl(qtl);
		Debug.println("testAddQtl() Added: " + (idAdded != null ? qtl : null));

		this.testAddQtlDetails(idAdded);
	}

	private void testAddQtlDetails(Integer qtlId) throws Exception {
		Integer mapId = -2;
		Float minPosition = 0f;
		Float maxPosition = 8f;
		Integer traitId = 1001; // "DE";
		String experiment = "";
		Float effect = 0f;
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

		QtlDetails qtlDetails =
				new QtlDetails(qtlId, mapId, minPosition, maxPosition, traitId, experiment, effect, scoreValue, rSquare, linkageGroup,
						interactions, leftFlankingMarker, rightFlankingMarker, position, clen, seAdditive, hvParent, hvAllele, lvParent,
						lvAllele);

		Integer idAdded = GenotypicDataManagerImplTest.manager.addQtlDetails(qtlDetails);
		Debug.println("testAddQtlDetails() Added: " + (idAdded != null ? qtlDetails : null));
	}

	@Test
	public void testAddMap() throws Exception {
		this.addMap();
	}

	private Integer addMap() throws Exception {
		Integer mapId = null;
		String mapName = "ICGS 44 X ICGS 76";
		String mapType = "genetic";
		Integer mpId = 0;
		String mapDesc = null;
		String mapUnit = null;
		String genus = "Genus";
		String species = "Groundnut";
		String institute = "ICRISAT";
		Map map = new Map(mapId, mapName, mapType, mpId, mapDesc, mapUnit, genus, species, institute);

		Integer idAdded = GenotypicDataManagerImplTest.manager.addMap(map);
		Debug.println("testAddMap() Added: " + (idAdded != null ? map : null));

		return idAdded;

	}

	@Test
	public void testSetSSRMarkers() throws Exception {
		List<Object> markerRecords = this.createMarkerRecords();
		Marker marker = (Marker) markerRecords.get(0);
		MarkerAlias markerAlias = (MarkerAlias) markerRecords.get(1);
		MarkerDetails markerDetails = (MarkerDetails) markerRecords.get(2);
		MarkerUserInfo markerUserInfo = (MarkerUserInfo) markerRecords.get(3);

		Boolean addStatus = GenotypicDataManagerImplTest.manager.setSSRMarkers(marker, markerAlias, markerDetails, markerUserInfo);
		if (addStatus) {
			Debug.println("testSetSSRMarkers() Added: ");
			Debug.println(MiddlewareIntegrationTest.INDENT, marker.toString());
			Debug.println(MiddlewareIntegrationTest.INDENT, markerAlias.toString());
			Debug.println(MiddlewareIntegrationTest.INDENT, markerDetails.toString());
			Debug.println(MiddlewareIntegrationTest.INDENT, markerUserInfo.toString());
		}
	}

	private Marker createMarker() {
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

		Marker marker =
				new Marker(markerId, markerType, markerName, species, dbAccessionId, reference, genotype, ploidy, primerId, remarks,
						assayType, motif, forwardPrimer, reversePrimer, productSize, annealingTemp, amplification);

		return marker;

	}

	private List<Object> createMarkerRecords() {

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

		Marker marker = this.createMarker();
		Integer markerId = marker.getMarkerId();

		MarkerAlias markerAlias = new MarkerAlias(null, markerId, alias);
		MarkerDetails markerDetails =
				new MarkerDetails(markerId, noOfRepeats, motifType, sequence, sequenceLength, minAllele, maxAllele, ssrNr,
						forwardPrimerTemp, reversePrimerTemp, elongationTemp, fragmentSizeExpected, fragmentSizeObserved,
						expectedProductSize, positionOnReferenceSequence, restrictionEnzymeForAssay);
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
		List<Object> markerRecords = this.createMarkerRecords();
		Marker marker = (Marker) markerRecords.get(0);
		MarkerAlias markerAlias = (MarkerAlias) markerRecords.get(1);
		MarkerDetails markerDetails = (MarkerDetails) markerRecords.get(2);
		MarkerUserInfo markerUserInfo = (MarkerUserInfo) markerRecords.get(3);

		Boolean addStatus = GenotypicDataManagerImplTest.manager.setSNPMarkers(marker, markerAlias, markerDetails, markerUserInfo);
		if (addStatus) {
			Debug.println("testSetSNPMarkers() Added: ");
			Debug.println(MiddlewareIntegrationTest.INDENT, marker.toString());
			Debug.println(MiddlewareIntegrationTest.INDENT, markerAlias.toString());
			Debug.println(MiddlewareIntegrationTest.INDENT, markerDetails.toString());
			Debug.println(MiddlewareIntegrationTest.INDENT, markerUserInfo.toString());
		}
	}

	@Test
	public void testSetSNPMarkersGCP8066() throws Exception {

		// Marker [markerId=null, markerType=SNP, markerName=GKAM0001, species=Groundnut, dbAccessionId=,
		// reference=, genotype=, ploidy=, primerId=null, remarks=null, assayType=KASPar,
		// motif=A/T, forwardPrimer=AGCTTAACAATGAAGGAAATGGTGAGGAGAGGAGGAGGTTTGGTGAGAGACGAGGACCTG,
		// reversePrimer=TCGTTCTTTCAGGCCACCTTACAATGGTAATGTTAATGAGAACTTTCACCTTAATGCT,
		// productSize=, annealingTemp=null, amplification=null]
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

		// MarkerAlias [markerId=null, alias=]
		String alias = "";

		// MarkerDetails [markerId=null, noOfRepeats=null, motifType=null, sequence=, sequenceLength=null,
		// minAllele=null, maxAllele=null, ssrNr=null, forwardPrimerTemp=null, reversePrimerTemp=null,
		// elongationTemp=null, fragmentSizeExpected=null, fragmentSizeObserved=null, expectedProductSize=null,
		// positionOnReferenceSequence=null, restrictionEnzymeForAssay=null]
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

		String principalInvestigator = "Rajeev K Varshney";
		String contact = "";
		String institute = "ICRISAT";

		Marker marker =
				new Marker(markerId, markerType, markerName, species, dbAccessionId, reference, genotype, ploidy, primerId, remarks,
						assayType, motif, forwardPrimer, reversePrimer, productSize, annealingTemp, amplification);
		MarkerAlias markerAlias = new MarkerAlias(null, markerId, alias);
		MarkerDetails markerDetails =
				new MarkerDetails(markerId, noOfRepeats, motifType, sequence, sequenceLength, minAllele, maxAllele, ssrNr,
						forwardPrimerTemp, reversePrimerTemp, elongationTemp, fragmentSizeExpected, fragmentSizeObserved,
						expectedProductSize, positionOnReferenceSequence, restrictionEnzymeForAssay);
		MarkerUserInfo markerUserInfo = new MarkerUserInfo(markerId, principalInvestigator, contact, institute);

		try {
			GenotypicDataManagerImplTest.manager.setSNPMarkers(marker, markerAlias, markerDetails, markerUserInfo);
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage().contains("Marker already exists in Central or Local and cannot be added."));
		}
	}

	@Test
	public void testSetCAPMarkers() throws Exception {
		List<Object> markerRecords = this.createMarkerRecords();
		Marker marker = (Marker) markerRecords.get(0);
		MarkerAlias markerAlias = (MarkerAlias) markerRecords.get(1);
		MarkerDetails markerDetails = (MarkerDetails) markerRecords.get(2);
		MarkerUserInfo markerUserInfo = (MarkerUserInfo) markerRecords.get(3);

		Boolean addStatus = GenotypicDataManagerImplTest.manager.setCAPMarkers(marker, markerAlias, markerDetails, markerUserInfo);
		if (addStatus) {
			Debug.println("testSetCAPMarkers() Added: ");
			Debug.println(MiddlewareIntegrationTest.INDENT, marker.toString());
			Debug.println(MiddlewareIntegrationTest.INDENT, markerAlias.toString());
			Debug.println(MiddlewareIntegrationTest.INDENT, markerDetails.toString());
			Debug.println(MiddlewareIntegrationTest.INDENT, markerUserInfo.toString());
		}
	}

	@Test
	public void testSetCISRMarkers() throws Exception {
		List<Object> markerRecords = this.createMarkerRecords();
		Marker marker = (Marker) markerRecords.get(0);
		MarkerAlias markerAlias = (MarkerAlias) markerRecords.get(1);
		MarkerDetails markerDetails = (MarkerDetails) markerRecords.get(2);
		MarkerUserInfo markerUserInfo = (MarkerUserInfo) markerRecords.get(3);

		try {
			Boolean addStatus = GenotypicDataManagerImplTest.manager.setCISRMarkers(marker, markerAlias, markerDetails, markerUserInfo);
			if (addStatus) {
				Debug.println("testSetCISRMarkers() Added: ");
				Debug.println(MiddlewareIntegrationTest.INDENT, marker.toString());
				Debug.println(MiddlewareIntegrationTest.INDENT, markerAlias.toString());
				Debug.println(MiddlewareIntegrationTest.INDENT, markerDetails.toString());
				Debug.println(MiddlewareIntegrationTest.INDENT, markerUserInfo.toString());
			}
		} catch (MiddlewareQueryException e) {
			// Marker already exists. Try again
			this.testSetCISRMarkers();
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
		Float effect = 0f;
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

		Dataset dataset = this.createDataset();
		dataset.setDatasetName(dataset.getDatasetName() + (int) (Math.random() * 100)); // Used to insert a new dataset

		QtlDetails qtlDetails =
				new QtlDetails(qtlId, mapId, minPosition, maxPosition, traitId, experiment, effect, scoreValue, rSquare, linkageGroup,
						interactions, leftFlankingMarker, rightFlankingMarker, position, clen, seAdditive, hvParent, hvAllele, lvParent,
						lvAllele);

		Qtl qtl = new Qtl(qtlId, qtlName, datasetId);

		List<QtlDataRow> dataRows = new ArrayList<QtlDataRow>();
		dataRows.add(new QtlDataRow(qtl, qtlDetails));

		Boolean addStatus = GenotypicDataManagerImplTest.manager.setQTL(dataset, datasetUser, dataRows);

		Assert.assertTrue(addStatus);

		Debug.println("testSetQTL() Added: ");
		Debug.println(MiddlewareIntegrationTest.INDENT, datasetUser.toString());
		Debug.println(MiddlewareIntegrationTest.INDENT, dataset.toString());
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, dataRows);
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
		Integer markerOnMapId = null;
		Integer mapId = null; // Value will be set/overriden by the function
		String mapName = "GCP-833TestMap";
		String mapType = "genetic";
		Integer mpId = 0;
		String mapDesc = null;
		String genus = "Genus";
		String institute = "ICRISAT";

		// MarkerOnMap Fields
		Float startPosition = new Float(0);
		Float endPosition = new Float(0);
		String mapUnit = "CM";
		String linkageGroup = "LG23";

		Map map = new Map(mapId, mapName, mapType, mpId, mapDesc, mapUnit, genus, species, institute);

		MarkerOnMap markerOnMap = new MarkerOnMap(markerOnMapId, mapId, markerId, startPosition, endPosition, linkageGroup);

		Boolean addStatus = null;
		try {
			addStatus = GenotypicDataManagerImplTest.manager.setMaps(marker, markerOnMap, map);
		} catch (MiddlewareQueryException e) {
			if (e.getMessage().contains("The marker on map combination already exists")) {
				Debug.println("The marker on map combination already exists");
			} else {
				Debug.println("testSetMaps() Added: " + (addStatus != null ? marker : null) + " | "
						+ (addStatus != null ? markerOnMap : null) + " | " + (addStatus != null ? map : null));
			}
		}

		Debug.println(MiddlewareIntegrationTest.INDENT, "Map: " + map);
		Debug.println(MiddlewareIntegrationTest.INDENT, "MarkerOnMap: " + markerOnMap);
	}

	@Test
	public void testGetMapIDsByQTLName() throws Exception {
		String qtlName = "HI Control 08_AhI";
		List<Integer> mapIDs = GenotypicDataManagerImplTest.manager.getMapIDsByQTLName(qtlName, 0, 2);
		Debug.println("TestGetMapIDsByQTLName(" + qtlName + ")");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, mapIDs);
	}

	@Test
	public void testCountMapIDsByQTLName() throws Exception {
		String qtlName = "HI Control 08_AhI";
		long count = GenotypicDataManagerImplTest.manager.countMapIDsByQTLName(qtlName);
		Debug.println("TestCountMapIDsByQTLName(" + qtlName + ")");
		Debug.println(MiddlewareIntegrationTest.INDENT, "Count of Map IDs: " + count);
	}

	@Test
	public void testGetMarkerIDsByMapIDAndLinkageBetweenStartPosition() throws Exception {
		int mapID = 1; // Groundnut central
		String linkage = "BC-1_b11";
		double startPos = 0;
		double endPos = 100;
		Set<Integer> markerIDs =
				GenotypicDataManagerImplTest.manager.getMarkerIDsByMapIDAndLinkageBetweenStartPosition(mapID, linkage, startPos, endPos, 0,
						Integer.MAX_VALUE);
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, new ArrayList<Integer>(markerIDs));
	}

	@Test
	public void testGetMarkersByPositionAndLinkageGroup() throws Exception {
		String linkage = "LG01";
		double startPos = 0;
		double endPos = 2.2;
		List<Marker> markers = GenotypicDataManagerImplTest.manager.getMarkersByPositionAndLinkageGroup(startPos, endPos, linkage);
		Assert.assertTrue(markers.size() > 0);
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, markers);
	}

	@Test
	public void testCountMarkerIDsByMapIDAndLinkageBetweenStartPosition() throws Exception {
		int mapID = 1;
		String linkage = "LG01";
		double startPos = 0;
		double endPos = 2.2;
		long count =
				GenotypicDataManagerImplTest.manager.countMarkerIDsByMapIDAndLinkageBetweenStartPosition(mapID, linkage, startPos, endPos);
		Debug.println("Count of Marker IDs: " + count);
	}

	@Test
	public void testGetMarkersByMarkerIDs() throws Exception {
		List<Integer> markerIDs = Arrays.asList(1317);
		List<Marker> markerList = GenotypicDataManagerImplTest.manager.getMarkersByMarkerIds(markerIDs, 0, 5);
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, markerList);
	}

	@Test
	public void testCountMarkersByMarkerIDs() throws Exception {
		List<Integer> markerIDs = Arrays.asList(1317);
		long count = GenotypicDataManagerImplTest.manager.countMarkersByMarkerIds(markerIDs);
		Debug.println("Count of Markers: " + count);
	}

	@Test
	public void testGetQtlByQtlIdsFromCentral() throws Exception {
		List<Integer> qtlIds = Arrays.asList(1, 2, 3);
		List<QtlDetailElement> results =
				GenotypicDataManagerImplTest.manager.getQtlByQtlIds(qtlIds, 0,
						(int) GenotypicDataManagerImplTest.manager.countQtlByQtlIds(qtlIds));
		Assert.assertTrue(results.size() > 0);
		Debug.printFormattedObjects(MiddlewareIntegrationTest.INDENT, results);
	}

	@Test
	public void testGetQtlByQtlIdsFromLocal() throws Exception {
		try {
			this.testSetQTL(); // to add a qtl_details entry
		} catch (MiddlewareQueryException e) {
			// Ignore. There is already a record for testing.
		}
		List<Integer> qtlIds = Arrays.asList(-1, -2, -3);
		List<QtlDetailElement> results =
				GenotypicDataManagerImplTest.manager.getQtlByQtlIds(qtlIds, 0,
						(int) GenotypicDataManagerImplTest.manager.countQtlByQtlIds(qtlIds));
		Debug.printFormattedObjects(MiddlewareIntegrationTest.INDENT, results);
	}

	@Test
	public void testGetQtlByQtlIdsBothDB() throws Exception {
		List<Integer> qtlIds = Arrays.asList(1, 2, 3, -1, -2, -3);
		List<QtlDetailElement> results =
				GenotypicDataManagerImplTest.manager.getQtlByQtlIds(qtlIds, 0,
						(int) GenotypicDataManagerImplTest.manager.countQtlByQtlIds(qtlIds));
		Assert.assertTrue(results.size() > 0);
		Debug.printFormattedObjects(MiddlewareIntegrationTest.INDENT, results);
	}

	@Test
	public void testCountQtlByQtlIdsFromCentral() throws Exception {
		List<Integer> qtlIds = Arrays.asList(1, 2, 3);
		long count = GenotypicDataManagerImplTest.manager.countQtlByQtlIds(qtlIds);
		Assert.assertTrue(count > 0);
		Debug.println("testCountQtlByQtlIdsFromCentral() RESULTS: " + count);
	}

	@Test
	public void testCountQtlByQtlIds() throws Exception {
		List<Integer> qtlIds = Arrays.asList(1, 2, 3, -1, -2, -3);
		long count = GenotypicDataManagerImplTest.manager.countQtlByQtlIds(qtlIds);
		Assert.assertTrue(count > 0);
		Debug.println("testCountQtlByQtlIds() RESULTS: " + count);
	}

	@Test
	public void testGetQtlDataByQtlTraits() throws Exception {
		List<Integer> qtlTraits = new ArrayList<Integer>();
		qtlTraits.add(1001); // "DE"
		List<QtlDataElement> results =
				GenotypicDataManagerImplTest.manager.getQtlDataByQtlTraits(qtlTraits, 0,
						(int) GenotypicDataManagerImplTest.manager.countQtlDataByQtlTraits(qtlTraits));
		Debug.println("testGetQtlDataByQtlTraits() RESULTS: " + results.size());
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, results);
	}

	@Test
	public void testCountQtlDataByQtlTraits() throws Exception {
		List<Integer> qtlTraits = new ArrayList<Integer>();
		qtlTraits.add(1001); // "DE"
		long count = GenotypicDataManagerImplTest.manager.countQtlDataByQtlTraits(qtlTraits);
		Debug.println("testCountQtlDataByQtlTraits() RESULTS: " + count);
	}

	@Test
	public void testGetQtlDetailsByQtlTraits() throws Exception {
		List<Integer> qtlTraits = Arrays.asList(22396);
		List<QtlDetailElement> results = GenotypicDataManagerImplTest.manager.getQtlDetailsByQtlTraits(qtlTraits, 0, Integer.MAX_VALUE);
		Assert.assertTrue(results.size() >= 3);
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, results);
	}

	@Test
	public void testCountQtlDetailsByQtlTraits() throws Exception {
		List<Integer> qtlTraits = Arrays.asList(22396);
		long count = GenotypicDataManagerImplTest.manager.countQtlDetailsByQtlTraits(qtlTraits);
		Assert.assertTrue(count >= 3);
		Debug.println("testCountQtlDataByQtlTraits() RESULTS: " + count);
	}

	@Test
	public void testCountAllelicValuesFromAlleleValuesByDatasetId() throws Exception {
		Integer datasetId = -1; // change value of the datasetid
		long count = GenotypicDataManagerImplTest.manager.countAllelicValuesFromAlleleValuesByDatasetId(datasetId);
		Assert.assertNotNull(count);
		Debug.println("testCountAllelicValuesFromAlleleValuesByDatasetId(" + datasetId + ") Results: " + count);
	}

	@Test
	public void testCountAllelicValuesFromCharValuesByDatasetId() throws Exception {
		Integer datasetId = -1; // change value of the datasetid
		long count = GenotypicDataManagerImplTest.manager.countAllelicValuesFromCharValuesByDatasetId(datasetId);
		Assert.assertNotNull(count);
		Debug.println("testCountAllelicValuesFromCharValuesByDatasetId(" + datasetId + ") Results: " + count);
	}

	@Test
	public void testCountAllelicValuesFromMappingPopValuesByDatasetId() throws Exception {
		Integer datasetId = 3; // change value of the datasetid
		long count = GenotypicDataManagerImplTest.manager.countAllelicValuesFromMappingPopValuesByDatasetId(datasetId);
		Assert.assertNotNull(count);
		Debug.println("testCountAllelicValuesFromMappingPopValuesByDatasetId(" + datasetId + ") Results: " + count);
	}

	@Test
	public void testCountAllParentsFromMappingPopulation() throws Exception {
		long count = GenotypicDataManagerImplTest.manager.countAllParentsFromMappingPopulation();
		Assert.assertNotNull(count);
		Debug.println("testCountAllParentsFromMappingPopulation() Results: " + count);
	}

	@Test
	public void testCountMapDetailsByName() throws Exception {
		String nameLike = "GCP-833TestMap"; // change map_name
		long count = GenotypicDataManagerImplTest.manager.countMapDetailsByName(nameLike);
		Assert.assertNotNull(count);
		Debug.println("testCountMapDetailsByName(" + nameLike + ") Results: " + count);
	}

	@Test
	public void testGetMapNamesByMarkerIds() throws Exception {
		List<Integer> markerIds = Arrays.asList(-6, 1317, 621, 825, 211);
		java.util.Map<Integer, List<String>> markerMaps = GenotypicDataManagerImplTest.manager.getMapNamesByMarkerIds(markerIds);
		Assert.assertTrue(markerMaps.size() > 0);
		for (int i = 0; i < markerIds.size(); i++) {
			Debug.println(MiddlewareIntegrationTest.INDENT,
					"Marker ID = " + markerIds.get(i) + " : Map Name/s = " + markerMaps.get(markerIds.get(i)));
		}
	}

	@Test
	public void testCountMarkerInfoByDbAccessionId() throws Exception {
		String dbAccessionId = ""; // change dbAccessionId
		long count = GenotypicDataManagerImplTest.manager.countMarkerInfoByDbAccessionId(dbAccessionId);
		Assert.assertNotNull(count);
		Debug.println("testCountMarkerInfoByDbAccessionId(" + dbAccessionId + ") Results: " + count);
	}

	@Test
	public void testCountMarkerInfoByGenotype() throws Exception {
		String genotype = "cv. Tatu"; // change genotype
		long count = GenotypicDataManagerImplTest.manager.countMarkerInfoByGenotype(genotype);
		Assert.assertNotNull(count);
		Debug.println("testCountMarkerInfoByGenotype(" + genotype + ") Results: " + count);
	}

	@Test
	public void testCountMarkerInfoByMarkerName() throws Exception {
		String markerName = "SeqTEST"; // change markerName
		long count = GenotypicDataManagerImplTest.manager.countMarkerInfoByMarkerName(markerName);
		Assert.assertNotNull(count);
		Debug.println("testCountMarkerInfoByMarkerName(" + markerName + ") Results: " + count);
	}

	@Test
	public void testGetAllParentsFromMappingPopulation() throws Exception {
		long count = GenotypicDataManagerImplTest.manager.countAllParentsFromMappingPopulation();
		List<ParentElement> results = GenotypicDataManagerImplTest.manager.getAllParentsFromMappingPopulation(0, (int) count);
		Assert.assertNotNull(results);
		Assert.assertFalse(results.isEmpty());
		Debug.println("testGetAllParentsFromMappingPopulation() Results: ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, results);
	}

	@Test
	public void testGetMapDetailsByName() throws Exception {
		String nameLike = "GCP-833TestMap"; // change map_name
		long count = GenotypicDataManagerImplTest.manager.countMapDetailsByName(nameLike);
		List<MapDetailElement> results = GenotypicDataManagerImplTest.manager.getMapDetailsByName(nameLike, 0, (int) count);
		Assert.assertNotNull(results);
		Assert.assertFalse(results.isEmpty());
		Debug.println("testGetMapDetailsByName(" + nameLike + ")");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, results);
	}

	@Test
	public void testGetMarkersByIds() throws Exception {
		List<Integer> markerIds = Arrays.asList(1, 3);
		List<Marker> results = GenotypicDataManagerImplTest.manager.getMarkersByIds(markerIds, 0, 100);
		Assert.assertNotNull(results);
		Assert.assertFalse(results.isEmpty());
		Debug.println("testGetMarkersByIds(" + markerIds + ") Results: ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, results);
	}

	@Test
	public void testGetMarkersByType() throws Exception {

		Marker marker = this.createMarker();
		marker.setMarkerType(GdmsType.TYPE_SNP.getValue());
		GenotypicDataManagerImplTest.manager.addMarker(marker);
		List<Marker> results = GenotypicDataManagerImplTest.manager.getMarkersByType(GdmsType.TYPE_SNP.getValue());
		Assert.assertFalse(results.isEmpty());

		marker = this.createMarker();
		marker.setMarkerType(GdmsType.TYPE_SSR.getValue());
		GenotypicDataManagerImplTest.manager.addMarker(marker);
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, results);
		results = GenotypicDataManagerImplTest.manager.getMarkersByType(GdmsType.TYPE_SSR.getValue());
		Assert.assertFalse(results.isEmpty());
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, results);

		marker = this.createMarker();
		marker.setMarkerType(GdmsType.TYPE_CISR.getValue());
		GenotypicDataManagerImplTest.manager.addMarker(marker);
		results = GenotypicDataManagerImplTest.manager.getMarkersByType(GdmsType.TYPE_CISR.getValue());
		Assert.assertFalse(results.isEmpty());
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, results);

		marker = this.createMarker();
		marker.setMarkerType(GdmsType.TYPE_DART.getValue());
		GenotypicDataManagerImplTest.manager.addMarker(marker);
		results = GenotypicDataManagerImplTest.manager.getMarkersByType(GdmsType.TYPE_DART.getValue());
		Assert.assertFalse(results.isEmpty());
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, results);

		marker = this.createMarker();
		marker.setMarkerType(GdmsType.TYPE_CAP.getValue());
		GenotypicDataManagerImplTest.manager.addMarker(marker);
		results = GenotypicDataManagerImplTest.manager.getMarkersByType(GdmsType.TYPE_CAP.getValue());
		Assert.assertFalse(results.isEmpty());
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, results);
	}

	@Test
	public void testGetSNPsByHaplotype() throws Exception {
		String haplotype = "TRIAL";
		List<Marker> results = GenotypicDataManagerImplTest.manager.getSNPsByHaplotype(haplotype);
		Debug.println("testGetSNPsByHaplotype(" + haplotype + ") Results: ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, results);
	}

	@Test
	public void testAddHaplotype() throws Exception {

		TrackData trackData = new TrackData(null, "TEST Track Data", 1);

		List<TrackMarker> trackMarkers = new ArrayList<TrackMarker>();
		trackMarkers.add(new TrackMarker(null, null, 1, 1));
		trackMarkers.add(new TrackMarker(null, null, 2, 2));
		trackMarkers.add(new TrackMarker(null, null, 3, 3));

		GenotypicDataManagerImplTest.manager.addHaplotype(trackData, trackMarkers);

		// INSERT INTO gdms_track_data(track_id, track_name, user_id) VALUES (generatedTrackId, haplotype, userId);
		// INSERT INTO gdms_track_markers(tmarker_id, track_id, marker_id, marker_sample_id)
		// VALUES (generatedTrackId, generatedTrackId, markerId, markerSampleId);

		Debug.println(MiddlewareIntegrationTest.INDENT, "Added:");
		Debug.printObject(MiddlewareIntegrationTest.INDENT * 2, trackData);
		Debug.printObjects(MiddlewareIntegrationTest.INDENT * 2, trackMarkers);
	}

	@Test
	public void testCountAllMaps() throws Exception {
		long count = GenotypicDataManagerImplTest.manager.countAllMaps(Database.LOCAL);
		Assert.assertNotNull(count);
		Debug.println("testCountAllMaps(" + Database.LOCAL + ") Results: " + count);
	}

	@Test
	public void testGetAllMaps() throws Exception {
		List<Map> results = GenotypicDataManagerImplTest.manager.getAllMaps(0, 100, Database.LOCAL);
		Assert.assertNotNull(results);
		Assert.assertFalse(results.isEmpty());
		Debug.println("testGetAllMaps(" + Database.LOCAL + ") Results:");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, results);
	}

	@Test
	public void testCountNidsFromAccMetadatasetByDatasetIds() throws Exception {
		List<Integer> datasetIds = Arrays.asList(2, 3, 4);
		long count = GenotypicDataManagerImplTest.manager.countAccMetadatasetByDatasetIds(datasetIds);
		Debug.println("testCountNidsFromAccMetadatasetByDatasetIds(" + datasetIds + ") = " + count);
	}

	@Test
	public void testCountMarkersFromMarkerMetadatasetByDatasetIds() throws Exception {
		List<Integer> datasetIds = Arrays.asList(2, 3, 4);
		long count = GenotypicDataManagerImplTest.manager.countMarkersFromMarkerMetadatasetByDatasetIds(datasetIds);
		Debug.println("testCountMarkersFromAccMetadatasetByDatasetIds(" + datasetIds + ") = " + count);
	}

	@Test
	public void testGetMapIdByName() throws Exception {
		String mapName = "Consensus_Biotic"; // CENTRAL test data
		Integer mapId = GenotypicDataManagerImplTest.manager.getMapIdByName(mapName);
		Debug.println("Map ID for " + mapName + " = " + mapId);
		mapName = "name that does not exist";
		mapId = GenotypicDataManagerImplTest.manager.getMapIdByName(mapName);
		Debug.println("Map ID for " + mapName + " = " + mapId);
		mapName = "TEST"; // local test data INSERT INTO gdms_map (map_id,
							// map_name, map_type, mp_id, map_desc, map_unit)
							// values(-1, 'TEST', 'genetic', 0, 'TEST', 'cM');
		mapId = GenotypicDataManagerImplTest.manager.getMapIdByName(mapName);
		Debug.println("Map ID for " + mapName + " = " + mapId);
	}

	@Test
	public void testCountMappingPopValuesByGids() throws Exception {
		List<Integer> gIds = Arrays.asList(1434, 1435, 1436);
		long count = GenotypicDataManagerImplTest.manager.countMappingPopValuesByGids(gIds);
		Debug.println("countMappingPopValuesByGids(" + gIds + ") = " + count);
	}

	@Test
	public void testCountMappingAlleleValuesByGids() throws Exception {
		List<Integer> gIds = Arrays.asList(2213, 2214);
		long count = GenotypicDataManagerImplTest.manager.countMappingAlleleValuesByGids(gIds);
		Debug.println("testCountMappingAlleleValuesByGids(" + gIds + ") = " + count);
	}

	@Test
	public void testGetAllFromMarkerMetadatasetByMarkers() throws Exception {
		List<Integer> markerIds = Arrays.asList(3302, -1);
		List<MarkerMetadataSet> result = GenotypicDataManagerImplTest.manager.getAllFromMarkerMetadatasetByMarkers(markerIds);
		Debug.println("testGetAllFromMarkerMetadatasetByMarker(" + markerIds + "): ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, result);
	}

	@Test
	public void testGetDatasetDetailsByDatasetIds() throws Exception {
		List<Integer> datasetIds = Arrays.asList(-1, -2, -3, 3, 4, 5);
		List<Dataset> result = GenotypicDataManagerImplTest.manager.getDatasetDetailsByDatasetIds(datasetIds);
		Debug.println("testGetDatasetDetailsByDatasetId(" + datasetIds + "): ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, result);
	}

	@Test
	public void testGetQTLIdsByDatasetIds() throws Exception {
		List<Integer> datasetIds = Arrays.asList(1, -6);
		// To get test dataset_ids, run in Local and Central: select qtl_id, dataset_id from gdms_qtl;
		List<Integer> qtlIds = GenotypicDataManagerImplTest.manager.getQTLIdsByDatasetIds(datasetIds);
		Debug.println("testGetQTLIdsByDatasetIds(" + datasetIds + "): " + qtlIds);
	}

	@Test
	public void testGetAllFromAccMetadataset() throws Exception {
		List<Integer> gids = Arrays.asList(2012, 2014, 2016);
		Integer datasetId = 5;
		List<AccMetadataSet> result = GenotypicDataManagerImplTest.manager.getAllFromAccMetadataset(gids, datasetId, SetOperation.NOT_IN);
		Debug.println("testGetAllFromAccMetadataset(gid=" + gids + ", datasetId=" + datasetId + "): ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, result);
	}

	@Test
	public void testGetMapAndMarkerCountByMarkers() throws Exception {
		List<Integer> markerIds = Arrays.asList(1317, 1318, 1788, 1053, 2279, 50);
		List<MapDetailElement> details = GenotypicDataManagerImplTest.manager.getMapAndMarkerCountByMarkers(markerIds);
		Debug.println("testGetMapAndMarkerCountByMarkers(" + markerIds + ")");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, details);
	}

	@Test
	public void testGetAllMTAs() throws Exception {
		List<Mta> result = GenotypicDataManagerImplTest.manager.getAllMTAs();
		Debug.println("testGetAllMTAs(): ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, result);
	}

	@Test
	public void testCountAllMTAs() throws Exception {
		long count = GenotypicDataManagerImplTest.manager.countAllMTAs();
		Debug.println("testCountAllMTAs(): " + count);
	}

	@Test
	public void testGetMTAsByTrait() throws Exception {
		Integer traitId = 1;
		List<Mta> result = GenotypicDataManagerImplTest.manager.getMTAsByTrait(traitId);
		Debug.println("testGetMTAsByTrait(): ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, result);
	}

	@Test
	public void testGetAllSNPs() throws Exception {
		List<Marker> result = GenotypicDataManagerImplTest.manager.getAllSNPMarkers();
		Debug.println("testGetAllSNPs(): ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, result);
	}

	@Test
	public void testGetAlleleValuesByMarkers() throws Exception {
		List<Integer> markerIds = Arrays.asList(-1, -2, 956, 1037);
		List<AllelicValueElement> result = GenotypicDataManagerImplTest.manager.getAlleleValuesByMarkers(markerIds);
		Debug.println("testGetAlleleValuesByMarkers(): ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, result);
	}

	@Test
	public void testDeleteQTLs() throws Exception {
		List<Qtl> qtls = GenotypicDataManagerImplTest.manager.getAllQtl(0, 1);
		List<Integer> qtlIds = new ArrayList<Integer>();
		if (qtls != null && qtls.size() > 0) {
			qtlIds.add(qtls.get(0).getQtlId());
			int datasetId = qtls.get(0).getDatasetId();
			Debug.println("testDeleteQTLs(qtlIds=" + qtlIds + ", datasetId=" + datasetId);
			GenotypicDataManagerImplTest.manager.deleteQTLs(qtlIds, datasetId);
			Debug.println("done with testDeleteQTLs");
		}
	}

	@Test
	public void testDeleteSSRGenotypingDatasets() throws Exception {

		// Insert test data to delete
		GenotypicDataManagerImplUploadFunctionsTest.setUp();
		GenotypicDataManagerImplUploadFunctionsTest uploadTest = new GenotypicDataManagerImplUploadFunctionsTest();
		uploadTest.testSetSSR();

		List<Dataset> dataset = GenotypicDataManagerImplTest.manager.getDatasetsByType(GdmsType.TYPE_SSR);

		Integer datasetId = dataset.get(0).getDatasetId();

		Debug.println("testDeleteSSRGenotypingDatasets(" + datasetId + ")");
		GenotypicDataManagerImplTest.manager.deleteSSRGenotypingDatasets(datasetId);
		Debug.println("done with testDeleteSSRGenotypingDatasets");
	}

	@Test
	public void testDeleteSNPGenotypingDatasets() throws Exception {

		// Insert test data to delete
		GenotypicDataManagerImplUploadFunctionsTest.setUp();
		GenotypicDataManagerImplUploadFunctionsTest uploadTest = new GenotypicDataManagerImplUploadFunctionsTest();
		uploadTest.testSetSNP();

		List<Dataset> dataset = GenotypicDataManagerImplTest.manager.getDatasetsByType(GdmsType.TYPE_SNP);

		Integer datasetId = dataset.get(0).getDatasetId();

		Debug.println("testDeleteSNPGenotypingDatasets(" + datasetId + ")");
		GenotypicDataManagerImplTest.manager.deleteSNPGenotypingDatasets(datasetId);
		Debug.println("done with testDeleteSNPGenotypingDatasets");
	}

	@Test
	public void testDeleteDArTGenotypingDatasets() throws Exception {

		// Insert test data to delete
		GenotypicDataManagerImplUploadFunctionsTest.setUp();
		GenotypicDataManagerImplUploadFunctionsTest uploadTest = new GenotypicDataManagerImplUploadFunctionsTest();
		uploadTest.testSetDart();

		List<Dataset> dataset = GenotypicDataManagerImplTest.manager.getDatasetsByType(GdmsType.TYPE_DART);
		Integer datasetId = dataset.get(0).getDatasetId();

		Debug.println("testDeleteDArTGenotypingDatasets(" + datasetId + ")");
		GenotypicDataManagerImplTest.manager.deleteDArTGenotypingDatasets(datasetId);
		Debug.println("done with testDeleteDArTGenotypingDatasets");
	}

	@Test
	public void testDeleteMappingPopulationDatasets() throws Exception {

		// Insert test data to delete
		GenotypicDataManagerImplUploadFunctionsTest.setUp();
		GenotypicDataManagerImplUploadFunctionsTest uploadTest = new GenotypicDataManagerImplUploadFunctionsTest();

		// Test deleting Mapping ABH Data
		uploadTest.testSetMappingABH();
		List<Dataset> dataset = GenotypicDataManagerImplTest.manager.getDatasetsByType(GdmsType.TYPE_MAPPING);
		Integer datasetId = dataset.get(0).getDatasetId();
		GenotypicDataManagerImplTest.manager.deleteMappingPopulationDatasets(datasetId);
		Debug.println("Deleted MappingPopulationDataset:MappingABH(datasetId=" + datasetId + ")");

	}

	@Test
	public void testDeleteMappingPopulationDatasetsSNP() throws Exception {

		// Insert test data to delete
		GenotypicDataManagerImplUploadFunctionsTest.setUp();
		GenotypicDataManagerImplUploadFunctionsTest uploadTest = new GenotypicDataManagerImplUploadFunctionsTest();

		// Test deleting Mapping Allelic SNP Data
		uploadTest.testSetMappingAllelicSNP(); // should also delete from gdms_char_values
		List<Dataset> dataset = GenotypicDataManagerImplTest.manager.getDatasetsByType(GdmsType.TYPE_MAPPING);
		Integer datasetId = dataset.get(0).getDatasetId();
		GenotypicDataManagerImplTest.manager.deleteMappingPopulationDatasets(datasetId);
		Debug.println("Deleted MappingPopulationDataset:MappingAllelicSNP(datasetId=" + datasetId + ")");
	}

	@Test
	public void testDeleteMappingPopulationDatasetsSSRDArT() throws Exception {

		// Insert test data to delete
		GenotypicDataManagerImplUploadFunctionsTest.setUp();
		GenotypicDataManagerImplUploadFunctionsTest uploadTest = new GenotypicDataManagerImplUploadFunctionsTest();

		// Test deleting Mapping Allelic SNP Data
		uploadTest.testSetMappingAllelicSSRDArT(); // should also delete from gdms_allele_values / gdms_dart_values
		List<Dataset> dataset = GenotypicDataManagerImplTest.manager.getDatasetsByType(GdmsType.TYPE_MAPPING);
		Integer datasetId = dataset.get(0).getDatasetId();
		GenotypicDataManagerImplTest.manager.deleteMappingPopulationDatasets(datasetId);
		Debug.println("Deleted MappingPopulationDataset:MappingAllelicSSRDArT(datasetId=" + datasetId + ")");
	}

	@Test
	public void testGetDatasetsByType() throws Exception {
		List<Dataset> datasets = GenotypicDataManagerImplTest.manager.getDatasetsByType(GdmsType.TYPE_MAPPING);
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, datasets);
	}

	@Test
	public void testGetQtlDetailsByMapId() throws Exception {
		try {
			this.testSetQTL(); // to add a qtl_details entry with map_id = 1
		} catch (MiddlewareQueryException e) {
			// Ignore. There is already a record for testing.
		}
		int mapId = 1;
		List<QtlDetails> qtls = GenotypicDataManagerImplTest.manager.getQtlDetailsByMapId(mapId);
		Debug.println("testGetQtlDetailsByMapId(" + mapId + ")");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, qtls);
	}

	@Test
	public void testCountQtlDetailsByMapId() throws Exception {
		int mapId = 7;
		Debug.println("testCountQTLsByMapId(" + mapId + ")");
		long count = GenotypicDataManagerImplTest.manager.countQtlDetailsByMapId(mapId);
		Debug.println("COUNT = " + count);
	}

	@Test
	public void testDeleteMaps() throws Exception {
		Integer mapId = this.addMap();
		Debug.println("testDeleteMaps(" + mapId + ")");
		GenotypicDataManagerImplTest.manager.deleteMaps(mapId);
		Debug.println("done with testDeleteMaps");
	}

	@Test
	public void testGetMarkerFromCharValuesByGids() throws Exception {
		List<Integer> gIds = Arrays.asList(2012, 2014, 2016, 310544);
		List<MarkerSampleId> result = GenotypicDataManagerImplTest.manager.getMarkerFromCharValuesByGids(gIds);
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, result);
	}

	@Test
	public void testGetMarkerFromAlleleValuesByGids() throws Exception {
		List<Integer> gIds = Arrays.asList(2213, 2214);
		List<MarkerSampleId> result = GenotypicDataManagerImplTest.manager.getMarkerFromAlleleValuesByGids(gIds);
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, result);
	}

	@Test
	public void testGetMarkerFromMappingPopValuesByGids() throws Exception {
		List<Integer> gIds = Arrays.asList(1434, 1435);
		List<MarkerSampleId> result = GenotypicDataManagerImplTest.manager.getMarkerFromMappingPopByGids(gIds);
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, result);
	}

	@Test
	public void testGetLastId() throws Exception {
		Database instance = Database.LOCAL;
		for (GdmsTable gdmsTable : GdmsTable.values()) {
			long lastId = GenotypicDataManagerImplTest.manager.getLastId(instance, gdmsTable);
			Debug.println("testGetLastId(" + gdmsTable + ") in " + instance + " = " + lastId);
		}

		instance = Database.CENTRAL;
		for (GdmsTable gdmsTable : GdmsTable.values()) {
			long lastId = GenotypicDataManagerImplTest.manager.getLastId(instance, gdmsTable);
			Debug.println("testGetLastId(" + gdmsTable + ") in " + instance + " = " + lastId);
		}
	}

	@Test
	public void testAddMta() throws Exception {
		Dataset dataset =
				new Dataset(null, "TEST DATASET NAME", "DATASET DESC", "MTA", "GENUS", "SPECIES", null, "REMARKS", "int", null, "METHOD",
						"0.43", "INSTITUTE", "PI", "EMAIL", "OBJECTIVE");
		Mta mta =
				new Mta(null, 1, null, 1, 2.1f, 1, 1.1f, 2.2f, 3.3f, "gene", "chromosome", "alleleA", "alleleB", "alleleAPhenotype",
						"alleleBPhenotype", 4.4f, 5.5f, 6.6f, 7.7f, "correctionMethod", 8.8f, 9.9f, "dominance", "evidence", "reference",
						"notes");
		MtaMetadata mtaMetadata = new MtaMetadata(null, "project1", "population", 100, "Thousand");
		DatasetUsers users = new DatasetUsers(null, 1);
		GenotypicDataManagerImplTest.manager.addMTA(dataset, mta, mtaMetadata, users);

		// non-null id means the records were inserted.
		Assert.assertTrue(mta.getMtaId() != null && mtaMetadata.getDatasetID() != null);

		Debug.println("MTA added: ");
		Debug.printObject(MiddlewareIntegrationTest.INDENT, mta);
		Debug.printObject(MiddlewareIntegrationTest.INDENT, mtaMetadata);
	}

	@Test
	public void testAddMtaGCP9174() throws Exception {
		int nextDatasetId = (int) (GenotypicDataManagerImplTest.manager.getLastId(Database.LOCAL, GdmsTable.GDMS_DATASET) - 1);
		Dataset dataset =
				new Dataset(nextDatasetId, "sample", "testing", "MTA", "Groundnut", "Groundnut", null, "", "int", null, "Tassel", "LOD",
						"ICRISAT", "TrusharShah", null, null);

		int nextMtaId = (int) (GenotypicDataManagerImplTest.manager.getLastId(Database.LOCAL, GdmsTable.GDMS_MTA) - 1);

		Mta mta =
				new Mta(nextMtaId, 964, nextDatasetId, 5, 6.01f, 22395, 0.0f, 0.0f, 0.0f, "1RS:1AL", "RIL-1 _LG12", "C", "T", "absent",
						"present", 0.0f, 0.0f, 0.0f, 0.0f, "Bonferroni", 0.0f, 0.0f, "co-dominant", "association",
						"Ellis et al (2002) TAG 105:1038-1042", "");

		MtaMetadata mtaMetadata = new MtaMetadata(nextMtaId, "project1", "population", 100, "Thousand");

		DatasetUsers users = new DatasetUsers(nextDatasetId, 1);

		GenotypicDataManagerImplTest.manager.addMTA(dataset, mta, mtaMetadata, users);

		// non-null id means the records were inserted.
		Assert.assertTrue(mta.getMtaId() != null && mtaMetadata.getDatasetID() != null);

		Debug.println("MTA added: ");
		Debug.printObject(MiddlewareIntegrationTest.INDENT, dataset);
		Debug.printObject(MiddlewareIntegrationTest.INDENT, mta);
		Debug.printObject(MiddlewareIntegrationTest.INDENT, mtaMetadata);
	}

	@Test
	public void testSetMTA() throws Exception {
		Dataset dataset =
				new Dataset(null, "TEST DATASET NAME", "DATASET DESC", "MTA", "GENUS", "SPECIES", null, "REMARKS", "int", null, "METHOD",
						"0.43", "INSTITUTE", "PI", "EMAIL", "OBJECTIVE");
		List<Mta> mtaList = new ArrayList<Mta>();
		mtaList.add(new Mta(null, 1, null, 1, 2.1f, 1, 1.1f, 2.2f, 3.3f, "gene", "chromosome", "alleleA", "alleleB", "alleleAPhenotype",
				"alleleBPhenotype", 4.4f, 5.5f, 6.6f, 7.7f, "correctionMethod", 8.8f, 9.9f, "dominance", "evidence", "reference", "notes"));
		mtaList.add(new Mta(null, 2, null, 2, 3.1f, 2, 2.1f, 3.2f, 4.3f, "gene", "chromosome", "alleleA", "alleleB", "alleleAPhenotype",
				"alleleBPhenotype", 5.4f, 6.5f, 7.6f, 8.7f, "correctionMethod", 9.8f, 10.9f, "dominance", "evidence", "reference", "notes"));

		MtaMetadata metadata = new MtaMetadata(null, "project1", "population", 100, "Thousand");

		DatasetUsers users = new DatasetUsers(null, 1);
		GenotypicDataManagerImplTest.manager.setMTA(dataset, users, mtaList, metadata);

		// Non-null id means the record was inserted.
		Assert.assertTrue(mtaList.get(0).getMtaId() != null && metadata.getDatasetID() != null);

		Debug.println("MTAs added: ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, mtaList);
		Debug.printObject(MiddlewareIntegrationTest.INDENT, metadata);
	}

	@Test
	public void testDeleteMTA() throws Exception {
		List<Mta> mtas = GenotypicDataManagerImplTest.manager.getAllMTAs();

		if (mtas != null && !mtas.isEmpty()) {
			List<Integer> datasetIds = new ArrayList<Integer>();
			datasetIds.add(mtas.get(0).getDatasetId());
			GenotypicDataManagerImplTest.manager.deleteMTA(datasetIds);
			Debug.println(MiddlewareIntegrationTest.INDENT, "datasetId deleted = " + datasetIds);
		}
	}

	@Test
	public void testAddMtaMetadata() throws Exception {
		List<Mta> mtas = GenotypicDataManagerImplTest.manager.getAllMTAs();

		if (mtas != null && !mtas.isEmpty()) {
			Mta mta = mtas.get(0);
			MtaMetadata mtaMetadata = new MtaMetadata(mta.getMtaId(), "project", "population", 100, "Thousand");
			GenotypicDataManagerImplTest.manager.addMtaMetadata(mtaMetadata);
			Debug.println(MiddlewareIntegrationTest.INDENT, "MtaMetadataset added: " + mtaMetadata);
		}
	}

	@Test
	public void testGetDartMarkerDetails() throws Exception {
		List<Integer> markerIds = Arrays.asList(-1, -2);
		List<DartValues> result = GenotypicDataManagerImplTest.manager.getDartMarkerDetails(markerIds);
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, result);
	}

	@Test
	public void testGetMarkerMetadatasetByDatasetId() throws Exception {
		Integer datasetId = 2;
		List<MarkerMetadataSet> result = GenotypicDataManagerImplTest.manager.getMarkerMetadataSetByDatasetId(datasetId);
		Assert.assertTrue(result != null && !result.isEmpty());
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, result);
	}

	@Test
	public void testGetCharValuesByMarkerIds() throws Exception {
		List<Integer> markerIds = Arrays.asList(-1, -2);
		List<CharValues> result = GenotypicDataManagerImplTest.manager.getCharValuesByMarkerIds(markerIds);
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, result);
	}

	@Test
	public void testUpdateMarkerInfoExisting() throws Exception {
		// Update existing Marker, MarkerAlias, MarkerDetails, MarkerUserInfo
		List<Object> markerRecords = this.createMarkerRecords();
		Marker marker = (Marker) markerRecords.get(0);
		MarkerAlias markerAlias = (MarkerAlias) markerRecords.get(1);
		MarkerDetails markerDetails = (MarkerDetails) markerRecords.get(2);
		MarkerUserInfo markerUserInfo = (MarkerUserInfo) markerRecords.get(3);

		// Insert first
		Boolean addStatus = GenotypicDataManagerImplTest.manager.setSNPMarkers(marker, markerAlias, markerDetails, markerUserInfo);
		if (addStatus) {
			Debug.println("ADDED: ");
			Debug.println(MiddlewareIntegrationTest.INDENT, marker.toString());
			Debug.println(MiddlewareIntegrationTest.INDENT, markerAlias.toString());
			Debug.println(MiddlewareIntegrationTest.INDENT, markerDetails.toString());
			Debug.println(MiddlewareIntegrationTest.INDENT, markerUserInfo.toString());
		}

		// Then update the newly-inserted set of records
		Integer updateId = (int) (Math.random() * 100);
		marker.setRemarks("UPDATE" + updateId);
		markerAlias.setAlias(markerAlias.getAlias() + updateId);
		markerDetails.setSequence(updateId.toString());
		markerUserInfo.setContactValue(updateId.toString());

		addStatus = GenotypicDataManagerImplTest.manager.updateMarkerInfo(marker, markerAlias, markerDetails, markerUserInfo);
		if (addStatus) {
			Debug.println("UPDATED: ");
			Debug.println(MiddlewareIntegrationTest.INDENT, marker.toString());
			Debug.println(MiddlewareIntegrationTest.INDENT, markerAlias.toString());
			Debug.println(MiddlewareIntegrationTest.INDENT, markerDetails.toString());
			Debug.println(MiddlewareIntegrationTest.INDENT, markerUserInfo.toString());
		}
	}

	@Test
	public void testUpdateMarkerInfoNewRecords() throws Exception {

		// Add a new set of MarkerAlias, MarkerDetails, MarkerUserInfo for the given Marker - should insert, but update
		List<Object> markerRecords = this.createMarkerRecords();
		Marker markerUA = (Marker) markerRecords.get(0);
		markerUA.setMarkerType(GdmsType.TYPE_UA.getValue());

		Integer markerId = GenotypicDataManagerImplTest.manager.addMarker(markerUA);

		Debug.println("MARKER ADDED: " + markerUA);

		MarkerAlias markerAlias = (MarkerAlias) markerRecords.get(1);
		markerAlias.setMarkerId(markerId);
		MarkerDetails markerDetails = (MarkerDetails) markerRecords.get(2);
		markerDetails.setMarkerId(markerId);
		MarkerUserInfo markerUserInfo = (MarkerUserInfo) markerRecords.get(3);
		markerUserInfo.setMarkerId(markerId);

		Boolean addStatus = GenotypicDataManagerImplTest.manager.updateMarkerInfo(markerUA, markerAlias, markerDetails, markerUserInfo);
		if (addStatus) {
			Debug.println("MarkerAlias/MarkerDetails/MarkerUserInfo ADDED TO AN EXISTING Marker: ");
			Debug.println(MiddlewareIntegrationTest.INDENT, markerUA.toString());
			Debug.println(MiddlewareIntegrationTest.INDENT, markerAlias.toString());
			Debug.println(MiddlewareIntegrationTest.INDENT, markerDetails.toString());
			Debug.println(MiddlewareIntegrationTest.INDENT, markerUserInfo.toString());
		}
	}

	@Test
	public void testUpdateMarkerInfoModifyMarkerNameSpecies() throws Exception {

		// Get an existing marker from local db
		Marker marker =
				GenotypicDataManagerImplTest.manager.getMarkersByIds(Arrays.asList(-1, -2, -3, -4, -5, -6, -7, -8, -9, -10), 0, 1).get(0);
		Debug.println(MiddlewareIntegrationTest.INDENT, "Existing marker:" + marker.toString());

		List<Object> markerRecords = this.createMarkerRecords();
		MarkerAlias markerAlias = (MarkerAlias) markerRecords.get(1);
		MarkerDetails markerDetails = (MarkerDetails) markerRecords.get(2);
		MarkerUserInfo markerUserInfo = (MarkerUserInfo) markerRecords.get(3);

		Integer updateId = (int) (Math.random() * 100);

		// Try to update marker name - should not be allowed.
		try {
			marker.setMarkerName(marker.getMarkerName() + updateId);
			GenotypicDataManagerImplTest.manager.updateMarkerInfo(marker, markerAlias, markerDetails, markerUserInfo);
		} catch (MiddlewareQueryException e) {
			Debug.println("Caught exception: Marker name and species cannot be updated.");
			Assert.assertTrue(e.getMessage().contains("Marker name and species cannot be updated."));
		}

		// Try to update species - should not be allowed.
		try {
			marker.setSpecies(marker.getSpecies() + updateId);
			GenotypicDataManagerImplTest.manager.updateMarkerInfo(marker, markerAlias, markerDetails, markerUserInfo);
		} catch (MiddlewareQueryException e) {
			Debug.println("Caught exception: Marker name and species cannot be updated.");
			Assert.assertTrue(e.getMessage().contains("Marker name and species cannot be updated."));
		}
	}

	@Test
	public void testUpdateMarkerInfoInCentral() throws Exception {

		// Get an existing marker from local db
		List<Marker> markers = GenotypicDataManagerImplTest.manager.getMarkersByIds(Arrays.asList(3316), 0, 1);
		if (markers != null && markers.size() > 0) {
			Marker marker = markers.get(0);
			Debug.println(MiddlewareIntegrationTest.INDENT, "Existing marker:" + marker.toString());

			List<Object> markerRecords = this.createMarkerRecords();
			MarkerAlias markerAlias = (MarkerAlias) markerRecords.get(1);
			MarkerDetails markerDetails = (MarkerDetails) markerRecords.get(2);
			MarkerUserInfo markerUserInfo = (MarkerUserInfo) markerRecords.get(3);

			try {
				GenotypicDataManagerImplTest.manager.updateMarkerInfo(marker, markerAlias, markerDetails, markerUserInfo);
			} catch (MiddlewareQueryException e) {
				Debug.println(MiddlewareIntegrationTest.INDENT, "Marker is in central database and cannot be updated.");
				Assert.assertTrue(e.getMessage().contains("Marker is in central database and cannot be updated."));
			}
		} else {
			Debug.println(MiddlewareIntegrationTest.INDENT, "No marker data found in the database to update.");
		}
	}

	@Test
	public void testGetMarkerByType() throws Exception {
		List<ExtendedMarkerInfo> markers = GenotypicDataManagerImplTest.manager.getMarkerInfoDataByMarkerType("SSR");
		Assert.assertNotNull(markers);

		Assert.assertTrue(markers.size() != 0);
	}

	@Test
	public void testGetMarkersLikeMarkerName() throws Exception {
		List<ExtendedMarkerInfo> markers = GenotypicDataManagerImplTest.manager.getMarkerInfoDataLikeMarkerName("ga");
		Assert.assertNotNull(markers);

		Assert.assertTrue(markers.size() != 0);
	}

	@Test
	public void testGetMarkerInfosByMarkerNames() throws Exception {
		List<String> paramList = new ArrayList<String>();
		paramList.add("GA1");
		paramList.add("GA2");
		List<ExtendedMarkerInfo> markers = GenotypicDataManagerImplTest.manager.getMarkerInfoByMarkerNames(paramList);
		Assert.assertNotNull(markers);

		Assert.assertTrue(markers.size() == paramList.size());
	}

}
