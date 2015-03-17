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
     * @throws MiddlewareQueryException
     */
    public Scale getScaleById(int scaleId) throws MiddlewareQueryException;

    /**
     * Get all scales from db
     * @throws MiddlewareQueryException
     */
    public List<Scale> getAllScales() throws MiddlewareQueryException;

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

}
