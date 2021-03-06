
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

	public void deleteByKeyPrefixes(final List<String> keyPrefixes) {
		try {
			final StringBuilder sql = new StringBuilder();
			sql.append("DELETE FROM key_sequence_register ");
			sql.append(" WHERE key_prefix IN (:keyPrefixes)");

			final Query query = this.getSession().createSQLQuery(sql.toString());
			query.setParameterList("keyPrefixes", keyPrefixes);
			query.executeUpdate();
		} catch (final HibernateException e) {
			final String message = "Error with deleteByKeyPrefixes(" + keyPrefixes + ") query from keyPrefixes " + e.getMessage();
			throw new MiddlewareQueryException(message, e);
		}


	}
}
