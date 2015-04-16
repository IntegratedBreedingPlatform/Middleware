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

import org.generationcp.middleware.domain.oms.Scale;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;

import java.util.List;

/**
 * This is the API for retrieving ontology scale data.
 * 
 * 
 */
public interface OntologyScaleDataManager {

    /**
     * This will fetch Scale by scaleId*
     * @param scaleId select method by scaleId
     * @return Scale
     * @throws MiddlewareException
     */
    public Scale getScaleById(int scaleId) throws MiddlewareException;

    /**
     * Get all scales from db
     * @throws MiddlewareException
     */
    public List<Scale> getAllScales() throws MiddlewareException;

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

}
