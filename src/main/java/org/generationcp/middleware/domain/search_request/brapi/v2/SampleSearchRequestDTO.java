package org.generationcp.middleware.domain.search_request.brapi.v2;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.domain.search_request.SearchRequestDto;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Collections;
import java.util.List;

@AutoProperty
public class SampleSearchRequestDTO extends SearchRequestDto {

	private List<String> sampleDbIds;
	private List<String> observationUnitDbIds;
	private List<String> plateDbIds;
	private List<String> germplasmDbIds;
	private List<String> studyDbIds;
	private List<String> externalReferenceIDs;
	private List<String> externalReferenceSources;
	private List<String> germplasmNames;
	private List<String> studyNames;

	public SampleSearchRequestDTO() {
	}

	public SampleSearchRequestDTO(final String sampleDbId, final String observationUnitDbId, final String plateDbId,
		final String germplasmDbId, final String studyDbId, final String externalReferenceID, final String externalReferenceSource) {
		this.sampleDbIds = StringUtils.isEmpty(sampleDbId) ? null : Collections.singletonList(sampleDbId);
		this.observationUnitDbIds = StringUtils.isEmpty(observationUnitDbId) ? null : Collections.singletonList(observationUnitDbId);
		this.plateDbIds = StringUtils.isEmpty(plateDbId) ? null : Collections.singletonList(plateDbId);
		this.germplasmDbIds = StringUtils.isEmpty(germplasmDbId) ? null : Collections.singletonList(germplasmDbId);
		this.studyDbIds = StringUtils.isEmpty(studyDbId) ? null : Collections.singletonList(studyDbId);
		this.externalReferenceIDs = StringUtils.isEmpty(externalReferenceID) ? null : Collections.singletonList(externalReferenceID);
		this.externalReferenceSources = StringUtils.isEmpty(externalReferenceSource) ? null : Collections.singletonList(externalReferenceSource);
	}

	public SampleSearchRequestDTO(final List<String> sampleDbIds, final List<String> observationUnitDbIds, final List<String> plateDbIds,
		final List<String> germplasmDbIds, final List<String> studyDbIds, final List<String> externalReferenceIDs,
		final List<String> externalReferenceSources) {
		this.sampleDbIds = sampleDbIds;
		this.observationUnitDbIds = observationUnitDbIds;
		this.plateDbIds = plateDbIds;
		this.germplasmDbIds = germplasmDbIds;
		this.studyDbIds = studyDbIds;
		this.externalReferenceIDs = externalReferenceIDs;
		this.externalReferenceSources = externalReferenceSources;
	}

	public List<String> getSampleDbIds() {
		return this.sampleDbIds;
	}

	public void setSampleDbIds(final List<String> sampleDbIds) {
		this.sampleDbIds = sampleDbIds;
	}

	public List<String> getObservationUnitDbIds() {
		return this.observationUnitDbIds;
	}

	public void setObservationUnitDbIds(final List<String> observationUnitDbIds) {
		this.observationUnitDbIds = observationUnitDbIds;
	}

	public List<String> getPlateDbIds() {
		return this.plateDbIds;
	}

	public void setPlateDbIds(final List<String> plateDbIds) {
		this.plateDbIds = plateDbIds;
	}

	public List<String> getGermplasmDbIds() {
		return this.germplasmDbIds;
	}

	public void setGermplasmDbIds(final List<String> germplasmDbIds) {
		this.germplasmDbIds = germplasmDbIds;
	}

	public List<String> getStudyDbIds() {
		return this.studyDbIds;
	}

	public void setStudyDbIds(final List<String> studyDbIds) {
		this.studyDbIds = studyDbIds;
	}

	public List<String> getExternalReferenceIDs() {
		return this.externalReferenceIDs;
	}

	public void setExternalReferenceIDs(final List<String> externalReferenceIDs) {
		this.externalReferenceIDs = externalReferenceIDs;
	}

	public List<String> getExternalReferenceSources() {
		return this.externalReferenceSources;
	}

	public void setExternalReferenceSources(final List<String> externalReferenceSources) {
		this.externalReferenceSources = externalReferenceSources;
	}

	public List<String> getGermplasmNames() {
		return this.germplasmNames;
	}

	public void setGermplasmNames(final List<String> germplasmNames) {
		this.germplasmNames = germplasmNames;
	}

	public List<String> getStudyNames() {
		return this.studyNames;
	}

	public void setStudyNames(final List<String> studyNames) {
		this.studyNames = studyNames;
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
