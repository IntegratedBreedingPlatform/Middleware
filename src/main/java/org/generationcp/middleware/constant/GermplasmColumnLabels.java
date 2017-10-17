package org.generationcp.middleware.constant;

import java.util.ArrayList;
import java.util.List;

public class GermplasmColumnLabels {

	public static List<String> ADDABLE_PROPERTY_IDS;

	static {
		GermplasmColumnLabels.ADDABLE_PROPERTY_IDS = new ArrayList<>();

		GermplasmColumnLabels.ADDABLE_PROPERTY_IDS.add(ColumnLabels.PREFERRED_ID.getName());
		GermplasmColumnLabels.ADDABLE_PROPERTY_IDS.add(ColumnLabels.PREFERRED_NAME.getName());
		GermplasmColumnLabels.ADDABLE_PROPERTY_IDS.add(ColumnLabels.GERMPLASM_DATE.getName());
		GermplasmColumnLabels.ADDABLE_PROPERTY_IDS.add(ColumnLabels.GERMPLASM_LOCATION.getName());
		GermplasmColumnLabels.ADDABLE_PROPERTY_IDS.add(ColumnLabels.BREEDING_METHOD_NAME.getName());
		GermplasmColumnLabels.ADDABLE_PROPERTY_IDS.add(ColumnLabels.BREEDING_METHOD_ABBREVIATION.getName());
		GermplasmColumnLabels.ADDABLE_PROPERTY_IDS.add(ColumnLabels.BREEDING_METHOD_NUMBER.getName());
		GermplasmColumnLabels.ADDABLE_PROPERTY_IDS.add(ColumnLabels.BREEDING_METHOD_GROUP.getName());
		GermplasmColumnLabels.ADDABLE_PROPERTY_IDS.add(ColumnLabels.CROSS_FEMALE_GID.getName());
		GermplasmColumnLabels.ADDABLE_PROPERTY_IDS.add(ColumnLabels.CROSS_FEMALE_PREFERRED_NAME.getName());
		GermplasmColumnLabels.ADDABLE_PROPERTY_IDS.add(ColumnLabels.CROSS_MALE_GID.getName());
		GermplasmColumnLabels.ADDABLE_PROPERTY_IDS.add(ColumnLabels.CROSS_MALE_PREFERRED_NAME.getName());
	}


}
