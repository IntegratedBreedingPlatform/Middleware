package org.generationcp.middleware.api.brapi.v2.observationunit;

import java.util.Map;

@SuppressWarnings("ALL")
public class ObservationUnitPatchRequestDTO {

	private ObservationUnitPosition observationUnitPosition = new ObservationUnitPosition();

	public static class ObservationUnitPosition {

		private Map<String, Object> geoCoordinates;

		public Map<String, Object> getGeoCoordinates() {
			return geoCoordinates;
		}

		public void setGeoCoordinates(final Map<String, Object> geoCoordinates) {
			this.geoCoordinates = geoCoordinates;
		}
	}

	public ObservationUnitPosition getObservationUnitPosition() {
		return observationUnitPosition;
	}

	public void setObservationUnitPosition(
		final ObservationUnitPosition observationUnitPosition) {
		this.observationUnitPosition = observationUnitPosition;
	}
}
