package org.generationcp.middleware.dao;

import org.generationcp.middleware.pojos.CropConfiguration;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

public class CropConfigurationDAO extends GenericDAO<CropConfiguration, Integer>{

	public String getConfiguration(final String key) {
		final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
		criteria.add(Restrictions.eq("key", key));
		return (String) criteria.uniqueResult();
	}

}
