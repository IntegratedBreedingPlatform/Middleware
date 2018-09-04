package org.generationcp.middleware.data.initializer;

import org.generationcp.middleware.domain.dms.DMSVariableTypeTestDataInitializer;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.oms.TermId;

public class DMSVariableTestDataInitializer {

	public static Variable createVariable() {
		final Variable variable = new Variable();
		variable.setVariableType(DMSVariableTypeTestDataInitializer.createDMSVariableType());
		return variable;
	}
	
	public static Variable createVariableWithStandardVariable(final TermId termId) {
		final Variable variable = new Variable();
		variable.setVariableType(DMSVariableTypeTestDataInitializer.createDMSVariableTypeWithStandardVariable(termId));
		variable.setValue("Cross/1");
		return variable;
	}
	
	public static Variable createVariableWithStandardVariable(final TermId termId, final String value) {
		final Variable variable = DMSVariableTestDataInitializer.createVariableWithStandardVariable(termId);
		variable.setValue(value);
		return variable;
	}
	
	public static Variable createVariableWithCategoricalStandardVariable() {
		final Variable variable = new Variable();
		variable.setVariableType(DMSVariableTypeTestDataInitializer.createDMSVariableTypeWithCategoricalStandardVariable());
		variable.setValue("1");
		return variable;
	}
}
