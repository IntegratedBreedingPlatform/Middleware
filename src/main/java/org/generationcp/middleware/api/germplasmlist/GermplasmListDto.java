package org.generationcp.middleware.api.germplasmlist;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.generationcp.middleware.pojos.GermplasmList;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Date;

@AutoProperty
public class GermplasmListDto {

	private Integer listId;
	private String listName;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date creationDate;
	private String description;
	private String programUUID;
	private Boolean locked;
	private Integer ownerId;
	private String listType;
	private String notes;
	private String parentFolderId;
	private Integer status;

	public GermplasmListDto() {

	}

	public GermplasmListDto(final GermplasmListGeneratorDTO germplasmListGeneratorDTO) {
		this.setListName(germplasmListGeneratorDTO.getName());
		this.setDescription(germplasmListGeneratorDTO.getDescription());
		this.setListType(germplasmListGeneratorDTO.getType());
		this.setCreationDate(germplasmListGeneratorDTO.getDate());
		this.setNotes(germplasmListGeneratorDTO.getNotes());
		this.setParentFolderId(germplasmListGeneratorDTO.getParentFolderId());
		this.setProgramUUID(germplasmListGeneratorDTO.getProgramUUID());
		this.setStatus(germplasmListGeneratorDTO.getStatus());
	}

	public GermplasmListDto(final GermplasmList list) {
		this.listId = list.getId();
		this.listName = list.getName();
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

	public Boolean isLocked() {
		return this.locked;
	}

	public void setLocked(final Boolean locked) {
		this.locked = locked;
	}

	public Integer getOwnerId() {
		return this.ownerId;
	}

	public void setOwnerId(final Integer ownerId) {
		this.ownerId = ownerId;
	}

	public String getListType() {
		return this.listType;
	}

	public void setListType(final String listType) {
		this.listType = listType;
	}

	public String getNotes() {
		return this.notes;
	}

	public void setNotes(final String notes) {
		this.notes = notes;
	}

	public String getParentFolderId() {
		return this.parentFolderId;
	}

	public void setParentFolderId(final String parentFolderId) {
		this.parentFolderId = parentFolderId;
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(final Integer status) {
		this.status = status;
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
