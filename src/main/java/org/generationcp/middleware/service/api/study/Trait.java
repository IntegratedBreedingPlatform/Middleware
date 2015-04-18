package org.generationcp.middleware.service.api.study;

public class Trait {
	
	private final Integer traitId;
	
	private final String triatValue;

	
	public Trait(final Integer traitId, final String triatValue) {
		super();
		this.traitId = traitId;
		this.triatValue = triatValue;
	}

	public Integer getTraitId() {
		return traitId;
	}

	public String getTriatValue() {
		return triatValue;
	}
	
}
