package org.generationcp.middleware.util;

public class ObjectTypeMapper {

	/**
	 * Parse hibernate query result value to boolean with null check
	 *
	 * @param val value
	 * @return boolean
	 */
	public static boolean mapToBoolean(Object val) {
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
	public static Integer mapToInteger(Object val) {
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
