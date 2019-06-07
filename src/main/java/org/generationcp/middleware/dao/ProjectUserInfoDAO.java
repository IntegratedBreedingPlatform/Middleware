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
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO class for {@link ProjectUserInfo}.
 *
 */
@Transactional
public class ProjectUserInfoDAO extends GenericDAO<ProjectUserInfo, Integer> {
	
	public static final String GET_USERS_BY_PROJECT_ID = 
			"SELECT users.userid, users.instalid, users.ustatus, users.uaccess, users.utype, "
			+ "users.uname, users.upswd, users.personid, users.adate, users.cdate "
			+ "FROM users "
			+ "JOIN workbench_project_user_info pu ON users.userid = pu.user_id " 
			+ "WHERE pu.project_id = :projectId "
			+ "GROUP BY users.userid";
	
	public static final String GET_ACTIVE_USER_IDS_BY_PROJECT_ID = 
			"SELECT DISTINCT pu.user_id "
			+ "FROM workbench_project_user_info pu "
			+ "INNER JOIN users u ON u.userid = pu.user_id "
			+ "WHERE u.ustatus = 0 AND pu.project_id = :projectId"; 
	
	public static final String GET_PERSONS_BY_PROJECT_ID = "SELECT users.userid, persons.personid, persons.fname, persons.ioname, "
			+ "persons.lname "
			+ "FROM persons "
			+ "JOIN users ON users.personid = persons.personid "
			+ "JOIN workbench_project_user_info pu ON users.userid = pu.user_id "
			+ "WHERE pu.project_id = :projectId GROUP BY users.userid";

	@SuppressWarnings("unchecked")
	public List<WorkbenchUser> getUsersByProjectId(final Long projectId) {
		final List<WorkbenchUser> users = new ArrayList<>();
		try {
			if (projectId != null) {
				final SQLQuery query = this.getSession().createSQLQuery(ProjectUserInfoDAO.GET_USERS_BY_PROJECT_ID);
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
					final WorkbenchUser u = new WorkbenchUser(userId, instalId, uStatus, uAccess, uType, uName, upswd, personId, aDate, cDate);
					users.add(u);
				}
			}
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in getUsersByProjectId(projectId=" + projectId + ") query from ProjectUserInfoDao: "
					+ e.getMessage(), e);
		}
		return users;
	}

	public List<Integer> getActiveUserIDsByProjectId(final Long projectId) {
		final List<Integer> userIDs = new ArrayList<>();
		try {
			if (projectId != null) {
				final SQLQuery query = this.getSession().createSQLQuery(ProjectUserInfoDAO.GET_ACTIVE_USER_IDS_BY_PROJECT_ID);
				query.setParameter("projectId", projectId);
				return query.list();
			}
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in getActiveUserIDsByProjectId(projectId=" + projectId + ") query from ProjectUser: "
					+ e.getMessage(), e);
		}
		return userIDs;
	}

	public ProjectUserInfo getByProjectIdAndUserId(final Long projectId, final Integer userId) {
		try {
			if (projectId != null && userId != null) {
				final Criteria criteria = this.getSession().createCriteria(ProjectUserInfo.class);
				criteria.add(Restrictions.eq("project.projectId", projectId));
				criteria.add(Restrictions.eq("userId", userId));
				return (ProjectUserInfo) criteria.uniqueResult();
			}
		} catch (final HibernateException ex) {
			throw new MiddlewareQueryException("Error in getByProjectIdAndUserId(projectId = " + projectId + ", userId = " + userId + "):"
					+ ex.getMessage(), ex);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<ProjectUserInfo> getByProjectId(final Long projectId) {
		try {
			if (projectId != null) {
				final Criteria criteria = this.getSession().createCriteria(ProjectUserInfo.class);
				criteria.add(Restrictions.eq("project.projectId", projectId));

				return criteria.list();
			}
		} catch (final HibernateException ex) {
			throw new MiddlewareQueryException("Error in getByProjectId(projectId = " + projectId + "):" + ex.getMessage(), ex);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<ProjectUserInfo> getByProjectIdAndUserIds(final Long projectId, final List<Integer> userIds) {
		try {
			final Criteria criteria = this.getSession().createCriteria(ProjectUserInfo.class);
			criteria.add(Restrictions.eq("project.projectId", projectId));
			criteria.add(Restrictions.in("userId", userIds));
			return criteria.list();
		} catch (final HibernateException ex) {
			throw new MiddlewareQueryException("Error in getByProjectIdAndUserIds(projectId = " + projectId + ", userIds = " + userIds + "):"
					+ ex.getMessage(), ex);
		}
	}
	
	@SuppressWarnings("unchecked")
	public Map<Integer, Person> getPersonsByProjectId(final Long projectId) {
		final Map<Integer, Person> persons = new HashMap<>();
		try {
			if (projectId != null) {
				final SQLQuery query = this.getSession().createSQLQuery(ProjectUserInfoDAO.GET_PERSONS_BY_PROJECT_ID);
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
			throw new MiddlewareQueryException("Error in getPersonsByProjectId(projectId=" + projectId + ") query from ProjectUser: "
					+ e.getMessage(), e);
		}
		return persons;
	}
}
