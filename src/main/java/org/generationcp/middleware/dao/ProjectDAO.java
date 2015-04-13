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
import org.generationcp.middleware.pojos.workbench.Project;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link Project}.
 *
 */
public class ProjectDAO extends GenericDAO<Project, Long> {

	@Override
	public Project getById(Long projectId) throws MiddlewareQueryException {
		try {
			if (projectId != null) {
				Criteria criteria =
						this.getSession().createCriteria(Project.class).add(Restrictions.eq("projectId", projectId)).setMaxResults(1);
				return (Project) criteria.uniqueResult();
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getById(projectId=" + projectId + ") query from Project: " + e.getMessage(), e);
		}
		return null;
	}

	public Project getByUuid(String projectUuid) throws MiddlewareQueryException {

		try {
			if (projectUuid != null) {
				Criteria criteria =
						this.getSession().createCriteria(Project.class).add(Restrictions.eq("uniqueID", projectUuid)).setMaxResults(1);
				return (Project) criteria.uniqueResult();
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getByUuid(uniqueID=" + projectUuid + ") query from Project: " + e.getMessage(), e);
		}
		return null;
	}

	public void deleteProject(String projectName) {
		SQLQuery query = this.getSession().createSQLQuery("delete from workbench_project where project_name= '" + projectName + "';");

		query.executeUpdate();
	}

	public void deleteDatabase(String projectName) {
		SQLQuery query = this.getSession().createSQLQuery("drop schema `" + projectName + "`;");

		query.executeUpdate();
	}

	public Project getByName(String projectName) throws MiddlewareQueryException {
		try {
			Criteria criteria =
					this.getSession().createCriteria(Project.class).add(Restrictions.eq("projectName", projectName)).setMaxResults(1);
			return (Project) criteria.uniqueResult();
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getByName(projectName=" + projectName + ") query from Project: " + e.getMessage(), e);
		}
		return null;
	}

	public Project getLastOpenedProject(Integer userId) throws MiddlewareQueryException {
		try {
			if (userId != null) {
				StringBuilder sb = new StringBuilder();
				sb.append("SELECT {w.*} FROM workbench_project w ")
				.append("INNER JOIN workbench_project_user_info r ON w.project_id = r.project_id ")
				.append("WHERE r.user_id = :userId AND r.last_open_date IS NOT NULL ORDER BY r.last_open_date DESC LIMIT 1 ;");

				SQLQuery query = this.getSession().createSQLQuery(sb.toString());
				query.addEntity("w", Project.class);
				query.setParameter("userId", userId);

				@SuppressWarnings("unchecked")
				List<Project> projectList = query.list();

				return !projectList.isEmpty() ? projectList.get(0) : null;
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getLastOpenedProject(userId=" + userId + ") query from Project " + e.getMessage(), e);
		}
		return null;
	}

	public Project getLastOpenedProjectAnyUser() throws MiddlewareQueryException {
		try {

			StringBuilder sb = new StringBuilder();
			sb.append("SELECT {w.*} FROM workbench_project w ")
					.append("INNER JOIN workbench_project_user_info r ON w.project_id = r.project_id ")
					.append("WHERE r.last_open_date IS NOT NULL ORDER BY r.last_open_date DESC LIMIT 1 ;");

			SQLQuery query = this.getSession().createSQLQuery(sb.toString());
			query.addEntity("w", Project.class);

			@SuppressWarnings("unchecked")
			List<Project> projectList = query.list();

			return !projectList.isEmpty() ? projectList.get(0) : null;

		} catch (HibernateException e) {
			this.logAndThrowException("Error with getLastOpenedProjectAnyUser(" + ") query from Project " + e.getMessage(), e);
		}
		return null;
	}
}
