package org.generationcp.middleware.api.cropparameter;

public class CropParameterPatchRequestDTO {

	private String value;

	private Boolean isEncrypted;

	public Boolean isEncrypted() {
		return this.isEncrypted;
	}

	public void setEncrypted(final Boolean encrypted) {
		this.isEncrypted = encrypted;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(final String value) {
		this.value = value;
	}
}
