package org.generationcp.middleware.v2.factory;

import org.generationcp.middleware.v2.helper.Variable;
import org.generationcp.middleware.v2.pojos.FactorDetails;

public class FactorDetailsFactory extends VariableDetailsFactory<FactorDetails> {

	private static final FactorDetailsFactory instance = new FactorDetailsFactory();
	
	public static FactorDetailsFactory getInstance() {
		return instance;
	}

	@Override
	boolean isAccepted(Variable variable) {
		return variable.isFactor();
	}

	@Override
	FactorDetails getNewObject() {
		return new FactorDetails();
	}

}
