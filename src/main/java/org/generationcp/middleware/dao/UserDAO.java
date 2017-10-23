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

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Locdes;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.service.api.user.UserDto;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

/**
 * DAO class for {@link User}.
 *
 */
public class UserDAO extends GenericDAO<User, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(UserDAO.class);

	public User getByUsernameAndPassword(final String username, final String password) throws MiddlewareQueryException {
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

	public boolean isPasswordSameAsUserName(final String username) throws MiddlewareQueryException {
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

	public boolean changePassword(final String username, final String password) throws MiddlewareQueryException {
		try {
			// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out
			// of synch with
			// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
			// statement
			this.getSession().flush();

			if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
				final String queryString = "UPDATE users SET upswd = :password WHERE uname LIKE :username";
				final Session s = this.getSession();
				final Query q = s.createSQLQuery(queryString);
				q.setString("username", username);
				q.setString("password", password);
				final int success = q.executeUpdate();

				return success > 0;
			}
		} catch (final Exception e) {
			final String message = "Error with changePassword(username=" + username + ") query from User: " + e.getMessage();
			UserDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
		return false;
	}

	public User getUserDetailsByUsername(final String username) throws MiddlewareQueryException {
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

	public boolean isUsernameExists(final String username) throws MiddlewareQueryException {
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

	public User getUserByUserName(final String username) throws MiddlewareQueryException {
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
	public List<User> getByNameUsingEqual(final String name, final int start, final int numOfRows) throws MiddlewareQueryException {
		try {
			if (name != null) {
				final Query query = this.getSession().getNamedQuery(User.GET_BY_NAME_USING_EQUAL);
				query.setParameter("name", name);
				query.setFirstResult(start);
				query.setMaxResults(numOfRows);
				return query.list();
			}
		} catch (final HibernateException e) {
			final String message = "Error with getByNameUsingEqual(name=" + name + ") query from User: " + e.getMessage();
			UserDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
		return new ArrayList<>();
	}

	@SuppressWarnings("unchecked")
	public List<User> getByNameUsingLike(final String name, final int start, final int numOfRows) throws MiddlewareQueryException {
		try {
			if (name != null) {
				final Query query = this.getSession().getNamedQuery(User.GET_BY_NAME_USING_LIKE);
				query.setParameter("name", name);
				query.setFirstResult(start);
				query.setMaxResults(numOfRows);
				return query.list();
			}
		} catch (final HibernateException e) {
			final String message = "Error with getByNameUsingLike(name=" + name + ") query from User: " + e.getMessage();
			UserDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
		return new ArrayList<>();
	}

	@SuppressWarnings("unchecked")
	public List<User> getAllUsersSorted() throws MiddlewareQueryException {
		try {
			final Query query = this.getSession().getNamedQuery(User.GET_ALL_USERS_SORTED);
			return query.list();
		} catch (final HibernateException e) {
			final String message = "Error with getAllUsersSorted query from User: " + e.getMessage();
			UserDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getUserIdsByCountryIds(final Collection<Integer> countryIds) throws MiddlewareQueryException {
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
	public List<User> getByIds(final List<Integer> ids) throws MiddlewareQueryException {
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

	@SuppressWarnings("unchecked")
	public List<UserDto> getAllUsersSortedByLastName() throws MiddlewareQueryException {
		try {
			final Criteria criteria = this.getSession().createCriteria(User.class);

			criteria.createAlias("person", "person");
			criteria.createAlias("roles", "roles");

			final ProjectionList projectionList = Projections.projectionList();

			projectionList.add(Projections.property("userid"), "userId");
			projectionList.add(Projections.property("name"), "username");
			projectionList.add(Projections.property("person.firstName"), "firstName");
			projectionList.add(Projections.property("person.lastName"), "lastName");
			projectionList.add(Projections.property("roles.role"), "role");
			projectionList.add(Projections.property("status"), "status");
			projectionList.add(Projections.property("person.email"), "email");

			criteria.setProjection(projectionList);

			criteria.addOrder(Order.asc("person.lastName"));

			criteria.setResultTransformer(Transformers.aliasToBean(UserDto.class));

			return criteria.list();
		} catch (final HibernateException e) {
			final String message = "Error with getAllUserDtosSorted() query from User: " + e.getMessage();
			UserDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public List<UserDto> getUsersAssociatedToStudy(final Integer studyId) throws MiddlewareQueryException {
		Preconditions.checkNotNull(studyId);
		final List<UserDto> users = new ArrayList<>();

		try {
			final Query query =
				this.getSession().createSQLQuery(User.GET_USERS_ASSOCIATED_TO_STUDY).addScalar("personId").addScalar("fName")
					.addScalar("lName").addScalar("email").addScalar("role").setParameter("studyId", studyId);
			final List<Object> results = query.list();
			this.mapUsers(users, results);
			return users;
		} catch (final MiddlewareQueryException e) {
			final String message = "Error with getUsersAssociatedToStudy() query from studyId: " + studyId;
			UserDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public List<UserDto> getUsersForEnvironment(final Integer instanceId) throws MiddlewareQueryException {
		Preconditions.checkNotNull(instanceId);
		final List<UserDto> users = new ArrayList<>();
		final StringBuilder sql = new StringBuilder().append("SELECT DISTINCT ")
				.append("    person.personid as personId, person.fname as fName, person.lname as lName, person.pemail as email , role.role as role  ")
				.append("FROM ").append("    cvterm scale ").append("        INNER JOIN ")
				.append("    cvterm_relationship r ON (r.object_id = scale.cvterm_id) ").append("        INNER JOIN ")
				.append("    cvterm variable ON (r.subject_id = variable.cvterm_id) ").append("        INNER JOIN ")
				.append("    nd_geolocationprop pp ON (pp.type_id = variable.cvterm_id) ")
				.append("        INNER JOIN workbench.persons person ").append("    ON (pp.value = person.personid) ")
				.append("    INNER JOIN workbench.users user on (user.personid = person.personid) ")
				.append("    left join workbench.users_roles role on (role.userid = user.userid) ").append("WHERE ")
				.append("    pp.nd_geolocation_id = :instanceDbId ").append("        AND r.object_id = 1901    ");
		try {
			final Query query = this.getSession().createSQLQuery(sql.toString()).addScalar("personId").addScalar("fName").addScalar("lName")
					.addScalar("email").addScalar("role").setParameter("instanceDbId", instanceId);
			final List<Object> results = query.list();
			this.mapUsers(users, results);
			return users;
		} catch (final MiddlewareQueryException e) {
			final String message = "Error with getUsersForEnvironment() query from instanceId: " + instanceId;
			UserDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	@SuppressWarnings({"unchecked"})
	public List<UserDto> getUsersByProjectUUId(final String projectUUID) {
		final List<UserDto> users = new ArrayList<>();
		try {
			if (projectUUID != null) {
				final SQLQuery query = this.getSession().createSQLQuery(User.GET_USERS_BY_PROJECT_UUID);
				query.setParameter("project_uuid", projectUUID);

				final List<Object> results = query.list();
				for (final Object o : results) {
					final Object[] user = (Object[]) o;
					final Integer userId = (Integer) user[0];
					final String username = (String) user[1];
					final String firstName = (String) user[2];
					final String lastName = (String) user[3];
					final String role = (String) user[4];
					final Integer status = (Integer) user[5];
					final String email = (String) user[6];
					final UserDto u = new UserDto(userId, username, firstName, lastName, role, status, email);
					users.add(u);
				}
			}
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in getUsersByProjectUUId(project_uuid=" + projectUUID + ")", e);
		}
		return users;
	}

	private void mapUsers(final List<UserDto> users, final List<Object> results) {
		for (final Object obj : results) {
			final Object[] row = (Object[]) obj;
			final UserDto user = new UserDto();
			user.setUserId((Integer) row[0]);
			user.setFirstName((String) row[1]);
			user.setLastName((String) row[2]);
			user.setEmail((String) row[3]);
			if (row[4] instanceof String && !StringUtils.isBlank((String) row[4])) {
				user.setRole((String) row[4]);
			}
			users.add(user);
		}
	}

	public UserDto mapUserToUserDto(final User user) {
		final UserDto userDto = new UserDto();
		userDto.setUserId(user.getUserid());
		userDto.setFirstName(user.getPerson().getFirstName());
		userDto.setLastName(user.getPerson().getLastName());
		userDto.setEmail(user.getPerson().getEmail());
		userDto.setRole(user.getRoles().toString());
		return userDto;
	}

}
