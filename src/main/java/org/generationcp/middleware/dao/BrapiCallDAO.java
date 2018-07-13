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

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.workbench.BrapiCall;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class BrapiCallDAO extends GenericDAO<BrapiCall, Long> {

	public List<BrapiCall> getByDataType(final String dataType, final Integer pageSize, final Integer pageNumber)
		throws MiddlewareQueryException {
		CropType toReturn = null;
		try {
			Criteria criteria = this.getSession().createCriteria(BrapiCall.class);

			if (dataType != null) {
				criteria.add(Restrictions.like("datatypes", dataType));
			}

			if (pageNumber != null && pageSize != null) {
				criteria.setFirstResult(pageSize * (pageNumber - 1));
				criteria.setMaxResults(pageSize);
			}

			return (List<BrapiCall>) criteria.list();
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getByDataType(dataType=" + dataType + ") query from BrapiCall: " + e.getMessage(), e);
		}
		return null;
	}

}
