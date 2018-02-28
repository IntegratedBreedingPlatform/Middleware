package org.generationcp.middleware.domain.dms;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.data.initializer.StandardVariableTestDataInitializer;
import org.generationcp.middleware.domain.oms.TermId;

public class DMSVariableTypeTestDataInitializer {
	public static List<DMSVariableType> createDMSVariableTypeList(){
		List<DMSVariableType> dmsVariableTypes = new ArrayList<>();
		dmsVariableTypes.add(DMSVariableTypeTestDataInitializer.createDMSVariableType());
		return dmsVariableTypes;
	}
	
	public static DMSVariableType createDMSVariableType() {
		DMSVariableType variable = new DMSVariableType();
		variable.setLocalName("TRIAL_INSTANCE");
		return variable;
	}
	
	public static DMSVariableType createDMSVariableTypeWithStandardVariable() {
		DMSVariableType variable = new DMSVariableType();
		variable.setLocalName("CROSS");
		variable.setStandardVariable(StandardVariableTestDataInitializer.createStandardVariable(TermId.CROSS.getId(), TermId.CROSS.name()));
		return variable;
	}
	
	public static DMSVariableType createDMSVariableTypeWithCategoricalStandardVariable() {
		DMSVariableType variable = new DMSVariableType();
		variable.setLocalName("CROSS");
		variable.setStandardVariable(StandardVariableTestDataInitializer.createStandardVariableWithCategoricalDataType(TermId.CROSS.getId(), TermId.CROSS.name()));
		return variable;
	}
	
}
