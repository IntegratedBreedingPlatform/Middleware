package org.generationcp.middleware.api.cropparameter;

import org.generationcp.middleware.pojos.CropParameter;

public class CropParameterDTO {

	private String key;
	private String value;
	private String description;
	private Boolean isEncrypted;

	public CropParameterDTO(final CropParameter cropParameter) {
		this.key = cropParameter.getKey();
		this.description = cropParameter.getDescription();
		this.isEncrypted = cropParameter.isEncrypted();

		if (this.isEncrypted) {
			this.value = cropParameter.getEncryptedValue();
		} else {
			this.value = cropParameter.getValue();
		}
	}

	public String getKey() {
		return this.key;
	}

	public void setKey(final String key) {
		this.key = key;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public Boolean isEncrypted() {
		return this.isEncrypted;
	}

	public void setIsEncrypted(final Boolean encrypted) {
		this.isEncrypted = encrypted;
	}
}
