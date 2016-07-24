package org.generationcp.middleware.service.api.study;

import java.util.List;

public class StudyDetailDto {
	
    private Integer studyDbId;

    private List<Integer> observationVariableDbId;
    
    private List<String> observationVariableName;
    
    private List<List<String>> data;

	public Integer getStudyDbId() {
		return studyDbId;
	}

	public StudyDetailDto setStudyDbId(final Integer studyDbId) {
		this.studyDbId = studyDbId;
		return this;
	}

	public List<Integer> getObservationVariableDbId() {
		return observationVariableDbId;
	}

	public StudyDetailDto setObservationVariableDbId(final List<Integer> observationVariableDbId) {
		this.observationVariableDbId = observationVariableDbId;
		return this;
	}

	public List<String> getObservationVariableName() {
		return observationVariableName;
	}

	public StudyDetailDto setObservationVariableName(final List<String> observationVariableName) {
		this.observationVariableName = observationVariableName;
		return this;
	}

	public List<List<String>> getData() {
		return data;
	}

	public StudyDetailDto setData(final List<List<String>> data) {
		this.data = data;
		return this;
	}

}
