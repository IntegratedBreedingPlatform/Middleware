package org.generationcp.middleware.api.germplasmlist.search;

import org.generationcp.middleware.api.germplasmlist.GermplasmListDto;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class GermplasmListSearchResponse extends GermplasmListDto {

	private String parentFolderName;
	private String description;
	private String listOwner;
	private String listType;
	private Integer numberOfEntries;
	private String notes;

	public String getParentFolderName() {
		return parentFolderName;
	}

	public void setParentFolderName(final String parentFolderName) {
		this.parentFolderName = parentFolderName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getListOwner() {
		return listOwner;
	}

	public void setListOwner(final String listOwner) {
		this.listOwner = listOwner;
	}

	public String getListType() {
		return listType;
	}

	public void setListType(final String listType) {
		this.listType = listType;
	}

	public Integer getNumberOfEntries() {
		return numberOfEntries;
	}

	public void setNumberOfEntries(final Integer numberOfEntries) {
		this.numberOfEntries = numberOfEntries;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(final String notes) {
		this.notes = notes;
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
