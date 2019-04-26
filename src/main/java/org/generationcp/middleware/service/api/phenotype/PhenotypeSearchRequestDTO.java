package org.generationcp.middleware.service.api.phenotype;

import java.io.Serializable;
import java.util.List;

public class PhenotypeSearchRequestDTO implements Serializable {

	private String observationLevel;
	private List<String> observationVariableDbIds;
	private List<String> locationDbIds;
	private Integer page;
	private Integer pageSize;
	private List<String> studyDbIds;

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
}
