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

package org.generationcp.middleware.dao.oms;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.oms.CV;
import org.generationcp.middleware.util.StringUtil;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link CV}.
 *
 */
public class CVDao extends GenericDAO<CV, Integer> {

	public CVDao(final Session session) {
		super(session);
	}

	public Integer getIdByName(String name) throws MiddlewareQueryException {
		try {
			if (!StringUtil.isEmpty(name)) {
				Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
				criteria.add(Restrictions.eq("name", name));
				criteria.setProjection(Projections.property("cvId"));

				return (Integer) criteria.uniqueResult();
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error at getIdByName=" + name + " query at CVDao " + e.getMessage(), e);
		}
		return null;
	}
}
