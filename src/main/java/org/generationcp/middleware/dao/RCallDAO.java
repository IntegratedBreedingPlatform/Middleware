package org.generationcp.middleware.dao;

import org.generationcp.middleware.pojos.workbench.RCall;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class RCallDAO extends GenericDAO<RCall, Integer> {

	public RCallDAO(final Session session) {
		super(session);
	}

	public List<RCall> getRCallsByPackageId(final Integer rPackageId) {
		final Criteria criteria = this.getSession().createCriteria(RCall.class);
		criteria.add(Restrictions.eq("rPackage.id", rPackageId));
		return criteria.list();
	}
}
