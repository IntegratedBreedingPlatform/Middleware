package org.generationcp.middleware.dao;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.workbench.CropPerson;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CropPersonDAO extends GenericDAO<CropPerson, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(CropPersonDAO.class);

	public CropPersonDAO(final Session session) {
		super(session);
	}

	public CropPerson getByCropNameAndPersonId(final String cropName, final Integer personId) {

		try {
			final Criteria criteria = this.getSession().createCriteria(CropPerson.class);
			criteria.add(Restrictions.eq("cropType.cropName", cropName));
			criteria.add(Restrictions.eq("person.id", personId));
			return (CropPerson) criteria.uniqueResult();
		} catch (final HibernateException e) {
			final String message =
				"Error with getByCropNameAndPersonId(cropName=" + cropName + ",personId= " + personId + ") query from CropPersonDAO: " + e
					.getMessage();
			CropPersonDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}

	}
}
