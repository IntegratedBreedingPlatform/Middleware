package org.generationcp.middleware.domain.study;

import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.olap4j.impl.ArrayMap;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AutoProperty
public class StudyEntrySearchDto {

	private int studyId;

	//In the near future the fixed descriptors will be removed
	private List<MeasurementVariable> fixedEntryDescriptors;

	private List<MeasurementVariable> variableEntryDescriptors;

	private Filter filter;

	public StudyEntrySearchDto() {
	}

	public StudyEntrySearchDto(final int studyId, final List<MeasurementVariable> fixedEntryDescriptors,
		final List<MeasurementVariable> variableEntryDescriptors, final Filter filter) {
		this.studyId = studyId;
		this.fixedEntryDescriptors = fixedEntryDescriptors;
		this.variableEntryDescriptors = variableEntryDescriptors;
		this.filter = filter;
	}

	public static class Filter {

		private List<String> entryNumbers;
		private List<Integer> entryIds;
		private Map<String, List<String>> filteredValues;
		private Map<String, String> filteredTextValues;
		private Map<String, String> variableTypeMap;
		private Integer variableId;

		public Filter() {
			this.entryNumbers = new ArrayList<>();
			this.entryIds = new ArrayList<>();
			this.filteredValues = new ArrayMap<>();
			this.filteredTextValues = new HashMap<>();
			this.variableId = null;
		}

		public List<String> getEntryNumbers() {
			return entryNumbers;
		}

		public void setEntryNumbers(final List<String> entryNumbers) {
			this.entryNumbers = entryNumbers;
		}

		public List<Integer> getEntryIds() {
			return entryIds;
		}

		public void setEntryIds(final List<Integer> entryIds) {
			this.entryIds = entryIds;
		}

		public Map<String, List<String>> getFilteredValues() {
			return filteredValues;
		}

		public void setFilteredValues(final Map<String, List<String>> filteredValues) {
			this.filteredValues = filteredValues;
		}

		public Map<String, String> getFilteredTextValues() {
			return filteredTextValues;
		}

		public void setFilteredTextValues(final Map<String, String> filteredTextValues) {
			this.filteredTextValues = filteredTextValues;
		}

		public Map<String, String> getVariableTypeMap() {
			return variableTypeMap;
		}

		public void setVariableTypeMap(final Map<String, String> variableTypeMap) {
			this.variableTypeMap = variableTypeMap;
		}

		public Integer getVariableId() {
			return variableId;
		}

		public void setVariableId(final Integer variableId) {
			this.variableId = variableId;
		}
	}

	public int getStudyId() {
		return studyId;
	}

	public void setStudyId(final int studyId) {
		this.studyId = studyId;
	}

	public List<MeasurementVariable> getFixedEntryDescriptors() {
		return fixedEntryDescriptors;
	}

	public void setFixedEntryDescriptors(final List<MeasurementVariable> fixedEntryDescriptors) {
		this.fixedEntryDescriptors = fixedEntryDescriptors;
	}

	public List<MeasurementVariable> getVariableEntryDescriptors() {
		return variableEntryDescriptors;
	}

	public void setVariableEntryDescriptors(final List<MeasurementVariable> variableEntryDescriptors) {
		this.variableEntryDescriptors = variableEntryDescriptors;
	}

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(final Filter filter) {
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

}
