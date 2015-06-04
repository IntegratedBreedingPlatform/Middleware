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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DAO class for {@link ProgramFavoriteDao}.
 *
 */
@SuppressWarnings("unchecked")
public class ProgramFavoriteDAO extends GenericDAO<ProgramFavorite, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(ProgramFavoriteDAO.class);

	public List<ProgramFavorite> getProgramFavorites(ProgramFavorite.FavoriteType type, String programUUID) throws MiddlewareQueryException {

		try {

			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("entityType", type.getName()));
			criteria.add(Restrictions.eq("uniqueID", programUUID));

			List<ProgramFavorite> result = criteria.list();
			return result;

		} catch (HibernateException e) {
			this.logAndThrowException("Error in getProgramFavorites(" + type.getName() + ") in ProgramFavoriteDao: " + e.getMessage(), e);
		}

		return null;

	}

	public int countProgramFavorites(ProgramFavorite.FavoriteType type) throws MiddlewareQueryException {

		try {

			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("entityType", type.getName()));
			criteria.setProjection(Projections.rowCount());

			Integer result = (Integer) criteria.uniqueResult();
			return result.intValue();

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

			List<ProgramFavorite> result = criteria.list();
			return result;

		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error in getProgramFavorites(" + type.getName() + "," + max + ") in ProgramFavoriteDao: " + e.getMessage(), e);
		}

		return null;
	}

}
