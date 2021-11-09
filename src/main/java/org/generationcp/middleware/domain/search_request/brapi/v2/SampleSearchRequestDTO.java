package org.generationcp.middleware.domain.search_request.brapi.v2;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.domain.search_request.SearchRequestDto;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

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
		this.sampleDbIds = Lists.newArrayList();
		this.observationUnitDbIds = Lists.newArrayList();
		this.plateDbIds = Lists.newArrayList();
		this.germplasmDbIds = Lists.newArrayList();
		this.studyDbIds = Lists.newArrayList();
		this.externalReferenceIDs = Lists.newArrayList();
		this.externalReferenceSources = Lists.newArrayList();
		this.germplasmNames = Lists.newArrayList();
		this.studyNames = Lists.newArrayList();
	}

	public SampleSearchRequestDTO(final String sampleDbIds, final String observationUnitDbIds, final String plateDbIds,
		final String germplasmDbIds, final String studyDbIds, final String externalReferenceIDs, final String externalReferenceSources,
		final String germplasmNames, final String studyNames) {
		this.sampleDbIds = StringUtils.isEmpty(sampleDbIds) ? Lists.newArrayList() : Lists.newArrayList(sampleDbIds);
		this.observationUnitDbIds =
			StringUtils.isEmpty(observationUnitDbIds) ? Lists.newArrayList() : Lists.newArrayList(observationUnitDbIds);
		this.plateDbIds = StringUtils.isEmpty(plateDbIds) ? Lists.newArrayList() : Lists.newArrayList(plateDbIds);
		this.germplasmDbIds = StringUtils.isEmpty(germplasmDbIds) ? Lists.newArrayList() : Lists.newArrayList(germplasmDbIds);
		this.studyDbIds = StringUtils.isEmpty(studyDbIds) ? Lists.newArrayList() : Lists.newArrayList(studyDbIds);
		this.externalReferenceIDs =
			StringUtils.isEmpty(externalReferenceIDs) ? Lists.newArrayList() : Lists.newArrayList(externalReferenceIDs);
		this.externalReferenceSources =
			StringUtils.isEmpty(externalReferenceSources) ? Lists.newArrayList() : Lists.newArrayList(externalReferenceSources);
		this.germplasmNames = StringUtils.isEmpty(germplasmNames) ? Lists.newArrayList() : Lists.newArrayList(germplasmNames);
		this.studyNames = StringUtils.isEmpty(studyNames) ? Lists.newArrayList() : Lists.newArrayList(studyNames);
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
