package org.generationcp.middleware.data.initializer;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.pojos.oms.CVTerm;

public class CVTermTestDataInitializer {

	public static CVTerm createTerm(final String name, final Integer cvId) {

		final CVTerm term = new CVTerm();
		term.setName(name);
		term.setDefinition("Test description " + RandomStringUtils.randomAlphanumeric(10));
		term.setCv(cvId);
		term.setIsObsolete(false);
		term.setIsRelationshipType(false);
		term.setIsSystem(false);
		return term;
	}

	public static CVTerm createTerm(final String name, final String definition, final Integer cvId) {

		final CVTerm term = new CVTerm();
		term.setName(name);
		term.setDefinition(definition);
		term.setCv(cvId);
		term.setIsObsolete(false);
		term.setIsRelationshipType(false);

		return term;
	}
}
