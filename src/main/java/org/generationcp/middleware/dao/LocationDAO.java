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
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.Location;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class LocationDAO extends GenericDAO<Location, Integer>{

    public List<Location> findAll(int start, int numOfRows) throws QueryException {
        try {
            Query query = getSession().getNamedQuery(Location.FIND_ALL);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);

            List results = query.list();
            return results;
        } catch (HibernateException ex) {
            throw new QueryException("Error with find all query for Location: " + ex.getMessage());
        }
    }
	
    @SuppressWarnings("unchecked")
    public List<Location> findByName(String name, int start, int numOfRows, Operation operation) {
        Criteria criteria = getSession().createCriteria(Location.class);

        if (operation == null || operation == Operation.EQUAL) {
            criteria.add(Restrictions.eq("lname", name));
        } else if (operation == Operation.LIKE) {
            criteria.add(Restrictions.like("lname", name));
        }

        criteria.setFirstResult(start);
        criteria.setMaxResults(numOfRows);

        return criteria.list();
    }

    public Long countByName(String name, Operation operation) {
        Criteria criteria = getSession().createCriteria(Location.class);
        criteria.setProjection(Projections.rowCount());

        if (operation == null || operation == Operation.EQUAL) {
            criteria.add(Restrictions.eq("lname", name));
        } else if (operation == Operation.LIKE) {
            criteria.add(Restrictions.like("lname", name));
        }

        Long count = (Long) criteria.uniqueResult();
        return count;
    }

	
}
