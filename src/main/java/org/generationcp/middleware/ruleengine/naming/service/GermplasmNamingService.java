package org.generationcp.middleware.ruleengine.naming.service;

public interface GermplasmNamingService {

	int getNextSequenceUsingNativeSQL(String keyPrefix);

	int getNextNumberAndIncrementSequenceUsingNativeSQL(String keyPrefix);

	String getNumberWithLeadingZeroesAsString(final Integer number, final Integer numOfDigits);

}
