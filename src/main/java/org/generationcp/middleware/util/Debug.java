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

import java.lang.reflect.Field;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class Debug. Used to print debug information.
 */
public class Debug {

	private static final Logger LOG = LoggerFactory.getLogger(Debug.class);

	private static StringBuffer printIndent(int indent) {
		StringBuffer sb = new StringBuffer();
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
	public static void println(int indent, String s) {
		StringBuffer sb = Debug.printIndent(indent);
		sb.append(s);
		if (Debug.LOG.isDebugEnabled()) {
			Debug.LOG.debug(sb.toString());
		} else {
			System.out.println(sb.toString());
		}
	}

	public static void println(String s) {
		if (Debug.LOG.isDebugEnabled()) {
			Debug.LOG.debug(s.toString());
		} else {
			System.out.println(s.toString());
		}
	}

	/**
	 * Println. Prints the obj.toString() with the given indent, followed by a newline.
	 *
	 * @param indent the indent
	 * @param obj the object to print
	 */
	public static void println(int indent, Object obj) {
		Debug.println(indent, obj.toString());
	}

	public static void print(Object obj) {
		Debug.print(0, obj);
	}

	/**
	 * Prints the String s with the given indent.
	 *
	 * @param indent the indent
	 * @param s the String to print
	 */
	public static void print(int indent, String s) {
		StringBuffer sb = Debug.printIndent(indent);
		sb.append(s);
		if (Debug.LOG.isDebugEnabled()) {
			Debug.LOG.debug(sb.toString());
		} else {
			System.out.print(sb.toString());
		}
	}

	/**
	 * Prints obj.toString() with the given indent.
	 *
	 * @param indent the indent
	 * @param obj the object to print
	 */
	public static void print(int indent, Object obj) {
		Debug.print(indent, obj.toString());
	}

	/**
	 * Prints the formatted object - one line for each field.
	 *
	 * @param indent the indent
	 * @param obj the object to print
	 */
	public static void printFormattedObject(int indent, Object obj) {

		// Print class name
		Debug.println(indent, obj.getClass().getSimpleName() + ": ");

		// Print fields
		for (Field field : obj.getClass().getDeclaredFields()) {
			field.setAccessible(true); // to access private fields
			try {
				Debug.println(indent + 3, field.getName() + " = " + field.get(obj));
			} catch (IllegalArgumentException e) {
				if (Debug.LOG.isDebugEnabled()) {
					Debug.LOG.error(e.getMessage(), e);
				} else {
					e.printStackTrace();
				}
			} catch (IllegalAccessException e) {
				if (Debug.LOG.isDebugEnabled()) {
					Debug.LOG.error(e.getMessage(), e);
				} else {
					e.printStackTrace();
				}
			}
		}
	}

	public static void printFormattedObject(Object obj) {
		Debug.printFormattedObject(0, obj);
	}

	/**
	 * Prints multiple formatted objects.
	 *
	 * @param indent the indent
	 * @param objects the objects to print
	 */
	public static void printFormattedObjects(int indent, List<?> objects) {
		if (objects != null && objects.size() > 0) {
			for (Object obj : objects) {
				Debug.printFormattedObject(indent, obj);
			}
		}
		Debug.println(indent, "#RECORDS: " + (objects != null ? objects.size() : 0));
	}

	public static void printFormattedObjects(List<?> objects) {
		Debug.printFormattedObjects(0, objects);
	}

	/**
	 * Prints the obj.toString().
	 *
	 * @param indent the indent
	 * @param obj the object to print
	 */
	public static void printObject(int indent, Object obj) {
		Debug.println(indent, obj.toString());
	}

	public static void printObject(Object obj) {
		Debug.printObject(0, obj);
	}

	/**
	 * Prints the obj.toString() of the objects passed.
	 *
	 * @param indent the indent
	 * @param objects the objects to print
	 */
	public static void printObjects(int indent, List<?> objects) {
		if (objects != null && objects.size() > 0) {
			for (Object obj : objects) {
				Debug.printObject(indent, obj);
			}
		}
		Debug.println(indent, "#RECORDS: " + (objects != null ? objects.size() : 0));
	}

	public static void printObjects(List<?> objects) {
		Debug.printObjects(0, objects);
	}

}
