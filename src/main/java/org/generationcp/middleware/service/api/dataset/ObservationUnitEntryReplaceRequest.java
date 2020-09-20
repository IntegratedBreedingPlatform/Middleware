package org.generationcp.middleware.service.api.dataset;

import org.generationcp.middleware.domain.inventory.common.SearchCompositeDto;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class ObservationUnitEntryReplaceRequest {

	private SearchCompositeDto<ObservationUnitsSearchDTO, Integer> searchRequest;

	private Integer entryId;

	public SearchCompositeDto<ObservationUnitsSearchDTO, Integer> getSearchRequest() {
		return searchRequest;
	}

	public void setSearchRequest(
		final SearchCompositeDto<ObservationUnitsSearchDTO, Integer> searchRequest) {
		this.searchRequest = searchRequest;
	}

	public Integer getEntryId() {
		return entryId;
	}

	public void setEntryId(final Integer entryId) {
		this.entryId = entryId;
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
