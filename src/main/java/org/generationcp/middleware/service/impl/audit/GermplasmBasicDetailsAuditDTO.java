package org.generationcp.middleware.service.impl.audit;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class GermplasmBasicDetailsAuditDTO extends AbstractAuditDTO {

	private String locationName;
	private String creationDate;
	private Integer groupId;

	private boolean locationChanged;
	private boolean creationDateChanged;
	private boolean groupIdChanged;

	public GermplasmBasicDetailsAuditDTO() {
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(final String locationName) {
		this.locationName = locationName;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(final String creationDate) {
		this.creationDate = creationDate;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(final Integer groupId) {
		this.groupId = groupId;
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

	public boolean isGroupIdChanged() {
		return groupIdChanged;
	}

	public void setGroupIdChanged(final boolean groupIdChanged) {
		this.groupIdChanged = groupIdChanged;
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
