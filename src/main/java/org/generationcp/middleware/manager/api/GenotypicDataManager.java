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
import org.generationcp.middleware.pojos.gdms.MarkerIdMarkerNameElement;
import org.generationcp.middleware.pojos.gdms.MarkerInfo;
import org.generationcp.middleware.pojos.gdms.MarkerNameElement;
import org.generationcp.middleware.pojos.gdms.ParentElement;

/**
 * This is the API for retrieving and storing genotypic data.
 * 
 */
public interface GenotypicDataManager{

    /**
     * Gets the name ids by germplasm ids. 
     * Searches the acc_metadataset table by giving the list of germplasm ids.
     *
     * @param gIds the list germplasm ids
     * @return the name ids matching the given germplasm ids
     * @throws QueryException 
     */
    public List<Integer> getNameIdsByGermplasmIds(List<Integer> gIds) throws QueryException;

    /**
     * Gets the Name records matching the given name ids.
     * This method is based on GMS_getNameRecord. 
     *
     * @param nIds the list of Name ids to match
     * @return the name records corresponding to the list of name ids
     * @throws QueryException
     */
    public List<Name> getNamesByNameIds(List<Integer> nIds) throws QueryException;

    /**
     * Gets the Name record by the given name id.
     *
     * @param nId the name id to match
     * @return the Name record corresponding to the name id
     * @throws QueryException
     */
    public Name getNameByNameId(Integer nId) throws QueryException;

    /**
     * Counts all Map records.
     *
     * @param instance 
     *          - specifies whether the data should be retrieved 
     *          from either the Central or the Local IBDB instance
     * @return the number of Map records found in the given instance
     * @throws QueryException
     */
    public Long countAllMaps(Database instance) throws QueryException;

    /**
     * Gets all the Map records in the given range from the given database instance.
     *
     * @param start 
     *          - the starting index of the sublist of results to be returned
     * @param numOfRows 
     *          - the number of rows to be included in the sublist of results 
     *          to be returned
     * @param instance 
     *          - specifies whether the data should be retrieved 
     *          from either the Central or the Local IBDB instance
     * @return List of all the maps on the given range from the given database instance
     * @throws QueryException
     */
    public List<Map> getAllMaps(Integer start, Integer numOfRows, Database instance) throws QueryException;

    /**
     * Gets map information (marker_name, linkage_group, start_position) 
     * from mapping_data view by the given map name.
     *
     * @param mapName 
     *          - the name of the map to retrieve
     * @param instance 
     *          - specifies whether the data should be retrieved 
     *          from either the Central or the Local IBDB instance
     * @return the map info corresponding to the given map name
     * @throws QueryException
     */
    public List<MapInfo> getMapInfoByMapName(String mapName, Database instance) throws QueryException;

    /**
     * Counts all the dataset names.
     *
     * @param instance 
     *          - specifies whether the data should be retrieved 
     *          from either the Central or the Local IBDB instance
     * @return the number of dataset names found in the given instance
     * @throws QueryException
     */
    public int countDatasetNames(Database instance) throws QueryException;

    /**
     * Gets the dataset names from the dataset table. 
     * Data is filtered by ignoring dataset  type = 'qtl'.
     * 
     * @param start 
     *          - the starting index of the sublist of results to be returned
     * @param numOfRows 
     *          - the number of rows to be included in the sublist of results 
     *          to be returned
     * @param instance 
     *          - specifies whether the data should be retrieved 
     *          from either the Central or the Local IBDB instance
     * @return List of the dataset names based on the given range 
     *          from the given database instance
     * @throws QueryException
     */
    public List<String> getDatasetNames(Integer start, Integer numOfRows, Database instance) throws QueryException;

    /**
     * Gets the dataset details (dataset id, dataset type) 
     * from the dataset table by dataset name.
     *
     * @param datasetName 
     *          - the dataset name to match
     * @param instance 
     *          - specifies whether the data should be retrieved 
     *          from either the Central or the Local IBDB instance
     * @return the dataset details by dataset name
     * @throws QueryException
     */
    public List<DatasetElement> getDatasetDetailsByDatasetName(String datasetName, Database instance) throws QueryException;

    /**
     * Retrieves a list of matching marker ids from the marker table based on
     * the specified list of Marker Names.
     *
     * @param markerNames 
     *          - List of marker names to search for the corresponding marker ids
     * @param start 
     *          - the starting index of the sublist of results to be returned
     * @param numOfRows 
     *          - the number of rows to be included in the sublist of results 
     *          to be returned
     * @param instance 
     *          - specifies whether the data should be retrieved 
     *          from either the Central or the Local IBDB instance
     * @return List of matching marker ids based on the specified marker names
     * @throws QueryException
     */
    public List<Integer> getMarkerIdsByMarkerNames(List<String> markerNames, int start, int numOfRows, Database instance)
            throws QueryException;

    /**
     * Gets the list of marker ids from the marker_metadataset table 
     * based on the given dataset id.
     *
     * @param datasetId the dataset id to match
     * @return the markerIds by datasetId
     * @throws QueryException
     */
    public List<Integer> getMarkerIdsByDatasetId(Integer datasetId) throws QueryException;

    /**
     * Gets the germplasm id of parents and the mapping type 
     * from the mapping_pop table based on the given dataset id.
     *
     * @param datasetId the dataset id to match
     * @return the parents and mapping type corresponding to the dataset id
     * @throws QueryException
     */
    public List<ParentElement> getParentsByDatasetId(Integer datasetId) throws QueryException;

    /**
     * Gets the marker type from the marker table based on the given marker ids.
     *
     * @param markerIds the marker ids to match
     * @return the marker type corresponding to the given marker ids
     * @throws QueryException
     */
    public List<String> getMarkerTypesByMarkerIds(List<Integer> markerIds) throws QueryException;

    /**
     * Gets the marker names by germplasm ids. 
     * This method searches the tables allele_values, char_values and mapping_pop_values 
     * for the existence of the given gids. Then gets the marker ids from allele_values, 
     * char_values, mapping_pop_values by the gids. And finally, gets the marker name 
     * from marker table by marker ids.
     *
     * @param gIds the germplasm ids to search for
     * @return the marker names corresponding to the given germplasm ids
     * @throws QueryException
     */
    public List<MarkerNameElement> getMarkerNamesByGIds(List<Integer> gIds) throws QueryException;

    /**
     * Gets the germplasm names by marker names.
     * This method searches the allele_values, char_values, mapping_pop_values tables 
     * for the existence of marker ids. Then gets gids from allele_values, char_values, 
     * mapping_pop_values by marker ids. And finally, returns the germplasm names 
     * matching the marker names.
     *
     * @param markerNames 
     *          - the marker names to match      
     * @param instance 
     *          - specifies whether the data should be retrieved 
     *          from either the Central or the Local IBDB instance
     * @return the GermplasmMarkerElement list that contains the germplasm name 
     *          and the corresponding marker names
     * @throws QueryException
     */
    public List<GermplasmMarkerElement> getGermplasmNamesByMarkerNames(List<String> markerNames, Database instance) throws QueryException;
    
    /**
     * Retrieves a list of mapping values based on the specified germplasm ids and marker names.
     * This method gets mapping values (dataset_id, mapping_type, parent_a_gid, parent_b_gid, marker_type) 
     * from mapping_pop and marker tables by the given gids and marker ids.

     * @param gids 
     *          - list of germplasm ids to match
     * @param markerNames 
     *          - list of marker names
     * @param start 
     *          - the starting index of the sublist of results to be returned
     * @param numOfRows 
     *          - the number of rows to be included in the sublist of results 
     *          to be returned
     * @return List of mapping values based on the specified germplasm ids and marker names
     * @throws QueryException
     */
    public List<MappingValueElement> getMappingValuesByGidsAndMarkerNames(List<Integer> gids, List<String> markerNames, int start,
            int numOfRows) throws QueryException;

    /**
     * Retrieves a list of allelic values (germplasm id, map_char_value, marker name) 
     * based on the specified germplasm ids and marker names.
     * Results are retrieved from 3 separate sources: allele_values, char_values, 
     * and mapping_pop_values.
     *
     * @param gids list of germplasm ids
     * @param markerNames list of marker names
     * @return List of allelic values based on the specified germplasm ids and marker names
     * @throws QueryException
     */
    public List<AllelicValueElement> getAllelicValuesByGidsAndMarkerNames(List<Integer> gids, List<String> markerNames)
            throws QueryException;

    /**
     * Retrieves a list of allelic values (germplasm id, char_value, marker id) 
     * based on the specified dataset id from the char_values table.
     *
     * @param datasetId 
     *          - the dataset id matching the allelic values
     * @param start 
     *          - the starting index of the sublist of results to be returned
     * @param numOfRows 
     *          - the number of rows to be included in the sublist of results 
     *          to be returned
     * @return List of allelic values based on the specified dataset id
     * @throws QueryException
     */
    public List<AllelicValueWithMarkerIdElement> getAllelicValuesFromCharValuesByDatasetId(Integer datasetId, int start, int numOfRows)
            throws QueryException;

    /**
     * Counts the allelic values based on the specified dataset id 
     * from the char_values table.
     *
     * @param datasetId the dataset id matching the allelic values
     * @return the number of allelic values from char_values table based on the specified dataset id
     * @throws QueryException
     */
    public int countAllelicValuesFromCharValuesByDatasetId(Integer datasetId) throws QueryException;

    /**
     * Retrieves a list of allelic values (germplasm id, allele_bin_value, marker id) 
     * based on the specified dataset id from the allele_values table.
     *
     * @param datasetId 
     *          - the dataset id matching the allelic values
     * @param start 
     *          - the starting index of the sublist of results to be returned
     * @param numOfRows 
     *          - the number of rows to be included in the sublist of results 
     *          to be returned
     * @return List of allelic values based on the specified dataset id
     * @throws QueryException
     */
    public List<AllelicValueWithMarkerIdElement> getAllelicValuesFromAlleleValuesByDatasetId(Integer datasetId, int start, int numOfRows)
            throws QueryException;

    /**
     * Counts the allelic values based on the specified datasetId 
     * from the allele_values table.
     *
     * @param datasetId the dataset id matching the allelic values
     * @return the number of allelic values from allele_values table based on the specified dataset id
     * @throws QueryException
     */
    public int countAllelicValuesFromAlleleValuesByDatasetId(Integer datasetId) throws QueryException;

    /**
     * Retrieves a list of allelic values (germplasm id, map_char_value, marker id) 
     * based on the specified dataset id from the mapping_pop_values table.
     *
     * @param datasetId 
     *          - the dataset id matching the allelic values
     * @param start 
     *          - the starting index of the sublist of results to be returned
     * @param numOfRows 
     *          - the number of rows to be included in the sublist of results 
     *          to be returned
     * @return List of allelic values based on the specified dataset id
     * @throws QueryException
     */
    public List<AllelicValueWithMarkerIdElement> getAllelicValuesFromMappingPopValuesByDatasetId(Integer datasetId, int start, int numOfRows)
            throws QueryException;

    /**
     * Counts the allelic values based on the specified dataset id from the mapping_pop_values table.
     *
     * @param datasetId the dataset id matching the allelic values
     * @return the number of allelic values from mapping_pop_values table based on the specified dataset id
     * @throws QueryException
     */
    public int countAllelicValuesFromMappingPopValuesByDatasetId(Integer datasetId) throws QueryException;

    /**
     * Retrieves a list of matching marker names from the marker table based on
     * the specified list of marker ids.
     *
     * @param markerIds List of marker ids to search for the corresponding marker names
     * @return List of matching marker names and marker ids 
     *          based on the specified marker ids
     * @throws QueryException
     */
    public List<MarkerIdMarkerNameElement> getMarkerNamesByMarkerIds(List<Integer> markerIds) throws QueryException;

    /**
     * Gets all marker types.
     *
     * @param start 
     *          - the starting index of the sublist of results to be returned
     * @param numOfRows 
     *          - the number of rows to be included in the sublist of results 
     *          to be returned
     * @return List of all marker types
     * @throws QueryException
     */
    public List<String> getAllMarkerTypes(int start, int numOfRows) throws QueryException;

    /**
     * Gets the number of marker types.
     *
     * @param instance 
     *          - specifies whether the data should be retrieved 
     *          from either the Central or the Local IBDB instance
     * @return the number of all marker types on the specified database instance
     * @throws QueryException
     */
    public Long countAllMarkerTypes(Database instance) throws QueryException;

    /**
     * Retrieves the names of the the markers which have the specified marker type.
     *
     * @param markerType 
     *          - the marker type to match
     * @param start 
     *          - the starting index of the sublist of results to be returned
     * @param numOfRows 
     *          - the number of rows to be included in the sublist of results 
     *          to be returned
     * @return List of marker names based on the specified marker type
     * @throws QueryException
     */
    public List<String> getMarkerNamesByMarkerType(String markerType, int start, int numOfRows) throws QueryException;

    /**
     * Count the number of marker names matching the given marker type.
     *
     * @param markerType the marker type to match
     * @return the number of marker names corresponding to the given marker type
     * @throws QueryException
     */
    public Long countMarkerNamesByMarkerType(String markerType) throws QueryException;

    /**
     * Retrieves all the associated germplasm ids matching the given marker id 
     * from the char_values table.
     * 
     * @param markerId 
     *          - the marker id to match
     * @param start 
     *          - the starting index of the sublist of results to be returned
     * @param numOfRows 
     *          - the number of rows to be included in the sublist of results 
     *          to be returned
     * @return List of germplasm ids from char_values based on the given marker id
     * @throws QueryException
     */
    public List<Integer> getGIDsFromCharValuesByMarkerId(Integer markerId, int start, int numOfRows) throws QueryException;

    /**
     * Retrieves a list of marker info based on the specified marker name 
     * from the marker_retrieval_info table.
     * 
     * @param markerName 
     *          - the markerName to match
     * @param start 
     *          - the starting index of the sublist of results to be returned
     * @param numOfRows 
     *          - the number of rows to be included in the sublist of results 
     *          to be returned
     * @param instance 
     *          - specifies whether the data should be retrieved 
     *          from either the Central or the Local IBDB instance
     * @return List of MarkerInfo based on the specified marker name
     * @throws QueryException
     */
    public List<MarkerInfo> getMarkerInfoByMarkerName(String markerName, int start, int numOfRows) throws QueryException;

    /**
     * Counts the marker info entries corresponding to the given marker name.
     * 
     * @param markerName 
     * @return the number of marker info entries
     * @throws QueryException
     */
    public int countMarkerInfoByMarkerName(String markerName) throws QueryException;

    /**
     * Retrieves a list of MarkerInfo based on the specified genotype from the marker_retrieval_info table.
     * 
     * @param genotype 
     *          - the genotype to match
     * @param start 
     *          - the starting index of the sublist of results to be returned
     * @param numOfRows 
     *          - the number of rows to be included in the sublist of results 
     *          to be returned
     * @return List of MarkerInfo based on the specified genotype
     * @throws QueryException
     */
    public List<MarkerInfo> getMarkerInfoByGenotype(String genotype, int start, int numOfRows) throws QueryException;

    /**
     * Counts the marker info entries corresponding to the given genotype.
     * 
     * @param genotype 
     * @return the number of marker info entries 
     * @throws QueryException
     */
    public int countMarkerInfoByGenotype(String genotype) throws QueryException;

    /**
     * Retrieves a list of marker info entries based on the specified db accession id 
     * from the marker_retrieval_info table.
     * 
     * @param dbAccessionId 
     *          - the db accession id  to match
     * @param start 
     *          - the starting index of the sublist of results to be returned
     * @param numOfRows 
     *          - the number of rows to be included in the sublist of results 
     *          to be returned
     * @return List of MarkerInfo based on the specified db accession id 
     * @throws QueryException
     */
    public List<MarkerInfo> getMarkerInfoByDbAccessionId(String dbAccessionId, int start, int numOfRows) throws QueryException;

    /**
     * Counts the marker info entries corresponding to the given db accession id.
     * 
     * @param dbAccessionId  
     * @return the number of marker info entries
     * @throws QueryException
     */
    public int countMarkerInfoByDbAccessionId(String dbAccessionId) throws QueryException;

    /**
     * Counts the number of germplasm ids matching the given marker id
     * from the char_values table.
     *
     * @param markerId the marker id
     * @return the count of germplasm ids corresponding to the given marker id 
     *          from char_values
     * @throws QueryException
     */
    public Long countGIDsFromCharValuesByMarkerId(Integer markerId) throws QueryException;

    /**
     * Retrieves all the associated germplasm ids matching the given marker id 
     * from the allele_values table.
     *
     * @param markerId the marker id
     * @param start 
     *          - the starting index of the sublist of results to be returned
     * @param numOfRows 
     *          - the number of rows to be included in the sublist of results 
     *          to be returned
     * @return List of germplasm ids from allele values based on the given marker id
     * @throws QueryException
     */
    public List<Integer> getGIDsFromAlleleValuesByMarkerId(Integer markerId, int start, int numOfRows) throws QueryException;

    /**
     * Counts the number of germplasm ids matching the given marker id 
     * from the allele_values table.
     *
     * @param markerId the marker id
     * @return the count of germplasm ids corresponding to the given marker id 
     *          from allele_values
     * @throws QueryException
     */
    public Long countGIDsFromAlleleValuesByMarkerId(Integer markerId) throws QueryException;

    /**
     * Retrieves all the associated germplasm ids matching the given marker id 
     * from the mapping_pop_values table.
     *
     * @param markerId 
     *          - the marker id to match
     * @param start 
     *          - the starting index of the sublist of results to be returned
     * @param numOfRows 
     *          - the number of rows to be included in the sublist of results 
     *          to be returned
     * @return List of germplasm ids from mapping_pop_values table 
     *          based on the given marker id
     * @throws QueryException
     */
    public List<Integer> getGIDsFromMappingPopValuesByMarkerId(Integer markerId, int start, int numOfRows) throws QueryException;

    /**
     * Counts the number of germplasm ids matching the given marker id 
     * from the mapping_pop_values table.
     *
     * @param markerId the marker id to match
     * @return the count of germplasm ids corresponding to the given marker id 
     *          from mapping_pop_values
     * @throws QueryException
     */
    public Long countGIDsFromMappingPopValuesByMarkerId(Integer markerId) throws QueryException;

    /**
     * Gets the all db accession ids from marker.
     *
     * @param start 
     *          - the starting index of the sublist of results to be returned
     * @param numOfRows 
     *          - the number of rows to be included in the sublist of results 
     *          to be returned
     * @return List of all non-empty db_accession IDs from Marker
     * @throws QueryException
     */
    public List<String> getAllDbAccessionIdsFromMarker(int start, int numOfRows) throws QueryException;

    /**
     * Count all db accession ids from marker.
     *
     * @return the number of non-empty db accession ids from marker
     * @throws QueryException
     */
    public Long countAllDbAccessionIdsFromMarker() throws QueryException;

    /**
     * Gets the nids from acc metadataset by dataset ids.
     *
     * @param datasetIds 
     *          - the dataset ids to match
     * @param start 
     *          - the starting index of the sublist of results to be returned
     * @param numOfRows 
     *          - the number of rows to be included in the sublist of results 
     *          to be returned
     * @return List of all name ids from acc_metadataset table 
     *          based on the given list of dataset ids
     * @throws QueryException
     */
    public List<Integer> getNidsFromAccMetadatasetByDatasetIds(List<Integer> datasetIds, int start, int numOfRows) throws QueryException;

    /**
     * Gets the nids from acc metadataset by dataset ids filtered by gids.
     *
     * @param datasetIds 
     *          - the dataset ids to match
     * @param gids 
     *          - the gids to match
     * @param start 
     *          - the starting index of the sublist of results to be returned
     * @param numOfRows 
     *          - the number of rows to be included in the sublist of results 
     *          to be returned
     * @return List of name ids from acc_metadataset based on the given list of dataset ids
     * @throws QueryException
     */
    public List<Integer> getNidsFromAccMetadatasetByDatasetIds(List<Integer> datasetIds, List<Integer> gids, int start, int numOfRows)
            throws QueryException;

}
