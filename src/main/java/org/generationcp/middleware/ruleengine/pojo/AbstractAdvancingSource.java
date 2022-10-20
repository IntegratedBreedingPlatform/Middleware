package org.generationcp.middleware.ruleengine.pojo;

import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.pojos.Name;

import java.util.List;

public class AbstractAdvancingSource {

	// These properties values are set by data resolvers
	private String season;
	private String locationAbbreviation;
	private Integer harvestLocationId;
	private String selectionTraitValue;

	// These properties values are set by data from the observation
	private String trialInstanceNumber;
	private String replicationNumber;
	private String plotNumber;

	// TODO: rename it?
	private List<MeasurementVariable> conditions;

	private List<Name> names;

	private Integer plantsSelected;

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

	public String getSelectionTraitValue() {
		return selectionTraitValue;
	}

	public void setSelectionTraitValue(final String selectionTraitValue) {
		this.selectionTraitValue = selectionTraitValue;
	}

	public String getTrialInstanceNumber() {
		return trialInstanceNumber;
	}

	public void setTrialInstanceNumber(final String trialInstanceNumber) {
		this.trialInstanceNumber = trialInstanceNumber;
	}

	public String getReplicationNumber() {
		return replicationNumber;
	}

	public void setReplicationNumber(final String replicationNumber) {
		this.replicationNumber = replicationNumber;
	}

	public String getPlotNumber() {
		return plotNumber;
	}

	public void setPlotNumber(final String plotNumber) {
		this.plotNumber = plotNumber;
	}

	public List<MeasurementVariable> getConditions() {
		return conditions;
	}

	public void setConditions(final List<MeasurementVariable> conditions) {
		this.conditions = conditions;
	}

	public List<Name> getNames() {
		return names;
	}

	public void setNames(final List<Name> names) {
		this.names = names;
	}

	public Integer getPlantsSelected() {
		return plantsSelected;
	}

	public void setPlantsSelected(final Integer plantsSelected) {
		this.plantsSelected = plantsSelected;
	}

}
