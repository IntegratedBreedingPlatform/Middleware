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
import org.generationcp.middleware.pojos.Country;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

public class CountryDAO extends GenericDAO<Country, Integer>{
	
	@SuppressWarnings("unchecked")
    public List<Country> getAllCountry() throws MiddlewareQueryException {
        try {
            Query query = getSession().getNamedQuery(Country.GET_ALL);

            return (List<Country>) query.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getAllCountry() query from Country: " + e.getMessage(), e);
        }
        return new ArrayList<Country>();
    }

	
	@SuppressWarnings("unchecked")
	public List<Country> getByIsoFull(String isoFull) throws MiddlewareQueryException {
		try {
			Criteria criteria = getSession().createCriteria(getPersistentClass());
			criteria.add(Restrictions.eq("isofull", isoFull));
			
			return criteria.list();
		} catch (HibernateException e) {
			logAndThrowException("Error calling getByIsoFull() query from Country: " + e.getMessage(), e);
		}
		return new ArrayList<Country>();
	}
}
