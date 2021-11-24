package org.generationcp.middleware.api.germplasm.pedigree.cop;

enum BTypeEnum {
	SELF_POLINATING(1),
	OTHER(0);

	private final double value;

	BTypeEnum(final int value) {
		this.value = value;
	}

	public static BTypeEnum fromValue(final int bType) {
		for (BTypeEnum bTypeEnum : BTypeEnum.values()) {
			if (bTypeEnum.value == bType) {
				return bTypeEnum;
			}
		}
		return null;
	}

	public double getValue() {
		return value;
	}
}
