/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/

package org.generationcp.middleware.service.api;

import java.util.List;
import java.util.Set;

import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Method;
import org.generationcp.middleware.domain.oms.Property;
import org.generationcp.middleware.domain.oms.Scale;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TraitReference;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;

// TODO: Auto-generated Javadoc
/**
 * 
 * This is the API for Ontology Browser requirements.
 * 
 */
public interface OntologyService {
    
    /*======================= STANDARD VARIABLE ================================== */

    /**
     * Gets the standard variable given the standard variable id.
     *
     * @param stdVariableId the standard variable id
     * @return the standard variable
     * @throws MiddlewareQueryException the middleware query exception
     */
    StandardVariable getStandardVariable(int stdVariableId) throws MiddlewareQueryException;

    
    /**
     * Gets the standard variable given the property, method and scale.
     *
     * @param propertyId the property id
     * @param scaleId the scale id
     * @param methodId the method id
     * @return the standard variable
     * @throws MiddlewareQueryException the middleware query exception
     */
    StandardVariable getStandardVariable(Integer propertyId, Integer scaleId, Integer methodId) throws MiddlewareQueryException;

    
    /**
     * Gets the list of standard variables given the name or synonym.
     *
     * @param nameOrSynonym the name or synonym to match
     * @return the standard variables
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<StandardVariable> getStandardVariables(String nameOrSynonym) throws MiddlewareQueryException;
    
    
    /**
     * Adds a standard variable.
     *
     * @param stdVariable the standard variable  to add
     * @throws MiddlewareQueryException the middleware query exception
     */
    void addStandardVariable(StandardVariable stdVariable) throws MiddlewareQueryException;
    
   
    /**
     * Gets the all standard variables.
     *
     * @return the all standard variables
     * @throws MiddlewareQueryException the middleware query exception
     */
    Set<StandardVariable> getAllStandardVariables() throws MiddlewareQueryException;
    
    /**
     * Gets the all terms by cv id.
     *
     * @param cvId the cv id
     * @return the all terms by cv id
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Term> getAllTermsByCvId(CvId cvId) throws MiddlewareQueryException;
    
    /*======================= PROPERTY ================================== */
   
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
     * Adds or updates a property with the given name and definition. 
     * If the given property name is not found in the databases, a new record is added to local database.
     * If the given name is already found in local database, update is performed. 
     * If the given name is already found in central database, no update is performed.
     * 
     * @param name the name of the property
     * @param definition the defintion of the property
     * @param isAId the id of the trait class of the property
     * @return the Term of the added / updated property
     * @throws MiddlewareQueryException
     * @throws MiddlewareException 
     */
    Property addOrUpdateProperty(String name, String definition, int isAId) throws MiddlewareQueryException, MiddlewareException;

    /**
     * Save a property.
     *
     * @param propertyId the id of the property
     * @param isA the is a type
     * @return the Term entry corresponding to the newly-added property
     * @throws MiddlewareQueryException the middleware query exception
     */
    Term saveProperty(int propertyId, int isA) throws MiddlewareQueryException;

    /*======================= SCALE ================================== */

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
     * Adds or updates a scale with the given name and definition. 
     * If the given scale name is not found in the databases, a new record is added to local database.
     * If the given name is already found in local database, update is performed. 
     * If the given name is already found in central database, no update is performed.
     * 
     * @param name the name of the scale
     * @param definition the defintion of the scale
     * @return the Term of the added / updated scale
     * @throws MiddlewareQueryException
     * @throws MiddlewareException 
     */
    Scale addOrUpdateScale(String name, String definition) throws MiddlewareQueryException, MiddlewareException;

    /*======================= METHOD ================================== */

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
     * Adds or updates a method with the given name and definition. 
     * If the given method name is not found in the databases, a new record is added to local database.
     * If the given name is already found in local database, update is performed. 
     * If the given name is already found in central database, no update is performed.
     * 
     * @param name the name of the method
     * @param definition the defintion of the method
     * @return the Term of the added / updated method
     * @throws MiddlewareQueryException
     * @throws MiddlewareException 
     */
    Method addOrUpdateMethod(String name, String definition) throws MiddlewareQueryException, MiddlewareException;


    /*======================= OTHERS ================================== */


    /**
     * Gets all the data types.
     *
     * @return the data types
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Term> getAllDataTypes() throws MiddlewareQueryException;

  
    /**
     * Gets all the trait groups, its properties and standard variables in a hierarchical structure.
     *
     * @return the trait groups
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<TraitReference> getTraitGroups() throws MiddlewareQueryException;

    /**
     * Gets all trait classes.
     *
     * @return All the trait classes
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<TraitReference> getAllTraitClasses() throws MiddlewareQueryException;

    /**
     * Gets all roles.
     *
     * @return all the roles
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Term> getAllRoles() throws MiddlewareQueryException;

    /**
     * Count the number of projects the variable was used.
     *
     * @param variableId the variable id
     * @return the long
     * @throws MiddlewareQueryException the middleware query exception
     */
    long countProjectsByVariable(int variableId) throws MiddlewareQueryException;
    
    /**
     * Count the number of experiments the variable was used.
     *
     * @param variableId the variable id
     * @param storedInId the stored in id
     * @return the long
     * @throws MiddlewareQueryException the middleware query exception
     */
    long countExperimentsByVariable(int variableId, int storedInId) throws MiddlewareQueryException;
    
    /**
     * Adds a new Term to the database.
     * Creates a new cvterm entry in the local database.
     * Returns a negative id.
     *
     * @param name the name
     * @param definition the definition
     * @param cvId the cv id
     * @return the term
     * @throws MiddlewareQueryException the middleware query exception
     */
    Term addTerm(String name, String definition, CvId cvId) throws MiddlewareQueryException;
    
    /**
     * Adds a new trait class to the database.
     * Creates a new cvterm and cvterm_relationship entry in the local database.
     * Returns a negative id.
     *
     * @param name the name
     * @param definition the definition
     * @param cvId the cv id
     * @return the term
     * @throws MiddlewareQueryException the middleware query exception
     */
    Term addTraitClass(String name, String definition, CvId cvId) throws MiddlewareQueryException;
    
    
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

}
