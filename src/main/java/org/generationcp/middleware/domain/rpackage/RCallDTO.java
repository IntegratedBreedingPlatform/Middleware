package org.generationcp.middleware.domain.rpackage;

import java.util.Map;

public class RCallDTO {

	private Integer rCallId;
	private String description;
	private String endpoint;
	private Map<String, String> parameters;
	private boolean isAggregate;

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getEndpoint() {
		return this.endpoint;
	}

	public void setEndpoint(final String endpoint) {
		this.endpoint = endpoint;
	}

	public Map<String, String> getParameters() {
		return this.parameters;
	}

	public void setParameters(final Map<String, String> parameters) {
		this.parameters = parameters;
	}

	public boolean isAggregate() {
		return this.isAggregate;
	}

	public void setAggregate(final boolean aggregate) {
		this.isAggregate = aggregate;
	}

	public Integer getrCallId() {
		return this.rCallId;
	}

	public void setrCallId(final Integer rCallId) {
		this.rCallId = rCallId;
	}
}
