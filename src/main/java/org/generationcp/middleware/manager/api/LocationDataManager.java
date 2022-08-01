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
	 * Returns all Location information from central and local databases.
	 *
	 * @return All Locations
	 * @ the middleware query exception
	 */
	List<Location> getAllLocations();

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

	List<Location> getAllBreedingLocations(List<Integer> locationIds);

	List<Location> getAllSeedingLocations(List<Integer> locationIds);

	/**
	 * Returns location ID of unspecified location
	 *
	 * @return the location ID of unspecified lication
	 */
	String retrieveLocIdOfUnspecifiedLocation();

	List<Locdes> getLocdes(List<Integer> locIds, List<String> dvals);
}
