package org.generationcp.middleware.dao;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.workbench.UserRole;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UserRoleDao extends GenericDAO<UserRole, Long> {

	private static final Logger LOG = LoggerFactory.getLogger(UserRole.class);

	public List<UserRole> getByProgramId(final Long programId) {
		List<UserRole> toReturn;

		try {
			final Criteria criteria = this.getSession().createCriteria(UserRole.class);
			if (programId != null) {
				criteria.createAlias("workbenchProject", "workbenchProject");
				criteria.add(Restrictions.eq("workbenchProject.projectId", programId));
			}
			toReturn = criteria.list();

		} catch (final HibernateException e) {
			final String message = "Error with getByProgramId query from UserRoleDao: " + e.getMessage();
			UserRoleDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
		return toReturn;
	}

	public List<WorkbenchUser> getUsersByRoleId(final Integer roleId) {
		final List<WorkbenchUser> toReturn;
		try {
			final Criteria criteria = this.getSession().createCriteria(UserRole.class);
			criteria.add(Restrictions.eq("role.id", roleId));
			criteria.setProjection(Projections.property("user"));
			toReturn = criteria.list();
		} catch (final HibernateException e) {
			final String message = "Error with getUsersByRoleId query from UserRoleDao: " + e.getMessage();
			UserRoleDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
		return toReturn;
	}

	public void delete(final UserRole userRole) {

		try {
			makeTransient(userRole);

		} catch (final Exception e) {
			final String message = "Cannot delete UserRole (UserRole=" + userRole
				+ "): " + e.getMessage();
			UserRoleDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public void removeUsersFromProgram(final List<Integer> workbenchUserIds, final Long projectId) {
		// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out
		// of synch with
		// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
		// statement
		this.getSession().flush();
		final String sql = "DELETE ur FROM users_roles ur "
			+ " WHERE ur.workbench_project_id = :projectId AND ur.userid in (:workbenchUserIds)";
		final SQLQuery statement = this.getSession().createSQLQuery(sql);
		statement.setParameter("projectId", projectId);
		statement.setParameterList("workbenchUserIds", workbenchUserIds);
		statement.executeUpdate();
	}
}
