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

package org.generationcp.middleware.dao.dms;

import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.generationcp.middleware.pojos.dms.ProgramFavorite.FavoriteType;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link ProgramFavoriteDAO}.
 *
 */
@SuppressWarnings("unchecked")
public class ProgramFavoriteDAO extends GenericDAO<ProgramFavorite, Integer> {

	public List<ProgramFavorite> getProgramFavorites(ProgramFavorite.FavoriteType type, String programUUID) throws MiddlewareQueryException {

		try {

			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("entityType", type.getName()));
			criteria.add(Restrictions.eq("uniqueID", programUUID));

			return criteria.list();

		} catch (HibernateException e) {
			this.logAndThrowException("Error in getProgramFavorites(" + type.getName() + ") in ProgramFavoriteDao: " + e.getMessage(), e);
		}

		return null;

	}

	public ProgramFavorite getProgramFavorite(String programUUID, ProgramFavorite.FavoriteType type, Integer entityId)
			throws MiddlewareQueryException {

		try {

			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("uniqueID", programUUID));
			criteria.add(Restrictions.eq("entityType", type.getName()));
			criteria.add(Restrictions.eq("entityId", entityId));

			List<ProgramFavorite> result = criteria.list();
			return result.size() > 0 ? result.get(0) : null;

		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error in getProgramFavorites(" + type.getName() + ") in ProgramFavoriteDao: "
					+ e.getMessage(), e);
		}
	}

	public int countProgramFavorites(ProgramFavorite.FavoriteType type) throws MiddlewareQueryException {

		try {

			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("entityType", type.getName()));
			criteria.setProjection(Projections.rowCount());

			return (Integer) criteria.uniqueResult();

		} catch (HibernateException e) {
			this.logAndThrowException("Error in countProgramFavorites(" + type.getName() + ") in ProgramFavoriteDao: " + e.getMessage(), e);
		}

		return 0;

	}

	public List<ProgramFavorite> getProgramFavorites(FavoriteType type, int max, String programUUID) throws MiddlewareQueryException {
		try {

			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("entityType", type.getName()));
			criteria.add(Restrictions.eq("uniqueID", programUUID));
			criteria.setMaxResults(max);

			return criteria.list();

		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error in getProgramFavorites(" + type.getName() + "," + max + ") in ProgramFavoriteDao: "
					+ e.getMessage(), e);
		}
	}

}
