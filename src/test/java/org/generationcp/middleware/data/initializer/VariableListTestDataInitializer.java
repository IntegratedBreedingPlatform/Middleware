package org.generationcp.middleware.data.initializer;

import java.util.Arrays;

import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.oms.TermId;

public class VariableListTestDataInitializer {
	public static VariableList createVariableList(final TermId termId) {
		final VariableList variableList = new VariableList();
		variableList.setVariables(Arrays.asList(DMSVariableTestDataInitializer.createVariableWithStandardVariable(termId)));
		return variableList;
	}
	
	public static VariableList createVariableListWithCategoricalVariable() {
		final VariableList variableList = new VariableList();
		variableList.setVariables(Arrays.asList(DMSVariableTestDataInitializer.createVariableWithCategoricalStandardVariable()));
		return variableList;
	}
}
