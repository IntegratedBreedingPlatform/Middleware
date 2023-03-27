package org.generationcp.middleware.domain.genotype;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;

@AutoProperty
public class SampleGenotypeVariablesSearchFilter {

	private Integer studyId;
	private List<Integer> datasetIds;
	private List<Integer> sampleListIds;

	public Integer getStudyId() {
		return this.studyId;
	}

	public void setStudyId(final Integer studyId) {
		this.studyId = studyId;
	}

	public List<Integer> getDatasetIds() {
		return this.datasetIds;
	}

	public void setDatasetIds(final List<Integer> datasetIds) {
		this.datasetIds = datasetIds;
	}

	public List<Integer> getSampleListIds() {
		return this.sampleListIds;
	}

	public void setSampleListIds(final List<Integer> sampleListIds) {
		this.sampleListIds = sampleListIds;
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
