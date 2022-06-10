package org.generationcp.middleware.service.impl.study;

import org.generationcp.middleware.domain.oms.TermId;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Stream;

public enum StudyEntryDescriptorColumns {

	GID(TermId.GID, TermId.GID.name(), 1),
	GUID(TermId.GUID, TermId.GUID.name(), 2),
	DESIG(TermId.DESIG, "DESIGNATION", 3),
	CROSS(TermId.CROSS, TermId.CROSS.name(), 4),
	GROUPGID(TermId.GROUPGID, TermId.GROUPGID.name(), 5),
	IMMEDIATE_SOURCE_NAME(TermId.IMMEDIATE_SOURCE_NAME, TermId.IMMEDIATE_SOURCE_NAME.name(), 6),
	GROUP_SOURCE_NAME(TermId.GROUP_SOURCE_NAME, TermId.GROUP_SOURCE_NAME.name(), 7);

	private final TermId termId;
	private final String name;
	private final int rank;

	StudyEntryDescriptorColumns(final TermId termId, final String name, final int rank) {
		this.termId = termId;
		this.name = name;
		this.rank = rank;
	}

	public int getId() {
		return termId.getId();
	}

	public String getName() {
		return name;
	}

	public int getRank() {
		return rank;
	}

	public static StudyEntryDescriptorColumns getByTermId(final int termId) {
		return Arrays.stream(StudyEntryDescriptorColumns.values())
			.filter(c -> c.getId() == termId)
			.findFirst()
			.orElse(null);
	}

	public static Stream<StudyEntryDescriptorColumns> getColumnsSortedByRank() {
		return Arrays.stream(StudyEntryDescriptorColumns.values())
			.sorted(Comparator.comparingInt(StudyEntryDescriptorColumns::getRank));
	}

}
