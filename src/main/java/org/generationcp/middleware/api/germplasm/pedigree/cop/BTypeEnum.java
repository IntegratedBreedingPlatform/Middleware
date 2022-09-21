package org.generationcp.middleware.api.germplasm.pedigree.cop;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

/**
 * See https://cropforge.github.io/iciswiki/articles/t/d/m/TDM_COP2.htm BTYPE
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum BTypeEnum {
	/**
	 * homozygous, Fully inbreed, self pollinating
	 * From fortran code: FOR SF CROPS ASSUMING F4 FOR LINES WITH UNKNOWN SOURCE BUT KNOWN PARENTS
	 */
	SELF_FERTILIZING_F4(2, 1, "Self Fertilizing with unknown source but known parents"),
	/**
	 * homozygous, Fully inbreed, self pollinating
	 */
	SELF_FERTILIZING(1, 1, "Self Fertilizing"),
	/**
	 * heterozygous, No inbreeding
	 */
	CROSS_FERTILIZING(0, 0, "Cross Fertilizing");

	/*
	 * btype=2 not meant to be used as value? we separate then between id and value, so that value can be used
	 */
	private final double id;
	private final double value;
	private final String description;

	BTypeEnum(final double id, final double value, final String description) {
		this.id = id;
		this.value = value;
		this.description = description;
	}

	public static Optional<BTypeEnum> parse(final String key) {
		if (key == null) {
			return empty();
		}
		Double k = null;
		try {
			k = Double.valueOf(key);
		} catch (final NumberFormatException exception) {
			return empty();
		}
		for (final BTypeEnum bTypeEnum : BTypeEnum.values()) {
			if (k.equals(bTypeEnum.getId())) {
				return of(bTypeEnum);
			}
		}
		return empty();
	}

	public double getId() {
		return id;
	}

	public double getValue() {
		return value;
	}

	public String getDescription() {
		return description;
	}
}
