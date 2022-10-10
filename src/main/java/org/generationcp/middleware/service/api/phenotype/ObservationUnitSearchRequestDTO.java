package org.generationcp.middleware.service.api.phenotype;

import org.generationcp.middleware.api.brapi.v2.observationunit.ObservationLevelRelationship;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@AutoProperty
public class ObservationUnitSearchRequestDTO implements Serializable {

	private List<String> germplasmDbIds;
	private List<String> locationDbIds;
	private String observationLevel;
	private String observationTimeStampRangeStart;
	private String observationTimeStampRangeEnd;
	private List<String> observationVariableDbIds;
	private Integer page;
	private Integer pageSize;
	private List<String> studyDbIds;
	private List<String> programDbIds;
	private List<String> trialDbIds;
	private List<String> seasonDbIds;
	private List<ObservationLevelRelationship> observationLevelRelationships;
	private Boolean includeObservations = false;
	private List<String> observationUnitDbIds;
	private List<String> externalReferenceIDs;
	private List<String> externalReferenceSources;

	// extracted from observation level relationships
	private List<String> observationLevelCodes;
	private Set<String> datasetTypeNames;

	public List<String> getObservationUnitDbIds() {
		return this.observationUnitDbIds;
	}

	public void setObservationUnitDbIds(final List<String> observationUnitDbIds) {
		this.observationUnitDbIds = observationUnitDbIds;
	}

	public String getObservationLevel() {
		return this.observationLevel;
	}

	public void setObservationLevels(final String observationLevel) {
		this.observationLevel = observationLevel;
	}

	public List<String> getObservationVariableDbIds() {
		return this.observationVariableDbIds;
	}

	public void setObservationVariableDbIds(final List<String> observationVariableDbIds) {
		this.observationVariableDbIds = observationVariableDbIds;
	}

	public Integer getPage() {
		return this.page;
	}

	public void setPage(final Integer page) {
		this.page = page;
	}

	public Integer getPageSize() {
		return this.pageSize;
	}

	public void setPageSize(final Integer pageSize) {
		this.pageSize = pageSize;
	}

	public List<String> getStudyDbIds() {
		return this.studyDbIds;
	}

	public void setStudyDbIds(final List<String> studyDbIds) {
		this.studyDbIds = studyDbIds;
	}

	public List<String> getLocationDbIds() {
		return this.locationDbIds;
	}

	public void setLocationDbIds(final List<String> locationDbIds) {
		this.locationDbIds = locationDbIds;
	}

	public List<String> getGermplasmDbIds() {
		return this.germplasmDbIds;
	}

	public void setGermplasmDbIds(final List<String> germplasmDbIds) {
		this.germplasmDbIds = germplasmDbIds;
	}

	public String getObservationTimeStampRangeStart() {
		return this.observationTimeStampRangeStart;
	}

	public void setObservationTimeStampRangeStart(final String observationTimeStampRangeStart) {
		this.observationTimeStampRangeStart = observationTimeStampRangeStart;
	}

	public String getObservationTimeStampRangeEnd() {
		return this.observationTimeStampRangeEnd;
	}

	public void setObservationTimeStampRangeEnd(final String observationTimeStampRangeEnd) {
		this.observationTimeStampRangeEnd = observationTimeStampRangeEnd;
	}

	public List<String> getProgramDbIds() {
		return this.programDbIds;
	}

	public void setProgramDbIds(final List<String> programDbIds) {
		this.programDbIds = programDbIds;
	}

	public List<String> getTrialDbIds() {
		return this.trialDbIds;
	}

	public void setTrialDbIds(final List<String> trialDbIds) {
		this.trialDbIds = trialDbIds;
	}

	public List<String> getExternalReferenceIDs() {
		return externalReferenceIDs;
	}

	public void setExternalReferenceIDs(final List<String> externalReferenceIDs) {
		this.externalReferenceIDs = externalReferenceIDs;
	}

	public List<String> getExternalReferenceSources() {
		return externalReferenceSources;
	}

	public void setExternalReferenceSources(final List<String> externalReferenceSources) {
		this.externalReferenceSources = externalReferenceSources;
	}

	public List<String> getSeasonDbIds() {
		return this.seasonDbIds;
	}

	public void setSeasonDbIds(final List<String> seasonDbIds) {
		this.seasonDbIds = seasonDbIds;
	}

	public List<ObservationLevelRelationship> getObservationLevelRelationships() {
		return this.observationLevelRelationships;
	}

	public void setObservationLevelRelationships(
		final List<ObservationLevelRelationship> observationLevelRelationships) {
		this.observationLevelRelationships = observationLevelRelationships;
	}

	public Boolean getIncludeObservations() {
		return this.includeObservations;
	}

	public void setIncludeObservations(final Boolean includeObservations) {
		this.includeObservations = includeObservations;
	}

	public List<String> getObservationLevelCodes() {
		return this.observationLevelCodes;
	}

	public void setObservationLevelCodes(final List<String> observationLevelCodes) {
		this.observationLevelCodes = observationLevelCodes;
	}

	public Set<String> getDatasetTypeNames() {
		return this.datasetTypeNames;
	}

	public void setDatasetTypeNames(final Set<String> datasetTypeNames) {
		this.datasetTypeNames = datasetTypeNames;
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
