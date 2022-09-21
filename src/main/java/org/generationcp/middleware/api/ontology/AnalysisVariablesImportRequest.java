package org.generationcp.middleware.api.ontology;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;

@AutoProperty
public class AnalysisVariablesImportRequest {

	private List<Integer> variableIds;
	private List<String> analysisMethodNames;
	private String variableType;

	public List<Integer> getVariableIds() {
		return this.variableIds;
	}

	public void setVariableIds(final List<Integer> variableIds) {
		this.variableIds = variableIds;
	}

	public List<String> getAnalysisMethodNames() {
		return this.analysisMethodNames;
	}

	public void setAnalysisMethodNames(final List<String> analysisMethodNames) {
		this.analysisMethodNames = analysisMethodNames;
	}

	public String getVariableType() {
		return this.variableType;
	}

	public void setVariableType(final String variableType) {
		this.variableType = variableType;
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}

	@Override
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}
}
