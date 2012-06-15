/***************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the 
 * GNU General Public License (http://bit.ly/8Ztv8M) and the 
 * provisions of Part F of the Generation Challenge Programme 
 * Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 **************************************************************/
package org.generationcp.middleware.dao;

import java.util.List;

import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.pojos.Variate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;

public class VariateDAO extends GenericDAO<Variate, Integer> {
    @SuppressWarnings("unchecked")
    public List<Variate> getByStudyID(Integer studyId) throws QueryException {
	try {
	    Query query = getSession().getNamedQuery(
		    Variate.GET_VARIATES_BY_STUDYID);
	    query.setParameter("studyId", studyId);

	    List<Variate> results = query.list();
	    return results;
	} catch (HibernateException ex) {
	    throw new QueryException(
		    "Error with get Variates by Study ID query: "
			    + ex.getMessage());
	}
    }

    @SuppressWarnings("unchecked")
    public List<Variate> getByRepresentationId(Integer representationId)
	    throws QueryException {
	try {
	    SQLQuery query = getSession().createSQLQuery(
		    Variate.GET_BY_REPRESENTATION_ID);
	    query.setParameter("representationId", representationId);
	    query.addEntity("v", Variate.class);

	    List<Variate> results = query.list();
	    return results;
	} catch (HibernateException ex) {
	    throw new QueryException(
		    "Error with get Variates by Representation ID query: "
			    + ex.getMessage());
	}
    }

}
