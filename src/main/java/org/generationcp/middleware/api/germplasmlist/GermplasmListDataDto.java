package org.generationcp.middleware.api.germplasmlist;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class GermplasmListDataDto {

	private Integer listDataId;

	private Integer listId;

	private Integer entryNumber;

	private Integer gid;

	public GermplasmListDataDto(final Integer listDataId, final Integer listId, final Integer entryNumber, final Integer gid) {
		this.listDataId = listDataId;
		this.listId = listId;
		this.entryNumber = entryNumber;
		this.gid = gid;
	}

	public Integer getListId() {
		return listId;
	}

	public void setListId(final Integer listId) {
		this.listId = listId;
	}

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

	public Integer getGid() {
		return gid;
	}

	public void setGid(final Integer gid) {
		this.gid = gid;
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
