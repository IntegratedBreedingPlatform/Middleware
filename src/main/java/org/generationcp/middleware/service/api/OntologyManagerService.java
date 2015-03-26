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

import org.generationcp.middleware.domain.oms.*;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;

import java.util.List;

/**
 * 
 * This is the API for Ontology Browser requirements.
 * 
 */
public interface OntologyManagerService {

    /*======================= Classes ================================== */
    /**
     * Return All Trait Classes
     * * @return
     * @throws MiddlewareQueryException*
     */
    List<Term> getAllTraitClass() throws MiddlewareQueryException;

    /**
     * Returns all dataTypes used by Scale
     */
    List<Term> getDataTypes() throws MiddlewareQueryException;

    /*======================= TERM ================================== */

    /**
     * Retrieves a Term record given its id. This can also be used to retrieve traits, methods and scales.
     *
     * @param termId the term id
     * @return the term by id
     * @throws MiddlewareQueryException the middleware query exception
     */
    Term getTermById(Integer termId) throws MiddlewareQueryException;

    /**
     * Return All Trait Classes
     * * @return
     * @throws MiddlewareQueryException*
     */
    Term getTermByNameAndCvId(String name, int cvId) throws MiddlewareQueryException;

    /**
     * @param termId TermId
     * @return true is term is referred
     * @throws MiddlewareQueryException
     */
    boolean isTermReferred(int termId) throws MiddlewareQueryException;

    /*======================= Methods ================================== */
    /**
     * Gets the method with the given id.
     *
     * @param id the id to match
     * @return the matching method
     * @throws MiddlewareQueryException the middleware query exception
     */
    Method getMethod(int id) throws MiddlewareQueryException;


    /**
     * Gets the all methods from Central and Local.
     *
     * @return All the methods
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Method> getAllMethods() throws MiddlewareQueryException;

    /**
     * Adds a method. If the method is already found in the local database, it simply retrieves the record found.
     * @param method to be added
     * @throws MiddlewareQueryException the middleware query exception
     */
    void addMethod(Method method) throws MiddlewareQueryException;

    /**
     * Updates the given method.
     * This searches for the id. If it exists, the entry in the database is replaced with the new value.
     * @param method The Method to update
     * @throws MiddlewareQueryException the middleware query exception
     * @throws org.generationcp.middleware.exceptions.MiddlewareException the middleware exception
     */
    void updateMethod(Method method) throws MiddlewareQueryException, MiddlewareException;

    /**
     * Delete method.
     *
     * @param id the cv term id
     * @throws MiddlewareQueryException the middleware query exception
     */
    void deleteMethod(int id) throws MiddlewareQueryException;

    /*======================= Properties ================================== */
    /**
     * Given the termId, retrieve the Property POJO.
     *
     * @param id the term id having cvId = Property
     * @return property
     * @throws MiddlewareQueryException the middleware query exception
     */
    Property getProperty(int id) throws MiddlewareQueryException, MiddlewareException;

    /**
     * Get all properties
     * @return property
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Property> getAllProperties() throws MiddlewareQueryException;

    /**
     * Get all properties by className
     * @return property
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Property> getAllPropertiesWithClass(String className) throws MiddlewareQueryException;

    /**
     * Adds a new property to the database.
     * This is new method which ignores isA flat relationship to define single class per property
     *
     * @param property to be added
     * @throws MiddlewareQueryException the middleware query exception
     */
    void addProperty(Property property) throws MiddlewareQueryException, MiddlewareException;

    /**
     * Adds a new property to the database.
     * This is new method which ignores isA flat relationship to define single class per property
     * @param property to be updated
     * @throws MiddlewareQueryException the middleware query exception
     */
    void updateProperty(Property property) throws MiddlewareQueryException, MiddlewareException;

    /**
     * Adds a new property to the database.
     * This is new method which ignores isA flat relationship to define single class per property
     * @param propertyId of property
     * @throws MiddlewareQueryException the middleware query exception
     */
    void deleteProperty(Integer propertyId) throws MiddlewareQueryException, MiddlewareException;

    /*======================= Scales ================================== */

    /**
     * This will fetch Scale by scaleId*
     * @param scaleId select method by scaleId
     * @return Scale
     * @throws MiddlewareQueryException
     */
    Scale getScaleById(int scaleId) throws MiddlewareQueryException;

    /**
     * Get all scales from db
     * @throws MiddlewareQueryException
     */
    List<Scale> getAllScales() throws MiddlewareQueryException;

    /**
     * Adding new scale
     * @param scale scale to be added
     * @throws MiddlewareQueryException
     * @throws MiddlewareException
     */
    void addScale(Scale scale) throws MiddlewareQueryException, MiddlewareException;

    /**
     * Update scale
     * @param scale scale to be added
     * @throws MiddlewareQueryException
     * @throws MiddlewareException
     */
    void updateScale(Scale scale) throws MiddlewareQueryException, MiddlewareException;

    /**
     * Delete scale
     * @param scaleId scale to be deleted
     * @throws MiddlewareQueryException
     * @throws MiddlewareException
     */
    void deleteScale(int scaleId) throws MiddlewareQueryException, MiddlewareException;


    /*======================= Variables ================================== */

    /**
     * Return All Variables
     * * @return List<OntologyVariableSummary>
     * @throws MiddlewareQueryException*
     */
    List<OntologyVariableSummary> getAllVariables() throws MiddlewareQueryException;

    /**
     * Return variable by Id
     * * @return OntologyVariable
     * @throws MiddlewareQueryException*
     */
    OntologyVariable getVariable(Integer id) throws MiddlewareQueryException, MiddlewareException;

    /**
     * @throws MiddlewareQueryException*
     * * @throws MiddlewareException*
     */
    void addVariable(OntologyVariableInfo variableInfo) throws MiddlewareQueryException, MiddlewareException;

    /**
     * @throws MiddlewareQueryException*
     * * @throws MiddlewareException*
     */
    void updateVariable(OntologyVariableInfo variableInfo) throws MiddlewareQueryException, MiddlewareException;

    /**
     * @throws MiddlewareQueryException*
     * * @throws MiddlewareException*
     */
    void deleteVariable(Integer id) throws MiddlewareQueryException, MiddlewareException;
}
