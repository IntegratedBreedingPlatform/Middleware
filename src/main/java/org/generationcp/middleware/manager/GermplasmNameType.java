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

package org.generationcp.middleware.manager;

import java.util.HashMap;
import java.util.Map;

/**
 * The different types for germplasm names. Taken from the udflds table. The primary key is included for querying purposes.
 *
 * @author Kevin Manansala
 *
 */
public enum GermplasmNameType {
	GERMPLASM_BANK_ACCESSION_NUMBER(1), //
	CROSS_NAME(2), //
	UNNAMED_CROSS(3), //
	RELEASE_NAME(4), //
	DERIVATIVE_NAME(5), //
	CULTIVAR_NAME(6), //
	ABBREVIATED_CULTIVAR_NAME(7), //
	SPECIES_NAME(8), //
	COLLECTORS_NUMBER(9), //
	FOREIGN_ACCESSION_NUMBER(10), //
	INTERNATIONAL_TESTING_NUMBER(11), //
	NATIONAL_TESTING_NUMBER(12), //
	LINE_NAME(13), //
	TEMPORARY_ACCESSION_NUMBER_QUARANTINE_NUMBER(14), //
	ALTERNATIVE_DERIVATIVE_NAME(15), //
	ALTERNATIVE_CULTIVAR_NAME(16), //
	ALTERNATIVE_ABBREVIATION(17), //
	OLD_MUTANT_NAME_1(18), //
	OLD_MUTANT_NAME_2(19), //
	ELITE_LINES(20), //
	MANAGEMENT_NAME(21), //
	DONORS_ACCESSION_NUMBER(22), //
	LOCAL_COMMON_NAME_OF_WILD_RICE_SPECIES(23), //
	IRRI_HRDC_CODE(24), //
	GQNPC_UNIQUE_ID(25), //
	NATIONAL_RICE_COOPERATIVE_TESTING_PROJECT_ID(26), //
	TRANSGENIC_EVENT_ID(27), //
	ALTERNATE_CROSS_NAME(28), //
	CIAT_GERMPLASM_BANK_ACCESSION_NUMBER(1019), //
	UNRESOLVED_NAME(1027), //
	CIMMYT_SELECTION_HISTORY(1028), //
	CIMMYT_WHEAT_PEDIGREE(1029);

	private final int userDefinedField;
	private static Map<Integer, GermplasmNameType> values = new HashMap<>();

	private GermplasmNameType(int id) {
		this.userDefinedField = id;
	}

	public int getUserDefinedFieldID() {
		return this.userDefinedField;
	}

	public static GermplasmNameType valueOf(int userDefinedField) {
		if (GermplasmNameType.values.isEmpty()) {
			for (GermplasmNameType type : GermplasmNameType.values()) {
				GermplasmNameType.values.put(type.userDefinedField, type);
			}

		}

		if (GermplasmNameType.values.containsKey(userDefinedField)) {
			return GermplasmNameType.values.get(userDefinedField);
		} else {
			return null;
		}
	}
}
