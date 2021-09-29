package org.generationcp.middleware.api.germplasmlist;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class GermplasmListObservationRequestDto {

	private Integer listDataId;

	private Integer variableId;

	private String value;

	@JsonIgnore
	private Integer cValueId;

	public Integer getListDataId() {
		return listDataId;
	}

	public void setListDataId(final Integer listDataId) {
		this.listDataId = listDataId;
	}

	public Integer getVariableId() {
		return variableId;
	}

	public void setVariableId(final Integer variableId) {
		this.variableId = variableId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	public Integer getcValueId() {
		return cValueId;
	}

	public void setcValueId(final Integer cValueId) {
		this.cValueId = cValueId;
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
