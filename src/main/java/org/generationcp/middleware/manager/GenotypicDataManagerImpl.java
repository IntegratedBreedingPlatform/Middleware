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

	private static final Logger LOG = LoggerFactory.getLogger(GenotypicDataManagerImpl.class);

	private static final String TYPE_SNP = GdmsType.TYPE_SNP.getValue();
	private static final String TYPE_MTA = GdmsType.TYPE_MTA.getValue();
	private static final String TYPE_CAP = GdmsType.TYPE_CAP.getValue();
	private static final String TYPE_CISR = GdmsType.TYPE_CISR.getValue();
	private static final String TYPE_UA = GdmsType.TYPE_UA.getValue(); // Unassigned

	private DaoFactory daoFactory;

	public GenotypicDataManagerImpl() {
	}

	public GenotypicDataManagerImpl(final HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<Integer> getMapIDsByQTLName(String qtlName, int start, int numOfRows) throws MiddlewareQueryException {
		if (qtlName == null || qtlName.isEmpty()) {
			return new ArrayList<>();
		}

		return new ArrayList<>(this.getQtlDao().getMapIDsByQTLName(qtlName, start, numOfRows));
	}

	@Override
	public long countMapIDsByQTLName(String qtlName) throws MiddlewareQueryException {

		return this.getQtlDao().countMapIDsByQTLName(qtlName);

	}

	@Override
	public List<Name> getNamesByNameIds(List<Integer> nIds) throws MiddlewareQueryException {
		return this.getNameDao().getNamesByNameIds(nIds);
	}

	@Override
	public List<Name> getGermplasmNamesByMarkerId(final Integer markerId) {
		return this.getDatasetDao().getGermplasmNamesByMarkerId(markerId);
	}

	@Override
	public Name getNameByNameId(Integer nId) throws MiddlewareQueryException {
		return this.getNameDao().getNameByNameId(nId);
	}

	@Override
	public long countAllMaps(Database instance) throws MiddlewareQueryException {
		return super.countFromInstance(this.getMapDao(), instance);
	}

	@Override
	public List<Map> getAllMaps(int start, int numOfRows, Database instance) throws MiddlewareQueryException {

		return this.getMapDao().getAll();

	}

	@Override
	public List<MapInfo> getMapInfoByMapName(String mapName, Database instance) throws MiddlewareQueryException {
		List<MapInfo> mapInfoList = new ArrayList<>();

		// Step 1: Get map id by map name
		Map map = this.getMapDao().getByName(mapName);
		if (map == null) {
			return new ArrayList<>();
		}

		// Step 2: Get markerId, linkageGroup, startPosition from gdms_markers_onmap
		List<MarkerOnMap> markersOnMap = this.getMarkerOnMapDao().getMarkersOnMapByMapId(map.getMapId());

		HashMap<Integer,Marker> markersMap = getMarkerByMapId(map.getMapId());

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
	public List<MapInfo> getMapInfoByMapName(String mapName) throws MiddlewareQueryException {
		List<MapInfo> mapInfoList = this.getMapInfoByMapName(mapName, Database.LOCAL);
		Collections.sort(mapInfoList);
		return mapInfoList;
	}

	@Override
	public List<MapInfo> getMapInfoByMapAndChromosome(int mapId, String chromosome) throws MiddlewareQueryException {
		return this.getMapDao().getMapInfoByMapAndChromosome(mapId, chromosome);
	}

	@Override
	public List<MapInfo> getMapInfoByMapChromosomeAndPosition(int mapId, String chromosome, float startPosition)
			throws MiddlewareQueryException {
		return this.getMapDao().getMapInfoByMapChromosomeAndPosition(mapId, chromosome, startPosition);
	}

	@Override
	public List<MapInfo> getMapInfoByMarkersAndMap(List<Integer> markers, Integer mapId) throws MiddlewareQueryException {
		return this.getMapDao().getMapInfoByMarkersAndMap(markers, mapId);
	}

	// GCP-8572
	@Override
	public List<MarkerOnMap> getMarkerOnMaps(List<Integer> mapIds, String linkageGroup, double startPos, double endPos)
			throws MiddlewareQueryException {
		return this.getMarkerOnMapDao().getMarkersOnMap(mapIds, linkageGroup, startPos, endPos);
	}

	// GCP-8571
	@Override
	public List<MarkerOnMap> getMarkersOnMapByMarkerIds(List<Integer> markerIds) throws MiddlewareQueryException {
		return this.getMarkerOnMapDao().getMarkersOnMapByMarkerIds(markerIds);
	}

	// GCP-8573
	@Override
	public List<String> getAllMarkerNamesFromMarkersOnMap() throws MiddlewareQueryException {
		List<Integer> markerIds = this.getMarkerOnMapDao().getAllMarkerIds();

		return this.getMarkerDao().getMarkerNamesByIds(markerIds);

	}

	@Override
	public String getMapNameById(Integer mapID) throws MiddlewareQueryException {
		return this.getMapDao().getMapNameById(mapID);
	}
	
	@Override
	public List<Dataset> getAllDatasets() {
		return this.getDatasetDao().getAll();
	}

	@Override
	public long countDatasetNames(Database instance) throws MiddlewareQueryException {
		return this.getDatasetDao().countByName();
	}

	@Override
	public List<String> getDatasetNames(int start, int numOfRows, Database instance) throws MiddlewareQueryException {
		return this.getDatasetDao().getDatasetNames(start, numOfRows);
	}

	@Override
	public List<String> getDatasetNamesByQtlId(Integer qtlId, int start, int numOfRows) throws MiddlewareQueryException {
		return this.getDatasetDao().getDatasetNamesByQtlId(qtlId, start, numOfRows);
	}

	@Override
	public long countDatasetNamesByQtlId(Integer qtlId) throws MiddlewareQueryException {
		return this.getDatasetDao().countDatasetNamesByQtlId(qtlId);

	}

	@Override
	public List<DatasetElement> getDatasetDetailsByDatasetName(String datasetName) throws MiddlewareQueryException {
		return this.getDatasetDao().getDetailsByName(datasetName);
	}

	@Override
	public List<Marker> getMarkersByMarkerNames(List<String> markerNames, int start, int numOfRows)
			throws MiddlewareQueryException {
		return this.getMarkerDao().getByNames(markerNames, start, numOfRows);
	}

	@Override
	public Set<Integer> getMarkerIDsByMapIDAndLinkageBetweenStartPosition(int mapId, String linkageGroup, double startPos, double endPos,
			int start, int numOfRows) throws MiddlewareQueryException {
		return this.getMarkerDao().getMarkerIDsByMapIDAndLinkageBetweenStartPosition(mapId, linkageGroup, startPos, endPos, start,
				numOfRows);
	}

	// GCP-8567
	@Override
	public List<Marker> getMarkersByPositionAndLinkageGroup(double startPos, double endPos, String linkageGroup)
			throws MiddlewareQueryException {

		List<Integer> markerIds = this.getMarkerOnMapDao().getMarkerIdsByPositionAndLinkageGroup(startPos, endPos, linkageGroup);
		return this.getMarkerDao().getMarkersByIds(markerIds);
	}

	@Override
	public long countMarkerIDsByMapIDAndLinkageBetweenStartPosition(int mapId, String linkageGroup, double startPos, double endPos)
			throws MiddlewareQueryException {
		return this.getMarkerDao().countMarkerIDsByMapIDAndLinkageBetweenStartPosition(mapId, linkageGroup, startPos, endPos);
	}

	@Override
	public List<Integer> getMarkerIdsByDatasetId(Integer datasetId) throws MiddlewareQueryException {
		return this.getMarkerMetadataSetDao().getMarkerIdByDatasetId(datasetId);

	}

	@Override
	public List<ParentElement> getParentsByDatasetId(Integer datasetId) throws MiddlewareQueryException {
		return this.getMappingPopDao().getParentsByDatasetId(datasetId);
	}

	@Override
	public List<String> getMarkerTypesByMarkerIds(List<Integer> markerIds) throws MiddlewareQueryException {
		return this.getMarkerDao().getMarkerTypeByMarkerIds(markerIds);
	}

	@Override
	public List<MarkerNameElement> getMarkerNamesByGIds(List<Integer> gIds) throws MiddlewareQueryException {

		List<MarkerNameElement> dataValues = this.getMarkerDao().getMarkerNamesByGIds(gIds);

		// Remove duplicates
		Set<MarkerNameElement> set = new HashSet<>();
		set.addAll(dataValues);
		dataValues.clear();
		dataValues.addAll(set);

		return dataValues;
	}

	@Override
	public List<GermplasmMarkerElement> getGermplasmNamesByMarkerNames(List<String> markerNames, Database instance)
			throws MiddlewareQueryException {
		return this.getMarkerDao().getGermplasmNamesByMarkerNames(markerNames);
	}

	@Override
	public List<MappingValueElement> getMappingValuesByGidsAndMarkerNames(List<Integer> gids, List<String> markerNames, int start,
			int numOfRows) throws MiddlewareQueryException {
		List<MappingValueElement> mappingValueElementList;

		List<Marker> markers = this.getMarkerDao().getByNames(markerNames, start, numOfRows);

		List<Integer> markerIds = new ArrayList<>();
		for (Marker marker : markers) {
			markerIds.add(marker.getMarkerId());
		}

		mappingValueElementList = this.getMappingPopDao().getMappingValuesByGidAndMarkerIds(gids, markerIds);

		for (MappingValueElement element : mappingValueElementList) {
			if (element != null && element.getMarkerId() != null) {
				if (element.getMarkerId() >= 0 && element.getMarkerType() == null) {
					for (Marker marker : markers) {
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
	public List<AllelicValueElement> getAllelicValuesByGidsAndMarkerNames(List<Integer> gids, List<String> markerNames)
			throws MiddlewareQueryException {
		List<AllelicValueElement> allelicValues = new ArrayList<>();

		// Get marker_ids by marker_names
		java.util.Map<Integer, String> markerIdName = this.getMarkerDao().getFirstMarkerIdByMarkerName(markerNames, Database.LOCAL);
		List<Integer> markerIds = new ArrayList<>(markerIdName.keySet());

		allelicValues.addAll(this.getMarkerDao().getAllelicValuesByGidsAndMarkerIds(gids, markerIds));

		for (AllelicValueElement allelicValue : allelicValues) {
			allelicValue.setMarkerName(markerIdName.get(allelicValue.getMarkerId()));
		}

		return allelicValues;
	}

	@Override
	public List<AllelicValueWithMarkerIdElement> getAllelicValuesFromAlleleValuesByDatasetId(Integer datasetId, int start, int numOfRows)
			throws MiddlewareQueryException {
		return this.getAlleleValuesDao().getAllelicValuesByDatasetId(datasetId, start, numOfRows);
	}

	@Override
	public long countAllelicValuesFromAlleleValuesByDatasetId(Integer datasetId) throws MiddlewareQueryException {
		return this.getAlleleValuesDao().countByDatasetId(datasetId);
	}

	@Override
	public List<AllelicValueWithMarkerIdElement> getAllelicValuesFromMappingPopValuesByDatasetId(Integer datasetId, int start, int numOfRows)
			throws MiddlewareQueryException {
		return this.getMappingPopValuesDao().getAllelicValuesByDatasetId(datasetId, start, numOfRows);
	}

	@Override
	public long countAllelicValuesFromMappingPopValuesByDatasetId(Integer datasetId) throws MiddlewareQueryException {
		return this.getMappingPopValuesDao().countByDatasetId(datasetId);
	}

	@Override
	public List<MarkerInfo> getMarkerInfoByMarkerName(String markerName, int start, int numOfRows) throws MiddlewareQueryException {
		return this.getMarkerInfoDao().getByMarkerName(markerName, start, numOfRows);
	}

	@Override
	public List<ExtendedMarkerInfo> getMarkerInfoDataByMarkerType(String markerType) throws MiddlewareQueryException {
		return this.getExtendedMarkerInfoDao().getByMarkerType(markerType);
	}

	@Override
	public List<ExtendedMarkerInfo> getMarkerInfoDataLikeMarkerName(String partialMarkerName) throws MiddlewareQueryException {
		return this.getExtendedMarkerInfoDao().getLikeMarkerName(partialMarkerName);
	}

	@Override
	public List<ExtendedMarkerInfo> getMarkerInfoByMarkerNames(List<String> markerNames) throws MiddlewareQueryException {
		return this.getExtendedMarkerInfoDao().getByMarkerNames(markerNames);
	}

	@Override
	public List<AllelicValueElement> getAllelicValuesByGid(Integer targetGID) throws MiddlewareQueryException {
		List<Integer> inputList = new ArrayList<>();
		inputList.add(targetGID);

		List<MarkerNameElement> markerNameElements = this.getMarkerNamesByGIds(inputList);

		List<String> markerNames = new ArrayList<>();

		for (MarkerNameElement markerNameElement : markerNameElements) {
			markerNames.add(markerNameElement.getMarkerName());
		}

		return this.getAllelicValuesByGidsAndMarkerNames(inputList, markerNames);
	}

	@Override
	public long countMarkerInfoByMarkerName(String markerName) throws MiddlewareQueryException {
		return this.getMarkerInfoDao().countByMarkerName(markerName);
	}

	@Override
	public List<MarkerInfo> getMarkerInfoByGenotype(String genotype, int start, int numOfRows) throws MiddlewareQueryException {

		return this.getMarkerInfoDao().getByGenotype(genotype, start, numOfRows);
	}

	@Override
	public long countMarkerInfoByGenotype(String genotype) throws MiddlewareQueryException {
		return this.getMarkerInfoDao().countByGenotype(genotype);
	}

	@Override
	public List<MarkerInfo> getMarkerInfoByDbAccessionId(String dbAccessionId, int start, int numOfRows) throws MiddlewareQueryException {
		return this.getMarkerInfoDao().getByDbAccessionId(dbAccessionId, start, numOfRows);
	}

	@Override
	public long countMarkerInfoByDbAccessionId(String dbAccessionId) throws MiddlewareQueryException {
		return this.getMarkerInfoDao().countByDbAccessionId(dbAccessionId);
	}

	@Override
	public List<MarkerIdMarkerNameElement> getMarkerNamesByMarkerIds(List<Integer> markerIds) throws MiddlewareQueryException {
		List<MarkerIdMarkerNameElement> markers = this.getMarkerDao().getNamesByIds(markerIds);

		// Sort based on the given input order
		List<MarkerIdMarkerNameElement> markersToReturn = new ArrayList<>();
		for (Integer markerId : markerIds) {
			for (MarkerIdMarkerNameElement element : markers) {
				if (element.getMarkerId() == markerId) {
					markersToReturn.add(element);
					break;
				}
			}
		}

		return markersToReturn;
	}

	private String getMarkerNameByMarkerId(Integer markerId) throws MiddlewareQueryException {
		return this.getMarkerDao().getNameById(markerId);
	}

	private HashMap<Integer, Marker> getMarkerByMapId(Integer mapId) throws MiddlewareQueryException {
		final HashMap<Integer, Marker> markersMap = new HashMap<>();
		List<Marker> markerList = this.getMarkerDao().getMarkersByMapId(mapId);
		for (Marker marker : markerList) {
			markersMap.put(marker.getMarkerId(), marker);
		}
		return markersMap;
	}

	@Override
	public List<String> getAllMarkerTypes(int start, int numOfRows) throws MiddlewareQueryException {
		return this.getMarkerDao().getAllMarkerTypes(start, numOfRows);
	}

	@Override
	public long countAllMarkerTypes(Database instance) throws MiddlewareQueryException {
		return this.getMarkerDao().countAllMarkerTypes();
	}

	@Override
	public List<String> getMarkerNamesByMarkerType(String markerType, int start, int numOfRows) throws MiddlewareQueryException {
		return this.getMarkerDao().getMarkerNamesByMarkerType(markerType, start, numOfRows);
	}

	@Override
	public long countMarkerNamesByMarkerType(String markerType) throws MiddlewareQueryException {
		return this.getMarkerDao().countMarkerNamesByMarkerType(markerType);
	}

	@Override
	public List<Integer> getGIDsFromCharValuesByMarkerId(Integer markerId, int start, int numOfRows) throws MiddlewareQueryException {
		return this.getCharValuesDao().getGIDsByMarkerId(markerId, start, numOfRows);
	}

	@Override
	public long countGIDsFromCharValuesByMarkerId(Integer markerId) throws MiddlewareQueryException {
		return this.getCharValuesDao().countGIDsByMarkerId(markerId);
	}

	@Override
	public List<Integer> getGIDsFromAlleleValuesByMarkerId(Integer markerId, int start, int numOfRows) throws MiddlewareQueryException {
		return this.getAlleleValuesDao().getGIDsByMarkerId(markerId, start, numOfRows);
	}

	@Override
	public List<Integer> getGIDsFromMappingPopValuesByMarkerId(Integer markerId, int start, int numOfRows) throws MiddlewareQueryException {
		return this.getMappingPopValuesDao().getGIDsByMarkerId(markerId, start, numOfRows);
	}

	@Override
	public long countGIDsFromMappingPopValuesByMarkerId(Integer markerId) throws MiddlewareQueryException {
		return this.getMappingPopValuesDao().countGIDsByMarkerId(markerId);
	}

	@Override
	public List<Integer> getGidsByMarkersAndAlleleValues(List<Integer> markerIdList, List<String> alleleValueList)
			throws MiddlewareQueryException {
		return this.getAlleleValuesDao().getGidsByMarkersAndAlleleValues(markerIdList, alleleValueList);
	}

	@Override
	public List<String> getAllDbAccessionIdsFromMarker(int start, int numOfRows) throws MiddlewareQueryException {

		return this.getMarkerDao().getAllDbAccessionIds(start, numOfRows);
	}

	@Override
	public long countAllDbAccessionIdsFromMarker() throws MiddlewareQueryException {
		return this.getMarkerDao().countAllDbAccessionIds();
	}

	@Override
	public List<AccMetadataSet> getAccMetadatasetsByDatasetIds(List<Integer> datasetIds, int start, int numOfRows)
			throws MiddlewareQueryException {
		return this.getAccMetadataSetDao().getByDatasetIds(datasetIds, start, numOfRows);
	}

	public Set<Integer> getNIdsByMarkerIdsAndDatasetIdsAndNotGIds(List<Integer> datasetIds, List<Integer> markerIds,
			List<Integer> gIds, int start, int numOfRows) throws MiddlewareQueryException {
		return this.getAccMetadataSetDao().getNIdsByMarkerIdsAndDatasetIdsAndNotGIds(datasetIds, markerIds, gIds, start, numOfRows);
	}

	@Override
	public int countNIdsByMarkerIdsAndDatasetIdsAndNotGIds(List<Integer> datasetIds, List<Integer> markerIds, List<Integer> gIds)
			throws MiddlewareQueryException {
		return (int) this.getAccMetadataSetDao().countNIdsByMarkerIdsAndDatasetIdsAndNotGIds(datasetIds, markerIds, gIds);
	}

	private List<Integer> getNIdsByMarkerIdsAndDatasetIds(List<Integer> datasetIds, List<Integer> markerIds)
			throws MiddlewareQueryException {
		Set<Integer> nidSet = new TreeSet<>();
		nidSet.addAll(this.getAccMetadataSetDao().getNIdsByMarkerIdsAndDatasetIds(datasetIds, markerIds));
		return new ArrayList<>(((TreeSet<Integer>) nidSet).descendingSet());
	}

	@Override
	public List<Integer> getNIdsByMarkerIdsAndDatasetIds(List<Integer> datasetIds, List<Integer> markerIds, int start, int numOfRows)
			throws MiddlewareQueryException {
		List<Integer> nidList = this.getNIdsByMarkerIdsAndDatasetIds(datasetIds, markerIds);
		return nidList.subList(start, start + numOfRows);
	}

	@Override
	public int countNIdsByMarkerIdsAndDatasetIds(List<Integer> datasetIds, List<Integer> markerIds) throws MiddlewareQueryException {
		List<Integer> nidList = this.getNIdsByMarkerIdsAndDatasetIds(datasetIds, markerIds);
		return nidList.size();
	}

	@Override
	public List<Integer> getDatasetIdsForFingerPrinting(int start, int numOfRows) throws MiddlewareQueryException {

		return this.getDatasetDao().getDatasetIdsForFingerPrinting(start, numOfRows);
	}

	@Override
	public long countDatasetIdsForFingerPrinting() throws MiddlewareQueryException {
		return this.getDatasetDao().countDatasetIdsForFingerPrinting();
	}

	@Override
	public List<Integer> getDatasetIdsForMapping(int start, int numOfRows) throws MiddlewareQueryException {
		return this.getDatasetDao().getDatasetIdsForMapping(start, numOfRows);
	}

	@Override
	public long countDatasetIdsForMapping() throws MiddlewareQueryException {
		return this.getDatasetDao().countDatasetIdsForMapping();
	}

	@Override
	public List<AccMetadataSet> getGdmsAccMetadatasetByGid(List<Integer> gids, int start, int numOfRows) throws MiddlewareQueryException {
		return this.getAccMetadataSetDao().getAccMetadataSetsByGids(gids, start, numOfRows);
	}

	@Override
	public long countGdmsAccMetadatasetByGid(List<Integer> gids) throws MiddlewareQueryException {
		return this.getAccMetadataSetDao().countAccMetadataSetsByGids(gids);
	}

	@Override
	public List<Integer> getMarkersBySampleIdAndDatasetIds(Integer sampleId, List<Integer> datasetIds, int start, int numOfRows)
			throws MiddlewareQueryException {
		return this.getMarkerMetadataSetDao().getMarkersBySampleIdAndDatasetIds(sampleId, datasetIds, start, numOfRows);
	}

	@Override
	public long countMarkersBySampleIdAndDatasetIds(Integer sampleId, List<Integer> datasetIds) throws MiddlewareQueryException {
		return this.getMarkerMetadataSetDao().countMarkersBySampleIdAndDatasetIds(sampleId, datasetIds);
	}

	@Override
	public List<Marker> getMarkersByMarkerIds(List<Integer> markerIds, int start, int numOfRows) throws MiddlewareQueryException {
		return this.getMarkerDao().getMarkersByIds(markerIds, start, numOfRows);
	}

	@Override
	public long countMarkersByMarkerIds(List<Integer> markerIds) throws MiddlewareQueryException {
		return this.getMarkerDao().countMarkersByIds(markerIds);
	}

	@Override
	public long countAlleleValuesByGids(List<Integer> gids) throws MiddlewareQueryException {
		return this.getAlleleValuesDao().countAlleleValuesByGids(gids);
	}

	@Override
	public List<AllelicValueElement> getIntAlleleValuesForPolymorphicMarkersRetrieval(List<Integer> gids, int start, int numOfRows)
			throws MiddlewareQueryException {
		List<AllelicValueElement> allelicValueElements =
				this.getAlleleValuesDao().getIntAlleleValuesForPolymorphicMarkersRetrieval(gids, start, numOfRows);

		// Sort by gid, markerName
		Collections.sort(allelicValueElements, AllelicValueElement.AllelicValueElementComparator);
		return allelicValueElements;

	}

	@Override
	public long countIntAlleleValuesForPolymorphicMarkersRetrieval(List<Integer> gids) throws MiddlewareQueryException {
		return this.getAlleleValuesDao().countIntAlleleValuesForPolymorphicMarkersRetrieval(gids);
	}

	@Override
	public List<AllelicValueElement> getCharAlleleValuesForPolymorphicMarkersRetrieval(List<Integer> gids, int start, int numOfRows)
			throws MiddlewareQueryException {
		List<AllelicValueElement> allelicValueElements =
				this.getAlleleValuesDao().getCharAlleleValuesForPolymorphicMarkersRetrieval(gids, start, numOfRows);

		// Sort by gid, markerName
		Collections.sort(allelicValueElements, AllelicValueElement.AllelicValueElementComparator);
		return allelicValueElements;
	}

	@Override
	public long countCharAlleleValuesForPolymorphicMarkersRetrieval(List<Integer> gids) throws MiddlewareQueryException {
		return this.getAlleleValuesDao().countCharAlleleValuesForPolymorphicMarkersRetrieval(gids);
	}

	@Override
	public List<AllelicValueElement> getMappingAlleleValuesForPolymorphicMarkersRetrieval(List<Integer> gids, int start, int numOfRows)
			throws MiddlewareQueryException {
		List<AllelicValueElement> allelicValueElements =
				this.getAlleleValuesDao().getMappingAlleleValuesForPolymorphicMarkersRetrieval(gids, start, numOfRows);

		// Sort by gid, markerName
		Collections.sort(allelicValueElements, AllelicValueElement.AllelicValueElementComparator);
		return allelicValueElements;

	}

	@Override
	public long countMappingAlleleValuesForPolymorphicMarkersRetrieval(List<Integer> gids) throws MiddlewareQueryException {
		return this.getAlleleValuesDao().countMappingAlleleValuesForPolymorphicMarkersRetrieval(gids);
	}

	@Override
	public List<Qtl> getAllQtl(int start, int numOfRows) throws MiddlewareQueryException {
		return this.getQtlDao().getAll(start, numOfRows);
	}

	@Override
	public long countAllQtl() throws MiddlewareQueryException {
		return this.countAll(this.getQtlDao());
	}

	@Override
	public List<Integer> getQtlIdByName(String name, int start, int numOfRows) throws MiddlewareQueryException {
		if (name == null || name.isEmpty()) {
			return new ArrayList<>();
		}

		return this.getQtlDao().getQtlIdByName(name, start, numOfRows);
	}

	@Override
	public long countQtlIdByName(String name) throws MiddlewareQueryException {
		if (name == null || name.isEmpty()) {
			return 0;
		}
		return this.getQtlDao().countQtlIdByName(name);
	}

	@Override
	public List<QtlDetailElement> getQtlByName(String name, int start, int numOfRows) throws MiddlewareQueryException {
		List<QtlDetailElement> qtlDetailElements = new ArrayList<>();
		if (name == null || name.isEmpty()) {
			return qtlDetailElements;
		}

		return this.getQtlDao().getQtlAndQtlDetailsByName(name, start, numOfRows);

	}

	@Override
	public long countQtlByName(String name) throws MiddlewareQueryException {
		if (name == null || name.isEmpty()) {
			return 0;
		}
		return this.getQtlDao().countQtlAndQtlDetailsByName(name);
	}

	@Override
	public java.util.Map<Integer, String> getQtlNamesByQtlIds(List<Integer> qtlIds) throws MiddlewareQueryException {
		java.util.Map<Integer, String> qtlNames = new HashMap<>();
		qtlNames.putAll(this.getQtlDao().getQtlNameByQtlIds(qtlIds));
		return qtlNames;
	}

	// TODO BMS-148 : Review for how to safely remove the dual db read pattern without breaking any logic.
	@Override
	public List<QtlDetailElement> getQtlByQtlIds(List<Integer> qtlIds, int start, int numOfRows) throws MiddlewareQueryException {
		List<QtlDetailElement> qtlDetailElements = new ArrayList<>();

		if (qtlIds == null || qtlIds.isEmpty()) {
			return qtlDetailElements;
		}

		return this.getQtlDao().getQtlAndQtlDetailsByQtlIds(qtlIds, start, numOfRows);
	}

	@Override
	public long countQtlByQtlIds(List<Integer> qtlIds) throws MiddlewareQueryException {
		if (qtlIds == null || qtlIds.isEmpty()) {
			return 0;
		}
		return this.getQtlDao().countQtlAndQtlDetailsByQtlIds(qtlIds);
	}

	@Override
	public List<Integer> getQtlByTrait(Integer trait, int start, int numOfRows) throws MiddlewareQueryException {

		return this.getQtlDao().getQtlByTrait(trait, start, numOfRows);
	}

	@Override
	public long countQtlByTrait(Integer trait) throws MiddlewareQueryException {
		return this.getQtlDao().countQtlByTrait(trait);
	}

	@Override
	public List<Integer> getQtlTraitsByDatasetId(Integer datasetId, int start, int numOfRows) throws MiddlewareQueryException {
		return this.getQtlDetailsDao().getQtlTraitsByDatasetId(datasetId, start, numOfRows);
	}

	@Override
	public long countQtlTraitsByDatasetId(Integer datasetId) throws MiddlewareQueryException {
		return this.getQtlDetailsDao().countQtlTraitsByDatasetId(datasetId);
	}

	@Override
	public List<ParentElement> getAllParentsFromMappingPopulation(int start, int numOfRows) throws MiddlewareQueryException {
		return this.getMappingPopDao().getAllParentsFromMappingPopulation(start, numOfRows);
	}

	@Override
	public Long countAllParentsFromMappingPopulation() throws MiddlewareQueryException {
		return this.getMappingPopDao().countAllParentsFromMappingPopulation();
	}

	@Override
	public List<MapDetailElement> getMapDetailsByName(String nameLike, int start, int numOfRows) throws MiddlewareQueryException {
		return this.getMapDao().getMapDetailsByName(nameLike, start, numOfRows);
	}

	@Override
	public Long countMapDetailsByName(String nameLike) throws MiddlewareQueryException {
		return this.getMapDao().countMapDetailsByName(nameLike);
	}

	@Override
	public java.util.Map<Integer, List<String>> getMapNamesByMarkerIds(List<Integer> markerIds) throws MiddlewareQueryException {

		java.util.Map<Integer, List<String>> markerMaps = new HashMap<>();

		if (markerIds == null || markerIds.isEmpty()) {
			return markerMaps;
		}

		markerMaps.putAll(this.getMarkerOnMapDao().getMapNameByMarkerIds(markerIds));
		return markerMaps;
	}

	@Override
	public List<MapDetailElement> getAllMapDetails(int start, int numOfRows) throws MiddlewareQueryException {
		return this.getMapDao().getAllMapDetails(start, numOfRows);
	}

	@Override
	public long countAllMapDetails() throws MiddlewareQueryException {
		return this.getMapDao().countAllMapDetails();
	}

	@Override
	public List<Integer> getMapIdsByQtlName(String qtlName, int start, int numOfRows) throws MiddlewareQueryException {
		return this.getQtlDetailsDao().getMapIdsByQtlName(qtlName, start, numOfRows);
	}

	@Override
	public long countMapIdsByQtlName(String qtlName) throws MiddlewareQueryException {
		return this.getQtlDetailsDao().countMapIdsByQtlName(qtlName);
	}

	@Override
	public List<Integer> getMarkerIdsByQtl(String qtlName, String chromosome, float min, float max, int start, int numOfRows)
			throws MiddlewareQueryException {
		return this.getQtlDetailsDao().getMarkerIdsByQtl(qtlName, chromosome, min, max, start, numOfRows);
	}

	@Override
	public long countMarkerIdsByQtl(String qtlName, String chromosome, float min, float max) throws MiddlewareQueryException {
		return this.getQtlDetailsDao().countMarkerIdsByQtl(qtlName, chromosome, min, max);
	}

	@Override
	public List<Marker> getMarkersByIds(List<Integer> markerIds, int start, int numOfRows) throws MiddlewareQueryException {
		List<Marker> markers = new ArrayList<>();
		markers.addAll(this.getMarkerDao().getMarkersByIds(markerIds, start, numOfRows));
		return markers;
	}

	@Override
	public Integer addQtlDetails(QtlDetails qtlDetails) throws MiddlewareQueryException {

		Integer savedId = null;
		try {

			// No need to auto-assign negative IDs for new local DB records
			// qtlId and mapId are foreign keys

			QtlDetails recordSaved = this.getQtlDetailsDao().save(qtlDetails);
			savedId = recordSaved.getQtlId();

		} catch (Exception e) {
			throw new MiddlewareQueryException("Error encountered while saving Qtl Details: GenotypicDataManager.addQtlDetails(qtlDetails="
					+ qtlDetails + "): " + e.getMessage(), e);
		}
		return savedId;

	}

	@Override
	public Integer addMarker(Marker marker) throws MiddlewareQueryException {
		return ((Marker) super.save(this.getMarkerDao(), marker)).getMarkerId();
	}

	@Override
	public Integer addMarkerDetails(MarkerDetails markerDetails) throws MiddlewareQueryException {
		MarkerDetailsDAO dao = this.getMarkerDetailsDao();
		MarkerDetails details = dao.getById(markerDetails.getMarkerId());
		if (details == null) {
			return ((MarkerDetails) super.save(dao, details)).getMarkerId();
		}

		return details.getMarkerId();
	}

	@Override
	public Integer addMarkerUserInfo(MarkerUserInfo markerUserInfo) throws MiddlewareQueryException {
		return ((MarkerUserInfo) super.save(this.getMarkerUserInfoDao(), markerUserInfo)).getUserInfoId();
	}

	@Override
	public Integer addAccMetadataSet(AccMetadataSet accMetadataSet) throws MiddlewareQueryException {

		Integer savedId = null;

		try {

			AccMetadataSet recordSaved = this.getAccMetadataSetDao().save(accMetadataSet);
			savedId = recordSaved.getAccMetadataSetId();

		} catch (Exception e) {
			throw new MiddlewareQueryException("Error encountered with addAccMetadataSet(accMetadataSet=" + accMetadataSet + "): "
					+ e.getMessage(), e);
		}
		return savedId;
	}

	@Override
	public Integer addMarkerMetadataSet(MarkerMetadataSet markerMetadataSet) throws MiddlewareQueryException {

		Integer savedId = null;

		try {

			MarkerMetadataSet recordSaved = this.getMarkerMetadataSetDao().save(markerMetadataSet);
			savedId = recordSaved.getMarkerMetadataSetId();

		} catch (Exception e) {
			throw new MiddlewareQueryException("Error encountered with addMarkerMetadataSet(markerMetadataSet=" + markerMetadataSet + "): "
					+ e.getMessage(), e);
		}
		return savedId;
	}

	@Override
	public Integer addDataset(Dataset dataset) throws MiddlewareQueryException {
		return ((Dataset) super.save(this.getDatasetDao(), dataset)).getDatasetId();
	}

	@Override
	public Integer addGDMSMarker(Marker marker) throws MiddlewareQueryException {
		// Check for existence. duplicate marker names are not allowed.

		Integer id = null;
		try {

			id = this.saveMarkerIfNotExisting(marker, marker.getMarkerType());

		} catch (Exception e) {
			throw new MiddlewareQueryException("Error encountered while adding Marker: " + e.getMessage(), e);
		}
		return id;
	}

	@Override
	public Integer addGDMSMarkerAlias(MarkerAlias markerAlias) throws MiddlewareQueryException {
		return ((MarkerAlias) super.save(this.getMarkerAliasDao(), markerAlias)).getMarkerId();
	}

	@Override
	public Integer addDatasetUser(DatasetUsers datasetUser) throws MiddlewareQueryException {
		DatasetUsersDAO dao = this.getDatasetUsersDao();
		DatasetUsers user = dao.getById(datasetUser.getDataset().getDatasetId());
		if (user == null) {
			return ((DatasetUsers) super.save(dao, datasetUser)).getUserId();
		}

		return user.getUserId();
	}

	@Override
	public Integer addAlleleValues(AlleleValues alleleValues) throws MiddlewareQueryException {
		return ((AlleleValues) super.saveOrUpdate(this.getAlleleValuesDao(), alleleValues)).getAnId();
	}

	@Override
	public Integer addCharValues(CharValues charValues) throws MiddlewareQueryException {
		return ((CharValues) super.saveOrUpdate(this.getCharValuesDao(), charValues)).getAcId();
	}

	@Override
	public Integer addMappingPop(MappingPop mappingPop) throws MiddlewareQueryException {

		MappingPopDAO dao = this.getMappingPopDao();
		MappingPop popFromDB = dao.getById(mappingPop.getDatasetId());
		if (popFromDB == null) {
			return ((MappingPop) super.save(dao, mappingPop)).getDatasetId();
		}

		return mappingPop.getDatasetId();
	}

	@Override
	public Integer addMappingPopValue(MappingPopValues mappingPopValue) throws MiddlewareQueryException {
		return ((MappingPopValues) super.saveOrUpdate(this.getMappingPopValuesDao(), mappingPopValue)).getMpId();
	}

	@Override
	public Integer addMarkerOnMap(MarkerOnMap markerOnMap) throws MiddlewareQueryException {
		if (this.getMapDao().getById(markerOnMap.getMapId()) == null) {
			throw new MiddlewareQueryException("Map Id not found: " + markerOnMap.getMapId());
		}

		return ((MarkerOnMap) super.save(this.getMarkerOnMapDao(), markerOnMap)).getMapId();
	}

	@Override
	public Integer addDartValue(DartValues dartValue) throws MiddlewareQueryException {
		return ((DartValues) super.save(this.getDartValuesDao(), dartValue)).getAdId();
	}

	@Override
	public Integer addQtl(Qtl qtl) throws MiddlewareQueryException {
		return ((Qtl) super.saveOrUpdate(this.getQtlDao(), qtl)).getQtlId();
	}

	@Override
	public Integer addMap(Map map) throws MiddlewareQueryException {
		return ((Map) super.saveOrUpdate(this.getMapDao(), map)).getMapId();
	}

	@Override
	public Boolean setSNPMarkers(Marker marker, MarkerAlias markerAlias, MarkerDetails markerDetails, MarkerUserInfo markerUserInfo)
			throws MiddlewareQueryException {
		return this.setMarker(marker, GenotypicDataManagerImpl.TYPE_SNP, markerAlias, markerDetails, markerUserInfo);
	}

	@Override
	public Boolean setCAPMarkers(Marker marker, MarkerAlias markerAlias, MarkerDetails markerDetails, MarkerUserInfo markerUserInfo)
			throws MiddlewareQueryException {
		return this.setMarker(marker, GenotypicDataManagerImpl.TYPE_CAP, markerAlias, markerDetails, markerUserInfo);
	}

	@Override
	public Boolean setCISRMarkers(Marker marker, MarkerAlias markerAlias, MarkerDetails markerDetails, MarkerUserInfo markerUserInfo)
			throws MiddlewareQueryException {
		return this.setMarker(marker, GenotypicDataManagerImpl.TYPE_CISR, markerAlias, markerDetails, markerUserInfo);
	}

	private Boolean setMarker(Marker marker, String markerType, MarkerAlias markerAlias, MarkerDetails markerDetails,
			MarkerUserInfo markerUserInfo) throws MiddlewareQueryException {

		try {
			// Add GDMS Marker
			Integer idGDMSMarkerSaved = this.saveMarkerIfNotExisting(marker, markerType);
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
		} catch (Exception e) {
			throw new MiddlewareQueryException("Error encountered while saving Marker: " + e.getMessage(), e);
		}
	}

	@Override
	public Boolean setMaps(Marker marker, MarkerOnMap markerOnMap, Map map) throws MiddlewareQueryException {

		try {

			Integer markerSavedId = this.saveMarker(marker, GenotypicDataManagerImpl.TYPE_UA);
			Integer mapSavedId = this.saveMap(map);
			this.saveMarkerOnMap(markerSavedId, mapSavedId, markerOnMap);

			return true;
		} catch (Exception e) {
			throw new MiddlewareQueryException("Error encountered while setting Maps: setMaps(): " + e.getMessage(), e);
		}
	}

	private Integer getMarkerIdByMarkerName(String markerName) throws MiddlewareQueryException, MiddlewareException {
		return this.getMarkerDao().getIdByName(markerName);
	}

	private Integer getMapIdByMapName(String mapName) throws MiddlewareQueryException {
		return this.getMapDao().getMapIdByName(mapName);
	}

	@Override
	public List<QtlDataElement> getQtlDataByQtlTraits(List<Integer> qtlTraitIds, int start, int numOfRows) throws MiddlewareQueryException {
		return this.getQtlDetailsDao().getQtlDataByQtlTraits(qtlTraitIds, start, numOfRows);
	}

	@Override
	public long countQtlDataByQtlTraits(List<Integer> qtlTraits) throws MiddlewareQueryException {
		return this.getQtlDetailsDao().countQtlDataByQtlTraits(qtlTraits);
	}

	@Override
	public List<QtlDetailElement> getQtlDetailsByQtlTraits(List<Integer> qtlTraitIds, int start, int numOfRows)
			throws MiddlewareQueryException {
		return this.getQtlDao().getQtlDetailsByQtlTraits(qtlTraitIds, start, numOfRows);
	}

	@Override
	public long countQtlDetailsByQtlTraits(List<Integer> qtlTraits) throws MiddlewareQueryException {
		return this.getQtlDao().countQtlDetailsByQtlTraits(qtlTraits);
	}

	@Override
	public long countAccMetadatasetByDatasetIds(List<Integer> datasetIds) throws MiddlewareQueryException {
		return this.getAccMetadataSetDao().countSampleIdsByDatasetIds(datasetIds);
	}

	@Override
	public long countMarkersFromMarkerMetadatasetByDatasetIds(List<Integer> datasetIds) throws MiddlewareQueryException {
		return this.getMarkerMetadataSetDao().countByDatasetIds(datasetIds);
	}

	@Override
	public Integer getMapIdByName(String mapName) throws MiddlewareQueryException {
		return this.getMapDao().getMapIdByName(mapName);
	}

	@Override
	public long countMappingPopValuesByGids(List<Integer> gIds) throws MiddlewareQueryException {
		return this.getMappingPopValuesDao().countByGids(gIds);
	}

	@Override
	public long countMappingAlleleValuesByGids(List<Integer> gIds) throws MiddlewareQueryException {
		return this.getAlleleValuesDao().countByGids(gIds);
	}

	@Override
	public List<MarkerMetadataSet> getAllFromMarkerMetadatasetByMarkers(List<Integer> markerIds) throws MiddlewareQueryException {
		return this.getMarkerMetadataSetDao().getByMarkerIds(markerIds);
	}

	@Override
	public Dataset getDatasetById(Integer datasetId) throws MiddlewareQueryException {
		return this.getDatasetDao().getById(datasetId);
	}

	@Override
	public List<Dataset> getDatasetsByType(GdmsType type) throws MiddlewareQueryException {
		return this.getDatasetDao().getDatasetsByType(type.getValue());
	}

	@Override
	public MappingPop getMappingPopByDatasetId(Integer datasetId) throws MiddlewareQueryException {
		return this.getMappingPopDao().getMappingPopByDatasetId(datasetId);
	}

	@Override
	public List<Dataset> getDatasetDetailsByDatasetIds(List<Integer> datasetIds) throws MiddlewareQueryException {
		return this.getDatasetDao().getDatasetsByIds(datasetIds);

	}

	@Override
	public List<Integer> getQTLIdsByDatasetIds(List<Integer> datasetIds) throws MiddlewareQueryException {
		return this.getQtlDao().getQTLIdsByDatasetIds(datasetIds);
	}

	@Override
	public List<AccMetadataSet> getAllFromAccMetadataset(List<Integer> gIds, Integer datasetId, SetOperation operation)
			throws MiddlewareQueryException {
		return this.getAccMetadataSetDao().getAccMetadataSetByGidsAndDatasetId(gIds, datasetId, operation);
	}

	@Override
	public List<MapDetailElement> getMapAndMarkerCountByMarkers(List<Integer> markerIds) throws MiddlewareQueryException {
		return this.getMapDao().getMapAndMarkerCountByMarkers(markerIds);
	}

	@Override
	public List<Mta> getAllMTAs() throws MiddlewareQueryException {
		return this.getMtaDao().getAll();
	}

	@Override
	public long countAllMTAs() throws MiddlewareQueryException {
		return this.getMtaDao().countAll();
	}

	@Override
	public List<Mta> getMTAsByTrait(Integer traitId) throws MiddlewareQueryException {
		return this.getMtaDao().getMtasByTrait(traitId);
	}

	@Override
	public void deleteQTLs(List<Integer> qtlIds, Integer datasetId) throws MiddlewareQueryException {

		try {

			// delete qtl and qtl details
			this.getQtlDetailsDao().deleteByQtlIds(qtlIds);
			this.getQtlDao().deleteByQtlIds(qtlIds);

			// delete dataset users and dataset
			this.getDatasetUsersDao().deleteByDatasetId(datasetId);
			this.getDatasetDao().deleteByDatasetId(datasetId);

		} catch (Exception e) {

			this.logAndThrowException("Cannot delete QTLs and Dataset: GenotypicDataManager.deleteQTLs(qtlIds=" + qtlIds
					+ " and datasetId = " + datasetId + "):  " + e.getMessage(), e);
		}
	}

	@Override
	public void deleteSNPGenotypingDatasets(Integer datasetId) throws MiddlewareQueryException {

		try {

			this.getCharValuesDao().deleteByDatasetId(datasetId);
			this.getDatasetUsersDao().deleteByDatasetId(datasetId);
			this.getAccMetadataSetDao().deleteByDatasetId(datasetId);
			this.getMarkerMetadataSetDao().deleteByDatasetId(datasetId);
			this.getDatasetDao().deleteByDatasetId(datasetId);

		} catch (Exception e) {

			this.logAndThrowException(
					"Cannot delete SNP Genotyping Datasets: " + "GenotypicDataManager.deleteSNPGenotypingDatasets(datasetId = " + datasetId
							+ "):  " + e.getMessage(), e);
		}
	}

	@Override
	public void deleteMappingPopulationDatasets(Integer datasetId) throws MiddlewareQueryException {

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

		} catch (Exception e) {

			this.logAndThrowException("Cannot delete Mapping Population Datasets: "
					+ "GenotypicDataManager.deleteMappingPopulationDatasets(datasetId = " + datasetId + "):  " + e.getMessage(), e);
		}
	}

	@Override
	public List<QtlDetails> getQtlDetailsByMapId(Integer mapId) throws MiddlewareQueryException {
		return this.getQtlDetailsDao().getQtlDetailsByMapId(mapId);
	}

	@Override
	public long countQtlDetailsByMapId(Integer mapId) throws MiddlewareQueryException {
		return this.getQtlDetailsDao().countQtlDetailsByMapId(mapId);
	}

	@Override
	public void deleteMaps(Integer mapId) throws MiddlewareQueryException {

		try {

			this.getMarkerOnMapDao().deleteByMapId(mapId);
			this.getMapDao().deleteByMapId(mapId);

		} catch (Exception e) {

			this.logAndThrowException("Cannot delete Mapping Population Datasets: "
					+ "GenotypicDataManager.deleteMappingPopulationDatasets(datasetId = " + mapId + "):  " + e.getMessage(), e);
		}
	}

	@Override
	public List<MarkerSampleId> getMarkerFromCharValuesByGids(List<Integer> gIds) throws MiddlewareQueryException {
		return this.getCharValuesDao().getMarkerSampleIdsByGids(gIds);
	}

	@Override
	public List<MarkerSampleId> getMarkerFromAlleleValuesByGids(List<Integer> gIds) throws MiddlewareQueryException {
		return this.getAlleleValuesDao().getMarkerSampleIdsByGids(gIds);
	}

	@Override
	public List<MarkerSampleId> getMarkerFromMappingPopByGids(List<Integer> gIds) throws MiddlewareQueryException {
		return this.getMappingPopValuesDao().getMarkerSampleIdsByGids(gIds);
	}

	@Override
	public void addMTA(Dataset dataset, Mta mta, MtaMetadata mtaMetadata, DatasetUsers users) throws MiddlewareQueryException {

		if (dataset == null) {
			throw new MiddlewareQueryException("Dataset passed must not be null");
		}

		try {

			dataset.setDatasetType(GenotypicDataManagerImpl.TYPE_MTA);
			dataset.setUploadTemplateDate(new Date());

			this.getDatasetDao().merge(dataset);

			users.setDataset(dataset);
			this.getDatasetUsersDao().merge(users);

			MtaDAO mtaDao = this.getMtaDao();
			mta.setDatasetId(dataset.getDatasetId());
			mtaDao.save(mta);

			MtaMetadataDAO mtaMetadataDao = this.getMtaMetadataDao();
			mtaMetadata.setDatasetID(dataset.getDatasetId());
			mtaMetadataDao.merge(mtaMetadata);

		} catch (Exception e) {

			this.logAndThrowException("Error in GenotypicDataManager.addMTA: " + e.getMessage(), e);
		}
	}

	@Override
	public void setMTA(Dataset dataset, DatasetUsers users, List<Mta> mtaList, MtaMetadata mtaMetadata) throws MiddlewareQueryException {

		if (dataset == null) {
			throw new MiddlewareQueryException("Dataset passed must not be null");
		}

		try {

			dataset.setDatasetType(GenotypicDataManagerImpl.TYPE_MTA);
			dataset.setUploadTemplateDate(new Date());

			this.getDatasetDao().merge(dataset);

			users.setDataset(dataset);
			this.getDatasetUsersDao().merge(users);

			MtaDAO mtaDao = this.getMtaDao();
			MtaMetadataDAO mtaMetadataDao = this.getMtaMetadataDao();

			for (Mta mta : mtaList) {
				mta.setDatasetId(dataset.getDatasetId());
				mtaDao.merge(mta);

				mtaMetadata.setDatasetID(dataset.getDatasetId());
				mtaMetadataDao.merge(mtaMetadata);
			}

		} catch (Exception e) {

			this.logAndThrowException("Error in GenotypicDataManager.addMTAs: " + e.getMessage(), e);
		}
	}

	@Override
	public void deleteMTA(List<Integer> datasetIds) throws MiddlewareQueryException {

		try {

			for (Integer datasetId : datasetIds) {

				// delete mta, dataset users and dataset
				this.getMtaDao().deleteByDatasetId(datasetId);
				this.getDatasetUsersDao().deleteByDatasetId(datasetId);
				this.getDatasetDao().deleteByDatasetId(datasetId);
			}

		} catch (Exception e) {

			this.logAndThrowException("Cannot delete MTAs and Dataset: GenotypicDataManager.deleteMTA(datasetIds=" + datasetIds + "):  "
					+ e.getMessage(), e);
		}

	}

	@Override
	public void addMtaMetadata(MtaMetadata mtaMetadata) throws MiddlewareQueryException {

		if (mtaMetadata == null) {
			throw new MiddlewareQueryException("Error in GenotypicDataManager.addMtaMetadata: MtaMetadata must not be null.");
		}
		if (mtaMetadata.getDatasetID() == null) {
			throw new MiddlewareQueryException("Error in GenotypicDataManager.addMtaMetadata: MtaMetadata.datasetID must not be null.");
		}

		try {

			// No need to generate id. The id (mta_id) is a foreign key
			MtaMetadataDAO mtaMetadataDao = this.getMtaMetadataDao();
			mtaMetadataDao.save(mtaMetadata);

		} catch (Exception e) {

			this.logAndThrowException("Error in GenotypicDataManager.addMtaMetadata: " + e.getMessage(), e);
		}
	}

	// --------------------------------- COMMON SAVER METHODS ------------------------------------------//

	private Integer saveMarkerIfNotExisting(Marker marker, String markerType) throws Exception {
		this.getActiveSession();

		Integer markerId = marker.getMarkerId();

		// If the marker has same marker name existing in local, use the existing record.
		if (markerId == null) {
			Integer markerIdWithName = this.getMarkerIdByMarkerName(marker.getMarkerName());
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
			MarkerDAO markerDao = this.getMarkerDao();
			marker.setMarkerType(markerType);
			Marker markerRecordSaved = markerDao.saveOrUpdate(marker);
			markerId = markerRecordSaved.getMarkerId();
		}

		if (markerId == null) {
			throw new Exception(); // To immediately roll back and to avoid executing the other insert functions
		}

		return markerId;
	}

	// If the marker is not yet in the database, add.
	private Integer saveMarker(Marker marker, String markerType) throws Exception {
		Integer markerId = marker.getMarkerId();

		// If the marker has same marker name existing in local, use the existing record.
		if (markerId == null) {
			this.getActiveSession();
			this.getMarkerIdByMarkerName(marker.getMarkerName());
		}

		// Save the marker
		this.getActiveSession();
		MarkerDAO markerDao = this.getMarkerDao();
		marker.setMarkerType(markerType);
		Marker markerRecordSaved = markerDao.merge(marker);
		markerId = markerRecordSaved.getMarkerId();

		if (markerId == null) {
			throw new Exception(); // To immediately roll back and to avoid executing the other insert functions
		}

		return markerId;
	}

	private void saveMarkers(List<Marker> markers) throws Exception {

		this.getActiveSession();
		MarkerDAO markerDao = this.getMarkerDao();
		if (markers != null) {
			for (Marker marker : markers) {
				markerDao.merge(marker);
			}
		}
	}

	private void updateMarkerInfo(Marker marker) throws Exception {

		if (marker == null || marker.getMarkerId() == null) {
			throw new MiddlewareException("Marker is null and cannot be updated.");
		}

		this.getActiveSession();
		MarkerDAO markerDao = this.getMarkerDao();

		Integer markerId = marker.getMarkerId();
		// Marker id, name and species cannot be updated.
		Marker markerFromDB = this.getMarkerDao().getById(markerId);
		if (markerFromDB == null) {
			throw new MiddlewareException("Marker is not found in the database and cannot be updated.");
		}
		if (!marker.getMarkerName().equals(markerFromDB.getMarkerName()) || !marker.getSpecies().equals(markerFromDB.getSpecies())) {
			throw new MiddlewareException("Marker name and species cannot be updated.");
		}

		markerDao.merge(marker);

	}

	private Integer saveMarkerAlias(MarkerAlias markerAlias) throws Exception {
		this.getActiveSession();

		MarkerAlias markerAliasRecordSaved = this.getMarkerAliasDao().save(markerAlias);
		Integer markerAliasRecordSavedMarkerId = markerAliasRecordSaved.getMarkerId();
		if (markerAliasRecordSavedMarkerId == null) {
			throw new Exception();
		}
		return markerAliasRecordSavedMarkerId;
	}

	private Integer saveOrUpdateMarkerAlias(MarkerAlias markerAlias) throws Exception {
		this.getActiveSession();
		MarkerAlias markerAliasFromDB = this.getMarkerAliasDao().getById(markerAlias.getMarkerId());
		if (markerAliasFromDB == null) {
			return this.saveMarkerAlias(markerAlias);
		} else {
			this.getMarkerAliasDao().merge(markerAlias);
		}
		return markerAlias.getMarkerId();
	}

	private Integer saveMarkerDetails(MarkerDetails markerDetails) throws Exception {
		this.getActiveSession();
		MarkerDetails markerDetailsRecordSaved = this.getMarkerDetailsDao().save(markerDetails);
		Integer markerDetailsSavedMarkerId = markerDetailsRecordSaved.getMarkerId();
		if (markerDetailsSavedMarkerId == null) {
			throw new Exception();
		}
		return markerDetailsSavedMarkerId;
	}

	private Integer saveOrUpdateMarkerDetails(MarkerDetails markerDetails) throws Exception {
		this.getActiveSession();
		MarkerDetails markerDetailsFromDB = this.getMarkerDetailsDao().getById(markerDetails.getMarkerId());
		if (markerDetailsFromDB == null) {
			return this.saveMarkerDetails(markerDetails);
		} else {
			this.getMarkerDetailsDao().merge(markerDetails);
		}
		return markerDetails.getMarkerId();
	}

	private Integer saveMarkerUserInfo(MarkerUserInfo markerUserInfo) throws Exception {
		this.getActiveSession();

		MarkerUserInfoDAO dao = this.getMarkerUserInfoDao();
		MarkerUserInfo markerUserInfoRecordSaved = dao.save(markerUserInfo);
		Integer markerUserInfoSavedId = markerUserInfoRecordSaved.getMarkerId();
		if (markerUserInfoSavedId == null) {
			throw new Exception();
		}
		return markerUserInfoSavedId;
	}

	private Integer saveOrUpdateMarkerUserInfo(MarkerUserInfo markerUserInfo) throws Exception {
		this.getActiveSession();
		MarkerUserInfoDAO dao = this.getMarkerUserInfoDao();

		if (markerUserInfo.getUserInfoId() != null) {
			MarkerUserInfo markerDetailsFromDB = this.getMarkerUserInfoDao().getById(markerUserInfo.getUserInfoId());
			if (markerDetailsFromDB == null) {
				return this.saveMarkerUserInfo(markerUserInfo);
			}
		}
		dao.merge(markerUserInfo);
		return markerUserInfo.getUserInfoId();
	}

	private Integer saveMap(Map map) throws Exception {
		this.getActiveSession();

		Integer mapSavedId = map.getMapId() == null ? this.getMapIdByMapName(map.getMapName()) : map.getMapId();
		if (mapSavedId == null) {
			MapDAO mapDao = this.getMapDao();

			Map mapRecordSaved = mapDao.saveOrUpdate(map);
			mapSavedId = mapRecordSaved.getMapId();
		}

		if (mapSavedId == null) {
			throw new Exception(); // To immediately roll back and to avoid executing the other insert functions
		}
		return mapSavedId;

	}

	private Integer saveMarkerOnMap(Integer markerId, Integer mapId, MarkerOnMap markerOnMap) throws Exception {
		this.getActiveSession();
		MarkerOnMapDAO markerOnMapDao = this.getMarkerOnMapDao();

		markerOnMap.setMarkerId(markerId);
		markerOnMap.setMapId(mapId);

		if (markerOnMapDao.findByMarkerIdAndMapId(markerId, mapId) != null) {
			throw new Exception("The marker on map combination already exists (markerId=" + markerId + ", mapId=" + mapId + ")");
		}
		MarkerOnMap markerOnMapRecordSaved = markerOnMapDao.save(markerOnMap);
		Integer markerOnMapSavedId = markerOnMapRecordSaved.getMapId();

		if (markerOnMapSavedId == null) {
			throw new Exception();
		}
		return markerOnMapSavedId;

	}

	// GCP-7873
	@Override
	public List<Marker> getAllSNPMarkers() throws MiddlewareQueryException {
		return this.getMarkerDao().getByType(GenotypicDataManagerImpl.TYPE_SNP);
	}

	// GCP-8568
	@Override
	public List<Marker> getMarkersByType(String type) throws MiddlewareQueryException {
		return this.getMarkerDao().getMarkersByType(type);
	}

	// GCP-7874
	@Override
	public List<Marker> getSNPsByHaplotype(String haplotype) throws MiddlewareQueryException {
		List<Integer> markerIds = this.getMarkerDao().getMarkerIDsByHaplotype(haplotype);
		return this.getMarkerDao().getMarkersByIdsAndType(markerIds, GdmsType.TYPE_SNP.getValue());
	}

	// GCP-8566
	@Override
	public void addHaplotype(TrackData trackData, List<TrackMarker> trackMarkers) throws MiddlewareQueryException {

		try {

			this.getTrackDataDao().save(trackData);

			for (TrackMarker trackMarker : trackMarkers) {
				trackMarker.setTrackId(trackData.getTrackId());
				this.getTrackMarkerDao().save(trackMarker);
			}

		} catch (Exception e) {

			this.logAndThrowException("Error in GenotypicDataManager.addHaplotype(trackData=" + trackData + ", trackMarkers="
					+ trackMarkers + "): " + e.getMessage(), e);
		}

	}

	// GCP-7881
	@Override
	public List<MarkerInfo> getMarkerInfoByMarkerIds(List<Integer> markerIds) throws MiddlewareQueryException {
		List<MarkerInfo> returnVal = new ArrayList<MarkerInfo>();
		returnVal.addAll(this.getMarkerInfoDao().getByMarkerIds(markerIds));
		return returnVal;
	}

	// GCP-7875
	@Override
	public List<AllelicValueElement> getAlleleValuesByMarkers(List<Integer> markerIds) throws MiddlewareQueryException {
		List<AllelicValueElement> returnVal = new ArrayList<>();
		returnVal.addAll(this.getAlleleValuesDao().getAlleleValuesByMarkerId(markerIds));
		returnVal.addAll(this.getCharValuesDao().getAlleleValuesByMarkerId(markerIds));
		return returnVal;
	}

	@Override
	public Boolean updateMarkerInfo(Marker marker, MarkerAlias markerAlias, MarkerDetails markerDetails, MarkerUserInfo markerUserInfo)
			throws MiddlewareQueryException {

		if (marker.getMarkerId() >= 0) {
			Marker markerFromDB = this.getMarkerDao().getById(marker.getMarkerId());
			if (markerFromDB != null) {
				throw new MiddlewareQueryException("Marker is in central database and cannot be updated.");
			} else {
				throw new MiddlewareQueryException("The given marker has positive id but is not found in central. Update cannot proceed.");
			}
		}

		try {

			// Update GDMS Marker - update all fields except marker_id, marker_name and species
			this.updateMarkerInfo(marker);
			Integer markerId = marker.getMarkerId();

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

		} catch (Exception e) {
			throw new MiddlewareQueryException("Error encountered while updating MarkerInfo: updateMarkerInfo(marker=" + marker
					+ ", markerAlias=" + markerAlias + ", markerDetails=" + markerDetails + ", markerUserInfo=" + markerUserInfo + "): "
					+ e.getMessage(), e);
		}
	}

	@Override
	public List<MarkerMetadataSet> getMarkerMetadataSetByDatasetId(Integer datasetId) throws MiddlewareQueryException {
		return this.getMarkerMetadataSetDao().getMarkerMetadataSetByDatasetId(datasetId);
	}

	@Override
	public List<CharValues> getCharValuesByMarkerIds(List<Integer> markerIds) throws MiddlewareQueryException {
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
	public List<Object> getUniqueCharAllelesByGidsAndMids(List<Integer> gids, List<Integer> mids) {
		return this.getCharValuesDao().getUniqueCharAllelesByGidsAndMids(gids, mids);
	}

	@Override
	public List<Object> getUniqueAllelesByGidsAndMids(List<Integer> gids, List<Integer> mids) {
		return this.getAlleleValuesDao().getUniqueAllelesByGidsAndMids(gids, mids);
	}

	@Override
	public List<Object> getUniqueMapPopAllelesByGidsAndMids(List<Integer> gids, List<Integer> mids) {
		return this.getMappingPopValuesDao().getUniqueMapPopAllelesByGidsAndMids(gids, mids);
	}

	@Override
	public List<Object> getUniqueAccMetaDataSetByGids(List gids) {
		return this.getAccMetadataSetDao().getUniqueAccMetaDatsetByGids(gids);
	}

	@Override
	public List<Integer> getMarkerIdsByNames(List<String> names, int start, int numOfRows) {
		return this.getMarkerDao().getIdsByNames(names, start, numOfRows);
	}

	@Override
	public int countAllMarkers() {
		return this.getMarkerDao().getAll().size();
	}

	@Override
	public List<Integer> getDatasetIdsByGermplasmIds(List<Integer> gIds) throws MiddlewareQueryException {
		return this.getAccMetadataSetDao().getDatasetIdsByGermplasmIds(gIds);
	}

	@Override
	public List<Integer> getAccMetadatasetByDatasetIds(List<Integer> datasetIds) throws MiddlewareQueryException {
		return this.getAccMetadataSetDao().getNidsByDatasetIds(datasetIds);
	}

	@Override
	public List<Object> getMarkersOnMapByMarkerIdsAndMapId(List<Integer> markerIds, Integer mapID) {
		return this.getMarkerOnMapDao().getMarkersOnMapByMarkerIdsAndMapId(markerIds, mapID);
	}

	@Override
	public List<MarkerOnMap> getMarkerOnMapByLinkageGroupAndMapIdAndNotInMarkerId(Integer mapId, Integer linkageGroupId, Integer markerId) {
		return this.getMarkerOnMapDao().getMarkerOnMapByLinkageGroupAndMapIdAndNotInMarkerId(mapId, linkageGroupId, markerId);
	}

	@Override
	public java.util.Map<Integer, Integer> getGIDsBySampleIds(final Set<Integer> sampleIds) {
		return daoFactory.getSampleDao().getGIDsBySampleIds(sampleIds);
	}

}
