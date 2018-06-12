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
import java.util.Collection;
import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Locdes;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.service.api.user.UserDto;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

/**
 * DAO class for {@link User}.
 *
 */
public class UserDAO extends GenericDAO<User, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(UserDAO.class);

	public User getByUsernameAndPassword(final String username, final String password) {
		try {
			if (username != null && password != null) {
				final Criteria criteria = this.getSession().createCriteria(User.class).add(Restrictions.eq("name", username))
						.add(Restrictions.eq("password", password));

				@SuppressWarnings("unchecked")
				final List<User> users = criteria.list();
				return !users.isEmpty() ? users.get(0) : null;
			}
		} catch (final HibernateException e) {
			final String message = "Error with getByUsernameAndPassword(username=" + username + ") query from User: " + e.getMessage();
			UserDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
		return null;
	}

	public boolean isPasswordSameAsUserName(final String username) {
		try {
			if (username != null) {
				final Criteria criteria = this.getSession().createCriteria(User.class).add(Restrictions.eq("name", username))
						.add(Restrictions.eq("password", username));

				@SuppressWarnings("unchecked")
				final List<User> users = criteria.list();
				return !users.isEmpty();
			}
		} catch (final HibernateException e) {
			final String message = "Error with isPasswordSameAsUserName(username=" + username + ") query from User: " + e.getMessage();
			UserDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
		return false;
	}

	public User getUserDetailsByUsername(final String username) {
		try {
			if (username != null) {
				final Criteria criteria = this.getSession().createCriteria(User.class).add(Restrictions.eq("name", username));
				@SuppressWarnings("unchecked")
				final List<User> users = criteria.list();
				return !users.isEmpty() ? users.get(0) : null;
			}
		} catch (final HibernateException e) {
			final String message = "Error with getUserDetailsByUsername(username=" + username + ") query from User: " + e.getMessage();
			UserDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
		return null;
	}

	public boolean isUsernameExists(final String username) {
		try {
			if (username != null) {
				final Criteria criteria = this.getSession().createCriteria(User.class);
				criteria.add(Restrictions.eq("name", username));

				// used a List in case of dirty data
				@SuppressWarnings("unchecked")
				final List<User> users = criteria.list();

				return !users.isEmpty();
			}
		} catch (final HibernateException e) {
			final String message = "Error with isUsernameExists(username=" + username + ") query from User: " + e.getMessage();
			UserDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
		return false;
	}

	public User getUserByUserName(final String username) {
		try {
			if (username != null) {
				final Criteria criteria = this.getSession().createCriteria(User.class);
				criteria.add(Restrictions.eq("name", username));

				// used a List in case of dirty data
				@SuppressWarnings("unchecked")
				final List<User> users = criteria.list();

				return users.isEmpty() ? null : users.get(0);
			}
		} catch (final HibernateException e) {
			final String message = "Error with getUserByUserName(username=" + username + ") query from User: " + e.getMessage();
			UserDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
		return null;
	}

	

	@SuppressWarnings("unchecked")
	public List<Integer> getUserIdsByCountryIds(final Collection<Integer> countryIds) {
		try {
			if (countryIds != null && !countryIds.isEmpty()) {
				final Criteria criteria = this.getSession().createCriteria(Locdes.class);
				criteria.createAlias("location", "l");
				criteria.add(Restrictions.in("l.cntryid", countryIds));
				criteria.setProjection(Projections.distinct(Projections.property("user.userid")));
				return criteria.list();
			}
		} catch (final HibernateException e) {
			final String message = "Error with getUserIdsByCountryIds query from User: " + e.getMessage();
			UserDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
		return new ArrayList<>();
	}

	@SuppressWarnings("unchecked")
	public List<User> getByIds(final List<Integer> ids) {
		List<User> toReturn = new ArrayList<>();

		if (ids == null || ids.isEmpty()) {
			return toReturn;
		}

		try {
			final Criteria criteria = this.getSession().createCriteria(User.class);
			criteria.add(Restrictions.in("userid", ids));
			toReturn = criteria.list();
		} catch (final HibernateException e) {
			final String message = "Error with getByIds query from User: " + e.getMessage();
			UserDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
		return toReturn;
	}

	public User getUserByFullname(final String fullname) {
		User user = null;
		try {
			final Query query = this.getSession().getNamedQuery(User.GET_BY_FULLNAME);
			query.setParameter("fullname", fullname);
			user = (User) query.uniqueResult();
		} catch (final HibernateException e) {
			final String message = "Error with getUserByFullname(name=" + fullname + ") query from User: " + e.getMessage();
			UserDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
		return user;
	}

	public List<UserDto> getUsersAssociatedToStudy(final Integer studyId) {
		Preconditions.checkNotNull(studyId);
		final List<UserDto> users = new ArrayList<>();

		try {
			final Query query =
				this.getSession().createSQLQuery(User.GET_USERS_ASSOCIATED_TO_STUDY).addScalar("personId").addScalar("fName")
					.addScalar("lName").addScalar("email").addScalar("roleId").addScalar("roleName").setParameter("studyId", studyId);
			final List<Object> results = query.list();
			this.mapUsers(users, results);
			return users;
		} catch (final MiddlewareQueryException e) {
			final String message = "Error with getUsersAssociatedToStudy() query from studyId: " + studyId;
			UserDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public List<UserDto> getUsersForEnvironment(final Integer instanceId) {
		Preconditions.checkNotNull(instanceId);
		final List<UserDto> users = new ArrayList<>();
		final StringBuilder sql = new StringBuilder().append("SELECT DISTINCT ")
				.append("    person.personid as personId, person.fname as fName, person.lname as lName, person.pemail as email , role.id as roleId, role.description as roleName ")
				.append("FROM ").append("    cvterm scale ").append("        INNER JOIN ")
				.append("    cvterm_relationship r ON (r.object_id = scale.cvterm_id) ").append("        INNER JOIN ")
				.append("    cvterm variable ON (r.subject_id = variable.cvterm_id) ").append("        INNER JOIN ")
				.append("    nd_geolocationprop pp ON (pp.type_id = variable.cvterm_id) ")
				.append("        INNER JOIN workbench.persons person ").append("    ON (pp.value = person.personid) ")
				.append("    INNER JOIN workbench.users user on (user.personid = person.personid) ")
				.append("    LEFT join workbench.users_roles urole on (urole.userid = user.userid) ")
				.append("    LEFT join workbench.role role on (role.id = urole.role_id) ")
				.append("WHERE ")
				.append("    pp.nd_geolocation_id = :instanceDbId ").append("        AND r.object_id = 1901    ");
		try {
			final Query query = this.getSession().createSQLQuery(sql.toString()).addScalar("personId").addScalar("fName").addScalar("lName")
					.addScalar("email").addScalar("roleId").addScalar("roleName").setParameter("instanceDbId", instanceId);
			final List<Object> results = query.list();
			this.mapUsers(users, results);
			return users;
		} catch (final MiddlewareQueryException e) {
			final String message = "Error with getUsersForEnvironment() query from instanceId: " + instanceId;
			UserDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	private void mapUsers(final List<UserDto> users, final List<Object> results) {
		for (final Object obj : results) {
			final Object[] row = (Object[]) obj;
			final UserDto user = new UserDto();
			user.setUserId((Integer) row[0]);
			user.setFirstName((String) row[1]);
			user.setLastName((String) row[2]);
			user.setEmail((String) row[3]);
			final Integer roleId = (Integer) row[4];
			final String roleName = (String) row[5];
			user.setRole(new Role(roleId, roleName));
			users.add(user);
		}
	}

}
