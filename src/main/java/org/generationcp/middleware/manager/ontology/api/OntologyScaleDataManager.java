/*******************************************************************************

 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.middleware.manager.ontology.api;

import org.generationcp.middleware.domain.ontology.Scale;
import org.generationcp.middleware.exceptions.MiddlewareException;

import java.util.List;

/**
 * This is the API for retrieving ontology scale data.
 * 
 * 
 */
public interface OntologyScaleDataManager {

    /**
     * This will fetch Scale by scaleId*
     * @param scaleId select scale by scaleId
     * @return @link Scale
     * @throws MiddlewareException
     */
    Scale getScaleById(int scaleId) throws MiddlewareException;

    /**
     * Get all scales from db
     *
     * @return All the scales
     * @throws MiddlewareException the middleware exception
     */
    List<Scale> getAllScales() throws MiddlewareException;

    /**
     * Adds a Scale. If the scale is already found in the local database, it simply retrieves the record found.
     * @param scale to be added
     * @throws MiddlewareException the middleware exception
     */
    void addScale(Scale scale) throws MiddlewareException;

    /**
    * Updates the given scale.
    * This searches for the id. If it exists, the entry in the database is replaced with the new value.
    * @param scale The Scale to update
    * @throws MiddlewareException the middleware exception
    */
    void updateScale(Scale scale) throws MiddlewareException;

    /**
     * Delete method.
     *
     * @param scaleId the cv term id
     * @throws MiddlewareException the middleware query exception
     */
    void deleteScale(int scaleId) throws MiddlewareException;

}
