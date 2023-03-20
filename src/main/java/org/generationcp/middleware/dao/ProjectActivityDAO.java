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

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class for {@link ProjectActivity}.
 *
 * @author Joyce Avestro
 *
 */
public class ProjectActivityDAO extends GenericDAO<ProjectActivity, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(ProjectActivityDAO.class);

	public ProjectActivityDAO(final Session session) {
		super(session);
	}

	public void deleteAllProjectActivities(final String programUUID) {
		try {
			final String sql =
				"DELETE a FROM workbench_project_activity a INNER JOIN workbench_project p ON p.project_id = a.project_id WHERE p.project_uuid = :programUUID";
			final SQLQuery sqlQuery = this.getSession().createSQLQuery(sql);
			sqlQuery.setParameter("programUUID", programUUID);
			sqlQuery.executeUpdate();
		} catch (final Exception e) {
			final String message = "Error with deleteAllProjectActivities(programUUID=" + programUUID + " ): " + e.getMessage();
			LOG.error(message, e);
			throw new MiddlewareQueryException(message);
		}
	}

	/**
	 * Returns a list of {@link ProjectActivity} records by project id.
	 *
	 * @param projectId the project id
	 * @param start the first row to retrieve
	 * @param numOfRows the number of rows to retrieve
	 * @return the list of {@link ProjectActivity} records
	 * @throws MiddlewareQueryException the MiddlewareQueryException
	 */
	@SuppressWarnings("unchecked")
	public List<ProjectActivity> getByProjectId(final Long projectId, final int start, final int numOfRows)
		throws MiddlewareQueryException {
		try {
			if (projectId != null) {
				final Criteria criteria = this.getSession().createCriteria(ProjectActivity.class);
				final Project p = new Project();
				p.setProjectId(projectId);
				criteria.add(Restrictions.eq("project", p));
				criteria.setFirstResult(start);
				criteria.setMaxResults(numOfRows);
				criteria.addOrder(Order.desc("createdAt"));
				return criteria.list();
			}
			return new ArrayList<>();
		} catch (final HibernateException e) {
			final String message = "Error with getByProjectId(projectId=" + projectId + ") query from ProjectActivity " + e.getMessage();
			LOG.error(message, e);
			throw new MiddlewareQueryException(message);
		}
	}
}
