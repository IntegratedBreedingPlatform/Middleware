package org.generationcp.middleware.service.impl.study.advance;

import org.apache.commons.lang3.math.NumberUtils;

public class AdvanceUtils {

	public static Integer getIntegerValue(final String value) {
		Integer integerValue = null;

		if (NumberUtils.isNumber(value)) {
			integerValue = Double.valueOf(value).intValue();
		}

		return integerValue;
	}

}
