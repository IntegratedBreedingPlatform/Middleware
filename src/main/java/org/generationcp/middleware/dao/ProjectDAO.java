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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.program.ProgramFilters;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToEntityMapResultTransformer;

/**
 * DAO class for {@link Project}.
 *
 */
public class ProjectDAO extends GenericDAO<Project, Long> {

	public static final String GET_PROJECTS_BY_USER_ID =
		"SELECT  "
			+ "    p.* "
			+ "FROM "
			+ "    workbench_project p "
			+ "        INNER JOIN "
			+ "    workbench_crop wc ON p.crop_type = wc.crop_name "
			+ "        INNER JOIN "
			+ "    users_crops uc ON uc.crop_name = wc.crop_name "
			+ "WHERE "
			+ "    uc.user_id = :userId "
			+ "UNION      "
			+ "SELECT  "
			+ "    p.* "
			+ "FROM "
			+ "    workbench_project p "
			+ "        INNER JOIN "
			+ "    workbench_crop wc ON p.crop_type = wc.crop_name "
			+ "        INNER JOIN "
			+ "    users_crops uc ON uc.crop_name = wc.crop_name "
			+ " 		INNER JOIN "
			+ "    users_roles ur ON ur.userid = uc.user_id"
			+ " WHERE "
			+ "    uc.user_id = :userId "
			+ "AND ur.workbench_project_id = p.project_id "
			+ "UNION      "
			+ "SELECT  "
			+ "    p.* "
			+ "FROM "
			+ "    workbench_project p "
			+ "        INNER JOIN "
			+ "    workbench_crop wc ON p.crop_type = wc.crop_name "
			+ "        INNER JOIN "
			+ "    users_crops uc ON uc.crop_name = wc.crop_name "
			+ "        INNER JOIN "
			+ "    users_roles ur ON ur.userid = uc.user_id "
			+ "        AND ur.workbench_project_id = p.project_id "
			+ "WHERE "
			+ "    uc.user_id = :userId";

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
			final Criteria criteria = this.getSession().createCriteria(Project.class);
			for (Map.Entry<ProgramFilters, Object> entry : filters.entrySet()) {
				final ProgramFilters filter = entry.getKey();
				final Object value = entry.getValue();
				criteria.add(Restrictions.eq(filter.getStatement(), value));
			}

			final int start = pageSize * (pageNumber - 1);
			final int numOfRows = pageSize;

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
			final Criteria criteria = this.getSession().createCriteria(Project.class);
			for (Map.Entry<ProgramFilters, Object> entry : filters.entrySet()) {
				final ProgramFilters filter = entry.getKey();
				final Object value = entry.getValue();
				criteria.add(Restrictions.eq(filter.getStatement(), value));
			}

			return criteria.list().size();
		} catch (HibernateException e) {
			throw new MiddlewareException("Error in countProjectsByFilter(): " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Project> getProjectsByUser(final WorkbenchUser user) {
		final List<Project> projects = new ArrayList<>();
		try {
			if (user != null) {
				final SQLQuery query = this.getSession().createSQLQuery(GET_PROJECTS_BY_USER_ID);
				query.setParameter("userId", user.getUserid());
				query
					.addScalar("project_id")
					.addScalar("project_uuid")
					.addScalar("project_name")
					.addScalar("start_date")
					.addScalar("user_id")
					.addScalar("crop_type")
					.addScalar("last_open_date");
				query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
				final List<Map<String, Object>> results = query.list();

				for (final Map<String, Object> result : results) {
					final Long project_id = Long.valueOf((Integer) result.get("project_id"));
					final String project_uuid = (String) result.get("project_uuid");
					final String project_name = (String) result.get("project_name");
					final Date start_date = (Date) result.get("start_date");
					final Integer user_id = (Integer) result.get("user_id");
					final CropType crop_type = new CropType((String) result.get("crop_type"));
					final Date last_open_date = (Date) result.get("last_open_date");
					final Project u = new Project(project_id, project_uuid,
						project_name, start_date,
						user_id, crop_type, last_open_date);
					projects.add(u);
				}
				return projects;
			}
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				"Error in getProjectsByUser(user=" + user + ") query from ProjectUserInfoDao: " + e.getMessage(), e);
		}
		return new ArrayList<>();
	}

}
