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

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.pojos.Installation;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.List;

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

    public Installation getLatest(Database instance) throws MiddlewareQueryException {
        try {
            Long id = null;
            Criteria criteria = getSession().createCriteria(Installation.class);

            if (instance == Database.LOCAL) {
                criteria.setProjection(Projections.min("id"));
                id = (Long) criteria.uniqueResult();
            } else if (instance == Database.CENTRAL) {
                criteria.setProjection(Projections.max("id"));
                id = (Long) criteria.uniqueResult();
            }

            if (id != null) {
                return getById(id, false);
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with getLatest(databaseInstance="+instance+") query from Installation: " + e.getMessage(), e);
        }
        return null;
    }

}
