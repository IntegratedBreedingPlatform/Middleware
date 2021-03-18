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
import org.generationcp.middleware.pojos.Progenitor;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.List;

/**
 * DAO class for {@link Progenitor}.
 *
 */
public class ProgenitorDAO extends GenericDAO<Progenitor, Integer> {

	public Progenitor getByGIDAndPID(final Integer gid, final Integer pid) {
		try {
			List<Criterion> criterions = new ArrayList<>();
			criterions.add(Restrictions.eq("progenitorGid", pid));
			criterions.add(Restrictions.eq("germplasm.gid", gid));
			List<Progenitor> progenitors = this.getByCriteria(criterions);
			if (!progenitors.isEmpty()) {
				return progenitors.get(0);
			}
		} catch (HibernateException e) {
			throw new MiddlewareQueryException(
					"Error with getByGIDAndPID(gid=" + gid + ", pid=" + pid + ") query from Progenitor: " + e.getMessage(), e);
		}
		return null;
	}

	public List<Progenitor> getByGID(final Integer gid) {
		try {

			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("germplasm.gid", gid));
			criteria.setProjection(Projections.property("germplasm"));
			return criteria.list();

		} catch (HibernateException e) {
			throw new MiddlewareQueryException(
				"Error with getByGIDAndPID(gid=" + gid + ") query from Progenitor: " + e.getMessage(), e);
		}
	}

	public Progenitor getByGIDAndProgenitorNumber(final Integer gid, final Integer progenitorNumber) {
		try {
			List<Criterion> criterions = new ArrayList<>();
			criterions.add(Restrictions.eq("progenitorNumber", progenitorNumber));
			criterions.add(Restrictions.eq("germplasm.gid", gid));
			List<Progenitor> progenitors = this.getByCriteria(criterions);
			if (!progenitors.isEmpty()) {
				return progenitors.get(0);
			}
		} catch (HibernateException e) {
			throw new MiddlewareQueryException(
					"Error with getByGIDAndProgenitorNumber(gid=" + gid + ", pno=" + progenitorNumber + ") query from Progenitor: " + e.getMessage(), e);
		}
		return null;
	}
}
