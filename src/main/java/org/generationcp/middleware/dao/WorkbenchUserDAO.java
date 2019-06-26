package org.generationcp.middleware.dao;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.domain.workbench.CropDto;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.user.UserDto;
import org.generationcp.middleware.service.api.user.UserRoleMapper;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

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
	public List<WorkbenchUser> getUsersByCrop(final String cropName) {
		try {
			final Query query = this.getSession().createQuery("SELECT u FROM WorkbenchUser u "
				+ " INNER JOIN FETCH u.person p "
				+ " INNER JOIN FETCH u.crops c "
				+ " WHERE u.status = 0 "
				+ " AND EXISTS(FROM WorkbenchUser wu INNER JOIN wu.crops ct WHERE ct.cropName = :cropName AND wu.userid = u.userid)"
				+ " ORDER BY p.firstName, p.lastName");
			query.setParameter("cropName", cropName);
			query.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
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
	public List<UserDto> getUsersByProjectUUId(final String projectUUID, final String cropName) {
		final List<UserDto> users = new ArrayList<>();
		try {
			if (projectUUID != null) {
				//TODO fix tests

				final SQLQuery query = this.getSession().createSQLQuery(WorkbenchUser.GET_USERS_BY_PROJECT_UUID);
				query.setParameter("project_uuid", projectUUID);
				query.setParameter("cropName", cropName);

				final List<Object> results = query.list();
				for (final Object o : results) {
					final Object[] user = (Object[]) o;
					final Integer userId = (Integer) user[0];
					final String username = (String) user[1];
					final String firstName = (String) user[2];
					final String lastName = (String) user[3];
					final Integer status = (Integer) user[4];
					final String email = (String) user[5];
					//TODO load list of userRoles if needed
					final UserDto u = new UserDto(userId, username, firstName, lastName, null, status, email);
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
			criteria.createAlias("crops", "crops", CriteriaSpecification.LEFT_JOIN);
			criteria.addOrder(Order.asc("person.lastName"));
			criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

			final List<WorkbenchUser> workbenchUsers = criteria.list();

			final List<UserDto> users = new ArrayList<>();
			if (workbenchUsers != null) {
				for (final WorkbenchUser workbenchUser : workbenchUsers) {
					final UserDto user = new UserDto();
					if (workbenchUser.getRoles() != null && !workbenchUser.getRoles().isEmpty()) {
						user.setUserRoles(UserRoleMapper.map(workbenchUser.getRoles()));
					}
					user.setUserId(workbenchUser.getUserid());
					if (workbenchUser.getPerson() != null) {
						user.setEmail(workbenchUser.getPerson().getEmail());
						user.setFirstName(workbenchUser.getPerson().getFirstName());
						user.setLastName(workbenchUser.getPerson().getLastName());
					}
					user.setStatus(workbenchUser.getStatus());
					user.setUsername(workbenchUser.getName());
					if (workbenchUser.getCrops() != null) {
						final List<CropDto> crops = new ArrayList<>();
						for (final CropType cropType : workbenchUser.getCrops()) {
							final CropDto crop = new CropDto();
							crop.setCropName(cropType.getCropName());
							crops.add(crop);
						}
						user.setCrops(crops);
					}
					users.add(user);
				}
			}
			return users;
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
	
	@SuppressWarnings("unchecked")
	public List<WorkbenchUser> getSuperAdminUsers() {
		try {
			final Criteria criteria = this.getSession().createCriteria(WorkbenchUser.class);
			criteria.createAlias("roles", "roles");
			criteria.createAlias("roles.role", "role");
			criteria.add(Restrictions.eq("role.name", Role.SUPERADMIN));
			return criteria.list();
			
		} catch (final HibernateException e) {
			final String message = "Error with getSuperAdminUsers query from WorkbenchUserDAO: " + e.getMessage();
			WorkbenchUserDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public boolean isSuperAdminUser(final Integer userId) {
		try {
			if (userId != null) {
				final Criteria criteria = this.getSession().createCriteria(WorkbenchUser.class);
				criteria.createAlias("roles", "roles");
				criteria.createAlias("roles.role", "role");
				criteria.add(Restrictions.eq("role.name", Role.SUPERADMIN));
				criteria.add(Restrictions.eq("userid", userId));
				
				final List<WorkbenchUser> users = criteria.list();
				return !users.isEmpty();
			}
		} catch (final HibernateException e) {
			final String message = "Error with isSuperAdminUser(userid=" + userId + ") query from User: " + e.getMessage();
			WorkbenchUserDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public List<WorkbenchUser> getUsersByProjectId(final Long projectId, final String cropName) {
		final List<WorkbenchUser> users = new ArrayList<>();
		try {
			if (projectId != null) {
				final SQLQuery query = this.getSession().createSQLQuery(WorkbenchUser.GET_USERS_BY_PROJECT_ID);
				query.setParameter("projectId", projectId);
				query.setParameter("cropName", cropName);
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
				final SQLQuery query = this.getSession().createSQLQuery(WorkbenchUser.GET_ACTIVE_USER_IDS_BY_PROJECT_ID);
				query.setParameter("projectId", projectId);
				return query.list();
			}
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in getActiveUserIDsByProjectId(projectId=" + projectId + ") query from ProjectUser: "
				+ e.getMessage(), e);
		}
		return userIDs;
	}
}
