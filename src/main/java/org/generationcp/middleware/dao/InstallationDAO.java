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
import org.generationcp.middleware.pojos.Installation;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link Installation}.
 * 
 */
public class InstallationDAO extends GenericDAO<Installation, Long>{

    @SuppressWarnings("unchecked")
    public List<Installation> getByAdminId(Long id) throws MiddlewareQueryException {
        List<Installation> toreturn = new ArrayList<Installation>();
        try {
        	if (id != null){
	            Criteria criteria = getSession().createCriteria(Installation.class);
	            criteria.add(Restrictions.eq("adminId", id));
	            toreturn.addAll(criteria.list());
        	}
        } catch (HibernateException e) {
            logAndThrowException("Error with getByAdminId(id="+id+") query from Installation: " + e.getMessage(), e);
        }
        return toreturn;
    }
}
