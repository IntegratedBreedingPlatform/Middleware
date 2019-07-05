package org.generationcp.middleware.dao;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.workbench.UserRole;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
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


}
