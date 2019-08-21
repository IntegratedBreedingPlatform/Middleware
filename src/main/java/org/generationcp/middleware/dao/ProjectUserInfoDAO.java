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
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO class for {@link ProjectUserInfo}.
 *
 */
@Transactional
public class ProjectUserInfoDAO extends GenericDAO<ProjectUserInfo, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(ProjectUserInfoDAO.class);

	public static final String GET_PERSONS_BY_PROJECT_ID = "SELECT users.userid, persons.personid, persons.fname, persons.ioname, "
		+ "persons.lname "
		+ "FROM persons "
		+ "JOIN users ON users.personid = persons.personid "
		+ "JOIN workbench_project_user_info pu ON users.userid = pu.user_id "
		+ "WHERE pu.project_id = :projectId GROUP BY users.userid";

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

	//Used to get the last opened date, if there is no row, a new one will be created
	public ProjectUserInfo getByProjectIdAndUserId(final Long projectId, final Integer userId) {
		try {
			if (projectId != null && userId != null) {
				final Criteria criteria = this.getSession().createCriteria(ProjectUserInfo.class);
				criteria.add(Restrictions.eq("project.projectId", projectId));
				criteria.add(Restrictions.eq("user.userid", userId));
				return (ProjectUserInfo) criteria.uniqueResult();
			}
		} catch (final HibernateException ex) {
			throw new MiddlewareQueryException("Error in getByProjectIdAndUserId(projectId = " + projectId + ", userId = " + userId + "):"
				+ ex.getMessage(), ex);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	// Used when deleting a program
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

}
