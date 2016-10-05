
package org.generationcp.middleware.domain.dms;

public class DesignTypeItem {
	
	// Constants for well known (e.g. BreedingView) design types.
	public static final DesignTypeItem RANDOMIZED_COMPLETE_BLOCK = new DesignTypeItem(0, "Randomized Complete Block Design",
			"randomizedCompleteBlockParams.html", false, 0, 0, false);

	public static final DesignTypeItem RESOLVABLE_INCOMPLETE_BLOCK = new DesignTypeItem(1, "Resolvable Incomplete Block Design",
			"incompleteBlockParams.html", false, 0, 0, false);
	
	public static final DesignTypeItem ROW_COL = new DesignTypeItem(2, "Row-and-Column", "rowAndColumnParams.html", false, 0, 0, false);
	
	public static final DesignTypeItem CUSTOM_IMPORT = new DesignTypeItem(3, "Custom Import Design", null, false, 0, 0, false);

	public static final DesignTypeItem AUGMENTED_RANDOMIZED_BLOCK = new DesignTypeItem(4, "Augmented Randomized Block design",
			"augmentedRandomizedBlockParams.html", false, 0, 0, false);

	public static final String ALPHA_LATTICE = "Alpha Lattice";

	private Integer id;
	private String name;

	// this is an html file that contains the specific fields under design type
	private String params;
	private Boolean isPreset;
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

	public DesignTypeItem(final Integer id, final String name, final String params, final Boolean isPreset,
			final Integer noOfReps, final Integer noOfEntries, final Boolean isDisabled) {
		this.id = id;
		this.name = name;
		this.params = params;
		this.isPreset = isPreset;
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

	public Boolean getIsPreset() {
		return this.isPreset;
	}

	public void setIsPreset(final Boolean isPreset) {
		this.isPreset = isPreset;
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
