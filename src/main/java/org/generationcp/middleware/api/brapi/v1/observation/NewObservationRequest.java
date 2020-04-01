package org.generationcp.middleware.api.brapi.v1.observation;

import java.util.List;

public class NewObservationRequest {

	public List<ObservationDTO> getObservations() {
		return this.observations;
	}

	public void setObservations(final List<ObservationDTO> observations) {
		this.observations = observations;
	}

	public List<ObservationDTO> observations;

}
