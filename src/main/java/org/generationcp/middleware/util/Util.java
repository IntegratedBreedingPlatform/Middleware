/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/
/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.util;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.domain.etl.Constants;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * A utility class used to get primitive values of wrapper classes, check for null values, and list functions such as getting the max,
 * existence of a value, existence of null, and making a list read only.
 *
 */
public class Util {

	private static final Logger LOG = LoggerFactory.getLogger(Util.class);

	public static final String DATE_AS_NUMBER_FORMAT = "yyyyMMdd";
	public static final String FRONTEND_DATE_FORMAT = "yyyy-MM-dd";
	// NOTE: Future Improvement: BMS should only use one front end date format
	public static final String FRONTEND_DATE_FORMAT_2 = "MM/dd/yyyy";
	public static final String FRONTEND_DATE_FORMAT_3 = "dd/MM/yyyy";
	public static final String FRONTEND_TIMESTAMP_FORMAT = "yyyy-MM-dd hh:mm:ss";
	public static final String DATE_AS_NUMBER_FORMAT_KSU = "d/M/yy";

	private Util() {
		// make a private constructor to hide the implicit public one
	}

	/**
	 * Get the boolean value of <code>value</code>.
	 *
	 * @param value
	 * @return the boolean value of <code>value</code>. If <code>value</code> is null, this method returns false.
	 */
	public static boolean getValue(final Boolean value) {
		return Util.getValue(value, false);
	}

	public static boolean getValue(final Boolean value, final boolean defaultValue) {
		return value == null ? defaultValue : value;
	}

	/**
	 * Test whether <code>obj</code> is equal to one of the specified objects.
	 *
	 * @param obj
	 * @param objs
	 * @return true if the obj is one of the objects
	 */
	public static boolean isOneOf(final Object obj, final Object... objs) {
		if (objs == null) {
			return false;
		}

		for (final Object tmp : objs) {
			if (obj.equals(tmp)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Test whether the specified list is "empty". A <code>null</code> value is considered "empty".
	 *
	 * @param list
	 * @return true if the given list is empty.
	 */
	public static boolean isEmpty(final List<?> list) {
		return list == null || list.isEmpty();
	}

	/**
	 * Returns the maximum among the input values.
	 *
	 * @param value1
	 * @param values
	 * @return Maximum of the given values.
	 */
	public static int max(final int value1, final int... values) {
		int max = value1;

		for (final int value : values) {
			if (value > max) {
				max = value;
			}
		}

		return max;
	}

	/**
	 * @param source source list
	 * @param projection projection function
	 * @param <Source> Source Type
	 * @param <Result> Result Type
	 * @return List<Result> Projected data
	 */
	public static <Source, Result> List<Result> convertAll(final List<Source> source, final Function<Source, Result> projection) {
		final ArrayList<Result> results = new ArrayList<>();
		for (final Source element : source) {
			results.add(projection.apply(element));
		}
		return results;
	}

	public static void checkAndThrowForNullObjects(final Object... objects) throws MiddlewareException {
		final String insufficientData = "One or more required fields are missing.";
		for (final Object o : objects) {
			if (o != null) {
				continue;
			}
			throw new MiddlewareException(insufficientData);
		}
	}

	/**
	 * @param source source list
	 * @param projection projection function
	 * @param <Key> Key Type
	 * @param <Source> Source Type
	 * @return List<Result> Projected data
	 */
	public static <Key, Source> Map<Key, Source> mapAll(final List<Source> source, final Function<Source, Key> projection) {
		final Map<Key, Source> results = new HashMap<>();
		for (final Source element : source) {
			results.put(projection.apply(element), element);
		}
		return results;
	}

	/**
	 * Returns the current date in format "yyyyMMdd" as Integer
	 *
	 * @return current date as Integer
	 */
	public static Integer getCurrentDateAsIntegerValue() {
		return Integer.valueOf(Util.getCurrentDateAsStringValue());
	}

	public static Integer convertDateToIntegerValue(final Date date) {
		return Integer.valueOf(Util.getSimpleDateFormat(Util.DATE_AS_NUMBER_FORMAT).format(date.getTime()));
	}

	/**
	 * Returns the current date in format "yyyyMMdd" as Long
	 *
	 * @return current date as Long
	 */
	public static Long getCurrentDateAsLongValue() {
		return Long.valueOf(Util.getCurrentDateAsStringValue());
	}

	/**
	 * Returns the current date in format "yyyyMMdd" as String
	 *
	 * @return current date as String
	 */
	public static String getCurrentDateAsStringValue() {
		return Util.getSimpleDateFormat(Util.DATE_AS_NUMBER_FORMAT).format(Util.getCurrentDate().getTime());
	}

	/**
	 * Returns the current date
	 *
	 * @return current date as Date
	 */
	public static Date getCurrentDate() {
		return Util.getCalendarInstance().getTime();
	}

	/**
	 * Returns the calendar instance
	 *
	 * @return calendar instance
	 */
	public static Calendar getCalendarInstance() {
		final Locale currentLocale = Locale.getDefault(Locale.Category.DISPLAY);
		return Calendar.getInstance(currentLocale);
	}

	/**
	 * Returns the current date in the specified format as String
	 *
	 * @return current date as String
	 */
	public static String getCurrentDateAsStringValue(final String format) {
		return Util.getSimpleDateFormat(format).format(Util.getCurrentDate().getTime());
	}

	/**
	 * Returns the SimpleDateFormat of the current display locale
	 *
	 * @return SimpleDateFormat
	 */
	public static SimpleDateFormat getSimpleDateFormat(final String format) {
		final Locale currentLocale = Locale.getDefault(Locale.Category.DISPLAY);
		final SimpleDateFormat formatter = new SimpleDateFormat(format, currentLocale);
		formatter.setLenient(false);
		return formatter;
	}

	/**
	 * Returns the date in the specified format as String
	 *
	 * @return date in the specified format as String
	 */
	public static String formatDateAsStringValue(final Date date, final String format) {
		if (date == null || format == null) {
			return null;
		}

		try {
			return Util.getSimpleDateFormat(format).format(date.getTime());
		} catch (final IllegalArgumentException e) {
			LOG.warn("Cannot format date: " + date + " - format: " + format, e);
			System.out.println("Cannot format date: " + date + " - format: " + format);
			return null;
		}
	}

	/**
	 * Returns the date object from the specified format
	 *
	 * @return date object
	 * @throws ParseException
	 */
	public static Date parseDate(final String date, final String format) throws ParseException {
		final SimpleDateFormat formatter = Util.getSimpleDateFormat(format);
		return formatter.parse(date);
	}

	/**
	 * Returns the date object from the specified format
	 *
	 * @return date object or null if it cannot be parsed
	 */
	public static Date tryParseDate(final String date, final String format) {
		try {
			return Util.parseDate(date, format);
		} catch (final ParseException | NullPointerException e) {
			return null;
		}
	}

	/**
	 * Parses the date given default format
	 *
	 * See {@link #tryParseDate(String, String)}
	 *
	 */
	public static Date tryParseDate(final String date) {
		return tryParseDate(date, Util.DATE_AS_NUMBER_FORMAT);
	}

	public static String nullIfEmpty(final String value) {
		if (StringUtils.isEmpty(value)) {
			return null;
		}
		return value;
	}

	public static Double zeroIfNull(final Double value) {
		if (value == null) {
			return 0.0;
		}
		return value;
	}

	public static String prependToCSV(final String valueToPrepend, final String csv) {
		return valueToPrepend + ", " + csv;
	}

	public static String prependToCSVAndArrange(final String valueToPrepend, final String csv) {
		final String updatedValue = Util.prependToCSV(valueToPrepend, csv);
		final String[] values = updatedValue.split(",");
		final Set<String> valueSet = new TreeSet<String>();
		for (final String value : values) {
			valueSet.add(value.trim());
		}
		return Util.convertCollectionToCSV(valueSet);
	}

	public static String convertCollectionToCSV(final Collection<?> collection) {
		int i = 0;
		final StringBuilder csv = new StringBuilder();
		for (final Object value : collection) {
			if (i != 0) {
				csv.append(", ");
			}
			csv.append(value);
			i++;
		}
		return csv.toString();
	}

	/**
	 * Converts the date from the old format to the new format
	 *
	 * @param date
	 * @param oldFormat
	 * @param newFormat
	 * @return String converted date from old format to new format
	 * @throws ParseException
	 */
	public static String convertDate(final String date, final String oldFormat, final String newFormat) throws ParseException {
		final SimpleDateFormat sdf = Util.getSimpleDateFormat(oldFormat);
		final Date d = sdf.parse(date);
		sdf.applyPattern(newFormat);
		return sdf.format(d);
	}

	public static String tryConvertDate(final String date, final String oldFormat, final String newformat) {
		final Date parsedDate = Util.tryParseDate(date, oldFormat);
		if (parsedDate != null) {
			final SimpleDateFormat sdf = Util.getSimpleDateFormat(newformat);
			return sdf.format(parsedDate);
		}
		return null;
	}

	public static boolean isValidDate(final String dateString) {
		if (dateString == null) {
			return false;
		}

		// If the dateString is parseable from yyyy-MM-dd format, it should be valid
		if (Util.tryParseDate(dateString, Util.FRONTEND_DATE_FORMAT) != null) {
			return true;
		}

		// Check if the dateString is parseable from yyyyMMdd format
		if (dateString.length() != Util.DATE_AS_NUMBER_FORMAT.length()) {
			return false;
		}

		final int date;
		try {
			date = Integer.parseInt(dateString);
		} catch (final NumberFormatException e) {
			return false;
		}

		final int year = date / 10000;
		final int month = date % 10000 / 100;
		final int day = date % 100;

		return Util.isValidDate(year, month, day);
	}

	public static boolean isValidDate(final int year, final int month, final int day) {
		final boolean yearOk = Util.isValidYear(year);
		final boolean monthOk = month >= 1 && month <= 12;
		final boolean dayOk = day >= 1 && day <= Util.daysInMonth(year, month);
		return yearOk && monthOk && dayOk;
	}

	public static boolean isValidYear(final Integer year) {
		if (year < 1900) {
			return false;
		} else if (year > 9999) {
			return false;
		}
		return true;
	}

	public static int daysInMonth(final int year, final int month) {
		final int daysInMonth;
		if (month == 2) {
			if (Util.isLeapYear(year)) {
				daysInMonth = 29;
			} else {
				daysInMonth = 28;
			}
		} else if (month == 4 || month == 6 || month == 9 || month == 11) {
			daysInMonth = 30;
		} else {
			daysInMonth = 31;
		}
		return daysInMonth;
	}

	public static boolean isLeapYear(final int year) {
		boolean isLeapYear = false;
		if (year % 400 == 0) {
			isLeapYear = true;
		} else if (year % 100 == 0) {
			isLeapYear = false;
		} else if (year % 4 == 0) {
			isLeapYear = true;
		} else {
			isLeapYear = false;
		}
		return isLeapYear;
	}

	/**
	 * Use to validate measurementVariable if using correct value for it's dataType
	 * @param measurementVariable
	 * @param value
	 * @return Message object
	 */
	@Deprecated // StringUtils.isNumeric not compatible with decimals
	public static Optional<Message> validateVariableValues(final MeasurementVariable variable, final String value) {

		if ((variable.getDataTypeId() != null && variable.getDataTypeId().equals(DataType.NUMERIC_VARIABLE.getId())) || variable.getDataType().equals(DataType.NUMERIC_VARIABLE.getDataTypeCode())) {
			if (!StringUtils.isNumeric(value)) {
				return Optional.of(new Message(Constants.INVALID_NUMERIC_VALUE_MESSAGE, variable.getLabel(), value));
			}
		}

		return Optional.absent();
	}

	public static int getIntValue(final Integer value) {
		if (value == null) {
			return 0;
		}
		return value;
	}

	/**
	 * Parse hibernate query result value to boolean with null check
	 *
	 * @param val value
	 * @return boolean
	 */
	public static final boolean typeSafeObjectToBoolean(final Object val) {
		if (val == null) {
			return false;
		}
		if (val instanceof Integer) {
			return (Integer) val != 0;
		}
		if (val instanceof Boolean) {
			return (Boolean) val;
		}
		return false;
	}

	/**
	 * Parse hibernate query result value to Integer with null check
	 *
	 * @param val value
	 * @return boolean
	 */
	public static final Integer typeSafeObjectToInteger(final Object val) {
		if (val == null) {
			return null;
		}
		if (val instanceof Integer) {
			return (Integer) val;
		}
		if (val instanceof String) {
			return Integer.valueOf((String) val);
		}
		throw new NumberFormatException("Can not cast " + val.getClass() + " to Integer for value: " + val);
	}

}
