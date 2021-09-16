package org.generationcp.middleware.api.germplasmlist;

import org.generationcp.middleware.domain.oms.TermId;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum GermplasmListStaticColumns {

	ENTRY_NO("ENTRY_NO", TermId.ENTRY_NO, true),
	GID("GID", TermId.GID, true),
	DESIGNATION("DESIGNATION", TermId.DESIG, true),
	LOTS("LOTS", TermId.GID_ACTIVE_LOTS_COUNT, true),
	AVAILABLE("AVAILABLE", TermId.GID_AVAILABLE_BALANCE, true),
	UNIT("UNIT", TermId.GID_UNIT, true),
	IMMEDIATE_SOURCE_GID("IMMEDIATE SOURCE GID", TermId.IMMEDIATE_SOURCE_GID),
	IMMEDIATE_SOURCE_NAME("IMMEDIATE SOURCE NAME", TermId.IMMEDIATE_SOURCE_NAME),
	GROUP_SOURCE_GID("GROUP SOURCE GID", TermId.GROUP_SOURCE_GID),
	GROUP_SOURCE_NAME("GROUP SOURCE NAME", TermId.GROUP_SOURCE_NAME),
	CROSS("CROSS", TermId.CROSS),
	FEMALE_PARENT_GID("FEMALE PARENT GID", TermId.FGID),
	FEMALE_PARENT_NAME("FEMALE PARENT NAME", TermId.FEMALE_PARENT),
	MALE_PARENT_GID("MALE PARENT GID", TermId.MGID),
	MALE_PARENT_NAME("MALE PARENT NAME", TermId.MALE_PARENT),
	BREEDING_METHOD_PREFERRED_NAME("BREEDING METHOD PREFERRED NAME", TermId.BREEDING_METHOD_NAME),
	BREEDING_METHOD_ABBREVIATION("BREEDING METHOD ABBREVIATION", TermId.BREEDING_METHOD_ABBREVIATION),
	BREEDING_METHOD_GROUP("BREEDING METHOD GROUP", TermId.BREEDING_METHOD_GROUP),
	GUID("GUID", TermId.GUID),
	LOCATION_NAME("LOCATION NAME", TermId.GERMPLASM_LOCATION),
	LOCATION_ABBREVIATION("LOCATION ABBREVIATION", TermId.LOCATION_ABBR),
	GERMPLASM_DATE("GERMPLASM DATE", TermId.GERMPLASM_DATE),
	GERMPLASM_REFERENCE("REFERENCE", TermId.GERMPLASM_REFERENCE);

	private final String name;
	private final TermId termId;
	private final boolean isDefault;

	GermplasmListStaticColumns(final String name, final TermId termId) {
		this.name = name;
		this.termId = termId;
		this.isDefault = false;
	}

	GermplasmListStaticColumns(final String name, final TermId termId, final boolean isDefault) {
		this.name = name;
		this.termId = termId;
		this.isDefault = isDefault;
	}

	public String getName() {
		return name;
	}

	public TermId getTermId() {
		return termId;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public static List<GermplasmListStaticColumns> getDefaultColumns() {
		return Arrays.stream(GermplasmListStaticColumns.values())
			.filter(GermplasmListStaticColumns::isDefault)
			.collect(Collectors.toList());
	}

	public static String getColumnNameByTermId(int termId) {
		return Arrays.stream(GermplasmListStaticColumns.values())
			.filter(c -> c.termId.getId() == termId)
			.findFirst()
			.map(GermplasmListStaticColumns::getName)
			.orElseThrow(() -> new IllegalStateException(String.format("There is no a static columns with termId %s.", termId)));
	}

}
