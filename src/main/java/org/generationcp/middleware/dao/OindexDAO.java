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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.pojos.Oindex;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class OindexDAO extends GenericDAO<OindexDAO, Integer>{

    public Long countOunitIDsByRepresentationId(Integer representationId) throws QueryException {
        try {
            Criteria crit = getSession().createCriteria(Oindex.class);
            crit.add(Restrictions.eq("representationNumber", representationId));

            crit.setProjection(Projections.countDistinct("observationUnitId"));

            Long ounitIdCount = (Long) crit.uniqueResult();

            return ounitIdCount;
        } catch (HibernateException ex) {
            throw new QueryException("Error with count Ounit IDs by Representation ID query: " + ex.getMessage(), ex);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Integer> getOunitIDsByRepresentationId(Integer representationId, int start, int numOfRows) throws QueryException {
        try {
            Criteria crit = getSession().createCriteria(Oindex.class);
            crit.add(Restrictions.eq("representationNumber", representationId));

            crit.setProjection(Projections.distinct(Projections.property("observationUnitId")));

            crit.setFirstResult(start);
            crit.setMaxResults(numOfRows);

            List<Integer> ounitIDs = crit.list();

            return ounitIDs;
        } catch (HibernateException ex) {
            throw new QueryException("Error with get Ounit IDs by Representation ID query: " + ex.getMessage(), ex);
        }
    }
    
    /**
     * Returns the factor ids and level numbers of conditions in a List of Object arrays.  The first element of the array
     * is the factor id as an Integer.  The second element is the level number as an Integer.
     * 
     * @param representationId
     * @return
     * @throws QueryException
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> getFactorIdAndLevelNoOfConditionsByRepresentationId(Integer representationId) throws QueryException {
        try {
            List<Object[]> toreturn = new ArrayList<Object[]>();
            
            //first get the number of rows in the dataset
            Long numOfRows = countOunitIDsByRepresentationId(representationId);
            
            SQLQuery query = getSession().createSQLQuery(Oindex.GET_FACTORID_AND_LEVELNO_OF_CONDITIONS_BY_REPRESNO);
            query.setParameter("represno", representationId);
            query.setParameter("count", numOfRows);
            
            toreturn = query.list();
            return toreturn;
        } catch (Exception ex) {
            throw new QueryException("Error with get Factor Id and Level number of Conditions by Representation ID query, " +
            		"given representation id = " + representationId + ": " + ex.getMessage(), ex);
        }
    }
}
