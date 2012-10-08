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
import org.generationcp.middleware.pojos.Attribute;
import org.hibernate.HibernateException;
import org.hibernate.Query;

public class AttributeDAO extends GenericDAO<Attribute, Integer>{

    @SuppressWarnings("unchecked")
    public List<Attribute> getByGID(Integer gid) throws MiddlewareQueryException {
        try {
            Query query = getSession().getNamedQuery(Attribute.GET_BY_GID);
            query.setParameter("gid", gid);
            return (List<Attribute>) query.list();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getByGID(gid=" + gid + ") query from Attributes: " + e.getMessage(), e);
        }
    }

    public void validateId(Attribute attribute) throws MiddlewareQueryException {
        // Check if not a local record (has negative ID)
        Integer id = attribute.getAid();
        if (id != null && id.intValue() > 0) {
            throw new MiddlewareQueryException("Error with validateId(attribute=" + attribute
                    + "): Cannot update a Central Database record. "
                    + "Attribute object to update must be a Local Record (ID must be negative)");
        }
    }
}
