/*******************************************************************************
 * Copyright (c) 2014, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.middleware.manager.api;

import org.generationcp.middleware.domain.fieldbook.FieldmapBlockInfo;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.LocationDetails;
import org.generationcp.middleware.pojos.LocationType;
import org.generationcp.middleware.pojos.Locdes;
import org.generationcp.middleware.pojos.UDTableType;
import org.generationcp.middleware.pojos.UserDefinedField;

import java.util.List;
import java.util.Map;

/**
 * This is the API for managing Location information.
 *
 * @author Joyce Avestro
 */
public interface LocationDataManager {

	/**
	 * Returns the germplasm records that were created at the locations with
	 * names matching the given parameter.
	 *
	 * @param locationId - location id to search details from
	 * @param start      - the starting index of the sublist of results to be returned
	 * @param numOfRows  - the number of rows to be included in the sublist of results
	 *                   to be returned
	 * @return List of Germplasm POJOs
	 */
	List<LocationDetails> getLocationDetailsByLocId(Integer locationId, int start, int numOfRows);

	/**
	 * Returns all Location information from central and local databases.
	 *
	 * @return All Locations
	 * @ the middleware query exception
	 */
	List<Location> getAllLocations();

	/**
	 * Returns number of all Locations.
	 *
	 * @return the number of all Locations
	 */
	long countAllLocations();

	/**
	 * Returns the Location records with names matching the given parameter.
	 *
	 * @param name - search string for the name of the locations
	 * @param op   - can be EQUAL like LIKE
	 * @return List of Location POJOs
	 */
	List<Location> getLocationsByName(String name, Operation op);

	/**
	 * Returns the Location records with names matching the given parameter.
	 *
	 * @param name      - search string for the name of the locations
	 * @param start     - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results
	 *                  to be returned
	 * @param op        - can be EQUAL like LIKE
	 * @return List of Location POJOs
	 */
	List<Location> getLocationsByName(String name, int start, int numOfRows, Operation op);

	/**
	 * Returns the number of Locations with names matching the given parameter.
	 *
	 * @param name - search string for the name of the locations
	 * @param op   - can be EQUAL like LIKE
	 * @return Number of Locations
	 */
	long countLocationsByName(String name, Operation op);

	/**
	 * Returns all country records.
	 *
	 * @return List of Location POJOs
	 * @ the middleware query exception
	 */
	List<Country> getAllCountry();

	/**
	 * Returns all the Location records with country matching the given
	 * parameter. The data is retrieved from both local and central databases.
	 *
	 * @param country - search string for the country of the locations
	 * @return List of Location POJOs
	 */
	List<Location> getLocationsByCountry(Country country);

	/**
	 * Returns all the Location records with country and location type matching
	 * the given parameter. The data is retrieved from both local and central
	 * databases.
	 *
	 * @param country - search string for the country of the locations
	 * @param type    - search string for the locations type
	 * @return List of Location POJOs
	 */
	List<Location> getLocationsByCountryAndType(Country country, Integer type);

	/**
	 * Returns all the Location records with name, country and location type
	 * matching the given parameter. The data is retrieved from both local and
	 * central databases.
	 *
	 * @param name    the name
	 * @param country the country
	 * @param type    the type
	 * @return the locations by name country and type
	 */
	List<Location> getLocationsByNameCountryAndType(String name, Country country, Integer type);

	/**
	 * Returns the Location records with country matching the given parameter.
	 * The data is retrieved from both local and central databases.
	 *
	 * @param country   - search string for the country of the locations
	 * @param start     - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results
	 *                  to be returned
	 * @return List of Location POJOs
	 */
	List<Location> getLocationsByCountry(Country country, int start, int numOfRows);

	/**
	 * Returns the number of Locations with countries matching the given
	 * parameter. The data is retrieved from both local and central databases.
	 *
	 * @param country - search string for the country of the locations
	 * @return Number of Locations
	 * @ the middleware query exception
	 */
	long countLocationsByCountry(Country country);

	/**
	 * Returns the Location records with type matching the given parameter. The
	 * data is retrieved from both local and central databases.
	 *
	 * @param type        - search string for the type of the locations
	 * @return List of Location POJOs
	 */
	List<Location> getLocationsByType(Integer type);

	/**
	 * Returns the Location records with type matching the given parameter. The
	 * data is retrieved from both local and central databases.
	 *
	 * @param type      - search string for the type of the locations
	 * @param start     - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results
	 *                  to be returned
	 * @return List of Location POJOs
	 */
	List<Location> getLocationsByType(Integer type, int start, int numOfRows);

	/**
	 * Returns the number of Locations with types matching the given parameter.
	 * The data is retrieved from both local and central databases.
	 *
	 * @param type - search string for the type of the locations
	 * @return Number of Locations
	 */
	long countLocationsByType(Integer type);

	/**
	 * Returns the udfld record identified by the given id.
	 *
	 * @param id - the id of the udfld record
	 * @return the Udflds POJO representing the record
	 */
	UserDefinedField getUserDefinedFieldByID(Integer id);

	/**
	 * Returns the Map representation of <Code, UserDefinedField> of the given
	 * tableType (ftable and ftype from the udflds table).
	 *
	 * @param tableType the table type
	 * @return the user defined field map of code by ud table type
	 */
	Map<String, UserDefinedField> getUserDefinedFieldMapOfCodeByUDTableType(UDTableType tableType);

	/**
	 * Gets the user defined field id of code.
	 *
	 * @param tableType the table type
	 * @param code      the code
	 * @return the user defined field id of code
	 */
	Integer getUserDefinedFieldIdOfCode(UDTableType tableType, String code);

	/**
	 * Returns the udfld records identified by the given tablename.
	 *
	 * @param tableName - the value of the ftable record
	 * @param fieldType - the value of the ftype record
	 * @return the Udflds POJO representing the record
	 */
	List<UserDefinedField> getUserDefinedFieldByFieldTableNameAndType(String tableName, String fieldType);

	/**
	 * Returns the country record identified by the given id.
	 *
	 * @param id - id of the country record
	 * @return the Country POJO representing the record
	 */
	Country getCountryById(Integer id);

	/**
	 * Returns the location record identified by the given id.
	 *
	 * @param id - id of the location record
	 * @return the Location POJO representing the record
	 */
	Location getLocationByID(Integer id);

	/**
	 * Inserts a single {@code Location} object into the database.
	 *
	 * @param location - The {@code Location} object to be persisted to the database.
	 *                 Must be a valid {@code Location} object.
	 * @return Returns the id of the {@code Location} record inserted in the
	 * database. Returns the id of the newly-added Germplasm
	 * {@code Name}s.
	 */
	Integer addLocation(Location location);

	/**
	 * Inserts a single {@code Location} object into the database.
	 *
	 * @param locations - The {@code Location} object to be persisted to the database.
	 *                  Must be a valid {@code Location} object.
	 * @return Returns the ids of the {@code Location} records inserted in the
	 * database.
	 */
	List<Integer> addLocation(List<Location> locations);

	/**
	 * Adds location and locdes.
	 *
	 * @param location the location
	 * @param locdes   the locdes
	 * @return the integer
	 */
	int addLocationAndLocdes(Location location, Locdes locdes);

	/**
	 * Get all breeding locations.
	 * <p/>
	 * Return a List of Locations which represent the breeding locations stored
	 * in the location table of IBDB.
	 *
	 * @return the all breeding locations
	 */
	List<Location> getAllBreedingLocations();

	/**
	 * Count all breeding locations.
	 * <p/>
	 * Return the total count of Locations which represent the breeding
	 * locations stored in the location table of IBDB.
	 *
	 * @return the long
	 */
	Long countAllBreedingLocations();

	/**
	 * Gets the locations by the given IDs.
	 *
	 * @param ids Location IDs
	 * @return the corresponding Locations
	 */
	List<Location> getLocationsByIDs(List<Integer> ids);

	/**
	 * Gets all fields belonging to the given location.
	 *
	 * @param locationId the location id of the parent of the fields to return.
	 * @return all field locations belonging to the given parent id.
	 */
	List<Location> getAllFieldLocations(int locationId);

	/**
	 * Gets all block belonging to the given field.
	 *
	 * @param fieldId the field id of the parent of the blocks to return.
	 * @return all block locations belonging to the given parent id.
	 */
	List<Location> getAllBlockLocations(int fieldId);

	/**
	 * Gets the block information.
	 *
	 * @param blockId the block id to retrieve
	 * @return the block information
	 */
	FieldmapBlockInfo getBlockInformation(int blockId);

	/**
	 * Retrieves all location entries from both central and local where location
	 * type = FIELD.
	 *
	 * @return the all fields
	 */
	List<Location> getAllFields();

	List<Location> getAllProvincesByCountry(Integer countryId);

	List<Location> getAllProvinces();

	/**
	 * Retrieves all locdes entries from both central and local by location id
	 *
	 * @return all locdes entries by location id
	 */
	List<Locdes> getLocdesByLocId(Integer locationId);

	/**
	 * Save or update the list of locdes object
	 *
	 * @param locationId id
	 * @param locdesList
	 */
	void saveOrUpdateLocdesList(Integer locationId, List<Locdes> locdesList);

	List<Location> getAllBreedingLocations(List<Integer> locationIds);

	List<Location> getAllSeedingLocations(List<Integer> locationIds);

	List<LocationDetails> getFilteredLocationsDetails(Integer countryId, Integer locationType, String locationName);

	/**
	 * Gets the user defined field id of name.
	 *
	 * @param tableType the table type
	 * @param name      the name
	 * @return the user defined field id of code
	 */
	Integer getUserDefinedFieldIdOfName(UDTableType tableType, String name);

	/**
	 * Returns location ID of unspecified location
	 *
	 * @return the location ID of unspecified lication
	 */
	String retrieveLocIdOfUnspecifiedLocation();

	/**
	 * Returns the number of locations where the location abbreviation value equals to the locationAbbreviation parameter
	 *
	 * @param locationAbbreviation
	 * @return
	 */
	long countByLocationAbbreviation(final String locationAbbreviation);

	Location getDefaultLocationByType(LocationType locationType);

}
