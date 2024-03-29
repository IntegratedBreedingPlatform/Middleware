package org.generationcp.middleware.domain.genotype;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Date;
import java.util.Map;

@AutoProperty
public class SampleGenotypeDTO {

	private Integer observationUnitId;

	private String sampleName;

	private String sampleUUID;

	private Date samplingDate;

	private Integer takenById;

	private String takenBy;

	private Map<String, SampleGenotypeData> genotypeDataMap;

	public Integer getObservationUnitId() {
		return this.observationUnitId;
	}

	public void setObservationUnitId(final Integer observationUnitId) {
		this.observationUnitId = observationUnitId;
	}

	public String getSampleName() {
		return this.sampleName;
	}

	public void setSampleName(final String sampleName) {
		this.sampleName = sampleName;
	}

	public String getSampleUUID() {
		return this.sampleUUID;
	}

	public void setSampleUUID(final String sampleUUID) {
		this.sampleUUID = sampleUUID;
	}

	public Date getSamplingDate() {
		return this.samplingDate;
	}

	public void setSamplingDate(final Date samplingDate) {
		this.samplingDate = samplingDate;
	}

	public Integer getTakenById() {
		return this.takenById;
	}

	public void setTakenById(final Integer takenById) {
		this.takenById = takenById;
	}

	public String getTakenBy() {
		return this.takenBy;
	}

	public void setTakenBy(final String takenBy) {
		this.takenBy = takenBy;
	}

	public Map<String, SampleGenotypeData> getGenotypeDataMap() {
		return this.genotypeDataMap;
	}

	public void setGenotypeDataMap(final Map<String, SampleGenotypeData> genotypeDataMap) {
		this.genotypeDataMap = genotypeDataMap;
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}

	@Override
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}

}
