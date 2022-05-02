package org.generationcp.middleware.api.config;

import org.generationcp.middleware.pojos.Config;

public class ConfigDTO {

	private String key;
	private String value;
	private String description;

	public ConfigDTO(final Config config) {
		this.key = config.getKey();
		this.value = config.getValue();
		this.description = config.getDescription();
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
