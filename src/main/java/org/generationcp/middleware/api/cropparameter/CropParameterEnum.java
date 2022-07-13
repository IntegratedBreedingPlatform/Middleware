package org.generationcp.middleware.api.cropparameter;

public enum CropParameterEnum {
	BTYPE("btype"),
	DEFAULT_BRAPI_SYNC_SOURCE("default-brapi-sync-source");

	private final String key;

	CropParameterEnum(final String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}
}
