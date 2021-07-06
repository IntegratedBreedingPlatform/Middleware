package org.generationcp.middleware.service.impl.audit;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class GermplasmAttributeAuditDTO extends AbstractAuditDTO {

	private String attributeType;
	private String locationName;
	private String value;
	private String creationDate;

	private boolean attributeTypeChanged;
	private boolean locationChanged;
	private boolean creationDateChanged;
	private boolean valueChanged;

	public GermplasmAttributeAuditDTO() {
	}

	public String getAttributeType() {
		return attributeType;
	}

	public void setAttributeType(final String attributeType) {
		this.attributeType = attributeType;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(final String locationName) {
		this.locationName = locationName;
	}

	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(final String creationDate) {
		this.creationDate = creationDate;
	}

	public boolean isAttributeTypeChanged() {
		return attributeTypeChanged;
	}

	public void setAttributeTypeChanged(final boolean attributeTypeChanged) {
		this.attributeTypeChanged = attributeTypeChanged;
	}

	public boolean isLocationChanged() {
		return locationChanged;
	}

	public void setLocationChanged(final boolean locationChanged) {
		this.locationChanged = locationChanged;
	}

	public boolean isCreationDateChanged() {
		return creationDateChanged;
	}

	public void setCreationDateChanged(final boolean creationDateChanged) {
		this.creationDateChanged = creationDateChanged;
	}

	public boolean isValueChanged() {
		return valueChanged;
	}

	public void setValueChanged(final boolean valueChanged) {
		this.valueChanged = valueChanged;
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
