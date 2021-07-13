package org.generationcp.middleware.domain.inventory.common;


public class SearchTypeComposeDto {

	private Integer searchRequestId;
	private SearchType searchType;

	public SearchTypeComposeDto() {

	}

	public Integer getSearchRequestId() {
		return searchRequestId;
	}

	public void setSearchRequestId(Integer searchRequestId) {
		this.searchRequestId = searchRequestId;
	}

	public SearchType getSearchType() {
		return searchType;
	}

	public void setSearchType(SearchType searchType) {
		this.searchType = searchType;
	}

	public enum SearchType {
		MANAGE_STUDY, GERMPLASM_SEARCH
	}

}
