package org.generationcp.middleware.service.impl.audit;

public enum RevisionType {

	CREATION(0),
	EDITION(1),
	DELETION(2);

	private int value;

	RevisionType(final int value) {
		this.value = value;
	}

}
