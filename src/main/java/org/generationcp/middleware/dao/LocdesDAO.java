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
import org.generationcp.middleware.pojos.LocdesType;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class LocdesDAO extends GenericDAO<Locdes, Integer> {

	private static final String CLASS_NAME_LOCDES = "Locdes";
	
	private static final Logger LOG = LoggerFactory.getLogger(LocdesDAO.class);

	private static final String GET_BLOCK_PARENTS_QUERY =
		"SELECT parent.locid FROM locdes parent"
			+ " WHERE parent.locid in (select block.dval from locdes block"
			+ " 	where block.locid in (:blockIds) and block.dtype = " + LocdesType.BLOCK_PARENT.getId() + ")"
			+ " AND parent.locid not in (select oth_block.dval from locdes oth_block"
			+ " 	where oth_block.locid not in (:blockIds) and oth_block.dtype = " + LocdesType.BLOCK_PARENT.getId() + ")";

	public LocdesDAO(final Session session) {
		super(session);
	}

	@SuppressWarnings("unchecked")
	public List<Locdes> getByLocation(final Integer locId) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(Locdes.class);
			criteria.add(Restrictions.eq("locationId", locId));
			return criteria.list();
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getByLocation(locId=" + locId + ") query from Locdes: " + e.getMessage(), e);
		}
		return new ArrayList<Locdes>();
	}

	public List<Locdes> getLocdes(final List<Integer> locIds, final List<String> dvals) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(Locdes.class);
			if (!CollectionUtils.isEmpty(locIds)) {
				criteria.add(Restrictions.in("locationId", locIds));
			}
			if (!CollectionUtils.isEmpty(dvals)) {
				criteria.add(Restrictions.in("dval", dvals));

			}
			return criteria.list();
		} catch (HibernateException e) {
			final String message = "Error with getLocdes(locIds=" + locIds + "dvals=" + dvals + ") in LocdesDAO: " + e.getMessage();
			LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
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
			final String message = "Error with deleteByLocationIds(locids=" + locids + ") in LocdesDAO: " + e.getMessage();
			LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	/**
	 * @param blockIdsToDelete
	 * @return block parent IDs that is not used as block parent after specified blocks deletion
	 */
	public List<Integer> getBlockParentsToDelete(final List<Integer> blockIdsToDelete) {
		try {
			final SQLQuery query = this.getSession().createSQLQuery(GET_BLOCK_PARENTS_QUERY);
			query.setParameterList("blockIds", blockIdsToDelete);
			return query.list();
		} catch (final HibernateException e) {
			LocdesDAO.LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage("getBlockParentsToDelete", "blockIdsToDelete",
					String.valueOf(blockIdsToDelete), e.getMessage(), "LocDes"), e);
		}
	}
}
