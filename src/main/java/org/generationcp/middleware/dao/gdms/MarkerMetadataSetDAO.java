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
package org.generationcp.middleware.dao.gdms;

import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.gdms.MarkerMetadataSet;
import org.hibernate.SQLQuery;

/**
 * The Class MarkerMetadataSetDAO.
 * 
 * @author Joyce Avestro
 * 
 */
public class MarkerMetadataSetDAO extends GenericDAO<MarkerMetadataSet, Integer>{
    
    /**
     * Gets the marker id by dataset id.
     *
     * @param datasetId the dataset id
     * @return the marker id by dataset id
     * @throws MiddlewareQueryException the MiddlewareQueryException
     */
    @SuppressWarnings("unchecked")
    public List<Integer> getMarkerIdByDatasetId(Integer datasetId) throws MiddlewareQueryException{
        SQLQuery query = getSession().createSQLQuery(MarkerMetadataSet.GET_MARKER_ID_BY_DATASET_ID); 
        query.setParameter("datasetId", datasetId);
        return (List<Integer>) query.list();
    }

}
