package org.generationcp.middleware.service.api.phenotype;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.generationcp.middleware.service.api.study.SeasonDto;
import org.pojomatic.Pojomatic;

import java.util.Date;

public class PhenotypeSearchObservationDTO {
	private String observationDbId;
	private String observationVariableDbId;
	private String observationVariableName;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
	private Date observationTimeStamp;
	private SeasonDto season;
	private String collector;
	private String value;

	public String getObservationDbId() {
		return observationDbId;
	}

	public void setObservationDbId(final String observationDbId) {
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

	public Date getObservationTimeStamp() {
		return observationTimeStamp;
	}

	public void setObservationTimeStamp(final Date observationTimeStamp) {
		this.observationTimeStamp = observationTimeStamp;
	}

	public SeasonDto getSeason() {
		return season;
	}

	public void setSeason(final SeasonDto season) {
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
