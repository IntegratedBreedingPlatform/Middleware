package org.generationcp.middleware.ruleengine.naming.service;

public interface GermplasmNamingService {

	// TODO: rename me!
	int getNextNumberAndIncrementSequenceWithoutHibernate(String keyPrefix);

	String getNumberWithLeadingZeroesAsString(final Integer number, final Integer numOfDigits);

}
