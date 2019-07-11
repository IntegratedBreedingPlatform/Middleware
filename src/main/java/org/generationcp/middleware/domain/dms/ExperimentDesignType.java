
package org.generationcp.middleware.domain.dms;

import com.google.common.collect.ImmutableList;
import org.generationcp.middleware.domain.oms.TermId;

import java.util.List;

public class ExperimentDesignType {

	// Constants for well known (e.g. BreedingView) design types.
	public static final ExperimentDesignType RANDOMIZED_COMPLETE_BLOCK = new ExperimentDesignType(0, "Randomized Complete Block Design",
		TermId.RANDOMIZED_COMPLETE_BLOCK.getId(), 0,
		"randomizedCompleteBlockParams.html", 0, 0, "Randomized block design");

	public static final ExperimentDesignType RESOLVABLE_INCOMPLETE_BLOCK =
		new ExperimentDesignType(1, "Resolvable Incomplete Block Design", TermId.RESOLVABLE_INCOMPLETE_BLOCK.getId(),
			TermId.RESOLVABLE_INCOMPLETE_BLOCK_LATIN.getId(),
			"incompleteBlockParams.html", 0, 0, "Resolvable incomplete block design");

	public static final ExperimentDesignType ROW_COL =
		new ExperimentDesignType(2, "Row-and-Column", TermId.RESOLVABLE_INCOMPLETE_ROW_COL.getId(),
			TermId.RESOLVABLE_INCOMPLETE_ROW_COL_LATIN.getId(), "rowAndColumnParams.html", 0, 0, "Resolvable row-column design");

	public static final ExperimentDesignType
		CUSTOM_IMPORT = new ExperimentDesignType(3, "Custom Import Design", TermId.OTHER_DESIGN.getId(), 0, null, 0, 0, "");

	// Augmented design is just a variation of the Incomplete Block Design type.
	public static final ExperimentDesignType AUGMENTED_RANDOMIZED_BLOCK =
		new ExperimentDesignType(4, "Augmented Randomized Block design", TermId.AUGMENTED_RANDOMIZED_BLOCK.getId(), 0,
			"augmentedRandomizedBlockParams.html", 0, 0, "Incomplete block design");

	public static final ExperimentDesignType
		ENTRY_LIST_ORDER = new ExperimentDesignType(5, "Entry list order", TermId.ENTRY_LIST_ORDER.getId(), 0,
		"entryListOrderParams.html", 0, 0, "");

	public static final ExperimentDesignType P_REP = new ExperimentDesignType(6, "P-Rep Design", TermId.P_REP.getId(), 0,
		"pRepParams.html", 0, 0, "P-rep design");

	private static final List<ExperimentDesignType> values = ImmutableList
		.of(RANDOMIZED_COMPLETE_BLOCK, RESOLVABLE_INCOMPLETE_BLOCK, ROW_COL, CUSTOM_IMPORT, AUGMENTED_RANDOMIZED_BLOCK, ENTRY_LIST_ORDER,
			P_REP);

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

	// The name of the design that the system sends to Breeding View. This name is constant and defined by the Breeding View application.
	private String bvDesignName;

	public ExperimentDesignType() {
		// do nothing
	}

	public static int getTermIdByDesignTypeId(final int designTypeId, final Boolean isLatinized) {
		for (final ExperimentDesignType experimentDesignType : values) {
			if (experimentDesignType.getId().intValue() == designTypeId) {
				return isLatinized == Boolean.TRUE ? experimentDesignType.getTermIdLatinized() : experimentDesignType.getTermId();
			}
		}
		return 0;
	}

	public static ExperimentDesignType getDesignTypeItemByTermId(final int termId) {
		for (final ExperimentDesignType experimentDesignType : values) {
			if (experimentDesignType.getTermId().intValue() == termId || experimentDesignType.getTermIdLatinized().intValue() == termId) {
				return experimentDesignType;
			}
		}
		return null;
	}

	public static boolean isLatinized(final int termId) {
		return TermId.RESOLVABLE_INCOMPLETE_BLOCK_LATIN.getId() == termId ||
			TermId.RESOLVABLE_INCOMPLETE_ROW_COL_LATIN.getId() == termId;
	}

	public static ExperimentDesignType getExperimentDesignTypeByBVDesignName(final String bvDesignName) {

		for (final ExperimentDesignType experimentDesignType : values) {
			if (experimentDesignType.getBvDesignName().equals(bvDesignName)) {
				return experimentDesignType;
			}
		}

		// if there is no match found in enum constants, throw an exception
		throw new IllegalArgumentException("No ExperimentDesignType constant found for name: \"" + bvDesignName + "\"");
	}

	public ExperimentDesignType(final int id) {
		this.id = id;
	}

	public ExperimentDesignType(
		final Integer id, final String name, final Integer termId, final Integer termIdLatinized, final String params,
		final Integer noOfReps, final Integer noOfEntries, final String bvDesignName) {
		this.id = id;
		this.name = name;
		this.termId = termId;
		this.termIdLatinized = termIdLatinized;
		this.params = params;
		this.repNo = noOfReps;
		this.totalNoOfEntries = noOfEntries;
		this.templateName = name.concat(".csv");
		this.bvDesignName = bvDesignName;
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

	public String getBvDesignName() {
		return this.bvDesignName;
	}

}
