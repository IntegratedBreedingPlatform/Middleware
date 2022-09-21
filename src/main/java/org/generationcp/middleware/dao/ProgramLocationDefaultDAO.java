package org.generationcp.middleware.dao;

import org.apache.commons.collections.CollectionUtils;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.ProgramLocationDefault;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

public class ProgramLocationDefaultDAO extends GenericDAO<ProgramLocationDefault, Integer> {

	public ProgramLocationDefaultDAO(final Session session) {
		super(session);
	}

	public ProgramLocationDefault getByProgramUUID(final String programUUID) throws MiddlewareQueryException {
		try {
			final Criteria criteria = this.getSession().createCriteria(ProgramLocationDefault.class);
			criteria.add(Restrictions.eq("programUUID", programUUID));

			return (ProgramLocationDefault) criteria.uniqueResult();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				"error in: ProgramLocationDefaultDao.getByProgramUUID(programUUID=" + programUUID + "): " + e.getMessage(), e);
		}
	}

	public boolean isProgramBreedingLocationDefault(final Integer locationId) throws MiddlewareQueryException {
		try {
			final Criteria criteria = this.getSession().createCriteria(ProgramLocationDefault.class);
			criteria.add(Restrictions.eq("breedingLocationId", locationId));

			return CollectionUtils.isNotEmpty(criteria.list());
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				"error in: ProgramLocationDefaultDao.isProgramBreedingLocationDefault(locationId=" + locationId + "): " + e.getMessage(), e);
		}
	}

	public boolean isProgramStorageLocationDefault(final Integer locationId) throws MiddlewareQueryException {
		try {
			final Criteria criteria = this.getSession().createCriteria(ProgramLocationDefault.class);
			criteria.add(Restrictions.eq("storageLocationId", locationId));

			return CollectionUtils.isNotEmpty(criteria.list());
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				"error in: ProgramLocationDefaultDao.isProgramStorageLocationDefault(locationId=" + locationId + "): " + e.getMessage(), e);
		}
	}
}
