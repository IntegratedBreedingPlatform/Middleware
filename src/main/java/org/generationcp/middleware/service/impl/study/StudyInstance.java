
package org.generationcp.middleware.service.impl.study;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class StudyInstance {

	private int instanceDbId;
	private Integer locationId;
	private String locationName;
	private String locationAbbreviation;
	private String customLocationAbbreviation;
	private int instanceNumber;
	private boolean hasFieldmap;
	private Boolean hasExperimentalDesign;
	private Boolean hasMeasurements;
	private Boolean isDesignRegenerationAllowed;

	public StudyInstance() {

	}

	public StudyInstance(final int instanceDbId, final Integer locationId, final String locationName, final String locationAbbreviation,
		final int instanceNumber, final String customLocationAbbreviation, final boolean hasFieldMap) {
		this.instanceDbId = instanceDbId;
		this.locationId = locationId;
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

	public Integer getLocationId() {
		return this.locationId;
	}

	public void setLocationId(final Integer locationId) {
		this.locationId = locationId;
	}

	public String getCustomLocationAbbreviation() {
		return this.customLocationAbbreviation;
	}

	public void setCustomLocationAbbreviation(final String customLocationAbbreviation) {
		this.customLocationAbbreviation = customLocationAbbreviation;
	}

	public boolean isHasFieldmap() {
		return this.hasFieldmap;
	}

	public void setHasFieldmap(final boolean hasFieldmap) {
		this.hasFieldmap = hasFieldmap;
	}

	public Boolean isHasExperimentalDesign() {
		return this.hasExperimentalDesign;
	}

	public void setHasExperimentalDesign(final boolean hasExperimentalDesign) {
		this.hasExperimentalDesign = hasExperimentalDesign;
	}

	public Boolean isHasMeasurements() {
		return this.hasMeasurements;
	}

	public void setHasMeasurements(final boolean hasMeasurements) {
		this.hasMeasurements = hasMeasurements;
	}

	public Boolean isDesignRegenerationAllowed() {
		return this.isDesignRegenerationAllowed;
	}

	public void setDesignRegenerationAllowed(final boolean designRegenerationAllowed) {
		this.isDesignRegenerationAllowed = designRegenerationAllowed;
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
