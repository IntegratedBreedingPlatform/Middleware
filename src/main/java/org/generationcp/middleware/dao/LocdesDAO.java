/*******************************************************************************
 * Copyright (c) 2014, All Rights Reserved.
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
import org.generationcp.middleware.pojos.Locdes;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;

public class LocdesDAO extends GenericDAO<Locdes, Integer> {

	@SuppressWarnings("unchecked")
	public List<Locdes> getByLocation(Integer locId) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(Locdes.class);
			criteria.add(Restrictions.eq("locationId", locId));
			return criteria.list();
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getByLocation(locId=" + locId + ") query from Locdes: " + e.getMessage(), e);
		}
		return new ArrayList<Locdes>();
	}

	@SuppressWarnings("unchecked")
	public List<Locdes> getByDval(String dval) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(Locdes.class);
			criteria.add(Restrictions.eq("dval", dval));
			return criteria.list();
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getByValue(value=" + dval + ") query from Locdes: " + e.getMessage(), e);
		}
		return new ArrayList<Locdes>();
	}

	@SuppressWarnings("unchecked")
	public List<Locdes> getAllLocdesByFilters(final String fcode, final Integer locid, final String dval) throws MiddlewareQueryException {
		try {
			final StringBuilder sqlString = new StringBuilder();

			sqlString.append("SELECT ld.ldid,ld.locid,ld.dtype,ld.duid,ld.dval,ld.ddate,ld.dref ").append(" FROM locdes ld, udflds ud") //
					.append(" WHERE ld.dtype = ud.fldno");

			if (fcode != null) {
				sqlString.append(" and ud.fcode= '").append(fcode).append("' ");
			}

			if (locid != null) {
				sqlString.append(" and ld.locid= ").append(locid);
			}

			if (dval != null) {
				sqlString.append(" and ld.dval= '").append(dval).append("' ");
			}

			final SQLQuery query = this.getSession().createSQLQuery(sqlString.toString());
			query.addEntity(Locdes.class);

			return query.list();
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getAllLocdesByFcode(fcode=" + fcode + ") query from Locdes: " + e.getMessage(), e);
		}
		return new ArrayList<Locdes>();
	}
}
