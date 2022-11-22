package org.generationcp.middleware.api.cropparameter;

public enum CropParameterEnum {
	BTYPE("btype"),
	DEFAULT_BRAPI_SYNC_SOURCE("default-brapi-sync-source"),
	DS_BRAPP_URL("ds_brapp_url"),
	STA_BRAPP_URL("sta_brapp_url"),
	GIGWA_TOKEN_ENDPOINT("gigwa_token_endpoint"),
	GIGWA_USERNAME("gigwa_username"),
	GIGWA_PASSWORD("gigwa_password");

	private final String key;

	CropParameterEnum(final String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}
}
