package org.generationcp.middleware.service.api.phenotype;

import java.io.Serializable;
import java.util.List;

public class PhenotypeSearchRequestDTO implements Serializable {

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

	public String getObservationLevel() {
		return observationLevel;
	}

	public void setObservationLevel(final String observationLevel) {
		this.observationLevel = observationLevel;
	}

	public List<String> getObservationVariableDbIds() {
		return observationVariableDbIds;
	}

	public void setObservationVariableDbIds(List<String> observationVariableDbIds) {
		this.observationVariableDbIds = observationVariableDbIds;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(final Integer page) {
		this.page = page;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(final Integer pageSize) {
		this.pageSize = pageSize;
	}

	public List<String> getStudyDbIds() {
		return studyDbIds;
	}

	public void setStudyDbIds(final List<String> studyDbIds) {
		this.studyDbIds = studyDbIds;
	}

	public List<String> getLocationDbIds() {
		return locationDbIds;
	}

	public void setLocationDbIds(final List<String> locationDbIds) {
		this.locationDbIds = locationDbIds;
	}

	public List<String> getGermplasmDbIds() {
		return germplasmDbIds;
	}

	public void setGermplasmDbIds(final List<String> germplasmDbIds) {
		this.germplasmDbIds = germplasmDbIds;
	}

	public String getObservationTimeStampRangeStart() {
		return observationTimeStampRangeStart;
	}

	public void setObservationTimeStampRangeStart(final String observationTimeStampRangeStart) {
		this.observationTimeStampRangeStart = observationTimeStampRangeStart;
	}

	public String getObservationTimeStampRangeEnd() {
		return observationTimeStampRangeEnd;
	}

	public void setObservationTimeStampRangeEnd(final String observationTimeStampRangeEnd) {
		this.observationTimeStampRangeEnd = observationTimeStampRangeEnd;
	}

	public List<String> getProgramDbIds() {
		return programDbIds;
	}

	public void setProgramDbIds(final List<String> programDbIds) {
		this.programDbIds = programDbIds;
	}

	public List<String> getTrialDbIds() {
		return trialDbIds;
	}

	public void setTrialDbIds(final List<String> trialDbIds) {
		this.trialDbIds = trialDbIds;
	}
}
