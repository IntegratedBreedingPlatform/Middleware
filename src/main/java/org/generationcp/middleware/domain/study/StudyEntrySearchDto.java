package org.generationcp.middleware.domain.study;

import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.ArrayList;
import java.util.List;

@AutoProperty
public class StudyEntrySearchDto {

	private int studyId;

	private List<MeasurementVariable> entryDescriptors;

	private Filter filter;

	public StudyEntrySearchDto() {
	}

	public StudyEntrySearchDto(final int studyId, final List<MeasurementVariable> entryDescriptors,
		final Filter filter) {
		this.studyId = studyId;
		this.entryDescriptors = entryDescriptors;
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

	public List<MeasurementVariable> getEntryDescriptors() {
		return entryDescriptors;
	}

	public void setEntryDescriptors(final List<MeasurementVariable> entryDescriptors) {
		this.entryDescriptors = entryDescriptors;
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
