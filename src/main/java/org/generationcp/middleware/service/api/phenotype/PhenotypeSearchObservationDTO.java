package org.generationcp.middleware.service.api.phenotype;

import org.pojomatic.Pojomatic;

public class PhenotypeSearchObservationDTO {
	private Integer observationDbId;
	private String observationVariableDbId;
	private String observationVariableName;
	private String observationTimeStamp;
	private String season;
	private String collector;
	private String value;

	public Integer getObservationDbId() {
		return observationDbId;
	}

	public void setObservationDbId(final Integer observationDbId) {
		this.observationDbId = observationDbId;
	}

	public String getObservationVariableDbId() {
		return observationVariableDbId;
	}

	public void setObservationVariableDbId(final String observationVariableDbId) {
		this.observationVariableDbId = observationVariableDbId;
	}

	public String getObservationVariableName() {
		return observationVariableName;
	}

	public void setObservationVariableName(final String observationVariableName) {
		this.observationVariableName = observationVariableName;
	}

	public String getObservationTimeStamp() {
		return observationTimeStamp;
	}

	public void setObservationTimeStamp(final String observationTimeStamp) {
		this.observationTimeStamp = observationTimeStamp;
	}

	public String getSeason() {
		return season;
	}

	public void setSeason(final String season) {
		this.season = season;
	}

	public String getCollector() {
		return collector;
	}

	public void setCollector(final String collector) {
		this.collector = collector;
	}

	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
	}


	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}

}
