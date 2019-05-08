package org.generationcp.middleware.domain.search_request;

public enum SearchRequestType {
	GERMPLASM(1, "GERMPLASM");

	private int id;

	private String name;

	SearchRequestType(final int id, final String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return this.id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public static SearchRequestType findById(final int id) {
		for (final SearchRequestType type : SearchRequestType.values()) {
			if (type.getId() == id) {
				return type;
			}
		}
		return null;
	}
}
