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
import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.WorkflowTemplate;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link Role}.
 *
 */
public class RoleDAO extends GenericDAO<Role, Integer> {

	@Override
	public Role getById(Integer id) throws MiddlewareQueryException {
		return super.getById(id, false);
	}

	@SuppressWarnings("unchecked")
	public Role getByNameAndWorkflowTemplate(String name, WorkflowTemplate workflowTemplate) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(Role.class);
			criteria.add(Restrictions.eq("name", name));
			criteria.add(Restrictions.eq("workflowTemplate", workflowTemplate));
			List<Role> roles = criteria.list();
			return !roles.isEmpty() ? roles.get(0) : null;
		} catch (HibernateException e) {
			this.logAndThrowException("Error in getByNameAndWorkflowTemplate(name=" + name + ", workflowTemplate=" + workflowTemplate
					+ ") query from Role: " + e.getMessage(), e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<Role> getByWorkflowTemplate(WorkflowTemplate workflowTemplate) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(Role.class);
			criteria.add(Restrictions.eq("workflowTemplate", workflowTemplate));
			return criteria.list();
		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error in getByWorkflowTemplate(workflowTemplate=" + workflowTemplate + ") query from Role: " + e.getMessage(), e);
		}
		return new ArrayList<Role>();
	}

	@SuppressWarnings("unchecked")
	public List<Role> getAllRolesDesc() throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(Role.class);
			criteria.addOrder(Order.desc("roleId"));
			return criteria.list();
		} catch (HibernateException e) {
			this.logAndThrowException("Error in getAllRolesSorted() query from Role: " + e.getMessage(), e);
		}
		return new ArrayList<Role>();
	}

	@SuppressWarnings("unchecked")
	public List<Role> getAllRolesOrderedByLabel() throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(Role.class);
			criteria.addOrder(Order.asc("labelOrder"));
			return criteria.list();
		} catch (HibernateException e) {
			this.logAndThrowException("Error in getAllRolesSorted() query from Role: " + e.getMessage(), e);
		}
		return new ArrayList<Role>();
	}

}
