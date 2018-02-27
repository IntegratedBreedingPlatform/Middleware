package org.generationcp.middleware.data.initializer;

import org.generationcp.middleware.pojos.oms.CVTerm;

public class CVTermTestDataInitializer {
	public static CVTerm createTerm(final String name, final Integer cvId) {

		CVTerm term = new CVTerm();
		term.setName(name);
		term.setDefinition("Test description");
		term.setCv(cvId);
		term.setIsObsolete(false);
		term.setIsRelationshipType(false);

		return term;
	}
}
