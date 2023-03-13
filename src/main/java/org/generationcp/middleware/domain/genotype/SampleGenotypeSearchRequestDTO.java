package org.generationcp.middleware.domain.genotype;

import org.generationcp.middleware.domain.search_request.SearchRequestDto;
import org.pojomatic.Pojomatic;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SampleGenotypeSearchRequestDTO extends SearchRequestDto {

	private int studyId;

	private GenotypeFilter filter;

	public int getStudyId() {
		return this.studyId;
	}

	public void setStudyId(final int studyId) {
		this.studyId = studyId;
	}

	public GenotypeFilter getFilter() {
		return this.filter;
	}

	public void setFilter(final GenotypeFilter filter) {
		this.filter = filter;
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

	public static class GenotypeFilter {

		public static final String GID = "gid";
		public static final String DESIGNATION = "designation";
		public static final String PLOT_NO = "plotNumber";
		public static final String SAMPLE_NO = "sampleNumber";
		public static final String SAMPLE_NAME = "sampleName";
		public static final String VARIABLE_ID = "variableId";
		public static final String VARIABLE_NAME = "variableName";
		public static final String VALUE = "value";

		public static final List<String> SORTABLE_FIELDS = Collections.unmodifiableList(Arrays
			.asList(GID, DESIGNATION, PLOT_NO, SAMPLE_NO, SAMPLE_NAME, VARIABLE_NAME, VALUE));

		private Integer datasetId;

		private List<Integer> instanceIds;

		private List<Integer> gidList;

		private String designation;

		private List<Integer> plotNumberList;

		private List<Integer> sampleNumberList;

		private String sampleName;

		private Map<Integer, String> variableMap;

		public List<Integer> getGidList() {
			return this.gidList;
		}

		public void setGidList(final List<Integer> gidList) {
			this.gidList = gidList;
		}

		public String getDesignation() {
			return this.designation;
		}

		public void setDesignation(final String designation) {
			this.designation = designation;
		}

		public List<Integer> getPlotNumberList() {
			return this.plotNumberList;
		}

		public void setPlotNumberList(final List<Integer> plotNumberList) {
			this.plotNumberList = plotNumberList;
		}

		public List<Integer> getSampleNumberList() {
			return this.sampleNumberList;
		}

		public void setSampleNumberList(final List<Integer> sampleNumberList) {
			this.sampleNumberList = sampleNumberList;
		}

		public String getSampleName() {
			return this.sampleName;
		}

		public void setSampleName(final String sampleName) {
			this.sampleName = sampleName;
		}

		public Map<Integer, String> getVariableMap() {
			return this.variableMap;
		}

		public void setVariableMap(final Map<Integer, String> variableMap) {
			this.variableMap = variableMap;
		}

		public Integer getDatasetId() {
			return this.datasetId;
		}

		public void setDatasetId(final Integer datasetId) {
			this.datasetId = datasetId;
		}

		public List<Integer> getInstanceIds() {
			return this.instanceIds;
		}

		public void setInstanceIds(final List<Integer> instanceIds) {
			this.instanceIds = instanceIds;
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
}
