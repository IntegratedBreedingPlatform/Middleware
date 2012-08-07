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

package org.generationcp.middleware.manager.api;

import java.util.List;

import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.gdms.AllelicValueElement;
import org.generationcp.middleware.pojos.gdms.AllelicValueWithMarkerIdElement;
import org.generationcp.middleware.pojos.gdms.DatasetElement;
import org.generationcp.middleware.pojos.gdms.GermplasmMarkerElement;
import org.generationcp.middleware.pojos.gdms.Map;
import org.generationcp.middleware.pojos.gdms.MapInfo;
import org.generationcp.middleware.pojos.gdms.MappingValueElement;
import org.generationcp.middleware.pojos.gdms.MarkerNameElement;
import org.generationcp.middleware.pojos.gdms.ParentElement;

// TODO: Auto-generated Javadoc
/**
 * This is the API for retrieving and storing genotypic data.
 * 
 * @author Joyce Avestro
 */
public interface GenotypicDataManager{

    /**
     * Gets the name ids by germplasm ids.
     *
     * @param gIds the germplasm ids
     * @return the name ids by germplasm ids
     * @throws QueryException the query exception
     */
    public List<Integer> getNameIdsByGermplasmIds(List<Integer> gIds) throws QueryException;

    /**
     * Gets the names by name ids.
     *
     * @param nIds the name ids
     * @return the names by name ids
     * @throws QueryException the query exception
     */
    public List<Name> getNamesByNameIds(List<Integer> nIds) throws QueryException;

    /**
     * Gets the name by name id.
     *
     * @param nId the name id
     * @return the name by name id
     * @throws QueryException the query exception
     */
    public Name getNameByNameId(Integer nId) throws QueryException;
    
    /**
     * Count all map records.
     *
     * @param instance the instance
     * @return the long
     * @throws QueryException the query exception
     */
    public Long countAllMaps(Database instance) throws QueryException; 
    
    /**
     * Gets the all maps.
     *
     * @param start the start
     * @param numOfRows the num of rows
     * @param instance the instance
     * @return the all maps
     * @throws QueryException the query exception
     */
    public List<Map> getAllMaps(Integer start, Integer numOfRows,  Database instance) throws QueryException;
    
    /**
     * Gets the map info by map name.
     *
     * @param mapName the map name
     * @param instance the instance
     * @return the map info by map name
     * @throws QueryException the query exception
     */
    public List<MapInfo> getMapInfoByMapName(String mapName, Database instance) throws QueryException;
    
    
    /**
     * Count the dataset names.
     *
     * @param instance the instance of the database - either Database.LOCAL or Database.CENTRAL 
     * @return the number of dataset names
     * @throws QueryException the query exception
     */
    public int countDatasetNames(Database instance) throws QueryException;

    
    /**
     * Gets the dataset names.
     *
     * @param start the start row
     * @param numOfRows the num of rows to retrieve
     * @param instance the instance of the database - either Database.LOCAL or Database.CENTRAL 
     * @return the dataset names
     * @throws QueryException the query exception
     */
    public List<String> getDatasetNames(Integer start, Integer numOfRows, Database instance) throws QueryException;
    
    
    /**
     * Gets the dataset details by dataset name.
     *
     * @param datasetName the dataset name
     * @param instance the instance
     * @return the dataset details by dataset name
     * @throws QueryException the query exception
     */
    public List<DatasetElement> getDatasetDetailsByDatasetName(String datasetName, Database instance) throws QueryException;
    
    /**
     * Retrieves a list of matching Marker IDs from the Marker table based on
     * the specified list of Marker Names.
     *
     * @param markerNames - List of Marker Names to search for the corresponding Marker IDs
     * @param start - the starting index of the sublist of results to be returned
     * @param numOfRows - the number of rows to be included in the sublist of results
     * to be returned
     * @param instance - specifies whether the data should be retrieved from either the Central
     * or the Local IBDB instance
     * @return List of matching Marker IDs based on the specified Marker Names
     * @throws QueryException the query exception
     */
    public List<Integer> getMarkerIdsByMarkerNames(List<String> markerNames, int start, int numOfRows, Database instance) throws QueryException;

    /**
     * Gets the markerId by datasetId.
     *
     * @param datasetId the dataset id
     * @return the markerIds by datasetId
     * @throws QueryException the query exception
     */
    public List<Integer> getMarkerIdsByDatasetId(Integer datasetId) throws QueryException;
    
    
    /**
     * Gets the parents by dataset id.
     *
     * @param datasetId the dataset id
     * @return the parents by dataset id
     * @throws QueryException the query exception
     */
    public List<ParentElement> getParentsByDatasetId(Integer datasetId) throws QueryException;
    
    
    /**
     * Gets the marker type by marker ids.
     *
     * @param markerIds the marker ids
     * @return the marker type by marker ids
     * @throws QueryException the query exception
     */
    public List<String> getMarkerTypesByMarkerIds(List<Integer> markerIds)  throws QueryException;
    
    /**
     * Gets the marker names by germplasm ids.
     *
     * @param gIds the germplasm ids
     * @return the marker names by germplasm ids
     * @throws QueryException the query exception
     */
    public List<MarkerNameElement> getMarkerNamesByGIds(List<Integer> gIds) throws QueryException;
    
    /**
     * Gets the germplasm names by marker names.
     *
     * @param markerNames the marker names
     * @param instance the instance of the database - either Database.LOCAL or Database.CENTRAL 
     * @return the GermplasmMarkerElement list that contains the germplasm name and the corresponding marker names
     * @throws QueryException the query exception
     */
    public List<GermplasmMarkerElement> getGermplasmNamesByMarkerNames(List<String> markerNames, Database instance) throws QueryException;

    /**
     * Retrieves a list of Mapping Values based on the specified GIDs and Marker Names.
     *
     * @param gids - list of Germplasm IDs
     * @param markerNames - list of Marker Names
     * @param start - the starting index of the sublist of results to be returned
     * @param numOfRows - the number of rows to be included in the sublist of results
     * to be returned
     * @return List of Mapping Values based on the specified Germplasm IDs and Marker Names
     * @throws QueryException the query exception
     */
    public List<MappingValueElement> getMappingValuesByGidsAndMarkerNames(
            List<Integer> gids, List<String> markerNames, int start, int numOfRows) throws QueryException;
    
    /**
     * Retrieves a list of Allelic Values (germplasm id, map_char_value, marker name) based on the specified GIDs and Marker Names.
     * Results are retrieved from 3 separate sources: Allele Values, Char Values, and Mapping Pop Values.
     *
     * @param gids - list of Germplasm IDs
     * @param markerNames - list of Marker Names
     * @return List of Allelic Values based on the specified Germplasm IDs and Marker Names
     * @throws QueryException the query exception
     */
    public List<AllelicValueElement> getAllelicValuesByGidsAndMarkerNames(
            List<Integer> gids, List<String> markerNames) throws QueryException;
    
    /**
     * Retrieves a list of Allelic Values (germplasm id, char_value, marker id) based on the specified datasetId from the char_values table.
     *
     * @param datasetId - the datasetId matching the allelic values
     * @param start the start
     * @param numOfRows the num of rows
     * @return List of Allelic Values based on the specified dataset id
     * @throws QueryException the query exception
     */
    public List<AllelicValueWithMarkerIdElement> getAllelicValuesFromCharValuesByDatasetId(
            Integer datasetId, int start, int numOfRows) throws QueryException;
    
    /**
     * Counts the allelic values based on the specified datasetId from the char_values table.
     *
     * @param datasetId - the datasetId matching the allelic values
     * @return the count
     * @throws QueryException the query exception
     */
    public int countAllelicValuesFromCharValuesByDatasetId(Integer datasetId) throws QueryException;

    /**
     * Retrieves a list of Allelic Values (germplasm id, allele_bin_value, marker id) based on the specified datasetId from the allele_values table.
     *
     * @param datasetId - the datasetId matching the allelic values
     * @param start the start
     * @param numOfRows the num of rows
     * @return List of Allelic Values based on the specified dataset id
     * @throws QueryException the query exception
     */
    public List<AllelicValueWithMarkerIdElement> getAllelicValuesFromAlleleValuesByDatasetId(
            Integer datasetId, int start, int numOfRows) throws QueryException;
    
    /**
     * Counts the allelic values based on the specified datasetId from the allele_values table.
     *
     * @param datasetId - the datasetId matching the allelic values
     * @return the count
     * @throws QueryException the query exception
     */
    public int countAllelicValuesFromAlleleValuesByDatasetId(Integer datasetId) throws QueryException;
    
    /**
     * Retrieves a list of Allelic Values (germplasm id, map_char_value, marker id) based on the specified datasetId from the mapping_pop_values table.
     *
     * @param datasetId - the datasetId matching the allelic values
     * @param start the start
     * @param numOfRows the num of rows
     * @return List of Allelic Values based on the specified dataset id
     * @throws QueryException the query exception
     */
    public List<AllelicValueWithMarkerIdElement> getAllelicValuesFromMappingPopValuesByDatasetId(
            Integer datasetId, int start, int numOfRows) throws QueryException;
    
    /**
     * Counts the allelic values based on the specified datasetId from the mapping_pop_values table.
     *
     * @param datasetId - the datasetId matching the allelic values
     * @return the count
     * @throws QueryException the query exception
     */
    public int countAllelicValuesFromMappingPopValuesByDatasetId(Integer datasetId) throws QueryException;

    /**
     * Retrieves a list of matching Marker Names from the Marker table based on
     * the specified list of Marker IDs.
     *
     * @param markerIds - List of Marker Ids to search for the corresponding Marker Names
     * @return List of matching Marker Names based on the specified Marker IDs
     * @throws QueryException the query exception
     */
    public List<String> getMarkerNamesByMarkerIds(List<Integer> markerIds) throws QueryException;

    /**
     * Gets the all marker types.
     *
     * @param start the start
     * @param numOfRows the num of rows
     * @return the all marker types
     * @throws QueryException the query exception
     */
    public List<String> getAllMarkerTypes(int start, int numOfRows) throws QueryException;
    
    /**
     * Get the number of all marker types.
     *
     * @param instance the instance
     * @return the long
     * @throws QueryException the query exception
     */
    public Long countAllMarkerTypes(Database instance) throws QueryException;
    
    /**
     * Gets the marker names by marker type.
     *
     * @param markerType the marker type
     * @param start the start
     * @param numOfRows the num of rows
     * @return the marker names by marker type
     * @throws QueryException the query exception
     */
    public List<String> getMarkerNamesByMarkerType(String markerType, int start, int numOfRows) throws QueryException;
    
    /**
     * Count marker names by marker type.
     *
     * @param markerType the marker type
     * @param instance the instance
     * @return the long
     * @throws QueryException the query exception
     */
    public Long countMarkerNamesByMarkerType(String markerType, Database instance) throws QueryException;
    
    /**
     * Gets the gids from char values by marker id.
     *
     * @param markerId the marker id
     * @param start the start
     * @param numOfRows the num of rows
     * @return the gI ds from char values by marker id
     * @throws QueryException the query exception
     */
    public List<Integer> getGIDsFromCharValuesByMarkerId(Integer markerId, int start, int numOfRows) throws QueryException;

    /**
     * Count gids from char values by marker id.
     *
     * @param markerId the marker id
     * @return the long
     * @throws QueryException the query exception
     */
    public Long countGIDsFromCharValuesByMarkerId(Integer markerId) throws QueryException;
    
    /**
     * Gets the gI ds from allele values by marker id.
     *
     * @param markerId the marker id
     * @param start the start
     * @param numOfRows the num of rows
     * @return the gI ds from allele values by marker id
     * @throws QueryException the query exception
     */
    public List<Integer> getGIDsFromAlleleValuesByMarkerId(Integer markerId, int start, int numOfRows) throws QueryException;
    
    /**
     * Count gi ds from allele values by marker id.
     *
     * @param markerId the marker id
     * @param start the start
     * @param numOfRows the num of rows
     * @return the long
     * @throws QueryException the query exception
     */
    public Long countGIDsFromAlleleValuesByMarkerId(Integer markerId, int start, int numOfRows) throws QueryException;
}
