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

import java.util.List;

import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.pojos.Oindex;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
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
}
