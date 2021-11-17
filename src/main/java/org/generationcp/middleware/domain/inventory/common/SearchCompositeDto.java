package org.generationcp.middleware.domain.inventory.common;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;
import java.util.Set;

@AutoProperty
public class SearchCompositeDto<X, Y> {

	private X searchRequest;

	private List<Y> itemIds;

	public X getSearchRequest() {
		return searchRequest;
	}

	public void setSearchRequest(final X searchRequest) {
		this.searchRequest = searchRequest;
	}

	public List<Y> getItemIds() {
		return itemIds;
	}

	public void setItemIds(List<Y> itemIds) {
		this.itemIds = itemIds;
	}

	public boolean isValid() {
		return !((searchRequest == null && (itemIds == null || itemIds.isEmpty()))
			|| (searchRequest != null && itemIds != null));
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
