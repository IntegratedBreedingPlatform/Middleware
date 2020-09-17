package org.generationcp.middleware.domain.study;

import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.ArrayList;
import java.util.List;

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

	public class Filter {
		private List<String> entryNumbers;

		private List<Integer> entryIds;

		public Filter() {
			this.entryNumbers = new ArrayList<>();
			this.entryIds = new ArrayList<>();
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
