package org.generationcp.middleware.domain.inventory.manager;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Set;

@AutoProperty
public class SearchCompositeDto {

	private Integer searchId;

	private Set<Integer> listIds;

	public Integer getSearchId() {
		return searchId;
	}

	public void setSearchId(Integer searchId) {
		this.searchId = searchId;
	}

	public Set<Integer> getListIds() {
		return listIds;
	}

	public void setListIds(Set<Integer> listIds) {
		this.listIds = listIds;
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
