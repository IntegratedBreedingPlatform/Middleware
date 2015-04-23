package org.generationcp.middleware.service.api.study;

public class Trait {
	
	private final Integer traitId;
	
	private final String triatValue;

	private String traitName;

	
	public Trait(final String traitName, final Integer traitId, final String triatValue) {
		super();
		this.traitName = traitName;
		this.traitId = traitId;
		this.triatValue = triatValue;
	}

	public Integer getTraitId() {
		return traitId;
	}

	public String getTriatValue() {
		return triatValue;
	}

	public String getTraitName() {
		return traitName;
	}
	
	
}
