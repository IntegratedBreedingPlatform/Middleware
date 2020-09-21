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
import org.generationcp.middleware.domain.germplasm.AttributeDTO;
import org.generationcp.middleware.domain.germplasm.GermplasmDTO;
import org.generationcp.middleware.domain.germplasm.PedigreeDTO;
import org.generationcp.middleware.domain.germplasm.ProgenyDTO;
import org.generationcp.middleware.domain.gms.search.GermplasmSearchParameter;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.search_request.brapi.v1.GermplasmSearchRequestDto;
import org.generationcp.middleware.manager.GermplasmNameType;
import org.generationcp.middleware.manager.GetGermplasmByNameModes;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.Bibref;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmNameDetails;
import org.generationcp.middleware.pojos.GermplasmPedigreeTreeNode;
import org.generationcp.middleware.pojos.KeySequenceRegister;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.Progenitor;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.generationcp.middleware.pojos.naming.NamingConfiguration;
import org.generationcp.middleware.pojos.workbench.CropType;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
	 */
	List<Germplasm> getGermplasmByName(String name, int start, int numOfRows, Operation op);

	/**
	 * Returns the number of germplasm records with any name matching the given parameter. It will count records having the following names:
	 * (1) the given name as it is, (2) the name with standardization performed on it, and (3) name with spaces removed.
	 *
	 * @param name - search string for the name of the germplasm
	 * @param op - can be EQUAL OR LIKE
	 * @return number of germplasm records
	 */
	long countGermplasmByName(String name, Operation op);

	/**
	 * Returns the germplasm records that were created at the locations with names matching the given parameter.
	 *
	 * @param name - search string for the name of the locations
	 * @param start - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @param op - can be EQUAL like LIKE
	 * @return List of Germplasm POJOs
	 */
	List<Germplasm> getGermplasmByLocationName(String name, int start, int numOfRows, Operation op);

	/**
	 * Returns the number of germplasm records that were created at the locations with names matching the given parameter.
	 *
	 * @param name - search string for the name of the locations
	 * @param op - can be EQUAL like LIKE
	 * @return Number of Germplasms
	 */
	long countGermplasmByLocationName(String name, Operation op);

	/**
	 * Please use LocationDataManager.getAllCountry().
	 *
	 * Returns all country records.
	 *
	 * @return List of Location POJOs
	 * @deprecated
	 */
	@Deprecated
	List<Country> getAllCountry();

	/**
	 * Retrieves all the Germplasm entries
	 *
	 * @param start - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return All the germplasms from the database instance satisfying the start and numOfRows parameters
	 */
	List<Germplasm> getAllGermplasm(int start, int numOfRows);

	/**
	 * Returns the germplasm records that were created by the methods with names matching the given parameter.
	 *
	 * @param name - search string for the name of the methods
	 * @param start - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @param op - can be EQUAL or LIKE
	 * @return List of Germplasm POJOS
	 */
	List<Germplasm> getGermplasmByMethodName(String name, int start, int numOfRows, Operation op);

	/**
	 * Returns the number of germplasm records that were created by methods with names matching the given parameter.
	 *
	 * @param name - search string for the name of the methods
	 * @param op - can be equal or like
	 * @return number of germplasm records
	 */
	long countGermplasmByMethodName(String name, Operation op);

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
	 * Given a gid, return the Germplasm POJO representing the record identified by the id with its preferred name and preferred
	 * abbreviation.
	 *
	 * @param gid - the id of the germplasm record to be retrieved
	 * @return the Germplasm POJO representing the record
	 */
	Germplasm getGermplasmWithPrefAbbrev(Integer gid);

	/**
	 * Returns the Name record identified by the given id.
	 *
	 * @param id - id of the name record
	 * @return the Name POJO representing the record
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
	 */
	Name getPreferredAbbrevByGID(Integer gid);

	/**
	 * Returns the preferred ID of the Germplasm identified by the gid parameter.
	 *
	 * @param gid - id of the Germplasm
	 * @return {@code Name} POJO of the Germplasm's preferred ID. Returns {@code null} when no preferred ID is found.
	 */
	Name getPreferredIdByGID(Integer gid);

	/**
	 * Returns a list of preferred IDs of the Germplasms associated with the Germplasm List identified by the listId parameter.
	 *
	 * @param listId - id of the Germplasm List
	 * @return {@code Name} A list of POJOs of the Germplasms' preferred IDs. Returns an empty list when no preferred ID is found.
	 */
	List<Name> getPreferredIdsByListId(Integer listId);

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
	 * @param gid - id of the Germplasm
	 * @param nval - value of the Name to search
	 * @param mode - can be normal, spaces removed, name standardized
	 * @return {@code Name} POJO of the matching {@code Name} object. Returns
	 */
	Name getNameByGIDAndNval(Integer gid, String nval, GetGermplasmByNameModes mode);

	/**
	 * Sets the specified Name as the specified Germplasm's new preferred Name.
	 *
	 * @param gid - id of the Germplasm to be updated
	 * @param newPrefName - new name to set as the preferred name
	 * @return Returns the id of the updated {@code Germplasm} record
	 */
	Integer updateGermplasmPrefName(Integer gid, String newPrefName);

	/**
	 * Sets the specified Abbreviation as the specified Germplasm's new preferred Abbreviation.
	 *
	 * @param gid - id of the Germplasm to be updated
	 * @param newPrefAbbrev - new abbreviation to set as the preferred abbreviation
	 * @return Returns the id of the updated {@code Germplasm} record
	 */
	Integer updateGermplasmPrefAbbrev(Integer gid, String newPrefAbbrev);

	/**
	 * Inserts a single {@code Name} object into the database.
	 *
	 * @param name - The {@code Name} object to be persisted to the database. Must be a valid {@code Name} object.
	 * @return Returns the id of the newly-added Germplasm {@code Name}.
	 */
	Integer addGermplasmName(Name name);

	/**
	 * Inserts a list of multiple {@code Name} objects into the database.
	 *
	 * @param names - A list of {@code Name} objects to be persisted to the database. {@code Name} objects must be valid.
	 * @return Returns the id of the newly-added Germplasm {@code Name}s.
	 */
	List<Integer> addGermplasmName(List<Name> names);

	/**
	 * Updates a single {@code Name} object in the database.
	 *
	 * @param name - The {@code Name} object to be updated in the database. Must be a valid {@code Name} object.
	 * @return Returns the id of the updated Germplasm {@code Name}.
	 */
	Integer updateGermplasmName(Name name);

	/**
	 * Updates the database with multiple {@code Name} objects specified.
	 *
	 * @param names - A list of {@code Name} objects to be updated in the database. {@code Name} objects must be valid.
	 * @return Returns the id of the updated Germplasm {@code Name}s.
	 */
	List<Integer> updateGermplasmName(List<Name> names);

	/**
	 * Returns all the available attribute types
	 *
	 * @return List of UserDefinedField POJOs that contains all the attribute types
	 */
	List<UserDefinedField> getAllAttributesTypes();

	/**
	 * Returns all the attributes of the Germplasm identified by the given id.
	 *
	 * @param gid - id of the Germplasm
	 * @return List of Atributs POJOs
	 */
	List<Attribute> getAttributesByGID(Integer gid);

	/**
	 * Returns all the list of attribute types identified by the given list of gids.
	 *
	 * @param gidList - list of GIDs
	 * @return List of UserDefinedField POJOs that contains the attribute types and names for the given GIDs.
	 */
	List<UserDefinedField> getAttributeTypesByGIDList(List<Integer> gidList);

	/**
	 * Returns a Map of GIDs to the attribute values given an attribute type and a list of GIDs.
	 *
	 * @param attributeType - attribute type of the values to retrieve
	 * @param gidList - list of GIDs
	 * @return Map<Integer, String> - map of gids to their corresponding attribute values for the specified attribute type
	 */
	Map<Integer, String> getAttributeValuesByTypeAndGIDList(Integer attributeType, List<Integer> gidList);

	/**
	 * Returns a Map of GIDs to the attribute values by type given a list of GIDs.
	 *
	 * @param gidList - list of GIDs
	 * @return Map<Integer, Map<Integer, String> - map of gids to their corresponding attribute values
	 */
	Map<Integer, Map<Integer, String>> getAttributeValuesGIDList(List<Integer> gidList);

	/**
	 * Returns all the list of name types available for the given list of gids.
	 *
	 * @param gidList - list of GIDs
	 * @return List of UserDefinedField POJOs that contains the name types for the given GIDs.
	 */
	List<UserDefinedField> getNameTypesByGIDList(List<Integer> gidList);

	/**
	 * Returns a Map of GIDs to the name values given name type and a list of GIDs.
	 *
	 * @param nameType - name type ID of the values to retrieve
	 * @param gidList - list of GIDs
	 * @return Map<Integer, String> - map of gids to their corresponding name values for the specified name type
	 */
	Map<Integer, String> getNamesByTypeAndGIDList(Integer nameType, List<Integer> gidList);


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
	 * Returns all the method records ordered by method name.
	 *
	 * @return List of Method POJOs
	 */
	List<Method> getAllMethodsOrderByMname();

	/**
	 * Validates the naming rules configuration for the selected breeding method
	 *
	 * @param breedingMethod
	 * @return true if the configurarion is present in the DB
	 */
	boolean isMethodNamingConfigurationValid(Method breedingMethod);

	/**
	 * Returns all the method records.
	 *
	 * @param programUUID - unique id of the current program
	 * @return List of Method POJOs
	 */
	List<Method> getMethodsByUniqueID(String programUUID);

	/**
	 * Returns the number of Methods with type matching the given parameter. Retrieves from both local and central databases.
	 *
	 * @param programUUID - unique id of the current program
	 * @return Number of Methods matching the given type
	 */
	long countMethodsByUniqueID(String programUUID);

	/**
	 * Gets the all methods not generative.
	 *
	 * @return the all methods not generative
	 */
	List<Method> getAllMethodsNotGenerative();

	/**
	 * Returns count of all the method records.
	 *
	 * @return count of methods
	 */
	long countAllMethods();

	/**
	 * Returns all the method records matching the given type. Retrieves from both local and central databases.
	 *
	 * @param type the type of the method
	 * @return List of Method POJOs
	 */
	List<Method> getMethodsByType(String type);

	/**
	 * Returns all the method records matching the given type. Retrieves from both local and central databases.
	 *
	 * @param type the type of the method
	 * @param programUUID - unique id of the current program
	 * @return List of Method POJOs
	 */
	List<Method> getMethodsByType(String type, String programUUID);

	/**
	 * Returns the number of Methods with type matching the given parameter. Retrieves from both local and central databases.
	 *
	 * @param type - search string for the methods
	 * @param programUUID - unique id of the current program
	 * @return Number of Methods matching the given type
	 */
	long countMethodsByType(String type, String programUUID);

	/**
	 * Returns all the method records matching the given group and the methods having the 'G' group. Retrieves from both local and central
	 * databases.
	 *
	 * @param group the group of the method
	 * @return List of Method POJOs
	 */
	List<Method> getMethodsByGroupIncludesGgroup(String group);

	/**
	 * Returns all the method records matching the given type. Retrieves from both local and central databases.
	 *
	 * @param type the type of the method
	 * @param start - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return List of Method POJOs
	 */
	List<Method> getMethodsByType(String type, int start, int numOfRows);

	/**
	 * Returns the number of Methods with type matching the given parameter. Retrieves from both local and central databases.
	 *
	 * @param type - search string for the methods
	 * @return Number of Methods matching the given type
	 */
	long countMethodsByType(String type);

	/**
	 * Returns all the method records matching the given group. Retrieves from both local and central databases.
	 *
	 * @param group the group of the method
	 * @return List of Method POJOs
	 */
	List<Method> getMethodsByGroup(String group);

	/**
	 * Returns all the method records matching the given group. Retrieves from both local and central databases.
	 *
	 * @param group the group of the method
	 * @param start - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return List of Method POJOs
	 */
	List<Method> getMethodsByGroup(String group, int start, int numOfRows);

	/**
	 * Returns all the method and type records matching the given group and type. Retrieves from both local and central databases.
	 *
	 * @param group the group of the method
	 * @param type the type of the method
	 * @return List of Method POJOs
	 */
	List<Method> getMethodsByGroupAndType(String group, String type);

	/**
	 * Returns all the method and type records matching the given group, type and name. Retrieves from both local and central databases.
	 *
	 * @param group the group of the method
	 * @param type the type of the method
	 * @param name the name of the method
	 * @return List of Method POJOs
	 */
	List<Method> getMethodsByGroupAndTypeAndName(String group, String type, String name);

	/**
	 * Returns the number of Methods with group matching the given parameter.
	 *
	 * Retrieves from both local and central databases.
	 *
	 * @param group - search string for the methods
	 * @return Number of Methods matching the given group
	 */
	long countMethodsByGroup(String group);

	/**
	 * Gets list of cvterm records which are possible values of method classes.
	 *
	 * @return the method classes
	 */
	List<Term> getMethodClasses();

	/**
	 * Returns the udfld record identified by the given id.
	 *
	 * @param id - the id of the udfld record
	 * @return the Udflds POJO representing the record
	 */
	UserDefinedField getUserDefinedFieldByID(Integer id);

	/**
	 * Returns the udfld records identified by the given tablename and field type.
	 *
	 * @param tableName - the value of the ftable record
	 * @param fieldType - the value of the ftype record
	 * @return the Udflds POJO representing the record
	 */
	List<UserDefinedField> getUserDefinedFieldByFieldTableNameAndType(String tableName, String fieldType);

	/**
	 * Returns the udfld records identified by the given tablename, field type, and field name.
	 *
	 * @param tableName - the value of the ftable record
	 * @param fieldType - the value of the ftype record
	 * @param fieldName - the value of the fname record
	 * @return the Udflds POJO representing the record
	 */
	List<UserDefinedField> getUserDefinedFieldByFieldTableNameAndFTypeAndFName(String tableName, String fieldType, String fieldName);

	/**
	 * Return the UserDefinedField using local field no
	 *
	 * @return
	 */
	UserDefinedField getUserDefinedFieldByLocalFieldNo(Integer lfldno);

	/**
	 * Please use LocationDataManager.getCountryById().
	 *
	 * Returns the country record identified by the given id.
	 *
	 * @param id - id of the country record
	 * @return the Country POJO representing the record
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
	 */
	Method editMethod(Method method);

	/**
	 * Inserts a single {@code Method} object into the database.
	 *
	 * @param method - The {@code Method} object to be persisted to the database. Must be a valid {@code Method} object.
	 * @return Returns the id of the {@code Method} record inserted in the database.
	 */
	Integer addMethod(Method method);

	/**
	 * Inserts a list of {@code Method} objects into the database.
	 *
	 * @param methods - The list of {@code Method} objects to be persisted to the database. Must be valid {@code Method} objects.
	 * @return Returns the ids of the {@code Method} records inserted in the database.
	 */
	List<Integer> addMethod(List<Method> methods);

	/**
	 * Deletes a single {@code Method} object into the database.
	 *
	 * @param method - The {@code Method} object to be deleted from the database. Must be a valid {@code Method} object.
	 */
	void deleteMethod(Method method);

	/**
	 * Returns the Bibref record identified by the given id.
	 *
	 * @param id - id of the bibref record
	 * @return the Bibref POJO representing the record
	 */
	Bibref getBibliographicReferenceByID(Integer id);

	/**
	 * Inserts a single {@code Bibref} (Bibliographic Reference) object into the database.
	 *
	 * @param bibref - The {@code Bibref} object to be persisted to the database. Must be a valid {@code Bibref} object.
	 * @return Returns the id of the {@code Bibref} record inserted in the database.
	 */
	Integer addBibliographicReference(Bibref bibref);

	/**
	 * Stores in the database the given valid Attribute object.
	 *
	 * @param attribute the attribute
	 * @return the id of {@code Attribute} records stored in the database
	 */
	Integer addGermplasmAttribute(Attribute attribute);

	/**
	 * Stores in the database all the given valid Attributes object contained in the parameter.
	 *
	 * @param attributes - List of Attribute objects
	 * @return the ids of the Attribute records stored in the database
	 */
	List<Integer> addGermplasmAttribute(List<Attribute> attributes);

	/**
	 * Given a valid Attribute object, update the corresponding record in the database.
	 *
	 * @param attribute the attribute
	 * @return Returns the id of the updated Germplasm {@code Attribute} record
	 */
	Integer updateGermplasmAttribute(Attribute attribute);

	/**
	 * Given a List of valid Attribute objects, update their corresponding records in the database.
	 *
	 * @param attributes - List of Attribute objects
	 * @return Returns the ids of the updated Germplasm {@code Attribute} record
	 */
	List<Integer> updateGermplasmAttribute(List<Attribute> attributes);

	/**
	 * Returns the attribute record identified by the given id.
	 *
	 * @param id the id
	 * @return The attribute record corresponding to the given id.
	 */
	Attribute getAttributeById(Integer id);

	/**
	 * Given a valid Germplasm object with a matching valid Name object to be set as its preferred name, add a new Germplasm record and a
	 * new Name record for the given parameters.
	 *
	 * @param germplasm the germplasm
	 * @param preferredName the preferred name
	 * @return the id of the {@code Germplasm} record added
	 */
	Integer addGermplasm(Germplasm germplasm, Name preferredName, final CropType cropType);

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
	 */
	List<Integer> addGermplasm(Map<Germplasm, Name> germplasmNameMap, CropType cropType);

	List<Integer> addGermplasm(List<Triple<Germplasm, Name, List<Progenitor>>> germplasmTriples, CropType cropType);

	/**
	 * Given a UserDefinedField object, add new record for the given parameter.
	 *
	 * @param field - the UserDefinedField object
	 * @return the id of the new UserDefinedField record added
	 */
	Integer addUserDefinedField(UserDefinedField field);

	/**
	 * Given a list of UserDefinedField objects, add new records for the given parameter.
	 *
	 * @param fields - the list of UserDefinedField objects
	 * @return the list of ids of the new UserDefinedField records added
	 */
	List<Integer> addUserDefinedFields(List<UserDefinedField> fields);

	/**
	 * Given a Attribute object, add new record for the given parameter.
	 *
	 * @param attr - the Attribute object
	 * @return the id of the new Attribute record added
	 */
	Integer addAttribute(Attribute attr);

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
	 * @param mode the mode
	 * @return List of GidNidElement based on the specified list of germplasm names
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
	 * @deprecated
	 */
	@Deprecated
	List<Location> getAllBreedingLocations();

	/**
	 * Returns the String representation of next available sequence number for Germplasm Names with given prefix
	 *
	 * @param prefix - String used as prefix for Germplasm Names querying
	 * @return next available sequence number for a germplasm with given prefix
	 */
	String getNextSequenceNumberAsString(String prefix);

	/**
	 * Returns a Map of GIDs to preferred ids given a list of GIDs.
	 *
	 * @param gids the gids
	 * @return the preferred ids by gi ds
	 */
	Map<Integer, String> getPreferredIdsByGIDs(List<Integer> gids);

	/**
	 * Given the germplasm name and a location ID, returns list of all germplasm with specified name and location id.
	 *
	 * @param name - search string for the name of the germplasm
	 * @param locationID the location id
	 * @return List of Germplasm POJOs
	 */
	List<Germplasm> getGermplasmByLocationId(String name, int locationID);

	/**
	 * Given a gid, return the Germplasm POJO representing the record identified by the id with its method type.
	 *
	 * @param gid - the id of the germplasm record to be retrieved
	 * @return the Germplasm POJO representing the record
	 */
	Germplasm getGermplasmWithMethodType(Integer gid);

	/**
	 * Given a range of gid, return the list of all Germplasm.
	 *
	 * @param startGID - the start ID of the range of germplasm gids
	 * @param endGID - the end ID of the range of germplasm gids
	 * @return List of Germplasm POJOs
	 */
	List<Germplasm> getGermplasmByGidRange(int startGID, int endGID);

	/**
	 * Given a List of GIDs, return the list of all Germplasm.
	 *
	 * @param gids the gids
	 * @return the germplasms
	 */
	List<Germplasm> getGermplasms(List<Integer> gids);

	/**
	 * Given a List of GIDs, return the list of Germplasm without group assigned (mgid = 0 or mgid is null).
	 *
	 * @param gids the gids
	 * @return the germplasms
	 */
	List<Germplasm> getGermplasmWithoutGroup(List<Integer> gids);

	/**
	 * Given a List of GIDs, return the list of all Germplasm together with their PreferredName.
	 *
	 * @param gids the gids
	 * @return the preferred names by gids
	 */
	Map<Integer, String> getPreferredNamesByGids(List<Integer> gids);

	/**
	 * @return the preferred name ids by gids
	 */
	Map<Integer, Integer> getPreferredNameIdsByGIDs(List<Integer> gids);

	/**
	 * Given a List of GIDs, return the list of gids mapped to their corresponding location name.
	 *
	 * @param gids the gids
	 * @return Map<Integer, String> - map of gids to their corresponding location name
	 */
	Map<Integer, String> getLocationNamesByGids(List<Integer> gids);

	/**
	 * Search for germplasms given a search term
	 *
	 * @param germplasmSearchParameter - contains all data needed for the germplasm search
	 * @return List of Germplasms
	 */
	List<Germplasm> searchForGermplasm(GermplasmSearchParameter germplasmSearchParameter);

	/**
	 * Get the gids of the result of germplasm search given a search term.
	 *
	 * @param germplasmSearchParameter - contains all data needed for the germplasm search
	 * @return List of Germplasm GIDs
	 */
	Set<Integer> retrieveGidsOfSearchGermplasmResult(GermplasmSearchParameter germplasmSearchParameter);

	/**
	 * Please use LocationDataManager.getLocationsByIDs().
	 *
	 * Gets the locations by the given IDs.
	 *
	 * @param ids Location IDs
	 * @return the corresponding Locations
	 * @deprecated
	 */
	@Deprecated
	List<Location> getLocationsByIDs(List<Integer> ids);

	/**
	 * Gets the methods by IDs.
	 *
	 * @param ids the Method Ids
	 * @return the methods corresponding to the given IDs
	 */
	List<Method> getMethodsByIDs(List<Integer> ids);

	List<Method> getNonGenerativeMethodsByID(List<Integer> ids);

	/**
	 * Get gDates given GIDs.
	 *
	 * @param gids the gids
	 * @return <gid, integerdatevalue>
	 */
	Map<Integer, Integer> getGermplasmDatesByGids(List<Integer> gids);

	/**
	 * Get methods given GIDs.
	 *
	 * @param gids the gids
	 * @return Map<gid, method>
	 */
	Map<Integer, Object> getMethodsByGids(List<Integer> gids);

	/**
	 * Gets the method by code.
	 *
	 * @param code the code
	 * @param programUUID - uniqueId of the current program
	 * @return the method by code
	 */
	Method getMethodByCode(String code, String programUUID);

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

	/**
	 * Gets the method by name.
	 *
	 * @param name the code
	 * @param programUUID - uniqueID of the current program
	 * @return the method by name
	 */
	Method getMethodByName(String name, String programUUID);

	List<Germplasm> getProgenitorsByGIDWithPrefName(Integer gid);

	/**
	 * Gets the list of favorite methods/locations
	 *
	 * @param type - can be FavoriteType.METHOD or FavoriteType.LOCATION
	 * @param programUUID - unique id of the program where the favorites location/method were created
	 * @return list of ProgramFavorite
	 */
	List<ProgramFavorite> getProgramFavorites(ProgramFavorite.FavoriteType type, String programUUID);

	/**
	 * Gets the list of favorite methods/locations
	 *
	 * @param type - can be FavoriteType.METHOD or FavoriteType.LOCATION
	 * @param max - maximum number of records to return
	 * @param programUUID - unique id of the program where the favorites location/method were created
	 * @return list of ProgramFavorite
	 */
	List<ProgramFavorite> getProgramFavorites(ProgramFavorite.FavoriteType type, int max, String programUUID);

	/**
	 * count favorite methods/locations
	 *
	 * @param type - can be FavoriteType.METHOD or FavoriteType.LOCATION
	 * @return count of ProgramFavorite list
	 */
	int countProgramFavorites(ProgramFavorite.FavoriteType type);

	/**
	 * Saves the list of favorite methods/locations
	 *
	 * @param list of ProgramFavorite
	 * @return none
	 */
	void saveProgramFavorites(List<ProgramFavorite> list);

	/**
	 * Saves a favorite method/location
	 *
	 * @param favorite to be saved
	 * @return none
	 */
	void saveProgramFavorite(ProgramFavorite favorite);

	/**
	 * Deletes a list of favorite methods/locations
	 *
	 * @param list of ProgramFavorite
	 * @return none
	 */
	void deleteProgramFavorites(List<ProgramFavorite> list);

	/**
	 * Deletes a favorite method/location
	 *
	 * @param favorite the code
	 * @return none
	 */
	void deleteProgramFavorite(ProgramFavorite favorite);

	/**
	 * check if name and standardized version of it already exists.
	 *
	 * @param name
	 * @return
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

	PedigreeDTO getPedigree(Integer germplasmDbId, String notation, final Boolean includeSiblings);

	ProgenyDTO getProgeny(Integer germplasmDbId);

	/*
	 * get the Germplasm from the crop database based on local gid reference
	 *
	 * @param lgid
	 */
	Germplasm getGermplasmByLocalGid(Integer lgid);

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

	/**
	 * Return the count of germplasm search results based on the following parameters:
	 *
	 ** @param germplasmSearchParameter - contains all data needed for the germplasm search
	 * @return
	 */
	Integer countSearchForGermplasm(GermplasmSearchParameter germplasmSearchParameter);

	List<Method> getDerivativeAndMaintenanceMethods(List<Integer> ids);

	/**
	 * Given a set of gids return the gid and all its parents including all their names.
	 *
	 * @param gids - the id of the germplasm record to be retrieved
	 * @param numberOfLevels the number of levels to traverse. Be careful do not go crazy
	 * @return a list of germplasms with all names populated in them
	 */
	List<Germplasm> getGermplasmWithAllNamesAndAncestry(Set<Integer> gids, int numberOfLevels);

	/**
	 * Returns the count of records that matched the gids in the specified list.
	 *
	 * @param gids - The list of gids to match for in the germplasm table
	 * @return
	 */
	long countMatchGermplasmInList(Set<Integer> gids);

	/**
	 * Returns map of list of names with given gids and ntype ids
	 *
	 * @param gids
	 * @param ntypeIds
	 * @return map of list of names with gid as key
	 */
	Map<Integer, List<Name>> getNamesByGidsAndNTypeIdsInMap(List<Integer> gids, List<Integer> ntypeIds);

	/**
	 * Returns the list of favorite methods in the current program
	 *
	 * @param mType
	 * @param programUUID
	 * @return list of favorite methods
	 */
	List<Method> getFavoriteMethodsByMethodType(String methodType, String programUUID);

	public List<Germplasm> getSortedGermplasmWithPrefName(final List<Integer> gids);

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
	 *
	 * @return List of all no bulking methods
	 */
	List<Method> getAllNoBulkingMethods();

	/**
	 *
	 * @param ids
	 * @return List of all no-bulking methods for a list of ids
	 */
	List<Method> getNoBulkingMethodsByIdList(final List<Integer> ids);

	/**
	 *
	 * @return List of all no-bulking and no generative methods
	 */
	List<Method> getAllMethodsNotBulkingNotGenerative();

	/**
	 *
	 * @param type
	 * @param programUUID
	 * @return List of all no-bulking methods given a type and a program
	 */
	List<Method> getNoBulkingMethodsByType(final String type, final String programUUID);


	/**
	 * Given a List of GIDs, return a Map of GIDs to PreferredName.
	 *
	 * @param gids the gids
	 * @return the preferred names by gids
	 */
	Map<Integer, String> getGroupSourcePreferredNamesByGids(final List<Integer> gids);

	/**
	 * Given a List of GIDs, return a Map of GIDs to PreferredName.
	 *
	 * @param gids the gids
	 * @return the preferred names by gids
	 */
	Map<Integer, String> getImmediateSourcePreferredNamesByGids(final List<Integer> gids);

	/**
	 *
	 * @param gid
	 * @param attributeName
	 * @return Attribute value that matches with attribute name and gid
	 */
	String getAttributeValue (final Integer gid, final String attributeName);

	void save(Germplasm germplasm);

	/**
	 * Get the NamingConfiguration by name
	 * @param name
	 * @return
	 */
	NamingConfiguration getNamingConfigurationByName(String name);

	GermplasmDTO getGermplasmDTOByGID (Integer gid);

	List<GermplasmDTO> searchGermplasmDTO(GermplasmSearchRequestDto germplasmSearchRequestDTO, Integer page, Integer pageSize);

	long countGermplasmDTOs(GermplasmSearchRequestDto germplasmSearchRequestDTO);

	Germplasm getUnknownGermplasmWithPreferredName();

	List<Integer> addOrUpdateGermplasm(final List<Germplasm> germplasms, final Operation operation);

	long countGermplasmByStudy(Integer studyDbId);

	List<GermplasmDTO> getGermplasmByStudy(Integer studyDbId, Integer pageNumber, Integer pageSize);

	List<AttributeDTO> getAttributesByGid(
		String gid, List<String> attributeDbIds, Integer pageSize, Integer pageNumber);

	long countAttributesByGid(String gid, List<String> attributeDbIds);

	List<Attribute> getAttributeByIds(List<Integer> ids);

	List<String> getNamesByGidsAndPrefixes(List<Integer> gids, List<String> prefixes);

	List<Germplasm> getExistingCrosses(Integer femaleParent, List<Integer> maleParentIds, Optional<Integer> gid);

	boolean hasExistingCrosses(Integer femaleParent, List<Integer> maleParentIds, Optional<Integer> gid);

	void generateGermplasmUUID(final CropType crop, final List<Germplasm> germplasmList);
}
