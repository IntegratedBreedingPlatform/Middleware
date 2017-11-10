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

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GenotypicDataManager;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.Sample;
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
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Ignore("Historic failing test. Disabled temporarily. Developers working in this area please spend some time to fix and remove @Ignore.")
public class GenotypicDataManagerImplTest extends IntegrationTestBase {

	@Autowired
	private GenotypicDataManager genotypicDataManager;

	private Integer markerId;
	private Integer mapId;
	private Integer datasetId;
	private Marker marker;
	private Dataset dataset;
	private MarkerMetadataSet markerMetadataSet;

	@Before
	public void setupBefore() throws Exception {
		this.marker = this.createMarker();
		this.marker.setMarkerName("TestMarker" + System.currentTimeMillis());
		this.marker.setMarkerType(GdmsType.TYPE_SNP.getValue());
		this.markerId = this.genotypicDataManager.addMarker(this.marker);
		this.mapId = this.addMap();
		this.dataset = this.createDataset();
		this.dataset.setDatasetName("TestDataset" + System.currentTimeMillis());
		this.datasetId = this.genotypicDataManager.addDataset(this.dataset);
		dataset.setDatasetId(datasetId);
		this.markerMetadataSet = new MarkerMetadataSet(null, dataset, this.markerId, 1);
		this.genotypicDataManager.addMarkerMetadataSet(this.markerMetadataSet);
	}

	@Test
	public void testGetNamesByNameIds() throws Exception {
		List<Integer> nameIds = Arrays.asList(1, 2, 3, -1, -2, -3);
		// For test data, get gids from LOCAL and CENTRAL: SELECT * FROM names where nid in (:nidList)

		List<Name> results = this.genotypicDataManager.getNamesByNameIds(nameIds);
		Debug.println("testGetNamesByNameIds(" + nameIds + ") RESULTS: " + results.size() + " = " + results);
	}

	@Test
	public void testGetMapNameByMapId() throws Exception {
		String expectedName = "RIL-1 (TAG 24 x ICGV 86031)";

		String retrievedName = this.genotypicDataManager.getMapNameById(this.mapId);
		Assert.assertEquals(expectedName, retrievedName);
	}

	@Test
	public void testGetNameByNameId() throws Exception {
		Integer nameId = -1;
		Name name = this.genotypicDataManager.getNameByNameId(nameId);
		Debug.println("testGetNameByNameId(nameId=" + nameId + ") RESULTS: " + name);
	}

	@Test
	public void testGetFirstFiveMaps() throws Exception {
		List<Map> maps = this.genotypicDataManager.getAllMaps(0, 5, Database.CENTRAL);
		Debug.println("RESULTS (testGetFirstFiveMaps): " + maps);
	}

	@Test
	public void testGetMapInfoByMapName() throws Exception {
		String mapName = "RIL-8 (Yueyou13 x J 11)"; // CENTRAL test data
		List<MapInfo> results = this.genotypicDataManager.getMapInfoByMapName(mapName, Database.CENTRAL);
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testGetMapInfoByMapNameBothDB() throws Exception {
		String mapName = "GCP-833TestMap"; // LOCAL test data
		List<MapInfo> results = this.genotypicDataManager.getMapInfoByMapName(mapName, Database.LOCAL);
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testgetMarkerInfoByMarkerIds() throws Exception {
		List<Integer> markerIds = Arrays.asList(1, -1, 2, -2, 3, 4, 5);
		List<MarkerInfo> results = this.genotypicDataManager.getMarkerInfoByMarkerIds(markerIds);
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testGetMapInfoByMapAndChromosome() throws Exception {
		// Replace with a gdms_markers_onmap.map_id, linkage_group in local
		int mapId = -2; // 1;
		String chromosome = "LG23"; // "BC-1_b11";

		List<MapInfo> results = this.genotypicDataManager.getMapInfoByMapAndChromosome(mapId, chromosome);
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testGetMapInfoByMapChromosomeAndPosition() throws Exception {
		// Replace with a gdms_markers_onmap.map_id, linkage_group, start_position in local
		int mapId = -2;
		String chromosome = "LG23";
		float startPosition = 123.4f;

		List<MapInfo> results = this.genotypicDataManager.getMapInfoByMapChromosomeAndPosition(mapId, chromosome, startPosition);
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testGetMapInfoByMarkersAndMap() throws Exception {
		int mapId = -2; // Replace with a gdms_markers_onmap.map_id in local
		List<Integer> markerList = new ArrayList<Integer>();
		markerList.add(-4); // Replace with a (-) gdms_markers_onmap.marker_id in local
		markerList.add(3407); // Replace with a (+) gdms_markers_onmap.marker_id in central

		List<MapInfo> results = this.genotypicDataManager.getMapInfoByMarkersAndMap(markerList, mapId);
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testGetMarkerOnMaps() throws Exception {
		List<Integer> mapIds = Arrays.asList(-2, 2, 3, 4);
		String linkageGroup = "LG23";
		double startPos = 0d;
		double endPos = 100d;

		List<MarkerOnMap> markersOnMap = this.genotypicDataManager.getMarkerOnMaps(mapIds, linkageGroup, startPos, endPos);

		Debug.printObjects(IntegrationTestBase.INDENT, markersOnMap);
	}

	@Test
	public void testGetMarkersOnMapByMarkerIds() throws Exception {
		List<Integer> markerIds = Arrays.asList(-1, -2, -6, 153, 994, 1975);
		List<MarkerOnMap> markersOnMap = this.genotypicDataManager.getMarkersOnMapByMarkerIds(markerIds);
		Debug.printObjects(IntegrationTestBase.INDENT, markersOnMap);
	}

	@Test
	public void testGetAllMarkerNamesFromMarkersOnMap() throws Exception {
		List<String> markerNames = this.genotypicDataManager.getAllMarkerNamesFromMarkersOnMap();
		Debug.println(IntegrationTestBase.INDENT, "Marker names (" + markerNames.size() + "): " + markerNames);
	}

	@Test
	public void testCountDatasetNames() throws Exception {
		long results = this.genotypicDataManager.countDatasetNames(Database.LOCAL);
		Debug.println("testCountDatasetNames(Database.LOCAL) RESULTS: " + results);
	}

	@Test
	public void testGetDatasetNames() throws Exception {
		List<String> result = this.genotypicDataManager.getDatasetNames(0, Integer.MAX_VALUE, Database.LOCAL);
		Debug.printObjects(0, result);
	}

	@Test
	public void testGetDatasetNamesByQtlId() throws Exception {
		Integer qtlId = 1;
		List<String> results = this.genotypicDataManager.getDatasetNamesByQtlId(qtlId, 0, 5);
		Debug.println("testGetDatasetNamesByQtlId(0,5) RESULTS: " + results);
	}

	@Test
	public void testCountDatasetNamesByQtlId() throws Exception {
		Integer qtlId = 1;
		long results = this.genotypicDataManager.countDatasetNamesByQtlId(qtlId);
		Debug.println("testCountDatasetNamesByQtlId() RESULTS: " + results);
	}

	@Test
	public void testGetDatasetDetailsByDatasetName() throws Exception {
		List<DatasetElement> results =
				this.genotypicDataManager.getDatasetDetailsByDatasetName(this.dataset.getDatasetName());
		Debug.println("testGetDatasetDetailsByDatasetName(" + this.dataset.getDatasetName() + ",LOCAL) RESULTS: " + results);
	}

	@Test
	public void testGetMarkersByMarkerNames() throws Exception {
		Marker marker1 = this.createMarker();
		marker1.setMarkerType(GdmsType.TYPE_SNP.getValue());
		marker1.setMarkerName("GA101");
		this.genotypicDataManager.addMarker(marker1);

		Marker marker2 = this.createMarker();
		marker2.setMarkerType(GdmsType.TYPE_SNP.getValue());
		marker2.setMarkerName("GA110");
		this.genotypicDataManager.addMarker(marker2);

		Marker marker3 = this.createMarker();
		marker3.setMarkerType(GdmsType.TYPE_SNP.getValue());
		marker3.setMarkerName("GA122");
		this.genotypicDataManager.addMarker(marker3);

		List<String> markerNames = new ArrayList<String>();
		markerNames.add("GA101");
		markerNames.add("GA110");
		markerNames.add("GA122");
		List<Marker> markers = this.genotypicDataManager.getMarkersByMarkerNames(markerNames, 0, 100);
		Assert.assertTrue(markers.size() > 0);
		Debug.printObjects(IntegrationTestBase.INDENT, markers);
	}

	@Test
	public void testGetMarkerIdsByDatasetId() throws Exception {
		Integer datasetId = Integer.valueOf(2);
		List<Integer> results = this.genotypicDataManager.getMarkerIdsByDatasetId(datasetId);
		Debug.println("testGetMarkerIdByDatasetId(" + datasetId + ") RESULTS: " + results);
	}

	@Test
	public void testGetParentsByDatasetId() throws Exception {
		Integer datasetId = Integer.valueOf(2);
		List<ParentElement> results = this.genotypicDataManager.getParentsByDatasetId(datasetId);
		Debug.println("testGetParentsByDatasetId(" + datasetId + ") RESULTS: " + results);
	}

	@Test
	public void testGetMarkerTypesByMarkerIds() throws Exception {
		List<Integer> markerIds = Arrays.asList(1, 2, 3, 4, 5, -1, -2, -3);
		List<String> results = this.genotypicDataManager.getMarkerTypesByMarkerIds(markerIds);
		Debug.println("testGetMarkerTypeByMarkerIds(" + markerIds + ") RESULTS: " + results);
	}

	@Test
	public void testGetMarkerNamesByGIds() throws Exception {
		List<Integer> gIds = Arrays.asList(1920, 1895, 1434);
		// For test data, SELECT marker_id, gid from gdms_allele_values / gdms_char_values / gdms_mapping_pop_values

		List<MarkerNameElement> results = this.genotypicDataManager.getMarkerNamesByGIds(gIds);
		Debug.println("testGetMarkerNamesByGIds(" + gIds + ") RESULTS: " + results.size() + " = " + results);
	}

	@Test
	public void testGetGermplasmNamesByMarkerNames() throws Exception {
		List<String> markerNames = Arrays.asList("1_0001", "1_0007", "1_0013");

		List<GermplasmMarkerElement> results = this.genotypicDataManager.getGermplasmNamesByMarkerNames(markerNames, Database.LOCAL);
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

		List<MappingValueElement> mappingValues = this.genotypicDataManager.getMappingValuesByGidsAndMarkerNames(gids, markerNames, 0, 100);
		Debug.println("testGetMappingValuesByGidsAndMarkerNames(" + gids + ", " + markerNames + "): ");
		Debug.printFormattedObjects(IntegrationTestBase.INDENT, mappingValues);
	}

	@Test
	public void testGetAllelicValuesByGidsAndMarkerNames() throws Exception {
		List<String> markerNames = Arrays.asList("RN18H05");
		List<Integer> gids = Arrays.asList(7, 1035);
		List<AllelicValueElement> allelicValues = this.genotypicDataManager.getAllelicValuesByGidsAndMarkerNames(gids, markerNames);
		Debug.printObjects(allelicValues);
	}

	@Test
	public void testGetAllelicValuesByGidsAndMarkerNamesForGDMS() throws Exception {

		List<String> markerNames = Arrays.asList("TC03A12", "Seq19E09", "TC7E04", "SeqTEST"); // marker id = 3295, 3296, 1044;
		List<Integer> gids = Arrays.asList(1434, 1435, 1);

		this.testAddGDMSMarker(); // Local test data: add Marker with markerName = "SeqTEST"

		List<AllelicValueElement> allelicValues = this.genotypicDataManager.getAllelicValuesByGidsAndMarkerNames(gids, markerNames);
		Debug.println("testGetAllelicValuesByGidsAndMarkerNamesForGDMS(" + gids + ", " + markerNames + ") RESULTS: ");
		Debug.printFormattedObjects(IntegrationTestBase.INDENT, allelicValues);
	}

	@Test
	public void testGetAllelicValuesByGid() throws Exception {
		Integer gid = 7;
		List<AllelicValueElement> allelicValues = this.genotypicDataManager.getAllelicValuesByGid(gid);
		Debug.printObjects(allelicValues);
	}

	@Test
	public void testGetAllelicValuesFromCharValuesByDatasetId() throws Exception {
		Integer datasetId = Integer.valueOf(6);
		long count = this.genotypicDataManager.countAllelicValuesFromCharValuesByDatasetId(datasetId);
		List<AllelicValueWithMarkerIdElement> allelicValues =
				this.genotypicDataManager.getAllelicValuesFromCharValuesByDatasetId(datasetId, 0, (int) count);
		Debug.println(0, "testGetAllelicValuesFromCharValuesByDatasetId(" + datasetId + ") RESULTS: " + allelicValues.size());
		Debug.printObjects(allelicValues);
	}

	@Test
	public void testGetAllelicValuesFromAlleleValuesByDatasetId() throws Exception {
		Integer datasetId = Integer.valueOf(2);
		long count = this.genotypicDataManager.countAllelicValuesFromAlleleValuesByDatasetId(datasetId);
		List<AllelicValueWithMarkerIdElement> allelicValues =
				this.genotypicDataManager.getAllelicValuesFromAlleleValuesByDatasetId(datasetId, 0, (int) count);
		Debug.println("testGetAllelicValuesFromAlleleValuesByDatasetId(dataset=" + datasetId + ") RESULTS: " + allelicValues.size());
		Debug.printObjects(allelicValues);
	}

	@Test
	public void testGetAllelicValuesFromMappingPopValuesByDatasetId() throws Exception {
		Integer datasetId = Integer.valueOf(3);
		long count = this.genotypicDataManager.countAllelicValuesFromMappingPopValuesByDatasetId(datasetId);
		// manager.countAllelicValuesFromCharValuesByDatasetId(datasetId);
		List<AllelicValueWithMarkerIdElement> allelicValues =
				this.genotypicDataManager.getAllelicValuesFromMappingPopValuesByDatasetId(datasetId, 0, (int) count);
		Debug.println("testGetAllelicValuesFromMappingPopValuesByDatasetId(" + datasetId + ") RESULTS: ");
		Debug.printObjects(IntegrationTestBase.INDENT, allelicValues);
	}

	@Test
	public void testGetMarkerNamesByMarkerIds() throws Exception {
		List<Integer> markerIds = Arrays.asList(1, -1, 2, -2, 3, -3, 4, -4, 5, -5);
		List<MarkerIdMarkerNameElement> markerNames = this.genotypicDataManager.getMarkerNamesByMarkerIds(markerIds);
		Debug.println("testGetMarkerNamesByMarkerIds(" + markerIds + ") RESULTS: ");
		Debug.printObjects(IntegrationTestBase.INDENT, markerNames);
	}

	@Test
	public void testGetAllMarkerTypes() throws Exception {
		List<String> markerTypes = this.genotypicDataManager.getAllMarkerTypes(0, 10);
		Debug.println("testGetAllMarkerTypes(0,10) RESULTS: " + markerTypes);
	}

	@Test
	public void testCountAllMarkerTypesLocal() throws Exception {
		long result = this.genotypicDataManager.countAllMarkerTypes(Database.LOCAL);
		Debug.println("testCountAllMarkerTypes(Database.LOCAL) RESULTS: " + result);
	}

	@Test
	public void testCountAllMarkerTypesCentral() throws Exception {
		long result = this.genotypicDataManager.countAllMarkerTypes(Database.CENTRAL);
		Debug.println("testCountAllMarkerTypes(Database.CENTRAL) RESULTS: " + result);
	}

	@Test
	public void testGetMarkerNamesByMarkerType() throws Exception {
		String markerType = "asdf";
		List<String> markerNames = this.genotypicDataManager.getMarkerNamesByMarkerType(markerType, 0, 10);
		Debug.println("testGetMarkerNamesByMarkerType(" + markerType + ") RESULTS: " + markerNames);
	}

	@Test
	public void testCountMarkerNamesByMarkerType() throws Exception {
		String markerType = "asdf";
		long result = this.genotypicDataManager.countMarkerNamesByMarkerType(markerType);
		Debug.println("testCountMarkerNamesByMarkerType(" + markerType + ") RESULTS: " + result);
	}

	@Test
	public void testGetMarkerInfoByMarkerName() throws Exception {
		String markerName = "1_0437";
		long count = this.genotypicDataManager.countMarkerInfoByMarkerName(markerName);
		Debug.println("countMarkerInfoByMarkerName(" + markerName + ")  RESULTS: " + count);
		List<MarkerInfo> results = this.genotypicDataManager.getMarkerInfoByMarkerName(markerName, 0, (int) count);
		Debug.println("testGetMarkerInfoByMarkerName(" + markerName + ") RESULTS: " + results);
	}

	@Test
	public void testGetMarkerInfoByGenotype() throws Exception {
		String genotype = "";
		long count = this.genotypicDataManager.countMarkerInfoByGenotype(genotype);
		Debug.println("countMarkerInfoByGenotype(" + genotype + ") RESULTS: " + count);
		List<MarkerInfo> results = this.genotypicDataManager.getMarkerInfoByGenotype(genotype, 0, (int) count);
		Debug.println("testGetMarkerInfoByGenotype(" + genotype + ") RESULTS: " + results);
	}

	@Test
	public void testGetMarkerInfoByDbAccessionId() throws Exception {
		String dbAccessionId = "";
		long count = this.genotypicDataManager.countMarkerInfoByDbAccessionId(dbAccessionId);
		Debug.println("countMarkerInfoByDbAccessionId(" + dbAccessionId + ")  RESULTS: " + count);
		List<MarkerInfo> results = this.genotypicDataManager.getMarkerInfoByDbAccessionId(dbAccessionId, 0, (int) count);
		Debug.println("testGetMarkerInfoByDbAccessionId(" + dbAccessionId + ")  RESULTS: " + results);
	}

	@Test
	public void testGetGidsFromCharValuesByMarkerId() throws Exception {
		Integer markerId = 1;
		List<Integer> gids = this.genotypicDataManager.getGIDsFromCharValuesByMarkerId(markerId, 0, 10);
		Debug.println("testGetGidsFromCharValuesByMarkerId(" + markerId + ") RESULTS: " + gids);
	}

	@Test
	public void testCountGidsFromCharValuesByMarkerId() throws Exception {
		Integer markerId = 1;
		long result = this.genotypicDataManager.countGIDsFromCharValuesByMarkerId(markerId);
		Debug.println("testCountGidsFromCharValuesByMarkerId(" + markerId + ") RESULTS: " + result);
	}

	@Test
	public void testGetGidsFromAlleleValuesByMarkerId() throws Exception {
		Integer markerId = 1;
		List<Integer> gids = this.genotypicDataManager.getGIDsFromAlleleValuesByMarkerId(markerId, 0, 10);
		Debug.println("testGetGidsFromAlleleValuesByMarkerId(" + markerId + ") RESULTS: " + gids);
	}

	@Test
	public void testCountGidsFromAlleleValuesByMarkerId() throws Exception {
		Integer markerId = 1;
		long result = this.genotypicDataManager.countGIDsFromCharValuesByMarkerId(markerId);
		Debug.println("testCountGidsFromAlleleValuesByMarkerId(" + markerId + ") RESULTS: " + result);
	}

	@Test
	public void testGetGidsFromMappingPopValuesByMarkerId() throws Exception {
		Integer markerId = 1;
		List<Integer> gids = this.genotypicDataManager.getGIDsFromMappingPopValuesByMarkerId(markerId, 0, 10);
		Debug.println("testGetGidsFromMappingPopValuesByMarkerId(" + markerId + ") RESULTS: " + gids);
	}

	@Test
	public void testGetAllelicValuesByMarkersAndAlleleValues() throws Exception {
		List<Integer> markerIdList = Arrays.asList(2325, 3345);
		List<String> alleleValueList = Arrays.asList("238/238", "T/T");
		List<AllelicValueElement> values =
				this.genotypicDataManager.getAllelicValuesByMarkersAndAlleleValues(Database.CENTRAL, markerIdList, alleleValueList);
		Debug.printObjects(IntegrationTestBase.INDENT, values);
	}

	@Test
	public void testGetAllAllelicValuesByMarkersAndAlleleValues() throws Exception {
		List<Integer> markerIdList = Arrays.asList(2325);
		List<String> alleleValueList = Arrays.asList("238/238");
		List<AllelicValueElement> values =
				this.genotypicDataManager.getAllAllelicValuesByMarkersAndAlleleValues(markerIdList, alleleValueList);
		Debug.printObjects(IntegrationTestBase.INDENT, values);
	}

	@Test
	public void testGetGidsByMarkersAndAlleleValues() throws Exception {
		List<Integer> markerIdList = Arrays.asList(2325);
		List<String> alleleValueList = Arrays.asList("238/238");
		List<Integer> values = this.genotypicDataManager.getGidsByMarkersAndAlleleValues(markerIdList, alleleValueList);
		Debug.printObjects(IntegrationTestBase.INDENT, values);
	}

	@Test
	public void testCountGidsFromMappingPopValuesByMarkerId() throws Exception {
		Integer markerId = 1;
		long result = this.genotypicDataManager.countGIDsFromMappingPopValuesByMarkerId(markerId);
		Debug.println("testCountGidsFromMappingPopValuesByMarkerId(" + markerId + ") RESULTS: " + result);
	}

	@Test
	public void testGetAllDbAccessionIdsFromMarker() throws Exception {
		List<String> dbAccessionIds = this.genotypicDataManager.getAllDbAccessionIdsFromMarker(0, 10);
		Debug.println("testGetAllDbAccessionIdsFromMarker(1,10) RESULTS: " + dbAccessionIds);
	}

	@Test
	public void testCountAllDbAccessionIdsFromMarker() throws Exception {
		long result = this.genotypicDataManager.countAllDbAccessionIdsFromMarker();
		Debug.println("testCountAllDbAccessionIdsFromMarker() RESULTS: " + result);
	}

	@Test
	public void testGetAccMetadatasetsByDatasetIds() throws Exception {
		List<Integer> datasetIds = Arrays.asList(-1, -2, -3, 1, 2, 3);
		List<Integer> gids = Arrays.asList(-2);

		List<AccMetadataSet> nids = this.genotypicDataManager.getAccMetadatasetsByDatasetIds(datasetIds, 0, 10);

		Debug.println("testGgetAccMetadatasetByDatasetIds: " + nids);
	}

	@Test
	public void testGetDatasetIdsForFingerPrinting() throws Exception {
		List<Integer> datasetIds =
				this.genotypicDataManager.getDatasetIdsForFingerPrinting(0,
						(int) this.genotypicDataManager.countDatasetIdsForFingerPrinting());
		Debug.println("testGetDatasetIdsForFingerPrinting() RESULTS: " + datasetIds);
	}

	@Test
	public void testCountDatasetIdsForFingerPrinting() throws Exception {
		long count = this.genotypicDataManager.countDatasetIdsForFingerPrinting();
		Debug.println("testCountDatasetIdsForFingerPrinting() RESULTS: " + count);
	}

	@Test
	public void testGetDatasetIdsForMapping() throws Exception {
		List<Integer> datasetIds =
				this.genotypicDataManager.getDatasetIdsForMapping(0, (int) this.genotypicDataManager.countDatasetIdsForMapping());
		Debug.println("testGetDatasetIdsForMapping() RESULTS: " + datasetIds);
	}

	@Test
	public void testCountDatasetIdsForMapping() throws Exception {
		long count = this.genotypicDataManager.countDatasetIdsForMapping();
		Debug.println("testCountDatasetIdsForMapping() RESULTS: " + count);
	}

	@Test
	public void testGetGdmsAccMetadatasetByGid() throws Exception {
		List<Integer> germplasmIds = Arrays.asList(1, -1, -2);
		List<AccMetadataSet> accMetadataSets =
				this.genotypicDataManager.getGdmsAccMetadatasetByGid(germplasmIds, 0,
						(int) this.genotypicDataManager.countGdmsAccMetadatasetByGid(germplasmIds));
		Debug.println("testGetGdmsAccMetadatasetByGid() RESULTS: ");
		Debug.printObjects(IntegrationTestBase.INDENT, accMetadataSets);
	}

	@Test
	public void testCountGdmsAccMetadatasetByGid() throws Exception {
		List<Integer> germplasmIds = Arrays.asList(956, 1042, -2213, -2215);
		long count = this.genotypicDataManager.countGdmsAccMetadatasetByGid(germplasmIds);
		Debug.println("testCountGdmsAccMetadatasetByGid() RESULTS: " + count);
	}

	@Test
	public void testGetMarkersByGidAndDatasetIds() throws Exception {
		Integer gid = Integer.valueOf(-2215);
		List<Integer> datasetIds = Arrays.asList(1, 2);
		List<Integer> markerIds =
				this.genotypicDataManager.getMarkersBySampleIdAndDatasetIds(gid, datasetIds, 0,
						(int) this.genotypicDataManager.countMarkersBySampleIdAndDatasetIds(gid, datasetIds));
		Debug.println("testGetMarkersByGidAndDatasetIds() RESULTS: " + markerIds);
	}

	@Test
	public void testCountMarkersByGidAndDatasetIds() throws Exception {
		Integer gid = Integer.valueOf(-2215);
		List<Integer> datasetIds = Arrays.asList(1, 2);
		long count = this.genotypicDataManager.countMarkersBySampleIdAndDatasetIds(gid, datasetIds);
		Debug.println("testCountGdmsAccMetadatasetByGid() RESULTS: " + count);
	}

	@Test
	public void testCountAlleleValuesByGids() throws Exception {
		List<Integer> germplasmIds = Arrays.asList(956, 1042, -2213, -2215);
		long count = this.genotypicDataManager.countAlleleValuesByGids(germplasmIds);
		Debug.println("testCountAlleleValuesByGids() RESULTS: " + count);
	}

	@Test
	public void testCountCharValuesByGids() throws Exception {
		List<Integer> germplasmIds = Arrays.asList(956, 1042, -2213, -2215);
		long count = this.genotypicDataManager.countCharValuesByGids(germplasmIds);
		Debug.println("testCountCharValuesByGids() RESULTS: " + count);
	}

	@Test
	public void testGetIntAlleleValuesForPolymorphicMarkersRetrieval() throws Exception {
		// For test data, get gids from LOCAL and CENTRAL: SELECT distinct FROM gdms_allele_values;
		List<Integer> germplasmIds = Arrays.asList(1, 956, 1042, -2213, -2215);

		List<AllelicValueElement> results =
				this.genotypicDataManager.getIntAlleleValuesForPolymorphicMarkersRetrieval(germplasmIds, 0, Integer.MAX_VALUE);
		Debug.println("testGetIntAlleleValuesForPolymorphicMarkersRetrieval() RESULTS: ");
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testCountIntAlleleValuesForPolymorphicMarkersRetrieval() throws Exception {
		// For test data, get gids from LOCAL and CENTRAL: SELECT distinct FROM gdms_allele_values;
		List<Integer> germplasmIds = Arrays.asList(1, 956, 1042, -2213, -2215);
		long count = this.genotypicDataManager.countIntAlleleValuesForPolymorphicMarkersRetrieval(germplasmIds);
		Debug.println("testCountIntAlleleValuesForPolymorphicMarkersRetrieval() RESULTS: " + count);
	}

	@Test
	public void testGetCharAlleleValuesForPolymorphicMarkersRetrieval() throws Exception {
		// For test data, get gids from LOCAL and CENTRAL: SELECT distinct FROM gdms_char_values;
		List<Integer> germplasmIds = Arrays.asList(1, 956, 1042, -2213, -2215);
		List<AllelicValueElement> results =
				this.genotypicDataManager.getCharAlleleValuesForPolymorphicMarkersRetrieval(germplasmIds, 0, Integer.MAX_VALUE);
		Debug.println("testGetIntAlleleValuesForPolymorphicMarkersRetrieval() RESULTS: ");
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testCountCharAlleleValuesForPolymorphicMarkersRetrieval() throws Exception {
		// For test data, get gids from LOCAL and CENTRAL: SELECT distinct FROM gdms_char_values;
		List<Integer> germplasmIds = Arrays.asList(1, 956, 1042, -2213, -2215);
		long count = this.genotypicDataManager.countCharAlleleValuesForPolymorphicMarkersRetrieval(germplasmIds);
		Debug.println("testCountCharAlleleValuesForPolymorphicMarkersRetrieval() RESULTS: " + count);
	}

	@Test
	public void testGetMappingAlleleValuesForPolymorphicMarkersRetrieval() throws Exception {
		// For test data, get gids from LOCAL and CENTRAL: SELECT distinct FROM gdms_mapping_pop_values;
		List<Integer> germplasmIds = Arrays.asList(1, 1434, 1435);
		List<AllelicValueElement> results =
				this.genotypicDataManager.getMappingAlleleValuesForPolymorphicMarkersRetrieval(germplasmIds, 0, Integer.MAX_VALUE);
		Debug.println("testGetMappingAlleleValuesForPolymorphicMarkersRetrieval() RESULTS: ");
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testCountMappingAlleleValuesForPolymorphicMarkersRetrieval() throws Exception {
		// For test data, get gids from LOCAL and CENTRAL: SELECT distinct FROM gdms_mapping_pop_values;
		List<Integer> germplasmIds = Arrays.asList(1, 1434, 1435);
		long count = this.genotypicDataManager.countMappingAlleleValuesForPolymorphicMarkersRetrieval(germplasmIds);
		Debug.println("testCountMappingAlleleValuesForPolymorphicMarkersRetrieval() RESULTS: " + count);
	}

	@Test
	public void testCountNIdsByDatasetIdsAndMarkerIdsAndNotGIds() throws Exception {
		List<Integer> gIds = Arrays.asList(29, 303, 4950);
		List<Integer> datasetIds = Arrays.asList(2);
		List<Integer> markerIds = Arrays.asList(6803);
		int count = this.genotypicDataManager.countNIdsByMarkerIdsAndDatasetIdsAndNotGIds(datasetIds, markerIds, gIds);
		Debug.println("testcCountNIdsByDatasetIdsAndMarkerIdsAndNotGIds() RESULTS: " + count);
	}

	@Test
	public void testGetNIdsByDatasetIdsAndMarkerIds() throws Exception {
		List<Integer> datasetIds = Arrays.asList(2);
		List<Integer> markerIds = Arrays.asList(6803);
		List<Integer> nIdList =
				this.genotypicDataManager.getNIdsByMarkerIdsAndDatasetIds(datasetIds, markerIds, 0,
						this.genotypicDataManager.countNIdsByMarkerIdsAndDatasetIds(datasetIds, markerIds));
		Debug.println("testGetNIdsByDatasetIdsAndMarkerIds() RESULTS: " + nIdList);
	}

	@Test
	public void testCountNIdsByDatasetIdsAndMarkerIds() throws Exception {
		List<Integer> datasetIds = Arrays.asList(2);
		List<Integer> markerIds = Arrays.asList(6803);
		int count = this.genotypicDataManager.countNIdsByMarkerIdsAndDatasetIds(datasetIds, markerIds);
		Debug.println("testCountNIdsByDatasetIdsAndMarkerIds() RESULTS: " + count);
	}

	@Test
	public void testGetAllQtl() throws Exception {
		List<Qtl> qtls = this.genotypicDataManager.getAllQtl(0, (int) this.genotypicDataManager.countAllQtl());
		Debug.println("testGetAllQtl() RESULTS: ");
		Debug.printObjects(IntegrationTestBase.INDENT, qtls);
	}

	@Test
	public void testCountAllQtl() throws Exception {
		long result = this.genotypicDataManager.countAllQtl();
		Debug.println("testCountAllQtl() RESULTS: " + result);
	}

	@Test
	public void testCountQtlIdByName() throws Exception {
		String qtlName = "SLA%";
		long count = this.genotypicDataManager.countQtlIdByName(qtlName);
		Debug.println("testCountQtlIdByName() RESULTS: " + count);
	}

	@Test
	public void testGetQtlIdByName() throws Exception {
		String qtlName = "TWW09_AhV";

		List<Integer> results =
				this.genotypicDataManager.getQtlIdByName(qtlName, 0, (int) this.genotypicDataManager.countQtlIdByName(qtlName));
		Debug.println("testGetQtlIdByName() RESULTS: " + results);
	}

	@Test
	public void testGetQtlByNameFromCentral() throws Exception {
		String qtlName = "TestQTL%";
		List<QtlDetailElement> results =
				this.genotypicDataManager.getQtlByName(qtlName, 0, (int) this.genotypicDataManager.countQtlByName(qtlName));
		Assert.assertTrue(results.size() > 0);
		Debug.printFormattedObjects(IntegrationTestBase.INDENT, results);
		Debug.println("testGetQtlByNameFromCentral() #records: " + results.size());
	}

	@Test
	public void testCountQtlByName() throws Exception {
		String qtlName = "TestQTL%";
		long count = this.genotypicDataManager.countQtlByName(qtlName);
		Assert.assertTrue(count > 0);
		Debug.println("testCountQtlByName() RESULTS: " + count);
	}

	@Test
	public void testgetQtlNamesByQtlIds() throws Exception {
		List<Integer> qtlIds = Arrays.asList(1, 2, 3, -4, -5);
		java.util.Map<Integer, String> qtlNames = this.genotypicDataManager.getQtlNamesByQtlIds(qtlIds);
		Assert.assertTrue(qtlNames.size() > 0);
		for (int i = 0; i < qtlIds.size(); i++) {
			Debug.println(IntegrationTestBase.INDENT, "QTL ID = " + qtlIds.get(i) + " : QTL NAME = " + qtlNames.get(qtlIds.get(i)));
		}
	}

	@Test
	public void testGetQtlByTrait() throws Exception {
		Integer qtlTraitId = 1001; // "SL%";
		List<Integer> results =
				this.genotypicDataManager.getQtlByTrait(qtlTraitId, 0, (int) this.genotypicDataManager.countQtlByTrait(qtlTraitId));
		Debug.println("testGetQtlByTrait() RESULTS: " + results);
	}

	@Test
	public void testCountQtlByTrait() throws Exception {
		Integer qtlTraitId = 1001; // "SL%";
		long count = this.genotypicDataManager.countQtlByTrait(qtlTraitId);
		Debug.println("testCountQtlByTrait() RESULTS: " + count);
	}

	@Test
	public void testGetQtlTraitsByDatasetId() throws Exception {
		Integer datasetId = 7;
		List<Integer> results =
				this.genotypicDataManager.getQtlTraitsByDatasetId(datasetId, 0,
						(int) this.genotypicDataManager.countQtlTraitsByDatasetId(datasetId));
		Debug.println("testGetQtlTraitsByDatasetId() RESULTS: " + results);
	}

	@Test
	public void testCountQtlTraitsByDatasetId() throws Exception {
		Integer datasetId = 7;
		long count = this.genotypicDataManager.countQtlTraitsByDatasetId(datasetId);
		Debug.println("testCountQtlTraitsByDatasetId() RESULTS: " + count);
	}

	@Test
	public void testGetMapIdsByQtlName() throws Exception {
		String qtlName = "HI Control 08_AhI";
		List<Integer> results =
				this.genotypicDataManager.getMapIdsByQtlName(qtlName, 0, (int) this.genotypicDataManager.countMapIdsByQtlName(qtlName));
		Debug.println("testGetMapIdsByQtlName() RESULTS: " + results);
	}

	@Test
	public void testCountMapIdsByQtlName() throws Exception {
		String qtlName = "HI Control 08_AhI";
		long count = this.genotypicDataManager.countMapIdsByQtlName(qtlName);
		Debug.println("testCountMapIdsByQtlName() RESULTS: " + count);
	}

	@Test
	public void testGetMarkersByQtl() throws Exception {
		String qtlName = "HI Control 08_AhI";
		String chromosome = "RIL-3 _LG01";
		float min = 0.0f;
		float max = 10f;
		List<Integer> results =
				this.genotypicDataManager.getMarkerIdsByQtl(qtlName, chromosome, min, max, 0,
						(int) this.genotypicDataManager.countMarkerIdsByQtl(qtlName, chromosome, min, max));
		Assert.assertTrue(results.size() > 0);
		Debug.println("testGetMarkersByQtl() RESULTS: " + results);
	}

	@Test
	public void testCountMarkersByQtl() throws Exception {
		String qtlName = "HI Control 08_AhI";
		String chromosome = "RIL-3 _LG01";
		float min = 0f;
		float max = 10f;
		long count = this.genotypicDataManager.countMarkerIdsByQtl(qtlName, chromosome, min, max);
		Assert.assertTrue(count > 0);
		Debug.println("testCountMarkersByQtl() RESULTS: " + count);
	}

	@Test
	public void getAllParentsFromMappingPopulation() throws Exception {
		int start = 0;
		int end = 10;
		List<ParentElement> parentElements = this.genotypicDataManager.getAllParentsFromMappingPopulation(start, end);
		Debug.println("getAllParentsFromMappingPopulation(" + start + "," + end + ")");
		for (ParentElement parentElement : parentElements) {
			Debug.println(IntegrationTestBase.INDENT, "Parent A NId: " + parentElement.getParentANId() + "  |  Parent B NId: "
					+ parentElement.getParentBGId());
		}
	}

	@Test
	public void countAllParentsFromMappingPopulation() throws Exception {
		long parentElementsCount = this.genotypicDataManager.countAllParentsFromMappingPopulation();
		Debug.println("countAllParentsFromMappingPopulation()");
		Debug.println("Count: " + parentElementsCount);
	}

	@Test
	public void getMapDetailsByName() throws Exception {
		String nameLike = "tag%";
		int start = 0;
		int end = 10;
		List<MapDetailElement> maps = this.genotypicDataManager.getMapDetailsByName(nameLike, start, end);
		Debug.println("getMapDetailsByName('" + nameLike + "'," + start + "," + end + ")");
		for (MapDetailElement map : maps) {
			Debug.println("Map: " + map.getMarkerCount() + "  |  maxStartPosition: " + map.getMaxStartPosition() + "  |  linkageGroup: "
					+ map.getLinkageGroup() + "  |  mapName: " + map.getMapName() + "  |  mapType:  " + map.getMapType());
		}
	}

	@Test
	public void countMapDetailsByName() throws Exception {
		String nameLike = "tag%";
		Long parentElementsCount = this.genotypicDataManager.countMapDetailsByName(nameLike);
		Debug.println("countMapDetailsByName('" + nameLike + "')");
		Debug.println("Count: " + parentElementsCount);
	}

	@Test
	public void testGetAllMapDetails() throws Exception {
		int start = 0;
		int end = (int) this.genotypicDataManager.countAllMapDetails();
		List<MapDetailElement> maps = this.genotypicDataManager.getAllMapDetails(start, end);
		Debug.println("testGetAllMapDetails(" + start + "," + end + ")");
		Debug.printObjects(IntegrationTestBase.INDENT, maps);
	}

	@Test
	public void testCountAllMapDetails() throws Exception {
		Debug.println("testCountAllMapDetails(): " + this.genotypicDataManager.countAllMapDetails());
	}

	@Test
	public void testAddMarkerDetails() throws Exception {
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
				new MarkerDetails(this.markerId, noOfRepeats, motifType, sequence, sequenceLength, minAllele, maxAllele, ssrNr,
						forwardPrimerTemp, reversePrimerTemp, elongationTemp, fragmentSizeExpected, fragmentSizeObserved,
						expectedProductSize, positionOnReferenceSequence, restrictionEnzymeForAssay);

		Integer idAdded = this.genotypicDataManager.addMarkerDetails(markerDetails);
		Debug.println("testAddMarkerDetails() Added: " + (idAdded != null ? markerDetails : null));
	}

	@Test
	public void testAddMarkerUserInfo() throws Exception {
		String principalInvestigator = "Juan Dela Cruz";
		String contact = "juan@irri.com.ph";
		String institute = "IRRI";

		MarkerUserInfo markerUserInfo = new MarkerUserInfo(this.markerId, principalInvestigator, contact, institute);

		Integer idAdded = this.genotypicDataManager.addMarkerUserInfo(markerUserInfo);
		Debug.println("testAddMarkerUserInfo() Added: " + (idAdded != null ? markerUserInfo : null));
	}

	@Test
	public void testAddAccMetadataSet() throws Exception {
		Integer accSampleId = 1;
		Integer sampleId = 1;
		Dataset dataset1 = new Dataset();
		dataset1.setDatasetId(datasetId);

		Sample sample = new Sample();
		sample.setSampleId(sampleId);
		AccMetadataSet accMetadataSet = new AccMetadataSet(null, dataset1, sample, accSampleId);

		Integer idAdded = this.genotypicDataManager.addAccMetadataSet(accMetadataSet);
		Debug.println("testAccMetadataSet() Added: " + (idAdded != null ? accMetadataSet : null));
	}

	@Test
	public void testAddMarkerMetadataSet() throws Exception {
		Integer markerMetadatasetId = null;
		Integer markerSampleId = 1;
		MarkerMetadataSet markerMetadataSet = new MarkerMetadataSet(markerMetadatasetId, dataset, this.markerId, markerSampleId);

		Integer idAdded = this.genotypicDataManager.addMarkerMetadataSet(markerMetadataSet);
		Debug.println("testAddMarkerMetadataSet() Added: " + (idAdded != null ? markerMetadataSet : null));
	}

	@Test
	public void testAddDataset() throws Exception {
		Dataset dataset = this.createDataset();
		this.genotypicDataManager.addDataset(dataset);
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

		Dataset datas =  new Dataset(datasetId, datasetName, datasetDesc, datasetType, genus, species, uploadTemplateDate, remarks, dataType,
				missingData, method, score, institute, principalInvestigator, email, purposeOfStudy, null, null, null, null);


		Sample sample = new Sample();
		sample.setSampleId(1);

		List<AccMetadataSet> accMetadataSets = new ArrayList<>();
		AccMetadataSet accMetadataSet = new AccMetadataSet(null, datas, sample ,1);
		accMetadataSets.add(accMetadataSet);

		List<CharValues> charValues = new ArrayList<>();
		CharValues charValues1 = new CharValues(null, datas, 1, sample, "A/C", 1, 1);
		charValues.add(charValues1);

		datas.setAccMetadataSets(accMetadataSets);
		datas.setCharValues(charValues);

		return datas;

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
			Integer idAdded = this.genotypicDataManager.addGDMSMarker(marker);
			Debug.println("testAddGDMSMarker() Added: " + (idAdded != null ? marker : null));
		} catch (MiddlewareQueryException e) {
			Debug.println(e.getMessage() + "\n");
		}

		// Try adding record that exists in central
		try {
			marker.setMarkerName("GA101");
			Integer idAdded = this.genotypicDataManager.addGDMSMarker(marker);
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

		Integer idAdded = this.genotypicDataManager.addGDMSMarkerAlias(markerAlias);
		Debug.println("testAddGDMSMarkerAlias() Added: " + (idAdded != null ? markerAlias : null));
	}

	@Test
	public void testAddDatasetUser() throws Exception {

		Integer userId = 123;
		DatasetUsers datasetUser = new DatasetUsers(dataset, userId);

		Integer idAdded = this.genotypicDataManager.addDatasetUser(datasetUser);
		Debug.println("testAddDatasetUser() Added: " + (idAdded != null ? datasetUser : null));

	}

	@Test
	public void testAddAlleleValues() throws Exception {
		Integer anId = null;
		Integer sampleId = 1920;
		String alleleBinValue = "alleleBinValue";
		String alleleRawValue = "alleleRawValue";
		Integer peakHeight = 10;

		Sample sample = new Sample();
		sample.setSampleId(sampleId);
		AlleleValues alleleValues = new AlleleValues(anId, this.datasetId, sample, this.markerId, alleleBinValue, alleleRawValue, peakHeight);

		Integer idAdded = this.genotypicDataManager.addAlleleValues(alleleValues);
		Debug.println("testAddAlleleValues() Added: " + (idAdded != null ? alleleValues : null));
	}

	@Test
	public void testAddCharValues() throws Exception {
		Integer acId = null;
		Integer sampleId = 1920;
		String charValue = "CV";
		Integer markerSampleId = 1;
		Integer accSampleId = 1;
		Dataset dataset = new Dataset();
		dataset.setDatasetId(datasetId);
		Sample sample = new Sample();
		sample.setSampleId(sampleId);
		CharValues charValues = new CharValues(acId, dataset, this.markerId, sample, charValue, markerSampleId, accSampleId);

		Integer idAdded = this.genotypicDataManager.addCharValues(charValues);
		Debug.println("testAddCharValues() Added: " + (idAdded != null ? charValues : null));
	}

	@Test
	public void testAddMappingPop() throws Exception {
		String mappingType = "test";
		Integer parentAGId = 956;
		Integer parentBGId = 1042;
		Integer populationSize = 999;
		String populationType = "test";
		String mapDataDescription = "test";
		String scoringScheme = "test";

		MappingPop mappingPop =
				new MappingPop(this.datasetId, mappingType, parentAGId, parentBGId, populationSize, populationType, mapDataDescription,
						scoringScheme, this.mapId);

		Integer idAdded = this.genotypicDataManager.addMappingPop(mappingPop);
		Debug.println("testAddMappingPop() Added: " + (idAdded != null ? mappingPop : null));
	}

	@Test
	public void testAddMappingPopValue() throws Exception {
		Integer mpId = null;
		String mapCharValue = "X";
		Integer sampleId = 1434;
		Integer markerSampleId = 1;
		Integer accSampleId = 1;
		Sample sample = new Sample();
		sample.setSampleId(sampleId);
		MappingPopValues mappingPopValue =
				new MappingPopValues(mpId, mapCharValue, this.datasetId, sample, this.markerId, markerSampleId, accSampleId);

		Integer idAdded = this.genotypicDataManager.addMappingPopValue(mappingPopValue);
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
			Integer idAdded = this.genotypicDataManager.addMarkerOnMap(markerOnMap);
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
		Integer cloneId = -1;
		Float qValue = Float.valueOf("123.45");
		Float reproducibility = Float.valueOf("543.21");
		Float callRate = Float.valueOf("12.3");
		Float picValue = Float.valueOf("543.2");
		Float discordance = Float.valueOf("1.2");

		DartValues dartValue =
				new DartValues(adId, this.datasetId, this.markerId, cloneId, qValue, reproducibility, callRate, picValue, discordance);

		Integer idAdded = this.genotypicDataManager.addDartValue(dartValue);
		Debug.println("testAddDartValue() Added: " + (idAdded != null ? dartValue : null));
	}

	@Test
	public void testAddQtl() throws Exception {
		Integer qtlId = null;
		String qtlName = "TestQTL";
		Dataset dataset = this.createDataset();
		Integer datasetId = this.genotypicDataManager.addDataset(dataset);

		Qtl qtl = new Qtl(qtlId, qtlName, datasetId);
		Integer idAdded = this.genotypicDataManager.addQtl(qtl);
		Debug.println("testAddQtl() Added: " + (idAdded != null ? qtl : null));

		this.testAddQtlDetails(idAdded);
	}

	private void testAddQtlDetails(Integer qtlId) throws Exception {
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
				new QtlDetails(qtlId, this.mapId, minPosition, maxPosition, traitId, experiment, effect, scoreValue, rSquare, linkageGroup,
						interactions, leftFlankingMarker, rightFlankingMarker, position, clen, seAdditive, hvParent, hvAllele, lvParent,
						lvAllele);

		Integer idAdded = this.genotypicDataManager.addQtlDetails(qtlDetails);
		Debug.println("testAddQtlDetails() Added: " + (idAdded != null ? qtlDetails : null));
	}

	@Test
	public void testAddMap() throws Exception {
		this.addMap();
	}

	private Integer addMap() throws Exception {
		Integer mapId = null;
		String mapName = "RIL-1 (TAG 24 x ICGV 86031)";
		String mapType = "genetic";
		Integer mpId = 0;
		String mapDesc = null;
		String mapUnit = null;
		String genus = "Genus";
		String species = "Groundnut";
		String institute = "ICRISAT";
		Map map = new Map(mapId, mapName, mapType, mpId, mapDesc, mapUnit, genus, species, institute);

		Integer idAdded = this.genotypicDataManager.addMap(map);
		Debug.println("testAddMap() Added: " + (idAdded != null ? map : null));

		return idAdded;

	}

	private Marker createMarker() {
		Integer markerId = null; // Will be set/overridden by the function
		String markerType = null; // Will be set/overridden by the function
		String markerName = "SeqTEST" + System.currentTimeMillis();
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

		Boolean addStatus = this.genotypicDataManager.setSNPMarkers(marker, markerAlias, markerDetails, markerUserInfo);
		if (addStatus) {
			Debug.println("testSetSNPMarkers() Added: ");
			Debug.println(IntegrationTestBase.INDENT, marker.toString());
			Debug.println(IntegrationTestBase.INDENT, markerAlias.toString());
			Debug.println(IntegrationTestBase.INDENT, markerDetails.toString());
			Debug.println(IntegrationTestBase.INDENT, markerUserInfo.toString());
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
			this.genotypicDataManager.setSNPMarkers(marker, markerAlias, markerDetails, markerUserInfo);
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

		Boolean addStatus = this.genotypicDataManager.setCAPMarkers(marker, markerAlias, markerDetails, markerUserInfo);
		if (addStatus) {
			Debug.println("testSetCAPMarkers() Added: ");
			Debug.println(IntegrationTestBase.INDENT, marker.toString());
			Debug.println(IntegrationTestBase.INDENT, markerAlias.toString());
			Debug.println(IntegrationTestBase.INDENT, markerDetails.toString());
			Debug.println(IntegrationTestBase.INDENT, markerUserInfo.toString());
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
			Boolean addStatus = this.genotypicDataManager.setCISRMarkers(marker, markerAlias, markerDetails, markerUserInfo);
			if (addStatus) {
				Debug.println("testSetCISRMarkers() Added: ");
				Debug.println(IntegrationTestBase.INDENT, marker.toString());
				Debug.println(IntegrationTestBase.INDENT, markerAlias.toString());
				Debug.println(IntegrationTestBase.INDENT, markerDetails.toString());
				Debug.println(IntegrationTestBase.INDENT, markerUserInfo.toString());
			}
		} catch (MiddlewareQueryException e) {
			// Marker already exists. Try again
			this.testSetCISRMarkers();
		}
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
			addStatus = this.genotypicDataManager.setMaps(marker, markerOnMap, map);
		} catch (MiddlewareQueryException e) {
			if (e.getMessage().contains("The marker on map combination already exists")) {
				Debug.println("The marker on map combination already exists");
			} else {
				Debug.println("testSetMaps() Added: " + (addStatus != null ? marker : null) + " | "
						+ (addStatus != null ? markerOnMap : null) + " | " + (addStatus != null ? map : null));
			}
		}

		Debug.println(IntegrationTestBase.INDENT, "Map: " + map);
		Debug.println(IntegrationTestBase.INDENT, "MarkerOnMap: " + markerOnMap);
	}

	@Test
	public void testGetMapIDsByQTLName() throws Exception {
		String qtlName = "TestQTL";
		List<Integer> mapIDs = this.genotypicDataManager.getMapIDsByQTLName(qtlName, 0, 2);
		Debug.println("TestGetMapIDsByQTLName(" + qtlName + ")");
		Debug.printObjects(IntegrationTestBase.INDENT, mapIDs);
	}

	@Test
	public void testCountMapIDsByQTLName() throws Exception {
		String qtlName = "TestQTL";
		long count = this.genotypicDataManager.countMapIDsByQTLName(qtlName);
		Debug.println("TestCountMapIDsByQTLName(" + qtlName + ")");
		Debug.println(IntegrationTestBase.INDENT, "Count of Map IDs: " + count);
	}

	@Test
	public void testGetMarkerIDsByMapIDAndLinkageBetweenStartPosition() throws Exception {
		int mapID = 1; // Groundnut central
		String linkage = "BC-1_b11";
		double startPos = 0;
		double endPos = 100;
		Set<Integer> markerIDs =
				this.genotypicDataManager.getMarkerIDsByMapIDAndLinkageBetweenStartPosition(mapID, linkage, startPos, endPos, 0,
						Integer.MAX_VALUE);
		Debug.printObjects(IntegrationTestBase.INDENT, new ArrayList<Integer>(markerIDs));
	}

	@Test
	public void testGetMarkersByPositionAndLinkageGroup() throws Exception {
		String linkage = "LG01";
		double startPos = 0;
		double endPos = 2.2;
		List<Marker> markers = this.genotypicDataManager.getMarkersByPositionAndLinkageGroup(startPos, endPos, linkage);
		Assert.assertTrue(markers.size() > 0);
		Debug.printObjects(IntegrationTestBase.INDENT, markers);
	}

	@Test
	public void testCountMarkerIDsByMapIDAndLinkageBetweenStartPosition() throws Exception {
		int mapID = 1;
		String linkage = "LG01";
		double startPos = 0;
		double endPos = 2.2;
		long count = this.genotypicDataManager.countMarkerIDsByMapIDAndLinkageBetweenStartPosition(mapID, linkage, startPos, endPos);
		Debug.println("Count of Marker IDs: " + count);
	}

	@Test
	public void testGetMarkersByMarkerIDs() throws Exception {
		List<Integer> markerIDs = Arrays.asList(1317);
		List<Marker> markerList = this.genotypicDataManager.getMarkersByMarkerIds(markerIDs, 0, 5);
		Debug.printObjects(IntegrationTestBase.INDENT, markerList);
	}

	@Test
	public void testCountMarkersByMarkerIDs() throws Exception {
		List<Integer> markerIDs = Arrays.asList(1317);
		long count = this.genotypicDataManager.countMarkersByMarkerIds(markerIDs);
		Debug.println("Count of Markers: " + count);
	}

	@Test
	public void testGetQtlByQtlIdsFromCentral() throws Exception {
		List<Integer> qtlIds = Arrays.asList(1, 2, 3);
		List<QtlDetailElement> results =
				this.genotypicDataManager.getQtlByQtlIds(qtlIds, 0, (int) this.genotypicDataManager.countQtlByQtlIds(qtlIds));
		Assert.assertTrue(results.size() > 0);
		Debug.printFormattedObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testGetQtlByQtlIdsBothDB() throws Exception {
		List<Integer> qtlIds = Arrays.asList(1, 2, 3, -1, -2, -3);
		List<QtlDetailElement> results =
				this.genotypicDataManager.getQtlByQtlIds(qtlIds, 0, (int) this.genotypicDataManager.countQtlByQtlIds(qtlIds));
		Assert.assertTrue(results.size() > 0);
		Debug.printFormattedObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testCountQtlByQtlIdsFromCentral() throws Exception {
		List<Integer> qtlIds = Arrays.asList(1, 2, 3);
		long count = this.genotypicDataManager.countQtlByQtlIds(qtlIds);
		Assert.assertTrue(count > 0);
		Debug.println("testCountQtlByQtlIdsFromCentral() RESULTS: " + count);
	}

	@Test
	public void testCountQtlByQtlIds() throws Exception {
		List<Integer> qtlIds = Arrays.asList(1, 2, 3, -1, -2, -3);
		long count = this.genotypicDataManager.countQtlByQtlIds(qtlIds);
		Assert.assertTrue(count > 0);
		Debug.println("testCountQtlByQtlIds() RESULTS: " + count);
	}

	@Test
	public void testGetQtlDataByQtlTraits() throws Exception {
		List<Integer> qtlTraits = new ArrayList<Integer>();
		qtlTraits.add(1001); // "DE"
		List<QtlDataElement> results =
				this.genotypicDataManager.getQtlDataByQtlTraits(qtlTraits, 0,
						(int) this.genotypicDataManager.countQtlDataByQtlTraits(qtlTraits));
		Debug.println("testGetQtlDataByQtlTraits() RESULTS: " + results.size());
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testCountQtlDataByQtlTraits() throws Exception {
		List<Integer> qtlTraits = new ArrayList<Integer>();
		qtlTraits.add(1001); // "DE"
		long count = this.genotypicDataManager.countQtlDataByQtlTraits(qtlTraits);
		Debug.println("testCountQtlDataByQtlTraits() RESULTS: " + count);
	}

	@Test
	public void testGetQtlDetailsByQtlTraits() throws Exception {
		List<Integer> qtlTraits = Arrays.asList(1001);
		List<QtlDetailElement> results = this.genotypicDataManager.getQtlDetailsByQtlTraits(qtlTraits, 0, Integer.MAX_VALUE);
		Assert.assertTrue(results.size() >= 3);
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testCountQtlDetailsByQtlTraits() throws Exception {
		List<Integer> qtlTraits = Arrays.asList(1001);
		long count = this.genotypicDataManager.countQtlDetailsByQtlTraits(qtlTraits);
		Assert.assertTrue(count >= 3);
		Debug.println("testCountQtlDataByQtlTraits() RESULTS: " + count);
	}

	@Test
	public void testCountAllelicValuesFromAlleleValuesByDatasetId() throws Exception {
		long count = this.genotypicDataManager.countAllelicValuesFromAlleleValuesByDatasetId(this.datasetId);
		Assert.assertNotNull(count);
		Debug.println("testCountAllelicValuesFromAlleleValuesByDatasetId(" + this.datasetId + ") Results: " + count);
	}

	@Test
	public void testCountAllelicValuesFromCharValuesByDatasetId() throws Exception {
		long count = this.genotypicDataManager.countAllelicValuesFromCharValuesByDatasetId(this.datasetId);
		Assert.assertNotNull(count);
		Debug.println("testCountAllelicValuesFromCharValuesByDatasetId(" + this.datasetId + ") Results: " + count);
	}

	@Test
	public void testCountAllelicValuesFromMappingPopValuesByDatasetId() throws Exception {
		long count = this.genotypicDataManager.countAllelicValuesFromMappingPopValuesByDatasetId(this.datasetId);
		Assert.assertNotNull(count);
		Debug.println("testCountAllelicValuesFromMappingPopValuesByDatasetId(" + this.datasetId + ") Results: " + count);
	}

	@Test
	public void testCountAllParentsFromMappingPopulation() throws Exception {
		long count = this.genotypicDataManager.countAllParentsFromMappingPopulation();
		Assert.assertNotNull(count);
		Debug.println("testCountAllParentsFromMappingPopulation() Results: " + count);
	}

	@Test
	public void testCountMapDetailsByName() throws Exception {
		String nameLike = "GCP-833TestMap"; // change map_name
		long count = this.genotypicDataManager.countMapDetailsByName(nameLike);
		Assert.assertNotNull(count);
		Debug.println("testCountMapDetailsByName(" + nameLike + ") Results: " + count);
	}

	@Test
	public void testGetMapNamesByMarkerIds() throws Exception {
		List<Integer> markerIds = Arrays.asList(-6, 1317, 621, 825, 211);
		java.util.Map<Integer, List<String>> markerMaps = this.genotypicDataManager.getMapNamesByMarkerIds(markerIds);
		Assert.assertTrue(markerMaps.size() > 0);
		for (int i = 0; i < markerIds.size(); i++) {
			Debug.println(IntegrationTestBase.INDENT,
					"Marker ID = " + markerIds.get(i) + " : Map Name/s = " + markerMaps.get(markerIds.get(i)));
		}
	}

	@Test
	public void testCountMarkerInfoByDbAccessionId() throws Exception {
		String dbAccessionId = ""; // change dbAccessionId
		long count = this.genotypicDataManager.countMarkerInfoByDbAccessionId(dbAccessionId);
		Assert.assertNotNull(count);
		Debug.println("testCountMarkerInfoByDbAccessionId(" + dbAccessionId + ") Results: " + count);
	}

	@Test
	public void testCountMarkerInfoByGenotype() throws Exception {
		String genotype = "cv. Tatu"; // change genotype
		long count = this.genotypicDataManager.countMarkerInfoByGenotype(genotype);
		Assert.assertNotNull(count);
		Debug.println("testCountMarkerInfoByGenotype(" + genotype + ") Results: " + count);
	}

	@Test
	public void testCountMarkerInfoByMarkerName() throws Exception {
		String markerName = "SeqTEST"; // change markerName
		long count = this.genotypicDataManager.countMarkerInfoByMarkerName(markerName);
		Assert.assertNotNull(count);
		Debug.println("testCountMarkerInfoByMarkerName(" + markerName + ") Results: " + count);
	}

	@Test
	public void testGetAllParentsFromMappingPopulation() throws Exception {
		long count = this.genotypicDataManager.countAllParentsFromMappingPopulation();
		List<ParentElement> results = this.genotypicDataManager.getAllParentsFromMappingPopulation(0, (int) count);
		Assert.assertNotNull(results);
		Assert.assertFalse(results.isEmpty());
		Debug.println("testGetAllParentsFromMappingPopulation() Results: ");
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testGetMapDetailsByName() throws Exception {
		String nameLike = "GCP-833TestMap"; // change map_name
		long count = this.genotypicDataManager.countMapDetailsByName(nameLike);
		List<MapDetailElement> results = this.genotypicDataManager.getMapDetailsByName(nameLike, 0, (int) count);
		Assert.assertNotNull(results);
		Assert.assertFalse(results.isEmpty());
		Debug.println("testGetMapDetailsByName(" + nameLike + ")");
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testGetMarkersByIds() throws Exception {
		List<Integer> markerIds = Arrays.asList(1, 3);
		List<Marker> results = this.genotypicDataManager.getMarkersByIds(markerIds, 0, 100);
		Assert.assertNotNull(results);
		Assert.assertFalse(results.isEmpty());
		Debug.println("testGetMarkersByIds(" + markerIds + ") Results: ");
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testGetMarkersByType() throws Exception {

		Marker marker = this.createMarker();
		marker.setMarkerType(GdmsType.TYPE_SNP.getValue());
		this.genotypicDataManager.addMarker(marker);
		List<Marker> results = this.genotypicDataManager.getMarkersByType(GdmsType.TYPE_SNP.getValue());
		Assert.assertFalse(results.isEmpty());

		marker = this.createMarker();
		marker.setMarkerType(GdmsType.TYPE_SSR.getValue());
		this.genotypicDataManager.addMarker(marker);
		Debug.printObjects(IntegrationTestBase.INDENT, results);
		results = this.genotypicDataManager.getMarkersByType(GdmsType.TYPE_SSR.getValue());
		Assert.assertFalse(results.isEmpty());
		Debug.printObjects(IntegrationTestBase.INDENT, results);

		marker = this.createMarker();
		marker.setMarkerType(GdmsType.TYPE_CISR.getValue());
		this.genotypicDataManager.addMarker(marker);
		results = this.genotypicDataManager.getMarkersByType(GdmsType.TYPE_CISR.getValue());
		Assert.assertFalse(results.isEmpty());
		Debug.printObjects(IntegrationTestBase.INDENT, results);

		marker = this.createMarker();
		marker.setMarkerType(GdmsType.TYPE_DART.getValue());
		this.genotypicDataManager.addMarker(marker);
		results = this.genotypicDataManager.getMarkersByType(GdmsType.TYPE_DART.getValue());
		Assert.assertFalse(results.isEmpty());
		Debug.printObjects(IntegrationTestBase.INDENT, results);

		marker = this.createMarker();
		marker.setMarkerType(GdmsType.TYPE_CAP.getValue());
		this.genotypicDataManager.addMarker(marker);
		results = this.genotypicDataManager.getMarkersByType(GdmsType.TYPE_CAP.getValue());
		Assert.assertFalse(results.isEmpty());
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testGetSNPsByHaplotype() throws Exception {
		String haplotype = "TRIAL";
		List<Marker> results = this.genotypicDataManager.getSNPsByHaplotype(haplotype);
		Debug.println("testGetSNPsByHaplotype(" + haplotype + ") Results: ");
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testAddHaplotype() throws Exception {

		TrackData trackData = new TrackData(null, "TEST Track Data", 1);

		List<TrackMarker> trackMarkers = new ArrayList<TrackMarker>();
		trackMarkers.add(new TrackMarker(null, null, 1, 1));
		trackMarkers.add(new TrackMarker(null, null, 2, 2));
		trackMarkers.add(new TrackMarker(null, null, 3, 3));

		this.genotypicDataManager.addHaplotype(trackData, trackMarkers);

		// INSERT INTO gdms_track_data(track_id, track_name, user_id) VALUES (generatedTrackId, haplotype, userId);
		// INSERT INTO gdms_track_markers(tmarker_id, track_id, marker_id, marker_sample_id)
		// VALUES (generatedTrackId, generatedTrackId, markerId, markerSampleId);

		Debug.println(IntegrationTestBase.INDENT, "Added:");
		Debug.printObject(IntegrationTestBase.INDENT * 2, trackData);
		Debug.printObjects(IntegrationTestBase.INDENT * 2, trackMarkers);
	}

	@Test
	public void testCountAllMaps() throws Exception {
		long count = this.genotypicDataManager.countAllMaps(Database.LOCAL);
		Assert.assertNotNull(count);
		Debug.println("testCountAllMaps(" + Database.LOCAL + ") Results: " + count);
	}

	@Test
	public void testGetAllMaps() throws Exception {
		List<Map> results = this.genotypicDataManager.getAllMaps(0, 100, Database.LOCAL);
		Assert.assertNotNull(results);
		Assert.assertFalse(results.isEmpty());
		Debug.println("testGetAllMaps(" + Database.LOCAL + ") Results:");
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testCountNidsFromAccMetadatasetByDatasetIds() throws Exception {
		List<Integer> datasetIds = Arrays.asList(this.datasetId);
		long count = this.genotypicDataManager.countAccMetadatasetByDatasetIds(datasetIds);
		Debug.println("testCountNidsFromAccMetadatasetByDatasetIds(" + datasetIds + ") = " + count);
	}

	@Test
	public void testCountMarkersFromMarkerMetadatasetByDatasetIds() throws Exception {
		List<Integer> datasetIds = Arrays.asList(2, 3, 4);
		long count = this.genotypicDataManager.countMarkersFromMarkerMetadatasetByDatasetIds(datasetIds);
		Debug.println("testCountMarkersFromAccMetadatasetByDatasetIds(" + datasetIds + ") = " + count);
	}

	@Test
	public void testGetMapIdByName() throws Exception {
		String mapName = "Consensus_Biotic"; // CENTRAL test data
		Integer mapId = this.genotypicDataManager.getMapIdByName(mapName);
		Debug.println("Map ID for " + mapName + " = " + mapId);
		mapName = "name that does not exist";
		mapId = this.genotypicDataManager.getMapIdByName(mapName);
		Debug.println("Map ID for " + mapName + " = " + mapId);
		mapName = "TEST"; // local test data INSERT INTO gdms_map (map_id,
		// map_name, map_type, mp_id, map_desc, map_unit)
		// values(-1, 'TEST', 'genetic', 0, 'TEST', 'cM');
		mapId = this.genotypicDataManager.getMapIdByName(mapName);
		Debug.println("Map ID for " + mapName + " = " + mapId);
	}

	@Test
	public void testCountMappingPopValuesByGids() throws Exception {
		List<Integer> gIds = Arrays.asList(1434, 1435, 1436);
		long count = this.genotypicDataManager.countMappingPopValuesByGids(gIds);
		Debug.println("countMappingPopValuesByGids(" + gIds + ") = " + count);
	}

	@Test
	public void testCountMappingAlleleValuesByGids() throws Exception {
		List<Integer> gIds = Arrays.asList(2213, 2214);
		long count = this.genotypicDataManager.countMappingAlleleValuesByGids(gIds);
		Debug.println("testCountMappingAlleleValuesByGids(" + gIds + ") = " + count);
	}

	@Test
	public void testGetAllFromMarkerMetadatasetByMarkers() throws Exception {
		List<Integer> markerIds = Arrays.asList(3302, -1);
		List<MarkerMetadataSet> result = this.genotypicDataManager.getAllFromMarkerMetadatasetByMarkers(markerIds);
		Debug.println("testGetAllFromMarkerMetadatasetByMarker(" + markerIds + "): ");
		Debug.printObjects(IntegrationTestBase.INDENT, result);
	}

	@Test
	public void testGetDatasetDetailsByDatasetIds() throws Exception {
		List<Integer> datasetIds = Arrays.asList(-1, -2, -3, 3, 4, 5);
		List<Dataset> result = this.genotypicDataManager.getDatasetDetailsByDatasetIds(datasetIds);
		Debug.println("testGetDatasetDetailsByDatasetId(" + datasetIds + "): ");
		Debug.printObjects(IntegrationTestBase.INDENT, result);
	}

	@Test
	public void testGetQTLIdsByDatasetIds() throws Exception {
		List<Integer> datasetIds = Arrays.asList(1, -6);
		// To get test dataset_ids, run in Local and Central: select qtl_id, dataset_id from gdms_qtl;
		List<Integer> qtlIds = this.genotypicDataManager.getQTLIdsByDatasetIds(datasetIds);
		Debug.println("testGetQTLIdsByDatasetIds(" + datasetIds + "): " + qtlIds);
	}

	@Test
	public void testGetAllFromAccMetadataset() throws Exception {
		List<Integer> gids = Arrays.asList(2012, 2014, 2016);
		Integer datasetId = 5;
		List<AccMetadataSet> result = this.genotypicDataManager.getAllFromAccMetadataset(gids, datasetId, SetOperation.NOT_IN);
		Debug.println("testGetAllFromAccMetadataset(gid=" + gids + ", datasetId=" + datasetId + "): ");
		Debug.printObjects(IntegrationTestBase.INDENT, result);
	}

	@Test
	public void testGetMapAndMarkerCountByMarkers() throws Exception {
		List<Integer> markerIds = Arrays.asList(1317, 1318, 1788, 1053, 2279, 50);
		List<MapDetailElement> details = this.genotypicDataManager.getMapAndMarkerCountByMarkers(markerIds);
		Debug.println("testGetMapAndMarkerCountByMarkers(" + markerIds + ")");
		Debug.printObjects(IntegrationTestBase.INDENT, details);
	}

	@Test
	public void testGetAllMTAs() throws Exception {
		List<Mta> result = this.genotypicDataManager.getAllMTAs();
		Debug.println("testGetAllMTAs(): ");
		Debug.printObjects(IntegrationTestBase.INDENT, result);
	}

	@Test
	public void testCountAllMTAs() throws Exception {
		long count = this.genotypicDataManager.countAllMTAs();
		Debug.println("testCountAllMTAs(): " + count);
	}

	@Test
	public void testGetMTAsByTrait() throws Exception {
		Integer traitId = 1;
		List<Mta> result = this.genotypicDataManager.getMTAsByTrait(traitId);
		Debug.println("testGetMTAsByTrait(): ");
		Debug.printObjects(IntegrationTestBase.INDENT, result);
	}

	@Test
	public void testGetAllSNPs() throws Exception {
		List<Marker> result = this.genotypicDataManager.getAllSNPMarkers();
		Debug.println("testGetAllSNPs(): ");
		Debug.printObjects(IntegrationTestBase.INDENT, result);
	}

	@Test
	public void testGetAlleleValuesByMarkers() throws Exception {
		List<Integer> markerIds = Arrays.asList(-1, -2, 956, 1037);
		List<AllelicValueElement> result = this.genotypicDataManager.getAlleleValuesByMarkers(markerIds);
		Debug.println("testGetAlleleValuesByMarkers(): ");
		Debug.printObjects(IntegrationTestBase.INDENT, result);
	}

	@Test
	public void testDeleteQTLs() throws Exception {
		List<Qtl> qtls = this.genotypicDataManager.getAllQtl(0, 1);
		List<Integer> qtlIds = new ArrayList<Integer>();
		if (qtls != null && qtls.size() > 0) {
			qtlIds.add(qtls.get(0).getQtlId());
			int datasetId = qtls.get(0).getDatasetId();
			Debug.println("testDeleteQTLs(qtlIds=" + qtlIds + ", datasetId=" + datasetId);
			this.genotypicDataManager.deleteQTLs(qtlIds, datasetId);
			Debug.println("done with testDeleteQTLs");
		}
	}

	@Test
	public void testGetDatasetsByType() throws Exception {
		List<Dataset> datasets = this.genotypicDataManager.getDatasetsByType(GdmsType.TYPE_MAPPING);
		Debug.printObjects(IntegrationTestBase.INDENT, datasets);
	}

	@Test
	public void testCountQtlDetailsByMapId() throws Exception {
		int mapId = 7;
		Debug.println("testCountQTLsByMapId(" + mapId + ")");
		long count = this.genotypicDataManager.countQtlDetailsByMapId(mapId);
		Debug.println("COUNT = " + count);
	}

	@Test
	public void testDeleteMaps() throws Exception {
		Integer mapId = this.addMap();
		Debug.println("testDeleteMaps(" + mapId + ")");
		this.genotypicDataManager.deleteMaps(mapId);
		Debug.println("done with testDeleteMaps");
	}

	@Test
	public void testGetMarkerFromCharValuesByGids() throws Exception {
		List<Integer> gIds = Arrays.asList(2012, 2014, 2016, 310544);
		List<MarkerSampleId> result = this.genotypicDataManager.getMarkerFromCharValuesByGids(gIds);
		Debug.printObjects(IntegrationTestBase.INDENT, result);
	}

	@Test
	public void testGetMarkerFromAlleleValuesByGids() throws Exception {
		List<Integer> gIds = Arrays.asList(2213, 2214);
		List<MarkerSampleId> result = this.genotypicDataManager.getMarkerFromAlleleValuesByGids(gIds);
		Debug.printObjects(IntegrationTestBase.INDENT, result);
	}

	@Test
	public void testGetMarkerFromMappingPopValuesByGids() throws Exception {
		List<Integer> gIds = Arrays.asList(1434, 1435);
		List<MarkerSampleId> result = this.genotypicDataManager.getMarkerFromMappingPopByGids(gIds);
		Debug.printObjects(IntegrationTestBase.INDENT, result);
	}

	@Test
	public void testAddMta() throws Exception {
		Mta mta =
				new Mta(null, 1, null, 1, 2.1f, 1, 1.1f, 2.2f, 3.3f, "gene", "chromosome", "alleleA", "alleleB", "alleleAPhenotype",
						"alleleBPhenotype", 4.4f, 5.5f, 6.6f, 7.7f, "correctionMethod", 8.8f, 9.9f, "dominance", "evidence", "reference",
						"notes");
		MtaMetadata mtaMetadata = new MtaMetadata(this.datasetId, "project1", "population", 100, "Thousand");
		DatasetUsers users = new DatasetUsers(dataset, 1);
		this.genotypicDataManager.addMTA(this.dataset, mta, mtaMetadata, users);

		// non-null id means the records were inserted.
		Assert.assertTrue(mta.getMtaId() != null && mtaMetadata.getDatasetID() != null);

		Debug.println("MTA added: ");
		Debug.printObject(IntegrationTestBase.INDENT, mta);
		Debug.printObject(IntegrationTestBase.INDENT, mtaMetadata);
	}

	@Test
	public void testAddMtaGCP9174() throws Exception {
		Dataset dataset =
				new Dataset(null, "sample", "testing", "MTA", "Groundnut", "Groundnut", null, "", "int", null, "Tassel", "LOD", "ICRISAT",
						"TrusharShah", null, null, null, null, null, null);
		Integer datasetId = this.genotypicDataManager.addDataset(dataset);
		dataset.setDatasetId(datasetId);

		Mta mta =
				new Mta(null, this.markerId, datasetId, this.mapId, 6.01f, 22395, 0.0f, 0.0f, 0.0f, "1RS:1AL", "RIL-1 _LG12", "C", "T",
						"absent", "present", 0.0f, 0.0f, 0.0f, 0.0f, "Bonferroni", 0.0f, 0.0f, "co-dominant", "association",
						"Ellis et al (2002) TAG 105:1038-1042", "");

		MtaMetadata mtaMetadata = new MtaMetadata(datasetId, "project1", "population", 100, "Thousand");

		DatasetUsers users = new DatasetUsers(dataset, 1);

		this.genotypicDataManager.addMTA(dataset, mta, mtaMetadata, users);

		// non-null id means the records were inserted.
		Assert.assertTrue(mta.getMtaId() != null && mtaMetadata.getDatasetID() != null);

		Debug.println("MTA added: ");
		Debug.printObject(IntegrationTestBase.INDENT, dataset);
		Debug.printObject(IntegrationTestBase.INDENT, mta);
		Debug.printObject(IntegrationTestBase.INDENT, mtaMetadata);
	}

	@Test
	public void testSetMTA() throws Exception {
		Dataset dataset =
				new Dataset(null, "TEST DATASET NAME", "DATASET DESC", "MTA", "GENUS", "SPECIES", null, "REMARKS", "int", null, "METHOD",
						"0.43", "INSTITUTE", "PI", "EMAIL", "OBJECTIVE", null, null, null, null);
		List<Mta> mtaList = new ArrayList<Mta>();
		mtaList.add(new Mta(null, 1, null, 1, 2.1f, 1, 1.1f, 2.2f, 3.3f, "gene", "chromosome", "alleleA", "alleleB", "alleleAPhenotype",
				"alleleBPhenotype", 4.4f, 5.5f, 6.6f, 7.7f, "correctionMethod", 8.8f, 9.9f, "dominance", "evidence", "reference", "notes"));
		mtaList.add(new Mta(null, 2, null, 2, 3.1f, 2, 2.1f, 3.2f, 4.3f, "gene", "chromosome", "alleleA", "alleleB", "alleleAPhenotype",
				"alleleBPhenotype", 5.4f, 6.5f, 7.6f, 8.7f, "correctionMethod", 9.8f, 10.9f, "dominance", "evidence", "reference", "notes"));

		MtaMetadata metadata = new MtaMetadata(null, "project1", "population", 100, "Thousand");

		DatasetUsers users = new DatasetUsers(null, 1);
		this.genotypicDataManager.setMTA(dataset, users, mtaList, metadata);

		// Non-null id means the record was inserted.
		Assert.assertTrue(mtaList.get(0).getMtaId() != null && metadata.getDatasetID() != null);

		Debug.println("MTAs added: ");
		Debug.printObjects(IntegrationTestBase.INDENT, mtaList);
		Debug.printObject(IntegrationTestBase.INDENT, metadata);
	}

	@Test
	public void testDeleteMTA() throws Exception {
		List<Mta> mtas = this.genotypicDataManager.getAllMTAs();

		if (mtas != null && !mtas.isEmpty()) {
			List<Integer> datasetIds = new ArrayList<Integer>();
			datasetIds.add(mtas.get(0).getDatasetId());
			this.genotypicDataManager.deleteMTA(datasetIds);
			Debug.println(IntegrationTestBase.INDENT, "datasetId deleted = " + datasetIds);
		}
	}

	@Test
	public void testAddMtaMetadata() throws Exception {
		List<Mta> mtas = this.genotypicDataManager.getAllMTAs();

		if (mtas != null && !mtas.isEmpty()) {
			Mta mta = mtas.get(0);
			MtaMetadata mtaMetadata = new MtaMetadata(mta.getMtaId(), "project", "population", 100, "Thousand");
			this.genotypicDataManager.addMtaMetadata(mtaMetadata);
			Debug.println(IntegrationTestBase.INDENT, "MtaMetadataset added: " + mtaMetadata);
		}
	}

	@Test
	public void testGetMarkerMetadatasetByDatasetId() throws Exception {
		List<MarkerMetadataSet> result = this.genotypicDataManager.getMarkerMetadataSetByDatasetId(this.datasetId);
		Assert.assertTrue(result != null && !result.isEmpty());
		Debug.printObjects(IntegrationTestBase.INDENT, result);
	}

	@Test
	public void testGetCharValuesByMarkerIds() throws Exception {
		List<Integer> markerIds = Arrays.asList(-1, -2);
		List<CharValues> result = this.genotypicDataManager.getCharValuesByMarkerIds(markerIds);
		Debug.printObjects(IntegrationTestBase.INDENT, result);
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
		Boolean addStatus = this.genotypicDataManager.setSNPMarkers(marker, markerAlias, markerDetails, markerUserInfo);
		if (addStatus) {
			Debug.println("ADDED: ");
			Debug.println(IntegrationTestBase.INDENT, marker.toString());
			Debug.println(IntegrationTestBase.INDENT, markerAlias.toString());
			Debug.println(IntegrationTestBase.INDENT, markerDetails.toString());
			Debug.println(IntegrationTestBase.INDENT, markerUserInfo.toString());
		}

		// Then update the newly-inserted set of records
		Integer updateId = (int) (Math.random() * 100);
		marker.setRemarks("UPDATE" + updateId);
		markerAlias.setAlias(markerAlias.getAlias() + updateId);
		markerDetails.setSequence(updateId.toString());
		markerUserInfo.setContactValue(updateId.toString());

		addStatus = this.genotypicDataManager.updateMarkerInfo(marker, markerAlias, markerDetails, markerUserInfo);
		if (addStatus) {
			Debug.println("UPDATED: ");
			Debug.println(IntegrationTestBase.INDENT, marker.toString());
			Debug.println(IntegrationTestBase.INDENT, markerAlias.toString());
			Debug.println(IntegrationTestBase.INDENT, markerDetails.toString());
			Debug.println(IntegrationTestBase.INDENT, markerUserInfo.toString());
		}
	}

	@Test
	public void testUpdateMarkerInfoNewRecords() throws Exception {

		// Add a new set of MarkerAlias, MarkerDetails, MarkerUserInfo for the given Marker - should insert, but update
		List<Object> markerRecords = this.createMarkerRecords();
		Marker markerUA = (Marker) markerRecords.get(0);
		markerUA.setMarkerType(GdmsType.TYPE_UA.getValue());

		Integer markerId = this.genotypicDataManager.addMarker(markerUA);

		Debug.println("MARKER ADDED: " + markerUA);

		MarkerAlias markerAlias = (MarkerAlias) markerRecords.get(1);
		markerAlias.setMarkerId(markerId);
		MarkerDetails markerDetails = (MarkerDetails) markerRecords.get(2);
		markerDetails.setMarkerId(markerId);
		MarkerUserInfo markerUserInfo = (MarkerUserInfo) markerRecords.get(3);
		markerUserInfo.setMarkerId(markerId);

		Boolean addStatus = this.genotypicDataManager.updateMarkerInfo(markerUA, markerAlias, markerDetails, markerUserInfo);
		if (addStatus) {
			Debug.println("MarkerAlias/MarkerDetails/MarkerUserInfo ADDED TO AN EXISTING Marker: ");
			Debug.println(IntegrationTestBase.INDENT, markerUA.toString());
			Debug.println(IntegrationTestBase.INDENT, markerAlias.toString());
			Debug.println(IntegrationTestBase.INDENT, markerDetails.toString());
			Debug.println(IntegrationTestBase.INDENT, markerUserInfo.toString());
		}
	}

	@Test
	public void testUpdateMarkerInfoModifyMarkerNameSpecies() throws Exception {

		List<Object> markerRecords = this.createMarkerRecords();
		MarkerAlias markerAlias = (MarkerAlias) markerRecords.get(1);
		MarkerDetails markerDetails = (MarkerDetails) markerRecords.get(2);
		MarkerUserInfo markerUserInfo = (MarkerUserInfo) markerRecords.get(3);

		Integer updateId = (int) (Math.random() * 100);

		// Try to update marker name - should not be allowed.
		try {
			this.marker.setMarkerName(this.marker.getMarkerName() + updateId);
			this.genotypicDataManager.updateMarkerInfo(this.marker, markerAlias, markerDetails, markerUserInfo);
		} catch (MiddlewareQueryException e) {
			Debug.println("Caught exception: Marker is in central database and cannot be updated.");
			Assert.assertTrue(e.getMessage().contains("Marker is in central database and cannot be updated."));
		}

		// Try to update species - should not be allowed.
		try {
			this.marker.setSpecies(this.marker.getSpecies() + updateId);
			this.genotypicDataManager.updateMarkerInfo(this.marker, markerAlias, markerDetails, markerUserInfo);
		} catch (MiddlewareQueryException e) {
			Debug.println("Caught exception: Marker is in central database and cannot be updated.");
			Assert.assertTrue(e.getMessage().contains("Marker is in central database and cannot be updated."));
		}
	}

	@Test
	public void testUpdateMarkerInfoInCentral() throws Exception {

		// Get an existing marker from local db
		List<Marker> markers = this.genotypicDataManager.getMarkersByIds(Arrays.asList(3316), 0, 1);
		if (markers != null && markers.size() > 0) {
			Marker marker = markers.get(0);
			Debug.println(IntegrationTestBase.INDENT, "Existing marker:" + marker.toString());

			List<Object> markerRecords = this.createMarkerRecords();
			MarkerAlias markerAlias = (MarkerAlias) markerRecords.get(1);
			MarkerDetails markerDetails = (MarkerDetails) markerRecords.get(2);
			MarkerUserInfo markerUserInfo = (MarkerUserInfo) markerRecords.get(3);

			try {
				this.genotypicDataManager.updateMarkerInfo(marker, markerAlias, markerDetails, markerUserInfo);
			} catch (MiddlewareQueryException e) {
				Debug.println(IntegrationTestBase.INDENT, "Marker is in central database and cannot be updated.");
				Assert.assertTrue(e.getMessage().contains("Marker is in central database and cannot be updated."));
			}
		} else {
			Debug.println(IntegrationTestBase.INDENT, "No marker data found in the database to update.");
		}
	}

	@Test
	public void testGetMarkerByType() throws Exception {
		List<ExtendedMarkerInfo> markers = this.genotypicDataManager.getMarkerInfoDataByMarkerType("SSR");
		Assert.assertNotNull(markers);

		Assert.assertTrue(markers.size() != 0);
	}

	@Test
	public void testGetMarkersLikeMarkerName() throws Exception {
		List<ExtendedMarkerInfo> markers = this.genotypicDataManager.getMarkerInfoDataLikeMarkerName("ga");
		Assert.assertNotNull(markers);

		Assert.assertTrue(markers.size() != 0);
	}

	@Test
	public void testGetMarkerInfosByMarkerNames() throws Exception {
		Marker marker1 = this.createMarker();
		marker1.setMarkerType(GdmsType.TYPE_SNP.getValue());
		marker1.setMarkerName("GA1");
		this.genotypicDataManager.addMarker(marker1);

		Marker marker2 = this.createMarker();
		marker2.setMarkerType(GdmsType.TYPE_SNP.getValue());
		marker2.setMarkerName("GA2");
		this.genotypicDataManager.addMarker(marker2);

		List<String> paramList = new ArrayList<String>();
		paramList.add("GA1");
		paramList.add("GA2");
		List<ExtendedMarkerInfo> markers = this.genotypicDataManager.getMarkerInfoByMarkerNames(paramList);
		Assert.assertNotNull(markers);

		Assert.assertTrue(markers.size() > 0);
	}

}
