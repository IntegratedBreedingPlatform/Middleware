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
    
    public static final String NUMERIC_DATATYPE = "N";
    public static final String CHARACTER_DATATYPE = "C";

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
    
    public boolean isVariateNumeric(int variateId) throws MiddlewareQueryException {
        try {
            Query query = getSession().createSQLQuery(Variate.GET_VARIATE_ID_DATATYPE);
            query.setParameter("variatid", variateId);
            
            String result = "";
            
            if (!query.list().isEmpty()) 
                result = (String) query.list().get(0);
            else 
                throw new HibernateException("Database Error: No Datatype assigned on the variate id: " + variateId);
            
            
            if (result.equals(NUMERIC_DATATYPE)) 
                return true;
            else if (result.equals(CHARACTER_DATATYPE))
               return false;
            else
                throw new HibernateException("Database Error: No Datatype assigned on the variate id: " + variateId);
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with isVariateNumeric: " + e.getMessage(),
                    e);
        }
    }
}
