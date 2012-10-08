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

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Variate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;

public class VariateDAO extends GenericDAO<Variate, Integer>{

    @SuppressWarnings("unchecked")
    public List<Variate> getByStudyID(Integer studyId) throws MiddlewareQueryException {
        try {
            Query query = getSession().getNamedQuery(Variate.GET_VARIATES_BY_STUDYID);
            query.setParameter("studyId", studyId);

            return (List<Variate>) query.list();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getByStudyID(studyId=" + studyId + ") query from Variate: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Variate> getByRepresentationId(Integer representationId) throws MiddlewareQueryException {
        try {
            SQLQuery query = getSession().createSQLQuery(Variate.GET_BY_REPRESENTATION_ID);
            query.setParameter("representationId", representationId);
            query.addEntity("v", Variate.class);

            return (List<Variate>) query.list();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getByRepresentationId(representationId=" + representationId
                    + ") query from Variate: " + e.getMessage(), e);
        }
    }

}
