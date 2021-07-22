package org.generationcp.middleware.api.germplasmlist.search;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class GermplasmListDataSearchResponse {

	private Integer listDataId;
	private Integer entryNumber;

	public Integer getListDataId() {
		return listDataId;
	}

	public void setListDataId(final Integer listDataId) {
		this.listDataId = listDataId;
	}

	public Integer getEntryNumber() {
		return entryNumber;
	}

	public void setEntryNumber(final Integer entryNumber) {
		this.entryNumber = entryNumber;
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
