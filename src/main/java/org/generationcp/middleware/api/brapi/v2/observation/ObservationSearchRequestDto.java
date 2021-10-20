package org.generationcp.middleware.api.brapi.v2.observation;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;

@AutoProperty
public class ObservationSearchRequestDto {

	private List<Integer> observationDbIds;
	private List<String> observationUnitDbIds;
	private List<String> germplasmDbIds;
	private List<Integer> observationVariableDbIds;
	private List<Integer> studyDbIds;
	private List<Integer> locationDbIds;
	private List<Integer> trialDbIds;
	private List<String> programDbIds;
	private String seasonDbId;
	private String observationUnitLevelName;
	private String observationUnitLevelOrder;
	private String observationUnitLevelCode;
	private String observationTimeStampRangeStart;
	private String observationTimeStampRangeEnd;
	private String externalReferenceID;
	private String externalReferenceSource;

	public List<Integer> getObservationDbIds() {
		return this.observationDbIds;
	}

	public void setObservationDbIds(final List<Integer> observationDbIds) {
		this.observationDbIds = observationDbIds;
	}

	public List<String> getObservationUnitDbIds() {
		return this.observationUnitDbIds;
	}

	public void setObservationUnitDbIds(final List<String> observationUnitDbIds) {
		this.observationUnitDbIds = observationUnitDbIds;
	}

	public List<String> getGermplasmDbIds() {
		return this.germplasmDbIds;
	}

	public void setGermplasmDbIds(final List<String> germplasmDbIds) {
		this.germplasmDbIds = germplasmDbIds;
	}

	public List<Integer> getObservationVariableDbIds() {
		return this.observationVariableDbIds;
	}

	public void setObservationVariableDbIds(final List<Integer> observationVariableDbIds) {
		this.observationVariableDbIds = observationVariableDbIds;
	}

	public List<Integer> getStudyDbIds() {
		return this.studyDbIds;
	}

	public void setStudyDbIds(final List<Integer> studyDbIds) {
		this.studyDbIds = studyDbIds;
	}

	public List<Integer> getLocationDbIds() {
		return this.locationDbIds;
	}

	public void setLocationDbIds(final List<Integer> locationDbIds) {
		this.locationDbIds = locationDbIds;
	}

	public List<Integer> getTrialDbIds() {
		return this.trialDbIds;
	}

	public void setTrialDbIds(final List<Integer> trialDbIds) {
		this.trialDbIds = trialDbIds;
	}

	public List<String> getProgramDbIds() {
		return this.programDbIds;
	}

	public void setProgramDbIds(final List<String> programDbIds) {
		this.programDbIds = programDbIds;
	}

	public String getSeasonDbId() {
		return this.seasonDbId;
	}

	public void setSeasonDbId(final String seasonDbId) {
		this.seasonDbId = seasonDbId;
	}

	public String getObservationUnitLevelName() {
		return this.observationUnitLevelName;
	}

	public void setObservationUnitLevelName(final String observationUnitLevelName) {
		this.observationUnitLevelName = observationUnitLevelName;
	}

	public String getObservationUnitLevelOrder() {
		return this.observationUnitLevelOrder;
	}

	public void setObservationUnitLevelOrder(final String observationUnitLevelOrder) {
		this.observationUnitLevelOrder = observationUnitLevelOrder;
	}

	public String getObservationUnitLevelCode() {
		return this.observationUnitLevelCode;
	}

	public void setObservationUnitLevelCode(final String observationUnitLevelCode) {
		this.observationUnitLevelCode = observationUnitLevelCode;
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

	public String getExternalReferenceID() {
		return this.externalReferenceID;
	}

	public void setExternalReferenceID(final String externalReferenceID) {
		this.externalReferenceID = externalReferenceID;
	}

	public String getExternalReferenceSource() {
		return this.externalReferenceSource;
	}

	public void setExternalReferenceSource(final String externalReferenceSource) {
		this.externalReferenceSource = externalReferenceSource;
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
