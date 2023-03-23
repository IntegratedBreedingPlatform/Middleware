package org.generationcp.middleware.ruleengine.pojo;

import org.apache.commons.lang3.StringUtils;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class AdvanceGermplasmPreview {

	// observation unit ID for studies, sample ID for samples
	private String uniqueId;

	private String trialInstance;
	private String locationName;
	private String entryNumber;
	private String plotNumber;
	private String plantNumber;
	private String cross;
	private String immediateSource;
	private String breedingMethodAbbr;
	private String designation;
	private Boolean isDeleted;

	public AdvanceGermplasmPreview(final String uniqueId, final String trialInstance, final String locationName, final String entryNumber,
		final String plotNumber,
		final String plantNumber, final String cross, final String immediateSource, final String breedingMethodAbbr,
		final String designation, final Boolean isDeleted) {
		this.uniqueId = uniqueId;
		this.trialInstance = trialInstance;
		this.locationName = locationName;
		this.entryNumber = entryNumber;
		this.plotNumber = plotNumber;
		this.plantNumber = plantNumber;
		this.cross = cross;
		this.immediateSource = immediateSource;
		this.breedingMethodAbbr = breedingMethodAbbr;
		this.designation = designation;
		this.isDeleted = isDeleted;
	}

	public String getUniqueId() {
		return this.uniqueId;
	}

	public void setUniqueId(final String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public Integer getEntryNumberValue() {
		return StringUtils.isEmpty(this.entryNumber) ? 0 : Integer.parseInt(this.entryNumber);
	}

	public String getEntryNumber() {
		return this.entryNumber;
	}

	public void setEntryNumber(final String entryNumber) {
		this.entryNumber = entryNumber;
	}

	public String getTrialInstance() {
		return this.trialInstance;
	}

	public void setTrialInstance(final String trialInstance) {
		this.trialInstance = trialInstance;
	}

	public String getLocationName() {
		return this.locationName;
	}

	public void setLocationName(final String locationName) {
		this.locationName = locationName;
	}

	public String getPlotNumber() {
		return this.plotNumber;
	}

	public void setPlotNumber(final String plotNumber) {
		this.plotNumber = plotNumber;
	}

	public String getPlantNumber() {
		return this.plantNumber;
	}

	public void setPlantNumber(final String plantNumber) {
		this.plantNumber = plantNumber;
	}

	public String getCross() {
		return this.cross;
	}

	public void setCross(final String cross) {
		this.cross = cross;
	}

	public String getImmediateSource() {
		return this.immediateSource;
	}

	public void setImmediateSource(final String immediateSource) {
		this.immediateSource = immediateSource;
	}

	public String getBreedingMethodAbbr() {
		return this.breedingMethodAbbr;
	}

	public void setBreedingMethodAbbr(final String breedingMethodAbbr) {
		this.breedingMethodAbbr = breedingMethodAbbr;
	}

	public String getDesignation() {
		return this.designation;
	}

	public void setDesignation(final String designation) {
		this.designation = designation;
	}

	public boolean isDeleted() {
		return this.isDeleted;
	}

	public void setDeleted(final boolean deleted) {
		this.isDeleted = deleted;
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
