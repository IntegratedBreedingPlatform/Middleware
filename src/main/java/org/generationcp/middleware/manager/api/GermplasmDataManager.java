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
import java.util.Map;

import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.GermplasmNameType;
import org.generationcp.middleware.manager.GetGermplasmByNameModes;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.Bibref;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmNameDetails;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.LocationDetails;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;

// TODO: Auto-generated Javadoc
/**
 * This is the API for retrieving Germplasm information.
 * 
 * @author Kevin Manansala
 * 
 */
public interface GermplasmDataManager {
    
    /**
     * Searches for all germplasm records which matches the given name. Three
     * searching modes are available; normal search, the spaces on the name will
     * be removed, the name will be standardize before searching. The three
     * modes can be specified using the enum GetGermplasmByNameModes.
     * 
     * Names to be included in the search may be limited by name status.
     * Accepted values are 0 - 10. If the given status is zero all names will be
     * included in the search.
     * 
     * Names to be included in the search may be limited by name type. The enum
     * GermplasmNameType is used to specify the type of names to be included in
     * the search. If the given type is null, all names will be included in the
     * search.
     *
     * @param name - search string for the name of the germplasm
     * @param start - the starting index of the sublist of results to be returned
     * @param numOfRows - the number of rows to be included in the sublist of results
     * to be returned
     * @param mode - can be normal, spaces removed, name standardized
     * @param op - can be EQUAL OR LIKE
     * @param status - nstat of the names to be included in the search
     * @param type - name type
     * @param instance - can be Database.LOCAL or Database.CENTRAL
     * @return List of Germplasm POJOs
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Germplasm> getGermplasmByName(String name, int start, int numOfRows, GetGermplasmByNameModes mode, Operation op,
            Integer status, GermplasmNameType type, Database instance) throws MiddlewareQueryException;

    /**
     * Searches for all germplasm records which matches the given name.
     * It will match records having the following names: (1) the given name as it is,
     * (2) the name with standardization performed on it, and (3) name with spaces removed.
     *
     * @param name - search string for the name of the germplasm
     * @param start - the starting index of the sublist of results to be returned
     * @param numOfRows - the number of rows to be included in the sublist of results
     * to be returned
     * @param op - can be EQUAL OR LIKE
     * @return List of Germplasm POJOs
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Germplasm> getGermplasmByName(String name, int start, int numOfRows, Operation op) throws MiddlewareQueryException;
    
    /**
     * Searches for all germplasm records which matches the given name.
     * It will match records having the given name as it is,
     *
     * @param name - search string for the name of the germplasm
     * @param start - the starting index of the sublist of results to be returned
     * @param numOfRows - the number of rows to be included in the sublist of results
     * to be returned
     * @return List of Germplasm POJOs
     * @throws MiddlewareQueryException the middleware query exception
     */
    @Deprecated
    List<Germplasm> getGermplasmByName(String name, int start, int numOfRows) throws MiddlewareQueryException;
    

    /**
     * Returns the number of germplasm records with any name matching the given
     * parameter. Search modes can also be specified like in using the
     * getGermplasmByName() method.
     * 
     * Names to be included in the search may be limited by name status.
     * Accepted values are 0 - 10. If the given status is zero all names will be
     * included in the search.
     * 
     * Names to be included in the search may be limited by name type. The enum
     * GermplasmNameType is used to specify the type of names to be included in
     * the search. If the given type is null, all names will be included in the
     * search.
     *
     * @param name - search string for the name of the germplasm
     * @param mode - can be normal, spaces removed, name standardized
     * @param op - can be EQUAL OR LIKE
     * @param status - nstat of the names to be included in the search
     * @param type - name type
     * @param instance - can be Database.LOCAL or Database.CENTRAL
     * @return number of germplasm records
     * @throws MiddlewareQueryException the middleware query exception
     */
    long countGermplasmByName(String name, GetGermplasmByNameModes mode, Operation op, Integer status, GermplasmNameType type,
            Database instance) throws MiddlewareQueryException;

    /**
     * Returns the number of germplasm records with any name matching the given
     * parameter.
     * It will count records having the following names: (1) the given name as it is,
     * (2) the name with standardization performed on it, and (3) name with spaces removed.
     *
     * @param name - search string for the name of the germplasm
     * @param op - can be EQUAL OR LIKE
     * @return number of germplasm records
     * @throws MiddlewareQueryException the middleware query exception
     */
    long countGermplasmByName(String name, Operation op) throws MiddlewareQueryException;
    
    /**
     * Returns the germplasm records that were created at the locations with
     * names matching the given parameter.
     *
     * @param name - search string for the name of the locations
     * @param start - the starting index of the sublist of results to be returned
     * @param numOfRows - the number of rows to be included in the sublist of results
     * to be returned
     * @param op - can be EQUAL like LIKE
     * @param instance - can be Database.CENTRAL or Database.LOCAL
     * @return List of Germplasm POJOs
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Germplasm> getGermplasmByLocationName(String name, int start, int numOfRows, Operation op, Database instance)
            throws MiddlewareQueryException;
    
    /**
     * Please use LocationDataManager.getLocationDetailsByLocId().
     * 
     * Returns the germplasm records that were created at the locations with
     * names matching the given parameter.
     *
     * @param locationId - location id to search details from
     * @param start - the starting index of the sublist of results to be returned
     * @param numOfRows - the number of rows to be included in the sublist of results
     * to be returned
     * @return List of Germplasm POJOs
     * @throws MiddlewareQueryException the middleware query exception
     */
    @Deprecated
    List<LocationDetails> getLocationDetailsByLocId(Integer locationId, int start, int numOfRows)
            throws MiddlewareQueryException;

    /**
     * Returns the number of germplasm records that were created at the locations with
     * names matching the given parameter.
     *
     * @param name - search string for the name of the locations
     * @param op - can be EQUAL like LIKE
     * @param instance - can be Database.CENTRAL or Database.LOCAL
     * @return Number of Germplasms
     * @throws MiddlewareQueryException the middleware query exception
     */    
    long countGermplasmByLocationName(String name, Operation op, Database instance) throws MiddlewareQueryException;
    
    /**
     * Please use LocationDataManager.getAllLocations().
     * 
     * Returns all Location information from central and local databases.
     *
     * @return All Locations
     * @throws MiddlewareQueryException the middleware query exception
     */   
    @Deprecated
    List<Location> getAllLocations() throws MiddlewareQueryException;

    /**
     * Please use LocationDataManager.getAllLocalLocations().
     * 
     * Returns all Local Locations.
     *
     * @param start - the starting index of the sublist of results to be returned
     * @param numOfRows - the number of rows to be included in the sublist of results
     * to be returned
     * @return All Locations based on the given start and numOfRows
     * @throws MiddlewareQueryException the middleware query exception
     */  
    @Deprecated
    List<Location> getAllLocalLocations(int start, int numOfRows)
            throws MiddlewareQueryException;
    
    
    /**
     * Please use LocationDataManager.countAllLocations().
     * 
     * Returns number of all Locations.
     *
     * @return the number of all Locations
     * @throws MiddlewareQueryException the middleware query exception
     */   
    @Deprecated
    long countAllLocations() throws MiddlewareQueryException;

    /**
     * Please use LocationDataManager.getLocationsByName().
     * 
     * Returns the Location records with
     * names matching the given parameter.
     *
     * @param name - search string for the name of the locations
     * @param op - can be EQUAL like LIKE
     * @return List of Location POJOs
     * @throws MiddlewareQueryException the middleware query exception
     */
    @Deprecated
    List<Location> getLocationsByName(String name, Operation op) throws MiddlewareQueryException;
    

    /**
     * Please use LocationDataManager.getLocationsByName().
     * 
     * Returns the Location records with
     * names matching the given parameter.
     *
     * @param name - search string for the name of the locations
     * @param start - the starting index of the sublist of results to be returned
     * @param numOfRows - the number of rows to be included in the sublist of results
     * to be returned
     * @param op - can be EQUAL like LIKE
     * @return List of Location POJOs
     * @throws MiddlewareQueryException the middleware query exception
     */
    @Deprecated
    List<Location> getLocationsByName(String name, int start, int numOfRows, Operation op) throws MiddlewareQueryException;
    
    /**
     * Please use LocationDataManager.getAllCountry().
     * 
     * Returns all country records.
     *
     * @return List of Location POJOs
     * @throws MiddlewareQueryException the middleware query exception
     */
    @Deprecated
    List<Country> getAllCountry() throws MiddlewareQueryException;
    
    /**
     * Please use LocationDataManager.countLocationsByName().
     * 
     * Returns the number of Locations with
     * names matching the given parameter.
     *
     * @param name - search string for the name of the locations
     * @param op - can be EQUAL like LIKE
     * @return Number of Locations
     * @throws MiddlewareQueryException the middleware query exception
     */
    @Deprecated
    long countLocationsByName(String name, Operation op) throws MiddlewareQueryException;


    /**
     * Please use LocationDataManager.getLocationsByCountry().
     * 
     * Returns all the Location records with
     * country matching the given parameter. The data is retrieved from both local and central databases.
     *
     * @param country - search string for the country of the locations
     * @return List of Location POJOs
     * @throws MiddlewareQueryException the middleware query exception
     */
    @Deprecated
    List<Location> getLocationsByCountry(Country country) throws MiddlewareQueryException;
    
    
    /**
     * Please use LocationDataManager.getLocationsByCountryAndType().
     * 
     * Returns all the Location records with
     * country and location type  matching  the given parameter. The data is retrieved from both local and central databases.
     *
     * @param country - search string for the country of the locations
     * @param type - search string for the locations type
     * @return List of Location POJOs
     * @throws MiddlewareQueryException the middleware query exception
     */
    @Deprecated
    List<Location> getLocationsByCountryAndType(Country country,Integer type) throws MiddlewareQueryException;

    /**
     * Please use LocationDataManager.getLocationsByNameCountryAndType().
     * 
     * Returns all the Location records with
     * name, country and location type  matching  the given parameter. 
     * The data is retrieved from both local and central databases.
     *
     * @param name the name
     * @param country the country
     * @param type the type
     * @return the locations by name country and type
     * @throws MiddlewareQueryException the middleware query exception
     */
    @Deprecated
    List<Location> getLocationsByNameCountryAndType(String name,
            Country country, Integer type) throws MiddlewareQueryException;
    

    /**
     * Please use LocationDataManager.getLocationsByCountry().
     * 
     * Returns the Location records with country matching the given parameter. 
     * The data is retrieved from both local and central databases.
     *
     * @param country - search string for the country of the locations
     * @param start - the starting index of the sublist of results to be returned
     * @param numOfRows - the number of rows to be included in the sublist of results
     * to be returned
     * @return List of Location POJOs
     * @throws MiddlewareQueryException the middleware query exception
     */
    @Deprecated
    List<Location> getLocationsByCountry(Country country, int start, int numOfRows) throws MiddlewareQueryException;
    
    /**
     * Please use LocationDataManager.countLocationsByContry().
     * 
     * Returns the number of Locations with
     * countries matching the given parameter. 
     * The data is retrieved from both local and central databases.
     *
     * @param country - search string for the country of the locations
     * @return Number of Locations
     * @throws MiddlewareQueryException the middleware query exception
     */
    @Deprecated
    long countLocationsByCountry(Country country) throws MiddlewareQueryException;
    


    /**
     * Please use LocationDataManager.getLocationsByType().
     * 
     * Returns the Location records with type matching the given parameter. 
     * The data is retrieved from both local and central databases.
     *
     * @param type - search string for the type of the locations
     * @return List of Location POJOs
     * @throws MiddlewareQueryException the middleware query exception
     */
    @Deprecated
    List<Location> getLocationsByType(Integer type) throws MiddlewareQueryException;

    /**
     * Please use LocationDataManager.getLocationsByType().
     * 
     * Returns the Location records with
     * type matching the given parameter. The data is retrieved from both local and central databases.
     *
     * @param type - search string for the type of the locations
     * @param start - the starting index of the sublist of results to be returned
     * @param numOfRows - the number of rows to be included in the sublist of results
     * to be returned
     * @return List of Location POJOs
     * @throws MiddlewareQueryException the middleware query exception
     */
    @Deprecated
    List<Location> getLocationsByType(Integer type, int start, int numOfRows) throws MiddlewareQueryException;
    
    /**
     * Please use LocationDataManager.countLocationsByType().
     * 
     * Returns the number of Locations with
     * types matching the given parameter. The data is retrieved from both local and central databases.
     *
     * @param type - search string for the type of the locations
     * @return Number of Locations
     * @throws MiddlewareQueryException the middleware query exception
     */
    @Deprecated
    long countLocationsByType(Integer type) throws MiddlewareQueryException;
    
    
    /**
     * Retrieves all the Germplasm entries from the given database instance.
     *
     * @param start - the starting index of the sublist of results to be returned
     * @param numOfRows - the number of rows to be included in the sublist of results
     * to be returned
     * @param instance - can be Database.CENTRAL or Database.LOCAL
     * @return All the germplasms from the database instance satisfying the start and numOfRows parameters
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Germplasm> getAllGermplasm(int start, int numOfRows, Database instance) throws MiddlewareQueryException;

    
    /**
     * Returns the germplasm records that were created by the methods with names
     * matching the given parameter.
     *
     * @param name - search string for the name of the methods
     * @param start - the starting index of the sublist of results to be returned
     * @param numOfRows - the number of rows to be included in the sublist of results
     * to be returned
     * @param op - can be EQUAL or LIKE
     * @param instance - can be Database.CENTRAL or Database.LOCAL
     * @return List of Germplasm POJOS
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Germplasm> getGermplasmByMethodName(String name, int start, int numOfRows, Operation op, Database instance)
            throws MiddlewareQueryException;

    /**
     * Returns the number of germplasm records that were created by methods with
     * names matching the given parameter.
     *
     * @param name - search string for the name of the methods
     * @param op - can be equal or like
     * @param instance - can be Database.CENTRAL or Database.LOCAL
     * @return number of germplasm records
     * @throws MiddlewareQueryException the middleware query exception
     */
    long countGermplasmByMethodName(String name, Operation op, Database instance) throws MiddlewareQueryException;

    /**
     * Returns the germplasm record identified by the given id.
     *
     * @param gid - id of the germplasm record to be retrieved
     * @return the Germplasm POJO representing the record
     * @throws MiddlewareQueryException the middleware query exception
     */
    Germplasm getGermplasmByGID(Integer gid) throws MiddlewareQueryException;

    /**
     * Given a gid, return the Germplasm POJO representing the record identified
     * by the id with its preferred name.
     *
     * @param gid - the id of the germplasm record to be retrieved
     * @return the Germplasm POJO representing the record
     * @throws MiddlewareQueryException the middleware query exception
     */
    Germplasm getGermplasmWithPrefName(Integer gid) throws MiddlewareQueryException;

    /**
     * Given a gid, return the Germplasm POJO representing the record identified
     * by the id with its preferred name and preferred abbreviation.
     *
     * @param gid - the id of the germplasm record to be retrieved
     * @return the Germplasm POJO representing the record
     * @throws MiddlewareQueryException the middleware query exception
     */
    Germplasm getGermplasmWithPrefAbbrev(Integer gid) throws MiddlewareQueryException;

    /**
     * Returns the Name record identified by the given id.
     *
     * @param id - id of the name record
     * @return the Name POJO representing the record
     * @throws MiddlewareQueryException the middleware query exception
     */
    Name getGermplasmNameByID(Integer id) throws MiddlewareQueryException;

    /**
     * Returns all the names of the Germplasm identified by the gid parameter.
     * 
     * Results may be filtered by name status. Accepted values are 0 - 10. If
     * the given status is zero all names will be included in the result.
     * 
     * Results may also be filtered by type. The enum GermplasmNameType is used
     * to specify the type of names to be included in the result. If the given
     * type is null, all names will be included in the result.
     *
     * @param gid - id of the Germplasm
     * @param status - may be used to filter the results
     * @param type - may be used to filter the results
     * @return List of Name POJOs
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Name> getNamesByGID(Integer gid, Integer status, GermplasmNameType type) throws MiddlewareQueryException;

    /**
     * Returns the preferred name of the Germplasm identified by the gid
     * parameter.
     *
     * @param gid - id of the Germplasm
     * @return {@code Name} POJO of the Germplasm's preferred name. Returns
     * @throws MiddlewareQueryException the middleware query exception
     * {@code null} when no preferred name is found.
     */
    Name getPreferredNameByGID(Integer gid) throws MiddlewareQueryException;

    /**
     * Returns the preferred abbreviation of the Germplasm identified by the gid
     * parameter.
     *
     * @param gid - id of the Germplasm
     * @return {@code Name} POJO of the Germplasm's preferred abbreviation.
     * Returns {@code null} when no preferred abbreviation is found.
     * @throws MiddlewareQueryException the middleware query exception
     */
    Name getPreferredAbbrevByGID(Integer gid) throws MiddlewareQueryException;
    
    /**
     * Returns the preferred ID of the Germplasm identified by the gid
     * parameter.
     *
     * @param gid - id of the Germplasm
     * @return {@code Name} POJO of the Germplasm's preferred ID.
     * Returns {@code null} when no preferred ID is found.
     * @throws MiddlewareQueryException the middleware query exception
     */
    Name getPreferredIdByGID(Integer gid) throws MiddlewareQueryException;
    
    /**
     * Returns a list of preferred IDs of the Germplasms associated with the
     * Germplasm List identified by the listId parameter.
     *
     * @param listId - id of the Germplasm List
     * @return {@code Name} A list of POJOs of the Germplasms' preferred IDs.
     * Returns an empty list when no preferred ID is found.
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Name> getPreferredIdsByListId(Integer listId) throws MiddlewareQueryException;

    /**
     * Returns the value (NVAL field) of preferred name of the Germplasm identified by the gid
     * parameter.
     *
     * @param gid - id of the Germplasm
     * @return Germplasm's preferred name as string. Returns
     * @throws MiddlewareQueryException the middleware query exception
     * {@code null} when no preferred name is found.
     */
    String getPreferredNameValueByGID(Integer gid) throws MiddlewareQueryException;


    /**
     * Returns the matching {@code Name} object given a Germplasm ID and a Name
     * value.
     *
     * @param gid - id of the Germplasm
     * @param nval - value of the Name to search
     * @param mode - can be normal, spaces removed, name standardized
     * @return {@code Name} POJO of the matching {@code Name} object. Returns
     * @throws MiddlewareQueryException the middleware query exception
     * {@code null} when no {@code Name} with the specified gid and nval
     * is found.
     */
    Name getNameByGIDAndNval(Integer gid, String nval, GetGermplasmByNameModes mode) throws MiddlewareQueryException;

    /**
     * Sets the specified Name as the specified Germplasm's new preferred Name.
     *
     * @param gid - id of the Germplasm to be updated
     * @param newPrefName - new name to set as the preferred name
     * @return Returns the id of the updated {@code Germplasm} record
     * @throws MiddlewareQueryException the middleware query exception
     */
    Integer updateGermplasmPrefName(Integer gid, String newPrefName) throws MiddlewareQueryException;

    /**
     * Sets the specified Abbreviation as the specified Germplasm's new
     * preferred Abbreviation.
     *
     * @param gid - id of the Germplasm to be updated
     * @param newPrefAbbrev - new abbreviation to set as the preferred abbreviation
     * @return Returns the id of the updated {@code Germplasm} record
     * @throws MiddlewareQueryException the middleware query exception
     */
    Integer updateGermplasmPrefAbbrev(Integer gid, String newPrefAbbrev) throws MiddlewareQueryException;

    /**
     * Inserts a single {@code Name} object into the database.
     *
     * @param name - The {@code Name} object to be persisted to the database.
     * Must be a valid {@code Name} object.
     * @return Returns the id of the newly-added Germplasm {@code Name}.
     * @throws MiddlewareQueryException the middleware query exception
     */
    Integer addGermplasmName(Name name) throws MiddlewareQueryException;

    /**
     * Inserts a list of multiple {@code Name} objects into the database.
     *
     * @param names - A list of {@code Name} objects to be persisted to the
     * database. {@code Name} objects must be valid.
     * @return Returns the id of the newly-added Germplasm {@code Name}s.
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Integer> addGermplasmName(List<Name> names) throws MiddlewareQueryException;

    /**
     * Updates a single {@code Name} object in the database.
     *
     * @param name - The {@code Name} object to be updated in the database. Must
     * be a valid {@code Name} object.
     * @return Returns the id of the updated Germplasm {@code Name}.
     * @throws MiddlewareQueryException the middleware query exception
     */
    Integer updateGermplasmName(Name name) throws MiddlewareQueryException;

    /**
     * Updates the database with multiple {@code Name} objects specified.
     *
     * @param names - A list of {@code Name} objects to be updated in the
     * database. {@code Name} objects must be valid.
     * @return Returns the id of the updated Germplasm {@code Name}s.
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Integer> updateGermplasmName(List<Name> names) throws MiddlewareQueryException;

    /**
     * Returns all the attributes of the Germplasm identified by the given id.
     *
     * @param gid - id of the Germplasm
     * @return List of Atributs POJOs
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Attribute> getAttributesByGID(Integer gid) throws MiddlewareQueryException;
    
    /**
     * Returns all the list of attribute types identified by the given list of gids.
     *
     * @param gidList - list of GIDs
     * @return List of UserDefinedField POJOs that contains the attribute types and names for the given GIDs.
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<UserDefinedField> getAttributeTypesByGIDList(List<Integer> gidList) throws MiddlewareQueryException;
    
    /**
     * Returns a Map of GIDs to the attribute values given an attribute type and a list of GIDs.
     *
     * @param attributeType - attribute type of the values to retrieve
     * @param gidList - list of GIDs
     * @return Map<Integer, String>
     * - map of gids to their corresponding attribute values for the specified attribute type
     * @throws MiddlewareQueryException the middleware query exception
     */
    Map<Integer, String> getAttributeValuesByTypeAndGIDList(Integer attributeType, List<Integer> gidList) throws MiddlewareQueryException;

    /**
     * Returns the Method record identified by the id.
     *
     * @param id - id of the method record
     * @return the Method POJO representing the record
     * @throws MiddlewareQueryException the middleware query exception
     */
    Method getMethodByID(Integer id) throws MiddlewareQueryException;

    /**
     * Returns all the method records.
     *
     * @return List of Method POJOs
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Method> getAllMethods() throws MiddlewareQueryException;
    
    /**
     * Returns all the method records.
     *
     * @param programUUID - unique id of the current program
     * @return List of Method POJOs
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Method> getMethodsByUniqueID(String programUUID) throws MiddlewareQueryException;    

    /**
     * Returns the number of Methods with
     * type matching the given parameter.
     * Retrieves from both local and central databases.
     *
     * @param programUUID - unique id of the current program
     * @return Number of Methods matching the given type
     * @throws MiddlewareQueryException the middleware query exception
     */
    long countMethodsByUniqueID(String programUUID) throws MiddlewareQueryException;
    
    /**
     * Gets the all methods not generative.
     *
     * @return the all methods not generative
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Method> getAllMethodsNotGenerative() throws MiddlewareQueryException;

    /**
     * Returns count of all the method records.
     *
     * @return count of methods
     * @throws MiddlewareQueryException the middleware query exception
     */
    long countAllMethods() throws MiddlewareQueryException;
    
    /**
     * Returns all the method records matching the given type.
     * Retrieves from both local and central databases.
     *
     * @param type the type of the method
     * @return List of Method POJOs
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Method> getMethodsByType(String type) throws MiddlewareQueryException;
    
    /**
     * Returns all the method records matching the given type.
     * Retrieves from both local and central databases.
     *
     * @param type the type of the method
     * @param programUUID - unique id of the current program
     * @return List of Method POJOs
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Method> getMethodsByType(String type, String programUUID) throws MiddlewareQueryException;
    
    /**
     * Returns the number of Methods with
     * type matching the given parameter.
     * Retrieves from both local and central databases.
     *
     * @param type - search string for the methods
     * @param programUUID - unique id of the current program
     * @return Number of Methods matching the given type
     * @throws MiddlewareQueryException the middleware query exception
     */
    long countMethodsByType(String type, String programUUID) throws MiddlewareQueryException;

    /**
     * Returns all the method records matching the given group and the methods having the 'G' group.
     * Retrieves from both local and central databases.
     *
     * @param group the group of the method
     * @return List of Method POJOs
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Method> getMethodsByGroupIncludesGgroup(String group) throws MiddlewareQueryException;

    
    /**
     * Returns all the method records matching the given type.
     * Retrieves from both local and central databases.
     *
     * @param type the type of the method
     * @param start - the starting index of the sublist of results to be returned
     * @param numOfRows - the number of rows to be included in the sublist of results
     * to be returned
     * @return List of Method POJOs
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Method> getMethodsByType(String type, int start, int numOfRows) throws MiddlewareQueryException;
    

    /**
     * Returns the number of Methods with
     * type matching the given parameter.
     * Retrieves from both local and central databases.
     *
     * @param type - search string for the methods
     * @return Number of Methods matching the given type
     * @throws MiddlewareQueryException the middleware query exception
     */
    long countMethodsByType(String type) throws MiddlewareQueryException;

    /**
     * Returns all the method records matching the given group.
     * Retrieves from both local and central databases.
     *
     * @param group the group of the method
     * @return List of Method POJOs
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Method> getMethodsByGroup(String group) throws MiddlewareQueryException;


    /**
     * Returns all the method records matching the given group.
     * Retrieves from both local and central databases.
     *
     * @param group the group of the method
     * @param start - the starting index of the sublist of results to be returned
     * @param numOfRows - the number of rows to be included in the sublist of results
     * to be returned
     * @return List of Method POJOs
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Method> getMethodsByGroup(String group, int start, int numOfRows) throws MiddlewareQueryException;
    
    /**
     * Returns all the method and type records matching the given group and type.
     * Retrieves from both local and central databases.
     *
     * @param group the group of the method
     * @param type the type of the method
     * @return List of Method POJOs
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Method> getMethodsByGroupAndType(String group,String type) throws MiddlewareQueryException;
    
    /**
     * Returns all the method and type records matching the given group, type and name.
     * Retrieves from both local and central databases.
     *
     * @param group the group of the method
     * @param type the type of the method
     * @param name the name of the method
     * @return List of Method POJOs
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Method> getMethodsByGroupAndTypeAndName(String group,String type, String name) throws MiddlewareQueryException;
    
    /**
     * Returns the number of Methods with
     * group matching the given parameter.
     * 
     * Retrieves from both local and central databases.
     *
     * @param group - search string for the methods
     * @return Number of Methods matching the given group
     * @throws MiddlewareQueryException the middleware query exception
     */
    long countMethodsByGroup(String group) throws MiddlewareQueryException;
    
    
    /**
     * Gets list of cvterm records which are possible values of method classes.
     *
     * @return the method classes
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Term> getMethodClasses() throws MiddlewareQueryException;

    /**
     * Returns the udfld record identified by the given id.
     *
     * @param id - the id of the udfld record
     * @return the Udflds POJO representing the record
     * @throws MiddlewareQueryException the middleware query exception
     */
    UserDefinedField getUserDefinedFieldByID(Integer id) throws MiddlewareQueryException;
    
    /**
     * Returns the udfld records identified by the given tablename.
     *
     * @param tableName - the value of the ftable record
     * @param fieldType - the value of the ftype record
     * @return the Udflds POJO representing the record
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<UserDefinedField> getUserDefinedFieldByFieldTableNameAndType(String tableName,String fieldType) throws MiddlewareQueryException;

    /**
     * Please use LocationDataManager.getCountryById().
     * 
     * Returns the country record identified by the given id.
     *
     * @param id - id of the country record
     * @return the Country POJO representing the record
     * @throws MiddlewareQueryException the middleware query exception
     */
    @Deprecated
    Country getCountryById(Integer id) throws MiddlewareQueryException;

    /**
     * Please use LocationDataManager.getLocationById().
     * 
     * Returns the location record identified by the given id.
     *
     * @param id - id of the location record
     * @return the Location POJO representing the record
     * @throws MiddlewareQueryException the middleware query exception
     */
    @Deprecated
    Location getLocationByID(Integer id) throws MiddlewareQueryException;

    /**
     * Updates the {@code Method} object into the database.
     *
     * @param method - The {@code Method} object to be persisted to the database.
     * Must be a valid {@code Method} object.
     * @return Returns the updated {@code Method} record
     *
     * @throws MiddlewareQueryException the middleware query exception
     */
    Method editMethod(Method method) throws MiddlewareQueryException;


        /**
         * Inserts a single {@code Method} object into the database.
         *
         * @param method - The {@code Method} object to be persisted to the database.
         * Must be a valid {@code Method} object.
         * @return Returns the id of the {@code Method} record inserted in the
         * database.
         * @throws MiddlewareQueryException the middleware query exception
         */
    Integer addMethod(Method method) throws MiddlewareQueryException;
    
    /**
     * Inserts a list of {@code Method} objects into the database.
     *
     * @param methods - The list of {@code Method} objects to be persisted to the database.
     * Must be valid {@code Method} objects.
     * @return Returns the ids of the {@code Method} records inserted in the
     * database.
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Integer> addMethod(List<Method> methods) throws MiddlewareQueryException;
    
    /**
     * Deletes a single {@code Method} object into the database.
     *
     * @param method - The {@code Method} object to be deleted from the database.
     * Must be a valid {@code Method} object.
     * @throws MiddlewareQueryException the middleware query exception
     */
    void deleteMethod(Method method) throws MiddlewareQueryException;

    /**
     * Returns the Bibref record identified by the given id.
     *
     * @param id - id of the bibref record
     * @return the Bibref POJO representing the record
     * @throws MiddlewareQueryException the middleware query exception
     */
    Bibref getBibliographicReferenceByID(Integer id) throws MiddlewareQueryException;

    /**
     * Inserts a single {@code Bibref} (Bibliographic Reference) object into the
     * database.
     *
     * @param bibref - The {@code Bibref} object to be persisted to the database.
     * Must be a valid {@code Bibref} object.
     * @return Returns the id of the {@code Bibref} record inserted in the
     * database.
     * @throws MiddlewareQueryException the middleware query exception
     */
    Integer addBibliographicReference(Bibref bibref) throws MiddlewareQueryException;


    /**
     * Stores in the database the given valid Attribute object.
     *
     * @param attribute the attribute
     * @return the id of {@code Attribute} records stored in the database
     * @throws MiddlewareQueryException the middleware query exception
     */
    Integer addGermplasmAttribute(Attribute attribute) throws MiddlewareQueryException;

    /**
     * Stores in the database all the given valid Attributes object contained in
     * the parameter.
     *
     * @param attributes - List of Attribute objects
     * @return the ids of the Attribute records stored in the database
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Integer> addGermplasmAttribute(List<Attribute> attributes) throws MiddlewareQueryException;

    /**
     * Given a valid Attribute object, update the corresponding record in the
     * database.
     *
     * @param attribute the attribute
     * @return Returns the id of the updated Germplasm {@code Attribute} record
     * @throws MiddlewareQueryException the middleware query exception
     */
    Integer updateGermplasmAttribute(Attribute attribute) throws MiddlewareQueryException;

    /**
     * Given a List of valid Attribute objects, update their corresponding
     * records in the database.
     *
     * @param attributes - List of Attribute objects
     * @return Returns the ids of the updated Germplasm {@code Attribute} record
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Integer> updateGermplasmAttribute(List<Attribute> attributes) throws MiddlewareQueryException;

    /**
     * Returns the attribute record identified by the given id.
     *
     * @param id the id
     * @return The attribute record corresponding to the given id.
     * @throws MiddlewareQueryException the middleware query exception
     */
    Attribute getAttributeById(Integer id) throws MiddlewareQueryException;

    /**
     * Given the gid of the child germplasm, the gid of the parent germplasm and
     * the progenitor number, this method makes the necessary changes to save
     * the relationship on the database.
     * 
     * This method will either update the Germplasm record, to change the gpid1
     * or gpid2 fields (if the progenitor number given is 1 or 2), or will
     * either add or update the Progenitor record which represents this
     * relationship. A new Progenitor record will be stored when necessary.
     *
     * @param gid the gid
     * @param progenitorId the progenitor id
     * @param progenitorNumber the progenitor number
     * @return Returns the id of the updated Progenitor
     * @throws MiddlewareQueryException the middleware query exception
     */
    Integer updateProgenitor(Integer gid, Integer progenitorId, Integer progenitorNumber) throws MiddlewareQueryException;

    /**
     * Given a valid Germplasm object, update the corresponding record in the
     * database.
     *
     * @param germplasm the germplasm
     * @return Returns the id of the updated {@code Germplasm} record
     * @throws MiddlewareQueryException the middleware query exception
     */
    Integer updateGermplasm(Germplasm germplasm) throws MiddlewareQueryException;

    /**
     * Given a List of valid Germplasm objects, update the corresponding records
     * in the database.
     *
     * @param germplasms the germplasms
     * @return Returns the ids of the updated {@code Germplasm} records
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Integer> updateGermplasm(List<Germplasm> germplasms) throws MiddlewareQueryException;

    /**
     * Given a valid Germplasm object with a matching valid Name object to be
     * set as its preferred name, add a new Germplasm record and a new Name
     * record for the given parameters.
     *
     * @param germplasm the germplasm
     * @param preferredName the preferred name
     * @return the id of the {@code Germplasm} record added
     * @throws MiddlewareQueryException the middleware query exception
     */
    Integer addGermplasm(Germplasm germplasm, Name preferredName) throws MiddlewareQueryException;

    /**
     * Given a map of valid Germplasm and Name objects, add new records for the
     * given parameters.
     * 
     * The Name objects matching each Germplasm object in the map will be set as
     * the preferred name of the Germplasm objects.
     * 
     * Note that you need to assign temporary ids for the Germplasm objects so
     * that they can serve as the keys for the Map. The function will replace
     * these temp ids with the correct ones for storing in the database.
     *
     * @param germplasmNameMap the germplasm name map
     * @return the ids of the {@code Germplasm} records added
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Integer> addGermplasm(Map<Germplasm, Name> germplasmNameMap) throws MiddlewareQueryException;
    
    /**
     * Given a UserDefinedField object, add new record for the
     * given parameter.
     *
     * @param field - the UserDefinedField object
     * @return the id of the new UserDefinedField record added
     * @throws MiddlewareQueryException the middleware query exception
     */
    Integer addUserDefinedField(UserDefinedField field) throws MiddlewareQueryException;
    
    /**
     * Given a list of UserDefinedField objects, add new records for the
     * given parameter.
     *
     * @param fields - the list of UserDefinedField objects
     * @return the list of ids of the new UserDefinedField records added
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Integer> addUserDefinedFields(List<UserDefinedField> fields) throws MiddlewareQueryException;
    
    /**
     * Given a Attribute object, add new record for the
     * given parameter.
     *
     * @param attr - the Attribute object
     * @return the id of the new Attribute record added
     * @throws MiddlewareQueryException the middleware query exception
     */
    Integer addAttribute(Attribute attr) throws MiddlewareQueryException;
    
    /**
     * Given a list of Attribute objects, add new records for the
     * given parameter.
     *
     * @param attrs - the list of Attribute objects
     * @return the id of the new Attribute record added
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Integer> addAttributes(List<Attribute> attrs) throws MiddlewareQueryException;
    
    /**
     * Gets the germplasm Id and name Id from the names table with the given germplasm names.
     *
     * @param germplasmNames the germplasm names
     * @param mode the mode
     * @return List of GidNidElement based on the specified list of germplasm names
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<GermplasmNameDetails> getGermplasmNameDetailsByGermplasmNames(List<String> germplasmNames, GetGermplasmByNameModes mode) throws MiddlewareQueryException;
    
    
    /**
     * Please use LocationDataManager.getAllBreedingLocations().
     * 
     * Get all breeding locations.
     * 
     * Return a List of Locations which represent the breeding locations stored in the location table of IBDB.
     *
     * @return the all breeding locations
     * @throws MiddlewareQueryException the middleware query exception
     */
    @Deprecated
    List<Location> getAllBreedingLocations() throws MiddlewareQueryException;
        
    /**
     * Returns the String representation of next available sequence number for
     * Germplasm Names with given prefix. Queries both Database.LOCAL and Database.CENTRAL
     * and returns the greater number.
     *
     * @param prefix - String used as prefix for Germplasm Names querying
     * @return next available sequence number for a germplasm with given prefix.
     * @throws MiddlewareQueryException the middleware query exception
     */
    String getNextSequenceNumberForCrossName(String prefix) throws MiddlewareQueryException;

    /**
     * Returns a Map of GIDs to preferred ids given a list of GIDs.
     *
     * @param gids the gids
     * @return the preffered ids by gi ds
     * @throws MiddlewareQueryException the middleware query exception
     */
    Map<Integer, String> getPrefferedIdsByGIDs(List<Integer> gids) throws MiddlewareQueryException;

    /**
     * Given the germplasm name and a location ID, returns list of all germplasm with specified name and location id.
     *
     * @param name - search string for the name of the germplasm
     * @param locationID the location id
     * @return List of Germplasm POJOs
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Germplasm> getGermplasmByLocationId(String name, int locationID) throws MiddlewareQueryException;
    
    /**
     * Given a gid, return the Germplasm POJO representing the record identified
     * by the id with its method type.
     *
     * @param gid - the id of the germplasm record to be retrieved
     * @return the Germplasm POJO representing the record
     * @throws MiddlewareQueryException the middleware query exception
     */
    Germplasm getGermplasmWithMethodType(Integer gid) throws MiddlewareQueryException;
    
    /**
     * Given a range of gid, return the list of all Germplasm.
     *
     * @param startGID - the start ID of the range of germplasm gids
     * @param endGID - the end ID of the range of germplasm gids
     * @return List of Germplasm POJOs
     * @throws MiddlewareQueryException the middleware query exception
     */ 
    List<Germplasm> getGermplasmByGidRange(int startGID, int endGID) throws MiddlewareQueryException;
    
    /**
     * Given a List of GIDs, return the list of all Germplasm.
     *
     * @param gids the gids
     * @return the germplasms
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Germplasm> getGermplasms(List<Integer> gids) throws MiddlewareQueryException;
    
    /**
     * Given a List of GIDs, return the list of all Germplasm together with their PreferredName.
     *
     * @param gids the gids
     * @return the preferred names by gids
     * @throws MiddlewareQueryException the middleware query exception
     */
    Map<Integer, String> getPreferredNamesByGids (List<Integer> gids) throws MiddlewareQueryException;
    
    /**
     * Given a List of GIDs, return the list of gids mapped to their corresponding location name.
     *
     * @param gids the gids
     * @return Map<Integer, String>
     * - map of gids to their corresponding location name
     * @throws MiddlewareQueryException the middleware query exception
     */
    Map<Integer, String> getLocationNamesByGids (List<Integer> gids) throws MiddlewareQueryException;

    
    /**
     * Search for germplasms given a search term Q.
     *
     * @param q - the search term to be used in retrieving the germplasm
     * @param o - the operation to be used for the query (equal or like)
     * @param includeParents - boolean flag to denote whether parents will be included in search results
     * @return - List of germplasms (including parents (level 1) with gid=Q or name like Q or in list name like Q
     * Given a List of GIDs, return the list of gids mapped to their corresponding location
     * Map<Integer, String>
     * - map of gids to their corresponding location name
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Germplasm> searchForGermplasm(String q, Operation o, boolean includeParents) throws MiddlewareQueryException;

    
    /**
     * Search for germplasms given a search term Q.
     *
     * @param q - the search term to be used in retrieving the germplasm
     * @param o - the operation to be used for the query (equal or like)
     * @param includeParents - boolean flag to denote whether parents will be included in search results
     * @param searchPublicData flag to indicate whether public (central) data should be searched
     * @return - List of germplasms (including parents (level 1) with gid=Q or name like Q or in list name like Q
     * Given a List of GIDs, return the list of gids mapped to their corresponding location
     * Map<Integer, String>
     * - map of gids to their corresponding location name
     * @throws MiddlewareQueryException the middleware query exception
     */
    @Deprecated
    List<Germplasm> searchForGermplasm(String q, Operation o, boolean includeParents, boolean searchPublicData) throws MiddlewareQueryException;

    /**
     * Please use LocationDataManager.getLocationsByIDs().
     * 
     * Gets the locations by the given IDs.
     *
     * @param ids Location IDs
     * @return the corresponding Locations
     * @throws MiddlewareQueryException the middleware query exception
     */
    @Deprecated
    List<Location> getLocationsByIDs(List<Integer> ids) throws  MiddlewareQueryException;

    /**
     * Gets the methods by IDs.
     *
     * @param ids the Method Ids
     * @return the methods corresponding to the given IDs
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Method> getMethodsByIDs(List<Integer> ids) throws MiddlewareQueryException;
    
    /**
     * Get gDates given GIDs.
     *
     * @param gids the gids
     * @return <gid, integerdatevalue>
     * @throws MiddlewareQueryException the middleware query exception
     */
    Map<Integer, Integer> getGermplasmDatesByGids(List<Integer> gids) throws MiddlewareQueryException;

    /**
     * Get methods given GIDs.
     *
     * @param gids the gids
     * @return Map<gid, method>
     * @throws MiddlewareQueryException the middleware query exception
     */
    Map<Integer, Object> getMethodsByGids(List<Integer> gids) throws MiddlewareQueryException;
    
    /**
     * Gets the method by code.
     *
     * @param code the code
     * @param programUUID - uniqueId of the current program 
     * @return the method by code
     * @throws MiddlewareQueryException the middleware query exception
     */
    Method getMethodByCode(String code, String programUUID) throws MiddlewareQueryException;
    
    /**
     * Gets the method by code.
     *
     * @param code the code
     * @return the method by code
     * @throws MiddlewareQueryException the middleware query exception
     */
    Method getMethodByCode(String code) throws MiddlewareQueryException;
    
    /**
     * Gets the method by name.
     *
     * @param code the code
     * @return the method by name
     * @throws MiddlewareQueryException the middleware query exception
     */
    Method getMethodByName(String name) throws MiddlewareQueryException;
    
    /**
     * Gets the method by name.
     *
     * @param code the code
     * @param programUUID - uniqueID of the current program
     * @return the method by name
     * @throws MiddlewareQueryException the middleware query exception
     */
    Method getMethodByName(String name, String programUUID) throws MiddlewareQueryException;
    
    List<Germplasm> getProgenitorsByGIDWithPrefName(Integer gid) throws MiddlewareQueryException;
    
    /**
     * Gets the list of favorite methods/locations
     *
     * @param type - can be FavoriteType.METHOD or FavoriteType.LOCATION
     * @param programUUID - unique id of the program where the favorites location/method were created 
     * @return list of ProgramFavorite
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<ProgramFavorite> getProgramFavorites(ProgramFavorite.FavoriteType type, String programUUID) throws MiddlewareQueryException;
    
    
    /**
     * Gets the list of favorite methods/locations
     *
     * @param type - can be FavoriteType.METHOD or FavoriteType.LOCATION
     * @param max - maximum number of records to return
     * @param programUUID - unique id of the program where the favorites location/method were created
     * @return list of ProgramFavorite
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<ProgramFavorite> getProgramFavorites(ProgramFavorite.FavoriteType type, int max, String programUUID) throws MiddlewareQueryException;
    
    /**
     * count favorite methods/locations
     *
     * @param type - can be FavoriteType.METHOD or FavoriteType.LOCATION
     * @return count of ProgramFavorite list
     * @throws MiddlewareQueryException the middleware query exception
     */
    int countProgramFavorites(ProgramFavorite.FavoriteType type) throws MiddlewareQueryException;
    
    
    /**
     * Saves the list of favorite methods/locations
     *
     * @param list of ProgramFavorite
     * @return none
     * @throws MiddlewareQueryException the middleware query exception
     */
    void saveProgramFavorites(List<ProgramFavorite> list) throws MiddlewareQueryException;
    
    /**
     * Saves a favorite method/location
     *
     * @param ProgramFavorite to be saved
     * @return none
     * @throws MiddlewareQueryException the middleware query exception
     */
    void saveProgramFavorite(ProgramFavorite favorite) throws MiddlewareQueryException;
    
    
    /**
     * Deletes a list of favorite methods/locations
     *
     * @param list of ProgramFavorite
     * @return none
     * @throws MiddlewareQueryException the middleware query exception
     */
    void deleteProgramFavorites(List<ProgramFavorite> list) throws MiddlewareQueryException;
    
    /**
     * Deletes a favorite method/location
     *
     * @param code the code
     * @return none
     * @throws MiddlewareQueryException the middleware query exception
     */
    void deleteProgramFavorite(ProgramFavorite favorite) throws MiddlewareQueryException;

    /**
     * Returns the maximum number in the sequence.
     * @param prefix
     * @param suffix
     * @param nameType
     * @return
     * @throws MiddlewareQueryException
     */
    int getMaximumSequence(boolean isBulk, String prefix, String suffix, int count) throws MiddlewareQueryException; 
    
    /**
     * check if name and standardized version of it already exists. 
     * @param prefix
     * @param count
     * @param suffix
     * @return
     * @throws MiddlewareQueryException
     */
    boolean checkIfMatches(String name) throws MiddlewareQueryException;
    
    /**
     * get all method records filtered by programUUID
     * @param programUUID
     * @return list of mid
     */
    List<Method> getProgramMethods(String programUUID) throws MiddlewareQueryException;
    
    /**
     * delete all method records filtered by programUUID
     * @param programUUID
     */
    void deleteProgramMethodsByUniqueId(String programUUID) throws MiddlewareQueryException;

    /**
     * get the Germplasm from the crop database based on local gid reference 
     * @param lgid
     */
	Germplasm getGermplasmByLocalGid(Integer lgid) throws MiddlewareQueryException;	
}
