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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.generationcp.middleware.domain.gms.search.GermplasmSearchParameter;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.GermplasmNameType;
import org.generationcp.middleware.manager.GetGermplasmByNameModes;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pedigree.Pedigree;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.Bibref;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmNameDetails;
import org.generationcp.middleware.pojos.GermplasmPedigreeTreeNode;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;

/**
 * This is the API for retrieving Germplasm information.
 *
 */
public interface GermplasmDataManager {

	/**
	 * Searches for all germplasm records which matches the given name. It will match records having the following names: (1) the given name
	 * as it is, (2) the name with standardization performed on it, and (3) name with spaces removed.
	 *
	 * @param name - search string for the name of the germplasm
	 * @param start - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @param op - can be EQUAL OR LIKE
	 * @return List of Germplasm POJOs
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Germplasm> getGermplasmByName(String name, int start, int numOfRows, Operation op);

	/**
	 * Returns the number of germplasm records with any name matching the given parameter. It will count records having the following names:
	 * (1) the given name as it is, (2) the name with standardization performed on it, and (3) name with spaces removed.
	 *
	 * @param name - search string for the name of the germplasm
	 * @param op - can be EQUAL OR LIKE
	 * @return number of germplasm records
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	long countGermplasmByName(String name, Operation op);

	/**
	 * Retrieves all the Germplasm entries
	 *
	 * @param start - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return All the germplasms from the database instance satisfying the start and numOfRows parameters
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Germplasm> getAllGermplasm(int start, int numOfRows);

	/**
	 * Returns the germplasm record identified by the given id.
	 *
	 * @param gid - id of the germplasm record to be retrieved
	 * @return the Germplasm POJO representing the record
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Germplasm getGermplasmByGID(Integer gid);

	/**
	 * Given a gid, return the Germplasm POJO representing the record identified by the id with its preferred name.
	 *
	 * @param gid - the id of the germplasm record to be retrieved
	 * @return the Germplasm POJO representing the record
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Germplasm getGermplasmWithPrefName(Integer gid);

	/**
	 * Returns the Name record identified by the given id.
	 *
	 * @param id - id of the name record
	 * @return the Name POJO representing the record
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Name getGermplasmNameByID(Integer id);

	/**
	 * Returns all the names of the Germplasm identified by the gid parameter.
	 *
	 * Results may be filtered by name status. Accepted values are 0 - 10. If the given status is zero all names will be included in the
	 * result.
	 *
	 * Results may also be filtered by type. The enum GermplasmNameType is used to specify the type of names to be included in the result.
	 * If the given type is null, all names will be included in the result.
	 *
	 * @param gid - id of the Germplasm
	 * @param status - may be used to filter the results
	 * @param type - may be used to filter the results
	 * @return List of Name POJOs
	 */
	List<Name> getNamesByGID(Integer gid, Integer status, GermplasmNameType type);

	/**
	 * Returns all the names of the Germplasm identified by the gid parameter.
	 *
	 * Results may be filtered by name status. Accepted values are 0 - 10. If the given status is zero all names will be included except
	 * deleted names
	 *
	 * Results may also be filtered by type list. These must be valid integer values
	 *
	 * @param gid the germplasm identifier
	 * @param status the name status used to filter the results
	 * @param type the type used to filter the results
	 * @return List of {@link Name}
	 */
	List<Name> getByGIDWithListTypeFilters(Integer gid, Integer status, List<Integer> type);

	/**
	 * Returns the preferred name of the Germplasm identified by the gid parameter.
	 *
	 * @param gid - id of the Germplasm
	 * @return {@code Name} POJO of the Germplasm's preferred name. Returns
	 */
	Name getPreferredNameByGID(Integer gid);

	/**
	 * Returns the preferred abbreviation of the Germplasm identified by the gid parameter.
	 *
	 * @param gid - id of the Germplasm
	 * @return {@code Name} POJO of the Germplasm's preferred abbreviation. Returns {@code null} when no preferred abbreviation is found.
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Name getPreferredAbbrevByGID(Integer gid);

	/**
	 * Returns the preferred ID of the Germplasm identified by the gid parameter.
	 *
	 * @param gid - id of the Germplasm
	 * @return {@code Name} POJO of the Germplasm's preferred ID. Returns {@code null} when no preferred ID is found.
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Name getPreferredIdByGID(Integer gid);

	/**
	 * Returns a list of preferred IDs of the Germplasms associated with the Germplasm List identified by the listId parameter.
	 *
	 * @param listId - id of the Germplasm List
	 * @return {@code Name} A list of POJOs of the Germplasms' preferred IDs. Returns an empty list when no preferred ID is found.
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Name> getPreferredIdsByListId(Integer listId);

	/**
	 * Returns the value (NVAL field) of preferred name of the Germplasm identified by the gid parameter.
	 *
	 * @param gid - id of the Germplasm
	 * @return Germplasm's preferred name as string. Returns
	 * @throws MiddlewareQueryException the middleware query exception {@code null} when no preferred name is found.
	 */
	String getPreferredNameValueByGID(Integer gid);

	/**
	 * Returns the matching {@code Name} object given a Germplasm ID and a Name value.
	 *
	 * @param gid - id of the Germplasm
	 * @param nval - value of the Name to search
	 * @param mode - can be normal, spaces removed, name standardized
	 * @return {@code Name} POJO of the matching {@code Name} object. Returns
	 * @throws MiddlewareQueryException the middleware query exception {@code null} when no {@code Name} with the specified gid and nval is
	 *         found.
	 */
	Name getNameByGIDAndNval(Integer gid, String nval, GetGermplasmByNameModes mode);

	/**
	 * Sets the specified Name as the specified Germplasm's new preferred Name.
	 *
	 * @param gid - id of the Germplasm to be updated
	 * @param newPrefName - new name to set as the preferred name
	 * @return Returns the id of the updated {@code Germplasm} record
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Integer updateGermplasmPrefName(Integer gid, String newPrefName);

	/**
	 * Sets the specified Abbreviation as the specified Germplasm's new preferred Abbreviation.
	 *
	 * @param gid - id of the Germplasm to be updated
	 * @param newPrefAbbrev - new abbreviation to set as the preferred abbreviation
	 * @return Returns the id of the updated {@code Germplasm} record
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Integer updateGermplasmPrefAbbrev(Integer gid, String newPrefAbbrev);

	/**
	 * Inserts a single {@code Name} object into the database.
	 *
	 * @param name - The {@code Name} object to be persisted to the database. Must be a valid {@code Name} object.
	 * @return Returns the id of the newly-added Germplasm {@code Name}.
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Integer addGermplasmName(Name name);

	/**
	 * Inserts a list of multiple {@code Name} objects into the database.
	 *
	 * @param names - A list of {@code Name} objects to be persisted to the database. {@code Name} objects must be valid.
	 * @return Returns the id of the newly-added Germplasm {@code Name}s.
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Integer> addGermplasmName(List<Name> names);

	/**
	 * Updates a single {@code Name} object in the database.
	 *
	 * @param name - The {@code Name} object to be updated in the database. Must be a valid {@code Name} object.
	 * @return Returns the id of the updated Germplasm {@code Name}.
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Integer updateGermplasmName(Name name);

	/**
	 * Updates the database with multiple {@code Name} objects specified.
	 *
	 * @param names - A list of {@code Name} objects to be updated in the database. {@code Name} objects must be valid.
	 * @return Returns the id of the updated Germplasm {@code Name}s.
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Integer> updateGermplasmName(List<Name> names);

	/**
	 * Returns all the attributes of the Germplasm identified by the given id.
	 *
	 * @param gid - id of the Germplasm
	 * @return List of Atributs POJOs
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Attribute> getAttributesByGID(Integer gid);

	/**
	 * Returns all the list of attribute types identified by the given list of gids.
	 *
	 * @param gidList - list of GIDs
	 * @return List of UserDefinedField POJOs that contains the attribute types and names for the given GIDs.
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<UserDefinedField> getAttributeTypesByGIDList(List<Integer> gidList);

	/**
	 * Returns a Map of GIDs to the attribute values given an attribute type and a list of GIDs.
	 *
	 * @param attributeType - attribute type of the values to retrieve
	 * @param gidList - list of GIDs
	 * @return Map<Integer, String> - map of gids to their corresponding attribute values for the specified attribute type
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Map<Integer, String> getAttributeValuesByTypeAndGIDList(Integer attributeType, List<Integer> gidList);

	/**
	 * Returns the Method record identified by the id.
	 *
	 * @param id - id of the method record
	 * @return the Method POJO representing the record
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Method getMethodByID(Integer id);

	/**
	 * Returns all the method records.
	 *
	 * @return List of Method POJOs
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Method> getAllMethods();

	/**
	 * Validates the naming rules configuration for the selected breeding method
	 *
	 * @param breedingMethodId
	 * @return true if the configurarion is present in the DB
	 */
	boolean isMethodNamingConfigurationValid(Integer breedingMethodId);

	/**
	 * Returns all the method records.
	 *
	 * @param programUUID - unique id of the current program
	 * @return List of Method POJOs
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Method> getMethodsByUniqueID(String programUUID);

	/**
	 * Returns the number of Methods with type matching the given parameter. Retrieves from both local and central databases.
	 *
	 * @param programUUID - unique id of the current program
	 * @return Number of Methods matching the given type
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	long countMethodsByUniqueID(String programUUID);

	/**
	 * Gets the all methods not generative.
	 *
	 * @return the all methods not generative
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Method> getAllMethodsNotGenerative();

	/**
	 * Returns count of all the method records.
	 *
	 * @return count of methods
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	long countAllMethods();

	/**
	 * Returns all the method records matching the given type. Retrieves from both local and central databases.
	 *
	 * @param type the type of the method
	 * @return List of Method POJOs
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Method> getMethodsByType(String type);

	/**
	 * Returns all the method records matching the given type. Retrieves from both local and central databases.
	 *
	 * @param type the type of the method
	 * @param programUUID - unique id of the current program
	 * @return List of Method POJOs
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Method> getMethodsByType(String type, String programUUID);

	/**
	 * Returns the number of Methods with type matching the given parameter. Retrieves from both local and central databases.
	 *
	 * @param type - search string for the methods
	 * @param programUUID - unique id of the current program
	 * @return Number of Methods matching the given type
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	long countMethodsByType(String type, String programUUID);

	/**
	 * Returns all the method records matching the given group and the methods having the 'G' group. Retrieves from both local and central
	 * databases.
	 *
	 * @param group the group of the method
	 * @return List of Method POJOs
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Method> getMethodsByGroupIncludesGgroup(String group);

	/**
	 * Returns all the method records matching the given type. Retrieves from both local and central databases.
	 *
	 * @param type the type of the method
	 * @param start - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return List of Method POJOs
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Method> getMethodsByType(String type, int start, int numOfRows);

	/**
	 * Returns the number of Methods with type matching the given parameter. Retrieves from both local and central databases.
	 *
	 * @param type - search string for the methods
	 * @return Number of Methods matching the given type
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	long countMethodsByType(String type);

	/**
	 * Returns all the method records matching the given group. Retrieves from both local and central databases.
	 *
	 * @param group the group of the method
	 * @return List of Method POJOs
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Method> getMethodsByGroup(String group);

	/**
	 * Returns all the method records matching the given group. Retrieves from both local and central databases.
	 *
	 * @param group the group of the method
	 * @param start - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return List of Method POJOs
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Method> getMethodsByGroup(String group, int start, int numOfRows);

	/**
	 * Returns all the method and type records matching the given group and type. Retrieves from both local and central databases.
	 *
	 * @param group the group of the method
	 * @param type the type of the method
	 * @return List of Method POJOs
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Method> getMethodsByGroupAndType(String group, String type);

	/**
	 * Returns all the method and type records matching the given group, type and name. Retrieves from both local and central databases.
	 *
	 * @param group the group of the method
	 * @param type the type of the method
	 * @param name the name of the method
	 * @return List of Method POJOs
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Method> getMethodsByGroupAndTypeAndName(String group, String type, String name);

	/**
	 * Returns the number of Methods with group matching the given parameter.
	 *
	 * Retrieves from both local and central databases.
	 *
	 * @param group - search string for the methods
	 * @return Number of Methods matching the given group
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	long countMethodsByGroup(String group);

	/**
	 * Gets list of cvterm records which are possible values of method classes.
	 *
	 * @return the method classes
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Term> getMethodClasses();

	/**
	 * Returns the udfld record identified by the given id.
	 *
	 * @param id - the id of the udfld record
	 * @return the Udflds POJO representing the record
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	UserDefinedField getUserDefinedFieldByID(Integer id);

	/**
	 * Returns the udfld records identified by the given tablename.
	 *
	 * @param tableName - the value of the ftable record
	 * @param fieldType - the value of the ftype record
	 * @return the Udflds POJO representing the record
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<UserDefinedField> getUserDefinedFieldByFieldTableNameAndType(String tableName, String fieldType);

	/**
	 * Return the UserDefinedField using local field no
	 *
	 * @return
	 * @throws MiddlewareQueryException
	 */
	UserDefinedField getUserDefinedFieldByLocalFieldNo(Integer lfldno);

	/**
	 * Please use LocationDataManager.getCountryById().
	 *
	 * Returns the country record identified by the given id.
	 *
	 * @param id - id of the country record
	 * @return the Country POJO representing the record
	 * @throws MiddlewareQueryException the middleware query exception
	 * @deprecated
	 */
	@Deprecated
	Country getCountryById(Integer id);

	/**
	 * Please use LocationDataManager.getLocationById().
	 *
	 * Returns the location record identified by the given id.
	 *
	 * @param id - id of the location record
	 * @return the Location POJO representing the record
	 * @throws MiddlewareQueryException the middleware query exception
	 * @deprecated
	 */
	@Deprecated
	Location getLocationByID(Integer id);

	/**
	 * Updates the {@code Method} object into the database.
	 *
	 * @param method - The {@code Method} object to be persisted to the database. Must be a valid {@code Method} object.
	 * @return Returns the updated {@code Method} record
	 *
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Method editMethod(Method method);

	/**
	 * Inserts a single {@code Method} object into the database.
	 *
	 * @param method - The {@code Method} object to be persisted to the database. Must be a valid {@code Method} object.
	 * @return Returns the id of the {@code Method} record inserted in the database.
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Integer addMethod(Method method);

	/**
	 * Inserts a list of {@code Method} objects into the database.
	 *
	 * @param methods - The list of {@code Method} objects to be persisted to the database. Must be valid {@code Method} objects.
	 * @return Returns the ids of the {@code Method} records inserted in the database.
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Integer> addMethod(List<Method> methods);

	/**
	 * Deletes a single {@code Method} object into the database.
	 *
	 * @param method - The {@code Method} object to be deleted from the database. Must be a valid {@code Method} object.
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	void deleteMethod(Method method);

	/**
	 * Returns the Bibref record identified by the given id.
	 *
	 * @param id - id of the bibref record
	 * @return the Bibref POJO representing the record
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Bibref getBibliographicReferenceByID(Integer id);

	/**
	 * Inserts a single {@code Bibref} (Bibliographic Reference) object into the database.
	 *
	 * @param bibref - The {@code Bibref} object to be persisted to the database. Must be a valid {@code Bibref} object.
	 * @return Returns the id of the {@code Bibref} record inserted in the database.
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Integer addBibliographicReference(Bibref bibref);

	/**
	 * Stores in the database the given valid Attribute object.
	 *
	 * @param attribute the attribute
	 * @return the id of {@code Attribute} records stored in the database
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Integer addGermplasmAttribute(Attribute attribute);

	/**
	 * Stores in the database all the given valid Attributes object contained in the parameter.
	 *
	 * @param attributes - List of Attribute objects
	 * @return the ids of the Attribute records stored in the database
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Integer> addGermplasmAttribute(List<Attribute> attributes);

	/**
	 * Given a valid Attribute object, update the corresponding record in the database.
	 *
	 * @param attribute the attribute
	 * @return Returns the id of the updated Germplasm {@code Attribute} record
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Integer updateGermplasmAttribute(Attribute attribute);

	/**
	 * Given a List of valid Attribute objects, update their corresponding records in the database.
	 *
	 * @param attributes - List of Attribute objects
	 * @return Returns the ids of the updated Germplasm {@code Attribute} record
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Integer> updateGermplasmAttribute(List<Attribute> attributes);

	/**
	 * Returns the attribute record identified by the given id.
	 *
	 * @param id the id
	 * @return The attribute record corresponding to the given id.
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Attribute getAttributeById(Integer id);

	/**
	 * Given the gid of the child germplasm, the gid of the parent germplasm and the progenitor number, this method makes the necessary
	 * changes to save the relationship on the database.
	 *
	 * This method will either update the Germplasm record, to change the gpid1 or gpid2 fields (if the progenitor number given is 1 or 2),
	 * or will either add or update the Progenitor record which represents this relationship. A new Progenitor record will be stored when
	 * necessary.
	 *
	 * @param gid the gid
	 * @param progenitorId the progenitor id
	 * @param progenitorNumber the progenitor number
	 * @return Returns the id of the updated Progenitor
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Integer updateProgenitor(Integer gid, Integer progenitorId, Integer progenitorNumber);

	/**
	 * Given a valid Germplasm object, update the corresponding record in the database.
	 *
	 * @param germplasm the germplasm
	 * @return Returns the id of the updated {@code Germplasm} record
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Integer updateGermplasm(Germplasm germplasm);

	/**
	 * Given a List of valid Germplasm objects, update the corresponding records in the database.
	 *
	 * @param germplasms the germplasms
	 * @return Returns the ids of the updated {@code Germplasm} records
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Integer> updateGermplasm(List<Germplasm> germplasms);

	/**
	 * Given a valid Germplasm object with a matching valid Name object to be set as its preferred name, add a new Germplasm record and a
	 * new Name record for the given parameters.
	 *
	 * @param germplasm the germplasm
	 * @param preferredName the preferred name
	 * @return the id of the {@code Germplasm} record added
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Integer addGermplasm(Germplasm germplasm, Name preferredName);

	/**
	 * Given a map of valid Germplasm and Name objects, add new records for the given parameters.
	 *
	 * The Name objects matching each Germplasm object in the map will be set as the preferred name of the Germplasm objects.
	 *
	 * Note that you need to assign temporary ids for the Germplasm objects so that they can serve as the keys for the Map. The function
	 * will replace these temp ids with the correct ones for storing in the database.
	 *
	 * @param germplasmNameMap the germplasm name map
	 * @return the ids of the {@code Germplasm} records added
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Integer> addGermplasm(Map<Germplasm, Name> germplasmNameMap);

	List<Integer> addGermplasm(List<Pair<Germplasm, Name>> germplasms);

	/**
	 * Given a UserDefinedField object, add new record for the given parameter.
	 *
	 * @param field - the UserDefinedField object
	 * @return the id of the new UserDefinedField record added
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Integer addUserDefinedField(UserDefinedField field);

	/**
	 * Given a list of UserDefinedField objects, add new records for the given parameter.
	 *
	 * @param fields - the list of UserDefinedField objects
	 * @return the list of ids of the new UserDefinedField records added
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Integer> addUserDefinedFields(List<UserDefinedField> fields);

	/**
	 * Given a Attribute object, add new record for the given parameter.
	 *
	 * @param attr - the Attribute object
	 * @return the id of the new Attribute record added
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Integer addAttribute(Attribute attr);

	/**
	 * Given a list of Attribute objects, add new records for the given parameter.
	 *
	 * @param attrs - the list of Attribute objects
	 * @return the id of the new Attribute record added
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Integer> addAttributes(List<Attribute> attrs);

	/**
	 * Gets the germplasm Id and name Id from the names table with the given germplasm names.
	 *
	 * @param germplasmNames the germplasm names
	 * @param mode the mode
	 * @return List of GidNidElement based on the specified list of germplasm names
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<GermplasmNameDetails> getGermplasmNameDetailsByGermplasmNames(List<String> germplasmNames, GetGermplasmByNameModes mode);

	/**
	 * Please use LocationDataManager.getAllBreedingLocations().
	 *
	 * Get all breeding locations.
	 *
	 * Return a List of Locations which represent the breeding locations stored in the location table of IBDB.
	 *
	 * @return the all breeding locations
	 * @throws MiddlewareQueryException the middleware query exception
	 * @deprecated
	 */
	@Deprecated
	List<Location> getAllBreedingLocations();

	/**
	 * Returns the String representation of next available sequence number for Germplasm Names with given prefix. Queries both
	 * Database.LOCAL and Database.CENTRAL and returns the greater number.
	 *
	 * @param prefix - String used as prefix for Germplasm Names querying
	 * @return next available sequence number for a germplasm with given prefix.
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	String getNextSequenceNumberForCrossName(String prefix);

	/**
	 * Returns a Map of GIDs to preferred ids given a list of GIDs.
	 *
	 * @param gids the gids
	 * @return the preffered ids by gi ds
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Map<Integer, String> getPrefferedIdsByGIDs(List<Integer> gids);

	/**
	 * Given a List of GIDs, return the list of all Germplasm.
	 *
	 * @param gids the gids
	 * @return the germplasms
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Germplasm> getGermplasms(List<Integer> gids);

	/**
	 * Given a List of GIDs, return the list of all Germplasm together with their PreferredName.
	 *
	 * @param gids the gids
	 * @return the preferred names by gids
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Map<Integer, String> getPreferredNamesByGids(List<Integer> gids);

	/**
	 * Given a List of GIDs, return the list of gids mapped to their corresponding location name.
	 *
	 * @param gids the gids
	 * @return Map<Integer, String> - map of gids to their corresponding location name
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Map<Integer, String> getLocationNamesByGids(List<Integer> gids);

	/**
	 * Search for germplasms given a search term
	 * 
	 * @param searchedString - the search term to be used
	 * @param o - like or equal
	 * @param includeParents boolean flag to denote whether parents will be included in search results
	 * @param withInventoryOnly - boolean flag to denote whether result will be filtered by those with inventories only
	 * @param includeMGMembers - boolean flag to denote whether the management group members (same mgid) will be included in the result
	 * @return List of Germplasms
	 * @throws MiddlewareQueryException
	 */
	List<Germplasm> searchForGermplasm(String q, Operation o, boolean includeParents, boolean withInventoryOnly, boolean includeMGMembers);

	/**
	 * Search for germplasms given a search term
	 * 
	 * @param germplasmSearchParameter - contains all data needed for the germplasm search
	 * @return List of Germplasms
	 * @throws MiddlewareQueryException
	 */
	List<Germplasm> searchForGermplasm(GermplasmSearchParameter germplasmSearchParameter);

	/**
	 * Please use LocationDataManager.getLocationsByIDs().
	 *
	 * Gets the locations by the given IDs.
	 *
	 * @param ids Location IDs
	 * @return the corresponding Locations
	 * @throws MiddlewareQueryException the middleware query exception
	 * @deprecated
	 */
	@Deprecated
	List<Location> getLocationsByIDs(List<Integer> ids);

	/**
	 * Gets the methods by IDs.
	 *
	 * @param ids the Method Ids
	 * @return the methods corresponding to the given IDs
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Method> getMethodsByIDs(List<Integer> ids);

	List<Method> getNonGenerativeMethodsByID(List<Integer> ids);

	/**
	 * Get gDates given GIDs.
	 *
	 * @param gids the gids
	 * @return <gid, integerdatevalue>
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Map<Integer, Integer> getGermplasmDatesByGids(List<Integer> gids);

	/**
	 * Get methods given GIDs.
	 *
	 * @param gids the gids
	 * @return Map<gid, method>
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Map<Integer, Object> getMethodsByGids(List<Integer> gids);

	/**
	 * Gets the method by code.
	 *
	 * @param code the code
	 * @param programUUID - uniqueId of the current program
	 * @return the method by code
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Method getMethodByCode(String code, String programUUID);

	/**
	 * Gets the method by code.
	 *
	 * @param code the code
	 * @return the method by code
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Method getMethodByCode(String code);

	/**
	 * Gets the method by name.
	 *
	 * @param name the code
	 * @return the method by name
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Method getMethodByName(String name);

	/**
	 * Gets the method by name.
	 *
	 * @param name the code
	 * @param programUUID - uniqueID of the current program
	 * @return the method by name
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Method getMethodByName(String name, String programUUID);

	List<Germplasm> getProgenitorsByGIDWithPrefName(Integer gid);

	/**
	 * Gets the list of favorite methods/locations
	 *
	 * @param type - can be FavoriteType.METHOD or FavoriteType.LOCATION
	 * @param programUUID - unique id of the program where the favorites location/method were created
	 * @return list of ProgramFavorite
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<ProgramFavorite> getProgramFavorites(ProgramFavorite.FavoriteType type, String programUUID);

	/**
	 * Gets the list of favorite methods/locations
	 *
	 * @param type - can be FavoriteType.METHOD or FavoriteType.LOCATION
	 * @param max - maximum number of records to return
	 * @param programUUID - unique id of the program where the favorites location/method were created
	 * @return list of ProgramFavorite
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<ProgramFavorite> getProgramFavorites(ProgramFavorite.FavoriteType type, int max, String programUUID);

	/**
	 * count favorite methods/locations
	 *
	 * @param type - can be FavoriteType.METHOD or FavoriteType.LOCATION
	 * @return count of ProgramFavorite list
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	int countProgramFavorites(ProgramFavorite.FavoriteType type);

	/**
	 * Saves the list of favorite methods/locations
	 *
	 * @param list of ProgramFavorite
	 * @return none
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	void saveProgramFavorites(List<ProgramFavorite> list);

	/**
	 * Saves a favorite method/location
	 *
	 * @param favorite to be saved
	 * @return none
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	void saveProgramFavorite(ProgramFavorite favorite);

	/**
	 * Deletes a list of favorite methods/locations
	 *
	 * @param list of ProgramFavorite
	 * @return none
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	void deleteProgramFavorites(List<ProgramFavorite> list);

	/**
	 * Deletes a favorite method/location
	 *
	 * @param favorite the code
	 * @return none
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	void deleteProgramFavorite(ProgramFavorite favorite);

	/**
	 * Returns the maximum number in the sequence.
	 *
	 * @param prefix
	 * @param suffix
	 * @param count
	 * @return
	 * @throws MiddlewareQueryException
	 */
	int getMaximumSequence(boolean isBulk, String prefix, String suffix, int count);

	/**
	 * check if name and standardized version of it already exists.
	 *
	 * @param name
	 * @return
	 * @throws MiddlewareQueryException
	 */
	boolean checkIfMatches(String name);

	/**
	 * get all method records filtered by programUUID
	 *
	 * @param programUUID
	 * @return list of mid
	 */
	List<Method> getProgramMethods(String programUUID);

	/**
	 * delete all method records filtered by programUUID
	 *
	 * @param programUUID
	 */
	void deleteProgramMethodsByUniqueId(String programUUID);

	/**
	 * Generates a Map of {@link GermplasmPedigreeTreeNode}, which is a wrapper for a Germplasm and its immediate parents, stored as nodes
	 * in <b>linkedNodes</b> atribute, being the first node the female and the second one the male parent. The information is ultimately
	 * stored in Germplasm beans, containing only gids and information about names. The key of the map is the gid.
	 *
	 * @param studyId The identifier for the study which parents will be retuned.
	 * @return The parents for each germplasm in a study.
	 */
	Map<Integer, GermplasmPedigreeTreeNode> getDirectParentsForStudy(int studyId);

	/*
	 * get the Germplasm from the crop database based on local gid reference
	 *
	 * @param lgid
	 */
	Germplasm getGermplasmByLocalGid(Integer lgid);

	/**
	 * return list of name and it's permutation count.
	 * 
	 * @param names list of names
	 * @return list of name and it's number of permutations.
	 */
	Map<String, Integer> getCountByNamePermutations(List<String> names);

	/**
	 * @return the UDFLD table record that represents "plot code": ftable=ATRIBUTS, ftype=PASSPORT, fcode=PLOTCODE. If no record matching
	 *         these critria is found, an empty record with fldno=0 is returned. Never returns null.
	 */
	UserDefinedField getPlotCodeField();

	/**
	 * Returns value of the plot code (seed source) where the germplasm was created, identified by the given gid. Returns "Unknown" if plot
	 * code attribute is not present. Never returns null.
	 */
	String getPlotCodeValue(Integer gid);

	/**
	 * Enables us to query the udflds table
	 * 
	 * @param table the ftable value
	 * @param type the ftype value
	 * @param code we are looking for
	 * @return
	 */
	UserDefinedField getUserDefinedFieldByTableTypeAndCode(final String table, final String type, final String code);

  	void addPedigreeString(HashMap<Germplasm, String> germplasmPedigreeStringMap, String profile, int cropGenerationLevel);

  	void updatePedigreeString(Pedigree pedigree, String crossExpansion, String profile, int cropGenerationLevel);

  	/**
  	 * Return the count of germplasm search results based on the following parameters:
  	 *
   	* @param q - keyword
   	* @param o - operation
   	* @param includeParents - include the parents of the search germplasm
   	* @param withInventoryOnly - include germplasm with inventory details only
   	* @param includeMGMembers - include germplasm of the same group of the search germplasm
   	* @return
   	*/
  	Integer countSearchForGermplasm(String q, Operation o, boolean includeParents, boolean withInventoryOnly, boolean includeMGMembers);

	List<Method> getDerivativeAndMaintenanceMethods(List<Integer> ids);

}
