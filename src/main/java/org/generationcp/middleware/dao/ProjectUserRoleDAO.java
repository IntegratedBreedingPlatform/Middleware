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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserRole;
import org.generationcp.middleware.pojos.workbench.Role;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link ProjectUserRole}.
 *
 * @author Joyce Avestro
 *
 */
public class ProjectUserRoleDAO extends GenericDAO<ProjectUserRole, Integer> {
	
	public static final String GET_USER_IDS_BY_PROJECT_ID = "Select distinct pu.user_id from workbench_project_user_role pu inner join users u on u.userid = pu.user_id where u.ustatus = 0 and pu.project_id = :projectId"; 
	
	public static final String GET_USERS_BY_PROJECT_ID = "SELECT users.userid, users.instalid, users.ustatus, users.uaccess, users.utype, "
			+ "users.uname, users.upswd, users.personid, users.adate, users.cdate "
			+ "FROM users JOIN workbench_project_user_role pu ON users.userid = pu.user_id " + "WHERE pu.project_id = :projectId "
			+ "GROUP BY users.userid";

	public static final String GET_PERSONS_BY_PROJECT_ID = "SELECT users.userid, persons.personid, persons.fname, persons.ioname, "
			+ "persons.lname "
			+ "FROM persons "
			+ "JOIN users ON users.personid = persons.personid "
			+ "JOIN workbench_project_user_role pu ON users.userid = pu.user_id "
			+ "WHERE pu.project_id = :projectId GROUP BY users.userid";

	public static final String COUNT_USERS_BY_PROJECT_ID = "SELECT COUNT(DISTINCT user_id) " + "FROM workbench_project_user_role "
			+ "WHERE project_id = :projectId";
	
	@Override
	public ProjectUserRole saveOrUpdate(ProjectUserRole projectUser) throws MiddlewareQueryException {

		if (projectUser.getProject() == null || projectUser.getProject().getProjectId() == null) {
			throw new IllegalArgumentException("Project cannot be null");
		}
		if (projectUser.getUserId() == null) {
			throw new IllegalArgumentException("User cannot be null");
		}

		return super.saveOrUpdate(projectUser);
	}

	public List<ProjectUserRole> getByProject(Project project) throws MiddlewareQueryException {
		try {
			List<Criterion> criteria = new ArrayList<Criterion>();
			criteria.add(Restrictions.eq("project", project));
			return super.getByCriteria(criteria);
		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error in getByProject(project=" + project + ") query from ProjectUser: " + e.getMessage(),
					e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<User> getUsersByProjectId(final Long projectId) {
		final List<User> users = new ArrayList<>();
		try {
			if (projectId != null) {
				final SQLQuery query = this.getSession().createSQLQuery(ProjectUserRole.GET_USERS_BY_PROJECT_ID);
				query.setParameter("projectId", projectId);
				final List<Object> results = query.list();
				for (final Object o : results) {
					final Object[] user = (Object[]) o;
					final Integer userId = (Integer) user[0];
					final Integer instalId = (Integer) user[1];
					final Integer uStatus = (Integer) user[2];
					final Integer uAccess = (Integer) user[3];
					final Integer uType = (Integer) user[4];
					final String uName = (String) user[5];
					final String upswd = (String) user[6];
					final Integer personId = (Integer) user[7];
					final Integer aDate = (Integer) user[8];
					final Integer cDate = (Integer) user[9];
					final User u = new User(userId, instalId, uStatus, uAccess, uType, uName, upswd, personId, aDate, cDate);
					users.add(u);
				}
			}
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in getUsersByProjectId(projectId=" + projectId + ") query from ProjectUser: "
					+ e.getMessage(), e);
		}
		return users;
	}
	
	public List<Integer> getUserIDsByProjectId(final Long projectId) {
		final List<Integer> userIDs = new ArrayList<>();
		try {
			if (projectId != null) {
				final SQLQuery query = this.getSession().createSQLQuery(ProjectUserRoleDAO.GET_USER_IDS_BY_PROJECT_ID);
				query.setParameter("projectId", projectId);
				return query.list();
			}
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in getUsersByProjectId(projectId=" + projectId + ") query from ProjectUser: "
					+ e.getMessage(), e);
		}
		return userIDs;
	}

	@SuppressWarnings("unchecked")
	public Map<Integer, Person> getPersonsByProjectId(final Long projectId) {
		final Map<Integer, Person> persons = new HashMap<>();
		try {
			if (projectId != null) {
				final SQLQuery query = this.getSession().createSQLQuery(ProjectUserRole.GET_PERSONS_BY_PROJECT_ID);
				query.setParameter("projectId", projectId);
				final List<Object> results = query.list();
				for (final Object o : results) {
					final Object[] person = (Object[]) o;
					final Integer userId = (Integer) person[0];
					final Integer personId = (Integer) person[1];
					final String firstName = (String) person[2];
					final String middleName = (String) person[3];
					final String lastName = (String) person[4];
					final Person p = new Person(personId, firstName, middleName, lastName);
					persons.put(userId, p);
				}
			}
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in getUsersByProjectId(projectId=" + projectId + ") query from ProjectUser: "
					+ e.getMessage(), e);
		}
		return persons;
	}

	public long countUsersByProjectId(Long projectId) throws MiddlewareQueryException {
		try {
			if (projectId != null) {
				SQLQuery query = this.getSession().createSQLQuery(ProjectUserRole.COUNT_USERS_BY_PROJECT_ID);
				query.setParameter("projectId", projectId);
				return ((BigInteger) query.uniqueResult()).longValue();
			}
		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error in countUsersByProjectId(projectId=" + projectId + ") query from ProjectUser: "
					+ e.getMessage(), e);
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	public List<Role> getRolesByProjectAndUser(Project project, User user) throws MiddlewareQueryException {
		List<Role> roles = new ArrayList<Role>();
		try {
			if (project != null && user != null) {
				Criteria criteria = this.getSession().createCriteria(ProjectUserRole.class);
				criteria.add(Restrictions.eq("project", project));
				criteria.add(Restrictions.eq("userId", user.getUserid()));
				List<ProjectUserRole> projectUsers = criteria.list();

				for (ProjectUserRole projectUser : projectUsers) {
					roles.add(projectUser.getRole());
				}
			}
		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error in getByProjectAndUser(project=" + project + ", user=" + user + ") query from Role: "
					+ e.getMessage(), e);
		}
		return roles;
	}

	@SuppressWarnings("unchecked")
	public List<Project> getProjectsByUser(User user) throws MiddlewareQueryException {
		try {
			if (user != null) {
				Criteria criteria = this.getSession().createCriteria(ProjectUserRole.class);
				criteria.add(Restrictions.eq("userId", user.getUserid()));
				criteria.setProjection(Projections.distinct(Projections.property("project")));
				return criteria.list();
			}
		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error in getProjectsByUser(user=" + user + ") query from Project: " + e.getMessage(), e);
		}
		return new ArrayList<Project>();
	}
}
