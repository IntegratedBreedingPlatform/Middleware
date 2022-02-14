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

import org.apache.commons.collections.CollectionUtils;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.generationcp.middleware.pojos.dms.ProgramFavorite.FavoriteType;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * DAO class for {@link ProgramFavoriteDAO}.
 *
 */
@SuppressWarnings("unchecked")
public class ProgramFavoriteDAO extends GenericDAO<ProgramFavorite, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(ProgramFavoriteDAO.class);

	public List<ProgramFavorite> getProgramFavorites(final ProgramFavorite.FavoriteType type, final String programUUID)
		throws MiddlewareQueryException {

		try {

			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("entityType", type));
			criteria.add(Restrictions.eq("uniqueID", programUUID));

			return criteria.list();

		} catch (final HibernateException e) {
			final String message = "Error in getProgramFavorites(" + type.name() + ") in ProgramFavoriteDao: " + e.getMessage();
			LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public Optional<ProgramFavorite> getProgramFavorite(final String programUUID, final ProgramFavorite.FavoriteType type, final Integer entityId)
			throws MiddlewareQueryException {

		try {

			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("uniqueID", programUUID));
			criteria.add(Restrictions.eq("entityType", type));
			criteria.add(Restrictions.eq("entityId", entityId));

			final List<ProgramFavorite> result = criteria.list();
			return result.size() > 0 ? Optional.of(result.get(0)) : Optional.empty();

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in getProgramFavorites(" + type.name() + ") in ProgramFavoriteDao: "
					+ e.getMessage(), e);
		}
	}

	public List<ProgramFavorite> getProgramFavorites(final FavoriteType type, final int max, final String programUUID)
		throws MiddlewareQueryException {
		try {

			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("entityType", type));
			criteria.add(Restrictions.eq("uniqueID", programUUID));
			criteria.setMaxResults(max);

			return criteria.list();

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in getProgramFavorites(" + type.name() + "," + max + ") in ProgramFavoriteDao: "
					+ e.getMessage(), e);
		}
	}

	public List<ProgramFavorite> getProgramFavorites(final String programUUID, final ProgramFavorite.FavoriteType favoriteType, final Set<Integer> entityIds)
		throws MiddlewareQueryException {
		try {

			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("entityType", favoriteType));
			criteria.add(Restrictions.eq("uniqueID", programUUID));

			if(CollectionUtils.isNotEmpty(entityIds)){
				criteria.add(Restrictions.in("entityId", entityIds));
			}

			return criteria.list();

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in getProgramFavorites(" + favoriteType.name() + "," + entityIds.toArray() + ") in ProgramFavoriteDao: "
				+ e.getMessage(), e);
		}
	}

	public void deleteAllProgramFavorites(final String programUUID) {
		try {
			final String sql = "DELETE pf FROM program_favorites pf WHERE program_uuid = :programUUID";
			final SQLQuery sqlQuery = this.getSession().createSQLQuery(sql);
			sqlQuery.setParameter("programUUID", programUUID);
			sqlQuery.executeUpdate();
		} catch (final HibernateException e) {
			final String message = "Error in deleteAllProgramFavorites(" + programUUID + ") in ProgramFavoriteDao: " + e.getMessage();
			LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public void deleteProgramFavorites(final String programUUID, final Set<Integer> programFavoriteIds) {
		try {
			final StringBuilder stringBuilder = new StringBuilder("DELETE pf FROM program_favorites pf WHERE pf.program_uuid = :programUUID");
			stringBuilder.append(" and pf.id in (:programFavoriteIds)");

			final SQLQuery sqlQuery = this.getSession().createSQLQuery(stringBuilder.toString());
			sqlQuery.setParameter("programUUID", programUUID);
			sqlQuery.setParameterList("programFavoriteIds", programFavoriteIds);

			sqlQuery.executeUpdate();
		} catch (final HibernateException e) {
			final String message =
				"Error in deleteProgramFavorites(" + programUUID + "," + programFavoriteIds.toArray() + ") in ProgramFavoriteDao: "
					+ e.getMessage();
			LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public void deleteProgramFavorites(final ProgramFavorite.FavoriteType favoriteType, final Set<Integer> entityIds) {
		try {
			final StringBuilder stringBuilder = new StringBuilder("DELETE pf FROM program_favorites pf WHERE pf.entity_Type = :favoriteType");
			stringBuilder.append(" and pf.entity_id in (:entityIds)");

			final SQLQuery sqlQuery = this.getSession().createSQLQuery(stringBuilder.toString());
			sqlQuery.setParameter("favoriteType", favoriteType.name());
			sqlQuery.setParameterList("entityIds", entityIds);

			sqlQuery.executeUpdate();
		} catch (final HibernateException e) {
			final String message =
				"Error in deleteProgramFavorites(" + favoriteType.name() + "," + entityIds.toArray() + ") in ProgramFavoriteDao: "
					+ e.getMessage();
			LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}
}
