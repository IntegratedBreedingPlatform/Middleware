package org.generationcp.middleware.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.validator.routines.DateValidator;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.oms.TermSummary;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

public class VariableValueUtil {

	// Do NOT use this in middleware while saving phenotypes, some special rules applies there like accepting empty strings
	// for some data types
	public static boolean isValidObservationValue(final Variable var, final String value) {
		return isValidValue(var, value, true, true);
	}

	public static boolean isValidAttributeValue(final Variable var, final String value) {
		if (StringUtils.isEmpty(value)) {
			return false;
		}
		return isValidValue(var, value, false, false);
	}

	public static String getExpectedRange(final Variable variable) {
		switch (DataType.getByName(variable.getScale().getDataType().getName())) {
			case CATEGORICAL_VARIABLE:
				final List<TermSummary> categories = variable.getScale().getCategories();
				final List<String> values =
					categories.stream().map(org.generationcp.middleware.domain.oms.TermSummary::getName).collect(Collectors.toList());
				return StringUtils.join(values, ";");

			case NUMERIC_VARIABLE:
				final StringBuilder range = new StringBuilder();
				if (
					(StringUtils.isNotBlank(variable.getMinValue()) //
						|| StringUtils.isNotBlank(variable.getMaxValue()))) {
					range.append(variable.getMinValue()).append("-");
					range.append(variable.getMaxValue());

				} else if (StringUtils.isNotBlank(variable.getScale().getMinValue()) //
					|| StringUtils.isNotBlank(variable.getScale().getMaxValue())) {
					range.append(variable.getScale().getMinValue()).append("-");
					range.append(variable.getScale().getMaxValue());
				}
				return range.toString();

			default:
				return "";
		}
	}

	//FIXME According with Mariano, observations should not accept invalid categories for a categorical scale
	//FIXME invalidCategoricalScale should be removed when observations are fixed
	private static boolean isValidValue(final Variable var, final String value, final boolean isMissingAccepted,
		final boolean isOutOfBoundsCategoricalAccepted) {
		if (StringUtils.isBlank(value)) {
			return true;
		}
		if (var.getMinValue() != null && var.getMaxValue() != null) {
			return validateIfValueIsMissingOrNumber(value.trim(), isMissingAccepted);
		} else if (var.getScale().getDataType() == DataType.NUMERIC_VARIABLE) {
			return validateIfValueIsMissingOrNumber(value.trim(), isMissingAccepted);
		} else if (var.getScale().getDataType() == DataType.DATE_TIME_VARIABLE) {
			return new DateValidator().isValid(value, "yyyyMMdd");
		} else if (var.getScale().getDataType() == DataType.CATEGORICAL_VARIABLE) {
			if (isOutOfBoundsCategoricalAccepted) {
				return true;
			}
			return validateCategoricalValue(var, value);
		}
		return true;
	}

	private static boolean validateIfValueIsMissingOrNumber(final String value, final boolean isMissingAccepted) {
		if (isMissingAccepted && MeasurementData.MISSING_VALUE.equals(value.trim())) {
			return true;
		}
		return NumberUtils.isNumber(value);
	}

	private static boolean validateCategoricalValue(final Variable variable, final String value) {
		final BigInteger categoricalValueId =
			variable.getScale().getCategories().stream().filter(category -> value.equalsIgnoreCase(category.getName())).findFirst()
				.map(category -> BigInteger.valueOf(category.getId())).orElse(null);
		return categoricalValueId != null;
	}

	public static Integer resolveCategoricalValueId(final Variable variable, final String value) {
		Integer categoricalValueId = null;
		if (variable.getScale().getDataType().getId().equals(TermId.CATEGORICAL_VARIABLE.getId())) {
			categoricalValueId =
				variable.getScale().getCategories().stream().filter(category -> value.equalsIgnoreCase(category.getName())).findFirst()
					.map(category -> BigInteger.valueOf(category.getId()).intValue()).orElse(null);
			if (categoricalValueId == null) {
				throw new MiddlewareRequestException("", "measurement.variable.invalid.categorical.value", variable.getName());
			}
		}
		return categoricalValueId;
	}

}
