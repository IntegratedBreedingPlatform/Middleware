package org.generationcp.middleware.domain.dataset;

public class ObservationDto {
	
	private Integer observationId;

	private Integer variableId;

	private String value;

	private Integer categoricalValueId;

	private String status;

	private String createdDate;

	private String updatedDate;

	private Integer observationUnitId;

	public Integer getObservationId() {
		return observationId;
	}

	public void setObservationId(final Integer observationId) {
		this.observationId = observationId;
	}

	public Integer getVariableId() {
		return variableId;
	}

	public void setVariableId(final Integer variableId) {
		this.variableId = variableId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	public Integer getCategoricalValueId() {
		return categoricalValueId;
	}

	public void setCategoricalValueId(final Integer categoricalValueId) {
		this.categoricalValueId = categoricalValueId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(final String status) {
		this.status = status;
	}

	public String getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(final String updatedDate) {
		this.updatedDate = updatedDate;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(final String createdDate) {
		this.createdDate = createdDate;
	}

	public Integer getObservationUnitId() {
		return observationUnitId;
	}

	public void setObservationUnitId(final Integer observationUnitId) {
		this.observationUnitId = observationUnitId;
	}


}
