package org.generationcp.middleware.ruleengine.newnaming.service;

import org.generationcp.middleware.exceptions.InvalidGermplasmNameSettingException;
import org.generationcp.middleware.pojos.germplasm.GermplasmNameSetting;

public interface GermplasmNamingService {

	String getNextNameInSequence(final GermplasmNameSetting setting) throws InvalidGermplasmNameSettingException;

	String generateNextNameAndIncrementSequence(final GermplasmNameSetting setting);

	int getNextSequence(String keyPrefix);

	int getNextNumberAndIncrementSequence(String keyPrefix);

	// TODO: rename me!
	int getNextNumberAndIncrementSequenceWithoutHibernate(String keyPrefix);

	void saveLastSequenceUsed(String keyPrefix, Integer lastSequenceUsed);

	String getNumberWithLeadingZeroesAsString(final Integer number, final Integer numOfDigits);
}
