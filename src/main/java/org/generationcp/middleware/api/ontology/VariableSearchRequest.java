package org.generationcp.middleware.api.ontology;

import org.generationcp.middleware.domain.sqlfilter.SqlTextFilter;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;

@AutoProperty
public class VariableSearchRequest {

	private SqlTextFilter nameFilter;
	private String nameOrAlias;
	private List<Integer> variableIds;
	private List<Integer> variableTypeIds;

	public SqlTextFilter getNameFilter() {
		return nameFilter;
	}

	public void setNameFilter(final SqlTextFilter nameFilter) {
		this.nameFilter = nameFilter;
	}

	public List<Integer> getVariableIds() {
		return variableIds;
	}

	public void setVariableIds(final List<Integer> variableIds) {
		this.variableIds = variableIds;
	}

	public List<Integer> getVariableTypeIds() {
		return variableTypeIds;
	}

	public void setVariableTypeIds(final List<Integer> variableTypeIds) {
		this.variableTypeIds = variableTypeIds;
	}

	public String getNameOrAlias() {
		return this.nameOrAlias;
	}

	public void setNameOrAlias(final String nameOrAlias) {
		this.nameOrAlias = nameOrAlias;
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
