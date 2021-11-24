package org.generationcp.middleware.api.germplasmlist;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Date;

@AutoProperty
public class GermplasmListDto {

	private Integer listId;
	private String listName;
	private Date creationDate;
	private String description;
	private String programUUID;
	private boolean locked;
	private Integer ownerId;
	private String notes;
	private String type;

	public GermplasmListDto() {

	}

	public Integer getListId() {
		return this.listId;
	}

	public void setListId(final Integer listId) {
		this.listId = listId;
	}

	public String getListName() {
		return this.listName;
	}

	public void setListName(final String listName) {
		this.listName = listName;
	}

	public Date getCreationDate() {
		return this.creationDate;
	}

	public void setCreationDate(final Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getProgramUUID() {
		return this.programUUID;
	}

	public void setProgramUUID(final String programUUID) {
		this.programUUID = programUUID;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(final boolean locked) {
		this.locked = locked;
	}

	public Integer getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(final Integer ownerId) {
		this.ownerId = ownerId;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(final String notes) {
		this.notes = notes;
	}

	public String getType() {
		return type;
	}

	public void setType(final String type) {
		this.type = type;
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
