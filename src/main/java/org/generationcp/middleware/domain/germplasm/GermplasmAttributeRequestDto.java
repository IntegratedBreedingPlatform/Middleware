package org.generationcp.middleware.domain.germplasm;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Date;

@AutoProperty
public class GermplasmAttributeRequestDto {

	private String value;

	private String attributeCode;

	private String attributeType;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
	private Date date;

	private Integer locationId;

	public GermplasmAttributeRequestDto() {

	}

	public GermplasmAttributeRequestDto(final String value, final String attributeCode, final String attributeType, final Date date,
		final Integer locationId) {
		this.value = value;
		this.attributeCode = attributeCode;
		this.attributeType = attributeType;
		this.date = date;
		this.locationId = locationId;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	public String getAttributeCode() {
		return this.attributeCode;
	}

	public void setAttributeCode(final String attributeCode) {
		this.attributeCode = attributeCode;
	}

	public String getAttributeType() {
		return this.attributeType;
	}

	public void setAttributeType(final String attributeType) {
		this.attributeType = attributeType;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(final Date date) {
		this.date = date;
	}

	public Integer getLocationId() {
		return this.locationId;
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
