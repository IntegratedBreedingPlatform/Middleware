package org.generationcp.middleware.service.impl.dataset;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.validator.routines.DateValidator;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.pojos.dms.Phenotype;

import java.util.Collection;

public abstract class ExportImportUtils {

	public static boolean isValidValue(
		final MeasurementVariable var, final String value,
		final Collection<Phenotype> possibleValues) {
		if (StringUtils.isBlank(value)) {
			return true;
		}

		if (var.getDataTypeId() == DataType.NUMERIC_VARIABLE.getId()) {
			final boolean isNumber = NumberUtils.isNumber(value);

			if (!isNumber) {
				return false;
			}

			boolean withinValidRange = true;
			final Double currentValue = Double.valueOf(value);

			if (var.getMinRange() != null) {
				final Double minValue = Double.valueOf(var.getMinRange());
				if (currentValue < minValue) {
					withinValidRange = false;
				}
			}

			if (var.getMaxRange() != null) {
				final Double maxValue = Double.valueOf(var.getMaxRange());
				if (currentValue > maxValue) {
					withinValidRange = false;
				}
			}
			return withinValidRange;
		} else if (var.getDataTypeId() == DataType.DATE_TIME_VARIABLE.getId()) {
			return new DateValidator().isValid(value, "yyyyMMdd");
		} else if (var.getDataTypeId() == DataType.CATEGORICAL_VARIABLE.getId()) {
			if (possibleValues != null && !possibleValues.contains(value)) {
				return false;
			}
		}

		return true;
	}

}
