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

			final Double currentValue = Double.valueOf(value);

			Double minValue = var.getMinRange();
			if (minValue != null) {
				if (currentValue < minValue) {
					return false;
				} else if (var.getScaleMinRange() != null) {
					minValue = Double.valueOf(var.getScaleMinRange());
					if (currentValue < minValue) {
						return false;
					}
				}
			}

			Double maxValue = var.getMaxRange();
			if (maxValue != null) {
				if (currentValue > maxValue) {
					return false;
				} else if (var.getScaleMaxRange() != null) {
					maxValue = Double.valueOf(var.getScaleMaxRange());
					if (currentValue > maxValue) {
						return false;
					}
				}
			}

			return true;
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
