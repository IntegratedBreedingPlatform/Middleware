package org.generationcp.middleware.data.initializer;

import org.generationcp.middleware.domain.dms.DMSVariableTypeTestDataInitializer;
import org.generationcp.middleware.domain.dms.Variable;

public class DMSVariableTestDataInitializer {

	public static Variable createVariable() {
		final Variable variable = new Variable();
		variable.setVariableType(DMSVariableTypeTestDataInitializer.createDMSVariableType());
		return variable;
	}
}
