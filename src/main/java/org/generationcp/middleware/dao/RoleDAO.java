package org.generationcp.middleware.dao;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.service.api.user.RoleSearchDto;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RoleDAO extends GenericDAO<Role, Integer> {
	
	private static final Logger LOG = LoggerFactory.getLogger(RoleDAO.class);
	
	public List<Role> getAssignableRoles() {
		List<Role> toReturn;
		
		try {
			final Criteria criteria = this.getSession().createCriteria(Role.class);
			criteria.add(Restrictions.ne("name", Role.SUPERADMIN));
			toReturn = criteria.list();
			
		} catch (final HibernateException e) {
			final String message = "Error with getAssignableRoles query from RoleDAO: " + e.getMessage();
			RoleDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
		return toReturn;
	}

	public List<Role> getRoles(final RoleSearchDto roleSearchDto) {
		List<Role> toReturn;

		try {
			final Criteria criteria = this.getSession().createCriteria(Role.class);
			if (roleSearchDto != null) {
				if (roleSearchDto.getAssignable() != null) {
					criteria.add(Restrictions.eq("assignable", Boolean.FALSE));
				}
			}
			toReturn = criteria.list();

		} catch (final HibernateException e) {
			final String message = "Error with getRoles query from RoleDAO: " + e.getMessage();
			RoleDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
		return toReturn;

	}

}
