package org.generationcp.middleware.dao;

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
}
