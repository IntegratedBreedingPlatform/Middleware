package org.generationcp.middleware.api.study;

import org.generationcp.middleware.domain.etl.ExperimentalDesignVariable;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.TreatmentVariable;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Date;
import java.util.List;
import java.util.Map;

@AutoProperty
public class StudyDetailsDTO {

	private Integer id;
	private String name;
	private String description;
	private String studyType;
	private String objective;
	private String createdByName;
	private Date startDate;
	private Date endDate;
	private Date lastUpdateDate;

	private Integer numberOfEntries;
	private Integer numberOfPlots;
	private boolean hasFieldLayout;
	private Integer numberOfVariablesWithData;
	private Integer totalVariablesWithData;

	private List<MeasurementVariable> studySettings;
	private List<MeasurementVariable> selections;
	private List<MeasurementVariable> entryDetails;
	private List<TreatmentVariable> treatmentFactors;

	private Integer numberOfEnvironments;
	private List<MeasurementVariable> environmentDetails;
	private List<MeasurementVariable> environmentConditions;

	private ExperimentalDesignVariable experimentalDesignDetail;
	private Integer numberOfChecks;
	private Integer nonReplicatedEntriesCount;
	private Map<Integer, MeasurementVariable> factorsByIds;

	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getStudyType() {
		return studyType;
	}

	public void setStudyType(final String studyType) {
		this.studyType = studyType;
	}

	public String getObjective() {
		return objective;
	}

	public void setObjective(final String objective) {
		this.objective = objective;
	}

	public String getCreatedByName() {
		return createdByName;
	}

	public void setCreatedByName(final String createdByName) {
		this.createdByName = createdByName;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(final Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(final Date endDate) {
		this.endDate = endDate;
	}

	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(final Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	public Integer getNumberOfEntries() {
		return numberOfEntries;
	}

	public void setNumberOfEntries(final Integer numberOfEntries) {
		this.numberOfEntries = numberOfEntries;
	}

	public Integer getNumberOfPlots() {
		return numberOfPlots;
	}

	public void setNumberOfPlots(final Integer numberOfPlots) {
		this.numberOfPlots = numberOfPlots;
	}

	public boolean isHasFieldLayout() {
		return hasFieldLayout;
	}

	public void setHasFieldLayout(final boolean hasFieldLayout) {
		this.hasFieldLayout = hasFieldLayout;
	}

	public Integer getNumberOfVariablesWithData() {
		return numberOfVariablesWithData;
	}

	public void setNumberOfVariablesWithData(final Integer numberOfVariablesWithData) {
		this.numberOfVariablesWithData = numberOfVariablesWithData;
	}

	public Integer getTotalVariablesWithData() {
		return totalVariablesWithData;
	}

	public void setTotalVariablesWithData(final Integer totalVariablesWithData) {
		this.totalVariablesWithData = totalVariablesWithData;
	}

	public List<MeasurementVariable> getStudySettings() {
		return studySettings;
	}

	public void setStudySettings(final List<MeasurementVariable> studySettings) {
		this.studySettings = studySettings;
	}

	public List<MeasurementVariable> getSelections() {
		return selections;
	}

	public void setSelections(final List<MeasurementVariable> selections) {
		this.selections = selections;
	}

	public List<MeasurementVariable> getEntryDetails() {
		return entryDetails;
	}

	public void setEntryDetails(final List<MeasurementVariable> entryDetails) {
		this.entryDetails = entryDetails;
	}

	public List<TreatmentVariable> getTreatmentFactors() {
		return treatmentFactors;
	}

	public void setTreatmentFactors(final List<TreatmentVariable> treatmentFactors) {
		this.treatmentFactors = treatmentFactors;
	}

	public Integer getNumberOfEnvironments() {
		return numberOfEnvironments;
	}

	public void setNumberOfEnvironments(final Integer numberOfEnvironments) {
		this.numberOfEnvironments = numberOfEnvironments;
	}

	public List<MeasurementVariable> getEnvironmentDetails() {
		return environmentDetails;
	}

	public void setEnvironmentDetails(final List<MeasurementVariable> environmentDetails) {
		this.environmentDetails = environmentDetails;
	}

	public List<MeasurementVariable> getEnvironmentConditions() {
		return environmentConditions;
	}

	public void setEnvironmentConditions(final List<MeasurementVariable> environmentConditions) {
		this.environmentConditions = environmentConditions;
	}

	public ExperimentalDesignVariable getExperimentalDesignDetail() {
		return experimentalDesignDetail;
	}

	public void setExperimentalDesignDetail(final ExperimentalDesignVariable experimentalDesignDetail) {
		this.experimentalDesignDetail = experimentalDesignDetail;
	}

	public Integer getNumberOfChecks() {
		return numberOfChecks;
	}

	public void setNumberOfChecks(final Integer numberOfChecks) {
		this.numberOfChecks = numberOfChecks;
	}

	public Integer getNonReplicatedEntriesCount() {
		return nonReplicatedEntriesCount;
	}

	public void setNonReplicatedEntriesCount(final Integer nonReplicatedEntriesCount) {
		this.nonReplicatedEntriesCount = nonReplicatedEntriesCount;
	}

	public Map<Integer, MeasurementVariable> getFactorsByIds() {
		return factorsByIds;
	}

	public void setFactorsByIds(final Map<Integer, MeasurementVariable> factorsByIds) {
		this.factorsByIds = factorsByIds;
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
