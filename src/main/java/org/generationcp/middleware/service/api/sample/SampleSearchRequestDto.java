package org.generationcp.middleware.service.api.sample;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.io.Serializable;
import java.util.List;

@AutoProperty
public class SampleSearchRequestDto implements Serializable {

	private List<String> germplasmDbId;
	private List<String> observationUnitDbId;
	private List<String> plateDbId;
	private List<String> sampleDbId;
	private String studyDbId;
	private Integer page;
	private Integer pageSize;

	public String getStudyDbId() {
		return studyDbId;
	}

	public void setStudyDbId(final String studyDbId) {
		this.studyDbId = studyDbId;
	}

	public List<String> getGermplasmDbId() {
		return germplasmDbId;
	}

	public void setGermplasmDbId(final List<String> germplasmDbId) {
		this.germplasmDbId = germplasmDbId;
	}

	public List<String> getObservationUnitDbId() {
		return observationUnitDbId;
	}

	public void setObservationUnitDbId(final List<String> observationUnitDbId) {
		this.observationUnitDbId = observationUnitDbId;
	}

	public List<String> getPlateDbId() {
		return plateDbId;
	}

	public void setPlateDbId(final List<String> plateDbId) {
		this.plateDbId = plateDbId;
	}

	public List<String> getSampleDbId() {
		return sampleDbId;
	}

	public void setSampleDbId(final List<String> sampleDbId) {
		this.sampleDbId = sampleDbId;
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
