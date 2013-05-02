package org.generationcp.middleware.v2.domain.builder;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.v2.domain.FactorDetails;
import org.generationcp.middleware.v2.domain.VariableType;

public class FactorDetailsBuilder extends AbstractVariableDetailsBuilder<FactorDetails> {

	public FactorDetailsBuilder(
			HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	@Override
	public boolean isAccepted(VariableType variable) {
		return (variable != null 
				&& !VARIATE_TYPES.contains(variable.getStoredIn().getId()));
	}

	@Override
	public FactorDetails createNewObject(VariableType variable) {
		return new FactorDetails();
	}

}
