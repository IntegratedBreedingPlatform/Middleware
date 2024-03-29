package org.generationcp.middleware.api.brapi;

import org.generationcp.middleware.domain.ontology.VariableType;

import java.util.Arrays;
import java.util.List;

public enum VariableTypeGroup {
	TRAIT(Arrays.asList(VariableType.TRAIT.getName(), VariableType.SELECTION_METHOD.getName(), VariableType.ANALYSIS.getName(), VariableType.ANALYSIS_SUMMARY.getName())),
	GERMPLASM_ATTRIBUTES(Arrays.asList(VariableType.GERMPLASM_ATTRIBUTE.getName(), VariableType.GERMPLASM_PASSPORT.getName()));

	private final List<String> variableTypeNames;

	VariableTypeGroup(final List<String> variableTypeNames) {
		this.variableTypeNames = variableTypeNames;
	}

	public List<String> getVariableTypeNames() {
		return this.variableTypeNames;
	}
}
