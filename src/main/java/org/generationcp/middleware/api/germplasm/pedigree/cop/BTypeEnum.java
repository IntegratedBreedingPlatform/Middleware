package org.generationcp.middleware.api.germplasm.pedigree.cop;

enum BTypeEnum {
	/**
	 * homozygous, Fully inbreed, self pollinating
	 */
	SELF_FERTILIZING(1),
	/**
	 * heterozygous, No inbreeding
	 */
	CROSS_FERTILIZING(0);

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
