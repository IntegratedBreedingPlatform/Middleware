
package org.generationcp.middleware.domain.dms;

import org.generationcp.middleware.domain.oms.TermId;

public enum DesignType {

	// Constants for well known (e.g. BreedingView) design types.
	RANDOMIZED_COMPLETE_BLOCK(0, "Randomized Complete Block Design",
		TermId.RANDOMIZED_COMPLETE_BLOCK.getId(), 0,
		"randomizedCompleteBlockParams.html", 0, 0),

	RESOLVABLE_INCOMPLETE_BLOCK(1, "Resolvable Incomplete Block Design", TermId.RESOLVABLE_INCOMPLETE_BLOCK.getId(),
		TermId.RESOLVABLE_INCOMPLETE_BLOCK_LATIN.getId(),
		"incompleteBlockParams.html", 0, 0),

	ROW_COL(2, "Row-and-Column", TermId.RESOLVABLE_INCOMPLETE_ROW_COL.getId(),
		TermId.RESOLVABLE_INCOMPLETE_ROW_COL_LATIN.getId(), "rowAndColumnParams.html", 0, 0),

	CUSTOM_IMPORT(3, "Custom Import Design", TermId.OTHER_DESIGN.getId(), 0, null, 0, 0),

	AUGMENTED_RANDOMIZED_BLOCK(4, "Augmented Randomized Block design", TermId.AUGMENTED_RANDOMIZED_BLOCK.getId(), 0,
		"augmentedRandomizedBlockParams.html", 0, 0),

	ENTRY_LIST_ORDER(5, "Entry list order", TermId.ENTRY_LIST_ORDER.getId(), 0,
		"entryListOrderParams.html", 0, 0),

	P_REP(6, "P-Rep Design", TermId.P_REP.getId(), 0,
		"pRepParams.html", 0, 0);

	public static final String ALPHA_LATTICE = "Alpha Lattice";

	private Integer id;
	private String name;
	private Integer termId;
	private Integer termIdLatinized;

	// this is an html file that contains the specific fields under design type
	private String params;
	private Integer repNo;
	private Integer totalNoOfEntries;
	private String templateName;

	public static int getTermIdByDesignTypeId(final int designTypeId, final Boolean isLatinized) {
		for (final DesignType item : DesignType.values()) {
			if (item.getId() == designTypeId) {
				return isLatinized ? item.getTermIdLatinized() : item.getTermId();
			}
		}
		return 0;
	}

	public static DesignType getDesignTypeItemByTermId(final int termId) {
		for (final DesignType item : DesignType.values()) {
			if (item.getTermId().intValue() == termId || item.getTermIdLatinized().intValue() == termId) {
				return item;
			}
		}
		return null;
	}

	public static boolean isLatinized(final int termId) {
		return TermId.RESOLVABLE_INCOMPLETE_BLOCK_LATIN.getId() == termId ||
			TermId.RESOLVABLE_INCOMPLETE_ROW_COL_LATIN.getId() == termId;
	}

	DesignType(
		final Integer id, final String name, final Integer termId, final Integer termIdLatinized, final String params,
		final Integer noOfReps, final Integer noOfEntries) {
		this.id = id;
		this.name = name;
		this.termId = termId;
		this.termIdLatinized = termIdLatinized;
		this.params = params;
		this.repNo = noOfReps;
		this.totalNoOfEntries = noOfEntries;
		this.templateName = name.concat(".csv");
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getTemplateName() {
		return this.templateName;
	}

	public String getParams() {
		return this.params;
	}

	public void setParams(final String params) {
		this.params = params;
	}

	public Integer getRepNo() {
		return this.repNo;
	}

	public void setRepNo(final Integer repNo) {
		this.repNo = repNo;
	}

	public Integer getTotalNoOfEntries() {
		return this.totalNoOfEntries;
	}

	public void setTotalNoOfEntries(final Integer totalNoOfEntries) {
		this.totalNoOfEntries = totalNoOfEntries;
	}

	public Integer getTermId() {
		return this.termId;
	}

	public void setTermId(final Integer termId) {
		this.termId = termId;
	}

	public Integer getTermIdLatinized() {
		return this.termIdLatinized;
	}

	public void setTermIdLatinized(final Integer termIdLatinized) {
		this.termIdLatinized = termIdLatinized;
	}

}
