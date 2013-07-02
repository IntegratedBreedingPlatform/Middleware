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
package org.generationcp.middleware.v2.dao;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.v2.pojos.GeolocationProperty;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link GeolocationProperty}.
 * 
 */
public class GeolocationPropertyDao extends GenericDAO<GeolocationProperty, Integer> {
	
	@SuppressWarnings("unchecked")
	public List<Integer> getGeolocationIdsByPropertyTypeAndValue(Integer typeId, String value) throws MiddlewareQueryException {
		try {
			Criteria criteria = getSession().createCriteria(getPersistentClass());
			criteria.add(Restrictions.eq("typeId", typeId));
			criteria.add(Restrictions.eq("value", value));
			criteria.setProjection(Projections.property("geolocation.locationId"));
			
			return criteria.list();
			
		} catch (HibernateException e) {
			logAndThrowException("Error at getIdsByPropertyTypeAndValue=" + typeId + ", " + value + " query on GeolocationDao: " + e.getMessage(), e);
		}
		return new ArrayList<Integer>();
	}
}
