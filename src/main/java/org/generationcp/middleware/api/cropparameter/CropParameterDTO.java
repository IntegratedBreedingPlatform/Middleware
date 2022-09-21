package org.generationcp.middleware.api.cropparameter;

import org.generationcp.middleware.pojos.CropParameter;

public class CropParameterDTO {

	private String key;
	private String value;
	private String description;

	public CropParameterDTO(final CropParameter cropParameter) {
		this.key = cropParameter.getKey();
		this.value = cropParameter.getValue();
		this.description = cropParameter.getDescription();
	}

	public String getKey() {
		return key;
	}

	public void setKey(final String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}
}
