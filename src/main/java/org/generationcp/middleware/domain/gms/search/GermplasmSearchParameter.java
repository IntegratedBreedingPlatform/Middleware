package org.generationcp.middleware.domain.gms.search;

import org.generationcp.middleware.manager.Operation;

/*
 * this class stores the parameters used in germplasm search
 */
public class GermplasmSearchParameter {

	private String searchKeyword;

	private Operation operation;

	private boolean includeParents;

	private boolean withInventoryOnly;

	private boolean includeMGMembers;

	private int startingRow;

	private int numberOfEntries;

	public GermplasmSearchParameter(final String searchKeyword, final Operation operation) {
		this.searchKeyword = searchKeyword;
		this.operation = operation;
	}

	public String getSearchKeyword() {
		return this.searchKeyword;
	}

	public void setSearchKeyword(final String searchKeyword) {
		this.searchKeyword = searchKeyword;
	}

	public Operation getOperation() {
		return this.operation;
	}

	public void setOperation(final Operation operation) {
		this.operation = operation;
	}

	public boolean isIncludeParents() {
		return this.includeParents;
	}

	public void setIncludeParents(final boolean includeParents) {
		this.includeParents = includeParents;
	}

	public boolean isWithInventoryOnly() {
		return this.withInventoryOnly;
	}

	public void setWithInventoryOnly(final boolean withInventoryOnly) {
		this.withInventoryOnly = withInventoryOnly;
	}

	public boolean isIncludeMGMembers() {
		return this.includeMGMembers;
	}

	public void setIncludeMGMembers(final boolean includeMGMembers) {
		this.includeMGMembers = includeMGMembers;
	}

	public int getStartingRow() {
		return this.startingRow;
	}

	public void setStartingRow(final int startingRow) {
		this.startingRow = startingRow;
	}

	public int getNumberOfEntries() {
		return this.numberOfEntries;
	}

	public void setNumberOfEntries(final int numberOfEntries) {
		this.numberOfEntries = numberOfEntries;
	}
	
	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append("GermplasmSearchParameter [searchKeyword=");
		builder.append(searchKeyword);
		builder.append(", operation=");
		builder.append(operation);
		builder.append(", includeParents=");
		builder.append(includeParents);
		builder.append(", withInventoryOnly=");
		builder.append(withInventoryOnly);
		builder.append(", includeMGMembers=");
		builder.append(includeMGMembers);
		builder.append(", startingRow=");
		builder.append(startingRow);
		builder.append(", numberOfEntries=");
		builder.append(numberOfEntries);
		builder.append("]");
		return builder.toString();
	}
}
