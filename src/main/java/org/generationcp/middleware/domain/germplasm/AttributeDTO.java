package org.generationcp.middleware.domain.germplasm;

import org.pojomatic.Pojomatic;

public class AttributeDTO {

	private String attributeCode;
	private Integer attributeDbId;
	private String  attributeName;
	private Integer determinedDate;
	private String  value;

	public String getAttributeCode() {
		return this.attributeCode;
	}

	public void setAttributeCode(final String attributeCode) {
		this.attributeCode = attributeCode;
	}

	public Integer getAttributeDbId() {
		return this.attributeDbId;
	}

	public void setAttributeDbId(final Integer attributeDbId) {
		this.attributeDbId = attributeDbId;
	}

	public String getAttributeName() {
		return this.attributeName;
	}

	public void setAttributeName(final String attributeName) {
		this.attributeName = attributeName;
	}

	public Integer getDeterminedDate() {
		return this.determinedDate;
	}

	public void setDeterminedDate(final Integer determinedDate) {
		this.determinedDate = determinedDate;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	@Override
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}
}
