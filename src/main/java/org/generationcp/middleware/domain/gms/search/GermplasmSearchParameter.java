package org.generationcp.middleware.domain.gms.search;

import java.util.HashMap;
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

	private Map<String, Boolean> sortState;

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
		return sortState;
	}

	/**
	 * Setup sort states if any (for sorting the germplasm query results)
	 * @param sortPropertyIds
	 * @param sortStates
	 */
	public void setSortState(final Object[] sortPropertyIds, final boolean[] sortStates) {
		if (sortPropertyIds == null || sortPropertyIds.length == 0) {
			return;
		}

		sortState.clear();

		for (int i = 0; i < sortPropertyIds.length; i++) {
			sortState.put(String.valueOf(sortPropertyIds[i]),sortStates[i]);
		}
	}

	@Override
	public String toString() {
		return "GermplasmSearchParameter{" +
				"searchKeyword='" + searchKeyword + '\'' +
				", operation=" + operation +
				", includeParents=" + includeParents +
				", withInventoryOnly=" + withInventoryOnly +
				", includeMGMembers=" + includeMGMembers +
				", startingRow=" + startingRow +
				", numberOfEntries=" + numberOfEntries +
				", sortState=" + sortState +
				'}';
	}
}
