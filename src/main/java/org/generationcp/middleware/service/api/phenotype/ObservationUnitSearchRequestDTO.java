package org.generationcp.middleware.service.api.phenotype;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import org.generationcp.middleware.api.brapi.v2.observationunit.ObservationLevelRelationship;
import org.generationcp.middleware.domain.search_request.SearchRequestDto;
import org.generationcp.middleware.service.api.BrapiView;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;

@AutoProperty
public class ObservationUnitSearchRequestDTO extends SearchRequestDto {

	private List<String> germplasmDbIds;
	private List<String> locationDbIds;
	private List<ObservationLevelRelationship> observationLevels;
	private List<String> observationVariableDbIds;
	private Integer page;
	private Integer pageSize;
	private List<String> studyDbIds;
	private List<String> programDbIds;
	private List<String> trialDbIds;
	private List<ObservationLevelRelationship> observationLevelRelationships;
	private Boolean includeObservations = false;
	private List<String> observationUnitDbIds;
	private List<String> externalReferenceIDs;
	private List<String> externalReferenceSources;


	// v1 only fields
	@JsonIgnore
	private String observationTimeStampRangeStart;
	@JsonIgnore
	private String observationTimeStampRangeEnd;
	@JsonIgnore
	private List<String> seasonDbIds;
	@JsonIgnore
	private String observationLevel;

	// extracted from observation level relationships
	@JsonIgnore
	private List<String> observationLevelCodes;
	@JsonIgnore
	private List<String> datasetTypeNames;

	public List<String> getObservationUnitDbIds() {
		return this.observationUnitDbIds;
	}

	public void setObservationUnitDbIds(final List<String> observationUnitDbIds) {
		this.observationUnitDbIds = observationUnitDbIds;
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

	public List<String> getDatasetTypeNames() {
		return this.datasetTypeNames;
	}

	public void setDatasetTypeNames(final List<String> datasetTypeNames) {
		this.datasetTypeNames = datasetTypeNames;
	}

	public List<ObservationLevelRelationship> getObservationLevels() {
		return this.observationLevels;
	}

	public void setObservationLevels(
		final List<ObservationLevelRelationship> observationLevels) {
		this.observationLevels = observationLevels;
	}

	public String getObservationLevel() {
		return this.observationLevel;
	}

	public void setObservationLevel(final String observationLevel) {
		this.observationLevel = observationLevel;
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
