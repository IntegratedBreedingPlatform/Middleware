package org.generationcp.middleware.api.cropparameter;

public enum CropParameterEnum {
	BTYPE("btype");

	private final String key;

	CropParameterEnum(final String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}
}
