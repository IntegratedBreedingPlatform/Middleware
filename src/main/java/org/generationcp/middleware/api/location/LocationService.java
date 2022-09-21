package org.generationcp.middleware.api.location;

import org.generationcp.middleware.api.location.search.LocationSearchRequest;
import org.generationcp.middleware.api.program.ProgramBasicDetailsDto;
import org.generationcp.middleware.domain.fieldbook.FieldmapBlockInfo;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.Locdes;
import org.generationcp.middleware.pojos.ProgramLocationDefault;
import org.generationcp.middleware.pojos.UDTableType;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LocationService {

	LocationDTO getLocation(Integer locationId);

	List<LocationTypeDTO> getLocationTypes();

	/**
	 * Returns the Location records filtered by LocationSearchRequest parameter.
	 *
	 * @param locationSearchRequest - filter parameters
	 * @param pageable              - pagination parameters
	 * @param programUUID
	 * @return
	 */
	List<LocationDTO> searchLocations(LocationSearchRequest locationSearchRequest, Pageable pageable,
			final String programUUID);

	/**
	 * Returns the count of Location records filtered by LocationSearchRequest parameter.
	 *
	 * @param locationSearchRequest - filter parameters
	 * @param programUUID
   * @return
	 */
	long countFilteredLocations(LocationSearchRequest locationSearchRequest, String programUUID);

	List<org.generationcp.middleware.api.location.Location> getLocations(LocationSearchRequest locationSearchRequest, Pageable pageable);

	void deleteLocation(Integer locationId);

	LocationDTO createLocation(LocationRequestDto locationRequestDto);

	void updateLocation(Integer locationId, LocationRequestDto locationRequestDto);

	boolean isDefaultCountryLocation(Integer locationId);

	boolean blockIdIsUsedInFieldMap(List<Integer> blockIds);

	List<LocationDTO> getCountries();

	/**
	 * Delete given block locations. Also delete field location (block parent)
	 * if no other blocks uses particular field as parent.
	 *
	 * @param blockLocIds
	 */
	void deleteBlockFieldLocationByBlockId (List<Integer> blockLocIds);

	ProgramLocationDefault saveProgramLocationDefault(String programUUID, Integer breedingLocationId, Integer storageLocationId);

	void updateProgramLocationDefault(String programUUID, ProgramBasicDetailsDto programBasicDetailsDto);

	ProgramLocationDefault getProgramLocationDefault(String programUUID);

	LocationDTO getDefaultBreedingLocation(String programUUID);

	LocationDTO getDefaultStorageLocation(String programUUID);

	boolean isProgramBreedingLocationDefault(Integer locationId);

	boolean isProgramStorageLocationDefault(Integer locationId);

	/**
	 * Please, use {@link LocationService#searchLocations(LocationSearchRequest, Pageable, String)}
	 *
	 * Returns all Location information from central and local databases.
	 *
	 * @return All Locations
	 * @ the middleware query exception
	 */
	@Deprecated
	List<org.generationcp.middleware.pojos.Location> getAllLocations();

	/**
	 * Please, use {@link LocationService#searchLocations(LocationSearchRequest, Pageable, String)}
	 *
	 * Gets the locations by the given IDs.
	 *
	 * @param ids Location IDs
	 * @return the corresponding Locations
	 */
	@Deprecated
	List<org.generationcp.middleware.pojos.Location> getLocationsByIDs(List<Integer> ids);

	/**
	 * Please, use {@link LocationService#searchLocations(LocationSearchRequest, Pageable, String)}
	 *
	 * Returns the Location records with names matching the given parameter.
	 *
	 * @param name - search string for the name of the locations
	 * @param op   - can be EQUAL like LIKE
	 * @return List of Location POJOs
	 */
	@Deprecated
	List<org.generationcp.middleware.pojos.Location> getLocationsByName(String name, Operation op);

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
	List<org.generationcp.middleware.pojos.Location> getLocationsByName(String name, int start, int numOfRows, Operation op);

	/**
	 * Returns location ID of unspecified location
	 *
	 * @return the location ID of unspecified lication
	 */
	String retrieveLocIdOfUnspecifiedLocation();

	/**
	 * Returns the location record identified by the given id.
	 *
	 * @param id - id of the location record
	 * @return the Location POJO representing the record
	 */
	org.generationcp.middleware.pojos.Location getLocationByID(Integer id);

	/**
	 * Adds location and locdes.
	 *
	 * @param location the location
	 * @param locdes   the locdes
	 * @return the integer
	 */
	int addLocationAndLocdes(org.generationcp.middleware.pojos.Location location, Locdes locdes);

	/**
	 * Get all breeding locations.
	 * <p/>
	 * Return a List of Locations which represent the breeding locations stored
	 * in the location table of IBDB.
	 *
	 * @return the all breeding locations
	 */
	List<org.generationcp.middleware.pojos.Location> getAllBreedingLocations();

	/**
	 * Gets all fields belonging to the given location.
	 *
	 * @param locationId the location id of the parent of the fields to return.
	 * @return all field locations belonging to the given parent id.
	 */
	List<org.generationcp.middleware.pojos.Location> getAllFieldLocations(int locationId);

	/**
	 * Gets all block belonging to the given field.
	 *
	 * @param fieldId the field id of the parent of the blocks to return.
	 * @return all block locations belonging to the given parent id.
	 */
	List<org.generationcp.middleware.pojos.Location> getAllBlockLocations(int fieldId);

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
	List<org.generationcp.middleware.pojos.Location> getAllFields();

	List<org.generationcp.middleware.pojos.Location> getAllBreedingLocations(List<Integer> locationIds);

	List<org.generationcp.middleware.pojos.Location> getAllSeedingLocations(List<Integer> locationIds);

	List<Locdes> getLocdes(List<Integer> locIds, List<String> dvals);

	/**
	 * Gets the user defined field id of code.
	 *
	 * @param tableType the table type
	 * @param code      the code
	 * @return the user defined field id of code
	 */
	Integer getUserDefinedFieldIdOfCode(UDTableType tableType, String code);

}
