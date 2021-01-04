package org.generationcp.middleware.service.api.study;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
@JsonInclude(JsonInclude.Include.NON_NULL) @JsonPropertyOrder({"PUI", "description"})
public class ExperimentalDesign {
	private String PUI;

	private String description;

	public ExperimentalDesign() {
	}

	public ExperimentalDesign(final String PUI, final String description) {
		this.PUI = PUI;
		this.description = description;
	}

	public void setPUI(final String PUI) {
		this.PUI = PUI;
	}

	public String getPUI() {
		return this.PUI;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}
}
