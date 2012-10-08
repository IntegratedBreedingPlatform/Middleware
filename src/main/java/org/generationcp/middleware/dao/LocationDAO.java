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
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.Location;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class LocationDAO extends GenericDAO<Location, Integer>{

    @SuppressWarnings("unchecked")
    public List<Location> getByName(String name, int start, int numOfRows, Operation operation) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Location.class);

            if (operation == null || operation == Operation.EQUAL) {
                criteria.add(Restrictions.eq("lname", name));
            } else if (operation == Operation.LIKE) {
                criteria.add(Restrictions.like("lname", name));
            }

            criteria.setFirstResult(start);
            criteria.setMaxResults(numOfRows);
            return criteria.list();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getByName(name=" + name + ", operation=" + operation + ") query from Location: "
                    + e.getMessage(), e);
        }
    }

    public long countByName(String name, Operation operation) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Location.class);
            criteria.setProjection(Projections.rowCount());

            if (operation == null || operation == Operation.EQUAL) {
                criteria.add(Restrictions.eq("lname", name));
            } else if (operation == Operation.LIKE) {
                criteria.add(Restrictions.like("lname", name));
            }

            return ((Long) criteria.uniqueResult()).longValue(); //count
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with countByName(name=" + name + ", operation=" + operation
                    + ") query from Location: " + e.getMessage(), e);
        }
    }

}
