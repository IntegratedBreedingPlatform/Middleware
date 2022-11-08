package org.generationcp.middleware.domain.dataset;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;

@AutoProperty
public class PlotDatasetPropertiesDTO {

	private List<Integer> variableIds;
	private List<Integer> nameTypeIds;

	public List<Integer> getVariableIds() {
		return this.variableIds;
	}

	public void setVariableIds(final List<Integer> variableIds) {
		this.variableIds = variableIds;
	}

	public List<Integer> getNameTypeIds() {
		return this.nameTypeIds;
	}

	public void setNameTypeIds(final List<Integer> nameTypeIds) {
		this.nameTypeIds = nameTypeIds;
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
