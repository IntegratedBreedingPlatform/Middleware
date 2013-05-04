package org.generationcp.middleware.v2.domain.builder;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.v2.domain.VariateDetails;
import org.generationcp.middleware.v2.domain.VariableType;

public class VariateDetailsBuilder extends AbstractVariableDetailsBuilder<VariateDetails> {

	public VariateDetailsBuilder(
			HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	@Override
	public boolean isAccepted(VariableType variable) {
		return (variable != null 
				&& VARIATE_TYPES.contains(variable.getStandardVariable().getStoredIn().getId()));
	}

	@Override
	public VariateDetails createNewObject(VariableType variable) {
		return new VariateDetails();
	}

}
