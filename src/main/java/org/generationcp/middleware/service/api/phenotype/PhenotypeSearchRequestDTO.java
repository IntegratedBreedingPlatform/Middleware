package org.generationcp.middleware.service.api.phenotype;

import java.io.Serializable;
import java.util.List;

public class PhenotypeSearchRequestDTO implements Serializable {

	private List<Integer> cvTermIds;

	private Integer studyDbId;

	public Integer getStudyDbId() {
		return studyDbId;
	}

	public void setStudyDbId(final Integer studyDbId) {
		this.studyDbId = studyDbId;
	}

	public List<Integer> getCvTermIds() {
		return cvTermIds;
	}

	public void setCvTermIds(List<Integer> cvTermIds) {
		this.cvTermIds = cvTermIds;
	}
}
