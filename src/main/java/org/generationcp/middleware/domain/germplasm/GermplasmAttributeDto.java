package org.generationcp.middleware.domain.germplasm;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;


@AutoProperty
public class GermplasmAttributeDto extends GermplasmAttributeRequestDto {

	private Integer id;

	private String attributeDescription;

	private String locationName;

	public GermplasmAttributeDto() {

	}

	public GermplasmAttributeDto(final Integer id, final String value, final String attributeCode, final String attributeType,
		final String date, final Integer locationId, final String attributeDescription, final String locationName) {
		super(value, attributeCode, attributeType, date, locationId);
		this.id = id;
		this.attributeDescription = attributeDescription;
		this.locationName = locationName;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public String getAttributeDescription() {
		return this.attributeDescription;
	}

	public void setAttributeDescription(final String attributeDescription) {
		this.attributeDescription = attributeDescription;
	}

	public String getLocationName() {
		return this.locationName;
	}

	public void setLocationName(final String locationName) {
		this.locationName = locationName;
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
