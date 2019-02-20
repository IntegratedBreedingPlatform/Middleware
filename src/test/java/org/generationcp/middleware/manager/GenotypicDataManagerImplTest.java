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
		final List<Integer> nameIds = Arrays.asList(1, 2, 3, -1, -2, -3);
		// For test data, get gids from LOCAL and CENTRAL: SELECT * FROM names where nid in (:nidList)

		final List<Name> results = this.genotypicDataManager.getNamesByNameIds(nameIds);
		Debug.println("testGetNamesByNameIds(" + nameIds + ") RESULTS: " + results.size() + " = " + results);
	}

	@Test
	public void testGetMapNameByMapId() throws Exception {
		final String expectedName = "RIL-1 (TAG 24 x ICGV 86031)";

		final String retrievedName = this.genotypicDataManager.getMapNameById(this.mapId);
		Assert.assertEquals(expectedName, retrievedName);
	}

	@Test
	public void testGetNameByNameId() throws Exception {
		final Integer nameId = -1;
		final Name name = this.genotypicDataManager.getNameByNameId(nameId);
		Debug.println("testGetNameByNameId(nameId=" + nameId + ") RESULTS: " + name);
	}

	@Test
	public void testGetFirstFiveMaps() throws Exception {
		final List<Map> maps = this.genotypicDataManager.getAllMaps(0, 5);
		Debug.println("RESULTS (testGetFirstFiveMaps): " + maps);
	}

	@Test
	public void testGetMapInfoByMapName() throws Exception {
		final String mapName = "RIL-8 (Yueyou13 x J 11)"; // CENTRAL test data
		final List<MapInfo> results = this.genotypicDataManager.getMapInfoByMapName(mapName);
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testGetMapInfoByMapNameBothDB() throws Exception {
		final String mapName = "GCP-833TestMap"; // LOCAL test data
		final List<MapInfo> results = this.genotypicDataManager.getMapInfoByMapName(mapName);
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testgetMarkerInfoByMarkerIds() throws Exception {
		final List<Integer> markerIds = Arrays.asList(1, -1, 2, -2, 3, 4, 5);
		final List<MarkerInfo> results = this.genotypicDataManager.getMarkerInfoByMarkerIds(markerIds);
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testGetMapInfoByMapAndChromosome() throws Exception {
		// Replace with a gdms_markers_onmap.map_id, linkage_group in local
		final int mapId = -2; // 1;
		final String chromosome = "LG23"; // "BC-1_b11";

		final List<MapInfo> results = this.genotypicDataManager.getMapInfoByMapAndChromosome(mapId, chromosome);
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testGetMapInfoByMapChromosomeAndPosition() throws Exception {
		// Replace with a gdms_markers_onmap.map_id, linkage_group, start_position in local
		final int mapId = -2;
		final String chromosome = "LG23";
		final float startPosition = 123.4f;

		final List<MapInfo> results = this.genotypicDataManager.getMapInfoByMapChromosomeAndPosition(mapId, chromosome, startPosition);
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testGetMapInfoByMarkersAndMap() throws Exception {
		final int mapId = -2; // Replace with a gdms_markers_onmap.map_id in local
		final List<Integer> markerList = new ArrayList<Integer>();
		markerList.add(-4); // Replace with a (-) gdms_markers_onmap.marker_id in local
		markerList.add(3407); // Replace with a (+) gdms_markers_onmap.marker_id in central

		final List<MapInfo> results = this.genotypicDataManager.getMapInfoByMarkersAndMap(markerList, mapId);
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testGetMarkerOnMaps() throws Exception {
		final List<Integer> mapIds = Arrays.asList(-2, 2, 3, 4);
		final String linkageGroup = "LG23";
		final double startPos = 0d;
		final double endPos = 100d;

		final List<MarkerOnMap> markersOnMap = this.genotypicDataManager.getMarkerOnMaps(mapIds, linkageGroup, startPos, endPos);

		Debug.printObjects(IntegrationTestBase.INDENT, markersOnMap);
	}

	@Test
	public void testGetMarkersOnMapByMarkerIds() throws Exception {
		final List<Integer> markerIds = Arrays.asList(-1, -2, -6, 153, 994, 1975);
		final List<MarkerOnMap> markersOnMap = this.genotypicDataManager.getMarkersOnMapByMarkerIds(markerIds);
		Debug.printObjects(IntegrationTestBase.INDENT, markersOnMap);
	}

	@Test
	public void testGetAllMarkerNamesFromMarkersOnMap() throws Exception {
		final List<String> markerNames = this.genotypicDataManager.getAllMarkerNamesFromMarkersOnMap();
		Debug.println(IntegrationTestBase.INDENT, "Marker names (" + markerNames.size() + "): " + markerNames);
	}

	@Test
	public void testCountDatasetNames() throws Exception {
		final long results = this.genotypicDataManager.countDatasetNames();
		Debug.println("testCountDatasetNames(Database.LOCAL) RESULTS: " + results);
	}

	@Test
	public void testGetDatasetNames() throws Exception {
		final List<String> result = this.genotypicDataManager.getDatasetNames(0, Integer.MAX_VALUE);
		Debug.printObjects(0, result);
	}

	@Test
	public void testGetDatasetNamesByQtlId() throws Exception {
		final Integer qtlId = 1;
		final List<String> results = this.genotypicDataManager.getDatasetNamesByQtlId(qtlId, 0, 5);
		Debug.println("testGetDatasetNamesByQtlId(0,5) RESULTS: " + results);
	}

	@Test
	public void testCountDatasetNamesByQtlId() throws Exception {
		final Integer qtlId = 1;
		final long results = this.genotypicDataManager.countDatasetNamesByQtlId(qtlId);
		Debug.println("testCountDatasetNamesByQtlId() RESULTS: " + results);
	}

	@Test
	public void testGetDatasetDetailsByDatasetName() throws Exception {
		final List<DatasetElement> results =
			this.genotypicDataManager.getDatasetDetailsByDatasetName(this.dataset.getDatasetName());
		Debug.println("testGetDatasetDetailsByDatasetName(" + this.dataset.getDatasetName() + ",LOCAL) RESULTS: " + results);
	}

	@Test
	public void testGetMarkersByMarkerNames() throws Exception {
		final Marker marker1 = this.createMarker();
		marker1.setMarkerType(GdmsType.TYPE_SNP.getValue());
		marker1.setMarkerName("GA101");
		this.genotypicDataManager.addMarker(marker1);

		final Marker marker2 = this.createMarker();
		marker2.setMarkerType(GdmsType.TYPE_SNP.getValue());
		marker2.setMarkerName("GA110");
		this.genotypicDataManager.addMarker(marker2);

		final Marker marker3 = this.createMarker();
		marker3.setMarkerType(GdmsType.TYPE_SNP.getValue());
		marker3.setMarkerName("GA122");
		this.genotypicDataManager.addMarker(marker3);

		final List<String> markerNames = new ArrayList<String>();
		markerNames.add("GA101");
		markerNames.add("GA110");
		markerNames.add("GA122");
		final List<Marker> markers = this.genotypicDataManager.getMarkersByMarkerNames(markerNames, 0, 100);
		Assert.assertTrue(markers.size() > 0);
		Debug.printObjects(IntegrationTestBase.INDENT, markers);
	}

	@Test
	public void testGetMarkerIdsByDatasetId() throws Exception {
		final Integer datasetId = Integer.valueOf(2);
		final List<Integer> results = this.genotypicDataManager.getMarkerIdsByDatasetId(datasetId);
		Debug.println("testGetMarkerIdByDatasetId(" + datasetId + ") RESULTS: " + results);
	}

	@Test
	public void testGetParentsByDatasetId() throws Exception {
		final Integer datasetId = Integer.valueOf(2);
		final List<ParentElement> results = this.genotypicDataManager.getParentsByDatasetId(datasetId);
		Debug.println("testGetParentsByDatasetId(" + datasetId + ") RESULTS: " + results);
	}

	@Test
	public void testGetMarkerTypesByMarkerIds() throws Exception {
		final List<Integer> markerIds = Arrays.asList(1, 2, 3, 4, 5, -1, -2, -3);
		final List<String> results = this.genotypicDataManager.getMarkerTypesByMarkerIds(markerIds);
		Debug.println("testGetMarkerTypeByMarkerIds(" + markerIds + ") RESULTS: " + results);
	}

	@Test
	public void testGetMarkerNamesByGIds() throws Exception {
		final List<Integer> gIds = Arrays.asList(1920, 1895, 1434);
		// For test data, SELECT marker_id, gid from gdms_allele_values / gdms_char_values / gdms_mapping_pop_values

		final List<MarkerNameElement> results = this.genotypicDataManager.getMarkerNamesByGIds(gIds);
		Debug.println("testGetMarkerNamesByGIds(" + gIds + ") RESULTS: " + results.size() + " = " + results);
	}

	@Test
	public void testGetGermplasmNamesByMarkerNames() throws Exception {
		final List<String> markerNames = Arrays.asList("1_0001", "1_0007", "1_0013");

		final List<GermplasmMarkerElement> results = this.genotypicDataManager.getGermplasmNamesByMarkerNames(markerNames);
		Debug.println("testGetGermplasmNamesByMarkerNames(" + markerNames + ")  RESULTS: " + results);
	}

	@Test
	public void testGetMappingValuesByGidsAndMarkerNames() throws Exception {
		final List<Integer> gids = Arrays.asList(2212, 2208, 1, -1);
		final List<String> markerNames = Arrays.asList("Ah26", "AC2C05", "Ah193", "Seq14H06", "SeqTEST843", "GA1");

		// For test input, please run the following in central/local, and choose select gid / marker_ids
		// SELECT DISTINCT gid, gdms_mapping_pop_values.marker_id
		// FROM gdms_mapping_pop_values
		// JOIN gdms_mapping_pop ON gdms_mapping_pop_values.dataset_id = gdms_mapping_pop.dataset_id
		// LEFT JOIN gdms_marker ON gdms_mapping_pop_values.marker_id = gdms_marker.marker_id;

		final List<MappingValueElement> mappingValues =
			this.genotypicDataManager.getMappingValuesByGidsAndMarkerNames(gids, markerNames, 0, 100);
		Debug.println("testGetMappingValuesByGidsAndMarkerNames(" + gids + ", " + markerNames + "): ");
		Debug.printFormattedObjects(IntegrationTestBase.INDENT, mappingValues);
	}

	@Test
	public void testGetAllelicValuesByGidsAndMarkerNames() throws Exception {
		final List<String> markerNames = Arrays.asList("RN18H05");
		final List<Integer> gids = Arrays.asList(7, 1035);
		final List<AllelicValueElement> allelicValues = this.genotypicDataManager.getAllelicValuesByGidsAndMarkerNames(gids, markerNames);
		Debug.printObjects(allelicValues);
	}

	@Test
	public void testGetAllelicValuesByGidsAndMarkerNamesForGDMS() throws Exception {

		final List<String> markerNames = Arrays.asList("TC03A12", "Seq19E09", "TC7E04", "SeqTEST"); // marker id = 3295, 3296, 1044;
		final List<Integer> gids = Arrays.asList(1434, 1435, 1);

		this.testAddGDMSMarker(); // Local test data: add Marker with markerName = "SeqTEST"

		final List<AllelicValueElement> allelicValues = this.genotypicDataManager.getAllelicValuesByGidsAndMarkerNames(gids, markerNames);
		Debug.println("testGetAllelicValuesByGidsAndMarkerNamesForGDMS(" + gids + ", " + markerNames + ") RESULTS: ");
		Debug.printFormattedObjects(IntegrationTestBase.INDENT, allelicValues);
	}

	@Test
	public void testGetAllelicValuesByGid() throws Exception {
		final Integer gid = 7;
		final List<AllelicValueElement> allelicValues = this.genotypicDataManager.getAllelicValuesByGid(gid);
		Debug.printObjects(allelicValues);
	}

	@Test
	public void testGetAllelicValuesFromAlleleValuesByDatasetId() throws Exception {
		final Integer datasetId = Integer.valueOf(2);
		final long count = this.genotypicDataManager.countAllelicValuesFromAlleleValuesByDatasetId(datasetId);
		final List<AllelicValueWithMarkerIdElement> allelicValues =
			this.genotypicDataManager.getAllelicValuesFromAlleleValuesByDatasetId(datasetId, 0, (int) count);
		Debug.println("testGetAllelicValuesFromAlleleValuesByDatasetId(dataset=" + datasetId + ") RESULTS: " + allelicValues.size());
		Debug.printObjects(allelicValues);
	}

	@Test
	public void testGetAllelicValuesFromMappingPopValuesByDatasetId() throws Exception {
		final Integer datasetId = Integer.valueOf(3);
		final long count = this.genotypicDataManager.countAllelicValuesFromMappingPopValuesByDatasetId(datasetId);
		// manager.countAllelicValuesFromCharValuesByDatasetId(datasetId);
		final List<AllelicValueWithMarkerIdElement> allelicValues =
			this.genotypicDataManager.getAllelicValuesFromMappingPopValuesByDatasetId(datasetId, 0, (int) count);
		Debug.println("testGetAllelicValuesFromMappingPopValuesByDatasetId(" + datasetId + ") RESULTS: ");
		Debug.printObjects(IntegrationTestBase.INDENT, allelicValues);
	}

	@Test
	public void testGetMarkerNamesByMarkerIds() throws Exception {
		final List<Integer> markerIds = Arrays.asList(1, -1, 2, -2, 3, -3, 4, -4, 5, -5);
		final List<MarkerIdMarkerNameElement> markerNames = this.genotypicDataManager.getMarkerNamesByMarkerIds(markerIds);
		Debug.println("testGetMarkerNamesByMarkerIds(" + markerIds + ") RESULTS: ");
		Debug.printObjects(IntegrationTestBase.INDENT, markerNames);
	}

	@Test
	public void testGetAllMarkerTypes() throws Exception {
		final List<String> markerTypes = this.genotypicDataManager.getAllMarkerTypes(0, 10);
		Debug.println("testGetAllMarkerTypes(0,10) RESULTS: " + markerTypes);
	}

	@Test
	public void testCountAllMarkerTypesLocal() throws Exception {
		final long result = this.genotypicDataManager.countAllMarkerTypes();
		Debug.println("testCountAllMarkerTypes(Database.LOCAL) RESULTS: " + result);
	}

	@Test
	public void testCountAllMarkerTypesCentral() throws Exception {
		final long result = this.genotypicDataManager.countAllMarkerTypes();
		Debug.println("testCountAllMarkerTypes(Database.CENTRAL) RESULTS: " + result);
	}

	@Test
	public void testGetMarkerNamesByMarkerType() throws Exception {
		final String markerType = "asdf";
		final List<String> markerNames = this.genotypicDataManager.getMarkerNamesByMarkerType(markerType, 0, 10);
		Debug.println("testGetMarkerNamesByMarkerType(" + markerType + ") RESULTS: " + markerNames);
	}

	@Test
	public void testCountMarkerNamesByMarkerType() throws Exception {
		final String markerType = "asdf";
		final long result = this.genotypicDataManager.countMarkerNamesByMarkerType(markerType);
		Debug.println("testCountMarkerNamesByMarkerType(" + markerType + ") RESULTS: " + result);
	}

	@Test
	public void testGetMarkerInfoByMarkerName() throws Exception {
		final String markerName = "1_0437";
		final long count = this.genotypicDataManager.countMarkerInfoByMarkerName(markerName);
		Debug.println("countMarkerInfoByMarkerName(" + markerName + ")  RESULTS: " + count);
		final List<MarkerInfo> results = this.genotypicDataManager.getMarkerInfoByMarkerName(markerName, 0, (int) count);
		Debug.println("testGetMarkerInfoByMarkerName(" + markerName + ") RESULTS: " + results);
	}

	@Test
	public void testGetMarkerInfoByGenotype() throws Exception {
		final String genotype = "";
		final long count = this.genotypicDataManager.countMarkerInfoByGenotype(genotype);
		Debug.println("countMarkerInfoByGenotype(" + genotype + ") RESULTS: " + count);
		final List<MarkerInfo> results = this.genotypicDataManager.getMarkerInfoByGenotype(genotype, 0, (int) count);
		Debug.println("testGetMarkerInfoByGenotype(" + genotype + ") RESULTS: " + results);
	}

	@Test
	public void testGetMarkerInfoByDbAccessionId() throws Exception {
		final String dbAccessionId = "";
		final long count = this.genotypicDataManager.countMarkerInfoByDbAccessionId(dbAccessionId);
		Debug.println("countMarkerInfoByDbAccessionId(" + dbAccessionId + ")  RESULTS: " + count);
		final List<MarkerInfo> results = this.genotypicDataManager.getMarkerInfoByDbAccessionId(dbAccessionId, 0, (int) count);
		Debug.println("testGetMarkerInfoByDbAccessionId(" + dbAccessionId + ")  RESULTS: " + results);
	}

	@Test
	public void testGetGidsFromCharValuesByMarkerId() throws Exception {
		final Integer markerId = 1;
		final List<Integer> gids = this.genotypicDataManager.getGIDsFromCharValuesByMarkerId(markerId, 0, 10);
		Debug.println("testGetGidsFromCharValuesByMarkerId(" + markerId + ") RESULTS: " + gids);
	}

	@Test
	public void testCountGidsFromCharValuesByMarkerId() throws Exception {
		final Integer markerId = 1;
		final long result = this.genotypicDataManager.countGIDsFromCharValuesByMarkerId(markerId);
		Debug.println("testCountGidsFromCharValuesByMarkerId(" + markerId + ") RESULTS: " + result);
	}

	@Test
	public void testGetGidsFromAlleleValuesByMarkerId() throws Exception {
		final Integer markerId = 1;
		final List<Integer> gids = this.genotypicDataManager.getGIDsFromAlleleValuesByMarkerId(markerId, 0, 10);
		Debug.println("testGetGidsFromAlleleValuesByMarkerId(" + markerId + ") RESULTS: " + gids);
	}

	@Test
	public void testCountGidsFromAlleleValuesByMarkerId() throws Exception {
		final Integer markerId = 1;
		final long result = this.genotypicDataManager.countGIDsFromCharValuesByMarkerId(markerId);
		Debug.println("testCountGidsFromAlleleValuesByMarkerId(" + markerId + ") RESULTS: " + result);
	}

	@Test
	public void testGetGidsFromMappingPopValuesByMarkerId() throws Exception {
		final Integer markerId = 1;
		final List<Integer> gids = this.genotypicDataManager.getGIDsFromMappingPopValuesByMarkerId(markerId, 0, 10);
		Debug.println("testGetGidsFromMappingPopValuesByMarkerId(" + markerId + ") RESULTS: " + gids);
	}

	@Test
	public void testGetGidsByMarkersAndAlleleValues() throws Exception {
		final List<Integer> markerIdList = Arrays.asList(2325);
		final List<String> alleleValueList = Arrays.asList("238/238");
		final List<Integer> values = this.genotypicDataManager.getGidsByMarkersAndAlleleValues(markerIdList, alleleValueList);
		Debug.printObjects(IntegrationTestBase.INDENT, values);
	}

	@Test
	public void testCountGidsFromMappingPopValuesByMarkerId() throws Exception {
		final Integer markerId = 1;
		final long result = this.genotypicDataManager.countGIDsFromMappingPopValuesByMarkerId(markerId);
		Debug.println("testCountGidsFromMappingPopValuesByMarkerId(" + markerId + ") RESULTS: " + result);
	}

	@Test
	public void testGetAllDbAccessionIdsFromMarker() throws Exception {
		final List<String> dbAccessionIds = this.genotypicDataManager.getAllDbAccessionIdsFromMarker(0, 10);
		Debug.println("testGetAllDbAccessionIdsFromMarker(1,10) RESULTS: " + dbAccessionIds);
	}

	@Test
	public void testCountAllDbAccessionIdsFromMarker() throws Exception {
		final long result = this.genotypicDataManager.countAllDbAccessionIdsFromMarker();
		Debug.println("testCountAllDbAccessionIdsFromMarker() RESULTS: " + result);
	}

	@Test
	public void testGetAccMetadatasetsByDatasetIds() throws Exception {
		final List<Integer> datasetIds = Arrays.asList(-1, -2, -3, 1, 2, 3);
		final List<Integer> gids = Arrays.asList(-2);

		final List<AccMetadataSet> nids = this.genotypicDataManager.getAccMetadatasetsByDatasetIds(datasetIds, 0, 10);

		Debug.println("testGgetAccMetadatasetByDatasetIds: " + nids);
	}

	@Test
	public void testGetDatasetIdsForFingerPrinting() throws Exception {
		final List<Integer> datasetIds =
			this.genotypicDataManager.getDatasetIdsForFingerPrinting(
				0,
				(int) this.genotypicDataManager.countDatasetIdsForFingerPrinting());
		Debug.println("testGetDatasetIdsForFingerPrinting() RESULTS: " + datasetIds);
	}

	@Test
	public void testCountDatasetIdsForFingerPrinting() throws Exception {
		final long count = this.genotypicDataManager.countDatasetIdsForFingerPrinting();
		Debug.println("testCountDatasetIdsForFingerPrinting() RESULTS: " + count);
	}

	@Test
	public void testGetDatasetIdsForMapping() throws Exception {
		final List<Integer> datasetIds =
			this.genotypicDataManager.getDatasetIdsForMapping(0, (int) this.genotypicDataManager.countDatasetIdsForMapping());
		Debug.println("testGetDatasetIdsForMapping() RESULTS: " + datasetIds);
	}

	@Test
	public void testCountDatasetIdsForMapping() throws Exception {
		final long count = this.genotypicDataManager.countDatasetIdsForMapping();
		Debug.println("testCountDatasetIdsForMapping() RESULTS: " + count);
	}

	@Test
	public void testGetGdmsAccMetadatasetByGid() throws Exception {
		final List<Integer> germplasmIds = Arrays.asList(1, -1, -2);
		final List<AccMetadataSet> accMetadataSets =
			this.genotypicDataManager.getGdmsAccMetadatasetByGid(germplasmIds, 0,
				(int) this.genotypicDataManager.countGdmsAccMetadatasetByGid(germplasmIds));
		Debug.println("testGetGdmsAccMetadatasetByGid() RESULTS: ");
		Debug.printObjects(IntegrationTestBase.INDENT, accMetadataSets);
	}

	@Test
	public void testCountGdmsAccMetadatasetByGid() throws Exception {
		final List<Integer> germplasmIds = Arrays.asList(956, 1042, -2213, -2215);
		final long count = this.genotypicDataManager.countGdmsAccMetadatasetByGid(germplasmIds);
		Debug.println("testCountGdmsAccMetadatasetByGid() RESULTS: " + count);
	}

	@Test
	public void testGetMarkersByGidAndDatasetIds() throws Exception {
		final Integer gid = Integer.valueOf(-2215);
		final List<Integer> datasetIds = Arrays.asList(1, 2);
		final List<Integer> markerIds =
			this.genotypicDataManager.getMarkersBySampleIdAndDatasetIds(gid, datasetIds, 0,
				(int) this.genotypicDataManager.countMarkersBySampleIdAndDatasetIds(gid, datasetIds));
		Debug.println("testGetMarkersByGidAndDatasetIds() RESULTS: " + markerIds);
	}

	@Test
	public void testCountMarkersByGidAndDatasetIds() throws Exception {
		final Integer gid = Integer.valueOf(-2215);
		final List<Integer> datasetIds = Arrays.asList(1, 2);
		final long count = this.genotypicDataManager.countMarkersBySampleIdAndDatasetIds(gid, datasetIds);
		Debug.println("testCountGdmsAccMetadatasetByGid() RESULTS: " + count);
	}

	@Test
	public void testCountAlleleValuesByGids() throws Exception {
		final List<Integer> germplasmIds = Arrays.asList(956, 1042, -2213, -2215);
		final long count = this.genotypicDataManager.countAlleleValuesByGids(germplasmIds);
		Debug.println("testCountAlleleValuesByGids() RESULTS: " + count);
	}

	@Test
	public void testGetIntAlleleValuesForPolymorphicMarkersRetrieval() throws Exception {
		// For test data, get gids from LOCAL and CENTRAL: SELECT distinct FROM gdms_allele_values;
		final List<Integer> germplasmIds = Arrays.asList(1, 956, 1042, -2213, -2215);

		final List<AllelicValueElement> results =
			this.genotypicDataManager.getIntAlleleValuesForPolymorphicMarkersRetrieval(germplasmIds, 0, Integer.MAX_VALUE);
		Debug.println("testGetIntAlleleValuesForPolymorphicMarkersRetrieval() RESULTS: ");
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testCountIntAlleleValuesForPolymorphicMarkersRetrieval() throws Exception {
		// For test data, get gids from LOCAL and CENTRAL: SELECT distinct FROM gdms_allele_values;
		final List<Integer> germplasmIds = Arrays.asList(1, 956, 1042, -2213, -2215);
		final long count = this.genotypicDataManager.countIntAlleleValuesForPolymorphicMarkersRetrieval(germplasmIds);
		Debug.println("testCountIntAlleleValuesForPolymorphicMarkersRetrieval() RESULTS: " + count);
	}

	@Test
	public void testGetCharAlleleValuesForPolymorphicMarkersRetrieval() throws Exception {
		// For test data, get gids from LOCAL and CENTRAL: SELECT distinct FROM gdms_char_values;
		final List<Integer> germplasmIds = Arrays.asList(1, 956, 1042, -2213, -2215);
		final List<AllelicValueElement> results =
			this.genotypicDataManager.getCharAlleleValuesForPolymorphicMarkersRetrieval(germplasmIds, 0, Integer.MAX_VALUE);
		Debug.println("testGetIntAlleleValuesForPolymorphicMarkersRetrieval() RESULTS: ");
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testCountCharAlleleValuesForPolymorphicMarkersRetrieval() throws Exception {
		// For test data, get gids from LOCAL and CENTRAL: SELECT distinct FROM gdms_char_values;
		final List<Integer> germplasmIds = Arrays.asList(1, 956, 1042, -2213, -2215);
		final long count = this.genotypicDataManager.countCharAlleleValuesForPolymorphicMarkersRetrieval(germplasmIds);
		Debug.println("testCountCharAlleleValuesForPolymorphicMarkersRetrieval() RESULTS: " + count);
	}

	@Test
	public void testGetMappingAlleleValuesForPolymorphicMarkersRetrieval() throws Exception {
		// For test data, get gids from LOCAL and CENTRAL: SELECT distinct FROM gdms_mapping_pop_values;
		final List<Integer> germplasmIds = Arrays.asList(1, 1434, 1435);
		final List<AllelicValueElement> results =
			this.genotypicDataManager.getMappingAlleleValuesForPolymorphicMarkersRetrieval(germplasmIds, 0, Integer.MAX_VALUE);
		Debug.println("testGetMappingAlleleValuesForPolymorphicMarkersRetrieval() RESULTS: ");
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testCountMappingAlleleValuesForPolymorphicMarkersRetrieval() throws Exception {
		// For test data, get gids from LOCAL and CENTRAL: SELECT distinct FROM gdms_mapping_pop_values;
		final List<Integer> germplasmIds = Arrays.asList(1, 1434, 1435);
		final long count = this.genotypicDataManager.countMappingAlleleValuesForPolymorphicMarkersRetrieval(germplasmIds);
		Debug.println("testCountMappingAlleleValuesForPolymorphicMarkersRetrieval() RESULTS: " + count);
	}

	@Test
	public void testCountNIdsByDatasetIdsAndMarkerIdsAndNotGIds() throws Exception {
		final List<Integer> gIds = Arrays.asList(29, 303, 4950);
		final List<Integer> datasetIds = Arrays.asList(2);
		final List<Integer> markerIds = Arrays.asList(6803);
		final int count = this.genotypicDataManager.countNIdsByMarkerIdsAndDatasetIdsAndNotGIds(datasetIds, markerIds, gIds);
		Debug.println("testcCountNIdsByDatasetIdsAndMarkerIdsAndNotGIds() RESULTS: " + count);
	}

	@Test
	public void testGetNIdsByDatasetIdsAndMarkerIds() throws Exception {
		final List<Integer> datasetIds = Arrays.asList(2);
		final List<Integer> markerIds = Arrays.asList(6803);
		final List<Integer> nIdList =
			this.genotypicDataManager.getNIdsByMarkerIdsAndDatasetIds(datasetIds, markerIds, 0,
				this.genotypicDataManager.countNIdsByMarkerIdsAndDatasetIds(datasetIds, markerIds));
		Debug.println("testGetNIdsByDatasetIdsAndMarkerIds() RESULTS: " + nIdList);
	}

	@Test
	public void testCountNIdsByDatasetIdsAndMarkerIds() throws Exception {
		final List<Integer> datasetIds = Arrays.asList(2);
		final List<Integer> markerIds = Arrays.asList(6803);
		final int count = this.genotypicDataManager.countNIdsByMarkerIdsAndDatasetIds(datasetIds, markerIds);
		Debug.println("testCountNIdsByDatasetIdsAndMarkerIds() RESULTS: " + count);
	}

	@Test
	public void testGetAllQtl() throws Exception {
		final List<Qtl> qtls = this.genotypicDataManager.getAllQtl(0, (int) this.genotypicDataManager.countAllQtl());
		Debug.println("testGetAllQtl() RESULTS: ");
		Debug.printObjects(IntegrationTestBase.INDENT, qtls);
	}

	@Test
	public void testCountAllQtl() throws Exception {
		final long result = this.genotypicDataManager.countAllQtl();
		Debug.println("testCountAllQtl() RESULTS: " + result);
	}

	@Test
	public void testCountQtlIdByName() throws Exception {
		final String qtlName = "SLA%";
		final long count = this.genotypicDataManager.countQtlIdByName(qtlName);
		Debug.println("testCountQtlIdByName() RESULTS: " + count);
	}

	@Test
	public void testGetQtlIdByName() throws Exception {
		final String qtlName = "TWW09_AhV";

		final List<Integer> results =
			this.genotypicDataManager.getQtlIdByName(qtlName, 0, (int) this.genotypicDataManager.countQtlIdByName(qtlName));
		Debug.println("testGetQtlIdByName() RESULTS: " + results);
	}

	@Test
	public void testGetQtlByNameFromCentral() throws Exception {
		final String qtlName = "TestQTL%";
		final List<QtlDetailElement> results =
			this.genotypicDataManager.getQtlByName(qtlName, 0, (int) this.genotypicDataManager.countQtlByName(qtlName));
		Assert.assertTrue(results.size() > 0);
		Debug.printFormattedObjects(IntegrationTestBase.INDENT, results);
		Debug.println("testGetQtlByNameFromCentral() #records: " + results.size());
	}

	@Test
	public void testCountQtlByName() throws Exception {
		final String qtlName = "TestQTL%";
		final long count = this.genotypicDataManager.countQtlByName(qtlName);
		Assert.assertTrue(count > 0);
		Debug.println("testCountQtlByName() RESULTS: " + count);
	}

	@Test
	public void testgetQtlNamesByQtlIds() throws Exception {
		final List<Integer> qtlIds = Arrays.asList(1, 2, 3, -4, -5);
		final java.util.Map<Integer, String> qtlNames = this.genotypicDataManager.getQtlNamesByQtlIds(qtlIds);
		Assert.assertTrue(qtlNames.size() > 0);
		for (int i = 0; i < qtlIds.size(); i++) {
			Debug.println(IntegrationTestBase.INDENT, "QTL ID = " + qtlIds.get(i) + " : QTL NAME = " + qtlNames.get(qtlIds.get(i)));
		}
	}

	@Test
	public void testGetQtlByTrait() throws Exception {
		final Integer qtlTraitId = 1001; // "SL%";
		final List<Integer> results =
			this.genotypicDataManager.getQtlByTrait(qtlTraitId, 0, (int) this.genotypicDataManager.countQtlByTrait(qtlTraitId));
		Debug.println("testGetQtlByTrait() RESULTS: " + results);
	}

	@Test
	public void testCountQtlByTrait() throws Exception {
		final Integer qtlTraitId = 1001; // "SL%";
		final long count = this.genotypicDataManager.countQtlByTrait(qtlTraitId);
		Debug.println("testCountQtlByTrait() RESULTS: " + count);
	}

	@Test
	public void testGetQtlTraitsByDatasetId() throws Exception {
		final Integer datasetId = 7;
		final List<Integer> results =
			this.genotypicDataManager.getQtlTraitsByDatasetId(datasetId, 0,
				(int) this.genotypicDataManager.countQtlTraitsByDatasetId(datasetId));
		Debug.println("testGetQtlTraitsByDatasetId() RESULTS: " + results);
	}

	@Test
	public void testCountQtlTraitsByDatasetId() throws Exception {
		final Integer datasetId = 7;
		final long count = this.genotypicDataManager.countQtlTraitsByDatasetId(datasetId);
		Debug.println("testCountQtlTraitsByDatasetId() RESULTS: " + count);
	}

	@Test
	public void testGetMapIdsByQtlName() throws Exception {
		final String qtlName = "HI Control 08_AhI";
		final List<Integer> results =
			this.genotypicDataManager.getMapIdsByQtlName(qtlName, 0, (int) this.genotypicDataManager.countMapIdsByQtlName(qtlName));
		Debug.println("testGetMapIdsByQtlName() RESULTS: " + results);
	}

	@Test
	public void testCountMapIdsByQtlName() throws Exception {
		final String qtlName = "HI Control 08_AhI";
		final long count = this.genotypicDataManager.countMapIdsByQtlName(qtlName);
		Debug.println("testCountMapIdsByQtlName() RESULTS: " + count);
	}

	@Test
	public void testGetMarkersByQtl() throws Exception {
		final String qtlName = "HI Control 08_AhI";
		final String chromosome = "RIL-3 _LG01";
		final float min = 0.0f;
		final float max = 10f;
		final List<Integer> results =
			this.genotypicDataManager.getMarkerIdsByQtl(qtlName, chromosome, min, max, 0,
				(int) this.genotypicDataManager.countMarkerIdsByQtl(qtlName, chromosome, min, max));
		Assert.assertTrue(results.size() > 0);
		Debug.println("testGetMarkersByQtl() RESULTS: " + results);
	}

	@Test
	public void testCountMarkersByQtl() throws Exception {
		final String qtlName = "HI Control 08_AhI";
		final String chromosome = "RIL-3 _LG01";
		final float min = 0f;
		final float max = 10f;
		final long count = this.genotypicDataManager.countMarkerIdsByQtl(qtlName, chromosome, min, max);
		Assert.assertTrue(count > 0);
		Debug.println("testCountMarkersByQtl() RESULTS: " + count);
	}

	@Test
	public void getAllParentsFromMappingPopulation() throws Exception {
		final int start = 0;
		final int end = 10;
		final List<ParentElement> parentElements = this.genotypicDataManager.getAllParentsFromMappingPopulation(start, end);
		Debug.println("getAllParentsFromMappingPopulation(" + start + "," + end + ")");
		for (final ParentElement parentElement : parentElements) {
			Debug.println(IntegrationTestBase.INDENT, "Parent A NId: " + parentElement.getParentANId() + "  |  Parent B NId: "
				+ parentElement.getParentBGId());
		}
	}

	@Test
	public void countAllParentsFromMappingPopulation() throws Exception {
		final long parentElementsCount = this.genotypicDataManager.countAllParentsFromMappingPopulation();
		Debug.println("countAllParentsFromMappingPopulation()");
		Debug.println("Count: " + parentElementsCount);
	}

	@Test
	public void getMapDetailsByName() throws Exception {
		final String nameLike = "tag%";
		final int start = 0;
		final int end = 10;
		final List<MapDetailElement> maps = this.genotypicDataManager.getMapDetailsByName(nameLike, start, end);
		Debug.println("getMapDetailsByName('" + nameLike + "'," + start + "," + end + ")");
		for (final MapDetailElement map : maps) {
			Debug.println("Map: " + map.getMarkerCount() + "  |  maxStartPosition: " + map.getMaxStartPosition() + "  |  linkageGroup: "
				+ map.getLinkageGroup() + "  |  mapName: " + map.getMapName() + "  |  mapType:  " + map.getMapType());
		}
	}

	@Test
	public void countMapDetailsByName() throws Exception {
		final String nameLike = "tag%";
		final Long parentElementsCount = this.genotypicDataManager.countMapDetailsByName(nameLike);
		Debug.println("countMapDetailsByName('" + nameLike + "')");
		Debug.println("Count: " + parentElementsCount);
	}

	@Test
	public void testGetAllMapDetails() throws Exception {
		final int start = 0;
		final int end = (int) this.genotypicDataManager.countAllMapDetails();
		final List<MapDetailElement> maps = this.genotypicDataManager.getAllMapDetails(start, end);
		Debug.println("testGetAllMapDetails(" + start + "," + end + ")");
		Debug.printObjects(IntegrationTestBase.INDENT, maps);
	}

	@Test
	public void testCountAllMapDetails() throws Exception {
		Debug.println("testCountAllMapDetails(): " + this.genotypicDataManager.countAllMapDetails());
	}

	@Test
	public void testAddMarkerDetails() throws Exception {
		final Integer noOfRepeats = 0;
		final String motifType = "";
		final String sequence = "";
		final Integer sequenceLength = 0;
		final Integer minAllele = 0;
		final Integer maxAllele = 0;
		final Integer ssrNr = 0;
		final Float forwardPrimerTemp = 0f;
		final Float reversePrimerTemp = 0f;
		final Float elongationTemp = 0f;
		final Integer fragmentSizeExpected = 0;
		final Integer fragmentSizeObserved = 0;
		final Integer expectedProductSize = 0;
		final Integer positionOnReferenceSequence = 0;
		final String restrictionEnzymeForAssay = null;

		final MarkerDetails markerDetails =
			new MarkerDetails(this.markerId, noOfRepeats, motifType, sequence, sequenceLength, minAllele, maxAllele, ssrNr,
				forwardPrimerTemp, reversePrimerTemp, elongationTemp, fragmentSizeExpected, fragmentSizeObserved,
				expectedProductSize, positionOnReferenceSequence, restrictionEnzymeForAssay);

		final Integer idAdded = this.genotypicDataManager.addMarkerDetails(markerDetails);
		Debug.println("testAddMarkerDetails() Added: " + (idAdded != null ? markerDetails : null));
	}

	@Test
	public void testAddMarkerUserInfo() throws Exception {
		final String principalInvestigator = "Juan Dela Cruz";
		final String contact = "juan@irri.com.ph";
		final String institute = "IRRI";

		final MarkerUserInfo markerUserInfo = new MarkerUserInfo(this.markerId, principalInvestigator, contact, institute);

		final Integer idAdded = this.genotypicDataManager.addMarkerUserInfo(markerUserInfo);
		Debug.println("testAddMarkerUserInfo() Added: " + (idAdded != null ? markerUserInfo : null));
	}

	@Test
	public void testAddAccMetadataSet() throws Exception {
		final Integer accSampleId = 1;
		final Integer sampleId = 1;
		final Dataset dataset1 = new Dataset();
		dataset1.setDatasetId(datasetId);

		final Sample sample = new Sample();
		sample.setSampleId(sampleId);
		final AccMetadataSet accMetadataSet = new AccMetadataSet(null, dataset1, sample, accSampleId);

		final Integer idAdded = this.genotypicDataManager.addAccMetadataSet(accMetadataSet);
		Debug.println("testAccMetadataSet() Added: " + (idAdded != null ? accMetadataSet : null));
	}

	@Test
	public void testAddMarkerMetadataSet() throws Exception {
		final Integer markerMetadatasetId = null;
		final Integer markerSampleId = 1;
		final MarkerMetadataSet markerMetadataSet = new MarkerMetadataSet(markerMetadatasetId, dataset, this.markerId, markerSampleId);

		final Integer idAdded = this.genotypicDataManager.addMarkerMetadataSet(markerMetadataSet);
		Debug.println("testAddMarkerMetadataSet() Added: " + (idAdded != null ? markerMetadataSet : null));
	}

	@Test
	public void testAddDataset() throws Exception {
		final Dataset dataset = this.createDataset();
		this.genotypicDataManager.addDataset(dataset);
		Debug.println("testAddDataset() Added: " + (dataset.getDatasetId() != null ? dataset : null));
	}

	private Dataset createDataset() throws Exception {
		final Integer datasetId = null;
		final String datasetName = " QTL_ ICGS 44 X ICGS 78" + (int) (Math.random() * 1000);
		final String datasetDesc = "ICGS 44 X ICGS 78";
		final String datasetType = "QTL";
		final String genus = "Groundnut";
		final String species = "";
		final Date uploadTemplateDate = new Date(System.currentTimeMillis());
		final String remarks = "";
		final String dataType = "int";
		final String missingData = null;
		final String method = null;
		final String score = null;
		final String institute = null;
		final String principalInvestigator = null;
		final String email = null;
		final String purposeOfStudy = null;

		final Dataset datas =
			new Dataset(datasetId, datasetName, datasetDesc, datasetType, genus, species, uploadTemplateDate, remarks, dataType,
				missingData, method, score, institute, principalInvestigator, email, purposeOfStudy, null, null, null, null);

		final Sample sample = new Sample();
		sample.setSampleId(1);

		final List<AccMetadataSet> accMetadataSets = new ArrayList<>();
		final AccMetadataSet accMetadataSet = new AccMetadataSet(null, datas, sample, 1);
		accMetadataSets.add(accMetadataSet);

		final Marker marker = new Marker();
		marker.setMarkerId(1);

		final List<CharValues> charValues = new ArrayList<>();
		final CharValues charValues1 = new CharValues(null, datas, marker, sample, "A/C", 1, 1);
		charValues.add(charValues1);

		datas.setAccMetadataSets(accMetadataSets);
		datas.setCharValues(charValues);

		return datas;

	}

	@Test
	public void testAddGDMSMarker() throws Exception {
		final Integer markerId = null;
		final String markerType = "SSR";
		final String markerName = "SeqTEST";
		final String species = "Groundnut";
		final String dbAccessionId = null;
		final String reference = null;
		final String genotype = null;
		final String ploidy = null;
		final String primerId = null;
		final String remarks = null;
		final String assayType = null;
		final String motif = null;
		final String forwardPrimer = null;
		final String reversePrimer = null;
		final String productSize = null;
		final Float annealingTemp = Float.valueOf(0);
		final String amplification = null;

		final Marker marker =
			new Marker(markerId, markerType, markerName, species, dbAccessionId, reference, genotype, ploidy, primerId, remarks,
				assayType, motif, forwardPrimer, reversePrimer, productSize, annealingTemp, amplification);

		// On first run, this will insert record to local
		try {
			final Integer idAdded = this.genotypicDataManager.addGDMSMarker(marker);
			Debug.println("testAddGDMSMarker() Added: " + (idAdded != null ? marker : null));
		} catch (final MiddlewareQueryException e) {
			Debug.println(e.getMessage() + "\n");
		}

		// Try adding record that exists in central
		try {
			marker.setMarkerName("GA101");
			final Integer idAdded = this.genotypicDataManager.addGDMSMarker(marker);
			Debug.println("testAddGDMSMarker() Added: " + (idAdded != null ? marker : null));
		} catch (final MiddlewareQueryException e) {
			Assert.assertTrue(e.getMessage().contains("Marker already exists in Central or Local and cannot be added"));
		}

	}

	@Test
	public void testAddGDMSMarkerAlias() throws Exception {
		final Integer markerId = -1;
		final String alias = "testalias";
		final MarkerAlias markerAlias = new MarkerAlias(null, markerId, alias);

		final Integer idAdded = this.genotypicDataManager.addGDMSMarkerAlias(markerAlias);
		Debug.println("testAddGDMSMarkerAlias() Added: " + (idAdded != null ? markerAlias : null));
	}

	@Test
	public void testAddDatasetUser() throws Exception {

		final Integer userId = 123;
		final DatasetUsers datasetUser = new DatasetUsers(dataset, userId);

		final Integer idAdded = this.genotypicDataManager.addDatasetUser(datasetUser);
		Debug.println("testAddDatasetUser() Added: " + (idAdded != null ? datasetUser : null));

	}

	@Test
	public void testAddAlleleValues() throws Exception {
		final Integer anId = null;
		final Integer sampleId = 1920;
		final String alleleBinValue = "alleleBinValue";
		final String alleleRawValue = "alleleRawValue";
		final Integer peakHeight = 10;

		final Sample sample = new Sample();
		sample.setSampleId(sampleId);
		final AlleleValues alleleValues =
			new AlleleValues(anId, this.datasetId, sample, this.markerId, alleleBinValue, alleleRawValue, peakHeight);

		final Integer idAdded = this.genotypicDataManager.addAlleleValues(alleleValues);
		Debug.println("testAddAlleleValues() Added: " + (idAdded != null ? alleleValues : null));
	}

	@Test
	public void testAddCharValues() throws Exception {
		final Integer acId = null;
		final Integer sampleId = 1920;
		final String charValue = "CV";
		final Integer markerSampleId = 1;
		final Integer accSampleId = 1;
		final Dataset dataset = new Dataset();
		dataset.setDatasetId(datasetId);
		final Sample sample = new Sample();
		sample.setSampleId(sampleId);

		final Marker marker = new Marker();
		marker.setMarkerId(this.markerId);
		final CharValues charValues = new CharValues(acId, dataset, marker, sample, charValue, markerSampleId, accSampleId);

		final Integer idAdded = this.genotypicDataManager.addCharValues(charValues);
		Debug.println("testAddCharValues() Added: " + (idAdded != null ? charValues : null));
	}

	@Test
	public void testAddMappingPop() throws Exception {
		final String mappingType = "test";
		final Integer parentAGId = 956;
		final Integer parentBGId = 1042;
		final Integer populationSize = 999;
		final String populationType = "test";
		final String mapDataDescription = "test";
		final String scoringScheme = "test";

		final MappingPop mappingPop =
			new MappingPop(this.datasetId, mappingType, parentAGId, parentBGId, populationSize, populationType, mapDataDescription,
				scoringScheme, this.mapId);

		final Integer idAdded = this.genotypicDataManager.addMappingPop(mappingPop);
		Debug.println("testAddMappingPop() Added: " + (idAdded != null ? mappingPop : null));
	}

	@Test
	public void testAddMappingPopValue() throws Exception {
		final Integer mpId = null;
		final String mapCharValue = "X";
		final Integer sampleId = 1434;
		final Integer markerSampleId = 1;
		final Integer accSampleId = 1;
		final Sample sample = new Sample();
		sample.setSampleId(sampleId);
		final MappingPopValues mappingPopValue =
			new MappingPopValues(mpId, mapCharValue, this.datasetId, sample, this.markerId, markerSampleId, accSampleId);

		final Integer idAdded = this.genotypicDataManager.addMappingPopValue(mappingPopValue);
		Debug.println("testAddMappingPopValue() Added: " + (idAdded != null ? mappingPopValue : null));
	}

	@Test
	public void testAddMarkerOnMap() throws Exception {
		final Integer markerOnMapId = null;
		final Integer mapId = -2;
		final Integer markerId = -6;
		final Float startPosition = Float.valueOf("123.4");
		final Float endPosition = Float.valueOf("567.8");
		// String mapUnit = "TS";
		final String linkageGroup = "Test";
		final MarkerOnMap markerOnMap = new MarkerOnMap(markerOnMapId, mapId, markerId, startPosition, endPosition, linkageGroup);

		try {
			final Integer idAdded = this.genotypicDataManager.addMarkerOnMap(markerOnMap);
			Debug.println("testAddMarkerOnMap() Added: " + (idAdded != null ? markerOnMap : null));
		} catch (final MiddlewareQueryException e) {
			if (e.getMessage().contains("Map Id not found")) {
				Debug.println("Foreign key constraint violation: Map Id not found");
			}
		}
	}

	@Test
	public void testAddDartValue() throws Exception {
		final Integer adId = null;
		final Integer cloneId = -1;
		final Float qValue = Float.valueOf("123.45");
		final Float reproducibility = Float.valueOf("543.21");
		final Float callRate = Float.valueOf("12.3");
		final Float picValue = Float.valueOf("543.2");
		final Float discordance = Float.valueOf("1.2");

		final DartValues dartValue =
			new DartValues(adId, this.datasetId, this.markerId, cloneId, qValue, reproducibility, callRate, picValue, discordance);

		final Integer idAdded = this.genotypicDataManager.addDartValue(dartValue);
		Debug.println("testAddDartValue() Added: " + (idAdded != null ? dartValue : null));
	}

	@Test
	public void testAddQtl() throws Exception {
		final Integer qtlId = null;
		final String qtlName = "TestQTL";
		final Dataset dataset = this.createDataset();
		final Integer datasetId = this.genotypicDataManager.addDataset(dataset);

		final Qtl qtl = new Qtl(qtlId, qtlName, datasetId);
		final Integer idAdded = this.genotypicDataManager.addQtl(qtl);
		Debug.println("testAddQtl() Added: " + (idAdded != null ? qtl : null));

		this.testAddQtlDetails(idAdded);
	}

	private void testAddQtlDetails(final Integer qtlId) throws Exception {
		final Float minPosition = 0f;
		final Float maxPosition = 8f;
		final Integer traitId = 1001; // "DE";
		final String experiment = "";
		final Float effect = 0f;
		final Float scoreValue = 2.5f;
		final Float rSquare = 10f;
		final String linkageGroup = "LG06";
		final String interactions = "";
		final String leftFlankingMarker = "Ah4-101";
		final String rightFlankingMarker = "GM2536";
		final Float position = 34.71f;
		final Float clen = 0f;
		final String seAdditive = null;
		final String hvParent = null;
		final String hvAllele = null;
		final String lvParent = null;
		final String lvAllele = null;

		final QtlDetails qtlDetails =
			new QtlDetails(qtlId, this.mapId, minPosition, maxPosition, traitId, experiment, effect, scoreValue, rSquare, linkageGroup,
				interactions, leftFlankingMarker, rightFlankingMarker, position, clen, seAdditive, hvParent, hvAllele, lvParent,
				lvAllele);

		final Integer idAdded = this.genotypicDataManager.addQtlDetails(qtlDetails);
		Debug.println("testAddQtlDetails() Added: " + (idAdded != null ? qtlDetails : null));
	}

	@Test
	public void testAddMap() throws Exception {
		this.addMap();
	}

	private Integer addMap() throws Exception {
		final Integer mapId = null;
		final String mapName = "RIL-1 (TAG 24 x ICGV 86031)";
		final String mapType = "genetic";
		final Integer mpId = 0;
		final String mapDesc = null;
		final String mapUnit = null;
		final String genus = "Genus";
		final String species = "Groundnut";
		final String institute = "ICRISAT";
		final Map map = new Map(mapId, mapName, mapType, mpId, mapDesc, mapUnit, genus, species, institute);

		final Integer idAdded = this.genotypicDataManager.addMap(map);
		Debug.println("testAddMap() Added: " + (idAdded != null ? map : null));

		return idAdded;

	}

	private Marker createMarker() {
		final Integer markerId = null; // Will be set/overridden by the function
		final String markerType = null; // Will be set/overridden by the function
		final String markerName = "SeqTEST" + System.currentTimeMillis();
		final String species = "Groundnut";
		final String dbAccessionId = null;
		final String reference = null;
		final String genotype = null;
		final String ploidy = null;
		final String primerId = null;
		final String remarks = null;
		final String assayType = null;
		final String motif = null;
		final String forwardPrimer = null;
		final String reversePrimer = null;
		final String productSize = null;
		final Float annealingTemp = Float.valueOf(0);
		final String amplification = null;

		final Marker marker =
			new Marker(markerId, markerType, markerName, species, dbAccessionId, reference, genotype, ploidy, primerId, remarks,
				assayType, motif, forwardPrimer, reversePrimer, productSize, annealingTemp, amplification);

		return marker;

	}

	private List<Object> createMarkerRecords() {

		final String alias = "testalias";
		final Integer noOfRepeats = 0;
		final String motifType = "";
		final String sequence = "";
		final Integer sequenceLength = 0;
		final Integer minAllele = 0;
		final Integer maxAllele = 0;
		final Integer ssrNr = 0;
		final Float forwardPrimerTemp = 0f;
		final Float reversePrimerTemp = 0f;
		final Float elongationTemp = 0f;
		final Integer fragmentSizeExpected = 0;
		final Integer fragmentSizeObserved = 0;
		final Integer expectedProductSize = 0;
		final Integer positionOnReferenceSequence = 0;
		final String restrictionEnzymeForAssay = null;
		final String principalInvestigator = "Juan Dela Cruz";
		final String contact = "juan@irri.com.ph";
		final String institute = "IRRI";

		final Marker marker = this.createMarker();
		final Integer markerId = marker.getMarkerId();

		final MarkerAlias markerAlias = new MarkerAlias(null, markerId, alias);
		final MarkerDetails markerDetails =
			new MarkerDetails(markerId, noOfRepeats, motifType, sequence, sequenceLength, minAllele, maxAllele, ssrNr,
				forwardPrimerTemp, reversePrimerTemp, elongationTemp, fragmentSizeExpected, fragmentSizeObserved,
				expectedProductSize, positionOnReferenceSequence, restrictionEnzymeForAssay);
		final MarkerUserInfo markerUserInfo = new MarkerUserInfo(markerId, principalInvestigator, contact, institute);

		final List<Object> markerRecords = new ArrayList<Object>();
		markerRecords.add(marker);
		markerRecords.add(markerAlias);
		markerRecords.add(markerDetails);
		markerRecords.add(markerUserInfo);
		return markerRecords;
	}

	@Test
	public void testSetSNPMarkers() throws Exception {
		final List<Object> markerRecords = this.createMarkerRecords();
		final Marker marker = (Marker) markerRecords.get(0);
		final MarkerAlias markerAlias = (MarkerAlias) markerRecords.get(1);
		final MarkerDetails markerDetails = (MarkerDetails) markerRecords.get(2);
		final MarkerUserInfo markerUserInfo = (MarkerUserInfo) markerRecords.get(3);

		final Boolean addStatus = this.genotypicDataManager.setSNPMarkers(marker, markerAlias, markerDetails, markerUserInfo);
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
		final Integer markerId = null; // Will be set/overridden by the function
		final String markerType = GdmsType.TYPE_SNP.getValue(); // Will be set/overridden by the function
		final String markerName = "GKAM0001";
		final String species = "Groundnut";
		final String dbAccessionId = "";
		final String reference = "";
		final String genotype = "";
		final String ploidy = "";
		final String primerId = null;
		final String remarks = null;
		final String assayType = "KASPar";
		final String motif = "A/T";
		final String forwardPrimer = "AGCTTAACAATGAAGGAAATGGTGAGGAGAGGAGGAGGTTTGGTGAGAGACGAGGACCTG";
		final String reversePrimer = "TCGTTCTTTCAGGCCACCTTACAATGGTAATGTTAATGAGAACTTTCACCTTAATGCT";
		final String productSize = "";
		final Float annealingTemp = null;
		final String amplification = null;

		// MarkerAlias [markerId=null, alias=]
		final String alias = "";

		// MarkerDetails [markerId=null, noOfRepeats=null, motifType=null, sequence=, sequenceLength=null,
		// minAllele=null, maxAllele=null, ssrNr=null, forwardPrimerTemp=null, reversePrimerTemp=null,
		// elongationTemp=null, fragmentSizeExpected=null, fragmentSizeObserved=null, expectedProductSize=null,
		// positionOnReferenceSequence=null, restrictionEnzymeForAssay=null]
		final Integer noOfRepeats = null;
		final String motifType = null;
		final String sequence = "";
		final Integer sequenceLength = null;
		final Integer minAllele = null;
		final Integer maxAllele = null;
		final Integer ssrNr = null;
		final Float forwardPrimerTemp = null;
		final Float reversePrimerTemp = null;
		final Float elongationTemp = null;
		final Integer fragmentSizeExpected = null;
		final Integer fragmentSizeObserved = null;
		final Integer expectedProductSize = null;
		final Integer positionOnReferenceSequence = null;
		final String restrictionEnzymeForAssay = null;

		final String principalInvestigator = "Rajeev K Varshney";
		final String contact = "";
		final String institute = "ICRISAT";

		final Marker marker =
			new Marker(markerId, markerType, markerName, species, dbAccessionId, reference, genotype, ploidy, primerId, remarks,
				assayType, motif, forwardPrimer, reversePrimer, productSize, annealingTemp, amplification);
		final MarkerAlias markerAlias = new MarkerAlias(null, markerId, alias);
		final MarkerDetails markerDetails =
			new MarkerDetails(markerId, noOfRepeats, motifType, sequence, sequenceLength, minAllele, maxAllele, ssrNr,
				forwardPrimerTemp, reversePrimerTemp, elongationTemp, fragmentSizeExpected, fragmentSizeObserved,
				expectedProductSize, positionOnReferenceSequence, restrictionEnzymeForAssay);
		final MarkerUserInfo markerUserInfo = new MarkerUserInfo(markerId, principalInvestigator, contact, institute);

		try {
			this.genotypicDataManager.setSNPMarkers(marker, markerAlias, markerDetails, markerUserInfo);
		} catch (final Exception e) {
			Assert.assertTrue(e.getMessage().contains("Marker already exists in Central or Local and cannot be added."));
		}
	}

	@Test
	public void testSetCAPMarkers() throws Exception {
		final List<Object> markerRecords = this.createMarkerRecords();
		final Marker marker = (Marker) markerRecords.get(0);
		final MarkerAlias markerAlias = (MarkerAlias) markerRecords.get(1);
		final MarkerDetails markerDetails = (MarkerDetails) markerRecords.get(2);
		final MarkerUserInfo markerUserInfo = (MarkerUserInfo) markerRecords.get(3);

		final Boolean addStatus = this.genotypicDataManager.setCAPMarkers(marker, markerAlias, markerDetails, markerUserInfo);
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
		final List<Object> markerRecords = this.createMarkerRecords();
		final Marker marker = (Marker) markerRecords.get(0);
		final MarkerAlias markerAlias = (MarkerAlias) markerRecords.get(1);
		final MarkerDetails markerDetails = (MarkerDetails) markerRecords.get(2);
		final MarkerUserInfo markerUserInfo = (MarkerUserInfo) markerRecords.get(3);

		try {
			final Boolean addStatus = this.genotypicDataManager.setCISRMarkers(marker, markerAlias, markerDetails, markerUserInfo);
			if (addStatus) {
				Debug.println("testSetCISRMarkers() Added: ");
				Debug.println(IntegrationTestBase.INDENT, marker.toString());
				Debug.println(IntegrationTestBase.INDENT, markerAlias.toString());
				Debug.println(IntegrationTestBase.INDENT, markerDetails.toString());
				Debug.println(IntegrationTestBase.INDENT, markerUserInfo.toString());
			}
		} catch (final MiddlewareQueryException e) {
			// Marker already exists. Try again
			this.testSetCISRMarkers();
		}
	}

	@Test
	public void testSetMaps() throws Exception {

		// Marker Fields
		final Integer markerId = null; // Value will be set/overriden by the function
		final String markerType = "UA";
		final String markerName = "GCP-833TestMarker";
		final String species = "Groundnut";

		final Marker marker = new Marker();
		marker.setMarkerId(markerId);
		marker.setMarkerType(markerType);
		marker.setMarkerName(markerName);
		marker.setSpecies(species);
		marker.setAnnealingTemp(new Float(0));

		// Map Fields
		final Integer markerOnMapId = null;
		final Integer mapId = null; // Value will be set/overriden by the function
		final String mapName = "GCP-833TestMap";
		final String mapType = "genetic";
		final Integer mpId = 0;
		final String mapDesc = null;
		final String genus = "Genus";
		final String institute = "ICRISAT";

		// MarkerOnMap Fields
		final Float startPosition = new Float(0);
		final Float endPosition = new Float(0);
		final String mapUnit = "CM";
		final String linkageGroup = "LG23";

		final Map map = new Map(mapId, mapName, mapType, mpId, mapDesc, mapUnit, genus, species, institute);

		final MarkerOnMap markerOnMap = new MarkerOnMap(markerOnMapId, mapId, markerId, startPosition, endPosition, linkageGroup);

		Boolean addStatus = null;
		try {
			addStatus = this.genotypicDataManager.setMaps(marker, markerOnMap, map);
		} catch (final MiddlewareQueryException e) {
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
		final String qtlName = "TestQTL";
		final List<Integer> mapIDs = this.genotypicDataManager.getMapIDsByQTLName(qtlName, 0, 2);
		Debug.println("TestGetMapIDsByQTLName(" + qtlName + ")");
		Debug.printObjects(IntegrationTestBase.INDENT, mapIDs);
	}

	@Test
	public void testCountMapIDsByQTLName() throws Exception {
		final String qtlName = "TestQTL";
		final long count = this.genotypicDataManager.countMapIDsByQTLName(qtlName);
		Debug.println("TestCountMapIDsByQTLName(" + qtlName + ")");
		Debug.println(IntegrationTestBase.INDENT, "Count of Map IDs: " + count);
	}

	@Test
	public void testGetMarkerIDsByMapIDAndLinkageBetweenStartPosition() throws Exception {
		final int mapID = 1; // Groundnut central
		final String linkage = "BC-1_b11";
		final double startPos = 0;
		final double endPos = 100;
		final Set<Integer> markerIDs =
			this.genotypicDataManager.getMarkerIDsByMapIDAndLinkageBetweenStartPosition(mapID, linkage, startPos, endPos, 0,
				Integer.MAX_VALUE);
		Debug.printObjects(IntegrationTestBase.INDENT, new ArrayList<Integer>(markerIDs));
	}

	@Test
	public void testGetMarkersByPositionAndLinkageGroup() throws Exception {
		final String linkage = "LG01";
		final double startPos = 0;
		final double endPos = 2.2;
		final List<Marker> markers = this.genotypicDataManager.getMarkersByPositionAndLinkageGroup(startPos, endPos, linkage);
		Assert.assertTrue(markers.size() > 0);
		Debug.printObjects(IntegrationTestBase.INDENT, markers);
	}

	@Test
	public void testCountMarkerIDsByMapIDAndLinkageBetweenStartPosition() throws Exception {
		final int mapID = 1;
		final String linkage = "LG01";
		final double startPos = 0;
		final double endPos = 2.2;
		final long count = this.genotypicDataManager.countMarkerIDsByMapIDAndLinkageBetweenStartPosition(mapID, linkage, startPos, endPos);
		Debug.println("Count of Marker IDs: " + count);
	}

	@Test
	public void testGetMarkersByMarkerIDs() throws Exception {
		final List<Integer> markerIDs = Arrays.asList(1317);
		final List<Marker> markerList = this.genotypicDataManager.getMarkersByMarkerIds(markerIDs, 0, 5);
		Debug.printObjects(IntegrationTestBase.INDENT, markerList);
	}

	@Test
	public void testCountMarkersByMarkerIDs() throws Exception {
		final List<Integer> markerIDs = Arrays.asList(1317);
		final long count = this.genotypicDataManager.countMarkersByMarkerIds(markerIDs);
		Debug.println("Count of Markers: " + count);
	}

	@Test
	public void testGetQtlByQtlIdsFromCentral() throws Exception {
		final List<Integer> qtlIds = Arrays.asList(1, 2, 3);
		final List<QtlDetailElement> results =
			this.genotypicDataManager.getQtlByQtlIds(qtlIds, 0, (int) this.genotypicDataManager.countQtlByQtlIds(qtlIds));
		Assert.assertTrue(results.size() > 0);
		Debug.printFormattedObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testGetQtlByQtlIdsBothDB() throws Exception {
		final List<Integer> qtlIds = Arrays.asList(1, 2, 3, -1, -2, -3);
		final List<QtlDetailElement> results =
			this.genotypicDataManager.getQtlByQtlIds(qtlIds, 0, (int) this.genotypicDataManager.countQtlByQtlIds(qtlIds));
		Assert.assertTrue(results.size() > 0);
		Debug.printFormattedObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testCountQtlByQtlIdsFromCentral() throws Exception {
		final List<Integer> qtlIds = Arrays.asList(1, 2, 3);
		final long count = this.genotypicDataManager.countQtlByQtlIds(qtlIds);
		Assert.assertTrue(count > 0);
		Debug.println("testCountQtlByQtlIdsFromCentral() RESULTS: " + count);
	}

	@Test
	public void testCountQtlByQtlIds() throws Exception {
		final List<Integer> qtlIds = Arrays.asList(1, 2, 3, -1, -2, -3);
		final long count = this.genotypicDataManager.countQtlByQtlIds(qtlIds);
		Assert.assertTrue(count > 0);
		Debug.println("testCountQtlByQtlIds() RESULTS: " + count);
	}

	@Test
	public void testGetQtlDataByQtlTraits() throws Exception {
		final List<Integer> qtlTraits = new ArrayList<Integer>();
		qtlTraits.add(1001); // "DE"
		final List<QtlDataElement> results =
			this.genotypicDataManager.getQtlDataByQtlTraits(qtlTraits, 0,
				(int) this.genotypicDataManager.countQtlDataByQtlTraits(qtlTraits));
		Debug.println("testGetQtlDataByQtlTraits() RESULTS: " + results.size());
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testCountQtlDataByQtlTraits() throws Exception {
		final List<Integer> qtlTraits = new ArrayList<Integer>();
		qtlTraits.add(1001); // "DE"
		final long count = this.genotypicDataManager.countQtlDataByQtlTraits(qtlTraits);
		Debug.println("testCountQtlDataByQtlTraits() RESULTS: " + count);
	}

	@Test
	public void testGetQtlDetailsByQtlTraits() throws Exception {
		final List<Integer> qtlTraits = Arrays.asList(1001);
		final List<QtlDetailElement> results = this.genotypicDataManager.getQtlDetailsByQtlTraits(qtlTraits, 0, Integer.MAX_VALUE);
		Assert.assertTrue(results.size() >= 3);
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testCountQtlDetailsByQtlTraits() throws Exception {
		final List<Integer> qtlTraits = Arrays.asList(1001);
		final long count = this.genotypicDataManager.countQtlDetailsByQtlTraits(qtlTraits);
		Assert.assertTrue(count >= 3);
		Debug.println("testCountQtlDataByQtlTraits() RESULTS: " + count);
	}

	@Test
	public void testCountAllelicValuesFromAlleleValuesByDatasetId() throws Exception {
		final long count = this.genotypicDataManager.countAllelicValuesFromAlleleValuesByDatasetId(this.datasetId);
		Assert.assertNotNull(count);
		Debug.println("testCountAllelicValuesFromAlleleValuesByDatasetId(" + this.datasetId + ") Results: " + count);
	}

	@Test
	public void testCountAllelicValuesFromMappingPopValuesByDatasetId() throws Exception {
		final long count = this.genotypicDataManager.countAllelicValuesFromMappingPopValuesByDatasetId(this.datasetId);
		Assert.assertNotNull(count);
		Debug.println("testCountAllelicValuesFromMappingPopValuesByDatasetId(" + this.datasetId + ") Results: " + count);
	}

	@Test
	public void testCountAllParentsFromMappingPopulation() throws Exception {
		final long count = this.genotypicDataManager.countAllParentsFromMappingPopulation();
		Assert.assertNotNull(count);
		Debug.println("testCountAllParentsFromMappingPopulation() Results: " + count);
	}

	@Test
	public void testCountMapDetailsByName() throws Exception {
		final String nameLike = "GCP-833TestMap"; // change map_name
		final long count = this.genotypicDataManager.countMapDetailsByName(nameLike);
		Assert.assertNotNull(count);
		Debug.println("testCountMapDetailsByName(" + nameLike + ") Results: " + count);
	}

	@Test
	public void testGetMapNamesByMarkerIds() throws Exception {
		final List<Integer> markerIds = Arrays.asList(-6, 1317, 621, 825, 211);
		final java.util.Map<Integer, List<String>> markerMaps = this.genotypicDataManager.getMapNamesByMarkerIds(markerIds);
		Assert.assertTrue(markerMaps.size() > 0);
		for (int i = 0; i < markerIds.size(); i++) {
			Debug.println(
				IntegrationTestBase.INDENT,
				"Marker ID = " + markerIds.get(i) + " : Map Name/s = " + markerMaps.get(markerIds.get(i)));
		}
	}

	@Test
	public void testCountMarkerInfoByDbAccessionId() throws Exception {
		final String dbAccessionId = ""; // change dbAccessionId
		final long count = this.genotypicDataManager.countMarkerInfoByDbAccessionId(dbAccessionId);
		Assert.assertNotNull(count);
		Debug.println("testCountMarkerInfoByDbAccessionId(" + dbAccessionId + ") Results: " + count);
	}

	@Test
	public void testCountMarkerInfoByGenotype() throws Exception {
		final String genotype = "cv. Tatu"; // change genotype
		final long count = this.genotypicDataManager.countMarkerInfoByGenotype(genotype);
		Assert.assertNotNull(count);
		Debug.println("testCountMarkerInfoByGenotype(" + genotype + ") Results: " + count);
	}

	@Test
	public void testCountMarkerInfoByMarkerName() throws Exception {
		final String markerName = "SeqTEST"; // change markerName
		final long count = this.genotypicDataManager.countMarkerInfoByMarkerName(markerName);
		Assert.assertNotNull(count);
		Debug.println("testCountMarkerInfoByMarkerName(" + markerName + ") Results: " + count);
	}

	@Test
	public void testGetAllParentsFromMappingPopulation() throws Exception {
		final long count = this.genotypicDataManager.countAllParentsFromMappingPopulation();
		final List<ParentElement> results = this.genotypicDataManager.getAllParentsFromMappingPopulation(0, (int) count);
		Assert.assertNotNull(results);
		Assert.assertFalse(results.isEmpty());
		Debug.println("testGetAllParentsFromMappingPopulation() Results: ");
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testGetMapDetailsByName() throws Exception {
		final String nameLike = "GCP-833TestMap"; // change map_name
		final long count = this.genotypicDataManager.countMapDetailsByName(nameLike);
		final List<MapDetailElement> results = this.genotypicDataManager.getMapDetailsByName(nameLike, 0, (int) count);
		Assert.assertNotNull(results);
		Assert.assertFalse(results.isEmpty());
		Debug.println("testGetMapDetailsByName(" + nameLike + ")");
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testGetMarkersByIds() throws Exception {
		final List<Integer> markerIds = Arrays.asList(1, 3);
		final List<Marker> results = this.genotypicDataManager.getMarkersByIds(markerIds, 0, 100);
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
		final String haplotype = "STUDY";
		final List<Marker> results = this.genotypicDataManager.getSNPsByHaplotype(haplotype);
		Debug.println("testGetSNPsByHaplotype(" + haplotype + ") Results: ");
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testAddHaplotype() throws Exception {

		final TrackData trackData = new TrackData(null, "TEST Track Data", 1);

		final List<TrackMarker> trackMarkers = new ArrayList<TrackMarker>();
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
		final long count = this.genotypicDataManager.countAllMaps();
		Assert.assertNotNull(count);
		Debug.println("testCountAllMaps() Results: " + count);
	}

	@Test
	public void testGetAllMaps() throws Exception {
		final List<Map> results = this.genotypicDataManager.getAllMaps(0, 100);
		Assert.assertNotNull(results);
		Assert.assertFalse(results.isEmpty());
		Debug.println("testGetAllMaps() Results:");
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testCountNidsFromAccMetadatasetByDatasetIds() throws Exception {
		final List<Integer> datasetIds = Arrays.asList(this.datasetId);
		final long count = this.genotypicDataManager.countAccMetadatasetByDatasetIds(datasetIds);
		Debug.println("testCountNidsFromAccMetadatasetByDatasetIds(" + datasetIds + ") = " + count);
	}

	@Test
	public void testCountMarkersFromMarkerMetadatasetByDatasetIds() throws Exception {
		final List<Integer> datasetIds = Arrays.asList(2, 3, 4);
		final long count = this.genotypicDataManager.countMarkersFromMarkerMetadatasetByDatasetIds(datasetIds);
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
		final List<Integer> gIds = Arrays.asList(1434, 1435, 1436);
		final long count = this.genotypicDataManager.countMappingPopValuesByGids(gIds);
		Debug.println("countMappingPopValuesByGids(" + gIds + ") = " + count);
	}

	@Test
	public void testCountMappingAlleleValuesByGids() throws Exception {
		final List<Integer> gIds = Arrays.asList(2213, 2214);
		final long count = this.genotypicDataManager.countMappingAlleleValuesByGids(gIds);
		Debug.println("testCountMappingAlleleValuesByGids(" + gIds + ") = " + count);
	}

	@Test
	public void testGetAllFromMarkerMetadatasetByMarkers() throws Exception {
		final List<Integer> markerIds = Arrays.asList(3302, -1);
		final List<MarkerMetadataSet> result = this.genotypicDataManager.getAllFromMarkerMetadatasetByMarkers(markerIds);
		Debug.println("testGetAllFromMarkerMetadatasetByMarker(" + markerIds + "): ");
		Debug.printObjects(IntegrationTestBase.INDENT, result);
	}

	@Test
	public void testGetDatasetDetailsByDatasetIds() throws Exception {
		final List<Integer> datasetIds = Arrays.asList(-1, -2, -3, 3, 4, 5);
		final List<Dataset> result = this.genotypicDataManager.getDatasetDetailsByDatasetIds(datasetIds);
		Debug.println("testGetDatasetDetailsByDatasetId(" + datasetIds + "): ");
		Debug.printObjects(IntegrationTestBase.INDENT, result);
	}

	@Test
	public void testGetQTLIdsByDatasetIds() throws Exception {
		final List<Integer> datasetIds = Arrays.asList(1, -6);
		// To get test dataset_ids, run in Local and Central: select qtl_id, dataset_id from gdms_qtl;
		final List<Integer> qtlIds = this.genotypicDataManager.getQTLIdsByDatasetIds(datasetIds);
		Debug.println("testGetQTLIdsByDatasetIds(" + datasetIds + "): " + qtlIds);
	}

	@Test
	public void testGetAllFromAccMetadataset() throws Exception {
		final List<Integer> gids = Arrays.asList(2012, 2014, 2016);
		final Integer datasetId = 5;
		final List<AccMetadataSet> result = this.genotypicDataManager.getAllFromAccMetadataset(gids, datasetId, SetOperation.NOT_IN);
		Debug.println("testGetAllFromAccMetadataset(gid=" + gids + ", datasetId=" + datasetId + "): ");
		Debug.printObjects(IntegrationTestBase.INDENT, result);
	}

	@Test
	public void testGetMapAndMarkerCountByMarkers() throws Exception {
		final List<Integer> markerIds = Arrays.asList(1317, 1318, 1788, 1053, 2279, 50);
		final List<MapDetailElement> details = this.genotypicDataManager.getMapAndMarkerCountByMarkers(markerIds);
		Debug.println("testGetMapAndMarkerCountByMarkers(" + markerIds + ")");
		Debug.printObjects(IntegrationTestBase.INDENT, details);
	}

	@Test
	public void testGetAllMTAs() throws Exception {
		final List<Mta> result = this.genotypicDataManager.getAllMTAs();
		Debug.println("testGetAllMTAs(): ");
		Debug.printObjects(IntegrationTestBase.INDENT, result);
	}

	@Test
	public void testCountAllMTAs() throws Exception {
		final long count = this.genotypicDataManager.countAllMTAs();
		Debug.println("testCountAllMTAs(): " + count);
	}

	@Test
	public void testGetMTAsByTrait() throws Exception {
		final Integer traitId = 1;
		final List<Mta> result = this.genotypicDataManager.getMTAsByTrait(traitId);
		Debug.println("testGetMTAsByTrait(): ");
		Debug.printObjects(IntegrationTestBase.INDENT, result);
	}

	@Test
	public void testGetAllSNPs() throws Exception {
		final List<Marker> result = this.genotypicDataManager.getAllSNPMarkers();
		Debug.println("testGetAllSNPs(): ");
		Debug.printObjects(IntegrationTestBase.INDENT, result);
	}

	@Test
	public void testGetAlleleValuesByMarkers() throws Exception {
		final List<Integer> markerIds = Arrays.asList(-1, -2, 956, 1037);
		final List<AllelicValueElement> result = this.genotypicDataManager.getAlleleValuesByMarkers(markerIds);
		Debug.println("testGetAlleleValuesByMarkers(): ");
		Debug.printObjects(IntegrationTestBase.INDENT, result);
	}

	@Test
	public void testDeleteQTLs() throws Exception {
		final List<Qtl> qtls = this.genotypicDataManager.getAllQtl(0, 1);
		final List<Integer> qtlIds = new ArrayList<Integer>();
		if (qtls != null && qtls.size() > 0) {
			qtlIds.add(qtls.get(0).getQtlId());
			final int datasetId = qtls.get(0).getDatasetId();
			Debug.println("testDeleteQTLs(qtlIds=" + qtlIds + ", datasetId=" + datasetId);
			this.genotypicDataManager.deleteQTLs(qtlIds, datasetId);
			Debug.println("done with testDeleteQTLs");
		}
	}

	@Test
	public void testGetDatasetsByType() throws Exception {
		final List<Dataset> datasets = this.genotypicDataManager.getDatasetsByType(GdmsType.TYPE_MAPPING);
		Debug.printObjects(IntegrationTestBase.INDENT, datasets);
	}

	@Test
	public void testCountQtlDetailsByMapId() throws Exception {
		final int mapId = 7;
		Debug.println("testCountQTLsByMapId(" + mapId + ")");
		final long count = this.genotypicDataManager.countQtlDetailsByMapId(mapId);
		Debug.println("COUNT = " + count);
	}

	@Test
	public void testDeleteMaps() throws Exception {
		final Integer mapId = this.addMap();
		Debug.println("testDeleteMaps(" + mapId + ")");
		this.genotypicDataManager.deleteMaps(mapId);
		Debug.println("done with testDeleteMaps");
	}

	@Test
	public void testGetMarkerFromCharValuesByGids() throws Exception {
		final List<Integer> gIds = Arrays.asList(2012, 2014, 2016, 310544);
		final List<MarkerSampleId> result = this.genotypicDataManager.getMarkerFromCharValuesByGids(gIds);
		Debug.printObjects(IntegrationTestBase.INDENT, result);
	}

	@Test
	public void testGetMarkerFromAlleleValuesByGids() throws Exception {
		final List<Integer> gIds = Arrays.asList(2213, 2214);
		final List<MarkerSampleId> result = this.genotypicDataManager.getMarkerFromAlleleValuesByGids(gIds);
		Debug.printObjects(IntegrationTestBase.INDENT, result);
	}

	@Test
	public void testGetMarkerFromMappingPopValuesByGids() throws Exception {
		final List<Integer> gIds = Arrays.asList(1434, 1435);
		final List<MarkerSampleId> result = this.genotypicDataManager.getMarkerFromMappingPopByGids(gIds);
		Debug.printObjects(IntegrationTestBase.INDENT, result);
	}

	@Test
	public void testAddMta() throws Exception {
		final Mta mta =
			new Mta(null, 1, null, 1, 2.1f, 1, 1.1f, 2.2f, 3.3f, "gene", "chromosome", "alleleA", "alleleB", "alleleAPhenotype",
				"alleleBPhenotype", 4.4f, 5.5f, 6.6f, 7.7f, "correctionMethod", 8.8f, 9.9f, "dominance", "evidence", "reference",
				"notes");
		final MtaMetadata mtaMetadata = new MtaMetadata(this.datasetId, "project1", "population", 100, "Thousand");
		final DatasetUsers users = new DatasetUsers(dataset, 1);
		this.genotypicDataManager.addMTA(this.dataset, mta, mtaMetadata, users);

		// non-null id means the records were inserted.
		Assert.assertTrue(mta.getMtaId() != null && mtaMetadata.getDatasetID() != null);

		Debug.println("MTA added: ");
		Debug.printObject(IntegrationTestBase.INDENT, mta);
		Debug.printObject(IntegrationTestBase.INDENT, mtaMetadata);
	}

	@Test
	public void testAddMtaGCP9174() throws Exception {
		final Dataset dataset =
			new Dataset(null, "sample", "testing", "MTA", "Groundnut", "Groundnut", null, "", "int", null, "Tassel", "LOD", "ICRISAT",
				"TrusharShah", null, null, null, null, null, null);
		final Integer datasetId = this.genotypicDataManager.addDataset(dataset);
		dataset.setDatasetId(datasetId);

		final Mta mta =
			new Mta(null, this.markerId, datasetId, this.mapId, 6.01f, 22395, 0.0f, 0.0f, 0.0f, "1RS:1AL", "RIL-1 _LG12", "C", "T",
				"absent", "present", 0.0f, 0.0f, 0.0f, 0.0f, "Bonferroni", 0.0f, 0.0f, "co-dominant", "association",
				"Ellis et al (2002) TAG 105:1038-1042", "");

		final MtaMetadata mtaMetadata = new MtaMetadata(datasetId, "project1", "population", 100, "Thousand");

		final DatasetUsers users = new DatasetUsers(dataset, 1);

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
		final Dataset dataset =
			new Dataset(null, "TEST DATASET NAME", "DATASET DESC", "MTA", "GENUS", "SPECIES", null, "REMARKS", "int", null, "METHOD",
				"0.43", "INSTITUTE", "PI", "EMAIL", "OBJECTIVE", null, null, null, null);
		final List<Mta> mtaList = new ArrayList<Mta>();
		mtaList.add(new Mta(null, 1, null, 1, 2.1f, 1, 1.1f, 2.2f, 3.3f, "gene", "chromosome", "alleleA", "alleleB", "alleleAPhenotype",
			"alleleBPhenotype", 4.4f, 5.5f, 6.6f, 7.7f, "correctionMethod", 8.8f, 9.9f, "dominance", "evidence", "reference", "notes"));
		mtaList.add(new Mta(null, 2, null, 2, 3.1f, 2, 2.1f, 3.2f, 4.3f, "gene", "chromosome", "alleleA", "alleleB", "alleleAPhenotype",
			"alleleBPhenotype", 5.4f, 6.5f, 7.6f, 8.7f, "correctionMethod", 9.8f, 10.9f, "dominance", "evidence", "reference", "notes"));

		final MtaMetadata metadata = new MtaMetadata(null, "project1", "population", 100, "Thousand");

		final DatasetUsers users = new DatasetUsers(null, 1);
		this.genotypicDataManager.setMTA(dataset, users, mtaList, metadata);

		// Non-null id means the record was inserted.
		Assert.assertTrue(mtaList.get(0).getMtaId() != null && metadata.getDatasetID() != null);

		Debug.println("MTAs added: ");
		Debug.printObjects(IntegrationTestBase.INDENT, mtaList);
		Debug.printObject(IntegrationTestBase.INDENT, metadata);
	}

	@Test
	public void testDeleteMTA() throws Exception {
		final List<Mta> mtas = this.genotypicDataManager.getAllMTAs();

		if (mtas != null && !mtas.isEmpty()) {
			final List<Integer> datasetIds = new ArrayList<Integer>();
			datasetIds.add(mtas.get(0).getDatasetId());
			this.genotypicDataManager.deleteMTA(datasetIds);
			Debug.println(IntegrationTestBase.INDENT, "datasetId deleted = " + datasetIds);
		}
	}

	@Test
	public void testAddMtaMetadata() throws Exception {
		final List<Mta> mtas = this.genotypicDataManager.getAllMTAs();

		if (mtas != null && !mtas.isEmpty()) {
			final Mta mta = mtas.get(0);
			final MtaMetadata mtaMetadata = new MtaMetadata(mta.getMtaId(), "project", "population", 100, "Thousand");
			this.genotypicDataManager.addMtaMetadata(mtaMetadata);
			Debug.println(IntegrationTestBase.INDENT, "MtaMetadataset added: " + mtaMetadata);
		}
	}

	@Test
	public void testGetMarkerMetadatasetByDatasetId() throws Exception {
		final List<MarkerMetadataSet> result = this.genotypicDataManager.getMarkerMetadataSetByDatasetId(this.datasetId);
		Assert.assertTrue(result != null && !result.isEmpty());
		Debug.printObjects(IntegrationTestBase.INDENT, result);
	}

	@Test
	public void testGetCharValuesByMarkerIds() throws Exception {
		final List<Integer> markerIds = Arrays.asList(-1, -2);
		final List<CharValues> result = this.genotypicDataManager.getCharValuesByMarkerIds(markerIds);
		Debug.printObjects(IntegrationTestBase.INDENT, result);
	}

	@Test
	public void testUpdateMarkerInfoExisting() throws Exception {
		// Update existing Marker, MarkerAlias, MarkerDetails, MarkerUserInfo
		final List<Object> markerRecords = this.createMarkerRecords();
		final Marker marker = (Marker) markerRecords.get(0);
		final MarkerAlias markerAlias = (MarkerAlias) markerRecords.get(1);
		final MarkerDetails markerDetails = (MarkerDetails) markerRecords.get(2);
		final MarkerUserInfo markerUserInfo = (MarkerUserInfo) markerRecords.get(3);

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
		final Integer updateId = (int) (Math.random() * 100);
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
		final List<Object> markerRecords = this.createMarkerRecords();
		final Marker markerUA = (Marker) markerRecords.get(0);
		markerUA.setMarkerType(GdmsType.TYPE_UA.getValue());

		final Integer markerId = this.genotypicDataManager.addMarker(markerUA);

		Debug.println("MARKER ADDED: " + markerUA);

		final MarkerAlias markerAlias = (MarkerAlias) markerRecords.get(1);
		markerAlias.setMarkerId(markerId);
		final MarkerDetails markerDetails = (MarkerDetails) markerRecords.get(2);
		markerDetails.setMarkerId(markerId);
		final MarkerUserInfo markerUserInfo = (MarkerUserInfo) markerRecords.get(3);
		markerUserInfo.setMarkerId(markerId);

		final Boolean addStatus = this.genotypicDataManager.updateMarkerInfo(markerUA, markerAlias, markerDetails, markerUserInfo);
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

		final List<Object> markerRecords = this.createMarkerRecords();
		final MarkerAlias markerAlias = (MarkerAlias) markerRecords.get(1);
		final MarkerDetails markerDetails = (MarkerDetails) markerRecords.get(2);
		final MarkerUserInfo markerUserInfo = (MarkerUserInfo) markerRecords.get(3);

		final Integer updateId = (int) (Math.random() * 100);

		// Try to update marker name - should not be allowed.
		try {
			this.marker.setMarkerName(this.marker.getMarkerName() + updateId);
			this.genotypicDataManager.updateMarkerInfo(this.marker, markerAlias, markerDetails, markerUserInfo);
		} catch (final MiddlewareQueryException e) {
			Debug.println("Caught exception: Marker is in central database and cannot be updated.");
			Assert.assertTrue(e.getMessage().contains("Marker is in central database and cannot be updated."));
		}

		// Try to update species - should not be allowed.
		try {
			this.marker.setSpecies(this.marker.getSpecies() + updateId);
			this.genotypicDataManager.updateMarkerInfo(this.marker, markerAlias, markerDetails, markerUserInfo);
		} catch (final MiddlewareQueryException e) {
			Debug.println("Caught exception: Marker is in central database and cannot be updated.");
			Assert.assertTrue(e.getMessage().contains("Marker is in central database and cannot be updated."));
		}
	}

	@Test
	public void testUpdateMarkerInfoInCentral() throws Exception {

		// Get an existing marker from local db
		final List<Marker> markers = this.genotypicDataManager.getMarkersByIds(Arrays.asList(3316), 0, 1);
		if (markers != null && markers.size() > 0) {
			final Marker marker = markers.get(0);
			Debug.println(IntegrationTestBase.INDENT, "Existing marker:" + marker.toString());

			final List<Object> markerRecords = this.createMarkerRecords();
			final MarkerAlias markerAlias = (MarkerAlias) markerRecords.get(1);
			final MarkerDetails markerDetails = (MarkerDetails) markerRecords.get(2);
			final MarkerUserInfo markerUserInfo = (MarkerUserInfo) markerRecords.get(3);

			try {
				this.genotypicDataManager.updateMarkerInfo(marker, markerAlias, markerDetails, markerUserInfo);
			} catch (final MiddlewareQueryException e) {
				Debug.println(IntegrationTestBase.INDENT, "Marker is in central database and cannot be updated.");
				Assert.assertTrue(e.getMessage().contains("Marker is in central database and cannot be updated."));
			}
		} else {
			Debug.println(IntegrationTestBase.INDENT, "No marker data found in the database to update.");
		}
	}

	@Test
	public void testGetMarkerByType() throws Exception {
		final List<ExtendedMarkerInfo> markers = this.genotypicDataManager.getMarkerInfoDataByMarkerType("SSR");
		Assert.assertNotNull(markers);

		Assert.assertTrue(markers.size() != 0);
	}

	@Test
	public void testGetMarkersLikeMarkerName() throws Exception {
		final List<ExtendedMarkerInfo> markers = this.genotypicDataManager.getMarkerInfoDataLikeMarkerName("ga");
		Assert.assertNotNull(markers);

		Assert.assertTrue(markers.size() != 0);
	}

	@Test
	public void testGetMarkerInfosByMarkerNames() throws Exception {
		final Marker marker1 = this.createMarker();
		marker1.setMarkerType(GdmsType.TYPE_SNP.getValue());
		marker1.setMarkerName("GA1");
		this.genotypicDataManager.addMarker(marker1);

		final Marker marker2 = this.createMarker();
		marker2.setMarkerType(GdmsType.TYPE_SNP.getValue());
		marker2.setMarkerName("GA2");
		this.genotypicDataManager.addMarker(marker2);

		final List<String> paramList = new ArrayList<String>();
		paramList.add("GA1");
		paramList.add("GA2");
		final List<ExtendedMarkerInfo> markers = this.genotypicDataManager.getMarkerInfoByMarkerNames(paramList);
		Assert.assertNotNull(markers);

		Assert.assertTrue(markers.size() > 0);
	}

}
