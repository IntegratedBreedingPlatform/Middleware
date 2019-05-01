
package org.generationcp.middleware.service.impl.study;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class StudyInstance {

	private int instanceDbId;
	private String locationName;
	private String locationAbbreviation;
	private String customLocationAbbreviation;
	private int instanceNumber;
	private boolean hasFieldmap;

	public StudyInstance() {

	}

	public StudyInstance(final int instanceDbId, final String locationName, final String locationAbbreviation, final int instanceNumber, final String customLocationAbbreviation, final boolean hasFieldMap) {
		this.instanceDbId = instanceDbId;
		this.locationName = locationName;
		this.locationAbbreviation = locationAbbreviation;
		this.instanceNumber = instanceNumber;
		this.customLocationAbbreviation = customLocationAbbreviation;
		this.hasFieldmap = hasFieldMap;
	}

	public int getInstanceDbId() {
		return this.instanceDbId;
	}

	public void setInstanceDbId(final int instanceDbId) {
		this.instanceDbId = instanceDbId;
	}

	public String getLocationName() {
		return this.locationName;
	}

	public void setLocationName(final String locationName) {
		this.locationName = locationName;
	}

	public String getLocationAbbreviation() {
		return this.locationAbbreviation;
	}

	public void setLocationAbbreviation(final String locationAbbreviation) {
		this.locationAbbreviation = locationAbbreviation;
	}

	public int getInstanceNumber() {
		return this.instanceNumber;
	}

	public void setInstanceNumber(final int instanceNumber) {
		this.instanceNumber = instanceNumber;
	}


	public String getCustomLocationAbbreviation() {
		return customLocationAbbreviation;
	}

	public void setCustomLocationAbbreviation(final String customLocationAbbreviation) {
		this.customLocationAbbreviation = customLocationAbbreviation;
	}

	public boolean isHasFieldmap() {
		return hasFieldmap;
	}

	public void setHasFieldmap(boolean hasFieldmap) {
		this.hasFieldmap = hasFieldmap;
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof StudyInstance)) {
			return false;
		}
		final StudyInstance castOther = (StudyInstance) other;
		return new EqualsBuilder().append(this.instanceDbId, castOther.instanceDbId).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.instanceDbId).toHashCode();
	}

	@Override
	public String toString() {
		return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
	}

}
