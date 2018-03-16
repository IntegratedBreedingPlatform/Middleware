package org.generationcp.middleware.data.initializer;

import org.generationcp.middleware.domain.dms.DMSVariableTypeTestDataInitializer;
import org.generationcp.middleware.domain.dms.Variable;

public class DMSVariableTestDataInitializer {

	public static Variable createVariable() {
		final Variable variable = new Variable();
		variable.setVariableType(DMSVariableTypeTestDataInitializer.createDMSVariableType());
		return variable;
	}
	
	public static Variable createVariableWithStandardVariable() {
		final Variable variable = new Variable();
		variable.setVariableType(DMSVariableTypeTestDataInitializer.createDMSVariableTypeWithStandardVariable());
		variable.setValue("Cross/1");
		return variable;
	}
	
	public static Variable createVariableWithCategoricalStandardVariable() {
		final Variable variable = new Variable();
		variable.setVariableType(DMSVariableTypeTestDataInitializer.createDMSVariableTypeWithCategoricalStandardVariable());
		variable.setValue("1");
		return variable;
	}
}
