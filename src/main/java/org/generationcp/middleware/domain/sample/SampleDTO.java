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

	public String getSampleName() {
		return sampleName;
	}

	public void setSampleName(String sampleName) {
		this.sampleName = sampleName;
	}

	public String getSampleBusinessKey() {
		return sampleBusinessKey;
	}

	public void setSampleBusinessKey(String sampleBusinessKey) {
		this.sampleBusinessKey = sampleBusinessKey;
	}

	public String getTakenBy() {
		return takenBy;
	}

	public void setTakenBy(String takenBy) {
		this.takenBy = takenBy;
	}

	public Date getSamplingDate() {
		return samplingDate;
	}

	public void setSamplingDate(Date samplingDate) {
		this.samplingDate = samplingDate;
	}

	public String getSampleList() {
		return sampleList;
	}

	public void setSampleList(String sampleList) {
		this.sampleList = sampleList;
	}

	public Integer getPlantNumber() {
		return plantNumber;
	}

	public void setPlantNumber(Integer plantNumber) {
		this.plantNumber = plantNumber;
	}

	public String getPlantBusinessKey() {
		return plantBusinessKey;
	}

	public void setPlantBusinessKey(String plantBusinessKey) {
		this.plantBusinessKey = plantBusinessKey;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		SampleDTO sampleDTO = (SampleDTO) o;

		return new EqualsBuilder().append(sampleName, sampleDTO.sampleName).append(sampleBusinessKey, sampleDTO.sampleBusinessKey)
			.append(takenBy, sampleDTO.takenBy).append(samplingDate, sampleDTO.samplingDate).append(sampleList, sampleDTO.sampleList)
			.append(plantNumber, sampleDTO.plantNumber).append(plantBusinessKey, sampleDTO.plantBusinessKey).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(sampleName).append(sampleBusinessKey).append(takenBy).append(samplingDate)
			.append(sampleList).append(plantNumber).append(plantBusinessKey).toHashCode();
	}
}
