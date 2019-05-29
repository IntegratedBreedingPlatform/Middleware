
package org.generationcp.middleware.domain.dms;

import org.generationcp.middleware.domain.oms.TermId;

public class ExperimentDesignType {

	// Constants for well known (e.g. BreedingView) design types.
	public static final ExperimentDesignType RANDOMIZED_COMPLETE_BLOCK = new ExperimentDesignType(0, "Randomized Complete Block Design",
		TermId.RANDOMIZED_COMPLETE_BLOCK.getId(), 0,
		"randomizedCompleteBlockParams.html", 0, 0);

	public static final ExperimentDesignType RESOLVABLE_INCOMPLETE_BLOCK =
		new ExperimentDesignType(1, "Resolvable Incomplete Block Design", TermId.RESOLVABLE_INCOMPLETE_BLOCK.getId(),
			TermId.RESOLVABLE_INCOMPLETE_BLOCK_LATIN.getId(),
			"incompleteBlockParams.html", 0, 0);

	public static final ExperimentDesignType ROW_COL = new ExperimentDesignType(2, "Row-and-Column", TermId.RESOLVABLE_INCOMPLETE_ROW_COL.getId(),
		TermId.RESOLVABLE_INCOMPLETE_ROW_COL_LATIN.getId(), "rowAndColumnParams.html", 0, 0);

	public static final ExperimentDesignType
		CUSTOM_IMPORT = new ExperimentDesignType(3, "Custom Import Design", TermId.OTHER_DESIGN.getId(), 0, null, 0, 0);

	public static final ExperimentDesignType AUGMENTED_RANDOMIZED_BLOCK =
		new ExperimentDesignType(4, "Augmented Randomized Block design", TermId.AUGMENTED_RANDOMIZED_BLOCK.getId(), 0,
			"augmentedRandomizedBlockParams.html", 0, 0);

	public static final ExperimentDesignType
		ENTRY_LIST_ORDER = new ExperimentDesignType(5, "Entry list order", TermId.ENTRY_LIST_ORDER.getId(), 0,
		"entryListOrderParams.html", 0, 0);

	public static final ExperimentDesignType P_REP = new ExperimentDesignType(6, "P-Rep Design", TermId.P_REP.getId(), 0,
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

	public ExperimentDesignType() {
		// do nothing
	}

	public static int getTermIdByDesignTypeId(final int designTypeId, final Boolean isLatinized) {
		if (designTypeId == 0) {
			return RANDOMIZED_COMPLETE_BLOCK.getTermId();
		}
		if (designTypeId == 1) {
			return isLatinized ? RESOLVABLE_INCOMPLETE_BLOCK.getTermIdLatinized() : RESOLVABLE_INCOMPLETE_BLOCK.getTermId();
		}
		if (designTypeId == 2) {
			return isLatinized ? ROW_COL.getTermIdLatinized() : ROW_COL.getTermId();
		}
		if (designTypeId == 3) {
			return CUSTOM_IMPORT.getTermId();
		}
		if (designTypeId == 4) {
			return AUGMENTED_RANDOMIZED_BLOCK.getTermId();
		}
		if (designTypeId == 5) {
			return ENTRY_LIST_ORDER.getTermId();
		}
		if (designTypeId == 6) {
			return P_REP.getTermId();
		}
		return 0;
	}

	public static ExperimentDesignType getDesignTypeItemByTermId(final int termId) {
		if (TermId.RANDOMIZED_COMPLETE_BLOCK.getId() == termId) {
			return RANDOMIZED_COMPLETE_BLOCK;
		}
		if (TermId.RESOLVABLE_INCOMPLETE_BLOCK.getId() == termId || TermId.RESOLVABLE_INCOMPLETE_BLOCK_LATIN.getId() == termId) {
			return RESOLVABLE_INCOMPLETE_BLOCK;
		}
		if (TermId.RESOLVABLE_INCOMPLETE_ROW_COL.getId() == termId || TermId.RESOLVABLE_INCOMPLETE_ROW_COL_LATIN.getId() == termId) {
			return ROW_COL;
		}
		if (TermId.OTHER_DESIGN.getId() == termId) {
			return CUSTOM_IMPORT;
		}
		if (TermId.AUGMENTED_RANDOMIZED_BLOCK.getId() == termId) {
			return AUGMENTED_RANDOMIZED_BLOCK;
		}
		if (TermId.ENTRY_LIST_ORDER.getId() == termId) {
			return ENTRY_LIST_ORDER;
		}
		if (TermId.P_REP.getId() == termId) {
			return P_REP;
		}
		return null;
	}

	public static boolean isLatinized(final int termId) {
		return TermId.RESOLVABLE_INCOMPLETE_BLOCK_LATIN.getId() == termId ||
			TermId.RESOLVABLE_INCOMPLETE_ROW_COL_LATIN.getId() == termId;
	}

	public ExperimentDesignType(final int id) {
		this.id = id;
	}

	public ExperimentDesignType(
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
