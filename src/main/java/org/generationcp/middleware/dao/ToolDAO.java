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
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolType;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

/**
 * DAO class for {@link Tool}.
 *
 */
@Transactional
public class ToolDAO extends GenericDAO<Tool, Long> {

	public Tool getByToolName(String toolName) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(Tool.class).add(Restrictions.eq("toolName", toolName)).setMaxResults(1);
			return (Tool) criteria.uniqueResult();
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getByToolName(toolName=" + toolName + ") query from Tool: " + e.getMessage(), e);
		}
		return null;
	}

	public Tool getByToolId(Long toolId) throws MiddlewareQueryException {
		try {
			if (toolId != null) {
				Criteria criteria = this.getSession().createCriteria(Tool.class).add(Restrictions.eq("toolId", toolId)).setMaxResults(1);
				return (Tool) criteria.uniqueResult();
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error withgetByToolId(toolId=" + toolId + ") query from Tool: " + e.getMessage(), e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<Tool> getUserTools() throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(Tool.class).add(Restrictions.eq("userTool", true));

			return criteria.list();
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getUserTools() query from Tool: " + e.getMessage(), e);
		}
		return new ArrayList<Tool>();
	}

	@Override
	public Tool save(Tool entity) throws MiddlewareQueryException {



		try {
			Tool out = super.save(entity);

			return out;
		} catch (MiddlewareQueryException e) {

			throw new MiddlewareQueryException(e.getMessage(), e);
		}
	}

	@Override
	public Tool update(Tool entity) throws MiddlewareQueryException {



		try {
			Tool out = super.update(entity);

			return out;
		} catch (MiddlewareQueryException e) {

			throw new MiddlewareQueryException(e.getMessage(), e);
		}
	}
}
