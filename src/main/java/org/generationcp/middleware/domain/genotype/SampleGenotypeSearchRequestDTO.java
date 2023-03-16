package org.generationcp.middleware.domain.genotype;

import org.generationcp.middleware.domain.search_request.SearchRequestDto;
import org.generationcp.middleware.service.api.study.MeasurementVariableDto;
import org.pojomatic.Pojomatic;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SampleGenotypeSearchRequestDTO extends SearchRequestDto {

	private int studyId;

	private List<MeasurementVariableDto> sampleGenotypeVariableDTOs;

	private List<MeasurementVariableDto> entryDetails;

	/**
	 * List of columns that will be included and retrieved in the query.
	 * If no columns are specified, the query will retrieve all columns by default.
	 */
	private Set<String> visibleColumns = new HashSet<>();

	/**
	 * This is used by DataTables to ensure that the Ajax returns from server-side processing requests are drawn in sequence
	 */
	private String draw;

	private GenotypeFilter filter;

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

	public List<MeasurementVariableDto> getSampleGenotypeVariableDTOs() {
		return sampleGenotypeVariableDTOs;
	}

	public void setSampleGenotypeVariableDTOs(List<MeasurementVariableDto> sampleGenotypeVariableDTOs) {
		this.sampleGenotypeVariableDTOs = sampleGenotypeVariableDTOs;
	}

	public List<MeasurementVariableDto> getEntryDetails() {
		return entryDetails;
	}

	public void setEntryDetails(List<MeasurementVariableDto> entryDetails) {
		this.entryDetails = entryDetails;
	}

	public Set<String> getVisibleColumns() {
		return visibleColumns;
	}

	public void setVisibleColumns(Set<String> visibleColumns) {
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

	public static class GenotypeFilter {

		private Integer datasetId;
		private List<Integer> instanceIds;

		private List<Integer> sampleIds;

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
			return instanceIds;
		}

		public void setInstanceIds(List<Integer> instanceIds) {
			this.instanceIds = instanceIds;
		}

		public Map<String, List<String>> getFilteredValues() {
			return filteredValues;
		}

		public void setFilteredValues(Map<String, List<String>> filteredValues) {
			this.filteredValues = filteredValues;
		}

		public Map<String, String> getFilteredTextValues() {
			return filteredTextValues;
		}

		public void setFilteredTextValues(Map<String, String> filteredTextValues) {
			this.filteredTextValues = filteredTextValues;
		}

		public Map<String, String> getVariableTypeMap() {
			return variableTypeMap;
		}

		public void setVariableTypeMap(Map<String, String> variableTypeMap) {
			this.variableTypeMap = variableTypeMap;
		}

		public List<Integer> getSampleIds() {
			return this.sampleIds;
		}

		public void setSampleIds(final List<Integer> sampleIds) {
			this.sampleIds = sampleIds;
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
