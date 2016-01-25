
package org.generationcp.middleware.pojos;

public class GermplasmListMetadata {

	private Long listId;
	private Long numberOfEntries;
	private String ownerName;

	public GermplasmListMetadata() {

	}

	public GermplasmListMetadata(final Long listId, final Long numberOfEntries, final String ownerName) {
		this.listId = listId;
		this.numberOfEntries = numberOfEntries;
		this.ownerName = ownerName;
	}

	public Long getListId() {
		return this.listId;
	}

	public void setListId(Long listId) {
		this.listId = listId;
	}

	public Long getNumberOfEntries() {
		return this.numberOfEntries;
	}

	public void setNumberOfEntries(final Long numberOfEntries) {
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
