package org.generationcp.middleware.dao;

import org.generationcp.middleware.pojos.workbench.security.UserDeviceMetaData;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class UserDeviceMetaDataDAO extends GenericDAO<UserDeviceMetaData, Integer> {

	public UserDeviceMetaDataDAO(final Session session) {
		super(session);
	}

	public List<UserDeviceMetaData> findByUserId(final Integer userId) {
		final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
		criteria.add(Restrictions.eq("userId", userId));
		return criteria.list();
	}

}
