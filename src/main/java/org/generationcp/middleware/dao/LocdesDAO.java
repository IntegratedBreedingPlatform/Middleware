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

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Locdes;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class LocdesDAO extends GenericDAO<Locdes, Integer> {

	private static final String CLASS_NAME_LOCDES = "Locdes";
	
	private static final Logger LOG = LoggerFactory.getLogger(LocdesDAO.class);

	
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

	public List<Locdes> getLocdes(final Integer locId, final String dval) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(Locdes.class);
			if (locId != null) {
				criteria.add(Restrictions.eq("locationId", locId));
			}
			if (StringUtils.isNotBlank(dval)) {
				criteria.add(Restrictions.eq("dval", dval));

			}
			return criteria.list();
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getLocdes(locId=" + locId + "dval=" + dval + ") query from Locdes: " + e.getMessage(), e);
		}
		return new ArrayList<Locdes>();
	}

	@SuppressWarnings("unchecked")
	public List<Locdes> getAllLocationDescriptionsByFilters(final String fcode,final String[] dval) throws MiddlewareQueryException {
		try {
			final StringBuilder sqlString = new StringBuilder();

			sqlString.append("SELECT ld.ldid,ld.locid,ld.dtype,ld.duid,ld.dval,ld.ddate,ld.dref ") //
					.append(" FROM locdes ld, udflds ud") //
					.append(" WHERE ld.dtype = ud.fldno");

			if (fcode != null) {
				sqlString.append(" and ud.fcode= '").append(fcode).append("' ");
			}

			if (dval != null) {
		        sqlString.append(" and ld.dval in ('").append(StringUtils.join(dval, "','")).append("')");
			}

			
			final SQLQuery query = this.getSession().createSQLQuery(sqlString.toString());
			query.addEntity(Locdes.class);

			return query.list();
		} catch (HibernateException e) {
			LocdesDAO.LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException(
					this.getLogExceptionMessage("getAllLocationDescriptionsByFilters", "", null, e.getMessage(), LocdesDAO.CLASS_NAME_LOCDES), e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Locdes> getAllLocationDescriptionsByFilters(final String fcode, final Integer locid, final String dval) throws MiddlewareQueryException {
		try {
			final StringBuilder sqlString = new StringBuilder();

			sqlString.append("SELECT ld.ldid,ld.locid,ld.dtype,ld.duid,ld.dval,ld.ddate,ld.dref ") //
					.append(" FROM locdes ld, udflds ud") //
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
			LocdesDAO.LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException(
					this.getLogExceptionMessage("getAllLocationDescriptionsByFilters", "", null, e.getMessage(), LocdesDAO.CLASS_NAME_LOCDES), e);
		}
	}

	public void deleteByLocationIds(final List<Integer> locids){
		try {
			this.getSession().createQuery("delete from Locdes "
				+ " where locid in (:locids) ")
				.setParameterList("locids", locids)
				.executeUpdate();
		} catch (final HibernateException e) {
			this.logAndThrowException("Error with deleteByLocationIds(locids=" + locids + ") query from Locdes: " + e.getMessage(), e);
		}
	}
}
