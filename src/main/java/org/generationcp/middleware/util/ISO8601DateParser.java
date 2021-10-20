
package org.generationcp.middleware.util;

import com.google.common.base.Strings;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

public class ISO8601DateParser {

	private static final String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

	public static Date parseToDateTime(String input) throws java.text.ParseException {
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat(ISO8601DateParser.DATETIME_FORMAT);
		df.setTimeZone(tz);
		return df.parse(input);
	}

	public static Date tryParseToDateTime(String input) {
		if (Strings.isNullOrEmpty(input)) {
			return null;
		}
		try {
			return ISO8601DateParser.parseToDateTime(input);
		} catch (ParseException ignored) {
			return null;
		}
	}

	public static String toString(Date date) {
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat(ISO8601DateParser.DATETIME_FORMAT);
		df.setTimeZone(tz);
		return df.format(date);
	}

	public static Date parseToDate(long input){
		try {
			return java.sql.Date.valueOf(LocalDate.parse(String.valueOf(input), DATE_FORMAT));
		} catch (Exception e) {
			return null;
		}
	}

}
