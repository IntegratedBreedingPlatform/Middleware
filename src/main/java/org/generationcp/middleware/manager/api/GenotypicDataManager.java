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

package org.generationcp.middleware.manager.api;

import java.util.List;
import java.util.Set;

import org.generationcp.middleware.manager.GdmsType;
import org.generationcp.middleware.manager.SetOperation;
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

/**
 * This is the API for retrieving and storing genotypic data.
 */
public interface GenotypicDataManager {

	/**
	 * Gets the dataset ids by germplasm ids. Searches the acc_metadataset table by giving the list of germplasm ids.
	 *
	 * @param gIds the list germplasm ids
	 * @return the dataset ids matching the given germplasm ids
	 * @ the middleware query exception
	 */
	List<Integer> getDatasetIdsByGermplasmIds(List<Integer> gIds);

	/**
	 * Gets the Name records matching the given name ids. This method is based on GMS_getNameRecord.
	 *
	 * @param nIds the list of Name ids to match
	 * @return the name records corresponding to the list of name ids
	 * @ the middleware query exception
	 */
	List<Name> getNamesByNameIds(List<Integer> nIds);

	/**
	 * Get all the Germplasm names associated with a marker
	 *
	 * @param markerId
	 * @return
	 */
	List<Name> getGermplasmNamesByMarkerId(Integer markerId);

	/**
	 * Gets the Name record by the given name id.
	 *
	 * @param nId the name id to match
	 * @return the Name record corresponding to the name id
	 * @ the middleware query exception
	 */
	Name getNameByNameId(Integer nId);

	/**
	 * Counts all Map records.
	 *
	 * @return the number of Map records found in the given instance
	 * @ the middleware query exception
	 */
	long countAllMaps();

	/**
	 * Gets all the Map records in the given range from the given database instance.
	 *
	 * @param start     - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return List of all the maps on the given range from the given database instance
	 * @ the middleware query exception
	 */
	List<Map> getAllMaps(int start, int numOfRows);

	/**
	 * Gets map information (marker_name, linkage_group, start_position) from mapping_data view by the given map name.
	 *
	 * @param mapName - the name of the map to retrieve
	 * @return the map info corresponding to the given map name
	 * @ the middleware query exception
	 */
	List<MapInfo> getMapInfoByMapName(String mapName);

	/**
	 * Gets map information given a map id and a chromosome. If the mapId is (+), the map received from central, otherwise it is retrieved
	 * from local.
	 *
	 * @param mapId      the map id
	 * @param chromosome the chromosome
	 * @return the map info by map and chromosome
	 * @ the middleware query exception
	 */
	List<MapInfo> getMapInfoByMapAndChromosome(int mapId, String chromosome);

	/**
	 * Gets map information given a map id and a chromosome. If the mapId is (+), the map received from central, otherwise it is retrieved
	 * from local.
	 *
	 * @param mapId         the map id
	 * @param chromosome    the chromosome
	 * @param startPosition the start position
	 * @return the map info by map chromosome and position
	 * @ the middleware query exception
	 */
	List<MapInfo> getMapInfoByMapChromosomeAndPosition(int mapId, String chromosome, float startPosition);

	/**
	 * Gets map information given a list of markers and mapId.
	 *
	 * @param markers the markers
	 * @param mapId   the map id
	 * @return the map info by markers and map
	 * @ the middleware query exception
	 */
	List<MapInfo> getMapInfoByMarkersAndMap(List<Integer> markers, Integer mapId);

	// GCP-8572

	/**
	 * Gets the marker on maps.
	 *
	 * @param mapIds        the map ids
	 * @param linkageGroup  the linkage group
	 * @param startPosition the start position
	 * @param endPosition   the end position
	 * @return the marker on maps
	 * @ the middleware query exception
	 */
	List<MarkerOnMap> getMarkerOnMaps(List<Integer> mapIds, String linkageGroup, double startPosition, double endPosition)
	;

	// GCP-8571

	/**
	 * Gets the markers on map by marker ids.
	 *
	 * @param markerIds the marker ids
	 * @return the markers on map by marker ids
	 * @ the middleware query exception
	 */
	List<MarkerOnMap> getMarkersOnMapByMarkerIds(List<Integer> markerIds);

	// GCP-8573

	/**
	 * Gets the all marker names from markers on map.
	 *
	 * @return the all marker names from markers on map
	 * @ the middleware query exception
	 */
	List<String> getAllMarkerNamesFromMarkersOnMap();

	String getMapNameById(Integer mapID);

	/**
	 * Fetches all datasets in the DB
	 *
	 * @return List of Dataset POJOs
	 */
	List<Dataset> getAllDatasets();

	/**
	 * Counts all the dataset names.
	 *
	 * @return the number of dataset names found in the given instance
	 * @ the middleware query exception
	 */
	long countDatasetNames();

	/**
	 * Gets the dataset names from the dataset table. Data is filtered by ignoring dataset type = 'qtl'.
	 *
	 * @param start     - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return List of the dataset names based on the given range from the given database instance
	 * @ the middleware query exception
	 */
	List<String> getDatasetNames(int start, int numOfRows);

	/**
	 * Gets the dataset names from the dataset table based on the given qtl id. Retrieves from both local and central database instances.
	 *
	 * @param qtlId     - the QTL ID to match
	 * @param start     - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return List of the dataset names based on the given qtl Id from both local and central database instances
	 * @ the middleware query exception
	 */
	List<String> getDatasetNamesByQtlId(Integer qtlId, int start, int numOfRows);

	/**
	 * Counts the dataset names from the dataset table based on the given qtl id. Counts from both local and central database instances.
	 *
	 * @param qtlId - the QTL ID to match
	 * @return the number of dataset names based on the given qtl Id from both local and central database instances
	 * @ the middleware query exception
	 */
	long countDatasetNamesByQtlId(Integer qtlId);

	/**
	 * Gets the dataset details (dataset id, dataset type) from the dataset table by dataset name.
	 *
	 * @param datasetName - the dataset name to match
	 * @param instance    - specifies whether the data should be retrieved from either the Central or the Local IBDB instance
	 * @return the dataset details by dataset name
	 * @ the middleware query exception
	 */
	List<DatasetElement> getDatasetDetailsByDatasetName(String datasetName);

	/**
	 * Retrieves a list of matching marker ids from the marker table based on the specified list of Marker Names.
	 *
	 * @param markerNames - List of marker names to search for the corresponding marker ids
	 * @param start       - the starting index of the sublist of results to be returned
	 * @param numOfRows   - the number of rows to be included in the sublist of results to be returned
	 * @return List of matching markers based on the specified marker names
	 * @ the middleware query exception
	 */
	List<Marker> getMarkersByMarkerNames(List<String> markerNames, int start, int numOfRows)
	;

	/**
	 * Gets the list of marker ids from the marker_metadataset table based on the given dataset id.
	 *
	 * @param datasetId the dataset id to match
	 * @return the markerIds by datasetId
	 * @ the middleware query exception
	 */
	List<Integer> getMarkerIdsByDatasetId(Integer datasetId);

	/**
	 * Gets the germplasm id of parents and the mapping type from the mapping_pop table based on the given dataset id.
	 *
	 * @param datasetId the dataset id to match
	 * @return the parents and mapping type corresponding to the dataset id
	 * @ the middleware query exception
	 */
	List<ParentElement> getParentsByDatasetId(Integer datasetId);

	/**
	 * Gets the marker type from the marker table based on the given marker ids.
	 *
	 * @param markerIds the marker ids to match
	 * @return the marker type corresponding to the given marker ids
	 * @ the middleware query exception
	 */
	List<String> getMarkerTypesByMarkerIds(List<Integer> markerIds);

	/**
	 * Gets the marker names by germplasm ids. This method searches the tables allele_values, char_values and mapping_pop_values for the
	 * existence of the given gids. Then gets the marker ids from allele_values, char_values, mapping_pop_values by the gids. And finally,
	 * gets the marker name from marker table by marker ids.
	 *
	 * @param gIds the germplasm ids to search for
	 * @return the marker names corresponding to the given germplasm ids
	 * @ the middleware query exception
	 */
	List<MarkerNameElement> getMarkerNamesByGIds(List<Integer> gIds);

	/**
	 * Gets the germplasm names by marker names. This method searches the allele_values, char_values, mapping_pop_values tables for the
	 * existence of marker ids. Then gets gids from allele_values, char_values, mapping_pop_values by marker ids. And finally, returns the
	 * germplasm names matching the marker names.
	 *
	 * @param markerNames - the marker names to match
	 * @return the GermplasmMarkerElement list that contains the germplasm name and the corresponding marker names
	 * @ the middleware query exception
	 */
	List<GermplasmMarkerElement> getGermplasmNamesByMarkerNames(List<String> markerNames)
	;

	/**
	 * Retrieves a list of mapping values based on the specified germplasm ids and marker names. This method gets mapping values
	 * (dataset_id, mapping_type, parent_a_gid, parent_b_gid, marker_type) from mapping_pop and marker tables by the given gids and marker
	 * ids.
	 *
	 * @param gids        - list of germplasm ids to match
	 * @param markerNames - list of marker names
	 * @param start       - the starting index of the sublist of results to be returned
	 * @param numOfRows   - the number of rows to be included in the sublist of results to be returned
	 * @return List of mapping values based on the specified germplasm ids and marker names
	 * @ the middleware query exception
	 */
	List<MappingValueElement> getMappingValuesByGidsAndMarkerNames(List<Integer> gids, List<String> markerNames, int start, int numOfRows)
	;

	/**
	 * Retrieves a list of allelic values (germplasm id, map_char_value, marker name) based on the specified germplasm ids and marker names.
	 * Results are retrieved from 3 separate sources: allele_values, char_values, and mapping_pop_values.
	 *
	 * @param gids        list of germplasm ids
	 * @param markerNames list of marker names
	 * @return List of allelic values based on the specified germplasm ids and marker names
	 * @ the middleware query exception
	 */
	List<AllelicValueElement> getAllelicValuesByGidsAndMarkerNames(List<Integer> gids, List<String> markerNames)
	;

	/**
	 * Retrieves a list of allelic values (germplasm id, allele_bin_value, marker id) based on the specified dataset id from the
	 * allele_values table.
	 *
	 * @param datasetId - the dataset id matching the allelic values
	 * @param start     - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return List of allelic values based on the specified dataset id
	 * @ the middleware query exception
	 */
	List<AllelicValueWithMarkerIdElement> getAllelicValuesFromAlleleValuesByDatasetId(Integer datasetId, int start, int numOfRows)
	;

	/**
	 * Counts the allelic values based on the specified datasetId from the allele_values table.
	 *
	 * @param datasetId the dataset id matching the allelic values
	 * @return the number of allelic values from allele_values table based on the specified dataset id
	 * @ the middleware query exception
	 */
	long countAllelicValuesFromAlleleValuesByDatasetId(Integer datasetId);

	/**
	 * Retrieves a list of allelic values (germplasm id, map_char_value, marker id) based on the specified dataset id from the
	 * mapping_pop_values table.
	 *
	 * @param datasetId - the dataset id matching the allelic values
	 * @param start     - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return List of allelic values based on the specified dataset id
	 * @ the middleware query exception
	 */
	List<AllelicValueWithMarkerIdElement> getAllelicValuesFromMappingPopValuesByDatasetId(Integer datasetId, int start, int numOfRows)
	;

	/**
	 * Counts the allelic values based on the specified dataset id from the mapping_pop_values table.
	 *
	 * @param datasetId the dataset id matching the allelic values
	 * @return the number of allelic values from mapping_pop_values table based on the specified dataset id
	 * @ the middleware query exception
	 */
	long countAllelicValuesFromMappingPopValuesByDatasetId(Integer datasetId);

	/**
	 * Retrieves a list of matching marker names from the marker table based on the specified list of marker ids.
	 *
	 * @param markerIds List of marker ids to search for the corresponding marker names
	 * @return List of matching marker names and marker ids based on the specified marker ids
	 * @ the middleware query exception
	 */
	List<MarkerIdMarkerNameElement> getMarkerNamesByMarkerIds(List<Integer> markerIds);

	/**
	 * Gets all marker types.
	 *
	 * @param start     - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return List of all marker types
	 * @ the middleware query exception
	 */
	List<String> getAllMarkerTypes(int start, int numOfRows);

	/**
	 * Gets the number of marker types.
	 *
	 * @return the number of all marker types on the specified database instance
	 * @ the middleware query exception
	 */
	long countAllMarkerTypes();

	/**
	 * Retrieves the names of the the markers which have the specified marker type.
	 *
	 * @param markerType - the marker type to match
	 * @param start      - the starting index of the sublist of results to be returned
	 * @param numOfRows  - the number of rows to be included in the sublist of results to be returned
	 * @return List of marker names based on the specified marker type
	 * @ the middleware query exception
	 */
	List<String> getMarkerNamesByMarkerType(String markerType, int start, int numOfRows);

	/**
	 * Count the number of marker names matching the given marker type.
	 *
	 * @param markerType the marker type to match
	 * @return the number of marker names corresponding to the given marker type
	 * @ the middleware query exception
	 */
	long countMarkerNamesByMarkerType(String markerType);

	/**
	 * Retrieves all the associated germplasm ids matching the given marker id from the char_values table.
	 *
	 * @param markerId  - the marker id to match
	 * @param start     - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return List of germplasm ids from char_values based on the given marker id
	 * @ the middleware query exception
	 */
	List<Integer> getGIDsFromCharValuesByMarkerId(Integer markerId, int start, int numOfRows);

	/**
	 * Retrieves a list of marker info based on the specified marker name from the marker_retrieval_info table.
	 *
	 * @param markerName - the markerName to match
	 * @param start      - the starting index of the sublist of results to be returned
	 * @param numOfRows  - the number of rows to be included in the sublist of results to be returned
	 * @return List of MarkerInfo based on the specified marker name
	 * @ the middleware query exception
	 */
	List<MarkerInfo> getMarkerInfoByMarkerName(String markerName, int start, int numOfRows);

	/**
	 * Retrieves a list of marker info based on the specified marker type from the marker_retrieval_info view
	 *
	 * @param markerType - the marker type to match. If null, application will use SSR as the marker type
	 * @param start      - the starting index of the sublist of results to be returned
	 * @param numOfRows  - the number of rows to be included in the sublist of results to be returned
	 * @return
	 * @
	 */
	List<ExtendedMarkerInfo> getMarkerInfoDataByMarkerType(String markerType);

	List<ExtendedMarkerInfo> getMarkerInfoDataLikeMarkerName(String partialMarkerName);

	List<ExtendedMarkerInfo> getMarkerInfoByMarkerNames(List<String> markerNames);

	/**
	 * Gets the allelic values by gid.
	 *
	 * @param targetGID the target gid
	 * @return the allelic values by gid
	 * @ the middleware query exception
	 */
	public List<AllelicValueElement> getAllelicValuesByGid(Integer targetGID);

	/**
	 * Counts the marker info entries corresponding to the given marker name.
	 *
	 * @param markerName the marker name
	 * @return the number of marker info entries
	 * @ the middleware query exception
	 */
	long countMarkerInfoByMarkerName(String markerName);

	/**
	 * Retrieves a list of MarkerInfo based on the specified genotype from the marker_retrieval_info table.
	 *
	 * @param genotype  - the genotype to match
	 * @param start     - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return List of MarkerInfo based on the specified genotype
	 * @ the middleware query exception
	 */
	List<MarkerInfo> getMarkerInfoByGenotype(String genotype, int start, int numOfRows);

	/**
	 * Counts the marker info entries corresponding to the given genotype.
	 *
	 * @param genotype the genotype
	 * @return the number of marker info entries
	 * @ the middleware query exception
	 */
	long countMarkerInfoByGenotype(String genotype);

	/**
	 * Retrieves a list of marker info entries based on the specified db accession id from the marker_retrieval_info table.
	 *
	 * @param dbAccessionId - the db accession id to match
	 * @param start         - the starting index of the sublist of results to be returned
	 * @param numOfRows     - the number of rows to be included in the sublist of results to be returned
	 * @return List of MarkerInfo based on the specified db accession id
	 * @ the middleware query exception
	 */
	List<MarkerInfo> getMarkerInfoByDbAccessionId(String dbAccessionId, int start, int numOfRows);

	/**
	 * Counts the marker info entries corresponding to the given db accession id.
	 *
	 * @param dbAccessionId the db accession id
	 * @return the number of marker info entries
	 * @ the middleware query exception
	 */
	long countMarkerInfoByDbAccessionId(String dbAccessionId);

	/**
	 * Counts the number of germplasm ids matching the given marker id from the char_values table.
	 *
	 * @param markerId the marker id
	 * @return the count of germplasm ids corresponding to the given marker id from char_values
	 * @ the middleware query exception
	 */
	long countGIDsFromCharValuesByMarkerId(Integer markerId);

	/**
	 * Retrieves all the associated germplasm ids matching the given marker id from the allele_values table.
	 *
	 * @param markerId  the marker id
	 * @param start     - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return List of germplasm ids from allele values based on the given marker id
	 * @ the middleware query exception
	 */
	List<Integer> getGIDsFromAlleleValuesByMarkerId(Integer markerId, int start, int numOfRows);

	/**
	 * Retrieves all the associated germplasm ids matching the given marker id from the mapping_pop_values table.
	 *
	 * @param markerId  - the marker id to match
	 * @param start     - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return List of germplasm ids from mapping_pop_values table based on the given marker id
	 * @ the middleware query exception
	 */
	List<Integer> getGIDsFromMappingPopValuesByMarkerId(Integer markerId, int start, int numOfRows);

	/**
	 * Gets the gids by markers and allele values.
	 *
	 * @param markerIdList    the marker id list
	 * @param alleleValueList the allele value list
	 * @return the gids by markers and allele values
	 * @ the middleware query exception
	 */
	List<Integer> getGidsByMarkersAndAlleleValues(List<Integer> markerIdList, List<String> alleleValueList);

	/**
	 * Counts the number of germplasm ids matching the given marker id from the mapping_pop_values table.
	 *
	 * @param markerId the marker id to match
	 * @return the count of germplasm ids corresponding to the given marker id from mapping_pop_values
	 * @ the middleware query exception
	 */
	long countGIDsFromMappingPopValuesByMarkerId(Integer markerId);

	/**
	 * Gets the all db accession ids from marker.
	 *
	 * @param start     - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return List of all non-empty db_accession IDs from Marker
	 * @ the middleware query exception
	 */
	List<String> getAllDbAccessionIdsFromMarker(int start, int numOfRows);

	/**
	 * Count all db accession ids from marker.
	 *
	 * @return the number of non-empty db accession ids from marker
	 * @ the middleware query exception
	 */
	long countAllDbAccessionIdsFromMarker();

	/**
	 * Gets the nids from acc metadataset by dataset ids.
	 *
	 * @param datasetIds - the dataset ids to match
	 * @param start      - the starting index of the sublist of results to be returned
	 * @param numOfRows  - the number of rows to be included in the sublist of results to be returned
	 * @return List of all name ids from acc_metadataset table based on the given list of dataset ids
	 * @ the middleware query exception
	 */
	List<AccMetadataSet> getAccMetadatasetsByDatasetIds(List<Integer> datasetIds, int start, int numOfRows);

	/**
	 * Gets all the dataset Ids for Fingerprinting. Retrieves data from both central and local database instances.
	 *
	 * @param start     - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return List of all dataset Ids where type is not equal to 'mapping' or 'QTL'
	 * @ the middleware query exception
	 */
	List<Integer> getDatasetIdsForFingerPrinting(int start, int numOfRows);

	/**
	 * Count the dataset Ids for Fingerprinting. Counts occurrences on both central and local database instances.
	 *
	 * @return the number of dataset Ids where type is not equal to 'mapping' or 'QTL'
	 * @ the middleware query exception
	 */
	long countDatasetIdsForFingerPrinting();

	/**
	 * Gets all the dataset Ids for Mapping. Retrieves data from both central and local database instances.
	 *
	 * @param start     - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return List of all dataset Ids where type is equal to 'mapping' and not equal to 'QTL'
	 * @ the middleware query exception
	 */
	List<Integer> getDatasetIdsForMapping(int start, int numOfRows);

	/**
	 * Count the dataset Ids for Mapping. Counts occurrences on both central and local database instances.
	 *
	 * @return the number of dataset Ids where type is equal to 'mapping' and not equal to 'QTL'
	 * @ the middleware query exception
	 */
	long countDatasetIdsForMapping();

	/**
	 * Gets the details of gdms_acc_metadataset given a set of Germplasm IDs. Retrieves from either local (negative gid) or both local and
	 * central (positive gid). Discards duplicates.
	 *
	 * @param gids      - list of GIDs to match
	 * @param start     - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return List of the corresponding details of entries in gdms_acc_metadataset given a set of GIDs
	 * @ the middleware query exception
	 */
	List<AccMetadataSet> getGdmsAccMetadatasetByGid(List<Integer> gids, int start, int numOfRows);

	/**
	 * Count the entries in gdms_acc_metadataset given a set of Germplasm IDs. Counts from either local (negative gid) or both local and
	 * central (positive gid). Includes duplicates in the count.
	 *
	 * @param gids the gids
	 * @return the number of entries in gdms_acc_metadataset given a set of Germplasm IDs
	 * @ the middleware query exception
	 */
	long countGdmsAccMetadatasetByGid(List<Integer> gids);

	/**
	 * Gets the marker ids matching the given GID and Dataset Ids Retrieves data from both central and local database instances.
	 *
	 * @param gid        - the GID to match
	 * @param datasetIds - the datasetIds to match
	 * @param start      - the starting index of the sublist of results to be returned
	 * @param numOfRows  - the number of rows to be included in the sublist of results to be returned
	 * @return List of marker ids matching the given GID and dataset ids
	 * @ the middleware query exception
	 */
	List<Integer> getMarkersBySampleIdAndDatasetIds(Integer gid, List<Integer> datasetIds, int start, int numOfRows)
	;

	/**
	 * Gets the number of marker ids matching the given GID and Dataset Ids Counts occurrences on both central and local database instances.
	 *
	 * @param gid        - the GID to match
	 * @param datasetIds - the datasetIds to match
	 * @return the number of marker ids matching the given GID and dataset ids
	 * @ the middleware query exception
	 */
	long countMarkersBySampleIdAndDatasetIds(Integer gid, List<Integer> datasetIds);

	/**
	 * Gets the number of alleles given a set of GIDs.
	 *
	 * @param gids - the GIDs to match
	 * @return the number of alleles matching the given GIDs
	 * @ the middleware query exception
	 */
	long countAlleleValuesByGids(List<Integer> gids);

	/**
	 * Gets int alleleValues for polymorphic markers retrieval given a list of GIDs Retrieves data from both central and local database
	 * instances.
	 *
	 * @param gids      - the GIDs to match
	 * @param start     the start
	 * @param numOfRows the num of rows
	 * @return List of int alleleValues for polymorphic markers retrieval
	 * @ the middleware query exception
	 */
	List<AllelicValueElement> getIntAlleleValuesForPolymorphicMarkersRetrieval(List<Integer> gids, int start, int numOfRows)
	;

	/**
	 * Gets the number of int alleleValues for polymorphic markers retrieval given a list of GIDs.
	 *
	 * @param gids - the GIDs to match
	 * @return the number of int alleleValues for polymorphic markers retrieval
	 * @ the middleware query exception
	 */
	long countIntAlleleValuesForPolymorphicMarkersRetrieval(List<Integer> gids);

	/**
	 * Gets char alleleValues for polymorphic markers retrieval given a list of GIDs Retrieves data from both central and local database
	 * instances.
	 *
	 * @param gids      - the GIDs to match
	 * @param start     the start
	 * @param numOfRows the num of rows
	 * @return List of char alleleValues for polymorphic markers retrieval
	 * @ the middleware query exception
	 */
	List<AllelicValueElement> getCharAlleleValuesForPolymorphicMarkersRetrieval(List<Integer> gids, int start, int numOfRows)
	;

	/**
	 * Gets the number of char alleleValues for polymorphic markers retrieval given a list of GIDs.
	 *
	 * @param gids - the GIDs to match
	 * @return the number of char alleleValues for polymorphic markers retrieval
	 * @ the middleware query exception
	 */
	long countCharAlleleValuesForPolymorphicMarkersRetrieval(List<Integer> gids);

	/**
	 * Gets the list og nids by dataset ids and marker ids and not by gids.
	 *
	 * @param datasetIds - the dataset ids to match
	 * @param gIds       - the gids not to match
	 * @param markerIds  - the marker ids to match
	 * @param start      - the starting index of the sublist of results to be returned
	 * @param numOfRows  - the number of rows to be included in the sublist of results to be returned
	 * @return Set of name ids based on the given list of dataset ids, list of marker ids and a list of germplasm ids
	 * @ the middleware query exception
	 */
	Set<Integer> getNIdsByMarkerIdsAndDatasetIdsAndNotGIds(
		List<Integer> datasetIds, List<Integer> gIds, List<Integer> markerIds,
		int start, int numOfRows);

	/**
	 * Gets the list of nids by dataset ids and marker ids.
	 *
	 * @param datasetIds - the dataset ids to match
	 * @param markerIds  - the marker ids to match
	 * @param start      - the starting index of the sublist of results to be returned
	 * @param numOfRows  - the number of rows to be included in the sublist of results to be returned
	 * @return Set of name ids based on the given list of dataset ids, list of marker ids
	 * @ the middleware query exception
	 */
	List<Integer> getNIdsByMarkerIdsAndDatasetIds(List<Integer> datasetIds, List<Integer> markerIds, int start, int numOfRows)
	;

	/**
	 * Gets the count of nids by dataset ids and marker ids and not by gids.
	 *
	 * @param datasetIds - the dataset ids to match
	 * @param markerIds  - the marker ids to match
	 * @param gIds       - the gids not to match
	 * @return count of name ids based on the given list of dataset ids, list of marker ids and a list of germplasm ids
	 * @ the middleware query exception
	 */
	int countNIdsByMarkerIdsAndDatasetIdsAndNotGIds(List<Integer> datasetIds, List<Integer> markerIds, List<Integer> gIds)
	;

	/**
	 * Gets the count of nids by dataset ids and marker ids.
	 *
	 * @param datasetIds - the dataset ids to match
	 * @param markerIds  - the marker ids to match
	 * @return count of name ids based on the given list of dataset ids, list of marker ids
	 * @ the middleware query exception
	 */
	int countNIdsByMarkerIdsAndDatasetIds(List<Integer> datasetIds, List<Integer> markerIds);

	/**
	 * Gets mapping alleleValues for polymorphic markers retrieval given a list of GIDs Retrieves data from both central and local database
	 * instances.
	 *
	 * @param gids      - the GIDs to match
	 * @param start     - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return List of mapping alleleValues for polymorphic markers retrieval
	 * @ the middleware query exception
	 */
	List<AllelicValueElement> getMappingAlleleValuesForPolymorphicMarkersRetrieval(List<Integer> gids, int start, int numOfRows)
	;

	/**
	 * Gets the number of mapping alleleValues for polymorphic markers retrieval given a list of GIDs.
	 *
	 * @param gids - the GIDs to match
	 * @return the number of mapping alleleValues for polymorphic markers retrieval
	 * @ the middleware query exception
	 */
	long countMappingAlleleValuesForPolymorphicMarkersRetrieval(List<Integer> gids);

	/**
	 * Retrieves all QTL entries from the gdms_qtl table.
	 *
	 * @param start     - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return List of all QTL entries
	 * @ the middleware query exception
	 */
	List<Qtl> getAllQtl(int start, int numOfRows);

	/**
	 * Returns the number of QTL entries from the gdms_qtl table.
	 *
	 * @return Count of QTL entries
	 * @ the middleware query exception
	 */
	long countAllQtl();

	/**
	 * Retrieves QTL Ids from the gdms_qtl table matching the given name.
	 *
	 * @param name      - the QTL name to match
	 * @param start     - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return List of QTL Ids
	 * @ the middleware query exception
	 */
	List<Integer> getQtlIdByName(String name, int start, int numOfRows);

	/**
	 * Returns the number of QTL Ids from the gdms_qtl table matching the given name.
	 *
	 * @param name - the QTL name to match
	 * @return Count of QTL Ids
	 * @ the middleware query exception
	 */
	long countQtlIdByName(String name);

	/**
	 * Retrieves QTL entries from the gdms_qtl table matching the given name.
	 *
	 * @param name      - the name to match
	 * @param start     - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return List of QTL entries
	 * @ the middleware query exception
	 */
	List<QtlDetailElement> getQtlByName(String name, int start, int numOfRows);

	/**
	 * Returns the number of QTL entries from the gdms_qtl table matching the given name.
	 *
	 * @param name - name of QTL
	 * @return Count of QTL entries
	 * @ the middleware query exception
	 */
	long countQtlByName(String name);

	/**
	 * Retrieves the QTL names for the list of QTL IDs provided. Retrieves data from both central and local, depending on the ID given.
	 *
	 * @param qtlIds the qtl ids
	 * @return Map of qtlId and its corresponding qtlName
	 * @ the middleware query exception
	 */
	java.util.Map<Integer, String> getQtlNamesByQtlIds(List<Integer> qtlIds);

	/**
	 * Retrieves QTL IDs from the gdms_qtl table matching the given trait id.
	 *
	 * @param trait     - the trait id to match
	 * @param start     - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return List of QTL IDs
	 * @ the middleware query exception
	 */
	List<Integer> getQtlByTrait(Integer trait, int start, int numOfRows);

	/**
	 * Returns the number of QTL entries from the gdms_qtl table matching the given trait id.
	 *
	 * @param trait - trait id of QTL
	 * @return Count of QTL entries
	 * @ the middleware query exception
	 */
	long countQtlByTrait(Integer trait);

	/**
	 * Retrieves the QTL trait ids from gdms_qtl_details table matching the given the dataset ID. If the dataset ID is positive, the traits
	 * are retrieved from central, otherwise they are retrieved from local.
	 *
	 * @param datasetId - the datasetId to match
	 * @param start     - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return List of QTL trait ids
	 * @ the middleware query exception
	 */
	List<Integer> getQtlTraitsByDatasetId(Integer datasetId, int start, int numOfRows);

	/**
	 * Returns the number of QTL traits from the gdms_qtl_details table matching the given dataset ID.
	 *
	 * @param datasetId - the datasetId to match
	 * @return Count of QTL traits
	 * @ the middleware query exception
	 */
	long countQtlTraitsByDatasetId(Integer datasetId);

	/**
	 * Returns all the parents from mapping population.
	 *
	 * @param start     - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return List of parent_a_gid and parent_b_gid - List of Parent A GId and Parent B GId
	 * @ the middleware query exception
	 */
	List<ParentElement> getAllParentsFromMappingPopulation(int start, int numOfRows);

	/**
	 * Returns the number of parent GIds (a and b).
	 *
	 * @return BigInteger - number of parent GIds
	 * @ the middleware query exception
	 */
	Long countAllParentsFromMappingPopulation();

	/**
	 * Returns map details given the name/part of the name of a map.
	 *
	 * @param nameLike  - search query, name or part of name (non case-sensitive), add % for wildcard
	 * @param start     - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return the map details by name
	 * @ the middleware query exception - List of Maps
	 */
	List<MapDetailElement> getMapDetailsByName(String nameLike, int start, int numOfRows);

	/**
	 * Returns count of map details given the name/part of the name of a map.
	 *
	 * @param nameLike - search query, name or part of name (non case-sensitive), add % for wildcard
	 * @return the long
	 * @ the middleware query exception - count of map details
	 */
	Long countMapDetailsByName(String nameLike);

	/**
	 * Returns the list of map names in which the respective markers are present.
	 *
	 * @param markerIds the marker ids
	 * @return Map of marker id with the list of map names
	 * @ the middleware query exception
	 */
	java.util.Map<Integer, List<String>> getMapNamesByMarkerIds(List<Integer> markerIds);

	/**
	 * Gets all the Map details.
	 *
	 * @param start     - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return List of all the map details
	 * @ the middleware query exception
	 */
	List<MapDetailElement> getAllMapDetails(int start, int numOfRows);

	/**
	 * Retrieves the number of map details.
	 *
	 * @return Count of the map details
	 * @ the middleware query exception
	 */
	long countAllMapDetails();

	/**
	 * Returns the map ids matching the given QTL name.
	 *
	 * @param qtlName   - the name of the QTL to match
	 * @param start     - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return the map ids matching the given parameters
	 * @ the middleware query exception
	 */
	List<Integer> getMapIdsByQtlName(String qtlName, int start, int numOfRows);

	/**
	 * Returns count of map ids given the qtl name.
	 *
	 * @param qtlName - name of the qtl to match
	 * @return the long
	 * @ the middleware query exception - count of map ids
	 */
	long countMapIdsByQtlName(String qtlName);

	/**
	 * Returns the marker ids matching the given QTL name, chromosome, min start position and max start position values.
	 *
	 * @param qtlName    - the name of the QTL to match
	 * @param chromosome - the value to match the linkage group of markers_onmap
	 * @param min        - the minimum start position
	 * @param max        - the maximum start position
	 * @param start      - the starting index of the sublist of results to be returned
	 * @param numOfRows  - the number of rows to be included in the sublist of results to be returned
	 * @return the marker ids matching the given parameters
	 * @ the middleware query exception
	 */
	List<Integer> getMarkerIdsByQtl(String qtlName, String chromosome, float min, float max, int start, int numOfRows)
	;

	/**
	 * Returns the number of marker ids matching the given QTL name, chromosome, min start position and max start position values.
	 *
	 * @param qtlName    - the name of the QTL to match
	 * @param chromosome - the value to match the linkage group of markers_onmap
	 * @param min        - the minimum start position
	 * @param max        - the maximum start position
	 * @return Count of marker id entries
	 * @ the middleware query exception
	 */
	long countMarkerIdsByQtl(String qtlName, String chromosome, float min, float max);

	/**
	 * Returns the markers matching the given marker ids.
	 *
	 * @param markerIds - the Ids of the markers to retrieve
	 * @param start     - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return the markers matching the given ids
	 * @ the middleware query exception
	 */
	List<Marker> getMarkersByIds(List<Integer> markerIds, int start, int numOfRows);

	/**
	 * Adds a QtlDetails entry to the database.
	 *
	 * @param qtlDetails - the object to add
	 * @return the id of the item added
	 * @ the middleware query exception
	 */
	Integer addQtlDetails(QtlDetails qtlDetails);

	/**
	 * Adds the marker.
	 *
	 * @param marker the marker
	 * @return the integer
	 * @ the middleware query exception
	 */
	Integer addMarker(Marker marker);

	/**
	 * Adds a MarkerDetails entry to the database.
	 *
	 * @param markerDetails - the object to add
	 * @return the id of the item added
	 * @ the middleware query exception
	 */
	Integer addMarkerDetails(MarkerDetails markerDetails);

	/**
	 * Adds a MarkerUserInfo entry to the database.
	 *
	 * @param markerUserInfo - the object to add
	 * @return the id of the item added
	 * @ the middleware query exception
	 */
	Integer addMarkerUserInfo(MarkerUserInfo markerUserInfo);

	/**
	 * Adds a AccMetadataSet entry to the database.
	 *
	 * @param accMetadataSet - the object to add
	 * @return the id of the item added
	 * @ the middleware query exception
	 */
	Integer addAccMetadataSet(AccMetadataSet accMetadataSet);

	/**
	 * Adds a MarkerMetadataSet entry to the database.
	 *
	 * @param markerMetadataSet - the object to add
	 * @return the id of the item added
	 * @ the middleware query exception
	 */
	Integer addMarkerMetadataSet(MarkerMetadataSet markerMetadataSet);

	/**
	 * Adds a Dataset entry to the database.
	 *
	 * @param dataset - the object to add
	 * @return the id of the item added
	 * @ the middleware query exception
	 */
	Integer addDataset(Dataset dataset);

	/**
	 * Adds a GDMS marker given a Marker.
	 *
	 * @param marker the marker
	 * @return markerId - markerId of the inserted record
	 * @ the middleware query exception
	 */
	Integer addGDMSMarker(Marker marker);

	/**
	 * Adds a GDMS marker alias given a MarkerAlias.
	 *
	 * @param markerAlias the marker alias
	 * @return markerId - markerId of the inserted record
	 * @ the middleware query exception
	 */
	Integer addGDMSMarkerAlias(MarkerAlias markerAlias);

	/**
	 * Adds a dataset user given a DatasetUser.
	 *
	 * @param datasetUser the dataset user
	 * @return userId - userId of the inserted record
	 * @ the middleware query exception
	 */
	Integer addDatasetUser(DatasetUsers datasetUser);

	/**
	 * Adds an AlleleValues entry to the database.
	 *
	 * @param alleleValues - the object to add
	 * @return the id of the item added
	 * @ the middleware query exception
	 */
	Integer addAlleleValues(AlleleValues alleleValues);

	/**
	 * Adds an CharValues entry to the database.
	 *
	 * @param charValues - the object to add
	 * @return the id of the item added
	 * @ the middleware query exception
	 */
	Integer addCharValues(CharValues charValues);

	/**
	 * Adds a mapping pop given a MappingPop.
	 *
	 * @param mappingPop the mapping pop
	 * @return DatasetId - datasetId of the inserted record
	 * @ the middleware query exception
	 */
	Integer addMappingPop(MappingPop mappingPop);

	/**
	 * Adds a mapping pop given a MappingPopValue.
	 *
	 * @param mappingPopValue the mapping pop value
	 * @return mpId - mpId of the inserted record
	 * @ the middleware query exception
	 */
	Integer addMappingPopValue(MappingPopValues mappingPopValue);

	/**
	 * Adds a marker on map given a MarkerOnMap.
	 *
	 * @param markerOnMap the marker on map
	 * @return mapId - mapId of the inserted record
	 * @ the middleware query exception
	 */
	Integer addMarkerOnMap(MarkerOnMap markerOnMap);

	/**
	 * Adds a dart value given a DartValue.
	 *
	 * @param dartValue the dart value
	 * @return adId - adId of the inserted record
	 * @ the middleware query exception
	 */
	Integer addDartValue(DartValues dartValue);

	/**
	 * Adds a Qtl given a Qtl.
	 *
	 * @param qtl the qtl
	 * @return qtlId - qltId of the inserted record
	 * @ the middleware query exception
	 */
	Integer addQtl(Qtl qtl);

	/**
	 * Adds a Map entry to the database.
	 *
	 * @param map - the object to add
	 * @return the id of the item added
	 * @ the middleware query exception
	 */
	Integer addMap(Map map);

	/**
	 * Sets SNP Markers.
	 *
	 * @param marker         (Marker) marker_type will be set to/overridden by "SNP"
	 * @param markerAlias    (MarkerAlias) (marker_id will automatically be set to inserted marker's ID)
	 * @param markerDetails  (MarkerDetails) (marker_id will automatically be set to inserted marker's ID)
	 * @param markerUserInfo (MarkerUserInfo) (marker_id will automatically be set to inserted marker's ID)
	 * @return (boolean) - true if successful, exception or false if failed
	 * @ the middleware query exception
	 */
	Boolean setSNPMarkers(Marker marker, MarkerAlias markerAlias, MarkerDetails markerDetails, MarkerUserInfo markerUserInfo)
	;

	/**
	 * Sets CAP Markers.
	 *
	 * @param marker         (Marker) marker_type will be set to/overridden by "CAP"
	 * @param markerAlias    (MarkerAlias) (marker_id will automatically be set to inserted marker's ID)
	 * @param markerDetails  (MarkerDetails) (marker_id will automatically be set to inserted marker's ID)
	 * @param markerUserInfo (MarkerUserInfo) (marker_id will automatically be set to inserted marker's ID)
	 * @return (boolean) - true if successful, exception or false if failed
	 * @ the middleware query exception
	 */
	Boolean setCAPMarkers(Marker marker, MarkerAlias markerAlias, MarkerDetails markerDetails, MarkerUserInfo markerUserInfo)
	;

	/**
	 * Sets CISR Markers.
	 *
	 * @param marker         (Marker) marker_type will be set to/overridden by "CISR"
	 * @param markerAlias    (MarkerAlias) (marker_id will automatically be set to inserted marker's ID)
	 * @param markerDetails  (MarkerDetails) (marker_id will automatically be set to inserted marker's ID)
	 * @param markerUserInfo (MarkerUserInfo) (marker_id will automatically be set to inserted marker's ID)
	 * @return (boolean) - true if successful, exception or false if failed
	 * @ the middleware query exception
	 */
	Boolean setCISRMarkers(Marker marker, MarkerAlias markerAlias, MarkerDetails markerDetails, MarkerUserInfo markerUserInfo)
	;

	/**
	 * Sets Maps.
	 *
	 * @param marker      - GDMS Marker
	 * @param markerOnMap - GDMS Marker On Map
	 * @param map         - GDMS Map
	 * @return true if values were successfully saved in the database, false otherwise
	 * @ the middleware query exception
	 */
	Boolean setMaps(Marker marker, MarkerOnMap markerOnMap, Map map);

	/**
	 * Gets Map ID from QTL Name.
	 *
	 * @param qtlName   - name of qtl
	 * @param start     - starting record to retrieve
	 * @param numOfRows - number of records to retrieve from start record
	 * @return (List < Integer >) list of Map IDs
	 * @ the middleware query exception
	 */
	List<Integer> getMapIDsByQTLName(String qtlName, int start, int numOfRows);

	/**
	 * Counts Map ID from QTL Name.
	 *
	 * @param qtlName - name of qtl
	 * @return (long)count of Map IDs
	 * @ the middleware query exception
	 */
	long countMapIDsByQTLName(String qtlName);

	/**
	 * Gets Marker IDs from Map ID, Linkage Group and Between start position values.
	 *
	 * @param mapID        - ID of map
	 * @param linkageGroup - chromosome
	 * @param startPos     - map starting position value
	 * @param endPos       - map ending position value
	 * @param start        - starting record to retrieve
	 * @param numOfRows    - number of records to retrieve from start record
	 * @return (Set < Integer >) set of Marker IDs
	 * @ the middleware query exception
	 */
	Set<Integer> getMarkerIDsByMapIDAndLinkageBetweenStartPosition(
		int mapID, String linkageGroup, double startPos, double endPos,
		int start, int numOfRows);

	/**
	 * Gets the markers by position and linkage group.
	 *
	 * @param startPos     the start pos
	 * @param endPos       the end pos
	 * @param linkageGroup the linkage group
	 * @return the markers by position and linkage group
	 * @ the middleware query exception
	 */
	List<Marker> getMarkersByPositionAndLinkageGroup(double startPos, double endPos, String linkageGroup);

	/**
	 * Counts Marker IDs from Map ID, Linkage Group and Between start position values.
	 *
	 * @param mapId        - ID of map
	 * @param linkageGroup - chromosome
	 * @param startPos     - map starting position value
	 * @param endPos       - map ending position value
	 * @return (long) count of Marker IDs
	 * @ the middleware query exception
	 */
	long countMarkerIDsByMapIDAndLinkageBetweenStartPosition(int mapId, String linkageGroup, double startPos, double endPos)
	;

	/**
	 * Gets Markers by Marker IDs.
	 *
	 * @param markerIds - IDs of markers
	 * @param start     - starting record to retrieve
	 * @param numOfRows - number of records to retrieve from start record
	 * @return (List < Marker >) List of Markers
	 * @ the middleware query exception
	 */
	List<Marker> getMarkersByMarkerIds(List<Integer> markerIds, int start, int numOfRows);

	/**
	 * Counts Markers by Marker IDs.
	 *
	 * @param markerIDs - IDs of markers
	 * @return Count of Markers
	 * @ the middleware query exception
	 */
	long countMarkersByMarkerIds(List<Integer> markerIDs);

	/**
	 * Retrieves QTL entries from the gdms_qtl table matching the given list of qtl ids.
	 *
	 * @param qtls      - the list of qtl ids to match
	 * @param start     - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return List of QTL entries
	 * @ the middleware query exception
	 */
	List<QtlDetailElement> getQtlByQtlIds(List<Integer> qtls, int start, int numOfRows);

	/**
	 * Returns the number of QTL entries from the gdms_qtl table matching the given list of qtl ids.
	 *
	 * @param qtls - list of QTL IDs
	 * @return Count of QTL entries
	 * @ the middleware query exception
	 */
	long countQtlByQtlIds(List<Integer> qtls);

	/**
	 * Returns the QTL data entries from the gdms_qtl and gdms_qtl_details tables matching the given list of trait ids. Retrieves values
	 * from both local and central.
	 *
	 * @param qtlTraitIds - list of QTL trait ids to match
	 * @param start       - the starting index of the sublist of results to be returned
	 * @param numOfRows   - the number of rows to be included in the sublist of results to be returned
	 * @return List of QTL data entries
	 * @ the middleware query exception
	 */
	List<QtlDataElement> getQtlDataByQtlTraits(List<Integer> qtlTraitIds, int start, int numOfRows);

	/**
	 * Returns the number of QTL data entries from the gdms_qtl and gdms_qtl_details tables matching the given list of trait ids. Counts
	 * from both local and central instances.
	 *
	 * @param qtlTraitIds - list of QTL trait ids to match
	 * @return Count of QTL data entries
	 * @ the middleware query exception
	 */
	long countQtlDataByQtlTraits(List<Integer> qtlTraitIds);

	/**
	 * Gets the qtl details by qtl traits.
	 *
	 * @param qtlTraitIds the qtl trait ids
	 * @param start       the start
	 * @param numOfRows   the num of rows
	 * @return the qtl details by qtl traits
	 * @ the middleware query exception
	 */
	List<QtlDetailElement> getQtlDetailsByQtlTraits(List<Integer> qtlTraitIds, int start, int numOfRows);

	/**
	 * Returns the number of QTL detail entries from the gdms_qtl and gdms_qtl_details tables matching the given list of trait ids. Counts
	 * from both local and central instances.
	 *
	 * @param qtlTraitIds - list of QTL trait ids to match
	 * @return Count of QTL data entries
	 * @ the middleware query exception
	 */
	long countQtlDetailsByQtlTraits(List<Integer> qtlTraitIds);

	/**
	 * Returns the number of nIds from AccMetaDataSet given a list of dataset Ids.
	 *
	 * @param datasetIds the dataset ids
	 * @return Count of NIDs
	 * @ the middleware query exception
	 */
	long countAccMetadatasetByDatasetIds(List<Integer> datasetIds);

	/**
	 * Returns a list of of nIds from AccMetaDataSet given a list of dataset Ids.
	 *
	 * @param datasetIds the dataset ids
	 * @return List of NIDs
	 * @ the middleware query exception
	 */
	List<Integer> getAccMetadatasetByDatasetIds(List<Integer> datasetIds);

	/**
	 * Returns the number of markers from Marker given a list of dataset Ids.
	 *
	 * @param datasetIds the dataset ids
	 * @return Count of entries from MarkerMetaDataset
	 * @ the middleware query exception
	 */
	long countMarkersFromMarkerMetadatasetByDatasetIds(List<Integer> datasetIds);

	/**
	 * Returns the Map ID given the map name.
	 *
	 * @param mapName the map name
	 * @return the Map ID
	 * @ the middleware query exception
	 */
	Integer getMapIdByName(String mapName);

	/**
	 * Returns the number of mapping pop values from gdms_mapping_pop_values given a list of germplasm IDs.
	 *
	 * @param gIds the g ids
	 * @return count of mapping pop values
	 * @ the middleware query exception
	 */
	long countMappingPopValuesByGids(List<Integer> gIds);

	/**
	 * Returns the number of mapping allele values gdms_allele_values given a list of germplasm IDs.
	 *
	 * @param gIds the g ids
	 * @return the long
	 * @ the middleware query exception
	 */
	long countMappingAlleleValuesByGids(List<Integer> gIds);

	/**
	 * Returns the list of MarkerMetadataSet from gdms_marker_metadataset matching the given marker IDs.
	 *
	 * @param markerIds - list of marker ids
	 * @return List of MarkerMetadataSet objects
	 * @ the middleware query exception
	 */
	List<MarkerMetadataSet> getAllFromMarkerMetadatasetByMarkers(List<Integer> markerIds);

	/**
	 * Returns the Dataset details given a dataset ID.
	 *
	 * @param datasetId the dataset id
	 * @return Dataset entry matching the given dataset ID
	 * @ the middleware query exception
	 */
	Dataset getDatasetById(Integer datasetId);

	/**
	 * Gets the datasets by type.
	 *
	 * @param type the type
	 * @return the datasets by type
	 * @ the middleware query exception
	 */
	List<Dataset> getDatasetsByType(GdmsType type);

	/**
	 * Gets the mapping pop by dataset id.
	 *
	 * @param datasetId the dataset id
	 * @return the mapping pop by dataset id
	 * @ the middleware query exception
	 */
	MappingPop getMappingPopByDatasetId(Integer datasetId);

	/**
	 * Returns the Dataset details given a list of dataset IDs.
	 *
	 * @param datasetIds the dataset ids
	 * @return List Dataset entries matching the given dataset IDs
	 * @ the middleware query exception
	 */
	List<Dataset> getDatasetDetailsByDatasetIds(List<Integer> datasetIds);

	/**
	 * Returns the QTL IDs given a list of dataset IDs.
	 *
	 * @param datasetIds the dataset ids
	 * @return QTL IDs
	 * @ the middleware query exception
	 */
	List<Integer> getQTLIdsByDatasetIds(List<Integer> datasetIds);

	/**
	 * Returns the list of AccMetadataSet from gdms_acc_metadataset matching given list of GIDs applied for given Set Operation and matching
	 * dataset ID.
	 *
	 * @param gIds      - list of germplasm IDs
	 * @param datasetId the dataset id
	 * @param operation - operation to be applied for list of GIDs. Either IN or NOT IN.
	 * @return the all from acc metadataset
	 * @ the middleware query exception
	 */
	List<AccMetadataSet> getAllFromAccMetadataset(List<Integer> gIds, Integer datasetId, SetOperation operation)
	;

	/**
	 * Returns the map name and marker count given a list of marker ids.
	 *
	 * @param markerIds the marker ids
	 * @return map name and marker count as a list of MapDetailElements
	 * @ the middleware query exception
	 */
	List<MapDetailElement> getMapAndMarkerCountByMarkers(List<Integer> markerIds);

	/**
	 * Returns all MTA data from both central and local databases.
	 *
	 * @return the all mt as
	 * @ the middleware query exception
	 */
	List<Mta> getAllMTAs();

	/**
	 * Counts all MTA data from both central and local databases.
	 *
	 * @return the long
	 * @ the middleware query exception
	 */
	long countAllMTAs();

	/**
	 * Returns all MTAs matching the given Trait ID.
	 *
	 * @param traitId the trait id
	 * @return the mT as by trait
	 * @ the middleware query exception
	 */
	List<Mta> getMTAsByTrait(Integer traitId);

	/**
	 * Delete QTLs given a dataset id and a qtl id.
	 *
	 * @param qtlIds    the qtl ids
	 * @param datasetId the dataset id
	 * @ the middleware query exception
	 */
	void deleteQTLs(List<Integer> qtlIds, Integer datasetId);

	/**
	 * Delete SNPGenotypingDatasets by dataset id.
	 *
	 * @param datasetId the dataset id
	 * @ the middleware query exception
	 */
	void deleteSNPGenotypingDatasets(Integer datasetId);

	/**
	 * Delete MappingPopulationDatasets by dataset id.
	 *
	 * @param datasetId the dataset id
	 * @ the middleware query exception
	 */
	void deleteMappingPopulationDatasets(Integer datasetId);

	/**
	 * Retrieves the QTL details (including QTL ID and Trait ID/TID) given a Map ID.
	 *
	 * @param mapId the map id
	 * @return list of QTL Details
	 * @ the middleware query exception
	 */
	List<QtlDetails> getQtlDetailsByMapId(Integer mapId);

	/**
	 * Counts the QTL details (including QTL ID and Trait ID/TID) given a Map ID.
	 *
	 * @param mapId the map id
	 * @return list of QTL Details
	 * @ the middleware query exception
	 */
	long countQtlDetailsByMapId(Integer mapId);

	/**
	 * Delete Map by map id.
	 *
	 * @param mapId the map id
	 * @ the middleware query exception
	 */
	void deleteMaps(Integer mapId);

	/**
	 * Retrieve the list of Marker Id and MarkerSampleId combinations from CharValues matching list of GIDs.
	 *
	 * @param gIds the g ids
	 * @return the marker from char values by gids
	 * @ the middleware query exception
	 */
	List<MarkerSampleId> getMarkerFromCharValuesByGids(List<Integer> gIds);

	/**
	 * Retrieve the list of Marker ID and MarkerSampleId combinations from AlleleValues matching list of GIDs.
	 *
	 * @param gIds the g ids
	 * @return the marker from allele values by gids
	 * @ the middleware query exception
	 */
	List<MarkerSampleId> getMarkerFromAlleleValuesByGids(List<Integer> gIds);

	/**
	 * Retrieve the list of Marker ID and MarkerSampleId combinations from MappingPop matching list of GIDs.
	 *
	 * @param gIds the g ids
	 * @return the marker from mapping pop by gids
	 * @ the middleware query exception
	 */
	List<MarkerSampleId> getMarkerFromMappingPopByGids(List<Integer> gIds);

	/**
	 * Adds the mta.
	 *
	 * @param dataset     the dataset
	 * @param mta         the mta
	 * @param mtaMetadata the mta metadata
	 * @param users       the users
	 * @ the middleware query exception
	 */
	void addMTA(Dataset dataset, Mta mta, MtaMetadata mtaMetadata, DatasetUsers users);

	/**
	 * Uploads MTA data to the database. Adds MTA, MTA Metadata, Dataset, and DatasetUsers records to the database. Mta and MtaMetadata have
	 * 1:1 correspondence, hence the same size.
	 *
	 * @param dataset         the dataset
	 * @param mtaList         the mtas to add
	 * @param mtaMetadataList the mtaMetadataList to add
	 * @param users           the users
	 * @ the middleware query exception
	 */
	void setMTA(Dataset dataset, DatasetUsers users, List<Mta> mtaList, MtaMetadata mtaMetadata);

	// GCP-8565

	/**
	 * Delete mta.
	 *
	 * @param datasetIds the dataset ids
	 * @ the middleware query exception
	 */
	void deleteMTA(List<Integer> datasetIds);

	/**
	 * Adds MtaMetadata.
	 *
	 * @param mtaMetadata the mtaMetadata to add
	 * @ the middleware query exception
	 */
	void addMtaMetadata(MtaMetadata mtaMetadata);

	// GCP-7873

	/**
	 * Gets the all snp markers.
	 *
	 * @return the all snp markers
	 * @ the middleware query exception
	 */
	List<Marker> getAllSNPMarkers();

	/**
	 * Gets the markers by type.
	 *
	 * @param type the type
	 * @return the markers by type
	 * @ the middleware query exception
	 */
	List<Marker> getMarkersByType(String type);

	// GCP-7874

	/**
	 * Gets the sN ps by haplotype.
	 *
	 * @param haplotype the haplotype
	 * @return the sN ps by haplotype
	 * @ the middleware query exception
	 */
	List<Marker> getSNPsByHaplotype(String haplotype);

	// GCP-8566

	/**
	 * Adds the haplotype.
	 *
	 * @param trackData    the track data
	 * @param trackMarkers the track markers
	 * @ the middleware query exception
	 */
	void addHaplotype(TrackData trackData, List<TrackMarker> trackMarkers);

	// GCP-7881

	/**
	 * Gets the marker info by marker ids.
	 *
	 * @param markerIds the marker ids
	 * @return the marker info by marker ids
	 * @ the middleware query exception
	 */
	List<MarkerInfo> getMarkerInfoByMarkerIds(List<Integer> markerIds);

	// GCP-7875

	/**
	 * Gets the allele values by markers. Retrieves from gdms_allele_values and gdms_char_values. Corresponding fields in
	 * AllelicValueElement are populated based on the table where the data is retrieved from - datasetId, markerId, gid, alleleBinValue,
	 * peakHeight for data coming from gdms_allele_values and datasetId, markerId, gid, data (charValue) for data coming from
	 * gdms_char_values.
	 *
	 * @param markerIds the marker ids
	 * @return the allele values by markers
	 * @ the middleware query exception
	 */
	List<AllelicValueElement> getAlleleValuesByMarkers(List<Integer> markerIds);

	/**
	 * Update marker info. For new values to be added, set the id to null.
	 *
	 * @param marker         the marker
	 * @param markerAlias    the marker alias
	 * @param markerDetails  the marker details
	 * @param markerUserInfo the marker user info
	 * @return the boolean
	 * @ the middleware query exception
	 */
	Boolean updateMarkerInfo(Marker marker, MarkerAlias markerAlias, MarkerDetails markerDetails, MarkerUserInfo markerUserInfo)
	;

	/**
	 * Gets the marker metadataset by dataset id.
	 *
	 * @param datasetId the dataset id
	 * @return the marker metadataset by dataset id
	 * @ the middleware query exception
	 */
	List<MarkerMetadataSet> getMarkerMetadataSetByDatasetId(Integer datasetId);

	/**
	 * Gets the char values by marker ids.
	 *
	 * @param markerIds the marker ids
	 * @return the char values by marker ids
	 * @ the middleware query exception
	 */
	List<CharValues> getCharValuesByMarkerIds(List<Integer> markerIds);

	/**
	 * Returns all Mapping data
	 *
	 * @return List of MappingData instances
	 */
	List<MappingData> getAllMappingData();

	// Added by Matthew to move GDMS SQL to middleware : services not heavily understood

	List<Object> getUniqueAccMetaDataSetByGids(List gids);

	List<QtlDetails> getAllQtlDetails();

	// I have a feeling we may need to paginate this guy ..... see the other limited getAllQtl
	List<Qtl> getAllQtl();

	// AlleleValues (AlleleValueDao)
	List<Object> getUniqueAllelesByGidsAndMids(List<Integer> gids, List<Integer> mids);

	// Char Alleles (CharValueDAO)
	List<Object> getUniqueCharAllelesByGidsAndMids(List<Integer> gids, List<Integer> mids);

	// Map Pop Alleles (MappingPopValueDAO)
	List<Object> getUniqueMapPopAllelesByGidsAndMids(List<Integer> gids, List<Integer> mids);

	int countAllMarkers();

	List<Integer> getMarkerIdsByNames(List<String> names, int start, int numOfRows);

	List<Object> getMarkersOnMapByMarkerIdsAndMapId(List<Integer> markerIds, Integer mapID);

	List<MarkerOnMap> getMarkerOnMapByLinkageGroupAndMapIdAndNotInMarkerId(Integer mapId, Integer linkageGroupId, Integer markerId);

}
