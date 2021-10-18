package org.generationcp.middleware.api.germplasmlist.data;

import org.generationcp.middleware.domain.oms.TermId;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Stream;

public enum GermplasmListStaticColumns {

	ENTRY_NO("ENTRY_NO", TermId.ENTRY_NO, 1),
	GID("GID", TermId.GID, 2),
	DESIGNATION("DESIGNATION", TermId.DESIG, 3),
	LOTS("LOTS", TermId.GID_ACTIVE_LOTS_COUNT, 4),
	AVAILABLE("AVAILABLE", TermId.GID_AVAILABLE_BALANCE, 5),
	UNIT("UNIT", TermId.GID_UNIT, 6),
	ENTRY_CODE("ENTRY CODE", TermId.ENTRY_CODE, 7),
	GROUP_ID("GROUP ID", TermId.GROUP_ID, 8),
	IMMEDIATE_SOURCE_GID("IMMEDIATE SOURCE GID", TermId.IMMEDIATE_SOURCE_GID, 9),
	IMMEDIATE_SOURCE_NAME("IMMEDIATE SOURCE NAME", TermId.IMMEDIATE_SOURCE_NAME, 10),
	GROUP_SOURCE_GID("GROUP SOURCE GID", TermId.GROUP_SOURCE_GID, 11),
	GROUP_SOURCE_NAME("GROUP SOURCE NAME", TermId.GROUP_SOURCE_NAME, 12),
	CROSS("CROSS", TermId.CROSS, 13),
	FEMALE_PARENT_GID("FEMALE PARENT GID", TermId.FGID, 14),
	FEMALE_PARENT_NAME("FEMALE PARENT NAME", TermId.FEMALE_PARENT, 15),
	MALE_PARENT_GID("MALE PARENT GID", TermId.MGID, 16),
	MALE_PARENT_NAME("MALE PARENT NAME", TermId.MALE_PARENT, 17),
	BREEDING_METHOD_PREFERRED_NAME("BREEDING METHOD PREFERRED NAME", TermId.BREEDING_METHOD_NAME, 18),
	BREEDING_METHOD_ABBREVIATION("BREEDING METHOD ABBREVIATION", TermId.BREEDING_METHOD_ABBREVIATION, 19),
	BREEDING_METHOD_GROUP("BREEDING METHOD GROUP", TermId.BREEDING_METHOD_GROUP, 20),
	GUID("GUID", TermId.GUID, 21),
	LOCATION_NAME("LOCATION NAME", TermId.GERMPLASM_LOCATION, 22),
	LOCATION_ABBREVIATION("LOCATION ABBREVIATION", TermId.LOCATION_ABBR, 23),
	GERMPLASM_DATE("GERMPLASM DATE", TermId.GERMPLASM_DATE, 24),
	GERMPLASM_REFERENCE("REFERENCE", TermId.GERMPLASM_REFERENCE, 25);

	private final String name;
	private final TermId termId;
	private final int rank;

	GermplasmListStaticColumns(final String name, final TermId termId, final int rank) {
		this.name = name;
		this.termId = termId;
		this.rank = rank;
	}

	public String getName() {
		return name;
	}

	public Integer getTermId() {
		return termId.getId();
	}

	public int getRank() {
		return rank;
	}

	public static GermplasmListStaticColumns getByTermId(final int termId) {
		return Arrays.stream(GermplasmListStaticColumns.values())
			.filter(c -> c.termId.getId() == termId)
			.findFirst()
			.orElseThrow(() -> new IllegalStateException(String.format("There is no a static columns with termId %s.", termId)));
	}

	public static GermplasmListStaticColumns getByName(final String name) {
		return Arrays.stream(GermplasmListStaticColumns.values())
			.filter(c -> c.getName().equalsIgnoreCase(name))
			.findFirst()
			.orElseThrow(() -> new IllegalStateException(String.format("There is no a static columns with name %s.", name)));
	}

	public static Stream<GermplasmListStaticColumns> getColumnsSortedByRank() {
		return Arrays.stream(GermplasmListStaticColumns.values())
			.sorted(Comparator.comparingInt(GermplasmListStaticColumns::getRank));
	}

}
