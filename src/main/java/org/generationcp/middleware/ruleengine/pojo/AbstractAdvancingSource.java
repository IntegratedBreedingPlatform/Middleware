package org.generationcp.middleware.ruleengine.pojo;

import org.generationcp.middleware.domain.etl.MeasurementVariable;

import java.util.List;

public class AbstractAdvancingSource {

	private String season;
	private String locationAbbreviation;
	private Integer harvestLocationId;
	private String trialInstanceNumber;
	private List<MeasurementVariable> conditions;
	private String replicationNumber;
	private String selectionTraitValue;

	public String getSeason() {
		return season;
	}

	public void setSeason(final String season) {
		this.season = season;
	}

	public String getLocationAbbreviation() {
		return locationAbbreviation;
	}

	public void setLocationAbbreviation(final String locationAbbreviation) {
		this.locationAbbreviation = locationAbbreviation;
	}

	public Integer getHarvestLocationId() {
		return harvestLocationId;
	}

	public void setHarvestLocationId(final Integer harvestLocationId) {
		this.harvestLocationId = harvestLocationId;
	}

	public String getTrialInstanceNumber() {
		return trialInstanceNumber;
	}

	public void setTrialInstanceNumber(final String trialInstanceNumber) {
		this.trialInstanceNumber = trialInstanceNumber;
	}

	public List<MeasurementVariable> getConditions() {
		return conditions;
	}

	public void setConditions(final List<MeasurementVariable> conditions) {
		this.conditions = conditions;
	}

	public String getReplicationNumber() {
		return this.replicationNumber;
	}

	public void setReplicationNumber(final String replicationNumber) {
		this.replicationNumber = replicationNumber;
	}

	public String getSelectionTraitValue() {
		return selectionTraitValue;
	}

	public void setSelectionTraitValue(final String selectionTraitValue) {
		this.selectionTraitValue = selectionTraitValue;
	}

}
