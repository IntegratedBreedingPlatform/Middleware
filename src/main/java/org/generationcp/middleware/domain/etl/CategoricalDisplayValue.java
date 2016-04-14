package org.generationcp.middleware.domain.etl;

/**
 *
 */
public class CategoricalDisplayValue {
	private String value;
	private String name;
	private String description;
	private Boolean isValid;

	public CategoricalDisplayValue(String value, String name, String description) {
		this.value = value;
		this.name = name;
		this.description = description;
		this.isValid = true;
	}

	public CategoricalDisplayValue(String value, String name, String description, Boolean isValid) {
		this.value = value;
		this.name = name;
		this.description = description;
		this.isValid = isValid;
	}

	public String getValue() {
		return value;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Boolean isValid() {
		return isValid;
	}
}
