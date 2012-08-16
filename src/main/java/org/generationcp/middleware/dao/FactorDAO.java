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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.pojos.Factor;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;

public class FactorDAO extends GenericDAO<Factor, Integer>{

    @SuppressWarnings("unchecked")
    public Set<Integer> getGIDSGivenObservationUnitIds(Set<Integer> ounitIds, int start, int numOfRows) {
        Set<Integer> results = new HashSet<Integer>();

        SQLQuery levelNQuery = getSession().createSQLQuery(Factor.GET_GID_FROM_NUMERIC_LEVELS_GIVEN_OBSERVATION_UNIT_IDS);
        levelNQuery.setParameterList("ounitids", ounitIds);
        levelNQuery.setFirstResult(start);
        levelNQuery.setMaxResults(numOfRows);

        List<Double> gids1 = levelNQuery.list();
        for (Double gid : gids1) {
            results.add(gid.intValue());
        }

        SQLQuery levelCQuery = getSession().createSQLQuery(Factor.GET_GID_FROM_CHARACTER_LEVELS_GIVEN_OBSERVATION_UNIT_IDS);
        levelCQuery.setParameterList("ounitids", ounitIds);
        levelCQuery.setFirstResult(start);
        levelCQuery.setMaxResults(numOfRows);

        List<String> gids2 = levelCQuery.list();
        for (String gid : gids2) {
            results.add(Integer.parseInt(gid));
        }

        return results;
    }

    @SuppressWarnings("unchecked")
    public List<Factor> getByStudyID(Integer studyId) throws QueryException {
        try {
            Query query = getSession().getNamedQuery(Factor.GET_FACTORS_BY_STUDYID);
            query.setParameter("studyId", studyId);

            List<Factor> results = query.list();
            return results;
        } catch (HibernateException ex) {
            throw new QueryException("Error with get Factors by Study ID query: " + ex.getMessage(), ex);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Factor> getByRepresentationID(Integer representationId) throws QueryException {
        try {
            SQLQuery query = getSession().createSQLQuery(Factor.GET_BY_REPRESENTATION_ID);
            query.setParameter("representationId", representationId);
            query.addEntity("f", Factor.class);

            List<Factor> results = query.list();
            return results;
        } catch (HibernateException ex) {
            throw new QueryException("Error with get Factors by Representation ID query: " + ex.getMessage(), ex);
        }
    }

    public String getMainLabel(Integer factorid) throws QueryException {
        try {
            Criteria crit = getSession().createCriteria(getPersistentClass());
            crit.add(Restrictions.eq("id", factorid));
            crit.add(Restrictions.eq("factorId", factorid));
            
            List<Factor> results = crit.list();
            if(!results.isEmpty()) {
                Factor factor = results.get(0);
                return factor.getName();
            }
            
            return null;
        } catch (HibernateException ex) {
            throw new QueryException("Error with get main label query for factor id = " + factorid + ": " + ex.getMessage(), ex);
        }
    }
}
