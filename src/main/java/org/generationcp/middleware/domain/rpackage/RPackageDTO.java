package org.generationcp.middleware.domain.rpackage;

public class RPackageDTO {

	public RPackageDTO() {

	}

	public RPackageDTO(final String endpoint, final String description) {
		this.endpoint = endpoint;
		this.description = description;
	}

	private String description;

	private String endpoint;

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

}
