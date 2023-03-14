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

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.domain.workbench.RoleType;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.service.api.program.ProgramSearchRequest;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * DAO class for {@link Project}.
 *
 */
public class ProjectDAO extends GenericDAO<Project, Long> {

	private static final Logger LOG = LoggerFactory.getLogger(ProjectDAO.class);

	/**
	 * User will be able to see the following programs: <br>
	 *  1. If user has instance role, then all programs for the assigned crop will be listed <br>
	 *  2. If user has crop role and no program roles associated to that crop, then all crop programs will be listed <br>
	 *  3. If user has a program role, then user will see ONLY programs with explicit access no matter if he has crop role assigned <br>
	 */
	public static final String GET_PROJECTS_BY_USER_ID =
		"SELECT  "
			+ "    p.* "
			+ "	FROM "
			+ "    	workbench_project p "
			+ "        	INNER JOIN "
			+ "    	crop_persons cp ON cp.crop_name = p.crop_type "
			+ "			INNER JOIN "
			+ "    	users u ON u.personid = cp.personid "
			+ "			INNER JOIN "
			+ "		users_roles ur ON ur.userid = u.userid "
			+ "			INNER JOIN "
			+ "		role r on ur.role_id = r.id "
			+ "	WHERE "
			+ "		u.userid = :userId and r.active = 1 "
			+ "		AND ( r.role_type_id = " + RoleType.INSTANCE.getId()
			+ "				OR ( r.role_type_id = " + RoleType.CROP.getId() + " and ur.crop_name = p.crop_type and NOT EXISTS ("
			+ "										 SELECT distinct p1.project_id "
			+ "                                      FROM workbench_project p1 "
			+ "                                             INNER JOIN "
			+ "                                      users_roles ur1 ON ur1.workbench_project_id = p1.project_id "
			+ "                                             INNER JOIN role r1 ON ur1.role_id = r1.id "
			+ "                                      where r1.role_type_id = " + RoleType.PROGRAM.getId()
			+ "                                        AND ur1.crop_name = p.crop_type AND ur1.userid = u.userid )) "
			+ "				OR ( r.role_type_id = "+ RoleType.PROGRAM.getId() +" and ur.crop_name = p.crop_type "
			+ "						and ur.workbench_project_id = p.project_id ) "
			+ "			) "
			+ "		AND ( :cropName IS NULL OR p.crop_type = :cropName ) "
			+ " 	AND ( :programName IS NULL OR p.project_name = :programName ) "
			+ " 	AND ( :programNameContainsString IS NULL OR p.project_name like :programNameContainsString ) "
			+ " 	AND ( :programDbId IS NULL OR p.project_uuid = :programDbId ) "
			+ " 	GROUP BY p.project_id ";

	public static final String LAST_OPENED_PROJECT_SUB_QUERY = "  (SELECT w.project_id FROM workbench_project w "
		+ " INNER JOIN workbench_project_user_info r ON w.project_id = r.project_id "
		+ " WHERE r.user_id = :userId "
		+ " AND r.last_open_date IS NOT NULL ORDER BY r.last_open_date DESC LIMIT 1) ";

	public ProjectDAO(final Session session) {
		super(session);
	}

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

	//FIXME, if 2 projects with same name, different crop, then an error may occur
	public void deleteProject(final String projectName) {
		// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out of
		// synch with
		// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
		// statement
		this.getSession().flush();

		final SQLQuery query = this.getSession().createSQLQuery("delete from workbench_project where project_name= '" + projectName + "';");

		query.executeUpdate();
	}

	public void deleteProjectByUUID(final String programUUID) {
		try {
			final SQLQuery query = this.getSession().createSQLQuery("delete from workbench_project where project_uuid = :programUUID");
			query.setParameter("programUUID", programUUID);
			query.executeUpdate();
		} catch (final Exception e) {
			final String message = "Error with deleteProjectByUUID(programUUID=" + programUUID + " ): " + e.getMessage();
			LOG.error(message, e);
			throw new MiddlewareQueryException(message);
		}
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

	public List<Project> getProjectsByFilter(final Pageable pageable, final ProgramSearchRequest programSearchRequest)
		throws MiddlewareException {
		final List<Project> projects = new ArrayList<>();
		try {
			final StringBuilder sb = new StringBuilder(GET_PROJECTS_BY_USER_ID);
			// Added this condition to sort by the last opened project
				sb.append("ORDER BY project_id =");
				sb.append(LAST_OPENED_PROJECT_SUB_QUERY);
				sb.append("desc, p.project_id");

			final SQLQuery sqlQuery = this.getSession().createSQLQuery(sb.toString());
			addProjectsByFilterParameters(programSearchRequest, sqlQuery);

			sqlQuery
					.addScalar("project_id")
					.addScalar("project_uuid")
					.addScalar("project_name")
					.addScalar("start_date")
					.addScalar("user_id")
					.addScalar("crop_type")
					.addScalar("last_open_date");

			sqlQuery.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);

			if (pageable != null) {
				final int start = pageable.getPageSize() * pageable.getPageNumber();
				final int numOfRows = pageable.getPageSize();

				sqlQuery.setFirstResult(start);
				sqlQuery.setMaxResults(numOfRows);
			}


			final List<Map<String, Object>> results = sqlQuery.list();

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

		} catch (final HibernateException e) {
			throw new MiddlewareException(
				"Error in getProjectsByFilter(start=" + pageable.getPageNumber() + ", numOfRows=" + pageable.getPageSize() + "): " + e.getMessage(), e);
		}
	}

	private static void addProjectsByFilterParameters(final ProgramSearchRequest programSearchRequest, final SQLQuery sqlQuery) {
		sqlQuery.setParameter("userId", programSearchRequest.getLoggedInUserId());
		sqlQuery.setParameter("cropName", programSearchRequest.getCommonCropName());
		sqlQuery.setParameter("programName", programSearchRequest.getProgramName());
		String programNameContainsString = null;
		if (!StringUtils.isBlank(programSearchRequest.getProgramNameContainsString())) {
			programNameContainsString = '%' + programSearchRequest.getProgramNameContainsString() + '%';
		}
		sqlQuery.setParameter("programNameContainsString", programNameContainsString);
		sqlQuery.setParameter("programDbId", programSearchRequest.getProgramDbId());
	}

	public long countProjectsByFilter(final ProgramSearchRequest programSearchRequest) throws MiddlewareException {
		try {
			final SQLQuery sqlQuery = this.getSession().createSQLQuery(GET_PROJECTS_BY_USER_ID);
			addProjectsByFilterParameters(programSearchRequest, sqlQuery);

			return sqlQuery.list().size();
		} catch (final HibernateException e) {
			throw new MiddlewareException("Error in countProjectsByFilter(): " + e.getMessage(), e);
		}
	}

	public List<Project> getProjectsByCropName(final String cropName) {
		final Criteria criteria = this.getSession().createCriteria(Project.class).add(Restrictions.eq("cropType.cropName", cropName));
		return criteria.list();
	}

}
