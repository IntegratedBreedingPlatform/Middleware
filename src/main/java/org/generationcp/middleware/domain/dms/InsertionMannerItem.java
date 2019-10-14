package org.generationcp.middleware.domain.dms;

public enum InsertionMannerItem {

	INSERT_EACH_IN_TURN(8414,"1","Insert each check in turn"),
	INSERT_ALL_CHECKS(8415,"2","Insert all checks at each position");


	private Integer id;
	private String name;
	private String definition;

	InsertionMannerItem(final Integer id, final String name, final String definition) {
		this.id = id;
		this.name = name;
		this.definition = definition;
	}
}
