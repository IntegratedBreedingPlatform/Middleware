package org.generationcp.middleware.api.brapi.v2.observationunit;

@SuppressWarnings("ALL")
public class ObservationUnitPatchRequestDTO {

	private ObservationUnitPosition observationUnitPosition = new ObservationUnitPosition();

	public ObservationUnitPosition getObservationUnitPosition() {
		return observationUnitPosition;
	}

	public void setObservationUnitPosition(
		final ObservationUnitPosition observationUnitPosition) {
		this.observationUnitPosition = observationUnitPosition;
	}
}
