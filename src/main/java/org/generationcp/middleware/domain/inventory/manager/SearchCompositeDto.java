package org.generationcp.middleware.domain.inventory.manager;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Set;

@AutoProperty
public class SearchCompositeDto {

	private Integer searchRequestId;

	private Set<Integer> itemIds;

	public Integer getSearchRequestId() {
		return searchRequestId;
	}

	public void setSearchRequestId(Integer searchRequestId) {
		this.searchRequestId = searchRequestId;
	}

	public Set<Integer> getItemIds() {
		return itemIds;
	}

	public void setItemIds(Set<Integer> itemIds) {
		this.itemIds = itemIds;
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
