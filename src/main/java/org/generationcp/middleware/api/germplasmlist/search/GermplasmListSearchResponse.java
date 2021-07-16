package org.generationcp.middleware.api.germplasmlist.search;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Date;

@AutoProperty
public class GermplasmListSearchResponse {

	private Integer listId;
	private String listName;
	private String parentFolderName;
	private String description;
	private String listOwner;
	private String listType;
	private Integer numberOfEntries;
	private String status;
	private String notes;
	private Date listDate;

	public Integer getListId() {
		return listId;
	}

	public void setListId(final Integer listId) {
		this.listId = listId;
	}

	public String getListName() {
		return listName;
	}

	public void setListName(final String listName) {
		this.listName = listName;
	}

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

	public String getStatus() {
		return status;
	}

	public void setStatus(final String status) {
		this.status = status;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(final String notes) {
		this.notes = notes;
	}

	public Date getListDate() {
		return listDate;
	}

	public void setListDate(final Date listDate) {
		this.listDate = listDate;
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
