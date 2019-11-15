package org.generationcp.middleware.domain.workbench;

public enum RoleType {

	INSTANCE(1),
	CROP(2),
	PROGRAM(3);

	private int id;

	public int getId() {
		return id;
	}

	RoleType(final int id) {
		this.id = id;
	}
}
