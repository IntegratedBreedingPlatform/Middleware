package org.generationcp.middleware.domain.dms;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.data.initializer.StandardVariableTestDataInitializer;
import org.generationcp.middleware.domain.oms.TermId;

public class DMSVariableTypeTestDataInitializer {
	public static List<DMSVariableType> createDMSVariableTypeList() {
		final List<DMSVariableType> dmsVariableTypes = new ArrayList<>();
		dmsVariableTypes.add(DMSVariableTypeTestDataInitializer.createDMSVariableType());
		return dmsVariableTypes;
	}

	public static DMSVariableType createDMSVariableType() {
		final DMSVariableType variable = new DMSVariableType();
		variable.setLocalName("TRIAL_INSTANCE");
		return variable;
	}

	public static DMSVariableType createDMSVariableTypeWithStandardVariable(final TermId termId) {
		final DMSVariableType variable = new DMSVariableType();
		variable.setLocalName(termId.name());
		variable.setStandardVariable(
				StandardVariableTestDataInitializer.createStandardVariable(termId.getId(), termId.name()));
		return variable;
	}

	public static DMSVariableType createDMSVariableTypeWithCategoricalStandardVariable() {
		final DMSVariableType variable = new DMSVariableType();
		variable.setLocalName("CROSS");
		variable.setStandardVariable(StandardVariableTestDataInitializer
				.createStandardVariableWithCategoricalDataType(TermId.CROSS.getId(), TermId.CROSS.name()));
		return variable;
	}

}
