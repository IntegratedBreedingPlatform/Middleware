
package org.generationcp.middleware.dao;

import org.generationcp.middleware.pojos.KeySequenceRegister;

public class KeySequenceRegisterDAO extends GenericDAO<KeySequenceRegister, String> {

	public int incrementAndGetNextSequence(final String keyPrefix) {

		final KeySequenceRegister existingKeySequence = this.getById(keyPrefix);
		if (existingKeySequence != null) {
			int newLastUsedSequence = existingKeySequence.getLastUsedSequence() + 1;
			existingKeySequence.setLastUsedSequence(newLastUsedSequence);
			this.getSession().update(existingKeySequence);
			return newLastUsedSequence;
		} else {
			this.getSession().save(new KeySequenceRegister(keyPrefix, 1));
			return 1;
		}
	}
}
