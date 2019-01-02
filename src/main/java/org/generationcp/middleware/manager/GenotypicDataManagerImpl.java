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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.generationcp.middleware.dao.gdms.DatasetUsersDAO;
import org.generationcp.middleware.dao.gdms.MapDAO;
import org.generationcp.middleware.dao.gdms.MappingPopDAO;
import org.generationcp.middleware.dao.gdms.MarkerDAO;
import org.generationcp.middleware.dao.gdms.MarkerDetailsDAO;
import org.generationcp.middleware.dao.gdms.MarkerOnMapDAO;
import org.generationcp.middleware.dao.gdms.MarkerUserInfoDAO;
import org.generationcp.middleware.dao.gdms.MtaDAO;
import org.generationcp.middleware.dao.gdms.MtaMetadataDAO;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
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
import org.generationcp.middleware.pojos.gdms.MappingData;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the GenotypicDataManager interface. To instantiate this class, a Hibernate Session must be passed to its constructor.
 *
 * @author Joyce Avestro, Glenn Marintes, Daniel Villafuerte
 */
@SuppressWarnings("unchecked")
@Transactional
public class GenotypicDataManagerImpl extends DataManager implements GenotypicDataManager {

	private static final String TYPE_SNP = GdmsType.TYPE_SNP.getValue();
	private static final String TYPE_MTA = GdmsType.TYPE_MTA.getValue();
	private static final String TYPE_CAP = GdmsType.TYPE_CAP.getValue();
	private static final String TYPE_CISR = GdmsType.TYPE_CISR.getValue();
	private static final String TYPE_UA = GdmsType.TYPE_UA.getValue(); // Unassigned

	public GenotypicDataManagerImpl() {
	}

	public GenotypicDataManagerImpl(final HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
	}

	@Override
	public List<Integer> getMapIDsByQTLName(final String qtlName, final int start, final int numOfRows) {
		if (qtlName == null || qtlName.isEmpty()) {
			return new ArrayList<>();
		}

		return new ArrayList<>(this.getQtlDao().getMapIDsByQTLName(qtlName, start, numOfRows));
	}

	@Override
	public long countMapIDsByQTLName(final String qtlName) {

		return this.getQtlDao().countMapIDsByQTLName(qtlName);

	}

	@Override
	public List<Name> getNamesByNameIds(final List<Integer> nIds) {
		return this.getNameDao().getNamesByNameIds(nIds);
	}

	@Override
	public List<Name> getGermplasmNamesByMarkerId(final Integer markerId) {
		return this.getDatasetDao().getGermplasmNamesByMarkerId(markerId);
	}

	@Override
	public Name getNameByNameId(final Integer nId) {
		return this.getNameDao().getNameByNameId(nId);
	}

	@Override
	public long countAllMaps() {
		return super.countFromInstance(this.getMapDao());
	}

	@Override
	public List<Map> getAllMaps(final int start, final int numOfRows) {

		return this.getMapDao().getAll();

	}

	@Override
	public List<MapInfo> getMapInfoByMapName(final String mapName) {
		final List<MapInfo> mapInfoList = new ArrayList<>();

		// Step 1: Get map id by map name
		final Map map = this.getMapDao().getByName(mapName);
		if (map == null) {
			return new ArrayList<>();
		}

		// Step 2: Get markerId, linkageGroup, startPosition from gdms_markers_onmap
		final List<MarkerOnMap> markersOnMap = this.getMarkerOnMapDao().getMarkersOnMapByMapId(map.getMapId());

		final HashMap<Integer, Marker> markersMap = getMarkerByMapId(map.getMapId());

		// Step 3: Get marker name from gdms_marker and build MapInfo
		for (final MarkerOnMap markerOnMap : markersOnMap) {
			final Integer markerId = markerOnMap.getMarkerId();
			final String markerName = markersMap.get(markerId).getMarkerName();//this.getMarkerNameByMarkerId(markerId);
			final MapInfo mapInfo =
				new MapInfo(markerId, markerName, markerOnMap.getMapId(), map.getMapName(), markerOnMap.getLinkageGroup(),
					markerOnMap.getStartPosition(), map.getMapType(), map.getMapUnit());
			mapInfoList.add(mapInfo);
		}

		Collections.sort(mapInfoList);
		return mapInfoList;

	}

	@Override
	public List<MapInfo> getMapInfoByMapAndChromosome(final int mapId, final String chromosome) {
		return this.getMapDao().getMapInfoByMapAndChromosome(mapId, chromosome);
	}

	@Override
	public List<MapInfo> getMapInfoByMapChromosomeAndPosition(final int mapId, final String chromosome, final float startPosition) {
		return this.getMapDao().getMapInfoByMapChromosomeAndPosition(mapId, chromosome, startPosition);
	}

	@Override
	public List<MapInfo> getMapInfoByMarkersAndMap(final List<Integer> markers, final Integer mapId) {
		return this.getMapDao().getMapInfoByMarkersAndMap(markers, mapId);
	}

	// GCP-8572
	@Override
	public List<MarkerOnMap> getMarkerOnMaps(
		final List<Integer> mapIds, final String linkageGroup, final double startPos, final double endPos) {
		return this.getMarkerOnMapDao().getMarkersOnMap(mapIds, linkageGroup, startPos, endPos);
	}

	// GCP-8571
	@Override
	public List<MarkerOnMap> getMarkersOnMapByMarkerIds(final List<Integer> markerIds) {
		return this.getMarkerOnMapDao().getMarkersOnMapByMarkerIds(markerIds);
	}

	// GCP-8573
	@Override
	public List<String> getAllMarkerNamesFromMarkersOnMap() {
		final List<Integer> markerIds = this.getMarkerOnMapDao().getAllMarkerIds();

		return this.getMarkerDao().getMarkerNamesByIds(markerIds);

	}

	@Override
	public String getMapNameById(final Integer mapID) {
		return this.getMapDao().getMapNameById(mapID);
	}

	@Override
	public List<Dataset> getAllDatasets() {
		return this.getDatasetDao().getAll();
	}

	@Override
	public long countDatasetNames() {
		return this.getDatasetDao().countByName();
	}

	@Override
	public List<String> getDatasetNames(final int start, final int numOfRows) {
		return this.getDatasetDao().getDatasetNames(start, numOfRows);
	}

	@Override
	public List<String> getDatasetNamesByQtlId(final Integer qtlId, final int start, final int numOfRows) {
		return this.getDatasetDao().getDatasetNamesByQtlId(qtlId, start, numOfRows);
	}

	@Override
	public long countDatasetNamesByQtlId(final Integer qtlId) {
		return this.getDatasetDao().countDatasetNamesByQtlId(qtlId);

	}

	@Override
	public List<DatasetElement> getDatasetDetailsByDatasetName(final String datasetName) {
		return this.getDatasetDao().getDetailsByName(datasetName);
	}

	@Override
	public List<Marker> getMarkersByMarkerNames(final List<String> markerNames, final int start, final int numOfRows) {
		return this.getMarkerDao().getByNames(markerNames, start, numOfRows);
	}

	@Override
	public Set<Integer> getMarkerIDsByMapIDAndLinkageBetweenStartPosition(
		final int mapId, final String linkageGroup, final double startPos, final double endPos,
		final int start, final int numOfRows) {
		return this.getMarkerDao().getMarkerIDsByMapIDAndLinkageBetweenStartPosition(mapId, linkageGroup, startPos, endPos, start,
			numOfRows);
	}

	// GCP-8567
	@Override
	public List<Marker> getMarkersByPositionAndLinkageGroup(final double startPos, final double endPos, final String linkageGroup) {

		final List<Integer> markerIds = this.getMarkerOnMapDao().getMarkerIdsByPositionAndLinkageGroup(startPos, endPos, linkageGroup);
		return this.getMarkerDao().getMarkersByIds(markerIds);
	}

	@Override
	public long countMarkerIDsByMapIDAndLinkageBetweenStartPosition(
		final int mapId, final String linkageGroup, final double startPos, final double endPos) {
		return this.getMarkerDao().countMarkerIDsByMapIDAndLinkageBetweenStartPosition(mapId, linkageGroup, startPos, endPos);
	}

	@Override
	public List<Integer> getMarkerIdsByDatasetId(final Integer datasetId) {
		return this.getMarkerMetadataSetDao().getMarkerIdByDatasetId(datasetId);

	}

	@Override
	public List<ParentElement> getParentsByDatasetId(final Integer datasetId) {
		return this.getMappingPopDao().getParentsByDatasetId(datasetId);
	}

	@Override
	public List<String> getMarkerTypesByMarkerIds(final List<Integer> markerIds) {
		return this.getMarkerDao().getMarkerTypeByMarkerIds(markerIds);
	}

	@Override
	public List<MarkerNameElement> getMarkerNamesByGIds(final List<Integer> gIds) {

		final List<MarkerNameElement> dataValues = this.getMarkerDao().getMarkerNamesByGIds(gIds);

		// Remove duplicates
		final Set<MarkerNameElement> set = new HashSet<>();
		set.addAll(dataValues);
		dataValues.clear();
		dataValues.addAll(set);

		return dataValues;
	}

	@Override
	public List<GermplasmMarkerElement> getGermplasmNamesByMarkerNames(final List<String> markerNames) {
		return this.getMarkerDao().getGermplasmNamesByMarkerNames(markerNames);
	}

	@Override
	public List<MappingValueElement> getMappingValuesByGidsAndMarkerNames(
		final List<Integer> gids, final List<String> markerNames, final int start,
		final int numOfRows) {
		final List<MappingValueElement> mappingValueElementList;

		final List<Marker> markers = this.getMarkerDao().getByNames(markerNames, start, numOfRows);

		final List<Integer> markerIds = new ArrayList<>();
		for (final Marker marker : markers) {
			markerIds.add(marker.getMarkerId());
		}

		mappingValueElementList = this.getMappingPopDao().getMappingValuesByGidAndMarkerIds(gids, markerIds);

		for (final MappingValueElement element : mappingValueElementList) {
			if (element != null && element.getMarkerId() != null) {
				if (element.getMarkerId() >= 0 && element.getMarkerType() == null) {
					for (final Marker marker : markers) {
						if (marker.getMarkerId().equals(element.getMarkerId())) {
							element.setMarkerType(marker.getMarkerType());
							break;
						}
					}
				}
			}
		}

		return mappingValueElementList;
	}

	@Override
	public List<AllelicValueElement> getAllelicValuesByGidsAndMarkerNames(final List<Integer> gids, final List<String> markerNames) {
		final List<AllelicValueElement> allelicValues = new ArrayList<>();

		// Get marker_ids by marker_names
		final java.util.Map<Integer, String> markerIdName = this.getMarkerDao().getFirstMarkerIdByMarkerName(markerNames);
		final List<Integer> markerIds = new ArrayList<>(markerIdName.keySet());

		allelicValues.addAll(this.getMarkerDao().getAllelicValuesByGidsAndMarkerIds(gids, markerIds));

		for (final AllelicValueElement allelicValue : allelicValues) {
			allelicValue.setMarkerName(markerIdName.get(allelicValue.getMarkerId()));
		}

		return allelicValues;
	}

	@Override
	public List<AllelicValueWithMarkerIdElement> getAllelicValuesFromAlleleValuesByDatasetId(
		final Integer datasetId, final int start, final int numOfRows) {
		return this.getAlleleValuesDao().getAllelicValuesByDatasetId(datasetId, start, numOfRows);
	}

	@Override
	public long countAllelicValuesFromAlleleValuesByDatasetId(final Integer datasetId) {
		return this.getAlleleValuesDao().countByDatasetId(datasetId);
	}

	@Override
	public List<AllelicValueWithMarkerIdElement> getAllelicValuesFromMappingPopValuesByDatasetId(
		final Integer datasetId, final int start, final int numOfRows) {
		return this.getMappingPopValuesDao().getAllelicValuesByDatasetId(datasetId, start, numOfRows);
	}

	@Override
	public long countAllelicValuesFromMappingPopValuesByDatasetId(final Integer datasetId) {
		return this.getMappingPopValuesDao().countByDatasetId(datasetId);
	}

	@Override
	public List<MarkerInfo> getMarkerInfoByMarkerName(final String markerName, final int start, final int numOfRows) {
		return this.getMarkerInfoDao().getByMarkerName(markerName, start, numOfRows);
	}

	@Override
	public List<ExtendedMarkerInfo> getMarkerInfoDataByMarkerType(final String markerType) {
		return this.getExtendedMarkerInfoDao().getByMarkerType(markerType);
	}

	@Override
	public List<ExtendedMarkerInfo> getMarkerInfoDataLikeMarkerName(final String partialMarkerName) {
		return this.getExtendedMarkerInfoDao().getLikeMarkerName(partialMarkerName);
	}

	@Override
	public List<ExtendedMarkerInfo> getMarkerInfoByMarkerNames(final List<String> markerNames) {
		return this.getExtendedMarkerInfoDao().getByMarkerNames(markerNames);
	}

	@Override
	public List<AllelicValueElement> getAllelicValuesByGid(final Integer targetGID) {
		final List<Integer> inputList = new ArrayList<>();
		inputList.add(targetGID);

		final List<MarkerNameElement> markerNameElements = this.getMarkerNamesByGIds(inputList);

		final List<String> markerNames = new ArrayList<>();

		for (final MarkerNameElement markerNameElement : markerNameElements) {
			markerNames.add(markerNameElement.getMarkerName());
		}

		return this.getAllelicValuesByGidsAndMarkerNames(inputList, markerNames);
	}

	@Override
	public long countMarkerInfoByMarkerName(final String markerName) {
		return this.getMarkerInfoDao().countByMarkerName(markerName);
	}

	@Override
	public List<MarkerInfo> getMarkerInfoByGenotype(final String genotype, final int start, final int numOfRows) {

		return this.getMarkerInfoDao().getByGenotype(genotype, start, numOfRows);
	}

	@Override
	public long countMarkerInfoByGenotype(final String genotype) {
		return this.getMarkerInfoDao().countByGenotype(genotype);
	}

	@Override
	public List<MarkerInfo> getMarkerInfoByDbAccessionId(final String dbAccessionId, final int start, final int numOfRows) {
		return this.getMarkerInfoDao().getByDbAccessionId(dbAccessionId, start, numOfRows);
	}

	@Override
	public long countMarkerInfoByDbAccessionId(final String dbAccessionId) {
		return this.getMarkerInfoDao().countByDbAccessionId(dbAccessionId);
	}

	@Override
	public List<MarkerIdMarkerNameElement> getMarkerNamesByMarkerIds(final List<Integer> markerIds) {
		final List<MarkerIdMarkerNameElement> markers = this.getMarkerDao().getNamesByIds(markerIds);

		// Sort based on the given input order
		final List<MarkerIdMarkerNameElement> markersToReturn = new ArrayList<>();
		for (final Integer markerId : markerIds) {
			for (final MarkerIdMarkerNameElement element : markers) {
				if (element.getMarkerId() == markerId) {
					markersToReturn.add(element);
					break;
				}
			}
		}

		return markersToReturn;
	}

	private String getMarkerNameByMarkerId(final Integer markerId) {
		return this.getMarkerDao().getNameById(markerId);
	}

	private HashMap<Integer, Marker> getMarkerByMapId(final Integer mapId) {
		final HashMap<Integer, Marker> markersMap = new HashMap<>();
		final List<Marker> markerList = this.getMarkerDao().getMarkersByMapId(mapId);
		for (final Marker marker : markerList) {
			markersMap.put(marker.getMarkerId(), marker);
		}
		return markersMap;
	}

	@Override
	public List<String> getAllMarkerTypes(final int start, final int numOfRows) {
		return this.getMarkerDao().getAllMarkerTypes(start, numOfRows);
	}

	@Override
	public long countAllMarkerTypes() {
		return this.getMarkerDao().countAllMarkerTypes();
	}

	@Override
	public List<String> getMarkerNamesByMarkerType(final String markerType, final int start, final int numOfRows) {
		return this.getMarkerDao().getMarkerNamesByMarkerType(markerType, start, numOfRows);
	}

	@Override
	public long countMarkerNamesByMarkerType(final String markerType) {
		return this.getMarkerDao().countMarkerNamesByMarkerType(markerType);
	}

	@Override
	public List<Integer> getGIDsFromCharValuesByMarkerId(final Integer markerId, final int start, final int numOfRows) {
		return this.getCharValuesDao().getGIDsByMarkerId(markerId, start, numOfRows);
	}

	@Override
	public long countGIDsFromCharValuesByMarkerId(final Integer markerId) {
		return this.getCharValuesDao().countGIDsByMarkerId(markerId);
	}

	@Override
	public List<Integer> getGIDsFromAlleleValuesByMarkerId(final Integer markerId, final int start, final int numOfRows) {
		return this.getAlleleValuesDao().getGIDsByMarkerId(markerId, start, numOfRows);
	}

	@Override
	public List<Integer> getGIDsFromMappingPopValuesByMarkerId(final Integer markerId, final int start, final int numOfRows) {
		return this.getMappingPopValuesDao().getGIDsByMarkerId(markerId, start, numOfRows);
	}

	@Override
	public long countGIDsFromMappingPopValuesByMarkerId(final Integer markerId) {
		return this.getMappingPopValuesDao().countGIDsByMarkerId(markerId);
	}

	@Override
	public List<Integer> getGidsByMarkersAndAlleleValues(final List<Integer> markerIdList, final List<String> alleleValueList) {
		return this.getAlleleValuesDao().getGidsByMarkersAndAlleleValues(markerIdList, alleleValueList);
	}

	@Override
	public List<String> getAllDbAccessionIdsFromMarker(final int start, final int numOfRows) {

		return this.getMarkerDao().getAllDbAccessionIds(start, numOfRows);
	}

	@Override
	public long countAllDbAccessionIdsFromMarker() {
		return this.getMarkerDao().countAllDbAccessionIds();
	}

	@Override
	public List<AccMetadataSet> getAccMetadatasetsByDatasetIds(final List<Integer> datasetIds, final int start, final int numOfRows) {
		return this.getAccMetadataSetDao().getByDatasetIds(datasetIds, start, numOfRows);
	}

	public Set<Integer> getNIdsByMarkerIdsAndDatasetIdsAndNotGIds(
		final List<Integer> datasetIds, final List<Integer> markerIds,
		final List<Integer> gIds, final int start, final int numOfRows) {
		return this.getAccMetadataSetDao().getNIdsByMarkerIdsAndDatasetIdsAndNotGIds(datasetIds, markerIds, gIds, start, numOfRows);
	}

	@Override
	public int countNIdsByMarkerIdsAndDatasetIdsAndNotGIds(
		final List<Integer> datasetIds, final List<Integer> markerIds, final List<Integer> gIds) {
		return (int) this.getAccMetadataSetDao().countNIdsByMarkerIdsAndDatasetIdsAndNotGIds(datasetIds, markerIds, gIds);
	}

	private List<Integer> getNIdsByMarkerIdsAndDatasetIds(final List<Integer> datasetIds, final List<Integer> markerIds) {
		final Set<Integer> nidSet = new TreeSet<>();
		nidSet.addAll(this.getAccMetadataSetDao().getNIdsByMarkerIdsAndDatasetIds(datasetIds, markerIds));
		return new ArrayList<>(((TreeSet<Integer>) nidSet).descendingSet());
	}

	@Override
	public List<Integer> getNIdsByMarkerIdsAndDatasetIds(
		final List<Integer> datasetIds, final List<Integer> markerIds, final int start, final int numOfRows) {
		final List<Integer> nidList = this.getNIdsByMarkerIdsAndDatasetIds(datasetIds, markerIds);
		return nidList.subList(start, start + numOfRows);
	}

	@Override
	public int countNIdsByMarkerIdsAndDatasetIds(final List<Integer> datasetIds, final List<Integer> markerIds) {
		final List<Integer> nidList = this.getNIdsByMarkerIdsAndDatasetIds(datasetIds, markerIds);
		return nidList.size();
	}

	@Override
	public List<Integer> getDatasetIdsForFingerPrinting(final int start, final int numOfRows) {

		return this.getDatasetDao().getDatasetIdsForFingerPrinting(start, numOfRows);
	}

	@Override
	public long countDatasetIdsForFingerPrinting() {
		return this.getDatasetDao().countDatasetIdsForFingerPrinting();
	}

	@Override
	public List<Integer> getDatasetIdsForMapping(final int start, final int numOfRows) {
		return this.getDatasetDao().getDatasetIdsForMapping(start, numOfRows);
	}

	@Override
	public long countDatasetIdsForMapping() {
		return this.getDatasetDao().countDatasetIdsForMapping();
	}

	@Override
	public List<AccMetadataSet> getGdmsAccMetadatasetByGid(final List<Integer> gids, final int start, final int numOfRows) {
		return this.getAccMetadataSetDao().getAccMetadataSetsByGids(gids, start, numOfRows);
	}

	@Override
	public long countGdmsAccMetadatasetByGid(final List<Integer> gids) {
		return this.getAccMetadataSetDao().countAccMetadataSetsByGids(gids);
	}

	@Override
	public List<Integer> getMarkersBySampleIdAndDatasetIds(
		final Integer sampleId, final List<Integer> datasetIds, final int start, final int numOfRows) {
		return this.getMarkerMetadataSetDao().getMarkersBySampleIdAndDatasetIds(sampleId, datasetIds, start, numOfRows);
	}

	@Override
	public long countMarkersBySampleIdAndDatasetIds(final Integer sampleId, final List<Integer> datasetIds) {
		return this.getMarkerMetadataSetDao().countMarkersBySampleIdAndDatasetIds(sampleId, datasetIds);
	}

	@Override
	public List<Marker> getMarkersByMarkerIds(final List<Integer> markerIds, final int start, final int numOfRows) {
		return this.getMarkerDao().getMarkersByIds(markerIds, start, numOfRows);
	}

	@Override
	public long countMarkersByMarkerIds(final List<Integer> markerIds) {
		return this.getMarkerDao().countMarkersByIds(markerIds);
	}

	@Override
	public long countAlleleValuesByGids(final List<Integer> gids) {
		return this.getAlleleValuesDao().countAlleleValuesByGids(gids);
	}

	@Override
	public List<AllelicValueElement> getIntAlleleValuesForPolymorphicMarkersRetrieval(
		final List<Integer> gids, final int start, final int numOfRows) {
		final List<AllelicValueElement> allelicValueElements =
			this.getAlleleValuesDao().getIntAlleleValuesForPolymorphicMarkersRetrieval(gids, start, numOfRows);

		// Sort by gid, markerName
		Collections.sort(allelicValueElements, AllelicValueElement.AllelicValueElementComparator);
		return allelicValueElements;

	}

	@Override
	public long countIntAlleleValuesForPolymorphicMarkersRetrieval(final List<Integer> gids) {
		return this.getAlleleValuesDao().countIntAlleleValuesForPolymorphicMarkersRetrieval(gids);
	}

	@Override
	public List<AllelicValueElement> getCharAlleleValuesForPolymorphicMarkersRetrieval(
		final List<Integer> gids, final int start, final int numOfRows) {
		final List<AllelicValueElement> allelicValueElements =
			this.getAlleleValuesDao().getCharAlleleValuesForPolymorphicMarkersRetrieval(gids, start, numOfRows);

		// Sort by gid, markerName
		Collections.sort(allelicValueElements, AllelicValueElement.AllelicValueElementComparator);
		return allelicValueElements;
	}

	@Override
	public long countCharAlleleValuesForPolymorphicMarkersRetrieval(final List<Integer> gids) {
		return this.getAlleleValuesDao().countCharAlleleValuesForPolymorphicMarkersRetrieval(gids);
	}

	@Override
	public List<AllelicValueElement> getMappingAlleleValuesForPolymorphicMarkersRetrieval(
		final List<Integer> gids, final int start, final int numOfRows) {
		final List<AllelicValueElement> allelicValueElements =
			this.getAlleleValuesDao().getMappingAlleleValuesForPolymorphicMarkersRetrieval(gids, start, numOfRows);

		// Sort by gid, markerName
		Collections.sort(allelicValueElements, AllelicValueElement.AllelicValueElementComparator);
		return allelicValueElements;

	}

	@Override
	public long countMappingAlleleValuesForPolymorphicMarkersRetrieval(final List<Integer> gids) {
		return this.getAlleleValuesDao().countMappingAlleleValuesForPolymorphicMarkersRetrieval(gids);
	}

	@Override
	public List<Qtl> getAllQtl(final int start, final int numOfRows) {
		return this.getQtlDao().getAll(start, numOfRows);
	}

	@Override
	public long countAllQtl() {
		return this.countAll(this.getQtlDao());
	}

	@Override
	public List<Integer> getQtlIdByName(final String name, final int start, final int numOfRows) {
		if (name == null || name.isEmpty()) {
			return new ArrayList<>();
		}

		return this.getQtlDao().getQtlIdByName(name, start, numOfRows);
	}

	@Override
	public long countQtlIdByName(final String name) {
		if (name == null || name.isEmpty()) {
			return 0;
		}
		return this.getQtlDao().countQtlIdByName(name);
	}

	@Override
	public List<QtlDetailElement> getQtlByName(final String name, final int start, final int numOfRows) {
		final List<QtlDetailElement> qtlDetailElements = new ArrayList<>();
		if (name == null || name.isEmpty()) {
			return qtlDetailElements;
		}

		return this.getQtlDao().getQtlAndQtlDetailsByName(name, start, numOfRows);

	}

	@Override
	public long countQtlByName(final String name) {
		if (name == null || name.isEmpty()) {
			return 0;
		}
		return this.getQtlDao().countQtlAndQtlDetailsByName(name);
	}

	@Override
	public java.util.Map<Integer, String> getQtlNamesByQtlIds(final List<Integer> qtlIds) {
		final java.util.Map<Integer, String> qtlNames = new HashMap<>();
		qtlNames.putAll(this.getQtlDao().getQtlNameByQtlIds(qtlIds));
		return qtlNames;
	}

	// TODO BMS-148 : Review for how to safely remove the dual db read pattern without breaking any logic.
	@Override
	public List<QtlDetailElement> getQtlByQtlIds(final List<Integer> qtlIds, final int start, final int numOfRows) {
		final List<QtlDetailElement> qtlDetailElements = new ArrayList<>();

		if (qtlIds == null || qtlIds.isEmpty()) {
			return qtlDetailElements;
		}

		return this.getQtlDao().getQtlAndQtlDetailsByQtlIds(qtlIds, start, numOfRows);
	}

	@Override
	public long countQtlByQtlIds(final List<Integer> qtlIds) {
		if (qtlIds == null || qtlIds.isEmpty()) {
			return 0;
		}
		return this.getQtlDao().countQtlAndQtlDetailsByQtlIds(qtlIds);
	}

	@Override
	public List<Integer> getQtlByTrait(final Integer trait, final int start, final int numOfRows) {

		return this.getQtlDao().getQtlByTrait(trait, start, numOfRows);
	}

	@Override
	public long countQtlByTrait(final Integer trait) {
		return this.getQtlDao().countQtlByTrait(trait);
	}

	@Override
	public List<Integer> getQtlTraitsByDatasetId(final Integer datasetId, final int start, final int numOfRows) {
		return this.getQtlDetailsDao().getQtlTraitsByDatasetId(datasetId, start, numOfRows);
	}

	@Override
	public long countQtlTraitsByDatasetId(final Integer datasetId) {
		return this.getQtlDetailsDao().countQtlTraitsByDatasetId(datasetId);
	}

	@Override
	public List<ParentElement> getAllParentsFromMappingPopulation(final int start, final int numOfRows) {
		return this.getMappingPopDao().getAllParentsFromMappingPopulation(start, numOfRows);
	}

	@Override
	public Long countAllParentsFromMappingPopulation() {
		return this.getMappingPopDao().countAllParentsFromMappingPopulation();
	}

	@Override
	public List<MapDetailElement> getMapDetailsByName(final String nameLike, final int start, final int numOfRows) {
		return this.getMapDao().getMapDetailsByName(nameLike, start, numOfRows);
	}

	@Override
	public Long countMapDetailsByName(final String nameLike) {
		return this.getMapDao().countMapDetailsByName(nameLike);
	}

	@Override
	public java.util.Map<Integer, List<String>> getMapNamesByMarkerIds(final List<Integer> markerIds) {

		final java.util.Map<Integer, List<String>> markerMaps = new HashMap<>();

		if (markerIds == null || markerIds.isEmpty()) {
			return markerMaps;
		}

		markerMaps.putAll(this.getMarkerOnMapDao().getMapNameByMarkerIds(markerIds));
		return markerMaps;
	}

	@Override
	public List<MapDetailElement> getAllMapDetails(final int start, final int numOfRows) {
		return this.getMapDao().getAllMapDetails(start, numOfRows);
	}

	@Override
	public long countAllMapDetails() {
		return this.getMapDao().countAllMapDetails();
	}

	@Override
	public List<Integer> getMapIdsByQtlName(final String qtlName, final int start, final int numOfRows) {
		return this.getQtlDetailsDao().getMapIdsByQtlName(qtlName, start, numOfRows);
	}

	@Override
	public long countMapIdsByQtlName(final String qtlName) {
		return this.getQtlDetailsDao().countMapIdsByQtlName(qtlName);
	}

	@Override
	public List<Integer> getMarkerIdsByQtl(
		final String qtlName, final String chromosome, final float min, final float max, final int start, final int numOfRows) {
		return this.getQtlDetailsDao().getMarkerIdsByQtl(qtlName, chromosome, min, max, start, numOfRows);
	}

	@Override
	public long countMarkerIdsByQtl(final String qtlName, final String chromosome, final float min, final float max) {
		return this.getQtlDetailsDao().countMarkerIdsByQtl(qtlName, chromosome, min, max);
	}

	@Override
	public List<Marker> getMarkersByIds(final List<Integer> markerIds, final int start, final int numOfRows) {
		final List<Marker> markers = new ArrayList<>();
		markers.addAll(this.getMarkerDao().getMarkersByIds(markerIds, start, numOfRows));
		return markers;
	}

	@Override
	public Integer addQtlDetails(final QtlDetails qtlDetails) {

		Integer savedId = null;
		try {

			// No need to auto-assign negative IDs for new local DB records
			// qtlId and mapId are foreign keys

			final QtlDetails recordSaved = this.getQtlDetailsDao().save(qtlDetails);
			savedId = recordSaved.getQtlId();

		} catch (final Exception e) {
			throw new MiddlewareQueryException("Error encountered while saving Qtl Details: GenotypicDataManager.addQtlDetails(qtlDetails="
				+ qtlDetails + "): " + e.getMessage(), e);
		}
		return savedId;

	}

	@Override
	public Integer addMarker(final Marker marker) {
		return ((Marker) super.save(this.getMarkerDao(), marker)).getMarkerId();
	}

	@Override
	public Integer addMarkerDetails(final MarkerDetails markerDetails) {
		final MarkerDetailsDAO dao = this.getMarkerDetailsDao();
		final MarkerDetails details = dao.getById(markerDetails.getMarkerId());
		if (details == null) {
			return ((MarkerDetails) super.save(dao, details)).getMarkerId();
		}

		return details.getMarkerId();
	}

	@Override
	public Integer addMarkerUserInfo(final MarkerUserInfo markerUserInfo) {
		return ((MarkerUserInfo) super.save(this.getMarkerUserInfoDao(), markerUserInfo)).getUserInfoId();
	}

	@Override
	public Integer addAccMetadataSet(final AccMetadataSet accMetadataSet) {

		Integer savedId = null;

		try {

			final AccMetadataSet recordSaved = this.getAccMetadataSetDao().save(accMetadataSet);
			savedId = recordSaved.getAccMetadataSetId();

		} catch (final Exception e) {
			throw new MiddlewareQueryException("Error encountered with addAccMetadataSet(accMetadataSet=" + accMetadataSet + "): "
				+ e.getMessage(), e);
		}
		return savedId;
	}

	@Override
	public Integer addMarkerMetadataSet(final MarkerMetadataSet markerMetadataSet) {

		Integer savedId = null;

		try {

			final MarkerMetadataSet recordSaved = this.getMarkerMetadataSetDao().save(markerMetadataSet);
			savedId = recordSaved.getMarkerMetadataSetId();

		} catch (final Exception e) {
			throw new MiddlewareQueryException("Error encountered with addMarkerMetadataSet(markerMetadataSet=" + markerMetadataSet + "): "
				+ e.getMessage(), e);
		}
		return savedId;
	}

	@Override
	public Integer addDataset(final Dataset dataset) {
		return ((Dataset) super.save(this.getDatasetDao(), dataset)).getDatasetId();
	}

	@Override
	public Integer addGDMSMarker(final Marker marker) {
		// Check for existence. duplicate marker names are not allowed.

		Integer id = null;
		try {

			id = this.saveMarkerIfNotExisting(marker, marker.getMarkerType());

		} catch (final Exception e) {
			throw new MiddlewareQueryException("Error encountered while adding Marker: " + e.getMessage(), e);
		}
		return id;
	}

	@Override
	public Integer addGDMSMarkerAlias(final MarkerAlias markerAlias) {
		return ((MarkerAlias) super.save(this.getMarkerAliasDao(), markerAlias)).getMarkerId();
	}

	@Override
	public Integer addDatasetUser(final DatasetUsers datasetUser) {
		final DatasetUsersDAO dao = this.getDatasetUsersDao();
		final DatasetUsers user = dao.getById(datasetUser.getDataset().getDatasetId());
		if (user == null) {
			return ((DatasetUsers) super.save(dao, datasetUser)).getUserId();
		}

		return user.getUserId();
	}

	@Override
	public Integer addAlleleValues(final AlleleValues alleleValues) {
		return ((AlleleValues) super.saveOrUpdate(this.getAlleleValuesDao(), alleleValues)).getAnId();
	}

	@Override
	public Integer addCharValues(final CharValues charValues) {
		return ((CharValues) super.saveOrUpdate(this.getCharValuesDao(), charValues)).getAcId();
	}

	@Override
	public Integer addMappingPop(final MappingPop mappingPop) {

		final MappingPopDAO dao = this.getMappingPopDao();
		final MappingPop popFromDB = dao.getById(mappingPop.getDatasetId());
		if (popFromDB == null) {
			return ((MappingPop) super.save(dao, mappingPop)).getDatasetId();
		}

		return mappingPop.getDatasetId();
	}

	@Override
	public Integer addMappingPopValue(final MappingPopValues mappingPopValue) {
		return ((MappingPopValues) super.saveOrUpdate(this.getMappingPopValuesDao(), mappingPopValue)).getMpId();
	}

	@Override
	public Integer addMarkerOnMap(final MarkerOnMap markerOnMap) {
		if (this.getMapDao().getById(markerOnMap.getMapId()) == null) {
			throw new MiddlewareQueryException("Map Id not found: " + markerOnMap.getMapId());
		}

		return ((MarkerOnMap) super.save(this.getMarkerOnMapDao(), markerOnMap)).getMapId();
	}

	@Override
	public Integer addDartValue(final DartValues dartValue) {
		return ((DartValues) super.save(this.getDartValuesDao(), dartValue)).getAdId();
	}

	@Override
	public Integer addQtl(final Qtl qtl) {
		return ((Qtl) super.saveOrUpdate(this.getQtlDao(), qtl)).getQtlId();
	}

	@Override
	public Integer addMap(final Map map) {
		return ((Map) super.saveOrUpdate(this.getMapDao(), map)).getMapId();
	}

	@Override
	public Boolean setSNPMarkers(
		final Marker marker, final MarkerAlias markerAlias, final MarkerDetails markerDetails, final MarkerUserInfo markerUserInfo) {
		return this.setMarker(marker, GenotypicDataManagerImpl.TYPE_SNP, markerAlias, markerDetails, markerUserInfo);
	}

	@Override
	public Boolean setCAPMarkers(
		final Marker marker, final MarkerAlias markerAlias, final MarkerDetails markerDetails, final MarkerUserInfo markerUserInfo) {
		return this.setMarker(marker, GenotypicDataManagerImpl.TYPE_CAP, markerAlias, markerDetails, markerUserInfo);
	}

	@Override
	public Boolean setCISRMarkers(
		final Marker marker, final MarkerAlias markerAlias, final MarkerDetails markerDetails, final MarkerUserInfo markerUserInfo) {
		return this.setMarker(marker, GenotypicDataManagerImpl.TYPE_CISR, markerAlias, markerDetails, markerUserInfo);
	}

	private Boolean setMarker(
		final Marker marker, final String markerType, final MarkerAlias markerAlias, final MarkerDetails markerDetails,
		final MarkerUserInfo markerUserInfo) {

		try {
			// Add GDMS Marker
			final Integer idGDMSMarkerSaved = this.saveMarkerIfNotExisting(marker, markerType);
			marker.setMarkerId(idGDMSMarkerSaved);
			marker.setMarkerType(markerType);

			// Add GDMS Marker Alias
			markerAlias.setMarkerId(idGDMSMarkerSaved);
			this.saveMarkerAlias(markerAlias);

			// Add Marker Details
			markerDetails.setMarkerId(idGDMSMarkerSaved);
			this.saveMarkerDetails(markerDetails);

			// Add marker user info
			markerUserInfo.setMarkerId(idGDMSMarkerSaved);
			this.saveMarkerUserInfo(markerUserInfo);

			return true;
		} catch (final Exception e) {
			throw new MiddlewareQueryException("Error encountered while saving Marker: " + e.getMessage(), e);
		}
	}

	@Override
	public Boolean setMaps(final Marker marker, final MarkerOnMap markerOnMap, final Map map) {

		try {

			final Integer markerSavedId = this.saveMarker(marker, GenotypicDataManagerImpl.TYPE_UA);
			final Integer mapSavedId = this.saveMap(map);
			this.saveMarkerOnMap(markerSavedId, mapSavedId, markerOnMap);

			return true;
		} catch (final Exception e) {
			throw new MiddlewareQueryException("Error encountered while setting Maps: setMaps(): " + e.getMessage(), e);
		}
	}

	private Integer getMarkerIdByMarkerName(final String markerName) throws MiddlewareException {
		return this.getMarkerDao().getIdByName(markerName);
	}

	private Integer getMapIdByMapName(final String mapName) {
		return this.getMapDao().getMapIdByName(mapName);
	}

	@Override
	public List<QtlDataElement> getQtlDataByQtlTraits(final List<Integer> qtlTraitIds, final int start, final int numOfRows) {
		return this.getQtlDetailsDao().getQtlDataByQtlTraits(qtlTraitIds, start, numOfRows);
	}

	@Override
	public long countQtlDataByQtlTraits(final List<Integer> qtlTraits) {
		return this.getQtlDetailsDao().countQtlDataByQtlTraits(qtlTraits);
	}

	@Override
	public List<QtlDetailElement> getQtlDetailsByQtlTraits(final List<Integer> qtlTraitIds, final int start, final int numOfRows) {
		return this.getQtlDao().getQtlDetailsByQtlTraits(qtlTraitIds, start, numOfRows);
	}

	@Override
	public long countQtlDetailsByQtlTraits(final List<Integer> qtlTraits) {
		return this.getQtlDao().countQtlDetailsByQtlTraits(qtlTraits);
	}

	@Override
	public long countAccMetadatasetByDatasetIds(final List<Integer> datasetIds) {
		return this.getAccMetadataSetDao().countSampleIdsByDatasetIds(datasetIds);
	}

	@Override
	public long countMarkersFromMarkerMetadatasetByDatasetIds(final List<Integer> datasetIds) {
		return this.getMarkerMetadataSetDao().countByDatasetIds(datasetIds);
	}

	@Override
	public Integer getMapIdByName(final String mapName) {
		return this.getMapDao().getMapIdByName(mapName);
	}

	@Override
	public long countMappingPopValuesByGids(final List<Integer> gIds) {
		return this.getMappingPopValuesDao().countByGids(gIds);
	}

	@Override
	public long countMappingAlleleValuesByGids(final List<Integer> gIds) {
		return this.getAlleleValuesDao().countByGids(gIds);
	}

	@Override
	public List<MarkerMetadataSet> getAllFromMarkerMetadatasetByMarkers(final List<Integer> markerIds) {
		return this.getMarkerMetadataSetDao().getByMarkerIds(markerIds);
	}

	@Override
	public Dataset getDatasetById(final Integer datasetId) {
		return this.getDatasetDao().getById(datasetId);
	}

	@Override
	public List<Dataset> getDatasetsByType(final GdmsType type) {
		return this.getDatasetDao().getDatasetsByType(type.getValue());
	}

	@Override
	public MappingPop getMappingPopByDatasetId(final Integer datasetId) {
		return this.getMappingPopDao().getMappingPopByDatasetId(datasetId);
	}

	@Override
	public List<Dataset> getDatasetDetailsByDatasetIds(final List<Integer> datasetIds) {
		return this.getDatasetDao().getDatasetsByIds(datasetIds);

	}

	@Override
	public List<Integer> getQTLIdsByDatasetIds(final List<Integer> datasetIds) {
		return this.getQtlDao().getQTLIdsByDatasetIds(datasetIds);
	}

	@Override
	public List<AccMetadataSet> getAllFromAccMetadataset(final List<Integer> gIds, final Integer datasetId, final SetOperation operation) {
		return this.getAccMetadataSetDao().getAccMetadataSetByGidsAndDatasetId(gIds, datasetId, operation);
	}

	@Override
	public List<MapDetailElement> getMapAndMarkerCountByMarkers(final List<Integer> markerIds) {
		return this.getMapDao().getMapAndMarkerCountByMarkers(markerIds);
	}

	@Override
	public List<Mta> getAllMTAs() {
		return this.getMtaDao().getAll();
	}

	@Override
	public long countAllMTAs() {
		return this.getMtaDao().countAll();
	}

	@Override
	public List<Mta> getMTAsByTrait(final Integer traitId) {
		return this.getMtaDao().getMtasByTrait(traitId);
	}

	@Override
	public void deleteQTLs(final List<Integer> qtlIds, final Integer datasetId) {

		try {

			// delete qtl and qtl details
			this.getQtlDetailsDao().deleteByQtlIds(qtlIds);
			this.getQtlDao().deleteByQtlIds(qtlIds);

			// delete dataset users and dataset
			this.getDatasetUsersDao().deleteByDatasetId(datasetId);
			this.getDatasetDao().deleteByDatasetId(datasetId);

		} catch (final Exception e) {

			this.logAndThrowException("Cannot delete QTLs and Dataset: GenotypicDataManager.deleteQTLs(qtlIds=" + qtlIds
				+ " and datasetId = " + datasetId + "):  " + e.getMessage(), e);
		}
	}

	@Override
	public void deleteSNPGenotypingDatasets(final Integer datasetId) {

		try {

			this.getCharValuesDao().deleteByDatasetId(datasetId);
			this.getDatasetUsersDao().deleteByDatasetId(datasetId);
			this.getAccMetadataSetDao().deleteByDatasetId(datasetId);
			this.getMarkerMetadataSetDao().deleteByDatasetId(datasetId);
			this.getDatasetDao().deleteByDatasetId(datasetId);

		} catch (final Exception e) {

			this.logAndThrowException(
				"Cannot delete SNP Genotyping Datasets: " + "GenotypicDataManager.deleteSNPGenotypingDatasets(datasetId = " + datasetId
					+ "):  " + e.getMessage(), e);
		}
	}

	@Override
	public void deleteMappingPopulationDatasets(final Integer datasetId) {

		try {

			this.getMappingPopValuesDao().deleteByDatasetId(datasetId);
			this.getMappingPopDao().deleteByDatasetId(datasetId);
			this.getDatasetUsersDao().deleteByDatasetId(datasetId);
			this.getAccMetadataSetDao().deleteByDatasetId(datasetId);
			this.getMarkerMetadataSetDao().deleteByDatasetId(datasetId);

			// DELETE from char_values - there will be entries for the given datasetId if markerType = SNP
			this.getCharValuesDao().deleteByDatasetId(datasetId);

			// DELETE from allele_values - there will be entries for the given datasetId if markerType = SSR or DART
			this.getAlleleValuesDao().deleteByDatasetId(datasetId);

			// DELETE from dart_values - there will be entries for the given datasetId if markerType = DART
			this.getDartValuesDao().deleteByDatasetId(datasetId);

			this.getDatasetDao().deleteByDatasetId(datasetId);

		} catch (final Exception e) {

			this.logAndThrowException("Cannot delete Mapping Population Datasets: "
				+ "GenotypicDataManager.deleteMappingPopulationDatasets(datasetId = " + datasetId + "):  " + e.getMessage(), e);
		}
	}

	@Override
	public List<QtlDetails> getQtlDetailsByMapId(final Integer mapId) {
		return this.getQtlDetailsDao().getQtlDetailsByMapId(mapId);
	}

	@Override
	public long countQtlDetailsByMapId(final Integer mapId) {
		return this.getQtlDetailsDao().countQtlDetailsByMapId(mapId);
	}

	@Override
	public void deleteMaps(final Integer mapId) {

		try {

			this.getMarkerOnMapDao().deleteByMapId(mapId);
			this.getMapDao().deleteByMapId(mapId);

		} catch (final Exception e) {

			this.logAndThrowException("Cannot delete Mapping Population Datasets: "
				+ "GenotypicDataManager.deleteMappingPopulationDatasets(datasetId = " + mapId + "):  " + e.getMessage(), e);
		}
	}

	@Override
	public List<MarkerSampleId> getMarkerFromCharValuesByGids(final List<Integer> gIds) {
		return this.getCharValuesDao().getMarkerSampleIdsByGids(gIds);
	}

	@Override
	public List<MarkerSampleId> getMarkerFromAlleleValuesByGids(final List<Integer> gIds) {
		return this.getAlleleValuesDao().getMarkerSampleIdsByGids(gIds);
	}

	@Override
	public List<MarkerSampleId> getMarkerFromMappingPopByGids(final List<Integer> gIds) {
		return this.getMappingPopValuesDao().getMarkerSampleIdsByGids(gIds);
	}

	@Override
	public void addMTA(final Dataset dataset, final Mta mta, final MtaMetadata mtaMetadata, final DatasetUsers users) {

		if (dataset == null) {
			throw new MiddlewareQueryException("Dataset passed must not be null");
		}

		try {

			dataset.setDatasetType(GenotypicDataManagerImpl.TYPE_MTA);
			dataset.setUploadTemplateDate(new Date());

			this.getDatasetDao().merge(dataset);

			users.setDataset(dataset);
			this.getDatasetUsersDao().merge(users);

			final MtaDAO mtaDao = this.getMtaDao();
			mta.setDatasetId(dataset.getDatasetId());
			mtaDao.save(mta);

			final MtaMetadataDAO mtaMetadataDao = this.getMtaMetadataDao();
			mtaMetadata.setDatasetID(dataset.getDatasetId());
			mtaMetadataDao.merge(mtaMetadata);

		} catch (final Exception e) {

			this.logAndThrowException("Error in GenotypicDataManager.addMTA: " + e.getMessage(), e);
		}
	}

	@Override
	public void setMTA(final Dataset dataset, final DatasetUsers users, final List<Mta> mtaList, final MtaMetadata mtaMetadata) {

		if (dataset == null) {
			throw new MiddlewareQueryException("Dataset passed must not be null");
		}

		try {

			dataset.setDatasetType(GenotypicDataManagerImpl.TYPE_MTA);
			dataset.setUploadTemplateDate(new Date());

			this.getDatasetDao().merge(dataset);

			users.setDataset(dataset);
			this.getDatasetUsersDao().merge(users);

			final MtaDAO mtaDao = this.getMtaDao();
			final MtaMetadataDAO mtaMetadataDao = this.getMtaMetadataDao();

			for (final Mta mta : mtaList) {
				mta.setDatasetId(dataset.getDatasetId());
				mtaDao.merge(mta);

				mtaMetadata.setDatasetID(dataset.getDatasetId());
				mtaMetadataDao.merge(mtaMetadata);
			}

		} catch (final Exception e) {

			this.logAndThrowException("Error in GenotypicDataManager.addMTAs: " + e.getMessage(), e);
		}
	}

	@Override
	public void deleteMTA(final List<Integer> datasetIds) {

		try {

			for (final Integer datasetId : datasetIds) {

				// delete mta, dataset users and dataset
				this.getMtaDao().deleteByDatasetId(datasetId);
				this.getDatasetUsersDao().deleteByDatasetId(datasetId);
				this.getDatasetDao().deleteByDatasetId(datasetId);
			}

		} catch (final Exception e) {

			this.logAndThrowException("Cannot delete MTAs and Dataset: GenotypicDataManager.deleteMTA(datasetIds=" + datasetIds + "):  "
				+ e.getMessage(), e);
		}

	}

	@Override
	public void addMtaMetadata(final MtaMetadata mtaMetadata) {

		if (mtaMetadata == null) {
			throw new MiddlewareQueryException("Error in GenotypicDataManager.addMtaMetadata: MtaMetadata must not be null.");
		}
		if (mtaMetadata.getDatasetID() == null) {
			throw new MiddlewareQueryException("Error in GenotypicDataManager.addMtaMetadata: MtaMetadata.datasetID must not be null.");
		}

		try {

			// No need to generate id. The id (mta_id) is a foreign key
			final MtaMetadataDAO mtaMetadataDao = this.getMtaMetadataDao();
			mtaMetadataDao.save(mtaMetadata);

		} catch (final Exception e) {

			this.logAndThrowException("Error in GenotypicDataManager.addMtaMetadata: " + e.getMessage(), e);
		}
	}

	// --------------------------------- COMMON SAVER METHODS ------------------------------------------//

	private Integer saveMarkerIfNotExisting(final Marker marker, final String markerType) throws Exception {
		this.getActiveSession();

		Integer markerId = marker.getMarkerId();

		// If the marker has same marker name existing in local, use the existing record.
		if (markerId == null) {
			final Integer markerIdWithName = this.getMarkerIdByMarkerName(marker.getMarkerName());
			if (markerIdWithName != null) {
				markerId = markerIdWithName;
			}
		}

		if (markerId != null) {
			throw new MiddlewareException("Marker already exists in Central or Local and cannot be added.");
		}

		// If the marker is not yet existing in the database (local and central) - should create a new marker in the local database.
		if (markerId == null) {
			this.getActiveSession();
			final MarkerDAO markerDao = this.getMarkerDao();
			marker.setMarkerType(markerType);
			final Marker markerRecordSaved = markerDao.saveOrUpdate(marker);
			markerId = markerRecordSaved.getMarkerId();
		}

		if (markerId == null) {
			throw new Exception(); // To immediately roll back and to avoid executing the other insert functions
		}

		return markerId;
	}

	// If the marker is not yet in the database, add.
	private Integer saveMarker(final Marker marker, final String markerType) throws Exception {
		Integer markerId = marker.getMarkerId();

		// If the marker has same marker name existing in local, use the existing record.
		if (markerId == null) {
			this.getActiveSession();
			this.getMarkerIdByMarkerName(marker.getMarkerName());
		}

		// Save the marker
		this.getActiveSession();
		final MarkerDAO markerDao = this.getMarkerDao();
		marker.setMarkerType(markerType);
		final Marker markerRecordSaved = markerDao.merge(marker);
		markerId = markerRecordSaved.getMarkerId();

		if (markerId == null) {
			throw new Exception(); // To immediately roll back and to avoid executing the other insert functions
		}

		return markerId;
	}

	private void saveMarkers(final List<Marker> markers) throws Exception {

		this.getActiveSession();
		final MarkerDAO markerDao = this.getMarkerDao();
		if (markers != null) {
			for (final Marker marker : markers) {
				markerDao.merge(marker);
			}
		}
	}

	private void updateMarkerInfo(final Marker marker) throws Exception {

		if (marker == null || marker.getMarkerId() == null) {
			throw new MiddlewareException("Marker is null and cannot be updated.");
		}

		this.getActiveSession();
		final MarkerDAO markerDao = this.getMarkerDao();

		final Integer markerId = marker.getMarkerId();
		// Marker id, name and species cannot be updated.
		final Marker markerFromDB = this.getMarkerDao().getById(markerId);
		if (markerFromDB == null) {
			throw new MiddlewareException("Marker is not found in the database and cannot be updated.");
		}
		if (!marker.getMarkerName().equals(markerFromDB.getMarkerName()) || !marker.getSpecies().equals(markerFromDB.getSpecies())) {
			throw new MiddlewareException("Marker name and species cannot be updated.");
		}

		markerDao.merge(marker);

	}

	private Integer saveMarkerAlias(final MarkerAlias markerAlias) throws Exception {
		this.getActiveSession();

		final MarkerAlias markerAliasRecordSaved = this.getMarkerAliasDao().save(markerAlias);
		final Integer markerAliasRecordSavedMarkerId = markerAliasRecordSaved.getMarkerId();
		if (markerAliasRecordSavedMarkerId == null) {
			throw new Exception();
		}
		return markerAliasRecordSavedMarkerId;
	}

	private Integer saveOrUpdateMarkerAlias(final MarkerAlias markerAlias) throws Exception {
		this.getActiveSession();
		final MarkerAlias markerAliasFromDB = this.getMarkerAliasDao().getById(markerAlias.getMarkerId());
		if (markerAliasFromDB == null) {
			return this.saveMarkerAlias(markerAlias);
		} else {
			this.getMarkerAliasDao().merge(markerAlias);
		}
		return markerAlias.getMarkerId();
	}

	private Integer saveMarkerDetails(final MarkerDetails markerDetails) throws Exception {
		this.getActiveSession();
		final MarkerDetails markerDetailsRecordSaved = this.getMarkerDetailsDao().save(markerDetails);
		final Integer markerDetailsSavedMarkerId = markerDetailsRecordSaved.getMarkerId();
		if (markerDetailsSavedMarkerId == null) {
			throw new Exception();
		}
		return markerDetailsSavedMarkerId;
	}

	private Integer saveOrUpdateMarkerDetails(final MarkerDetails markerDetails) throws Exception {
		this.getActiveSession();
		final MarkerDetails markerDetailsFromDB = this.getMarkerDetailsDao().getById(markerDetails.getMarkerId());
		if (markerDetailsFromDB == null) {
			return this.saveMarkerDetails(markerDetails);
		} else {
			this.getMarkerDetailsDao().merge(markerDetails);
		}
		return markerDetails.getMarkerId();
	}

	private Integer saveMarkerUserInfo(final MarkerUserInfo markerUserInfo) throws Exception {
		this.getActiveSession();

		final MarkerUserInfoDAO dao = this.getMarkerUserInfoDao();
		final MarkerUserInfo markerUserInfoRecordSaved = dao.save(markerUserInfo);
		final Integer markerUserInfoSavedId = markerUserInfoRecordSaved.getMarkerId();
		if (markerUserInfoSavedId == null) {
			throw new Exception();
		}
		return markerUserInfoSavedId;
	}

	private Integer saveOrUpdateMarkerUserInfo(final MarkerUserInfo markerUserInfo) throws Exception {
		this.getActiveSession();
		final MarkerUserInfoDAO dao = this.getMarkerUserInfoDao();

		if (markerUserInfo.getUserInfoId() != null) {
			final MarkerUserInfo markerDetailsFromDB = this.getMarkerUserInfoDao().getById(markerUserInfo.getUserInfoId());
			if (markerDetailsFromDB == null) {
				return this.saveMarkerUserInfo(markerUserInfo);
			}
		}
		dao.merge(markerUserInfo);
		return markerUserInfo.getUserInfoId();
	}

	private Integer saveMap(final Map map) throws Exception {
		this.getActiveSession();

		Integer mapSavedId = map.getMapId() == null ? this.getMapIdByMapName(map.getMapName()) : map.getMapId();
		if (mapSavedId == null) {
			final MapDAO mapDao = this.getMapDao();

			final Map mapRecordSaved = mapDao.saveOrUpdate(map);
			mapSavedId = mapRecordSaved.getMapId();
		}

		if (mapSavedId == null) {
			throw new Exception(); // To immediately roll back and to avoid executing the other insert functions
		}
		return mapSavedId;

	}

	private Integer saveMarkerOnMap(final Integer markerId, final Integer mapId, final MarkerOnMap markerOnMap) throws Exception {
		this.getActiveSession();
		final MarkerOnMapDAO markerOnMapDao = this.getMarkerOnMapDao();

		markerOnMap.setMarkerId(markerId);
		markerOnMap.setMapId(mapId);

		if (markerOnMapDao.findByMarkerIdAndMapId(markerId, mapId) != null) {
			throw new Exception("The marker on map combination already exists (markerId=" + markerId + ", mapId=" + mapId + ")");
		}
		final MarkerOnMap markerOnMapRecordSaved = markerOnMapDao.save(markerOnMap);
		final Integer markerOnMapSavedId = markerOnMapRecordSaved.getMapId();

		if (markerOnMapSavedId == null) {
			throw new Exception();
		}
		return markerOnMapSavedId;

	}

	// GCP-7873
	@Override
	public List<Marker> getAllSNPMarkers() {
		return this.getMarkerDao().getByType(GenotypicDataManagerImpl.TYPE_SNP);
	}

	// GCP-8568
	@Override
	public List<Marker> getMarkersByType(final String type) {
		return this.getMarkerDao().getMarkersByType(type);
	}

	// GCP-7874
	@Override
	public List<Marker> getSNPsByHaplotype(final String haplotype) {
		final List<Integer> markerIds = this.getMarkerDao().getMarkerIDsByHaplotype(haplotype);
		return this.getMarkerDao().getMarkersByIdsAndType(markerIds, GdmsType.TYPE_SNP.getValue());
	}

	// GCP-8566
	@Override
	public void addHaplotype(final TrackData trackData, final List<TrackMarker> trackMarkers) {

		try {

			this.getTrackDataDao().save(trackData);

			for (final TrackMarker trackMarker : trackMarkers) {
				trackMarker.setTrackId(trackData.getTrackId());
				this.getTrackMarkerDao().save(trackMarker);
			}

		} catch (final Exception e) {

			this.logAndThrowException("Error in GenotypicDataManager.addHaplotype(trackData=" + trackData + ", trackMarkers="
				+ trackMarkers + "): " + e.getMessage(), e);
		}

	}

	// GCP-7881
	@Override
	public List<MarkerInfo> getMarkerInfoByMarkerIds(final List<Integer> markerIds) {
		final List<MarkerInfo> returnVal = new ArrayList<MarkerInfo>();
		returnVal.addAll(this.getMarkerInfoDao().getByMarkerIds(markerIds));
		return returnVal;
	}

	// GCP-7875
	@Override
	public List<AllelicValueElement> getAlleleValuesByMarkers(final List<Integer> markerIds) {
		final List<AllelicValueElement> returnVal = new ArrayList<>();
		returnVal.addAll(this.getAlleleValuesDao().getAlleleValuesByMarkerId(markerIds));
		returnVal.addAll(this.getCharValuesDao().getAlleleValuesByMarkerId(markerIds));
		return returnVal;
	}

	@Override
	public Boolean updateMarkerInfo(
		final Marker marker, final MarkerAlias markerAlias, final MarkerDetails markerDetails, final MarkerUserInfo markerUserInfo) {

		if (marker.getMarkerId() >= 0) {
			final Marker markerFromDB = this.getMarkerDao().getById(marker.getMarkerId());
			if (markerFromDB != null) {
				throw new MiddlewareQueryException("Marker is in central database and cannot be updated.");
			} else {
				throw new MiddlewareQueryException("The given marker has positive id but is not found in central. Update cannot proceed.");
			}
		}

		try {

			// Update GDMS Marker - update all fields except marker_id, marker_name and species
			this.updateMarkerInfo(marker);
			final Integer markerId = marker.getMarkerId();

			// Add or Update GDMS Marker Alias
			markerAlias.setMarkerId(markerId);
			this.saveOrUpdateMarkerAlias(markerAlias);

			// Add or Update Marker Details
			markerDetails.setMarkerId(markerId);
			this.saveOrUpdateMarkerDetails(markerDetails);

			// Add or update marker user info
			markerUserInfo.setMarkerId(markerId);
			this.saveOrUpdateMarkerUserInfo(markerUserInfo);

			return true;

		} catch (final Exception e) {
			throw new MiddlewareQueryException("Error encountered while updating MarkerInfo: updateMarkerInfo(marker=" + marker
				+ ", markerAlias=" + markerAlias + ", markerDetails=" + markerDetails + ", markerUserInfo=" + markerUserInfo + "): "
				+ e.getMessage(), e);
		}
	}

	@Override
	public List<MarkerMetadataSet> getMarkerMetadataSetByDatasetId(final Integer datasetId) {
		return this.getMarkerMetadataSetDao().getMarkerMetadataSetByDatasetId(datasetId);
	}

	@Override
	public List<CharValues> getCharValuesByMarkerIds(final List<Integer> markerIds) {
		return this.getCharValuesDao().getCharValuesByMarkerIds(markerIds);
	}

	@Override
	public List<MappingData> getAllMappingData() {
		return this.getMappingDataDao().getAll();
	}

	// --- Added by Matthew transferring GDMS SQL to middleware -- //
	@Override
	public List<QtlDetails> getAllQtlDetails() {
		return this.getQtlDetailsDao().getAll();
	}

	@Override
	public List<Qtl> getAllQtl() {
		return this.getQtlDao().getAll();
	}

	@Override
	public List<Object> getUniqueCharAllelesByGidsAndMids(final List<Integer> gids, final List<Integer> mids) {
		return this.getCharValuesDao().getUniqueCharAllelesByGidsAndMids(gids, mids);
	}

	@Override
	public List<Object> getUniqueAllelesByGidsAndMids(final List<Integer> gids, final List<Integer> mids) {
		return this.getAlleleValuesDao().getUniqueAllelesByGidsAndMids(gids, mids);
	}

	@Override
	public List<Object> getUniqueMapPopAllelesByGidsAndMids(final List<Integer> gids, final List<Integer> mids) {
		return this.getMappingPopValuesDao().getUniqueMapPopAllelesByGidsAndMids(gids, mids);
	}

	@Override
	public List<Object> getUniqueAccMetaDataSetByGids(final List gids) {
		return this.getAccMetadataSetDao().getUniqueAccMetaDatsetByGids(gids);
	}

	@Override
	public List<Integer> getMarkerIdsByNames(final List<String> names, final int start, final int numOfRows) {
		return this.getMarkerDao().getIdsByNames(names, start, numOfRows);
	}

	@Override
	public int countAllMarkers() {
		return this.getMarkerDao().getAll().size();
	}

	@Override
	public List<Integer> getDatasetIdsByGermplasmIds(final List<Integer> gIds) {
		return this.getAccMetadataSetDao().getDatasetIdsByGermplasmIds(gIds);
	}

	@Override
	public List<Integer> getAccMetadatasetByDatasetIds(final List<Integer> datasetIds) {
		return this.getAccMetadataSetDao().getNidsByDatasetIds(datasetIds);
	}

	@Override
	public List<Object> getMarkersOnMapByMarkerIdsAndMapId(final List<Integer> markerIds, final Integer mapID) {
		return this.getMarkerOnMapDao().getMarkersOnMapByMarkerIdsAndMapId(markerIds, mapID);
	}

	@Override
	public List<MarkerOnMap> getMarkerOnMapByLinkageGroupAndMapIdAndNotInMarkerId(
		final Integer mapId, final Integer linkageGroupId, final Integer markerId) {
		return this.getMarkerOnMapDao().getMarkerOnMapByLinkageGroupAndMapIdAndNotInMarkerId(mapId, linkageGroupId, markerId);
	}

}
