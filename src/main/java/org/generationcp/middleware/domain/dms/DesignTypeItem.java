
package org.generationcp.middleware.domain.dms;

public class DesignTypeItem {
	
	// Constants for well known (e.g. BreedingView) design types.
	public static final DesignTypeItem RANDOMIZED_COMPLETE_BLOCK = new DesignTypeItem(0, "Randomized Complete Block Design",
			"randomizedCompleteBlockParams.html", 0, 0, false);

	public static final DesignTypeItem RESOLVABLE_INCOMPLETE_BLOCK = new DesignTypeItem(1, "Resolvable Incomplete Block Design",
			"incompleteBlockParams.html", 0, 0, false);
	
	public static final DesignTypeItem ROW_COL = new DesignTypeItem(2, "Row-and-Column", "rowAndColumnParams.html", 0, 0, false);
	
	public static final DesignTypeItem CUSTOM_IMPORT = new DesignTypeItem(3, "Custom Import Design", null, 0, 0, false);

	public static final DesignTypeItem AUGMENTED_RANDOMIZED_BLOCK = new DesignTypeItem(4, "Augmented Randomized Block design",
			"augmentedRandomizedBlockParams.html", 0, 0, false);

	public static final DesignTypeItem ENTRY_LIST_ORDER = new DesignTypeItem(5, "Entry list order",
			"entryListOrderParams.html", 0, 0, false);

	public static final DesignTypeItem P_REP = new DesignTypeItem(6, "P-Rep Design",
		"pRepParams.html", 0, 0, false);

	public static final String ALPHA_LATTICE = "Alpha Lattice";

	private Integer id;
	private String name;

	// this is an html file that contains the specific fields under design type
	private String params;
	private Integer repNo;
	private Integer totalNoOfEntries;
	private Boolean isDisabled;
	private String templateName;

	public DesignTypeItem() {
		// do nothing
	}

	public DesignTypeItem(final int id) {
		this.id = id;
	}

	public DesignTypeItem(final Integer id, final String name, final String params,
			final Integer noOfReps, final Integer noOfEntries, final Boolean isDisabled) {
		this.id = id;
		this.name = name;
		this.params = params;
		this.repNo = noOfReps;
		this.totalNoOfEntries = noOfEntries;
		this.isDisabled = isDisabled;
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

	public Boolean getIsDisabled() {
		return this.isDisabled;
	}

	public void setIsDisabled(final Boolean isDisabled) {
		this.isDisabled = isDisabled;
	}

}
