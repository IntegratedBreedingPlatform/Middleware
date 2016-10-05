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
import org.generationcp.middleware.pojos.workbench.ToolConfiguration;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link ToolConfiguration}.
 *
 * <b>Author</b>: Michael Blancaflor <br>
 * <b>File Created</b>: Aug 28, 2012
 */
public class ToolConfigurationDAO extends GenericDAO<ToolConfiguration, Long> {

	@SuppressWarnings("unchecked")
	public List<ToolConfiguration> getListOfToolConfigurationsByToolId(Long toolId) throws MiddlewareQueryException {
		try {
			if (toolId != null) {
				Criteria criteriaTool = this.getSession().createCriteria(Tool.class);
				criteriaTool.add(Restrictions.eq("toolId", toolId));
				Tool tool = (Tool) criteriaTool.uniqueResult();

				Criteria criteria = this.getSession().createCriteria(ToolConfiguration.class);
				criteria.add(Restrictions.eq("tool", tool));

				return criteria.list();
			}
		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error with getListOfToolConfigurationsByToolId(toolId=" + toolId + ") query from Tool: " + e.getMessage(), e);
		}
		return new ArrayList<ToolConfiguration>();
	}

	public ToolConfiguration getToolConfigurationByToolIdAndConfigKey(Long toolId, String configKey) throws MiddlewareQueryException {
		try {
			if (toolId != null) {
				Criteria criteriaTool = this.getSession().createCriteria(Tool.class);
				criteriaTool.add(Restrictions.eq("toolId", toolId));
				Tool tool = (Tool) criteriaTool.uniqueResult();

				Criteria criteria = this.getSession().createCriteria(ToolConfiguration.class);
				criteria.add(Restrictions.eq("tool", tool));
				criteria.add(Restrictions.eq("configKey", configKey));

				return (ToolConfiguration) criteria.uniqueResult();
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getToolConfigurationByToolIdAndConfigKey(toolId=" + toolId + ", configKey=" + configKey
					+ ") query from Tool: " + e.getMessage(), e);
		}
		return null;
	}

}
