package org.generationcp.middleware.domain.sample;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Date;

public class SampleDTO {

	private String sampleName;
	private String sampleBusinessKey;
	private String takenBy;
	private Date samplingDate;
	private String sampleList;
	private Integer plantNumber;
	private String plantBusinessKey;

	public SampleDTO() {
	}

	public SampleDTO(final String sampleName, final String sampleBusinessKey, final String takenBy, final Date samplingDate,
		final String sampleList, final Integer plantNumber, final String plantBusinessKey) {
		this.sampleName = sampleName;
		this.sampleBusinessKey = sampleBusinessKey;
		this.takenBy = takenBy;
		this.samplingDate = samplingDate;
		this.sampleList = sampleList;
		this.plantNumber = plantNumber;
		this.plantBusinessKey = plantBusinessKey;
	}

	public String getSampleName() {
		return sampleName;
	}

	public void setSampleName(final String sampleName) {
		this.sampleName = sampleName;
	}

	public String getSampleBusinessKey() {
		return sampleBusinessKey;
	}

	public void setSampleBusinessKey(final String sampleBusinessKey) {
		this.sampleBusinessKey = sampleBusinessKey;
	}

	public String getTakenBy() {
		return takenBy;
	}

	public void setTakenBy(final String takenBy) {
		this.takenBy = takenBy;
	}

	public Date getSamplingDate() {
		return samplingDate;
	}

	public void setSamplingDate(final Date samplingDate) {
		this.samplingDate = samplingDate;
	}

	public String getSampleList() {
		return sampleList;
	}

	public void setSampleList(final String sampleList) {
		this.sampleList = sampleList;
	}

	public Integer getPlantNumber() {
		return plantNumber;
	}

	public void setPlantNumber(final Integer plantNumber) {
		this.plantNumber = plantNumber;
	}

	public String getPlantBusinessKey() {
		return plantBusinessKey;
	}

	public void setPlantBusinessKey(final String plantBusinessKey) {
		this.plantBusinessKey = plantBusinessKey;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final SampleDTO sampleDTO = (SampleDTO) o;

		return new EqualsBuilder().append(sampleName, sampleDTO.sampleName)
			.append(sampleBusinessKey, sampleDTO.sampleBusinessKey)
			.append(takenBy, sampleDTO.takenBy)
			.append(samplingDate, sampleDTO.samplingDate)
			.append(sampleList, sampleDTO.sampleList)
			.append(plantNumber, sampleDTO.plantNumber)
			.append(plantBusinessKey, sampleDTO.plantBusinessKey)
			.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(sampleName)
			.append(sampleBusinessKey)
			.append(takenBy)
			.append(samplingDate)
			.append(sampleList)
			.append(plantNumber)
			.append(plantBusinessKey)
			.toHashCode();
	}
}
