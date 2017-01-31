
package org.generationcp.middleware.domain.gms;

public enum GermplasmListType {

	LST, NURSERY, CHECK, TRIAL, ADVANCED, CROSSES, STOCK, F1,
	/**
	 * Imported F1 list
	 */
	F1IMP,
	/**
	 * Crossing tool F1 list
	 */
	F1CRT,
	/**
	 * Crossing tool parent list
	 */
	PLCRT;

	public static boolean isCrosses(GermplasmListType type) {
		return CROSSES.equals(type)
			|| F1CRT.equals(type)
			|| F1IMP.equals(type);
	}

	public static boolean isCrosses(String type) {
		GermplasmListType germplasmListType;
		try {
			germplasmListType = GermplasmListType.valueOf(type);
		} catch (IllegalArgumentException e) {
			return false;
		}
		return isCrosses(germplasmListType);
	}
}
