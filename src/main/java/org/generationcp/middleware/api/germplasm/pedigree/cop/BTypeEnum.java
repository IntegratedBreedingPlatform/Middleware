package org.generationcp.middleware.api.germplasm.pedigree.cop;

enum BTypeEnum {
	SELF_POLINATING(1),
	OTHER(0);

	private final double value;

	BTypeEnum(final int value) {
		this.value = value;
	}

	public double getValue() {
		return value;
	}
}
