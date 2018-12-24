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

	public Integer getLocalIbdbUserId(Integer workbenchUserId, Long projectId) throws MiddlewareQueryException {
		try {
			if (workbenchUserId != null && projectId != null) {
				Query query = this.getSession().createSQLQuery(IbdbUserMap.GET_LOCAL_IBDB_USER_ID);
				query.setParameter("workbenchUserId", workbenchUserId);
				query.setParameter("projectId", projectId);
				return (Integer) query.uniqueResult();
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getLocalIbdbUserId(workbenchUserId=" + workbenchUserId + ", projectId=" + projectId
					+ ") query from IbdbUserMap: " + e.getMessage(), e);
		}
		return null;
	}

	public Integer getWorkbenchUserId(Integer ibdbUserId, Long projectId) throws MiddlewareQueryException {
		try {
			if (ibdbUserId != null && projectId != null) {
				Query query = this.getSession().createSQLQuery(IbdbUserMap.GET_WORKBENCH_USER_ID);
				query.setParameter("ibdbUserId", ibdbUserId);
				query.setParameter("projectId", projectId);
				return (Integer) query.uniqueResult();
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getWorkbenchUserId(ibdbUserId=" + ibdbUserId + ", projectId=" + projectId
					+ ") query from IbdbUserMap: " + e.getMessage(), e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<IbdbUserMap> getIbdbUserMapByID(Long projectId) throws MiddlewareQueryException {
		try {
			if (projectId != null) {
				return this.getSession().createCriteria(IbdbUserMap.class).add(Restrictions.eq("projectId", projectId)).list();
			}
		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error with getIbdbUserMapByID( projectId=" + projectId + ") query from IbdbUserMap: " + e.getMessage(), e);
		}
		return null;
	}

	public IbdbUserMap getIbdbUserMapByUserAndProjectID(Integer workbenchUserId, Long projectId) throws MiddlewareQueryException {
		try {
			if (projectId != null && workbenchUserId != null) {
				return (IbdbUserMap) this.getSession().createCriteria(IbdbUserMap.class).add(Restrictions.eq("projectId", projectId))
						.add(Restrictions.eq("workbenchUserId", workbenchUserId)).uniqueResult();
			}
		} catch (HibernateException e) {
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
		final String sql = "DELETE project_user_info, ibdb_user_map FROM workbench_project_user_info project_user_info"
			+ " INNER JOIN workbench_ibdb_user_map ibdb_user_map"
			+ " ON project_user_info.project_id = ibdb_user_map.project_id"
			+ " AND project_user_info.user_id = ibdb_user_map.workbench_user_id"
			+ " WHERE project_user_info.project_id = :projectId AND project_user_info.user_id in (:workbenchUserIds)";
		final SQLQuery statement = this.getSession().createSQLQuery(sql);
		statement.setParameter("projectId", projectId);
		statement.setParameterList("workbenchUserIds", workbenchUserIds);
		statement.executeUpdate();
	}
}
