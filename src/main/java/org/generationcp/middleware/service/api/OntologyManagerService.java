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
import org.generationcp.middleware.exceptions.MiddlewareException;

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
     * @throws MiddlewareException*
     */
    List<Term> getAllTraitClass() throws MiddlewareException;

    /*======================= TERM ================================== */

    /**
     * Retrieves a Term record given its id. This can also be used to retrieve traits, methods and scales.
     *
     * @param termId the term id
     * @return the term by id
     * @throws MiddlewareException the middleware query exception
     */
    Term getTermById(Integer termId) throws MiddlewareException;

    /**
     * Return All Trait Classes
     * * @return
     * @throws MiddlewareException*
     */
    Term getTermByNameAndCvId(String name, int cvId) throws MiddlewareException;

    /**
     * Get terms by given cv_id
     * @param cvId cv id
     * @return list of terms
     * @throws MiddlewareException
     */
    List<Term> getTermByCvId(int cvId) throws MiddlewareException;

    /**
     * @param termId TermId
     * @return true is term is referred
     * @throws MiddlewareException
     */
    boolean isTermReferred(int termId) throws MiddlewareException;

    /*======================= Methods ================================== */
    /**
     * Gets the method with the given id.
     *
     * @param id the id to match
     * @return the matching method
     * @throws MiddlewareException the middleware query exception
     */
    Method getMethod(int id) throws MiddlewareException;


    /**
     * Gets the all methods from Central and Local.
     *
     * @return All the methods
     * @throws MiddlewareException the middleware query exception
     */
    List<Method> getAllMethods() throws MiddlewareException;

    /**
     * Adds a method. If the method is already found in the local database, it simply retrieves the record found.
     * @param method to be added
     * @throws MiddlewareException the middleware query exception
     */
    void addMethod(Method method) throws MiddlewareException;

    /**
     * Updates the given method.
     * This searches for the id. If it exists, the entry in the database is replaced with the new value.
     * @param method The Method to update
     * @throws MiddlewareException the middleware query exception
     */
    void updateMethod(Method method) throws MiddlewareException;

    /**
     * Delete method.
     *
     * @param id the cv term id
     * @throws MiddlewareException the middleware query exception
     */
    void deleteMethod(int id) throws MiddlewareException;

    /*======================= Properties ================================== */
    /**
     * Given the termId, retrieve the Property POJO.
     *
     * @param id the term id having cvId = Property
     * @return property
     * @throws MiddlewareException the middleware query exception
     */
    Property getProperty(int id) throws MiddlewareException;

    /**
     * Get all properties
     * @return property
     * @throws MiddlewareException the middleware query exception
     */
    List<Property> getAllProperties() throws MiddlewareException;

    /**
     * Get all properties by className
     * @return property
     * @throws MiddlewareException the middleware query exception
     */
    List<Property> getAllPropertiesWithClass(String className) throws MiddlewareException;

    /**
     * Adds a new property to the database.
     * This is new method which ignores isA flat relationship to define single class per property
     *
     * @param property to be added
     * @throws MiddlewareException the middleware query exception
     */
    void addProperty(Property property) throws MiddlewareException;

    /**
     * Adds a new property to the database.
     * This is new method which ignores isA flat relationship to define single class per property
     * @param property to be updated
     * @throws MiddlewareException the middleware query exception
     */
    void updateProperty(Property property) throws MiddlewareException;

    /**
     * Adds a new property to the database.
     * This is new method which ignores isA flat relationship to define single class per property
     * @param propertyId of property
     * @throws MiddlewareException the middleware query exception
     */
    void deleteProperty(Integer propertyId) throws MiddlewareException;

    /*======================= Scales ================================== */

    /**
     * This will fetch Scale by scaleId*
     * @param scaleId select method by scaleId
     * @return Scale
     * @throws MiddlewareException
     */
    Scale getScaleById(int scaleId) throws MiddlewareException;

    /**
     * Get all scales from db
     * @throws MiddlewareException
     */
    List<Scale> getAllScales() throws MiddlewareException;

    /**
     * Adding new scale
     * @param scale scale to be added
     * @throws MiddlewareException
     */
    void addScale(Scale scale) throws MiddlewareException;

    /**
     * Update scale
     * @param scale scale to be added
     * @throws MiddlewareException
     */
    void updateScale(Scale scale) throws MiddlewareException;

    /**
     * Delete scale
     * @param scaleId scale to be deleted
     * @throws MiddlewareException
     */
    void deleteScale(int scaleId) throws MiddlewareException;


    /*======================= Variables ================================== */

    /**
     * Return All Variables
     * * @return List<OntologyVariableSummary>
     * @throws MiddlewareException*
     */
    List<OntologyVariableSummary> getWithFilter(String programUuid, Boolean favorites, Integer methodId, Integer propertyId, Integer scaleId) throws MiddlewareException;

    /**
     * Return variable by Id
     * * @return OntologyVariable
     * @throws MiddlewareException*
     */
    OntologyVariable getVariable(String programUuid, Integer id) throws MiddlewareException;

    /**
     * @throws MiddlewareException*
     * * @throws MiddlewareException*
     */
    void addVariable(OntologyVariableInfo variableInfo) throws MiddlewareException;

    /**
     * @throws MiddlewareException*
     * * @throws MiddlewareException*
     */
    void updateVariable(OntologyVariableInfo variableInfo) throws MiddlewareException;

    /**
     * @throws MiddlewareException*
     */
    void deleteVariable(Integer id) throws MiddlewareException;

    /**
     * Give observations by variable Id
     * @param variableId Variable Id
     * @return Number of Observations
     * @throws MiddlewareException
     */
    int getObservationsByVariableId(Integer variableId) throws MiddlewareException;
}
