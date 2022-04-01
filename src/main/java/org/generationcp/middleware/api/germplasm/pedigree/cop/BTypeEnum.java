package org.generationcp.middleware.api.germplasm.pedigree.cop;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * See https://cropforge.github.io/iciswiki/articles/t/d/m/TDM_COP2.htm BTYPE
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum BTypeEnum {
	/**
	 * homozygous, Fully inbreed, self pollinating
	 * From fortran code: FOR SF CROPS ASSUMING F4 FOR LINES WITH UNKNOWN SOURCE BUT KNOWN PARENTS
	 */
	SELF_FERTILIZING_F4(2, "Self Fertilizing with unknown source but known parents"),
	/**
	 * homozygous, Fully inbreed, self pollinating
	 */
	SELF_FERTILIZING(1, "Self Fertilizing"),
	/**
	 * heterozygous, No inbreeding
	 */
	CROSS_FERTILIZING(0, "Cross Fertilizing");

	private final double value;
	private final String description;

	BTypeEnum(final int value, final String description) {
		this.value = value;
		this.description = description;
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

	public String getDescription() {
		return description;
	}
}
