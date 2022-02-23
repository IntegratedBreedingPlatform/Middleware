/*******************************************************************************
 *
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

import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.NameSynonym;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.StandardVariableSummary;
import org.generationcp.middleware.domain.dms.VariableConstraints;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Property;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.oms.TraitClass;
import org.generationcp.middleware.domain.oms.TraitClassReference;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.manager.Operation;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is the API for retrieving ontology data from the CHADO schema.
 */
public interface OntologyDataManager {

	/**
	 * Retrieves a Term record given its id. This can also be used to retrieve traits, methods and scales.
	 *
	 * @param termId the term id
	 * @return the term by id
	 */
	Term getTermById(int termId);

	/**
	 * Retrieves a StandardVariable given its id. After the first read, the variable is cached in memory.
	 *
	 * @param stdVariableId the std variable id
	 * @return the standard variable
	 */
	StandardVariable getStandardVariable(int stdVariableId, String programUUID) throws MiddlewareException;

	/**
	 * Retrieves a list of Standard Variables from a list of Ids.
	 *
	 * @param ids - list of ids
	 * @return list of StandardVariable instances
	 */
	List<StandardVariable> getStandardVariables(List<Integer> ids, String programUUID) throws MiddlewareException;

	/**
	 * Gets standard variable summaries for given a list of ids. Returns an empty list if no matches are found.
	 *
	 * @param standardVariableIds the list of standard variable ids
	 * @return the list of standard variable summaries
	 */
	List<StandardVariableSummary> getStandardVariableSummaries(List<Integer> standardVariableIds);

	/**
	 * Find standard variables by name or synonym.
	 *
	 * @param nameOrSynonym the name or synonym
	 * @return the sets the
	 */
	Set<StandardVariable> findStandardVariablesByNameOrSynonym(String nameOrSynonym, String programUUID) throws MiddlewareException;

	/**
	 * Adds a StandardVariable to the database. Must provide the property, method, scale, dataType, and storedIn info. Otherwise, it will
	 * throw an exception.
	 *
	 * @param stdVariable the std variable
	 */
	void addStandardVariable(StandardVariable stdVariable, String programUUID);

	/**
	 * Find method by id.
	 *
	 * @param id the id
	 * @return the term
	 */
	Term findMethodById(int id);

	/**
	 * Find method by name.
	 *
	 * @param name the name
	 * @return the term
	 */
	Term findMethodByName(String name);

	/**
	 * Retrieves the StandardVariable given the property, scale and method names.
	 *
	 * @param property the property
	 * @param scale    the scale
	 * @param method   the method
	 * @return StandardVariable
	 */
	StandardVariable findStandardVariableByTraitScaleMethodNames(String property, String scale, String method, String programUUID)
		throws MiddlewareException;

	/**
	 * Returns the list of Term entries based on the given CvId. The CvId can be CvId.PROPERTIES, CvId.METHODS, CvId.SCALES, CvId.VARIABLES.
	 * <p>
	 * This can be used to get all scales, all traits, all trait methods, all properties, all methods and all variables.
	 *
	 * @param cvId the cv id
	 * @return the all terms by cv id
	 */
	List<Term> getAllTermsByCvId(CvId cvId);

	/**
	 * Returns Term based on the given name and cvid.
	 *
	 * @param name the name
	 * @param cvId the cv id
	 * @return Term
	 */
	Term findTermByName(String name, CvId cvId);

	/**
	 * Adds a new Term to the database. Creates a new cvterm entry in the local database. Returns a negative id.
	 *
	 * @param name       the name
	 * @param definition the definition
	 * @param cvId       the cv id
	 * @return the term
	 */
	Term addTerm(String name, String definition, CvId cvId);

	/**
	 * Updates an existing term in the database. This method searches for the given id in local. If it exists, the corresponding name and
	 * definition are updated.
	 *
	 * @param term the term
	 * @throws MiddlewareException the middleware exception
	 */
	void updateTerm(Term term) throws MiddlewareException;

	/**
	 * Updates the terms.
	 *
	 * @param terms
	 * @throws MiddlewareException
	 */
	void updateTerms(List<Term> terms) throws MiddlewareException;

	/**
	 * Returns the list of Term entries based on possible data types.
	 *
	 * @return list of data type Term objects
	 */
	List<Term> getDataTypes();

	/**
	 * Returns the key-value pairs of PhenotypicType - StandardVariable.
	 *
	 * @param type      the type
	 * @param start     the start
	 * @param numOfRows the num of rows
	 * @return Map of PhenotypicType - StandardVariable
	 */
	Map<String, StandardVariable> getStandardVariablesForPhenotypicType(PhenotypicType type, String programUUID, int start, int numOfRows)
		throws MiddlewareException;

	/**
	 * Returns the standard variables associated to a project from projectprop, cvterm or trait - in the given order.
	 * <p>
	 * 1. Search for DISTINCT standard variables used for projectprop records where projectprop.value equals input name (eg. REP)
	 * 2. If no variable found, search for cvterm (standard variables) with given name.
	 * 3. If no variable still found for steps 1 and 2, treat the
	 * header as a trait / property name. Search for trait with given name and return the standard variables using that trait (if any)
	 *
	 * @param headers the headers
	 * @return The key in map would be the header string (In UPPERCASE). If no standard variable list found, an empty list on map is returned for that
	 * header key.
	 */
	Map<String, List<StandardVariable>> getStandardVariablesInProjects(List<String> headers, String programUUID) throws MiddlewareException;

	/**
	 * Retrieves the List of Terms matching the given nameOrSynonym and CvId.
	 *
	 * @param nameOrSynonym the name or synonym
	 * @param cvId          the cv id
	 * @return the list
	 */
	List<Term> findTermsByNameOrSynonym(String nameOrSynonym, CvId cvId);

	/**
	 * Adds a new property to the database that adds the property term and it's is a relationship) Creates a new cvterm entry in the local
	 * database and a cvterm_relationship of type is_a Returns the added term.
	 *
	 * @param name       the name
	 * @param definition the definition
	 * @param isA        the is a
	 * @return Term
	 */
	Term addProperty(String name, String definition, int isA);

	/**
	 * @param subjectId
	 * @param objectId
	 * @param typeId
	 */
	void addCvTermRelationship(int subjectId, int objectId, int typeId);

	/**
	 * Given the termId, retrieve the Property POJO.
	 *
	 * @param termId the term id
	 * @return property
	 */
	Property getProperty(int termId);

	/**
	 * Given the name, retrieve the Property POJO.
	 *
	 * @param name the name
	 * @return property
	 */
	Property getProperty(String name);

	/**
	 * Retrieves ALL the trait classes containing the hierarchical structure of the trait classes. If includePropertiesAndVariables = true,
	 * it retrieves the properties and standard variables in a hierarchical structure as well: Trait Group --> Properties --> Standard
	 * Variables.
	 * <p>
	 * The list is returned in alphabetical order of the name.
	 *
	 * @param includePropertiesAndVariables true if we want to load the property and standard variable, else false
	 * @return the trait groups
	 */

	List<TraitClassReference> getAllTraitGroupsHierarchy(boolean includePropertiesAndVariables);

	/**
	 * Retrieves all the Term entries based on the given list of ids.
	 *
	 * @param ids the ids
	 * @return the terms by ids
	 */
	List<Term> getTermsByIds(List<Integer> ids);

	/**
	 * Adds the trait class.
	 *
	 * @param name               the name
	 * @param definition         the definition
	 * @param parentTraitClassId the parent trait class id
	 * @return the term
	 */
	TraitClass addTraitClass(String name, String definition, int parentTraitClassId);

	/**
	 * Gets all the standard variables.
	 *
	 * @return the all standard variable
	 */
	Set<StandardVariable> getAllStandardVariables(String programUUID) throws MiddlewareException;

	/**
	 * Gets the all standard variables based on the parameters with values. At least one parameter needs to have a value. If a standard
	 * variable has no trait class, it is not included in the result.
	 *
	 * @param traitClassId the trait class id
	 * @param propertyId   the property id
	 * @param methodId     the method id
	 * @param scaleId      the scale id
	 * @return the standard variables matching the given parameters
	 */
	List<StandardVariable> getStandardVariables(
		Integer traitClassId, Integer propertyId, Integer methodId, Integer scaleId,
		String programUUID) throws MiddlewareException;

	/**
	 * @param property       the specific Property Term element to which the crop ontology ID will be saved
	 * @param cropOntologyID the crop ontology ID to be saved
	 * @
	 */
	void addOrUpdateCropOntologyID(Property property, String cropOntologyID);

	/**
	 * Adds or updates the term and relationship.
	 *
	 * @param name       the name
	 * @param definition the definition
	 * @param cvId       the cv id
	 * @param typeId     the type id
	 * @param objectId   the object id
	 * @return the term
	 * @throws MiddlewareException the middleware exception
	 */
	Term addOrUpdateTermAndRelationship(String name, String definition, CvId cvId, int typeId, int objectId) throws MiddlewareException;

	/**
	 * Updates the given term and its associated entry in the cvterm_relationship table. Searches first if the given term id exists. If it
	 * exists in local, the records are updated.
	 *
	 * @param term     The term to update
	 * @param typeId   the type id of the relationship between the term id and the objectId
	 * @param objectId the object id
	 * @return the term
	 * @throws MiddlewareException the middleware exception
	 */
	Term updateTermAndRelationship(Term term, int typeId, int objectId) throws MiddlewareException;

	/**
	 * Adds or updates the term.
	 *
	 * @param name       the name
	 * @param definition the definition
	 * @param cvId       the cv id
	 * @return the term
	 * @throws MiddlewareException the middleware exception
	 */
	Term addOrUpdateTerm(String name, String definition, CvId cvId) throws MiddlewareException;

	/**
	 * Gets the standard variable id by term id.
	 *
	 * @param cvTermId the cv term id
	 * @param termId   the term id
	 * @return the standard variable id by term id
	 */
	Integer getStandardVariableIdByTermId(int cvTermId, TermId termId);

	/**
	 * Insert or Update a Standard Variable.
	 *
	 * @param standardVariable the standard variable
	 * @param operation        the operation
	 * @throws MiddlewareException the middleware exception
	 */
	void saveOrUpdateStandardVariable(StandardVariable standardVariable, Operation operation) throws MiddlewareException;

	/**
	 * Adds or updates standard variable constraints.
	 *
	 * @param standardVariableId the standard variable id
	 * @param constraints        the constraints
	 * @throws MiddlewareException the middleware exception
	 */
	void addOrUpdateStandardVariableConstraints(int standardVariableId, VariableConstraints constraints) throws MiddlewareException;

	/**
	 * Deletes standard variable constraints.
	 *
	 * @param standardVariableId the standard variable id
	 */
	void deleteStandardVariableLocalConstraints(int standardVariableId);

	/**
	 * Adds standard variable enumeration.
	 *
	 * @param variable    the variable
	 * @param enumeration the enumeration
	 * @return the enumeration
	 * @throws MiddlewareException the middleware exception
	 */
	Enumeration addStandardVariableEnumeration(StandardVariable variable, Enumeration enumeration) throws MiddlewareException;

	/**
	 * - Save or update standard variable enumeration. - The enumeration passed is treated as a new value if the id is null, otherwise it is
	 * treated as an update operation. - If the ID of the Enumeration passed is positive, a new entry is added to local. - If the ID of the
	 * Enumeration passed is negative, the existing entry in local is updated. - Only the name and description can be updated.
	 *
	 * @param variable    the variable
	 * @param enumeration the enumeration
	 * @throws MiddlewareException the middleware exception
	 */
	void saveOrUpdateStandardVariableEnumeration(StandardVariable variable, Enumeration enumeration) throws MiddlewareException;

	/**
	 * Deletes standard variable enumeration.
	 *
	 * @param standardVariableId the standard variable id
	 * @param validValueId       the valid value id
	 */
	void deleteStandardVariableEnumeration(int standardVariableId, int validValueId);

	/**
	 * Delete term.
	 *
	 * @param cvTermId the cv term id
	 * @param cvId     the cv id
	 */
	void deleteTerm(int cvTermId, CvId cvId);

	/**
	 * @param originalVariableTermID the term ID of the standard variable that we are retrieving the derived analysis variable of
	 * @param analysisMethodID       the term ID representing the analysis method used in deriving the analysis variable
	 * @return the term ID of the derived analysis variable if available, or null if non-existing
	 */
	Integer retrieveDerivedAnalysisVariable(Integer originalVariableTermID, Integer analysisMethodID);

	/**
	 * Delete term and relationship.
	 *
	 * @param cvTermId the cv term id
	 * @param cvId     the cv id
	 * @param typeId   the type id
	 * @param objectId the object id
	 */
	void deleteTermAndRelationship(int cvTermId, CvId cvId, int typeId, int objectId);

	/**
	 * Returns all Properties with its trait class.
	 *
	 * @return the all properties with trait class
	 */
	List<Property> getAllPropertiesWithTraitClass();

	/**
	 * Delete standard variable.
	 *
	 * @param stdVariableId the std variable id
	 */
	void deleteStandardVariable(int stdVariableId);

	/**
	 * Returns the variable id given the property, scale, method, and role (P-S-M-R).
	 *
	 * @param property the property
	 * @param scale    the scale
	 * @param method   the method
	 * @return the standard variable id by property scale method
	 */
	Integer getStandardVariableIdByPropertyScaleMethod(String property, String scale, String method);

	/**
	 * Returns the variable id given the property, scale, method (P-S-M).
	 *
	 * @param propertyId the property id
	 * @param scaleId    the scale id
	 * @param methodId   the method id
	 * @return the standard variable id by property scale method
	 */
	Integer getStandardVariableIdByPropertyIdScaleIdMethodId(Integer propertyId, Integer scaleId, Integer methodId)
	;

	/**
	 * validate if the enumeration is being used.
	 *
	 * @param standardVariableId
	 * @param enumerationId
	 * @return true if valid
	 * @
	 */
	boolean validateDeleteStandardVariableEnumeration(int standardVariableId, int enumerationId);

	/**
	 * Returns synonyms (if any) of given term (not limited to standard variable)
	 *
	 * @param termId
	 * @return
	 */
	List<NameSynonym> getSynonymsOfTerm(Integer termId);

	boolean isSeedAmountVariable(String variateProperty);

	/**
	 * Returns Term based on the given name and cv id.
	 *
	 * @param name the name
	 * @param cvId the cv id
	 * @return Term
	 */
	Term findTermByName(String name, int cvId);

}
