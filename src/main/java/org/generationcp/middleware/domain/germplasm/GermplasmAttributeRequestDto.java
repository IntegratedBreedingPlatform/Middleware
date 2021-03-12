package org.generationcp.middleware.domain.germplasm;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class GermplasmAttributeRequestDto {

	private String value;

	private String attributeCode;

	private String attributeType;

	private String date;

	private Integer locationId;

	public GermplasmAttributeRequestDto() {

	}

	public GermplasmAttributeRequestDto(final String value, final String attributeCode, final String attributeType, final String date,
		final Integer locationId) {
		this.value = value;
		this.attributeCode = attributeCode;
		this.attributeType = attributeType;
		this.date = date;
		this.locationId = locationId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	public String getAttributeCode() {
		return attributeCode;
	}

	public void setAttributeCode(final String attributeCode) {
		this.attributeCode = attributeCode;
	}

	public String getAttributeType() {
		return attributeType;
	}

	public void setAttributeType(final String attributeType) {
		this.attributeType = attributeType;
	}

	public String getDate() {
		return date;
	}

	public void setDate(final String date) {
		this.date = date;
	}

	public Integer getLocationId() {
		return locationId;
	}

	public void setLocationId(final Integer locationId) {
		this.locationId = locationId;
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
