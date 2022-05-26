package org.generationcp.middleware.dao;

import org.apache.commons.collections.CollectionUtils;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.ProgramLocationDefault;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

public class ProgramLocationDefaultDAO extends GenericDAO<ProgramLocationDefault, Integer>  {

	public ProgramLocationDefaultDAO(final Session session) {
		super(session);
	}

	public ProgramLocationDefault getByprogramUUID(final String programUUID) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(ProgramLocationDefault.class);
			criteria.add(Restrictions.eq("programUUID", programUUID));

			return (ProgramLocationDefault)criteria.uniqueResult();
		} catch (HibernateException e) {
			throw new MiddlewareQueryException(
				"error in: ProgramLocationDefaultDao.getByprogramUUID(programUUID=" + programUUID + "): " + e.getMessage(), e);
		}
	}

	public boolean isProgramLocationDefault(final Integer locationId) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(ProgramLocationDefault.class);
			criteria.add(Restrictions.eq("locationId", locationId));

			return CollectionUtils.isNotEmpty(criteria.list());
		} catch (HibernateException e) {
			throw new MiddlewareQueryException(
				"error in: ProgramLocationDefaultDao.isProgramLocationDefault(locationId=" + locationId + "): " + e.getMessage(), e);
		}
	}
}
