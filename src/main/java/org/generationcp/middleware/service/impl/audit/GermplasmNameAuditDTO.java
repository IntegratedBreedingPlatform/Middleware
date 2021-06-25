package org.generationcp.middleware.service.impl.audit;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Date;

@AutoProperty
public class GermplasmNameAuditDTO extends AbstractAuditDTO {

	private String nameType;
	private String value;
	private Date creationDate;
	private String locationName;
	private boolean preferred;

	private boolean nameTypeChanged;
	private boolean locationChanged;
	private boolean creationDateChanged;
	private boolean valueChanged;
	private boolean preferredChanged;

	public GermplasmNameAuditDTO() {
	}

	public String getNameType() {
		return nameType;
	}

	public void setNameType(final String nameType) {
		this.nameType = nameType;
	}

	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(final Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(final String locationName) {
		this.locationName = locationName;
	}

	public boolean isPreferred() {
		return preferred;
	}

	public void setPreferred(final boolean preferred) {
		this.preferred = preferred;
	}

	public boolean isNameTypeChanged() {
		return nameTypeChanged;
	}

	public void setNameTypeChanged(final boolean nameTypeChanged) {
		this.nameTypeChanged = nameTypeChanged;
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

	public boolean isPreferredChanged() {
		return preferredChanged;
	}

	public void setPreferredChanged(final boolean preferredChanged) {
		this.preferredChanged = preferredChanged;
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
