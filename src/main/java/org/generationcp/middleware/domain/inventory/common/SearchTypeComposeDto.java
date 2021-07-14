package org.generationcp.middleware.domain.inventory.common;


public class SearchTypeComposeDto {

	private Integer searchRequestId;
	private String searchType;

	public SearchTypeComposeDto() {

	}

	public Integer getSearchRequestId() {
		return searchRequestId;
	}

	public void setSearchRequestId(Integer searchRequestId) {
		this.searchRequestId = searchRequestId;
	}

	public String getSearchType() {
		return searchType;
	}

	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	public enum SearchType {
		MANAGE_STUDY("ManageStudy"), GERMPLASM_SEARCH("GermplasmSearch");

		private String code;

		SearchType(final String code) {
			this.setCode(code);
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public static SearchType getEnumByCode(final String code) {
			for (final SearchType e : SearchType.values()) {
				if (code.equals(e.getCode())) {
					return e;
				}
			}
			return null;
		}

	}

}
