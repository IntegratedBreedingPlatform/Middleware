package org.generationcp.middleware.service.api.study;

import java.util.List;

public class StudyDetailDto {
	
    private Integer studyDbId;

	private List<Integer> observationVariableDbIds;
    
	private List<String> observationVariableNames;
    
	private List<String> headerRow;

    private List<List<String>> data;

	public Integer getStudyDbId() {
		return studyDbId;
	}

	public StudyDetailDto setStudyDbId(final Integer studyDbId) {
		this.studyDbId = studyDbId;
		return this;
	}

	public List<Integer> getObservationVariableDbIds() {
		return observationVariableDbIds;
	}

	public StudyDetailDto setObservationVariableDbIds(List<Integer> observationVariableDbIds) {
		this.observationVariableDbIds = observationVariableDbIds;
		return this;
	}

	public List<String> getObservationVariableNames() {
		return observationVariableNames;
	}

	public StudyDetailDto setObservationVariableNames(List<String> observationVariableNames) {
		this.observationVariableNames = observationVariableNames;
		return this;
	}

	public List<String> getHeaderRow() {
		return headerRow;
	}

	public void setHeaderRow(List<String> headerRow) {
		this.headerRow = headerRow;
	}

	public List<List<String>> getData() {
		return data;
	}

	public StudyDetailDto setData(final List<List<String>> data) {
		this.data = data;
		return this;
	}

}
