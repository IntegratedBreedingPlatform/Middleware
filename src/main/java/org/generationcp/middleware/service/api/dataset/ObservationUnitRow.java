package org.generationcp.middleware.service.api.dataset;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Map;

@AutoProperty
public class ObservationUnitRow {

	private Integer observationUnitId;

	private Integer gid;

	private String designation;

	private Integer trialInstance;

	private Integer entryNumber;

	private String action;

	private Map<String, ObservationUnitData> variables;

	private Map<String, StockPropertyData> entryDetails;

	// Contains environment level variables (variables added in Environment Details and Environment Conditions)
	private Map<String, ObservationUnitData> environmentVariables;

	private String obsUnitId;

	private String samplesCount;

	private Integer fileCount;

	private String[] fileVariableIds;

	private String stockId;

	public ObservationUnitRow() {

	}

	public Integer getObservationUnitId() {
		return this.observationUnitId;
	}

	public void setObservationUnitId(final Integer observationUnitId) {
		this.observationUnitId = observationUnitId;
	}

	public Integer getGid() {
		return this.gid;
	}

	public void setGid(final Integer gid) {
		this.gid = gid;
	}

	public String getDesignation() {
		return this.designation;
	}

	public void setDesignation(final String designation) {
		this.designation = designation;
	}

	public String getAction() {
		return this.action;
	}

	public void setAction(final String action) {
		this.action = action;
	}

	public Map<String, ObservationUnitData> getVariables() {
		return this.variables;
	}

	public Map<String, ObservationUnitData> getEnvironmentVariables() {
		return this.environmentVariables;
	}

	public void setEnvironmentVariables(
		final Map<String, ObservationUnitData> environmentVariables) {
		this.environmentVariables = environmentVariables;
	}

	public void setObsUnitId(final String obsUnitId) {
		this.obsUnitId = obsUnitId;
	}

	public String getObsUnitId() {
		return this.obsUnitId;
	}

	public void setVariables(final Map<String, ObservationUnitData> variables) {
		this.variables = variables;
	}

	public String getSamplesCount() {
		return samplesCount;
	}

	public void setSamplesCount(final String samplesCount) {
		this.samplesCount = samplesCount;
	}

	public Integer getFileCount() {
		return this.fileCount;
	}

	public void setFileCount(final Integer fileCount) {
		this.fileCount = fileCount;
	}

	public String[] getFileVariableIds() {
		return this.fileVariableIds;
	}

	public void setFileVariableIds(final String[] fileVariableIds) {
		this.fileVariableIds = fileVariableIds;
	}

	public Integer getTrialInstance() {
		return trialInstance;
	}

	public void setTrialInstance(final Integer trialInstance) {
		this.trialInstance = trialInstance;
	}

	public Integer getEntryNumber() {
		return entryNumber;
	}

	public void setEntryNumber(final Integer entryNumber) {
		this.entryNumber = entryNumber;
	}

	public String getStockId() {
		return this.stockId;
	}

	public void setStockId(final String stockId) {
		this.stockId = stockId;
	}

	public Map<String, StockPropertyData> getEntryDetails() {
		return entryDetails;
	}

	public void setEntryDetails(final Map<String, StockPropertyData> entryDetails) {
		this.entryDetails = entryDetails;
	}

	@Override
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}

}


