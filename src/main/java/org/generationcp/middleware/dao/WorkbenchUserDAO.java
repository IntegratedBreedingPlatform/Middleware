package org.generationcp.middleware.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
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

public class WorkbenchUserDAO extends GenericDAO<WorkbenchUser, Integer> {
	
	private static final Logger LOG = LoggerFactory.getLogger(WorkbenchUserDAO.class);
	
	public boolean isUsernameExists(final String username) {
		try {
			if (username != null) {
				final Criteria criteria = this.getSession().createCriteria(WorkbenchUser.class);
				criteria.add(Restrictions.eq("name", username));

				// used a List in case of dirty data
				@SuppressWarnings("unchecked")
				final List<WorkbenchUser> users = criteria.list();

				return !users.isEmpty();
			}
		} catch (final HibernateException e) {
			final String message = "Error with isUsernameExists(username=" + username + ") query from User: " + e.getMessage();
			WorkbenchUserDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public List<WorkbenchUser> getAllActiveUsersSorted() {
		try {
			final Query query = this.getSession().getNamedQuery(WorkbenchUser.GET_ALL_ACTIVE_USERS_SORTED);
			return query.list();
		} catch (final HibernateException e) {
			final String message = "Error with getAllUsersSorted query from User: " + e.getMessage();
			WorkbenchUserDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<WorkbenchUser> getByNameUsingEqual(final String name, final int start, final int numOfRows) {
		try {
			if (name != null) {
				final Query query = this.getSession().getNamedQuery(WorkbenchUser.GET_BY_NAME_USING_EQUAL);
				query.setParameter("name", name);
				query.setFirstResult(start);
				query.setMaxResults(numOfRows);
				return query.list();
			}
		} catch (final HibernateException e) {
			final String message = "Error with getByNameUsingEqual(name=" + name + ") query from User: " + e.getMessage();
			WorkbenchUserDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
		return new ArrayList<>();
	}

	@SuppressWarnings("unchecked")
	public List<WorkbenchUser> getByNameUsingLike(final String name, final int start, final int numOfRows) {
		try {
			if (name != null) {
				final Query query = this.getSession().getNamedQuery(WorkbenchUser.GET_BY_NAME_USING_LIKE);
				query.setParameter("name", name);
				query.setFirstResult(start);
				query.setMaxResults(numOfRows);
				return query.list();
			}
		} catch (final HibernateException e) {
			final String message = "Error with getByNameUsingLike(name=" + name + ") query from User: " + e.getMessage();
			WorkbenchUserDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
		return new ArrayList<>();
	}
	
	@SuppressWarnings({"unchecked"})
	public List<UserDto> getUsersByProjectUUId(final String projectUUID) {
		final List<UserDto> users = new ArrayList<>();
		try {
			if (projectUUID != null) {
				final SQLQuery query = this.getSession().createSQLQuery(WorkbenchUser.GET_USERS_BY_PROJECT_UUID);
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
	
	@SuppressWarnings("unchecked")
	public List<UserDto> getAllUsersSortedByLastName() {
		try {
			final Criteria criteria = this.getSession().createCriteria(WorkbenchUser.class);

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
			WorkbenchUserDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}
	
	public WorkbenchUser getUserByUserName(final String username) {
		try {
			if (username != null) {
				final Criteria criteria = this.getSession().createCriteria(WorkbenchUser.class);
				criteria.add(Restrictions.eq("name", username));

				// used a List in case of dirty data
				@SuppressWarnings("unchecked")
				final List<WorkbenchUser> users = criteria.list();

				return users.isEmpty() ? null : users.get(0);
			}
		} catch (final HibernateException e) {
			final String message = "Error with getUserByUserName(username=" + username + ") query from User: " + e.getMessage();
			WorkbenchUserDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
		return null;
	}
	
	public boolean changePassword(final String username, final String password) {
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
			WorkbenchUserDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
		return false;
	}
	
}
