package org.generationcp.middleware.pojos;


/**
 * Container for the list meta data.
 *
 */
public class ListMetadata {

	private Integer listId;
	private Integer numberOfChildren;
	private Integer numberOfEntries;

	public ListMetadata(final Integer listId, final Integer numberOfChildren, final Integer numberOfEntries) {
		this.listId = listId;
		this.numberOfChildren = numberOfChildren;
		this.numberOfEntries = numberOfEntries;
	}

	public ListMetadata() {
	}

	public Integer getListId() {
		return listId;
	}
	
	public void setListId(Integer listId) {
		this.listId = listId;
	}
	
	public Integer getNumberOfChildren() {
		return numberOfChildren;
	}
	
	public void setNumberOfChildren(Integer numberOfChildren) {
		this.numberOfChildren = numberOfChildren;
	}

	public Integer getNumberOfEntries() {
		return numberOfEntries;
	}

	public void setNumberOfEntries(final Integer numberOfEntries) {
		this.numberOfEntries = numberOfEntries;
	}
}
