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
import org.generationcp.middleware.pojos.Progenitor;
import org.generationcp.middleware.pojos.ProgenitorPK;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link Progenitor}.
 *
 */
public class ProgenitorDAO extends GenericDAO<Progenitor, ProgenitorPK> {

	public Progenitor getByGIDAndPID(Integer gid, Integer pid) throws MiddlewareQueryException {
		try {
			if (gid != null && pid != null) {
				List<Criterion> criterions = new ArrayList<Criterion>();
				criterions.add(Restrictions.eq("pid", pid));
				criterions.add(Restrictions.eq("progntrsPK.gid", gid));
				List<Progenitor> progenitors = this.getByCriteria(criterions);
				if (!progenitors.isEmpty()) {
					return progenitors.get(0);
				}
			}
		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error with getByGIDAndPID(gid=" + gid + ", pid=" + pid + ") query from Progenitor: " + e.getMessage(), e);
		}
		return null;
	}
}
