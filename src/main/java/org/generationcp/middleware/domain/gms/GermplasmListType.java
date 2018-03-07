
package org.generationcp.middleware.domain.gms;

public enum GermplasmListType {

	LST, CHECK, STUDY, ADVANCED, CROSSES, STOCK, F1,
	/**
	 * Imported cross list from a study
	 */
	IMP_CROSS,
	/**
	 * Designed cross list from a study
	 */
	CRT_CROSS,
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
			|| IMP_CROSS.equals(type)
			|| CRT_CROSS.equals(type);
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
