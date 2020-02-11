package org.generationcp.middleware.api.brapi.v1.observation;

public class ObservationDTO {

	public String collector;
	public Integer observationDbId;
	public String observationTimeStamp;
	public String observationUnitDbId;
	public Integer observationVariableDbId;
	public String value;

	public String getCollector() {
		return collector;
	}

	public void setCollector(final String collector) {
		this.collector = collector;
	}

	public Integer getObservationDbId() {
		return observationDbId;
	}

	public void setObservationDbId(final Integer observationDbId) {
		this.observationDbId = observationDbId;
	}

	public String getObservationTimeStamp() {
		return observationTimeStamp;
	}

	public void setObservationTimeStamp(final String observationTimeStamp) {
		this.observationTimeStamp = observationTimeStamp;
	}

	public String getObservationUnitDbId() {
		return observationUnitDbId;
	}

	public void setObservationUnitDbId(final String observationUnitDbId) {
		this.observationUnitDbId = observationUnitDbId;
	}

	public Integer getObservationVariableDbId() {
		return observationVariableDbId;
	}

	public void setObservationVariableDbId(final Integer observationVariableDbId) {
		this.observationVariableDbId = observationVariableDbId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
	}
}
