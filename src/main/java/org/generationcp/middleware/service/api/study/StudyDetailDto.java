package org.generationcp.middleware.service.api.study;

import java.util.List;

public class StudyDetailDto {
	
    private Integer studyDbId;

    private List<Integer> observationVariableDbId;
    
    private List<String> observationVariableName;
    
    private List<List<String>> data;

	public StudyDetailDto(Integer studyDbId, List<Integer> observationVariableDbId, List<String> observationVariableName,
			List<List<String>> data) {
		super();
		this.studyDbId = studyDbId;
		this.observationVariableDbId = observationVariableDbId;
		this.observationVariableName = observationVariableName;
		this.data = data;
	}

	
	public Integer getStudyDbId() {
		return studyDbId;
	}

	
	public void setStudyDbId(Integer studyDbId) {
		this.studyDbId = studyDbId;
	}

	
	public List<Integer> getObservationVariableDbId() {
		return observationVariableDbId;
	}

	
	public void setObservationVariableDbId(List<Integer> observationVariableDbId) {
		this.observationVariableDbId = observationVariableDbId;
	}

	
	public List<String> getObservationVariableName() {
		return observationVariableName;
	}

	
	public void setObservationVariableName(List<String> observationVariableName) {
		this.observationVariableName = observationVariableName;
	}

	
	public List<List<String>> getData() {
		return data;
	}

	
	public void setData(List<List<String>> data) {
		this.data = data;
	}
    
}
