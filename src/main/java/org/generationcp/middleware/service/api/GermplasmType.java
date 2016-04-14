package org.generationcp.middleware.service.api;

// TODO spec talks about deriving germplasm type based on breeding methods. Each type is a set of methods to be defined in config.

public enum GermplasmType {

	H("Hybrid"),
	L("Line"),
	P("Open Pollinated Varieties/Populations");

	private final String description;

	private GermplasmType(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
