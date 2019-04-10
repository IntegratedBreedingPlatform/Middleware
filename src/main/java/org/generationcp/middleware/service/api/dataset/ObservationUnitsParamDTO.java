package org.generationcp.middleware.service.api.dataset;

public class ObservationUnitsParamDTO {

	private String newValue;
	private Integer newCategoricalValueId;
	private ObservationUnitsSearchDTO observationUnitsSearchDTO;

	public ObservationUnitsParamDTO(
		final String newValue, final Integer newCategoricalValueId,
		final ObservationUnitsSearchDTO observationUnitsSearchDTO) {
		this.newValue = newValue;
		this.newCategoricalValueId = newCategoricalValueId;
		this.observationUnitsSearchDTO = observationUnitsSearchDTO;
	}

	public String getNewValue() {
		return this.newValue;
	}

	public void setNewValue(final String newValue) {
		this.newValue = newValue;
	}

	public Integer getNewCategoricalValueId() {
		return this.newCategoricalValueId;
	}

	public void setNewCategoricalValueId(final Integer newCategoricalValueId) {
		this.newCategoricalValueId = newCategoricalValueId;
	}

	public ObservationUnitsSearchDTO getObservationUnitsSearchDTO() {
		return this.observationUnitsSearchDTO;
	}

	public void setObservationUnitsSearchDTO(final ObservationUnitsSearchDTO observationUnitsSearchDTO) {
		this.observationUnitsSearchDTO = observationUnitsSearchDTO;
	}
}
