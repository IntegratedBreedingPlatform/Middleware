
package org.generationcp.middleware.dao;

import java.util.List;

import org.generationcp.middleware.pojos.KeySequenceRegister;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public class KeySequenceRegisterDAO extends GenericDAO<KeySequenceRegister, String> {

	@SuppressWarnings("unchecked")
	public KeySequenceRegister getByPrefixAndSuffix(final String keyPrefix, final String suffix){
		if (keyPrefix != null) {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("keyPrefix", keyPrefix));
			/*
			 *  There can be multiple results (eg. same prefix but both null and empty string suffix will be returned from DB)
			 *  So order by the record with the greater last sequence # used
			 */
			criteria.addOrder(Order.desc("lastUsedSequence"));

			final List<Object[]> result = criteria.list();;
			if (result != null && !result.isEmpty()) {
				return (KeySequenceRegister) criteria.list().get(0);
			}
		}
		return null;
	}
	
	public int getNextSequence(final String keyPrefix, final String suffix) {
		final KeySequenceRegister keySequenceRegister = this.getByPrefixAndSuffix(keyPrefix, suffix);

		if (keySequenceRegister != null) {
			return keySequenceRegister.getLastUsedSequence() + 1;
		} 
		return 1;
	}
	
	public int incrementAndGetNextSequence(final String keyPrefix, final String suffix) {

		final KeySequenceRegister keySequenceRegister = this.getByPrefixAndSuffix(keyPrefix, suffix);

		if (keySequenceRegister != null) {
			int newLastUsedSequence = keySequenceRegister.getLastUsedSequence() + 1;
			keySequenceRegister.setLastUsedSequence(newLastUsedSequence);
			this.getSession().update(keySequenceRegister);
			return newLastUsedSequence;
		} else {
			this.getSession().save(new KeySequenceRegister(keyPrefix, suffix, 1));
			return 1;
		}
	}
	
	public void saveLastSequenceUsed(final String keyPrefix, final String suffix, final Integer lastSequence) {

		final KeySequenceRegister keySequenceRegister = this.getByPrefixAndSuffix(keyPrefix, suffix);

		if (keySequenceRegister != null) {
			final Integer lastUsedSequence = keySequenceRegister.getLastUsedSequence();
			if (lastSequence > lastUsedSequence){
				keySequenceRegister.setLastUsedSequence(lastSequence);
				this.getSession().update(keySequenceRegister);
			}
		} else {
			this.getSession().save(new KeySequenceRegister(keyPrefix, suffix, lastSequence));
		}
	}
}
