package org.generationcp.middleware.v2.domain.builder;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.v2.domain.ObservationDetails;
import org.generationcp.middleware.v2.domain.VariableType;

public class ObservationDetailsBuilder extends AbstractVariableDetailsBuilder<ObservationDetails> {

	public ObservationDetailsBuilder(
			HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	@Override
	public boolean isAccepted(VariableType variable) {
		return (variable != null 
				&& OBSERVATION_TYPES.contains(variable.getStoredIn().getId()));
	}

	@Override
	public ObservationDetails createNewObject(VariableType variable) {
		return new ObservationDetails();
	}

}
