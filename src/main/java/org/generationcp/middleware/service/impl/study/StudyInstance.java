
package org.generationcp.middleware.service.impl.study;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class StudyInstance {

	private int instanceDbId;
	private int experimentId;
	private Integer locationId;
	private String locationName;
	private String locationAbbreviation;
	private String customLocationAbbreviation;
	private int instanceNumber;
	private boolean hasFieldmap;
	private Boolean hasGeoJSON;
	/** has X/Y coordinates */
	private Boolean hasFieldLayout;
	private Boolean hasInventory;
	private Boolean hasExperimentalDesign;
	private Boolean hasMeasurements;
	private Boolean canBeDeleted;

	public StudyInstance() {

	}

	public StudyInstance(final int instanceDbId, final int experimentId, final int instanceNumber, final boolean hasFieldmap,
		final Boolean hasExperimentalDesign,
		final Boolean hasMeasurements, final Boolean canBeDeleted) {
		this.instanceDbId = instanceDbId;
		this.experimentId = experimentId;
		this.instanceNumber = instanceNumber;
		this.hasFieldmap = hasFieldmap;
		this.hasExperimentalDesign = hasExperimentalDesign;
		this.hasMeasurements = hasMeasurements;
		this.canBeDeleted = canBeDeleted;
	}

	public StudyInstance(final int instanceDbId, final Integer experimentId, final Integer locationId, final String locationName,
		final String locationAbbreviation,
		final int instanceNumber, final String customLocationAbbreviation, final boolean hasFieldMap) {
		this.instanceDbId = instanceDbId;
		this.locationId = locationId;
		this.locationName = locationName;
		this.locationAbbreviation = locationAbbreviation;
		this.instanceNumber = instanceNumber;
		this.customLocationAbbreviation = customLocationAbbreviation;
		this.hasFieldmap = hasFieldMap;
		this.experimentId = experimentId;
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

	public Boolean getHasGeoJSON() {
		return this.hasGeoJSON;
	}

	public void setHasGeoJSON(final Boolean hasGeoJSON) {
		this.hasGeoJSON = hasGeoJSON;
	}

	public Boolean getHasFieldLayout() {
		return this.hasFieldLayout;
	}

	public void setHasFieldLayout(final Boolean hasFieldLayout) {
		this.hasFieldLayout = hasFieldLayout;
	}

	public Boolean getHasInventory() {
		return this.hasInventory;
	}

	public void setHasInventory(final Boolean hasInventory) {
		this.hasInventory = hasInventory;
	}

	public Boolean isHasExperimentalDesign() {
		return this.hasExperimentalDesign;
	}

	public void setHasExperimentalDesign(final Boolean hasExperimentalDesign) {
		this.hasExperimentalDesign = hasExperimentalDesign;
	}

	public Boolean isHasMeasurements() {
		return this.hasMeasurements;
	}

	public void setHasMeasurements(final Boolean hasMeasurements) {
		this.hasMeasurements = hasMeasurements;
	}

	public Boolean getCanBeDeleted() {
		return canBeDeleted;
	}

	public void setCanBeDeleted(final Boolean canBeDeleted) {
		this.canBeDeleted = canBeDeleted;
	}

	public int getExperimentId() {
		return this.experimentId;
	}

	public void setExperimentId(final int experimentId) {
		this.experimentId = experimentId;

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
