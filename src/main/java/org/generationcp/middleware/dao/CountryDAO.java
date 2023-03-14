/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
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
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link Country}.
 *
 */
public class CountryDAO extends GenericDAO<Country, Integer> {

	public CountryDAO(final Session session) {
		super(session);
	}

	@SuppressWarnings("unchecked")
	public List<Country> getAllCountry() throws MiddlewareQueryException {
		List<Country> toReturn = new ArrayList<Country>();
		try {
			Query query = this.getSession().getNamedQuery(Country.GET_ALL);
			toReturn = query.list();
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getAllCountry() query from Country: " + e.getMessage(), e);
		}
		return toReturn;
	}

	@SuppressWarnings("unchecked")
	public List<Country> getByIsoFull(String isoFull) throws MiddlewareQueryException {
		List<Country> toReturn = new ArrayList<Country>();
		try {
			if (isoFull != null) {
				Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
				criteria.add(Restrictions.eq("isofull", isoFull));
				toReturn = criteria.list();
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error calling getByIsoFull() query from Country: " + e.getMessage(), e);
		}
		return toReturn;
	}
}
