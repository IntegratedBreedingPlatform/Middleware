package org.generationcp.middleware.api.germplasmlist.data;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;

@AutoProperty
public class GermplasmListReorderEntriesRequest {

	private List<Integer> selectedEntries;
	private Integer entryNumberPosition;
	private Boolean endOfList;

	public List<Integer> getSelectedEntries() {
		return selectedEntries;
	}

	public void setSelectedEntries(final List<Integer> selectedEntries) {
		this.selectedEntries = selectedEntries;
	}

	public Integer getEntryNumberPosition() {
		return entryNumberPosition;
	}

	public void setEntryNumberPosition(final Integer entryNumberPosition) {
		this.entryNumberPosition = entryNumberPosition;
	}

	public Boolean getEndOfList() {
		return endOfList;
	}

	public void setEndOfList(final Boolean endOfList) {
		this.endOfList = endOfList;
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
