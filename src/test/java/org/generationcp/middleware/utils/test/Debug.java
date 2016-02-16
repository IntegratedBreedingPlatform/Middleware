/*******************************************************************************
 * Copyright (c) 2014, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.utils.test;

import java.lang.reflect.Field;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class Debug. Used to print debug information.
 */

public class Debug {

	private static final Logger LOG = LoggerFactory.getLogger(Debug.class);

	private static StringBuffer printIndent(final int indent) {
		final StringBuffer sb = new StringBuffer();
		for (int i = 0; i < indent; i++) {
			sb.append(" ");
		}
		return sb;
	}

	/**
	 * Println. Prints the given String with the given indent, followed by a newline.
	 *
	 * @param indent the indent
	 * @param s the string to print
	 */
	public static void println(final int indent, final String s) {
		final StringBuffer sb = Debug.printIndent(indent);
		sb.append(s);
		Debug.LOG.debug(sb.toString());
	}

	public static void println(final String s) {
		Debug.println(0, s);
	}

	/**
	 * Println. Prints the obj.toString() with the given indent, followed by a newline.
	 *
	 * @param indent the indent
	 * @param obj the object to print
	 */
	public static void println(final int indent, final Object obj) {
		Debug.println(indent, obj.toString());
	}

	public static void print(final Object obj) {
		Debug.print(0, obj);
	}

	/**
	 * Prints the String s with the given indent.
	 *
	 * @param indent the indent
	 * @param s the String to print
	 */
	public static void print(final int indent, final String s) {
		final StringBuffer sb = Debug.printIndent(indent);
		sb.append(s);
		Debug.LOG.debug(sb.toString());
	}

	/**
	 * Prints obj.toString() with the given indent.
	 *
	 * @param indent the indent
	 * @param obj the object to print
	 */
	public static void print(final int indent, final Object obj) {
		Debug.print(indent, obj.toString());
	}

	/**
	 * Prints the formatted object - one line for each field.
	 *
	 * @param indent the indent
	 * @param obj the object to print
	 */
	public static void printFormattedObject(final int indent, final Object obj) {

		// Print class name
		Debug.println(indent, obj.getClass().getSimpleName() + ": ");

		// Print fields
		for (final Field field : obj.getClass().getDeclaredFields()) {
			field.setAccessible(true); // to access private fields
			try {
				Debug.println(indent + 3, field.getName() + " = " + field.get(obj));
			} catch (final IllegalArgumentException e) {
				Debug.LOG.error("Illegal argument to print formatted object", e);
			} catch (final IllegalAccessException e) {
				Debug.LOG.error("Illegal access to print formatted object", e);
			}
		}
	}

	public static void printFormattedObject(final Object obj) {
		Debug.printFormattedObject(0, obj);
	}

	/**
	 * Prints multiple formatted objects.
	 *
	 * @param indent the indent
	 * @param objects the objects to print
	 */
	public static void printFormattedObjects(final int indent, final List<?> objects) {
		if (objects != null && objects.size() > 0) {
			for (final Object obj : objects) {
				Debug.printFormattedObject(indent, obj);
			}
		}
		Debug.println(indent, "#RECORDS: " + (objects != null ? objects.size() : 0));
	}

	public static void printFormattedObjects(final List<?> objects) {
		Debug.printFormattedObjects(0, objects);
	}

	/**
	 * Prints the obj.toString().
	 *
	 * @param indent the indent
	 * @param obj the object to print
	 */
	public static void printObject(final int indent, final Object obj) {
		Debug.println(indent, obj.toString());
	}

	public static void printObject(final Object obj) {
		Debug.printObject(0, obj);
	}

	/**
	 * Prints the obj.toString() of the objects passed.
	 *
	 * @param indent the indent
	 * @param objects the objects to print
	 */
	public static void printObjects(final int indent, final List<?> objects) {
		if (objects != null && objects.size() > 0) {
			for (final Object obj : objects) {
				Debug.printObject(indent, obj);
			}
		}
		Debug.println(indent, "#RECORDS: " + (objects != null ? objects.size() : 0));
	}

	public static void printObjects(final List<?> objects) {
		Debug.printObjects(0, objects);
	}

}
