package org.generationcp.middleware.dao;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.workbench.Role;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoleDAO extends GenericDAO<Role, Integer> {
	
	protected static final String SUPERADMIN = "SUPERADMIN";
	private static final Logger LOG = LoggerFactory.getLogger(RoleDAO.class);
	
	@SuppressWarnings("unchecked")
	public List<Role> getAssignableRoles() {
		List<Role> toReturn = new ArrayList<>();
		
		try {
			final Criteria criteria = this.getSession().createCriteria(Role.class);
			criteria.add(Restrictions.ne("name", SUPERADMIN));
			toReturn = criteria.list();
			
		} catch (final HibernateException e) {
			final String message = "Error with getAssignableRoles query from RoleDAO: " + e.getMessage();
			RoleDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
		return toReturn;
	}

}
