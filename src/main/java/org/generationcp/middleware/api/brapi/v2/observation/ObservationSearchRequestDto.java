package org.generationcp.middleware.api.brapi.v2.observation;

import org.generationcp.middleware.api.brapi.v2.observationlevel.ObservationLevel;
import org.generationcp.middleware.api.brapi.v2.observationunit.ObservationLevelRelationship;
import org.generationcp.middleware.domain.search_request.SearchRequestDto;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;

@AutoProperty
public class ObservationSearchRequestDto extends SearchRequestDto {


	private List<String> commonCropNames;
	private List<String> externalReferenceIds;
	private List<String> externalReferenceSources;
	private List<String> germplasmDbIds;
	private List<String> germplasmNames;
	private List<String> locationDbIds;
	private List<String> locationNames;
	private List<String> observationDbIds;
	private List<ObservationLevelRelationship> observationLevelRelationships;
	private List<ObservationLevelRelationship> observationLevels;
	private String observationTimeStampRangeStart;
	private String observationTimeStampRangeEnd;
	private List<String> observationUnitDbIds;
	private List<String> observationVariableDbIds;
	private List<String> observationVariableNames;
	private List<String> observationVariablePUIs;
	private List<String> programDbIds;
	private List<String> programNames;
	private List<String> seasonDbIds;
	private List<String> studyDbIds;
	private List<String> studyNames;
	private List<String> trialDbIds;
	private List<String> trialNames;
	private int page;
	private int pageSize;

	public List<String> getCommonCropNames() {
		return this.commonCropNames;
	}

	public void setCommonCropNames(final List<String> commonCropNames) {
		this.commonCropNames = commonCropNames;
	}

	public List<String> getExternalReferenceIds() {
		return this.externalReferenceIds;
	}

	public void setExternalReferenceIds(final List<String> externalReferenceIds) {
		this.externalReferenceIds = externalReferenceIds;
	}

	public List<String> getExternalReferenceSources() {
		return this.externalReferenceSources;
	}

	public void setExternalReferenceSources(final List<String> externalReferenceSources) {
		this.externalReferenceSources = externalReferenceSources;
	}

	public List<String> getGermplasmDbIds() {
		return this.germplasmDbIds;
	}

	public void setGermplasmDbIds(final List<String> germplasmDbIds) {
		this.germplasmDbIds = germplasmDbIds;
	}

	public List<String> getGermplasmNames() {
		return this.germplasmNames;
	}

	public void setGermplasmNames(final List<String> germplasmNames) {
		this.germplasmNames = germplasmNames;
	}

	public List<String> getLocationDbIds() {
		return this.locationDbIds;
	}

	public void setLocationDbIds(final List<String> locationDbIds) {
		this.locationDbIds = locationDbIds;
	}

	public List<String> getLocationNames() {
		return this.locationNames;
	}

	public void setLocationNames(final List<String> locationNames) {
		this.locationNames = locationNames;
	}

	public List<String> getObservationDbIds() {
		return this.observationDbIds;
	}

	public void setObservationDbIds(final List<String> observationDbIds) {
		this.observationDbIds = observationDbIds;
	}

	public List<ObservationLevelRelationship> getObservationLevelRelationships() {
		return this.observationLevelRelationships;
	}

	public void setObservationLevelRelationships(final List<ObservationLevelRelationship> observationLevelRelationships) {
		this.observationLevelRelationships = observationLevelRelationships;
	}

	public List<ObservationLevelRelationship> getObservationLevels() {
		return this.observationLevels;
	}

	public void setObservationLevels(final List<ObservationLevelRelationship> observationLevels) {
		this.observationLevels = observationLevels;
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

	public List<String> getObservationVariableNames() {
		return this.observationVariableNames;
	}

	public void setObservationVariableNames(final List<String> observationVariableNames) {
		this.observationVariableNames = observationVariableNames;
	}

	public List<String> getObservationVariablePUIs() {
		return this.observationVariablePUIs;
	}

	public void setObservationVariablePUIs(final List<String> observationVariablePUIs) {
		this.observationVariablePUIs = observationVariablePUIs;
	}

	public List<String> getProgramDbIds() {
		return this.programDbIds;
	}

	public void setProgramDbIds(final List<String> programDbIds) {
		this.programDbIds = programDbIds;
	}

	public List<String> getProgramNames() {
		return this.programNames;
	}

	public void setProgramNames(final List<String> programNames) {
		this.programNames = programNames;
	}

	public List<String> getSeasonDbIds() {
		return this.seasonDbIds;
	}

	public void setSeasonDbIds(final List<String> seasonDbIds) {
		this.seasonDbIds = seasonDbIds;
	}

	public List<String> getStudyDbIds() {
		return this.studyDbIds;
	}

	public void setStudyDbIds(final List<String> studyDbIds) {
		this.studyDbIds = studyDbIds;
	}

	public List<String> getStudyNames() {
		return this.studyNames;
	}

	public void setStudyNames(final List<String> studyNames) {
		this.studyNames = studyNames;
	}

	public List<String> getTrialDbIds() {
		return this.trialDbIds;
	}

	public void setTrialDbIds(final List<String> trialDbIds) {
		this.trialDbIds = trialDbIds;
	}

	public List<String> getTrialNames() {
		return this.trialNames;
	}

	public void setTrialNames(final List<String> trialNames) {
		this.trialNames = trialNames;
	}

	public int getPage() {
		return this.page;
	}

	public void setPage(final int page) {
		this.page = page;
	}

	public int getPageSize() {
		return this.pageSize;
	}

	public void setPageSize(final int pageSize) {
		this.pageSize = pageSize;
	}



	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return Pojomatic.equals(this, obj);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}
}
