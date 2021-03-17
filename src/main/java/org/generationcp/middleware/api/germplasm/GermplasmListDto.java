package org.generationcp.middleware.api.germplasm;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class GermplasmListDto {

	private Integer listId;

	private String listName;

	private String creationDate;

	private String description;

	public GermplasmListDto() {

	}

	public GermplasmListDto(final Integer listId, final String listName, final String creationDate, final String description) {
		this.listId = listId;
		this.listName = listName;
		this.creationDate = creationDate;
		this.description = description;
	}

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

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(final String creationDate) {
		this.creationDate = creationDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
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
