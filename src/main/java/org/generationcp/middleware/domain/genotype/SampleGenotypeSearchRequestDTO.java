package org.generationcp.middleware.domain.genotype;

import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.search_request.SearchRequestDto;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@AutoProperty
public class SampleGenotypeSearchRequestDTO extends SearchRequestDto {

	private int studyId;

	private List<MeasurementVariable> sampleGenotypeVariables;

	private List<Integer> takenByIds;

	/**
	 * List of columns that will be included and retrieved in the query.
	 * If no columns are specified, the query will retrieve all columns by default.
	 */
	private Set<String> visibleColumns = new HashSet<>();

	/**
	 * This is used by DataTables to ensure that the Ajax returns from server-side processing requests are drawn in sequence
	 */
	private String draw;

	private GenotypeFilter filter = new GenotypeFilter();

	public int getStudyId() {
		return this.studyId;
	}

	public void setStudyId(final int studyId) {
		this.studyId = studyId;
	}

	public String getDraw() {
		return this.draw;
	}

	public void setDraw(final String draw) {
		this.draw = draw;
	}

	public GenotypeFilter getFilter() {
		return this.filter;
	}

	public void setFilter(final GenotypeFilter filter) {
		this.filter = filter;
	}

	public List<MeasurementVariable> getSampleGenotypeVariables() {
		return this.sampleGenotypeVariables;
	}

	public void setSampleGenotypeVariables(final List<MeasurementVariable> sampleGenotypeVariables) {
		this.sampleGenotypeVariables = sampleGenotypeVariables;
	}

	public List<Integer> getTakenByIds() {
		return this.takenByIds;
	}

	public void setTakenByIds(final List<Integer> takenByIds) {
		this.takenByIds = takenByIds;
	}

	public Set<String> getVisibleColumns() {
		return this.visibleColumns;
	}

	public void setVisibleColumns(final Set<String> visibleColumns) {
		this.visibleColumns = visibleColumns;
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

	@AutoProperty
	public static class GenotypeFilter {

		private Integer datasetId;
		private List<Integer> instanceIds;
		private List<Integer> sampleIds;
		private List<Integer> sampleListIds;

		private Map<String, List<String>> filteredValues;
		private Map<String, String> filteredTextValues;
		private Map<String, String> variableTypeMap;

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

		public Map<String, List<String>> getFilteredValues() {
			return this.filteredValues;
		}

		public void setFilteredValues(final Map<String, List<String>> filteredValues) {
			this.filteredValues = filteredValues;
		}

		public Map<String, String> getFilteredTextValues() {
			return this.filteredTextValues;
		}

		public void setFilteredTextValues(final Map<String, String> filteredTextValues) {
			this.filteredTextValues = filteredTextValues;
		}

		public Map<String, String> getVariableTypeMap() {
			return this.variableTypeMap;
		}

		public void setVariableTypeMap(final Map<String, String> variableTypeMap) {
			this.variableTypeMap = variableTypeMap;
		}

		public List<Integer> getSampleIds() {
			return this.sampleIds;
		}

		public void setSampleIds(final List<Integer> sampleIds) {
			this.sampleIds = sampleIds;
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
}
