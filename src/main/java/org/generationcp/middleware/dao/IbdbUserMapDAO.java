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

import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.workbench.IbdbUserMap;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link IbdbUserMap}.
 *
 */
public class IbdbUserMapDAO extends GenericDAO<IbdbUserMap, Long> {

	public Integer getLocalIbdbUserId(final Integer workbenchUserId, final Long projectId) throws MiddlewareQueryException {
		try {
			if (workbenchUserId != null && projectId != null) {
				final Query query = this.getSession().createSQLQuery(IbdbUserMap.GET_LOCAL_IBDB_USER_ID);
				query.setParameter("workbenchUserId", workbenchUserId);
				query.setParameter("projectId", projectId);
				return (Integer) query.uniqueResult();
			}
		} catch (final HibernateException e) {
			this.logAndThrowException("Error with getLocalIbdbUserId(workbenchUserId=" + workbenchUserId + ", projectId=" + projectId
					+ ") query from IbdbUserMap: " + e.getMessage(), e);
		}
		return null;
	}

	public Integer getWorkbenchUserId(final Integer ibdbUserId, final Long projectId) throws MiddlewareQueryException {
		try {
			if (ibdbUserId != null && projectId != null) {
				final Query query = this.getSession().createSQLQuery(IbdbUserMap.GET_WORKBENCH_USER_ID);
				query.setParameter("ibdbUserId", ibdbUserId);
				query.setParameter("projectId", projectId);
				return (Integer) query.uniqueResult();
			}
		} catch (final HibernateException e) {
			this.logAndThrowException("Error with getWorkbenchUserId(ibdbUserId=" + ibdbUserId + ", projectId=" + projectId
					+ ") query from IbdbUserMap: " + e.getMessage(), e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<IbdbUserMap> getIbdbUserMapByID(final Long projectId) throws MiddlewareQueryException {
		try {
			if (projectId != null) {
				return this.getSession().createCriteria(IbdbUserMap.class).add(Restrictions.eq("projectId", projectId)).list();
			}
		} catch (final HibernateException e) {
			this.logAndThrowException(
					"Error with getIbdbUserMapByID( projectId=" + projectId + ") query from IbdbUserMap: " + e.getMessage(), e);
		}
		return null;
	}

	public IbdbUserMap getIbdbUserMapByUserAndProjectID(final Integer workbenchUserId, final Long projectId) throws MiddlewareQueryException {
		try {
			if (projectId != null && workbenchUserId != null) {
				return (IbdbUserMap) this.getSession().createCriteria(IbdbUserMap.class).add(Restrictions.eq("projectId", projectId))
						.add(Restrictions.eq("workbenchUserId", workbenchUserId)).uniqueResult();
			}
		} catch (final HibernateException e) {
			this.logAndThrowException("Error with getIbdbUserMapByUserAndProjectID( projectId=" + projectId + ") query from IbdbUserMap: "
					+ e.getMessage(), e);
		}
		return null;
	}

	public void removeUsersFromProgram(final List<Integer> workbenchUserIds, final Long projectId) {
		// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out
		// of synch with
		// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
		// statement
		this.getSession().flush();
		final String sql = "DELETE rte"
			+ "ur FROM users_roles ur WHERE ur.workbench_project_id = :projectId AND ur.userid IN (:workbenchUserIds)";
		final SQLQuery statement = this.getSession().createSQLQuery(sql);
		statement.setParameter("projectId", projectId);
		statement.setParameterList("workbenchUserIds", workbenchUserIds);
		statement.executeUpdate();
	}
}
