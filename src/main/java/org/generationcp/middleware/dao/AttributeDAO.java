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
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.GermplasmList;
import org.hibernate.HibernateException;
import org.hibernate.Query;

public class AttributeDAO extends GenericDAO<Attribute, Integer> {
    @SuppressWarnings("unchecked")
    public List<Attribute> getByGID(Integer gid) throws QueryException {
	try {
	    Query query = getSession().getNamedQuery(Attribute.GET_BY_GID);
	    query.setParameter("gid", gid);

	    List<Attribute> results = query.list();
	    return results;
	} catch (HibernateException ex) {
	    throw new QueryException("Error with get Attributes by GID query: "
		    + ex.getMessage());
	}
    }

    public void validateId(Attribute attribute) throws QueryException {
	// Check if not a local record (has negative ID)
	Integer id = attribute.getAid();
	if (id != null && id.intValue() > 0) {
	    throw new QueryException(
		    "Cannot update a Central Database record. "
			    + "Attribute object to update must be a Local Record (ID must be negative)");
	}
    }
}
