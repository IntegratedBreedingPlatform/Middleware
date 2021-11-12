package org.generationcp.middleware.api.germplasmlist;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class GermplasmListVariableRequestDto {

	private Integer variableTypeId;

	private Integer variableId;

	public Integer getVariableTypeId() {
		return variableTypeId;
	}

	public void setVariableTypeId(final Integer variableTypeId) {
		this.variableTypeId = variableTypeId;
	}

	public Integer getVariableId() {
		return variableId;
	}

	public void setVariableId(final Integer variableId) {
		this.variableId = variableId;
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
