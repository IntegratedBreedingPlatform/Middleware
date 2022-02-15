package org.generationcp.middleware.api.germplasmlist;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public abstract class GermplasmListBasicInfoDTO {

	private Integer listId;
	private String listName;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date creationDate;
	private String description;
	private String programUUID;
	private String listType;
	private String notes;
	private Integer status;
	private String parentFolderId;

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

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(final Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getProgramUUID() {
		return programUUID;
	}

	public void setProgramUUID(final String programUUID) {
		this.programUUID = programUUID;
	}

	public String getListType() {
		return listType;
	}

	public void setListType(final String listType) {
		this.listType = listType;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(final String notes) {
		this.notes = notes;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(final Integer status) {
		this.status = status;
	}

	public String getParentFolderId() {
		return parentFolderId;
	}

	public void setParentFolderId(final String parentFolderId) {
		this.parentFolderId = parentFolderId;
	}
}
