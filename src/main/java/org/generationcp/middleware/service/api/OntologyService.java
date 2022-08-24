/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.service.api;

import java.util.List;
import java.util.Set;

import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.StandardVariableSummary;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.dms.VariableConstraints;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Method;
import org.generationcp.middleware.domain.oms.Property;
import org.generationcp.middleware.domain.oms.Scale;
import org.generationcp.middleware.domain.oms.StandardVariableReference;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.oms.TraitClass;
import org.generationcp.middleware.domain.oms.TraitClassReference;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Operation;

/**
 *
 * This is the API for Ontology Browser requirements.
 *
 */
public interface OntologyService {

	/* ======================= STANDARD VARIABLE ================================== */

	/**
	 * Gets the standard variable given the standard variable id.
	 *
	 * @param stdVariableId the standard variable id
	 * @return the standard variable
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	StandardVariable getStandardVariable(int stdVariableId, String programUUID) throws MiddlewareException;

	/**
	 * Gets the standard variables given a list of ids
	 *
	 * @param stdVariableIds the list of standard variable ids
	 * @return the list of standard variables
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<StandardVariable> getStandardVariables(List<Integer> standardVariableIds, String programUUID) throws MiddlewareException;

	/**
	 * Gets standard variable summaries for given a list of ids
	 *
	 * @param stdVariableIds the list of standard variable ids
	 * @return the list of standard variable summaries
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<StandardVariableSummary> getStandardVariableSummaries(List<Integer> standardVariableIds) throws MiddlewareQueryException;

	/**
	 * Gets the standard variable given the property, method and scale.
	 *
	 * @param propertyId the property id
	 * @param scaleId the scale id
	 * @param methodId the method id
	 * @return the standard variable
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	StandardVariable getStandardVariable(Integer propertyId, Integer scaleId, Integer methodId, String programUUID) throws MiddlewareException;

	/**
	 * Gets the list of standard variables given the name or synonym.
	 *
	 * @param nameOrSynonym the name or synonym to match
	 * @return the standard variables
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<StandardVariable> getStandardVariables(String nameOrSynonym,String programUUID) throws MiddlewareException;

	/**
	 * Adds a standard variable.
	 *
	 * @param stdVariable the standard variable to add
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	void addStandardVariable(StandardVariable stdVariable,String programUUID) throws MiddlewareQueryException;

	/**
	 * Gets the all standard variables.
	 *
	 * @return the standard variables
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	// Review: why do we need such a method? Can load a large portion of the DB.
	Set<StandardVariable> getAllStandardVariables(String programUUID) throws MiddlewareException;

	/**
	 * Gets all the standard variables matching the given trait class ID.
	 *
	 * @param traitClassId the trait class id
	 * @return the standard variables by trait class
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<StandardVariable> getStandardVariablesByTraitClass(Integer traitClassId,String programUUID) throws MiddlewareException;

	/**
	 * Gets all the standard variables matching the given property ID.
	 *
	 * @param propertyId the property id
	 * @return the standard variables by property
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<StandardVariable> getStandardVariablesByProperty(Integer propertyId, String programUUID) throws MiddlewareException;

	/**
	 * Gets all the standard variables matching the given method ID.
	 *
	 * @param methodId the method id
	 * @return the standard variables by method
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<StandardVariable> getStandardVariablesByMethod(Integer methodId, String programUUID) throws MiddlewareException;

	/**
	 * Gets all the standard variables matching the given scale ID.
	 *
	 * @param scaleId the scale id
	 * @return the standard variables by scale
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<StandardVariable> getStandardVariablesByScale(Integer scaleId, String programUUID) throws MiddlewareException;

	/**
	 * Gets the all terms by cv id.
	 *
	 * @param cvId the cv id
	 * @return the terms by cv id
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Term> getAllTermsByCvId(CvId cvId) throws MiddlewareQueryException;

	/**
	 * Gets the standard variable id by term id.
	 *
	 * @param cvTermId the cv term id
	 * @param termId the term id
	 * @return the standard variable id by term id
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Integer getStandardVariableIdByTermId(int cvTermId, TermId termId) throws MiddlewareQueryException;

	/**
	 * Insert or Update a Standard Variable.
	 *
	 * @param standardVariable the standard variable
	 * @param operation the operation
	 * @throws MiddlewareQueryException the middleware query exception
	 * @throws MiddlewareException the middleware exception
	 */
	void saveOrUpdateStandardVariable(StandardVariable standardVariable, Operation operation) throws MiddlewareException;

	/**
	 * Delete standard variable.
	 *
	 * @param stdVariableId the std variable id
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	void deleteStandardVariable(int stdVariableId) throws MiddlewareQueryException;

	/**
	 * Adds or update the given standard variable's min-max constraints.
	 *
	 * @param standardVariableId the standard variable id
	 * @param constraints the constraints
	 * @throws MiddlewareQueryException the middleware query exception
	 * @throws MiddlewareException the middleware exception
	 */
	void addOrUpdateStandardVariableMinMaxConstraints(int standardVariableId, VariableConstraints constraints) throws MiddlewareException;

	/**
	 * Deletes the given standard variable's min-max constraints.
	 *
	 * @param standardVariableId the standard variable id
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	void deleteStandardVariableMinMaxConstraints(int standardVariableId) throws MiddlewareQueryException;

	/**
	 * Adds a valid value to the given standard variable. Checks if the standard variable passed is categorical.
	 *
	 * @param variable the variable
	 * @param validValue the valid value to add
	 * @return the enumeration containing the id of the new valid value
	 * @throws MiddlewareQueryException the middleware query exception
	 * @throws MiddlewareException the middleware exception
	 */
	Enumeration addStandardVariableValidValue(StandardVariable variable, Enumeration validValue) throws MiddlewareException;

	/**
	 * Deletes the given valid value from a standard variable.
	 *
	 * @param standardVariableId the standard variable id
	 * @param validValueId the valid value id
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	void deleteStandardVariableValidValue(int standardVariableId, int validValueId) throws MiddlewareQueryException;

	/**
	 * Save or update standard variable enumeration.
	 *
	 * @param variable the variable
	 * @param enumeration the enumeration
	 * @throws MiddlewareQueryException the middleware query exception
	 * @throws MiddlewareException the middleware exception
	 */
	void saveOrUpdateStandardVariableEnumeration(StandardVariable variable, Enumeration enumeration) throws MiddlewareException;

	/* ======================= PROPERTY ================================== */

	/**
	 * Gets the property with the given id.
	 *
	 * @param id the property id to match
	 * @return the matching property
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Property getProperty(int id) throws MiddlewareQueryException;

	/**
	 * Gets the property with the given name.
	 *
	 * @param name the name of the property
	 * @return the matching property
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Property getProperty(String name) throws MiddlewareQueryException;

	/**
	 * Gets all properties from Central and Local.
	 *
	 * @return All the properties
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Property> getAllProperties() throws MiddlewareQueryException;

	/**
	 * Adds a property. If the property is already found in the local database, it simply retrieves the record found.
	 *
	 * @param name the name
	 * @param definition the definition
	 * @param isA the is a type
	 * @return the Term entry corresponding to the newly-added property
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Property addProperty(String name, String definition, int isA) throws MiddlewareQueryException;

	/**
	 * Adds or updates a property with the given name and definition. If the given property name is not found in the databases, a new record
	 * is added to local database. If the given name is already found in local database, update is performed. If the given name is already
	 * found in central database, no update is performed.
	 *
	 * @param name the name of the property
	 * @param definition the defintion of the property
	 * @param isAId the id of the trait class of the property
	 * @param cropOntologyId the crop ontology id
	 * @return the Term of the added / updated property
	 * @throws MiddlewareQueryException the middleware query exception
	 * @throws MiddlewareException the middleware exception
	 */
	Property addOrUpdateProperty(String name, String definition, int isAId, String cropOntologyId) throws MiddlewareException;

	/**
	 * Updates the given property. This searches for the id. If it exists, the entry in the database is replaced with the new value.
	 * 
	 * @param property The Property to update
	 * @throws MiddlewareQueryException the middleware query exception
	 * @throws MiddlewareException the middleware exception
	 */
	void updateProperty(Property property) throws MiddlewareException;

	/**
	 * Delete property.
	 *
	 * @param cvTermId the cv term id
	 * @param isAId the is a id
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	void deleteProperty(int cvTermId, int isAId) throws MiddlewareQueryException;

	/* ======================= SCALE ================================== */

	/**
	 * Gets the scale with the given id.
	 *
	 * @param id the id to match
	 * @return the matching scale
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Scale getScale(int id) throws MiddlewareQueryException;

	/**
	 * Gets the scale with the given name.
	 *
	 * @param name the name to match
	 * @return the matching scale
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Scale getScale(String name) throws MiddlewareQueryException;
	
	/**
	 * Gets all scales from Central and Local.
	 *
	 * @return All the scales
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Scale> getAllScales() throws MiddlewareQueryException;

	/**
	 * Adds a scale. If the scale is already found in the local database, it simply retrieves the record found.
	 *
	 * @param name the name
	 * @param definition the definition
	 * @return the Term entry corresponding to the newly-added scale
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Scale addScale(String name, String definition) throws MiddlewareQueryException;

	/**
	 * Adds or updates a scale with the given name and definition. If the given scale name is not found in the databases, a new record is
	 * added to local database. If the given name is already found in local database, update is performed. If the given name is already
	 * found in central database, no update is performed.
	 *
	 * @param name the name of the scale
	 * @param definition the defintion of the scale
	 * @return the Term of the added / updated scale
	 * @throws MiddlewareQueryException the middleware query exception
	 * @throws MiddlewareException the middleware exception
	 */
	Scale addOrUpdateScale(String name, String definition) throws MiddlewareException;

	/**
	 * Updates the given scale. This searches for the id. If it exists, the entry in the database is replaced with the new value.
	 * 
	 * @param scale The Scale to update
	 * @throws MiddlewareQueryException the middleware query exception
	 * @throws MiddlewareException the middleware exception
	 */
	void updateScale(Scale scale) throws MiddlewareException;

	/**
	 * Delete scale.
	 *
	 * @param cvTermId the cv term id
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	void deleteScale(int cvTermId) throws MiddlewareQueryException;

	/* ======================= METHOD ================================== */

	/**
	 * Gets the method with the given id.
	 *
	 * @param id the id to match
	 * @return the matching method
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Method getMethod(int id) throws MiddlewareQueryException;

	/**
	 * Gets the method with the given name.
	 *
	 * @param name the name to match
	 * @return the matching method
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Method getMethod(String name) throws MiddlewareQueryException;

	/**
	 * Gets the all methods from Central and Local.
	 *
	 * @return All the methods
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Method> getAllMethods() throws MiddlewareQueryException;

	/**
	 * Adds a method. If the method is already found in the local database, it simply retrieves the record found.
	 *
	 * @param name the name
	 * @param definition the definition
	 * @return the Term entry corresponding to the newly-added method
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Method addMethod(String name, String definition) throws MiddlewareQueryException;

	/**
	 * Adds or updates a method with the given name and definition. If the given method name is not found in the databases, a new record is
	 * added to local database. If the given name is already found in local database, update is performed. If the given name is already
	 * found in central database, no update is performed.
	 *
	 * @param name the name of the method
	 * @param definition the defintion of the method
	 * @return the Term of the added / updated method
	 * @throws MiddlewareQueryException the middleware query exception
	 * @throws MiddlewareException the middleware exception
	 */
	Method addOrUpdateMethod(String name, String definition) throws MiddlewareException;

	/**
	 * Updates the given method. This searches for the id. If it exists, the entry in the database is replaced with the new value.
	 * 
	 * @param method The Method to update
	 * @throws MiddlewareQueryException the middleware query exception
	 * @throws MiddlewareException the middleware exception
	 */
	void updateMethod(Method method) throws MiddlewareException;

	/**
	 * Delete method.
	 *
	 * @param cvTermId the cv term id
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	void deleteMethod(int cvTermId) throws MiddlewareQueryException;

	/* ======================= OTHERS ================================== */

	/**
	 * Gets all the data types.
	 *
	 * @return the data types
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Term> getAllDataTypes() throws MiddlewareQueryException;

	/**
	 * Gets all the trait groups, its properties and standard variables in a hierarchical structure. All the trait classes under crop
	 * research ontology and ontology trait class are retrieved.
	 * 
	 * @param includePropertiesAndVariable true if we want to include property and variable information
	 * @return the trait groups
	 * @throws MiddlewareQueryException the middleware query exception
	 */

	List<TraitClassReference> getAllTraitGroupsHierarchy(boolean includePropertiesAndVariable) throws MiddlewareQueryException;

	/**
	 * Adds a new trait class to the database. Creates a new cvterm and cvterm_relationship entry in the local database. Returns a negative
	 * id.
	 *
	 * @param name the name
	 * @param definition the definition
	 * @param parentTraitClassId can be either TermId.ONTOLOGY_TRAIT_CLASS.getId() or TermId.ONTOLOGY_RESEARCH_CLASS.getId() for first-level
	 *        class or another termId if not a first-level class
	 * @return the TraitClass added
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	TraitClass addTraitClass(String name, String definition, int parentTraitClassId) throws MiddlewareQueryException;

	/**
	 * Adds a new trait class to the database if it does not exist. Otherwise, the existing trait class & its relationship is updated.
	 * Returns a negative id.
	 *
	 * @param name the name
	 * @param definition the definition
	 * @param parentTraitClassId can be either TermId.ONTOLOGY_TRAIT_CLASS.getId() or TermId.ONTOLOGY_RESEARCH_CLASS.getId() for first-level
	 *        class or another termId if not a first-level class
	 * @return the TraitClass added
	 * @throws MiddlewareQueryException the middleware query exception
	 * @throws MiddlewareException the middleware exception
	 */
	TraitClass addOrUpdateTraitClass(String name, String definition, int parentTraitClassId) throws MiddlewareException;

	/**
	 * Updates the given trait class. This searches for the id. If it exists, the entry in the database is replaced with the new value.
	 *
	 * @param traitClass The TraitClass to update
	 * @return the trait class
	 * @throws MiddlewareQueryException the middleware query exception
	 * @throws MiddlewareQueryException the middleware query exception
	 * @throws MiddlewareException the middleware exception
	 */
	TraitClass updateTraitClass(TraitClass traitClass) throws MiddlewareException;

	/**
	 * Delete trait class.
	 *
	 * @param cvTermId the cv term id
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	void deleteTraitClass(int cvTermId) throws MiddlewareQueryException;

	/**
	 * Gets all roles.
	 *
	 * @return all the roles
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Term> getAllRoles() throws MiddlewareQueryException;

	/**
	 * Adds a new Term to the database. Creates a new cvterm entry in the local database. Returns a negative id.
	 *
	 * @param name the name
	 * @param definition the definition
	 * @param cvId the cv id
	 * @return the term
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Term addTerm(String name, String definition, CvId cvId) throws MiddlewareQueryException;

	/**
	 * Find term by name.
	 *
	 * @param termId the term id
	 * @return the term
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Term getTermById(int termId) throws MiddlewareQueryException;

	/**
	 * Gets the phenotypic type.
	 *
	 * @param termId the term id
	 * @return the phenotypic type
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	PhenotypicType getPhenotypicTypeById(Integer termId) throws MiddlewareQueryException;

	/**
	 * Find term by name.
	 *
	 * @param name the name
	 * @param cvId the cv id
	 * @return the term
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Term findTermByName(String name, CvId cvId) throws MiddlewareQueryException;

	/**
	 * Returns all Properties with its trait class.
	 *
	 * @return the properties with trait class
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Property> getAllPropertiesWithTraitClass() throws MiddlewareQueryException;

	/**
	 * Validates if the enumeration is being used.
	 * 
	 * @param standardVariableId
	 * @param enumerationId
	 * @return true if valid
	 * @throws MiddlewareQueryException
	 */
	boolean validateDeleteStandardVariableEnumeration(int standardVariableId, int enumerationId) throws MiddlewareQueryException;

	/**
	 * Return all invenotry scales.
	 * 
	 * @return
	 * @throws MiddlewareQueryException
	 */
	List<Scale> getAllInventoryScales() throws MiddlewareQueryException;
	
	/**
	 * Find inventory scale by name
	 * 
	 * @param name
	 * @return Scale
	 * @throws MiddlewareQueryException
	 */
	Scale getInventoryScaleByName(final String name) throws MiddlewareQueryException;

	List<ValueReference> getDistinctStandardVariableValues(int stdVarId) throws MiddlewareQueryException;
}
