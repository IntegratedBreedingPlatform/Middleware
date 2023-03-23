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

package org.generationcp.middleware.dao.dms;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Location;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

/**
 * DAO class for {@link Location}.
 *
 * @author Darla Ani, Joyce Avestro
 *
 */
public class LocationSearchDao extends GenericDAO<Location, Integer> {

	public LocationSearchDao(final Session session) {
		super(session);
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getLocationIds(List<Country> countries) throws MiddlewareQueryException {
		List<Integer> locationIds = new ArrayList<Integer>();
		try {
			SQLQuery query =
					this.getSession().createSQLQuery(
							"select distinct loc.locid " + "from location loc " + "where cntryid in (" + this.getCountryIds(countries)
									+ ")");

			List<Object> results = query.list();
			for (Object row : results) {
				locationIds.add((Integer) row);
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error in getLocationIds=" + countries + " in LocationSearchDao: " + e.getMessage(), e);
		}
		return locationIds;
	}

	private String getCountryIds(List<Country> countries) {
		boolean first = true;
		StringBuffer ids = new StringBuffer();
		for (Country country : countries) {
			if (first) {
				ids.append(country.getCntryid());
			} else {
				ids.append(",").append(country.getCntryid());
			}
			first = false;
		}
		return ids.toString();
	}
}
