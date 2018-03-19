package org.generationcp.middleware.data.initializer;

import java.util.Arrays;

import org.generationcp.middleware.domain.dms.VariableList;

public class VariableListTestDataInitializer {
	public static VariableList createVariableList() {
		final VariableList variableList = new VariableList();
		variableList.setVariables(Arrays.asList(DMSVariableTestDataInitializer.createVariableWithStandardVariable()));
		return variableList;
	}
	
	public static VariableList createVariableListWithCategoricalVariable() {
		final VariableList variableList = new VariableList();
		variableList.setVariables(Arrays.asList(DMSVariableTestDataInitializer.createVariableWithCategoricalStandardVariable()));
		return variableList;
	}
}
