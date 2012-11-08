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

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.GetGermplasmByNameModes;
import org.generationcp.middleware.manager.GermplasmNameType;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.Bibref;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmPedigreeTree;
import org.generationcp.middleware.pojos.GidNidElement;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.UserDefinedField;

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
     * @param name
     *            - search string for the name of the germplasm
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * @param mode
     *            - can be normal, spaces removed, name standardized
     * @param op
     *            - can be EQUAL OR LIKE
     * @param status
     *            - nstat of the names to be included in the search
     * @param type
     *            - name type
     * @param instance
     *            - can be Database.LOCAL or Database.CENTRAL
     * 
     * @return List of Germplasm POJOs
     * @throws MiddlewareQueryException
     */
    public List<Germplasm> getGermplasmByName(String name, int start, int numOfRows, GetGermplasmByNameModes mode, Operation op,
            Integer status, GermplasmNameType type, Database instance) throws MiddlewareQueryException;

    /**
     * Searches for all germplasm records which matches the given name. 
     * It will match records having the following names: (1) the given name as it is, 
     * (2) the name with standardization performed on it, and (3) name with spaces removed.
     * 
     * @param name
     *            - search string for the name of the germplasm
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * @return List of Germplasm POJOs
     * @throws MiddlewareQueryException
     */
    public List<Germplasm> getGermplasmByName(String name, int start, int numOfRows) throws MiddlewareQueryException;

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
     * @param name
     *            - search string for the name of the germplasm
     * @param mode
     *            - can be normal, spaces removed, name standardized
     * @param op
     *            - can be EQUAL OR LIKE
     * @param status
     *            - nstat of the names to be included in the search
     * @param type
     *            - name type
     * @param instance
     *            - can be Database.LOCAL or Database.CENTRAL
     * 
     * @return number of germplasm records
     * @throws MiddlewareQueryException
     */
    public long countGermplasmByName(String name, GetGermplasmByNameModes mode, Operation op, Integer status, GermplasmNameType type,
            Database instance) throws MiddlewareQueryException;

    /**
     * Returns the number of germplasm records with any name matching the given
     * parameter. 
     * It will count records having the following names: (1) the given name as it is, 
     * (2) the name with standardization performed on it, and (3) name with spaces removed.
     * 
     * @param name
     *            - search string for the name of the germplasm
     *            
     * @return number of germplasm records
     * @throws MiddlewareQueryException
     */
    public long countGermplasmByName(String name) throws MiddlewareQueryException;
    
    /**
     * Returns the germplasm records that were created at the locations with
     * names matching the given parameter.
     * 
     * @param name
     *            - search string for the name of the locations
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * @param op
     *            - can be EQUAL like LIKE
     * @param instance
     *            - can be Database.CENTRAL or Database.LOCAL
     * @return List of Germplasm POJOs
     * @throws MiddlewareQueryException
     */
    public List<Germplasm> getGermplasmByLocationName(String name, int start, int numOfRows, Operation op, Database instance)
            throws MiddlewareQueryException;

    /**
     * Returns the number of germplasm records that were created at the locations with
     * names matching the given parameter.
     * 
     * @param name
     *            - search string for the name of the locations
     * @param op
     *            - can be EQUAL like LIKE
     * @param instance
     *            - can be Database.CENTRAL or Database.LOCAL
     * @return Number of Germplasms
     * @throws MiddlewareQueryException
     */    
    public long countGermplasmByLocationName(String name, Operation op, Database instance) throws MiddlewareQueryException;

    /**
     * Returns all Locations
     * 
     * @param name
     *            - search string for the name or the locations
     * @param op
     *            - can be EQUAL or LIKE
     * @return gets all Locations
     */   
    public List<Location> getAllLocations(int start, int numOfRows) throws MiddlewareQueryException;
    
    /**
     * Returns number of all Locations
     * 
     * @return the number of all Locations
     */   
    public long countAllLocations() throws MiddlewareQueryException;

    /**
     * Returns the Location records with
     * names matching the given parameter.
     * 
     * @param name
     *            - search string for the name of the locations
     * @param op
     *            - can be EQUAL like LIKE
     * @return List of Location POJOs
     * @throws MiddlewareQueryException
     */
    public List<Location> getLocationsByName(String name, Operation op) throws MiddlewareQueryException;
    

    /**
     * Returns the Location records with
     * names matching the given parameter.
     * 
     * @param name
     *            - search string for the name of the locations
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * @param op
     *            - can be EQUAL like LIKE
     * @return List of Location POJOs
     * @throws MiddlewareQueryException
     */
    public List<Location> getLocationsByName(String name, int start, int numOfRows, Operation op) throws MiddlewareQueryException;
    
    /**
     * Returns the number of Locations with
     * names matching the given parameter.
     * 
     * @param name
     *            - search string for the name of the locations
     * @param op
     *            - can be EQUAL like LIKE
     * @return Number of Locations
     * @throws MiddlewareQueryException
     */
    public long countLocationsByName(String name, Operation op) throws MiddlewareQueryException;


    /**
     * Returns all the Location records with
     * country matching the given parameter. The data is retrieved from both local and central databases.
     * 
     * @param country
     *            - search string for the country of the locations
     * @return List of Location POJOs
     * @throws MiddlewareQueryException
     */
    public List<Location> getLocationsByCountry(Country country) throws MiddlewareQueryException;


    /**
     * Returns the Location records with
     * country matching the given parameter. The data is retrieved from both local and central databases.
     * 
     * @param country
     *            - search string for the country of the locations
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * @return List of Location POJOs
     * @throws MiddlewareQueryException
     */
    public List<Location> getLocationsByCountry(Country country, int start, int numOfRows) throws MiddlewareQueryException;
    
    /**
     * Returns the number of Locations with
     * countries matching the given parameter. The data is retrieved from both local and central databases.
     * 
     * @param country
     *            - search string for the country of the locations
     * @return Number of Locations
     * @throws MiddlewareQueryException
     */
    public long countLocationsByCountry(Country country) throws MiddlewareQueryException;
    


    /**
     * Returns the Location records with
     * type matching the given parameter. The data is retrieved from both local and central databases.
     * 
     * @param type
     *            - search string for the type of the locations
     * @return List of Location POJOs
     * @throws MiddlewareQueryException
     */
    public List<Location> getLocationsByType(Integer type) throws MiddlewareQueryException;

    /**
     * Returns the Location records with
     * type matching the given parameter. The data is retrieved from both local and central databases.
     * 
     * @param type
     *            - search string for the type of the locations
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * @return List of Location POJOs
     * @throws MiddlewareQueryException
     */
    public List<Location> getLocationsByType(Integer type, int start, int numOfRows) throws MiddlewareQueryException;
    
    /**
     * Returns the number of Locations with
     * types matching the given parameter. The data is retrieved from both local and central databases.
     * 
     * @param type
     *            - search string for the type of the locations
     * @return Number of Locations
     * @throws MiddlewareQueryException
     */
    public long countLocationsByType(Integer type) throws MiddlewareQueryException;
    
    /**
     * Returns the germplasm records that were created by the methods with names
     * matching the given parameter.
     * 
     * @param name
     *            - search string for the name of the methods
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * @param op
     *            - can be EQUAL or LIKE
     * @param instance
     *            - can be Database.CENTRAL or Database.LOCAL
     * @return List of Germplasm POJOS
     * @throws MiddlewareQueryException
     */
    public List<Germplasm> getGermplasmByMethodName(String name, int start, int numOfRows, Operation op, Database instance)
            throws MiddlewareQueryException;

    /**
     * Returns the number of germplasm records that were created by methods with
     * names matching the given parameter
     * 
     * @param name
     *            - search string for the name of the methods
     * @param op
     *            - can be equal or like
     * @param instance
     *            - can be Database.CENTRAL or Database.LOCAL
     * @return number of germplasm records
     * @throws MiddlewareQueryException
     */
    public long countGermplasmByMethodName(String name, Operation op, Database instance) throws MiddlewareQueryException;

    /**
     * Returns the germplasm record identified by the given id.
     * 
     * @param gid
     *            - id of the germplasm record to be retrieved
     * @return the Germplasm POJO representing the record
     */
    public Germplasm getGermplasmByGID(Integer gid);

    /**
     * Given a gid, return the Germplasm POJO representing the record identified
     * by the id with its preferred name.
     * 
     * @param gid
     *            - the id of the germplasm record to be retrieved
     * @return the Germplasm POJO representing the record
     */
    public Germplasm getGermplasmWithPrefName(Integer gid) throws MiddlewareQueryException;

    /**
     * Given a gid, return the Germplasm POJO representing the record identified
     * by the id with its preferred name and preferred abbreviation.
     * 
     * @param gid
     *            - the id of the germplasm record to be retrieved
     * @return the Germplasm POJO representing the record
     */
    public Germplasm getGermplasmWithPrefAbbrev(Integer gid) throws MiddlewareQueryException;

    /**
     * Returns the Name record identified by the given id.
     * 
     * @param id
     *            - id of the name record
     * @return the Name POJO representing the record
     */
    public Name getGermplasmNameByID(Integer id);

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
     * @param gid
     *            - id of the Germplasm
     * @param status
     *            - may be used to filter the results
     * @param type
     *            - may be used to filter the results
     * @return List of Name POJOs
     */
    public List<Name> getNamesByGID(Integer gid, Integer status, GermplasmNameType type) throws MiddlewareQueryException;

    /**
     * Returns the preferred name of the Germplasm identified by the gid
     * parameter.
     * 
     * @param gid
     *            - id of the Germplasm
     * @return {@code Name} POJO of the Germplasm's preferred name. Returns
     *         {@code null} when no preferred name is found.
     * @throws MiddlewareQueryException
     */
    public Name getPreferredNameByGID(Integer gid) throws MiddlewareQueryException;

    /**
     * Returns the preferred abbreviation of the Germplasm identified by the gid
     * parameter.
     * 
     * @param gid
     *            - id of the Germplasm
     * @return {@code Name} POJO of the Germplasm's preferred abbreviation.
     *         Returns {@code null} when no preferred abbreviation is found.
     * @throws MiddlewareQueryException
     */
    public Name getPreferredAbbrevByGID(Integer gid) throws MiddlewareQueryException;

    /**
     * Returns the matching {@code Name} object given a Germplasm ID and a Name
     * value.
     * 
     * @param gid
     *            - id of the Germplasm
     * @param nval
     *            - value of the Name to search
     * @return {@code Name} POJO of the matching {@code Name} object. Returns
     *         {@code null} when no {@code Name} with the specified gid and nval
     *         is found.
     * @throws MiddlewareQueryException
     */
    public Name getNameByGIDAndNval(Integer gid, String nval) throws MiddlewareQueryException;

    /**
     * Sets the specified Name as the specified Germplasm's new preferred Name.
     * 
     * @param gid
     *            - id of the Germplasm to be updated
     * @param newPrefName
     *            - new name to set as the preferred name
     * @return Returns the id of the updated {@code Germplasm} record
     * @throws MiddlewareQueryException
     */
    public Integer updateGermplasmPrefName(Integer gid, String newPrefName) throws MiddlewareQueryException;

    /**
     * Sets the specified Abbreviation as the specified Germplasm's new
     * preferred Abbreviation.
     * 
     * @param gid
     *            - id of the Germplasm to be updated
     * @param newPrefAbbrev
     *            - new abbreviation to set as the preferred abbreviation
     * @return Returns the id of the updated {@code Germplasm} record
     * @throws MiddlewareQueryException
     */
    public Integer updateGermplasmPrefAbbrev(Integer gid, String newPrefAbbrev) throws MiddlewareQueryException;

    /**
     * Inserts a single {@code Name} object into the database.
     * 
     * @param location
     *            - The {@code Name} object to be persisted to the database.
     *            Must be a valid {@code Name} object.
     * @return Returns the id of the newly-added Germplasm {@code Name}.
     * @throws MiddlewareQueryException
     */
    public Integer addGermplasmName(Name name) throws MiddlewareQueryException;

    /**
     * Inserts a list of multiple {@code Name} objects into the database.
     * 
     * @param names
     *            - A list of {@code Name} objects to be persisted to the
     *            database. {@code Name} objects must be valid.
     * @return Returns the id of the newly-added Germplasm {@code Name}s.
     * @throws MiddlewareQueryException
     */
    public List<Integer> addGermplasmName(List<Name> names) throws MiddlewareQueryException;

    /**
     * Updates a single {@code Name} object in the database.
     * 
     * @param location
     *            - The {@code Name} object to be updated in the database. Must
     *            be a valid {@code Name} object.
     * @return Returns the id of the updated Germplasm {@code Name}.
     * @throws MiddlewareQueryException
     */
    public Integer updateGermplasmName(Name name) throws MiddlewareQueryException;

    /**
     * Updates the database with multiple {@code Name} objects specified.
     * 
     * @param names
     *            - A list of {@code Name} objects to be updated in the
     *            database. {@code Name} objects must be valid.
     * @return Returns the id of the updated Germplasm {@code Name}s.
     * @throws MiddlewareQueryException
     */
    public List<Integer> updateGermplasmName(List<Name> names) throws MiddlewareQueryException;

    /**
     * Returns all the attributes of the Germplasm identified by the given id.
     * 
     * @param gid
     *            - id of the Germplasm
     * @return List of Atributs POJOs
     */
    public List<Attribute> getAttributesByGID(Integer gid) throws MiddlewareQueryException;

    /**
     * Returns the Method record identified by the id.
     * 
     * @param id
     *            - id of the method record
     * @return the Method POJO representing the record
     */
    public Method getMethodByID(Integer id);

    /**
     * Returns all the method records.
     * 
     * @return List of Method POJOs
     * @throws MiddlewareQueryException
     */
    public List<Method> getAllMethods() throws MiddlewareQueryException;
    
    /**
     * Returns all the method records matching the given type.
     * Retrieves from both local and central databases.
     * 
     * @param type the type of the method
     * @return List of Method POJOs
     * @throws MiddlewareQueryException
     */
    public List<Method> getMethodsByType(String type) throws MiddlewareQueryException;


    
    /**
     * Returns all the method records matching the given type.
     * Retrieves from both local and central databases.
     * 
     * @param type the type of the method
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * @return List of Method POJOs
     * @throws MiddlewareQueryException
     */
    public List<Method> getMethodsByType(String type, int start, int numOfRows) throws MiddlewareQueryException;

    /**
     * Returns the number of Methods with
     * type matching the given parameter.
     * Retrieves from both local and central databases.
     * 
     * @param type
     *            - search string for the methods
     * @return Number of Methods matching the given type
     * @throws MiddlewareQueryException
     */
    public long countMethodsByType(String type) throws MiddlewareQueryException;

    /**
     * Returns all the method records matching the given group.
     * Retrieves from both local and central databases.
     * 
     * @param group the group of the method
     * @return List of Method POJOs
     * @throws MiddlewareQueryException
     */
    public List<Method> getMethodsByGroup(String group) throws MiddlewareQueryException;


    /**
     * Returns all the method records matching the given group.
     * Retrieves from both local and central databases.
     * 
     * @param type the group of the method
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * @return List of Method POJOs
     * @throws MiddlewareQueryException
     */
    public List<Method> getMethodsByGroup(String group, int start, int numOfRows) throws MiddlewareQueryException;
    /**
     * Returns all the method and type records matching the given group and type.
     * Retrieves from both local and central databases.
     * 
     * @param group the group of the method
     * @param type the type of the method
     * @return List of Method POJOs
     * @throws MiddlewareQueryException
     */
    public List<Method> getMethodsByGroupAndType(String group,String type) throws MiddlewareQueryException;
    
    /**
     * Returns the number of Methods with
     * group matching the given parameter. 
     * Retrieves from both local and central databases.
     * 
     * @param group
     *            - search string for the methods
     * @return Number of Methods matching the given group
     * @throws MiddlewareQueryException
     */
    public long countMethodsByGroup(String group) throws MiddlewareQueryException;

    /**
     * Returns the udfld record identified by the given id.
     * 
     * @param id
     *            - the id of the udfld record
     * @return the Udflds POJO representing the record
     */
    public UserDefinedField getUserDefinedFieldByID(Integer id);

    /**
     * Returns the country record identified by the given id.
     * 
     * @param id
     *            - id of the country record
     * @return the Country POJO representing the record
     */
    public Country getCountryById(Integer id);

    /**
     * Returns the location record identified by the given id.
     * 
     * @param id
     *            - id of the location record
     * @return the Location POJO representing the record
     */
    public Location getLocationByID(Integer id);

    /**
     * Inserts a single {@code Location} object into the database.
     * 
     * @param location
     *            - The {@code Location} object to be persisted to the database.
     *            Must be a valid {@code Location} object.
     * @return Returns the id of the {@code Location} record inserted in the
     *         database.
     *         Returns the id of the newly-added Germplasm {@code Name}s.
     * @throws MiddlewareQueryException
     */
    public Integer addLocation(Location location) throws MiddlewareQueryException;
    
    /**
     * Inserts a single {@code Location} object into the database.
     * 
     * @param locations
     *            - The {@code Location} object to be persisted to the database.
     *            Must be a valid {@code Location} object.
     * @return Returns the ids of the {@code Location} records inserted in the
     *         database.
     * @throws MiddlewareQueryException
     */
    public List<Integer> addLocation(List<Location> locations) throws MiddlewareQueryException;
    
    /**
     * Deletes a single {@code Location} object into the database.
     * 
     * @param location
     *            - The {@code Location} object to be deleted from the database.
     *            Must be a valid {@code Location} object.
     * @throws MiddlewareQueryException
     */
    public void deleteLocation(Location location) throws MiddlewareQueryException;

    /**
     * Inserts a single {@code Method} object into the database.
     * 
     * @param method
     *            - The {@code Method} object to be persisted to the database.
     *            Must be a valid {@code Method} object.
     * @return Returns the id of the {@code Method} record inserted in the
     *         database.
     * @throws MiddlewareQueryException
     */
    public Integer addMethod(Method method) throws MiddlewareQueryException;
    
    /**
     * Inserts a list of {@code Method} objects into the database.
     * 
     * @param methods
     *            - The list of {@code Method} objects to be persisted to the database.
     *            Must be valid {@code Method} objects.
     * @return Returns the ids of the {@code Method} records inserted in the
     *         database.
     * @throws MiddlewareQueryException
     */
    public List<Integer> addMethod(List<Method> methods) throws MiddlewareQueryException;
    
    /**
     * Deletes a single {@code Method} object into the database.
     * 
     * @param method
     *            - The {@code Method} object to be deleted from the database.
     *            Must be a valid {@code Method} object.
     * @throws MiddlewareQueryException
     */
    public void deleteMethod(Method method) throws MiddlewareQueryException;

    /**
     * Returns the Bibref record identified by the given id.
     * 
     * @param id
     *            - id of the bibref record
     * @return the Bibref POJO representing the record
     */
    public Bibref getBibliographicReferenceByID(Integer id);

    /**
     * Inserts a single {@code Bibref} (Bibliographic Reference) object into the
     * database.
     * 
     * @param location
     *            - The {@code Bibref} object to be persisted to the database.
     *            Must be a valid {@code Bibref} object.
     * @return Returns the id of the {@code Bibref} record inserted in the
     *         database.
     * @throws MiddlewareQueryException
     */
    public Integer addBibliographicReference(Bibref bibref) throws MiddlewareQueryException;

    /**
     * Returns the Germplasm representing the parent of the child Germplasm
     * identified by the given gid and having the given progenitor number.
     * 
     * @param gid
     *            - gid of child Germplasm
     * @param progenitorNumber
     *            - progenitor number of the parent with respect to the child
     * @return Germplasm POJO
     * @throws MiddlewareQueryException
     */
    public Germplasm getParentByGIDAndProgenitorNumber(Integer gid, Integer progenitorNumber) throws MiddlewareQueryException;

    /**
     * Returns the Germplasm representing the children of the Germplasm
     * identified by the given gid. The function returns a List of Object
     * arrays. Each Object array contains 2 elements, the first is an int to
     * specify the progenitor number and the second is the Germplasm POJO
     * representing the child germplasm.
     * 
     * @param gid
     *            - gid of the parent Germplasm
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * @return List of Object arrays, the arrays have 2 elements in them
     * @throws MiddlewareQueryException
     */
    public List<Object[]> getDescendants(Integer gid, int start, int numOfRows) throws MiddlewareQueryException;

    /**
     * Returns the number of children of the Germplasm identified by the given
     * gid.
     * 
     * @param gid
     * @return count of children
     * @throws MiddlewareQueryException
     */
    public long countDescendants(Integer gid) throws MiddlewareQueryException;

    /**
     * Creates a pedigree tree for the Germplasm identified by the given gid.
     * The tree contains all generative progenitors down to the specified level.
     * The Germplasm POJOs included in the tree have their preferred names
     * pre-loaded. The root of the tree is the Germplasm identified by the given
     * gid parameter. The nodes down the tree are the ancestors of the nodes
     * above them.
     * 
     * Example tree:
     * 
     * Result of calling: generatePedigreeTree(new Integer(306436), 4);
     * 
     * 306436 : TOX 494 (root node) 33208 : 63-83 (child node of root,
     * representing parent of Germplasm 306436) 2269311 : 63-83 310357 : IRAT 2
     * 96783 : IGUAPE CATETO (child node of root, representing parent of
     * Germplasm 306436) 312744 : RPCB-2B-849 (child node of root, representing
     * parent of Germplasm 306436) 2268822 : RPCB-2B-849 3160 : IR 1416-131
     * (child node of root, representing parent of Germplasm 306436) 2231 : IR
     * 1416 1163 : IR 400-28-4-5 (child node containing Germplasm 2231,
     * representing parent of Germplasm 2231) 2229 : TE TEP (child node
     * containing Germplasm 2231, representing parent of Germplasm 2231) 312646
     * : LITA 506 (child node of root, representing parent of Germplasm 306436)
     * 
     * 
     * @param gid
     *            - GID of a Germplasm
     * @param level
     *            - level of the tree to be created
     * @return GermplasmPedigreeTree representing the pedigree tree
     * @throws MiddlewareQueryException
     */
    public GermplasmPedigreeTree generatePedigreeTree(Integer gid, int level) throws MiddlewareQueryException;

    /**
     * Returns the Germplasm which are management group neighbors of the
     * Germplasm identified by the given GID. The given Germplasm is assumed to
     * be a root of a management group. The Germplasm POJOs included in the
     * results come with their preferred names which can be accessed by calling
     * Germplasm.getPreferredName().
     * 
     * @param gid
     * @return List of Germplasm POJOs
     * @throws MiddlewareQueryException
     */
    public List<Germplasm> getManagementNeighbors(Integer gid) throws MiddlewareQueryException;

    /**
     * Returns the Germplasm which are group relatives of the Germplasm
     * identified by the given GID. The Germplasm POJOs included in the results
     * come with their preferred names which can be accessed by calling
     * Germplasm.getPreferredName().
     * 
     * @param gid
     * @return List of Germplasm POJOs
     * @throws MiddlewareQueryException
     */
    public List<Germplasm> getGroupRelatives(Integer gid) throws MiddlewareQueryException;

    /**
     * Returns the generation history of the Germplasm identified by the given
     * GID. The history is created by tracing back through the source germplasms
     * from the given Germplasm through its progenitors, up until a Germplasm
     * created by a generative method is encountered. The Germplasm POJOs
     * included in the results come with their preferred names which can be
     * accessed by calling Germplasm.getPreferredName().
     * 
     * @param gid
     * @return List of Germplasm POJOs, arranged from the given Germplasm down
     *         to the last source on the generation history
     * @throws MiddlewareQueryException
     */
    public List<Germplasm> getGenerationHistory(Integer gid) throws MiddlewareQueryException;

    /**
     * Returns the GermplasmPedigreeTree object which represents the derivative
     * neighborhood for the germplasm identified by the given gid. The
     * derivative neighborhood is created by tracing back the source parents
     * from the given germplasm until the given number of steps backward is
     * reached or the first source germplasm created by a generative method is
     * reached, whichever comes first. The last source parent reached by tracing
     * back becomes the root of the GermplasmPedigreeTree object. From the root,
     * all immediate derived lines are retrieved and added to the tree. And then
     * from each of those derived germplasms, all immediate derived lines are
     * retrieved and added to the tree, and so on and so forth. The number of
     * levels of the tree is the sum of the actual number of steps backward made
     * to reach the root and the given number of steps forward plus 1 (for the
     * level which the given germplasm belongs).
     * 
     * The Germplasm POJOs included in the tree have their preferred names
     * pre-loaded.
     * 
     * @param gid
     * @param numberOfStepsBackward
     *            - number of steps backward from the germplasm identified by
     *            the given gid
     * @param numberOfStepsForward
     *            - number of steps forward from the germplasm identified by the
     *            given gid
     * @return GermplasmPedigreeTree representing the neighborhood
     */
    public GermplasmPedigreeTree getDerivativeNeighborhood(Integer gid, int numberOfStepsBackward, int numberOfStepsForward)
            throws MiddlewareQueryException;

//    /**
//     * Returns the Germplasm records with field values matching the specified
//     * values on the given sample Germplasm object. The following fields will be
//     * checked for matches: method, gnpgs, gpid1, gpid2, user, location, gdate,
//     * reference, mgid, and attributes.
//     * 
//     * @param sample
//     * @param start
//     * @param numOfRows
//     * @return List of Germplasm POJOs
//     */
    // public List<Germplasm> getGermplasmByExample(Germplasm sample, int
    // start, int numOfRows);

    /**
     * Returns the number of Germplasm records with field values matching the
     * specified values on the given sample Germplasm object. The following
     * fields will be checked for matches: method, gnpgs, gpid1, gpid2, user,
     * location, gdate, reference, mgid, and attributes.
     * 
     * @param sample
     * @return
     */
    // public long countGermplasmByExample(Germplasm sample);

    /**
     * Stores in the database the given valid Attribute object.
     * 
     * @param attribute
     * @return the id of {@code Attribute} records stored in the database
     * @throws MiddlewareQueryException
     */
    public Integer addGermplasmAttribute(Attribute attribute) throws MiddlewareQueryException;

    /**
     * Stores in the database all the given valid Attributes object contained in
     * the parameter.
     * 
     * @param attributes
     *            - List of Attribute objects
     * @return the ids of the Attribute records stored in the database
     * @throws MiddlewareQueryException
     */
    public List<Integer> addGermplasmAttribute(List<Attribute> attributes) throws MiddlewareQueryException;

    /**
     * Given a valid Attribute object, update the corresponding record in the
     * database.
     * 
     * @param attribute
     * @return Returns the id of the updated Germplasm {@code Attribute} record
     * @throws MiddlewareQueryException
     */
    public Integer updateGermplasmAttribute(Attribute attribute) throws MiddlewareQueryException;

    /**
     * Given a List of valid Attribute objects, update their corresponding
     * records in the database.
     * 
     * @param attributes
     *            - List of Attribute objects
     * @return Returns the ids of the updated Germplasm {@code Attribute} record
     * @throws MiddlewareQueryException
     */
    public List<Integer> updateGermplasmAttribute(List<Attribute> attributes) throws MiddlewareQueryException;

    /**
     * Returns the attribute record identified by the given id.
     * 
     * @param id
     * @return
     */
    public Attribute getAttributeById(Integer id);

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
     * @param gid
     * @param progenitorId
     * @param progenitorNumber
     * @return Returns the id of the updated Progenitor
     * @throws MiddlewareQueryException
     */
    public Integer updateProgenitor(Integer gid, Integer progenitorId, Integer progenitorNumber) throws MiddlewareQueryException;

    /**
     * Given a valid Germplasm object, update the corresponding record in the
     * database.
     * 
     * @param germplasm
     * @return Returns the id of the updated {@code Germplasm} record
     * @throws MiddlewareQueryException
     */
    public Integer updateGermplasm(Germplasm germplasm) throws MiddlewareQueryException;

    /**
     * Given a List of valid Germplasm objects, update the corresponding records
     * in the database.
     * 
     * @param germplasms
     * @return Returns the ids of the updated {@code Germplasm} records
     * @throws MiddlewareQueryException
     */
    public List<Integer> updateGermplasm(List<Germplasm> germplasms) throws MiddlewareQueryException;

    /**
     * Given a valid Germplasm object with a matching valid Name object to be
     * set as its preferred name, add a new Germplasm record and a new Name
     * record for the given parameters.
     * 
     * @param germplasm
     * @param preferredName
     * @return the id of the {@code Germplasm} record added
     * @throws MiddlewareQueryException
     */
    public Integer addGermplasm(Germplasm germplasm, Name preferredName) throws MiddlewareQueryException;

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
     * @param germplasmNameMap
     * @return the ids of the {@code Germplasm} records added
     * @throws MiddlewareQueryException
     */
    public List<Integer> addGermplasm(Map<Germplasm, Name> germplasmNameMap) throws MiddlewareQueryException;
    
    /**
     * Gets the germplasm Id and name Id from the names table with the given germplasm name
     *
     * @param germplasmName the germplasm name
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results to be returned
     * @return List of GidNidElement based on the specified list of germplasm names 
     * @throws MiddlewareQueryException
     */
    public List<GidNidElement> getGidAndNidByGermplasmNames(List<String> germplasmNames) throws MiddlewareQueryException;

}
