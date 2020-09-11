package org.generationcp.middleware.domain.study;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;

@AutoProperty
public class StudyEntrySearchDto {

	private List<String> entryNumbers;

	private List<Integer> entryIds;

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
