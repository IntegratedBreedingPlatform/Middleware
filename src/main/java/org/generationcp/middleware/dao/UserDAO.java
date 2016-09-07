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
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

/**
 * DAO class for {@link User}.
 *
 */
public class UserDAO extends GenericDAO<User, Integer> {

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
			this.logAndThrowException("Error with getByUsernameAndPassword(username=" + username + ") query from User: " + e.getMessage(),
					e);
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
				return !users.isEmpty() ? true : false;
			}
		} catch (final HibernateException e) {
			this.logAndThrowException("Error with getByUsernameAndPassword(username=" + username + ") query from User: " + e.getMessage(),
					e);
		}
		return false;
	}

	public boolean changePassword(final String userName, final String password) throws MiddlewareQueryException {
		try {
			// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out
			// of synch with
			// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
			// statement
			this.getSession().flush();

			if (userName != null && password != null) {
				final String queryString = "UPDATE users SET upswd = :password WHERE uname LIKE :username";
				final Session s = this.getSession();
				final Query q = s.createSQLQuery(queryString);
				q.setString("username", userName);
				q.setString("password", password);
				final int success = q.executeUpdate();

				return success > 0;
			}
		} catch (final Exception e) {
			this.logAndThrowException(e.getMessage(), e);
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
			this.logAndThrowException("Error with getByUsernameAndPassword(username=" + username + ") query from User: " + e.getMessage(),
					e);
		}
		return null;
	}

	public boolean isUsernameExists(final String userName) throws MiddlewareQueryException {
		try {
			if (userName != null) {
				final Criteria criteria = this.getSession().createCriteria(User.class);
				criteria.add(Restrictions.eq("name", userName));

				// used a List in case of dirty data
				@SuppressWarnings("unchecked")
				final List<User> users = criteria.list();

				return !users.isEmpty();
			}
		} catch (final HibernateException e) {
			this.logAndThrowException("Error with isUsernameExists(username=" + userName + ") query from User: " + e.getMessage(), e);
		}
		return false;
	}

	public User getUserByUserName(final String userName) throws MiddlewareQueryException {
		try {
			if (userName != null) {
				final Criteria criteria = this.getSession().createCriteria(User.class);
				criteria.add(Restrictions.eq("name", userName));

				// used a List in case of dirty data
				@SuppressWarnings("unchecked")
				final List<User> users = criteria.list();

				return users.isEmpty() ? null : users.get(0);
			}
		} catch (final HibernateException e) {
			this.logAndThrowException("Error with isUsernameExists(username=" + userName + ") query from User: " + e.getMessage(), e);
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
			this.logAndThrowException("Error with getByNameUsingEqual(name=" + name + ") query from User: " + e.getMessage(), e);
		}
		return new ArrayList<User>();
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
			this.logAndThrowException("Error with getByNameUsingLike(name=" + name + ") query from User: " + e.getMessage(), e);
		}
		return new ArrayList<User>();
	}

	@SuppressWarnings("unchecked")
	public List<User> getAllUsersSorted() throws MiddlewareQueryException {
		try {
			final Query query = this.getSession().getNamedQuery(User.GET_ALL_USERS_SORTED);
			return query.list();
		} catch (final HibernateException e) {
			this.logAndThrowException("Error with getAllUsersSorted() query from User: " + e.getMessage(), e);
		}
		return new ArrayList<User>();
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
			this.logAndThrowException("Error with getUserIdsByCountryIds() query from User: " + e.getMessage(), e);
		}
		return new ArrayList<Integer>();
	}

	@SuppressWarnings("unchecked")
	public List<User> getByIds(final List<Integer> ids) throws MiddlewareQueryException {
		List<User> toReturn = new ArrayList<User>();

		if (ids == null || ids.isEmpty()) {
			return toReturn;
		}

		try {
			final Criteria criteria = this.getSession().createCriteria(User.class);
			criteria.add(Restrictions.in("userid", ids));
			toReturn = criteria.list();
		} catch (final HibernateException e) {
			this.logAndThrowException("Error with getByIds() query from User: " + e.getMessage(), e);
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
			this.logAndThrowException(String.format("Error with getPersonByFullName(fullname=[%s])", StringUtils.join(fullname, ",")), e);
		}
		return user;
	}

	@SuppressWarnings("unchecked")
	public List<UserDto> getAllUserDtosSorted() throws MiddlewareQueryException {
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
			this.logAndThrowException("Error with getAllUserDtosSorted() query from User: " + e.getMessage(), e);
		}
		return new ArrayList<UserDto>();
	}

}
