package org.generationcp.middleware.v2.domain.builder;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.v2.domain.FactorDetails;
import org.generationcp.middleware.v2.domain.VariableType;

public class FactorDetailsBuilder extends AbstractDetailsBuilder<FactorDetails> {

	public FactorDetailsBuilder(
			HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	@Override
	public boolean isAccepted(VariableType variable) {
		return (variable != null 
				&& !OBSERVATION_TYPES.contains(variable.getStoredInId()));
	}

	@Override
	public FactorDetails createNewObject(VariableType variable) {
		return new FactorDetails();
	}

}
