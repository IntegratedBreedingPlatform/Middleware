
package org.generationcp.middleware.dao;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.KeySequenceRegister;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class KeySequenceRegisterDAO extends GenericDAO<KeySequenceRegister, String> {

	@SuppressWarnings("unchecked")
	public KeySequenceRegister getByPrefix(final String keyPrefix){
		if (keyPrefix != null) {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("keyPrefix", keyPrefix));
			/*
			 *  There can be multiple results (eg. same prefix but both null and empty string suffix will be returned from DB)
			 *  So order by the record with the greater last sequence # used
			 */
			criteria.addOrder(Order.desc("lastUsedSequence"));

			final List<Object[]> result = criteria.list();
			if (result != null && !result.isEmpty()) {
				return (KeySequenceRegister) criteria.list().get(0);
			}
		}
		return null;
	}

	public int getNextSequence(final String keyPrefix) {
		final KeySequenceRegister keySequenceRegister = this.getByPrefix(keyPrefix);

		if (keySequenceRegister != null) {
			return keySequenceRegister.getLastUsedSequence() + 1;
		}
		return 1;
	}

	public int incrementAndGetNextSequence(final String keyPrefix) {

		final KeySequenceRegister keySequenceRegister = this.getByPrefix(keyPrefix);

		if (keySequenceRegister != null) {
			final int newLastUsedSequence = keySequenceRegister.getLastUsedSequence() + 1;
			keySequenceRegister.setLastUsedSequence(newLastUsedSequence);
			this.getSession().update(keySequenceRegister);
			return newLastUsedSequence;
		} else {
			this.getSession().save(new KeySequenceRegister(keyPrefix, 1));
			return 1;
		}
	}

	public void saveLastSequenceUsed(final String keyPrefix, final Integer lastSequence) {

		final KeySequenceRegister keySequenceRegister = this.getByPrefix(keyPrefix);

		if (keySequenceRegister != null) {
			final Integer lastUsedSequence = keySequenceRegister.getLastUsedSequence();
			if (lastSequence > lastUsedSequence){
				keySequenceRegister.setLastUsedSequence(lastSequence);
				this.getSession().update(keySequenceRegister);
			}
		} else {
			this.getSession().save(new KeySequenceRegister(keyPrefix, lastSequence));
		}
	}

	public List<KeySequenceRegister> getByKeyPrefixes(final List<String> keyPrefixes) {
		final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
		criteria.add(Restrictions.in("keyPrefix", keyPrefixes));
		criteria.addOrder(Order.asc("lastUsedSequence"));

		return criteria.list();
	}

	public void updateKeySequenceRegister(final List<Integer> keySequenceRegisterIds) {
		try {
			final StringBuilder sql = new StringBuilder();
			sql.append("UPDATE key_sequence_register ksr ")
				.append(" SET last_used_sequence = (SELECT COALESCE(MAX(CONVERT(REPLACE(UPPER(n.nval), ksr.key_prefix, ''), SIGNED)), 0) ")
				.append(" FROM names n ")
				.append(" INNER JOIN germplsm g ON g.gid=n.gid ")
				.append(" WHERE n.nval LIKE CONCAT(ksr.key_prefix, '%') ")
				.append(" AND g.deleted = 0) WHERE ksr.id in (:ids)");
			final Query query = this.getSession().createSQLQuery(sql.toString());
			query.setParameterList("ids", keySequenceRegisterIds);
			query.executeUpdate();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				"Error with updateKeySequenceRegister(keySequenceRegisterIds=" + keySequenceRegisterIds + "): " + e.getMessage(), e);
		}
	}
}
