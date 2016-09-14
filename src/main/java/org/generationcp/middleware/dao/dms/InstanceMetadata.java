
package org.generationcp.middleware.dao.dms;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class InstanceMetadata {

	private Integer instanceDbId;
	private String instanceNumber;
	private Integer trialDbId;
	private String trialName;
	private String instanceDatasetName;
	private String programDbId;
	private String locationName;
	private Integer locationDbId;
	private String locationAbbreviation;
	private String season;

	public Integer getInstanceDbId() {
		return this.instanceDbId;
	}

	public void setInstanceDbId(final Integer instanceDbId) {
		this.instanceDbId = instanceDbId;
	}

	public String getInstanceNumber() {
		return this.instanceNumber;
	}

	public void setInstanceNumber(final String instanceNumber) {
		this.instanceNumber = instanceNumber;
	}

	public Integer getTrialDbId() {
		return this.trialDbId;
	}

	public void setTrialDbId(final Integer trialDbId) {
		this.trialDbId = trialDbId;
	}

	public String getTrialName() {
		return this.trialName;
	}

	public void setTrialName(final String trialName) {
		this.trialName = trialName;
	}

	public String getInstanceDatasetName() {
		return this.instanceDatasetName;
	}

	public void setInstanceDatasetName(final String instanceDatasetName) {
		this.instanceDatasetName = instanceDatasetName;
	}

	public String getProgramDbId() {
		return this.programDbId;
	}

	public void setProgramDbId(final String programDbId) {
		this.programDbId = programDbId;
	}

	public String getLocationName() {
		return this.locationName;
	}

	public void setLocationName(final String locationName) {
		this.locationName = locationName;
	}

	public Integer getLocationDbId() {
		return this.locationDbId;
	}

	public void setLocationDbId(final Integer locationDbId) {
		this.locationDbId = locationDbId;
	}

	public String getLocationAbbreviation() {
		return this.locationAbbreviation;
	}

	public void setLocationAbbreviation(final String locationAbbreviation) {
		this.locationAbbreviation = locationAbbreviation;
	}

	public String getSeason() {
		return this.season;
	}

	public void setSeason(final String season) {
		this.season = season;
	}

	@Override
	public String toString() {
		return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
	}
}
