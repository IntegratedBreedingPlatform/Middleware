package org.generationcp.middleware.domain.dms;

public enum InsertionMannerItem {

	INSERT_EACH_IN_TURN(8414,"1","Insert each check in turn"),
	INSERT_ALL_CHECKS(8415,"2","Insert all checks at each position");


	private final Integer id;
	private final String name;
	private final String description;

	InsertionMannerItem(final Integer id, final String name, final String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}

	public Integer getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getDescription() {
		return this.description;
	}
}
