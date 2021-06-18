package org.generationcp.middleware.service.impl.audit;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Date;

@AutoProperty
public class GermplasmNameAuditDTO {

	private RevisionType revisionType;
	private String nameType;
	private String value;
	private Date creationDate;
	private String locationName;
	private String createdBy;
	private Date createdDate;
	private String modifiedBy;
	private Date modifiedDate;
	private boolean preferred;

	private boolean nameTypeChanged;
	private boolean locationChanged;
	private boolean creationDateChanged;
	private boolean valueChanged;
	private boolean preferredChanged;

	public GermplasmNameAuditDTO() {
	}

	public RevisionType getRevisionType() {
		return revisionType;
	}

	public void setRevisionType(final RevisionType revisionType) {
		this.revisionType = revisionType;
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

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(final String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(final Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(final String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(final Date modifiedDate) {
		this.modifiedDate = modifiedDate;
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
