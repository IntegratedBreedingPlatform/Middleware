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
	private Integer draftCategoricalValueId;
	private String draftValue;

	public Integer getObservationId() {
		return this.observationId;
	}

	public void setObservationId(final Integer observationId) {
		this.observationId = observationId;
	}

	public Integer getVariableId() {
		return this.variableId;
	}

	public void setVariableId(final Integer variableId) {
		this.variableId = variableId;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	public Integer getCategoricalValueId() {
		return this.categoricalValueId;
	}

	public void setCategoricalValueId(final Integer categoricalValueId) {
		this.categoricalValueId = categoricalValueId;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(final String status) {
		this.status = status;
	}

	public String getUpdatedDate() {
		return this.updatedDate;
	}

	public void setUpdatedDate(final String updatedDate) {
		this.updatedDate = updatedDate;
	}

	public String getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(final String createdDate) {
		this.createdDate = createdDate;
	}

	public Integer getObservationUnitId() {
		return this.observationUnitId;
	}

	public void setObservationUnitId(final Integer observationUnitId) {
		this.observationUnitId = observationUnitId;
	}

	public Integer getDraftCategoricalValueId() {
		return this.draftCategoricalValueId;
	}

	public void setDraftCategoricalValueId(final Integer draftCategoricalValueId) {
		this.draftCategoricalValueId = draftCategoricalValueId;
	}

	public String getDraftValue() {
		return this.draftValue;
	}

	public void setDraftValue(final String draftValue) {
		this.draftValue = draftValue;
	}
}
