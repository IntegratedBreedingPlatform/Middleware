
package org.generationcp.middleware.pojos;

public class GermplasmListMetadata {

	private Integer listId;
	private Integer numberOfEntries;
	private String ownerName;

	public GermplasmListMetadata() {

	}

	public GermplasmListMetadata(final Integer listId, final Integer numberOfEntries, final String ownerName) {
		this.listId = listId;
		this.numberOfEntries = numberOfEntries;
		this.ownerName = ownerName;
	}

	public Integer getListId() {
		return this.listId;
	}

	public void setListId(Integer listId) {
		this.listId = listId;
	}

	public Integer getNumberOfEntries() {
		return this.numberOfEntries;
	}

	public void setNumberOfEntries(final Integer numberOfEntries) {
		this.numberOfEntries = numberOfEntries;
	}

	public String getOwnerName() {
		return this.ownerName;
	}

	public void setOwnerName(final String ownerName) {
		this.ownerName = ownerName;
	}

	@Override
	public String toString() {
		return "GermplasmListMetadata [listId=" + listId + ", numberOfEntries=" + numberOfEntries + ", ownerName=" + ownerName + "]";
	}
}
