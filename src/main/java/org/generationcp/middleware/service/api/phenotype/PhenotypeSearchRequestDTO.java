package org.generationcp.middleware.service.api.phenotype;

import java.io.Serializable;
import java.util.List;

public class PhenotypeSearchRequestDTO implements Serializable {

	private List<Integer> cvTermIds;
	private List<String> studyDbIds;

	public List<String> getStudyDbIds() {
		return studyDbIds;
	}

	public void setStudyDbIds(final List<String> studyDbIds) {
		this.studyDbIds = studyDbIds;
	}


	public List<Integer> getCvTermIds() {
		return cvTermIds;
	}

	public void setCvTermIds(List<Integer> cvTermIds) {
		this.cvTermIds = cvTermIds;
	}
}
