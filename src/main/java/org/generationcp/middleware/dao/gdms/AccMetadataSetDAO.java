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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.gdms.AccMetadataSet;
import org.generationcp.middleware.pojos.gdms.AccMetadataSetPK;
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
    public List<Integer> getNameIdsByGermplasmIds(List<Integer> gIds) throws MiddlewareQueryException {
        try {
            if (gIds == null || gIds.isEmpty()) {
                return new ArrayList<Integer>();
            }

            SQLQuery query = getSession().createSQLQuery(AccMetadataSet.GET_NAME_IDS_BY_GERMPLASM_IDS);
            query.setParameterList("gIdList", gIds);
            return (List<Integer>) query.list();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getNameIdsByGermplasmIds(" + gIds + ") query from AccMetadataSet: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Integer> getNIDsByDatasetIds(List<Integer> datasetIds, List<Integer> gids, int start, int numOfRows)
            throws MiddlewareQueryException {
        try {
            List<Integer> nids;
            SQLQuery query;

            if (gids == null || gids.isEmpty()) {
                query = getSession().createSQLQuery(AccMetadataSet.GET_NIDS_BY_DATASET_IDS);
            } else {
                query = getSession().createSQLQuery(
                        AccMetadataSet.GET_NIDS_BY_DATASET_IDS + AccMetadataSet.GET_NIDS_BY_DATASET_IDS_FILTER_BY_GIDS);
                query.setParameterList("gids", gids);
            }

            query.setParameterList("datasetId", datasetIds);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);
            nids = query.list();

            return nids;
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getNIDsByDatasetIds(datasetIds=" + datasetIds + ", gids=" + gids + ") query from AccMetadataSet: "
                    + e.getMessage(), e);
        }
    }

    @SuppressWarnings("rawtypes")
    public List<AccMetadataSetPK> getAccMetadasetByGids(List<Integer> gids, int start, int numOfRows) throws MiddlewareQueryException {

        try {

            List<AccMetadataSetPK> dataValues = new ArrayList<AccMetadataSetPK>();

            if (gids != null && !gids.isEmpty()) {
                
                SQLQuery query = getSession().createSQLQuery(AccMetadataSet.GET_ACC_METADATASETS_BY_GIDS);
                query.setParameterList("gids", gids);
                query.setFirstResult(start);
                query.setMaxResults(numOfRows);

                List results = query.list();
                for (Object o : results) {
                    Object[] result = (Object[]) o;
                    if (result != null) {
                        Integer gid = (Integer) result[0];
                        Integer nid = (Integer) result[1];
                        Integer datasetId = (Integer) result[2];

                        AccMetadataSetPK dataElement = new AccMetadataSetPK(gid, nid, datasetId);

                        dataValues.add(dataElement);
                        
                    }
                }
            }
            return dataValues;
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getAccMetadasetByGids(gids=" + gids + ") query from AccMetadataSet: "
                    + e.getMessage(), e);
        }
    }

    public long countAccMetadataSetByGids(List<Integer> gids) throws MiddlewareQueryException {
        long count = 0;
        try {
            if ((gids != null) && (!gids.isEmpty())) {
                SQLQuery query = getSession().createSQLQuery(AccMetadataSet.COUNT_ACC_METADATASETS_BY_GIDS);
                query.setParameterList("gids", gids);
                BigInteger result = (BigInteger) query.uniqueResult();
                if (result != null) {
                    count = result.longValue();
                }
            }
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with countAccMetadataSetByGids(gids=" + gids + ") query from AccMetadataSet: "
                    + e.getMessage(), e);
        }
        return count;
    }
}
