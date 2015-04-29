/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.middleware.util;

import java.text.ParseException;
import com.google.common.base.Function;
import org.generationcp.middleware.exceptions.MiddlewareException;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * A utility class used to get primitive values of wrapper classes, check for
 * null values, and list functions such as getting the max, existence of a
 * value, existence of null, and making a list read only.
 * 
 */
public class Util{

	public static final String DATE_AS_NUMBER_FORMAT = "yyyyMMdd";
	public static final String FRONTEND_DATE_FORMAT = "yyyy-MM-dd";
	//NOTE: Future Improvement: BMS should only use one front end date format
	public static final String FRONTEND_DATE_FORMAT_2 = "MM/dd/yyyy";
	public static final String FRONTEND_TIMESTAMP_FORMAT = "yyyy-MM-dd hh:mm:ss";
	
	private Util() {
		//make a private constructor to hide the implicit public one
	}
    /**
     * Get the boolean value of <code>value</code>.
     * 
     * @param value
     * @return the boolean value of <code>value</code>. If <code>value</code> is
     *         null, this method returns false.
     */
    public static boolean getValue(Boolean value) {
        return getValue(value, false);
    }

    public static boolean getValue(Boolean value, boolean defaultValue) {
        return value == null ? defaultValue : value;
    }

    /**
     * Test whether <code>obj</code> is equal to one of the specified objects.
     * 
     * @param obj
     * @param objs
     * @return true if the obj is one of the objects
     */
    public static boolean isOneOf(Object obj, Object... objs) {
        if (objs == null) {
            return false;
        }

        for (Object tmp : objs) {
            if (obj.equals(tmp)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns true if all values are null.
     * @param args
     * @return true if all values are null.
     */
    public static boolean isAllNull(Object... args) {
        for (Object obj : args) {
            if (obj != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Test whether <code>value</code> is equal to all of the specified values.
     * 
     * @param value
     * @param values
     * @return true if value is equal to all values.
     */
    public static boolean isAllEqualTo(Double value, Double... values) {
        if (values == null) {
            return false;
        }

        for (Double val : values) {
            if (!value.equals(val)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Test whether the specified list is "empty". A <code>null</code> value is
     * considered "empty".
     * 
     * @param list
     * @return true if the given list is empty.
     */
    public static boolean isEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }

    /**
     * Returns the maximum among the input values.
     * @param value1
     * @param values
     * @return Maximum of the given values.
     */
    public static int max(int value1, int... values) {
        int max = value1;

        for (int value : values) {
            if (value > max) {
                max = value;
            }
        }

        return max;
    }

    /**
     * Makes the given objects in the list unmodifiable.
     * @param objects
     * @return the read-only list.
     */
    public static <T> List<T> makeReadOnlyList(T... objects) {
        if (objects == null) {
            return Collections.unmodifiableList(new ArrayList<T>());
        }

        return Arrays.asList(objects);
    }
    
    public static Integer getCurrentDateAsInteger(){
        Calendar now = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_AS_NUMBER_FORMAT);
        String dateNowStr = formatter.format(now.getTime());
        Integer dateNowInt = Integer.valueOf(dateNowStr);
        return dateNowInt;

    }

    /**
     * @param source source list
     * @param projection projection function
     * @param <Source> Source Type
     * @param <Result> Result Type
     * @return List<Result> Projected data
     */
    public static <Source, Result> List<Result> convertAll(List<Source> source, Function<Source, Result> projection)
    {
        ArrayList<Result> results = new ArrayList<>();
        for (Source element : source)
        {
            results.add(projection.apply(element));
        }
        return results;
    }

    public static void checkAndThrowForNullObjects(Object ... objects) throws MiddlewareException {
        final String insufficientData =  "One or more required fields are missing.";
        for(Object o : objects) {
            if(o != null) continue;
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
    public static <Key, Source> Map<Key, Source> mapAll(List<Source> source, Function<Source, Key> projection)
    {
        Map<Key, Source> results = new HashMap<>();
        for (Source element : source)
        {
            results.put(projection.apply(element), element);
        }
        return results;
    }

    public static boolean isNonNullValidNumericString(Object value) {
        return value != null && (value instanceof Integer || value instanceof String && ((String) value).matches("^[0-9]+$"));
    /**
     * Returns the current date in format "yyyyMMdd" as Integer
     * @return current date as Integer
     */
    public static Integer getCurrentDateAsIntegerValue(){
    	return Integer.valueOf(getCurrentDateAsStringValue());
    }
    
    /**
     * Returns the current date in format "yyyyMMdd" as Long
     * @return current date as Long
     */
    public static Long getCurrentDateAsLongValue(){
    	return Long.valueOf(getCurrentDateAsStringValue());
    }
    
    /**
     * Returns the current date in format "yyyyMMdd" as String
     * @return current date as String
     */
    public static String getCurrentDateAsStringValue(){
    	return getSimpleDateFormat(DATE_AS_NUMBER_FORMAT).format(getCurrentDate().getTime());
    }
    
    /**
     * Returns the current date
     * @return current date as Date
     */
    public static Date getCurrentDate(){
    	return getCalendarInstance().getTime();
    }
    
    /**
     * Returns the calendar instance
     * @return calendar instance
     */
    public static Calendar getCalendarInstance(){
    	Locale currentLocale = Locale.getDefault(Locale.Category.DISPLAY);
        return Calendar.getInstance(currentLocale);
    }
    
    
    /**
     * Returns the current date in the specified format as String
     * @return current date as String
     */
    public static String getCurrentDateAsStringValue(String format){
    	return getSimpleDateFormat(format).format(getCurrentDate().getTime());
    }
    
    /**
     * Returns the SimpleDateFormat of the current display locale
     * @return SimpleDateFormat
     */
    public static SimpleDateFormat getSimpleDateFormat(String format){
    	Locale currentLocale = Locale.getDefault(Locale.Category.DISPLAY);
    	SimpleDateFormat formatter = new SimpleDateFormat(format,currentLocale);
    	formatter.setLenient(false);
        return formatter;
    }
    
    /**
     * Returns the date in the specified format as String
     * @return date in the specified format as String
     */
    public static String formatDateAsStringValue(Date date, String format){
    	return getSimpleDateFormat(format).format(date.getTime());
    }
    
    /**
     * Returns the date object from the specified format
     * @return date object
     * @throws ParseException 
     */
    public static Date parseDate(String date, String format) throws ParseException{
    	SimpleDateFormat formatter = getSimpleDateFormat(format);
    	return formatter.parse(date);
    }
}



