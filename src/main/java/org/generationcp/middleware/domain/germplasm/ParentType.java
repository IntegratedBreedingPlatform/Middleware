package org.generationcp.middleware.domain.germplasm;

public enum ParentType {
	MALE,
	FEMALE,
	SELF,
	POPULATION;

	public static ParentType fromString(final String text) {
		for (final ParentType parentType : ParentType.values()) {
			if (parentType.name().equals(text)) {
				return parentType;
			}
		}
		return null;
	}
}
