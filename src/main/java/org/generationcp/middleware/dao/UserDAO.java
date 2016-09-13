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
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link User}.
 *
 */
public class UserDAO extends GenericDAO<User, Integer> {

	public User getByUsernameAndPassword(String username, String password) throws MiddlewareQueryException {
		try {
			if (username != null && password != null) {
				Criteria criteria =
						this.getSession().createCriteria(User.class).add(Restrictions.eq("name", username))
								.add(Restrictions.eq("password", password));

				@SuppressWarnings("unchecked")
				List<User> users = criteria.list();
				return !users.isEmpty() ? users.get(0) : null;
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getByUsernameAndPassword(username=" + username + ") query from User: " + e.getMessage(),
					e);
		}
		return null;
	}

	public boolean isPasswordSameAsUserName(String username) throws MiddlewareQueryException {
		try {
			if (username != null) {
				Criteria criteria =
						this.getSession().createCriteria(User.class).add(Restrictions.eq("name", username))
								.add(Restrictions.eq("password", username));

				@SuppressWarnings("unchecked")
				List<User> users = criteria.list();
				return !users.isEmpty() ? true : false;
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getByUsernameAndPassword(username=" + username + ") query from User: " + e.getMessage(),
					e);
		}
		return false;
	}

	public boolean changePassword(String userName, String password) throws MiddlewareQueryException {
		try {
			// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out of synch with
			// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
			// statement
			this.getSession().flush();
			
			if (userName != null && password != null) {
				String queryString = "UPDATE users SET upswd = :password WHERE uname LIKE :username";
				Session s = this.getSession();
				Query q = s.createSQLQuery(queryString);
				q.setString("username", userName);
				q.setString("password", password);
				int success = q.executeUpdate();

				return success > 0;
			}
		} catch (Exception e) {
			this.logAndThrowException(e.getMessage(), e);
		}
		return false;
	}

	public User getUserDetailsByUsername(String username) throws MiddlewareQueryException {
		try {
			if (username != null) {
				Criteria criteria = this.getSession().createCriteria(User.class).add(Restrictions.eq("name", username));
				@SuppressWarnings("unchecked")
				List<User> users = criteria.list();
				return !users.isEmpty() ? users.get(0) : null;
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getByUsernameAndPassword(username=" + username + ") query from User: " + e.getMessage(),
					e);
		}
		return null;
	}

	public boolean isUsernameExists(String userName) throws MiddlewareQueryException {
		try {
			if (userName != null) {
				Criteria criteria = this.getSession().createCriteria(User.class);
				criteria.add(Restrictions.eq("name", userName));

				// used a List in case of dirty data
				@SuppressWarnings("unchecked")
				List<User> users = criteria.list();

				return !users.isEmpty();
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with isUsernameExists(username=" + userName + ") query from User: " + e.getMessage(), e);
		}
		return false;
	}

	public User getUserByUserName(String userName) throws MiddlewareQueryException {
		try {
			if (userName != null) {
				Criteria criteria = this.getSession().createCriteria(User.class);
				criteria.add(Restrictions.eq("name", userName));

				// used a List in case of dirty data
				@SuppressWarnings("unchecked")
				List<User> users = criteria.list();

				return users.isEmpty() ? null : users.get(0);
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with isUsernameExists(username=" + userName + ") query from User: " + e.getMessage(), e);
		}
		return null;
	}

	public List<User> getUsersByUserNames(List<String> userNames) throws MiddlewareQueryException {
		try {
			if (userNames != null && !userNames.isEmpty()) {
				Criteria criteria = this.getSession().createCriteria(User.class);
				criteria.add(Restrictions.in("name", userNames));

				// used a List in case of dirty data
				@SuppressWarnings("unchecked")
				List<User> users = criteria.list();

				return users;
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getUsersByUserName(usernames) query from User: " + e.getMessage(), e);
		}
		return new ArrayList<>();
	}

	@SuppressWarnings("unchecked")
	public List<User> getByNameUsingEqual(String name, int start, int numOfRows) throws MiddlewareQueryException {
		try {
			if (name != null) {
				Query query = this.getSession().getNamedQuery(User.GET_BY_NAME_USING_EQUAL);
				query.setParameter("name", name);
				query.setFirstResult(start);
				query.setMaxResults(numOfRows);
				return query.list();
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getByNameUsingEqual(name=" + name + ") query from User: " + e.getMessage(), e);
		}
		return new ArrayList<User>();
	}

	@SuppressWarnings("unchecked")
	public List<User> getByNameUsingLike(String name, int start, int numOfRows) throws MiddlewareQueryException {
		try {
			if (name != null) {
				Query query = this.getSession().getNamedQuery(User.GET_BY_NAME_USING_LIKE);
				query.setParameter("name", name);
				query.setFirstResult(start);
				query.setMaxResults(numOfRows);
				return query.list();
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getByNameUsingLike(name=" + name + ") query from User: " + e.getMessage(), e);
		}
		return new ArrayList<User>();
	}

	@SuppressWarnings("unchecked")
	public List<User> getAllUsersSorted() throws MiddlewareQueryException {
		try {
			Query query = this.getSession().getNamedQuery(User.GET_ALL_USERS_SORTED);
			return query.list();
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getAllUsersSorted() query from User: " + e.getMessage(), e);
		}
		return new ArrayList<User>();
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getUserIdsByCountryIds(Collection<Integer> countryIds) throws MiddlewareQueryException {
		try {
			if (countryIds != null && !countryIds.isEmpty()) {
				Criteria criteria = this.getSession().createCriteria(Locdes.class);
				criteria.createAlias("location", "l");
				criteria.add(Restrictions.in("l.cntryid", countryIds));
				criteria.setProjection(Projections.distinct(Projections.property("user.userid")));
				return criteria.list();
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getUserIdsByCountryIds() query from User: " + e.getMessage(), e);
		}
		return new ArrayList<Integer>();
	}

	@SuppressWarnings("unchecked")
	public List<User> getByIds(List<Integer> ids) throws MiddlewareQueryException {
		List<User> toReturn = new ArrayList<User>();

		if (ids == null || ids.isEmpty()) {
			return toReturn;
		}

		try {
			Criteria criteria = this.getSession().createCriteria(User.class);
			criteria.add(Restrictions.in("userid", ids));
			toReturn = criteria.list();
		} catch (HibernateException e) {
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

	public List<User> getAllUsersByRole(String role)  {
		try {

			final StringBuilder sb = new StringBuilder();
			sb.append("SELECT DISTINCT {u.*} FROM users u ").append("INNER JOIN users_roles r ON r.userid = u.userid ")
					.append("WHERE r.role = :role");

			final SQLQuery query = this.getSession().createSQLQuery(sb.toString());
			query.setParameter("role", role);
			query.addEntity("u", User.class);
			return query.list();

		} catch (final HibernateException e) {
			this.logAndThrowException("Error with getAllUsersByRole(" + role + ") query from UserDAO " + e.getMessage(), e);
		}
		return new ArrayList<User>();
	}



}
