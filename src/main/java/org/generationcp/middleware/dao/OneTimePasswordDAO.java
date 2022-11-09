package org.generationcp.middleware.dao;

import org.generationcp.middleware.pojos.workbench.security.OneTimePassword;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.Date;
import java.util.Optional;

public class OneTimePasswordDAO extends GenericDAO<OneTimePassword, Integer> {

	public OneTimePasswordDAO(final Session session) {
		super(session);
	}

	public Optional<OneTimePassword> findNonExpiredOtpCode(final Integer otpCode) {
		final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
		criteria.add(Restrictions.eq("otpCode", otpCode));
		// Only return one time password record that's not yet expired
		criteria.add(Restrictions.ge("expires", new Date()));
		return Optional.ofNullable((OneTimePassword) criteria.uniqueResult());
	}

}
