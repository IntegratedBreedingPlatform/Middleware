package org.generationcp.middleware.domain.dms;

import java.util.ArrayList;
import java.util.List;

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
}
