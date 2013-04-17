package org.generationcp.middleware.v2.factory;

import org.generationcp.middleware.v2.helper.Variable;
import org.generationcp.middleware.v2.pojos.ObservationDetails;

public class ObservationDetailsFactory extends VariableDetailsFactory<ObservationDetails> {

	private static final ObservationDetailsFactory instance = new ObservationDetailsFactory();
	
	public static ObservationDetailsFactory getInstance() {
		return instance;
	}
	
	@Override
	boolean isAccepted(Variable variable) {
		return variable.isObservation();
	}

	@Override
	ObservationDetails getNewObject() {
		return new ObservationDetails();
	}

}
