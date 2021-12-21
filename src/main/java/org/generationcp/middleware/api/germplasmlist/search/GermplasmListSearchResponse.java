package org.generationcp.middleware.api.germplasmlist.search;

import org.generationcp.middleware.api.germplasmlist.GermplasmListDto;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class GermplasmListSearchResponse extends GermplasmListDto {

	private String parentFolderName;
	private String listOwner;
	private Integer numberOfEntries;

	public String getParentFolderName() {
		return this.parentFolderName;
	}

	public void setParentFolderName(final String parentFolderName) {
		this.parentFolderName = parentFolderName;
	}

	public String getListOwner() {
		return this.listOwner;
	}

	public void setListOwner(final String listOwner) {
		this.listOwner = listOwner;
	}

	public Integer getNumberOfEntries() {
		return this.numberOfEntries;
	}

	public void setNumberOfEntries(final Integer numberOfEntries) {
		this.numberOfEntries = numberOfEntries;
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
