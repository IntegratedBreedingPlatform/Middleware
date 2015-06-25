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

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.dao.gdms.AccMetadataSetDAO;
import org.generationcp.middleware.dao.gdms.AlleleValuesDAO;
import org.generationcp.middleware.dao.gdms.CharValuesDAO;
import org.generationcp.middleware.dao.gdms.DartValuesDAO;
import org.generationcp.middleware.dao.gdms.DatasetDAO;
import org.generationcp.middleware.dao.gdms.DatasetUsersDAO;
import org.generationcp.middleware.dao.gdms.MapDAO;
import org.generationcp.middleware.dao.gdms.MappingPopDAO;
import org.generationcp.middleware.dao.gdms.MappingPopValuesDAO;
import org.generationcp.middleware.dao.gdms.MarkerDAO;
import org.generationcp.middleware.dao.gdms.MarkerDetailsDAO;
import org.generationcp.middleware.dao.gdms.MarkerMetadataSetDAO;
import org.generationcp.middleware.dao.gdms.MarkerOnMapDAO;
import org.generationcp.middleware.dao.gdms.MarkerUserInfoDAO;
import org.generationcp.middleware.dao.gdms.MtaDAO;
import org.generationcp.middleware.dao.gdms.MtaMetadataDAO;
import org.generationcp.middleware.dao.gdms.QtlDAO;
import org.generationcp.middleware.dao.gdms.QtlDetailsDAO;
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
import org.generationcp.middleware.pojos.gdms.DartDataRow;
import org.generationcp.middleware.pojos.gdms.DartValues;
import org.generationcp.middleware.pojos.gdms.Dataset;
import org.generationcp.middleware.pojos.gdms.DatasetElement;
import org.generationcp.middleware.pojos.gdms.DatasetUsers;
import org.generationcp.middleware.pojos.gdms.ExtendedMarkerInfo;
import org.generationcp.middleware.pojos.gdms.GermplasmMarkerElement;
import org.generationcp.middleware.pojos.gdms.Map;
import org.generationcp.middleware.pojos.gdms.MapDetailElement;
import org.generationcp.middleware.pojos.gdms.MapInfo;
import org.generationcp.middleware.pojos.gdms.MappingABHRow;
import org.generationcp.middleware.pojos.gdms.MappingAllelicSNPRow;
import org.generationcp.middleware.pojos.gdms.MappingAllelicSSRDArTRow;
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
import org.generationcp.middleware.pojos.gdms.MarkerUserInfoDetails;
import org.generationcp.middleware.pojos.gdms.Mta;
import org.generationcp.middleware.pojos.gdms.MtaMetadata;
import org.generationcp.middleware.pojos.gdms.ParentElement;
import org.generationcp.middleware.pojos.gdms.Qtl;
import org.generationcp.middleware.pojos.gdms.QtlDataElement;
import org.generationcp.middleware.pojos.gdms.QtlDataRow;
import org.generationcp.middleware.pojos.gdms.QtlDetailElement;
import org.generationcp.middleware.pojos.gdms.QtlDetails;
import org.generationcp.middleware.pojos.gdms.SNPDataRow;
import org.generationcp.middleware.pojos.gdms.SSRDataRow;
import org.generationcp.middleware.pojos.gdms.TrackData;
import org.generationcp.middleware.pojos.gdms.TrackMarker;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.generationcp.middleware.util.DatabaseBroker;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the GenotypicDataManager interface. To instantiate this class, a Hibernate Session must be passed to its constructor.
 *
 * @author Joyce Avestro, Glenn Marintes, Daniel Villafuerte
 */
@SuppressWarnings("unchecked")
public class GenotypicDataManagerImpl extends DataManager implements GenotypicDataManager {

	private static final Logger LOG = LoggerFactory.getLogger(GenotypicDataManagerImpl.class);

	private static final String TYPE_SSR = GdmsType.TYPE_SSR.getValue();
	private static final String TYPE_SNP = GdmsType.TYPE_SNP.getValue();
	private static final String TYPE_DART = GdmsType.TYPE_DART.getValue();
	private static final String TYPE_MAPPING = GdmsType.TYPE_MAPPING.getValue();
	private static final String TYPE_MTA = GdmsType.TYPE_MTA.getValue();
	private static final String TYPE_QTL = GdmsType.TYPE_QTL.getValue();
	private static final String TYPE_CAP = GdmsType.TYPE_CAP.getValue();
	private static final String TYPE_CISR = GdmsType.TYPE_CISR.getValue();
	private static final String TYPE_UA = GdmsType.TYPE_UA.getValue(); // Unassigned

	private static final String DATA_TYPE_INT = GdmsType.DATA_TYPE_INT.getValue();
	private static final String DATA_TYPE_MAP = GdmsType.DATA_TYPE_MAP.getValue();

	public GenotypicDataManagerImpl() {
	}

	public GenotypicDataManagerImpl(HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
	}

	@Override
	public List<Integer> getMapIDsByQTLName(String qtlName, int start, int numOfRows) throws MiddlewareQueryException {
		if (qtlName == null || qtlName.isEmpty()) {
			return new ArrayList<Integer>();
		}

		return new ArrayList<>(this.getQtlDao().getMapIDsByQTLName(qtlName, start, numOfRows));
	}

	@Override
	public long countMapIDsByQTLName(String qtlName) throws MiddlewareQueryException {

		return this.getQtlDao().countMapIDsByQTLName(qtlName);

	}

	@Override
	public List<Integer> getNameIdsByGermplasmIds(List<Integer> gIds) throws MiddlewareQueryException {

		return this.getAccMetadataSetDao().getNameIdsByGermplasmIds(gIds);

	}

	@Override
	public List<Name> getNamesByNameIds(List<Integer> nIds) throws MiddlewareQueryException {
		return this.getNameDao().getNamesByNameIds(nIds);
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
		List<MapInfo> mapInfoList = new ArrayList<MapInfo>();

		// Step 1: Get map id by map name
		Map map = this.getMapDao().getByName(mapName);
		if (map == null) {
			return new ArrayList<MapInfo>();
		}

		// Step 2: Get markerId, linkageGroup, startPosition from gdms_markers_onmap
		List<MarkerOnMap> markersOnMap = this.getMarkerOnMapDao().getMarkersOnMapByMapId(map.getMapId());

		// Step 3: Get marker name from gdms_marker and build MapInfo
		for (MarkerOnMap markerOnMap : markersOnMap) {
			Integer markerId = markerOnMap.getMarkerId();
			String markerName = this.getMarkerNameByMarkerId(markerId);
			MapInfo mapInfo =
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
		List<MapInfo> mapInfoList = this.getMapDao().getMapInfoByMapAndChromosome(mapId, chromosome);
		return mapInfoList;
	}

	@Override
	public List<MapInfo> getMapInfoByMapChromosomeAndPosition(int mapId, String chromosome, float startPosition)
			throws MiddlewareQueryException {
		List<MapInfo> mapInfoList = this.getMapDao().getMapInfoByMapChromosomeAndPosition(mapId, chromosome, startPosition);
		return mapInfoList;
	}

	@Override
	public List<MapInfo> getMapInfoByMarkersAndMap(List<Integer> markers, Integer mapId) throws MiddlewareQueryException {
		List<MapInfo> mapInfoList = this.getMapDao().getMapInfoByMarkersAndMap(markers, mapId);
		return mapInfoList;
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
	public List<DatasetElement> getDatasetDetailsByDatasetName(String datasetName, Database instance) throws MiddlewareQueryException {
		return this.getDatasetDao().getDetailsByName(datasetName);
	}

	@Override
	public List<Marker> getMarkersByMarkerNames(List<String> markerNames, int start, int numOfRows, Database instance)
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
		Set<MarkerNameElement> set = new HashSet<MarkerNameElement>();
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
		List<MappingValueElement> mappingValueElementList = new ArrayList<>();

		List<Marker> markers = this.getMarkerDao().getByNames(markerNames, start, numOfRows);

		List<Integer> markerIds = new ArrayList<Integer>();
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
		List<AllelicValueElement> allelicValues = new ArrayList<AllelicValueElement>();

		// Get marker_ids by marker_names
		java.util.Map<Integer, String> markerIdName = this.getMarkerDao().getFirstMarkerIdByMarkerName(markerNames, Database.LOCAL);
		List<Integer> markerIds = new ArrayList<Integer>(markerIdName.keySet());

		allelicValues.addAll(this.getMarkerDao().getAllelicValuesByGidsAndMarkerIds(gids, markerIds));

		for (AllelicValueElement allelicValue : allelicValues) {
			allelicValue.setMarkerName(markerIdName.get(allelicValue.getMarkerId()));
		}

		return allelicValues;
	}

	@Override
	public List<AllelicValueWithMarkerIdElement> getAllelicValuesFromCharValuesByDatasetId(Integer datasetId, int start, int numOfRows)
			throws MiddlewareQueryException {
		return this.getCharValuesDao().getAllelicValuesByDatasetId(datasetId, start, numOfRows);
	}

	@Override
	public long countAllelicValuesFromCharValuesByDatasetId(Integer datasetId) throws MiddlewareQueryException {
		return this.getCharValuesDao().countByDatasetId(datasetId);
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
		List<Integer> inputList = new ArrayList<Integer>();
		inputList.add(targetGID);

		List<MarkerNameElement> markerNameElements = this.getMarkerNamesByGIds(inputList);

		List<String> markerNames = new ArrayList<String>();

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
		List<MarkerIdMarkerNameElement> markersToReturn = new ArrayList<MarkerIdMarkerNameElement>();
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
	public long countGIDsFromAlleleValuesByMarkerId(Integer markerId) throws MiddlewareQueryException {
		return this.getAlleleValuesDao().countGIDsByMarkerId(markerId);
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
	public List<AllelicValueElement> getAllelicValuesByMarkersAndAlleleValues(Database instance, List<Integer> markerIdList,
			List<String> alleleValueList) throws MiddlewareQueryException {
		List<AllelicValueElement> elements = new ArrayList<AllelicValueElement>();
		elements.addAll(this.getAlleleValuesDao().getByMarkersAndAlleleValues(markerIdList, alleleValueList));
		elements.addAll(this.getCharValuesDao().getByMarkersAndAlleleValues(markerIdList, alleleValueList));
		return elements;
	}

	@Override
	public List<AllelicValueElement> getAllAllelicValuesByMarkersAndAlleleValues(List<Integer> markerIdList, List<String> alleleValueList)
			throws MiddlewareQueryException {
		return this.getAllelicValuesByMarkersAndAlleleValues(Database.LOCAL, markerIdList, alleleValueList);
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
		return this.getAccMetadatasetsByDatasetIdsAndNotGids(datasetIds, null, start, numOfRows);
	}

	@Override
	public List<AccMetadataSet> getAccMetadatasetsByDatasetIdsAndNotGids(List<Integer> datasetIds, List<Integer> notGids, int start,
			int numOfRows) throws MiddlewareQueryException {
		return this.getAccMetadataSetDao().getByDatasetIdsAndNotInGids(datasetIds, notGids, start, numOfRows);
	}

	private List<Integer> getNIdsByMarkerIdsAndDatasetIdsAndNotGIdsFromDB(List<Integer> datasetIds, List<Integer> markerIds,
			List<Integer> gIds, int start, int numOfRows) throws MiddlewareQueryException {
		Set<Integer> nidSet = new TreeSet<Integer>();
		nidSet.addAll(this.getAccMetadataSetDao().getNIdsByMarkerIdsAndDatasetIdsAndNotGIds(datasetIds, markerIds, gIds, start, numOfRows));
		return new ArrayList<Integer>(((TreeSet<Integer>) nidSet).descendingSet());
	}

	@Override
	public List<Integer> getNIdsByMarkerIdsAndDatasetIdsAndNotGIds(List<Integer> datasetIds, List<Integer> markerIds, List<Integer> gIds,
			int start, int numOfRows) throws MiddlewareQueryException {
		return this.getNIdsByMarkerIdsAndDatasetIdsAndNotGIdsFromDB(datasetIds, markerIds, gIds, start, numOfRows);
	}

	@Override
	public int countNIdsByMarkerIdsAndDatasetIdsAndNotGIds(List<Integer> datasetIds, List<Integer> markerIds, List<Integer> gIds)
			throws MiddlewareQueryException {
		return (int) this.getAccMetadataSetDao().countNIdsByMarkerIdsAndDatasetIdsAndNotGIds(datasetIds, markerIds, gIds);
	}

	private List<Integer> getNIdsByMarkerIdsAndDatasetIds(List<Integer> datasetIds, List<Integer> markerIds)
			throws MiddlewareQueryException {
		Set<Integer> nidSet = new TreeSet<Integer>();
		nidSet.addAll(this.getAccMetadataSetDao().getNIdsByMarkerIdsAndDatasetIds(datasetIds, markerIds));
		return new ArrayList<Integer>(((TreeSet<Integer>) nidSet).descendingSet());
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
	public List<Integer> getMarkersByGidAndDatasetIds(Integer gid, List<Integer> datasetIds, int start, int numOfRows)
			throws MiddlewareQueryException {
		return this.getMarkerMetadataSetDao().getMarkersByGidAndDatasetIds(gid, datasetIds, start, numOfRows);
	}

	@Override
	public long countMarkersByGidAndDatasetIds(Integer gid, List<Integer> datasetIds) throws MiddlewareQueryException {
		return this.getMarkerMetadataSetDao().countMarkersByGidAndDatasetIds(gid, datasetIds);
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
	public long countCharValuesByGids(List<Integer> gids) throws MiddlewareQueryException {
		return this.getCharValuesDao().countCharValuesByGids(gids);
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
			return new ArrayList<Integer>();
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
		List<QtlDetailElement> qtlDetailElements = new ArrayList<QtlDetailElement>();
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
		java.util.Map<Integer, String> qtlNames = new HashMap<Integer, String>();
		qtlNames.putAll(this.getQtlDao().getQtlNameByQtlIds(qtlIds));
		return qtlNames;
	}

	// TODO BMS-148 : Review for how to safely remove the dual db read pattern without breaking any logic.
	@Override
	public List<QtlDetailElement> getQtlByQtlIds(List<Integer> qtlIds, int start, int numOfRows) throws MiddlewareQueryException {
		List<QtlDetailElement> qtlDetailElements = new ArrayList<QtlDetailElement>();

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

		java.util.Map<Integer, List<String>> markerMaps = new HashMap<Integer, List<String>>();

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
		List<Marker> markers = new ArrayList<Marker>();
		markers.addAll(this.getMarkerDao().getMarkersByIds(markerIds, start, numOfRows));
		return markers;
	}

	@Override
	public java.util.Map<Integer, String> getMarkerTypeMapByIds(List<Integer> markerIds) throws MiddlewareQueryException {
		java.util.Map<Integer, String> markerTypes = new HashMap<Integer, String>();
		if (markerIds != null && !markerIds.isEmpty()) {
			markerTypes.putAll(this.getMarkerDao().getMarkerTypeMapByIds(markerIds));
		}
		return markerTypes;
	}

	@Override
	public Integer addQtlDetails(QtlDetails qtlDetails) throws MiddlewareQueryException {
		Session session = this.getActiveSession();
		Transaction trans = null;
		Integer savedId = null;
		try {
			trans = session.beginTransaction();

			// No need to auto-assign negative IDs for new local DB records
			// qtlId and mapId are foreign keys

			QtlDetails recordSaved = this.getQtlDetailsDao().save(qtlDetails);
			savedId = recordSaved.getQtlId();

			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Error encountered while saving Qtl Details: GenotypicDataManager.addQtlDetails(qtlDetails="
					+ qtlDetails + "): " + e.getMessage(), e, GenotypicDataManagerImpl.LOG);
		} finally {
			session.flush();
		}
		return savedId;

	}

	@Override
	public Integer addMarker(Marker marker) throws MiddlewareQueryException {
		marker.setMarkerId(this.getMarkerDao().getNextId("markerId"));
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
		MarkerUserInfoDetails details = markerUserInfo.getMarkerUserInfoDetails();
		if (details != null && details.getContactId() == null) {
			details.setContactId(this.getMarkerUserInfoDetailsDao().getNextId("contactId"));
		}
		markerUserInfo.setUserInfoId(this.getMarkerUserInfoDao().getNextId("userInfoId"));
		return ((MarkerUserInfo) super.save(this.getMarkerUserInfoDao(), markerUserInfo)).getUserInfoId();
	}

	@Override
	public Integer addAccMetadataSet(AccMetadataSet accMetadataSet) throws MiddlewareQueryException {
		Session session = this.getActiveSession();
		Transaction trans = null;
		Integer savedId = null;

		try {
			trans = session.beginTransaction();

			AccMetadataSetDAO dao = this.getAccMetadataSetDao();
			Integer generatedId = dao.getNextId("accMetadataSetId");
			accMetadataSet.setAccMetadataSetId(generatedId);

			AccMetadataSet recordSaved = this.getAccMetadataSetDao().save(accMetadataSet);
			savedId = recordSaved.getAccMetadataSetId();

			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Error encountered with addAccMetadataSet(accMetadataSet=" + accMetadataSet + "): " + e.getMessage(),
					e, GenotypicDataManagerImpl.LOG);
		} finally {
			session.flush();
		}
		return savedId;
	}

	@Override
	public Integer addMarkerMetadataSet(MarkerMetadataSet markerMetadataSet) throws MiddlewareQueryException {
		Session session = this.getActiveSession();
		Transaction trans = null;
		Integer savedId = null;

		try {
			trans = session.beginTransaction();

			if (markerMetadataSet != null && markerMetadataSet.getMarkerMetadataSetId() == null) {
				markerMetadataSet.setMarkerMetadataSetId(this.getMarkerMetadataSetDao().getNextId("markerMetadataSetId"));
			}

			MarkerMetadataSet recordSaved = this.getMarkerMetadataSetDao().save(markerMetadataSet);
			savedId = recordSaved.getMarkerMetadataSetId();

			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException(
					"Error encountered with addMarkerMetadataSet(markerMetadataSet=" + markerMetadataSet + "): " + e.getMessage(), e,
					GenotypicDataManagerImpl.LOG);
		} finally {
			session.flush();
		}
		return savedId;
	}

	@Override
	public Integer addDataset(Dataset dataset) throws MiddlewareQueryException {
		dataset.setDatasetId(this.getDatasetDao().getNextId("datasetId"));
		return ((Dataset) super.save(this.getDatasetDao(), dataset)).getDatasetId();
	}

	@Override
	public Integer addGDMSMarker(Marker marker) throws MiddlewareQueryException {
		// Check for existence. duplicate marker names are not allowed.

		Session session = this.getActiveSession();
		Transaction trans = null;
		Integer id = null;
		try {
			// begin save transaction
			trans = session.beginTransaction();
			id = this.saveMarkerIfNotExisting(marker, marker.getMarkerType());
			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Error encountered while adding Marker: " + e.getMessage(), e, GenotypicDataManagerImpl.LOG);
		} finally {
			session.flush();
		}
		return id;
	}

	@Override
	public Integer addGDMSMarkerAlias(MarkerAlias markerAlias) throws MiddlewareQueryException {
		markerAlias.setMarkerAliasId(this.getMarkerAliasDao().getNextId("markerAliasId"));
		return ((MarkerAlias) super.save(this.getMarkerAliasDao(), markerAlias)).getMarkerId();
	}

	@Override
	public Integer addDatasetUser(DatasetUsers datasetUser) throws MiddlewareQueryException {
		DatasetUsersDAO dao = this.getDatasetUsersDao();
		DatasetUsers user = dao.getById(datasetUser.getDatasetId());
		if (user == null) {
			return ((DatasetUsers) super.save(dao, datasetUser)).getUserId();
		}

		return user.getUserId();
	}

	@Override
	public Integer addAlleleValues(AlleleValues alleleValues) throws MiddlewareQueryException {
		alleleValues.setAnId(this.getAlleleValuesDao().getNextId("anId"));
		return ((AlleleValues) super.saveOrUpdate(this.getAlleleValuesDao(), alleleValues)).getAnId();
	}

	@Override
	public Integer addCharValues(CharValues charValues) throws MiddlewareQueryException {
		charValues.setAcId(this.getCharValuesDao().getNextId("acId"));
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
		mappingPopValue.setMpId(this.getMappingPopValuesDao().getNextId("mpId"));
		return ((MappingPopValues) super.saveOrUpdate(this.getMappingPopValuesDao(), mappingPopValue)).getMpId();
	}

	@Override
	public Integer addMarkerOnMap(MarkerOnMap markerOnMap) throws MiddlewareQueryException {
		if (this.getMapDao().getById(markerOnMap.getMapId()) == null) {
			throw new MiddlewareQueryException("Map Id not found: " + markerOnMap.getMapId());
		}

		markerOnMap.setMarkerOnMapId(this.getMarkerOnMapDao().getNextId("markerOnMapId"));
		return ((MarkerOnMap) super.save(this.getMarkerOnMapDao(), markerOnMap)).getMapId();
	}

	@Override
	public Integer addDartValue(DartValues dartValue) throws MiddlewareQueryException {
		dartValue.setAdId(this.getDartValuesDao().getNextId("adId"));
		return ((DartValues) super.save(this.getDartValuesDao(), dartValue)).getAdId();
	}

	@Override
	public Integer addQtl(Qtl qtl) throws MiddlewareQueryException {
		qtl.setQtlId(this.getQtlDao().getNextId("qtlId"));
		return ((Qtl) super.saveOrUpdate(this.getQtlDao(), qtl)).getQtlId();
	}

	@Override
	public Integer addMap(Map map) throws MiddlewareQueryException {
		map.setMapId(this.getMapDao().getNextId("mapId"));
		return ((Map) super.saveOrUpdate(this.getMapDao(), map)).getMapId();
	}

	@Override
	public Boolean setSSRMarkers(Marker marker, MarkerAlias markerAlias, MarkerDetails markerDetails, MarkerUserInfo markerUserInfo)
			throws MiddlewareQueryException {
		return this.setMarker(marker, GenotypicDataManagerImpl.TYPE_SSR, markerAlias, markerDetails, markerUserInfo);
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

	@Override
	public Boolean setDArTMarkers(Marker marker, MarkerAlias markerAlias, MarkerDetails markerDetails, MarkerUserInfo markerUserInfo)
			throws MiddlewareQueryException {
		return this.setMarker(marker, GenotypicDataManagerImpl.TYPE_DART, markerAlias, markerDetails, markerUserInfo);

	}

	private Boolean setMarker(Marker marker, String markerType, MarkerAlias markerAlias, MarkerDetails markerDetails,
			MarkerUserInfo markerUserInfo) throws MiddlewareQueryException {
		Session session = this.getActiveSession();
		Transaction trans = null;

		try {
			// begin save transaction
			trans = session.beginTransaction();

			// Add GDMS Marker
			Integer idGDMSMarkerSaved = this.saveMarkerIfNotExisting(marker, markerType);
			marker.setMarkerId(idGDMSMarkerSaved);
			marker.setMarkerType(markerType);

			// Add GDMS Marker Alias
			markerAlias.setMarkerAliasId(this.getMarkerAliasDao().getNextId("markerAliasId"));
			markerAlias.setMarkerId(idGDMSMarkerSaved);
			this.saveMarkerAlias(markerAlias);

			// Add Marker Details
			markerDetails.setMarkerId(idGDMSMarkerSaved);
			this.saveMarkerDetails(markerDetails);

			// Add marker user info
			markerUserInfo.setMarkerId(idGDMSMarkerSaved);
			this.saveMarkerUserInfo(markerUserInfo);

			trans.commit();
			return true;
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Error encountered while saving Marker: " + e.getMessage(), e, GenotypicDataManagerImpl.LOG);
			return false;
		} finally {
			session.flush();
		}

	}

	@Override
	public Boolean setQTL(Dataset dataset, DatasetUsers datasetUser, List<QtlDataRow> rows) throws MiddlewareQueryException {
		Session session = this.getActiveSession();
		Transaction trans = null;
		try {
			trans = session.beginTransaction();

			Integer datasetId = this.saveDataset(dataset, GenotypicDataManagerImpl.TYPE_QTL, null);

			this.saveDatasetUser(datasetId, datasetUser);

			// Save QTL data rows
			if (rows != null && !rows.isEmpty()) {
				for (QtlDataRow row : rows) {
					Qtl qtl = row.getQtl();
					QtlDetails qtlDetails = row.getQtlDetails();

					Integer qtlIdSaved = this.saveQtl(datasetId, qtl);
					qtlDetails.setQtlId(qtlIdSaved);
					this.saveQtlDetails(qtlDetails);
				}
			}

			trans.commit();
			return true;
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Error encountered with setQTL(): " + e.getMessage(), e, GenotypicDataManagerImpl.LOG);
			return false;
		} finally {
			session.flush();
		}
	}

	@Override
	public Boolean setDart(Dataset dataset, DatasetUsers datasetUser, List<Marker> markers, List<MarkerMetadataSet> markerMetadataSets,
			List<AccMetadataSet> accMetadataSets, List<DartValues> dartValueList, List<AlleleValues> alleleValueList)
			throws MiddlewareQueryException {

		Session session = this.getActiveSession();
		Transaction trans = null;

		try {
			trans = session.beginTransaction();

			dataset.setDatasetType(GenotypicDataManagerImpl.TYPE_DART);
			dataset.setDataType(GenotypicDataManagerImpl.DATA_TYPE_INT);
			Integer datasetId = this.saveDatasetDatasetUserMarkersAndMarkerMetadataSets(dataset, datasetUser, markers, markerMetadataSets);

			// Save data rows
			for (AccMetadataSet accMetadataSet : accMetadataSets) {
				accMetadataSet.setDatasetId(datasetId);
			}

			for (AlleleValues alleleValue : alleleValueList) {
				alleleValue.setDatasetId(datasetId);
			}

			for (DartValues dartValue : dartValueList) {
				dartValue.setDatasetId(datasetId);
			}

			this.saveAccMetadataSets(accMetadataSets);
			this.saveAlleleValues(alleleValueList);
			this.saveDartValues(dartValueList);

			trans.commit();
			return true;
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Error encountered while setting DArT: setDart(): " + e.getMessage(), e, GenotypicDataManagerImpl.LOG);
			return false;
		} finally {
			session.flush();
		}
	}

	@Override
	public Boolean setSSR(Dataset dataset, DatasetUsers datasetUser, List<Marker> markers, List<MarkerMetadataSet> markerMetadataSets,
			List<AccMetadataSet> accMetadataSets, List<AlleleValues> alleleValueList) throws MiddlewareQueryException {

		Session session = this.getActiveSession();
		Transaction trans = null;

		try {
			trans = session.beginTransaction();
			dataset.setDatasetType(GenotypicDataManagerImpl.TYPE_SSR);
			dataset.setDataType(GenotypicDataManagerImpl.DATA_TYPE_INT);

			Integer datasetId = this.saveDatasetDatasetUserMarkersAndMarkerMetadataSets(dataset, datasetUser, markers, markerMetadataSets);

			// Save data rows
			for (AccMetadataSet accMetadataSet : accMetadataSets) {
				accMetadataSet.setDatasetId(datasetId);
			}

			for (AlleleValues alleleValue : alleleValueList) {
				alleleValue.setDatasetId(datasetId);
			}

			this.saveAccMetadataSets(accMetadataSets);
			this.saveAlleleValues(alleleValueList);

			trans.commit();
			return true;
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Error encountered while setting SSR: setSSR(): " + e.getMessage(), e, GenotypicDataManagerImpl.LOG);
			return false;
		} finally {
			session.flush();
		}
	}

	@Override
	public Boolean setSNP(Dataset dataset, DatasetUsers datasetUser, List<Marker> markers, List<MarkerMetadataSet> markerMetadataSets,
			List<AccMetadataSet> accMetadataSets, List<CharValues> charValueList) throws MiddlewareQueryException {

		Session session = this.getActiveSession();
		Transaction trans = null;

		try {
			trans = session.beginTransaction();

			dataset.setDatasetType(GenotypicDataManagerImpl.TYPE_SNP);
			dataset.setDataType(GenotypicDataManagerImpl.DATA_TYPE_INT);
			Integer datasetId = this.saveDatasetDatasetUserMarkersAndMarkerMetadataSets(dataset, datasetUser, markers, markerMetadataSets);

			// Save data rows
			for (AccMetadataSet accMetadataSet : accMetadataSets) {
				accMetadataSet.setDatasetId(datasetId);
			}
			for (CharValues charValue : charValueList) {
				charValue.setDatasetId(datasetId);
			}

			this.saveAccMetadataSets(accMetadataSets);
			this.saveCharValues(charValueList);

			trans.commit();
			return true;
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Error encountered while setting SNP: setSNP(): " + e.getMessage(), e, GenotypicDataManagerImpl.LOG);
			return false;
		} finally {
			session.flush();
		}
	}

	@Override
	public Boolean setMappingABH(Dataset dataset, DatasetUsers datasetUser, MappingPop mappingPop, List<Marker> markers,
			List<MarkerMetadataSet> markerMetadataSets, List<AccMetadataSet> accMetadataSets, List<MappingPopValues> mappingPopValueList)
			throws MiddlewareQueryException {

		Session session = this.getActiveSession();
		Transaction trans = null;

		try {
			trans = session.beginTransaction();

			Integer datasetId = this.saveMappingData(dataset, datasetUser, mappingPop, markers, markerMetadataSets);

			// Save data rows
			for (AccMetadataSet accMetadataSet : accMetadataSets) {
				accMetadataSet.setDatasetId(datasetId);
			}

			for (MappingPopValues mappingPopValue : mappingPopValueList) {
				mappingPopValue.setDatasetId(datasetId);
			}

			this.saveAccMetadataSets(accMetadataSets);
			this.saveMappingPopValues(mappingPopValueList);

			trans.commit();
			return true;
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Error encountered while setting MappingABH: setMappingABH(): " + e.getMessage(), e,
					GenotypicDataManagerImpl.LOG);
			return false;
		} finally {
			session.flush();
		}
	}

	@Override
	public Boolean setMappingAllelicSNP(Dataset dataset, DatasetUsers datasetUser, MappingPop mappingPop, List<Marker> markers,
			List<MarkerMetadataSet> markerMetadataSets, List<AccMetadataSet> accMetadataSets, List<MappingPopValues> mappingPopValueList,
			List<CharValues> charValueList) throws MiddlewareQueryException {

		Session session = this.getActiveSession();
		Transaction trans = null;

		try {
			trans = session.beginTransaction();

			Integer datasetId = this.saveMappingData(dataset, datasetUser, mappingPop, markers, markerMetadataSets);

			// Save data rows
			for (AccMetadataSet accMetadataSet : accMetadataSets) {
				accMetadataSet.setDatasetId(datasetId);
			}

			for (MappingPopValues mappingPopValue : mappingPopValueList) {
				mappingPopValue.setDatasetId(datasetId);
			}

			for (CharValues charValue : charValueList) {
				charValue.setDatasetId(datasetId);
			}

			this.saveAccMetadataSets(accMetadataSets);
			this.saveMappingPopValues(mappingPopValueList);
			this.saveCharValues(charValueList);

			trans.commit();
			return true;
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Error encountered while setting MappingAllelicSNP: setMappingAllelicSNP(): " + e.getMessage(), e,
					GenotypicDataManagerImpl.LOG);
			return false;
		} finally {
			session.flush();
		}
	}

	@Override
	public Boolean setMappingAllelicSSRDArT(Dataset dataset, DatasetUsers datasetUser, MappingPop mappingPop, List<Marker> markers,
			List<MarkerMetadataSet> markerMetadataSets, List<AccMetadataSet> accMetadataSets, List<MappingPopValues> mappingPopValueList,
			List<AlleleValues> alleleValueList, List<DartValues> dartValueList) throws MiddlewareQueryException {

		Session session = this.getActiveSession();
		Transaction trans = null;

		try {
			trans = session.beginTransaction();

			Integer datasetId = this.saveMappingData(dataset, datasetUser, mappingPop, markers, markerMetadataSets);

			// Save data rows
			for (AccMetadataSet accMetadataSet : accMetadataSets) {
				accMetadataSet.setDatasetId(datasetId);
			}

			for (MappingPopValues mappingPopValue : mappingPopValueList) {
				mappingPopValue.setDatasetId(datasetId);
			}

			for (AlleleValues alleleValue : alleleValueList) {
				alleleValue.setDatasetId(datasetId);
			}

			for (DartValues dartValue : dartValueList) {
				dartValue.setDatasetId(datasetId);
			}

			this.saveAccMetadataSets(accMetadataSets);
			this.saveMappingPopValues(mappingPopValueList);
			this.saveAlleleValues(alleleValueList);
			this.saveDartValues(dartValueList);

			trans.commit();
			return true;
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException(
					"Error encountered while setting MappingAllelicSSRDArT: setMappingAllelicSSRDArT(): " + e.getMessage(), e,
					GenotypicDataManagerImpl.LOG);
			return false;
		} finally {
			session.flush();
		}
	}

	@Override
	public Boolean updateDart(Dataset dataset, List<Marker> markers, List<MarkerMetadataSet> markerMetadataSets, List<DartDataRow> rows)
			throws MiddlewareQueryException, MiddlewareException {

		if (dataset == null || dataset.getDatasetId() == null) {
			throw new MiddlewareException("Dataset is null and cannot be updated.");
		}
		Session session = this.getActiveSession();
		Transaction trans = null;

		try {
			trans = session.beginTransaction();

			Integer datasetId = this.updateDatasetMarkersAndMarkerMetadataSets(dataset, markers, markerMetadataSets);

			// Save data rows
			if (rows != null && !rows.isEmpty()) {

				List<AccMetadataSet> accMetadataSets = new ArrayList<AccMetadataSet>();
				List<AlleleValues> alleleValues = new ArrayList<AlleleValues>();
				List<DartValues> dartValues = new ArrayList<DartValues>();

				for (DartDataRow row : rows) {

					// AlleleValues is mandatory
					AlleleValues alleleValue = row.getAlleleValues();
					if (alleleValue == null) {
						throw new MiddlewareException("AlleleValues must not be null: " + row.toString());
					}

					// Save or update AccMetadaset
					AccMetadataSet accMetadataSet = row.getAccMetadataSet();
					accMetadataSet.setDatasetId(datasetId);
					accMetadataSets.add(accMetadataSet);

					// Save or update alleleValues
					alleleValue.setDatasetId(datasetId);
					alleleValues.add(alleleValue);

					// Save or update dartValues
					DartValues dartValue = row.getDartValues();
					dartValue.setDatasetId(datasetId);
					dartValues.add(dartValue);
				}

				this.saveAccMetadataSets(accMetadataSets);
				this.saveAlleleValues(alleleValues);
				this.saveDartValues(dartValues);

			}

			trans.commit();
			return true;
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Error encountered while updating DArT: updateDart(): " + e.getMessage(), e,
					GenotypicDataManagerImpl.LOG);
		} finally {
			session.flush();
		}
		return false;
	}

	@Override
	public Boolean updateSSR(Dataset dataset, List<Marker> markers, List<MarkerMetadataSet> markerMetadataSets, List<SSRDataRow> rows)
			throws MiddlewareQueryException, MiddlewareException {

		if (dataset == null || dataset.getDatasetId() == null) {
			throw new MiddlewareException("Dataset is null and cannot be updated.");
		}

		Session session = this.getActiveSession();
		Transaction trans = null;

		try {
			trans = session.beginTransaction();

			Integer datasetId = this.updateDatasetMarkersAndMarkerMetadataSets(dataset, markers, markerMetadataSets);

			// Save data rows
			if (rows != null && !rows.isEmpty()) {

				List<AccMetadataSet> accMetadataSets = new ArrayList<AccMetadataSet>();
				List<AlleleValues> alleleValues = new ArrayList<AlleleValues>();

				for (SSRDataRow row : rows) {

					// AlleleValues is mandatory
					AlleleValues alleleValue = row.getAlleleValues();
					if (alleleValue == null) {
						throw new MiddlewareException("AlleleValues must not be null: " + row.toString());
					}

					// Save or update AccMetadaset
					AccMetadataSet accMetadataSet = row.getAccMetadataSet();
					accMetadataSet.setDatasetId(datasetId);
					accMetadataSets.add(accMetadataSet);

					// Save or update alleleValues
					alleleValue.setDatasetId(datasetId);
					alleleValues.add(alleleValue);

				}

				this.saveAccMetadataSets(accMetadataSets);
				this.saveAlleleValues(alleleValues);

			}

			trans.commit();
			return true;
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Error encountered while updating SSR: updateSSR(): " + e.getMessage(), e,
					GenotypicDataManagerImpl.LOG);
		} finally {
			session.flush();
		}
		return false;
	}

	@Override
	public Boolean updateSNP(Dataset dataset, List<Marker> markers, List<MarkerMetadataSet> markerMetadataSets, List<SNPDataRow> rows)
			throws MiddlewareQueryException, MiddlewareException {

		if (dataset == null || dataset.getDatasetId() == null) {
			throw new MiddlewareException("Dataset is null and cannot be updated.");
		}

		Session session = this.getActiveSession();
		Transaction trans = null;

		try {
			trans = session.beginTransaction();

			Integer datasetId = this.updateDatasetMarkersAndMarkerMetadataSets(dataset, markers, markerMetadataSets);

			// Save data rows
			if (rows != null && !rows.isEmpty()) {

				List<AccMetadataSet> accMetadataSets = new ArrayList<AccMetadataSet>();
				List<CharValues> charValues = new ArrayList<CharValues>();

				for (SNPDataRow row : rows) {

					// CharValues is mandatory
					CharValues charValue = row.getCharValues();
					if (charValue == null) {
						throw new MiddlewareException("CharValues must not be null: " + row.toString());
					}

					// Save or update AccMetadaset
					AccMetadataSet accMetadataSet = row.getAccMetadataSet();
					accMetadataSet.setDatasetId(datasetId);
					accMetadataSets.add(accMetadataSet);

					// Save or update charValues
					charValue.setDatasetId(datasetId);
					charValues.add(charValue);

				}

				this.saveAccMetadataSets(accMetadataSets);
				this.saveCharValues(charValues);
			}

			trans.commit();
			return true;
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Error encountered while updating SNP: updateSNP(): " + e.getMessage(), e,
					GenotypicDataManagerImpl.LOG);
		} finally {
			session.flush();
		}
		return false;
	}

	@Override
	public Boolean updateMappingABH(Dataset dataset, MappingPop mappingPop, List<Marker> markers,
			List<MarkerMetadataSet> markerMetadataSets, List<MappingABHRow> rows) throws MiddlewareQueryException, MiddlewareException {

		if (dataset == null || dataset.getDatasetId() == null) {
			throw new MiddlewareException("Dataset is null and cannot be updated.");
		}

		Session session = this.getActiveSession();
		Transaction trans = null;

		try {
			trans = session.beginTransaction();

			Integer datasetId = this.saveOrUpdateMappingData(dataset, mappingPop, markers, markerMetadataSets);

			// Save data rows
			if (rows != null && !rows.isEmpty()) {

				List<AccMetadataSet> accMetadataSets = new ArrayList<AccMetadataSet>();
				List<MappingPopValues> mappingPopValues = new ArrayList<MappingPopValues>();

				for (MappingABHRow row : rows) {

					// MappingPopValues is mandatory
					MappingPopValues mappingPopValue = row.getMappingPopValues();
					if (mappingPopValue == null) {
						throw new MiddlewareException("MappingPopValues must not be null: " + row.toString());
					}

					// Save or update AccMetadaset
					AccMetadataSet accMetadataSet = row.getAccMetadataSet();
					accMetadataSet.setDatasetId(datasetId);
					accMetadataSets.add(accMetadataSet);

					// Save or update mappingPopValues
					mappingPopValue.setDatasetId(datasetId);
					mappingPopValues.add(mappingPopValue);

				}

				this.saveAccMetadataSets(accMetadataSets);
				this.saveMappingPopValues(mappingPopValues);
			}

			trans.commit();
			return true;
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Error encountered while updating MappingABH: updateMappingABH(): " + e.getMessage(), e,
					GenotypicDataManagerImpl.LOG);
		} finally {
			session.flush();
		}
		return false;
	}

	@Override
	public Boolean updateMappingAllelicSNP(Dataset dataset, MappingPop mappingPop, List<Marker> markers,
			List<MarkerMetadataSet> markerMetadataSets, List<MappingAllelicSNPRow> rows) throws MiddlewareQueryException,
			MiddlewareException {

		if (dataset == null || dataset.getDatasetId() == null) {
			throw new MiddlewareException("Dataset is null and cannot be updated.");
		}

		Session session = this.getActiveSession();
		Transaction trans = null;

		try {
			trans = session.beginTransaction();

			Integer datasetId = this.saveOrUpdateMappingData(dataset, mappingPop, markers, markerMetadataSets);

			// Save data rows
			if (rows != null && !rows.isEmpty()) {

				List<AccMetadataSet> accMetadataSets = new ArrayList<AccMetadataSet>();
				List<MappingPopValues> mappingPopValues = new ArrayList<MappingPopValues>();
				List<CharValues> charValues = new ArrayList<CharValues>();

				for (MappingAllelicSNPRow row : rows) {

					// MappingPopValues is mandatory
					MappingPopValues mappingPopValue = row.getMappingPopValues();
					if (mappingPopValue == null) {
						throw new MiddlewareException("MappingPopValues must not be null: " + row.toString());
					}

					// Save or update AccMetadaset
					AccMetadataSet accMetadataSet = row.getAccMetadataSet();
					accMetadataSet.setDatasetId(datasetId);
					accMetadataSets.add(accMetadataSet);

					// Save or update mappingPopValues
					mappingPopValue.setDatasetId(datasetId);
					mappingPopValues.add(mappingPopValue);

					// Save or update charValues
					CharValues charValue = row.getCharValues();
					charValue.setDatasetId(datasetId);
					charValues.add(charValue);

				}

				this.saveAccMetadataSets(accMetadataSets);
				this.saveMappingPopValues(mappingPopValues);
				this.saveCharValues(charValues);
			}

			trans.commit();
			return true;
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Error encountered while updating MappingAllelicSNP: updateMappingAllelicSNP(): " + e.getMessage(),
					e, GenotypicDataManagerImpl.LOG);
		} finally {
			session.flush();
		}
		return false;
	}

	@Override
	public Boolean updateMappingAllelicSSRDArT(Dataset dataset, MappingPop mappingPop, List<Marker> markers,
			List<MarkerMetadataSet> markerMetadataSets, List<MappingAllelicSSRDArTRow> rows) throws MiddlewareQueryException,
			MiddlewareException {

		if (dataset == null || dataset.getDatasetId() == null) {
			throw new MiddlewareException("Dataset is null and cannot be updated.");
		}

		Session session = this.getActiveSession();
		Transaction trans = null;

		try {
			trans = session.beginTransaction();

			Integer datasetId = this.saveOrUpdateMappingData(dataset, mappingPop, markers, markerMetadataSets);

			// Save data rows
			if (rows != null && !rows.isEmpty()) {

				List<AccMetadataSet> accMetadataSets = new ArrayList<AccMetadataSet>();
				List<MappingPopValues> mappingPopValues = new ArrayList<MappingPopValues>();
				List<AlleleValues> alleleValues = new ArrayList<AlleleValues>();
				List<DartValues> dartValues = new ArrayList<DartValues>();

				for (MappingAllelicSSRDArTRow row : rows) {

					// MappingPopValues is mandatory
					MappingPopValues mappingPopValue = row.getMappingPopValues();
					if (mappingPopValue == null) {
						throw new MiddlewareException("MappingPopValues must not be null: " + row.toString());
					}

					// Save or update AccMetadaset
					AccMetadataSet accMetadataSet = row.getAccMetadataSet();
					accMetadataSet.setDatasetId(datasetId);
					accMetadataSets.add(accMetadataSet);

					// Save or update mappingPopValues
					mappingPopValue.setDatasetId(datasetId);
					mappingPopValues.add(mappingPopValue);

					// Save or update alleleValues
					AlleleValues alleleValue = row.getAlleleValues();
					alleleValue.setDatasetId(datasetId);
					alleleValues.add(alleleValue);

					DartValues dartValue = row.getDartValues();
					dartValue.setDatasetId(datasetId);
					dartValues.add(dartValue);

				}

				this.saveAccMetadataSets(accMetadataSets);
				this.saveMappingPopValues(mappingPopValues);
				this.saveAlleleValues(alleleValues);
				this.saveDartValues(dartValues);
			}

			trans.commit();
			return true;
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException(
					"Error encountered while updating MappingAllelicSSRDArT updateMappingAllelicSSRDArT(): " + e.getMessage(), e,
					GenotypicDataManagerImpl.LOG);
		} finally {
			session.flush();
		}
		return false;

	}

	private Integer saveMappingData(Dataset dataset, DatasetUsers datasetUser, MappingPop mappingPop, List<Marker> markers,
			List<MarkerMetadataSet> markerMetadataSets) throws Exception {

		dataset.setDatasetType(GenotypicDataManagerImpl.TYPE_MAPPING);
		dataset.setDataType(GenotypicDataManagerImpl.DATA_TYPE_MAP);
		Integer datasetId = this.saveDatasetDatasetUserMarkersAndMarkerMetadataSets(dataset, datasetUser, markers, markerMetadataSets);
		this.saveMappingPop(datasetId, mappingPop);
		return datasetId;
	}

	private Integer saveOrUpdateMappingData(Dataset dataset, MappingPop mappingPop, List<Marker> markers,
			List<MarkerMetadataSet> markerMetadataSets) throws Exception {

		if (dataset == null) {
			throw new MiddlewareException("dataset is null and cannot be saved nor updated.");
		}

		Integer datasetId = this.updateDatasetMarkersAndMarkerMetadataSets(dataset, markers, markerMetadataSets);

		// Save or update MappingPop
		if (mappingPop != null) {
			if (mappingPop.getDatasetId() == null) {
				this.saveMappingPop(datasetId, mappingPop);
			} else {
				this.updateMappingPop(datasetId, mappingPop);
			}
		}

		return datasetId;
	}

	@Override
	public Boolean setMaps(Marker marker, MarkerOnMap markerOnMap, Map map) throws MiddlewareQueryException {
		Session session = this.getActiveSession();
		Transaction trans = null;
		try {
			trans = session.beginTransaction();
			Integer markerSavedId = this.saveMarker(marker, GenotypicDataManagerImpl.TYPE_UA);
			Integer mapSavedId = this.saveMap(map);
			this.saveMarkerOnMap(markerSavedId, mapSavedId, markerOnMap);
			trans.commit();
			return true;
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Error encountered while setting Maps: setMaps(): " + e.getMessage(), e, GenotypicDataManagerImpl.LOG);
			return false;
		} finally {
			session.flush();
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
		return this.getAccMetadataSetDao().countNidsByDatasetIds(datasetIds);
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
	public List<Dataset> getDatasetsByMappingTypeFromLocal(GdmsType type) throws MiddlewareQueryException {
		return this.getDatasetDao().getDatasetsByMappingType(type);
	}

	@Override
	public MappingPop getMappingPopByDatasetId(Integer datasetId) throws MiddlewareQueryException {
		return this.getMappingPopDao().getMappingPopByDatasetId(datasetId);
	}

	private Dataset getDatasetByName(String datasetName) throws MiddlewareQueryException {
		return this.getDatasetDao().getByName(datasetName);
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
		Session session = this.getActiveSession();
		Transaction trans = null;

		try {
			trans = session.beginTransaction();

			// delete qtl and qtl details
			this.getQtlDetailsDao().deleteByQtlIds(qtlIds);
			this.getQtlDao().deleteByQtlIds(qtlIds);

			// delete dataset users and dataset
			this.getDatasetUsersDao().deleteByDatasetId(datasetId);
			this.getDatasetDao().deleteByDatasetId(datasetId);

			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Cannot delete QTLs and Dataset: GenotypicDataManager.deleteQTLs(qtlIds=" + qtlIds
					+ " and datasetId = " + datasetId + "):  " + e.getMessage(), e);
		}
	}

	@Override
	public void deleteSSRGenotypingDatasets(Integer datasetId) throws MiddlewareQueryException {
		Session session = this.getActiveSession();
		Transaction trans = null;

		try {
			trans = session.beginTransaction();
			this.getAlleleValuesDao().deleteByDatasetId(datasetId);
			this.getDatasetUsersDao().deleteByDatasetId(datasetId);
			this.getAccMetadataSetDao().deleteByDatasetId(datasetId);
			this.getMarkerMetadataSetDao().deleteByDatasetId(datasetId);
			this.getDatasetDao().deleteByDatasetId(datasetId);
			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Cannot delete SSR Genotyping Datasets: "
					+ "GenotypicDataManager.deleteSSRGenotypingDatasets(datasetId = " + datasetId + "):  " + e.getMessage(), e);
		}
	}

	@Override
	public void deleteSNPGenotypingDatasets(Integer datasetId) throws MiddlewareQueryException {
		Session session = this.getActiveSession();
		Transaction trans = null;

		try {
			trans = session.beginTransaction();
			this.getCharValuesDao().deleteByDatasetId(datasetId);
			this.getDatasetUsersDao().deleteByDatasetId(datasetId);
			this.getAccMetadataSetDao().deleteByDatasetId(datasetId);
			this.getMarkerMetadataSetDao().deleteByDatasetId(datasetId);
			this.getDatasetDao().deleteByDatasetId(datasetId);
			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Cannot delete SNP Genotyping Datasets: "
					+ "GenotypicDataManager.deleteSNPGenotypingDatasets(datasetId = " + datasetId + "):  " + e.getMessage(), e);
		}
	}

	@Override
	public void deleteDArTGenotypingDatasets(Integer datasetId) throws MiddlewareQueryException {
		Session session = this.getActiveSession();
		Transaction trans = null;

		try {
			trans = session.beginTransaction();
			this.getAlleleValuesDao().deleteByDatasetId(datasetId);
			this.getDartValuesDao().deleteByDatasetId(datasetId);
			this.getDatasetUsersDao().deleteByDatasetId(datasetId);
			this.getAccMetadataSetDao().deleteByDatasetId(datasetId);
			this.getMarkerMetadataSetDao().deleteByDatasetId(datasetId);
			this.getDatasetDao().deleteByDatasetId(datasetId);
			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Cannot delete DArT Genotyping Datasets: "
					+ "GenotypicDataManager.deleteDArTGenotypingDatasets(datasetId = " + datasetId + "):  " + e.getMessage(), e);
		}
	}

	@Override
	public void deleteMappingPopulationDatasets(Integer datasetId) throws MiddlewareQueryException {

		Session session = this.getActiveSession();
		Transaction trans = null;

		try {
			trans = session.beginTransaction();
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

			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
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
		Session session = this.getActiveSession();
		Transaction trans = null;

		try {
			trans = session.beginTransaction();

			this.getMarkerOnMapDao().deleteByMapId(mapId);
			this.getMapDao().deleteByMapId(mapId);

			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
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
	public long getLastId(Database instance, GdmsTable gdmsTable) throws MiddlewareQueryException {
		return GenericDAO.getLastId(this.getActiveSession(), instance, gdmsTable.getTableName(), gdmsTable.getIdName());
	}

	@Override
	public void addMTA(Dataset dataset, Mta mta, MtaMetadata mtaMetadata, DatasetUsers users) throws MiddlewareQueryException {
		Session session = this.getActiveSession();
		Transaction trans = null;

		if (dataset == null) {
			this.logAndThrowException("Dataset passed must not be null");
		}

		try {
			trans = session.beginTransaction();

			if (dataset.getDatasetId() == null) {
				dataset.setDatasetId(this.getDatasetDao().getNextId("datasetId"));
			}
			dataset.setDatasetType(GenotypicDataManagerImpl.TYPE_MTA);
			dataset.setUploadTemplateDate(new Date());
			// TODO review this -ve id based logic.. Overall, this whole methods seems to be unused no refs from GDMS code. Remove entirely,
			// once confirmed.
			if (dataset.getDatasetId() < 0) {
				this.getDatasetDao().merge(dataset);
			}

			users.setDatasetId(dataset.getDatasetId());
			this.getDatasetUsersDao().merge(users);

			MtaDAO mtaDao = this.getMtaDao();
			int id = mtaDao.getNextId("mtaId");
			mta.setMtaId(id);
			mta.setDatasetId(dataset.getDatasetId());
			mtaDao.merge(mta);

			MtaMetadataDAO mtaMetadataDao = this.getMtaMetadataDao();
			mtaMetadata.setDatasetID(dataset.getDatasetId());
			mtaMetadataDao.merge(mtaMetadata);

			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Error in GenotypicDataManager.addMTA: " + e.getMessage(), e);
		}
	}

	@Override
	public void setMTA(Dataset dataset, DatasetUsers users, List<Mta> mtaList, MtaMetadata mtaMetadata) throws MiddlewareQueryException {

		if (dataset == null) {
			this.logAndThrowException("Dataset passed must not be null");
		}

		Session session = this.getActiveSession();
		Transaction trans = null;

		try {
			trans = session.beginTransaction();

			if (dataset.getDatasetId() == null) {
				dataset.setDatasetId(this.getDatasetDao().getNextId("datasetId"));
			}
			dataset.setDatasetType(GenotypicDataManagerImpl.TYPE_MTA);
			dataset.setUploadTemplateDate(new Date());
			// TODO review this -ve id based logic.. Overall, this whole methods seems to be unused no refs from GDMS code. Remove entirely,
			// once confirmed.
			if (dataset.getDatasetId() < 0) {
				this.getDatasetDao().merge(dataset);
			}

			users.setDatasetId(dataset.getDatasetId());
			this.getDatasetUsersDao().merge(users);

			MtaDAO mtaDao = this.getMtaDao();
			MtaMetadataDAO mtaMetadataDao = this.getMtaMetadataDao();

			int rowsSaved = 0;
			int id = mtaDao.getNextId("mtaId");

			for (int i = 0; i < mtaList.size(); i++) {
				Mta mta = mtaList.get(i);
				mta.setMtaId(id);
				mta.setDatasetId(dataset.getDatasetId());
				mtaDao.merge(mta);

				mtaMetadata.setDatasetID(dataset.getDatasetId());
				mtaMetadataDao.merge(mtaMetadata);

				id++;

				rowsSaved++;
				if (rowsSaved % DatabaseBroker.JDBC_BATCH_SIZE == 0) {
					mtaDao.flush();
					mtaDao.clear();
					mtaMetadataDao.flush();
					mtaMetadataDao.clear();
				}
			}

			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Error in GenotypicDataManager.addMTAs: " + e.getMessage(), e);
		}
	}

	@Override
	public void deleteMTA(List<Integer> datasetIds) throws MiddlewareQueryException {
		Session session = this.getActiveSession();
		Transaction trans = null;

		try {
			trans = session.beginTransaction();

			for (Integer datasetId : datasetIds) {

				// delete mta, dataset users and dataset
				this.getMtaDao().deleteByDatasetId(datasetId);
				this.getDatasetUsersDao().deleteByDatasetId(datasetId);
				this.getDatasetDao().deleteByDatasetId(datasetId);
			}
			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Cannot delete MTAs and Dataset: GenotypicDataManager.deleteMTA(datasetIds=" + datasetIds + "):  "
					+ e.getMessage(), e);
		}

	}

	@Override
	public void addMtaMetadata(MtaMetadata mtaMetadata) throws MiddlewareQueryException {

		if (mtaMetadata == null) {
			this.logAndThrowException("Error in GenotypicDataManager.addMtaMetadata: MtaMetadata must not be null.");
		}
		if (mtaMetadata.getDatasetID() == null) {
			this.logAndThrowException("Error in GenotypicDataManager.addMtaMetadata: MtaMetadata.datasetID must not be null.");
		}

		Session session = this.getActiveSession();
		Transaction trans = null;

		try {
			trans = session.beginTransaction();

			// No need to generate id. The id (mta_id) is a foreign key
			MtaMetadataDAO mtaMetadataDao = this.getMtaMetadataDao();
			mtaMetadataDao.save(mtaMetadata);

			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Error in GenotypicDataManager.addMtaMetadata: " + e.getMessage(), e);
		}
	}

	// --------------------------------- COMMON SAVER METHODS ------------------------------------------//

	// Saves a dataset of the given datasetType and dataType
	private Integer saveDataset(Dataset dataset, String datasetType, String dataType) throws Exception {
		this.getActiveSession();

		// If the dataset has same dataset name existing in the database (local and central) - should throw an error.
		if (this.getDatasetByName(dataset.getDatasetName()) != null) {
			throw new MiddlewareQueryException("Dataset already exists. Please specify a new GDMS dataset record with a different name.");
		}

		// If the dataset is not yet existing in the database (local and central) - should create a new dataset in the local database.
		Integer datasetId = null;
		this.getActiveSession();
		DatasetDAO datasetDao = this.getDatasetDao();
		Integer datasetGeneratedId = datasetDao.getNextId("datasetId");
		dataset.setDatasetId(datasetGeneratedId);

		dataset.setDatasetType(datasetType);

		if (!datasetType.equals(GenotypicDataManagerImpl.TYPE_QTL)) {
			dataset.setDataType(dataType);
		}

		Dataset datasetRecordSaved = datasetDao.saveOrUpdate(dataset);
		datasetId = datasetRecordSaved.getDatasetId();

		if (datasetId == null) {
			throw new Exception(); // To immediately roll back and to avoid executing the other insert functions
		}

		return datasetId;

	}

	// Saves a dataset
	private Integer saveDataset(Dataset dataset) throws Exception {
		this.getActiveSession();
		DatasetDAO datasetDao = this.getDatasetDao();
		Integer datasetGeneratedId = datasetDao.getNextId("datasetId");
		dataset.setDatasetId(datasetGeneratedId);
		Dataset datasetRecordSaved = datasetDao.merge(dataset);
		return datasetRecordSaved.getDatasetId();
	}

	private Integer saveDatasetDatasetUserMarkersAndMarkerMetadataSets(Dataset dataset, DatasetUsers datasetUser, List<Marker> markers,
			List<MarkerMetadataSet> markerMetadataSets) throws Exception {

		Integer datasetId = this.saveDataset(dataset);
		dataset.setDatasetId(datasetId);

		this.saveDatasetUser(datasetId, datasetUser);

		this.saveMarkers(markers);

		if (markerMetadataSets != null && !markerMetadataSets.isEmpty()) {
			for (MarkerMetadataSet markerMetadataSet : markerMetadataSets) {
				markerMetadataSet.setDatasetId(datasetId);
			}
			this.saveMarkerMetadataSets(markerMetadataSets);
		}

		return datasetId;
	}

	private Integer updateDatasetMarkersAndMarkerMetadataSets(Dataset dataset, List<Marker> markers,
			List<MarkerMetadataSet> markerMetadataSets) throws Exception {

		Integer datasetId = this.updateDataset(dataset);
		dataset.setDatasetId(datasetId);

		this.saveMarkers(markers);

		if (markerMetadataSets != null && !markerMetadataSets.isEmpty()) {
			for (MarkerMetadataSet markerMetadataSet : markerMetadataSets) {
				markerMetadataSet.setDatasetId(datasetId);
			}
			this.saveMarkerMetadataSets(markerMetadataSets);
		}

		return datasetId;
	}

	private Integer updateDataset(Dataset dataset) throws Exception {
		this.getActiveSession();

		if (dataset == null) {
			throw new MiddlewareException("Dataset is null and cannot be updated.");
		}

		Integer datasetId = dataset.getDatasetId();

		if (datasetId == null) {
			datasetId = this.saveDataset(dataset);
		} else {
			this.getActiveSession();
			Dataset datasetSaved = this.getDatasetDao().merge(dataset);
			datasetId = datasetSaved.getDatasetId();
		}

		return datasetId;
	}

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
			Integer markerGeneratedId = markerDao.getNextId("markerId");
			marker.setMarkerId(markerGeneratedId);
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
			Integer markerIdWithName = this.getMarkerIdByMarkerName(marker.getMarkerName());
			if (markerIdWithName != null) {
				markerId = markerIdWithName;
			}
		}

		// Save the marker
		this.getActiveSession();
		MarkerDAO markerDao = this.getMarkerDao();
		Integer markerGeneratedId = markerDao.getNextId("markerId");
		marker.setMarkerId(markerGeneratedId);
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
		Integer markerGeneratedId = markerDao.getNextId("markerId");
		Integer rowsSaved = 0;

		if (markers != null) {
			for (Marker marker : markers) {

				if (marker.getMarkerId() == null) {
					marker.setMarkerId(markerGeneratedId);
					markerGeneratedId++;
				}
				markerDao.merge(marker);

				// Flush
				rowsSaved++;
				if (rowsSaved % DatabaseBroker.JDBC_BATCH_SIZE == 0) {
					markerDao.flush();
					markerDao.clear();
				}
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

		if (markerAlias.getMarkerAliasId() == null) {
			markerAlias.setMarkerAliasId(this.getMarkerAliasDao().getNextId("markerAliasId"));
		}

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

		// Set contact id
		if (markerUserInfo != null) {
			MarkerUserInfoDetails details = markerUserInfo.getMarkerUserInfoDetails();
			if (details != null && details.getContactId() == null) {
				details.setContactId(this.getMarkerUserInfoDetailsDao().getNextId("contactId"));
			}
		}

		MarkerUserInfoDAO dao = this.getMarkerUserInfoDao();
		markerUserInfo.setUserInfoId(dao.getNextId("userInfoId"));
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

		if (markerUserInfo.getUserInfoId() == null) {
			markerUserInfo.setUserInfoId(dao.getNextId("userInfoId"));

			// Set contact id
			if (markerUserInfo != null) {
				MarkerUserInfoDetails details = markerUserInfo.getMarkerUserInfoDetails();
				if (details != null && details.getContactId() == null) {
					details.setContactId(this.getMarkerUserInfoDetailsDao().getNextId("contactId"));
				}
			}
		} else {
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

			Integer mapGeneratedId = mapDao.getNextId("mapId");
			map.setMapId(mapGeneratedId);

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

		Integer generatedId = markerOnMapDao.getNextId("markerOnMapId");
		markerOnMap.setMarkerOnMapId(generatedId);
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

	private void saveAccMetadataSets(List<AccMetadataSet> accMetadataSets) throws Exception {

		this.getActiveSession();
		AccMetadataSetDAO accMetadataSetDao = this.getAccMetadataSetDao();
		Integer rowsSaved = 0;

		if (accMetadataSets != null) {
			for (AccMetadataSet accMetadataSet : accMetadataSets) {
				if (accMetadataSet.getAccMetadataSetId() == null) {
					accMetadataSet.setAccMetadataSetId(accMetadataSetDao.getNextId("accMetadataSetId"));
				}
				accMetadataSetDao.merge(accMetadataSet);
				rowsSaved++;
				if (rowsSaved % DatabaseBroker.JDBC_BATCH_SIZE == 0) {
					accMetadataSetDao.flush();
					accMetadataSetDao.clear();
				}
			}
		}
	}

	private void saveMarkerMetadataSets(List<MarkerMetadataSet> markerMetadataSets) throws Exception {
		this.getActiveSession();
		MarkerMetadataSetDAO markerMetadataSetDao = this.getMarkerMetadataSetDao();
		Integer rowsSaved = 0;

		for (MarkerMetadataSet markerMetadataSet : markerMetadataSets) {
			if (markerMetadataSet.getMarkerMetadataSetId() == null) {
				markerMetadataSet.setMarkerMetadataSetId(markerMetadataSetDao.getNextId("markerMetadataSetId"));
			}
			markerMetadataSetDao.merge(markerMetadataSet);
			rowsSaved++;
			if (rowsSaved % DatabaseBroker.JDBC_BATCH_SIZE == 0) {
				markerMetadataSetDao.flush();
				markerMetadataSetDao.clear();
			}
		}
	}

	private Integer saveDatasetUser(Integer datasetId, DatasetUsers datasetUser) throws Exception {
		this.getActiveSession();
		DatasetUsersDAO datasetUserDao = this.getDatasetUsersDao();
		datasetUser.setDatasetId(datasetId);

		DatasetUsers datasetUserSaved = datasetUserDao.saveOrUpdate(datasetUser);
		Integer datasetUserSavedId = datasetUserSaved.getUserId();

		if (datasetUserSavedId == null) {
			throw new Exception(); // To immediately roll back and to avoid executing the other insert functions
		}

		return datasetUserSavedId;

	}

	private Integer saveQtl(Integer datasetId, Qtl qtl) throws Exception {
		this.getActiveSession();
		QtlDAO qtlDao = this.getQtlDao();

		Integer qtlId = qtlDao.getNextId("qtlId");
		qtl.setQtlId(qtlId);

		qtl.setDatasetId(datasetId);

		Qtl qtlRecordSaved = qtlDao.saveOrUpdate(qtl);
		Integer qtlIdSaved = qtlRecordSaved.getQtlId();

		if (qtlIdSaved == null) {
			throw new Exception();
		}

		return qtlIdSaved;
	}

	private Integer saveQtlDetails(QtlDetails qtlDetails) throws Exception {
		this.getActiveSession();

		QtlDetailsDAO qtlDetailsDao = this.getQtlDetailsDao();
		QtlDetails qtlDetailsRecordSaved = qtlDetailsDao.saveOrUpdate(qtlDetails);
		Integer qtlDetailsSavedId = qtlDetailsRecordSaved.getQtlId();

		if (qtlDetailsSavedId == null) {
			throw new Exception();
		}

		return qtlDetailsSavedId;

	}

	private void saveCharValues(List<CharValues> charValuesList) throws Exception {
		if (charValuesList == null) {
			return;
		}
		this.getActiveSession();
		CharValuesDAO charValuesDao = this.getCharValuesDao();
		Integer rowsSaved = 0;

		Integer generatedId = charValuesDao.getNextId("acId");

		for (CharValues charValues : charValuesList) {
			if (charValues.getAcId() == null) {
				charValues.setAcId(generatedId);
				generatedId++;
			}
			charValuesDao.merge(charValues);

			rowsSaved++;
			if (rowsSaved % DatabaseBroker.JDBC_BATCH_SIZE == 0) {
				charValuesDao.flush();
				charValuesDao.clear();
			}
		}
	}

	private Integer saveMappingPop(Integer datasetId, MappingPop mappingPop) throws Exception {
		this.getActiveSession();
		MappingPopDAO mappingPopDao = this.getMappingPopDao();
		mappingPop.setDatasetId(datasetId);

		MappingPop mappingPopRecordSaved = mappingPopDao.save(mappingPop);
		Integer mappingPopSavedId = mappingPopRecordSaved.getDatasetId();

		if (mappingPopSavedId == null) {
			throw new Exception();
		}

		return mappingPopSavedId;
	}

	private Integer updateMappingPop(Integer datasetId, MappingPop mappingPop) throws Exception {
		this.getActiveSession();
		mappingPop.setDatasetId(datasetId);

		MappingPop mappingPopRecordSaved = this.getMappingPopDao().merge(mappingPop);
		return mappingPopRecordSaved.getDatasetId();
	}

	private void saveMappingPopValues(List<MappingPopValues> mappingPopValuesList) throws Exception {
		if (mappingPopValuesList == null) {
			return;
		}
		this.getActiveSession();
		MappingPopValuesDAO mappingPopValuesDao = this.getMappingPopValuesDao();
		Integer rowsSaved = 0;

		Integer generatedId = mappingPopValuesDao.getNextId("mpId");

		for (MappingPopValues mappingPopValues : mappingPopValuesList) {
			if (mappingPopValues.getMpId() == null) {
				mappingPopValues.setMpId(generatedId);
				generatedId++;
			}
			mappingPopValuesDao.merge(mappingPopValues);

			rowsSaved++;
			if (rowsSaved % DatabaseBroker.JDBC_BATCH_SIZE == 0) {
				mappingPopValuesDao.flush();
				mappingPopValuesDao.clear();
			}
		}
	}

	private void saveAlleleValues(List<AlleleValues> alleleValuesList) throws Exception {
		if (alleleValuesList == null) {
			return;
		}
		this.getActiveSession();
		AlleleValuesDAO alleleValuesDao = this.getAlleleValuesDao();
		Integer rowsSaved = 0;

		Integer generatedId = alleleValuesDao.getNextId("anId");

		for (AlleleValues alleleValues : alleleValuesList) {
			if (alleleValues.getAnId() == null) {
				alleleValues.setAnId(generatedId);
				generatedId++;
			}
			alleleValuesDao.merge(alleleValues);

			rowsSaved++;
			if (rowsSaved % DatabaseBroker.JDBC_BATCH_SIZE == 0) {
				alleleValuesDao.flush();
				alleleValuesDao.clear();
			}
		}

	}

	private void saveDartValues(List<DartValues> dartValuesList) throws Exception {

		if (dartValuesList == null) {
			return;
		}

		this.getActiveSession();
		DartValuesDAO dartValuesDao = this.getDartValuesDao();
		Integer rowsSaved = 0;

		Integer generatedId = dartValuesDao.getNextId("adId");

		for (DartValues dartValues : dartValuesList) {
			if (dartValues.getAdId() == null) {
				dartValues.setAdId(generatedId);
				generatedId++;
			}
			dartValuesDao.merge(dartValues);

			rowsSaved++;
			if (rowsSaved % DatabaseBroker.JDBC_BATCH_SIZE == 0) {
				dartValuesDao.flush();
				dartValuesDao.clear();
			}

		}
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
		Session session = this.getActiveSession();
		Transaction trans = null;

		try {
			trans = session.beginTransaction();

			trackData.setTrackId(this.getTrackDataDao().getNextId("trackId"));
			this.getTrackDataDao().save(trackData);

			for (TrackMarker trackMarker : trackMarkers) {
				trackMarker.setTrackId(trackData.getTrackId());
				trackMarker.setTrackMarkerId(this.getTrackMarkerDao().getNextId("trackMarkerId"));
				this.getTrackMarkerDao().save(trackMarker);
			}

			trans.commit();
		} catch (Exception e) {
			this.rollbackTransaction(trans);
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
		List<AllelicValueElement> returnVal = new ArrayList<AllelicValueElement>();
		returnVal.addAll(this.getAlleleValuesDao().getAlleleValuesByMarkerId(markerIds));
		returnVal.addAll(this.getCharValuesDao().getAlleleValuesByMarkerId(markerIds));
		return returnVal;
	}

	@Override
	public List<DartDataRow> getDartDataRows(Integer datasetId) throws MiddlewareQueryException {
		List<DartDataRow> toReturn = new ArrayList<DartDataRow>();

		// Get MarkerMetadataSets of the given datasetId
		List<MarkerMetadataSet> markerMetadataSets = this.getMarkerMetadataSetDao().getMarkerMetadataSetsByDatasetId(datasetId);

		List<AccMetadataSet> accMetadataSets = this.getAccMetadataSetDao().getAccMetadataSetsByDatasetId(datasetId);

		List<AlleleValues> alleleValues = this.getAlleleValuesDao().getAlleleValuesByDatasetId(datasetId);

		List<DartValues> dartValues = this.getDartValuesDao().getDartValuesByDatasetId(datasetId);

		for (MarkerMetadataSet markerMetadataSet : markerMetadataSets) {

			Integer markerId = markerMetadataSet.getMarkerId();
			for (AccMetadataSet accMetadataSet : accMetadataSets) {
				Integer gid = accMetadataSet.getGermplasmId();
				for (AlleleValues alleleValue : alleleValues) {
					if (alleleValue.getDatasetId().equals(datasetId) && alleleValue.getGid().equals(gid)
							&& alleleValue.getMarkerId().equals(markerId)) {

						for (DartValues dartValue : dartValues) {
							if (dartValue.getDatasetId().equals(datasetId) && dartValue.getMarkerId().equals(markerId)) {

								toReturn.add(new DartDataRow(accMetadataSet, alleleValue, dartValue));
								break;
							}
						}
					}
				}
			}
		}

		return toReturn;
	}

	@Override
	public List<SNPDataRow> getSNPDataRows(Integer datasetId) throws MiddlewareQueryException {
		List<SNPDataRow> toReturn = new ArrayList<SNPDataRow>();

		// Get MarkerMetadataSets of the given datasetId
		List<MarkerMetadataSet> markerMetadataSets = this.getMarkerMetadataSetDao().getMarkerMetadataSetsByDatasetId(datasetId);

		List<AccMetadataSet> accMetadataSets = this.getAccMetadataSetDao().getAccMetadataSetsByDatasetId(datasetId);

		List<CharValues> charValues = this.getCharValuesDao().getCharValuesByDatasetId(datasetId);

		for (MarkerMetadataSet markerMetadataSet : markerMetadataSets) {
			Integer markerId = markerMetadataSet.getMarkerId();
			for (AccMetadataSet accMetadataSet : accMetadataSets) {
				Integer gid = accMetadataSet.getGermplasmId();
				for (CharValues charValue : charValues) {
					if (charValue != null && charValue.getDatasetId().equals(datasetId) && charValue.getGid().equals(gid)
							&& charValue.getMarkerId().equals(markerId)) {
						toReturn.add(new SNPDataRow(accMetadataSet, charValue));
						break;
					}
				}
			}
		}

		return toReturn;
	}

	@Override
	public List<SSRDataRow> getSSRDataRows(Integer datasetId) throws MiddlewareQueryException {
		List<SSRDataRow> toReturn = new ArrayList<SSRDataRow>();

		// Get MarkerMetadataSets of the given datasetId
		List<MarkerMetadataSet> markerMetadataSets = this.getMarkerMetadataSetDao().getMarkerMetadataSetsByDatasetId(datasetId);

		List<AccMetadataSet> accMetadataSets = this.getAccMetadataSetDao().getAccMetadataSetsByDatasetId(datasetId);

		List<AlleleValues> alleleValues = this.getAlleleValuesDao().getAlleleValuesByDatasetId(datasetId);

		for (MarkerMetadataSet markerMetadataSet : markerMetadataSets) {

			Integer markerId = markerMetadataSet.getMarkerId();
			for (AccMetadataSet accMetadataSet : accMetadataSets) {
				Integer gid = accMetadataSet.getGermplasmId();
				for (AlleleValues alleleValue : alleleValues) {
					if (alleleValue != null && alleleValue.getDatasetId().equals(datasetId) && alleleValue.getGid().equals(gid)
							&& alleleValue.getMarkerId().equals(markerId)) {
						toReturn.add(new SSRDataRow(accMetadataSet, alleleValue));
						break;
					}
				}
			}
		}

		return toReturn;
	}

	@Override
	public List<MappingABHRow> getMappingABHRows(Integer datasetId) throws MiddlewareQueryException {
		List<MappingABHRow> toReturn = new ArrayList<MappingABHRow>();

		List<MarkerMetadataSet> markerMetadataSets = this.getMarkerMetadataSetDao().getMarkerMetadataSetsByDatasetId(datasetId);

		List<AccMetadataSet> accMetadataSets = this.getAccMetadataSetDao().getAccMetadataSetsByDatasetId(datasetId);

		List<MappingPopValues> mappingPopValues = this.getMappingPopValuesDao().getMappingPopValuesByDatasetId(datasetId);

		for (MarkerMetadataSet markerMetadataSet : markerMetadataSets) {

			Integer markerId = markerMetadataSet.getMarkerId();
			for (AccMetadataSet accMetadataSet : accMetadataSets) {
				Integer gid = accMetadataSet.getGermplasmId();
				for (MappingPopValues mappingPopValue : mappingPopValues) {
					if (mappingPopValue != null && mappingPopValue.getDatasetId().equals(datasetId) && mappingPopValue.getGid().equals(gid)
							&& mappingPopValue.getMarkerId().equals(markerId)) {
						toReturn.add(new MappingABHRow(accMetadataSet, mappingPopValue));
						break;
					}
				}
			}
		}

		return toReturn;
	}

	@Override
	public List<MappingAllelicSNPRow> getMappingAllelicSNPRows(Integer datasetId) throws MiddlewareQueryException {
		List<MappingAllelicSNPRow> toReturn = new ArrayList<MappingAllelicSNPRow>();

		List<MarkerMetadataSet> markerMetadataSets = this.getMarkerMetadataSetDao().getMarkerMetadataSetsByDatasetId(datasetId);

		List<AccMetadataSet> accMetadataSets = this.getAccMetadataSetDao().getAccMetadataSetsByDatasetId(datasetId);

		List<MappingPopValues> mappingPopValues = this.getMappingPopValuesDao().getMappingPopValuesByDatasetId(datasetId);

		List<CharValues> charValues = this.getCharValuesDao().getCharValuesByDatasetId(datasetId);

		for (MarkerMetadataSet markerMetadataSet : markerMetadataSets) {
			Integer markerId = markerMetadataSet.getMarkerId();
			Marker marker = this.getMarkerDao().getById(markerId);
			for (AccMetadataSet accMetadataSet : accMetadataSets) {
				Integer gid = accMetadataSet.getGermplasmId();
				MappingPopValues mappingPopValue = null;
				for (MappingPopValues value : mappingPopValues) {
					if (value.getDatasetId().equals(datasetId) && value.getGid().equals(gid) && value.getMarkerId().equals(markerId)) {

						mappingPopValue = value;
						break;
					}
				}

				CharValues charValue = null;
				for (CharValues value : charValues) {
					if (value.getDatasetId().equals(datasetId) && value.getGid().equals(gid)) {
						charValue = value;
						break;
					}
				}

				if (mappingPopValue != null) {
					toReturn.add(new MappingAllelicSNPRow(marker, accMetadataSet, markerMetadataSet, mappingPopValue, charValue));
				}

			}
		}

		return toReturn;
	}

	@Override
	public List<MappingAllelicSSRDArTRow> getMappingAllelicSSRDArTRows(Integer datasetId) throws MiddlewareQueryException {
		List<MappingAllelicSSRDArTRow> toReturn = new ArrayList<MappingAllelicSSRDArTRow>();

		List<MarkerMetadataSet> markerMetadataSets = this.getMarkerMetadataSetDao().getMarkerMetadataSetsByDatasetId(datasetId);

		List<AccMetadataSet> accMetadataSets = this.getAccMetadataSetDao().getAccMetadataSetsByDatasetId(datasetId);

		List<AlleleValues> alleleValues = this.getAlleleValuesDao().getAlleleValuesByDatasetId(datasetId);

		List<DartValues> dartValues = this.getDartValuesDao().getDartValuesByDatasetId(datasetId);

		List<MappingPopValues> mappingPopValues = this.getMappingPopValuesDao().getMappingPopValuesByDatasetId(datasetId);

		for (MarkerMetadataSet markerMetadataSet : markerMetadataSets) {

			Integer markerId = markerMetadataSet.getMarkerId();
			for (AccMetadataSet accMetadataSet : accMetadataSets) {
				Integer gid = accMetadataSet.getGermplasmId();
				MappingPopValues mappingPopValue = null;
				for (MappingPopValues value : mappingPopValues) {
					if (value.getDatasetId().equals(datasetId) && value.getGid().equals(gid) && value.getMarkerId().equals(markerId)) {
						mappingPopValue = value;
						break;
					}
				}

				DartValues dartValue = null;
				for (DartValues value : dartValues) {
					if (value.getDatasetId().equals(datasetId) && value.getMarkerId().equals(markerId)) {
						dartValue = value;
						break;
					}
				}

				AlleleValues alleleValue = null;
				for (AlleleValues value : alleleValues) {
					if (value.getDatasetId().equals(datasetId) && value.getMarkerId().equals(markerId) && value.getGid().equals(gid)) {
						alleleValue = value;
						break;
					}
				}

				if (mappingPopValue != null) {
					toReturn.add(new MappingAllelicSSRDArTRow(accMetadataSet, mappingPopValue, alleleValue, dartValue));
				}

			}
		}

		return toReturn;
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

		Session session = this.getActiveSession();
		Transaction trans = null;

		try {
			trans = session.beginTransaction();

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

			trans.commit();
			return true;

		} catch (Exception e) {
			this.rollbackTransaction(trans);
			this.logAndThrowException("Error encountered while updating MarkerInfo: updateMarkerInfo(marker=" + marker + ", markerAlias="
					+ markerAlias + ", markerDetails=" + markerDetails + ", markerUserInfo=" + markerUserInfo + "): " + e.getMessage(), e,
					GenotypicDataManagerImpl.LOG);
		} finally {
			session.flush();
		}
		return false;
	}

	@Override
	public List<DartValues> getDartMarkerDetails(List<Integer> markerIds) throws MiddlewareQueryException {
		return this.getDartValuesDao().getDartValuesByMarkerIds(markerIds);
	}

	@Override
	public List<MarkerMetadataSet> getMarkerMetadataSetByDatasetId(Integer datasetId) throws MiddlewareQueryException {
		return this.getMarkerMetadataSetDao().getMarkerMetadataSetByDatasetId(datasetId);
	}

	@Override
	public List<CharValues> getCharValuesByMarkerIds(List<Integer> markerIds) throws MiddlewareQueryException {
		return this.getCharValuesDao().getCharValuesByMarkerIds(markerIds);
	}

}
