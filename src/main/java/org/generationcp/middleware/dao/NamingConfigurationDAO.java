package org.generationcp.middleware.dao;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.naming.NamingConfiguration;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

public class NamingConfigurationDAO extends GenericDAO<NamingConfiguration, Integer> {

	public static final String NAME = "name";

	public NamingConfigurationDAO(final Session session) {
		super(session);
	}

	public NamingConfiguration getByName(final String name) {
		try {
			final Criteria criteria = this.getSession().createCriteria(NamingConfiguration.class);
			criteria.add(Restrictions.eq(NAME, name));
			return (NamingConfiguration) criteria.uniqueResult();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
					this.getLogExceptionMessage("getByName", "name", name, e.getMessage(), "NamingConfigurationDAO"), e);
		}
	}

}
