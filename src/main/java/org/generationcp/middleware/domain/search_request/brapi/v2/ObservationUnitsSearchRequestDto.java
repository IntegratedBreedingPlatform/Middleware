package org.generationcp.middleware.domain.search_request.brapi.v2;

import com.google.common.collect.Lists;
import org.generationcp.middleware.api.brapi.v2.observationunit.ObservationLevelRelationship;
import org.generationcp.middleware.domain.search_request.SearchRequestDto;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;

@AutoProperty
public class ObservationUnitsSearchRequestDto extends SearchRequestDto {

	private List<String> germplasmDbIds;
	private List<String> locationDbIds;
	private List<String> observationUnitDbIds;
	private List<String> programDbIds;
	private List<String> studyDbIds;
	private List<String> trialDbIds;
	private List<ObservationLevelRelationship> observationLevels;
	private List<String> externalReferenceIDs;
	private List<String> externalReferenceSources;

	private Boolean includeObservations = false;

	public ObservationUnitsSearchRequestDto() {
		this.locationDbIds = Lists.newArrayList();
		this.observationUnitDbIds = Lists.newArrayList();
		this.germplasmDbIds = Lists.newArrayList();
		this.programDbIds = Lists.newArrayList();
		this.studyDbIds = Lists.newArrayList();
		this.trialDbIds = Lists.newArrayList();
		this.externalReferenceIDs = Lists.newArrayList();
		this.externalReferenceSources = Lists.newArrayList();
	}

	public List<String> getGermplasmDbIds() {
		return this.germplasmDbIds;
	}

	public void setGermplasmDbIds(final List<String> germplasmDbIds) {
		this.germplasmDbIds = germplasmDbIds;
	}

	public List<String> getLocationDbIds() {
		return this.locationDbIds;
	}

	public void setLocationDbIds(final List<String> locationDbIds) {
		this.locationDbIds = locationDbIds;
	}

	public List<String> getObservationUnitDbIds() {
		return this.observationUnitDbIds;
	}

	public void setObservationUnitDbIds(final List<String> observationUnitDbIds) {
		this.observationUnitDbIds = observationUnitDbIds;
	}

	public List<String> getProgramDbIds() {
		return this.programDbIds;
	}

	public void setProgramDbIds(final List<String> programDbIds) {
		this.programDbIds = programDbIds;
	}

	public List<String> getStudyDbIds() {
		return this.studyDbIds;
	}

	public void setStudyDbIds(final List<String> studyDbIds) {
		this.studyDbIds = studyDbIds;
	}

	public List<String> getTrialDbIds() {
		return this.trialDbIds;
	}

	public void setTrialDbIds(final List<String> trialDbIds) {
		this.trialDbIds = trialDbIds;
	}

	public List<ObservationLevelRelationship> getObservationLevels() {
		return this.observationLevels;
	}

	public void setObservationLevels(final List<ObservationLevelRelationship> observationLevels) {
		this.observationLevels = observationLevels;
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

	public Boolean getIncludeObservations() {
		return this.includeObservations;
	}

	public void setIncludeObservations(final Boolean includeObservations) {
		this.includeObservations = includeObservations;
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
