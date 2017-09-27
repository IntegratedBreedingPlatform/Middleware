
package org.generationcp.middleware.domain.gms.search;

import java.util.LinkedHashMap;
import java.util.Map;

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

	private final Map<String, Boolean> sortState;

	public GermplasmSearchParameter(final String searchKeyword, final Operation operation) {
		this.searchKeyword = searchKeyword;
		this.operation = operation;
		this.sortState = new LinkedHashMap<>();
		this.startingRow = 0;
		this.numberOfEntries = Integer.MAX_VALUE;
	}

	public GermplasmSearchParameter(final String searchKeyword, final Operation operation, final boolean includeParents,
			final boolean withInventoryOnly, final boolean includeMGMembers) {

		this(searchKeyword, operation);

		this.includeParents = includeParents;
		this.withInventoryOnly = withInventoryOnly;
		this.includeMGMembers = includeMGMembers;
	}

	// Constructor for making a copy of GermplasmSearchParameter object
	public GermplasmSearchParameter(final GermplasmSearchParameter aSearchParameter) {
		this(aSearchParameter.getSearchKeyword(), aSearchParameter.getOperation(), aSearchParameter.isIncludeParents(),
				aSearchParameter.isWithInventoryOnly(), aSearchParameter.includeMGMembers);
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

	public Map<String, Boolean> getSortState() {
		return this.sortState;
	}

	/**
	 * Setup sort states if any (for sorting the germplasm query results)
	 * 
	 * @param sortPropertyIds
	 * @param sortStates
	 */
	public void setSortState(final Object[] sortPropertyIds, final boolean[] sortStates) {
		if (sortPropertyIds == null || sortPropertyIds.length == 0) {
			return;
		}

		this.sortState.clear();

		for (int i = 0; i < sortPropertyIds.length; i++) {
			this.sortState.put(String.valueOf(sortPropertyIds[i]), sortStates[i]);
		}
	}

	@Override
	public String toString() {
		return "GermplasmSearchParameter{" + "searchKeyword='" + this.searchKeyword + '\'' + ", operation=" + this.operation
				+ ", includeParents=" + this.includeParents + ", withInventoryOnly=" + this.withInventoryOnly + ", includeMGMembers="
				+ this.includeMGMembers + ", startingRow=" + this.startingRow + ", numberOfEntries=" + this.numberOfEntries + ", sortState="
				+ this.sortState + '}';
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.includeMGMembers ? 1231 : 1237);
		result = prime * result + (this.includeParents ? 1231 : 1237);
		result = prime * result + this.numberOfEntries;
		result = prime * result + (this.operation == null ? 0 : this.operation.hashCode());
		result = prime * result + (this.searchKeyword == null ? 0 : this.searchKeyword.hashCode());
		result = prime * result + (this.sortState == null ? 0 : this.sortState.hashCode());
		result = prime * result + this.startingRow;
		result = prime * result + (this.withInventoryOnly ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final GermplasmSearchParameter other = (GermplasmSearchParameter) obj;
		if (this.includeMGMembers != other.includeMGMembers) {
			return false;
		}
		if (this.includeParents != other.includeParents) {
			return false;
		}
		if (this.numberOfEntries != other.numberOfEntries) {
			return false;
		}
		if (this.operation != other.operation) {
			return false;
		}
		if (this.searchKeyword == null) {
			if (other.searchKeyword != null) {
				return false;
			}
		} else if (!this.searchKeyword.equals(other.searchKeyword)) {
			return false;
		}
		if (this.sortState == null) {
			if (other.sortState != null) {
				return false;
			}
		} else if (!this.sortState.equals(other.sortState)) {
			return false;
		}
		if (this.startingRow != other.startingRow) {
			return false;
		}
		return this.withInventoryOnly == other.withInventoryOnly;
	}

}
