package org.generationcp.middleware.util;

import com.google.common.base.Strings;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ISO8601DateParser {

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public static Date parse( String input ) throws java.text.ParseException {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat(DATE_FORMAT);
        df.setTimeZone(tz);
        return df.parse(input);
    }

    public static Date tryParse( String input ) {
        if(Strings.isNullOrEmpty(input)) return null;
        try {
            return parse(input);
        } catch (ParseException ignored) {
            return null;
        }
    }

    public static String toString( Date date ) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat(DATE_FORMAT);
        df.setTimeZone(tz);
        return df.format(date);
    }
}
