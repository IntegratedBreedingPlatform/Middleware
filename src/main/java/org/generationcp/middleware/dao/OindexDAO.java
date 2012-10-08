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

package org.generationcp.middleware.dao;

import java.util.ArrayList;
import java.util.List;


import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Oindex;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class OindexDAO extends GenericDAO<OindexDAO, Integer>{

    public long countOunitIDsByRepresentationId(Integer representationId) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Oindex.class);
            criteria.add(Restrictions.eq("representationNumber", representationId));

            criteria.setProjection(Projections.countDistinct("observationUnitId"));

            Long ounitIdCount = (Long) criteria.uniqueResult();

            return ounitIdCount;
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with countOunitIDsByRepresentationId(representationId=" + representationId
                    + ") query from Oindex: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Integer> getOunitIDsByRepresentationId(Integer representationId, int start, int numOfRows) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Oindex.class);
            criteria.add(Restrictions.eq("representationNumber", representationId));

            criteria.setProjection(Projections.distinct(Projections.property("observationUnitId")));
            criteria.setFirstResult(start);
            criteria.setMaxResults(numOfRows);

            List<Integer> ounitIDs = criteria.list();

            return ounitIDs;
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getOunitIDsByRepresentationId(representationId=" + representationId
                    + ")query from Oindex: " + e.getMessage(), e);
        }
    }

    /**
     * Returns the factor ids and level numbers of conditions in a List of Object arrays.  The first element of the array
     * is the factor id as an Integer.  The second element is the level number as an Integer.
     * 
     * @param representationId
     * @return
     * @throws MiddlewareQueryException
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> getFactorIdAndLevelNoOfConditionsByRepresentationId(Integer representationId) throws MiddlewareQueryException {
        try {
            List<Object[]> toreturn = new ArrayList<Object[]>();

            //first get the number of rows in the dataset
            long numOfRows = countOunitIDsByRepresentationId(representationId);

            SQLQuery query = getSession().createSQLQuery(Oindex.GET_FACTORID_AND_LEVELNO_OF_CONDITIONS_BY_REPRESNO);
            query.setParameter("represno", representationId);
            query.setParameter("count", Long.valueOf(numOfRows));

            toreturn = query.list();
            return toreturn;
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getFactorIdAndLevelNoOfConditionsByRepresentationId(representationId="
                    + representationId + ") query from Oindex: " + e.getMessage(), e);
        }
    }
}
