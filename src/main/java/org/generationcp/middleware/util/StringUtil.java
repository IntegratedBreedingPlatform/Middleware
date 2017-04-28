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

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains utility classes for String formatting, format checking and conversion. For parse methods, the given input String is converted to
 * the required output if available. If no value is available, the given default output is returned.
 *
 *
 * @author Glenn Marintes
 */
public abstract class StringUtil {
	
	private static final Logger LOG = LoggerFactory.getLogger(StringUtil.class);

	public static int parseInt(String string, int defaultValue) {
		if (StringUtils.isBlank(string)) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(string);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public static Integer parseInt(String string, Integer defaultValue) {
		if (StringUtils.isBlank(string)) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(string);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public static long parseLong(String string, long defaultValue) {
		if (StringUtils.isBlank(string)) {
			return defaultValue;
		}
		try {
			return Long.parseLong(string);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public static Long parseLong(String string, Long defaultValue) {
		if (StringUtils.isBlank(string)) {
			return defaultValue;
		}
		try {
			return Long.parseLong(string);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public static float parseFloat(String string, float defaultValue) {
		if (StringUtils.isBlank(string)) {
			return defaultValue;
		}

		try {
			return Float.parseFloat(string);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public static Float parseFloat(String string, Float defaultValue) {
		if (StringUtils.isBlank(string)) {
			return defaultValue;
		}

		try {
			return Float.parseFloat(string);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public static BigInteger parseBigInteger(String string, BigInteger defaultValue) {
		if (StringUtils.isBlank(string)) {
			return defaultValue;
		}

		try {
			return new BigInteger(string);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public static BigDecimal parseBigDecimal(String string, BigDecimal defaultValue) {
		if (StringUtils.isBlank(string)) {
			return defaultValue;
		}

		try {
			return new BigDecimal(string);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	/**
	 * A lenient version of {@link Double#parseDouble(String)}.
	 * 
	 * @param string
	 * @param defaultValue
	 * @return the double value
	 */
	public static double parseDouble(String string, double defaultValue) {
		if (StringUtils.isBlank(string)) {
			return defaultValue;
		}

		try {
			return Double.parseDouble(string);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	/**
	 * A lenient version of {@link Double#parseDouble(String)}.
	 * 
	 * @param string
	 * @param defaultValue
	 * @return <code>defaultValue</code> if <code>string</code> is blank or cannot be parsed as double. Returns the parsed double value
	 *         otherwise.
	 */
	public static Double parseDouble(String string, Double defaultValue) {
		if (StringUtils.isBlank(string)) {
			return defaultValue;
		}

		try {
			return Double.parseDouble(string);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	/**
	 * Join the specified list of objects with the specified delimiter. Any empty value in the list will be ignored.
	 * <p>
	 * If the specified <code>objectList</code> contains a {@link Collection} or an {@link Array}, its member objects will be recursively
	 * "joined".
	 * 
	 * @param delimiter
	 * @param objectList
	 * @return The joined value.
	 */
	public static String joinIgnoreEmpty(Object delimiter, Object... objectList) {
		StringBuilder sb = new StringBuilder();
		if (objectList == null) {
			return sb.toString();
		}

		for (Object obj : objectList) {
			if (obj == null) {
				continue;
			}

			String value = null;
			if (obj instanceof Collection<?>) {
				value = StringUtil.joinIgnoreEmpty(delimiter, ((Collection<?>) obj).toArray());
			} else if (Object[].class.isInstance(obj)) {
				value = StringUtil.joinIgnoreEmpty(delimiter, (Object[]) obj);
			} else {
				value = obj.toString();
			}

			if (StringUtils.isEmpty(value)) {
				continue;
			}

			if (sb.length() > 0) {
				sb.append(delimiter);
			}
			sb.append(value);
		}

		return sb.toString();
	}

    public static boolean areBothEmptyOrEqual(String string1, String string2) {
        if (StringUtils.isEmpty(string1) && StringUtils.isEmpty(string2)) {
            return true;
        } else if(string1 != null || string2 != null) {
          return string1.equals(string2);
        } else {
        	return false;
        }
    }

	/**
	 * Join the specified list of objects with the specified delimiter. Any null value in the list will be ignored.
	 * <p>
	 * If the specified <code>objectList</code> contains a {@link Collection} or an {@link Array}, its member objects will be recursively
	 * "joined".
	 * 
	 * @param delimiter
	 * @param objectList
	 * @return The joined value.
	 */
	public static String joinIgnoreNull(Object delimiter, Object... objectList) {
		StringBuilder sb = new StringBuilder();
		if (objectList == null) {
			return sb.toString();
		}

		for (Object obj : objectList) {
			if (obj == null) {
				continue;
			}

			String value = null;
			if (obj instanceof Collection<?>) {
				value = StringUtil.joinIgnoreEmpty(delimiter, ((Collection<?>) obj).toArray());
			} else if (Object[].class.isInstance(obj)) {
				value = StringUtil.joinIgnoreEmpty(delimiter, (Object[]) obj);
			} else {
				value = obj.toString();
			}

			if (value == null) {
				continue;
			}

			if (sb.length() > 0) {
				sb.append(delimiter);
			}
			sb.append(value);
		}

		return sb.toString();
	}

	/**
	 * Prepend the {@link String} representation of the specified <code>obj</code> with the specified <code>prefix</code> until it reaches
	 * <code>maxLength</code>.
	 * 
	 * @param obj
	 * @param prefix
	 * @param maxLength
	 * @return The prepended value.
	 */
	public static String prependWith(Object obj, String prefix, int maxLength) {
		if (obj == null) {
			return null;
		}
		if (prefix == null) {
			throw new IllegalArgumentException("prefix cannot be null");
		}

		String str = obj.toString();
		int strLength = str.length();
		for (int i = strLength; i < maxLength; i++) {
			str = prefix + str;
		}

		return str;
	}

	/**
	 * Prepend the {@link String} representation of the specified <code>obj</code> with the specified <code>suffix</code> until it reaches
	 * <code>maxLength</code>.
	 * 
	 * @param obj
	 * @param suffix
	 * @param maxLength
	 * @return The appended value.
	 */
	public static String appendWith(Object obj, String suffix, int maxLength) {
		if (obj == null) {
			return null;
		}
		if (suffix == null) {
			throw new IllegalArgumentException("suffix cannot be null");
		}

		String str = obj.toString();
		int strLength = str.length();
		for (int i = strLength; i < maxLength; i++) {
			str = str + suffix;
		}

		return str;
	}

	/**
	 * Create a {@link String} composed of <code>ch</code> concatenated <code>count</code> times.
	 * 
	 * @param ch
	 * @param count
	 * @return String of ch concatenated count times
	 */
	public static String stringOf(char ch, int count) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < count; i++) {
			sb.append(ch);
		}

		return sb.toString();
	}

	/**
	 * Create a {@link String} composed of <code>str</code> concatenated <code>count</code> times.
	 * 
	 * @param str
	 * @param count
	 * @return String of str concatenated count times
	 */
	public static String stringOf(String str, int count) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < count; i++) {
			sb.append(str);
		}

		return sb.toString();
	}

	public static boolean isEmpty(String s) {
		return s == null || s.length() == 0;
	}

	public static boolean isEmptyOrWhitespaceOnly(String s) {
		return StringUtil.isEmpty(s) || s.matches("\\A\\s*\\z");
	}

	/**
	 * Prepares a value for being displayed in a fixed length fashion. For example:
	 * 
	 * <pre>
	 * format("ABC", 5, true) returns "ABC  "
	 * </pre>
	 * 
	 * <pre>
	 * format(123, 6, false) returns "   123"
	 * </pre>
	 * 
	 * <pre>
	 * format("ABCDE", 4, false) returns "ABCD"
	 * </pre>
	 * 
	 * @param field The object's value to print
	 * @param maxSize The space allowed for printing this value, or the maximum number of characters allowed for printing the field
	 * @param isLeftAligned indicate if field is aligned to left or right, in case the field is shorter the space allowed for it
	 * @return
	 */
	 public static String format(Object field, int maxSize, boolean isLeftAligned) {
		 String notAvailableValue = "N/A";
		 String value = null == field ? notAvailableValue : field.toString().trim();

		if (value.length() > maxSize) {
			 value = value.substring(0, maxSize);
		 }

		try {
			 String expression = new StringBuilder("%").append(isLeftAligned ? "-" : "").append(maxSize).append('s').toString();

			value = String.format(expression, value);
		 } catch (Exception e) {
			 StringUtil.LOG.error("String format was incorrect", e);
		 }
		 return value;
	 }



	public static String removeBraces(final String str){
		if(StringUtils.isBlank(str)){
			return str;
		}

		if((str.startsWith("<") && str.endsWith(">")) || (str.startsWith("(") && str.endsWith(")"))
                || (str.startsWith("[") && str.endsWith("]")) || (str.startsWith("{") && str.endsWith("}"))) {
			return str.substring(1, str.length() -1);
		}

		return str;
	}

	public static boolean containsIgnoreCase(final List<String> list, final String searchFor) {
		for (final String item : list) {
			if (StringUtils.isEmpty(searchFor) || StringUtils.containsIgnoreCase(item, searchFor)) {
				return true;
			}
		}
		return false;
	}

}
