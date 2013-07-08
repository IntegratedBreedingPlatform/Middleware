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
package org.generationcp.middleware.dao.dms;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Location;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;

/**
 * DAO class for {@link Location}.
 * 
 * @author Darla Ani, Joyce Avestro
 *
 */
public class LocationSearchDao extends GenericDAO<Location, Integer> {
	
	@SuppressWarnings("unchecked")
	public List<Integer> getLocationIds(List<Country> countries) throws MiddlewareQueryException {
		List<Integer> locationIds = new ArrayList<Integer>();
		try {
			SQLQuery query = getSession().createSQLQuery("select distinct loc.locid " +
		                                                 "from location loc " + 
					                                     "where cntryid in (" + getCountryIds(countries) + ")");
					                                  
			List<Object> results = (List<Object>) query.list();
			for (Object row : results) {
				locationIds.add((Integer) row);
			}
			
		} catch(HibernateException e) {
			logAndThrowException("Error in getLocationIds=" + countries + " in LocationSearchDao: " + e.getMessage(), e);
		}
		return locationIds;
	}

	private String getCountryIds(List<Country> countries) {
		String ids = new String();
		boolean first = true;
		for (Country country : countries) {
			if (first) {
			    ids += country.getCntryid();
			}
			else {
				ids += "," + country.getCntryid();
			}
			first = false;
		}
		return ids;
	}
}
