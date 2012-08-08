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

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.pojos.gdms.AccMetadataSet;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;

/**
 * The Class AccMetadataSetDAO.
 * 
 * @author Joyce Avestro
 * 
 */
public class AccMetadataSetDAO extends GenericDAO<AccMetadataSet, Integer>{
    
    /**
     * Gets the name ids by germplasm ids.
     *
     * @param gIds the germplasm ids
     * @return the name ids by germplasm ids
     */
    @SuppressWarnings("unchecked")
    public List<Integer> getNameIdsByGermplasmIds(List<Integer> gIds) {
        
        if (gIds == null || gIds.isEmpty()){
            return new ArrayList<Integer>();
        }

        SQLQuery query = getSession().createSQLQuery(AccMetadataSet.GET_NAME_IDS_BY_GERMPLASM_IDS);        
        query.setParameterList("gIdList", gIds);
        return (List<Integer>) query.list();        
    }
    
    @SuppressWarnings("unchecked")
    public List<Integer> getNIDsByDatasetIds(List<Integer> datasetIds, List<Integer> gids, int start, int numOfRows) throws QueryException{
        try {
            List<Integer> nids;
            SQLQuery query;
            
            if(gids == null || gids.isEmpty()) {
                query = getSession().createSQLQuery(AccMetadataSet.GET_NIDS_BY_DATASET_IDS);
            } else {
                query = getSession().createSQLQuery(
                        AccMetadataSet.GET_NIDS_BY_DATASET_IDS + 
                        AccMetadataSet.GET_NIDS_BY_DATASET_IDS_FILTER_BY_GIDS);
                query.setParameterList("gids", gids);
            }
            
            query.setParameterList("datasetId", datasetIds);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);
            nids = query.list();
            
            return nids;
        } catch (HibernateException e) {
            throw new QueryException("Error with getNIDsByDatasetIds query: " + e.getMessage(), e);
        }
    }

}
