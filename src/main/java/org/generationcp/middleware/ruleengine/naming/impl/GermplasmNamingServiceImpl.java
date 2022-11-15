package org.generationcp.middleware.ruleengine.naming.impl;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.ruleengine.naming.service.GermplasmNamingService;
import org.generationcp.middleware.service.api.KeySequenceRegisterService;
import org.springframework.beans.factory.annotation.Autowired;

public class GermplasmNamingServiceImpl implements GermplasmNamingService {

	@Autowired
	private KeySequenceRegisterService keySequenceRegisterService;

	@Autowired
	private GermplasmDataManager germplasmDataManager;

	@Override
	public String getNumberWithLeadingZeroesAsString(final Integer number, final Integer numOfDigits) {
		if (numOfDigits == null || numOfDigits <= 0) {
			return number.toString();
		}
		return String.format("%0" + numOfDigits + "d", number);
	}

	@Override
	public int getNextNumberAndIncrementSequenceWithoutHibernate(final String keyPrefix) {
		final int nextSequence = this.getNextSequenceWithoutHibernate(keyPrefix);
		this.keySequenceRegisterService.saveLastSequenceUsedWithoutHibernate(keyPrefix, nextSequence);
		return nextSequence;
	}

	// TODO: rename me!
	private int getNextSequenceWithoutHibernate(final String keyPrefix) {
		if (!StringUtils.isEmpty(keyPrefix)) {
			final int nextSequenceNumber = this.keySequenceRegisterService.getNextSequenceWithoutHibernate(keyPrefix.trim());
			if (nextSequenceNumber > 1) {
				return nextSequenceNumber;

				// If the sequence doesn't exist yet in key_sequence_register table, query in NAMES table for the latest one used
			} else {
				return Integer.valueOf(this.germplasmDataManager.getNextSequenceNumberAsString(keyPrefix.trim()));
			}
		}

		return 1;
	}

}
