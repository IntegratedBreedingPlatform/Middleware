package org.generationcp.middleware.service.api.phenotype;

import java.io.Serializable;
import java.util.List;

public class PhenotypeSearchRequestDTO implements Serializable {

	private List<Integer> cvTermIds;
	private List<String> studyDbIds;
	private Integer page;
	private Integer pageSize;

	public List<String> getStudyDbIds() {
		return studyDbIds;
	}

	public void setStudyDbIds(final List<String> studyDbIds) {
		this.studyDbIds = studyDbIds;
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

	public List<Integer> getCvTermIds() {
		return cvTermIds;
	}

	public void setCvTermIds(List<Integer> cvTermIds) {
		this.cvTermIds = cvTermIds;
	}
}
