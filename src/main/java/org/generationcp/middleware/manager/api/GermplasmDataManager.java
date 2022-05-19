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

import org.apache.commons.lang3.tuple.Triple;
import org.generationcp.middleware.manager.GermplasmNameType;
import org.generationcp.middleware.manager.GetGermplasmByNameModes;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmNameDetails;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.Progenitor;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.generationcp.middleware.pojos.workbench.CropType;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * This is the API for retrieving Germplasm information.
 */
public interface GermplasmDataManager {

	/**
	 * Searches for all germplasm records which matches the given name. It will match records having the following names: (1) the given name
	 * as it is, (2) the name with standardization performed on it, and (3) name with spaces removed.
	 *
	 * @param name      - search string for the name of the germplasm
	 * @param start     - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @param op        - can be EQUAL OR LIKE
	 * @return List of Germplasm POJOs
	 */
	List<Germplasm> getGermplasmByName(String name, int start, int numOfRows, Operation op);

	/**
	 * Returns the number of germplasm records with any name matching the given parameter. It will count records having the following names:
	 * (1) the given name as it is, (2) the name with standardization performed on it, and (3) name with spaces removed.
	 *
	 * @param name - search string for the name of the germplasm
	 * @param op   - can be EQUAL OR LIKE
	 * @return number of germplasm records
	 */
	long countGermplasmByName(String name, Operation op);

	/**
	 * Please use LocationDataManager.getAllCountry().
	 * <p>
	 * Returns all country records.
	 *
	 * @return List of Location POJOs
	 * @deprecated
	 */
	@Deprecated
	List<Country> getAllCountry();

	/**
	 * Returns the germplasm record identified by the given id.
	 *
	 * @param gid - id of the germplasm record to be retrieved
	 * @return the Germplasm POJO representing the record
	 */
	Germplasm getGermplasmByGID(Integer gid);

	/**
	 * Given a gid, return the Germplasm POJO representing the record identified by the id with its preferred name.
	 *
	 * @param gid - the id of the germplasm record to be retrieved
	 * @return the Germplasm POJO representing the record
	 */
	Germplasm getGermplasmWithPrefName(Integer gid);

	/**
	 * Returns the Name record identified by the given id.
	 *
	 * @param id - id of the name record
	 * @return the Name POJO representing the record
	 */
	Name getGermplasmNameByID(Integer id);

	/**
	 * Returns all the names of the Germplasm identified by the gid parameter.
	 * <p>
	 * Results may be filtered by name status. Accepted values are 0 - 10. If the given status is zero all names will be included in the
	 * result.
	 * <p>
	 * Results may also be filtered by type. The enum GermplasmNameType is used to specify the type of names to be included in the result.
	 * If the given type is null, all names will be included in the result.
	 *
	 * @param gid    - id of the Germplasm
	 * @param status - may be used to filter the results
	 * @param type   - may be used to filter the results
	 * @return List of Name POJOs
	 */
	List<Name> getNamesByGID(Integer gid, Integer status, GermplasmNameType type);

	/**
	 * Returns the value (NVAL field) of preferred name of the Germplasm identified by the gid parameter.
	 *
	 * @param gid - id of the Germplasm
	 * @return Germplasm's preferred name as string. Returns
	 */
	String getPreferredNameValueByGID(Integer gid);

	/**
	 * Returns the matching {@code Name} object given a Germplasm ID and a Name value.
	 *
	 * @param gid  - id of the Germplasm
	 * @param nval - value of the Name to search
	 * @param mode - can be normal, spaces removed, name standardized
	 * @return {@code Name} POJO of the matching {@code Name} object. Returns
	 */
	Name getNameByGIDAndNval(Integer gid, String nval, GetGermplasmByNameModes mode);

	/**
	 * Inserts a list of multiple {@code Name} objects into the database.
	 *
	 * @param names - A list of {@code Name} objects to be persisted to the database. {@code Name} objects must be valid.
	 * @return Returns the id of the newly-added Germplasm {@code Name}s.
	 */
	List<Integer> addGermplasmName(List<Name> names);

	/**
	 * Returns the Method record identified by the id.
	 *
	 * @param id - id of the method record
	 * @return the Method POJO representing the record
	 */
	Method getMethodByID(Integer id);

	/**
	 * Returns all the method records.
	 *
	 * @return List of Method POJOs
	 */
	List<Method> getAllMethods();

	/**
	 * Validates the naming rules configuration for the selected breeding method
	 *
	 * @param breedingMethod
	 * @return true if the configurarion is present in the DB
	 */
	boolean isMethodNamingConfigurationValid(Method breedingMethod);

	/**
	 * Gets the all methods not generative.
	 *
	 * @return the all methods not generative
	 */
	List<Method> getAllMethodsNotGenerative();

	/**
	 * Returns all the method records matching the given type. Retrieves from both local and central databases.
	 *
	 * @param type the type of the method
	 * @return List of Method POJOs
	 */
	List<Method> getMethodsByType(String type);

	/**
	 * Returns the udfld records identified by the given tablename and field type.
	 *
	 * @param tableName - the value of the ftable record
	 * @param fieldType - the value of the ftype record
	 * @return the Udflds POJO representing the record
	 */
	List<UserDefinedField> getUserDefinedFieldByFieldTableNameAndType(String tableName, String fieldType);

	/**
	 * Please use LocationDataManager.getLocationById().
	 * <p>
	 * Returns the location record identified by the given id.
	 *
	 * @param id - id of the location record
	 * @return the Location POJO representing the record
	 * @deprecated
	 */
	@Deprecated
	Location getLocationByID(Integer id);

	/**
	 * Given a map of valid Germplasm and Name objects, add new records for the given parameters.
	 * <p>
	 * The Name objects matching each Germplasm object in the map will be set as the preferred name of the Germplasm objects.
	 * <p>
	 * Note that you need to assign temporary ids for the Germplasm objects so that they can serve as the keys for the Map. The function
	 * will replace these temp ids with the correct ones for storing in the database.
	 *
	 * @return the ids of the {@code Germplasm} records added
	 */
	List<Integer> addGermplasm(List<Triple<Germplasm, Name, List<Progenitor>>> germplasmTriples, CropType cropType);

	/**
	 * Given a list of Attribute objects, add new records for the given parameter.
	 *
	 * @param attrs - the list of Attribute objects
	 * @return the id of the new Attribute record added
	 */
	List<Integer> addAttributes(List<Attribute> attrs);

	/**
	 * Gets the germplasm Id and name Id from the names table with the given germplasm names.
	 *
	 * @param germplasmNames the germplasm names
	 * @param mode           the mode
	 * @return List of GidNidElement based on the specified list of germplasm names
	 */
	List<GermplasmNameDetails> getGermplasmNameDetailsByGermplasmNames(List<String> germplasmNames, GetGermplasmByNameModes mode);

	/**
	 * Returns the String representation of next available sequence number for Germplasm Names with given prefix
	 *
	 * @param prefix - String used as prefix for Germplasm Names querying
	 * @return next available sequence number for a germplasm with given prefix
	 */
	String getNextSequenceNumberAsString(String prefix);

	/**
	 * Given a List of GIDs, return the list of all Germplasm.
	 *
	 * @param gids the gids
	 * @return list of germplasm
	 */
	List<Germplasm> getGermplasms(List<Integer> gids);

	/**
	 * Given a List of GIDs, return the list of all Germplasm together with their PreferredName.
	 *
	 * @param gids the gids
	 * @return the preferred names by gids
	 */
	Map<Integer, String> getPreferredNamesByGids(List<Integer> gids);

	/**
	 * Gets the method by code.
	 *
	 * @param code the code
	 * @return the method by code
	 */
	Method getMethodByCode(String code);

	/**
	 * Gets the method by name.
	 *
	 * @param name the code
	 * @return the method by name
	 */
	Method getMethodByName(String name);

	List<Germplasm> getProgenitorsByGIDWithPrefName(Integer gid);

	/**
	 * Gets the list of favorite methods/locations
	 *
	 * @param type        - can be FavoriteType.METHOD or FavoriteType.LOCATION
	 * @param max         - maximum number of records to return
	 * @param programUUID - unique id of the program where the favorites location/method were created
	 * @return list of ProgramFavorite
	 */
	List<ProgramFavorite> getProgramFavorites(ProgramFavorite.FavoriteType type, int max, String programUUID);

	/**
	 * check if name and standardized version of it already exists.
	 *
	 * @param name
	 * @return
	 */
	boolean checkIfMatches(String name);

	/**
	 * Enables us to query the udflds table
	 *
	 * @param table the ftable value
	 * @param type  the ftype value
	 * @param code  we are looking for
	 * @return
	 */
	UserDefinedField getUserDefinedFieldByTableTypeAndCode(String table, String type, String code);

	/**
	 * Given a set of gids return the gid and all its parents including all their names.
	 *
	 * @param gids           - the id of the germplasm record to be retrieved
	 * @param numberOfLevels the number of levels to traverse. Be careful do not go crazy
	 * @return a list of germplasms with all names populated in them
	 */
	List<Germplasm> getGermplasmWithAllNamesAndAncestry(Set<Integer> gids, int numberOfLevels);

	/**
	 * Return the pedigree for a list of germplasms
	 *
	 * @param gidList
	 * @return
	 */
	Map<Integer, String[]> getParentsInfoByGIDList(List<Integer> gidList);

	/**
	 * Returns the method codes specified in the set of integers
	 *
	 * @param methodIds
	 * @return
	 */
	List<String> getMethodCodeByMethodIds(Set<Integer> methodIds);

	/**
	 * @param gid
	 * @param variableId
	 * @return Attribute value that matches with variableId and gid
	 */
	String getAttributeValue(Integer gid, Integer variableId);

	Germplasm getUnknownGermplasmWithPreferredName();

	List<String> getNamesByGidsAndPrefixes(List<Integer> gids, List<String> prefixes);

	List<Germplasm> getExistingCrosses(Integer femaleParent, List<Integer> maleParentIds, Optional<Integer> gid);

	boolean hasExistingCrosses(Integer femaleParent, List<Integer> maleParentIds, Optional<Integer> gid);

}
