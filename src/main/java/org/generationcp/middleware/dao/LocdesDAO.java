/*******************************************************************************
 * Copyright (c) 2014, All Rights Reserved.
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
import org.generationcp.middleware.pojos.Locdes;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;

public class LocdesDAO extends GenericDAO<Locdes, Integer> {

    @SuppressWarnings("unchecked")
    public List<Locdes> getByLocation(Integer locId) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Locdes.class);
            criteria.add(Restrictions.eq("locationId", locId));
            return criteria.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getByLocation(locId=" + locId + ") query from Locdes: "
                    + e.getMessage(), e);
        }
        return new ArrayList<Locdes>();
    }

    @SuppressWarnings("unchecked")
    public List<Locdes> getByDval(String dval) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Locdes.class);
            criteria.add(Restrictions.eq("dval", dval));
            return criteria.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getByValue(value=" + dval + ") query from Locdes: "
                    + e.getMessage(), e);
        }
        return new ArrayList<Locdes>();
    }


}
