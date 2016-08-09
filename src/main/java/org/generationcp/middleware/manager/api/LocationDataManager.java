/*******************************************************************************
 * Copyright (c) 2014, All Rights Reserved.
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
import java.util.Map;

import org.generationcp.middleware.domain.fieldbook.FieldmapBlockInfo;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.LocationDetails;
import org.generationcp.middleware.pojos.Locdes;
import org.generationcp.middleware.pojos.UDTableType;
import org.generationcp.middleware.pojos.UserDefinedField;

/**
 * This is the API for managing Location information.
 *
 * @author Joyce Avestro
 *
 */
public interface LocationDataManager {

	/**
	 * Returns the germplasm records that were created at the locations with names matching the given parameter.
	 *
	 * @param locationId - location id to search details from
	 * @param start - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return List of Germplasm POJOs
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<LocationDetails> getLocationDetailsByLocId(Integer locationId, int start, int numOfRows) throws MiddlewareQueryException;

	/**
	 * Returns all Location information from central and local databases.
	 *
	 * @return All Locations
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Location> getAllLocations() throws MiddlewareQueryException;

	/**
	 * Returns all Local Locations.
	 *
	 * @param start - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return All Locations based on the given start and numOfRows
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Location> getAllLocalLocations(int start, int numOfRows) throws MiddlewareQueryException;

	/**
	 * Returns number of all Locations.
	 *
	 * @return the number of all Locations
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	long countAllLocations() throws MiddlewareQueryException;

	/**
	 * Returns the Location records with names matching the given parameter.
	 *
	 * @param programUUID - unique ID of the current program
	 * @return List of Location POJOs
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Location> getLocationsByUniqueID(String programUUID) throws MiddlewareQueryException;

	/**
	 * Returns the number of Locations with names matching the given parameter.
	 *
	 * @param name - search string for the name of the locations
	 * @param op - can be EQUAL like LIKE
	 * @return Number of Locations
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	long countLocationsByUniqueID(String programUUID) throws MiddlewareQueryException;

	/**
	 * Returns the Location records with names matching the given parameter.
	 *
	 * @param name - search string for the name of the locations
	 * @param op - can be EQUAL like LIKE
	 * @param programUUID - uniqueID of the current program
	 * @return List of Location POJOs
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Location> getLocationsByName(String name, Operation op, String programUUID) throws MiddlewareQueryException;

	/**
	 * Returns the Location records with names matching the given parameter.
	 *
	 * @param name - search string for the name of the locations
	 * @param op - can be EQUAL like LIKE
	 * @return List of Location POJOs
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Location> getLocationsByName(String name, Operation op) throws MiddlewareQueryException;

	/**
	 * Returns the Location records with names matching the given parameter.
	 *
	 * @param name - search string for the name of the locations
	 * @param start - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @param op - can be EQUAL like LIKE
	 * @param programUUID - uniqueID of the current program
	 * @return List of Location POJOs
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Location> getLocationsByName(String name, int start, int numOfRows, Operation op, String programUUID)
			throws MiddlewareQueryException;

	/**
	 * Returns the Location records with names matching the given parameter.
	 *
	 * @param name - search string for the name of the locations
	 * @param start - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @param op - can be EQUAL like LIKE
	 * @return List of Location POJOs
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Location> getLocationsByName(String name, int start, int numOfRows, Operation op) throws MiddlewareQueryException;

	/**
	 * Returns the number of Locations with names matching the given parameter.
	 *
	 * @param name - search string for the name of the locations
	 * @param op - can be EQUAL like LIKE
	 * @param programUUID - uniqueID of the current program
	 * @return Number of Locations
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	long countLocationsByName(String name, Operation op, String programUUID) throws MiddlewareQueryException;

	/**
	 * Returns the number of Locations with names matching the given parameter.
	 *
	 * @param name - search string for the name of the locations
	 * @param op - can be EQUAL like LIKE
	 * @return Number of Locations
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	long countLocationsByName(String name, Operation op) throws MiddlewareQueryException;

	/**
	 * Returns all country records.
	 *
	 * @return List of Location POJOs
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Country> getAllCountry() throws MiddlewareQueryException;

	/**
	 * Returns all the Location records with country matching the given parameter. The data is retrieved from both local and central
	 * databases.
	 *
	 * @param country - search string for the country of the locations
	 * @return List of Location POJOs
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Location> getLocationsByCountry(Country country) throws MiddlewareQueryException;

	/**
	 * Returns all the Location records with country and location type matching the given parameter. The data is retrieved from both local
	 * and central databases.
	 *
	 * @param country - search string for the country of the locations
	 * @param type - search string for the locations type
	 * @return List of Location POJOs
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Location> getLocationsByCountryAndType(Country country, Integer type) throws MiddlewareQueryException;

	/**
	 * Returns all the Location records with name, country and location type matching the given parameter. The data is retrieved from both
	 * local and central databases.
	 *
	 * @param name the name
	 * @param country the country
	 * @param type the type
	 * @return the locations by name country and type
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Location> getLocationsByNameCountryAndType(String name, Country country, Integer type) throws MiddlewareQueryException;

	/**
	 * Returns the Location records with country matching the given parameter. The data is retrieved from both local and central databases.
	 *
	 * @param country - search string for the country of the locations
	 * @param start - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return List of Location POJOs
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Location> getLocationsByCountry(Country country, int start, int numOfRows) throws MiddlewareQueryException;

	/**
	 * Returns the number of Locations with countries matching the given parameter. The data is retrieved from both local and central
	 * databases.
	 *
	 * @param country - search string for the country of the locations
	 * @return Number of Locations
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	long countLocationsByCountry(Country country) throws MiddlewareQueryException;

	/**
	 * Returns the Location records with type matching the given parameter. The data is retrieved from both local and central databases.
	 *
	 * @param type - search string for the type of the locations
	 * @return List of Location POJOs
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Location> getLocationsByType(Integer type) throws MiddlewareQueryException;

	/**
	 * Returns the Location records with type matching the given parameter. The data is retrieved from both local and central databases.
	 *
	 * @param type - search string for the type of the locations
	 * @param programUUID - unique id of the current program
	 * @return List of Location POJOs
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Location> getLocationsByType(Integer type, String programUUID) throws MiddlewareQueryException;

	/**
	 * Returns the Location records with type matching the given parameter. The data is retrieved from both local and central databases.
	 *
	 * @param type - search string for the type of the locations
	 * @param start - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return List of Location POJOs
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Location> getLocationsByType(Integer type, int start, int numOfRows) throws MiddlewareQueryException;

	/**
	 * Returns the number of Locations with types matching the given parameter. The data is retrieved from both local and central databases.
	 *
	 * @param type - search string for the type of the locations
	 * @return Number of Locations
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	long countLocationsByType(Integer type) throws MiddlewareQueryException;

	/**
	 * Returns the number of Locations with types matching the given parameter. The data is retrieved from both local and central databases.
	 *
	 * @param type - search string for the type of the locations
	 * @param programUUID - unique id of the current program
	 * @return Number of Locations
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	long countLocationsByType(Integer type, String programUUID) throws MiddlewareQueryException;

	/**
	 * Returns the udfld record identified by the given id.
	 *
	 * @param id - the id of the udfld record
	 * @return the Udflds POJO representing the record
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	UserDefinedField getUserDefinedFieldByID(Integer id) throws MiddlewareQueryException;

	/**
	 * Returns the Map representation of <Code, UserDefinedField> of the given tableType (ftable and ftype from the udflds table).
	 *
	 * @param tableType the table type
	 * @return the user defined field map of code by ud table type
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Map<String, UserDefinedField> getUserDefinedFieldMapOfCodeByUDTableType(UDTableType tableType) throws MiddlewareQueryException;

	/**
	 * Gets the user defined field id of code.
	 *
	 * @param tableType the table type
	 * @param code the code
	 * @return the user defined field id of code
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Integer getUserDefinedFieldIdOfCode(UDTableType tableType, String code) throws MiddlewareQueryException;

	/**
	 * Returns the udfld records identified by the given tablename.
	 *
	 * @param tableName - the value of the ftable record
	 * @param fieldType - the value of the ftype record
	 * @return the Udflds POJO representing the record
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<UserDefinedField> getUserDefinedFieldByFieldTableNameAndType(String tableName, String fieldType) throws MiddlewareQueryException;

	/**
	 * Returns the country record identified by the given id.
	 *
	 * @param id - id of the country record
	 * @return the Country POJO representing the record
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Country getCountryById(Integer id) throws MiddlewareQueryException;

	/**
	 * Returns the location record identified by the given id.
	 *
	 * @param id - id of the location record
	 * @return the Location POJO representing the record
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Location getLocationByID(Integer id) throws MiddlewareQueryException;

	/**
	 * Inserts a single {@code Location} object into the database.
	 *
	 * @param location - The {@code Location} object to be persisted to the database. Must be a valid {@code Location} object.
	 * @return Returns the id of the {@code Location} record inserted in the database. Returns the id of the newly-added Germplasm
	 *         {@code Name}s.
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Integer addLocation(Location location) throws MiddlewareQueryException;

	/**
	 * Inserts a single {@code Location} object into the database.
	 *
	 * @param locations - The {@code Location} object to be persisted to the database. Must be a valid {@code Location} object.
	 * @return Returns the ids of the {@code Location} records inserted in the database.
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Integer> addLocation(List<Location> locations) throws MiddlewareQueryException;

	/**
	 * Adds location and locdes.
	 *
	 * @param location the location
	 * @param locdes the locdes
	 * @return the integer
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	int addLocationAndLocdes(Location location, Locdes locdes) throws MiddlewareQueryException;

	/**
	 * Deletes a single {@code Location} object into the database.
	 *
	 * @param location - The {@code Location} object to be deleted from the database. Must be a valid {@code Location} object.
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	void deleteLocation(Location location) throws MiddlewareQueryException;

	/**
	 * Get all breeding locations.
	 *
	 * Return a List of Locations which represent the breeding locations stored in the location table of IBDB.
	 *
	 * @return the all breeding locations
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Location> getAllBreedingLocations() throws MiddlewareQueryException;

	/**
	 * Count all breeding locations.
	 *
	 * Return the total count of Locations which represent the breeding locations stored in the location table of IBDB.
	 *
	 * @return the long
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Long countAllBreedingLocations() throws MiddlewareQueryException;

	/**
	 * Gets the locations by the given IDs.
	 *
	 * @param ids Location IDs
	 * @return the corresponding Locations
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Location> getLocationsByIDs(List<Integer> ids) throws MiddlewareQueryException;

	/**
	 * Gets all fields belonging to the given location.
	 *
	 * @param locationId the location id of the parent of the fields to return.
	 * @return all field locations belonging to the given parent id.
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Location> getAllFieldLocations(int locationId) throws MiddlewareQueryException;

	/**
	 * Gets all block belonging to the given field.
	 *
	 * @param fieldId the field id of the parent of the blocks to return.
	 * @return all block locations belonging to the given parent id.
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Location> getAllBlockLocations(int fieldId) throws MiddlewareQueryException;

	/**
	 * Gets the block information.
	 *
	 * @param blockId the block id to retrieve
	 * @return the block information
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	FieldmapBlockInfo getBlockInformation(int blockId) throws MiddlewareQueryException;

	/**
	 * Retrieves all location entries from both central and local where location type = FIELD.
	 *
	 * @return the all fields
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Location> getAllFields() throws MiddlewareQueryException;

	List<Location> getAllProvincesByCountry(Integer countryId) throws MiddlewareQueryException;

	List<Location> getAllProvinces() throws MiddlewareQueryException;

	/**
	 * get all location records filtered by programUUID
	 *
	 * @param programUUID
	 * @return list of locid
	 */
	List<Location> getProgramLocations(String programUUID) throws MiddlewareQueryException;

	/**
	 * delete all location records filtered by programUUID
	 *
	 * @param programUUID
	 */
	void deleteProgramLocationsByUniqueId(String programUUID) throws MiddlewareQueryException;

	/**
	 * Retrieves all locdes entries from both central and local by location id
	 *
	 * @return all locdes entries by location id
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Locdes> getLocdesByLocId(Integer locationId) throws MiddlewareQueryException;

	/**
	 * Save or update the list of locdes object
	 *
	 * @param locationId id
	 * @param locdesList
	 * @throws MiddlewareQueryException
	 */
	void saveOrUpdateLocdesList(Integer locationId, List<Locdes> locdesList) throws MiddlewareQueryException;

	/**
	 * get all seeding location records filtered by programUUID
	 *
	 * @param programUUID
	 */
	public List<Location> getAllSeedingLocations(String programUUID);

	public List<Location> getAllBreedingLocations(List<Integer> locationIds);

	public List<Location> getAllSeedingLocations(List<Integer> locationIds);

	List<LocationDetails> getFilteredLocations(Integer countryId, Integer locationType, String locationName, String programUUID);

}
