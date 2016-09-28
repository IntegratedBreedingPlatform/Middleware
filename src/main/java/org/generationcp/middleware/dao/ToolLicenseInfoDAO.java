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

import org.generationcp.middleware.pojos.workbench.ToolLicenseInfo;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

/**
 * DAO class for {@link ToolLicenseInfo}.
 *
 */
@Transactional
public class ToolLicenseInfoDAO extends GenericDAO<ToolLicenseInfo, Integer> {

	public ToolLicenseInfo getByToolName(final String toolName) {
		try {
			final Criteria criteria = this.getSession().createCriteria(ToolLicenseInfo.class);
			criteria.createAlias("tool", "t");
			criteria.add(Restrictions.eq("t.toolName", toolName));
			criteria.setMaxResults(1);
			return (ToolLicenseInfo) criteria.uniqueResult();
		} catch (final HibernateException e) {
			this.logAndThrowException(
					"Error with getByToolName(toolName=" + toolName + ") query from ToolLicenseInfoDAO: " + e.getMessage(), e);
		}
		return null;
	}
}
