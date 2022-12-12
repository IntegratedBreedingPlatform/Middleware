package org.generationcp.middleware.dao;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.dao.workbench.ProgramEligibleUsersQuery;
import org.generationcp.middleware.dao.workbench.ProgramEligibleUsersSearchRequest;
import org.generationcp.middleware.dao.workbench.ProgramMembersQuery;
import org.generationcp.middleware.dao.workbench.ProgramMembersSearchRequest;
import org.generationcp.middleware.domain.workbench.ProgramMemberDto;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.user.RoleDto;
import org.generationcp.middleware.service.api.user.RoleTypeDto;
import org.generationcp.middleware.service.api.user.UserDto;
import org.generationcp.middleware.util.SQLQueryBuilder;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Transactional
public class WorkbenchUserDAO extends GenericDAO<WorkbenchUser, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(WorkbenchUserDAO.class);

	public boolean isUsernameExists(final String username) {
		try {
			if (username != null) {
				final Criteria criteria = this.getSession().createCriteria(WorkbenchUser.class);
				criteria.add(Restrictions.eq("name", username));

				// used a List in case of dirty data
				@SuppressWarnings("unchecked") final List<WorkbenchUser> users = criteria.list();

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

	@SuppressWarnings("unchecked")
	public List<UserDto> getAllUsersSortedByLastName() {

		try {
			final Criteria criteria = this.getSession().createCriteria(WorkbenchUser.class);

			criteria.createAlias("person", "person", CriteriaSpecification.LEFT_JOIN);
			criteria.createAlias("person.crops", "crops", CriteriaSpecification.LEFT_JOIN);
			criteria.addOrder(Order.asc("person.lastName"));
			criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

			final List<WorkbenchUser> workbenchUsers = criteria.list();

			final List<UserDto> users = new ArrayList<>();
			if (workbenchUsers != null) {
				for (final WorkbenchUser workbenchUser : workbenchUsers) {
					users.add(new UserDto(workbenchUser));
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
				@SuppressWarnings("unchecked") final List<WorkbenchUser> users = criteria.list();

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

	public List<WorkbenchUser> getUsers(final List<Integer> userIds) {
		try {
			final Criteria criteria = this.getSession().createCriteria(WorkbenchUser.class);
			criteria.add(Restrictions.in("userid", userIds));
			return criteria.list();
		} catch (final HibernateException e) {
			final String message = "Error with getUsers(userIds=" + userIds + ") query from User: " + e.getMessage();
			WorkbenchUserDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public Map<Integer, String> getUserIDFullNameMap(final List<Integer> userIds) {
		final Map<Integer, String> idNamesMap = new HashMap<>();
		try {
			final Criteria criteria = this.getSession().createCriteria(WorkbenchUser.class, "user");
			criteria.createAlias("person", "person");
			final ProjectionList projectionList = Projections.projectionList();
			projectionList.add(Projections.property("userid"));
			projectionList.add(Projections.property("person.firstName"));
			projectionList.add(Projections.property("person.lastName"));
			criteria.setProjection(projectionList);
			criteria.add(Restrictions.in("userid", userIds));

			final List<Object[]> results = criteria.list();
			for (final Object[] row : results) {
				idNamesMap.put((Integer) row[0], row[1] + " " + row[2]);
			}
		} catch (final HibernateException e) {
			final String message =
				"Error with getUserIDFullNameMap(userIds= " + userIds + ") query from WorkbenchUserDAO: " + e.getMessage();
			WorkbenchUserDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
		return idNamesMap;
	}

	public Map<Integer, String> getAllUserIDFullNameMap() {
		final Map<Integer, String> idNamesMap = new HashMap<>();
		try {
			final Criteria criteria = this.getSession().createCriteria(WorkbenchUser.class, "user");
			criteria.createAlias("person", "person");
			final ProjectionList projectionList = Projections.projectionList();
			projectionList.add(Projections.property("userid"));
			projectionList.add(Projections.property("person.firstName"));
			projectionList.add(Projections.property("person.lastName"));
			criteria.setProjection(projectionList);

			final List<Object[]> results = criteria.list();
			for (final Object[] row : results) {
				idNamesMap.put((Integer) row[0], row[1] + " " + row[2]);
			}
		} catch (final HibernateException e) {
			final String message = "Error with getAllUserIDFullNameMap() query from WorkbenchUserDAO: " + e.getMessage();
			WorkbenchUserDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
		return idNamesMap;
	}

	public List<WorkbenchUser> getUsersByPersonIds(final List<Integer> personIds) {

		try {
			final Criteria criteria = this.getSession().createCriteria(WorkbenchUser.class, "user");
			criteria.createAlias("person", "person");
			criteria.add(Restrictions.in("person.id", personIds));
			return criteria.list();
		} catch (final HibernateException e) {
			final String message =
				"Error with getUsersByPersonIds(personIds= " + personIds + ") query from WorkbenchUserDAO: " + e.getMessage();
			WorkbenchUserDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}

	}

	public Optional<WorkbenchUser> getUserByFullName(final String fullname) {
		try {
			final Query query = this.getSession().getNamedQuery(WorkbenchUser.GET_BY_FULLNAME);
			query.setParameter("fullname", fullname);
			final WorkbenchUser user = (WorkbenchUser) query.uniqueResult();
			return (user != null) ? Optional.of(user) : Optional.empty();
		} catch (final HibernateException e) {
			final String message = "Error with getUserByFullName query from User: " + e.getMessage();
			WorkbenchUserDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}

	}

	public Long countUsersByFullName(final String fullname) {
		try {
			final Query query = this.getSession().getNamedQuery(WorkbenchUser.COUNT_BY_FULLNAME);
			query.setParameter("fullname", fullname);
			return (Long) query.uniqueResult();
		} catch (final HibernateException e) {
			final String message = "Error with getUserByFullName query from User: " + e.getMessage();
			WorkbenchUserDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}

	}

	@SuppressWarnings("unchecked")
	public List<WorkbenchUser> getUsersByProjectId(final Long projectId) {
		final List<WorkbenchUser> users = new ArrayList<>();
		try {
			if (projectId != null) {
				final SQLQuery query = this.getSession().createSQLQuery(WorkbenchUser.GET_USERS_BY_PROJECT_ID);
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
					final String fName = (String) user[10];
					final String lName = (String) user[11];
					final Person person = new Person();
					person.setId(personId);
					person.setFirstName(fName);
					person.setLastName(lName);
					final WorkbenchUser u =
						new WorkbenchUser(userId, instalId, uStatus, uAccess, uType, uName, upswd, person, aDate, cDate);
					users.add(u);
				}
			}
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in getUsersByProjectId(projectId=" + projectId + ") query from WorkbenchUserDAO: "
				+ e.getMessage(), e);
		}
		return users;
	}

	public long countAllProgramEligibleUsers(final String programUUID, final ProgramEligibleUsersSearchRequest searchRequest) {
		final SQLQueryBuilder queryBuilder = ProgramEligibleUsersQuery.getCountQuery(searchRequest);
		final SQLQuery query = this.getSession().createSQLQuery(queryBuilder.build());
		query.setParameter("programUUID", programUUID);
		queryBuilder.addParamsToQuery(query);
		return ((BigInteger) query.uniqueResult()).longValue();
	}

	public List<UserDto> getAllProgramEligibleUsers(
		final String programUUID, final ProgramEligibleUsersSearchRequest searchRequest,
		final Pageable pageable) {
		try {
			final SQLQueryBuilder queryBuilder = ProgramEligibleUsersQuery.getSelectQuery(pageable, searchRequest);
			final SQLQuery query = this.getSession().createSQLQuery(queryBuilder.build());
			query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			query.addScalar(ProgramEligibleUsersQuery.USER_ID);
			query.addScalar(ProgramEligibleUsersQuery.USERNAME);
			query.addScalar(ProgramEligibleUsersQuery.FIRST_NAME);
			query.addScalar(ProgramEligibleUsersQuery.LAST_NAME);
			query.addScalar(ProgramEligibleUsersQuery.EMAIL);

			queryBuilder.setParameter("programUUID", programUUID);
			queryBuilder.addParamsToQuery(query);

			addPaginationToSQLQuery(query, pageable);
			final List<Map<String, Object>> queryResults = (List<Map<String, Object>>) query.list();

			final List<UserDto> users = new ArrayList<>();
			for (final Map<String, Object> item : queryResults) {
				users.add(new UserDto((Integer) item.get(ProgramEligibleUsersQuery.USER_ID),
					(String) item.get(ProgramEligibleUsersQuery.USERNAME),
					(String) item.get(ProgramEligibleUsersQuery.FIRST_NAME), (String) item.get(ProgramEligibleUsersQuery.LAST_NAME), null,
					"true", (String) item.get(ProgramEligibleUsersQuery.EMAIL)));
			}
			return users;

		} catch (final HibernateException e) {
			final String message = "Error with getAllProgramEligibleUsers query from programUUID: " + e.getMessage();
			WorkbenchUserDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public List<ProgramMemberDto> getProgramMembers(
		final String programUUID, final ProgramMembersSearchRequest searchRequest,
		final Pageable pageable) {
		try {
			final SQLQueryBuilder queryBuilder = ProgramMembersQuery.getSelectQuery(pageable, searchRequest);
			final SQLQuery query = this.getSession().createSQLQuery(queryBuilder.build());
			query.setParameter("programUUID", programUUID);
			query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			query.addScalar(ProgramMembersQuery.USER_ID);
			query.addScalar(ProgramMembersQuery.USERNAME);
			query.addScalar(ProgramMembersQuery.FIRST_NAME);
			query.addScalar(ProgramMembersQuery.LAST_NAME);
			query.addScalar(ProgramMembersQuery.EMAIL);
			query.addScalar(ProgramMembersQuery.ROLE_ID);
			query.addScalar(ProgramMembersQuery.ROLE_NAME);
			query.addScalar(ProgramMembersQuery.ROLE_DESCRIPTION);
			query.addScalar(ProgramMembersQuery.ROLE_TYPE_ID);
			query.addScalar(ProgramMembersQuery.ROLE_TYPE_NAME);
			query.addScalar(ProgramMembersQuery.ROLE_ACTIVE);

			queryBuilder.addParamsToQuery(query);
			GenericDAO.addPaginationToSQLQuery(query, pageable);

			final List<Map<String, Object>> queryResults = (List<Map<String, Object>>) query.list();
			final List<ProgramMemberDto> members = new ArrayList<>();
			for (final Map<String, Object> item : queryResults) {
				final RoleDto roleDto = new RoleDto((Integer) item
					.get(ProgramMembersQuery.ROLE_ID), (String) item.get(ProgramMembersQuery.ROLE_NAME),
					(String) item.get(ProgramMembersQuery.ROLE_DESCRIPTION), new RoleTypeDto((Integer) item.get(ProgramMembersQuery.ROLE_TYPE_ID), (String) item.get(ProgramMembersQuery.ROLE_TYPE_NAME)),
					(Boolean) item.get(ProgramMembersQuery.ROLE_ACTIVE), null, null);
				final ProgramMemberDto programMemberDto =
					new ProgramMemberDto((Integer) item.get(ProgramMembersQuery.USER_ID), (String) item.get(ProgramMembersQuery.USERNAME),
						(String) item.get(ProgramMembersQuery.FIRST_NAME),
						(String) item.get(ProgramMembersQuery.LAST_NAME), (String) item.get(ProgramMembersQuery.EMAIL), roleDto);
				members.add(programMemberDto);
			}
			return members;
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				"Error in getProgramMembers(programUUID=" + programUUID + ") query from WorkbenchUserDAO: "
					+ e.getMessage(), e);
		}
	}

	public long countAllProgramMembers(final String programUUID, final ProgramMembersSearchRequest searchRequest) {
		final SQLQueryBuilder queryBuilder = ProgramMembersQuery.getCountQuery(searchRequest);
		final SQLQuery query = this.getSession().createSQLQuery(queryBuilder.build());
		queryBuilder.setParameter("programUUID", programUUID);
		queryBuilder.addParamsToQuery(query);
		return ((BigInteger) query.uniqueResult()).longValue();
	}

	public long countAllActiveUsers() {
		final Criteria criteria = this.getSession().createCriteria(WorkbenchUser.class);
		criteria.setProjection(Projections.rowCount());
		criteria.add(Restrictions.eq("status", 0));
		criteria.setMaxResults(Integer.MAX_VALUE);
		return (long) criteria.uniqueResult();
	}

}
