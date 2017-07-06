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
import java.util.Map;

import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.service.api.program.ProgramFilters;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link Project}.
 *
 */
public class ProjectDAO extends GenericDAO<Project, Long> {

	public Project getByUuid(final String projectUuid) throws MiddlewareQueryException {

		try {
			if (projectUuid != null) {
				final Criteria criteria =
					this.getSession().createCriteria(Project.class).add(Restrictions.eq("uniqueID", projectUuid)).setMaxResults(1);
				return (Project) criteria.uniqueResult();
			}
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error with getByUuid(uniqueID=" + projectUuid + ") query from Project: " + e.getMessage(), e);
		}
		return null;
	}

	@Override
	public Project getById(final Long projectId) throws MiddlewareQueryException {
		try {
			if (projectId != null) {
				final Criteria criteria =
						this.getSession().createCriteria(Project.class).add(Restrictions.eq("projectId", projectId)).setMaxResults(1);
				return (Project) criteria.uniqueResult();
			}
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error with getById(projectId=" + projectId + ") query from Project: " + e.getMessage(), e);
		}
		return null;
	}

	public Project getByUuid(final String projectUuid, final String cropType) throws MiddlewareQueryException {

		try {
			if (projectUuid != null) {
				final Criteria criteria =
						this.getSession().createCriteria(Project.class).add(Restrictions.eq("uniqueID", projectUuid))
								.add(Restrictions.eq("cropType.cropName", cropType)).setMaxResults(1);
				return (Project) criteria.uniqueResult();
			}
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error with getByUuid(uniqueID=" + projectUuid + ") query from Project: " + e.getMessage(), e);
		}
		return null;
	}

	public void deleteProject(final String projectName) {
		// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out of
		// synch with
		// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
		// statement
		this.getSession().flush();

		final SQLQuery query = this.getSession().createSQLQuery("delete from workbench_project where project_name= '" + projectName + "';");

		query.executeUpdate();
	}

	public Project getProjectByNameAndCrop(final String projectName, final CropType cropType) throws MiddlewareQueryException {
		final Criteria criteria = this.getSession().createCriteria(Project.class).add(Restrictions.eq("projectName", projectName))
				.add(Restrictions.eq("cropType", cropType)).setMaxResults(1);
		return (Project) criteria.uniqueResult();
	}

	public Project getLastOpenedProject(final Integer userId) throws MiddlewareQueryException {
		try {
			if (userId != null) {
				final StringBuilder sb = new StringBuilder();
				sb.append("SELECT {w.*} FROM workbench_project w ")
						.append("INNER JOIN workbench_project_user_info r ON w.project_id = r.project_id ")
						.append("WHERE r.user_id = :userId AND r.last_open_date IS NOT NULL ORDER BY r.last_open_date DESC LIMIT 1 ;");

				final SQLQuery query = this.getSession().createSQLQuery(sb.toString());
				query.addEntity("w", Project.class);
				query.setParameter("userId", userId);

				@SuppressWarnings("unchecked")
				final List<Project> projectList = query.list();

				return !projectList.isEmpty() ? projectList.get(0) : null;
			}
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error with getLastOpenedProject(userId=" + userId + ") query from Project " + e.getMessage(), e);
		}
		return null;
	}

	public Project getLastOpenedProjectAnyUser() throws MiddlewareQueryException {
		try {

			final StringBuilder sb = new StringBuilder();
			sb.append("SELECT {w.*} FROM workbench_project w ")
					.append("INNER JOIN workbench_project_user_info r ON w.project_id = r.project_id ")
					.append("WHERE r.last_open_date IS NOT NULL ORDER BY r.last_open_date DESC LIMIT 1 ;");

			final SQLQuery query = this.getSession().createSQLQuery(sb.toString());
			query.addEntity("w", Project.class);

			@SuppressWarnings("unchecked")
			final List<Project> projectList = query.list();

			return !projectList.isEmpty() ? projectList.get(0) : null;

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error with getLastOpenedProjectAnyUser(" + ") query from Project " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Project> getProjectsByCrop(final CropType cropType) throws MiddlewareQueryException {
		final Criteria criteria = this.getSession().createCriteria(Project.class).add(Restrictions.eq("cropType", cropType));
		return criteria.list();
	}

	public List<Project> getProjectsByFilter(final int pageNumber,final int pageSize, final Map<ProgramFilters, Object> filters)
		throws MiddlewareException {
		try {
			Criteria criteria = this.getSession().createCriteria(Project.class);
			for (Map.Entry<ProgramFilters, Object> entry : filters.entrySet()) {
				ProgramFilters filter = entry.getKey();
				Object value = entry.getValue();
				criteria.add(Restrictions.eq(filter.getStatement(), value));
			}

			int start = pageSize * (pageNumber - 1);
			int numOfRows = pageSize;

			criteria.setFirstResult(start);
			criteria.setMaxResults(numOfRows);
			return criteria.list();
		} catch (HibernateException e) {
			throw new MiddlewareException(
				"Error in getProjectsByFilter(start=" + pageNumber + ", numOfRows=" + pageSize + "): " + e.getMessage(), e);
		}
	}

	public long countProjectsByFilter(final Map<ProgramFilters, Object> filters) throws MiddlewareException {
		try {
			Criteria criteria = this.getSession().createCriteria(Project.class);
			for (Map.Entry<ProgramFilters, Object> entry : filters.entrySet()) {
				ProgramFilters filter = entry.getKey();
				Object value = entry.getValue();
				criteria.add(Restrictions.eq(filter.getStatement(), value));
			}

			return criteria.list().size();
		} catch (HibernateException e) {
			throw new MiddlewareException("Error in countProjectsByFilter(): " + e.getMessage(), e);
		}
	}
}
